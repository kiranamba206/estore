/* global WPHB_Admin */
/* global wphbHistoricFieldData */
/* global google */

import Fetcher from '../utils/fetcher';
import PerfScanner from '../scanners/PerfScanner';

( function ( $ ) {
	'use strict';
	WPHB_Admin.performance = {
		module: 'performance',
		iteration: 0,
		progress: 0,
		pressedKeys: [],
		key_timer: false,

		init() {
			const self = this;

			this.wphbSetInterval();

			document.onkeyup = function ( e ) {
				clearInterval( self.key_timer );
				self.wphbSetInterval();
				e = e || event;
				self.pressedKeys.push( e.keyCode );
				const count = self.pressedKeys.length;
				if ( count >= 2 ) {
					// Get the previous key pressed. If they are H+B, we'll display the error
					if (
						66 === self.pressedKeys[ count - 1 ] &&
						72 === self.pressedKeys[ count - 2 ]
					) {
						const errorDetails = document.getElementById(
							'wphb-error-details'
						);
						errorDetails.style.display = 'block';
					}
				}
			};

			// Init scanner.
			this.scanner = new PerfScanner( 100, 0 );

			// Run performance test from empty report meta box.
			$( '#run-performance-test' ).on( 'click', function ( e ) {
				e.preventDefault();

				window.SUI.openModal(
					'run-performance-test-modal',
					'wpbody-content'
				);

				$( this ).attr( 'disabled', true );
				self.scanner.start();
			} );

			// If a hash is present in URL, let's open the rule extra content
			const hash = window.location.hash;
			if ( hash ) {
				const row = $( hash );
				if ( row.length && ! row.hasClass( 'sui-box' ) ) {
					row.find( '.sui-accordion-open-indicator' ).trigger(
						'click'
					);
					$( 'html, body' ).animate(
						{
							scrollTop: row.offset().top,
						},
						1000
					);
				}
			}

			// Save performance test settings
			$( 'body' ).on( 'submit', '.settings-frm', function ( e ) {
				e.preventDefault();
				const formData = $( this ).serialize();

				Fetcher.performance
					.savePerformanceTestSettings( formData )
					.then( () => WPHB_Admin.notices.show() );
				return false;
			} );

			/**
			 * Init Google charts on Historic Field Data meta box page.
			 *
			 * @since 2.0.0
			 */
			if (
				'undefined' !== typeof google &&
				'undefined' !== typeof wphbHistoricFieldData
			) {
				google.charts.load( 'current', {
					packages: [ 'corechart', 'bar' ],
				} );

				google.charts.setOnLoadCallback( () => {
					this.drawChart(
						wphbHistoricFieldData.fcp,
						'first_contentful_paint'
					);
					$( window ).resize( () =>
						this.drawChart(
							wphbHistoricFieldData.fcp,
							'first_contentful_paint'
						)
					);
				} );

				google.charts.setOnLoadCallback( () => {
					this.drawChart(
						wphbHistoricFieldData.fid,
						'first_input_delay'
					);
					$( window ).resize( () =>
						this.drawChart(
							wphbHistoricFieldData.fid,
							'first_input_delay'
						)
					);
				} );
			}

			/**
			 * Parse dashboard widget device setting change.
			 *
			 * @since 2.0.0
			 */
			$( 'input[name=desktop-report]' ).on( 'change', function ( e ) {
				const otherClass =
					'desktop_report-true' === e.target.id
						? 'desktop_report-false'
						: 'desktop_report-true';
				e.target.parentNode.classList.add( 'active' );
				document
					.getElementById( otherClass )
					.parentNode.classList.remove( 'active' );
			} );

			/**
			 * Parse subsite settings change.
			 *
			 * @since 2.0.0
			 */
			$( 'input[name=subsite-tests]' ).on( 'change', function ( e ) {
				const otherClass =
					'subsite_tests-false' === e.target.id
						? 'subsite_tests-true'
						: 'subsite_tests-false';
				e.target.parentNode.classList.add( 'active' );
				document
					.getElementById( otherClass )
					.parentNode.classList.remove( 'active' );
			} );

			/**
			 * Parse report type setting change.
			 *
			 * @since 2.0.0
			 */
			$( 'input[name=report-type]' ).on( 'change', function ( e ) {
				const divs = document.querySelectorAll(
					'input[name=report-type]'
				);
				for ( let i = 0; i < divs.length; ++i ) {
					divs[ i ].parentNode.classList.remove( 'active' );
				}
				e.target.parentNode.classList.add( 'active' );
			} );

			/**
			 * Refresh page, when selecting a report type.
			 *
			 * @since 2.0.0
			 */
			$( 'select[name=wphb-performance-report-type]' ).on(
				'change',
				function ( e ) {
					const url = new URL( window.location );
					url.searchParams.set( 'type', e.target.value );
					window.location = url;
				}
			);

			return this;
		},

		wphbSetInterval() {
			const self = this;

			this.key_timer = window.setInterval( function () {
				// Clean pressedKeys every 1sec
				self.pressedKeys = [];
			}, 1000 );
		},

		/**
		 * Draw chart on Historic Field Data meta box.
		 *
		 * @since 2.0.0
		 *
		 * @param {Object} strings
		 * @param {string} chartID
		 */
		drawChart( strings, chartID ) {
			const data = google.visualization.arrayToDataTable( [
				[
					'Type',
					'Fast',
					{ type: 'string', role: 'tooltip', p: { html: true } },
					'Average',
					{ type: 'string', role: 'tooltip', p: { html: true } },
					'Slow',
					{ type: 'string', role: 'tooltip', p: { html: true } },
				],
				[
					'',
					strings.fast,
					this.generateTooltip( 'fast', strings.fast_desc ),
					strings.average,
					this.generateTooltip( 'average', strings.average_desc ),
					strings.slow,
					this.generateTooltip( 'slow', strings.slow_desc ),
				],
			] );

			const options = {
				tooltip: { isHtml: true },
				colors: [ '#1ABC9C', '#FECF2F', '#FF6D6D' ],
				chartArea: { width: '100%' },
				hAxis: {
					baselineColor: '#fff',
					gridlines: { color: '#fff', count: 0 },
					textPosition: 'none',
				},
				isStacked: 'percent',
				height: 80,
				legend: 'none',
			};

			const chart = new google.visualization.BarChart(
				document.getElementById( chartID )
			);
			chart.draw( data, options );
		},

		/**
		 * Generate custom tooltip.
		 *
		 * @since 2.0.0
		 *
		 * @param {string} type   Metrics type. Accepts: fast, average, slow.
		 * @param {string} value  Tooltip text.
		 *
		 * @return {string} Div element.
		 */
		generateTooltip( type, value ) {
			return (
				'<div class="wphb-field-data-tooltip wphb-tooltip-' +
				type +
				'">' +
				value +
				'</div>'
			);
		},
	};
} )( jQuery );
