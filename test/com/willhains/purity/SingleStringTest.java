package com.willhains.purity;

import org.junit.Test;

import static com.willhains.purity.Rule.all;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SingleStringTest
{
	public static final class Name extends SingleString<Name>
	{
		public Name(final String rawValue) { super(rawValue, Name::new); }
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
		assertTrue(x.compareTo(y) == 0);
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
	
	static final class A extends SingleString<A>
	{
		private static final Rule RULES = trimWhitespace;
		A(String a) { super(a, A::new); }
	}
	
	@Test
	public void shouldTrimWhitespace()
	{
		final A x = new A(" abc ");
		assertThat(x.raw, is("abc"));
	}
	
	static final class B extends SingleString<B>
	{
		private static final Rule RULES = validCharacters("abcdefg");
		B(String a) { super(a, B::new); }
	}
	
	@Test
	public void shouldAcceptAllValidCharacters()
	{
		new B("abc");
	}
	
	static final class C extends SingleString<C>
	{
		private static final Rule RULES = validCharacters("abcdefg");
		C(String a) { super(a, C::new); }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapInvalidCharacters()
	{
		new C("abc ");
	}
	
	static final class D extends SingleString<D>
	{
		private static final Rule RULES = validPattern("[a-z]-[0-9]");
		D(String a) { super(a, D::new); }
	}
	
	@Test
	public void shouldAcceptMatchingPattern()
	{
		new D("b-7");
	}
	
	static final class E extends SingleString<E>
	{
		private static final Rule RULES = validPattern("[a-z]-[0-9]");
		E(String a) { super(a, E::new); }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapNonMatchingPattern()
	{
		new E("b-52");
	}
	
	static final class F extends SingleString<F>
	{
		private static final Rule RULES = all(minLength(2), maxLength(5));
		F(String a) { super(a, F::new); }
	}
	
	@Test
	public void shouldAcceptValidLength()
	{
		new F("abc");
	}
	
	static final class G extends SingleString<G>
	{
		private static final Rule RULES = all(minLength(2), maxLength(5));
		G(String a) { super(a, G::new); }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLengthTooShort()
	{
		new G("a");
	}
	
	static final class H extends SingleString<H>
	{
		private static final Rule RULES = all(minLength(2), maxLength(5));
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
