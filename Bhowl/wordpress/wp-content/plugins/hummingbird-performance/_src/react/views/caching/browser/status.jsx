/**
 * External dependencies
 */
import React from 'react';
import classNames from 'classnames';

/**
 * WordPress dependencies
 */
import { __, sprintf } from '@wordpress/i18n';

/**
 * Internal dependencies
 */
import Action from '../../../components/sui-box/action';
import BorderFrame from '../../../components/border-frame';
import Box from '../../../components/sui-box';
import Button from '../../../components/sui-button';
import Notice from '../../../components/sui-notice';
import Tag from '../../../components/sui-tag';
import Tooltip from '../../../components/sui-tooltip';

/**
 * Status component.
 *
 * @since 2.7.2
 */
class Status extends React.Component {
	/**
	 * Generate the status tag for browser caching, based on the number of active
	 * elements (JavaScript, CSS, Media, Images).
	 *
	 * @param {Object}  status      Browser caching status object.
	 * @param {boolean} successTag  On success show a tick tag.
	 * @return {*} Browser caching status.
	 */
	getStatus( status, successTag = false ) {
		let statusTag;

		if ( successTag ) {
			statusTag = <Tag />;
		}

		const failedItems = this.getFailedItems( status );

		if ( 0 < failedItems ) {
			statusTag = <Tag value={ failedItems } type="warning" />;
		}

		return statusTag;
	}

	/**
	 * Get an array of failed items.
	 *
	 * @param {Object} items  Browser caching statues.
	 * @return {number} Number of failed items.
	 */
	getFailedItems( items ) {
		const failed = Object.entries( items ).filter( ( item ) => {
			return (
				! item[ 1 ] ||
				this.props.data.recommended[ item[ 0 ].toLowerCase() ].value >
					item[ 1 ]
			);
		} );

		let failedItems = failed.length;
		if ( 0 < failedItems && this.props.cloudflare.isSetup ) {
			failedItems++;
		}

		return failedItems;
	}

	/**
	 * Check problems that might be related to bad configuration.
	 *
	 * @return {*}  Notice.
	 */
	checkExternalProblems() {
		if ( this.props.loading ) {
			return;
		}

		if (
			this.props.data.htaccessWritable &&
			! this.props.data.htaccessWritten
		) {
			return;
		}

		const failed = Object.values( this.props.status ).filter( ( item ) => {
			return ! item || 'privacy' === item;
		} );

		// There must be another plugin/server config that is setting its own browser caching stuff.
		if ( 4 !== Object.keys( this.props.status ).length || 0 < failed ) {
			const message = (
				<React.Fragment>
					<p>{ __( 'Browser Caching is not working properly:' ) }</p>
					<p>
						{ __(
							'Your server may not have the "expires" module enabled (mod_expires for Apache, ngx_http_headers_module for NGINX). Another plugin may be interfering with the configuration. If re-checking and restarting does not resolve, please check with your host or'
						) }
						&nbsp;
						<Button
							text={ __( 'open a support ticket.' ) }
							url={ this.props.link.support.forum }
							target="blank"
						/>
					</p>
				</React.Fragment>
			);

			return <Notice message={ message } classes="sui-notice-error" />;
		}
	}

	/**
	 * Show Cloudflare notice.
	 */
	showCloudflareNotice() {
		if ( this.props.loading ) {
			return;
		}

		if ( this.props.cloudflare.isSetup ) {
			return;
		}

		if ( 'dismiss' === this.props.cloudflare.notice ) {
			return;
		}

		let notice = __( 'Using CloudFlare?' );
		if ( this.props.cloudflare.isConnected ) {
			notice = __( 'Ahoi, we’ve detected you’re using CloudFlare!' );
		}
		notice =
			notice +
			' ' +
			__(
				'Connect your account to control your settings via Hummingbird.'
			);
		if ( ! this.props.cloudflare.isConnected ) {
			notice =
				notice +
				' ' +
				__(
					'CloudFlare is a Content Delivery Network (CDN) that sends traffic through its global network to automatically optimize the delivery of your site so your visitors can browse your site at top speeds. There is a free plan and we recommend using it.'
				);
		}

		const connectButton = (
			<Button
				text={ __( 'Connect' ) }
				icon="sui-icon-plus-circle"
				classes={ [
					'sui-button',
					'sui-button-ghost',
					'connect-cloudflare-link',
				] }
				onClick={ this.props.onCloudflareClick }
			/>
		);

		return (
			<div className="sui-box-settings-row sui-upsell-row cf-dash-notice sui-no-padding-top">
				{ ! this.props.data.isWhiteLabeled && (
					<img
						className="sui-image sui-upsell-image"
						alt={ __( 'Connect your account to Cloudflare' ) }
						src={
							this.props.link.wphbDirUrl +
							'admin/assets/image/graphic-hb-cf-sell.png'
						}
						srcSet={
							this.props.link.wphbDirUrl +
							'admin/assets/image/graphic-hb-cf-sell.png 1x, ' +
							this.props.link.wphbDirUrl +
							'admin/assets/image/graphic-hb-cf-sell@2x.png 2x'
						}
					/>
				) }
				{ this.props.data.isWhiteLabeled ? (
					<Notice
						message={ notice }
						content={ connectButton }
						classes="sui-notice-grey"
					/>
				) : (
					<div className="sui-upsell-notice">
						<p>
							{ notice }
							<span>
								{ connectButton }
								<Button
									text={ __( 'Learn More' ) }
									url="https://wpmudev.com/blog/cloudflare-review/"
									target="_blank"
								/>
							</span>
						</p>
					</div>
				) }
			</div>
		);
	}

