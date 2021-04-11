<?php
/**
 * Integrations meta box.
 *
 * @since 2.5.0
 * @package Hummingbird
 *
 * @var bool $redis_connected        Redis server status.
 * @var bool $redis_enabled          Redis enabled.
 * @var bool $is_redis_object_cache  Is Redis object cache enabled.
 * @var bool $disable_redis          Whether to disable redis ( In case it's not supported )
 * @var bool $error                  If there was an error connecting to redis
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

$accordion_class  = $disable_redis ? ' sui-accordion-item--disabled' : '';
$accordion_class .= $redis_enabled ? ' sui-accordion-item--open' : '';

$title_class = $disable_redis ? ' justify-content-start' : '';
?>

<p>
	<?php esc_html_e( 'Integrate with powerful third-party providers to gain full control over your caching.', 'wphb' ); ?>
</p>

<div class="sui-accordion sui-accordion-flushed">
	<div class="sui-accordion-header">
		<div class="sui-accordion-col-12"><?php esc_html_e( 'Available Integrations', 'wphb' ); ?></div>
	</div>

	<div class="sui-accordion-item<?php echo esc_attr( $accordion_class ); ?>">
		<div class="sui-accordion-item-header">
			<div class="sui-accordion-item-title<?php echo esc_attr( $title_class ); ?>">
				<img class="sui-image" alt="<?php esc_attr_e( 'Redis', 'wphb' ); ?>"
					src="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/integrations/icon-redis.png' ); ?>"
					srcset="<?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/integrations/icon-redis.png' ); ?> 1x, <?php echo esc_url( WPHB_DIR_URL . 'admin/assets/image/integrations/icon-redis@2x.png' ); ?> 2x">
				<?php esc_html_e( 'Redis', 'wphb' ); ?>
				<?php if ( $disable_redis ) : ?>
					<span class="sui-tooltip sui-tooltip-constrained" data-tooltip="<?php esc_html_e( 'Redis cache is disabled for your website because object caching is already enabled on your server.', 'wphb' ); ?>">
						<span class="sui-icon-info sui-sm" aria-hidden="true"></span>
					</span>
				<?php endif; ?>
			</div>
			<?php
			if ( ! $disable_redis ) :
				?>
				<div class="sui-accordion-col-auto">
				<?php if ( ! $redis_enabled && ! defined( 'WPHB_REDIS_HOST' ) ) : ?>
					<button class="sui-button-icon sui-accordion-item-action sui-tooltip"
						aria-label="<?php esc_attr_e( 'Connect', 'wphb' ); ?>"
						data-tooltip="<?php esc_attr_e( 'Connect', 'wphb' ); ?>"
						data-modal-open="redis-connect"
						data-modal-open-focus="redis-host"
						data-modal-close-focus="wphb-box-integrations"
						data-modal-mask="true"
					><span class="sui-icon-plus" aria-hidden="true"></span></button>
				<?php endif; ?>
				<button class="sui-button-icon sui-accordion-open-indicator" aria-label="<?php esc_attr_e( 'Open item', 'wphb' ); ?>">
					<span class="sui-icon-chevron-down" aria-hidden="true"></span>
				</button>
				</div>
				<?php
			endif;
			?>
		</div>

		<?php
		if ( ! $disable_redis ) :
			?>
			<div class="sui-accordion-item-body">
			<div class="sui-box">
				<div class="sui-box-body">
					<h4><?php esc_html_e( 'Overview', 'wphb' ); ?></h4>
					<p>
						<?php esc_html_e( 'Redis is an open source tool for storing data in memory which is used as a database, cache and message broker. It supports many data structures such as strings, hashes and lists and can be easily enabled.', 'wphb' ); ?>
					</p>

					<h4><?php esc_html_e( 'Status', 'wphb' ); ?></h4>

					<?php if ( $redis_connected ) : ?>
						<?php $this->admin_notices->show_inline( esc_html__( 'Redis is connected.', 'wphb' ) ); ?>

						<h4><?php esc_html_e( 'Object Cache', 'wphb' ); ?></h4>
						<p>
							<?php esc_html_e( 'Object cache keeps the database query results and provides the cached version without requesting it every time. We recommend using object cache if your site gets a lot of traffic and you have a lot of data across your pages.', 'wphb' ); ?>
						</p>

						<div class="sui-form-field">
							<label for="object-cache" class="sui-toggle">
								<input type="checkbox" id="object-cache" name="object-cache"
									aria-labelledby="object-cache-label" <?php checked( $is_redis_object_cache ); ?>>
								<span class="sui-toggle-slider" aria-hidden="true"></span>
								<span id="object-cache-label" class="sui-toggle-label"
									style="font-size: 13px"><?php esc_html_e( 'Enable object caching', 'wphb' ); ?></span>
							</label>
						</div>
					<?php else : ?>
						<?php
						$this->admin_notices->show_inline(
							esc_html__( 'Redis is not connected.', 'wphb' ),
							'grey',
							$error
						);
						?>
					<?php endif; ?>
				</div>
				<div class="sui-box-footer">
					<?php if ( $redis_connected ) : ?>
						<div>
							<button class="sui-button sui-button-ghost" id="disconnect-redis-cache"
									onclick="WPHB_Admin.caching.redisDisable()">
								<?php esc_html_e( 'Disconnect', 'wphb' ); ?>
							</button>

							<button class="sui-button sui-button-ghost"
									aria-label="<?php esc_attr_e( 'Configure', 'wphb' ); ?>"
									data-modal-open="redis-connect"
									data-modal-open-focus="redis-host"
									data-modal-close-focus="wphb-box-integrations"
									data-modal-mask="true"
							>
								<span class="sui-icon-wrench-tool" aria-hidden="true"></span>
								<?php esc_html_e( 'Configure', 'wphb' ); ?>
							</button>
						</div>
					<?php endif; ?>

					<div class="sui-actions-right">
						<?php if ( $redis_connected ) : ?>
							<button class="sui-button" id="clear-redis-cache">
								<!-- Default State Content -->
								<span class="sui-button-text-default" aria-hidden="true">
									<?php esc_html_e( 'Clear Cache', 'wphb' ); ?>
								</span>
								<!-- Loading State Content -->
								<span class="sui-button-text-onload">
									<span class="sui-icon-loader sui-loading" aria-hidden="true"></span>
									<?php esc_html_e( 'Clear Cache', 'wphb' ); ?>
								</span>
							</button>
						<?php else : ?>
							<button class="sui-button"
								aria-label="<?php esc_attr_e( 'Connect', 'wphb' ); ?>"
								data-modal-open="redis-connect"
								data-modal-open-focus="redis-host"
								data-modal-close-focus="wphb-box-integrations"
								data-modal-mask="true"
							><?php esc_html_e( 'Connect', 'wphb' ); ?></button>
						<?php endif; ?>
					</div>
				</div>
			</div>
			</div>
			<?php
		endif;
		?>
	</div>
</div>

<?php $this->modal( 'integration-redis-connect' ); ?>
