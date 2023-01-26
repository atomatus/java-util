package com.atomatus.util.mod;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Stack;

/**
 * <p>
 * Mod base implementation for each Mod derived.
 * </p>
 * <i>Created by chcmatos on 25, janeiro, 2023</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 * @see Mod
 */
abstract class ModBase implements Mod {

    @FunctionalInterface
    protected interface NumericFunction {
        int getNumericValue(char code);
    }

    @FunctionalInterface
    protected interface CharFilterFunction {
        boolean isValid(char code);
    }

    @FunctionalInterface
    protected interface CheckumCharFunction {
        char parse(int number);
    }

    protected ModWeight weight;
    protected boolean reverse;
    protected boolean sumDigits;
    protected final NumericFunction numericCallback;
    protected final CharFilterFunction filterFunction;
    protected final CheckumCharFunction checkumCharFunction;

    public ModBase(ModWeight weight,
                   boolean rightToLeft,
                   boolean sumDigits,
                   NumericFunction numericCallback,
                   CharFilterFunction filterFunction,
                   CheckumCharFunction checkumCharFunction) {
        this.weight(weight);
        this.reverse = rightToLeft;
        this.sumDigits = sumDigits;
        this.numericCallback = numericCallback;
        this.filterFunction = filterFunction;
        this.checkumCharFunction = checkumCharFunction;
    }

    public ModBase(ModWeight weight,
                   boolean rightToLeft,
                   boolean sumDigits) {
        this(weight, rightToLeft, sumDigits,
                Character::getNumericValue,
                Character::isDigit,
                ModBase::onCheckumCharCallback);
    }

    @Override
    public Mod weight(ModWeight weight) {
        this.weight = Objects.requireNonNull(weight, "Mod weight can not be null!");
        return this;
    }

    @Override
    public Mod reverse() {
        return reverse(!reverse);
    }

    @Override
    public Mod reverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    @Override
    public Mod sumDigits(boolean sumDigits) {
        this.sumDigits = sumDigits;
        return this;
    }

    @Override
    public int calc(long value) {
        return this.onCalc(new CalcParams(reverse ?
                new OfIntDesc(value) :
                new OfIntAsc(value), weight.state(), sumDigits));
    }

    @Override
    public int calc(CharSequence value) {
        if(Objects.requireNonNull(value).length() == 0) {
            throw new IllegalArgumentException("Value is empty!");
        }
        return this.onCalc(new CalcParams(reverse ?
                new CharDesc(value, numericCallback, filterFunction) :
                new CharAsc(value, numericCallback, filterFunction), weight.state(), sumDigits));
    }

    @Override
    public int checkum(long value) {
        return onCheckum(calc(value));
    }

    @Override
    public int checkum(CharSequence value) {
        return onCheckum(calc(value));
    }

    @Override
    public char checkumChar(CharSequence value) {
        return checkumCharFunction.parse(onCheckum(calc(value)));
    }

    @Override
    public int mod(long value) {
        return onMod(calc(value));
    }

    @Override
    public int mod(CharSequence value) {
        return onMod(calc(value));
    }

    protected int onSumDigits(int i) {
        if(i < 10) {
            return i;
        } else if (i < 20) {
            return i - 9;
        } else {
            int j = 0;
            while(i > 0) {
                j += i % 10;
                i /= 10;
            }
            return onSumDigits(j);
        }
    }

    protected int onCalc(CalcParams params) {
        int i = 0;
        while(params.digits.hasNext()) {
            int d = params.digits.nextInt();
            int w = params.weight.nextInt();
            int m = params.weight.calc(d, w);
            i += params.sumDigits ? onSumDigits(m) : m;
        }
        return i;
    }

    protected abstract int onMod(int i);

    protected abstract int onCheckum(int i);

    private static char onCheckumCharCallback(int i) {
        return (char) ('0' + i);
    }

    private static class OfIntAsc extends Stack<Integer> implements PrimitiveIterator.OfInt {

        public OfIntAsc(long i) {
            for (OfIntDesc it = new OfIntDesc(i); it.hasNext(); ) {
                push(it.next());
            }
        }

        @Override
        public int nextInt() {
            return pop();
        }

        @Override
        public boolean hasNext() {
            return !isEmpty();
        }
    }

    private static class OfIntDesc implements PrimitiveIterator.OfInt {

        private static final int BASE_10 = 10;
        private long i;

        OfIntDesc(long i) {
            this.i = i;
        }

        @Override
        public int nextInt() {
            int r = (int) (i % BASE_10);
            i /= BASE_10;
            return r;
        }

        @Override
        public boolean hasNext() {
            return i > 0;
        }
    }

    private static class CharAsc implements PrimitiveIterator.OfInt {

        private final NumericFunction numericCallback;
        private final CharFilterFunction filterFunction;
        private final CharSequence seq;
        private final int len;
        private int index;

        CharAsc(CharSequence seq, NumericFunction numericCallback, CharFilterFunction filterFunction) {
            this.seq = seq;
            this.len = seq.length();
            this.numericCallback = numericCallback;
            this.filterFunction = filterFunction;
        }

        @Override
        public int nextInt() {
            return numericCallback.getNumericValue(seq.charAt(index++));
        }

        @Override
        public boolean hasNext() {
            while (index < len) {
                if(filterFunction.isValid(seq.charAt(index))) {
                    return true;
                }
                index++;
            }
            return false;
        }
    }

    private static class CharDesc implements PrimitiveIterator.OfInt {

        private final CharSequence seq;
        private final NumericFunction numericCallback;
        private final CharFilterFunction filterFunction;
        private int index;

        CharDesc(CharSequence seq, NumericFunction numericCallback, CharFilterFunction filterFunction) {
            this.seq = seq;
            this.index = seq.length() - 1;
            this.numericCallback = numericCallback;
            this.filterFunction = filterFunction;
        }

        @Override
        public int nextInt() {
            return numericCallback.getNumericValue(seq.charAt(index--));
        }

        @Override
        public boolean hasNext() {
            while (index > -1) {
                if(filterFunction.isValid(seq.charAt(index))) {
                    return true;
                }
                index--;
            }
            return false;
        }
    }

    protected static class CalcParams {
        public final PrimitiveIterator.OfInt digits;
        public final ModWeight weight;
        public final boolean sumDigits;

        CalcParams(PrimitiveIterator.OfInt digits, ModWeight weight, boolean sumDigits) {
            this.digits = digits;
            this.weight = weight;
            this.sumDigits = sumDigits;
        }
    }
}
