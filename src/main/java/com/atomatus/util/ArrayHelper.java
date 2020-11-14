package com.atomatus.util;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Helper to analyze, manipulate and convert array objects.
 */
@SuppressWarnings("unchecked")
public final class ArrayHelper {

    public interface Function<I, O> {
        O apply(I i);
    }

    public interface Reducer<A, I> {
        A apply(A acc, I i);
    }

    public interface Filter<I> {
        boolean accept(I i);
    }

    private ArrayHelper() { }

    /**
     * Create a new object array from current object (current object referecing a array).
     *
     * @param arr target object.
     * @return object array.
     */
    public static Object[] toArray(Object arr) {
        Objects.requireNonNull(arr);
        if (!arr.getClass().isArray()) {
            throw new IllegalArgumentException("Object is not an array!");
        }
        int len = Array.getLength(arr);
        Object[] objArr = new Object[len];
        for (int i = 0; i < len; i++) {
            objArr[i] = Array.get(arr, i);
        }
        return objArr;
    }

    /**
     * Create a new object array from current object (current object referecing a array).
     *
     * @param arr       target object.
     * @param clazzType array class type.
     * @param <E>       array type.
     * @return object array.
     */
    public static <E> E[] toArray(Object arr, Class<E> clazzType) {
        Objects.requireNonNull(arr);
        if (!arr.getClass().isArray()) {
            throw new IllegalArgumentException("Object is not an array!");
        }
        int len = Array.getLength(arr);
        E[] objArr = (E[]) Array.newInstance(clazzType, len);
        for (int i = 0; i < len; i++) {
            Object obj = Array.get(arr, i);
            if (clazzType.isInstance(obj)) {
                objArr[i] = (E) obj;
            }
        }

        return objArr;
    }

    /**
     * Insert a new element at object array (on index).
     *
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @param <E>   element type
     * @return new array.
     */
    public static <E> E[] insertAt(E[] arr, E e, int index) {
        Objects.requireNonNull(arr);
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }

        int length = arr.length;
        int newLength = Math.max(index, length) + 1;
        Class<?> newType = arr.getClass();
        E[] nArr = (newType == Object[].class) ?
                (E[]) new Object[newLength] :
                (E[]) Array.newInstance(newType.getComponentType(), newLength);

        boolean found = false;
        for (int i = 0, j = 0; i < length; j++, i++) {
            if (!found && (found = i == index)) {
                j++;
            }
            nArr[j] = arr[i];
        }
        nArr[index] = e;
        return nArr;
    }

    /**
     * Insert a new element at first index of object array.
     *
     * @param arr target array
     * @param e   new element to array
     * @param <E> element type
     * @return new array.
     */
    public static <E> E[] push(E[] arr, E e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at end of object array.
     *
     * @param arr target array
     * @param e   new element to array
     * @param <E> element type
     * @return new array.
     */
    public static <E> E[] add(E[] arr, E e) {
        return insertAt(arr, e, arr.length);
    }

    /**
     * Select current array objects data.
     * @param args target array
     * @param func select function
     * @param <I> array type
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <I, O> O[] select(I[] args, Function<I, O> func) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(func);
        if(args.length == 0) {
            return (O[]) new Object[0];
        }

        O aux = func.apply(args[0]);
        O[] out = aux == null ? (O[]) new Object[args.length] :
                (O[]) Array.newInstance(aux.getClass(), args.length);
        out[0] = aux;

        for (int i = 1, l = args.length; i < l; i++) {
            out[i] = func.apply(args[i]);
        }

        return out;
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @param <I> array type
     * @return new array with filtered items.
     */
    public static <I> I[] filter(I[] args, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);
        I[] out = (I[]) new Object[args.length];
        int offset = 0;
        for (I curr : args) {
            if (where.accept(curr)) {
                out[offset++] = curr;
            }
        }
        return take(out, offset);
    }

    /**
     * First element on array into condition.
     * @param args target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> I first(I[] args, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);
        for (I curr : args) {
            if (where.accept(curr)) {
                return curr;
            }
        }
        return null;
    }

    /**
     * Apply reduce operation.
     * @param args target
     * @param func reduce function
     * @param acc accumulator
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A> A reduce(I[] args, Reducer<A, I> func, A acc) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(func);
        for(int i=0,l=args.length; i < l; i++) {
            if(i == 0 && acc == null) {
                //noinspection unchecked
                acc = (A) args[i];
            } else {
                acc = func.apply(acc, args[i]);
            }
        }
        return acc;
    }

    /**
     * Apply reduce operation.
     * @param args target
     * @param func reduce function
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A extends I> A reduce(I[] args, Reducer<A, I> func) {
        return reduce(args, func, null);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @param <I> array element type
     * @return new array.
     */
    public static <I> I[] take(I[] arr, int count) {
        if(count >= Objects.requireNonNull(arr).length) {
            return arr;
        } else {
            I[] out = (I[]) new Object[count];
            System.arraycopy(arr, 0, out, 0, count);
            return  out;
        }
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @param <I> array element type
     * @return new array after count.
     */
    public static <I> I[] jump(I[] arr, int count) {
        Objects.requireNonNull(arr);
        if(count <= 0) {
            return arr;
        } else if(count >= arr.length) {
            return (I[]) new Object[0];
        } else {
            I[] out = (I[]) new Object[arr.length - count];
            System.arraycopy(arr, count, out, 0, out.length);
            return  out;
        }
    }

    /**
     * Compare if sequence of arrays are equals.
     * @param arr0 target array
     * @param arr1 target array
     * @return true when both are sequential equals.
     */
    public static boolean sequenceEquals(Object[] arr0, Object[] arr1) {
        if(Objects.requireNonNull(arr0).length != Objects.requireNonNull(arr1).length) {
            return false;
        } else {
            for(int i=0, l=arr0.length; i < l; i ++) {
                if(!Objects.equals(arr0[i], arr1[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}