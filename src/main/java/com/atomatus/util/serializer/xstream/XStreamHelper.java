package com.atomatus.util.serializer.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper to serialize/deserialize objects to/from XML using {@link XStream}.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class XStreamHelper {

    public interface XStreamConsumer {
        void accept(XStream x);
    }

    private static final ConcurrentMap<Class<?>, XStreamConsumer> consumers;

    static {
        consumers = new ConcurrentHashMap<>();
    }

    private XStreamHelper() { }

    /**
     * Create a new instance of XStream with autodetect and ignoring not mapping fields.
     * @return instance of XStream.
     */
    public static XStream getInstance() {
        return getInstance(XStreamHelper.class);
    }

    /**
     * Create a new instance of XStream with autodetect and ignoring not mapping fields.
     * @param classType class type to identity on {@link XStreamHelper#setupDefaultConfiguration}.
     * @return instance of XStream.
     */
    public static XStream getInstance(Class<?> classType) {

        XStream x = new XStream(new DomDriver(Charset.defaultCharset().name(), new XmlFriendlyNameCoder("$", "_")){
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CDataStringPrintWriter(out, (XmlFriendlyNameCoder) getNameCoder());
            }
        })
        {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    /*
                     * Ignore not mapped tags.
                     */
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        return definedIn != Object.class && super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        };

        XStream.setupDefaultSecurity(x);

        x.registerConverter(new CalendarConverter());
        x.registerConverter(new BigDecimalConverter());
        x.autodetectAnnotations(true);

        // allow some basics
        x.addPermission(NullPermission.NULL);
        x.addPermission(PrimitiveTypePermission.PRIMITIVES);
        x.allowTypeHierarchy(Collection.class);
        x.allowTypeHierarchy(List.class);
        // allow any type from the same package
        x.allowTypesByWildcard(new String[] { "com.atomatus.**" });

        if(classType != null) {
            XStreamConsumer consumer = consumers.get(classType);
            if (consumer != null) {
                consumer.accept(x);
            }
        }

        return x;
    }

    /**
     * Configure XML converter rule for target class. <br/>
     * When any converter is working with any target class consumer action will be fired.
     * @param consumer consumer action to affect serializing/deserializing for any object.
     */
    public static void setupDefaultConfiguration(XStreamConsumer consumer) {
        setupDefaultConfiguration(XStreamHelper.class, consumer);
    }

    /**
     * Configure XML converter rule for target class. <br/>
     * When converter is working with target class consumer action will be fired.
     * @param targetClass target class (object owned of class are serializing/deserializing)
     * @param consumer consumer action to affect serializing/deserializing object owned of target class.
     */
    public static void setupDefaultConfiguration(Class<?> targetClass, XStreamConsumer consumer) {
        consumers.put(Objects.requireNonNull(targetClass), Objects.requireNonNull(consumer));
    }

}