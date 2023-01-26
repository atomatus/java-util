package com.atomatus.util.mod;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
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
