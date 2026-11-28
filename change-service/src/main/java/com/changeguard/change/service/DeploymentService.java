package com.changeguard.change.service;

import com.changeguard.change.api.dto.DeploymentResponse;
import com.changeguard.change.api.dto.WaveResponse;
import com.changeguard.change.client.ApprovalClient;
import com.changeguard.change.client.ApprovalResult;
import com.changeguard.change.client.HealthClient;
import com.changeguard.change.client.HealthSnapshot;
import com.changeguard.change.domain.Change;
import com.changeguard.change.domain.ChangeStatus;
import com.changeguard.change.domain.Wave;
import com.changeguard.change.domain.WaveStatus;
import com.changeguard.change.error.ChangeFreezeException;
import com.changeguard.change.error.ChangeRejectedException;
import com.changeguard.change.error.InvalidStateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

/** Volontairement sans @Transactional : chaque état doit être visible tout de suite. */
@Service
public class DeploymentService {

    private static final List<Integer> PERCENTAGES = List.of(1, 10, 100);

    private final ChangeStore store;
    private final ApprovalClient approval;
    private final HealthClient health;
    private final long observationSeconds;

    public DeploymentService(ChangeStore store, ApprovalClient approval, HealthClient health,
                             @Value("${changeguard.wave-observation-seconds}") long observationSeconds) {
        this.store = store;
        this.approval = approval;
        this.health = health;
        this.observationSeconds = observationSeconds;
    }

    public DeploymentResponse deploy(Long id) {
        Change change = store.get(id);
        switch (change.getStatus()) {
            case PENDING_APPROVAL -> requestApproval(change);
            case APPROVED, PAUSED -> { }
            default -> throw new InvalidStateException(
                    "Déploiement impossible depuis l'état " + change.getStatus());
        }
        store.moveTo(id, ChangeStatus.DEPLOYING);

        for (Wave wave : store.getOrCreateWaves(id, PERCENTAGES)) {
            if (wave.getStatus() == WaveStatus.SUCCEEDED) {
                continue;
            }
            HealthSnapshot snapshot = observe(change, wave);
            double errorRate = snapshot.errorRatePercent();
            switch (snapshot.state()) {
                case SAIN -> store.finishWave(wave.getId(), WaveStatus.SUCCEEDED, errorRate);
                case DEGRADE -> {
                    store.finishWave(wave.getId(), WaveStatus.FAILED, errorRate);
                    store.moveTo(id, ChangeStatus.PAUSED);
                    return response(id);
                }
                case EN_PANNE -> {
                    store.finishWave(wave.getId(), WaveStatus.FAILED, errorRate);
                    store.moveTo(id, ChangeStatus.ROLLED_BACK);
                    return response(id);
                }
            }
        }
        store.moveTo(id, ChangeStatus.COMPLETED);
        return response(id);
    }

    private void requestApproval(Change change) {
        ApprovalResult result;
        try {
            result = approval.requestApproval(
                    change.getId(), change.getTargetService(), change.getRequestedBy());
        } catch (ChangeFreezeException e) {
            store.moveTo(change.getId(), ChangeStatus.REJECTED);
            throw new ChangeRejectedException("Période de gel : " + e.getMessage());
        }
        if (!result.approved()) {
            store.moveTo(change.getId(), ChangeStatus.REJECTED);
            throw new ChangeRejectedException(result.reason());
        }
        store.moveTo(change.getId(), ChangeStatus.APPROVED);
    }

    private HealthSnapshot observe(Change change, Wave wave) {
        store.startWave(wave.getId());
        try {
            sleep();
            return health.getServiceHealth(change.getTargetService());
        } catch (RuntimeException e) {
            store.finishWave(wave.getId(), WaveStatus.FAILED, null);
            store.moveTo(change.getId(), ChangeStatus.PAUSED);
            throw e;
        }
    }

    private void sleep() {
        try {
            Thread.sleep(observationSeconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Observation interrompue", e);
        }
    }

    private DeploymentResponse response(Long id) {
        List<WaveResponse> waves = store.waves(id).stream().map(WaveResponse::from).toList();
        return new DeploymentResponse(id, store.get(id).getStatus(), waves);
    }
}