package com.willhains.purity;

/**
 * Choices for trimming string values.
 *
 * @author willhains
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use {@link org.udtopia.rules.Trim} instead.
 */
@Deprecated
public @Pure enum Trim
{
	/**
	 * Trim whitespace from the beginning and end of the string value.
	 *
	 * @see String#trim
	 */
	WHITESPACE(String::trim);

	final StringRule stringRule;
	Trim(final StringRule rule) { stringRule = rule; }
}
