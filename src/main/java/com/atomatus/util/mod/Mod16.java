package com.atomatus.util.mod;

/**
 * <h2>MOD 16</h2>
 * <h3>Codabar</h3>
 * <p>
 * Some bar code types and modifiers call for the calculation and presentation of check digits.
 * Check digits are a method of verifying data integrity during the bar coding reading process.
 * <p>
 * <h4>Modulo 16</h4>
 * It is used by the barcode symbology Codabar. Here is an example of how to calculate the check digit according to Modulo 16:
 * <p>
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
 * <p>
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
 * See more <a href="https://activebarcode.com/codes/checkdigit/modulo16"?>here</a>.
 * <p>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
final class Mod16 extends ModCode {

    public Mod16() {
        super("0123456789-$:/.+ABCD", true);
    }

    @Override
    protected int onMod(int i) {
        return i % 16;
    }

    @Override
    protected int onCheckum(int i) {
        return 16 - (i % 16);
    }

}
