package com.changeguard.approval;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FreezePolicy {

    private final Instant start;
    private final Instant end;

    public FreezePolicy(@Value("${freeze.start:}") String start,
                        @Value("${freeze.end:}") String end) {
        this.start = start.isBlank() ? null : Instant.parse(start); // ex. 2026-12-10T08:00:00Z
        this.end = end.isBlank() ? null : Instant.parse(end);
    }

    // debut inclus, fin exclue ; une borne vide = pas de gel
    public boolean isFrozen(Instant now) {
        return start != null && end != null && !now.isBefore(start) && now.isBefore(end);
    }
}