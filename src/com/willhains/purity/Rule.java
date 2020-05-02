package com.willhains.purity;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link Single} or other {@link Pure} object.
 *
 * @param <Raw> the raw type to be validated/normalised.
 * @author willhains
 */
public @FunctionalInterface interface Rule<Raw>
{
	/** @return An empty rule that does nothing. */
	@SuppressWarnings("unchecked") static <Raw> Rule<Raw> none() { return (Rule<Raw>)NONE; }
	static final Rule<?> NONE = raw -> raw;
	
	/** Applies this rule to the given argument. */
	Raw apply(Raw raw) throws IllegalArgumentException;
	
	/**
	 * @return the value of the first constant of type {@link Rule} declared in the given {@link Single} subclass.
	 */
	static <Raw, This extends Single<Raw, This>> Rule<Raw>[] rulesForClass(final Class<This> single)
	{
		return Constants.ofClass(single).getConstantsOfType(Rule.class);
	}
	
	/** Combine multiple rules into a single rule. */
	@SafeVarargs
	static <Raw> Rule<Raw> allOf(final Rule<Raw>... combiningRules)
	{
		return raw ->
		{
			Raw result = raw;
			for(final Rule<Raw> rule: combiningRules) result = rule.apply(result);
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
	static <Raw> Rule<Raw> validIf(
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
		return validIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validIf(Predicate,Function)
	 */
	static <Raw> Rule<Raw> validIf(final Predicate<? super Raw> condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(Predicate,Function)
	 */
	static <Raw> Rule<Raw> validUnless(final Predicate<? super Raw> condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}

// Extension of Class<?> to extract constant values
@FunctionalInterface interface Constants extends Supplier<Class<?>>
{
	static Constants ofClass(Class<?> single)
	{
		return () -> single;
	}
	
	/** @return the values of constants of the given type, in the supplied class. */
	default <T> T[] getConstantsOfType(final Class<T> ofType)
	{
		final Class<?> fromClass = get();
		return Arrays.stream(fromClass.getDeclaredFields())
			.filter(field -> field.getType().equals(ofType))
			.filter(field -> Modifier.isStatic(field.getModifiers()))
			.filter(field -> Modifier.isFinal(field.getModifiers()))
			.peek(field -> field.setAccessible(true))
			.flatMap(Constants::getConstantValue)
			.toArray(size -> (T[])Array.newInstance(ofType, size));
	}
	
	static <T> Stream<T> getConstantValue(final Field field)
	{
		try
		{
			@SuppressWarnings("unchecked") final T value = (T)field.get(null);
			return Stream.of(value);
		}
		catch(IllegalAccessException e)
		{
			return Stream.empty();
		}
	}
}