	/**
	 * Get content for the component.
	 *
	 * @return {Object}  Module content.
	 */
	getContent() {
		const failedItems = this.getFailedItems( this.props.status );

		let classes = 'sui-notice-warning';

		// Get the tooltips and icons.
		let text = sprintf(
			/* translators: %d - number of failed items */
			__(
				'%d of your cache types don’t meet the recommended expiry period of 1 year. Configure browser caching below.'
			),
			failedItems
		);

		if ( 0 === failedItems ) {
			classes = 'sui-notice-success';
			text = __(
				'All of your cache types meet the recommended expiry period of 1 year. Great work!'
			);

			// Browser caching enabled on host site.
			if ( false === this.props.data.htaccessWritten ) {
				text = __(
					'All of your cache types meet the recommended expiry period of 1 year. Your hosting has automatically pre-configured browser caching for you and no further actions are required.'
				);
			}
		}

		// Build the items array.
		const items = Object.entries( this.props.status ).map( ( item ) => {
			let tag = 'warning';
			let type = item[ 0 ].toLowerCase();
			let iconLabel = type;

			if ( item[ 1 ] >= this.props.data.recommended[ type ].value ) {
				tag = 'success';
			}

			const recommendedTooltipText = sprintf(
				/* translators: %s - recommended value label */
				__(
					'The recommended value for this file type is at least %s.'
				),
				this.props.data.recommended[ type ].label
			);

			const recommendedTag = (
				<Tag
					value={ this.props.data.recommended[ type ].label }
					type="disabled"
				/>
			);

			if ( 'javascript' === type ) {
				type = 'js';
				iconLabel = 'js';
			} else if ( 'images' === type ) {
				iconLabel = 'img';
			}

			const labelData = (
				<React.Fragment>
					<span
						className={ classNames(
							'wphb-filename-extension',
							'wphb-filename-extension-' + type
						) }
					>
						{ iconLabel }
					</span>
					<span className="wphb-filename-extension-label">
						{ item[ 0 ] }
					</span>
				</React.Fragment>
			);

			return {
				label: labelData,
				expiry: (
					<Tooltip
						text={ recommendedTooltipText }
						data={ recommendedTag }
						classes={ [ 'sui-tooltip-constrained' ] }
					/>
				),
				details: (
					<Tag value={ this.props.human[ item[ 0 ] ] } type={ tag } />
				),
			};
		} );

		if ( this.props.cloudflare.isSetup ) {
			const cfRow = {
				label: (
					<React.Fragment>
						<Tooltip
							text={ this.props.data.cacheTypes.cloudflare }
							data="oth"
							classes={ classNames(
								'sui-tooltip-constrained',
								'wphb-filename-extension',
								'wphb-filename-extension-other'
							) }
						/>
						<span className="wphb-filename-extension-label">
							Cloudflare
						</span>
					</React.Fragment>
				),
				expiry: items[ 0 ].expiry,
				details: items[ 0 ].details,
			};

			window.lodash.assign( items, { 4: cfRow } );
		}

		const boxClass =
			! this.props.cloudflare.isSetup &&
			'dismiss' !== this.props.cloudflare.notice
				? 'sui-box-body'
				: '';

		return (
			<React.Fragment>
				<div className={ boxClass }>
					{ this.checkExternalProblems() }

					<p>
						{ __(
							'Store temporary data on your visitors devices so that they don’t have to download assets twice if they don’t have to. This results in a much faster second time round page load speed.'
						) }
					</p>

					<Notice message={ text } classes={ classes } />

					<BorderFrame
						header={ [
							__( 'File type' ),
							__( 'Current expiry' ),
							__( 'Recommended expiry' ),
						] }
						elements={ items }
					/>
				</div>

				{ this.showCloudflareNotice() }
			</React.Fragment>
		);
	}

	/**
	 * Render component.
	 *
	 * @return {*} Status component.
	 */
	render() {
		const browserCaching = this.getStatus( this.props.status );

		const rightAction = (
			<React.Fragment>
				<span className="label-notice-inline sui-hidden-xs sui-hidden-sm">
					{ __( 'Made changes?' ) }
				</span>
				<Button
					text={ __( 'Re-check status' ) }
					onClick={ this.props.onUpdate }
					classes={ [ 'sui-button', 'sui-button-ghost' ] }
					icon="sui-icon-update"
				/>
			</React.Fragment>
		);

		const headerActions = (
			<React.Fragment>
				{ browserCaching && (
					<Action type="left" content={ browserCaching } />
				) }
				<Action type="right" content={ rightAction } />
			</React.Fragment>
		);

		const boxBodyClass =
			! this.props.cloudflare.isSetup &&
			'dismiss' !== this.props.cloudflare.notice
				? 'sui-upsell-items'
				: '';

		return (
			<Box
				boxBodyClass={ [ boxBodyClass ] }
				loading={ this.props.loading }
				title={ __( 'Status' ) }
				headerActions={ headerActions }
				content={ this.getContent() }
			/>
		);
	}
}

export default Status;
