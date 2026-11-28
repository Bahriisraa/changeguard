package com.changeguard.approval;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CUSTOM,
        customFaultCode = "{" + WebServiceConfig.NAMESPACE + "}ChangeFreezeFault")
public class ChangeFreezeException extends RuntimeException {

    public ChangeFreezeException(String message) {
        super(message);
    }
}