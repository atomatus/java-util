package com.atomatus.util;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Inflate (create instance or load access for static class) a class by fullName (included package path)
 * to try manipulate and access it.
 */
public abstract class Reflection {

    private Reflection() { }

    /**
     * Check if target class was inflated successfully.
     * @return true while class is loaded.
     */
    public abstract boolean inflated();

    /**
     * Method executed on target inflated class. Might return a
     * new Reflection within result when target method has a return, otherwise,
     * return current loaded class that executed it.
     * @param methodName target method name of current class loaded.
     * @param args method arguments
     * @return return current class loaded when method is void,
     * otherwise, return new reflection for result generated.
     */
    public abstract Reflection method(String methodName, Object... args);

    /**
     * Request field from current inflated class.
     * @param fieldName declared field name
     * @return current field inflated value.
     */
    public abstract Reflection field(String fieldName);

    /**
     * Deflate current target. When an instance of object, maybe calling {@link Closeable#close()},
     * {@link HttpURLConnection#disconnect()}, etc. Otherwise for static class is only
     * unreferenced it.
     */
    public abstract void deflate();

    /**
     * Configure current reflection and chain's generated reflection by it to
     * self deflate after method return some result or value request.
     * @return current reflection configured to self deflate.
     */
    public abstract Reflection configDeflateAfterReturns();

    /**
     * Configure current reflection to return a empty reflection or it self (if method is void) when catch an exception.
     * @return current reflection configured to do not throws exceptions.
     */
    public abstract Reflection configNoExceptions();

    /**
     * Current object loaded as int.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as int.
     */
    public abstract int valueInt();

    /**
     * Current object loaded as long.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as long.
     */
    public abstract long valueLong();

    /**
     * Current object loaded as float.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as float.
     */
    public abstract float valueFloat();

    /**
     * Current object loaded as double.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as double.
     */
    public abstract double valueDouble();

    /**
     * Current object loaded as {@link BigDecimal}.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as {@link BigDecimal}.
     */
    public abstract BigDecimal valueBigDecimal();

    /**
     * Current object loaded as {@link BigInteger}.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as {@link BigInteger}.
     */
    public abstract BigInteger valueBigInteger();

    /**
     * Current object loaded as boolean.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as boolean.
     */
    public abstract boolean valueBoolean();

    /**
     * Current object loaded as String.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as String.
     */
    public abstract String valueString();

    /**
     * Current object loaded as casted generic type.<br/>
     * <i>Obs.: call it when desire recover a method result after execute it.</i>
     * @return generated value as casted generic type.
     */
    public abstract <T> T value();

    protected static Reflection empty(){
        return new ReflectionImplEmpty();
    }

