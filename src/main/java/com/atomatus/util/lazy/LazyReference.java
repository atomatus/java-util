package com.atomatus.util.lazy;

import javax.security.auth.Destroyable;
import java.util.Objects;

/**
 * <p>
 *     Lazy Reference is an interface for <a href="https://en.wikipedia.org/wiki/Lazy_loading">LAZY-Loading Pattern</a> reference
 *     objects as <i>{@link #soft}</i> or <i>{@link #weak}</i> approach.
 *
 *     This pattern is holding to defer initialization of an object until
 *     the point at which it is needed. So, when developer request in first time the
 *     {@link #get} method this object will be loaded and keep in memory by soft or weak approach.
 * </p>
 *
 * <p>
 *     <ul>
 *         <li>{@link java.lang.ref.SoftReference}</li>
 *         <li>{@link java.lang.ref.WeakReference}</li>
 *     </ul>
 * </p>
 *
 * <i>Created by chcmatos (cmatos) on 04, march, 2022</i>
 *
 * @param <T> target object type
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public interface LazyReference<T> {

    //region wrappers
    final class FunctionWrapper<I, O> implements LazySupplier<O>, Destroyable {

        private I inputArg;
        private LazyFunction<I, O> function;

        FunctionWrapper(I inputArg, LazyFunction<I, O> function) {
            this.inputArg = inputArg;
            this.function = Objects.requireNonNull(function);
        }

        @Override
        public O get() {
            return Objects.requireNonNull(this.function, "FunctionWrapper was already destroyed!")
                    .apply(inputArg);
        }

        @Override
        public boolean isDestroyed() {
            return inputArg == null && function == null;
        }

        @Override
        public void destroy() {
            inputArg = null;
            function = null;
        }
    }

    final class BiFunctionWrapper<I0, I1, O> implements LazySupplier<O>, Destroyable {

        private I0 inputArg0;
        private I1 inputArg1;
        private LazyBiFunction<I0, I1, O> function;

        BiFunctionWrapper(I0 inputArg0, I1 inputArg1, LazyBiFunction<I0, I1, O> function) {
            this.inputArg0 = inputArg0;
            this.inputArg1 = inputArg1;
            this.function = Objects.requireNonNull(function);
        }

        @Override
        public O get() {
            return Objects.requireNonNull(this.function, "BiFunctionWrapper was already destroyed!").apply(inputArg0, inputArg1);
        }

        @Override
        public boolean isDestroyed() {
            return inputArg0 == null && inputArg1 == null && function == null;
        }

        @Override
        public void destroy() {
            inputArg0 = null;
            inputArg1 = null;
            function  = null;
        }
    }
    //endregion

    //region soft
    static <J> LazyReference<J> soft(J data) {
        return new LazyReferenceImplSoft<>(data);
    }

    static <J> LazyReference<J> soft(LazySupplier<J> supplier) {
        return new LazyReferenceImplSoft<>(supplier);
    }

    static <I, O> LazyReference<O> soft(I input, LazyFunction<I, O> function) {
        return soft(new FunctionWrapper<>(input, function));
    }

    static <I0, I1, O> LazyReference<O> soft(I0 arg0, I1 arg1, LazyBiFunction<I0, I1, O> function) {
        return soft(new BiFunctionWrapper<>(arg0, arg1, function));
    }
    //endregion

    //region weak
    static <J> LazyReference<J> weak(J data) {
        return new LazyReferenceImplWeak<>(data);
    }

    static <J> LazyReference<J> weak(LazySupplier<J> supplier) {
        return new LazyReferenceImplWeak<>(supplier);
    }

    static <I, O> LazyReference<O> weak(I input, LazyFunction<I, O> function) {
        return weak(new FunctionWrapper<>(input, function));
    }

    static <I0, I1, O> LazyReference<O> weak(I0 arg0, I1 arg1, LazyBiFunction<I0, I1, O> function) {
        return weak(new BiFunctionWrapper<>(arg0, arg1, function));
    }
    //endregion

    /**
     * Returns this reference object's referent.
     * If this reference object has been cleared,
     * either by the program or by the garbage
     * collector, then this method returns
     * <code>null</code>.
     *
     * @return The object to which this reference refers, or
     * <code>null</code> if this reference object has been cleared
     */
    T get();

    /**
     * Clears this reference object.
     * Invoking this method will not cause this
     * object to be enqueued.
     *
     * <p> This method is invoked only by Java code; when the garbage collector
     * clears references it does so directly, without invoking this method.
     */
    void clear();

    /**
     * Parse and convert current LazyReference when initialized with a
     * LazySupplier or LazyFunction callback
     * to a LazyReference revivable
     * @return Revivable Lazy reference instance
     * @throws UnsupportedOperationException throws when is not possible
     * convert LazyReference to revivable, because it was not initialized
     * using a callback function/supplier.
     */
    LazyReferenceRevive<T> revive();
}
