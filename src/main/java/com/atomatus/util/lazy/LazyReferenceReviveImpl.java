package com.atomatus.util.lazy;

import java.lang.ref.Reference;
import java.util.Objects;


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
 * @see LazyReferenceRevive
 * @see LazyReference
 */
final class LazyReferenceReviveImpl<Data, Ref extends Reference<Data>> implements LazyReferenceRevive<Data> {

    private Ref ref;
    private LazySupplier<Data> supplier;
    private LazyFunction<Data, Ref> refBuilder;

    public LazyReferenceReviveImpl(LazySupplier<Data> supplier,
                                   LazyFunction<Data, Ref> refBuilder) {
        this.supplier = Objects.requireNonNull(supplier);
        this.refBuilder = Objects.requireNonNull(refBuilder);
    }

    /**
     * Returns this reference object's referent.
     * If this reference object has been cleared,
     * either by the program or by the garbage
     * collector, then this method returns a new reference,
     * only if initialized by a LazySupplier, otherwise throws exceptions.
     *
     * @return The object to which this reference refers.
     */
    @Override
    public Data get() {
        Data data;
        if(ref == null || (data = ref.get()) == null) {
            assert supplier != null;
            data = supplier.get();
            assert refBuilder != null;
            ref = refBuilder.apply(data);
        }
        return data;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        supplier = null;
        refBuilder = null;
        ref = null;
    }

}
