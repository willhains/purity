package com.willhains.purity.annotations;

/** Choices for what to do when validation fails. */
public enum OnFailure
{
	/** Throw an {@link IllegalArgumentException}. */
	THROW,

	/** If JVM assertions are enabled, throw an {@link AssertionError}. */
	ASSERT,

	/** Exit the JVM, with a non-zero error code. */
	EXIT,

	/** Ignore validation failures. */
	IGNORE
}
