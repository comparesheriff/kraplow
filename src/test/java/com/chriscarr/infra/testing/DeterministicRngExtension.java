package com.chriscarr.infra.testing;

import com.chriscarr.infra.Rng;
import org.junit.jupiter.api.extension.*;

import java.util.Random;

public class DeterministicRngExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NS =
        ExtensionContext.Namespace.create(DeterministicRngExtension.class);
    private static final String KEY = "prevRng";

    @Override
    public void beforeEach(ExtensionContext context) {
        applySeed(context);

    }

    @Override
    public void afterEach(ExtensionContext context) {
        restore(context);

    }

    @Override
    public void beforeAll(ExtensionContext context) {
        applySeed(context);

    }

    @Override
    public void afterAll(ExtensionContext context) {
        restore(context);

    }

    private void applySeed(ExtensionContext context) {
        DeterministicRng ann = findAnnotation(context);
        if (ann == null) return;
        ExtensionContext.Store store = context.getStore(NS);
        store.put(KEY, Rng.get());
        Rng.seed(ann.seed());

    }

    private void restore(ExtensionContext context) {
        Random prev = context.getStore(NS).remove(KEY, Random.class);
        if (prev != null) {
            Rng.set(prev);

        }

    }

    private DeterministicRng findAnnotation(ExtensionContext context) {
        return context.getElement()
            .map(el -> el.getAnnotation(DeterministicRng.class))
            .orElseGet(() -> context.getTestClass()
                .map(cls -> cls.getAnnotation(DeterministicRng.class))
                .orElse(null));

    }

}