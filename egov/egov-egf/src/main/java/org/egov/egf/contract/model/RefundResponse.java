package org.egov.egf.contract.model;

import org.egov.infra.microservice.models.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundResponse {

	 	@JsonProperty("Status")
	    private String responseStatus;
	 
	 	private String billNumber;
	 	
	    @JsonProperty("ResponseInfo")
	    private ResponseInfo responseInfo;

		public String getResponseStatus() {
			return responseStatus;
		}

		public void setResponseStatus(String responseStatus) {
			this.responseStatus = responseStatus;
		}

		public ResponseInfo getResponseInfo() {
			return responseInfo;
		}

		public void setResponseInfo(ResponseInfo responseInfo) {
			this.responseInfo = responseInfo;
		}

		public String getBillNumber() {
			return billNumber;
		}

		public void setBillNumber(String billNumber) {
			this.billNumber = billNumber;
		}
		
}
