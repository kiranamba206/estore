<?php
/**
 * Manages activation/deactivation and upgrades of Hummingbird
 *
 * @author: WPMUDEV, Ignacio Cruz (igmoweb)
 * @package Hummingbird\Core
 */

namespace Hummingbird\Core;

use Hummingbird\WP_Hummingbird;

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

/**
 * Class Installer
 */
class Installer {

	/**
	 * Plugin activation
	 */
	public static function activate() {
		if ( ! defined( 'WPHB_ACTIVATING' ) ) {
			define( 'WPHB_ACTIVATING', true );
		}

		update_site_option( 'wphb_version', WPHB_VERSION );
		update_site_option( 'wphb-notice-uptime-info-show', 'yes' ); // Add uptime notice.
		update_site_option( 'wphb_run_onboarding', true );
	}

	/**
	 * Plugin activation in a blog (if the site is a multisite)
	 */
	public static function activate_blog() {
		update_option( 'wphb_version', WPHB_VERSION );
		update_option( 'wphb_run_onboarding', true );
		do_action( 'wphb_activate' );
	}

	/**
	 * Plugin deactivation
	 */
	public static function deactivate() {
		// Avoid to execute this over an over in same thread execution.
		if ( defined( 'WPHB_SWITCHING_VERSION' ) ) {
			return;
		}

		$settings = Settings::get_settings( 'settings' );
		WP_Hummingbird::flush_cache( $settings['remove_data'], $settings['remove_settings'] );

		Utils::get_module( 'page_cache' )->toggle_service( false );

		if ( $settings['remove_settings'] ) {
			// Completely remove hummingbird-asset folder.
			Filesystem::instance()->purge( '', true );
		}
		do_action( 'wphb_deactivate' );
	}

	/**
	 * Plugin upgrades
	 */
	public static function maybe_upgrade() {
		// Avoid to execute this over an over in same thread execution.
		if ( defined( 'WPHB_ACTIVATING' ) ) {
			return;
		}

		if ( defined( 'WPHB_UPGRADING' ) && WPHB_UPGRADING ) {
			return;
		}

		self::upgrade();
	}

	/**
	 * Upgrade
	 */
	public static function upgrade() {
		$version = get_site_option( 'wphb_version' );

		if ( false === $version ) {
			self::activate();
		}

		if ( is_multisite() ) {
			$blog_version = get_option( 'wphb_version' );
			if ( false === $blog_version ) {
				self::activate_blog();
			}

			if ( false !== $blog_version && WPHB_VERSION !== $blog_version ) {
				if ( version_compare( $blog_version, '2.6.0', '<' ) ) {
					self::upgrade_2_6_0_multi();
				}

				if ( version_compare( $version, '2.6.2', '<' ) ) {
					self::upgrade_2_6_2_multi();
				}

				if ( version_compare( $version, '2.7.1', '<' ) ) {
					self::upgrade_2_7_1_multi();
				}

				if ( version_compare( $version, '2.7.2', '<' ) ) {
					self::upgrade_2_7_2_multi();
				}
			}
		}

		if ( false !== $version && WPHB_VERSION !== $version ) {
			if ( ! defined( 'WPHB_UPGRADING' ) ) {
				define( 'WPHB_UPGRADING', true );
			}

			if ( version_compare( $version, '2.5.0', '<' ) ) {
				self::upgrade_2_5_0();
			}

			if ( version_compare( $version, '2.5.1', '<' ) ) {
				self::upgrade_2_5_1();
			}

			if ( version_compare( $version, '2.5.2', '<' ) ) {
				self::upgrade_2_5_2();
			}

			if ( version_compare( $version, '2.6.0', '<' ) ) {
				self::upgrade_2_6_0();
			}

			if ( version_compare( $version, '2.6.2', '<' ) ) {
				self::upgrade_2_6_2();
			}

			if ( version_compare( $version, '2.7.1', '<' ) ) {
				self::upgrade_2_7_1();
				self::upgrade_2_7_1_multi();
			}

			if ( version_compare( $version, '2.7.2', '<' ) ) {
				// Need to refresh the data.
				delete_option( 'wphb-caching-data' );
				self::upgrade_2_7_2_multi();
			}

			update_site_option( 'wphb_version', WPHB_VERSION );
		}
	}

