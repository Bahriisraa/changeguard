package com.changeguard.change.api.dto;

import com.changeguard.change.domain.Wave;
import com.changeguard.change.domain.WaveStatus;
import java.time.Instant;

public record WaveResponse(Long id, Long changeId, int percentage, WaveStatus status,
                           Instant startedAt, Instant endedAt, Double observedErrorRate) {

    public static WaveResponse from(Wave w) {
        return new WaveResponse(w.getId(), w.getChangeId(), w.getPercentage(), w.getStatus(),
                w.getStartedAt(), w.getEndedAt(), w.getObservedErrorRate());
    }
}