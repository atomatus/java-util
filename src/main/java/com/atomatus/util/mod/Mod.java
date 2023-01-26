package com.atomatus.util.mod;

/**
 * <p>
 * Mod algorithm for digit verifier calculate.
 * <ul>
 *  <li>You can use {@link #MOD_10}, {@link #MOD_11}, {@link #MOD_16}, {@link #MOD_43} in {@link #get(int)} to instance it for each digit verifier type.</li>
 *  <li>Use {@link #weight} to setup an own weight number value for your calculate</li>
 *  <li>Use {@link #calc} to calculate the digits by weight and sum all it.</li>
 *  <li>Use {@link #mod} to calculate the mod.</li>
 *  <li>Use {@link #checkum} to calculate the checkum.</li>
 * </ul>
 * <p>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public interface Mod {

    //region mod types
    /**
     * <b>MOD 10</b>
     * <p>
     * The Luhn algorithm or Luhn formula, also known as the "modulus 10" or "mod 10" algorithm,
     * named after its creator, IBM scientist Hans Peter Luhn, is a simple checksum formula used
     * to validate a variety of identification numbers, such as credit card numbers, IMEI numbers,
     * National Provider Identifier numbers in the United States, Canadian social insurance numbers,
     * Israeli ID numbers, South African ID numbers, Swedish national identification numbers,
     * Swedish Corporate Identity Numbers (OrgNr), Greek Social Security Numbers (ΑΜΚΑ),
     * SIM card numbers, European patent application number and survey codes appearing on
     * McDonald's, Taco Bell, and Tractor Supply Co. receipts.
     * <br>
     * It is described in U.S. Patent No. 2,950,048, granted on August 23, 1960.
     * <br>
     * <table style="background:#F8F8F; color:#000; padding:10px;">
     * <caption>A calculation example:</caption>
     * <tbody><tr>
     * <td style="width:1.5em">
     * </td>
     * <td style="width:1.5em">7
     * </td>
     * <td style="width:1.5em">9
     * </td>
     * <td style="width:1.5em">9
     * </td>
     * <td style="width:1.5em">2
     * </td>
     * <td style="width:1.5em">7
     * </td>
     * <td style="width:1.5em">3
     * </td>
     * <td style="width:1.5em">9
     * </td>
     * <td style="width:1.5em">8
     * </td>
     * <td style="width:1.5em">7
     * </td>
     * <td style="width:1.5em">1
     * </td></tr>
     * <tr>
     * <th>Multipliers
     * </th>
     * <td>1
     * </td>
     * <td>2
     * </td>
     * <td>1
     * </td>
     * <td>2
     * </td>
     * <td>1
     * </td>
     * <td>2
     * </td>
     * <td>1
     * </td>
     * <td>2
     * </td>
     * <td>1
     * </td>
     * <td>2
     * </td></tr>
     * <tr>
     * <th>
     * </th>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td>
     * <td>=
     * </td></tr>
     * <tr>
     * <th>
     * </th>
     * <td>7
     * </td>
     * <td style="background: #FFA;"><b>18</b>
     * </td>
     * <td>9
     * </td>
     * <td style="background: #FFA;"><b>4</b>
     * </td>
     * <td>7
     * </td>
     * <td style="background: #FFA;"><b>6</b>
     * </td>
     * <td>9
     * </td>
     * <td style="background: #FFA;"><b>16</b>
     * </td>
     * <td>7
     * </td>
     * <td style="background: #FFA;"><b>2</b>
     * </td></tr>
     * <tr>
     * <th>Sum digits
     * </th>
     * <td>7
     * </td>
     * <td><b>9</b> (1+8)
     * </td>
     * <td>9
     * </td>
     * <td><b>4</b>
     * </td>
     * <td>7
     * </td>
     * <td><b>6</b>
     * </td>
     * <td>9
     * </td>
     * <td><b>7</b> (1+6)
     * </td>
     * <td>7
     * </td>
     * <td><b>2</b>
     * </td></tr></tbody></table>
     * <p>
     * The sum of the resulting digits is 67.
     * <p>
     * The check digit is equal to
     * <br>
     * <div style="background:black; color:white; padding:4px; width: 200px;">
     *     (10 - (67 mod 10))mod 10 = 3
     * </div>
     * <br>
     * This makes the full account number read: <b>7992739871<span style="background: #FFA;">3</span></b>.
     * <p>
     * See more <a href="https://activebarcode.com/codes/checkdigit/modulo10">here</a>.
     */
    int MOD_10 = 10;

    /**
     * <b>MOD 11</b><br>
     * <i>ISBN, PZN, IBM algorithm to verify digit</i>
     * <p>
     * Some bar code types and modifiers call for the calculation and presentation of check digits.
     * Check digits are a method of verifying data integrity during the bar coding reading process.
     * <br><br>
     * <b>The ISBN-10 and the ISSN checksum</b><br>
     * First all digits are multiplied individually with a multiplier. The multiplier corresponds to the position of the digit + 1 from the right. All resulting products are added. The result is then divided by 11. The resulting remainder is subtracted from 11 and results in the check digit.
     * If result 11 is calculated for this checksum procedure, the checksum is 0. As result the 0 could not be calculated, because in the previous step (remainder with division by 11) 11 is impossible as a result.
     * It may also happen that the digit 10 is required. 10 is represented by an X: "3-928444-00-X".
     * <br><br>
     * <table style="background:#F8F8F; color:#000; padding:10px;">
     * <caption>A calculation example:</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>3 9 2 8 4 4 4 0 4</td>
     * </tr>
     * <tr>
     * <td>Weight:</td>
     * <td>10 9 8 7 6 5 4 3 2</td>
     * </tr>
     * <tr>
     * <td>Results:</td>
     * <td>30 + 81 + 16 + 56 + 24 + 20 + 16 + 0 + 8 = 251</td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>251 / 11 = 22 Remainder 9 -&gt; 11 - 9 = <b>2</b></td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td><b>2</b></td>
     * </tr>
     * </tbody></table>
     * <p>
     * See more <a href="https://activebarcode.com/codes/checkdigit/modulo11">here</a>.
     */
    int MOD_11 = 11;

    /**
     * <b>MOD 16</b><br>
     * <i>Codabar</i>
     * <p>
     * Some bar code types and modifiers call for the calculation and presentation of check digits.
     * Check digits are a method of verifying data integrity during the bar coding reading process.
     * <br><br>
     * <b>Modulo 16</b><br>
     * It is used by the barcode symbology Codabar. Here is an example of how to calculate the check digit according to Modulo 16:
     * <br><br>
     * <table style="background:#F8F8F; color:#000; padding:10px;">
     * <caption>A calculation example:</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>A 7 8 9 A</td>
     * </tr>
     * <tr>
     * <td>Reference numbers:</td>
     * <td>16 7 8 9 16</td>
     * </tr>
     * <tr>
     * <td>Sum of reference numbers:</td>
     * <td>56</td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>56 / 16 = 3 Remainder 8</td>
     * </tr>
     * <tr>
     * <td>difference:</td>
     * <td>16 - 8 = 8</td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td>Reference number 8 = <b>8</b></td>
     * </tr>
     * </tbody></table>
     * <br><br>
     * <table style="background:#F8F8F; color:#000; padding:10px;">
     * <caption>Reference Numbers:</caption>
     * <tbody><tr>
     * <td style="background:#F8F8F8; vertical-align:top; padding: 10px;">
     * 00 = 0 <br>
     * 01 = 1 <br>
     * 02 = 2 <br>
     * 03 = 3 <br>
     * 04 = 4 <br>
     * 05 = 5 <br>
     *  06 = 6
     * </td>
     * <td style="background:#FFFFEE; vertical-align:top; padding: 10px;">
     * 07 = 7 <br>
     * 08 = 8 <br>
     * 09 = 9 <br>
     * 10 = - <br>
     * 11 = $ <br>
     * 12 = : <br>
     * 13 = /
     * </td>
     * <td style="background:#F8F8F8; vertical-align:top; padding: 10px;">
     * 14 = . <br>
     * 15 = + <br>
     * 16 = A <br>
     * 17 = B <br>
     * 18 = C <br>
     * 19 = D
     * </td>
     * </tr>
     * </tbody></table>
     * See more <a href="https://activebarcode.com/codes/checkdigit/modulo16">here</a>.
     */
    int MOD_16 = 16;

    /**
     * <b>MOD 43</b><br>
     * <i>Code39 (3-of-9 code), AIM USS-39</i>
     * <p>
     * Some bar code types and modifiers call for the calculation and presentation of check digits.
     * Check digits are a method of verifying data integrity during the bar coding reading process.
     * <br><br>
     * <h4>Calculation of a checksum according to Modulo 43:</h4>
     * <p>
     * First, reference numbers are assigned to all characters of the code. These reference numbers are added to a total. This sum is divided by 43. The rest of this division corresponds to the checksum, which is then replaced by the character corresponding to the reference number is represented.
     * <br><br>
     * <table style="background:#F8F8F; color:#000; padding:10px;">
     * <caption>A calculation example:</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>1 5 9 A Z</td>
     * </tr>
     * <tr>
     * <td>Reference numbers:</td>
     * <td>1 +5 +9 +10 +35</td>
     * </tr>
     * <tr>
     * <td>Sum of reference numbers:</td>
     * <td>60</td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>60 / 43 = 1 Remainder <b>17</b></td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td>"<b>H</b>" (Reference number 17)</td>
     * </tr>
     * </tbody></table>
     * <br><br>
     * <table style="background:#F8F8F8; padding:10px;">
     * <caption>Reference numbers:</caption>
     * <tbody>
     * <tr>
     * <td style="vertical-align:top; padding: 10px;">
     * 00 0 <br>
     * 01 1 <br>
     * 02 2 <br>
     * 03 3 <br>
     * 04 4 <br>
     * 05 5 <br>
     * 06 6 <br>
     * 07 7 <br>
     * 08 8 <br>
     * 09 9 <br>
     * 10 A
     * </td>
     * <td style="vertical-align:top; padding: 10px;">
     * 11 B <br>
     * 12 C <br>
     * 13 D <br>
     * 14 E <br>
     * 15 F <br>
     * 16 G <br>
     * 17 H <br>
     * 18 I <br>
     * 19 J <br>
     * 20 K <br>
     * 21 L
     * </td>
     * <td style="vertical-align:top; padding: 10px;">
     * 22 M <br>
     * 23 N <br>
     * 24 O <br>
     * 25 P <br>
     * 26 Q <br>
     * 27 R <br>
     * 28 S <br>
     * 29 T <br>
     * 30 U <br>
     * 31 V <br>
     * 32 W</td>
     * <td style="vertical-align:top; padding: 10px;">
     * 33 X <br>
     * 34 Y <br>
     * 35 Z <br>
     * 36 - <br>
     * 37 . <br>
     * 38 Space<br>
     * 39 $ <br>
     * 40 / <br>
     * 41 + <br>
     * 42 %</td>
     * </tr>
     * </tbody></table>
     * <p>
     * See more <a href="https://activebarcode.com/codes/checkdigit/modulo43">here</a>.
     */
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
     * <dl>
     * <dt>Mod sum each calculated digits.</dt>
     * </dl>
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
     * @param sumDigits true sum calculated digits, otherwise false.
     * @return mod reference.
     */
    Mod sumDigits(boolean sumDigits);

    /**
     * Calculate digits by weight rule {@link #weight(ModWeight)}.
     * @param value target value
     * @return calculated weight digits result
     */
    int calc(long value);

    /**
     * Calculate digits by weight rule {@link #weight(ModWeight)}.
     * @param value target value
     * @return calculated weight digits result
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    int calc(CharSequence value);

    /**
     * Calculate digits checkum based at {@link #mod}, and it is based in {@link #calc}.
     * @param value target value
     * @return calculated digits verify
     */
    int checkum(long value);

    /**
     * Calculate digits checkum based at {@link #mod}, and it is based in {@link #calc}.
     * @param value target value
     * @return calculated digits verify
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    int checkum(CharSequence value);

    /**
     * Calculate digits checkum based at {@link #mod}, and it is based in {@link #calc}.
     * @param value target value
     * @return calculated digits verify converted in char.
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    char checkumChar(CharSequence value);

    /**
     * Calculate mod by digits weight result.
     * @param value target value
     * @return mod result
     */
    int mod(long value);

    /**
     * Calculate mod by digits weight result.
     * @param value target value
     * @return mod result
     * @throws NullPointerException throws if value is null.
     * @throws IllegalArgumentException throws if value is empty.
     */
    int mod(CharSequence value);

}
