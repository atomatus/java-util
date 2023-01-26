package com.atomatus.util.mod;

/**
 * <h2>MOD 10</h2>
 * <h3>Luhn algorithm</h3>
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
 * <p>
 * <div style="background:black; color:white; padding:4px; width: 200px;">
 *     (10 - (67 mod 10))mod 10 = 3
 * </div>
 * <br>
 * This makes the full account number read: <b>7992739871<span style="background: #FFA;">3</span></b>.
 * <p>
 * See more <a href="https://activebarcode.com/codes/checkdigit/modulo10"?>here</a>.
 * <p>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 * @see Mod
 */
final class Mod10 extends ModBase {

    public Mod10() {
        super(new ModWeight.Pair(2, 1), true, true);
    }

    @Override
    protected int onMod(int i) {
        return i % 10;
    }

    @Override
    protected int onCheckum(int i) {
        return (i + (10 - i % 10)) - i;
    }
}
