<?php
/**
 * Browser caching meta box.
 *
 * @package Hummingbird
 *
 * @var array  $results            Current report.
 * @var array  $human_results      Current report in readable format.
 * @var array  $expires            Current expiration value settings.
 * @var bool   $cf_active          CloudFlare status.
 * @var bool   $show_cf_notice     Show CloudFlare notice.
 * @var bool   $cf_server          Do we detect CloudFlare headers.
 * @var int    $cf_current         CloudFlare expiration value.
 * @var string $cf_disable_url     CloudFlare deactivate URL.
 * @var string $server_type        Current server type.
 * @var array  $snippets           Code snippets for servers.
 * @var bool   $htaccess_written   File .htaccess is written.
 * @var bool   $htaccess_writable  File .htaccess is writable.
 * @var bool   $already_enabled    Caching is enabled.
 * @var bool   $all_expiry         All expiry values the same.
 * @var string $enable_link        Activate automatic caching link.
 * @var string $disable_link       Disable automatic caching link.
 * @var string $recheck_expiry_url Url to recheck status.
 * @var bool   $different_expiry   Are different expiry values used?
 */

use Hummingbird\Core\Module_Server;
use Hummingbird\Core\Utils;

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

?>

<div class="sui-box-settings-row">
	<div class="sui-box-settings-col-1">
		<span class="sui-settings-label"><?php esc_html_e( 'Server Type', 'wphb' ); ?></span>
		<span class="sui-description">
			<?php esc_html_e( 'Choose your server type so Hummingbird can give you the rules to apply caching.', 'wphb' ); ?>
		</span>
	</div>
	<div class="sui-box-settings-col-2">
		<div class="sui-form-field sui-input-md">
			<label for="wphb-server-type" class="sui-label"><?php esc_html_e( 'Server type', 'wphb' ); ?></label>
			<?php Utils::get_servers_dropdown( $server_type ); ?>
		</div>
		<?php
		if ( ! $cf_active && ! $show_cf_notice ) {
			$servers = Module_Server::get_servers();
			$this->admin_notices->show_inline(
				sprintf( /* translators: %1$s: server type, %2$s: opening a tag, %3$s: closing a tag */
					esc_html__( "We've automatically detected your server type is %1\$s. If this is incorrect, manually select your server type to generate the relevant rules and instructions. If you are using Cloudflare %2\$sconnect your account%3\$s to control your cache settings from here.", 'wphb' ),
					esc_html( $servers[ $server_type ] ),
					'<a href="#" class="connect-cloudflare-link">',
					'</a>'
				),
				'grey'
			);
		} elseif ( ! $cf_active ) {
			$this->admin_notices->show_inline(
				esc_html__( 'We’ve detected you’re using Cloudflare which handles browser caching for you. You can control your CloudFlare settings from Hummingbird by connecting your account below.', 'wphb' ),
				'grey'
			);
		}
		?>
	</div>
</div>

