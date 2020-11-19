package com.atomatus.util;

import junit.framework.TestCase;

public class SNTPClientTest extends TestCase {

    public void testRequest() {
        SNTPClient client = new SNTPClient();
        if(client.request()) {
            long t  = client.getNtpTime();
            long rt = client.getRoundTripTime();
            assertTrue("SNTPClient Time is less than One!", t > 0L);
            assertTrue("SNTPClient RoundTripTime is less than Zero!", rt >= 0L);
        }
    }
}