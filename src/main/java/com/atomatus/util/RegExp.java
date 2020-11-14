package com.atomatus.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>Regular Expression</h1>
 * Validate trivial types of input text values.
 */
public final class RegExp {

    enum Expressions {
        MAIL("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$"),
        ONLY_NUMBERS_CHARACTERS("[a-zA-Z0-9]+"),
        DATE("^([1-9]|0[1-9]|[1,2][0-9]|3[0,1])/([1-9]|1[0,1,2])/\\d{4}$"),
        DECIMAL("^\\d*[0-9](\\.\\d*[0-9])?$"),
        IP("^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$"),
        DATE_FORM_DB("^\\d{4}-(0[1-9]|1[0,1,2])-(0[1-9]|[1,2][0-9]|3[0,1])$"),
        TEL_BR("^\\(?\\d{2}\\)?[\\s-]?\\d{4}-?\\d{4}$"),
        TIME("^([0-1][0-9]|[2][0-3])(:([0-5][0-9]))(:([0-5][0-9])){1,2}$"),
        SIMPLE_TIME("^([0-1][0-9]|[2][0-3])(:([0-5][0-9])){1,2}$"),
        URL("^(http[s]?://|ftp[s]?://|file://)?(www\\.)?[a-zA-Z0-9-\\.]+\\.(com|org|net|mil|edu|ca|co.uk|com.au|gov|br)$"),
        USER_NAME("^[a-zA-Z0-9\\.\\_]+$"),
        PASSWORD("^[a-zA-Z0-9\\!\\@\\#\\$\\%\\&\\*\\?]+$"),
        MAC_ADDRESS("([0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}$"),
        NEED_CDATA_ENCAPSULATION("[^a-zA-Z0-9\\u00E0-\\u00FC\\s\\.\\'\\\"\\!\\#\\$\\%\\\\�\\*\\-\\\\_\\=\\+\\,\\.\\:\\;\\/\\?\\^\\~\\`\\\\�]"),
        CDATA("(?i)^<!\\[CDATA\\[.*]\\]>$");

        final String str;

        Expressions(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    private static Matcher getMatcher(String str, Expressions exp) {
        Pattern p = Pattern.compile(exp.toString());
        return p.matcher(str);
    }

    public static boolean isValidMail(String mailAddress) {
        return getMatcher(mailAddress, Expressions.MAIL).matches();
    }

    public static boolean isTextNoEspecialChar(String str) {
        return getMatcher(str, Expressions.ONLY_NUMBERS_CHARACTERS).matches();
    }

    public static boolean isValidData(String str) {
        return getMatcher(str, Expressions.DATE).matches();
    }

    public static boolean isValidDataForDataBase(String str) {
        return getMatcher(str, Expressions.DATE_FORM_DB).matches();
    }

    public static boolean isValidTime(String str) {
        return getMatcher(str, Expressions.TIME).matches();
    }

    public static boolean isValidSimpleTime(String str) {
        return getMatcher(str, Expressions.SIMPLE_TIME).matches();
    }

    public static boolean isValidDecimal(String str) {
        return getMatcher(str, Expressions.DECIMAL).matches();
    }

    public static boolean isValidIP(String str) {
        return getMatcher(str, Expressions.IP).matches();
    }

    public static boolean isValidTel(String str) {
        return getMatcher(str, Expressions.TEL_BR).matches();
    }

    public static boolean isValidURL(String str) {
        return getMatcher(str, Expressions.URL).matches();
    }

    public static boolean isValidUserName(String str) {
        return getMatcher(str, Expressions.USER_NAME).matches();
    }

    public static boolean isValidPassword(String str) {
        return getMatcher(str, Expressions.PASSWORD).matches();
    }

    public static boolean isValidMacAddress(String str) {
        return getMatcher(str, Expressions.MAC_ADDRESS).matches();
    }

    public static boolean isNeedCDataEncapsulationString(String str) {
        return getMatcher(str, Expressions.NEED_CDATA_ENCAPSULATION).find();
    }

    public static boolean isValidCDataString(String str) {
        return getMatcher(str, Expressions.CDATA).matches();
    }
}