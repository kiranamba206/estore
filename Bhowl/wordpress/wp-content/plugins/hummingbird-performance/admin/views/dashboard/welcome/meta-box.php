<?php
/**
 * Welcome meta box on dashboard page.
 *
 * @package Hummingbird
 *
 * @var int    $caching_issues     Number of issues.
 * @var bool   $cf_active          CloudFlare status.
 * @var int    $cf_current         CloudFlare expiry settings.
 * @var int    $gzip_issues        Number of gzip issues.
 * @var bool   $is_doing_report    If is doing performance report.
 * @var object $last_report        Last report object.
 * @var string $report_type        Performance report type: desktop or mobile.
 * @var bool   $uptime_active      Uptime status.
 * @var object $uptime_report      Uptime report object.
 * @var bool   $report_dismissed   Last report dismissed warning.
 */

use Hummingbird\Core\Modules\Performance;
use Hummingbird\Core\Utils;

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

$branded_image = apply_filters( 'wpmudev_branding_hero_image', '' );
?>

<?php if ( $branded_image ) : ?>
	<div class="sui-summary-image-space" aria-hidden="true" style="background-image: url('<?php echo esc_url( $branded_image ); ?>')"></div>
<?php else : ?>
	<div class="sui-summary-image-space" aria-hidden="true"></div>
<?php endif; ?>
<div class="sui-summary-segment">
	<div class="sui-summary-details">
		<?php if ( $last_report && ! is_wp_error( $last_report ) && ! $report_dismissed ) : ?>
			<span class="sui-summary-large"><?php echo esc_html( $last_report->{$report_type}->score ); ?></span>
			<span aria-hidden="true" class="sui-icon-<?php echo esc_attr( Performance::get_impact_class( $last_report->{$report_type}->score, 'icon' ) ); ?> sui-md sui-<?php echo esc_attr( Performance::get_impact_class( $last_report->{$report_type}->score ) ); ?>"></span>
			<span class='sui-summary-percent'>/100</span>
		<?php elseif ( $is_doing_report ) : ?>
			<div class="sui-progress-text sui-icon-loader sui-loading"></div>
		<?php elseif ( $report_dismissed && isset( $last_report->{$report_type}->score ) ) : ?>
			<span class="sui-summary-large"><?php echo esc_html( $last_report->{$report_type}->score ); ?></span>
			<span aria-hidden="true" class="sui-icon-info sui-md"></span>
			<span class='sui-summary-percent'>/100</span>
		<?php else : ?>
			&mdash;
		<?php endif; ?>
		<span class="sui-summary-sub"><?php esc_html_e( 'Performance score', 'wphb' ); ?></span>

		<span class="sui-summary-detail">
			<?php
			if ( $last_report && ! is_wp_error( $last_report ) ) {
				$data_time    = strtotime( get_date_from_gmt( date( 'Y-m-d H:i:s', $last_report->time ) ) );
				$time_string  = esc_html( date_i18n( get_option( 'date_format' ), $data_time ) );
				$time_string .= sprintf(
					/* translators: %s - time in proper format */
					esc_html_x( ' at %s', 'Time of the last performance report', 'wphb' ),
					esc_html( date_i18n( get_option( 'time_format' ), $data_time ) )
				);
				echo esc_html( $time_string );
			} elseif ( $is_doing_report ) {
				$time_string = esc_html__( 'Running scan...', 'wphb' );
			} else {
				$time_string = esc_html__( 'Never', 'wphb' );
			}
			?>
		</span>
		<span class="sui-summary-sub"><?php esc_html_e( 'Last test date', 'wphb' ); ?></span>
	</div>
</div>
<div class="sui-summary-segment">
	<ul class="sui-list">
		<li>
			<span class="sui-list-label"><?php esc_html_e( 'Browser Caching', 'wphb' ); ?></span>
			<span class="sui-list-detail">
				<?php if ( ( $cf_active && YEAR_IN_SECONDS <= $cf_current ) || ( ! $cf_active && ! $caching_issues ) ) : ?>
					<span class="sui-icon-check-tick sui-lg sui-success" aria-hidden="true"></span>
				<?php elseif ( $cf_active && YEAR_IN_SECONDS > $cf_current ) : ?>
					<span class="sui-tag sui-tag-warning">5</span>
				<?php else : ?>
					<span class="sui-tag sui-tag-warning"><?php echo (int) $caching_issues; ?></span>
				<?php endif; ?>
			</span>
		</li>
		<li>
			<span class="sui-list-label"><?php esc_html_e( 'GZIP Compression', 'wphb' ); ?></span>
			<span class="sui-list-detail">
				<?php if ( $gzip_issues ) : ?>
					<span class="sui-tag sui-tag-warning"><?php echo (int) $gzip_issues; ?></span>
				<?php else : ?>
					<span class="sui-icon-check-tick sui-lg sui-success" aria-hidden="true"></span>
				<?php endif; ?>
			</span>
		</li>
		<li>
			<span class="sui-list-label"><?php esc_html_e( 'Last Down', 'wphb' ); ?></span>
			<span class="sui-list-detail">
				<?php if ( ! Utils::is_member() ) : ?>
					<a class="sui-button sui-button-purple" href="<?php echo esc_url( Utils::get_link( 'plugin', 'hummingbird_dash_summary_pro_tag' ) ); ?>" target="_blank">
						<?php esc_html_e( 'PRO', 'wphb' ); ?>
					</a>
				<?php elseif ( is_wp_error( $uptime_report ) || ( ! $uptime_active ) ) : ?>
					<span class="sui-tag sui-tag-disabled">
						<?php esc_html_e( 'Uptime Inactive', 'wphb' ); ?>
					</span>
				<?php elseif ( empty( $site_date ) ) : ?>
					<?php esc_html_e( 'Website is reported down', 'wphb' ); ?>
				<?php else : ?>
					<?php echo esc_html( $site_date ); ?>
				<?php endif; ?>
			</span>
		</li>
	</ul>
</div>
