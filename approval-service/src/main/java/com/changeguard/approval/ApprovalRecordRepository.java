package com.changeguard.approval;

import com.changeguard.approval.ws.ApprovalDecision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    boolean existsByChangeIdAndDecision(String changeId, ApprovalDecision decision);
}