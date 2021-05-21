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
 * @author Carlos Matos
 * @see Lazy
 */
public final class LazyFactory {

    //region Lazy Functions
    public interface LazyNoParamFunction<Result> {

        default Lazy.LazyFunction<Void, Result> boxing() {
            return this::applyArgs;
        }

        default Result applyArgs(Void[] args) {
            return apply();
        }

        Result apply();
    }

    public interface LazySingleParamFunction<Param, Result> {
        default Lazy.LazyFunction<Param, Result> boxing() {
            return this::applyArgs;
        }

        default Result applyArgs(Param[] args) {
            args = resize(args, 1);
            return this.apply(args[0]);
        }

        Result apply(Param param);
    }

    public interface LazyDoubleParamFunction<Param0, Param1, Result> {

        default Lazy.LazyFunction<Object, Result> boxing() {
            return this::applyArgs;
        }

        @SuppressWarnings("unchecked")
        default Result applyArgs(Object[] args) {
            args = resize(args, 2);
            return apply((Param0) args[0], (Param1) args[1]);
        }

        Result apply(Param0 param0, Param1 param1);
    }

    public interface LazyTripleParamFunction<Param0, Param1, Param2, Result> {

        default Lazy.LazyFunction<Object, Result> boxing() {
            return this::applyArgs;
        }

        @SuppressWarnings("unchecked")
        default Result applyArgs(Object[] args) {
            args = resize(args, 3);
            return apply((Param0) args[0], (Param1) args[1], (Param2) args[2]);
        }

        Result apply(Param0 param0, Param1 param1, Param2 param2);
    }

    public interface LazyQuadrupleParamFunction<Param0, Param1, Param2, Param3, Result> {

        default Lazy.LazyFunction<Object, Result> boxing() {
            return this::applyArgs;
        }

        @SuppressWarnings("unchecked")
        default Result applyArgs(Object[] args) {
            args = resize(args, 4);
            return apply((Param0) args[0], (Param1) args[1], (Param2) args[2], (Param3) args[3]);
        }

        Result apply(Param0 param0, Param1 param1, Param2 param2, Param3 param);
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
    public static <Param, Result> Lazy<Param, Result> create(Lazy.LazyFunction<Param, Result> function, boolean isThreadSafe) {
        return new Lazy<>(function, isThreadSafe);
    }

    public static <Param, Result> Lazy<Param, Result> create(Lazy.LazyFunction<Param, Result> function) {
        return new Lazy<>(function, false);
    }
    //endregion

    //region createNoParam
    public static <Result> Lazy<Void, Result> create(LazyNoParamFunction<Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    public static <Result> Lazy<Void, Result> create(LazyNoParamFunction<Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createSingleParam
    public static <Param, Result> Lazy<Param, Result> create(LazySingleParamFunction<Param, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    public static <Param, Result> Lazy<Param, Result> create(LazySingleParamFunction<Param, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createDoubleParam
    public static <Param0, Param1, Result> Lazy<Object, Result> create(LazyDoubleParamFunction<Param0, Param1, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    public static <Param0, Param1, Result> Lazy<Object, Result> create(LazyDoubleParamFunction<Param0, Param1, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createTripleParam
    public static <Param0, Param1, Param2, Result> Lazy<Object, Result> create(LazyTripleParamFunction<Param0, Param1, Param2, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    public static <Param0, Param1, Param2, Result> Lazy<Object, Result> create(LazyTripleParamFunction<Param0, Param1, Param2, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

    //region createQuadrupleParam
    public static <Param0, Param1, Param2, Param3, Result> Lazy<Object, Result> create(LazyQuadrupleParamFunction<Param0, Param1, Param2, Param3, Result> function, boolean isThreadSafe) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), isThreadSafe);
    }

    public static <Param0, Param1, Param2, Param3, Result> Lazy<Object, Result> create(LazyQuadrupleParamFunction<Param0, Param1, Param2, Param3, Result> function) {
        return new Lazy<>(Objects.requireNonNull(function).boxing(), false);
    }
    //endregion

}
