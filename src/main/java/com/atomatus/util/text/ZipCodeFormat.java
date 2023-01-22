package com.atomatus.util.text;

import com.atomatus.util.StringUtils;
import com.atomatus.util.ZipCodes;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Objects;

/**
 * <p>
 * <code>ZipCodeFormat</code> is used to format and edit strings with zip code
 * by input type defined in constructor.
 * </p>
 * <i>Created by chcmatos on 21, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public final class ZipCodeFormat extends Format {

    /**
     * Zip code.
     */
    private final ZipCodes zipCode;

    /**
     * When true and is not possible apply format
     * return the original input as result, otherwsise return empty.
     */
    private final boolean reuse;

    /**
     * Create a Zip format by input zip code.
     * @param zipCode zip code type.
     * @param reuse when true and is not possible apply format
     *              return the original input as result, otherwsise return empty.
     */
    public ZipCodeFormat(ZipCodes zipCode, boolean reuse) {
        this.zipCode = Objects.requireNonNull(zipCode, "Zip code is null!");
        this.reuse = reuse;
    }

    /**
     * Create a Zip format by input zip code.
     * @param zipCode zip code type.
     */
    public ZipCodeFormat(ZipCodes zipCode) {
        this(zipCode, false);
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        requireValidState(obj);
        String str = String.valueOf(obj);
        MaskFormat maskFormat = new MaskFormat(zipCode.findFormat(str));
        maskFormat.format(obj, toAppendTo, pos);
        if(reuse && toAppendTo.length() == 0) {
            toAppendTo.append(StringUtils.digitLettersOnly(str));
        }
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("ZipCodeFormat can not parse formatted string back to original Object!");
    }

    private static void requireValidState(Object obj) {
        if(obj == null) {
            throw new NullPointerException();
        } else if(!(obj instanceof CharSequence || obj instanceof Number || obj.getClass() == Short.TYPE || obj.getClass() == Integer.TYPE || obj.getClass() == Long.TYPE || obj.getClass() == Float.TYPE || obj.getClass() == Double.TYPE)) {
            throw new UnsupportedOperationException("Value of type \"" + obj.getClass().getName() +
                    "\" is not a valid type for ZipCodeFormat:\n" +
                    "Use a String, CharSequence or any Number type!" );
        }
    }
}
