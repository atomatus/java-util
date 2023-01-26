package com.atomatus.util.mod;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public abstract class ModWeight {

    protected abstract int nextInt();

    protected int calc(int number, int weight) {
        return number * weight;
    }

    protected abstract ModWeight state();

    public static class Leitcode extends Pair {
        public Leitcode(int leitcode) {
            super(leitcode, 1);
        }

        @Override
        protected ModWeight state() {
            return new Leitcode(codes[LEITCODE_INDEX]);
        }
    }

    public static class Pair extends ModWeight {

        protected static final int LEITCODE_INDEX = 0;
        protected static final int IDENTCODE_INDEX = 1;

        final int[] codes;
        private int index;

        public Pair(int leitcode, int identcode) {
            this.codes = new int[]{leitcode, identcode};
        }

        @Override
        protected synchronized int nextInt() {
            try{
                return codes[index];
            } finally {
                index = index == LEITCODE_INDEX ?
                        IDENTCODE_INDEX :
                        LEITCODE_INDEX;
            }
        }

        @Override
        protected ModWeight state() {
            return new Pair(codes[LEITCODE_INDEX], codes[IDENTCODE_INDEX]);
        }
    }

    public static class Asc extends ModWeight {

        private int i;
        private final int start;
        private final int max;

        public Asc(int start, int max) {
            this.start = start;
            this.max = max;
            this.i = start;
        }

        public Asc(int start) {
            this(start, -1);
        }

        @Override
        protected int nextInt() {
            if(max != -1 && i > max) {
                i = start;
            }
            return i++;
        }

        @Override
        protected ModWeight state() {
            return new Asc(start, max);
        }
    }

    public static class None extends ModWeight {

        @Override
        protected int nextInt() {
            return 0;
        }

        @Override
        protected int calc(int number, int weight) {
            return number;
        }

        @Override
        protected ModWeight state() {
            return new None();
        }
    }
}
