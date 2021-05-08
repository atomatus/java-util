package com.atomatus.util;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Helper to analyze, manipulate and convert array objects.
 */
@SuppressWarnings("unchecked")
public final class ArrayHelper {

    /**
     * Simple Function Callback.
     * @param <I> input type
     * @param <O> output type
     */
    public interface Function<I, O> {
        /**
         * Apply function to convert Input type to Output type.
         * @param i input element target.
         * @return output element generated.
         */
        O apply(I i);
    }

    /**
     * Simple reducer function callback.
     * @param <A> accumulate and output type
     * @param <I> input type
     */
    public interface Reducer<A, I> {
        /**
         * Apply reducer
         * @param acc accumulate value.
         * @param i current value.
         * @return accumulate value.
         */
        A apply(A acc, I i);
    }

    /**
     * Simple reducer function callback.
     * @param <A> accumulate and output type
     * @param <I> input type
     */
    public interface ReducerIndex<A, I> {
        /**
         * Apply reducer
         * @param acc accumulate value.
         * @param i current value.
         * @param index current index.
         * @return accumulate value.
         */
        A apply(A acc, I i, int index);
    }

    /**
     * Simple filter callback.
     * @param <I> input type
     */
    public interface Filter<I> {
        /**
         * Filter accept callback.
         * @param i target element
         * @return true, element has to be filtered, otherwise, element is ignored.
         */
        boolean accept(I i);
    }

    private ArrayHelper() { }

    //region requires
    /**
     * Throws exception when object is null or is not an array.
     * @param arr target array object.
     */
    public static void requireArray(Object arr) {
        if (!Objects.requireNonNull(arr).getClass().isArray()) {
            throw new IllegalArgumentException("Object is not an array!");
        }
    }

    /**
     * Throws exception when object is null or is not an array or is empty.
     * @param arr target array object.
     */
    public static void requireArrayNonNullOrEmpty(Object arr) {
        requireArray(arr);
        if(Array.getLength(arr) == 0) {
            throw new IllegalArgumentException("Array is empty!");
        }
    }
    //endregion

    //region clear
    /**
     * Remove all elements of target array.
     * @param arr target array.
     */
    public static void clear(Object arr) {
        requireArray(arr);
        Class<?> arrType = arr.getClass().getComponentType();
        int len = Array.getLength(arr);
        for (int i = 0; i < len; i++) {
            Object aux = Array.get(arr, i);
            if(arrType.isPrimitive()) {
                if(aux instanceof Number) {
                    Array.set(arr, i, 0);
                } else if(aux instanceof Boolean) {
                    Array.set(arr, i, false);
                } else if(aux instanceof Character) {
                    Array.set(arr, i, '\0');
                } else {
                    Array.set(arr, i, null);
                }
            } else {
                Array.set(arr, i, null);
            }
        }
    }
    //endregion

