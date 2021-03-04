package org.egov.egf.model;

import java.math.BigDecimal;

public class VoucherDetailLedger {
	
	
	private Long id;
	private String glCode;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private Long voucherId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGlCode() {
		return glCode;
	}
	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}
	public BigDecimal getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}
	public Long getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}
	
	

}
