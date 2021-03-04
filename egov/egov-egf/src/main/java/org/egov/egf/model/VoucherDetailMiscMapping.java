package org.egov.egf.model;

import java.math.BigDecimal;

public class VoucherDetailMiscMapping {

	
	
	private Long id;
	private Long voucherId;
	private Long bpvId;
	private BigDecimal amountPaid;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}
	public Long getBpvId() {
		return bpvId;
	}
	public void setBpvId(Long bpvId) {
		this.bpvId = bpvId;
	}
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	
}
