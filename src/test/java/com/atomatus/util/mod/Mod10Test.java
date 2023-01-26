package com.atomatus.util.mod;

import junit.framework.TestCase;

public class Mod10Test extends TestCase {

    public void testMod10Calc() {
        //https://en.wikipedia.org/wiki/Luhn_algorithm#Example_for_computing_check_digit
        Mod mod = Mod.get(Mod.MOD_10);
        int r = mod.calc(7992739871L);
        assertEquals(67, r);
    }

    public void testMod10Checkum() {
        //https://en.wikipedia.org/wiki/Luhn_algorithm#Example_for_computing_check_digit
        Mod mod = Mod.get(Mod.MOD_10);
        int r = mod.checkum(7992739871L);
        assertEquals(3, r);
    }

    public void testMod10EAN() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo10
        Mod mod = Mod.get(Mod.MOD_10)
                .reverse(false)
                .sumDigits(false)
                .weight(new ModWeight.Leitcode(3));
        int r = mod.calc(400763000011L);
        int c = mod.checkum(400763000011L);
        assertEquals(44, r);
        assertEquals(6, c);
    }

    public void testMod10EAN14() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo10
        Mod mod = Mod.get(Mod.MOD_10)
                .reverse(false)
                .sumDigits(false)
                .weight(new ModWeight.Leitcode(3));
        int r = mod.calc("0400763000011");
        int c = mod.checkum("0 4 0 0 7 6 3 0 0 0 0 1 1");
        assertEquals(44, r);
        assertEquals(6, c);
    }

    public void testMod10Code25() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo10
        Mod mod = Mod.get(Mod.MOD_10)
                .reverse(false)
                .sumDigits(false)
                .weight(new ModWeight.Leitcode(3));
        int r = mod.calc(12345);
        int c = mod.checkum(12345);
        assertEquals(33, r);
        assertEquals(7, c);

        int j = mod.calc("12345");
        assertEquals(r, j);
    }

    public void testMod10LeitcodeIdentcode() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo10
        Mod mod = Mod.get(Mod.MOD_10)
                .reverse(false)
                .sumDigits(false)
                .weight(new ModWeight.Pair(4, 9));
        int r = mod.calc("2366901201230");
        assertEquals(215, r);
        int c = mod.checkum("2366901201230");
        assertEquals(5, c);
    }

    public void testMod10CreditCardNumber() {
        for(String card : new String[] {
                "4556 7375 8689 9855",
                "4703 2507 9600 3955",
                "4759 5337 7632 2674",
                "4637 2973 3095 4871",
                "5155 2788 8361 0424",
                "3485 0109 5486 9076",
                "6212 1193 2963 9842",
                "3528 2507 2274 5971",
                "3629 1999 6460 6359",
                "6056 9080 7997 7074"
        }) {
            Mod mod = Mod.get(Mod.MOD_10)
                    .reverse(true)
                    .sumDigits(true)
                    .weight(new ModWeight.Pair(2, 1));

            int m = mod.checkum(card.substring(0, card.length() - 1));
            assertEquals("Test failed for card number: " + card,
                    Character.getNumericValue(card.charAt(card.length() - 1)), m);

            // Luhn algorithm
            // MOD10:
            // 4  7  5  9  5  3  3  7  7  6  3  2  2  6  7 | 4
            // 2  1  2  1  2  1  2  1  2  1  2  1  2  1  2
            // =
            // 8  7  1  9  1  3  6  7  5  6  6  2  4  6  5
            // =
            // 76 (s)
            // (10 - s mod 10) mod 10
            // (10 - (76 % 10)) % 10
            // (10 - 6) % 10
            // 4 % 10
            // 4
        }
    }


}