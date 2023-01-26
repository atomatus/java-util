package com.atomatus.util.mod;

import com.atomatus.util.StringUtils;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
abstract class ModCode extends ModBase {

    protected ModCode(String codes, boolean ignoreCase) {
        super(new ModWeight.None(), false, false,
                new MapTranslateIndex(codes, ignoreCase),
                new MapFilter(codes, ignoreCase),
                new MapCheckumChar(codes, ignoreCase));
    }

    private static abstract class MapCallback {

        final int INVALID_INDEX = -1;

        private final String map;
        private final boolean ignoreCase;

        MapCallback(String map, boolean ignoreCase) {
            this.map = map;
            this.ignoreCase = ignoreCase;
        }

        int indexOf(char code) {
            return StringUtils.indexOf(map, code, ignoreCase);
        }

        char charAt(int index) {
            return map.charAt(index);
        }
    }

    protected static class MapTranslateIndex extends MapCallback implements NumericFunction {

        MapTranslateIndex(String map, boolean ignoreCase) {
            super(map, ignoreCase);
        }

        @Override
        public int getNumericValue(char code) {
            int i = indexOf(code);
            if(i == INVALID_INDEX) {
                throw new IllegalArgumentException("Character \"" + code + "\" is not valid for MODE16 calculate!");
            }
            return i;
        }
    }

    protected static class MapFilter extends MapCallback implements CharFilterFunction {

        MapFilter(String map, boolean ignoreCase) {
            super(map, ignoreCase);
        }

        @Override
        public boolean isValid(char code) {
            return indexOf(code) != INVALID_INDEX;
        }
    }

    protected static class MapCheckumChar extends MapCallback implements CheckumCharFunction {

        MapCheckumChar(String map, boolean ignoreCase) {
            super(map, ignoreCase);
        }

        @Override
        public char parse(int number) {
            return charAt(number);
        }
    }
}
