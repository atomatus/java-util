package com.atomatus.util.mod;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
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
