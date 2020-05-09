[![GitHub Release][release-badge]][release] 
[![Build Status][build-badge]][build]
[![Follow @willhains][twitter-badge]][twitter] 

[release-badge]:  https://img.shields.io/github/v/release/willhains/purity?include_prereleases
[build-badge]:    https://travis-ci.org/willhains/purity.svg?branch=master
[twitter-badge]:  https://img.shields.io/twitter/follow/willhains

[release]:  https://github.com/willhains/purity/releases
[build]:    https://travis-ci.org/willhains/purity
[twitter]:  https://twitter.com/intent/follow?screen_name=willhains

# Purity

Build robust, *value-oriented* applications in Java.

## Motivation

You are here because...

- You have tasted the sweet elixir of [value semantics][values], and you want it in Java, without an endless sea of boilerplate.
- You have been bitten by the evils of [Stringly-typed][stringly] code one too many times.
- Pulling all-nighters to troubleshoot bugs was fun when you were a fresh young programmer, but you're a grown-up now, and you just want the code to work.
- You've heard promises of code reuse for years, but have rarely seen it actually happen.
- You expect your app will grow in size and complexity, and you don't want to have to continually rewrite everything to avoid a [spaghetti mess][spaghetti].
- You believe in the virtues of unit testing, but somehow it always feels like a frustrating chore.
- Performance is important to you, but code correctness and clarity are even more important in the long run.

[stringly]: http://wiki.c2.com/?StringlyTyped
[spaghetti]: https://en.wikipedia.org/wiki/Spaghetti_code
[values]: docs/value-semantics.md

## Value Wrapping

Purity provides [a set of `Single*` base types][single] to wrap single values. Use these to encapsulate all the leaf values in types of your own, so that they may have sensible names and APIs that fit into the conceptual domain of your application.

[single]: docs/Single.md

For example, `String`s are *everywhere*, but they are not much more typesafe than byte arrays. All `String`s are potentially invalid, untrustworthy, raw data. Wrap them to make them pure, safe, clear, and dependable.

```java
public final @Pure class HostName extends SingleString<HostName>
{
	public HostName(String hostName) { super(hostName, HostName::new); }
}
```

Purity's value-wrapping base types are:

- `SingleString`, for `String`-based values
- `SingleInt`, for `int`-based values
- `SingleLong`, for `long`-based values
- `SingleDouble`, for `double`-based values
- `SingleDecimal`, for `BigDecimal`-based values
- `Single`, for values based on other types

Each base type includes a wide range of useful functions, including normalisation and validation rules you can apply to ensure *every* instance of your value type is *always* valid. Just declare constants of type `Rule`, and Purity will automatically apply them when creating new instances.

```java
public final @Pure class HostName extends SingleString<HostName>
{
	private static final Rule TRIM = trimWhitespace;
    private static final Rule FORMAT = validCharacters(letters + numbers + "-._");
    private static final Rule LENGTH = Rule.combine(minLength(1), maxLength(255));

	public HostName(String hostName) { super(hostName, HostName::new); }
}
```

Validating raw data in the constructors of `@Pure` types pushes errors to the sources of input, which is the best place to handle such errors. For example, you can display an error to the user upon manual input. When all value types contain *only* valid data, your app's core logic is cleaner.

See [the `Single` docs][single] for more information.

## Annotations

There are four categories of types in a well-written, value-oriented application. Purity introduces four annotations to help document and identify them.

1. `@Pure` = a pure, immutable [value type][values]
2. `@Mutable` = holds data that may change
3. `@IO` = connects to an external input and/or output
4. `@Barrier` = provides concurrency protection

Generally, a class should not belong to more than one of these categories. Specifically, a `@Pure` class must not belong to any other categories, as that would violate the [definition of "value"][values].

Purity introduces three more annotations for arguments and return values, to help keep track of non-`@Pure` object 
ownership.

5. `@Retained` = the object is stored inside the callee object, or in another object it calls.
6. `@Returned` = the object is stored only inside the return value.
7. `@Released` = the object is not stored by the callee object, nor by any other objects it calls.

At a high level, refactoring a codebase to Purity is done in four steps:

1. **Wrap** *everything* that isn't yours in a class of your own. Give each a name that makes sense in the context of your application. Add only the methods you need, and give the methods names and arguments that make sense for your app.
2. **Classify** every type according to the four categories above, adding the Purity annotations to document their purpose. A class can belong to multiple non-`@Pure` categories, but it's better to separate them cleanly if you can.
3. **Move** *all conditional branches* to `@Pure` types, and write tests for them. Non-`@Pure` types should be as dumb and simple as possible, since they are inherently difficult to test, and are therefore the source of most bugs.
4. **Rearrange** the ownership graphs of non-`@Pure` types to minimise or eliminate singletons. Since singletons are globally-accessible, they must be thread-safe `@Barrier`s, and you want as few of those as possible.

## Development Status

Purity is currently is in an early development stage, but is based on a design that is already used in mission-critical systems of a large financial institution. (No guarantees of safety or quality are made or implied. Use at your own risk.) Comments and contributions are welcome and encouraged. Public APIs are <s>un</s>likely to change<s>, but may do so</s> without notice.

## Contribution

1. Fork
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
