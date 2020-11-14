package com.atomatus.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public abstract class Reflection {

    private Reflection() { }

    public abstract boolean inflated();

    public abstract Reflection method(String methodName, Object... args);

    public abstract int valueInt();

    public abstract long valueLong();

    public abstract float valueFloat();

    public abstract double valueDouble();

    public abstract BigDecimal valueBigDecimal();

    public abstract BigInteger valueBigInteger();

    public abstract boolean valueBoolean();

    public abstract String valueString();

    public abstract <T> T value();

    protected static Reflection empty(){
        return new ReflectionImplEmpty();
    }

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

    public static Reflection inflate(String classFullName, Object... args) {
        ClassLoader loader = Debug.class.getClassLoader();
        try {
            return new ReflectionImplInflateClass(loader.loadClass(classFullName), args);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }

    public static Reflection tryInflate(String classFullName, Object... args) {
        try {
            return inflate(classFullName, args);
        } catch (ReflectionException e) {
            return empty();
        }
    }

    private static final class ReflectionImplInflateClass extends Reflection {

        private final Object obj;
        private final Class<?> objClass;

        public ReflectionImplInflateClass(Class<?> clazz, Object[] args)  {
            this(newInstance(clazz, args), clazz);
        }

        private ReflectionImplInflateClass(Object obj, Class<?> objClass) {
            this.obj = obj;
            this.objClass = objClass;
        }

        @Override
        public boolean inflated() {
            return obj != null || objClass != null;
        }

        @Override
        public Reflection method(String methodName, Object... args) {
            if(inflated()) {
                try {
                    Class<?>[] arr = args.length > 0 ? ArrayHelper.select(args, Object::getClass) :
                            new Class<?>[0];

                    Method m = args.length == 0 ? objClass.getMethod(methodName) :
                            objClass.getMethod(methodName, arr);
                    m.setAccessible(true);
                    Object result = m.invoke(Modifier.isStatic(m.getModifiers()) ? null : obj, args);
                    if(m.getReturnType().equals(Void.TYPE)) {
                        return this;
                    } else if(result != null) {
                        return new ReflectionImplInflateClass(result, result.getClass());
                    }
                } catch (Exception e) {
                    throw new ReflectionException(e);
                }
            }

            return empty();
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
            return DecimalHelper.toBigDecimal(obj);
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
            return obj == null ? null : obj.toString();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T value() {
            return (T) obj;
        }
    }

    private static final class ReflectionImplEmpty extends Reflection {

        @Override
        public boolean inflated() { return false; }

        @Override
        public Reflection method(String methodName, Object... args) { return this; }

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
