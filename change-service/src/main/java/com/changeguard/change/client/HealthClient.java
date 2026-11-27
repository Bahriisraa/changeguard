package com.changeguard.change.client;

public interface HealthClient {
    HealthSnapshot getServiceHealth(String serviceName);
}