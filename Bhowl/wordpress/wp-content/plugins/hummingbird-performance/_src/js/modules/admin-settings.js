/* global WPHB_Admin */

import Fetcher from '../utils/fetcher';
import { getLink } from '../utils/helpers';

( function ( $ ) {
	'use strict';

	WPHB_Admin.settings = {
		module: 'settings',

		init() {
			const body = $( 'body' );
			const wrap = body.find( '.wrap-wphb-settings' );

			// Save settings
			body.on( 'click', 'button.sui-button-blue', function( e ) {
				e.preventDefault();
				const form_data = body.find( '.settings-frm' ).serialize();

				const contrastDiv = $( '#color_accessible' );
				if ( contrastDiv.length ) {
					if ( contrastDiv.is( ':checked' ) ) {
						wrap.addClass( 'sui-color-accessible' );
					} else {
						wrap.removeClass( 'sui-color-accessible' );
					}
				}

				/**
				 * Opt in to tracking.
				 *
				 * @since 2.5.0
				 */
				const tracking = document.getElementById( 'tracking' );
				if ( tracking && true === tracking.checked ) {
					WPHB_Admin.Tracking.optIn();
				}

				Fetcher.settings.saveSettings( form_data ).then( () => {
					WPHB_Admin.notices.show();
				} );

				return false;
			} );

			/**
			 * Parse remove settings change.
			 */
			$( 'input[name=remove_settings]' ).on( 'change', function ( e ) {
				const otherClass =
					'remove_settings-false' === e.target.id
						? 'remove_settings-true'
						: 'remove_settings-false';
				e.target.parentNode.classList.add( 'active' );
				document
					.getElementById( otherClass )
					.parentNode.classList.remove( 'active' );
			} );

			/**
			 * Parse remove data change.
			 */
			$( 'input[name=remove_data]' ).on( 'change', function ( e ) {
				const otherClass =
					'remove_data-false' === e.target.id
						? 'remove_data-true'
						: 'remove_data-false';
				e.target.parentNode.classList.add( 'active' );
				document
					.getElementById( otherClass )
					.parentNode.classList.remove( 'active' );
			} );

			/**
			 * Handle import file change.
			 *
			 * @since 2.6.0
			 */
			$( '#wphb-import-file-input' ).on( 'change', function () {
				const elm = $( this )[ 0 ];
				if ( elm.files.length ) {
					const file = elm.files[ 0 ];
					$( '#wphb-import-file-name' ).text( file.name );
					$( '#wphb-import-upload-wrap' ).addClass( 'sui-has_file' );
					$( '#wphb-import-btn' ).removeAttr( 'disabled' );
				} else {
					$( '#wphb-import-file-name' ).text( '' );
					$( '#wphb-import-upload-wrap' ).removeClass(
						'sui-has_file'
					);
					$( '#wphb-import-btn' ).attr( 'disabled', 'disabled' );
				}
			} );

			/**
			 * Handle import file remove button.
			 *
			 * @since 2.6.0
			 */
			$( '#wphb-import-remove-file' ).on( 'click', function () {
				$( '#wphb-import-file-input' ).val( '' ).trigger( 'change' );
			} );

			/**
			 * Handle import button click.
			 *
			 * @since 2.6.0
			 */
			$( '#wphb-begin-import-btn' ).on( 'click', function ( e ) {
				e.preventDefault();
				$( this ).attr( 'disabled', 'disabled' ).addClass('sui-button-onload-text');
					
				$( '#wphb-import-remove-file' ).attr( 'disabled', 'disabled' );

				const file_elm = $( '#wphb-import-file-input' )[ 0 ];
				if ( file_elm.files.length == 0 ) {
					return false;
				}

				const form_data = new FormData();
				form_data.append(
					'settings_json_file',
					file_elm.files[ 0 ],
					file_elm.files[ 0 ].name
				);

				Fetcher.settings
					.importSettings( form_data )
					.then( ( response ) => {
						WPHB_Admin.notices.show( response.message );
						$( '#wphb-begin-import-btn' )
							.removeAttr( 'disabled' )
							.removeClass('sui-button-onload-text');
						$( '#wphb-import-remove-file' ).removeAttr( 'disabled' );
						$( '#wphb-import-remove-file' ).trigger( 'click' );
						window.SUI.closeModal();
					} )
					.catch( ( error ) => {
						WPHB_Admin.notices.show( error, 'error' );
						$( '#wphb-begin-import-btn' )
							.removeAttr( 'disabled' )
							.removeClass('sui-button-onload-text');
						$( '#wphb-import-remove-file' ).removeAttr( 'disabled' );
						window.SUI.closeModal();
					} );
			} );

			/**
			 * Handle export button click.
			 *
			 * @since 2.6.0
			 */
			$( '#wphb-export-btn' ).on( 'click', function ( e ) {
				e.preventDefault();
				Fetcher.settings.exprotSettings();
			} );

			return this;
		},

		/**
		 * Parse confirm settings reset from the modal.
		 *
		 * @since 2.0.0
		 */
		confirmReset: () => {
			Fetcher.common.call( 'wphb_reset_settings' ).then( () => {
				Fetcher.common.call( 'wphb_redis_disconnect' );
				window.location.href = getLink( 'resetSettings' );
			} );
		},
	};
} )( jQuery );
