package com.atomatus.util.mod;

/**
 * <p>
 * Mod algorithm for digit verifier calculate.
 * <p> -> You can use {@link #MOD_10}, {@link #MOD_11},
 * {@link #MOD_16}, {@link #MOD_43}
 * in {@link #get(int)} to instance it for each digit verifier type.
 * <p> -> Use {@link #weight} to setup an own weight number value for your calculate
 * <p> -> Use {@link #calc} to calculate the digit verifier.
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public interface Mod {

    //region mod types
    int MOD_10 = 10;
    int MOD_11 = 11;
    int MOD_16 = 16;
    int MOD_43 = 43;
    //endregion

    /**
     * Instance of Mod by mod types.
     * @param modType mod type
     *                Must be one of the following types
     *                {@link #MOD_10}, {@link #MOD_11},
     *                {@link #MOD_16}, {@link #MOD_43}.
     * @return mod instance by type.
     */
    static Mod get(int modType) {
        switch (modType) {
            case MOD_10:
                return new Mod10();
            case MOD_11:
                return new Mod11();
            case MOD_16:
                return new Mod16();
            case MOD_43:
                return new Mod43();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Mod weight to be set up on calculate rule.
     * @param weight weight positive value.
     * @return mod reference
     */
    Mod weight(ModWeight weight);

    /**
     * Mod reverse mode.
     * When enabled, begin digit calculate with weight righ-left (last digit until first digit),
     * otherwise, begin from first until last.
     * @param reverse true to begin calculate from last digit until first digit, otherwise false.
     * @return mod reference.
     */
    Mod reverse(boolean reverse);

    /**
     * Mod reverrse mode.
     * @see #reverse(boolean)
     * @return mod reference
     */
    Mod reverse();

    /**
     * Mod sum each calculated digits.
     * <p>
     *     <table>
     *         <caption>Weight calculate</caption>
     *         <tr>
     *             <td>Digit&nbsp;&nbsp;</td>
     *             <td>0&nbsp;&nbsp;</td>
     *             <td>7&nbsp;&nbsp;</td>
     *             <td>6&nbsp;&nbsp;</td>
     *             <td>3&nbsp;&nbsp;</td>
     *         </tr>
     *         <tr>
     *             <td>Weight&nbsp;</td>
     *             <td>1&nbsp;&nbsp;</td>
     *             <td>3&nbsp;&nbsp;</td>
     *             <td>1&nbsp;&nbsp;</td>
     *             <td>3&nbsp;&nbsp;</td>
     *         </tr>
     *         <tr>
     *             <td>True</td>
     *             <td>0&nbsp;&nbsp;</td>
     *             <td><b>3&nbsp;</b></td>
     *             <td>6&nbsp;&nbsp;</td>
     *             <td>9&nbsp;&nbsp;</td>
     *         </tr>
     *         <tr>
     *             <td>False</td>
     *             <td>0&nbsp;&nbsp;</td>
     *             <td><b>21&nbsp;</b></td>
     *             <td>6&nbsp;&nbsp;</td>
     *             <td>9&nbsp;&nbsp;</td>
     *         </tr>
     *     </table>
     *
     * @param sumDigits true sum calculated digits, otherwise false.
     * @return
     */
    Mod sumDigits(boolean sumDigits);

    /**
     *
     * @param value
     * @return
     */
    int calc(long value);

    /**
     *
     * @param value
     * @return
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    int calc(CharSequence value);

    /**
     *
     * @param value
     * @return
     */
    int checkum(long value);

    /**
     *
     * @param value
     * @return
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    int checkum(CharSequence value);

    /**
     *
     * @param value
     * @return
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    char checkumChar(CharSequence value);

    /**
     *
     * @param value
     * @return
     */
    int mod(long value);

    /**
     *
     * @param value
     * @return
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    int mod(CharSequence value);

}
