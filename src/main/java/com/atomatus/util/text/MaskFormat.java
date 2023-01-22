package com.atomatus.util.text;

import com.atomatus.util.StringUtils;

import java.io.Closeable;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * Custom <code>MaskFormat</code> is used to format and edit strings. The behavior
 * of a <code>MaskFormatter</code> is controlled by way of a String mask
 * that specifies the valid characters that can be contained at a particular
 * location in the <code>Document</code> model. The following characters can
 * be specified:
 * </p>
 *      <table>
 *              <caption>
 *                  <b>
 *                      Mask Characters<br>
 *                      Acceptable Object Type: String, Number or any other as String by toString
 *                  </b>
 *              </caption>
 *              <tr>
 *                  <td><b>Character</b></td>
 *                  <td><b>Description</b></td>
 *              </tr>
 *              <tr>
 *                  <td>#</td>
 *                  <td>Any valid number (Character.isDigit).</td>
 *              </tr>
 *              <tr>
 *                  <td>\(Backslash)&emsp;&emsp;</td>
 *                  <td>Use Backslash to indicate that next character when a pre-defined mask character must be ignored. Ex.: \A or \H</td>
 *              </tr>
 *              <tr>
 *                  <td>U</td>
 *                  <td>Any character (Character.isLetter). All lowercase letters are mapped to uppercase.</td>
 *              </tr>
 *              <tr>
 *                  <td>L</td>
 *                  <td>Any character (Character.isLetter). All uppercase letters are mapped to lowercase.</td>
 *              </tr>
 *              <tr>
 *                  <td>A</td>
 *                  <td>Any character or number (Character.isLetter or Character.isDigit).</td>
 *              </tr>
 *              <tr>
 *                  <td>?</td>
 *                  <td>Any character (Character.isLetter).</td>
 *              </tr>
 *              <tr>
 *                  <td>*</td>
 *                  <td>Anything.</td>
 *              </tr>
 *              <tr>
 *                  <td>H</td>
 *                  <td>Any hex character (0-9, a-f or A-F).</td>
 *              </tr>
 *      </table>
 *      <table>
 *              <caption>
 *                  <b>Mask Characters for Date Time Exclusive</b><br>
 *                  <b>Acceptable Object Type: Calendar, Date, LocalDate or LocalDateTime</b>
 *              </caption>
 *              <tr>
 *                  <td><b>Character</b>&emsp;&emsp;</td>
 *                  <td><b>Description</b>&emsp;&emsp;</td>
 *              </tr>
 *              <tr>
 *                  <td>DD</td>
 *                  <td>Day number</td>
 *              </tr>
 *              <tr>
 *                  <td>DDD</td>
 *                  <td>Day  of week short</td>
 *              </tr>
 *              <tr>
 *                  <td>DDDD</td>
 *                  <td>Day  of week long</td>
 *              </tr>
 *              <tr>
 *                  <td>MM</td>
 *                  <td>Month number</td>
 *              </tr>
 *              <tr>
 *                  <td>MMM</td>
 *                  <td>Month name short</td>
 *              </tr>
 *              <tr>
 *                  <td>MMM</td>
 *                  <td>Month name full</td>
 *              </tr>
 *              <tr>
 *                  <td>YY</td>
 *                  <td>Short year</td>
 *              </tr>
 *              <tr>
 *                  <td>YYYY</td>
 *                  <td>Long year</td>
 *              </tr>
 *              <tr>
 *                  <td>HH</td>
 *                  <td>hour</td>
 *              </tr>
 *              <tr>
 *                  <td>mm</td>
 *                  <td>minutes</td>
 *              </tr>
 *              <tr>
 *                  <td>ss</td>
 *                  <td>seconds</td>
 *              </tr>
 *              <tr>
 *                  <td>t</td>
 *                  <td>A.M or P.M short</td>
 *              </tr>
 *              <tr>
 *                  <td>tt</td>
 *                  <td>A.M or P.M</td>
 *              </tr>
 *              <tr>
 *                  <td>zz</td>
 *                  <td>time zone hour</td>
 *              </tr>
 *              <tr>
 *                  <td>zzz</td>
 *                  <td>time zone full</td>
 *              </tr>
 *      </table>
 *      <table>
 *              <caption>
 *                  <b>See bellow usage examples</b>
 *              </caption>
 *              <tr>
 *                  <td><b>Target</b>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;</td>
 *                  <td><b>Mask</b>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;</td>
 *                  <td><b>Result</b></td>
 *              </tr>
 *              <tr>
 *                  <td>02289554</td>
 *                  <td>####-###</td>
 *                  <td>02289-554</td>
 *              </tr>
 *              <tr>
 *                  <td>PC02289554CP</td>
 *                  <td>####-###</td>
 *                  <td>02289-554</td>
 *              </tr>
 *              <tr>
 *                  <td>PC022895545P</td>
 *                  <td>?? ####-###/AA</td>
 *                  <td>PC 02289-554/5P</td>
 *              </tr>
 *              <tr>
 *                  <td>11968792023</td>
 *                  <td>(##) # ####-####</td>
 *                  <td>(11) 9 6879-2023</td>
 *              </tr>
 *      </table>
 * <br>
 * <i>Created by chcmatos on 21, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public class MaskFormat extends Format {

    /**
     * Mask type.
     */
    private final String mask;

    /**
     * Create a MaskFormat instance by input mask.
     * @param mask input mask.
     */
    public MaskFormat(String mask) {
        this.mask = Objects.requireNonNull(mask, "Mask is null!");
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        try (DefaultMaskHandler handler = new DefaultMaskHandler()) {
            handler.next(new CalendarMaskHandler())
                    .next(new DateMaskHandler())
                    .next(new TemporalAccessorMaskHandler());
            return handler.format(mask, obj, toAppendTo, pos);
        }
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("MaskFormat can not parse formatted string back to original Object!");
    }

    //region Mask handler (chain of responsibility pattern)
    private abstract static class MaskHandler<T> implements Closeable {

        private MaskHandler<?> next;

        final MaskHandler<?> next(MaskHandler<?> next) {
            return this.next = next;
        }

        final StringBuffer format(String mask, Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return onHandle(obj) ? handle(mask, onParse(obj), toAppendTo, pos) :
                    next != null ? next.format(mask, obj, toAppendTo, pos) : toAppendTo;
        }

        @SuppressWarnings("unchecked")
        T onParse(Object obj) {
            return (T) obj;
        }

        abstract boolean onHandle(Object obj);

        abstract StringBuffer handle(String mask, T obj, StringBuffer toAppendTo, FieldPosition pos);

        public void close() {
            if (next != null) {
                next.close();
                next = null;
            }
        }
    }

    private static class DefaultMaskHandler extends MaskHandler<CharSequence> {
        @Override
        boolean onHandle(Object obj) {
            return obj instanceof CharSequence ||
                    obj instanceof Number ||
                    (obj != null && (
                            obj.getClass() == Short.TYPE ||
                                    obj.getClass() == Integer.TYPE ||
                                    obj.getClass() == Long.TYPE ||
                                    obj.getClass() == Float.TYPE ||
                                    obj.getClass() == Double.TYPE));
        }

        @Override
        CharSequence onParse(Object obj) {
            return StringUtils.digitLettersOnly(obj.toString());
        }

        @Override
        StringBuffer handle(String mask, CharSequence obj, StringBuffer toAppendTo, FieldPosition pos) {
            try(CharHandler handler = new CharHandler('#', Character::isDigit)) {
                handler.next(new DigitCharHandler())
                        .next(new LetterCharHandler())
                        .next(new LetterDigitCharHandler())
                        .next(new LetterUpperCharHandler())
                        .next(new LetterLowerCharHandler())
                        .next(new AlphanumbericCharHandler())
                        .next(new AnyCharHandler())
                        .next(new BackSlashCharHandler())
                        .next(new MaskCharHandler());

                while (handler.handleable(mask, obj)) {
                    handler.handle(mask, obj, toAppendTo);
                }

                return toAppendTo;
            }
        }

        //region Char Handler
        static class CharHandler implements Closeable {
            @FunctionalInterface
            interface CheckCharCallback {
                boolean apply(char c);
            }

            private Pos pos;
            final char maskSymbol;
            CheckCharCallback maskRuleCallback;
            CharHandler next;

            CharHandler(char maskSymbol, CheckCharCallback maskRuleCallback) {
                this.maskSymbol = maskSymbol;
                this.maskRuleCallback = maskRuleCallback;
            }

            final boolean handleable(CharSequence mask, CharSequence obj) {
                return pos().maskIndex < mask.length() && pos().targetIndex < obj.length();
            }

            boolean isMaskSymbol(CharSequence mask) {
                return maskSymbol == mask.charAt(pos().maskIndex);
            }

            final void handle(CharSequence mask, CharSequence obj, StringBuffer toAppendTo) {
                if(isMaskSymbol(mask)) {
                    onHandle(mask, obj, pos(), toAppendTo);
                } else if(next != null) {
                    next.handle(mask, obj, toAppendTo);
                } else {
                    pos().targetIndex++;
                }
            }

            void onHandle(CharSequence mask, CharSequence obj, Pos pos, StringBuffer toAppendTo) {
                char c = obj.charAt(pos.targetIndex);
                if(maskRuleCallback.apply(c)) {
                    toAppendTo.append(c);
                    pos.maskIndex++;
                }
                pos.targetIndex++;
            }

            CharHandler next(CharHandler next) {
                next.pos = pos();
                return this.next = next;
            }

            private Pos pos() {
                if(pos == null) {
                    pos = new Pos();
                }
                return pos;
            }

            @Override
            public void close() {
                if(next != null) {
                    next.close();
                    next = null;
                }
                pos = null;
                maskRuleCallback = null;
            }

            static class Pos {
                int targetIndex;
                int maskIndex;
            }
        }

        static class DigitCharHandler extends CharHandler {
            DigitCharHandler() {
                super('#', Character::isDigit);
            }
        }

        static class LetterCharHandler extends CharHandler {
            LetterCharHandler() {
                super('?', Character::isLetter);
            }
        }

        static class LetterDigitCharHandler extends CharHandler {
            LetterDigitCharHandler() {
                super('A', c -> Character.isLetter(c) || Character.isDigit(c));
            }
        }

        static class LetterUpperCharHandler extends CharHandler {
            LetterUpperCharHandler() {
                super('U', c -> Character.isLetter(c) && Character.isUpperCase(c));
            }
        }

        static class LetterLowerCharHandler extends CharHandler {
            LetterLowerCharHandler() {
                super('L', c -> Character.isLetter(c) && Character.isUpperCase(c));
            }
        }

        static class AlphanumbericCharHandler extends CharHandler {
            AlphanumbericCharHandler() {
                super('H', AlphanumbericCharHandler::isAlphaNumeric);
            }

            private static boolean isAlphaNumeric(char c) {
                return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
            }
        }

        static class AnyCharHandler extends CharHandler {
            AnyCharHandler() {
                super('*', c -> true);
            }
        }

        static class BackSlashCharHandler extends CharHandler {
            BackSlashCharHandler(){
                super('\\', null);
            }

            @Override
            void onHandle(CharSequence mask, CharSequence obj, Pos pos, StringBuffer toAppendTo) {
                toAppendTo.append(mask.charAt(++pos.maskIndex));
                pos.maskIndex++;
            }
        }

        static class MaskCharHandler extends CharHandler {

            MaskCharHandler(){
                super('\0', null);
            }

            @Override
            boolean isMaskSymbol(CharSequence mask) {
                return true;
            }

            @Override
            void onHandle(CharSequence mask, CharSequence obj, Pos pos, StringBuffer toAppendTo) {
                toAppendTo.append(mask.charAt(pos.maskIndex++));
            }
        }
        //endregion
    }

    private static class CalendarMaskHandler extends MaskHandler<Calendar> {

        @Override
        boolean onHandle(Object obj) {
            return obj instanceof Calendar;
        }

        @Override
        StringBuffer handle(String mask, Calendar obj, StringBuffer toAppendTo, FieldPosition pos) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(mask);
            dateFormat.setTimeZone(obj.getTimeZone());
            return dateFormat.format(obj.getTime(), toAppendTo, pos);
        }
    }

    private static class DateMaskHandler extends MaskHandler<Date> {

        @Override
        boolean onHandle(Object obj) {
            return obj instanceof Date;
        }

        @Override
        StringBuffer handle(String mask, Date obj, StringBuffer toAppendTo, FieldPosition pos) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(mask);
            return dateFormat.format(obj, toAppendTo, pos);
        }
    }

    private static class TemporalAccessorMaskHandler extends MaskHandler<TemporalAccessor> {

        @Override
        boolean onHandle(Object obj) {
            return obj instanceof TemporalAccessor;
        }

        @Override
        StringBuffer handle(String mask, TemporalAccessor obj, StringBuffer toAppendTo, FieldPosition pos) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mask);
            formatter.formatTo(obj, toAppendTo);
            return toAppendTo;
        }
    }
    //endregion
}
