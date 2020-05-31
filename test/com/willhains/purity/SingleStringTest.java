package com.willhains.purity;

import org.junit.*;

import static com.willhains.purity.Trim.*;
import static com.willhains.purity.Validate.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/** @author willhains */
public class SingleStringTest
{
	public static final class Name extends SingleString<Name>
	{
		public Name(final String rawValue) { super(rawValue, Name::new); }
	}

	@Adjust(trim = WHITESPACE, intern = true)
	@Validate(min = 1, max = 255, chars = LETTERS + DIGITS + "-._")
	public static final @Pure class HostName extends SingleString<HostName>
	{
		public HostName(String hostName) { super(hostName, HostName::new); }
	}

	@Test
	public void shouldCompareLess()
	{
		final Name x = new Name("Anthony");
		final Name y = new Name("Barnaby");
		assertTrue(x.compareTo(y) < 0);
	}

	@Test
	public void shouldCompareMore()
	{
		final Name x = new Name("Anthony");
		final Name y = new Name("Barnaby");
		assertTrue(y.compareTo(x) > 0);
	}

	@Test
	public void shouldCompareEqual()
	{
		final Name x = new Name("Anthony");
		final Name y = new Name("Anthony");
		assertEquals(0, x.compareTo(y));
	}

	@Test
	public void shouldReturnUntrimmedLength()
	{
		final Name x = new Name("Will ");
		assertThat(x.length(), is(5));
	}

	@Test
	public void shouldReturnZeroLength()
	{
		final Name x = new Name("");
		assertThat(x.length(), is(0));
	}

	@Test
	public void shouldReturnNthCharacter()
	{
		final Name x = new Name("Will");
		assertThat(x.charAt(0), is('W'));
		assertThat(x.charAt(1), is('i'));
		assertThat(x.charAt(2), is('l'));
		assertThat(x.charAt(3), is('l'));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapNegativeIndex()
	{
		final Name x = new Name("Will");
		x.charAt(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapIndexEqualToLength()
	{
		final Name x = new Name("Will");
		x.charAt(4);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapIndexGreaterThanLength()
	{
		final Name x = new Name("Will");
		x.charAt(5);
	}

	@Test
	public void shouldExtractSubSequenceFromValidIndices()
	{
		final Name x = new Name("Will");
		assertThat(x.subSequence(1, 3), is(new Name("il")));
	}

	@Adjust(trim = WHITESPACE)
	static final class A extends SingleString<A>
	{
		A(String a) { super(a, A::new); }
	}

	@Test
	public void shouldTrimWhitespace()
	{
		final A x = new A(" abc ");
		assertThat(x.raw(), is("abc"));
	}

	@Validate(chars = "abcdefg")
	static final class B extends SingleString<B>
	{
		B(String a) { super(a, B::new); }
	}

	@Test
	public void shouldAcceptAllValidCharacters()
	{
		new B("abc");
	}

	@Validate(chars = "abcdefg")
	static final class C extends SingleString<C>
	{
		C(String a) { super(a, C::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapInvalidCharacters() { new C("abc "); }

	@Validate(match = "[a-z]-[0-9]")
	static final class D extends SingleString<D>
	{
		D(String a) { super(a, D::new); }
	}

	@Test
	public void shouldAcceptMatchingPattern() { new D("b-7"); }

	@Validate(match = "[a-z]-[0-9]")
	static final class E extends SingleString<E>
	{
		E(String a) { super(a, E::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapNonMatchingPattern() { new E("b-52"); }

	@Validate(min = 2, max = 5)
	static final class F extends SingleString<F>
	{
		F(String a) { super(a, F::new); }
	}

	@Test
	public void shouldAcceptValidLength() { new F("abc"); }

	@Validate(min = 2, max = 5)
	static final class G extends SingleString<G>
	{
		G(String a) { super(a, G::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLengthTooShort()
	{
		new G("a");
	}

	@Validate(min = 2, max = 5)
	static final class H extends SingleString<H>
	{
		H(String a) { super(a, H::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLengthTooLong()
	{
		new H("abcdef");
	}

	@Test(expected = StringIndexOutOfBoundsException.class)
	public void shouldTrapNegativeLeftLength()
	{
		final Name x = new Name("Will Hains");
		x.left(-1);
	}

	@Test(expected = StringIndexOutOfBoundsException.class)
	public void shouldTrapNegativeRightLength()
	{
		final Name x = new Name("Will Hains");
		x.right(-1);
	}

	@Test
	public void shouldReturnLeftZeroLengthSubstring()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.left(0), is(new Name("")));
	}

	@Test
	public void shouldReturnRightZeroLengthSubstring()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.right(0), is(new Name("")));
	}

	@Test
	public void shouldReturnLeftSubstring()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.left(4), is(new Name("Will")));
	}

	@Test
	public void shouldReturnRightSubstring()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.right(5), is(new Name("Hains")));
	}

	@Test
	public void shouldReturnWholeLeftString()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.left(100), is(new Name("Will Hains")));
	}

	@Test
	public void shouldReturnWholeRightString()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.right(100), is(new Name("Will Hains")));
	}

	@Test
	public void shouldTrimToSameType()
	{
		final Name x = new Name("Will ");
		assertThat(x.trim(), is(instanceOf(Name.class)));
		assertThat(x.trim(), is(new Name("Will")));
	}

	@Test
	public void shouldDetectEmpty()
	{
		final Name x = new Name("");
		assertTrue(x.isEmpty());
		assertFalse(x.isNotEmpty());
	}

	@Test
	public void shouldDetectNonEmpty()
	{
		final Name x = new Name("Will Hains");
		assertTrue(x.isNotEmpty());
		assertFalse(x.isEmpty());
	}

	@Test
	public void shouldReplaceNothingMatchingPattern()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.replaceRegex("[0-9]+", "_"), is(x));
	}

	@Test
	public void shouldReplaceVowels()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.replaceRegex("[aieou]", "_"), is(new Name("W_ll H__ns")));
	}

	@Test
	public void shouldReplaceNothing()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.replaceLiteral("x", "_"), is(x));
	}

	@Test
	public void shouldReplace()
	{
		final Name x = new Name("Will Hains");
		assertThat(x.replaceLiteral("i", "_"), is(new Name("W_ll Ha_ns")));
	}
}
