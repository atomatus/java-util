package com.atomatus.util.lazy;

import com.atomatus.util.Debug;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.lang.ref.Reference;
import java.util.Objects;

/**
 * <p>
 * Abstract base class for LAZY-Loading Pattern reference objects.
 * This class defines the operations common to all reference objects.
 * Because reference objects are implemented in close cooperation with the garbage collector,
 * this class may not be subclassed directly.
 * </p>
 * <p>
 * See more about Lazy-loading pattern <a href="https://en.wikipedia.org/wiki/Lazy_loading">here</a>
 * </p>
 * <i>Created by chcmatos (cmatos) on 04, march, 2022</i>
 *
 * @param <Data> target type referenced by lazy-loading
 * @param <Ref> reference type used
 *
 * @author Carlos Matos {@literal @chcmatos}
 * @see Reference
 */
abstract class LazyReferenceImpl<Data, Ref extends Reference<Data>> implements LazyReference<Data> {

    private Ref ref;
    private LazySupplier<Data> supplier;
    private LazyFunction<Data, Ref> refBuilder;

    public LazyReferenceImpl(LazySupplier<Data> supplier,
                             LazyFunction<Data, Ref> refBuilder) {
        this.supplier = Objects.requireNonNull(supplier);
        this.refBuilder = Objects.requireNonNull(refBuilder);
    }

    public LazyReferenceImpl(Ref ref) {
        this.ref = Objects.requireNonNull(ref);
    }

    private void destroy(Object target) {
        if (target instanceof Destroyable && !((Destroyable) target).isDestroyed()) {
            try {
                ((Destroyable) target).destroy();
            } catch (DestroyFailedException e) {
                if (Debug.isDebugMode()) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Data get() {
        if(supplier != null && refBuilder != null) {
            Data data = supplier.get();
            ref = refBuilder.apply(data);
            destroy(supplier);
            supplier = null;
            refBuilder = null;
            return data;
        } else if (ref != null) {
            return ref.get();
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        if (ref != null) {
            ref.clear();
        }
        destroy(supplier);
        supplier = null;
        refBuilder = null;
        ref = null;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        supplier = null;
        refBuilder = null;
        ref = null;
    }

    @Override
    public LazyReferenceRevive<Data> revive() {
        if(supplier == null || refBuilder == null) {
            throw new UnsupportedOperationException("Is not possible parse current " +
                    "LazyReference to revivable when it was initialized without " +
                    "by a callback creator function!");
        }
        try {
            return new LazyReferenceReviveImpl<>(this.supplier, this.refBuilder);
        } finally {
            if (ref != null) {
                ref.clear();
            }
            supplier = null;
            refBuilder = null;
            ref = null;
        }
    }
}