<div class="sui-box-settings-row">
	<div class="sui-box-settings-col-1">
		<span class="sui-settings-label"><?php esc_html_e( 'Expiry Time', 'wphb' ); ?></span>
		<span class="sui-description">
			<?php esc_html_e( 'Please choose your desired expiry time. Google recommends 1 year as a good benchmark.', 'wphb' ); ?>
		</span>
	</div><!-- end sui-box-settings-col-1 -->
	<div class="sui-box-settings-col-2">
		<form method="post" id="expiry-settings">
			<input type="hidden" class="hb-server-type" name="hb_server_type" value="<?php echo esc_attr( $server_type ); ?>">
			<input type="hidden" id="hb_all_expiry" <?php checked( $all_expiry ); ?>>
			<?php wp_nonce_field( 'wphb-caching' ); ?>
			<div class="sui-side-tabs sui-tabs">
				<?php if ( ! $cf_active && ! $cf_server ) : ?>
					<div data-tabs>
						<div class="<?php echo $all_expiry ? 'active' : ''; ?>" data-name="expiry-set-type" data-value="all">
							<?php esc_html_e( 'All file types', 'wphb' ); ?>
						</div>
						<div class="<?php echo $all_expiry ? '' : 'active'; ?>" data-name="expiry-set-type" data-value="single">
							<?php esc_html_e( 'Individual file types', 'wphb' ); ?>
						</div>
					</div>
					<div data-panes>
						<div class="sui-tab-boxed <?php echo $different_expiry ? 'active' : ''; ?>">
							<div class="sui-form-field sui-input-md">
								<label class="sui-label">
									<?php esc_html_e( 'JavaScript, CSS, Media, Images', 'wphb' ); ?>
								</label>
								<?php
								Utils::get_caching_frequencies_dropdown(
									array(
										'name'      => 'set-expiry-all',
										'class'     => 'sui-select wphb-expiry-select',
										'selected'  => $expires['CSS'],
										'data-type' => 'all',
									)
								);
								?>
							</div>
						</div>
						<div class="sui-tab-boxed <?php echo $different_expiry ? '' : 'active'; ?>">
							<?php foreach ( $human_results as $cache_type => $result ) : ?>
								<?php $label = strtolower( $cache_type ); ?>
								<div class="sui-form-field sui-input-md">
									<label class="sui-label">
										<?php echo esc_html( $cache_type ); ?>
									</label>
									<?php
									Utils::get_caching_frequencies_dropdown(
										array(
											'name'      => "set-expiry-{$label}",
											'class'     => 'sui-select wphb-expiry-select',
											'selected'  => $expires[ $cache_type ],
											'data-type' => $label,
										)
									);
									?>
								</div>
							<?php endforeach; ?>
						</div>

						<div class="wphb-expiry-changes sui-notice sui-notice-warning sui-margin-top" style="display: none" id="wphb-expiry-change-notice">
							<div class="sui-notice-content">
								<div class="sui-notice-message">
									<span class="sui-notice-icon sui-icon-info sui-md" aria-hidden="true"></span>
									<p>
										<?php if ( $htaccess_writable && $already_enabled ) : ?>
											<?php esc_html_e( 'You’ve made changes to your browser cache settings. You need to update your .htaccess or nginx.conf file with the newly generated code below.', 'wphb' ); ?>
											<br />
											<a class="sui-button update-htaccess" id="view-snippet-code" >
												<?php esc_attr_e( 'View code', 'wphb' ); ?>
											</a>
										<?php elseif ( $htaccess_writable && $htaccess_written ) : ?>
											<?php esc_html_e( 'You’ve made changes to your browser cache settings. You need to update your .htaccess for the new settings to take effect.', 'wphb' ); ?>
											<br />
											<input type="submit" class="sui-button update-htaccess" name="submit" value="<?php esc_attr_e( 'Update .htaccess', 'wphb' ); ?>"/>
											<span class="spinner standalone"></span>
										<?php else : ?>
											<?php esc_html_e( 'Code snippet updated.', 'wphb' ); ?>
										<?php endif; ?>
									</p>
								</div>
							</div>
						</div>
					</div>
				<?php elseif ( $cf_active || $cf_server ) : ?>
					<div class="sui-border-frame">
						<div class="sui-form-field sui-input-md">
							<label class="sui-label">
								<?php esc_html_e( 'JavaScript, CSS, Media, Images', 'wphb' ); ?>
							</label>
							<?php
							Utils::get_caching_frequencies_dropdown(
								array(
									'name'      => 'set-expiry-all',
									'class'     => 'sui-select wphb-expiry-select',
									'selected'  => $cf_current,
									'data-type' => 'all',
								),
								true
							);
							?>
						</div>
						<div id="wphb-expiry-change-notice" style="display: none">
							<?php
							if ( ! $cf_active && $cf_server ) {
								$this->admin_notices->show_inline(
									esc_html__( 'Note: You need to connect your CloudFlare account below for your selected expiry time to take effect.', 'wphb' ),
									'grey'
								);
							} elseif ( $cf_active ) {
								$this->admin_notices->show_inline(
									esc_html__( 'You’ve made changes to your browser cache settings. You need to save changes for the new settings to take effect.', 'wphb' ),
									'warning wphb-expiry-changes',
									'<button type="submit" class="sui-button update-htaccess" style="margin-top: 0" id="set-cf-expiry-button">' . esc_html__( 'Save Changes', 'wphb' ) . '</button><span class="spinner standalone"></span>'
								);
							}
							?>
						</div>
					</div>
				<?php endif; ?>
			</div><!-- end sui-side-tabs -->
		</form>
	</div><!-- end sui-box-settings-col-2 -->
