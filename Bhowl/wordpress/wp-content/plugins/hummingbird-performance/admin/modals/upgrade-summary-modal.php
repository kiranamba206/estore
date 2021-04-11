<?php
/**
 * Upgrade highlight modal.
 *
 * @since 2.6.0
 * @package Hummingbird
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

?>

<div class="sui-modal sui-modal-md">
	<div
			role="dialog"
			id="upgrade-summary-modal"
			class="sui-modal-content"
			aria-modal="true"
			aria-labelledby="upgrade-summary-modal-title"
			aria-describedby="upgrade-summary-modal-desc"
	>
		<div class="sui-box">
			<div class="sui-box-header sui-flatten sui-content-center sui-spacing-top--60">
				<?php if ( ! apply_filters( 'wpmudev_branding_hide_branding', false ) ) : ?>
					<figure class="sui-box-banner" aria-hidden="true">
						<img src="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/upgrade-summary-bg.png' ); ?>" alt=""
							srcset="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/upgrade-summary-bg.png' ); ?> 1x, <?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/upgrade-summary-bg@2x.png' ); ?> 2x">
					</figure>
				<?php endif; ?>

				<button class="sui-button-icon sui-button-float--right" data-modal-close=""
						onclick="window.WPHB_Admin.dashboard.hideUpgradeSummary()">
					<span class="sui-icon-close sui-md" aria-hidden="true"></span>
					<span class="sui-screen-reader-text"><?php esc_attr_e( 'Close this modal', 'wphb' ); ?></span>
				</button>

				<h3 id="upgrade-summary-modal-title" class="sui-box-title sui-lg" style="white-space: inherit">
					<?php esc_html_e( 'New Automated Asset Optimization and Import / Export feature', 'wphb' ); ?>
				</h3>

				<p id="upgrade-summary-modal-desc" class="sui-description">
					<?php
					// translators: Plugin Version.
					printf( esc_html__( 'New automated asset optimization and import/export features were added with %1$s release.', 'wphb' ), esc_attr( WPHB_VERSION ) );
					?>
				</p>
			</div>

			<div class="sui-box-body sui-spacing-top--20 sui-spacing-bottom--20">
				<div class="wphb-upgrade-feature">
					<h6 class="wphb-upgrade-item"><?php esc_html_e( 'Basic and Speedy optimization', 'wphb' ); ?></h6>
					<p class="wphb-upgrade-item-desc">
						<?php esc_html_e( 'With new optimization you will be able to automatically optimize your CSS and JavaScript files by just enabling the Basic or Speedy optimization option. Check the “How does it work” section, to know what each automated feature does behind the scenes.', 'wphb' ); ?>
					</p>
				</div>
				<div class="wphb-upgrade-feature">
					<h6 class="wphb-upgrade-item"><?php esc_html_e( 'Import / Export for Asset Optimization', 'wphb' ); ?></h6>
					<p class="wphb-upgrade-item-desc">
						<?php
						$url = add_query_arg( 'view', 'import_export', \Hummingbird\Core\Utils::get_admin_menu_url( 'settings' ) );
						printf(
							// translators: Import/Export page url.
							esc_html__( 'The import/Export feature allows you to export manual configurations for asset optimization. Simply export the Json file with the configuration from the %1$s and import the file to another site with Hummingbird installed and activated.', 'wphb' ),
							sprintf( '<a href="%1$s" onclick="window.WPHB_Admin.dashboard.hideUpgradeSummary()">%2$s</a>', esc_url( $url ), esc_html__( 'Import/Export page', 'wphb' ) )
						);
						?>
					</p>
				</div>
			</div>

			<div class="sui-box-footer sui-flatten sui-content-center">
				<button role="button" class="sui-button" onclick="window.WPHB_Admin.dashboard.hideUpgradeSummary()">
					<?php esc_html_e( 'Got it', 'wphb' ); ?>
				</button>
			</div>
		</div>
	</div>
</div>
