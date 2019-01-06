# Single

Wrap naked data in your own value types.

## Declaration

```java
public abstract @Value class Single<Raw, This extends Single<Raw, This>>
```

```java
public abstract @Value class SingleInt<This extends SingleInt<This>>
    implements SingleNumber<This>
```

```java
public abstract @Value class SingleLong<This extends SingleLong<This>>
    implements SingleNumber<This>
```

```java
public abstract @Value class SingleDouble<This extends SingleDouble<This>>
    implements SingleNumber<This>
```

```java
public abstract @Value class SingleDecimal<This extends SingleDecimal<This>>
    extends Single<BigDecimal, This>
    implements SingleNumber<This>
```

```java
public abstract @Value class SingleString<This extends SingleString<This>>
    extends Single<String, This>
    implements SingleComparable<This>, CharSequence
```

## Overview

`Single`, and its sibling classes, allow you to wrap naked data values in your own custom value types.

- Use `SingleString` for `String`-based values
- Use `SingleInt` for `int`-based values
- Use `SingleLong` for `long`-based values
- Use `SingleDouble` for `double`-based values
- Use `SingleDecimal` for `BigDecimal`-based values
- Use `Single` for values based on other types

Wrapping naked values in your own custom value classes has many benefits:

- **Naming.** You can give the class a name that describes the kind of information it contains. You can name methods, and method arguments. Well-named classes, methods, and arguments reduce the need for documentation and Javadoc comments, because you can make it obvious.
- **Custom API.** You can expose only methods that make sense in the context of your app. For example, `.toLowerCase()` wouldn't make sense on a string-based value class `PhoneNumber`, so we wouldn't include it.
- **Type Safety.** Most apps have an abundance of `String`s and `int`s all over the place. The compiler doesn't know anything about what data they represent, so you can easily mistake a customer name for a phone number, or whatever. Giving each kind of value its own class allows the compiler to do that checking for you.
- **Code Clarity.** It's much easier to read code that deals with its data in terms of `ModelNumber`/`ProductCode`/`HostName`/`PhoneNumber` than code that uses `String`/`String`/`String`/`String`.
- **Validation and Normalisation.** You can check and massage the raw data at construction time, to make sure it is always valid.
- **Operations.** You can add custom operations to your data, that make sense in the context of your app. In fact, Purity strongly encourages moving *all* of your app's logic to value classes.
- **Testing.** Since value types are free of dependencies and side effects ([no I/O; no concurrency; no mutation](value-semantics.md)), they are extremely pleasant to test. Because they are trivial to create, you never need to stub them. Because they are immutable, you never need to mock them.
- **Code Reuse.** Since value types have no dependencies, they are generally very portable. You can freely share them widely across your app, and with other codebases, and expect them to work predictably.

## Creating a Value Class

The simplest use of `Single*` base classes is:

```java
public final @Value class FamilyName extends SingleString<FamilyName>
{
	public FamilyName(String name) { super(name, FamilyName::new); }
}
```

1. Declare your class `final`.
2. Add the `@Value` annotation.
3. Give it a good name.
4. Extend the appropriate `Single*` base class.
5. Repeat your class name in the generic parameter.
6. Add a constuctor with a single argument of the raw type.
7. Call the super constructor with the raw value, and a method reference to your constructor (`YourClass::new`).

### Wrap a Mutable Type to Make it Immutable

If you are using a mutable type in an immutable way, you can use `Single` to wrap it and enforce that immutability.

```java
public final @Value class TextSupport extends Single<EnumSet<TextOption>>
{
	private static final Rule<EnumSet<TextOption>> defensiveCopy = EnumSet::copyOf;
	public TextSupport(EnumSet<TextOption> options)
	{
		super(options, TextSupport::new, defensiveCopy);
	}
}
```

With mutable `Raw` types, be careful to:

- Make a defensive copy in the constructor, to prevent mutations after the constructor returns. You can use a `Rule` to implement this.
- Don't leak a reference to your `raw` object outside the class.
- Obviously, don't mutate the `raw` object directly in your class's methods.

## Validation and Normalisation

Purity strongly encourages *normalising* and *validating* data in your value class constructors. Doing so pushes data validation/normalisation out to the edges of your app, at their points of input. That means, wherever you see a `ModelNumber` in your code, you *know* it is definitely valid data. In the core logic of your app, you don't ever have to deal with the possibility of invalid data.

Adding validation/normalisation rules is easy. Just declare a `Rule` constant, and pass that to the `super` constructor.

```java
public final @Value class ModelNumber extends SingleString<ModelNumber>
{
	private static final Rule rules = Rule.rules(
		min(7), max(13),
		validPattern("[AO]\d\d-\d{3,9}"));
	public ModelNumber(final String model) { super(model, ModelNumber::new, rules); }
}
```

