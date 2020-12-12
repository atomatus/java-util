package com.atomatus.util;

public final class EnumUtils {

    public interface EnumCondition<IN, E extends Enum<?>> {
        boolean apply(IN in, E e);
    }

    public interface EnumFunction<E extends Enum<?>> {
        E apply();
    }

    private EnumUtils() { }

    public static <E extends Enum<?>> E[] values(Class<E> clazz) {
        return clazz.getEnumConstants();
    }

    public static <E extends Enum<?>> E ordinalValue(Class<E> clazz, int ordinal) {
        return clazz.getEnumConstants()[ordinal];
    }

    public static <IN, E extends Enum<?>> E valueOf(Class<E> clazz, IN value,
                                                    EnumCondition<IN, E> func,
                                                    EnumFunction<E> defaultValueFunc) {
        for(E e : clazz.getEnumConstants()) {
            if(func.apply(value, e)) {
                return e;
            }
        }
        return defaultValueFunc.apply();
    }

    public static <IN, E extends Enum<?>> E valueOf(Class<E> clazz, IN value,
                                                    EnumCondition<IN, E> func) {
        return valueOf(clazz, value, func,
                () -> {
                    throw new RuntimeException("No onae enum found for input value \"" + value + "\"!");
                });
    }

}
