package com.changeguard.change.client.mock;

import com.changeguard.change.client.HealthClient;
import com.changeguard.change.client.HealthSnapshot;
import com.changeguard.change.client.HealthState;
import com.changeguard.change.error.DependencyUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock")
public class MockHealthClient implements HealthClient {

    private final String mode;

    public MockHealthClient(@Value("${mock.health.mode:SAIN}") String mode) {
        this.mode = mode;
    }

    @Override
    public HealthSnapshot getServiceHealth(String serviceName) {
        if (mode.equals("DOWN")) {
            throw new DependencyUnavailableException("health-service indisponible", null);
        }
        HealthState state = HealthState.valueOf(mode);
        double errorRate = switch (state) {
            case SAIN -> 1.0;
            case DEGRADE -> 20.0;
            case EN_PANNE -> 45.0;
        };
        return new HealthSnapshot(state, errorRate, 120);
    }
}