package com.chriscarr.infra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RngScopedTest {

    @Test
    @Timeout(1)
    void scopedShuffle_sameSeed_sameOrder() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Integer> a = Rng.scoped(123, () -> {
            var copy = new ArrayList<>(list);
            Rng.shuffle(copy);
            return copy;
        });
        List<Integer> b = Rng.scoped(123, () -> {
            var copy = new ArrayList<>(list);
            Rng.shuffle(copy);
            return copy;
        });
        assertEquals(a, b);
    }

    @Test
    @Timeout(1)
    void scopedNextInt_sameSeed_sameSequence_and_noLeakage() {
        Rng.seed(9);
        int first = Rng.nextInt(100);
        List<Integer> inside = Rng.scoped(456, () -> List.of(Rng.nextInt(100), Rng.nextInt(100), Rng.nextInt(100)));
        int after = Rng.nextInt(100);

        Rng.seed(9);
        int first2 = Rng.nextInt(100);
        List<Integer> inside2 = Rng.scoped(456, () -> List.of(Rng.nextInt(100), Rng.nextInt(100), Rng.nextInt(100)));
        int after2 = Rng.nextInt(100);

        assertEquals(first, first2);
        assertEquals(inside, inside2);
        assertEquals(after, after2);
    }
}
