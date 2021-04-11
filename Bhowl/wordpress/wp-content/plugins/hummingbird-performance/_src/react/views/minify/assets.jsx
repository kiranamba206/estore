/* global SUI */

/**
 * External dependencies
 */
import React from 'react';
import classNames from 'classnames';

/**
 * WordPress dependencies
 */
import { __ } from '@wordpress/i18n';

/**
 * Internal dependencies
 */
import './assets.scss';
import Action from '../../components/sui-box/action';
import Box from '../../components/sui-box';
import Button from '../../components/sui-button';
import Icon from '../../components/sui-icon';
import Tag from '../../components/sui-tag';
import Toggle from '../../components/sui-toggle';
import Tooltip from '../../components/sui-tooltip';
import SideTabs from '../../components/sui-side-tabs';

/**
 * Assets component.
 *
 * @since 2.7.2
 */
export default class Assets extends React.Component {
	/**
	 * Component header.
	 *
	 * @return {JSX.Element}  Header action buttons.
	 */
	getHeaderActions() {
		const buttons = (
			<React.Fragment>
				<Tooltip
					classes="sui-tooltip-constrained"
					text={ __(
						'Added/removed plugins or themes? Update your file list to include new files, and remove old ones'
					) }
					data={
						<Button
							text={ __( 'Re-Check Files' ) }
							classes={ [ 'sui-button', 'sui-button-ghost' ] }
							icon="sui-icon-update"
							onClick={ this.props.reCheckFiles }
						/>
					}
				/>
				<Tooltip
					classes={ [
						'sui-tooltip-constrained',
						'sui-tooltip-top-right',
					] }
					text={ __(
						'Clears all local or hosted assets and recompresses files that need it'
					) }
					data={
						<Button
							text={ __( 'Clear cache' ) }
							classes="sui-button"
							onClick={ this.props.clearCache }
						/>
					}
				/>
			</React.Fragment>
		);

		return <Action type="right" content={ buttons } />;
	}

	/**
	 * Show "How does it work" modal.
	 */
	showHowDoesItWork() {
		SUI.openModal(
			'automatic-ao-hdiw-modal-content',
			'wphb-basic-hdiw-link'
		);
	}

	/**
	 * Handle "Manual" button click.
	 */
	onManualClick() {
		SUI.openModal(
			'wphb-advanced-minification-modal',
			'wphb-switch-to-advanced',
			'wphb-switch-to-advanced',
			false
		);
	}

	/**
	 * Speedy view toggle.
	 *
	 * @return {JSX.Element}  Box element with view.
	 */
	speedyView() {
		return (
			<Box
				boxClass={ classNames( {
					'wphb-close-section': 'basic' === this.props.view,
				} ) }
				headerActions={
					<React.Fragment>
						<span className="wphb-ao-type-icon">
							<Icon classes="sui-icon-hummingbird" />
						</span>

						<div className="wphb-ao-type-title">
							<strong>{ __( 'Speedy' ) }</strong>
							<Tag value={ __( 'Recommended' ) } type="sm" />
							<small>
								{ __(
									'Automatically optimize and compress all files.'
								) }
							</small>
						</div>

						<Action
							type="right"
							content={
								<Toggle
									id="wphb-speedy-toggle"
									checked={ 'speedy' === this.props.view }
									onChange={ this.props.handleToggleChange }
									data-type="speedy"
								/>
							}
						/>
					</React.Fragment>
				}
			/>
		);
	}

	/**
	 * Basic view toggle.
	 *
	 * @return {JSX.Element}  Box element with view.
	 */
	basicView() {
		return (
			<Box
				boxClass={ classNames( {
					'wphb-close-section': 'speedy' === this.props.view,
				} ) }
				headerActions={
					<React.Fragment>
						<span className="wphb-ao-type-icon">
							<Icon classes="sui-icon-speed-optimize" />
						</span>

						<div className="wphb-ao-type-title">
							<strong>{ __( 'Basic' ) }</strong>
							<small>
								{ __(
									'Apply basic compression to all files.'
								) }
							</small>
						</div>

						<Action
							type="right"
							content={
								<Toggle
									id="wphb-basic-toggle"
									checked={ 'basic' === this.props.view }
									onChange={ this.props.handleToggleChange }
									data-type="basic"
								/>
							}
						/>
					</React.Fragment>
				}
			/>
		);
	}

	/**
	 * Component body.
	 *
	 * @return {JSX.Element}  Content.
	 */
	getContent() {
		const sideTabs = [
			{
				title: __( 'Automatic' ),
				id: 'wphb-ao-auto',
				name: 'asset_optimization_mode',
				value: 'auto',
				checked: true,
			},
			{
				title: __( 'Manual' ),
				id: 'wphb-ao-manual',
				name: 'asset_optimization_mode',
				value: 'manual',
				onChange: this.onManualClick,
			},
		];

		return (
			<React.Fragment>
				<p>
					{ __(
						'Optimizing your assets will compress and organize them in a way that improves page load times. You can choose to use our automated options, or manually configure each file yourself.'
					) }
				</p>

				<div className="sui-actions" style={ { float: 'right' } }>
					<small>
						<Button
							text={ __( 'How Does it Work?' ) }
							id="wphb-basic-hdiw-link"
							url="#"
							onClick={ this.showHowDoesItWork }
						/>
					</small>
				</div>

				<SideTabs tabs={ sideTabs } />

				<div className="wphb-minification-files">
					{ this.speedyView() }
					{ this.basicView() }
				</div>
			</React.Fragment>
		);
	}

	/**
	 * Render component.
	 *
	 * @return {JSX.Element}  Assets component.
	 */
	render() {
		return (
			<Box
				boxClass="box-minification-assets-auto"
				loading={ this.props.loading }
				title={ __( 'Assets' ) }
				headerActions={ this.getHeaderActions() }
				content={ this.getContent() }
			/>
		);
	}
}
