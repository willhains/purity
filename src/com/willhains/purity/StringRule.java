package com.willhains.purity;

import com.willhains.purity.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleString} object.
 *
 * @author willhains
 */
@FunctionalInterface interface StringRule
{
	/** Applies this rule to the raw value. */
	String applyTo(String d);

	// Lazy cache of rules for subclasses
	RulesCache<StringRule> CACHE = new RulesCache<>();
	static StringRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, StringRule::fromAnnotations);
	}

	static StringRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final List<StringRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			if(adjust.trimWhitespace()) rules.add(String::trim);
			if(adjust.lowercase()) rules.add(String::toLowerCase);
			if(adjust.uppercase()) rules.add(String::toUpperCase);
//			for(int length: adjust.truncate()) rules.add(truncate(length)); TODO
			if(adjust.internRaw()) rules.add(String::intern);
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even create the validation rules
			if(validate.onFailure() != Validate.OnFailure.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(double length: validate.min()) rules.add(minLength((int)length));
				for(double length: validate.max()) rules.add(maxLength((int)length));

				final String allowedCharacters = String.join("", validate.validCharacters());
				if(!allowedCharacters.isEmpty()) rules.add(validCharacters(allowedCharacters));

				final String allowedPattern = String.join("|", validate.validPatterns());
				if(!allowedPattern.isEmpty()) rules.add(validPattern(allowedPattern));
			}
		}

		// Build a new rule from the StringRule constants declared in the class
		return StringRule.combine(rules.toArray(new StringRule[0]));
	}

	/** Letters (of the English alphabet). */
	public static final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** Digit characters. */
	public static final String numbers = "0123456789";

	/** Generate rule to allow only the characters of `allowedCharacters`. */
	public static StringRule validCharacters(final String allowedCharacters)
	{
		final boolean[] validCharMap = new boolean[Character.MAX_VALUE + 1];
		allowedCharacters.chars().forEach(c -> validCharMap[c] = true);
		return validIf(raw -> raw.chars().allMatch(c -> validCharMap[c]),
			raw -> "\"" + raw + "\" contains invalid characters (valid = " + allowedCharacters + ")");
	}

	/** Generate rules to allow only raw strings that match `regExPattern`. */
	public static StringRule validPattern(final String regExPattern)
	{
		final Pattern pattern = Pattern.compile(regExPattern);
		return StringRule.validIf(raw -> pattern.matcher(raw).matches(),
			raw -> "\"" + raw + "\" does not match pattern: " + regExPattern);
	}

	/** Generate rule to allow only raw strings of length greater than or equal to `length`. */
	public static StringRule minLength(final int length)
	{
		return validUnless(raw -> raw.length() < length,
			raw -> "Value \"" + raw + "\" too short: " + raw.length() + " < " + length);
	}

	/** Generate rule to allow only raw strings of length less than or equal to `length`. */
	public static StringRule maxLength(final int length)
	{
		return validUnless(raw -> raw.length() > length,
			raw -> "Value \\\"\" + raw + \"\\\" too long: " + raw.length() + " > " + length);
	}

	/** Combine multiple rules into a single rule. */
	static StringRule combine(final StringRule... combiningRules)
	{
		return raw ->
		{
			String result = raw;
			for(final StringRule rule: combiningRules) result = rule.applyTo(result);
			return result;
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link StringRule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @return a {@link StringRule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static StringRule validIf(
		final Predicate<String> condition,
		final Function<String, String> errorMessageFactory)
	{
		return raw ->
		{
			if(condition.test(raw)) return raw;
			throw new IllegalArgumentException(errorMessageFactory.apply(raw));
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link StringRule} where `condition` must evaluate to `false`.
	 *
	 * @param condition the raw value must not satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is met.
	 * @return a {@link StringRule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static StringRule validUnless(
		final Predicate<String> condition,
		final Function<String, String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}

	/**
	 * @see #validIf(Predicate,Function)
	 */
	static StringRule validIf(final Predicate<String> condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}

	/**
	 * @see #validUnless(Predicate,Function)
	 */
	static StringRule validUnless(final Predicate<String> condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}
