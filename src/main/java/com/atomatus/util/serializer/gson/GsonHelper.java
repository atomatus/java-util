package com.atomatus.util.serializer.gson;

import com.atomatus.util.Debug;
import com.atomatus.util.serializer.xstream.XStreamHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper to serialize/deserialize objects to/from JSON using {@link com.google.gson.Gson}.
 */
public final class GsonHelper {

    public interface GsonConsumer {
        void accept(GsonBuilder builder);
    }

    private static final ConcurrentMap<Class<?>, GsonConsumer> consumers;

    static {
        consumers = new ConcurrentHashMap<>();
    }

    private GsonHelper() { }

    /**
     * Create a new instance of Gson with autodetect and mapping fields.
     * @return instance of Gson.
     */
    public static Gson getInstance() {
        return getInstance(GsonHelper.class);
    }

    /**
     * Create a new instance of Gson with autodetect and mapping fields.
     * @param classType class type to identity on {@link GsonHelper#setupDefaultConfiguration}.
     * @return instance of Gson.
     */
    public static Gson getInstance(Class<?> classType) {
        return getInstance(classType, null);
    }

    /**
     * Create a new instance of Gson with autodetect and mapping fields.
     * @param classType class type to identity on {@link GsonHelper#setupDefaultConfiguration}.
     * @param configBuilder If not null, receive current builder and apply custom configurations.
     * @return instance of Gson.
     */
    public static Gson getInstance(Class<?> classType, GsonConsumer configBuilder) {

        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC | Modifier.VOLATILE)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(BigDecimal.class, new BigDecimalConverter())
                .registerTypeAdapter(Calendar.class, new CalendarConverter())
                .registerTypeAdapter(Date.class, new DateConverter())
                .setLenient();

        if(Debug.isDebugMode()) {
            builder.setPrettyPrinting();
        }

        if(configBuilder != null) {
            configBuilder.accept(builder);
        }

        if(classType != null) {
            GsonConsumer consumer = consumers.get(classType);
            if (consumer != null) {
                consumer.accept(builder);
            }
        }

        return builder.create();
    }

    /**
     * Configure Gson generated from {@link GsonHelper#getInstance}.
     * @param consumer consumer to configure instance of Gson.
     */
    public static void setupDefaultConfiguration(GsonConsumer consumer) {
        setupDefaultConfiguration(XStreamHelper.class, consumer);
    }

    /**
     * Configure Gson generated from {@link GsonHelper#getInstance}
     * when target is an object instance of targetClass set.
     * @param targetClass target class that will fire consumer configuration.
     * @param consumer consumer to configure instance of Gson to targetClass object.
     */
    public static void setupDefaultConfiguration(Class<?> targetClass, GsonConsumer consumer) {
        consumers.put(Objects.requireNonNull(targetClass), Objects.requireNonNull(consumer));
    }

}