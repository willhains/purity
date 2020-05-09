package com.willhains.purity;

import com.willhains.purity.annotations.Pure;

import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleLong} or other {@link Pure} object.
 *
 * @author willhains
 */
public @FunctionalInterface interface LongRule
{
	/** An empty rule that does nothing. */
	static final LongRule NONE = raw -> raw;
	
	/** Applies this rule to the given argument. */
	long apply(long i);
	
	/**
	 * @return the value of the first constant of type {@link LongRule} declared in the given {@link SingleLong}
	 *  subclass.
	 */
	static <This extends SingleLong<This>> LongRule[] rulesForClass(final Class<This> single)
	{
		return Constants.ofClass(single).getConstantsOfType(LongRule.class);
	}
	
	/** Combine multiple rules into a single rule. */
	static LongRule combine(final LongRule... combiningRules)
	{
		return raw ->
		{
			long result = raw;
			for(final LongRule rule: combiningRules) result = rule.apply(result);
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
	static LongRule validIf(
		final LongPredicate condition,
		final LongFunction<String> errorMessageFactory)
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
	static LongRule validUnless(
		final LongPredicate condition,
		final LongFunction<String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validIf(LongPredicate,LongFunction)
	 */
	static LongRule validIf(final LongPredicate condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(LongPredicate,LongFunction)
	 */
	static LongRule validUnless(final LongPredicate condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}
