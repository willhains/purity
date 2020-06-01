package com.willhains.purity;

import java.util.*;

import static java.lang.Long.*;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleLong} object.
 *
 * @author willhains
 */
@FunctionalInterface interface LongRule
{
	/** Applies this rule to the raw value. */
	long applyTo(long raw);

	// Lazy cache of rules for subclasses
	RulesCache<LongRule> CACHE = new CopyOnWriteRulesCache<>();
	static LongRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, LongRule::fromAnnotations);
	}

	static LongRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final ArrayList<LongRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			for(final double limit: adjust.floor()) rules.add(floor(limit));
			for(final double limit: adjust.ceiling()) rules.add(ceiling(limit));
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
			}
		}

		// Build a new rule from the Rule constants declared in the class
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
					long result = raw;
					for(final LongRule rule: rules) result = rule.applyTo(result);
					return result;
				};
			}
		}
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static LongRule min(final double minValue)
	{
		return raw ->
		{
			if(raw >= minValue) return raw;
			throw new IllegalArgumentException(raw + " < " + minValue);
		};
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static LongRule max(final double maxValue)
	{
		return raw ->
		{
			if(raw <= maxValue) return raw;
			throw new IllegalArgumentException(raw + " > " + maxValue);
		};
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static LongRule greaterThan(final double lowerBound)
	{
		return raw ->
		{
			if(raw > lowerBound) return raw;
			throw new IllegalArgumentException(raw + " <= " + lowerBound);
		};
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static LongRule lessThan(final double upperBound)
	{
		return raw ->
		{
			if(raw < upperBound) return raw;
			throw new IllegalArgumentException(raw + " >= " + upperBound);
		};
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static LongRule floor(final double minValue)
	{
		@SuppressWarnings("NumericCastThatLosesPrecision") final long min = (long)Math.max(minValue, MIN_VALUE);
		return raw -> Math.max(raw, min);
	}

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static LongRule ceiling(final double maxValue)
	{
		@SuppressWarnings("NumericCastThatLosesPrecision") final long max = (long)Math.min(maxValue, MAX_VALUE);
		return raw -> Math.min(raw, max);
	}
}
