package com.willhains.purity.rule;

import com.willhains.purity.Single;
import com.willhains.purity.annotations.Pure;

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
		@SuppressWarnings("unchecked")
		final Rule<Raw>[] rules = (Rule<Raw>[])Constants.ofClass(single).getConstantsOfType(Rule.class);
		return rules;
	}
	
	/** Combine multiple rules into a single rule. */
	@SafeVarargs
	static <Raw> Rule<Raw> combine(final Rule<Raw>... combiningRules)
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
	static Constants ofClass(final Class<?> type) { return () -> type; }
	
	/** @return the values of constants of the given type, or arrays of the type, in the supplied class. */
	default <T> T[] getConstantsOfType(final Class<T> type)
	{
		return Arrays.stream(get().getDeclaredFields())
			.map(Constant::new)
			.filter(Constant::isConstant)
			.filter(constant -> constant.isTypeOrArrayOfType(type))
			.flatMap(Constant::getValues)
			.map(type::cast)
			.toArray(size -> _newArray(type, size));
	}
	
	static <T> T[] _newArray(final Class<T> type, final int size)
	{
		@SuppressWarnings("unchecked") final T[] array = (T[])Array.newInstance(type, size);
		return array;
	}
}

// Wrapper of Field to extract modifiers and value
final @Pure class Constant<T> extends Single<Field, Constant<T>>
{
	Constant(final Field field) { super(field, Constant::new); }
	
	boolean isConstant()
	{
		final int modifiers = raw.getModifiers();
		return Modifier.isStatic(modifiers)
			&& Modifier.isFinal(modifiers);
	}
	
	boolean isTypeOrArrayOfType(final Class<?> type)
	{
		if(raw.getType().equals(type)) return true;
		if(!type.isArray()) return false;
		return type.getComponentType().equals(type);
	}
	
	/** @return a stream of the value (if a non-array) or values (if an array) of the given constant field. */
	Stream<T> getValues()
	{
		raw.setAccessible(true);
		try
		{
			final Object value = raw.get(null);
			if(raw.getType().isArray())
			{
				@SuppressWarnings("unchecked") final T[] array = (T[])value;
				return Arrays.stream(array);
			}
			@SuppressWarnings("unchecked") final T t = (T)value;
			return Stream.of(t);
		}
		catch(IllegalAccessException e)
		{
			return Stream.empty();
		}
	}
}
