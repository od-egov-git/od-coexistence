package org.egov.model.deduction;

import java.math.BigDecimal;

public class DeductionReportBean {
	
	private int slNo;
	private String recoveryCode;
	private String voucherNo;
	private String division;
	private String nameOfAgency;
	private String workDone;
	private BigDecimal amount;
	private String gstNoOfAgency;
	private String panNoOfAgency;
	private String billVoucherNo;
	private String pexNo;
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}
	public String getRecoveryCode() {
		return recoveryCode;
	}
	public void setRecoveryCode(String recoveryCode) {
		this.recoveryCode = recoveryCode;
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	
	public String getNameOfAgency() {
		return nameOfAgency;
	}
	public void setNameOfAgency(String nameOfAgency) {
		this.nameOfAgency = nameOfAgency;
	}
	public String getWorkDone() {
		return workDone;
	}
	public void setWorkDone(String workDone) {
		this.workDone = workDone;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getGstNoOfAgency() {
		return gstNoOfAgency;
	}
	public void setGstNoOfAgency(String gstNoOfAgency) {
		this.gstNoOfAgency = gstNoOfAgency;
	}
	public String getPanNoOfAgency() {
		return panNoOfAgency;
	}
	public void setPanNoOfAgency(String panNoOfAgency) {
		this.panNoOfAgency = panNoOfAgency;
	}
	public String getBillVoucherNo() {
		return billVoucherNo;
	}
	public void setBillVoucherNo(String billVoucherNo) {
		this.billVoucherNo = billVoucherNo;
	}
	public String getPexNo() {
		return pexNo;
	}
	public void setPexNo(String pexNo) {
		this.pexNo = pexNo;
	}

}
