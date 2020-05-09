package com.willhains.purity.rule;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Supplier;

/** Extension of Class to extract constant values. */
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
