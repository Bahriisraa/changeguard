package com.changeguard.approval;

import com.changeguard.approval.ws.ApprovalDecision;

public record ApprovalResult(ApprovalDecision decision, String reason) {
}