package com.atomatus.util;

import java.util.Objects;

/**
 * <strong>{@link Lazy} Factory</strong>
 * <p>
 *     A <a href="https://refactoring.guru/pt-br/design-patterns/factory-method">factory pattern</a>
 *     implementation to build a {@link Lazy} load object container within several
 *     callback functions possibilities.
 * </p>
 * <pre>
 *     {@code
 *      private Result callbackWithInputArgs(Integer... args) {
 *         //...
 *      }
 *
 *      //default lazy load creation with callback for input args
 *      Lazy<?, Result> ll0 = LazyFactory.create(this::callbackWithInputArgs, true);//thread safe
 *      Lazy<Integer, Result> ll1 = LazyFactory.create(this::callbackWithInputArgs);//no thread safe
 *     }
 * </pre>
 * <pre>
 *     {@code
 *      private Result callbackNoInputArgs() {
 *         //...
 *      }
 *
 *      //default lazy load creation with callback for no input args
 *      Lazy<?, Result> ll0 = LazyFactory.create(this::callbackNoInputArgs, true);//thread safe
 *      Lazy<Void, Result> ll1 = LazyFactory.create(this::callbackNoInputArgs);//no thread safe
 *     }
 * </pre>
 * @author Carlos Matos {@literal @chcmatos}
 * @see Lazy
 */
public final class LazyFactory {

    //region Lazy Functions

    /**
     * Lazy callback no params
     * @param <Result> result type
     */
    @FunctionalInterface
    public interface LazyNoParamFunction<Result> {

        /**
         * Boxing function to lazyFuntion args.
         * @return apply function args callback.
         */
        default Lazy.LazyFunction<Void, Result> boxing() {
            return this::applyArgs;
        }

        /**
         * Boxing argument to current callback.
         * @param args arguments
         * @return result
         */
        default Result applyArgs(Void[] args) {
            return apply();
        }

        /**
         * Apply callback
         * @return result generated
         */
        Result apply();
    }

    /**
     * Lazy single input type callback.
     * @param <Param> input type
     * @param <Result> result type
     */
    @FunctionalInterface
    public interface LazySingleParamFunction<Param, Result> {

        /**
         * Boxing function to lazyFuntion args.
         * @return apply function args callback.
         */
        default Lazy.LazyFunction<Param, Result> boxing() {
            return this::applyArgs;
        }

        /**
         * Boxing argument to current callback.
         * @param args arguments
         * @return result
         */
        default Result applyArgs(Param[] args) {
            args = resize(args, 1);
            return this.apply(args[0]);
        }

        /**
         * Apply callback
         * @param param first param
         * @return result generated
         */
        Result apply(Param param);
    }

    /**
     * Lazy double input params callback.
     * @param <Param0> first param type
     * @param <Param1> second param type
     * @param <Result> result type
     */
    @FunctionalInterface
    public interface LazyDoubleParamFunction<Param0, Param1, Result> {

        /**
         * Boxing function to lazyFuntion args.
         * @return apply function args callback.
         */
        default Lazy.LazyFunction<Object, Result> boxing() {
            return this::applyArgs;
        }

        /**
         * Boxing argument to current callback.
         * @param args arguments
         * @return result
         */
        @SuppressWarnings("unchecked")
        default Result applyArgs(Object[] args) {
            args = resize(args, 2);
            return apply((Param0) args[0], (Param1) args[1]);
        }

        /**
         * Apply callback
         * @param param0 first param
         * @param param1 second param
         * @return result generated
         */
        Result apply(Param0 param0, Param1 param1);
    }

    /**
     * Lazy triple params callback
     * @param <Param0> first param type
     * @param <Param1> second param type
     * @param <Param2> third param type
     * @param <Result> result type
     */
    @FunctionalInterface
    public interface LazyTripleParamFunction<Param0, Param1, Param2, Result> {

        /**
         * Boxing function to lazyFuntion args.
         * @return apply function args callback.
         */
        default Lazy.LazyFunction<Object, Result> boxing() {
            return this::applyArgs;
        }

        /**
         * Boxing argument to current callback.
         * @param args arguments
         * @return result
         */
        @SuppressWarnings("unchecked")
        default Result applyArgs(Object[] args) {
            args = resize(args, 3);
            return apply((Param0) args[0], (Param1) args[1], (Param2) args[2]);
        }

        /**
         * Apply callback
         * @param param0 first param
         * @param param1 second param
         * @param param2 thrid param
         * @return result generated
         */
        Result apply(Param0 param0, Param1 param1, Param2 param2);
    }

    /**
     * Lazy quadruple params callback.
     * @param <Param0> first param type
     * @param <Param1> second param type
     * @param <Param2> third param type
     * @param <Param3> fourth param type
     * @param <Result> result type
     */
    @FunctionalInterface
    public interface LazyQuadrupleParamFunction<Param0, Param1, Param2, Param3, Result> {

