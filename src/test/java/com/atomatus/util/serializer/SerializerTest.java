package com.atomatus.util.serializer;

import com.atomatus.util.macvendors.MacVendors;
import com.atomatus.util.macvendors.Vendor;
import junit.framework.TestCase;

public class SerializerTest extends TestCase {

    public void testSerialize() {
        Vendor v0 = MacVendors.getInstance().find("BC:92:6B:FF:FF:FF");
        assertNotNull(v0);
        String xml = Serializer.getInstance(Serializer.Type.XML).serialize(v0);
        Vendor v1 = Serializer.getInstance(Serializer.Type.XML).deserialize(xml);
        assertNotNull(v1);

        assertEquals(v0.getMacPrefix(), v1.getMacPrefix());
        assertEquals(v0.getCompany(), v1.getCompany());
    }

    public void testDeserialize() {

    }
}