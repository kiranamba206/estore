<?php
/**
 * Cloudflare module.
 *
 * @package Hummingbird
 */

namespace Hummingbird\Core\Modules;

use Hummingbird\Core\Module;
use Hummingbird\Core\Settings;
use Hummingbird\Core\Traits\Module as ModuleContract;
use Hummingbird\Core\Utils;
use WP_Error;

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

/**
 * Class Cloudflare
 */
class Cloudflare extends Module {

	use ModuleContract;

	/**
	 * Module slug name
	 *
	 * @var string
	 */
	protected $slug = 'cloudflare';

	/**
	 * Module name
	 *
	 * @var string
	 */
	protected $name = 'Cloudflare';

	/**
	 * Initializes Cloudflare module
	 */
	public function init() {
		add_filter( 'wp_hummingbird_is_active_module_cloudflare', array( $this, 'module_status' ) );
	}

	/**
	 * Detect if site is using Cloudflare
	 *
	 * @param bool $force If set to true it will check again.
	 *
	 * @return bool
	 */
	public function has_cloudflare( $force = false ) {
		if ( isset( $_GET['wphb-check-cf'] ) ) { // Input var ok.
			// If we're checking do not try to check again or it will return a timeout.
			return (bool) Settings::get_setting( 'connected', $this->slug );
		}

		if ( $force ) {
			Settings::update_setting( 'connected', false, $this->slug );
		}

		$options = $this->get_options();

		$is_cloudflare_db = isset( $options['connected'] ) ? $options['connected'] : false;

		// Check once every hour.
		if ( ! $force && isset( $options['last_check'] ) && ( (int) $options['last_check'] + HOUR_IN_SECONDS ) >= time() ) {
			return $is_cloudflare_db;
		}

		$is_cloudflare = false;
		if ( ! is_numeric( $is_cloudflare_db ) || $force ) {
			$url  = add_query_arg( 'wphb-check-cf', 'true', home_url() );
			$head = wp_remote_head(
				$url,
				array(
					'sslverify' => false,
				)
			);

			if ( ! is_wp_error( $head ) ) {
				$headers = wp_remote_retrieve_headers( $head );
				if ( isset( $headers['server'] ) && strpos( $headers['server'], 'cloudflare' ) > -1 ) {
					$is_cloudflare = true;
				}
			}

			// Only write if value changes.
			if ( $is_cloudflare_db !== $is_cloudflare || is_wp_error( $head ) ) {
				Settings::update_setting( 'connected', $is_cloudflare, $this->slug );
			}

			Settings::update_setting( 'last_check', time(), $this->slug );
		}

		$is_cloudflare = (bool) $is_cloudflare;
		return apply_filters( 'wphb_has_cloudflare', $is_cloudflare );
	}

	/**
	 * Check if Cloudflare is connected.
	 *
	 * @return bool
	 */
	public function is_connected() {
		$options = $this->get_options();

		return $options['enabled'];
	}

	/**
	 * Check if zone is selected.
	 *
	 * @return bool
	 */
	public function is_zone_selected() {
		$options = $this->get_options();

		return ! empty( $options['zone'] );
	}

	/**
	 * Get Cloudflare plan.
	 *
	 * @return mixed
	 */
	public function get_plan() {
		$options = $this->get_options();

		return $options['plan'];
	}

	/**
	 * Tries to set the same caching rules in CF.
	 */
	private function set_caching_rules() {
		if ( ! $this->is_connected() || ! $this->is_zone_selected() ) {
			return;
		}

		$this->clear_caching_page_rules();

		$expirations = $this->get_filetypes_expirations();

		foreach ( $expirations as $filetype => $expiration ) {
			$this->add_caching_page_rule( $filetype );
		}
	}

	/**
	 * Clear Cloudflare caching page rules.
	 */
	private function clear_caching_page_rules() {
		$rules = $this->get_registered_caching_page_rules();

		foreach ( $rules as $filetype => $id ) {
			$this->delete_caching_page_rule( $filetype );
		}
	}

	/**
	 * Delete Cloudflare caching page rule.
	 *
	 * @param string $filetype  File type.
	 */
	private function delete_caching_page_rule( $filetype ) {
		$id = $this->get_registered_caching_page_rule_id( $filetype );
		$this->unregister_caching_page_rule( $filetype );

		if ( ! $this->is_connected() || ! $this->is_zone_selected() ) {
			return;
		}

		$options = $this->get_options();
		$api     = Utils::get_api();

		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );

