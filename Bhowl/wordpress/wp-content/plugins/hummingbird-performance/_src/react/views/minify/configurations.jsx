/**
 * External dependencies
 */
import React from 'react';

/**
 * WordPress dependencies
 */
import { __ } from '@wordpress/i18n';

/**
 * Internal dependencies
 */
import './configurations.scss';
import Action from '../../components/sui-box/action';
import Box from '../../components/sui-box';
import Button from '../../components/sui-button';
import Checkbox from '../../components/sui-checkbox';
import SettingsRow from '../../components/sui-box-settings/row';
import Table from '../../components/sui-table';
import Tabs from '../../components/sui-tabs';
import Tag from '../../components/sui-tag';
import Select from '../../components/sui-select';

/**
 * Configurations component.
 *
 * @since 2.7.2
 */
export default class Configurations extends React.Component {
	/**
	 * Component header.
	 *
	 * @return {JSX.Element}  Header action buttons.
	 */
	getHeaderActions() {
		const buttons = (
			<Button
				text={ __( 'Reset settings' ) }
				classes={ [ 'sui-button', 'sui-button-ghost' ] }
				icon="sui-icon-undo"
				onClick={ this.props.resetSettings }
			/>
		);

		return <Action type="right" content={ buttons } />;
	}

	/**
	 * Component footer.
	 *
	 * @return {JSX.Element}  Footer action buttons.
	 */
	getFooterActions() {
		const buttons = (
			<Button
				text={ __( 'Publish changes' ) }
				classes={ [ 'sui-button', 'sui-button-blue' ] }
				onClick={ this.props.saveSettings }
			/>
		);

		return <Action type="right" content={ buttons } />;
	}

	/**
	 * Files tab content.
	 *
	 * @return {JSX.Element}  Content
	 */
	tabFiles() {
		return (
			<React.Fragment>
				<Checkbox
					id="auto-css"
					label={ __( 'CSS files' ) }
					description={ __(
						'Hummingbird will minify your CSS files, generating a version that loads faster. It will remove unnecessary characters or lines of code from your file to make it more compact.'
					) }
					size="sm"
					checked={ this.props.enabled.styles }
					onChange={ this.props.onEnabledChange }
				/>

				<Checkbox
					id="auto-js"
					label={ __( 'JavaScript files' ) }
					description={ __(
						'JavaScript minification is the process of removing whitespace and any code that is not necessary to create a smaller but valid code.'
					) }
					size="sm"
					checked={ this.props.enabled.scripts }
					onChange={ this.props.onEnabledChange }
				/>
			</React.Fragment>
		);
	}

	/**
	 * Presets tab content.
	 *
	 * @return {JSX.Element}  Content
	 */
	tabPresets() {
		const bodyElements = [
			[
				{
					content: (
						<React.Fragment>
							<img
								className="sui-image"
								alt=""
								src={
									this.props.link.wphbDirUrl +
									'admin/assets/image/divi.png'
								}
								srcSet={
									this.props.link.wphbDirUrl +
									'admin/assets/image/divi@2x.png 2x'
								}
							/>
							<strong>Divi</strong>
						</React.Fragment>
					),
				},
				{
					content: this.props.isMember ? (
						<Tag
							value={ __( 'Coming Soon' ) }
							type="blue sui-tag-sm"
						/>
					) : (
						<Tag value={ __( 'Pro' ) } type="pro" />
					),
				},
				{
					content: __(
						'Enable the preset to auto-optimize this theme.'
					),
				},
			],
		];

		return (
			<React.Fragment>
				{ this.props.module.isDivi && (
					<Table
						header={ [
							__( 'Available presets' ),
							__( 'Status' ),
							'',
						] }
						body={ bodyElements }
						flushed="true"
					/>
				) }
				{ ! this.props.module.isWhiteLabeled && (
					<SettingsRow
						classes="sui-upsell-row"
						content={
							<React.Fragment>
								<img
									className="sui-image sui-upsell-image"
									alt=""
									src={
										this.props.link.wphbDirUrl +
										'admin/assets/image/hummingbird-upsell-minify.png'
									}
									srcSet={
										this.props.link.wphbDirUrl +
										'admin/assets/image/hummingbird-upsell-minify@2x.png 2x'
									}
								/>
								<div className="sui-upsell-notice sui-margin-left sui-margin-bottom">
									<p>
										{ this.props.module.isDivi
											? __(
													'Preset for Divi coming soon on Hummingbird Pro! Hummingbird Pro will automatically compress and optimize your Divi theme files with just one click. Not using Divi? Vote for the next preset you would like us to add.'
											  )
											: __(
													'Presets coming soon! Hummingbird Pro will automatically compress and optimize your theme and plugin files with just a click. You can vote for the next preset you would like added.'
											  ) }
										<br />
										<Button
											url="https://forms.gle/7iwfSxTd21kn5pdT6"
											target="_blank"
											text={ __(
												'Vote for the next Preset'
											) }
										/>
									</p>
								</div>
							</React.Fragment>
						}
					/>
				) }
			</React.Fragment>
		);
	}

