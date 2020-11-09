package atomatus.util;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.Locale;

public class LocaleHelperTest extends TestCase {

    public void testGetDefaultLocale() {
        Locale locale = LocaleHelper.getDefaultLocale();
        assertNotNull("Locale recovered is null!", locale);
    }

    public void testGetLocales() {
        Iterable<Locale> locales = LocaleHelper.getLocales();
        assertNotNull("Could not recover iterable locales!", locales);
        assertTrue("Iterable locales is empty!", locales.iterator().hasNext());
        testIteratorLocalesChildren(locales.iterator());
    }

    public void testGetLocalesAsIterator() {
        Iterator<Locale> locales = LocaleHelper.getLocalesAsIterator();
        testIteratorLocalesChildren(locales);
    }

    private void testIteratorLocalesChildren(Iterator<Locale> locales) {
        while(locales.hasNext()) {
            Locale l = locales.next();
            assertNotNull("Iterable locale child is null!", l);

            String reg = l.getCountry();
            assertTrue (String.format("Iterable locale child (%1$s) do not contains country property!", l), reg != null && !reg.isEmpty());

            String lang = l.getLanguage();
            assertTrue (String.format("Iterable locale child (%1$s) do not contains language property!", l), lang != null && !lang.isEmpty());
        }
    }
}