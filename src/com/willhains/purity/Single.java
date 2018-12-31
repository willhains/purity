package com.willhains.purity;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A value type wrapping an underlying immutable type.
 * This supertype and its abstract subtypes make it easy to implement pure {@link Value} types.
 * 
 * <pre>
 * public final @Value class HostName extends Single&lt;String, HostName&gt;
 * {
 *     private static final Rule&lt;String&gt; rules = Rule.rules(
 *         String:toLowerCase,
 *         validUnless(String::isEmpty, "Host name must not be empty"));
 *     public HostName(final String hostname)
 *     {
 *         super(hostname, HostName::new, rules);
 *     }
 *     
 *     // add domain methods here
 * }
 * </pre>
 *
 * @author willhains
 * @param <Raw> The underlying type. Must be strictly immutable.
 * @param <This> Self-reference to the subclass type itself.
 */
public abstract @Value class Single<Raw, This extends Single<Raw, This>>
{
	// The single-argument constructor of the subclass
	private final Function<? super Raw, ? extends This> _constructor;
	
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	public final Raw raw;
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected Single(final Raw rawValue, final Function<? super Raw, ? extends This> constructor)
	{
		raw = requireNonNull(rawValue);
		_constructor = requireNonNull(constructor);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param rules Validation and data normalisation rules for the raw underlying value.
	 */
	protected Single(final Raw rawValue, final Function<? super Raw, ? extends This> constructor, final Rule<Raw> rules)
	{
		this(rules.apply(rawValue), constructor);
	}
	
	@Override
	public final int hashCode()
	{
		if(!raw.getClass().isArray()) return raw.hashCode();
		
		if(raw instanceof Object[]) return Arrays.deepHashCode((Object[])raw);
		if(raw instanceof int[]) return Arrays.hashCode((int[])raw);
		if(raw instanceof byte[]) return Arrays.hashCode((byte[])raw);
		if(raw instanceof boolean[]) return Arrays.hashCode((boolean[])raw);
		if(raw instanceof long[]) return Arrays.hashCode((long[])raw);
		if(raw instanceof double[]) return Arrays.hashCode((double[])raw);
		if(raw instanceof float[]) return Arrays.hashCode((float[])raw);
		if(raw instanceof char[]) return Arrays.hashCode((char[])raw);
		return Arrays.hashCode((short[])raw);
	}
	
	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This)other;
		
		final Raw thisRaw = this.raw;
		final Raw thatRaw = that.raw;
		if(thisRaw == thatRaw) return true;
		if(thisRaw.equals(thatRaw)) return true;
		if(!thisRaw.getClass().isArray()) return false;
		
		if(thisRaw instanceof Object[]) return Arrays.deepEquals((Object[])thisRaw, (Object[])thatRaw);
		if(thisRaw instanceof int[]) return Arrays.equals((int[])thisRaw, (int[])thatRaw);
		if(thisRaw instanceof byte[]) return Arrays.equals((byte[])thisRaw, (byte[])thatRaw);
		if(thisRaw instanceof boolean[]) return Arrays.equals((boolean[])thisRaw, (boolean[])thatRaw);
		if(thisRaw instanceof long[]) return Arrays.equals((long[])thisRaw, (long[])thatRaw);
		if(thisRaw instanceof double[]) return Arrays.equals((double[])thisRaw, (double[])thatRaw);
		if(thisRaw instanceof float[]) return Arrays.equals((float[])thisRaw, (float[])thatRaw);
		if(thisRaw instanceof char[]) return Arrays.equals((char[])thisRaw, (char[])thatRaw);
		return Arrays.equals((short[])thisRaw, (short[])thatRaw);
	}
	
	@Override
	public String toString()
	{
		if(!raw.getClass().isArray()) return String.valueOf(raw);
		
		if(raw instanceof Object[]) return Arrays.toString((Object[])raw);
		if(raw instanceof int[]) return Arrays.toString((int[])raw);
		if(raw instanceof byte[]) return Arrays.toString((byte[])raw);
		if(raw instanceof boolean[]) return Arrays.toString((boolean[])raw);
		if(raw instanceof long[]) return Arrays.toString((long[])raw);
		if(raw instanceof double[]) return Arrays.toString((double[])raw);
		if(raw instanceof float[]) return Arrays.toString((float[])raw);
		if(raw instanceof char[]) return Arrays.toString((char[])raw);
		return Arrays.toString((short[])raw);
	}
	
	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is(String::isEmpty))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #raw} value satisfies {@code condition};
	 *         {@code false} otherwise.
	 */
	public final boolean is(final Predicate<? super Raw> condition) { return condition.test(raw); }
	
	/** Reverse of {@link #is(Predicate)}. */
	public final boolean isNot(final Predicate<? super Raw> condition) { return !is(condition); }
}
