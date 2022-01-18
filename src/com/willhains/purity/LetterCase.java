package com.willhains.purity;

/**
 * Choices for transforming the case of string values.
 *
 * @author willhains
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use {@link org.udtopia.rules.LowerCase} and {@link org.udtopia.rules.UpperCase} instead.
 */
@Deprecated
public @Pure enum LetterCase
{
	LOWERCASE(String::toLowerCase),
	UPPERCASE(String::toUpperCase);

	final StringRule stringRule;
	LetterCase(final StringRule rule) { stringRule = rule; }
}
