package com.changeguard.change;

import com.changeguard.change.client.ApprovalClient;
import com.changeguard.change.client.ApprovalResult;
import com.changeguard.change.client.HealthClient;
import com.changeguard.change.client.HealthSnapshot;
import com.changeguard.change.client.HealthState;
import com.changeguard.change.error.ChangeFreezeException;
import com.changeguard.change.error.DependencyUnavailableException;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeploymentFlowTest {

    private static final String DEPLOY = "/api/v1/changes/{id}/deployments";
    private static final String ROLLBACK = "/api/v1/changes/{id}/rollbacks";

    @Autowired MockMvc mvc;
    @MockBean ApprovalClient approval;
    @MockBean HealthClient health;

    @Test
    void scenarioSain() throws Exception {
        approve();
        health(HealthState.SAIN);
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void santeDegradee() throws Exception {
        approve();
        health(HealthState.DEGRADE);
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }

    @Test
    void serviceEnPanne() throws Exception {
        approve();
        health(HealthState.EN_PANNE);
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ROLLED_BACK"));
    }

    @Test
    void repriseApresPause() throws Exception {
        approve();
        health(HealthState.DEGRADE);
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(jsonPath("$.status").value("PAUSED"));
        health(HealthState.SAIN);
        mvc.perform(post(DEPLOY, id)).andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.waves.length()").value(3));
    }

    @Test
    void refusDonne409EtRejected() throws Exception {
        when(approval.requestApproval(any(), any(), any()))
                .thenReturn(new ApprovalResult(false, "refus"));
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isConflict());
        expectStatus(id, "REJECTED");
    }

    @Test
    void gelDonne409EtRejected() throws Exception {
        when(approval.requestApproval(any(), any(), any()))
                .thenThrow(new ChangeFreezeException("gel"));
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isConflict());
        expectStatus(id, "REJECTED");
    }

    @Test
    void approvalIndisponibleDonne503() throws Exception {
        when(approval.requestApproval(any(), any(), any()))
                .thenThrow(new DependencyUnavailableException("panne", null));
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isServiceUnavailable());
        expectStatus(id, "PENDING_APPROVAL");
    }

    @Test
    void healthIndisponibleDonne503EtPaused() throws Exception {
        approve();
        when(health.getServiceHealth(any()))
                .thenThrow(new DependencyUnavailableException("panne", null));
        long id = createChange();
        mvc.perform(post(DEPLOY, id)).andExpect(status().isServiceUnavailable());
        expectStatus(id, "PAUSED");
    }

    @Test
    void rollbackManuel() throws Exception {
        approve();
        health(HealthState.SAIN);
        long id = createChange();
        mvc.perform(post(ROLLBACK, id)).andExpect(status().isConflict());
        mvc.perform(post(DEPLOY, id)).andExpect(status().isOk());
        mvc.perform(post(ROLLBACK, id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ROLLED_BACK"));
    }

    @Test
    void titreVideDonne400() throws Exception {
        mvc.perform(post("/api/v1/changes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"targetService\":\"x\",\"requestedBy\":\"y\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void idInconnuDonne404() throws Exception {
        mvc.perform(get("/api/v1/changes/{id}", 999999)).andExpect(status().isNotFound());
    }

    private void approve() {
        when(approval.requestApproval(any(), any(), any()))
                .thenReturn(new ApprovalResult(true, "ok"));
    }

    private void health(HealthState state) {
        when(health.getServiceHealth(any())).thenReturn(new HealthSnapshot(state, 1.0, 100));
    }

    private void expectStatus(long id, String expected) throws Exception {
        mvc.perform(get("/api/v1/changes/{id}", id))
                .andExpect(jsonPath("$.status").value(expected));
    }

    private long createChange() throws Exception {
        String json = mvc.perform(post("/api/v1/changes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"t\",\"targetService\":\"target-app\",\"requestedBy\":\"p1\"}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.parse(json).read("$.id", Long.class);
    }
}
