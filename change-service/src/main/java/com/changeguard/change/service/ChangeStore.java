package com.changeguard.change.service;

import com.changeguard.change.domain.*;
import com.changeguard.change.error.ChangeNotFoundException;
import com.changeguard.change.repository.ChangeRepository;
import com.changeguard.change.repository.WaveRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ChangeStore {

    private final ChangeRepository changes;
    private final WaveRepository waves;

    public ChangeStore(ChangeRepository changes, WaveRepository waves) {
        this.changes = changes;
        this.waves = waves;
    }

    public Change create(String title, String targetService, String requestedBy) {
        return changes.save(new Change(title, targetService, requestedBy));
    }

    @Transactional(readOnly = true)
    public Change get(Long id) {
        return changes.findById(id).orElseThrow(() -> new ChangeNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Change> list() {
        return changes.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public List<Wave> waves(Long changeId) {
        get(changeId);
        return waves.findByChangeIdOrderByPercentageAsc(changeId);
    }

    public Change moveTo(Long id, ChangeStatus target) {
        Change change = get(id);
        ChangeStateMachine.check(change.getStatus(), target);
        change.setStatus(target);
        return changes.save(change);
    }

    public List<Wave> getOrCreateWaves(Long changeId, List<Integer> percentages) {
        List<Wave> existing = waves.findByChangeIdOrderByPercentageAsc(changeId);
        if (!existing.isEmpty()) {
            return existing;
        }
        return waves.saveAll(percentages.stream().map(p -> new Wave(changeId, p)).toList());
    }

    public void startWave(Long waveId) {
        waves.findById(waveId).orElseThrow().start();
    }

    public void finishWave(Long waveId, WaveStatus result, Double errorRate) {
        waves.findById(waveId).orElseThrow().finish(result, errorRate);
    }
}