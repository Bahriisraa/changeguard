package com.changeguard.change.service;

import com.changeguard.change.api.dto.ChangeResponse;
import com.changeguard.change.domain.ChangeStatus;
import com.changeguard.change.error.InvalidStateException;
import org.springframework.stereotype.Service;

@Service
public class RollbackService {

    private final ChangeStore store;

    public RollbackService(ChangeStore store) {
        this.store = store;
    }

    public ChangeResponse rollback(Long id) {
        ChangeStatus status = store.get(id).getStatus();
        if (status != ChangeStatus.PAUSED && status != ChangeStatus.COMPLETED) {
            throw new InvalidStateException("Annulation impossible depuis l'état " + status);
        }
        return ChangeResponse.from(store.moveTo(id, ChangeStatus.ROLLED_BACK));
    }
}