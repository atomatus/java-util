package com.atomatus.util.mod;

/**
 * <h2>MOD 11</h2>
 * <h3>ISBN, PZN, IBM algorithm to verify digit</h3>
 * <p>
 * Some bar code types and modifiers call for the calculation and presentation of check digits.
 * Check digits are a method of verifying data integrity during the bar coding reading process.
 * <p>
 * <h4>The ISBN-10 and the ISSN checksum</h4>
 * First all digits are multiplied individually with a multiplier. The multiplier corresponds to the position of the digit + 1 from the right. All resulting products are added. The result is then divided by 11. The resulting remainder is subtracted from 11 and results in the check digit.
 * If result 11 is calculated for this checksum procedure, the checksum is 0. As result the 0 could not be calculated, because in the previous step (remainder with division by 11) 11 is impossible as a result.
 * It may also happen that the digit 10 is required. 10 is represented by an X: "3-928444-00-X".
 * <p>
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
 * See more <a href="https://activebarcode.com/codes/checkdigit/modulo11"?>here</a>.
 * <p>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
final class Mod11 extends ModBase {

    public Mod11() {
        super(new ModWeight.Asc(2), false, false);
    }

    @Override
    protected int onMod(int i) {
        return i % 11;
    }

    @Override
    protected int onCheckum(int i) {
        return 11 - (i % 11);
    }
}
