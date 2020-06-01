package com.willhains.purity;

import java.util.*;

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
		final ArrayList<DoubleRule> rules = new ArrayList<>();

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
				if(!validate.allowInfinity()) rules.add(finite);
				if(!validate.allowNaN()) rules.add(isNumber);
			}
		}

		// Build a new rule from the Rule constants declared in the class
		rules.trimToSize();
		return raw ->
		{
			double result = raw;
			for(final DoubleRule rule: rules) result = rule.applyTo(result);
			return result;
		};
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static DoubleRule min(final double minValue)
	{
		return raw ->
		{
			if(raw >= minValue) return raw;
			throw new IllegalArgumentException(raw + " < " + minValue);
		};
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static DoubleRule max(final double maxValue)
	{
		return raw ->
		{
			if(raw <= maxValue) return raw;
			throw new IllegalArgumentException(raw + " > " + maxValue);
		};
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static DoubleRule greaterThan(final double lowerBound)
	{
		return raw ->
		{
			if(raw > lowerBound) return raw;
			throw new IllegalArgumentException(raw + " <= " + lowerBound);
		};
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static DoubleRule lessThan(final double upperBound)
	{
		return raw ->
		{
			if(raw < upperBound) return raw;
			throw new IllegalArgumentException(raw + " >= " + upperBound);
		};
	}

	/** Rule to disallow infinite values. */
	DoubleRule finite = raw ->
	{
		if(Double.isFinite(raw)) return raw;
		throw new IllegalArgumentException(raw + " is not finite");
	};

	/** Rule to disallow not-a-number values. */
	DoubleRule isNumber = raw ->
	{
		if(!Double.isNaN(raw)) return raw;
		throw new IllegalArgumentException(raw + " is not a number");
	};

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static DoubleRule floor(final double minValue) { return raw -> Math.max(raw, minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static DoubleRule ceiling(final double maxValue) { return raw -> Math.min(raw, maxValue); }
}
