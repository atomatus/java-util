package com.atomatus.util.serializer;

import junit.framework.TestCase;

public class SerializerTest extends TestCase {

    public void testSerialize() {
        Example ex0 = Example.getExample();
        assertNotNull(ex0);

        for (Serializer.Type type : Serializer.Type.values()) {
            Serializer sXml = Serializer.getInstance(type);
            String xml = sXml.serialize(ex0);
            Example ex1 =  sXml.deserialize(xml, Example.class);
            assertNotNull(ex1);
            assertEquals("From String " + type + " objects not Equals!", ex0, ex1);
        }

        for (Serializer.Type type : Serializer.Type.values()) {
            Serializer sXml = Serializer.getInstance(type);
            byte[] xml = sXml.serializeAsBytes(ex0);
            Example ex1 = sXml.deserialize(xml, Example.class);
            assertNotNull(ex1);
            assertEquals("From ByteArray " + type + " objects not Equals!", ex0, ex1);
        }
    }
}