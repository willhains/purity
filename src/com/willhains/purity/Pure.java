package com.willhains.purity;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Indicates that a type has pure value semantics.
 * <p>
 * Formally:
 * <ol>
 * <li>It is final, or all of its subtypes are guaranteed to have pure value semantics.</li>
 * <li><b>It is strictly and deeply immutable</b>, and therefore inherently thread-safe.</li>
 * <li>It has correct {@link Object#equals equals}, {@link Object#hashCode hashCode},
 *     and {@link Object#toString toString} implementations.</li>
 * <li>It has no interaction with input/output whatsoever.</li>
 * <li>It has no interaction with locks or concurrency mechanisms whatsoever.</li>
 * </ol>
 *
 * @author willhains
 * @see Mutable
 * @see IO
 * @see Barrier
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use {@link org.udtopia.Value} instead.
 */
@Deprecated
@Retention(SOURCE)
@Target({TYPE, TYPE_PARAMETER, PARAMETER, TYPE_USE})
public @interface Pure { }
