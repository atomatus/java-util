package com.atomatus.util;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalHelperTest extends TestCase {

    private void compareBigDecimals(BigDecimal... arr) {
        for(int i=0, j=0, l = arr.length; i < l;) {

            if(i != j) {
                assertEquals("BigDecimal conversion from same origin results different values!", arr[i], arr[j++]);
            } else {
                j++;
            }

            if(j == l) {
                i++;
                j = 0;
            }
        }
    }

    public void testToBigDecimal() {

        BigDecimal fromStr      = DecimalHelper.toBigDecimal("13.5467");
        BigDecimal fromFloat    = DecimalHelper.toBigDecimal(13.5467f);
        BigDecimal fromDouble   = DecimalHelper.toBigDecimal(13.5467d);
        BigDecimal fromBD       = DecimalHelper.toBigDecimal(new BigDecimal("13.5467"));
        BigDecimal fromChars    = DecimalHelper.toBigDecimal(new char[] {'1','3','.','5','4', '6', '7' });
        compareBigDecimals(fromStr, fromFloat, fromDouble, fromBD, fromChars);

        compareBigDecimals(
                DecimalHelper.toBigDecimal(BigInteger.valueOf(Integer.MAX_VALUE)),
                DecimalHelper.toBigDecimal(Integer.MAX_VALUE),
                DecimalHelper.toBigDecimal((long) Integer.MAX_VALUE));
    }
}