package com.changeguard.approval;

import com.changeguard.approval.ws.ApprovalDecision;
import com.changeguard.approval.ws.ApprovalStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class ApprovalService {

    private final ApprovalRecordRepository repository;
    private final FreezePolicy freezePolicy;
    private final Clock clock;

    public ApprovalService(ApprovalRecordRepository repository, FreezePolicy freezePolicy, Clock clock) {
        this.repository = repository;
        this.freezePolicy = freezePolicy;
        this.clock = clock;
    }

    // noRollbackFor : sinon l'enregistrement du refus "gel" serait annule par l'exception
    @Transactional(noRollbackFor = ChangeFreezeException.class)
    public ApprovalResult requestApproval(String changeId, String targetService, String requestedBy) {
        Instant now = clock.instant();

        if (freezePolicy.isFrozen(now)) {
            save(changeId, targetService, requestedBy, ApprovalDecision.REJECTED, "Periode de gel", now);
            throw new ChangeFreezeException("Periode de gel : aucun changement accepte");
        }

        if (repository.existsByChangeIdAndDecision(changeId, ApprovalDecision.APPROVED)) {
            save(changeId, targetService, requestedBy, ApprovalDecision.REJECTED, "Changement deja approuve", now);
            return new ApprovalResult(ApprovalDecision.REJECTED, "Changement deja approuve");
        }

        save(changeId, targetService, requestedBy, ApprovalDecision.APPROVED, "Approuve", now);
        return new ApprovalResult(ApprovalDecision.APPROVED, "Approuve");
    }

    // APPROVED l'emporte : un refus "deja approuve" ne doit pas masquer l'approbation d'origine
    @Transactional(readOnly = true)
    public ApprovalStatus getChangeStatus(String changeId) {
        if (repository.existsByChangeIdAndDecision(changeId, ApprovalDecision.APPROVED)) {
            return ApprovalStatus.APPROVED;
        }
        if (repository.existsByChangeIdAndDecision(changeId, ApprovalDecision.REJECTED)) {
            return ApprovalStatus.REJECTED;
        }
        return ApprovalStatus.NOT_REQUESTED;
    }

    private void save(String changeId, String targetService, String requestedBy,
                      ApprovalDecision decision, String reason, Instant now) {
        repository.save(new ApprovalRecord(changeId, targetService, requestedBy, decision, reason, now));
    }
}