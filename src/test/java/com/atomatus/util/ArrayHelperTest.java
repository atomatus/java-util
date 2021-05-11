package com.atomatus.util;

import junit.framework.TestCase;
import org.junit.Assert;

public class ArrayHelperTest extends TestCase {

    public void testClear() {
        int[] arr0 = new int[]{11, 22, 33};
        ArrayHelper.clear(arr0);
        Assert.assertArrayEquals(arr0, new int[]{0, 0, 0});

        float[] arr1 = new float[]{11.5f, 22.5f, 33.5f};
        ArrayHelper.clear(arr1);
        Assert.assertArrayEquals(arr1, new float[]{0f, 0f, 0f}, 0f);

        Double[] arr2 = new Double[]{11.5d, 22.5d, 33.5d};
        ArrayHelper.clear(arr2);
        Assert.assertArrayEquals(arr2, new Double[]{null, null, null});
    }

    public void testIndexOf() {
        int i = ArrayHelper.indexOf(new Integer[]{0, 3, 5}, 3);
        assertEquals(i, 1);

        i = ArrayHelper.indexOf(new int[]{0, 3, 5}, 3);
        assertEquals(i, 1);
    }

    public void testToArray() {
        Object[] arr0 = ArrayHelper.toArray(new Double[]{0.11d, 0.22d});
        assertEquals(arr0[0], 0.11d);
        assertEquals(arr0[1], 0.22d);

        Double[] arr1 = ArrayHelper.toArray(new double[]{0.11d, 0.22d}, Double.class);
        assertEquals(arr1[0], 0.11d);
        assertEquals(arr1[1], 0.22d);
    }

    public void testInsertAt() {
        Float[] arr0 = ArrayHelper.insertAt(new Float[]{4f, 5f, 7f}, 6f, 2);
        Assert.assertArrayEquals(arr0, new Float[]{4f, 5f, 6f, 7f});

        int[] arr1 = ArrayHelper.insertAt(new int[]{4, 5, 7}, 6, 2);
        Assert.assertArrayEquals(arr1, new int[]{4, 5, 6, 7});
    }

    public void testPush() {
        Float[] arr0 = ArrayHelper.push(new Float[]{4f, 5f, 6f}, 3f);
        Assert.assertArrayEquals(arr0, new Float[]{3f, 4f, 5f, 6f});

        int[] arr1 = ArrayHelper.push(new int[]{4, 5, 6}, 3);
        Assert.assertArrayEquals(arr1, new int[]{3, 4, 5, 6});
    }

    public void testAdd() {
        Float[] arr0 = ArrayHelper.add(new Float[]{4f, 5f}, 6f);
        Assert.assertArrayEquals(arr0, new Float[]{4f, 5f, 6f});

        int[] arr1 = ArrayHelper.add(new int[]{4, 5}, 6);
        Assert.assertArrayEquals(arr1, new int[]{4, 5, 6});
    }

    public void testSelect() {
        Double[] arr0 = ArrayHelper.select(new Integer[]{0, 1}, i -> i + .2d);
        Assert.assertArrayEquals(arr0, new Double[]{0.2d, 1.2d});

        Double[] arr1 = ArrayHelper.select(new int[]{0, 1}, i -> i + .2d);
        Assert.assertArrayEquals(arr1, new Double[]{0.2d, 1.2d});

        String[] arr2 = ArrayHelper.select(new int[]{0, 1}, Object::toString);
        Assert.assertArrayEquals(arr2, new String[]{ "0", "1" });
    }

    public void testFilter() {
        Double[] arr0 = ArrayHelper.filter(new Double[]{0d, 1d, 2d, 3d}, i -> i % 2 == 0);
        Assert.assertArrayEquals(arr0, new Double[]{0d, 2d});

        double[] arr1 = ArrayHelper.filter(new double[]{0d, 1d, 2d, 3d}, i -> i % 2 == 0);
        Assert.assertArrayEquals(arr1, new double[]{0d, 2d}, 0d);
    }

    public void testFirst() {
        Float f0 = ArrayHelper.first(new Float[] { .01f, .03f, .02f}, f -> f == .03f);
        assertEquals(f0, .03f);

        float f1 = ArrayHelper.first(new float[] { .01f, .03f, .02f}, f -> f == .03f);
        assertEquals(f1, .03f);
    }

    public void testDistinct() {
        Float[] arr0 = ArrayHelper.distinct(new Float[] { 0f, 1f, 2f, 3f, 3f, 4f, 4f, 5f, 6f, 7f, 7f, 6f, 1f, 8f, 9f, 0f});
        Assert.assertArrayEquals(arr0, new Float[] {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f});

        float[] arr1 = ArrayHelper.distinct(new float[] { 0f, 1f, 2f, 3f, 3f, 4f, 4f, 5f, 6f, 7f, 7f, 6f, 1f, 8f, 9f, 0f});
        Assert.assertArrayEquals(arr1, new float[] {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f}, 0f);
    }

