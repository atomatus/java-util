package com.atomatus.util.lazy;

import java.util.function.Function;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link Function}.
 *
 * <p>
 * This is a functional interface
 * whose functional method is {@link #apply(Object, Object)}.
 * </p>
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 *
 * <i>Created by chcmatos on 24, janeiro, 2023</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
@FunctionalInterface
public interface LazyBiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    R apply(T t, U u);
}
