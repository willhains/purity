package com.willhains.purity;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that a method argument is not retained (stored in a field) by the method, or by any objects it calls,
 * except those that are returned to the caller.
 * <p>
 * Formally, the annotated argument:
 * <ol>
 * <li>Is not stored in a field of the method's class.</li>
 * <li>Is not stored directly in a field of another class.</li>
 * <li>Is not passed to a constructor or method as a {@link Retained} argument.</li>
 * <li>Is returned as the method's return value; or, if passed to a constructor or method as a {@link Returned}
 *     argument, the return value of that method/constructor satisfies these conditions.</li>
 * </ol>
 * <p>
 * Constructor arguments (for both {@link Pure} and non-{@link Pure} types) are {@link Returned} by default.
 *
 * @author willhains
 * @see Retained
 * @see Released
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 */
@Deprecated
@Retention(RetentionPolicy.CLASS)
@Target(PARAMETER)
public @interface Returned { }
