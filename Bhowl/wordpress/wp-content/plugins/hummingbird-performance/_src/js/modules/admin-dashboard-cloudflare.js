/* global WPHB_Admin */

import Fetcher from '../utils/fetcher';
import { getString } from '../utils/helpers';

( function ( $ ) {
	WPHB_Admin.DashboardCloudFlare = {
		init( settings ) {
			this.currentStep = settings.currentStep;
			this.data = settings;
			this.email = settings.email;
			this.apiKey = settings.apiKey;
			this.$stepsContainer = $( '#cloudflare-steps' );
			this.$spinner = $( '.cloudflare-spinner' );
			this.$deactivateButton = $( '.cloudflare-deactivate.button' );
			this.$body = $( 'body' );

			this.renderStep( this.currentStep );

			this.$body.on(
				'click',
				'input[type="submit"].cloudflare-clear-cache',
				function ( e ) {
					e.preventDefault();
					this.purgeCache.apply( $( e.target ), [ this ] );
				}.bind( this )
			);

			this.$body.on(
				'click',
				'#cf-recheck-zones',
				function ( e ) {
					e.preventDefault();
					$( '#cf-recheck-zones' ).addClass( 'sui-button-onload' );
					this.updateZones.apply( $( e.target ), [ this ] );
				}.bind( this )
			);
		},

		purgeCache( self ) {
			// Show spinner
			const $button = this;
			$button.attr( 'disabled', true );
			self.showSpinner();

			Fetcher.common.call( 'wphb_cloudflare_purge_cache' )
				.then( ( response ) => {
					WPHB_Admin.notices.show(
						getString( 'successCloudflarePurge' )
					);
				} )
				.catch( ( reject ) => {
					WPHB_Admin.notices.show( reject.responseText, 'error' );
				} );

			// Remove spinner
			$button.removeAttr( 'disabled' );
			self.hideSpinner();
		},

		renderStep( step ) {
			const template = WPHB_Admin.DashboardCloudFlare.template(
				'#cloudflare-step-' + step
			);
			const content = template( this.data );
			const self = this;

			if ( content ) {
				this.currentStep = step;
				this.$stepsContainer
					.hide()
					.html( template( this.data ) )
					.fadeIn()
					.find( 'form' )
					.on( 'submit', function ( e ) {
						e.preventDefault();
						self.submitStep.call( self, $( this ) );
					} );

				// Trigger SUI select styles.
				if ( 'zone' === this.currentStep ) {
					$( '#cloudflare-zone' ).SUIselect2();
				}

				this.$spinner = this.$stepsContainer.find(
					'.cloudflare-spinner'
				);
			}

			this.bindEvents();
		},

		bindEvents() {
			const $howToInstructions = $( '#cloudflare-how-to' );

			$howToInstructions.hide();

			$( 'a.cloudflare-how-to-title' ).on( 'click', function ( e ) {
				e.preventDefault();
				$howToInstructions.toggle();
			} );

			if ( 'final' === this.currentStep ) {
				this.$deactivateButton.removeClass( 'hidden' );
			} else {
				this.$deactivateButton.addClass( 'hidden' );
			}
		},

		updateZones( self ) {
			Fetcher.common
				.call( 'wphb_cloudflare_recheck_zones' )
				.then( ( response ) => {
					self.data.zones = response.zones;
					self.renderStep( self.currentStep );
					$( '#cf-recheck-zones' ).removeClass( 'sui-button-onload' );
				} )
				.catch( ( error ) => {
					WPHB_Admin.notices.show( error, 'error' );
					$( '#cf-recheck-zones' ).removeClass( 'sui-button-onload' );
				} );
		},

		showSpinner() {
			this.$spinner.css( 'visibility', 'visible' );
		},

		hideSpinner() {
			this.$spinner.css( 'visibility', 'hidden' );
		},

		submitStep( $form ) {
			const self = this;

			$form.find( 'input[type=submit]' ).attr( 'disabled', 'true' );
			this.showSpinner();

			Fetcher.cloudflare
				.connect( this.currentStep, $form.serialize(), this.data )
				.then( ( response ) => {
					self.data = response.newData;
					self.renderStep( response.nextStep );

					if ( response.nextStep === 'final' ) {
						window.location.href = response.redirect;
					}
				} )
				.catch( ( error ) => {
					WPHB_Admin.notices.show( error, 'error' );
				} );

			$form.find( 'input[type=submit]' ).removeAttr( 'disabled' );
			self.hideSpinner();
		},
	};

	WPHB_Admin.DashboardCloudFlare.template = _.memoize( function ( id ) {
		let compiled,
			options = {
				evaluate: /<#([\s\S]+?)#>/g,
				interpolate: /\{\{\{([\s\S]+?)\}\}\}/g,
				escape: /\{\{([^\}]+?)\}\}(?!\})/g,
				variable: 'data',
			};

		return function ( data ) {
			_.templateSettings = options;
			compiled = compiled || _.template( $( id ).html() );
			return compiled( data );
		};
	} );
} )( jQuery );
