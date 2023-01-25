package com.atomatus.util.lazy;

import junit.framework.TestCase;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public class LazyReferenceTest extends TestCase {

    public void testSoft() {
        LazyReference<Object> ref = LazyReference.soft(Object::new);
        ref.get();

        assertEquals(ref.get(), ref.get());

        ref.clear();
        assertNull(ref.get());

        LazyReference<Object> ref2 = LazyReference.soft(Object::new);
        ref2.get();

        assertNotSame(ref.get(), ref2.get());
    }

    public void testWeak() {
        LazyReference<Object> ref = LazyReference.weak(Object::new);
        ref.get();

        assertEquals(ref.get(), ref.get());

        ref.clear();
        assertNull(ref.get());

        LazyReference<Object> ref2 = LazyReference.weak(Object::new);
        ref2.get();

        assertNotSame(ref.get(), ref2.get());
    }

    public void testWeakCollectedByGC() {
        LazyReference<Object> ref = LazyReference.weak(Object::new);
        ref.get();//recover data.
        try {
            System.gc();//request to be collect.
            Thread.sleep(100);//delay to collect.
        } catch (InterruptedException ignored) { }
        assertNull(ref.get());//data collected.
    }

    public void testRevive() {
        LazyReferenceRevive<Object> ref = LazyReference.weak(Object::new).revive();
        Object data = ref.get();
        try {
            System.gc();//request to be collect.
            Thread.sleep(100);//delay to collect.
        } catch (InterruptedException ignored) { }
        assertNotNull(ref.get());//data collected revive.
        assertEquals(data, ref.get());
    }
}