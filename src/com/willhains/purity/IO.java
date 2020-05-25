package com.willhains.purity;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Indicates that a type has controller semantics.
 *
 * Formally:
 * <ol>
 * <li>It is final, or all of its subtypes are guaranteed to have controller semantics.</li>
 * <li>It is at least shallowly immutable.</li>
 * <li>It might not have reliable {@link Object#equals equals}, {@link Object#hashCode hashCode},
 *     or {@link Object#toString toString} implementations.</li>
 * <li><b>It interacts with input and/or output.</b></li>
 * <li>It has no interaction with locks or concurrency mechanisms whatsoever.</li>
 * </ol>
 *
 * @see Pure
 * @see Mutable
 * @see Barrier
 * @author willhains
 */
@Retention(SOURCE)
@Target({TYPE, TYPE_PARAMETER})
public @interface IO {}
