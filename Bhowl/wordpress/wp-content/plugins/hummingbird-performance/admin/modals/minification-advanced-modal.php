<?php
/**
 * Asset optimization: switch to advanced mode modal.
 *
 * @package Hummingbird
 *
 * @since 2.6.0
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

?>

<div class="sui-modal sui-modal-sm">
	<div role="dialog" class="sui-modal-content" id="wphb-advanced-minification-modal" aria-modal="true" aria-labelledby="switchAdvanced" aria-describedby="dialogDescription">
		<div id="slide-one" class="sui-box sui-modal-slide sui-loaded sui-active" data-modal-size="sm">
			<div class="sui-box-header sui-flatten sui-content-center sui-spacing-top--60">
				<button class="sui-button-icon sui-button-float--right" id="dialog-close-div" data-modal-close="">
					<span class="sui-icon-close sui-md" aria-hidden="true"></span>
					<span class="sui-screen-reader-text"><?php esc_attr_e( 'Close this dialog window', 'wphb' ); ?></span>
				</button>

				<h3 class="sui-box-title sui-lg" id="switchAdvanced">
					<?php esc_html_e( 'Apply latest configurations?', 'wphb' ); ?>
				</h3>

				<p class="sui-description" id="dialogDescription">
					<?php esc_html_e( 'Do you want to load the configurations made on automatic mode or you want to reset the settings and start configuring the mode from scratch?', 'wphb' ); ?>
				</p>
			</div>

			<div class="sui-box-body sui-content-center">
				<?php if ( get_option( 'wphb-minification-show-advanced_modal' ) ) : ?>
					<button onclick="WPHB_Admin.minification.switchView( 'advanced', false, false )" class="close sui-button sui-no-margin-right" id="wphb-switch-to-advanced" data-modal-slide="slide-two" data-modal-slide-focus="slide-next" data-modal-slide-intro="next">
						<?php esc_html_e( 'Apply Configurations', 'wphb' ); ?>
					</button>

					<a href="#" onclick="WPHB_Admin.minification.switchView( 'advanced', true, false )" data-modal-slide="slide-two" data-modal-slide-focus="slide-next" data-modal-slide-intro="next">
						<?php esc_html_e( 'Reset Settings', 'wphb' ); ?>
					</a>
				<?php else : ?>
					<button onclick="WPHB_Admin.minification.switchView( 'advanced', false, true )" class="close sui-button sui-no-margin-right" id="wphb-switch-to-advanced">
						<?php esc_html_e( 'Apply Configurations', 'wphb' ); ?>
					</button>

					<a href="#" onclick="WPHB_Admin.minification.switchView( 'advanced', true, true )">
						<?php esc_html_e( 'Reset Settings', 'wphb' ); ?>
					</a>
				<?php endif; ?>
			</div>

			<?php if ( ! apply_filters( 'wpmudev_branding_hide_branding', false ) ) : ?>
				<img class="sui-image" alt="" src="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/graphic-minify-modal-warning@1x.png' ); ?>"
					srcset="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/graphic-minify-modal-warning@1x.png' ); ?> 1x, <?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/graphic-minify-modal-warning@2x.png' ); ?> 2x">
			<?php endif; ?>
		</div>

		<div id="slide-two" class="sui-box sui-modal-slide" data-modal-size="sm">
			<div class="sui-box-header sui-flatten sui-content-center sui-spacing-top--60">
				<h3 class="sui-box-title sui-lg" id="switchAdvanced">
					<?php esc_html_e( 'Just be Careful!', 'wphb' ); ?>
				</h3>

				<p class="sui-description" id="dialogDescription">
					<?php esc_html_e( 'Manual mode gives you full control over your files but can easily break your website if configured incorrectly.', 'wphb' ); ?>
				</p>

				<p class="sui-description" style="font-weight: 500">
					<?php esc_html_e( 'We recommend you make one tweak at a time and check the frontend of your website each change to avoid any mishaps.', 'wphb' ); ?>
				</p>
			</div>

			<div class="sui-box-body sui-content-center">
				<div class="sui-form-field">
					<label for="hide-advanced-modal" class="sui-checkbox sui-checkbox-sm">
						<input type="checkbox" id="hide-advanced-modal" aria-labelledby="hide-advanced-label"/>
						<span aria-hidden="true"></span>
						<span id="hide-advanced-label" class="sui-toggle-label">
							<?php esc_html_e( "Don't show me this again", 'wphb' ); ?>
						</span>
					</label>
				</div>
			</div>

			<div class="sui-box-footer sui-flatten sui-content-center">
				<button class="sui-button" onclick="WPHB_Admin.minification.switchView( 'advanced' )" id="wphb-switch-to-advanced">
					<?php esc_html_e( 'Got it', 'wphb' ); ?>
				</button>
			</div>

			<?php if ( ! apply_filters( 'wpmudev_branding_hide_branding', false ) ) : ?>
				<img class="sui-image" alt="" src="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/graphic-minify-modal-warning@1x.png' ); ?>"
					srcset="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/graphic-minify-modal-warning@1x.png' ); ?> 1x, <?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/graphic-minify-modal-warning@2x.png' ); ?> 2x">
			<?php endif; ?>
		</div>
	</div>
</div>
