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
 * <ul>
 *      <li>{@link java.lang.ref.SoftReference}</li>
 *      <li>{@link java.lang.ref.WeakReference}</li>
 * </ul>
 *
 * <i>Created by chcmatos (cmatos) on 04, march, 2022</i>
 *
 * @param <T> target object type
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public interface LazyReference<T> {

    //region wrappers
    /**
     * Function Wrapper to encapsulate LazyFunction with args.
     * @param <I> input type
     * @param <O> output type
     */
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

    /**
     * BiFunction Wrapper to encapsulate LazyBiFunction with args.
     * @param <I0> first input type
     * @param <I1> second input type
     * @param <O> output type
     */
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
     * @param data input data
     * @param <J> input data type
     * @return lazy reference.
     */
    static <J> LazyReference<J> soft(J data) {
        return new LazyReferenceImplSoft<>(data);
    }

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
     * </p>
     * @param supplier input supplier callback
     * @param <J> input data type
     * @return lazy reference.
     */
    static <J> LazyReference<J> soft(LazySupplier<J> supplier) {
        return new LazyReferenceImplSoft<>(supplier);
    }

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
     * </p>
     * @param input input data
     * @param function function callback to produce output data
     * @param <I> input type
     * @param <O> output type
     * @return lazy reference
     */
    static <I, O> LazyReference<O> soft(I input, LazyFunction<I, O> function) {
        return soft(new FunctionWrapper<>(input, function));
    }

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
     * </p>
     * @param arg0 first input argument
     * @param arg1 second input argument
     * @param function function callback to generate output
     * @param <I0> first input type
     * @param <I1> second input type
     * @param <O> output type
     * @return lazy reference
     */
    static <I0, I1, O> LazyReference<O> soft(I0 arg0, I1 arg1, LazyBiFunction<I0, I1, O> function) {
        return soft(new BiFunctionWrapper<>(arg0, arg1, function));
    }
    //endregion

    //region weak
    /**
     * <p>
     * Weak reference objects with LAZY-Loading Pattern,
     * which do not prevent their referents from being made finalizable,
     * finalized, and then reclaimed.
     *
     * Weak references are most often used to implement canonicalizing mappings.
     * </p>
     * @param data input data
     * @param <J> input type
     * @return lazy reference
     */
    static <J> LazyReference<J> weak(J data) {
        return new LazyReferenceImplWeak<>(data);
    }

    /**
     * <p>
     * Weak reference objects with LAZY-Loading Pattern,
     * which do not prevent their referents from being made finalizable,
     * finalized, and then reclaimed.
     *
     * Weak references are most often used to implement canonicalizing mappings.
     * </p>
     * @param supplier supplier callback to produce output value
     * @param <J> output type
     * @return lazy reference
     */
    static <J> LazyReference<J> weak(LazySupplier<J> supplier) {
        return new LazyReferenceImplWeak<>(supplier);
    }

    /**
     * <p>
     * Weak reference objects with LAZY-Loading Pattern,
     * which do not prevent their referents from being made finalizable,
     * finalized, and then reclaimed.
     *
     * Weak references are most often used to implement canonicalizing mappings.
     * </p>
     * @param input input data
     * @param function function callback to produce output value
     * @param <I> input type
     * @param <O> output type
     * @return lazy reference
     */
    static <I, O> LazyReference<O> weak(I input, LazyFunction<I, O> function) {
        return weak(new FunctionWrapper<>(input, function));
    }

    /**
     * <p>
     * Weak reference objects with LAZY-Loading Pattern,
     * which do not prevent their referents from being made finalizable,
     * finalized, and then reclaimed.
     *
     * Weak references are most often used to implement canonicalizing mappings.
     * </p>
     * @param arg0 first argument
     * @param arg1 second argument
     * @param function function callback to produce output value
     * @param <I0> first input type
     * @param <I1> second input type
     * @param <O> output type
     * @return lazy reference
     */
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
