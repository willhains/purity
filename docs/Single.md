# Single

Wrap naked data in your own value types.

## Declaration

```java
public abstract @Pure class Single<Raw, This extends Single<Raw, This>>
```

```java
public abstract @Pure class SingleInt<This extends SingleInt<This>>
    implements SingleNumber<This>
```

```java
public abstract @Pure class SingleLong<This extends SingleLong<This>>
    implements SingleNumber<This>
```

```java
public abstract @Pure class SingleDouble<This extends SingleDouble<This>>
    implements SingleNumber<This>
```

```java
public abstract @Pure class SingleDecimal<This extends SingleDecimal<This>>
    extends Single<BigDecimal, This>
    implements SingleNumber<This>
```

```java
public abstract @Pure class SingleString<This extends SingleString<This>>
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

- **Custom API.** You can expose only methods that make sense in the context of your app. For example, `.toLowerCase()` wouldn't make sense on a string-based value class `PhoneNumber`, so we wouldn't include it; but `.getAreaCode()` would make sense, so we can add it.

- **Type Safety.** Most apps have an abundance of `String`s and `int`s all over the place. The compiler doesn't know anything about what data they represent, so you can easily mistake a customer name for a phone number, or whatever. Giving each kind of value its own class allows the compiler to do that checking for you.

- **Code Clarity.** It's much easier to read code that deals with its data in terms of `ModelNumber`/`ProductCode`/`HostName`/`PhoneNumber` than code that uses `String`/`String`/`String`/`String`.

- **Validation and Normalisation.** You can check and massage the raw data at construction time, to make sure it is always valid.

- **Operations.** You can add custom operations to your data, that make sense in the context of your app. In fact, Purity strongly encourages moving *all* of your app's logic to value classes.

- **Testing.** Since value types are free of dependencies and side effects ([no I/O; no concurrency; no mutation](value-semantics.md)), they are extremely pleasant to test. Because they are trivial to create, you never need to stub them. Because they are immutable, you never need to mock them.

- **Code Reuse.** Since value types have no dependencies, they are generally very portable. You can freely share them widely across your app, and with other codebases, and expect them to work predictably.

## Creating a Value Class

The simplest use of `Single*` base classes is:

```java
public final @Pure class FamilyName extends SingleString<FamilyName>
{
	public FamilyName(String name) { super(name, FamilyName::new); }
}
```

1. Declare your class `final`.
2. Add the `@Pure` annotation.
3. Give it a good name.
4. Extend the appropriate `Single*` base class.
5. Repeat your class name in the generic parameter.
6. Add a constuctor with a single argument of the raw type.
7. Call the super constructor with the raw value, and a method reference to your constructor (`YourClass::new`).

### Wrap a Mutable Type to Make it Immutable

If you are using a mutable type in an immutable way, you can use `Single` to wrap it and enforce that immutability.

```java
public final @Pure class TextSupport extends Single<EnumSet<TextOption>>
{
	private static final Rule<EnumSet<TextOption>> DEFENSIVE_COPY = EnumSet::copyOf;
	public TextSupport(EnumSet<TextOption> options) { super(options, TextSupport::new); }
	@Override public EnumSet<TextOption> raw() { return defensiveCopy.apply(raw); }
}
```

With mutable `Raw` types, be careful to:

- Make a defensive copy in the constructor, to prevent mutations after the constructor returns. You can use a `Rule` to implement this.
- Override `raw()` to also return a defensive copy of the `raw` property value.
- Don't leak a reference to your `raw` object outside the class.
- Obviously, don't mutate the `raw` object directly in your class's methods.

## Validation and Normalisation

Purity strongly encourages *normalising* and *validating* data in your value class constructors. Doing so pushes data validation/normalisation out to the edges of your app, at their points of input. That means, wherever you see a `ModelNumber` in your code, you *know* it is definitely valid data. In the core logic of your app, you don't ever have to deal with the possibility of invalid data.

Adding validation/normalisation rules is easy. Just declare some `Rule` constants in your class. Purity will find them, and automatically apply it to raw values passed to the super constructor.

```java
public final @Pure class ModelNumber extends SingleString<ModelNumber>
{
	private static final Rule LENGTH = Rule.all(minLength(7), maxLength(13));
    private static final Rule PATTERN = validPattern("[AO]\\d\\d-\\d+");
	public ModelNumber(final String model) { super(model, ModelNumber::new); }
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

#### `Rule.combine(...)`

Use this method to chain together multiple rules into a composite rule, to be passed to the super constructor. It is strongly recommended to declare your composite rule as a `static final` constant.

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

#### `intern`

- Rule type: normalisation
- Availability: `SingleString`

Interns the raw string value, reducing memory usage for oft-repeated string values.

### Add Your Own Custom Rules

When you need more than the built-in rules above, you can easily create your own rules. A rule is just a function that takes a raw value and either returns a new raw value in its place (a “normalisation rule”), or throws an exception (a “validation rule”).

```java
// Normalisation: remove space characters from the string
private static final Rule<String> removeSpaces = raw -> raw.replace(" ", "");

// Validation: throw exception when raw string begins with an underscore character
private static final Rule<String> noLeadingUnderscore =
	validUnless(raw -> raw.startsWith("_"), "no leading underscores!");
```

Purity provides validation rule factory methods, for building your own custom validations.

#### `validUnless(condition,errorMessage)`

- Rule type: validation
- Availability: `Rule`, `IntRule`, `LongRule`, `DoubleRule`

Throws an `IllegalArgumentException` with the specified `errorMessage` if `condition` evaluates to true.

#### `validUnless(condition,errorMessageFactory)`

- Rule type: validation
- Availability: `Rule`, `IntRule`, `LongRule`, `DoubleRule`

Throws an `IllegalArgumentException` and builds an error message with the specified `errorMessageFactory` if `condition` evaluates to true.

#### `validIf(condition,errorMessage)`

- Rule type: validation
- Availability: `Rule`, `IntRule`, `LongRule`, `DoubleRule`

Throws an `IllegalArgumentException` with the specified `errorMessage` if `condition` evaluates to false.

#### `validIf(condition,errorMessageFactory)`

- Rule type: validation
- Availability: `Rule`, `IntRule`, `LongRule`, `DoubleRule`

Throws an `IllegalArgumentException` and builds an error message with the specified `errorMessageFactory` if `condition` evaluates to false.

## Operations

Extend app-domain value classes with app-domain methods.

### Built-in Operations

Purity provides several built-in operations for common tasks with value types. Many of these methods return the same value type (`This`), re-wrapping the result of an operation on the raw value in the same value type. Note that if rules are applied in the constructor, they will be applied to the new raw value, so your values are always valid.

#### `raw()`

- Availability: `Single`, `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

Returns the raw value. Ideally, this should only be necessary when you need to pass the raw type to an API outside your control. Inside your own code, it's better to always use your custom wrapper value type.

> Note: `raw()` is not `final`, and should be overridden by your class if the raw type is not strictly immutable. See ["Wrap a Mutable Type to Make it Immutable"](#wrap-a-mutable-type-to-make-it-immutable) for details.

#### `equals(other)`, `hashCode()`, and `toString()`

- Availability: `Single`, `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

More-or-less passes through to the raw value.

#### `Single.equals(object1,object2)`, `Single.hashCode(object)`, and `Single.toString(object)`

Static functions to strictly implement the JDK `Object` good-citizen contract methods.

#### `is(condition)` and `isNot(condition)`

- Availability: `Single`, `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

Tests the raw value against the specified `condition` predicate, and returns a `boolean` result.

#### `filter(condition)`

- Availability: `Single`, `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

Tests the raw value against the specified `condition` predicate, and returns an `Optional` of itself, or empty if the condition is not satisfied.

#### `map(mapper)` and `flatMap(mapper)`

- Availability: `Single`, `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

Applies the `mapper` function to the raw value, and wraps the result in the same value type.

#### `asNumber()`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Returns a JDK `Number`-compatible representation of the raw value.

#### `compareToNumber(number)`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Numerically compares the raw value to `number`, returning an `int` result similar to `compareTo(that)`.

#### `isGreaterThan(number)`, `isGreaterThanOrEqualTo(number)`, `isLessThan(number)`, and `isLessThanOrEqualTo(number)`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Numerically compares the raw value to `number`, returning a `boolean` result.

#### `compareTo(that)`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

Implementation of the JDK `Comparable` interface.

#### `min(that)` and `max(that)`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`, `SingleString`

Compare the raw values of two value objects, and return the smaller/larger of the two.

#### `isZero()`, `isNonZero()`, `isPositive()`, and `isNegative()`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Numerically compares the raw value to zero.

#### `plus(number)`, `minus(number)`, `multiplyBy(number)`, and `divideBy(number)`

- Availability: `SingleInt`, `SingleLong`, `SingleDouble`, `SingleDecimal`

Perform arithmetic operations on the raw value, and wrap the result in the same value type. These operations are overloaded to accept either `Number`-compatible types or `String`s as the operands.

#### `round()`, `roundUp()`, and `roundDown()`

- Availability: `SingleDouble`, `SingleDecimal`

Round the raw value to the nearest whole number, and wrap the result in the same value type.

#### `roundToPrecision(decimals)`

- Availability: `SingleDecimal`

Round the raw value to the specified number of `decimals`.

#### `SingleNumber.$(x)`

A static function to convert anything into `BigDecimal`, via its string representation. Designed to be used with `static import SingleNumber`.

The name of this function is a reminder to programmers to never use floating-point types for money.

#### `length()`, `charAt(position)`, and `subSequence(start,end)`

- Availability: `SingleString`

Implementations of the JDK `CharSequence` interface.

#### `left(length)` and `right(length)`

- Availability: `SingleString`

Reduce the raw string value to a specified `length`, starting from the beginning/end.

#### `trim()`

- Availability: `SingleString`

Remove whitespace from the beginning/end of the raw string value.

#### `isEmpty()` and `isNotEmpty()`

- Availability: `SingleString`

Check whether the raw string value is zero-length.

#### `replaceRegex(regex,replacement)` and `replaceLiteral(literal,replacement)`

- Availability: `SingleString`

Replace all instances of the specified pattern or literal in the raw string value with a `replacement` string.

#### `split(regex,tokenConstructor)`

- Availability: `SingleString`

Split the raw string value by a delimiter pattern, and construct a `Plural` of the specified token type.

### Add Custom Operations

Turn your values into *smart values* by adding custom methods on your value class related to the data it represents. Methods on value classes are easy to test, which means they tend to have fewer bugs. That’s why Purity encourages moving as much of your app’s logic as possible to methods of value classes. As long as they don't mutate, do input/output, or mess with concurrency, go nuts!

The `Single` base classes expose the raw value as a protected property `raw`. Use it to implement methods on your class.

```java
public final @Pure class ModelNumber extends SingleString<ModelNumber>
{
	private static final Rule RULES = Rule.combine(min(7), max(13), validPattern("[AO]\\d\\d-\\d+"));
	public ModelNumber(final String model) { super(model, ModelNumber::new); }

	public ProductCode getProductCode() { return new ProductCode(raw.substring(0, 3)); }
	public ProductVariant getProductVariant() { return new ProductVariant(raw.substring(4)); }
}
```