    /**
     * Generate a new instance of input class.
     * @param clazz target class
     * @param args constructor arguments.
     * @return instance of target class.
     * @throws ReflectionException throw this exception when is not possible load class or instance it.
     */
    public static Object newInstance(Class<?> clazz, Object... args) {
        Objects.requireNonNull(clazz);
        try {
            int len = args.length;
            Constructor<?>[] cons = clazz.getDeclaredConstructors();
            if (len > 0) {
                Class<?>[] argsClazz = ArrayHelper.select(args, Object::getClass);
                for (Constructor<?> c : cons) {
                    Class<?>[] paramTypes = c.getParameterTypes();
                    if (len == paramTypes.length && ArrayHelper.sequenceEquals(argsClazz, paramTypes)) {
                        return c.newInstance(args);
                    }
                }
            } else if(cons.length > 0) {
                Constructor<?> found = ArrayHelper.first(cons,
                        c -> c.isAccessible() && c.getParameterCount() == 0);
                if(found != null) {
                    return found.newInstance();
                } else {
                    return null; // no one accessible.
                }
            }
            return clazz.newInstance();
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Inflate (create instance or load access for static class) target class by
     * fullName and constructor arguments (when is not static class)
     * @param classFullName target class path
     * @param args constructor arguments
     * @return a new reflection inflated for target class.
     * @throws ReflectionException throw this exception when is not possible load class or inflate it.
     */
    public static Reflection inflate(String classFullName, Object... args) {
        try {
            ClassLoader loader = Reflection.class.getClassLoader();
            return new ReflectionImplInflateClass(loader.loadClass(classFullName), args);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Try inflate (create instance or load access for static class) target class by
     * fullName and constructor arguments (when is not static class)
     * @param classFullName target class path
     * @param args constructor arguments
     * @return If success, generate a new reflection inflated for target class, otherwise,
     * return a empty reflection instance (check in @link {{@link Reflection#inflated()}).
     */
    public static Reflection tryInflate(String classFullName, Object... args) {
        try {
            return inflate(classFullName, args);
        } catch (ReflectionException e) {
            return empty();
        }
    }

    /**
     * Try inflate casting from initialized object to target class request.
     * @param obj target object non null.
     * @param castClassFullName name of class to do cast.
     * @return a new reflection inflated for cast class and target object.
     * @throws ReflectionException throw this exception when is not possible load class or cast it.
     */
    public static Reflection cast(Object obj, String castClassFullName) {
        try {
            ClassLoader loader = Reflection.class.getClassLoader();
            return new ReflectionImplInflateClass(Objects.requireNonNull(obj),
                    loader.loadClass(Objects.requireNonNull(castClassFullName)));
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Try inflate casting from initialized object to target class by fullName.
     * @param obj target object non null.
     * @param castClassFullName name of class to do cast.
     * @return If success, generate a new reflection inflated for cast target class and object, otherwise,
     * return a empty reflection instance (check in @link {{@link Reflection#inflated()}).
     */
    public static Reflection tryCast(Object obj, String castClassFullName) {
        try {
            return cast(obj, castClassFullName);
        } catch (ReflectionException e) {
            return empty();
        }
    }

    private static final class ReflectionImplInflateClass extends Reflection {

        private Object obj;
        private Class<?> objClass;
        private boolean isSelfDeflateAfterReturn;
        private boolean isThrowException;

        private ReflectionImplInflateClass(Class<?> clazz, Object[] args)  {
            this(newInstance(clazz, args), clazz);
        }

        private ReflectionImplInflateClass(Object obj, Class<?> objClass) {
            this(obj, objClass, false, true);
        }

        private ReflectionImplInflateClass(Object obj, Class<?> objClass, boolean isSelfDeflateAfterReturn, boolean isThrowException) {
            this.obj = obj;
            this.objClass = Objects.requireNonNull(objClass);
            this.isSelfDeflateAfterReturn = isSelfDeflateAfterReturn;
            this.isThrowException = isThrowException;
            this.requireAssignableClassForObjectNonNull();
        }

        @Override
        public boolean inflated() {
            return obj != null || objClass != null;
        }

        @Override
        public void deflate() {
            deflate(true);
        }

        private void deflate(boolean disposing) {
            try {
                if (disposing && obj != null) {
                    if (obj instanceof Closeable) {
                        ((Closeable) obj).close();
                    } else if (obj instanceof HttpURLConnection) {
                        ((HttpURLConnection) obj).disconnect();
                    } else if(obj instanceof Collection<?>) {
                        ((Collection<?>)obj).clear();
                    } else if(obj instanceof Map<?, ?>) {
                        ((Map<?, ?>)obj).clear();
                    } else if(obj.getClass().isArray()) {
                        ArrayHelper.clear(obj);
                    }
                }
            } catch (Exception ignored) { } finally {
                obj = null;
                objClass = null;
            }
        }

        @Override
        public Reflection configNoExceptions() {
            this.isThrowException = false;
            return this;
        }

        @Override
        public Reflection configDeflateAfterReturns() {
            this.isSelfDeflateAfterReturn = true;
            return this;
        }

        @Override
        public Reflection method(String methodName, Object... args) {
            if(inflated()) {
                boolean isVoid = false;
                try {
                    Method m = args.length == 0 ? objClass.getMethod(StringUtils.requireNonNullOrWhitespace(methodName)) :
                            objClass.getMethod(methodName, ArrayHelper.select(args, Object::getClass));
                    m.setAccessible(true);
                    isVoid = m.getReturnType().equals(Void.TYPE);
                    Object result = m.invoke(Modifier.isStatic(m.getModifiers()) ? null : obj, args);
                    checkSelfDeflateAfterReturn(true);
                    if(isVoid) {
                        return this;
                    } else if(result != null) {
                        return new ReflectionImplInflateClass(result, result.getClass(),
                                isSelfDeflateAfterReturn, isThrowException);
                    }
                } catch (Exception e) {
                    checkThrowsException(e, isVoid);
                }
            }
            return empty();
        }

        @Override
        public Reflection field(String fieldName) {
            try {
                Field f = objClass.getDeclaredField(StringUtils.requireNonNullOrWhitespace(fieldName));
                f.setAccessible(true);
                Object result = f.get(obj);
                checkSelfDeflateAfterReturn(true);
                return result != null ?
                        new ReflectionImplInflateClass(result, result.getClass(),
                                isSelfDeflateAfterReturn, isThrowException) :
                        empty();
            } catch (Exception e) {
                return checkThrowsException(e, false);
            }
        }

        private void requireAssignableClassForObjectNonNull() {
            if(obj != null && obj.getClass() != objClass && !objClass.isAssignableFrom(obj.getClass())) {
                checkThrowsException(new ClassCastException(
                        String.format("Can not cast instance of \"%1$s\" to \"%2$s\"",
                                obj.getClass(), objClass)),
                        true);
            }
        }

        private Reflection checkThrowsException(Exception e, boolean isReturnSelf) {
            if(isThrowException) {
                throw new ReflectionException(e);
            } else if(Debug.isDebugMode()) {
                System.err.printf("Reflection of %1$s caught an error: %2$s\n", objClass, e);
            }
            return isReturnSelf ? this : empty();
        }

        private void checkSelfDeflateAfterReturn(boolean disposing){
            if(isSelfDeflateAfterReturn) {
                deflate(disposing);
            }
        }

        @SuppressWarnings("unchecked")
        private <N extends Number> N parseToNumber(Class<N> clazz,
                                                   ArrayHelper.Function<Number, N> fromNumberFunc,
                                                   ArrayHelper.Function<String, N> parseFunc,
                                                   N defaultValue,
                                                   Object obj) {
            if(obj != null) {
                if(clazz.isAssignableFrom(obj.getClass())) {
                    return (N) obj;
                } else if(obj instanceof Number) {
                    return fromNumberFunc.apply((Number) obj);
                } else if(obj instanceof Boolean) {
                    return parseToNumber(clazz, fromNumberFunc, parseFunc, defaultValue, ((Boolean)obj) ? 1 : 0);
                } else {
                    return parseFunc.apply(obj.toString());
                }
            }

            checkSelfDeflateAfterReturn(false);
            return defaultValue;
        }

        @Override
        public int valueInt() {
            return parseToNumber(Integer.class, Number::intValue, Integer::parseInt, 0, obj);
        }

        @Override
        public long valueLong() {
            return parseToNumber(Long.class, Number::longValue, Long::parseLong, 0L, obj);
        }

        @Override
        public float valueFloat() {
            return parseToNumber(Float.class, Number::floatValue, Float::parseFloat, 0f, obj);
        }

        @Override
        public double valueDouble() {
            return parseToNumber(Double.class, Number::doubleValue, Double::parseDouble, 0d, obj);
        }

        @Override
        public BigDecimal valueBigDecimal() {
            try {
                return DecimalHelper.toBigDecimal(obj);
            } finally {
                checkSelfDeflateAfterReturn(true);
            }
        }

        @Override
        public BigInteger valueBigInteger() {
            return valueBigDecimal().toBigInteger();
        }

        @Override
        public boolean valueBoolean() {
            return parseToNumber(Integer.class, Number::intValue, Integer::parseInt, 0, obj) == 1;
        }

        @Override
        public String valueString() {
            try {
                return obj == null ? null : obj.toString();
            } finally {
                checkSelfDeflateAfterReturn(false);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T value() {
            try {
                return (T) obj;
            } finally {
                checkSelfDeflateAfterReturn(false);
            }
        }
    }

    private static final class ReflectionImplEmpty extends Reflection {

        @Override
        public Reflection configNoExceptions() { return this; }

        @Override
        public boolean inflated() { return false; }

        @Override
        public Reflection method(String methodName, Object... args) { return this; }

        @Override
        public Reflection field(String fieldName) { return this; }

        @Override
        public void deflate() { }

        @Override
        public Reflection configDeflateAfterReturns() { return this; }

        @Override
        public int valueInt() { return 0; }

        @Override
        public long valueLong() { return 0L; }

        @Override
        public float valueFloat() { return 0f; }

        @Override
        public double valueDouble() { return 0d; }

        @Override
        public BigDecimal valueBigDecimal() { return BigDecimal.ZERO; }

        @Override
        public BigInteger valueBigInteger() { return BigInteger.ZERO; }

        @Override
        public boolean valueBoolean() { return false; }

        @Override
        public String valueString() { return null; }

        @Override
        public <T> T value() { return null; }
    }

}