		$api->cloudflare->delete_page_rule( $id, $options['zone'] );
	}

	/**
	 * Update Cloduflare caching page rule.
	 *
	 * @param string $filetype  File type.
	 *
	 * @return bool
	 */
	private function update_caching_page_rule( $filetype ) {
		// Check if the rule exists already.
		$id = $this->get_registered_caching_page_rule_id( $filetype );

		if ( $id ) {
			// Delete the rule and add it a new one.
			$this->delete_caching_page_rule( $filetype );
		}

		return $this->add_caching_page_rule( $filetype );
	}

	/**
	 * Add Cloduflare caching page rule.
	 *
	 * @param string $filetype  File type.
	 *
	 * @return bool
	 */
	private function add_caching_page_rule( $filetype ) {
		// If exists, delete it.
		$this->delete_caching_page_rule( $filetype );

		if ( ! $this->is_connected() || ! $this->is_zone_selected() ) {
			return false;
		}

		$expirations = $this->get_filetypes_expirations();

		if ( ! isset( $expirations[ $filetype ] ) ) {
			return false;
		}

		if ( ! $expirations[ $filetype ] ) {
			return false;
		}

		$targets = self::page_rule_targets( $filetype );
		$actions = self::page_rule_actions( $expirations[ $filetype ] );

		$options = $this->get_options();
		$api     = Utils::get_api();
		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );

		$result = $api->cloudflare->add_page_rule( $targets, $actions, $options['zone'] );

		if ( is_wp_error( $result ) ) {
			return false;
		}

		$this->register_caching_page_rule( $result->result->id, $filetype );
		return $result->result->id;

	}

	/**
	 * Get expiration values.
	 *
	 * @return array
	 */
	private function get_filetypes_expirations() {
		$options = $this->get_options();

		$expirations  = array();
		$_expirations = array(
			'css'  => $options['expiry_css'],
			'js'   => $options['expiry_javascript'],
			'jpg'  => $options['expiry_images'],
			'png'  => $options['expiry_images'],
			'jpeg' => $options['expiry_images'],
			'gif'  => $options['expiry_images'],
			'mp3'  => $options['expiry_media'],
			'mp4'  => $options['expiry_media'],
			'ico'  => $options['expiry_media'],
		);

		foreach ( $_expirations as $filetype => $time ) {
			if ( ! $time ) {
				$expirations[ $filetype ] = false;
				continue;
			}

			$time = explode( '/', $time );
			if ( 2 !== count( $time ) ) {
				$expirations[ $filetype ] = false;
				continue;
			}

			$time = absint( ltrim( $time[1], 'A' ) );

			if ( ! $time ) {
				$expirations[ $filetype ] = false;
				continue;
			}

			$expirations[ $filetype ] = $time;
		}

		return $expirations;
	}

	/**
	 * Page rule targets.
	 *
	 * @param string $filetype  File type.
	 *
	 * @return array
	 */
	private static function page_rule_targets( $filetype ) {
		return array(
			array(
				'target'     => 'url',
				'constraint' => array(
					'operator' => 'matches',
					'value'    => '*caninomag.es*.' . $filetype,
				),
			),
		);
	}

	/**
	 * Page rule actions.
	 *
	 * @param string $time  Time.
	 *
	 * @return array
	 */
	private static function page_rule_actions( $time ) {
		return array(
			array(
				'id'    => 'browser_cache_ttl',
				'value' => $time,
			),
		);
	}

	/**
	 * Register a rule added to CF so they can be listed them later
	 *
	 * @param int    $id        Id.
	 * @param string $filetype  File type.
	 */
	private function register_caching_page_rule( $id, $filetype ) {
		$options                            = $this->get_options();
		$options['page_rules'][ $filetype ] = $id;
		$this->update_options( $options );
	}

	/**
	 * Register a rule added to CF so they can be listed them later
	 *
	 * @param string $filetype  File type.
	 */
	private function unregister_caching_page_rule( $filetype ) {
		$options = $this->get_options();

		if ( isset( $options['page_rules'][ $filetype ] ) ) {
			unset( $options['page_rules'][ $filetype ] );
			$this->update_options( $options );
		}
	}

	/**
	 * Get the ID of registered rule.
	 *
	 * @param string $filetype  File type.
	 *
	 * @return bool
	 */
	private function get_registered_caching_page_rule_id( $filetype ) {
		$options = $this->get_options();

		return ( isset( $options['page_rules'][ $filetype ] ) ) ? $options['page_rules'][ $filetype ] : false;
	}

	/**
	 * Get registered caching rules.
	 *
	 * @return mixed
	 */
	private function get_registered_caching_page_rules() {
		$options = $this->get_options();

		return $options['page_rules'];
	}

	/**
	 * Get a list of Cloudflare zones
	 *
	 * @param int   $page   Current page.
	 * @param array $zones  List of zones.
	 *
	 * @return WP_Error|array
	 */
	public function get_zones_list( $page = 1, $zones = array() ) {
		if ( is_wp_error( $zones ) ) {
			return $zones;
		}
		$options = $this->get_options();
		$api     = Utils::get_api();
		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );
		$result = $api->cloudflare->get_zones_list( $page );
		if ( is_wp_error( $result ) ) {
			return $result;
		}

		$_zones = $result->result;
		foreach ( (array) $_zones as $zone ) {
			$zones[] = array(
				'value' => $zone->id,
				'label' => $zone->name,
				'plan'  => $zone->plan->legacy_id,
			);
		}

		if ( $result->result_info->total_pages > $page ) {
			// Get the next page.
			return $this->get_zones_list( ++$page, $zones );
		}

		return $zones;
	}

	/**
	 * Get a list of all page rules in CF
	 *
	 * @return WP_Error|array
	 */
	private function get_page_rules_list() {
		$options = $this->get_options();
		$api     = Utils::get_api();
		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );

		$result = $api->cloudflare->get_page_rules_list( $options['zone'] );
		if ( is_wp_error( $result ) ) {
			return $result;
		}

		return $result->result;
	}

	/**
	 * Set caching expiration.
	 *
	 * @param int $value  Expiration value.
	 *
	 * @return array|mixed|object|WP_Error
	 */
	public function set_caching_expiration( $value ) {
		$options = $this->get_options();
		$api     = Utils::get_api();
		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );

		$value = absint( $value );
		$freqs = self::get_frequencies();
		if ( ! $value || ! array_key_exists( $value, $freqs ) ) {
			return new WP_Error( 'cf_invalid_value', __( 'Invalid Cloudflare expiration value', 'wphb' ) );
		}

		$options['cache_expiry'] = $value;
		$this->update_options( $options );

		return $api->cloudflare->set_caching_expiration( $options['zone'], $value );
	}

	/**
	 * Get caching expiration.
	 *
	 * @return mixed
	 */
	public function get_caching_expiration() {
		$options = $this->get_options();

		if ( isset( $options['cache_expiry'] ) ) {
			return $options['cache_expiry'];
		}

		$api = Utils::get_api();
		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );
		$result = $api->cloudflare->get_caching_expiration( $options['zone'] );
		if ( is_wp_error( $result ) ) {
			return $result;
		}

		return $result->result->value;
	}

	/**
	 * Implement abstract parent method for clearing cache.
	 *
	 * @since 1.7.1 Changed name from purge_cache to clear_cache
	 *
	 * @return mixed
	 */
	public function clear_cache() {
		$options = $this->get_options();
		$api     = Utils::get_api();
		$api->cloudflare->set_auth_email( $options['email'] );
		$api->cloudflare->set_auth_key( $options['api_key'] );
		$result = $api->cloudflare->purge_cache( $options['zone'] );

		return is_wp_error( $result ) ? $result : $result->result;
	}

	/**
	 * Check if Cloudflare is disconnected.
	 *
	 * @used-by \Hummingbird\Admin\Pages\Caching::trigger_load_action()
	 */
	public function disconnect() {
		$options = $this->get_options();
		$this->clear_caching_page_rules();

		$options['enabled']   = false;
		$options['connected'] = false;
		$options['email']     = '';
		$options['api_key']   = '';
		$options['zone']      = '';
		$options['zone_name'] = '';
		$options['plan']      = '';

		$this->update_options( $options );
	}

	/**
	 * Get module status.
	 *
	 * @param bool $current  Current status.
	 *
	 * @return bool
	 */
	public function module_status( $current ) {
		$options = $this->get_options();
		if ( ! $options['enabled'] && empty( $options['zone'] ) ) {
			return $current;
		}

		return true;
	}

	/**
	 * Get an array of caching frequencies for Cloudflare.
	 *
	 * @return array
	 */
	public static function get_frequencies() {
		return array(
			7200     => __( '2 hours', 'wphb' ),
			10800    => __( '3 hours', 'wphb' ),
			14400    => __( '4 hours', 'wphb' ),
			18000    => __( '5 hours', 'wphb' ),
			28800    => __( '8 hours', 'wphb' ),
			43200    => __( '12 hours', 'wphb' ),
			57600    => __( '16 hours', 'wphb' ),
			72000    => __( '20 hours', 'wphb' ),
			86400    => __( '1 day', 'wphb' ),
			172800   => __( '2 days', 'wphb' ),
			259200   => __( '3 days', 'wphb' ),
			345600   => __( '4 days', 'wphb' ),
			432000   => __( '5 days', 'wphb' ),
			691200   => __( '8 days', 'wphb' ),
			1382400  => __( '16 days', 'wphb' ),
			2073600  => __( '24 days', 'wphb' ),
			2678400  => __( '1 month', 'wphb' ),
			5356800  => __( '2 months', 'wphb' ),
			16070400 => __( '6 months', 'wphb' ),
			31536000 => __( '1 year', 'wphb' ),
		);
	}

	/**
	 * Convert Cloudflare frequency to normal. Used when updating the custom code in browser caching.
	 *
	 * @param  int $frequency  Cloudflare frequency to convert.
	 *
	 * @return string  Caching frequency.
	 */
	public function convert_frequency( $frequency ) {
		$frequencies = array(
			7200     => '2h/A7200',
			10800    => '3h/A10800',
			14400    => '4h/A14400',
			18000    => '5h/A18000',
			28800    => '8h/A28800',
			43200    => '12h/A43200',
			57600    => '16h/A57600',
			72000    => '20h/A72000',
			86400    => '1d/A86400',
			172800   => '2d/A172800',
			259200   => '3d/A259200',
			345600   => '4d/A345600',
			432000   => '5d/A432000',
			691200   => '8d/A691200',
			1382400  => '16d/A1382400',
			2073600  => '24d/A2073600',
			2678400  => '1M/A2678400',
			5356800  => '2M/A5356800',
			16070400 => '6M/A16070400',
			31536000 => '1y/A31536000',
		);

		return $frequencies[ $frequency ];
	}

}
