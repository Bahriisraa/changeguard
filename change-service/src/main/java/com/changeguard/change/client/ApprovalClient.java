package com.changeguard.change.client;

public interface ApprovalClient {
    ApprovalResult requestApproval(Long changeId, String targetService, String requestedBy);
}
