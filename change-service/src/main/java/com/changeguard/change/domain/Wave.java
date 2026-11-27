package com.changeguard.change.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "waves")
public class Wave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long changeId;

    @Column(nullable = false)
    private int percentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaveStatus status = WaveStatus.PENDING;

    private Instant startedAt;
    private Instant endedAt;
    private Double observedErrorRate;

    protected Wave() { }

    public Wave(Long changeId, int percentage) {
        this.changeId = changeId;
        this.percentage = percentage;
    }

    public void start() {
        status = WaveStatus.RUNNING;
        startedAt = Instant.now();
        endedAt = null;
        observedErrorRate = null;
    }

    public void finish(WaveStatus result, Double errorRate) {
        status = result;
        endedAt = Instant.now();
        observedErrorRate = errorRate;
    }

    public Long getId() { return id; }
    public Long getChangeId() { return changeId; }
    public int getPercentage() { return percentage; }
    public WaveStatus getStatus() { return status; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getEndedAt() { return endedAt; }
    public Double getObservedErrorRate() { return observedErrorRate; }
}