	/**
	 * Upgrades a single blog in a multisite
	 */
	public static function maybe_upgrade_blog() {
		$version = get_option( 'wphb_version' );

		if ( WPHB_VERSION === $version ) {
			return;
		}

		update_option( 'wphb_version', WPHB_VERSION );
	}

	/**
	 * Upgrade to 2.5.0
	 *
	 * Update advanced-cache.php file.
	 *
	 * @since 2.5.0
	 */
	private static function upgrade_2_5_0() {
		// Force new quick setup (with tracking option).
		delete_option( 'wphb-quick-setup' );

		$adv_cache_file = dirname( get_theme_root() ) . '/advanced-cache.php';
		if ( Settings::get_settings( 'page_cache' ) && file_exists( $adv_cache_file ) ) {
			unlink( $adv_cache_file );
		}
	}

	/**
	 * Upgrade to 2.5.1
	 *
	 * Remove possible cron schedule loop.
	 *
	 * @since 2.5.1
	 */
	private static function upgrade_2_5_1() {
		if ( ! function_exists( 'wp_unschedule_hook' ) ) {
			return;
		}

		wp_unschedule_hook( 'wphb_performance_fetch_report' );
	}

	/**
	 * Upgrade to 2.5.2
	 *
	 * Change default value for "File change detection".
	 *
	 * @since 2.5.2
	 */
	private static function upgrade_2_5_2() {
		Settings::update_setting( 'detection', 'auto', 'page_cache' );
	}

	/**
	 * This is a fixed up mechanism for only updating the required stuff.
	 *
	 * Common for both subsites and single sites.
	 *
	 * @since 2.6.0
	 */
	private static function upgrade_2_6_0_multi() {
		add_option( 'wphb-minification-show-config_modal', true );

		// Add AO upgrade modal.
		if ( Settings::get_setting( 'minify_blog', 'minify' ) ) {
			add_option( 'wphb_do_minification_upgrade', true );
			self::upgrade_minification_structure();
		}
	}

	/**
	 * Upgrade to 2.6.0
	 *
	 * @since 2.6.0
	 */
	private static function upgrade_2_6_0() {
		// Show upgrade summary, Currently only for v2.6.0.
		add_site_option( 'wphb_show_upgrade_summary', true );
		add_option( 'wphb-minification-show-config_modal', true );

		// Add AO upgrade modal.
		if ( Settings::get_setting( 'enabled', 'minify' ) ) {
			add_option( 'wphb_do_minification_upgrade', true );
			self::upgrade_minification_structure();
		}

		// Change default value for "File change detection".
		Settings::update_setting( 'detection', 'auto', 'page_cache' );

		if ( ! Settings::get_setting( 'enabled', 'page_cache' ) ) {
			return;
		}

		// Update Page Cache exclusion rule for split sitemap.
		$settings = Utils::get_module( 'page_cache' )->get_settings();
		if ( is_array( $settings['exclude']['url_strings'] ) && ! in_array( 'sitemap[0-9]*\.xml', $settings['exclude']['url_strings'], true ) ) {
			// Remove old value.
			$key = array_search( 'sitemap.xml', $settings['exclude']['url_strings'], true );
			if ( false !== $key && isset( $settings['exclude']['url_strings'][ $key ] ) ) {
				unset( $settings['exclude']['url_strings'][ $key ] );
			}

			// Add new one.
			$settings['exclude']['url_strings'][] = 'sitemap[0-9]*\.xml';
			Utils::get_module( 'page_cache' )->save_settings( $settings );
		}
	}

	/**
	 * Upgrade the pre 2.5 settings to new 2.6 format.
	 *
	 * @since 2.6.0
	 */
	private static function upgrade_minification_structure() {
		$options = Settings::get_settings( 'minify' );

		$collections = Modules\Minify\Sources_Collector::get_collection();

		foreach ( array( 'scripts', 'styles' ) as $type ) {
			$keys = array_keys( $collections[ $type ] );

			if ( ! isset( $options['minify'] ) || ! isset( $options['minify'][ $type ] ) ) {
				continue;
			}

			$options['dont_minify'][ $type ]  = array_diff( $keys, $options['minify'][ $type ] );
			$options['dont_combine'][ $type ] = array_diff( $keys, $options['combine'][ $type ] );
		}

		unset( $options['minify'] );
		unset( $options['combine'] );

		Settings::update_settings( $options, 'minify' );
	}