	/**
	 * Exclusions tab content.
	 *
	 * @return {JSX.Element}  Content
	 */
	tabExclusions() {
		const types = [ 'styles', 'scripts' ];

		const select = jQuery( '#wphb-auto-exclude' );
		select.val( null ).trigger( 'change' );

		types.forEach( ( type ) => {
			if ( 'undefined' === this.props.assets[ type ] ) {
				return;
			}

			Object.values( this.props.assets[ type ] ).forEach( ( el ) => {
				const text = el.handle + ' (' + __( 'file: ' ) + el.src + ')';
				const excluded = window.lodash.includes(
					this.props.exclusions[ type ],
					el.handle
				);

				const option = new Option( text, el.handle, false, excluded );
				option.dataset.type = type;
				select.append( option );
			} );
		} );

		select.trigger( 'change' );

		return (
			<Select
				selectId="wphb-auto-exclude"
				classes="sui-select-lg"
				label={ __( 'File exclusions' ) }
				description={ __(
					'Type the filename and click on the filename to add it to the list.'
				) }
				placeholder={ __( 'Start typing the files to exclude…' ) }
				multiple="true"
			/>
		);
	}

	/**
	 * Tabs content.
	 *
	 * @return {Object}  Tab content elements.
	 */
	getTabs() {
		return [
			{
				id: 'auto-files',
				description: __(
					'Choose which files you want to automatically optimize.'
				),
				content: this.tabFiles(),
				active: true,
			},
			{
				id: 'auto-presets',
				description: __(
					'Use presets to optimize your theme and plugins automatically. No manual configuration needed.'
				),
				content: this.tabPresets(),
			},
			{
				id: 'auto-exclusions',
				description: __(
					"By default, we'll optimize all the CSS and JS files we can find. If you have specific files you want to leave as-is, list them here, and we'll exclude them."
				),
				content: this.tabExclusions(),
			},
		];
	}

	/**
	 * Component body.
	 *
	 * @return {JSX.Element}  Content.
	 */
	getContent() {
		const tabsMenu = [
			{
				title: __( 'Files' ),
				id: 'auto-files',
				checked: true,
			},
			{
				title: __( 'Presets' ),
				id: 'auto-presets',
			},
			{
				title: __( 'Exclusions' ),
				id: 'auto-exclusions',
			},
		];

		return (
			<React.Fragment>
				<p>
					{ __(
						'The configurations will be applied to the enabled automatic optimization option.'
					) }
				</p>
				<Tabs
					menu={ tabsMenu }
					tabs={ this.getTabs() }
					flushed="true"
				/>
			</React.Fragment>
		);
	}

	/**
	 * Render component.
	 *
	 * @return {JSX.Element}  Configurations component.
	 */
	render() {
		return (
			<Box
				boxClass="box-minification-assets-auto-config"
				loading={ this.props.loading }
				title={ __( 'Configurations' ) }
				headerActions={ this.getHeaderActions() }
				content={ this.getContent() }
				footerActions={ this.getFooterActions() }
			/>
		);
	}
}
