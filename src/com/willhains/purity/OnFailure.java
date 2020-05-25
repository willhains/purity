package com.willhains.purity;

/** Choices for what to do when validation fails. */
public enum OnFailure
{
	/** Throw an {@link IllegalArgumentException}. */
	THROW,

	/** If JVM assertions are enabled, throw an {@link AssertionError}. */
	ASSERT
}
