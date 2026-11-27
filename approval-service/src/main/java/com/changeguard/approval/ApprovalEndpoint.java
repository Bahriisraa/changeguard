package com.changeguard.approval;

import com.changeguard.approval.ws.ApprovalDecision;
import com.changeguard.approval.ws.RequestApprovalRequest;
import com.changeguard.approval.ws.RequestApprovalResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ApprovalEndpoint {

    // MOCK (semaine 1) : répond toujours APPROVED
    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE, localPart = "RequestApprovalRequest")
    @ResponsePayload
    public RequestApprovalResponse requestApproval(@RequestPayload RequestApprovalRequest request) {
        RequestApprovalResponse response = new RequestApprovalResponse();
        response.setDecision(ApprovalDecision.APPROVED);
        response.setReason("Mock");
        return response;
    }
}