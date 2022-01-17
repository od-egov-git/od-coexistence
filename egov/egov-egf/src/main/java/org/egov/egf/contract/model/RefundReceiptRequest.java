package org.egov.egf.contract.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundReceiptRequest {
	
	@JsonProperty("Receipt")
	RefundReceiptRest refundReceiptRest;

	public RefundReceiptRest getRefundReceiptRest() {
		return refundReceiptRest;
	}

	public void setRefundReceiptRest(RefundReceiptRest refundReceiptRest) {
		this.refundReceiptRest = refundReceiptRest;
	}
	
}
