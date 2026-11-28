package com.changeguard.approval;

import com.changeguard.approval.ws.ApprovalDecision;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "approval_record")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String changeId;

    @Column(nullable = false)
    private String targetService;

    @Column(nullable = false)
    private String requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalDecision decision;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private Instant decidedAt;

    protected ApprovalRecord() {
        // exigé par JPA
    }

    public ApprovalRecord(String changeId, String targetService, String requestedBy,
                          ApprovalDecision decision, String reason, Instant decidedAt) {
        this.changeId = changeId;
        this.targetService = targetService;
        this.requestedBy = requestedBy;
        this.decision = decision;
        this.reason = reason;
        this.decidedAt = decidedAt;
    }

    public Long getId() { return id; }
    public String getChangeId() { return changeId; }
    public String getTargetService() { return targetService; }
    public String getRequestedBy() { return requestedBy; }
    public ApprovalDecision getDecision() { return decision; }
    public String getReason() { return reason; }
    public Instant getDecidedAt() { return decidedAt; }
}