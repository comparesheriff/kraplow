package com.chriscarr.bang.userinterface;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageImplTest {
    @Test
    @Timeout(2)
    void idsAreUniqueUnderConcurrency() throws Exception {
        int n = 500;
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            Set<Integer> ids = ConcurrentHashMap.newKeySet();
            CountDownLatch start = new CountDownLatch(1);
            for (int i = 0; i < n; i++) {
                pool.submit(() -> {
                    try {
                        start.await();
                    } catch (InterruptedException ignored) {
                    }
                    ids.add(new MessageImpl("x").getId());
                });
            }
            start.countDown();
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.SECONDS);
            assertEquals(n, ids.size(), "all IDs must be unique");
        }
    }
}