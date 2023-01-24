package com.atomatus.util.lazy;

import java.lang.ref.SoftReference;

/**
 * <p>
 * Soft reference objects with Lazy-Loading Pattern, which are cleared at the discretion of the garbage
 * collector in response to memory demand.
 *
 * <p> Suppose that the garbage collector determines at a certain point in time
 * that an object is <a href="package-summary.html#reachability">softly
 * reachable</a>.  At that time it may choose to clear atomically all soft
 * references to that object and all soft references to any other
 * softly-reachable objects from which that object is reachable through a chain
 * of strong references.  At the same time or at some later time it will
 * enqueue those newly-cleared soft references that are registered with
 * reference queues.
 *
 * </p>
 *
 * <i>Created by chcmatos (cmatos) on 04, march, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 * @see SoftReference
 */
final class LazyReferenceImplSoft<T> extends LazyReferenceImpl<T, SoftReference<T>> {

    LazyReferenceImplSoft(LazySupplier<T> supplier) {
        super(supplier, SoftReference::new);
    }

    LazyReferenceImplSoft(T data) {
        super(new SoftReference<>(data));
    }
}
