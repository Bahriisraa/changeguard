package com.changeguard.change.api.dto;

import com.changeguard.change.domain.ChangeStatus;
import java.util.List;

public record DeploymentResponse(Long id, ChangeStatus status, List<WaveResponse> waves) {
}