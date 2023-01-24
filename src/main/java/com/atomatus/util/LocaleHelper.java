package com.atomatus.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

/**
 * Helper to get information about Locale.
 * @author Carlos Matos {@literal @chcmatos}
 */
public final class LocaleHelper {

    private static final String DEFAULT_LANGUAGE = "pt";
    private static final String DEFAULT_COUNTRY  = "BR";
    private static final Locale defaultLocale;

    static{
        defaultLocale = initDefaultLocale();
    }

    private static Locale initDefaultLocale(){
        try {
            return Locale.getDefault(Locale.Category.FORMAT);
        } catch (Throwable e0) {
            try {
                try {
                    Properties ps = System.getProperties();
                    String language = ps.getProperty("user.language");
                    String country  = ps.getProperty("user.country");
                    if (language != null && !language.isEmpty() && country != null && !country.isEmpty()) {
                        return new Locale(language, country);
                    } else {
                        throw new NullPointerException();
                    }
                } catch (Throwable e1) {
                    return new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
                }
            } catch (Throwable e2) {
                return Locale.US;
            }
        }
    }

    /**
     * Get default Locale, how like, defined on system.
     * @return default system locale.
     */
    public static Locale getDefaultLocale(){
        return defaultLocale;
    }

    /**
     * Get more popular locales.
     * @param args some locales pre-defined to insert on top of iterable.
     * @return all popular locales.
     */
    public static Iterable<Locale> getLocales(Locale... args){
        return () -> new PopularLocales(args);
    }

    /**
     * Get more popular locales.
     * @param args some locales pre-defined to insert on top of iterable.
     * @return all popular locales
     */
    public static Iterator<Locale> getLocalesAsIterator(Locale... args){
        return new PopularLocales(args);
    }

    private static class PopularLocales implements Iterator<Locale> {

        static final String[][] localesArgs;

        final Locale[] args;
        Locale next;
        int argIndex;
        int argLen;

        static {
            localesArgs = new String[][] {
                    { DEFAULT_LANGUAGE, DEFAULT_COUNTRY },
                    { "en", "US" },
                    { "en", "GB" },
                    { "es", "AR" },
                    { "en", "AU" },
                    { "en", "CA" },
                    { "fr", "CA" },
                    { "es", "CL" },
                    { "zh", "CN" },
                    { "es", "CO" },
                    { "da", "DK" },
                    { "de", "AT" },
                    { "de", "DE" },
                    { "de", "LU" },
                    { "de", "CH" },
                    { "fr", "FR" },
                    { "pt", "PT" },
                    { "zh", "HK" },
                    { "hu", "HU" },
                    { "en", "IN" },
                    { "iw", "IL" },
                    { "ja", "JP" },
                    { "ko", "KR" },
                    { "ms", "MY" },
                    { "es", "MX" },
                    { "ar", "MA" },
                    { "en", "NZ" },
                    { "no", "NO" },
                    { "en", "PH" },
                    { "pl", "PL" },
                    { "ru", "RU" },
                    { "ar", "SA" },
                    { "en", "ZA" },
                    { "sv", "SE" },
                    { "fr", "CH" },
                    { "zh", "TW" },
                    { "th", "TH" },
                    { "tr", "TR" },
                    { "vi", "VN" }
            };
        }

        public PopularLocales(Locale... args) {
            this.args   = args;
            this.argLen = args.length;
        }

        @Override
        public boolean hasNext() {
            int diff;
            if(argIndex < argLen){
                next = args[argIndex++];
                return true;
            } else if ((diff = argIndex - argLen) < localesArgs.length) {
                argIndex++;
                String[] lArgs = localesArgs[diff];
                next = new Locale(lArgs[0], lArgs[1]);
                return true;
            } else{
                next = null;
                return false;
            }
        }

        @Override
        public Locale next() {
            return next;
        }
    }
}
