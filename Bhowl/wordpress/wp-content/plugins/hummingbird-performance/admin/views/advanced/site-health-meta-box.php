<?php
/**
 * Advanced tools: plugin health meta box.
 *
 * @since 2.7.0
 * @package Hummingbird
 *
 * @var string $data_size       Database data size.
 * @var string $index_size      Database index size.
 * @var array  $minify_groups   Array of wphb_minify_group posts.
 * @var int    $orphaned_metas  Orphaned rows in wp_postmeta.
 * @var bool   $preloading      Is preloading active.
 * @var int    $queue_size      Number of items in preloader queue.
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

?>

<div class="sui-box-settings-row">
	<div class="sui-box-settings-col-2">
		<p>
			<?php esc_html_e( 'Plugin Health provides plugin information and advanced database usage information to fix critical plugin issues, right inside the plugin.', 'wphb' ); ?>
		</p>

		<?php
		ob_start();
		esc_html_e( 'This feature is implemented to fix critical issues. Action only if you need to resolve a critical issue related to one of the features below.', 'wphb' );
		echo esc_html( '&nbsp;' );
		\Hummingbird\Core\Utils::still_having_trouble_link();
		$text = ob_get_clean();
		$this->admin_notices->show_inline( $text, 'warning' );
		?>
	</div>
</div>

<div class="sui-box-settings-row sui-flushed">
	<div class="sui-box-settings-col-2">
		<p class="sui-description sui-no-margin-bottom">
			<?php esc_html_e( 'Summary of database information related to Hummingbird.', 'wphb' ); ?>
		</p>

		<div class="wphb-database-stats">
			<div>
				<h4><?php esc_html_e( 'Database Data Size', 'wphb' ); ?></h4>
				<?php echo esc_html( $data_size ); ?>
			</div>
			<div>
				<h4><?php esc_html_e( 'Database Index Size', 'wphb' ); ?></h4>
				<?php echo esc_html( $index_size ); ?>
			</div>
		</div>
	</div>
</div>

<div class="sui-tabs sui-tabs-flushed">
	<div role="tablist" class="sui-tabs-menu">
		<?php if ( ! is_multisite() || is_network_admin() ) : ?>
		<button
			type="button"
			role="tab"
			id="page-cache-tab"
			class="sui-tab-item active"
			aria-controls="page-cache-tab-content"
			aria-selected="true"
		>
			<?php esc_html_e( 'Page Cache Preloader', 'wphb' ); ?>
		</button>
		<?php endif; ?>

		<button
			type="button"
			role="tab"
			id="asset-optimization-tab"
			class="sui-tab-item <?php echo is_multisite() && ! is_network_admin() ? 'active' : ''; ?>"
			aria-controls="asset-optimization-tab-content"
			aria-selected="<?php echo is_multisite() && ! is_network_admin() ? 'true' : 'false'; ?>"
			tabindex="-1"
		>
			<?php esc_html_e( 'Asset Optimization', 'wphb' ); ?>
		</button>

	</div>

	<div class="sui-tabs-content">
		<?php if ( ! is_multisite() || is_network_admin() ) : ?>
		<div
			role="tabpanel"
			tabindex="0"
			id="page-cache-tab-content"
			class="sui-tab-content active"
			aria-labelledby="page-cache-tab"
		>
			<p class="sui-description">
				<?php esc_html_e( 'Below is the list of your database data related to the Page Cache Preloader feature. If you have issues related to the feature, you can force purge the preloader cache and check if the issue is resolved. If the issue persists after purging the cache, you can delete the fields which have a large size.', 'wphb' ); ?>
			</p>

			<div>
				<button role="button" class="sui-button sui-button-blue" <?php disabled( ! $preloading || empty( $queue_size ) ); ?> id="btn-cache-purge">
					<span class="sui-button-text-default">
						<span class="sui-icon-undo" aria-hidden="true"></span>
						<?php esc_html_e( 'Force purge cache', 'wphb' ); ?>
					</span>
					<span class="sui-button-text-onload">
						<span class="sui-icon-loader sui-loading" aria-hidden="true"></span>
						<?php esc_html_e( 'Clearing cache...', 'wphb' ); ?>
					</span>
				</button>
			</div>

			<table class="sui-table sui-table-flushed">
				<thead>
				<tr>
					<th><?php esc_html_e( 'Field Name', 'wphb' ); ?></th>
					<th><?php esc_html_e( 'Rows', 'wphb' ); ?></th>
					<th>&nbsp;</th>
				</tr>
				</thead>

				<?php if ( $preloading && ! empty( $queue_size ) ) : ?>
					<tbody>
					<tr>
						<td class="sui-table-item-title">wphb_cache_preload_batch</td>
						<td id="count-cache"><?php echo absint( $queue_size ); ?></td>
						<td>
							<button
								type="button"
								class="sui-button-icon sui-tooltip"
								id="icon-cache-purge"
								data-tooltip="<?php esc_attr_e( 'Remove', 'wphb' ); ?>"
								<?php disabled( ! $preloading || empty( $queue_size ) ); ?>
							>
								<span class="sui-loading-text" aria-hidden="true">
									<span class="sui-icon-trash sui-sm"></span>
								</span>
								<span class="sui-icon-loader sui-loading" aria-hidden="true"></span>
								<span class="sui-screen-reader-text">
									<?php esc_html_e( 'Force purge cache', 'wphb' ); ?>
								</span>
							</button>
						</td>
					</tr>
					</tbody>
				<?php else : ?>
					<tr><td colspan="3" style="height: 30px; border: 0"> </td></tr>
				<?php endif; ?>
			</table>

			<?php
			if ( ! $preloading || empty( $queue_size ) ) {
				$notice = esc_html__( 'There is no database information. Either you have the Page Cache Preloader feature disabled or you have removed all the data related to it.', 'wphb' );
				$this->admin_notices->show_inline( $notice, 'grey' );
			}
			?>
		</div>
		<?php endif; ?>

		<div
			role="tabpanel"
			tabindex="0"
			id="asset-optimization-tab-content"
			class="sui-tab-content <?php echo is_multisite() && ! is_network_admin() ? 'active' : ''; ?>"
			aria-labelledby="asset-optimization-tab"
			hidden
		>
			<p class="sui-description">
				<?php esc_html_e( 'Below is a list of your database data and Custom Post Type information related to asset optimization.', 'wphb' ); ?>
			</p>

			<table class="sui-table sui-table-flushed">
				<thead>
				<tr>
					<th><?php esc_html_e( 'Field Name', 'wphb' ); ?></th>
					<th><?php esc_html_e( 'Rows', 'wphb' ); ?></th>
					<th>&nbsp;</th>
				</tr>
				</thead>

				<?php if ( $minify_groups || $orphaned_metas ) : ?>
					<tbody>
					<tr>
						<td class="sui-table-item-title">wphb_minify_group</td>
						<td id="count-minify"><?php echo count( $minify_groups ); ?></td>
						<td>
							<button
								type="button"
								class="sui-button-icon sui-tooltip"
								id="icon-minify-purge"
								data-tooltip="<?php esc_attr_e( 'Remove', 'wphb' ); ?>"
								<?php disabled( ! count( $minify_groups ) ); ?>
							>
							<span class="sui-loading-text" aria-hidden="true">
								<span class="sui-icon-trash sui-sm"></span>
							</span>
								<span class="sui-icon-loader sui-loading" aria-hidden="true"></span>
								<span class="sui-screen-reader-text">
								<?php esc_html_e( 'Force purge cache', 'wphb' ); ?>
							</span>
							</button>
						</td>
					</tr>
					<tr>
						<td class="sui-table-item-title">
							<?php esc_html_e( 'Orphaned asset optimization meta data', 'wphb' ); ?>
							<span class="sui-tooltip sui-tooltip-constrained" data-tooltip="<?php esc_attr_e( 'Orphaned asset optimization meta data is the data stored in the wp_postmeta table and it isn’t associated with posts in the wp-posts table.', 'wphb' ); ?>">
								<span class="sui-icon-info sui-md" aria-hidden="true"></span>
							</span>
						</td>
						<td id="count-ao-orphaned"><?php echo absint( $orphaned_metas ); ?></td>
						<td>
							<button
								type="button"
								class="sui-button-icon sui-tooltip"
								id="icon-ao-orphan-purge"
								data-tooltip="<?php esc_attr_e( 'Remove', 'wphb' ); ?>"
								<?php disabled( empty( $orphaned_metas ) ); ?>
							>
							<span class="sui-loading-text" aria-hidden="true">
								<span class="sui-icon-trash sui-sm"></span>
							</span>
								<span class="sui-icon-loader sui-loading" aria-hidden="true"></span>
								<span class="sui-screen-reader-text">
								<?php esc_html_e( 'Force purge cache', 'wphb' ); ?>
							</span>
							</button>
						</td>
					</tr>
					</tbody>
				<?php else : ?>
					<tr><td colspan="3" style="height: 30px; border: 0"> </td></tr>
				<?php endif; ?>
			</table>

			<?php
			if ( ! $minify_groups && empty( $orphaned_metas ) ) {
				$notice = esc_html__( 'There is no database information. Either you have the Asset Optimization feature disabed or you have removed all the data related to it.', 'wphb' );
				$this->admin_notices->show_inline( $notice, 'grey' );
			}
			?>
		</div>

	</div>
</div>
