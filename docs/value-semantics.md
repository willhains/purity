# Value Semantics

> The first fact about facts is that facts are **values**.
>
> — [Rich Hickey][hickey]

[hickey]: https://www.youtube.com/watch?v=-6BsiVyC1kM

What is a value? Think of an obvious example: the integer `3`.

```java
int x = 3;
int y = x;
x++;
System.out.println(x); // prints "4"
System.out.println(y); // prints "3"
```

> Variables mutate; **values** never change.
>
> — [Justin Spahr-Summers][jss]

[jss]: https://youtu.be/7AqXBuJOJkY?t=812

Here, we see the *variable* `x` can change, but not the *value* `3`. Mutating `x` does not mutate `y`, and it is impossible to mutate the value `3` itself.

In Java, the only true values are those of primitive types, because those are passed by value to methods. But even an object can be used as a value if it obeys *value semantics*. There are three key properties of a value-like type:

1. No mutation
2. No I/O
3. No concurrency

## Values Do Not (Observably) Mutate

This is the most important property of a value type. After the constructor returns, its state should never change. If you create two instances of a value type with the same constructor inputs, they should be indistinguishable (except by their instance ID). Immutable types are easy to use in multi-threaded apps because they require no synchronisation.

To be immutable, a class's internal state must also be immutable, or at least impossible to mutate after the constructor returns. In the following examples, `ClassA` is mutable, because it provides a method that mutates `list`; while `ClassB` is immutable, because it never mutates `list`. In this way, `ClassB` makes the mutable `List` *effectively immutable* by encapsulating it in an immutable wrapper.

```java
public final class ClassA
{
	private final List<String> list;
	public ClassA(final List<String> strings) { list = new ArrayList(strings); }
	public void add(String newElement) { list.add(newElement); }
}

public final class ClassB
{
	private final List<String> list;
	public ClassB(final List<String> strings) { list = new ArrayList(strings); }
	public ClassB add(String newElement)
	{
		final List<String> newList = new ArrayList<>(list);
		newList.add(newElement);
		return new ClassB(newList);
	}
}
```

It is possible (with great care) to allow some internal state to mutate in a *non-observable* way. For example, `java.lang.String` has a lazily-initiated hash code. Since the character data from which the hash is computed is immutable, even if a race condition occurs where two threads both compute the hash, they will compute the same value. Neither thread is able to observe the internal mutation, and so `String` is *effectively immutable*.

Other things to be careful of when making classes immutable:

1. Immutable classes must be final, to prevent subclasses from adding mutable state.
2. Take defensive copies of mutable constructor arguments, to prevent the caller from mutating the input after the constructor returns.

## Values Do Not Interact with I/O

The world is a mutable place. By interacting with it via I/O, you change the state of the world. Even reading from a file mutates the state of the file system I/O stack. I/O is a *side effect*, and values never produce side effects. This is the key reason that value types are a pleasure to test — no matter how many times you use them, the same method inputs *always* produce the same return value.

## Values Do Not (Need To) Interact with Concurrency Mechanisms

Locks are mutable. Queues are mutable. Threads are mutable. Therefore interacting with concurrency mechanisms breaks immutability. Anyway, since values never change after construction, there is never any *need* to use concurrency controls with values.

Since values are lock-free and avoid interaction with concurrency controls, they are fast. They don't impose any restrictions on the compiler or CPU about where they are stored or how often they are synced to main memory, so the runtime is free to optimise them any way it likes.