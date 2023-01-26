package com.atomatus.util.mod;

/**
 * <h2>MOD 43</h2>
 * <h3>Code39 (3-of-9 code), AIM USS-39</h3>
 * <p>
 * Some bar code types and modifiers call for the calculation and presentation of check digits.
 * Check digits are a method of verifying data integrity during the bar coding reading process.
 * <p>
 * <h4>Calculation of a checksum according to Modulo 43:</h4>
 * <p>
 * First, reference numbers are assigned to all characters of the code. These reference numbers are added to a total. This sum is divided by 43. The rest of this division corresponds to the checksum, which is then replaced by the character corresponding to the reference number is represented.
 * <p>
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
 * <p>
 * <table style="background:#F8F8F8; padding:10px;">
 * <capion>Reference numbers:</capion>
 * <tbody><tr>
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
 * See more <a href="https://activebarcode.com/codes/checkdigit/modulo43"?>here</a>.
 * <p>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
final class Mod43 extends ModCode {

    protected Mod43() {
        super("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%", true);
    }

    @Override
    protected int onMod(int i) {
        return i % 43;
    }

    @Override
    protected int onCheckum(int i) {
        return i % 43;
    }
}
