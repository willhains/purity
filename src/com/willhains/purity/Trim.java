package com.willhains.purity;

/**
 * Choices for trimming string values.
 *
 * @author willhains
 */
public @Pure enum Trim
{
	/**
	 * Trim whitespace from the beginning and end of the string value.
	 *
	 * @see String#trim
	 */
	WHITESPACE(String::trim);

	final StringRule stringRule;
	Trim(final StringRule rule) { this.stringRule = rule; }
}
