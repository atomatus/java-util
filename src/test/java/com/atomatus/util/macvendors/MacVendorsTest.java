package com.atomatus.util.macvendors;

import junit.framework.TestCase;

public class MacVendorsTest extends TestCase {

    public void testFind() {
        //example to find Apple Inc from macAddress.
        Vendor v =  MacVendors.getInstance().find("BC:92:6B:FF:FF:FF");
        assertNotNull(v);
    }
}