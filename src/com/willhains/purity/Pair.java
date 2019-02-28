package com.willhains.purity;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * A tuple of two values.
 *
 * @param <Left> the first value type.
 * @param <Right> the second value type.
 */
public final @Pure class Pair<@Pure Left, @Pure Right>
{
	public final Left left;
	public final Right right;
	
	private Pair(final Left left, final Right right)
	{
		this.left = requireNonNull(left);
		this.right = requireNonNull(right);
	}
	
	/** @return a new {@link Pair} containing two values. */
	public static <@Pure Left, @Pure Right> Pair<Left, Right> of(final Left left, final Right right)
	{
		return new Pair<>(left, right);
	}
	
	@Override public boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final Pair<?,?> that = (Pair<?,?>)other;
		return Single.equals(this.left, that.left) && Single.equals(this.right, that.right);
	}
	
	@Override public int hashCode() { return Single.hashCode(left) * 37 + Single.hashCode(right); }
	@Override public String toString() { return Single.toString(left) + "|" + Single.toString(right); }
	
	/** Reverse the left and right sides of the pair. */
	public Pair<Right, Left> flip() { return new Pair<>(right, left); }
	
	/**
	 * @return an {@link Optional} containing this pair if {@link #left} satisfies {@code condition}; empty otherwise.
	 */
	public Optional<Pair<Left, Right>> filterLeft(final Predicate<? super Left> condition)
	{
		return requireNonNull(condition).test(left) ? Optional.of(this) : Optional.empty();
	}
	
	/**
	 * @return an {@link Optional} containing this pair if {@link #right} satisfies {@code condition}; empty otherwise.
	 */
	public Optional<Pair<Left, Right>> filterRight(final Predicate<? super Right> condition)
	{
		return requireNonNull(condition).test(right) ? Optional.of(this) : Optional.empty();
	}
	
	/**
	 * Transform the {@link #left} value with the given mapper function.
	 */
	public <@Pure Converted> Pair<Converted, Right> mapLeft(final Function<? super Left, ? extends Converted> mapper)
	{
		return new Pair<>(requireNonNull(mapper).apply(left), right);
	}
	
	/**
	 * Transform the {@link #right} value with the given mapper function.
	 */
	public <@Pure Converted> Pair<Left, Converted> mapRight(final Function<? super Right, ? extends Converted> mapper)
	{
		return new Pair<>(left, requireNonNull(mapper).apply(right));
	}
	
	public static <Left, Right> Collector<Pair<Left, Right>, ?, Index<Left, Right>> toIndex()
	{
		return collectingAndThen(toMap(pair -> pair.left, pair -> pair.right), Index::copy);
	}
}
