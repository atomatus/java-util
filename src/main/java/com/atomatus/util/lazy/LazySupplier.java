package com.atomatus.util.lazy;

/**
 * Represents a supplier of results.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a functional interface
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 *
 * @author Carlos Matos {@literal @chcmatos}
 *
 */
@FunctionalInterface
public interface LazySupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}