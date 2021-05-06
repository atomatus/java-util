package com.atomatus.util;

/**
 * Helper to check object is a wrapper type.
 */
public final class WrapperHelper {

    private WrapperHelper() { }

    /**
     * Check if target object is a wrapper type.
     * @param obj target object
     * @return true, object is wrapper type, otherwise false.
     */
    public static boolean isWrapper(Object obj) {
        return (obj instanceof String ||
                obj instanceof Byte ||
                obj instanceof Short ||
                obj instanceof Integer ||
                obj instanceof Long ||
                obj instanceof Float ||
                obj instanceof Double ||
                obj instanceof Boolean ||
                obj instanceof Character);
    }

    /**
     * Check if target class is a wrapper type.
     * @param type target class
     * @return true, class is wrapper type, otherwise false.
     */
    public static boolean isWrapper(Class<?> type) {
        return type != null && (String.class.isAssignableFrom(type) ||
                Byte.class.isAssignableFrom(type) ||
                Short.class.isAssignableFrom(type) ||
                Integer.class.isAssignableFrom(type) ||
                Long.class.isAssignableFrom(type) ||
                Float.class.isAssignableFrom(type) ||
                Double.class.isAssignableFrom(type) ||
                Boolean.class.isAssignableFrom(type) ||
                Character.class.isAssignableFrom(type));
    }
}
