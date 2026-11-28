package com.changeguard.change.api;

import com.changeguard.change.api.dto.ChangeResponse;
import com.changeguard.change.api.dto.CreateChangeRequest;
import com.changeguard.change.api.dto.DeploymentResponse;
import com.changeguard.change.api.dto.WaveResponse;
import com.changeguard.change.service.ChangeService;
import com.changeguard.change.service.DeploymentService;
import com.changeguard.change.service.RollbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/changes")
public class ChangeController {

    private final ChangeService changes;
    private final DeploymentService deployments;
    private final RollbackService rollbacks;

    public ChangeController(ChangeService changes, DeploymentService deployments,
                            RollbackService rollbacks) {
        this.changes = changes;
        this.deployments = deployments;
        this.rollbacks = rollbacks;
    }

    @PostMapping
    public ResponseEntity<ChangeResponse> create(@Valid @RequestBody CreateChangeRequest request) {
        ChangeResponse created = changes.create(request);
        return ResponseEntity.created(URI.create("/api/v1/changes/" + created.id())).body(created);
    }

    @GetMapping
    public List<ChangeResponse> list() {
        return changes.list();
    }

    @GetMapping("/{id}")
    public ChangeResponse get(@PathVariable("id") Long id) {
        return changes.get(id);
    }

    @GetMapping("/{id}/waves")
    public List<WaveResponse> waves(@PathVariable("id") Long id) {
        return changes.waves(id);
    }

    @PostMapping("/{id}/deployments")
    public DeploymentResponse deploy(@PathVariable("id") Long id) {
        return deployments.deploy(id);
    }

    @PostMapping("/{id}/rollbacks")
    public ChangeResponse rollback(@PathVariable("id") Long id) {
        return rollbacks.rollback(id);
    }
}