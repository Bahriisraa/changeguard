package com.changeguard.change.domain;

import com.changeguard.change.error.InvalidStateException;
import java.util.Map;
import java.util.Set;
import static com.changeguard.change.domain.ChangeStatus.*;

public final class ChangeStateMachine {

    private static final Map<ChangeStatus, Set<ChangeStatus>> ALLOWED = Map.of(
            PENDING_APPROVAL, Set.of(APPROVED, REJECTED),
            APPROVED, Set.of(DEPLOYING),
            DEPLOYING, Set.of(COMPLETED, PAUSED, ROLLED_BACK),
            PAUSED, Set.of(DEPLOYING, ROLLED_BACK),
            COMPLETED, Set.of(ROLLED_BACK),
            REJECTED, Set.of(),
            ROLLED_BACK, Set.of());

    private ChangeStateMachine() { }

    public static void check(ChangeStatus from, ChangeStatus to) {
        if (!ALLOWED.get(from).contains(to)) {
            throw new InvalidStateException("Transition interdite : " + from + " vers " + to);
        }
    }
}