- Use `IntRule` for rules in `SingleInt`-based classes
- Use `LongRule` for rules in `SingleLong`-based classes
- Use `DoubleRule` for rules in `SingleDouble`-based classes
- Use `Rule<Decimal>` for rules in `SingleDecimal`-based classes
- Use `Rule<String>` for rules in `SingleString`-based classes
- Use `Rule<...>` for custom rules in other `Single<...>`-based classes

### Built-in Rules and Rule Factories

Purity provides several built-in constants and static factory methods to help you build your rules.

#### `Rule.rules(...)`

Use this method to chain together multiple rules into a composite rule, to be passed to the super constructor. It is 
strongly recommended to declare your composite rule as a `static final` constant.

The rules are executed in the same order they are provided to the `rules(...)` function.

#### `min(minValue)`

- Rule type: validation
- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Set the lower bound (inclusive) of the raw value. When the raw value is not greater than or equal to `minValue`, the rule throws an `IllegalArgumentException`.

#### `max(maxValue)`

- Rule type: validation
- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Set the upper bound (inclusive) of the raw value. When the raw value is not less than or equal to `maxValue`, the rule throws an `IllegalArgumentException`.

#### `greaterThan(lowerBound)`

- Rule type: validation
- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Set the lower bound (exclusive) of the raw value. When the raw value is not greater than `lowerBound`, the rule throws an `IllegalArgumentException`.

#### `lessThan(upperBound)`

- Rule type: validation
- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Set the upper bound (exclusive) of the raw value. When the raw value is not less than `upperBound`, the rule throws an `IllegalArgumentException`.

#### `floor(minValue)`

- Rule type: normalisation
- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Adjust the raw value to `minValue` if it is less than `minValue`.

#### `ceiling(maxValue)`

- Rule type: normalisation
- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Adjust the raw value to `maxValue` if it is greater than `maxValue`.

#### `realNumber`

- Rule type: validation
- Availability: `SingleDouble`

When the raw value is positive/negative infinity or not a number, the rule throws an `IllegalArgumentException`.

#### `trimWhitespace`

- Rule type: normalisation
- Availability: `SingleString`

Removes whitespace characters from the beginning and end of the raw value.

#### `uppercase`

- Rule type: normalisation
- Availability: `SingleString`

Converts the raw value to all-uppercase.

#### `lowercase`

- Rule type: normalisation
- Availability: `SingleString`

Converts the raw value to all-lowercase.

#### `validCharacters(allowedCharacters)`

- Rule type: validation
- Availability: `SingleString`

Sets the allowed set of characters for the raw value. If the raw value contains characters not found in `allowedCharacters`, the rule throws an `IllegalArgumentException`.

Purity provides constants `letters` and `numbers` to help you build the `allowedCharacters` string.

```java
allowedCharacters(letters + numbers + "-._")
```

#### `validPattern(regExPattern)`

- Rule type: validation
- Availability: `SingleString`

Sets the allowed format of the raw value. If the raw value does not match `regExPattern`, the rule throws an `IllegalArgumentException`.

#### `minLength(length)`

- Rule type: validation
- Availability: `SingleString`

Sets the minimum character count of the raw value. If the raw value is not at least `length` characters long, the rule throws an `IllegalArgumentException`.

#### `maxLength(length)`

- Rule type: validation
- Availability: `SingleString`

Sets the maximum character count of the raw value. If the raw value is more than `length` characters long, the rule throws an `IllegalArgumentException`.

### Add Your Own Custom Rules

When you need more than the built-in rules above, you can easily create your own rules. A rule is just a function that takes a raw value and either returns a new raw value in its place (a “normalisation rule”), or throws an exception (a “validation rule”).

```java
// Normalisation: remove space characters from the string
private static final Rule<String> removeSpaces = raw -> raw.replace(" ", "");

// Validation: throw exception when raw string begins with an underscore character
private static final Rule<String> noLeadingUnderscore = raw ->
{
	if(raw.startsWith("_")) throw new IllegalArgumentException("no leading underscores!");
}
```

## Add Custom Operations

Add methods to your value class related to the data it represents. Methods on value classes are easy to test, which means they tend to have fewer bugs. That’s why Purity encourages moving as much of your app’s logic as possible to methods of value classes.

The `Single` base classes expose the raw value as a protected property `raw`. Use it to implement methods on your class.

```java
public final @Value class ModelNumber extends SingleString<ModelNumber>
{
	private static final Rule rules = Rule.rules(min(7), max(13), validPattern("[AO]\d\d-\d{3,9}"));
	public ModelNumber(final String model) { super(model, ModelNumber::new, rules); }

	public ProductCode getProductCode() { return new ProductCode(raw.substring(0, 3)); }
	public ProductVariant getProductVariant() { return new ProductVariant(raw.substring(4)); }
}
```

## Built-in Operations

TODO...

TODO: SingleComparable and SingleNumber

## `Object` and `Comparable`

TODO: equals, hashCode, toString, compareTo
