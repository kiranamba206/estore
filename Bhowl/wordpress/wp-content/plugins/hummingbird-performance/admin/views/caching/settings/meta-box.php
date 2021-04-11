<?php
/**
 * Settings meta box.
 *
 * @since 1.8.1
 * @package Hummingbird
 *
 * @var bool   $enabled    Page cache enabled.
 * @var bool   $control    Page cache control.
 * @var string $detection  File change detection. Accepts: 'manual', 'auto' and 'none'.
 */

use Hummingbird\Core\Utils;

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

?>

<div class="sui-box-settings-row">
	<div class="sui-box-settings-col-1">
		<span class="sui-settings-label"><?php esc_html_e( 'Admin Cache Control', 'wphb' ); ?></span>
		<span class="sui-description">
			<?php
			if ( is_network_admin() ) {
				esc_html_e( 'This feature adds a Clear Page Cache button to the WordPress Admin Bar for Network and Subsite Admin users. ', 'wphb' );
			} else {
				esc_html_e( 'This feature adds a Clear Page Cache button to the WordPress Admin Top Bar area for admin users.', 'wphb' );
			}
			?>
		</span>
	</div>
	<div class="sui-box-settings-col-2">
		<div class="sui-form-field">
			<label for="cc_button" class="sui-toggle">
				<input type="checkbox" id="cc_button" name="cc_button" aria-labelledby="cc_button-label" <?php checked( $control ); ?><?php disabled( ! $enabled ); ?>>
				<span class="sui-toggle-slider" aria-hidden="true"></span>
				<span id="cc_button-label" class="sui-toggle-label"><?php esc_html_e( 'Show Clear Page Cache button in Admin Bar', 'wphb' ); ?></span>
			</label>
			<?php if ( ! $enabled ) : ?>
			<span class="sui-description sui-toggle-description">
				<?php
				$this->admin_notices->show_inline(
					sprintf(
						/* translators: %1$s - opening a tag, %2$s - closing a tag */
						esc_html__( 'Activate %1$sPage Caching%2$s to use this feature.', 'wphb' ),
						'<a href="' . esc_url( Utils::get_admin_menu_url( 'caching' ) ) . '">',
						'</a>'
					),
					'grey'
				);
				?>
			</span>
			<?php endif; ?>
		</div>
	</div>
</div>

<div class="sui-box-settings-row">
	<div class="sui-box-settings-col-1">
		<span class="sui-settings-label"><?php esc_html_e( 'File Change Detection', 'wphb' ); ?></span>
		<span class="sui-description">
			<?php esc_html_e( 'Choose how you want Hummingbird to react when we detect changes to your file structure.', 'wphb' ); ?>
		</span>
	</div>
	<div class="sui-box-settings-col-2">
		<div class="sui-form-field" role="radiogroup">
			<label for="automatic" class="sui-radio">
				<input type="radio" name="detection" id="automatic" value="auto" aria-labelledby="automatic-label" <?php checked( $detection, 'auto' ); ?>>
				<span aria-hidden="true"></span>
				<span id="automatic-label"><?php esc_html_e( 'Automatic', 'wphb' ); ?></span>
			</label>
			<span class="sui-description sui-radio-description">
				<?php esc_html_e( 'Set Hummingbird to automatically clear your cache instead of prompting you to do it manually.', 'wphb' ); ?>
			</span>

			<label for="manual" class="sui-radio">
				<input type="radio" name="detection" id="manual" value="manual" aria-labelledby="manual-label" <?php checked( $detection, 'manual' ); ?>>
				<span aria-hidden="true"></span>
				<span id="manual-label"><?php esc_html_e( 'Manual Notice', 'wphb' ); ?></span>
			</label>
			<span class="sui-description sui-radio-description">
				<?php esc_html_e( 'Get a global notice inside your WordPress Admin area anytime your cache needs clearing.', 'wphb' ); ?>
			</span>

			<label for="none" class="sui-radio">
				<input type="radio" name="detection" id="none" value="none" aria-labelledby="automatic-label" <?php checked( $detection, 'none' ); ?>>
				<span aria-hidden="true"></span>
				<span id="none-label"><?php esc_html_e( 'None', 'wphb' ); ?></span>
			</label>
			<span class="sui-description sui-radio-description">
				<?php esc_html_e( 'Disable warnings in your WP Admin area.', 'wphb' ); ?>
			</span>
		</div>
	</div>
</div>
