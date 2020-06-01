package com.willhains.purity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Objects.*;

/**
 * A value type wrapping an underlying data type.
 * This base class makes it easy to implement {@link Pure} value types.
 *
 * @param <Raw> The underlying type.
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class Single<Raw, This extends Single<Raw, This>>
{
	// The single-argument constructor of the subclass
	private final Function<? super Raw, ? extends This> _constructor;

	/** The raw underlying value. Do not mutate! */
	private final Raw _raw;

	/**
	 * @param rawValue The raw value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected Single(final Raw rawValue, final Function<? super Raw, ? extends This> constructor)
	{
		this._raw = requireNonNull(rawValue);
		_constructor = requireNonNull(constructor);
	}

	/** Return the raw underlying value. */
	public final Raw raw() { return defensiveCopy(_raw); }

	/**
	 * Override this method if {@link Raw} is mutable. The default implementation assumes {@link Raw} is immutable, and
	 * returns it as-is. The returned {@link Raw} value, if mutable, could be mutated, so the overriding method should
	 * be careful to make a deep, defensive copy to return to the caller.
	 */
	@SuppressWarnings({"WeakerAccess", "DesignForExtension"})
	protected Raw defensiveCopy(final Raw raw) { return raw; }

	@Override public final int hashCode() { return Single.hashCode(raw()); }

	/** Generate a hash code for {@code object}. If {@code object} is an array, combine the hashes of each element. */
	@SuppressWarnings("ChainOfInstanceofChecks")
	public static int hashCode(final Object object)
	{
		if(!object.getClass().isArray()) return object.hashCode();

		if(object instanceof Object[]) return Arrays.deepHashCode((Object[])object);
		if(object instanceof int[]) return Arrays.hashCode((int[])object);
		if(object instanceof byte[]) return Arrays.hashCode((byte[])object);
		if(object instanceof boolean[]) return Arrays.hashCode((boolean[])object);
		if(object instanceof long[]) return Arrays.hashCode((long[])object);
		if(object instanceof double[]) return Arrays.hashCode((double[])object);
		if(object instanceof float[]) return Arrays.hashCode((float[])object);
		if(object instanceof char[]) return Arrays.hashCode((char[])object);
		if(object instanceof short[]) return Arrays.hashCode((short[])object);
		throw new AssertionError("Missing array case in Purity");
	}

	@Override
	public final boolean equals(final Object obj)
	{
		if(obj == this) return true;
		if(obj == null) return false;
		if(!this.getClass().equals(obj.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This)obj;
		return Single.equals(this.raw(), that.raw());
	}

	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return Single.equals(this.raw(), that.raw());
	}

	/** Compare two objects for equality. If they are arrays, compare their elements. */
	@SuppressWarnings("ChainOfInstanceofChecks")
	static boolean equals(final Object object1, final Object object2)
	{
		if(object1 == object2) return true;
		if(object1 == null || object2 == null) return false;
		if(object1.equals(object2)) return true;
		if(!object1.getClass().isArray()) return false;

		if(object1 instanceof Object[]) return Arrays.deepEquals((Object[])object1, (Object[])object2);
		if(object1 instanceof int[]) return Arrays.equals((int[])object1, (int[])object2);
		if(object1 instanceof byte[]) return Arrays.equals((byte[])object1, (byte[])object2);
		if(object1 instanceof boolean[]) return Arrays.equals((boolean[])object1, (boolean[])object2);
		if(object1 instanceof long[]) return Arrays.equals((long[])object1, (long[])object2);
		if(object1 instanceof double[]) return Arrays.equals((double[])object1, (double[])object2);
		if(object1 instanceof float[]) return Arrays.equals((float[])object1, (float[])object2);
		if(object1 instanceof char[]) return Arrays.equals((char[])object1, (char[])object2);
		if(object1 instanceof short[]) return Arrays.equals((short[])object1, (short[])object2);
		throw new AssertionError("Missing array case in Purity");
	}

	@Override
	public String toString()
	{
		return Single.toString(raw());
	}

	/** Format a string to represent {@code object}. If {@code object} is an array, include each element. */
	@SuppressWarnings("ChainOfInstanceofChecks")
	public static String toString(final Object object)
	{
		if(!object.getClass().isArray()) return String.valueOf(object);

		if(object instanceof Object[]) return Arrays.toString((Object[])object);
		if(object instanceof int[]) return Arrays.toString((int[])object);
		if(object instanceof byte[]) return Arrays.toString((byte[])object);
		if(object instanceof boolean[]) return Arrays.toString((boolean[])object);
		if(object instanceof long[]) return Arrays.toString((long[])object);
		if(object instanceof double[]) return Arrays.toString((double[])object);
		if(object instanceof float[]) return Arrays.toString((float[])object);
		if(object instanceof char[]) return Arrays.toString((char[])object);
		if(object instanceof short[]) return Arrays.toString((short[])object);
		throw new AssertionError("Missing array case in Purity");
	}

	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is(String::isEmpty))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link Raw} value satisfies {@code condition};
	 *    {@code false} otherwise.
	 */
	public final boolean is(final Predicate<? super Raw> condition) { return condition.test(raw()); }

	/** Reverse of {@link #is(Predicate)}. */
	public final boolean isNot(final Predicate<? super Raw> condition) { return !is(condition); }

	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(Predicate)
	 */
	public final Optional<This> filter(final Predicate<? super Raw> condition)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return Optional.of(self).filter(it -> it.is(condition));
	}

	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return A new instance of this type.
	 */
	public final This map(final Function<? super Raw, ? extends Raw> mapper)
	{
		final Raw mapped = mapper.apply(raw());
		@SuppressWarnings("unchecked") final This self = (This)this;
		if(mapped.equals(raw())) return self;
		return _constructor.apply(mapped);
	}

	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final Function<? super Raw, ? extends This> mapper) { return mapper.apply(raw()); }
}
