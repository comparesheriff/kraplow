package com.chriscarr.infra;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public final class Rng {
    private static final ThreadLocal<Random> TL = new ThreadLocal<>();
    private static volatile Random rng = new Random();

    private Rng() {
    }

    public static Random get() {
        Random r = TL.get();
        return r != null ? r : rng;
    }

    public static <T> T scoped(long seed, Supplier<T> body) {
        Random prev = TL.get();
        TL.set(new Random(seed));
        try {
            return body.get();
        } finally {
            TL.set(prev);
        }
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
