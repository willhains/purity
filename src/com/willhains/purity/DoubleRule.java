package com.willhains.purity;

import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleDouble} or other {@link Pure} object.
 *
 * @author willhains
 */
public @FunctionalInterface interface DoubleRule
{
	/** Applies this rule to the given argument. */
	double applyRule(double i);
	
	static <This extends SingleDouble<This>> DoubleRule rulesForClass(final Class<This> single)
	{
		/* Nullable */ DoubleRule rules = Rule.getConstant(single, "RULES");
		if(rules == null) rules = Rule.getConstant(single, "_RULES");
		if(rules == null) rules = all(); // empty
		return rules;
	}
	
	/** Combine multiple rules into a single rule. */
	static DoubleRule all(final DoubleRule... combiningRules)
	{
		return raw ->
		{
			double result = raw;
			for(final DoubleRule rule: combiningRules) result = rule.applyRule(result);
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
	static DoubleRule validOnlyIf(
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
		return validOnlyIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validOnlyIf(DoublePredicate,DoubleFunction)
	 */
	static DoubleRule validOnlyIf(final DoublePredicate condition, final String errorMessage)
	{
		return validOnlyIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(DoublePredicate,DoubleFunction)
	 */
	static DoubleRule validUnless(final DoublePredicate condition, final String errorMessage)
	{
		return validOnlyIf(condition.negate(), errorMessage);
	}
}
