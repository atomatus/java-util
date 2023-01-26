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
 *
 * It is described in U.S. Patent No. 2,950,048, granted on August 23, 1960.
 *
 * <p>
 * See more about mod10 here
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
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
