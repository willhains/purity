package com.willhains.purity;

/**
 * Choices for what to do when validation fails.
 *
 * @author willhains
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use {@link org.udtopia.rules.ApplyRuleWhen} instead.
 */
@Deprecated
public @Pure enum ValidationPolicy
{
	/** Throw an {@link IllegalArgumentException}. */
	THROW,

	/** If JVM assertions are enabled, throw an {@link AssertionError}. */
	ASSERT;

	void onFailure(final String message)
	{
		switch(this)
		{
			case THROW:
				throw new IllegalArgumentException(message);
			case ASSERT:
				throw new AssertionError(message);
		}
	}
}
