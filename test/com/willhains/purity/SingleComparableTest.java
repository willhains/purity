package com.willhains.purity;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/** @author willhains */
public class SingleComparableTest
{
	public static final class Height extends Single<Float, Height> implements SingleComparable<Height>
	{
		public Height(final Float rawValue) { super(rawValue, Height::new); }
		@Override public int compareTo(final Height that) { return this.raw().compareTo(that.raw()); }
	}

	@Test
	public void shouldChooseLarger()
	{
		final Height x = new Height(50.0f);
		final Height y = new Height(100.0f);
		assertThat(x.max(y), is(y));
	}

	@Test
	public void shouldChooseSmaller()
	{
		final Height x = new Height(50f);
		final Height y = new Height(100f);
		assertThat(x.min(y), is(x));
	}

	@Test
	public void shouldChooseOne()
	{
		final Height x = new Height(100.0f);
		final Height y = new Height(100.0f);
		assertThat(x.max(y), is(y));
		assertThat(x.max(y), is(x));
		assertThat(x.min(y), is(y));
		assertThat(x.min(y), is(x));
	}
}
