package com.atomatus.util;

import junit.framework.TestCase;
import org.junit.Assert;

public class ArrayHelperTest extends TestCase {

    public void testClear() {
        int[] arr0 = new int[]{ 11,22,33 };
        ArrayHelper.clear(arr0);
        Assert.assertArrayEquals(arr0, new int[]{ 0,0,0 });

        float[] arr1 = new float[]{ 11.5f, 22.5f, 33.5f };
        ArrayHelper.clear(arr1);
        Assert.assertArrayEquals(arr1, new float[]{ 0f, 0f, 0f }, 0f);

        Double[] arr2 = new Double[]{ 11.5d, 22.5d, 33.5d };
        ArrayHelper.clear(arr2);
        Assert.assertArrayEquals(arr2, new Double[]{ null, null, null });
    }

    public void testIndexOf() {
        int i = ArrayHelper.indexOf(new Integer[]{ 0, 3, 5 }, 3);
        assertEquals(i, 1);

        i = ArrayHelper.indexOf(new int[]{ 0, 3, 5 }, 3);
        assertEquals(i, 1);
    }

    public void testToArray() {
        Object[] arr0 = ArrayHelper.toArray(new Double[]{ 0.11d, 0.22d });
        assertEquals(arr0[0], 0.11d);
        assertEquals(arr0[1], 0.22d);

        Double[] arr1 = ArrayHelper.toArray(new double[]{ 0.11d, 0.22d }, Double.class);
        assertEquals(arr1[0], 0.11d);
        assertEquals(arr1[1], 0.22d);
    }

    public void testInsertAt() {
        Float[] arr0 = ArrayHelper.insertAt(new Float[]{4f, 5f, 7f}, 6f, 2);
        Assert.assertArrayEquals(arr0, new Float[] { 4f, 5f, 6f, 7f});

        int[] arr1 = ArrayHelper.insertAt(new int[]{4, 5, 7}, 6, 2);
        Assert.assertArrayEquals(arr1, new int[] { 4, 5, 6, 7});
    }

    public void testPush(){
        Float[] arr0 = ArrayHelper.push(new Float[]{4f, 5f, 6f}, 3f);
        Assert.assertArrayEquals(arr0, new Float[] { 3f, 4f, 5f, 6f});

        int[] arr1 = ArrayHelper.push(new int[]{ 4, 5, 6}, 3);
        Assert.assertArrayEquals(arr1, new int[] { 3, 4, 5, 6});
    }

    public void testAdd(){
        Float[] arr0 = ArrayHelper.add(new Float[]{4f, 5f}, 6f);
        Assert.assertArrayEquals(arr0, new Float[] { 4f, 5f, 6f});

        int[] arr1 = ArrayHelper.add(new int[]{ 4, 5 }, 6);
        Assert.assertArrayEquals(arr1, new int[] { 4, 5, 6 });
    }

}
