package com.changeguard.change.client;

public record HealthSnapshot(HealthState state, double errorRatePercent, long latencyMs) {
}