    //region indexOf
    /**
     * Retrieve index of target element.
     * @param arr target array
     * @param e target element
     * @param start start index to search
     * @param end end length to search
     * @param <E> element type
     * @return index of element, or -1 when not found.
     */
    public static <E> int indexOf(E[] arr, E e, int start, int end) {
        if (e == null) {
            for (int i = start; i < end; i++) {
                if (arr[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (e.equals(arr[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Retrieve index of target element.
     * @param arr target array
     * @param e target element
     * @param <E> element type
     * @return index of element, or -1 when not found.
     */
    public static <E> int indexOf(E[] arr, E e) {
        return indexOf(Objects.requireNonNull(arr), e, 0, arr.length);
    }

    /**
     * Retrieve index of target element.
     * @param arr target array
     * @param e target element
     * @param start start index to search
     * @param end end length to search
     * @param <E> element type
     * @return index of element, or -1 when not found.
     */
    public static <E> int indexOf(Object arr, E e, int start, int end) {
        requireArray(arr);
        if (e == null) {
            for (int i = start; i < end; i++) {
                Object aux = Array.get(arr, i);
                if (aux == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                Object aux = Array.get(arr, i);
                if (e.equals(aux)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Retrieve index of target element.
     * @param arr target array
     * @param e target element
     * @param <E> element type
     * @return index of element, or -1 when not found.
     */
    public static <E> int indexOf(Object arr, E e) {
        return indexOf(Objects.requireNonNull(arr), e, 0, Array.getLength(arr));
    }
    //endregion

    //region toArray
    /**
     * Create a new object array from current object (current object referecing a array).
     *
     * @param arr target object.
     * @return object array.
     */
    public static Object[] toArray(Object arr) {
        requireArray(arr);
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
        requireArray(arr);
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
    //endregion

    //region insertAt
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
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static boolean[] insertAt(boolean[] arr, boolean e, int index) {
        return (boolean[]) insertAt(arr, e, index, boolean[]::new);
    }

    /**
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static char[] insertAt(char[] arr, char e, int index) {
        return (char[]) insertAt(arr, e, index, char[]::new);
    }

    /**
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static short[] insertAt(short[] arr, short e, int index) {
        return (short[]) insertAt(arr, e, index, short[]::new);
    }

    /**
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static int[] insertAt(int[] arr, int e, int index) {
        return (int[]) insertAt(arr, e, index, int[]::new);
    }

    /**
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static long[] insertAt(long[] arr, long e, int index) {
        return (long[]) insertAt(arr, e, index, long[]::new);
    }

    /**
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static float[] insertAt(float[] arr, float e, int index) {
        return (float[]) insertAt(arr, e, index, float[]::new);
    }

    /**
     * Insert a new element at array (on index).
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @return new array.
     */
    public static double[] insertAt(double[] arr, double e, int index) {
        return (double[]) insertAt(arr, e, index, double[]::new);
    }

    /**
     * Insert a new element at object array (on index).
     *
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @param newArrayLengthFun new array length function callback.
     * @param <E>   element type
     * @return new array.
     */
    private static <E> Object insertAt(Object arr, E e, int index,
                                      Function<Integer, Object> newArrayLengthFun) {
        Objects.requireNonNull(arr);
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }

        int length = Array.getLength(arr);
        Object nArr = newArrayLengthFun.apply(Math.max(index, length) + 1 /*new length*/);

        boolean found = false;
        for (int i = 0, j = 0; i < length; j++, i++) {
            if (!found && (found = i == index)) {
                j++;
            }
            Array.set(nArr, j, Array.get(arr, i));
        }
        Array.set(nArr, index, e);
        return nArr;
    }
    //endregion

    //region push
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
     * Insert a new element at first index of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static boolean[] push(boolean[] arr, boolean e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at first index of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static char[] push(char[] arr, char e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at first index of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static short[] push(short[] arr, short e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at first index of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static int[] push(int[] arr, int e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at first index of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static float[] push(float[] arr, float e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at first index of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static double[] push(double[] arr, double e) {
        return insertAt(arr, e, 0);
    }
    //endregion

    //region add
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
     * Insert a new element at end of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static boolean[] add(boolean[] arr, boolean e) {
        return insertAt(arr, e, arr.length);
    }

    /**
     * Insert a new element at end of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static char[] add(char[] arr, char e) {
        return insertAt(arr, e, arr.length);
    }

    /**
     * Insert a new element at end of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static int[] add(int[] arr, int e) {
        return insertAt(arr, e, arr.length);
    }

    /**
     * Insert a new element at end of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static long[] add(long[] arr, long e) {
        return insertAt(arr, e, arr.length);
    }

    /**
     * Insert a new element at end of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static float[] add(float[] arr, float e) {
        return insertAt(arr, e, arr.length);
    }

    /**
     * Insert a new element at end of array.
     * @param arr target array
     * @param e   new element to array
     * @return new array.
     */
    public static double[] add(double[] arr, double e) {
        return insertAt(arr, e, arr.length);
    }
    //endregion

    //region select
    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <I> array type
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <I, O> O[] select(I[] arr, Function<I, O> func) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(func);
        if(arr.length == 0) {
            return (O[]) Array.newInstance(
                    arr.getClass().getComponentType(),
                    arr.length);
        }

        O aux   = func.apply(arr[0]);
        O[] out = (O[])
                (aux != null ? Array.newInstance(aux.getClass(), arr.length) :
                new Object[arr.length]);
        out[0]  = aux;

        for (int i = 1, l = arr.length; i < l; i++) {
            out[i] = func.apply(arr[i]);
        }

        return out;
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(byte[] arr, Function<Byte, O> func) {
        return selectInternal(arr, func, byte[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(boolean[] arr, Function<Byte, O> func) {
        return selectInternal(arr, func, boolean[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(char[] arr, Function<Character, O> func) {
        return selectInternal(arr, func, char[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(short[] arr, Function<Short, O> func) {
        return selectInternal(arr, func, short[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(int[] arr, Function<Integer, O> func) {
        return selectInternal(arr, func, int[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(long[] arr, Function<Long, O> func) {
        return selectInternal(arr, func, long[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(float[] arr, Function<Float, O> func) {
        return selectInternal(arr, func, float[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <O> result array type
     * @return a new array within selection.
     */
    public static <O> O[] select(double[] arr, Function<Double, O> func) {
        return selectInternal(arr, func, double[]::new);
    }

    /**
     * Select current array objects data.
     * @param arr target array
     * @param func select function
     * @param <I> array type
     * @param <O> result array type
     * @return a new array within selection.
     */
    private static <I, O> O[] selectInternal(Object arr,
                                             Function<I, O> func,
                                             Function<Integer, Object> newArrayFunction) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(func);
        int length = Array.getLength(arr);
        if(length == 0) {
            return (O[]) newArrayFunction.apply(length);
        }

        O aux   = func.apply((I) Array.get(arr, 0));
        O[] out = (O[])
                (aux != null ? Array.newInstance(aux.getClass(), length) :
                        new Object[length]);
        out[0]  = aux;


        for (int i = 1; i < length; i++) {
            out[i] = func.apply((I) Array.get(arr, i));
        }

        return out;
    }
    //endregion

    //region filter
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
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static boolean[] filter(boolean[] args, Filter<Boolean> where) {
        return (boolean[]) filter(args, Boolean[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static char[] filter(char[] args, Filter<Character> where) {
        return (char[]) filter(args, Character[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static short[] filter(short[] args, Filter<Short> where) {
        return (short[]) filter(args, Short[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static int[] filter(int[] args, Filter<Integer> where) {
        return (int[]) filter(args, Integer[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static long[] filter(long[] args, Filter<Long> where) {
        return (long[]) filter(args, Long[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static float[] filter(float[] args, Filter<Float> where) {
        return (float[]) filter(args, Float[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static double[] filter(double[] args, Filter<Double> where) {
        return (double[]) filter(args, Double[]::new, where);
    }

    /**
     * Filter current array.
     * @param arr target
     * @param where condition
     * @return new array with filtered items.
     */
    private static <I> Object filter(Object arr,
                                     Function<Integer, Object> newArrayLengthFun,
                                     Filter<I> where) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(where);
        int len = Array.getLength(arr);
        int offset = 0;

        Object out = newArrayLengthFun.apply(len);

        for (int i=0; i < len; i++) {
            I curr = (I) Array.get(arr, i);
            if (where.accept(curr)) {
                Array.set(out, offset ++, curr);
            }
        }

        return take(out, offset, newArrayLengthFun);
    }
    //endregion

    //region filterAs
    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(I[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(boolean[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(boolean[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(char[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(char[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(short[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(short[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(int[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(int[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(long[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(long[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(float[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(float[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(double[] args, Filter<I> where, Class<I> clazz) {
        return filterAsLocal(args, where, clazz);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static <I> I[] filterAs(double[] args, Filter<I> where) {
        return filterAsLocal(args, where);
    }

    /**
     * Filter current array.
     * @param arr target
     * @param where condition
     * @return new array with filtered items.
     */
    private static <I> I[] filterAsLocal(Object arr, Filter<I> where, Class<I> clazz) {
        return (I[]) filter(arr, len -> Array.newInstance(clazz, len), where);
    }

    /**
     * Filter current array.
     * @param arr target
     * @param where condition
     * @return new array with filtered items.
     */
    private static <I> I[] filterAsLocal(Object arr, Filter<I> where) {
        return (I[]) filter(arr, Object[]::new, where);
    }
    //endregion

    //region first
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
     * First element on array into condition.
     * @param args target
     * @param start start index
     * @param end end index
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> I first(I[] args, int start, int end, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);

        for (int i = start; i < end; i++) {
            if (where.accept(args[i])) {
                return args[i];
            }
        }

        return null;
    }
    //endregion

    //region distinct
    /**
     * Recover distinct (non duplicated) element of array.
     * @param args target
     * @param <I>  element type
     * @return new array with distinct elements.
     */
    @SuppressWarnings("rawtypes")
    public static <I> I[] distinct(I[] args) {
        Objects.requireNonNull(args);

        if(args.length == 0) {
            return args;
        }

        Class<?> clazz = args[0].getClass();
        I[] dist = (I[]) Array.newInstance(clazz, args.length);
        int offset = 0;

        for(I i : args) {
            if(i instanceof Comparable<?>) {
                Comparable comp = (Comparable<?>) i;
                if(any(dist, 0, offset, curr -> comp.compareTo(curr) == 0)){
                    continue;
                }
            } else if(contains(dist, i, 0, offset)) {
                continue;
            }

            dist[offset++] = i;
        }

        if(offset < dist.length) {
            I[] aux = dist;
            dist = (I[]) Array.newInstance(clazz, offset);
            System.arraycopy(aux, 0, dist, 0, dist.length);
        }

        return dist;
    }
    //endregion

    //region reduce
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
     * Apply reduce operation.
     * @param args target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A> A reduceI(I[] args, ReducerIndex<A, I> func, A acc) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(func);
        for(int i=0,l=args.length; i < l; i++) {
            if(i == 0 && acc == null) {
                //noinspection unchecked
                acc = (A) args[i];
            } else {
                acc = func.apply(acc, args[i], i);
            }
        }
        return acc;
    }

    /**
     * Apply reduce operation.
     * @param args target
     * @param func reduce function with index
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A extends I> A reduceI(I[] args, ReducerIndex<A, I> func) {
        return reduceI(args, func, null);
    }
    //endregion

    //region take
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
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static byte[] take(byte[] arr, int count) {
        return (byte[]) take(arr, count, byte[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static boolean[] take(boolean[] arr, int count) {
        return (boolean[]) take(arr, count, boolean[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static short[] take(short[] arr, int count) {
        return (short[]) take(arr, count, short[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static int[] take(int[] arr, int count) {
        return (int[]) take(arr, count, int[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static long[] take(long[] arr, int count) {
        return (long[]) take(arr, count, long[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static float[] take(float[] arr, int count) {
        return (float[]) take(arr, count, float[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    public static double[] take(double[] arr, int count) {
        return (double[]) take(arr, count, double[]::new);
    }

    /**
     * Take a count of elements.
     * @param arr target
     * @param count count to take
     * @return new array.
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object take(Object arr, int count,
                               Function<Integer, Object> newArrayLengthFun) {
        if(count >= Array.getLength(Objects.requireNonNull(arr))) {
            return arr;
        } else {
            Object out = newArrayLengthFun.apply(count);
            System.arraycopy(arr, 0, out, 0, count);
            return  out;
        }
    }
    //endregion

    //region jump
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
    //endregion

    //region all
    /**
     * Check if all elements on array pass on test action.
     * @param args target
     * @param start start index
     * @param end end length
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean all(I[] args, int start, int end, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);

        for (int i = start; i < end; i++) {
            if (!where.accept(args[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if all elements on array pass on test action.
     * @param args target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean all(I[] args, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);

        for (I arg : args) {
            if (!where.accept(arg)) {
                return false;
            }
        }

        return true;
    }
    //endregion

    //region any
    /**
     * Check if at least one element on array pass on test action.
     * @param args target
     * @param start start index
     * @param end end length
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean any(I[] args, int start, int end, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);

        for (int i = start; i < end; i++) {
            if (where.accept(args[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if at least one array on iterable pass on test action.
     * @param args target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean any(I[] args, Filter<I> where) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(where);

        for (I arg : args) {
            if (where.accept(arg)) {
                return true;
            }
        }

        return false;
    }
    //endregion

    //region contains
    /**
     * Check if current array contains target element.
     * @param args target array
     * @param e target element to find
     * @param <I> element type
     * @return true, element exists on array, otherwise, false.
     */
    public static <I> boolean contains(I[] args, I e){
        return indexOf(args, e) > -1;
    }

    /**
     * Check if current array contains target element.
     * @param args target array
     * @param e target element to find
     * @param start start index to search
     * @param end end length to search
     * @param <I> element type
     * @return true, element exists on array, otherwise, false.
     */
    public static <I> boolean contains(I[] args, I e, int start, int end){
        return indexOf(args, e, start, end) > -1;
    }

    /**
     * Check if current array contains target element.
     * @param arr target array
     * @param e target element to find
     * @param <I> element type
     * @return true, element exists on array, otherwise, false.
     */
    public static <I> boolean contains(Object arr, I e){
        return indexOf(arr, e) > -1;
    }

    /**
     * Check if current array contains target element.
     * @param arr target array
     * @param e target element to find
     * @param start start index to search
     * @param end end length to search
     * @param <I> element type
     * @return true, element exists on array, otherwise, false.
     */
    public static <I> boolean contains(Object arr, I e, int start, int end){
        return indexOf(arr, e, start, end) > -1;
    }
    //endregion

    //region sequenceEquals
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

    /**
     * Compare if sequence of arrays are equals.
     * @param arr0 target array
     * @param arr1 target array
     * @return true when both are sequential equals.
     */
    public static boolean sequenceEquals(Object arr0, Object arr1) {
        int len0 = Array.getLength(Objects.requireNonNull(arr0));
        int len1 = Array.getLength(Objects.requireNonNull(arr1));

        if(len0 != len1) {
            return false;
        } else {
            for(int i = 0; i < len0; i ++) {
                if(!Objects.equals(Array.get(arr0, i), Array.get(arr1, i))) {
                    return false;
                }
            }
            return true;
        }
    }
    //endregion

    //region reverse
    /**
     * Reverse position of array elements.
     * @param arr target array
     * @param <I> element type
     */
    public static <I> void reverse(I[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            I aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(boolean[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            boolean aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(short[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            short aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(int[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            int aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(long[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            long aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(byte[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            byte aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(float[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            float aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }

    /**
     * Reverse position of array elements.
     * @param arr target array
     */
    public static void reverse(double[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            double aux = arr[start];
            arr[start] = arr[end];
            arr[end] = aux;
        }
    }
    //endregion
}
