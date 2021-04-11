<?php
/**
 * Minify service.
 *
 * @package Hummingbird\Core\Api\Service
 */

namespace Hummingbird\Core\Api\Service;

use Hummingbird\Core\Api\Exception;
use WP_Error;

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

/**
 * Class Minify
 */
class Minify extends Service {

	/**
	 * Service name.
	 *
	 * @var string $name
	 */
	public $name = 'minify';

	/**
	 * Minify constructor.
	 *
	 * @throws Exception  Exception.
	 */
	public function __construct() {
		$this->request = new \Hummingbird\Core\Api\Request\Minify( $this );
	}

	/**
	 * Check if performance test has finished on server
	 *
	 * @param array $files  List of files.
	 *
	 * @return array|mixed|object|WP_Error
	 */
	public function process_files( $files = array() ) {
		$args = array(
			'domain' => $this->request->get_this_site(),
		);

		$args['path'] = '';
		if ( is_multisite() && ! is_subdomain_install() ) {
			$blog_details = get_blog_details( get_current_blog_id() );
			$args['path'] = $blog_details->path;
		} elseif ( is_multisite() && is_subdomain_install() ) {
			global $current_site;
			$pattern = '/(https?\:\/\/)?(.*)\.' . $current_site->domain . '(.*)/';
			if ( preg_match_all( $pattern, home_url(), $matches ) ) {
				$args['path'] = $matches[2][0];
			}
		}

		$args['files'] = $files;

		$args = wp_json_encode( $args );

		$this->request->add_header_argument( 'content-type', 'application/json' );

		$result = $this->request->post( 'minify', $args );
		if ( is_wp_error( $result ) ) {
			return $result;
		}

		return json_decode( wp_remote_retrieve_body( $result ) );
	}

	/**
	 * Get HTTP2 status.
	 *
	 * @return bool
	 */
	public function is_http2() {
		define( 'WPHB_USE_LOCAL_SITE', true );

		$response = $this->request->head( $this->request->get_this_site(), array() ); // get_this_site() doesn't really matter here.

		$code = wp_remote_retrieve_response_code( $response );

		if ( 200 === $code && isset( $response['http_response'] ) && $response['http_response'] instanceof \WP_HTTP_Requests_Response ) {
			if ( method_exists( $response['http_response'], 'get_response_object' ) ) {
				$protocol_version = $response['http_response']->get_response_object()->protocol_version;
				return 2 === $protocol_version || 2.0 === $protocol_version;
			}

			return false;
		}

		return false;
	}

}
