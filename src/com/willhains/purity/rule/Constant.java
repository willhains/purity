package com.willhains.purity.rule;

import com.willhains.purity.Single;
import com.willhains.purity.annotations.Pure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

/** Wrapper of Field to extract modifiers and value. */
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
