package com.willhains.purity;

/**
 * A {@link Single} value with a {@link Comparable} underlying value.
 * 
 * @author willhains
 * @param <This> Self-reference to the subclass type itself.
 */
public @Pure interface SingleComparable<This extends SingleComparable<This>> extends Comparable<This>
{
	/** @return the larger of {@code this} and {@code that}. */
	default This max(final This that)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return this.compareTo(that) > 0 ? self : that;
	}

	/** @return the smaller of {@code this} and {@code that}. */
	default This min(final This that)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return this.compareTo(that) < 0 ? self : that;
	}
}
