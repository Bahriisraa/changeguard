package com.changeguard.approval;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FreezePolicyTest {

    private static final String START = "2026-12-05T00:00:00Z";
    private static final String END = "2026-12-06T00:00:00Z";

    @Test
    void noFreezeWhenBothBoundsAreEmpty() {
        FreezePolicy policy = new FreezePolicy("", "");
        assertFalse(policy.isFrozen(Instant.parse("2026-12-05T10:00:00Z")));
    }

    @Test
    void frozenInsideTheWindow() {
        FreezePolicy policy = new FreezePolicy(START, END);
        assertTrue(policy.isFrozen(Instant.parse("2026-12-05T10:00:00Z")));
    }

    @Test
    void startIsIncluded() {
        FreezePolicy policy = new FreezePolicy(START, END);
        assertTrue(policy.isFrozen(Instant.parse(START)));
    }

    @Test
    void endIsExcluded() {
        FreezePolicy policy = new FreezePolicy(START, END);
        assertFalse(policy.isFrozen(Instant.parse(END)));
    }

    @Test
    void noFreezeWhenOnlyOneBoundIsDefined() {
        FreezePolicy policy = new FreezePolicy(START, "");
        assertFalse(policy.isFrozen(Instant.parse("2026-12-05T10:00:00Z")));
    }
}