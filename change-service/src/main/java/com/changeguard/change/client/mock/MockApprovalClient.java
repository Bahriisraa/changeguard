package com.changeguard.change.client.mock;

import com.changeguard.change.client.ApprovalClient;
import com.changeguard.change.client.ApprovalResult;
import com.changeguard.change.error.ChangeFreezeException;
import com.changeguard.change.error.DependencyUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock")
public class MockApprovalClient implements ApprovalClient {

    private final String mode;

    public MockApprovalClient(@Value("${mock.approval.mode:APPROVED}") String mode) {
        this.mode = mode;
    }

    @Override
    public ApprovalResult requestApproval(Long changeId, String targetService, String requestedBy) {
        return switch (mode) {
            case "APPROVED" -> new ApprovalResult(true, "Approuvé (simulation)");
            case "REJECTED" -> new ApprovalResult(false, "Refusé (simulation)");
            case "FREEZE" -> throw new ChangeFreezeException("gel simulé");
            case "DOWN" -> throw new DependencyUnavailableException("approval-service indisponible", null);
            default -> throw new IllegalStateException("Mode d'approbation inconnu : " + mode);
        };
    }
}