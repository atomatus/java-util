package com.atomatus.util.serializer;

import junit.framework.TestCase;

public class SerializerTest extends TestCase {

    private Example ex0;

    @Override
    protected void setUp() {
        assertNotNull(ex0 = Example.getExample());
    }

    @Override
    protected void tearDown() {
        ex0 = null;
    }

    //region serialize/deserialize
    private void serialize(Serializer.Type type) {
        Serializer s = Serializer.getInstance(type);
        String str = s.serialize(ex0);
        Example ex1 = s.deserialize(str, Example.class);
        assertNotNull(ex1);
        assertEquals("From String " + type + " objects not Equals!", ex0, ex1);
    }

    private void serializeBytes(Serializer.Type type) {
        Serializer s = Serializer.getInstance(type);
        byte[] xml = s.serializeAsBytes(ex0);
        Example ex1 = s.deserialize(xml, Example.class);
        assertNotNull(ex1);
        assertEquals("From ByteArray " + type + " objects not Equals!", ex0, ex1);
    }
    //endregion

    //region Base64
    public void testSerializeBase64() {
        serialize(Serializer.Type.BASE64);
    }

    public void testSerializeBytesBase64() {
        serializeBytes(Serializer.Type.BASE64);
    }
    //endregion

    //region BSON
    public void testSerializeBson() {
        serialize(Serializer.Type.BSON);
    }

    public void testSerializeBytesBson() {
        serializeBytes(Serializer.Type.BSON);
    }
    //endregion

    //region JSON
    public void testSerializeJson() {
        serialize(Serializer.Type.JSON);
    }

    public void testSerializeBytesJson() {
        serializeBytes(Serializer.Type.JSON);
    }
    //endregion

    //region XML
    public void testSerializeXml() {
        serialize(Serializer.Type.XML);
    }

    public void testSerializeBytesXml() {
        serializeBytes(Serializer.Type.XML);
    }
    //endregion
}