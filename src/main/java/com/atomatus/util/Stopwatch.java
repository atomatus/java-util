package com.atomatus.util;

/**
 * Stopwatch to check performance.
 */
public final class Stopwatch {

    private long start;
    private long result;

    /**
     * Start a new stopwatch.
     * @return instance of stopwatch started.
     */
    public static Stopwatch startNew() {
        Stopwatch sw = new Stopwatch();
        sw.start();
        return sw;
    }

    private Stopwatch() {
        start   = Long.MIN_VALUE;
        result  = Long.MIN_VALUE;
    }

    private void requireNonStarted() {
        if(start != Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch already be stated!");
        }
    }

    private void requireNonStopped() {
        if(result != Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch already be stopped!");
        }
    }

    private void requireStarted() {
        if(start == Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch was not initialized!");
        }
    }

    private void requireStopped() {
        if(result == Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch was not stopped!");
        }
    }

    /**
     * Start a new performance count.
     */
    public void start() {
        requireNonStarted();
        result = Long.MIN_VALUE;
        start  = System.nanoTime();
    }

    /**
     * Stop and calculate performance count.
     */
    public void stop() {
        long stop = System.nanoTime();
        requireNonStopped();
        requireStarted();
        if(result == Long.MIN_VALUE) {
            result = stop - start;
        } else {
            result += stop - start;
        }

        start = Long.MIN_VALUE;
    }

    /**
     * Reset stopwatch.
     */
    public void reset() {
        requireNonStarted();
        result  = Long.MIN_VALUE;
        start   = Long.MIN_VALUE;
    }

    /**
     * Restart stopwatch.
     */
    public void restart() {
        result  = Long.MIN_VALUE;
        start   = System.nanoTime();
    }

    public long getElapsedInNano() {
        requireStopped();
        return result;
    }

    public long getElapsedInMillis(){
        return getElapsedInNano() / 1000000;
    }

    public long getElapsedInSec(){
        return getElapsedInMillis() / 1000;
    }

    public long getElapsedInMin(){
        return getElapsedInSec() / 60;
    }
}
