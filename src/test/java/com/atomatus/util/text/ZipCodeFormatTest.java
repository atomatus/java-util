package com.atomatus.util.text;

import com.atomatus.util.ZipCodes;
import junit.framework.TestCase;
import org.junit.Assert;

public class ZipCodeFormatTest extends TestCase {

    public void testFormatBrazil() {
        ZipCodeFormat formatter = new ZipCodeFormat(ZipCodes.BRAZIL);
        String res = formatter.format("02299887");
        Assert.assertEquals("02299-887", res);

        res = formatter.format("CEP: 02299887");
        Assert.assertEquals("02299-887", res);

        res = formatter.format("02299");
        Assert.assertEquals("02299", res);

        res = formatter.format("BR02299");
        Assert.assertEquals("02299", res);
    }

    public void testFormatSweden() {
        ZipCodeFormat formatter = new ZipCodeFormat(ZipCodes.SWEDEN);
        String res = formatter.format("01234");
        Assert.assertEquals("012-34", res);

        res = formatter.format("SE01234");
        Assert.assertEquals("SE-012-34", res);

        res = formatter.format("AA01234");
        Assert.assertEquals("SE-012-34", res);
    }

    public void testFormatCanada() {
        ZipCodeFormat formatter = new ZipCodeFormat(ZipCodes.CANADA);
        String res = formatter.format("C0H1A2");
        Assert.assertEquals("C0H 1A2", res);

        res = formatter.format("CC0H1A23");
        Assert.assertEquals("C0H 1A2", res);
    }

    public void testFormatCanadaInvalidWithReuse() {
        ZipCodeFormat formatter = new ZipCodeFormat(ZipCodes.CANADA, true);
        //when is not valid return original input.
        String res = formatter.format("0000");
        Assert.assertEquals("0000", res);
    }


    public void testFormatCanadaInvalidWithoutReuse() {
        ZipCodeFormat formatter = new ZipCodeFormat(ZipCodes.CANADA, false);
        //when is not valid return original input.
        String res = formatter.format("0000");
        Assert.assertEquals("", res);
    }
}