package com.changeguard.change.domain;

import com.changeguard.change.error.InvalidStateException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChangeStateMachineTest {

    @ParameterizedTest
    @CsvSource({
            "PENDING_APPROVAL,APPROVED", "PENDING_APPROVAL,REJECTED", "APPROVED,DEPLOYING",
            "DEPLOYING,COMPLETED", "DEPLOYING,PAUSED", "DEPLOYING,ROLLED_BACK",
            "PAUSED,DEPLOYING", "PAUSED,ROLLED_BACK", "COMPLETED,ROLLED_BACK"})
    void transitionAutorisee(ChangeStatus from, ChangeStatus to) {
        assertDoesNotThrow(() -> ChangeStateMachine.check(from, to));
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING_APPROVAL,DEPLOYING", "APPROVED,COMPLETED", "DEPLOYING,DEPLOYING",
            "PAUSED,COMPLETED", "COMPLETED,DEPLOYING", "REJECTED,DEPLOYING",
            "ROLLED_BACK,DEPLOYING"})
    void transitionInterdite(ChangeStatus from, ChangeStatus to) {
        assertThrows(InvalidStateException.class, () -> ChangeStateMachine.check(from, to));
    }
}