	/**
	 * Upgrade sub sites to 2.6.2
	 *
	 * @since 2.6.2
	 */
	private static function upgrade_2_6_2_multi() {
		// Remove AO upgrade modal for sub sites that do not have AO enabled.
		if ( ! Settings::get_setting( 'minify_blog', 'minify' ) ) {
			delete_option( 'wphb_do_minification_upgrade' );
		}
	}

	/**
	 * Upgrade to 2.6.2
	 *
	 * @since 2.6.2
	 */
	private static function upgrade_2_6_2() {
		Logger::cleanup();

		// Remove unused database entries.
		delete_option( 'wphb-new-user-tour' );

		// Enhance page cache sitemaps support.
		if ( Settings::get_setting( 'enabled', 'page_cache' ) ) {
			$settings = Utils::get_module( 'page_cache' )->get_settings();
			if ( isset( $settings['exclude']['url_strings'] ) && is_array( $settings['exclude']['url_strings'] ) ) {
				// Remove old value.
				$key = array_search( 'sitemap[0-9]*\.xml', $settings['exclude']['url_strings'], true );
				if ( false !== $key && isset( $settings['exclude']['url_strings'][ $key ] ) ) {
					unset( $settings['exclude']['url_strings'][ $key ] );
				}

				// We double search, one of the previous updates, added an extra value.
				$key = array_search( 'sitemap[0-9]*.xml', $settings['exclude']['url_strings'], true );
				if ( false !== $key && isset( $settings['exclude']['url_strings'][ $key ] ) ) {
					unset( $settings['exclude']['url_strings'][ $key ] );
				}

				$key = array_search( 'sitemap[0-9]*.xml', $settings['exclude']['url_strings'], true );
				if ( false !== $key && isset( $settings['exclude']['url_strings'][ $key ] ) ) {
					unset( $settings['exclude']['url_strings'][ $key ] );
				}

				// Add new one.
				$settings['exclude']['url_strings'][] = 'sitemap[^\/.]*\.(?:xml|xsl)';
				Utils::get_module( 'page_cache' )->save_settings( $settings );
			}
		}
	}

	/**
	 * Upgrade to 2.7.1
	 *
	 * @since 2.7.1
	 */
	private static function upgrade_2_7_1() {
		if ( Settings::get_setting( 'enabled', 'page_cache' ) ) {
			unlink( WP_CONTENT_DIR . '/advanced-cache.php' );
		}
	}

	/**
	 * Upgrade to 2.7.1
	 *
	 * @since 2.7.1
	 */
	private static function upgrade_2_7_1_multi() {
		wp_cache_delete( 'wphb_process_queue', 'options' );
	}

	/**
	 * Upgrade to 2.7.1 (single and subsites).
	 *
	 * @since 2.7.2
	 */
	private static function upgrade_2_7_2_multi() {
		$minify  = Utils::get_module( 'minify' );
		$options = $minify->get_options();

		// Asset optimization not enabled - skip.
		if ( ! $options['enabled'] ) {
			return;
		}

		// If not enabled on subsites - skip.
		if ( is_multisite() && ! is_network_admin() && ! $options['minify_blog'] ) {
			return;
		}

		// Not Auto Basic mode - exit.
		if ( ! isset( $options['type'] ) || 'basic' !== $options['type'] ) {
			return;
		}

		$collections = \Hummingbird\Core\Modules\Minify\Sources_Collector::get_collection();

		// Fix styles and scripts from combining on basic mode.
		$options['dont_combine']['styles']  = array_keys( $collections['styles'] );
		$options['dont_combine']['scripts'] = array_keys( $collections['scripts'] );

		$minify->update_options( $options );
		$minify->clear_cache( false );
	}

}
