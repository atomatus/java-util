package com.atomatus.util.macvendors;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MacVendorsTest extends TestCase {

    private final String macAddress;

    public MacVendorsTest(String macAddress) {
        this.macAddress = macAddress;
    }

    @Parameterized.Parameters
    public static Collection<String> parameters() {
        return Arrays.asList(
                "BC:92:6B:FF:FF:FF", //Apple Inc
                "FC-A1-3E-2A-1C-33" //Samsung
        );
    }

    @Test
    public void testFind() {
        Vendor v =  MacVendors.getInstance().find(macAddress);
        assertNotNull(v);
    }
}