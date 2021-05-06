package com.atomatus.util;

/**
 * Enumeration Utils to help
 * to parse, recovery and analyse input type to enum type.
 * @author Carlos Matos
 */
public final class EnumUtils {

    /**
     * Enum condition callback to parse.
     * @param <IN> input type
     * @param <E> element type
     */
    public interface EnumCondition<IN, E extends Enum<?>> {
        /**
         * Apply action.
         * @param in input element
         * @param e enum element to compare
         * @return boolean result
         */
        boolean apply(IN in, E e);
    }

    /**
     * Enum callback.
     * @param <E> element type.
     */
    public interface EnumFunction<E extends Enum<?>> {
        /**
         * Apply condition.
         * @return recover element.
         */
        E apply();
    }

    private EnumUtils() { }

    /**
     * Recovery all enum values as array from class type.
     * @param clazz enum class type
     * @param <E> enum type
     * @return enum array type
     */
    public static <E extends Enum<?>> E[] values(Class<E> clazz) {
        return clazz.getEnumConstants();
    }

    /**
     * Recovery enum value from class type and target ordinal.
     * @param clazz enum class type
     * @param ordinal enum ordinal value
     * @param <E> enum type
     * @return enum value found
     */
    public static <E extends Enum<?>> E ordinalValue(Class<E> clazz, int ordinal) {
        return clazz.getEnumConstants()[ordinal];
    }

    /**
     * Recovery first enum value that macths to recover
     * first enum value that matches function condition
     * and return it, otherwhise return default value from callback.
     * @param clazz enum class type.
     * @param value comparing value.
     * @param func comparing function callback.
     * @param defaultValueFunc default value callback.
     * @param <IN> input comparing value
     * @param <E> enum type
     * @return enum value that matchs to conditional callback.
     */
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

    /**
     * Recovery first enum value that macths to recover
     * first enum value that matches function condition
     * and return it, otherwise throws exception.
     * @param clazz enum class type.
     * @param value comparing value.
     * @param func comparing function callback.
     * @param <IN> input comparing value
     * @param <E> enum type
     * @return enum value that matchs to conditional callback.
     * @exception RuntimeException throws when not found.
     */
    public static <IN, E extends Enum<?>> E valueOf(Class<E> clazz, IN value,
                                                    EnumCondition<IN, E> func) {
        return valueOf(clazz, value, func,
                () -> {
                    throw new RuntimeException("No one enum found for input value \"" + value + "\"!");
                });
    }

}
