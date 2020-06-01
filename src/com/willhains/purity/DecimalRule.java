package com.willhains.purity;

import java.math.*;
import java.util.*;

import static com.willhains.purity.SingleNumber.*;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleDecimal} object.
 *
 * @author willhains
 */
@FunctionalInterface interface DecimalRule
{
	/** Applies this rule to the raw value. */
	BigDecimal applyTo(BigDecimal raw);

	// Lazy cache of rules for subclasses
	RulesCache<DecimalRule> CACHE = new CopyOnWriteRulesCache<>();
	static DecimalRule rulesForClass(final Class<?> singleClass)
	{
		return CACHE.computeIfAbsent(singleClass, DecimalRule::fromAnnotations);
	}

	static DecimalRule fromAnnotations(final Class<?> singleClass)
	{
		// Build a new rule from the annotations on the class
		final ArrayList<DecimalRule> rules = new ArrayList<>();

		// Raw value adjustments
		final Adjust adjust = singleClass.getAnnotation(Adjust.class);
		if(adjust != null)
		{
			for(final double limit: adjust.floor()) rules.add(floor($(limit)));
			for(final double limit: adjust.ceiling()) rules.add(ceiling($(limit)));
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even fromAnnotations the
			// validation rules
			if(validate.onFailure() != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(final double min: validate.min()) rules.add(min($(min)));
				for(final double max: validate.max()) rules.add(max($(max)));
				for(final double bound: validate.greaterThan()) rules.add(greaterThan($(bound)));
				for(final double bound: validate.lessThan()) rules.add(lessThan($(bound)));
				if(!validate.allowNegative()) rules.add(min($(0.0)));
				if(!validate.allowZero()) rules.add(notEqualTo("0"));
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
					BigDecimal result = raw;
					for(final DecimalRule rule: rules) result = rule.applyTo(result);
					return result;
				};
			}
		}
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static DecimalRule min(final @Returned BigDecimal minValue)
	{
		return raw ->
		{
			if(raw.compareTo(minValue) >= 0) return raw;
			throw new IllegalArgumentException(raw + " < " + minValue);
		};
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static DecimalRule max(final @Returned BigDecimal maxValue)
	{
		return raw ->
		{
			if(raw.compareTo(maxValue) <= 0) return raw;
			throw new IllegalArgumentException(raw + " > " + maxValue);
		};
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static DecimalRule greaterThan(final @Returned BigDecimal lowerBound)
	{
		return raw ->
		{
			if(raw.compareTo(lowerBound) > 0) return raw;
			throw new IllegalArgumentException(raw + " <= " + lowerBound);
		};
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static DecimalRule lessThan(final @Returned BigDecimal upperBound)
	{
		return raw ->
		{
			if(raw.compareTo(upperBound) < 0) return raw;
			throw new IllegalArgumentException(raw + " >= " + upperBound);
		};
	}

	static DecimalRule notEqualTo(final @Returned String disallowedValue)
	{
		final BigDecimal disallowedDecimal = new BigDecimal(disallowedValue);
		return raw ->
		{
			if(raw.compareTo(disallowedDecimal) != 0) return raw;
			throw new IllegalArgumentException("\"" + raw + "\" matches \"" + disallowedValue + "\"");
		};
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static DecimalRule floor(final BigDecimal minValue) { return raw -> raw.max(minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static DecimalRule ceiling(final BigDecimal maxValue) { return raw -> raw.min(maxValue); }
}
