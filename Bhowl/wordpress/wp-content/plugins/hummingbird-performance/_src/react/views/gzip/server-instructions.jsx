/* global SUI */
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
import Button from '../../components/sui-button';
import Notice from '../../components/sui-notice';
import { UserContext } from '../../context';

/**
 * Server instructions component.
 */
export default class ServerInstructions extends React.Component {
	/**
	 * Share UI actions need to be performed manually for elements.
	 * They should be done in this method.
	 */
	componentDidMount() {
		ServerInstructions.initSUIcomponents();
	}

	componentDidUpdate() {
		ServerInstructions.initSUIcomponents();
	}

	static initSUIcomponents() {
		const codeSnippet = document.querySelector( 'pre.sui-code-snippet' );
		if ( codeSnippet ) {
			SUI.suiCodeSnippet( codeSnippet );
		}

		const el = document.getElementById( 'wphb-server-instructions-apache' );
		if ( el ) {
			SUI.suiTabs( el.querySelector( '.sui-tabs' ) );
		}

		const troubleshootLink = document.getElementById(
			'troubleshooting-link'
		);
		if ( troubleshootLink ) {
			troubleshootLink.addEventListener( 'click', ( e ) => {
				e.preventDefault();
				jQuery( 'html, body' ).animate(
					{
						scrollTop: jQuery( '#troubleshooting-apache' ).offset()
							.top,
					},
					'slow'
				);
			} );
		}
	}

	/**
	 * Return support link based on user status.
	 *
	 * @return {*}  Support link.
	 */
	getSupportLink() {
		let button = '';

		if ( this.context.isMember ) {
			button = (
				<Button
					url={ this.context.links.support.chat }
					target="blank"
					text={ __( 'Start a live chat.' ) }
				/>
			);
		} else {
			button = (
				<Button
					url={ this.context.links.support.forum }
					target="blank"
					text={ __( 'Open a support ticket.' ) }
				/>
			);
		}

		return (
			<p className="sui-description">
				{ __( 'Still having trouble?' ) } { button }
			</p>
		);
	}

	/**
	 * Render cache wrapper element.
	 *
	 * @return {*}  Notice or success message.
	 */
	cacheWrap() {
		let classNames = 'sui-hidden';
		if ( 'apache' === this.props.currentServer ) {
			classNames = '';
		}

		const enableButton = this.props.htaccessWritten ? (
			<Button
				onClick={ this.props.disable }
				classes={ [
					'sui-button',
					'sui-button-ghost',
					'sui-margin-top',
				] }
				text={ __( 'Deactivate' ) }
			/>
		) : (
			<Button
				onClick={ this.props.enable }
				classes={ [
					'sui-button',
					'sui-button-blue',
					'sui-margin-top',
				] }
				text={ __( 'Apply Rules' ) }
			/>
		);

		const noticeText = (
			<p>
				{ __(
					'We tried applying the .htaccess rules automatically but we weren’t able to. Make sure your file permissions on your .htaccess file are set to 644, or'
				) }
				<Button
					url="#apache-config-manual"
					classes={ [ 'switch-manual' ] }
					text={ __( 'switch to manual mode' ) }
				/>
				{ __( 'and apply the rules yourself.' ) }
			</p>
		);

		return (
			<div id="enable-cache-wrap" className={ classNames }>
				{ this.props.htaccessError && (
					<Notice
						classes={ [ 'sui-notice-warning' ] }
						message={ noticeText }
					/>
				) }
				{ enableButton }
			</div>
		);
	}

