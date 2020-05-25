package com.willhains.purity;

import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;

import static com.willhains.purity.LetterCase.*;
import static com.willhains.purity.Trim.WHITESPACE;

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
			for(Trim trim: adjust.trim())
			{
				if(trim == WHITESPACE) rules.add(String::trim);
			}
			for(LetterCase adjustCase: adjust.transformTo())
			{
				if(adjustCase == LOWERCASE) rules.add(String::toLowerCase);
				if(adjustCase == UPPERCASE) rules.add(String::toUpperCase);
			}
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even create the validation rules
			if(validate.onFailure() != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(double length: validate.min()) rules.add(minLength((int)length));
				for(double length: validate.max()) rules.add(maxLength((int)length));

				final String allowedCharacters = String.join("", validate.validCharacters());
				if(!allowedCharacters.isEmpty()) rules.add(validCharacters(allowedCharacters));

				final String allowedPattern = String.join("|", validate.validPatterns());
				if(!allowedPattern.isEmpty()) rules.add(validPattern(allowedPattern));
			}
		}

		// Heap management
		final Intern intern = singleClass.getAnnotation(Intern.class);
		if(intern != null)
		{
			// No annotation parameters
			rules.add(String::intern);
		}

		// Build a new rule from the StringRule constants declared in the class
		return StringRule.combine(rules.toArray(new StringRule[0]));
	}

	/** Generate rule to allow only the characters of `allowedCharacters`. */
	static StringRule validCharacters(final String allowedCharacters)
	{
		final boolean[] validCharMap = new boolean[Character.MAX_VALUE + 1];
		allowedCharacters.chars().forEach(c -> validCharMap[c] = true);
		return validIf(raw -> raw.chars().allMatch(c -> validCharMap[c]),
			raw -> "\"" + raw + "\" contains invalid characters (valid = " + allowedCharacters + ")");
	}

	/** Generate rules to allow only raw strings that match `regExPattern`. */
	static StringRule validPattern(final String regExPattern)
	{
		final Pattern pattern = Pattern.compile(regExPattern);
		return StringRule.validIf(raw -> pattern.matcher(raw).matches(),
			raw -> "\"" + raw + "\" does not match pattern: " + regExPattern);
	}

	/** Generate rule to allow only raw strings of length greater than or equal to `length`. */
	static StringRule minLength(final int length)
	{
		return validUnless(raw -> raw.length() < length,
			raw -> "Value \"" + raw + "\" too short: " + raw.length() + " < " + length);
	}

	/** Generate rule to allow only raw strings of length less than or equal to `length`. */
	static StringRule maxLength(final int length)
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
