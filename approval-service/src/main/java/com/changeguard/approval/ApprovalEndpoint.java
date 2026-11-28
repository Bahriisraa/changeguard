package com.changeguard.approval;

import com.changeguard.approval.ws.RequestApprovalRequest;
import com.changeguard.approval.ws.RequestApprovalResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ApprovalEndpoint {

    private final ApprovalService service;

    public ApprovalEndpoint(ApprovalService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE, localPart = "RequestApprovalRequest")
    @ResponsePayload
    public RequestApprovalResponse requestApproval(@RequestPayload RequestApprovalRequest request) {
        ApprovalResult result = service.requestApproval(
                request.getChangeId(), request.getTargetService(), request.getRequestedBy());
        RequestApprovalResponse response = new RequestApprovalResponse();
        response.setDecision(result.decision());
        response.setReason(result.reason());
        return response;
    }
}