	/**
	 * Render Apache tabs.
	 *
	 * @return {*}  Tab content.
	 */
	renderApacheTabs() {
		const hideEnableButton =
			this.props.fullyEnabled &&
			( ! this.props.htaccessWritten || 'nginx' === this.props.server );

		return (
			<div
				id="wphb-server-instructions-apache"
				className="wphb-server-instructions"
				data-server="apache"
			>
				<div className="sui-tabs">
					<div data-tabs>
						<div className="active">{ __( 'Automatic' ) }</div>
						<div>{ __( 'Manual' ) }</div>
					</div>
					<div data-panes>
						<div className="active">
							<span className="sui-description">
								{ __(
									'Hummingbird can automatically apply GZip compression for Apache/LiteSpeed servers by writing your .htaccess file. Alternately, switch to Manual to apply these rules yourself.'
								) }
							</span>
							{ this.props.htaccessWritable &&
								! hideEnableButton &&
								this.cacheWrap() }
						</div>
						<div>
							<div className="apache-instructions">
								<p className="sui-description">
									{ __(
										'If you are unable to get the automated method working you can copy the generated code below into your .htaccess file to activate GZip compression.'
									) }
								</p>

								<ol className="wphb-listing wphb-listing-ordered">
									<li>
										{ __(
											'Copy & paste the generated code below into your .htaccess file'
										) }
									</li>
									<li>
										{ __( 'Next' ) },{ ' ' }
										<Button
											url="#"
											text={ __(
												're-check your GZip status'
											) }
										/>{ ' ' }
										{ __( 'to see if it worked' ) }.{ ' ' }
										<Button
											url="#"
											id="troubleshooting-link"
											text={ __(
												'Still having issues?'
											) }
										/>
									</li>
								</ol>

								<div id="wphb-code-snippet">
									<div
										id="wphb-code-snippet-apache"
										className="wphb-code-snippet"
									>
										<div className="wphb-block-content">
											<pre className="sui-code-snippet">
												{
													this.props.serverSnippets
														.apache
												}
											</pre>
										</div>
									</div>
								</div>
								<p
									className="sui-description sui-margin-top"
									id="troubleshooting-apache"
								>
									<strong>{ __( 'Troubleshooting' ) }</strong>
								</p>
								<p className="sui-description">
									{ __(
										'If .htaccess does not work, and you have access to vhosts.conf or httpd.conf try this:'
									) }
								</p>
								<ol className="wphb-listing wphb-listing-ordered">
									<li>
										{ __(
											'Look for your site in the file and find the line that starts with <Directory> - add the code above into that section and save the file.'
										) }
									</li>
									<li>
										{ __( 'Reload Apache/LiteSpeed.' ) }
									</li>
									<li>
										{ __(
											"If you don't know where those files are, or you aren't able to reload Apache/LiteSpeed, you would need to consult with your hosting provider or a system administrator who has access to change the configuration of your server"
										) }
									</li>
								</ol>
								{ this.getSupportLink() }
							</div>
						</div>
					</div>
				</div>
			</div>
		);
	}

	/**
	 * Render Nginx tab.
	 *
	 * @return {*}  Tab content.
	 */
	renderNginxTabs() {
		return (
			<div
				id="wphb-server-instructions-nginx"
				className="wphb-server-instructions"
				data-server="nginx"
			>
				<p className="sui-description">
					{ __( 'For NGINX servers:' ) }
				</p>

				<ol className="wphb-listing wphb-listing-ordered">
					<li>
						{ __(
							"Edit your nginx.conf. Usually it's located at /etc/nginx/nginx.conf or /usr/local/nginx/nginx.conf"
						) }
					</li>
					<li>
						{ __(
							'Copy the generated code found below and paste it inside your http or server block.'
						) }
					</li>
					<li>{ __( 'Reload/restart NGINX.' ) }</li>
				</ol>

				<p className="sui-description">
					{ __(
						'If you do not have access to your NGINX config files you will need to contact your hosting provider to make these changes.'
					) }
				</p>

				{ this.getSupportLink() }

				<pre className="sui-code-snippet">
					{ this.props.serverSnippets.nginx }
				</pre>
			</div>
		);
	}

	/**
	 * Render IIS tab.
	 *
	 * @return {*}  Tab content.
	 */
	renderIisTabs() {
		return (
			<div
				id="wphb-server-instructions-iis"
				className="wphb-server-instructions"
				data-server="iis"
			>
				<p className="sui-description">
					{ __( 'For IIS 7 servers and above,' ) }{ ' ' }
					<Button
						url="https://technet.microsoft.com/en-us/library/cc771003(v=ws.10).aspx"
						target="blank"
						text={ __( 'visit Microsoft TechNet' ) }
					/>
				</p>
			</div>
		);
	}

	/**
	 * Render Cloudflare tab.
	 *
	 * @since 2.7.2
	 *
	 * @return {*}  Tab content.
	 */
	renderCloudflareTabs() {
		return (
			<div
				id="wphb-server-instructions-cloudflare"
				className="wphb-server-instructions"
				data-server="cloudflare"
			>
				<p className="sui-description">
					{ __(
						'Hummingbird can control your CloudFlare GZip compression settings from here. Simply add your CloudFlare API details and configure away'
					) }
				</p>
			</div>
		);
	}

	/**
	 * Render component.
	 *
	 * @return {JSX.Element}  ServerInstructions component.
	 */
	render() {
		if ( this.props.htaccessWritten && this.props.fullyEnabled ) {
			return (
				<React.Fragment>
					<Notice
						classes={ [ 'sui-notice-info' ] }
						message={ __(
							'Automatic .htaccess rules have been applied.'
						) }
					/>
					<Button
						onClick={ this.props.disable }
						classes={ [ 'sui-button', 'sui-button-ghost' ] }
						text={ __( 'Deactivate' ) }
					/>
				</React.Fragment>
			);
		}

		return (
			<React.Fragment>
				{ 'apache' === this.props.currentServer &&
					this.renderApacheTabs() }
				{ 'nginx' === this.props.currentServer &&
					this.renderNginxTabs() }
				{ 'iis' === this.props.currentServer && this.renderIisTabs() }
				{ 'cloudflare' === this.props.currentServer &&
					this.renderCloudflareTabs() }
			</React.Fragment>
		);
	}
}

ServerInstructions.contextType = UserContext;
