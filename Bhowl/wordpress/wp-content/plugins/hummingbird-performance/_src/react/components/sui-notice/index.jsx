/**
 * External dependencies
 */
import React from 'react';
import classNames from 'classnames';

/**
 * Notice functional component.
 *
 * @param {string} message  Notice message.
 * @param {Array}  classes  Array of extra classes to use.
 * @param {Object} content  CTA content.
 * @return {*} Notice component.
 * @class
 */
export default function Notice( { message, classes, content } ) {
	const combinedClasses = classNames( 'sui-notice', classes );

	return (
		<div className={ combinedClasses }>
			<div className="sui-notice-content">
				<div className="sui-notice-message">
					<span
						className="sui-notice-icon sui-icon-info sui-md"
						aria-hidden="true"
					></span>
					<p>{ message }</p>

					{ content && <p>{ content }</p> }
				</div>
			</div>
		</div>
	);
}