        /**
         * Boxing function to lazyFuntion args.
         * @return apply function args callback.
         */
        default Lazy.LazyFunction<Object, Result> boxing() {
            return this::applyArgs;
        }

        /**
         * Boxing argument to current callback.
         * @param args arguments
         * @return result
         */
        @SuppressWarnings("unchecked")
        default Result applyArgs(Object[] args) {
            args = resize(args, 4);
            return apply((Param0) args[0], (Param1) args[1], (Param2) args[2], (Param3) args[3]);
        }

        /**
         * Apply callback
         * @param param0 first param
         * @param param1 second param
         * @param param2 thrid param
         * @param param3 fourth param
         * @return result generated
         */
        Result apply(Param0 param0, Param1 param1, Param2 param2, Param3 param3);
    }

    private static  <E> E[] resize(E[] arr, int len) {
        if(arr.length > len) {
            throw new IllegalArgumentException("This callback operation require maximum of " + len + " arguments!");
        }
        return ArrayHelper.resize(arr, len);
    }
    //endregion

    private LazyFactory() { }

    //region create

    /**
     * Create an instance of Lazy for param arguments callback.
     * @param function function callback
     * @param isThreadSafe define if lazy instance is thread safe
     * @param <Param> callback param args input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param, Result> Lazy<Param, Result> create(Lazy.LazyFunction<Param, Result> function, boolean isThreadSafe) {
        return new Lazy<>(function, isThreadSafe);
    }

    /**
     * Create an instance of Lazy for param arguments callback.
     * @param function function callback
     * @param <Param> callback param args input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param, Result> Lazy<Param, Result> create(Lazy.LazyFunction<Param, Result> function) {
        return new Lazy<>(function, false);
    }
    //endregion

    //region createNoParam

    /**
     * Create an instance of Lazy for no param callback.
     * @param function function callback
     * @param isThreadSafe define if lazy instance is thread safe
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Result> Lazy<Void, Result> create(LazyNoParamFunction<Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    /**
     * Create an instance of Lazy for no param callback.
     * @param function function callback
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Result> Lazy<Void, Result> create(LazyNoParamFunction<Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createSingleParam

    /**
     * Create an instance of Lazy for single param callback.
     * @param function function callback
     * @param isThreadSafe define if lazy instance is thread safe
     * @param <Param> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param, Result> Lazy<Param, Result> create(LazySingleParamFunction<Param, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    /**
     * Create an instance of Lazy for single param callback.
     * @param function function callback
     * @param <Param> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param, Result> Lazy<Param, Result> create(LazySingleParamFunction<Param, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createDoubleParam

    /**
     * Create an instance of Lazy for double param callback.
     * @param function function callback
     * @param isThreadSafe define if lazy instance is thread safe
     * @param <Param0> callback param input type
     * @param <Param1> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param0, Param1, Result> Lazy<Object, Result> create(LazyDoubleParamFunction<Param0, Param1, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    /**
     * Create an instance of Lazy for double param callback.
     * @param function function callback
     * @param <Param0> callback param input type
     * @param <Param1> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param0, Param1, Result> Lazy<Object, Result> create(LazyDoubleParamFunction<Param0, Param1, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createTripleParam

    /**
     * Create an instance of Lazy for triple param callback.
     * @param function function callback
     * @param isThreadSafe define if lazy instance is thread safe
     * @param <Param0> callback param input type
     * @param <Param1> callback param input type
     * @param <Param2> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param0, Param1, Param2, Result> Lazy<Object, Result> create(LazyTripleParamFunction<Param0, Param1, Param2, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    /**
     * Create an instance of Lazy for triple param callback.
     * @param function function callback
     * @param <Param0> callback param input type
     * @param <Param1> callback param input type
     * @param <Param2> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param0, Param1, Param2, Result> Lazy<Object, Result> create(LazyTripleParamFunction<Param0, Param1, Param2, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createQuadrupleParam

    /**
     * Create an instance of Lazy for quadruple param callback.
     * @param function function callback
     * @param isThreadSafe define if lazy instance is thread safe
     * @param <Param0> callback param input type
     * @param <Param1> callback param input type
     * @param <Param2> callback param input type
     * @param <Param3> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param0, Param1, Param2, Param3, Result> Lazy<Object, Result> create(LazyQuadrupleParamFunction<Param0, Param1, Param2, Param3, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    /**
     * Create an instance of Lazy for quadruple param callback.
     * @param function function callback
     * @param <Param0> callback param input type
     * @param <Param1> callback param input type
     * @param <Param2> callback param input type
     * @param <Param3> callback param input type
     * @param <Result> lazy result type
     * @return lazy load instance
     */
    public static <Param0, Param1, Param2, Param3, Result> Lazy<Object, Result> create(LazyQuadrupleParamFunction<Param0, Param1, Param2, Param3, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

}
