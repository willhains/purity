package com.willhains.purity;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.willhains.purity.SingleNumber.*;
import static java.math.BigDecimal.*;
import static java.math.RoundingMode.*;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleDecimal} object.
 *
 * @author willhains
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use {@link org.udtopia.rules.RationalNormalizer} and {@link org.udtopia.rules.RationalValidator} instead.
 */
@Deprecated
@FunctionalInterface interface DecimalRule
{
	/** Do-nothing rule. */
	DecimalRule NONE = raw -> raw;

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
			for(final double increment: adjust.roundToIncrement()) rules.add(round($(increment)));
		}

		// Raw value validations
		final Validate validate = singleClass.getAnnotation(Validate.class);
		if(validate != null)
		{
			// When the validation policy is ASSERT and assertions are disabled, don't even create the validation rules
			final ValidationPolicy onFailure = validate.onFailure();
			if(onFailure != ValidationPolicy.ASSERT || singleClass.desiredAssertionStatus())
			{
				for(final double min: validate.min()) rules.add(min($(min), onFailure));
				for(final double max: validate.max()) rules.add(max($(max), onFailure));
				for(final double bound: validate.greaterThan()) rules.add(greaterThan($(bound), onFailure));
				for(final double bound: validate.lessThan()) rules.add(lessThan($(bound), onFailure));
				for(final double increment: validate.multipleOf()) rules.add(divisibleBy($(increment), onFailure));
			}
		}

		// Build a new rule from the Rule constants declared in the class
		switch(rules.size())
		{
			case 0:
				return NONE;
			case 1:
				return rules.get(0);
			default:
				rules.trimToSize();
				return raw ->
				{
					BigDecimal result = raw;
					for(final DecimalRule rule: rules) result = rule.applyTo(result);
					return result;
				};
		}
	}

	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	static DecimalRule min(@Returned final BigDecimal minValue, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw.compareTo(minValue) < 0) validationPolicy.onFailure(raw + " < " + minValue);
			return raw;
		};
	}

	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	static DecimalRule max(@Returned final BigDecimal maxValue, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw.compareTo(maxValue) > 0) validationPolicy.onFailure(raw + " > " + maxValue);
			return raw;
		};
	}

	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	static DecimalRule greaterThan(@Returned final BigDecimal lowerBound, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw.compareTo(lowerBound) <= 0) validationPolicy.onFailure(raw + " <= " + lowerBound);
			return raw;
		};
	}

	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	static DecimalRule lessThan(@Returned final BigDecimal upperBound, final ValidationPolicy validationPolicy)
	{
		return raw ->
		{
			if(raw.compareTo(upperBound) >= 0) validationPolicy.onFailure(raw + " >= " + upperBound);
			return raw;
		};
	}

	/** Generate rule to require values that are evenly divisible by an increment. */
	static DecimalRule divisibleBy(final BigDecimal increment, final ValidationPolicy validationPolicy)
	{
		final BigDecimal decimalIncrement = $(increment);
		return raw ->
		{
			if(raw.remainder(decimalIncrement).compareTo(ZERO) != 0)
			{
				validationPolicy.onFailure(raw + " is not a multiple of " + increment);
			}
			return raw;
		};
	}

	/** Generate rule to normalise the raw value to a minimum floor value. */
	static DecimalRule floor(final BigDecimal minValue) { return raw -> raw.max(minValue); }

	/** Generate rule to normalise the raw value to a maximum ceiling value. */
	static DecimalRule ceiling(final BigDecimal maxValue) { return raw -> raw.min(maxValue); }

	/** Generate rule to round the raw value to an increment. */
	static DecimalRule round(final BigDecimal increment)
	{
		return raw -> raw.divide(increment, HALF_UP).setScale(0, HALF_UP).multiply(increment);
	}
}
