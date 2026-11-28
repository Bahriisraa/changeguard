package com.changeguard.change.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChangeRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 255) String targetService,
        @NotBlank @Size(max = 255) String requestedBy) {
}