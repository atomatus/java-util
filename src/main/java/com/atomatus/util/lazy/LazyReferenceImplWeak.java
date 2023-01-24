package com.atomatus.util.lazy;

import java.lang.ref.WeakReference;

/**
 * <p>
 * Weak reference objects with LAZY-Loading Pattern,
 * which do not prevent their referents from being made finalizable,
 * finalized, and then reclaimed.
 *
 * Weak references are most often used to implement canonicalizing mappings.
 * </p>
 *
 * <i>Created by chcmatos (cmatos) on 04, march, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 * @see WeakReference
 */
final class LazyReferenceImplWeak<T> extends LazyReferenceImpl<T, WeakReference<T>> {

    LazyReferenceImplWeak(LazySupplier<T> supplier) {
        super(supplier, WeakReference::new);
    }

    LazyReferenceImplWeak(T data) {
        super(new WeakReference<>(data));
    }
}
