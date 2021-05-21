package com.atomatus.util;

import junit.framework.TestCase;

public class LazyFactoryTest extends TestCase {

    private Object callbackDefault(String[] args) {
        return args.length == 2 && "Test".equalsIgnoreCase(args[0]) && "Default".equalsIgnoreCase(args[1]) ?
                new Object() : null;
    }

    public void testCreateNullArgument() {
        try {
            LazyFactory.create((Lazy.LazyFunction<Object, Object>) null);
        } catch (Exception e){
            assertTrue(e instanceof NullPointerException);
        }
    }

    public void testCreateSuccessfully() {
        Lazy<String, Object> lazy = LazyFactory.create(this::callbackDefault);
        assertFalse(lazy.isValueCreated());
        Object ref0 = lazy.value("Test Default".split(" "));
        assertNotNull(ref0);
        assertTrue(lazy.isValueCreated());

        Object ref1 = lazy.value();
        assertNotNull(ref1);
        assertSame(ref0, ref1);

        lazy.reset();
        assertFalse(lazy.isValueCreated());

        Object ref2 = lazy.value("Test Default".split(" "));
        assertNotNull(ref2);
        assertNotSame(ref0, ref2);
    }
}