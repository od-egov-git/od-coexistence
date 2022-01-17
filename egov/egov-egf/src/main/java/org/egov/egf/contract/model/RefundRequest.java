package org.egov.egf.contract.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.infra.microservice.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundRequest {
	
	@NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("Receipt")
    private RefundReceipt receipt;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public RefundReceipt getReceipt() {
		return receipt;
	}

	public void setReceipt(RefundReceipt receipt) {
		this.receipt = receipt;
	}

}
