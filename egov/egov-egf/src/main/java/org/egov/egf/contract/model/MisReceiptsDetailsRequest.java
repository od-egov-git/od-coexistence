package org.egov.egf.contract.model;

import javax.validation.constraints.NotNull;

import org.egov.infra.microservice.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MisReceiptsDetailsRequest {
	
	@NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
	
	@JsonProperty("misReceiptsPOJO")
	private MisReceiptsPOJO misReceiptsPOJO;
	
	public MisReceiptsPOJO getMisReceiptsPOJO() {
		return misReceiptsPOJO;
	}
	
	public void setMisReceiptsPOJO(MisReceiptsPOJO misReceiptsPOJO) {
		this.misReceiptsPOJO = misReceiptsPOJO;
	}
	
	public RequestInfo getRequestInfo() {
		return requestInfo;
	}
	
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
}
