package com.atomatus.util;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Helper to analyze, manipulate and convert array objects.
 */
@SuppressWarnings("unchecked")
public final class ArrayHelper {

    //region callbacks
    /**
     * Simple Function Callback.
     * @param <I> input type
     * @param <O> output type
     */
    @FunctionalInterface
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
    @FunctionalInterface
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
    @FunctionalInterface
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
    @FunctionalInterface
    public interface Filter<I> {
        /**
         * Filter accept callback.
         * @param i target element
         * @return true, element has to be filtered, otherwise, element is ignored.
         */
        boolean accept(I i);
    }
    //endregion

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

    //region copy
    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @param <E> array element type
     * @return new array within same elements references.
     */
    public static <E> E[] copy(E[] arr) {
        return (E[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static byte[] copy(byte[] arr) {
        return (byte[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static boolean[] copy(boolean[] arr) {
        return (boolean[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static char[] copy(char[] arr) {
        return (char[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static short[] copy(short[] arr) {
        return (short[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static int[] copy(int[] arr) {
        return (int[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static long[] copy(long[] arr) {
        return (long[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static float[] copy(float[] arr) {
        return (float[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    public static double[] copy(double[] arr) {
        return (double[]) copyInternal(arr);
    }

    /**
     * Generate an array copy within same elements references.
     * @param arr target array
     * @return new array within same elements references.
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object copyInternal(Object arr) {
        requireArray(arr);
        int len = Array.getLength(arr);
        Object aux = Array.newInstance(arr.getClass().getComponentType(), len);
        System.arraycopy(arr, 0, aux, 0, len);
        return aux;
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
    public static byte[] insertAt(byte[] arr, byte e, int index) {
        return (byte[]) insertAt(arr, e, index, byte[]::new);
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
    public static byte[] push(byte[] arr, byte e) {
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
    public static byte[] add(byte[] arr, byte e) {
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
    public static short[] add(short[] arr, short e) {
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

        if(args.length == 0) {
            return args;
        }

        Class<?> itemClazz = args.getClass().getComponentType();
        I[] out = (I[]) Array.newInstance(itemClazz, args.length);
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
    public static byte[] filter(byte[] args, Filter<Byte> where) {
        return (byte[]) filter(args, byte[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static boolean[] filter(boolean[] args, Filter<Boolean> where) {
        return (boolean[]) filter(args, boolean[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static char[] filter(char[] args, Filter<Character> where) {
        return (char[]) filter(args, char[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static short[] filter(short[] args, Filter<Short> where) {
        return (short[]) filter(args, short[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static int[] filter(int[] args, Filter<Integer> where) {
        return (int[]) filter(args, int[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static long[] filter(long[] args, Filter<Long> where) {
        return (long[]) filter(args, long[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static float[] filter(float[] args, Filter<Float> where) {
        return (float[]) filter(args, float[]::new, where);
    }

    /**
     * Filter current array.
     * @param args target
     * @param where condition
     * @return new array with filtered items.
     */
    public static double[] filter(double[] args, Filter<Double> where) {
        return (double[]) filter(args, double[]::new, where);
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

        if(len == 0) {
            return arr;
        }

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

    //region first
    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> I first(I[] arr, Filter<I> where) {
        return firstInternal(Objects.requireNonNull(arr), 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> I first(I[] arr, int start, int end, Filter<I> where) {
        return firstInternal(Objects.requireNonNull(arr), start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static byte first(byte[] arr, Filter<Byte> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static byte first(byte[] arr, int start, int end, Filter<Byte> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static char first(char[] arr, Filter<Character> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static char first(char[] arr, int start, int end, Filter<Character> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static short first(short[] arr, Filter<Short> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static short first(short[] arr, int start, int end, Filter<Short> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static int first(int[] arr, Filter<Integer> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static int first(int[] arr, int start, int end, Filter<Integer> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static long first(long[] arr, Filter<Long> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static long first(long[] arr, int start, int end, Filter<Long> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static float first(float[] arr, Filter<Float> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static float first(float[] arr, int start, int end, Filter<Float> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static double first(double[] arr, Filter<Double> where) {
        return firstInternalOrThrowsEx(arr, 0, arr.length, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end index
     * @param where condition
     * @return first element into condition or null.
     */
    public static double first(double[] arr, int start, int end, Filter<Double> where) {
        return firstInternalOrThrowsEx(arr, start, end, where);
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    private static <I> I firstInternal(Object arr, int start, int end, Filter<I> where) {
        requireArray(arr);
        Objects.requireNonNull(where);
        end = Math.min(Array.getLength(arr), end);
        for(int i=start; i < end; i++) {
            I curr = (I) Array.get(arr, i);
            if(where.accept(curr)) {
                return curr;
            }
        }
        return null;
    }

    /**
     * First element on array into condition.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    private static <I> I firstInternalOrThrowsEx(Object arr, int start, int end, Filter<I> where) {
        I i = firstInternal(arr, start, end, where);
        if(i == null) throw new RuntimeException("Search condition does not produced no one result!");
        return i;
    }
    //endregion

    //region distinct
    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @param <I>  element type
     * @return new array with distinct elements.
     */
    public static <I> I[] distinct(I[] arr) {
        return (I[]) distinct(arr, i -> Array.newInstance(arr.getClass().getComponentType(), i));
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static byte[] distinct(byte[] arr){
        return (byte[]) distinct(arr, byte[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static boolean[] distinct(boolean[] arr){
        return (boolean[]) distinct(arr, boolean[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static char[] distinct(char[] arr){
        return (char[]) distinct(arr, char[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static short[] distinct(short[] arr){
        return (short[]) distinct(arr, short[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static int[] distinct(int[] arr){
        return (int[]) distinct(arr, int[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static long[] distinct(long[] arr){
        return (long[]) distinct(arr, long[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static float[] distinct(float[] arr){
        return (float[]) distinct(arr, float[]::new);
    }

    /**
     * Recover distinct (non duplicated) element of array.
     * @param arr target
     * @return new array with distinct elements.
     */
    public static double[] distinct(double[] arr){
        return (double[]) distinct(arr, double[]::new);
    }

    @SuppressWarnings({"rawtypes", "SuspiciousSystemArraycopy"})
    private static Object distinct(Object arr, Function<Integer, Object> newArrayFunction) {
        Objects.requireNonNull(arr);
        int len = Array.getLength(arr);
        if(len == 0) return arr;

        Object dist = newArrayFunction.apply(len);
        int offset  = 0;

        for(int i=0; i < len; i++) {
            Object e = Array.get(arr, i);
            if(e instanceof Comparable<?>) {
                Comparable comp = (Comparable<?>) e;
                if(anyInternal(dist, 0, offset, curr -> comp.compareTo(curr) == 0)){
                    continue;
                }
            } else if(contains(dist, i, 0, offset)) {
                continue;
            }

            Array.set(dist, offset++, e);
        }

        if(offset < len) {
            Object aux = dist;
            dist = newArrayFunction.apply(offset);
            System.arraycopy(aux, 0, dist, 0, offset);
        }

        return dist;
    }
    //endregion

    //region reduce

    //region reduce object
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A> A reduce(I[] arr, Reducer<A, I> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A extends I> A reduce(I[] arr, Reducer<A, I> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A> A reduceI(I[] arr, ReducerIndex<A, I> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <I, A extends I> A reduceI(I[] arr, ReducerIndex<A, I> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce byte
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(byte[] arr, Reducer<A, Byte> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduce(byte[] arr, Reducer<A, Byte> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(byte[] arr, ReducerIndex<A, Byte> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduceI(byte[] arr, ReducerIndex<A, Byte> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce boolean
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(boolean[] arr, Reducer<A, Boolean> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @return accumulator result.
     */
    public static boolean reduce(boolean[] arr, Reducer<Boolean, Boolean> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(boolean[] arr, ReducerIndex<A, Boolean> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @return accumulator result.
     */
    public static boolean reduceI(boolean[] arr, ReducerIndex<Boolean, Boolean> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce char
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(char[] arr, Reducer<A, Character> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @return accumulator result.
     */
    public static char reduce(char[] arr, Reducer<Character, Character> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(char[] arr, ReducerIndex<A, Character> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @return accumulator result.
     */
    public static Character reduceI(char[] arr, ReducerIndex<Character, Character> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce short
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(short[] arr, Reducer<A, Short> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduce(short[] arr, Reducer<A, Short> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(short[] arr, ReducerIndex<A, Short> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduceI(short[] arr, ReducerIndex<A, Short> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce int
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(int[] arr, Reducer<A, Integer> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduce(int[] arr, Reducer<A, Integer> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(int[] arr, ReducerIndex<A, Integer> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduceI(int[] arr, ReducerIndex<A, Integer> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce long
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(long[] arr, Reducer<A, Long> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduce(long[] arr, Reducer<A, Long> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(long[] arr, ReducerIndex<A, Long> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduceI(long[] arr, ReducerIndex<A, Long> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce float
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(float[] arr, Reducer<A, Float> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduce(float[] arr, Reducer<A, Float> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(float[] arr, ReducerIndex<A, Float> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduceI(float[] arr, ReducerIndex<A, Float> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce double
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduce(double[] arr, Reducer<A, Double> func, A acc) {
        return reduceInternal(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduce(double[] arr, Reducer<A, Double> func) {
        return reduceInternal(arr, func, null);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A> A reduceI(double[] arr, ReducerIndex<A, Double> func, A acc) {
        return reduceInternalI(arr, func, acc);
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    public static <A extends Number> A reduceI(double[] arr, ReducerIndex<A, Double> func) {
        return reduceInternalI(arr, func, null);
    }
    //endregion

    //region reduce internal
    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function with index
     * @param acc accumulator
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    private static <I, A> A reduceInternalI(Object arr, ReducerIndex<A, I> func, A acc) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(func);
        int length = Array.getLength(arr);
        for(int i = 0; i < length; i++) {
            if(i == 0 && acc == null) {
                acc = (A) Array.get(arr, i);
            } else {
                acc = func.apply(acc, (I) Array.get(arr, i), i);
            }
        }
        return acc;
    }

    /**
     * Apply reduce operation.
     * @param arr target
     * @param func reduce function
     * @param acc accumulator
     * @param <I> array element type
     * @param <A> accumulator element type
     * @return accumulator result.
     */
    private static <I, A> A reduceInternal(Object arr, Reducer<A, I> func, A acc) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(func);
        int length = Array.getLength(arr);
        for(int i = 0; i < length; i++) {
            if(i == 0 && acc == null) {
                acc = (A) Array.get(arr, i);
            } else {
                acc = func.apply(acc, (I) Array.get(arr, i));
            }
        }
        return acc;
    }
    //endregion

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
        return (I[]) take(arr, count, l -> Array.newInstance(arr.getClass().getComponentType(), l));
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
    public static char[] take(char[] arr, int count) {
        return (char[]) take(arr, count, char[]::new);
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
        if(count < 0) {
            throw new IndexOutOfBoundsException();
        } else if(count == 0) {
            return newArrayLengthFun.apply(0);
        } else if(count >= Array.getLength(Objects.requireNonNull(arr))) {
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
        return (I[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static byte[] jump(byte[] arr, int count) {
        return (byte[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static boolean[] jump(boolean[] arr, int count) {
        return (boolean[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static char[] jump(char[] arr, int count) {
        return (char[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static short[] jump(short[] arr, int count) {
        return (short[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static int[] jump(int[] arr, int count) {
        return (int[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static long[] jump(long[] arr, int count) {
        return (long[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static float[] jump(float[] arr, int count) {
        return (float[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    public static double[] jump(double[] arr, int count) {
        return (double[]) jumpInternal(arr, count);
    }

    /**
     * Ignore a count of element.
     * @param arr target
     * @param count count to be ignored
     * @return new array after count.
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object jumpInternal(Object arr, int count) {
        Objects.requireNonNull(arr);

        if(count <= 0) {
            return arr;
        }

        int length = Array.getLength(arr);
        if(count >= length) {
            return Array.newInstance(arr.getClass().getComponentType(), 0);
        } else {
            int diff = length - count;
            Object out = Array.newInstance(arr.getClass().getComponentType(), diff);
            System.arraycopy(arr, count, out, 0, diff);
            return  out;
        }
    }
    //endregion

    //region all

    //region all object
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean all(I[] arr, int start, int end, Filter<I> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean all(I[] arr, Filter<I> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all byte
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(byte[] arr, int start, int end, Filter<Byte> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(byte[] arr, Filter<Byte> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all boolean
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(boolean[] arr, int start, int end, Filter<Boolean> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(boolean[] arr, Filter<Boolean> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all char
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(char[] arr, int start, int end, Filter<Character> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(char[] arr, Filter<Character> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all short
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(short[] arr, int start, int end, Filter<Short> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(short[] arr, Filter<Short> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all int
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(int[] arr, int start, int end, Filter<Integer> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(int[] arr, Filter<Integer> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all long
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(long[] arr, int start, int end, Filter<Long> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(long[] arr, Filter<Long> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all float
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(float[] arr, int start, int end, Filter<Float> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(float[] arr, Filter<Float> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all double
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(double[] arr, int start, int end, Filter<Double> where) {
        return allInternal(arr, start, end, where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean all(double[] arr, Filter<Double> where) {
        return allInternal(arr, where);
    }
    //endregion

    //region all internal
    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean allInternal(Object arr, Filter<I> where) {
        Objects.requireNonNull(arr);
        return allInternal(arr, 0, Array.getLength(arr), where);
    }

    /**
     * Check whether all elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    private static <I> boolean allInternal(Object arr, int start, int end, Filter<I> where) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(where);

        for (int i = start; i < end; i++) {
            I e = (I) Array.get(arr, i);
            if (!where.accept(e)) {
                return false;
            }
        }

        return true;
    }
    //endregion

    //endregion

    //region any

    //region any object
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean any(I[] arr, int start, int end, Filter<I> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean any(I[] arr, Filter<I> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any byte
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(byte[] arr, int start, int end, Filter<Byte> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(byte[] arr, Filter<Byte> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any boolean
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(boolean[] arr, int start, int end, Filter<Boolean> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(boolean[] arr, Filter<Boolean> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any char
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(char[] arr, int start, int end, Filter<Character> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(char[] arr, Filter<Character> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any short
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(short[] arr, int start, int end, Filter<Short> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(short[] arr, Filter<Short> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any int
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(int[] arr, int start, int end, Filter<Integer> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(int[] arr, Filter<Integer> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any long
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(long[] arr, int start, int end, Filter<Long> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(long[] arr, Filter<Long> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any float
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(float[] arr, int start, int end, Filter<Float> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(float[] arr, Filter<Float> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any double
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(double[] arr, int start, int end, Filter<Double> where) {
        return anyInternal(arr, start, end, where);
    }

    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @return first element into condition or null.
     */
    public static boolean any(double[] arr, Filter<Double> where) {
        return anyInternal(arr, where);
    }
    //endregion

    //region any internal
    /**
     * Check whether least one elements on array pass on test action.
     * @param arr target
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    public static <I> boolean anyInternal(Object arr, Filter<I> where) {
        Objects.requireNonNull(arr);
        return anyInternal(arr, 0, Array.getLength(arr), where);
    }

    /**
     * Check if at least one array on iterable pass on test action.
     * @param arr target
     * @param start start index
     * @param end end length
     * @param where condition
     * @param <I> array type
     * @return first element into condition or null.
     */
    private static <I> boolean anyInternal(Object arr, int start, int end, Filter<I> where) {
        for(int i=start; i < end; i++) {
            I e = (I) Array.get(arr, i);
            if(where.accept(e)) {
                return true;
            }
        }
        return false;
    }
    //endregion

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
    public static void reverse(char[] arr) {
        Objects.requireNonNull(arr);
        for(int start=0, end=arr.length-1; start <= end; start++, end--) {
            char aux = arr[start];
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

    //region resize
    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @param <T> array type.
     * @return new array resized.
     */
    public static <T> T[] resize(T[] arr, int len) {
        return (T[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static byte[] resize(byte[] arr, int len) {
        return (byte[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static boolean[] resize(boolean[] arr, int len) {
        return (boolean[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static char[] resize(char[] arr, int len) {
        return (char[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static short[] resize(short[] arr, int len) {
        return (short[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static int[] resize(int[] arr, int len) {
        return (int[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static long[] resize(long[] arr, int len) {
        return (long[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static float[] resize(float[] arr, int len) {
        return (float[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    public static double[] resize(double[] arr, int len) {
        return (double[]) resizeInternal(arr, len);
    }

    /**
     * Resize target array to new length.
     * @param arr target array
     * @param len new array length
     * @return new array resized.
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object resizeInternal(Object arr, int len) {
        int arrLen = Array.getLength(Objects.requireNonNull(arr));
        if(len <= 0) {
            throw new IllegalArgumentException("Length can not be less or equals 0 (zero)!");
        }
        Object aux = Array.newInstance(arr.getClass().getComponentType(), len);
        System.arraycopy(arr, 0, aux, 0, Math.min(len, arrLen));
        return aux;
    }
    //endregion

    //region join
    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @param <T> array type
     * @return joined arrays
     */
    public static <T> T[] join(T[] arr, T[]... args) {
        return (T[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @param <T> array type
     * @return joined arrays
     */
    public static <T> T[] join(T[] arr, T[] arg, int start, int end) {
        return (T[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static byte[] join(byte[] arr, byte[]... args) {
        return (byte[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static byte[] join(byte[] arr, byte[] arg, int start, int end) {
        return (byte[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static boolean[] join(boolean[] arr, boolean[]... args) {
        return (boolean[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static boolean[] join(boolean[] arr, boolean[] arg, int start, int end) {
        return (boolean[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static char[] join(char[] arr, char[]... args) {
        return (char[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static char[] join(char[] arr, char[] arg, int start, int end) {
        return (char[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static short[] join(short[] arr, short[]... args) {
        return (short[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static short[] join(short[] arr, short[] arg, int start, int end) {
        return (short[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static int[] join(int[] arr, int[]... args) {
        return (int[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static int[] join(int[] arr, int[] arg, int start, int end) {
        return (int[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static long[] join(long[] arr, long[]... args) {
        return (long[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static long[] join(long[] arr, long[] arg, int start, int end) {
        return (long[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static float[] join(float[] arr, float[]... args) {
        return (float[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static float[] join(float[] arr, float[] arg, int start, int end) {
        return (float[]) joinInternal(arr, arg, start, end);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param args targets to join
     * @return joined arrays
     */
    public static double[] join(double[] arr, double[]... args) {
        return (double[]) joinInternal(arr, args);
    }

    /**
     * Join arrays.
     * @param arr current array
     * @param arg target array to join
     * @param start start index of target array
     * @param end count of element of target array to join.
     * @return joined arrays
     */
    public static double[] join(double[] arr, double[] arg, int start, int end) {
        return (double[]) joinInternal(arr, arg, start, end);
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object joinInternal(Object arr, Object arg, int start, int end) {
        int arrLen = Array.getLength(Objects.requireNonNull(arr));
        int argLen = Array.getLength(Objects.requireNonNull(arg)) - start;

        if(start < 0 || start >= argLen || start > end || end > argLen) {
            throw new IndexOutOfBoundsException();
        } else if((argLen = end) == 0) {
            return arr;
        }

        int len     = arrLen + argLen;
        Object res  = Array.newInstance(arr.getClass().getComponentType(), len);
        System.arraycopy(arr, 0, res, 0, arrLen);
        System.arraycopy(arg, start, res, arrLen, argLen);
        return res;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object joinInternal(Object arr, Object args) {
        Objects.requireNonNull(arr);

        int arrLen  = Array.getLength(arr);//arr length
        int argsLen = Array.getLength(args); //args length

        if(argsLen == 0) {
            return arr;
        }

        int len = reduceInternal(args, (acc, curr) -> acc + Array.getLength(curr), arrLen);//accumulate length

        if(len == arrLen) {
            return arr;
        }

        //result.
        Object result = Array.newInstance(arr.getClass().getComponentType(), len);

        //from arr to result.
        int offset = 0;
        System.arraycopy(arr, 0, result, offset, offset += arrLen);

        //from each args to result.
        for (int i=0; i < argsLen; i++) {
            Object argArr  = Array.get(args, i);
            int currArgLen = Array.getLength(argArr);
            System.arraycopy(argArr, 0, result, offset, currArgLen);
            offset += currArgLen;
        }

        return result;
    }
    //endregion
}
