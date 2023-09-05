package org.egov.egf.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BSStatementEntry {
	
	private String glCode;
    private String accountName;
	private BigDecimal debitamount;
    private BigDecimal creditamount;
	private BigDecimal prevDebitamount;
    private BigDecimal prevCreditamount;
    private boolean displayBold = false;
    private Character type;
    
    public BSStatementEntry() {
    }

	public BSStatementEntry(String glCode, String accountName, BigDecimal debitamount, BigDecimal creditamount,
			BigDecimal previousYearTotal, BigDecimal currentYearTotal, Map<String, BigDecimal> fundWiseAmount,
			boolean displayBold) {
		super();
		this.glCode = glCode;
		this.accountName = accountName;
		this.debitamount = debitamount;
		this.creditamount = creditamount;
		this.displayBold = displayBold;
	}
	
	


	public BSStatementEntry(String glCode, String accountName,BigDecimal debitamount, BigDecimal creditamount, BigDecimal prevDebitamount,
			BigDecimal prevCreditamount, boolean displayBold, Character type) {
		super();
		this.glCode = glCode;
		this.accountName = accountName;
		this.debitamount = debitamount;
		this.creditamount = creditamount;
		this.prevDebitamount = prevDebitamount;
		this.prevCreditamount = prevCreditamount;
		this.displayBold = displayBold;
		this.type = type;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public BigDecimal getDebitamount() {
		return debitamount;
	}

	public void setDebitamount(BigDecimal debitamount) {
		this.debitamount = debitamount;
	}

	public BigDecimal getCreditamount() {
		return creditamount;
	}

	public void setCreditamount(BigDecimal creditamount) {
		this.creditamount = creditamount;
	}

	public boolean isDisplayBold() {
		return displayBold;
	}

	public void setDisplayBold(boolean displayBold) {
		this.displayBold = displayBold;
	}

	public BigDecimal getPrevDebitamount() {
		return prevDebitamount;
	}

	public void setPrevDebitamount(BigDecimal prevDebitamount) {
		this.prevDebitamount = prevDebitamount;
	}

	public BigDecimal getPrevCreditamount() {
		return prevCreditamount;
	}

	public void setPrevCreditamount(BigDecimal prevCreditamount) {
		this.prevCreditamount = prevCreditamount;
	}

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}
    

}
