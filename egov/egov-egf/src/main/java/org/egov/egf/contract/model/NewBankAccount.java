package org.egov.egf.contract.model;

public class NewBankAccount extends BankAccount {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String bankName;
	private String bankBranchName;	
	
	

	public NewBankAccount() {
		super();
	}

	public NewBankAccount(String code, String account, String bankName, String bankBranchName) {
		super(code, account);
		this.bankName = bankName;
		this.bankBranchName = bankBranchName;
	}
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankBranchName() {
		return bankBranchName;
	}
	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}
	
}
