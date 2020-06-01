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
			final ValidationPolicy validationPolicy = validate.onFailure();
			if(validationPolicy != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(final double min: validate.min()) rules.add(min(min, validationPolicy));
				for(final double max: validate.max()) rules.add(max(max, validationPolicy));
				for(final double bound: validate.greaterThan()) rules.add(greaterThan(bound, validationPolicy));
				for(final double bound: validate.lessThan()) rules.add(lessThan(bound, validationPolicy));
//		   		for(double increment: validate.multipleOf()) rules.add(divisibleBy(increment, validationPolicy)); TODO
				if(!validate.allowInfinity()) rules.add(finite(validationPolicy));
				if(!validate.allowNaN()) rules.add(isNumber(validationPolicy));
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
					double result = raw;
					for(final DoubleRule rule: rules) result = rule.applyTo(result);
					return result;
				};
			}
		}
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static DoubleRule min(final double minValue, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw < minValue) validationPolicy.onFailure(raw + " < " + minValue);
			return raw;
		};
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static DoubleRule max(final double maxValue, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw > maxValue) validationPolicy.onFailure(raw + " > " + maxValue);
			return raw;
		};
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static DoubleRule greaterThan(final double lowerBound, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw <= lowerBound) validationPolicy.onFailure(raw + " <= " + lowerBound);
			return raw;
		};
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static DoubleRule lessThan(final double upperBound, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw >= upperBound) validationPolicy.onFailure(raw + " >= " + upperBound);
			return raw;
		};
	}

	/** Generate rule to disallow infinite values. */
	static DoubleRule finite(final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(Double.isInfinite(raw)) validationPolicy.onFailure(raw + " is not finite");
			return raw;
		};
	}

	/** Generate rule to disallow not-a-number values. */
	static DoubleRule isNumber(final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(Double.isNaN(raw)) validationPolicy.onFailure(raw + " is not a number");
			return raw;
		};
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static DoubleRule floor(final double minValue) { return raw -> Math.max(raw, minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static DoubleRule ceiling(final double maxValue) { return raw -> Math.min(raw, maxValue); }
}
