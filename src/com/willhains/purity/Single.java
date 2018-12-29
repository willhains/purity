package com.willhains.purity;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * A value type wrapping an underlying immutable type.
 * This supertype and its abstract subtypes make it easy to implement pure {@link Value} types.
 *
 * <pre>
 * public final @Value class HostName extends Single&lt;String&gt;
 * {
 *     public HostName(final String hostname)
 *     {
 *         super(hostname);
 *         // validate the value of hostname here
 *     }
 *
 *     // add domain methods here
 * }
 * </pre>
 *
 * @author willhains
 * @param <Raw> The underlying type. Must be strictly immutable.
 */
public abstract @Value class Single<Raw>
{
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	public final Raw raw;
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 */
	protected Single(final Raw rawValue)
	{
		raw = requireNonNull(rawValue);
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
		@SuppressWarnings("unchecked") final Single<Raw> that = (Single<Raw>)other;
		
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
}
