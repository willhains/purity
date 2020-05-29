package com.willhains.purity;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;

import static com.willhains.purity.SingleNumber.$;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleDecimal} object.
 *
 * @author willhains
 */
@FunctionalInterface interface DecimalRule
{
	/** Applies this rule to the raw value. */
	BigDecimal applyTo(BigDecimal d);

	// Lazy cache of rules for subclasses
	RulesCache<DecimalRule> CACHE = new RulesCache<>();
	static DecimalRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, DecimalRule::fromAnnotations);
	}

	static DecimalRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final List<DecimalRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			for(double limit: adjust.floor()) rules.add(floor($(limit)));
			for(double limit: adjust.ceiling()) rules.add(ceiling($(limit)));
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even fromAnnotations the
			// validation rules
			if(validate.onFailure() != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(double min: validate.min()) rules.add(min($(min)));
				for(double max: validate.max()) rules.add(max($(max)));
				for(double bound: validate.greaterThan()) rules.add(greaterThan($(bound)));
				for(double bound: validate.lessThan()) rules.add(lessThan($(bound)));
				if(!validate.allowNegative()) rules.add(min($(0.0)));
				if(!validate.allowZero()) rules.add(notEqualTo("0"));
			}
		}

		// Build a new rule from the Rule constants declared in the class
		return DecimalRule.combine(rules.toArray(new DecimalRule[0]));
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static DecimalRule min(final @Returned BigDecimal minValue)
	{
		return validUnless(raw -> raw.compareTo(minValue) < 0, raw -> raw + " < " + minValue);
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static DecimalRule max(final @Returned BigDecimal maxValue)
	{
		return validUnless(raw -> raw.compareTo(maxValue) > 0, raw -> raw + " > " + maxValue);
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static DecimalRule greaterThan(final @Returned BigDecimal lowerBound)
	{
		return validIf(raw -> raw.compareTo(lowerBound) > 0, raw -> raw + " <= " + lowerBound);
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static DecimalRule lessThan(final @Returned BigDecimal upperBound)
	{
		return validIf(raw -> raw.compareTo(upperBound) < 0, raw -> raw + " >= " + upperBound);
	}

	static DecimalRule notEqualTo(final @Returned String disallowedValue)
	{
		final BigDecimal disallowedDecimal = new BigDecimal(disallowedValue);
		return validUnless(raw -> raw.compareTo(disallowedDecimal) == 0,
			raw -> "\"" + raw + "\" matches \"" + disallowedValue + "\"");
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static DecimalRule floor(final BigDecimal minValue) { return raw -> raw.max(minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static DecimalRule ceiling(final BigDecimal maxValue) { return raw -> raw.min(maxValue); }
	
	/** Combine multiple rules into a single rule. */
	static DecimalRule combine(final DecimalRule... combiningRules)
	{
		return raw ->
		{
			BigDecimal result = raw;
			for(final DecimalRule rule: combiningRules) result = rule.applyTo(result);
			return result;
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link DecimalRule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @return a {@link DecimalRule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static DecimalRule validIf(
		final Predicate<BigDecimal> condition,
		final Function<BigDecimal, String> errorMessageFactory)
	{
		return raw ->
		{
			if(condition.test(raw)) return raw;
			throw new IllegalArgumentException(errorMessageFactory.apply(raw));
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link DecimalRule} where `condition` must evaluate to `false`.
	 *
	 * @param condition the raw value must not satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is met.
	 * @return a {@link DecimalRule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static DecimalRule validUnless(
		final Predicate<BigDecimal> condition,
		final Function<BigDecimal, String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}

	/**
	 * @see #validIf(Predicate,Function)
	 */
	static DecimalRule validIf(final Predicate<BigDecimal> condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}

	/**
	 * @see #validUnless(Predicate,Function)
	 */
	static DecimalRule validUnless(final Predicate<BigDecimal> condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}
