package com.changeguard.change.api.dto;

import com.changeguard.change.domain.Change;
import com.changeguard.change.domain.ChangeStatus;
import java.time.Instant;

public record ChangeResponse(Long id, String title, String targetService, String requestedBy,
                             ChangeStatus status, Instant createdAt) {

    public static ChangeResponse from(Change c) {
        return new ChangeResponse(c.getId(), c.getTitle(), c.getTargetService(),
                c.getRequestedBy(), c.getStatus(), c.getCreatedAt());
    }
}