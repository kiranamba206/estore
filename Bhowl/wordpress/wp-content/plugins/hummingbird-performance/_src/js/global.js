/* global wphbGlobal */

( function () {
	'use strict';

	const WPHBGlobal = {
		init() {
			this.registerClearPageCache();
			this.registerClearNetworkCache();
			this.registerClearCacheFromNotice();
			this.registerClearCloudflare();
		},

		/**
		 * Clear page cache.
		 */
		registerClearPageCache() {
			const btn = document.querySelector(
				'#wp-admin-bar-wphb-clear-cache > a'
			);

			if ( ! btn ) {
				return;
			}

			btn.addEventListener( 'click', () =>
				this.post( 'wphb_front_clear_cache' )
			);
		},

		/**
		 * Clear network cache.
		 */
		registerClearNetworkCache() {
			const btn = document.querySelector(
				'#wp-admin-bar-wphb-clear-cache-network-wide > a'
			);

			if ( ! btn ) {
				return;
			}

			btn.addEventListener( 'click', () => {
				if ( 'undefined' === typeof window.WPHB_Admin ) {
					window.location.href =
						'/wp-admin/network/admin.php?page=wphb-caching&update=open-ccnw';
					return;
				}

				window.SUI.openModal(
					'ccnw-modal',
					'wpbody',
					'ccnw-clear-now'
				);
			} );
		},

		/**
		 * Clear cache from notice regarding plugin/theme updates.
		 */
		registerClearCacheFromNotice() {
			const btn = document.getElementById(
				'wp-admin-notice-wphb-clear-cache'
			);

			if ( ! btn ) {
				return;
			}

			btn.addEventListener( 'click', () =>
				this.post( 'wphb_global_clear_cache' )
			);
		},

		/**
		 * Clear Cloudflare browser cache.
		 *
		 * @since 2.7.2
		 */
		registerClearCloudflare() {
			const btn = document.querySelector(
				'#wp-admin-bar-wphb-clear-cloudflare > a'
			);

			if ( ! btn ) {
				return;
			}

			btn.addEventListener( 'click', () =>
				this.post( 'wphb_front_clear_cloudflare' )
			);
		},

		post: ( action ) => {
			const xhr = new XMLHttpRequest();
			xhr.open( 'POST', wphbGlobal.ajaxurl + '?action=' + action );
			xhr.onload = function () {
				if ( xhr.status === 200 ) {
					location.reload();
				}
			};
			xhr.send();
		},
	};

	document.addEventListener( 'DOMContentLoaded', function () {
		WPHBGlobal.init();
	} );
} )();
