package org.egov.egf.contract.model;

import java.util.ArrayList;
import java.util.List;

public class RefundReceipt {

	
	private String receiptNumber;
	private String isCitizenRefund;
	private String citizenName;
	private String correspondingAddress;
	private String bankName;
	private String bankAccount;
	private String ifscCode;
	private List<RefundLedger> ledgers=new ArrayList<RefundLedger>();
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
	public List<RefundLedger> getLedgers() {
		return ledgers;
	}
	public void setLedgers(List<RefundLedger> ledgers) {
		this.ledgers = ledgers;
	}
}
