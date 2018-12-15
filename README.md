[![GitHub Release][release-badge]][release] 
[![Build Status][build-badge]][build]
[![codecov][coverage-badge]][coverage]
[![Follow @willhains][twitter-badge]][twitter] 

[release-badge]:  https://img.shields.io/github/release/willhains/purity.svg
[build-badge]:    https://travis-ci.org/willhains/purity.svg?branch=master
[coverage-badge]: https://codecov.io/gh/willhains/purity/branch/master/graph/badge.svg
[twitter-badge]:  https://img.shields.io/twitter/follow/willhains.svg?style=social

[release]:  https://github.com/willhains/purity/releases
[build]:    https://travis-ci.org/willhains/purity
[coverage]: https://codecov.io/gh/willhains/purity
[twitter]:  https://twitter.com/intent/follow?screen_name=willhains

# Purity

Build robust, *value-based* applications in Java.

## Motivation

You are here because...

- You have tasted the sweet elixir of [value semantics][values], and you want it in Java, without an endless sea of boilerplate.
- You have been bitten by the evils of [Stringly-typed][stringly] code one too many times.
- Pulling all-nighters to troubleshoot bugs was fun when you were a fresh young programmer, but you're a grown-up now, and you just want the code to work.
- You've heard promises of code reuse for years, but never seen it actually happen.
- You expect your app will grow in size and complexity, and you don't want to have to continually rewrite everything to avoid a [spaghetti mess][spaghetti].
- You believe in the virtues of unit testing, but somehow it always feels like a frustrating chore.
- Performance is important to you, but code correctness and clarity are even more important in the long run.

[stringly]: http://wiki.c2.com/?StringlyTyped
[spaghetti]: https://en.wikipedia.org/wiki/Spaghetti_code
[values]: docs/value-semantics.md

## Development Status

Purity is currently is in an early development stage, but is based on a design that is already used in mission-critical systems of a large financial institution. (No guarantees of safety or quality are made or implied. Use at your own risk.) Comments and contributions are welcome and encouraged. Public APIs are unlikely to change, but may do so without notice.

## Contribution

1. Fork
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