    public void testReduce() {
        int ti = ArrayHelper.reduce(new int[]{ 0, 1, 2, 3, 4, 5}, Integer::sum);
        double td = ArrayHelper.reduce(new int[]{ 0, 1, 2, 3, 4, 5}, Double::sum, 0d);
        StringBuilder sb = ArrayHelper.reduce(new int[]{ 0, 1, 2, 3, 4, 5}, StringBuilder::append, new StringBuilder());
        float tf = ArrayHelper.reduce(new float[]{ 0, 1, 2, 3, 4, 5}, Float::sum);

        assertEquals(ti, 15);
        assertEquals(td, 15d);
        assertEquals(tf, 15f);
        assertEquals(sb.toString(), "012345");
    }

    public void testTake(){
        int[] arr0 = ArrayHelper.take(new int[] {0, 1, 2, 3}, 2);
        float[] arr1 = ArrayHelper.take(new float[] {0.1f, 1.2f, 2.3f, 3.4f}, 3);

        String[] strArr;
        String[] arr2 = ArrayHelper.take(strArr = new String[] { "a", "b" }, 3);

        Assert.assertArrayEquals(new int[] {0, 1}, arr0);
        Assert.assertArrayEquals(new float[] {0.1f, 1.2f, 2.3f}, arr1, 0f);

        assertEquals(strArr, arr2);
    }

    public void testJump() {
        byte[] arr0 = ArrayHelper.jump(new byte[]{ 0x01, 0x02, 0x04}, 1);
        Assert.assertArrayEquals(new byte[]{ 0x02, 0x04 }, arr0);

        Long[] arr1 = ArrayHelper.jump(new Long[]{ 1L, 2L, 4L}, 1);
        Assert.assertArrayEquals(new Long[]{ 2L, 4L }, arr1);
    }

    public void testAll() {
        boolean allOdd = ArrayHelper.all(new Double[]{1d, 5d, 3d}, i -> i % 2 != 0);
        assertTrue(allOdd);

        boolean allEven = ArrayHelper.all(new int[]{0, 1, 2, 3}, i -> i % 2 == 0);
        assertFalse(allEven);
    }

    public void testAny() {
        boolean anyOdd = ArrayHelper.any(new Double[]{1d, 5d, 3d}, i -> i % 2 != 0);
        assertTrue(anyOdd);

        boolean anyEven = ArrayHelper.any(new int[]{0, 1, 2, 3}, i -> i % 2 == 0);
        assertTrue(anyEven);
    }

    public void testContains(){
        boolean contains = ArrayHelper.contains(new int[] {1,3,5,9}, 8);
        assertFalse(contains);

        contains = ArrayHelper.contains(new int[] {1,3,5,9}, 9);
        assertTrue(contains);

        contains = ArrayHelper.contains(new Double[] {.0d, .2d, .4d, .8d}, .8d);
        assertTrue(contains);
    }

    public void testSequenceEquals() {
        boolean seq = ArrayHelper.sequenceEquals(new int[] {1, 2, 3}, new int[]{ 1, 2, 3});
        assertTrue(seq);

        seq = ArrayHelper.sequenceEquals(new int[] {1, 2, 3}, new long[]{ 1L, 2L, 3L});
        assertFalse(seq);

        seq = ArrayHelper.sequenceEquals(new int[] {1, 2, 3}, new int[]{ 1, 2, 4 });
        assertFalse(seq);
    }

    public void testReverse() {
        int[] arr0 = new int[] { 3, 2, 1 };
        ArrayHelper.reverse(arr0);
        Assert.assertArrayEquals(arr0, new int[] { 1, 2, 3 });

        Long[] arr1 = new Long[] { 3L, 2L, 1L };
        ArrayHelper.reverse(arr1);
        Assert.assertArrayEquals(arr1, new Long[] { 1L, 2L, 3L });
    }

    public void testResize() {
        byte[] arr0 = ArrayHelper.resize(new byte[]{ 0x01, 0x02, 0x04}, 2);
        Assert.assertArrayEquals(new byte[]{ 0x01, 0x02 }, arr0);

        Long[] arr1 = ArrayHelper.resize(new Long[]{ 1L, 2L, 4L}, 4);
        Assert.assertArrayEquals(new Long[]{ 1L, 2L, 4L, null }, arr1);
    }

    public void testJoin() {
        Integer[] arr0 = new Integer[] {0, 1};
        Integer[] arr1 = new Integer[] {2, 3, 4, 5};
        Integer[] arr2 = ArrayHelper.join(arr0, arr1);
        Assert.assertArrayEquals(new Integer[] {0, 1, 2, 3, 4, 5}, arr2);

        byte[] arr3 = new byte[] {0, 1};
        byte[] arr4 = new byte[] {2, 3, 4};
        byte[] arr5 = new byte[] {5};
        byte[] arr6 = ArrayHelper.join(arr3, arr4, arr5);
        Assert.assertArrayEquals(new byte[] {0, 1, 2, 3, 4, 5}, arr6);

        arr3 = new byte[] {0, 1};
        arr4 = new byte[] {2, 3, 4};
        arr5 = ArrayHelper.join(arr3, arr4, 1, 2);
        Assert.assertArrayEquals(new byte[] {0, 1, 3, 4}, arr5);
    }

}
