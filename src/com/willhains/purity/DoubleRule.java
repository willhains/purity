package com.willhains.purity;

import java.util.*;
import java.util.function.*;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleDouble} object.
 *
 * @author willhains
 */
@FunctionalInterface interface DoubleRule
{
	/** Applies this rule to the raw value. */
	double applyTo(double raw);

	// Lazy cache of rules for subclasses
	RulesCache<DoubleRule> CACHE = new CopyOnWriteRulesCache<>();
	static DoubleRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, DoubleRule::fromAnnotations);
	}

	static DoubleRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final List<DoubleRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			for(final double limit: adjust.floor()) rules.add(floor(limit));
			for(final double limit: adjust.ceiling()) rules.add(ceiling(limit));
//  	    for(double increment: adjust.roundToIncrement()) rules.add(round(increment, adjust.rounding())); TODO
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even create the validation rules
			if(validate.onFailure() != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(final double min: validate.min()) rules.add(min(min));
				for(final double max: validate.max()) rules.add(max(max));
				for(final double bound: validate.greaterThan()) rules.add(greaterThan(bound));
				for(final double bound: validate.lessThan()) rules.add(lessThan(bound));
//		   		for(double increment: validate.multipleOf()) rules.add(divisibleBy(increment)); TODO
				if(!validate.allowNegative()) rules.add(min(0.0));
//		    	if(!validate.allowZero()) rules.add(rejectZero); TODO
				if(!validate.allowInfinity()) rules.add(validIf(Double::isFinite, "Must be finite"));
				if(!validate.allowNaN()) rules.add(validUnless(Double::isNaN, "Not a number"));
			}
		}

		// Build a new rule from the Rule constants declared in the class
		return DoubleRule.combine(rules.toArray(new DoubleRule[0]));
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static DoubleRule min(final double minValue)
	{
		return validIf(raw -> raw >= minValue, raw -> raw + " < " + minValue);
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static DoubleRule max(final double maxValue)
	{
		return validIf(raw -> raw <= maxValue, raw -> raw + " > " + maxValue);
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static DoubleRule greaterThan(final double lowerBound)
	{
		return validIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static DoubleRule lessThan(final double upperBound)
	{
		return validIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static DoubleRule floor(final double minValue) { return raw -> Math.max(raw, minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static DoubleRule ceiling(final double maxValue) { return raw -> Math.min(raw, maxValue); }

	/** Combine multiple rules into a single rule. */
	static DoubleRule combine(final DoubleRule... combiningRules)
	{
		return raw ->
		{
			double result = raw;
			for(final DoubleRule rule: combiningRules) result = rule.applyTo(result);
			return result;
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link Rule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static DoubleRule validIf(
		final DoublePredicate condition,
		final DoubleFunction<String> errorMessageFactory)
	{
		return raw ->
		{
			if(condition.test(raw)) return raw;
			throw new IllegalArgumentException(errorMessageFactory.apply(raw));
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link Rule} where `condition` must evaluate to `false`.
	 *
	 * @param condition the raw value must not satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is met.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static DoubleRule validUnless(
		final DoublePredicate condition,
		final DoubleFunction<String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}

	/**
	 * @see #validIf(DoublePredicate, DoubleFunction)
	 */
	static DoubleRule validIf(final DoublePredicate condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}

	/**
	 * @see #validUnless(DoublePredicate, DoubleFunction)
	 */
	static DoubleRule validUnless(final DoublePredicate condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}
