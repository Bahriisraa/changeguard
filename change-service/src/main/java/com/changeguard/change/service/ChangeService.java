package com.changeguard.change.service;

import com.changeguard.change.api.dto.ChangeResponse;
import com.changeguard.change.api.dto.CreateChangeRequest;
import com.changeguard.change.api.dto.WaveResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChangeService {

    private final ChangeStore store;

    public ChangeService(ChangeStore store) {
        this.store = store;
    }

    public ChangeResponse create(CreateChangeRequest request) {
        return ChangeResponse.from(store.create(
                request.title(), request.targetService(), request.requestedBy()));
    }

    public List<ChangeResponse> list() {
        return store.list().stream().map(ChangeResponse::from).toList();
    }

    public ChangeResponse get(Long id) {
        return ChangeResponse.from(store.get(id));
    }

    public List<WaveResponse> waves(Long id) {
        return store.waves(id).stream().map(WaveResponse::from).toList();
    }
}