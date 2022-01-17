package org.egov.egf.contract.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundReceiptRest {
	
	String receiptNumber;
	String isCitizenRefund;
	String citizenName;
	String correspondingAddress;
	String bankName;
	String bankAccount;
	String ifscCode;
	@JsonProperty("ledgers")
	List<RefundLedgerPojo> refundledgerPojo = new ArrayList<RefundLedgerPojo>();

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getIsCitizenRefund() {
		return isCitizenRefund;
	}

	public void setIsCitizenRefund(String isCitizenRefund) {
		this.isCitizenRefund = isCitizenRefund;
	}

	public String getCitizenName() {
		return citizenName;
	}

	public void setCitizenName(String citizenName) {
		this.citizenName = citizenName;
	}

	public String getCorrespondingAddress() {
		return correspondingAddress;
	}

	public void setCorrespondingAddress(String correspondingAddress) {
		this.correspondingAddress = correspondingAddress;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public List<RefundLedgerPojo> getRefundledgerPojo() {
		return refundledgerPojo;
	}

	public void setRefundledgerPojo(List<RefundLedgerPojo> refundledgerPojo) {
		this.refundledgerPojo = refundledgerPojo;
	}
	
	
}
