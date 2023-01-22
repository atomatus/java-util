package com.atomatus.util.text;

import junit.framework.TestCase;
import org.junit.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class MaskFormatTest extends TestCase {

    public void testMaskFormatInteger() {
        MaskFormat mask = new MaskFormat("####");
        String result = mask.format(12345);
        Assert.assertEquals("1234", result);
    }

    public void testMaskFormatLong() {
        MaskFormat mask = new MaskFormat("#############");
        String result = mask.format(1234567890123456789L);
        Assert.assertEquals("1234567890123", result);
    }

    public void testMaskFormatNumberLetter() {
        MaskFormat mask = new MaskFormat("##AB##");
        String result = mask.format(123456789);
        Assert.assertEquals("123B45", result);
    }

    public void testMaskFormatNumberLetterAB() {
        MaskFormat mask = new MaskFormat("##\\AB##");
        String result = mask.format(123456789);
        Assert.assertEquals("12AB34", result);
    }

    public void testMaskFormatLetter() {
        MaskFormat mask = new MaskFormat("????");
        String result = mask.format("1B3C4D7E9");
        Assert.assertEquals("BCDE", result);
    }

    public void testMaskFormatZipCode() {
        MaskFormat mask = new MaskFormat("#####-###");
        String result = mask.format("03389564");
        Assert.assertEquals("03389-564", result);
    }

    public void testMaskFormatZipCodeSE() {
        MaskFormat mask = new MaskFormat("SE-#####-###");
        String result = mask.format("03389564");
        Assert.assertEquals("SE-03389-564", result);
    }

    public void testMaskFormatZipCodeCA() {
        MaskFormat mask = new MaskFormat("?? #####");
        String result = mask.format("AB03389564");
        Assert.assertEquals("AB 03389", result);
    }

    public void testMaskFormatPhoneNumber() {
        MaskFormat mask = new MaskFormat("(##) # ####-####");
        String result = mask.format("11985476325");
        Assert.assertEquals("(11) 9 8547-6325", result);
    }

    public void testMaskFormatPhoneNumberCountry() {
        MaskFormat mask = new MaskFormat("+55 (##) # ####-####");
        String result = mask.format("11985476325");
        Assert.assertEquals("+55 (11) 9 8547-6325", result);
    }

    public void testMaskFormatCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 21);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 2022);

        MaskFormat mask = new MaskFormat("DD/MM/yyyy");
        String result = mask.format(cal);
        Assert.assertEquals("21/01/2022", result);
    }

    @SuppressWarnings("deprecation")
    public void testMaskFormatDate() {
        Date date = new Date();
        date.setDate(21);
        date.setMonth(Calendar.JANUARY);
        date.setYear(2022 - 1900);

        MaskFormat mask = new MaskFormat("DD/MM/yyyy");
        String result = mask.format(date);
        Assert.assertEquals("21/01/2022", result);
    }

    public void testMaskFormatLocalDate() {
        LocalDate date = LocalDate.now()
                .withDayOfYear(21)
                .withMonth(1)
                .withYear(2022);
        MaskFormat mask = new MaskFormat("DD/MM/yyyy");
        String result = mask.format(date);
        Assert.assertEquals("21/01/2022", result);
    }

    public void testMaskFormatLocalDateTime() {
        LocalDateTime date = LocalDateTime.now()
                .withDayOfYear(21)
                .withMonth(1)
                .withYear(2022)
                .withHour(16)
                .withMinute(34)
                .withSecond(22);
        MaskFormat mask = new MaskFormat("DD/MM/yyyy HH:mm:ss");
        String result = mask.format(date);
        Assert.assertEquals("21/01/2022 16:34:22", result);
    }
}