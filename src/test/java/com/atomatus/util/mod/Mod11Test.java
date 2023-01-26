package com.atomatus.util.mod;

import junit.framework.TestCase;

public class Mod11Test extends TestCase {

    public void testMod11PZN() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo11
        Mod mod = Mod.get(Mod.MOD_11);
        int r = mod.calc("6 3 1 9 4 2");
        assertEquals(108, r);
        int m = mod.mod("6 3 1 9 4 2");
        assertEquals(9, m);
        int c = mod.checkum("6 3 1 9 4 2");
        assertEquals(2, c);
    }

    public void testMod11ISBN_10_AND_ISSN() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo11
        Mod mod = Mod.get(Mod.MOD_11).reverse(true);
        int r = mod.calc("3 9 2 8 4 4 4 0 4");
        assertEquals(251, r);
        int m = mod.mod("3 9 2 8 4 4 4 0 4");
        assertEquals(9, m);
        int c = mod.checkum("3 9 2 8 4 4 4 0 4");
        assertEquals(2, c);
    }

    public void testMod11CPF_BrazilNacionalID() {
        for (String cpf : new String[]{
                "48702078007",
                "17071252017",
                "14980906052",
                "05902545021",
                "08839178074"
        }) {
            Mod mod = Mod.get(Mod.MOD_11).reverse(true);
            int d0 = mod.checkum(cpf.substring(0, cpf.length() - 2));
            int firstDigit = d0 >= 10 ? 0 : d0;
            assertEquals("First Digit Test failed for CPF " + cpf,
                    Character.getNumericValue(cpf.charAt(cpf.length() - 2)), firstDigit);

            int d1 = mod.checkum(cpf.substring(0, cpf.length() - 1));
            int secondDigit = d1 >= 10 ? 0 : d1;
            assertEquals("Second Digit Test failed for CPF " + cpf,
                    Character.getNumericValue(cpf.charAt(cpf.length() - 1)), secondDigit);
        }
    }

    public void testMod11CNPJ_BrazilNacionalIDForCompany() {
        for (String cnpj : new String[]{
                "31628392000161",
                "10857191000166",
                "16085107000110",
                "21971900000156",
                "30095982000103",
                "14619448000100",
                "06399977000100",
                "03841225000169"
        }) {
            Mod mod = Mod.get(Mod.MOD_11).reverse(true).weight(new ModWeight.Asc(2, 9));
            int d0 = mod.mod(cnpj.substring(0, cnpj.length() - 2));
            int firstDigit = d0 < 2 ? 0 : 11 - d0;
            assertEquals("First Digit Test failed for CNPJ " + cnpj,
                    Character.getNumericValue(cnpj.charAt(cnpj.length() - 2)), firstDigit);

            int d1 = mod.mod(cnpj.substring(0, cnpj.length() - 1));
            int secondDigit = d1 < 2 ? 0 : 11 - d1;
            assertEquals("Second Digit Test failed for CNPJ " + cnpj,
                    Character.getNumericValue(cnpj.charAt(cnpj.length() - 1)), secondDigit);
        }
    }

}