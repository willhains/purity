package com.willhains.purity;

import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static com.willhains.purity.Plural.toPlural;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PluralTest
{
	@Test
	public void shouldCreateEmpty()
	{
		final Plural<Object> x = Plural.empty();
		final Plural<Object> y = Plural.copy(new Object[0]);
		assertTrue(x.asList().isEmpty());
		assertTrue(y.asList().isEmpty());
		assertEquals(x, y);
		assertEquals(x.hashCode(), y.hashCode());
	}
	
	@Test
	public void shouldCreateWithOneElement()
	{
		final Plural<String> x = Plural.of("a");
		final Plural<String> y = Plural.of("b");
		assertThat(x.asList().size(), is(1));
		assertNotEquals(x, y);
		assertThat(x.toString(), containsString("a"));
	}
	
	@Test
	public void shouldCreateWithUpToTwentyElements()
	{
		// @formatter:off
		final Plural<String> x2 = Plural.of("a","b");
		final Plural<String> x3 = Plural.of("a","b","c");
		final Plural<String> x4 = Plural.of("a","b","c","d");
		final Plural<String> x5 = Plural.of("a","b","c","d","e");
		final Plural<String> x6 = Plural.of("a","b","c","d","e","f");
		final Plural<String> x7 = Plural.of("a","b","c","d","e","f","g");
		final Plural<String> x8 = Plural.of("a","b","c","d","e","f","g","h");
		final Plural<String> x9 = Plural.of("a","b","c","d","e","f","g","h","i");
		final Plural<String> x10 = Plural.of("a","b","c","d","e","f","g","h","i","j");
		final Plural<String> x11 = Plural.of("a","b","c","d","e","f","g","h","i","j","k");
		final Plural<String> x12 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l");
		final Plural<String> x13 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m");
		final Plural<String> x14 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n");
		final Plural<String> x15 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o");
		final Plural<String> x16 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p");
		final Plural<String> x17 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q");
		final Plural<String> x18 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r");
		final Plural<String> x19 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s");
		final Plural<String> x20 = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t");
		assertThat(x2.asList().stream().collect(joining("")), is("ab"));
		assertThat(x3.asList().stream().collect(joining("")), is("abc"));
		assertThat(x4.asList().stream().collect(joining("")), is("abcd"));
		assertThat(x5.asList().stream().collect(joining("")), is("abcde"));
		assertThat(x6.asList().stream().collect(joining("")), is("abcdef"));
		assertThat(x7.asList().stream().collect(joining("")), is("abcdefg"));
		assertThat(x8.asList().stream().collect(joining("")), is("abcdefgh"));
		assertThat(x9.asList().stream().collect(joining("")), is("abcdefghi"));
		assertThat(x10.asList().stream().collect(joining("")), is("abcdefghij"));
		assertThat(x11.asList().stream().collect(joining("")), is("abcdefghijk"));
		assertThat(x12.asList().stream().collect(joining("")), is("abcdefghijkl"));
		assertThat(x13.asList().stream().collect(joining("")), is("abcdefghijklm"));
		assertThat(x14.asList().stream().collect(joining("")), is("abcdefghijklmn"));
		assertThat(x15.asList().stream().collect(joining("")), is("abcdefghijklmno"));
		assertThat(x16.asList().stream().collect(joining("")), is("abcdefghijklmnop"));
		assertThat(x17.asList().stream().collect(joining("")), is("abcdefghijklmnopq"));
		assertThat(x18.asList().stream().collect(joining("")), is("abcdefghijklmnopqr"));
		assertThat(x19.asList().stream().collect(joining("")), is("abcdefghijklmnopqrs"));
		assertThat(x20.asList().stream().collect(joining("")), is("abcdefghijklmnopqrst"));
		// @formatter:on
	}
	
	@Test
	public void shouldCreateWithMoreElements()
	{
		// @formatter:off
		final Plural<Integer> x21 = Plural.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21);
		final Plural<Integer> x22 = Plural.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22);
		final Plural<Integer> x23 = Plural.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23);
		assertThat(x21.asList().stream().map($ -> $.toString()).collect(joining(",")), is("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21"));
		assertThat(x22.asList().stream().map($ -> $.toString()).collect(joining(",")), is("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22"));
		assertThat(x23.asList().stream().map($ -> $.toString()).collect(joining(",")), is("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23"));
		// @formatter:on
	}
	
	@Test
	public void shouldBuildFromArray()
	{
		final String[] s0 = {};
		final String[] s1 = {"a"};
		final String[] s2 = {"a", "b"};
		final Plural<String> x0 = Plural.copy(s0);
		final Plural<String> x1 = Plural.copy(s1);
		final Plural<String> x2 = Plural.copy(s2);
		assertThat(x0.asList().stream().collect(joining("")), is(""));
		assertThat(x1.asList().stream().collect(joining("")), is("a"));
		assertThat(x2.asList().stream().collect(joining("")), is("ab"));
	}
	
	@Test
	public void shouldBeImmutableAfterArrayConstructors()
	{
		// @formatter:off
		final String[] strings = {"u","v","w"};
		final Plural<String> x = Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t", strings);
		strings[1] = "1";
		assertThat(x, is(Plural.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w")));
		final Plural<String> y = Plural.copy(strings);
		strings[2] = "2";
		assertThat(y, is(Plural.of("u","1","w")));
		// @formatter:on
	}
	
	@Test
	public void shouldIterateOverAllElements()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final StringBuilder sb = new StringBuilder();
		for(final String s: x) sb.append(s);
		assertThat(sb.toString(), is("abc"));
	}
	
	@Test
	public void shouldGenerateIndependentStreams()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Stream<String> s1 = x.stream();
		final Stream<String> s2 = x.stream();
		assertThat(s1.collect(joining("")), is("abc"));
		assertThat(s2.collect(joining(",")), is("a,b,c"));
	}
	
	@Test
	public void shouldKeepPlural()
	{
		final Plural<String> x = Plural.of("a", "b");
		final Plural<String> y = Plural.copy(x);
		assertThat(y, is(sameInstance(x)));
	}
	
	@Test
	public void shouldCopyCollection()
	{
		final List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		final Plural<String> x = Plural.copy(list);
		assertThat(x.asList(), is(equalTo(list)));
	}
	
	@Test
	public void shouldCopyEmptyCollection()
	{
		final List<String> list = Collections.emptyList();
		final Plural<String> x = Plural.copy(list);
		assertTrue(x.asList().isEmpty());
	}
	
	@Test
	public void shouldCopyIterableAsCollection()
	{
		final Iterable<String> iterable = Arrays.asList("a", "b");
		final Plural<String> x = Plural.copy(iterable);
		assertThat(x.asList(), is(equalTo(iterable)));
	}
	
	@Test
	public void shouldBeImmutableAfterCopyConstructor()
	{
		final String[] strings = {"a", "b"};
		final Iterable<String> list = Arrays.asList(strings);
		final Plural<String> x = Plural.copy(list);
		strings[1] = "c";
		assertThat(x, is(Plural.of("a", "b")));
	}
	
	@Test
	public void shouldCopyNonCollectionIterable()
	{
		final Iterable<Character> iterable = () -> new Iterator<Character>()
		{
			int i = 0;
			final String abc = "abc";
			@Override public boolean hasNext() { return i < abc.length(); }
			@Override public Character next() { return abc.charAt(i++); }
		};
		final Plural<Character> x = Plural.copy(iterable);
		assertThat(x.asList().size(), is(3));
		final StringBuilder sb = new StringBuilder();
		x.forEach(sb::append);
		assertThat(sb.toString(), is("abc"));
	}
	
	@Test
	public void shouldCopyEmptyIterable()
	{
		final Iterable<String> emptyIterable = () -> new Iterator<String>()
		{
			@Override public boolean hasNext() { return false; }
			@Override public String next() { throw new NoSuchElementException(); }
		};
		final Plural<String> x = Plural.copy(emptyIterable);
		assertTrue(x.asList().isEmpty());
	}
	
	@Test
	public void shouldNeverEqualNull()
	{
		final Plural<String> x = Plural.of("a");
		assertFalse(x.equals(null));
	}
	
	@Test
	public void shouldNeverEqualAnotherClass()
	{
		final List<String> list = Arrays.asList("a", "b", "c");
		final Plural<String> x = Plural.of("a", "b", "c");
		assertFalse(x.equals(list));
	}

	@Test
	public void shouldBeEmpty()
	{
		final Plural<String> x = Plural.empty();
		assertTrue(x.isEmpty());
		assertThat(x.size(), is(0));
	}
	
	@Test
	public void shouldBeNonEmpty()
	{
		final Plural<String> x = Plural.of("a");
		assertFalse(x.isEmpty());
		assertThat(x.size(), is(1));
	}
	
	@Test
	public void shouldContainElements()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		assertTrue(x.contains("a"));
		assertTrue(x.contains("c"));
		assertTrue(x.containsAll(Arrays.asList("a", "b")));
		assertTrue(x.containsAll("b", "c"));
	}
	
	@Test
	public void shouldNotContainElements()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		assertFalse(x.contains("d"));
		assertFalse(x.containsAll(Arrays.asList("a", "d")));
		assertFalse(x.containsAll("b", "e"));
	}
	
	@Test
	public void shouldExportUniqueElementsInOrder()
	{
		final Plural<String> x = Plural.of("a", "a", "b", "a", "c", "b", "a");
		final Set<String> set = x.asSet();
		assertTrue(set.containsAll(Arrays.asList("a", "b", "c")));
		assertThat(set.size(), is(3));
		assertThat(set.stream().collect(joining("")), is("abc"));
	}
	
	@Test
	public void shouldCollectToPlural()
	{
		final List<String> list = Arrays.asList("a", "b", "c");
		final Plural<String> x = list.stream().collect(toPlural());
		assertThat(x.size(), is(3));
		assertTrue(x.containsAll("a", "b", "c"));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapIndexTooLow()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		x.get(-1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapIndexTooHigh()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		x.get(3);
	}
	
	@Test
	public void shouldGetElementAtValidIndex()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		assertThat(x.get(0), is("a"));
		assertThat(x.get(1), is("b"));
		assertThat(x.get(2), is("c"));
	}
	
	@Test
	public void shouldMutateMultiplePluralsIndependently()
	{
		final Plural<String> x = Plural.of("a", "b");
		final Plural<String> y = x.append("c");
		final Plural<String> z = x.append("d");
		assertThat(z, is(Plural.of("a", "b", "d")));
		assertThat(y, is(Plural.of("a", "b", "c")));
	}
	
	@Test
	public void shouldNotStackOverflowWhenAppendingManyElements()
	{
		final int stackSize = 100_000;
		Plural<Integer> x = Plural.empty();
		for(int i = 0; i < stackSize; i++) x = x.append(i);
		assertThat(x.size(), is(stackSize));
	}
	
	@Test
	public void shouldAddElementsToEnd()
	{
		final Plural<String> x = Plural.of("a", "b");
		final Plural<String> y = x.append("c").append("d").append("e");
		assertThat(y, is(Plural.of("a", "b", "c", "d", "e")));
	}
	
	@Test
	public void shouldRemoveFirstMatchingElement()
	{
		final Plural<String> x = Plural.of("a", "b", "a", "b", "c");
		final Plural<String> y = x.delete("b");
		assertThat(y, is(Plural.of("a", "a", "b", "c")));
	}
	
	@Test
	public void shouldRemoveMatchingElements()
	{
		final Plural<String> x = Plural.of("apple", "banana", "blueberry", "coconut", "date");
		final Plural<String> y = x.deleteIf(s -> s.startsWith("b"));
		assertThat(y, is(Plural.of("apple", "coconut", "date")));
	}
	
	@Test
	public void shouldKeepMatchingElements()
	{
		final Plural<String> x = Plural.of("apple", "banana", "blueberry", "coconut", "date");
		final Plural<String> y = x.filter(s -> s.startsWith("b"));
		assertThat(y, is(Plural.of("banana", "blueberry")));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapFromIndexTooLow()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		x.fromIndex(-1);
	}
	
	@Test
	public void shouldNormaliseWhenFromIndexTooHigh()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		x.fromIndex(4);
	}
	
	@Test
	public void shouldGetLastXElements()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.fromIndex(1);
		assertThat(y, is(Plural.of("b", "c")));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapTruncateIndexTooLow()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.truncate(-1);
		assertThat(y, is(Plural.empty()));
	}
	
	@Test
	public void shouldNormaliseWhenTruncateIndexTooHigh()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		x.truncate(4);
	}
	
	@Test
	public void shouldGetFirstXElements()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.truncate(2);
		assertThat(y, is(Plural.of("a", "b")));
	}
	
	@Test
	public void shouldExtractZeroLengthPlurals()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		assertThat(x.fromIndex(3), is(Plural.empty()));
		assertThat(x.truncate(0), is(Plural.empty()));
	}
	
	@Test
	public void shouldConvertSingleElements()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		final Plural<String> y = x.map(String::toUpperCase);
		assertThat(y, is(Plural.of("APPLE", "BANANA", "COCONUT")));
	}
	
	@Test
	public void shouldConvertToEmpty()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		final Plural<String> y = x.flatMap($ -> Plural.empty());
		assertThat(y, is(Plural.empty()));
	}
	
	@Test
	public void shouldConvertToSameSize()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		final Plural<Integer> y = x.flatMap($ -> Plural.of($.length()));
		assertThat(y, is(Plural.of(5, 6, 7)));
	}
	
	@Test
	public void shouldConvertToLarger()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		final Plural<String> y = x.flatMap($ -> Plural.of($ + "1", $ + "2"));
		assertThat(y, is(Plural.of("apple1", "apple2", "banana1", "banana2", "coconut1", "coconut2")));
	}
	
	@Test
	public void shouldCombineAllElementsToResult()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		final int length = x.reduce(0, ($, element) -> $ + element.length());
		assertThat(length, is(18));
	}
	
	@Test
	public void shouldZipToSizeOfShorterPlural()
	{
		final Plural<String> x = Plural.of("a", "b", "c", "d");
		final Plural<String> y = Plural.of("apple", "banana", "coconut");
		assertThat(x.zip(y), is(Plural.of(
			Pair.of("a", "apple"),
			Pair.of("b", "banana"),
			Pair.of("c", "coconut")
		)));
		assertThat(y.zip(x), is(Plural.of(
			Pair.of("apple", "a"),
			Pair.of("banana", "b"),
			Pair.of("coconut", "c")
		)));
	}
	
	@Test
	public void shouldJoinWithDelimiter()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		assertThat(x.join(""), is("abc"));
		assertThat(x.join("-"), is("a-b-c"));
	}
	
	@Test
	public void shouldAddAllFromPlural()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = Plural.of("d", "e", "f");
		final Plural<String> xy = x.append(y);
		assertThat(xy.join(""), is("abcdef"));
	}
	
	@Test
	public void shouldAddAllFromCollection()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final List<String> y = Arrays.asList("d", "e", "f");
		final Plural xy = x.append(y);
		assertThat(xy.join(""), is("abcdef"));
	}
	
	@Test
	public void shouldRetainOnlyUniqueElements()
	{
		final Plural<String> x = Plural.of("a", "b", "a", "b", "c");
		assertThat(x.distinct().join(""), is("abc"));
	}
	
	@Test
	public void shouldKeepAlreadyDistinctPlural()
	{
		final Plural<String> x = Plural.of("a", "b", "a", "b", "c");
		final Plural<String> y = x.distinct();
		final Plural<String> z = y.distinct();
		assertThat(z, is(sameInstance(y)));
	}
	
	public void shouldReturnFirstElement()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Optional<String> first = x.getFirst();
		assertTrue(first.isPresent());
		assertThat(first.get(), is("a"));
	}
	
	@Test
	public void shouldReturnLastElement()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Optional<String> last = x.getLast();
		assertTrue(last.isPresent());
		assertThat(last.get(), is("c"));
	}
	
	@Test
	public void shouldReturnEmptyOptionalsFromEmptyPlural()
	{
		final Plural<String> x = Plural.empty();
		assertFalse(x.getFirst().isPresent());
		assertFalse(x.getLast().isPresent());
	}
	
	@Test
	public void shouldRemoveFirstElement()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.deleteFirst();
		assertThat(y, is(Plural.of("b", "c")));
	}
	
	@Test
	public void shouldRemoveLastElement()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.deleteLast();
		assertThat(y, is(Plural.of("a", "b")));
	}
	
	@Test
	public void shouldRemoveNothingWithoutError()
	{
		final Plural<String> x = Plural.empty();
		assertThat(x.deleteFirst(), is(x));
		assertThat(x.deleteLast(), is(x));
	}
	
	@Test
	public void shouldRequireAllToMatch()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		assertTrue(x.allMatch(s -> s.length() > 3));
		assertFalse(x.allMatch(s -> s.startsWith("b")));
	}
	
	@Test
	public void shouldRequireAtLeastOneToMatch()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		assertTrue(x.anyMatch(s -> s.startsWith("b")));
		assertFalse(x.anyMatch(String::isEmpty));
	}
	
	@Test
	public void shouldRequireAllToNotMatch()
	{
		final Plural<String> x = Plural.of("apple", "banana", "coconut");
		assertTrue(x.noneMatch(String::isEmpty));
		assertFalse(x.noneMatch(s -> s.startsWith("b")));
	}
	
	@Test
	public void shouldFindMaxUnlessEmpty()
	{
		final Plural<String> x = Plural.empty();
		final Plural<String> y = Plural.of("apple", "banana", "coconut");
		assertFalse(x.maxBy(String::compareTo).isPresent());
		assertThat(y.maxBy(String::compareTo).get(), is("coconut"));
	}
	
	@Test
	public void shouldFindMinUnlessEmpty()
	{
		final Plural<String> x = Plural.empty();
		final Plural<String> y = Plural.of("apple", "banana", "coconut");
		assertFalse(x.minBy(String::compareTo).isPresent());
		assertThat(y.minBy(String::compareTo).get(), is("apple"));
	}
	
	@Test
	public void shouldSortByComparator()
	{
		final Plural<String> x = Plural.of("acrobatics", "coconut", "banana");
		final Comparator<String> comparator = Comparator.comparingInt(s -> s.lastIndexOf('a'));
		assertThat(x.sortedBy(comparator), is(Plural.of("coconut", "acrobatics", "banana")));
	}
	
	@Test
	public void shouldSortByProperty()
	{
		final Plural<String> x = Plural.of("acrobatics", "coconut", "banana");
		assertThat(x.sortedBy(String::length), is(Plural.of("banana", "coconut", "acrobatics")));
	}
	
	@Test
	public void shouldSortByCompareTo()
	{
		final Plural<String> x = Plural.of("acrobatics", "coconut", "banana");
		assertThat(x.sorted(), is(Plural.of("acrobatics", "banana", "coconut")));
	}
	
	@Test
	public void shouldSortByToString()
	{
		class Name extends Single<String, Name> { Name(final String raw) { super(raw, Name::new); } }
		final Plural<Name> x = Plural.of(new Name("Will"), new Name("Hains"));
		assertThat(x.sorted(), is(Plural.of(new Name("Hains"), new Name("Will"))));
	}
	
	@Test
	public void shouldReturnEmptyOnlyIfElementIsNull()
	{
		final Plural<String> x = Plural.ofNullable("apple");
		final Plural<String> y = Plural.ofNullable(null);
		assertFalse(x.isEmpty());
		assertTrue(y.isEmpty());
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldTrapInsertIndexTooLow()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		x.insert("d", -1);
	}
	
	@Test
	public void shouldNormaliseWhenInsertIndexTooHigh()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.insert("d", 4);
		assertThat(y, is(Plural.of("a", "b", "c", "d")));
	}
	
	@Test
	public void shouldInsertElementAtIndex()
	{
		final Plural<String> x = Plural.of("a", "b", "c");
		final Plural<String> y = x.insert("a.5", 1);
		assertThat(y, is(Plural.of("a", "a.5", "b", "c")));
	}
}
