package com.atomatus.util.mod;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
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
