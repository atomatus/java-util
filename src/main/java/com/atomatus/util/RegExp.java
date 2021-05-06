package com.atomatus.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <strong>Regular Expression</strong><br>
 * <i>Validate trivial types of input text values.</i>
 */
public final class RegExp {

    enum Expressions {
        MAIL("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$"),
        ONLY_NUMBERS_CHARACTERS("[a-zA-Z0-9]+"),
        DATE("^([1-9]|0[1-9]|[1,2][0-9]|3[0,1])/([1-9]|1[0,1,2])/\\d{4}$"),
        DECIMAL("^(-?\\d*(\\.\\d+))?$"),
        IP("^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$"),
        DATE_FORM_DB("^\\d{4}-(0[1-9]|1[0,1,2])-(0[1-9]|[1,2][0-9]|3[0,1])$"),
        TEL_BR("^\\(?\\d{2}\\)?[\\s-]?\\d{4}-?\\d{4}$"),
        TIME("^([0-1][0-9]|[2][0-3])(:([0-5][0-9]))(:([0-5][0-9])){1,2}$"),
        SIMPLE_TIME("^([0-1][0-9]|[2][0-3])(:([0-5][0-9])){1,2}$"),
        URL("^(http[s]?://|ftp[s]?://|file://)?(www\\.)?[a-zA-Z0-9-\\.]+\\.(com|org|net|mil|edu|ca|co.uk|com.au|gov|br)$"),
        USER_NAME("^[a-zA-Z0-9\\.\\_]+$"),
        PASSWORD("^[a-zA-Z0-9\\!\\@\\#\\$\\%\\&\\*\\?]+$"),
        MAC_ADDRESS("([0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}$"),
        MONETARY("^((([\\u20AC\\u00A3\\u20BE\\u20BD\\u20BA\\u20B4\\u20AA\\u20A6\\u0024\\u09F3\\u00A5\\u5143\\u20B9\\u20B1\\u20A9\\u0E3F\\u20AB\\u20BF\\u0271\\u0141\\u039E]|\\u043B\\u0432|CHF|K\\u010D|kr|kn|ft|z\\u0142|lei|\\u062F\\u002E\\u0625|Ksh|\\u002E\\u062F\\u002E\\u0645|R|R\\u0024|Rp|RM|Rs|XRP|\\u0053\\u002F\\u002E)\\s*)?[\\-|\\+]?\\d+(([,\\.\\s]\\d{3})+)?([,\\.]\\d+)?(\\s*([\\u20AC\\u00A3\\u20BE\\u20BD\\u20BA\\u20B4\\u20AA\\u20A6\\u0024\\u09F3\\u00A5\\u5143\\u20B9\\u20B1\\u20A9\\u0E3F\\u20AB\\u20BF\\u0271\\u0141\\u039E]|\\u043B\\u0432|CHF|K\\u010D|kr|kn|ft|z\\u0142|lei|\\u062F\\u002E\\u0625|Ksh|\\u002E\\u062F\\u002E\\u0645|R|R\\u0024|Rp|RM|Rs|XRP|\\u0053\\u002F\\u002E))?)$"),
        NEED_CDATA_ENCAPSULATION("[^a-zA-Z0-9\\u00E0-\\u00FC\\s\\.\\'\\\"\\!\\#\\$\\%\\\\�\\*\\-\\\\_\\=\\+\\,\\.\\:\\;\\/\\?\\^\\~\\`\\\\�]"),
        NUMERIC("^([\\-|\\+]?\\d*([\\.|\\,]\\d+)+)?$"),
        NUMBER_ONLY("^[0-9]+$"),
        CDATA("(?i)^<!\\[CDATA\\[.*]\\]>$");

        final String pattern;

        Expressions(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String toString() {
            return pattern;
        }
    }

    private static Matcher getMatcher(String str, Expressions exp) {
        Pattern p = Pattern.compile(exp.toString());
        return p.matcher(str);
    }

    /**
     * Check input string is a valid mail.
     * @param mailAddress target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidMail(String mailAddress) {
        return getMatcher(mailAddress, Expressions.MAIL).matches();
    }

    /**
     * Check input string is a valid text with no especial char (only numbers and letters).
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isTextNoEspecialChar(String str) {
        return getMatcher(str, Expressions.ONLY_NUMBERS_CHARACTERS).matches();
    }

    /**
     * Check input string is a valid date time.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidData(String str) {
        return getMatcher(str, Expressions.DATE).matches();
    }

    /**
     * Check input string is a valid date time for default database format.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidDataForDataBase(String str) {
        return getMatcher(str, Expressions.DATE_FORM_DB).matches();
    }

    /**
     * Check input string is a valid time.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidTime(String str) {
        return getMatcher(str, Expressions.TIME).matches();
    }

    /**
     * Check input string is a valid small time (hour:minutes, minutes:seconds,...).
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidSimpleTime(String str) {
        return getMatcher(str, Expressions.SIMPLE_TIME).matches();
    }

    /**
     * Check input string is a valid decimal value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidDecimal(String str) {
        return getMatcher(str, Expressions.DECIMAL).matches();
    }

    /**
     * Check input string is a valid IPAddress value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidIP(String str) {
        return getMatcher(str, Expressions.IP).matches();
    }

    /**
     * Check input string is a valid telephone number value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidTel(String str) {
        return getMatcher(str, Expressions.TEL_BR).matches();
    }

    /**
     * Check input string is a valid URL value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidURL(String str) {
        return getMatcher(str, Expressions.URL).matches();
    }

    /**
     * Check input string is a valid Username value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidUserName(String str) {
        return getMatcher(str, Expressions.USER_NAME).matches();
    }

    /**
     * Check input string is a valid Password value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidPassword(String str) {
        return getMatcher(str, Expressions.PASSWORD).matches();
    }

    /**
     * Check input string is a valid MacAddress format value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidMacAddress(String str) {
        return getMatcher(str, Expressions.MAC_ADDRESS).matches();
    }

    /**
     * Check input string is a valid monetary decimal (with or wihthout monetary symbol) value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidMonetary(String str) {
        return getMatcher(str, Expressions.MONETARY).matches();
    }

    /**
     * Check input string is a valid numeric value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidNumeric(String str) {
        return getMatcher(str, Expressions.NUMERIC).matches();
    }

    /**
     * Check input string is a valid number only value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidNumberOnly(String str) {
        return getMatcher(str, Expressions.NUMBER_ONLY).matches();
    }

    /**
     * Check input string is must have to set a CData encapsulation.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isNeedCDataEncapsulationString(String str) {
        return getMatcher(str, Expressions.NEED_CDATA_ENCAPSULATION).find();
    }

    /**
     * Check input string is a valid CData value.
     * @param str target string
     * @return true, input value matches condition, otherwise false.
     */
    public static boolean isValidCDataString(String str) {
        return getMatcher(str, Expressions.CDATA).matches();
    }
}