package com.atomatus.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Reflection helper within methods to
 * check and validate access permition to fields and methods from
 * desired class type.
 */
public final class ReflectionHelper {

    private interface CheckFieldFunction {
        boolean apply(Class<?> clazz, Field field);
    }

    private static boolean hasAccessMethodByPrefix(Field field, Method m, String prefix) {
        String mName = m.getName();
        return mName.startsWith(prefix) && mName.equalsIgnoreCase(prefix + field.getName());
    }

    private static boolean hasAccessMethodByPrefix(Field field, Method m, String... prefixes) {
        for(String prefix : prefixes) {
            if(hasAccessMethodByPrefix(field, m, prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether target field contains getter and setter access methods.
     * @param clazz target class
     * @param field target field
     * @return true, field contains getter and setter, otherwhise false.
     */
    public static boolean hasGetterAndSetter(Class<?> clazz, Field field) {
        boolean getter = false, setter = false;
        for(Method m : clazz.getDeclaredMethods()) {
            getter = getter || hasAccessMethodByPrefix(field, m, "get", "is", "has");
            setter = setter || hasAccessMethodByPrefix(field, m, "set");

            if(getter && setter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether target field contains getter or setter access methods.
     * @param clazz target class
     * @param field target field
     * @return true, field contains getter or setter, otherwhise false.
     */
    public static boolean hasGetterOrSetter(Class<?> clazz, Field field) {
        for(Method m : clazz.getDeclaredMethods()) {
            if(hasAccessMethodByPrefix(field, m, "get", "is", "has") ||
                    hasAccessMethodByPrefix(field, m, "set")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether target field contains getter  access methods.
     * @param clazz target class
     * @param field target field
     * @return true, field contains getter, otherwhise false.
     */
    public static boolean hasGetter(Class<?> clazz, Field field) {
        for(Method m : clazz.getDeclaredMethods()) {
            if(hasAccessMethodByPrefix(field, m, "get", "is", "has")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether target field contains setter access methods.
     * @param clazz target class
     * @param field target field
     * @return true, field contains setter, otherwhise false.
     */
    public static boolean hasSetter(Class<?> clazz, Field field) {
        for(Method m : clazz.getDeclaredMethods()) {
            if(hasAccessMethodByPrefix(field, m, "set")) {
                return true;
            }
        }
        return false;
    }

    /**
     * List all fields that matches conditional callback function.
     * @param clazz target class
     * @param condition conditional callback
     * @return all fields found.
     */
    public static Field[] getAllFieldsByCondition(Class<?> clazz, CheckFieldFunction condition) {
        Field[] all = Objects.requireNonNull(clazz).getDeclaredFields();
        Field[] arr = new Field[all.length];
        int offset  = 0;

        for(Field f : clazz.getDeclaredFields()) {
            if(condition.apply(clazz, f)) {
                arr[offset++] = f;
            }
        }

        if(offset == 0) {
            return new Field[0];
        } else if(offset < arr.length) {
            Field[] aux = arr;
            arr = new Field[offset];
            System.arraycopy(aux, 0, arr, 0, arr.length);
        }

        return arr;
    }

    /**
     * Get all fields that contains getter and setters methods accessibles.
     * @param clazz target class
     * @return fields found.
     */
    public static Field[] getAllFieldsWithGetterAndSetter(Class<?> clazz) {
        return getAllFieldsByCondition(clazz, ReflectionHelper::hasGetterAndSetter);
    }

    /**
     * Get all fields that contains getter or setters methods accessibles.
     * @param clazz target class
     * @return fields found.
     */
    public static Field[] getAllFieldsWithGetterOrSetter(Class<?> clazz) {
        return getAllFieldsByCondition(clazz, ReflectionHelper::hasGetterOrSetter);
    }

    /**
     * Get all fields that contains getter methods accessibles.
     * @param clazz target class
     * @return fields found.
     */
    public static Field[] getAllFieldsWithGetter(Class<?> clazz) {
        return getAllFieldsByCondition(clazz, ReflectionHelper::hasGetter);
    }

    /**
     * Get all fields that contains setters methods accessibles.
     * @param clazz target class
     * @return fields found.
     */
    public static Field[] getAllFieldsWithSetter(Class<?> clazz) {
        return getAllFieldsByCondition(clazz, ReflectionHelper::hasSetter);
    }
}
