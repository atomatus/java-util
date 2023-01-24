package com.atomatus.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Java Warning Logger State class allow you to disable or reenable internal
 * java warning log displaying when building or testing your app using some
 * request not indicated, how like, in reflection to change access level of
 * some member, or it is deprecated.
 * @author Carlos Matos {@literal @chcmatos}
 */
public final class WarningLoggerState {

    //region callbacks
    /**
     * Consumer callback.
     */
    @FunctionalInterface
    public interface Consumer {
        /**
         * Action.
         */
        void action();
    }

    /**
     * Consumer callback.
     * @param <I> input type
     */
    @FunctionalInterface
    public interface ConsumerI<I> {
        /**
         * Action
         * @param input input value
         */
        void action(I input);
    }

    /**
     * Function callback.
     * @param <O> output type
     */
    @FunctionalInterface
    public interface Function<O> {
        /**
         * Apply
         * @return result
         */
        O apply();
    }

    /**
     * Function callback.
     * @param <I> input type
     * @param <O> output type
     */
    @FunctionalInterface
    public interface FunctionIO<I, O> {
        /**
         * Apply
         * @param input input value
         * @return result
         */
        O apply(I input);
    }
    //endregion

    private static final WarningLoggerState instance;
    private final Object locker;

    private transient Object unsafe, logger;
    private transient Long offset;
    private transient Class<?> loggerClass;
    private transient Method staticFieldOffset, getObjectVolatile, putObjectVolatile;

    /**
     * Singleton instance of WarningLoggerState.
     * @return singleton instance.
     */
    public static WarningLoggerState getInstance() {
        return instance;
    }

    static {
        instance = new WarningLoggerState();
    }

    /**
     * Default constructor.
     */
    private WarningLoggerState() {
        locker = new Object();
    }

    /**
     * Check and load access to internal Logger.
     * @throws NoSuchFieldException throws when field name is not found.
     * @throws IllegalAccessException throws when does not have permission.
     * @throws ClassNotFoundException throws when class path is invalid.
     * @throws NoSuchMethodException throws when method is not found.
     * @throws InvocationTargetException throw when method invocation is invalid, parameter invalid.
     */
    private void loadValues() throws NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        if (unsafe == null || staticFieldOffset == null || getObjectVolatile == null || putObjectVolatile == null) {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);

            staticFieldOffset = unsafeClass.getMethod("staticFieldOffset", Field.class);
            getObjectVolatile = unsafeClass.getMethod("getObjectVolatile", Object.class, long.class);
            putObjectVolatile = unsafeClass.getMethod("putObjectVolatile", Object.class, long.class, Object.class);

            unsafe = theUnsafe.get(null);
            loggerClass = null;
        }

        if (loggerClass == null) {
            loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            offset = null;
        }

        if (offset == null) {
            Field logger = loggerClass.getDeclaredField("logger");
            offset = (Long) staticFieldOffset.invoke(unsafe, logger);
        }
    }

    //region disable/reenable
    /**
     * Disable warning logger.
     */
    public void disable() {
        synchronized (locker) {
            try {
                if (logger == null) {
                    loadValues();
                    logger = getObjectVolatile.invoke(unsafe, loggerClass, offset);
                    putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Reenable warning logger.
     */
    public void reenable() {
        synchronized (locker) {
            try {
                if (logger != null) {
                    loadValues();
                    putObjectVolatile.invoke(unsafe, loggerClass, offset, logger);
                    logger = null;
                }
            } catch (Exception ignored) {
            }
        }
    }
    //endregion

    //region suppressWarningsFor

    /**
     * Suppress java warnings for target action execution.
     * @param consumer action target.
     */
    public void suppressWarningsFor(Consumer consumer) {
        Objects.requireNonNull(consumer);
        try{
            disable();
            consumer.action();
        } finally {
            reenable();
        }
    }

    /**
     * Suppress java warnings for target action execution.
     * @param consumer action callback target.
     * @param input input data
     * @param <T> input type
     */
    public <T> void suppressWarningsFor(ConsumerI<T> consumer, T input) {
        Objects.requireNonNull(consumer);
        try{
            disable();
            consumer.action(input);
        } finally {
            reenable();
        }
    }

    /**
     * Suppress java warnings for target function execution.
     * @param function function callback target.
     * @param <O> output type
     * @return output data generated for callback.
     */
    public <O> O suppressWarningsFor(Function<O> function) {
        Objects.requireNonNull(function);
        try{
            disable();
            return function.apply();
        } finally {
            reenable();
        }
    }

    /**
     * Suppress java warnings for target function execution.
     * @param function function callback target.
     * @param input input data
     * @param <I> input type
     * @param <O> output type
     * @return output data generated for callback.
     */
    public <I, O> O suppressWarningsFor(FunctionIO<I, O> function, I input) {
        Objects.requireNonNull(function);
        try{
            disable();
            return function.apply(input);
        } finally {
            reenable();
        }
    }
    //endregion
}
