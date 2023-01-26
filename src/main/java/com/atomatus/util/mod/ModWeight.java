package com.atomatus.util.mod;

/**
 * <h2>Mod Weight Rule</h2>
 * This is used to calculate ({@link Mod#calc}) the mod weight by each digit,
 * to calculate each value alternating between leitcode and identcode by each read index
 * <br><br>
 * <ul>
 * <li>Use {@link ModWeight.Pair} to setup leitcode and identcode</li>
 * <li>Use {@link ModWeight.Leitcode} to setup leitcode only</li>
 * <li>Use {@link ModWeight.Asc} to setup a asc code starting in a n digit.</li>
 * <li>Use {@link ModWeight.None} to ignore weight calculate.</li>
 * </ul>
 * <i>Created by chcmatos on 25, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public abstract class ModWeight {

    /**
     * Next weight value.
     * @return weight value
     */
    protected abstract int nextInt();

    /**
     * Weight calculate
     * @param number digit
     * @param weight weight value
     * @return result
     */
    protected int calc(int number, int weight) {
        return number * weight;
    }

    /**
     * Preserve ModWeight original state.
     * @return ModWeight proxy
     */
    protected abstract ModWeight state();

    /**
     * <h2>Mod Weight Leitcode implementation</h2>
     * <p>
     *  To calculate each value alternating between leitcode and identcode by each read index,
     *  here identcode is 1 and leitcode may be changed, see bellow example with leitcode equals 3.
     * <table style="background:#F8F8F8; padding:10px;">
     * <caption>Weight with Leitcode = 3, IdentCode = 1</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>0 4 0 0 7 6 3 0 0 0 0 1 1</td>
     * </tr>
     * <tr>
     * <td>Weight:</td>
     * <td>3 1 3 1 3 1 3 1 3 1 3 1 3</td>
     * </tr>
     * <tr>
     * <td>Results:</td>
     * <td>0+4+0+0+21+6+9+0+0+0+0+1+3 = 44</td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>The addition to the next multiple of 10. (50)</td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td><b>6</b></td>
     * </tr>
     * </tbody></table>
     *
     * @author Carlos Matos {@literal @chcmatos}
     */
    public static class Leitcode extends Pair {
        /**
         * Construct a Leitcode ModWeight with identcode default value 1.
         * @param leitcode leitcode value.
         */
        public Leitcode(int leitcode) {
            super(leitcode, 1);
        }

        @Override
        protected ModWeight state() {
            return new Leitcode(codes[LEITCODE_INDEX]);
        }
    }

    /**
     * <h2>Mod Weight Pair implementation</h2>
     * <p>
     *  To calculate each value alternating between leitcode and identcode by each read index,
     *  here both leticode and identcode may be changed, see bellow example with leitcode equals 4 and identcode = 9.
     * <table style="background:#F8F8F8; padding:10px;">
     * <caption>Weight with Leitcode = 4, IdentCode = 9</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>2 3 6 6 9 0 1 2 0 1 2 3 0</td>
     * </tr>
     * <tr>
     * <td>Weight:</td>
     * <td>4 9 4 9 4 9 4 9 4 9 4 9 4</td>
     * </tr>
     * <tr>
     * <td>Results:</td>
     * <td>8+27+24+54+36+0+4+18+0+9+8+27+0 = 215 </td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>The addition to the next multiple of 10. (220)</td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td><b>5</b></td>
     * </tr>
     * </tbody></table>
     *
     * @author Carlos Matos {@literal @chcmatos}
     */
    public static class Pair extends ModWeight {

        /**
         * Leitcode index in {@link #codes}.
         */
        protected static final int LEITCODE_INDEX = 0;

        /**
         * Identcode index in {@link #codes}.
         */
        protected static final int IDENTCODE_INDEX = 1;

        final int[] codes;
        private int index;

        /**
         * Construct a Pair ModWeight.
         * @param leitcode leitcode value.
         * @param identcode identcode value.
         */
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

    /**
     * <h2>Mod Weight Asc implementation</h2>
     * To calculate each digit value from start weight and sum one for next weight.
     * See below two examples first left-right mode and second right-left (reversed {@link Mod#reverse}) mode, both
     * starting in 2.
     * <br><br>
     * <table style="background:#F8F8F8; padding:10px;">
     * <caption>Weight Asc left-right ({@link Mod#reverse}(false))</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>6 3 1 9 4 2</td>
     * </tr>
     * <tr>
     * <td>Weight:</td>
     * <td>2 3 4 5 6 7</td>
     * </tr>
     * <tr>
     * <td>Results:</td>
     * <td>12+9+4+45+24+14 = 108</td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>108 / 11 = 9 Remainder <b>9</b></td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td><b>9</b></td>
     * </tr>
     * </tbody></table>
     * <br><br>
     * <table style="background:#F8F8F8; padding:10px;">
     * <caption>Weight Asc right-left ({@link Mod#reverse}(true))</caption>
     * <tbody><tr>
     * <td>Digits:</td>
     * <td>3 9 2 8 4 4 4 0 4</td>
     * </tr>
     * <tr>
     * <td>Weight:</td>
     * <td>10 9 8 7 6 5 4 3 2</td>
     * </tr>
     * <tr>
     * <td>Results:</td>
     * <td>30 + 81 + 16 + 56 + 24 + 20 + 16 + 0 + 8 = 251</td>
     * </tr>
     * <tr>
     * <td>Calculate checksum:</td>
     * <td>251 / 11 = 22 Remainder 9 -&gt; 11 - 9 = <b>2</b></td>
     * </tr>
     * <tr>
     * <td><b>Check digit</b></td>
     * <td><b>2</b></td>
     * </tr>
     * </tbody></table>
     *
     * @author Carlos Matos {@literal @chcmatos}
     */
    public static class Asc extends ModWeight {

        private int i;
        private final int start;
        private final int max;

        /**
         * Construct an Asc ModWeight with restart posibility.
         * @param start start index
         * @param max max valeu, when hit it, restart.
         */
        public Asc(int start, int max) {
            this.start = start;
            this.max = max;
            this.i = start;
        }

        /**
         * Construct an Asc ModWeight.
         * @param start start index
         */
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

    /**
     * Use it to don't apply no one Weight rule.
     */
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
