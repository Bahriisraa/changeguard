package com.changeguard.change.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "changes")
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private String targetService;

    @Column(nullable = false)
    private String requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeStatus status = ChangeStatus.PENDING_APPROVAL;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Version
    private Long version;

    protected Change() { }

    public Change(String title, String targetService, String requestedBy) {
        this.title = title;
        this.targetService = targetService;
        this.requestedBy = requestedBy;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getTargetService() { return targetService; }
    public String getRequestedBy() { return requestedBy; }
    public ChangeStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStatus(ChangeStatus status) { this.status = status; }
}