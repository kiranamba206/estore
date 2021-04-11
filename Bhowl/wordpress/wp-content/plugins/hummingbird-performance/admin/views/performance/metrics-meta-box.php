<?php
/**
 * Performance summary meta box.
 *
 * @package Hummingbird
 *
 * @var bool     $report_dismissed  If performance report is dismissed.
 * @var bool     $can_run_test      If there is no cool down period and user can run a new test.
 * @var string   $retry_url         URL to trigger a new performance scan.
 * @var stdClass $last_test         Last test details.
 * @var string   $type              Report type: desktop or mobile.
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

?>

<div class="sui-box-body">
	<?php if ( $report_dismissed ) : ?>
		<?php
		if ( true === $can_run_test ) {
			$buttons = sprintf( /* translators: %1$s - opening a tag, %2$s - </a> */
				esc_html__( '%1$sRun Test%2$s', 'wphb' ),
				'<a href="' . esc_url( $retry_url ) . '" class="sui-button sui-button-blue">',
				'</a>'
			);
		} else {
			$tooltip = sprintf( /* translators: %d: number of minutes. */
				_n(
					'Hummingbird is just catching her breath - you can run another test in %d minute',
					'Hummingbird is just catching her breath - you can run another test in %d minutes',
					$can_run_test,
					'wphb'
				),
				number_format_i18n( $can_run_test )
			);
			$buttons = sprintf( /* translators: %1$s - opening a tag, %2$s - </a> */
				esc_html__( '%1$sRun Test%2$s', 'wphb' ),
				'<span class="sui-tooltip sui-tooltip-constrained sui-tooltip-bottom-right" data-tooltip="' . esc_attr( $tooltip ) . '" aria-hidden="true">' .
					'<a href="#" disabled class="sui-button sui-button-blue">',
				'</a></span>'
			);
		}

		$this->admin_notices->show_inline(
			esc_html__( 'You have chosen to ignore this performance test. Run a new test to see new recommendations.', 'wphb' ),
			'grey',
			$buttons
		);

		$impact_score_class = 'dismissed';
		$impact_icon_class  = 'warning-alert';
		?>
	<?php else : ?>
		<p><?php esc_html_e( 'Your performance score is calculated based on how your site performs on each of the following metrics. You can expand the metrics for recommendations on improving them.', 'wphb' ); ?></p>
	<?php endif; ?>
</div>

<div class="sui-accordion sui-accordion-flushed">
	<?php foreach ( $last_test->metrics as $rule => $rule_result ) : ?>
		<?php
		$score = isset( $rule_result->score ) ? $rule_result->score : 0;

		if ( ! $report_dismissed ) {
			$impact_score_class = \Hummingbird\Core\Modules\Performance::get_impact_class( absint( $score * 100 ) );
			$impact_icon_class  = \Hummingbird\Core\Modules\Performance::get_impact_class( absint(  $score * 100 ), 'icon' );
		}
		?>
		<div class="sui-accordion-item sui-<?php echo esc_attr( $impact_score_class ); ?>" id="<?php echo esc_attr( $rule ); ?>">
			<div class="sui-accordion-item-header">
				<div class="sui-accordion-item-title">
					<span aria-hidden="true" class="sui-icon-<?php echo esc_attr( $impact_icon_class ); ?> sui-<?php echo esc_attr( $impact_score_class ); ?>"></span> <?php echo esc_html( $rule_result->title ); ?>
				</div>
				<div>
					<?php $gray_class = isset( $score ) && 0 === $score ? 'wphb-gray-color' : ''; ?>
					<div class="sui-circle-score sui-grade-<?php echo esc_attr( $impact_score_class ) . ' ' . esc_attr( $gray_class ); ?>" data-score="<?php echo absint( $score * 100 ); ?>"></div>
				</div>
				<div>
					<?php if ( 'disabled' !== $impact_score_class && $this->view_exists( "performance/metrics/{$rule}" ) ) : ?>
						<?php if ( ! empty( $rule_result->description ) || ! empty( $rule_result->tip ) ) : ?>
							<?php echo isset( $rule_result->displayValue ) ? esc_html( $rule_result->displayValue ) : esc_html__( 'N/A', 'wphb' ); ?>
							<button class="sui-button-icon sui-accordion-open-indicator" aria-label="<?php esc_attr_e( 'Open item', 'wphb' ); ?>">
								<span class="sui-icon-chevron-down" aria-hidden="true"></span>
							</button>
						<?php endif; ?>
					<?php endif; ?>
				</div>
			</div>

			<?php if ( $this->view_exists( "performance/metrics/{$rule}" ) ) : ?>
				<div class="sui-accordion-item-body">
					<div class="sui-box">
						<div class="sui-box-body">
							<?php
							$this->view(
								"performance/metrics/{$rule}",
								array(
									'audit' => $rule_result,
									'url'   => \Hummingbird\Core\Utils::get_admin_menu_url( 'performance' ) . '&view=audits&type=' . $type,
								)
							);
							?>
						</div>
					</div>
				</div>
			<?php endif; ?>
		</div>
	<?php endforeach; ?>
</div>
