/**
 * External dependencies
 */
import React from 'react';

/**
 * Toggle functional component.
 *
 * @param {string}  text     Toggle text.
 * @param {string}  id       Toggle ID.
 * @param {string}  name     Toggle name.
 * @param {boolean} checked  Checked status.
 * @return {*} Toggle component.
 * @class
 */
export default function Toggle( { text, id, name, onChange, checked = false, ...props } ) {
	return (
		<label htmlFor={ id } className="sui-toggle">
			<input
				type="checkbox"
				name={ name }
				id={ id }
				checked={ checked }
				onChange={ onChange }
				{ ...props }
			/>
			<span className="sui-toggle-slider" aria-hidden="true" />
			{ text }
		</label>
	);
}
