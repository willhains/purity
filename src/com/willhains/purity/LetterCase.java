package com.willhains.purity;

/**
 * Choices for transforming the case of string values.
 *
 * @author willhains
 */
public enum LetterCase
{
	LOWERCASE(String::toLowerCase),
	UPPERCASE(String::toUpperCase);

	final StringRule stringRule;
	LetterCase(final StringRule rule) { this.stringRule = rule; }
}
