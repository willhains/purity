package com.willhains.purity;

import java.util.*;
import java.util.function.*;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleInt} object.
 *
 * @author willhains
 */
@FunctionalInterface interface IntRule
{
	/** Applies this rule to the raw value. */
	int applyTo(int i);

	// Lazy cache of rules for subclasses
	RulesCache<IntRule> CACHE = new CopyOnWriteRulesCache<>();
	static IntRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, IntRule::fromAnnotations);
	}

	static IntRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final ArrayList<IntRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			for(final double limit: adjust.floor()) rules.add(floor((int)limit));
			for(final double limit: adjust.ceiling()) rules.add(ceiling((int)limit));
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even create the validation rules
			if(validate.onFailure() != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(final double min: validate.min()) rules.add(min((int)min));
				for(final double max: validate.max()) rules.add(max((int)max));
				for(final double bound: validate.greaterThan()) rules.add(greaterThan((int)bound));
				for(final double bound: validate.lessThan()) rules.add(lessThan((int)bound));
//		   		for(double increment: validate.multipleOf()) rules.add(divisibleBy(increment)); TODO
				if(!validate.allowNegative()) rules.add(min(0));
//		    	if(!validate.allowZero()) rules.add(rejectZero); TODO
			}
		}

		// Build a new rule from the Rule constants declared in the class
		rules.trimToSize();
		return raw ->
		{
			int result = raw;
			for(final IntRule rule: rules) result = rule.applyTo(result);
			return result;
		};
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static IntRule min(final int minValue)
	{
		return validIf(raw -> raw >= minValue, raw -> raw + " < " + minValue);
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static IntRule max(final int maxValue)
	{
		return validIf(raw -> raw <= maxValue, raw -> raw + " > " + maxValue);
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static IntRule greaterThan(final int lowerBound)
	{
		return validIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static IntRule lessThan(final int upperBound)
	{
		return validIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static IntRule floor(final int minValue) { return raw -> Math.max(raw, minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static IntRule ceiling(final int maxValue) { return raw -> Math.min(raw, maxValue); }

	/**
	 * Convert the {@link Predicate} `condition` into a {@link IntRule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @return a {@link IntRule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static IntRule validIf(
		final IntPredicate condition,
		final IntFunction<String> errorMessageFactory)
	{
		return raw ->
		{
			if(condition.test(raw)) return raw;
			throw new IllegalArgumentException(errorMessageFactory.apply(raw));
		};
	}

	/**
	 * Convert the {@link Predicate} `condition` into a {@link IntRule} where `condition` must evaluate to `false`.
	 *
	 * @param condition the raw value must not satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is met.
	 * @return a {@link IntRule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static IntRule validUnless(
		final IntPredicate condition,
		final IntFunction<String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}

	/**
	 * @see #validIf(IntPredicate, IntFunction)
	 */
	static IntRule validIf(final IntPredicate condition, final String errorMessage)
	{
		return validIf(condition, raw -> errorMessage);
	}

	/**
	 * @see #validUnless(IntPredicate, IntFunction)
	 */
	static IntRule validUnless(final IntPredicate condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}
