package org.egov.egf.model;

import java.math.BigDecimal;

public class OpeningAndClosingBalanceEntry {
	
	private String glcode;
	private String accName;
	private BigDecimal openingCreditBalance;
	private BigDecimal openingDebitBalance;
	private BigDecimal totalCashOpeningBalance;
	private BigDecimal totalBankOpeningBalance;
	
	public OpeningAndClosingBalanceEntry(String glcode, String accName, BigDecimal openingCreditBalance,
			BigDecimal openingDebitBalance, BigDecimal totalCashOpeningBalance, BigDecimal totalBankOpeningBalance) {
		super();
		this.glcode = glcode;
		this.accName = accName;
		this.openingCreditBalance = openingCreditBalance;
		this.openingDebitBalance = openingDebitBalance;
		this.totalCashOpeningBalance = totalCashOpeningBalance;
		this.totalBankOpeningBalance = totalBankOpeningBalance;
	}

	public OpeningAndClosingBalanceEntry() {
		
	}
	
	public BigDecimal getTotalCashOpeningBalance() {
		return totalCashOpeningBalance;
	}

	public void setTotalCashOpeningBalance(BigDecimal totalCashOpeningBalance) {
		this.totalCashOpeningBalance = totalCashOpeningBalance;
	}

	public BigDecimal getTotalBankOpeningBalance() {
		return totalBankOpeningBalance;
	}

	public void setTotalBankOpeningBalance(BigDecimal totalBankOpeningBalance) {
		this.totalBankOpeningBalance = totalBankOpeningBalance;
	}

	public OpeningAndClosingBalanceEntry(String glcode, String accName, BigDecimal openingCreditBalance,
			BigDecimal openingDebitBalance) {
		super();
		this.glcode = glcode;
		this.accName = accName;
		this.openingCreditBalance = openingCreditBalance;
		this.openingDebitBalance = openingDebitBalance;
	}
	
	public String getGlcode() {
		return glcode;
	}
	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public BigDecimal getOpeningCreditBalance() {
		return openingCreditBalance;
	}
	public void setOpeningCreditBalance(BigDecimal openingCreditBalance) {
		this.openingCreditBalance = openingCreditBalance;
	}
	public BigDecimal getOpeningDebitBalance() {
		return openingDebitBalance;
	}
	public void setOpeningDebitBalance(BigDecimal openingDebitBalance) {
		this.openingDebitBalance = openingDebitBalance;
	}
	
	

}
