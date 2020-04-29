package com.willhains.purity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link Single} or other {@link Pure} object.
 *
 * @param <Raw> the raw type to be validated/normalised.
 * @author willhains
 */
public @FunctionalInterface interface Rule<Raw>
{
	Raw applyRule(Raw raw) throws IllegalArgumentException;
	
	static <Raw, This extends Single<Raw, This>> Rule<Raw> rulesForClass(final Class<This> single)
	{
		/* Nullable */ Rule<Raw> rules = getConstant(single, "RULES");
		if(rules == null) rules = getConstant(single, "_RULES");
		if(rules == null) rules = all(); // empty
		return rules;
	}
	
	static <T> /* Nullable */ T getConstant(final Class<?> fromClass, final String name)
	{
		try
		{
			final Field constant = fromClass.getDeclaredField(name);
			final int modifiers = constant.getModifiers();
			if((modifiers & Modifier.STATIC) == 0) return null;
			if((modifiers & Modifier.FINAL) == 0) return null;
			if((modifiers & Modifier.PUBLIC) == 0) constant.setAccessible(true);
			return (T)constant.get(null);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			return null;
		}
		catch(ClassCastException e)
		{
			throw new UnsupportedOperationException("RULES constant of incorrect type found in " + fromClass);
		}
	}
	
	/** Combine multiple rules into a single rule. */
	@SafeVarargs
	static <Raw> Rule<Raw> all(final Rule<Raw>... combiningRules)
	{
		return raw ->
		{
			Raw result = raw;
			for(final Rule<Raw> rule: combiningRules) result = rule.applyRule(result);
			return result;
		};
	}
	
	/**
	 * Convert the {@link Predicate} `condition` into a {@link Rule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @param <Raw> the raw value type to be validated.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static <Raw> Rule<Raw> validOnlyIf(
		final Predicate<? super Raw> condition,
		final Function<? super Raw, String> errorMessageFactory)
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
	 * @param <Raw> the raw value type to be validated.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static <Raw> Rule<Raw> validUnless(
		final Predicate<? super Raw> condition,
		final Function<? super Raw, String> errorMessageFactory)
	{
		return validOnlyIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validOnlyIf(Predicate,Function)
	 */
	static <Raw> Rule<Raw> validOnlyIf(final Predicate<? super Raw> condition, final String errorMessage)
	{
		return validOnlyIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(Predicate,Function)
	 */
	static <Raw> Rule<Raw> validUnless(final Predicate<? super Raw> condition, final String errorMessage)
	{
		return validOnlyIf(condition.negate(), errorMessage);
	}
}
