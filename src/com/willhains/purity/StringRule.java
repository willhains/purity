package com.willhains.purity;

import java.util.*;
import java.util.regex.*;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleString} object.
 *
 * @author willhains
 */
@FunctionalInterface interface StringRule
{
	/** Applies this rule to the raw value. */
	String applyTo(String raw);

	// Lazy cache of rules for subclasses
	RulesCache<StringRule> CACHE = new CopyOnWriteRulesCache<>();
	static StringRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, StringRule::fromAnnotations);
	}

	static StringRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final ArrayList<StringRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			for(final Trim trim: adjust.trim()) rules.add(trim.stringRule);
			for(final LetterCase adjustCase: adjust.transformTo()) rules.add(adjustCase.stringRule);
			if(adjust.intern()) rules.add(String::intern);
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even create the validation rules
			final ValidationPolicy validationPolicy = validate.onFailure();
			if(validationPolicy != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(final double length: validate.min()) rules.add(minLength(length, validationPolicy));
				for(final double length: validate.max()) rules.add(maxLength(length, validationPolicy));

				final String allowedCharacters = String.join("", validate.chars());
				if(!allowedCharacters.isEmpty()) rules.add(validCharacters(allowedCharacters, validationPolicy));

				final String disallowedChars = String.join("", validate.notChars());
				if(!disallowedChars.isEmpty()) { rules.add(invalidCharacters(disallowedChars, validationPolicy)); }

				final String validPattern = String.join("|", validate.match());
				if(!validPattern.isEmpty()) rules.add(validPattern(validPattern, validationPolicy));

				for(final String invalid: validate.notMatch()) rules.add(invalidPattern(invalid, validationPolicy));
			}
		}

		// Build a new rule from the StringRule constants declared in the class
		switch(rules.size())
		{
			case 0:
				return raw -> raw;
			case 1:
				return rules.get(0);
			default:
			{
				rules.trimToSize();
				return raw ->
				{
					String result = raw;
					for(final StringRule rule: rules) result = rule.applyTo(result);
					return result;
				};
			}
		}
	}

	/** Generate rule to allow only the characters of `allowedCharacters`. */
	static StringRule validCharacters(final String allowedCharacters, final ValidationPolicy validationPolicy)
	{
		final boolean[] validCharMap = new boolean[Character.MAX_VALUE + 1];
		allowedCharacters.chars().forEach(charIndex -> validCharMap[charIndex] = true);
		return raw ->
		{
			if(!raw.chars().allMatch(charIndex -> validCharMap[charIndex]))
			{
				validationPolicy.onFailure(
					"\"" + raw + "\" contains invalid characters (valid = " + allowedCharacters + ")");
			}
			return raw;
		};
	}

	/** Generate rule to disallow the characters of `disallowedCharacters`. */
	static StringRule invalidCharacters(
		final String disallowedCharacters,
		final ValidationPolicy validationPolicy)
	{
		final boolean[] validCharMap = new boolean[Character.MAX_VALUE + 1];
		disallowedCharacters.chars().forEach(charIndex -> validCharMap[charIndex] = true);
		return raw ->
		{
			if(raw.chars().anyMatch(charIndex -> validCharMap[charIndex]))
			{
				validationPolicy.onFailure(
					"\"" + raw + "\" contains invalid characters (invalid = " + disallowedCharacters + ")");
			}
			return raw;
		};
	}

	/** Generate rules to allow only raw strings that match `regExPattern`. */
	static StringRule validPattern(
		final String regExPattern,
		final ValidationPolicy validationPolicy)
	{
		final Pattern pattern = Pattern.compile(regExPattern);
		return raw ->
		{
			if(!pattern.matcher(raw).matches())
			{
				validationPolicy.onFailure("\"" + raw + "\" does not match pattern: " + regExPattern);
			}
			return raw;
		};
	}

	/** Generate rules to disallow raw strings that match `regExPattern`. */
	static StringRule invalidPattern(
		final String regExPattern,
		final ValidationPolicy validationPolicy)
	{
		final Pattern pattern = Pattern.compile(regExPattern);
		return raw ->
		{
			if(pattern.matcher(raw).matches())
			{
				validationPolicy.onFailure("\"" + raw + "\" matches pattern: " + regExPattern);
			}
			return raw;
		};
	}

	/** Generate rule to allow only raw strings of length greater than or equal to `length`. */
	static StringRule minLength(final double length, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw.length() < length)
			{
				validationPolicy.onFailure("Value \"" + raw + "\" too short: " + raw.length() + " < " + length);
			}
			return raw;
		};
	}

	/** Generate rule to allow only raw strings of length less than or equal to `length`. */
	static StringRule maxLength(final double length, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw.length() > length)
			{
				validationPolicy.onFailure("Value \"" + raw + "\" too long: " + raw.length() + " > " + length);
			}
			return raw;
		};
	}
}
