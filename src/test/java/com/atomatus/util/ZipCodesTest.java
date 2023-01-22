package com.atomatus.util;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Locale;

public class ZipCodesTest extends TestCase {

    public void testByLocale() {
        for (Locale locale : LocaleHelper.getLocales()) {
            ZipCodes zipCode = ZipCodes.byLocale(locale);
            Assert.assertNotNull("Zip code not found for locale \"" + locale + "\" (" + locale.getDisplayCountry() + ")", zipCode);
            Assert.assertEquals("Found zip code invalid for locale \"" + locale + "\" (" + locale.getDisplayCountry() + ")", zipCode.country(), locale.getCountry());
        }
    }

}