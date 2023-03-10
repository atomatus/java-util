package com.atomatus.util;

import javax.security.auth.Destroyable;
import java.util.Arrays;
import java.util.Objects;

/**
 * <p>
 *      Provides support for lazy initialization.
 * </p>
 * <p>
 *     Lazy loading (also known as asynchronous loading) is a design pattern commonly used in computer programming
 *     (and mostly in web design) and development to defer initialization of an object until the point at which it is needed.<br>
 *     It can contribute to efficiency in the program's operation if properly and appropriately used.
 *     This makes it ideal in use cases where network content is accessed and initialization
 *     times are to be kept at a minimum.<br>
 *     <a href="https://en.wikipedia.org/wiki/Lazy_loading">See more here</a>
 * </p>
 * @param <Param> callback input parameter type
 * @param <Result> callback result value type
 * @author Carlos Matos {@literal @chcmatos}
 */
public final class Lazy<Param, Result> implements Destroyable {

    /**
     * Lazy loading callback function.
     * @param <IN> callback input parameter type
     * @param <OUT> callback result value type
     */
    @FunctionalInterface
    public interface LazyFunction<IN, OUT> {
        /**
         * Apply request to load value.
         * @param args input arguments
         * @return generated value.
         */
        OUT apply(IN[] args);
    }

    private static final int UNSET_HASH = -1;

    private final LazyFunction<Param, Result> function;
    private final boolean isThreadSafe;
    private final Object locker;

    private volatile boolean destroyed;
    private transient Object value;
    private transient int valueHash;

    /**
     * Constructs lazy loading.
     * @param function callback function to load value.
     * @param isThreadSafe current lazy loadind is thread safe.
     */
    public Lazy(LazyFunction<Param, Result> function, boolean isThreadSafe) {
        this.locker         = new Object();
        this.function       = Objects.requireNonNull(function);
        this.isThreadSafe   = isThreadSafe;
        this.valueHash      = UNSET_HASH;
    }

    //region value
    /**
     * Indicates whether a value has been created for this Lazy instance.
     * @return true if a value has been created for instance; otherwise, false.
     */
    public boolean isValueCreated() {
        if(isThreadSafe) {
            synchronized (locker) {
                return isValueCreatedInternal();
            }
        } else {
            return isValueCreatedInternal();
        }
    }

    private boolean isValueCreatedInternal() {
        requireNonDestroyed();
        return valueHash != UNSET_HASH;
    }

    /**
     * Initialize lazy value of recover already initialized one time before.
     * @param args input argument to callback function initialization.
     * @return required value.
     */
    @SafeVarargs
    public final Result value(Param... args) {
        if(isThreadSafe) {
            synchronized (locker) {
                return valueInternal(args);
            }
        } else {
            return valueInternal(args);
        }
    }

    @SuppressWarnings("unchecked")
    private Result valueInternal(Param[] args) {
        requireNonDestroyed();
        int hash = Objects.hash(value, Arrays.hashCode(args));
        if (valueHash == UNSET_HASH || (args.length != 0 && valueHash != hash)) {
            value = function.apply(args);
            valueHash = Objects.hash(value, Arrays.hashCode(args));
        }
        return (Result) value;
    }
    //endregion

    //region reset
    /**
     * Reset lazy value state to not initialized and
     * makes current value available to garbage colector.
     */
    public void reset() {
        if(isThreadSafe) {
            synchronized (locker) {
                resetInternal();
            }
        } else {
            resetInternal();
        }
    }

    private void resetInternal() {
        requireNonDestroyed();
        value = null;
        valueHash = UNSET_HASH;
    }
    //endregion

    //region Destroyable
    private void requireNonDestroyed() {
        if(destroyed){
            throw new UnsupportedOperationException("Object was destroyed!");
        }
    }

    @Override
    public boolean isDestroyed() {
        synchronized (locker) {
            return destroyed;
        }
    }

    @Override
    public void destroy() {
        synchronized (locker) {
            if(!destroyed) {
                value = null;
                valueHash = UNSET_HASH;
                destroyed = true;
            }
        }
    }
    //endregion
}
