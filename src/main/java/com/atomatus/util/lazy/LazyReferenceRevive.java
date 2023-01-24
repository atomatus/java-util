package com.atomatus.util.lazy;

/**
 * <p>
 * Works how like {@link LazyReference}, but prevent null return on {@link #get()},
 * when loaded data is collected revive it self, creating a new instance by callback (supplier of function)
 * setup on LazyReference creator method ({@link LazyReference#soft} or {@link LazyReference#weak}).
 * </p>
 *
 * <i>Created by chcmatos (cmatos) on 01, April, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 * @see LazyReference
 */
public interface LazyReferenceRevive<T> {

    /**
     * Returns this reference object's referent.
     * If this reference object has been cleared,
     * either by the program or by the garbage
     * collector, then this method returns a new reference,
     * only if initialized by a NextSupplier, otherwise throws exceptions.
     *
     * @return The object to which this reference refers.
     */
    T get();

}