</div><!-- end row -->

<div class="sui-box-settings-row">
	<div class="sui-box-settings-col-1">
		<span class="sui-settings-label"><?php esc_html_e( 'Setup', 'wphb' ); ?></span>
		<span class="sui-description">
			<?php esc_html_e( 'Follow the instructions provided to enable browser caching.', 'wphb' ); ?>
		</span>
	</div><!-- end sui-box-settings-col-1 -->

	<div class="sui-box-settings-col-2">

		<div class="spinner standalone hide visible"></div>

		<div id="wphb-server-instructions-apache" class="wphb-server-instructions sui-hidden" data-server="apache">
			<div class="sui-tabs">
				<div data-tabs>
					<div id="auto-apache" class="active"><?php esc_html_e( 'Automatic', 'wphb' ); ?></div>
					<div id="manual-apache"><?php esc_html_e( 'Manual', 'wphb' ); ?></div>
				</div>
				<div data-panes>
					<div class="active">
						<p>
							<?php esc_html_e( 'Hummingbird can automatically apply browser caching for Apache/LiteSpeed servers by writing your .htaccess file. Alternately, switch to Manual to apply these rules yourself.', 'wphb' ); ?>
						</p>

						<?php
						if ( $htaccess_writable && $already_enabled ) {
							$this->admin_notices->show_inline( esc_html__( 'Your browser caching is already enabled and working well', 'wphb' ) );
						} elseif ( $htaccess_writable && $htaccess_written ) {
							$this->admin_notices->show_inline(
								esc_html__( 'Automatic browser caching is active.', 'wphb' ),
								'info'
							);
						}
						?>

						<?php if ( ! $cf_active && $htaccess_writable ) : ?>
							<div id="enable-cache-wrap" class="enable-cache-wrap-apache <?php echo 'apache' === $server_type ? '' : 'sui-hidden'; ?>">
								<?php if ( $htaccess_written ) : ?>
									<a href="<?php echo esc_url( $disable_link ); ?>" class="sui-button sui-button-ghost">
										<?php esc_html_e( 'Deactivate', 'wphb' ); ?>
									</a>
								<?php elseif ( ! $already_enabled ) : ?>
									<a href="<?php echo esc_url( $enable_link ); ?>" class="sui-button sui-button-blue activate-button">
										<span class="sui-loading-text"><?php esc_html_e( 'Activate', 'wphb' ); ?></span>
										<span class="sui-icon-loader sui-loading" aria-hidden="true"></span>
									</a>
								<?php endif; ?>
							</div>
						<?php endif; ?>
					</div>
					<div>
						<div class="apache-instructions">
							<p>
								<?php esc_html_e( 'Follow the steps below to add browser caching to your Apache/LiteSpeed server.', 'wphb' ); ?>
							</p>

							<ol class="wphb-listing wphb-listing-ordered">
								<li><?php esc_html_e( 'Copy the generated code into your .htaccess file & save your changes.', 'wphb' ); ?></li>
								<li><?php esc_html_e( 'Restart Apache/LiteSpeed.', 'wphb' ); ?></li>
								<li><a href="<?php echo esc_url( $recheck_expiry_url ); ?>"><?php esc_html_e( 'Re-check expiry status.', 'wphb' ); ?></a></li>
							</ol>

							<pre class="sui-code-snippet" id="wphb-apache"><?php echo htmlentities2( $snippets['apache'] ); ?></pre>

							<p><strong>Troubleshooting</strong></p>
							<p><?php esc_html_e( 'If adding the rules to your .htaccess doesn’t work and you have access to vhosts.conf or httpd.conf try to find the line that starts with <Directory> - add the code above into that section and save the file.', 'wphb' ); ?></p>
							<p><?php esc_html_e( "If you don't know where those files are, or you aren't able to reload Apache/LiteSpeed, you would need to consult with your hosting provider or a system administrator who has access to change the configuration of your server", 'wphb' ); ?></p>
							<p><?php Utils::still_having_trouble_link(); ?></p>
						</div>
					</div>
				</div>
			</div>
		</div><!-- end wphb-server-instructions -->

		<div id="wphb-server-instructions-nginx" class="wphb-server-instructions sui-hidden" data-server="nginx">
			<?php
			if ( $already_enabled ) {
				$this->admin_notices->show_inline( esc_html__( 'Your browser caching is already enabled and working well', 'wphb' ) );
			} elseif ( $htaccess_writable && $htaccess_written ) {
				$this->admin_notices->show_inline(
					esc_html__( 'Automatic browser caching is active.', 'wphb' ),
					'info'
				);
			}
			?>

			<div>
				<p><?php esc_html_e( 'Follow the steps below to add browser caching to your NGINX server.', 'wphb' ); ?></p>

				<ol class="wphb-listing wphb-listing-ordered">
					<li><?php esc_html_e( "Edit your nginx.conf. Usually it's located at /etc/nginx/nginx.conf or /usr/local/nginx/nginx.conf", 'wphb' ); ?></li>
					<li><?php esc_html_e( 'Copy the generated code found below and paste it inside your server block.', 'wphb' ); ?></li>
					<li><?php esc_html_e( 'Reload/restart NGINX.', 'wphb' ); ?></li>
					<li><a href="<?php echo esc_url( $recheck_expiry_url ); ?>"><?php esc_html_e( 'Re-check expiry status.', 'wphb' ); ?></a></li>
				</ol>
				<pre class="sui-code-snippet" id="wphb-nginx"><?php echo htmlentities2( $snippets['nginx'] ); ?></pre>
				<p><?php esc_html_e( 'Note: If you do not have access to your NGINX config files you will need to contact your hosting provider to make these changes.', 'wphb' ); ?></p>
				<p><?php Utils::still_having_trouble_link(); ?></p>
			</div>
		</div>

		<div id="wphb-server-instructions-iis" class="wphb-server-instructions sui-hidden" data-server="iis">
			<?php if ( $already_enabled ) : ?>
				<?php $this->admin_notices->show_inline( esc_html__( 'Your browser caching is already enabled and working well', 'wphb' ) ); ?>
			<?php elseif ( $htaccess_writable && $htaccess_written ) : ?>
				<?php
				$this->admin_notices->show_inline(
					esc_html__( 'Automatic browser caching is active.', 'wphb' ),
					'info'
				);
				?>
			<?php else : ?>
				<p>
					<?php
					printf( /* translators: %s: Link to TechNet */
						__( 'For IIS 7 servers and above, <a href="%s" target="_blank">visit Microsoft TechNet</a>', 'wphb' ),
						'https://technet.microsoft.com/en-us/library/cc732475(v=ws.10).aspx'
					);
					?>
				</p>
			<?php endif; ?>
		</div>

		<div id="wphb-server-instructions-cloudflare" class="wphb-server-instructions sui-hidden" data-server="cloudflare">
			<span class="sui-description">
				<?php esc_html_e( 'Hummingbird can control your CloudFlare Browser Cache settings from here. Simply add your CloudFlare API details and configure away.', 'wphb' ); ?>
			</span>
			<?php
			$cf_module    = Utils::get_module( 'cloudflare' );
			$current_step = 'credentials';
			$zones        = array();
			if ( $cf_module->is_zone_selected() && $cf_module->is_connected() ) {
				$current_step = 'final';
			} elseif ( ! $cf_module->is_zone_selected() && $cf_module->is_connected() ) {
				$current_step = 'zone';
				$zones        = $cf_module->get_zones_list();
				if ( is_wp_error( $zones ) ) {
					$zones = array();
				}
			}

			$cf_settings            = $cf_module->get_options();
			$cloudflare_js_settings = array(
				'currentStep' => $current_step,
				'email'       => $cf_settings['email'],
				'apiKey'      => $cf_settings['api_key'],
				'zone'        => $cf_settings['zone'],
				'zoneName'    => $cf_settings['zone_name'],
				'plan'        => $cf_module->get_plan(),
				'zones'       => $zones,
			);

			$cloudflare_js_settings = wp_json_encode( $cloudflare_js_settings );
			?>

			<script type="text/template" id="cloudflare-step-credentials">
				<div class="cloudflare-step">
					<form class="sui-border-frame" action="" method="post" id="cloudflare-credentials">
						<div class="sui-form-field">
							<label for="cloudflare-email" class="sui-label"><?php esc_html_e( 'Cloudflare account email', 'wphb' ); ?></label>
							<input type="text" class="sui-form-control" autocomplete="off" value="{{ data.email }}" name="cloudflare-email" id="cloudflare-email" placeholder="<?php esc_attr_e( 'Enter email address', 'wphb' ); ?>">
						</div>

						<div class="sui-form-field">
							<label for="cloudflare-api-key" class="sui-label"><?php esc_html_e( 'Cloudflare Global API Key', 'wphb' ); ?></label>
							<input type="text" class="sui-form-control" autocomplete="off" value="{{ data.apiKey }}" name="cloudflare-api-key" id="cloudflare-api-key" placeholder="<?php esc_attr_e( 'Enter your 37 digit API key', 'wphb' ); ?>">
						</div>

						<div class="cloudflare-submit sui-margin-top sui-no-padding-bottom">
							<a href="#cloudflare-how-to" class="cloudflare-how-to-title"><?php esc_html_e( 'Need help getting your API Key?', 'wphb' ); ?></a>
							<input type="submit" class="sui-button sui-button-blue" value="<?php echo esc_attr( _x( 'Connect', 'Connect to Cloudflare button text', 'wphb' ) ); ?>">
						</div>

						<ol id="cloudflare-how-to" class="wphb-block-content-blue">
							<li><?php printf( __( '<a target="_blank" href="%s">Log in</a> to your Cloudflare account.', 'wphb' ), 'https://dash.cloudflare.com/login' ); ?></li>
							<li><?php esc_html_e( 'Go to My Profile.', 'wphb' ); ?></li>
							<li><?php esc_html_e( 'Switch to API Tokens tab.', 'wphb' ); ?></li>
							<li><?php esc_html_e( "Click 'View' button and copy the Global API Key identifier.", 'wphb' ); ?></li>
						</ol>
					</form>
				</div>
			</script>

			<script type="text/template" id="cloudflare-step-zone">
				<div class="cloudflare-step">
					<form action="" method="post" id="cloudflare-zone-form">
						<# if ( ! data.zones.length ) { #>
							<p><?php esc_html_e( 'It appears you have no active zones available. Double check your domain has been added to Cloudflare and try again.', 'wphb' ); ?></p>
							<p class="cloudflare-submit">
								<a href="<?php echo esc_url( Utils::get_admin_menu_url( 'caching' ) ); ?>&view=caching&reload=<?php echo time(); ?>#connect-cloudflare" class="sui-button sui-button-blue">
									<?php esc_html_e( 'Re-Check', 'wphb' ); ?>
								</a>
							</p>
						<# } else { #>
							<# var zone = false; #>
							<# var current_host = location.host; #>
							<# for( var i = 0, len = data.zones.length; i < len; i++ ) { #>
								<# if( current_host.indexOf(data.zones[i].label) !== -1 ) { #>
									<# zone = true; #>
									<# break; #>
								<# } #>
							<# } #>
							<# if ( zone ) { #>
								<div class="sui-border-frame" style="margin-top: 30px">
									<label for="cloudflare-zone" class="sui-label">
										<?php esc_html_e( 'Select the zone that matches your domain name', 'wphb' ); ?>
									</label>
									<select class="sui-select" name="cloudflare-zone" id="cloudflare-zone" data-width="250">
										<option value=""><?php esc_html_e( 'Select zone', 'wphb' ); ?></option>
										<# for ( i in data.zones ) { #>
											<option value="{{ data.zones[i].value }}">{{{ data.zones[i].label }}}</option>
										<# } #>
									</select>
									<div class="cloudflare-submit">
										<input type="submit" class="sui-button sui-button-blue" value="<?php esc_attr_e( 'Enable Cloudflare', 'wphb' ); ?>">
									</div>
								</div>
							<# } else { #>
								<?php
								$this->admin_notices->show_inline(
									esc_html__( 'CloudFlare is connected, but it appears you don’t have any active zones for this domain. Double check your domain has been added to Cloudflare and tap re-check when ready.', 'wphb' ),
									'warning sui-margin-top',
									sprintf( /* translators: %1$s - opening a tag, %2$s - </a> */
										esc_html__( '%1$sRe-check%2$s', 'wphb' ),
										'<button class="sui-button sui-button-icon-left" id="cf-recheck-zones"><span class="sui-loading-text"><span class="sui-icon-update" aria-hidden="true"></span>',
										'</span><span class="sui-icon-loader sui-loading" aria-hidden="true"></span></button>'
									)
								);
								?>
							<# } #>
						<# } #>
					</form>

					<a href="<?php echo esc_url( $cf_disable_url ); ?>" class="sui-button sui-button-ghost sui-margin-top">
						<span class="sui-icon-power-on-off" aria-hidden="true"></span>
						<?php esc_attr_e( 'Deactivate', 'wphb' ); ?>
					</a>
				</div>
			</script>

			<script type="text/template" id="cloudflare-step-final">
				<div class="cloudflare-step sui-margin-top">
					<?php
					$this->admin_notices->show_inline(
						esc_html__( 'Cloudflare is connected for this domain. Adjust your expiry settings and save your settings to update your Cloudflare cache settings.', 'wphb' ),
						'info'
					);
					?>
					<div class="buttons buttons-on-left">
						<a href="<?php echo esc_url( $cf_disable_url ); ?>" class="cloudflare-deactivate sui-button sui-button-ghost sui-button-icon-left">
							<span class="sui-icon-power-on-off" aria-hidden="true"></span>
							<?php esc_attr_e( 'Deactivate', 'wphb' ); ?>
						</a>
						<span class="alignright sui-tooltip sui-tooltip-top-right" data-tooltip="<?php esc_attr_e( 'Clear all assets cached by CloudFlare', 'wphb' ); ?>">
							<input type="submit" class="cloudflare-clear-cache sui-button" value="<?php esc_attr_e( 'Clear Cache', 'wphb' ); ?>">
						</span>
						<span class="spinner cloudflare-spinner"></span>
					</div>
				</div>
			</script>

			<div id="cloudflare-steps"></div>
		</div>

	</div><!-- end sui-box-settings-col-1 -->

	<script>
		jQuery(document).ready( function() {
			window.WPHB_Admin.DashboardCloudFlare.init( <?php echo $cloudflare_js_settings; ?> );
			<?php if ( $cf_active ) : ?>
				if ( window.WPHB_Admin ) {
					window.WPHB_Admin.getModule( 'cloudflare' );
				}
			<?php endif; ?>
		});
	</script>
</div><!-- end row -->


