package com.chriscarr.infra;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class Rng {
    private static volatile Random rng = new Random();

    private Rng() {
    }

    public static Random get() {
        return rng;
    }

    public static void set(Random newRng) {
        rng = Objects.requireNonNull(newRng);
    }

    public static void seed(long seed) {
        set(new Random(seed));
    }

    public static int nextInt(int bound) {
        return rng.nextInt(bound);
    }

    public static <T> void shuffle(List<T> list) {
        Collections.shuffle(list, rng);
    }
}
