package org.egov.model.report;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class TransactionReport {
	private String ulbName;
	private BigInteger vNoOfTrxn;
	private BigInteger bNoOfTrxn;
	private List<String> ulbNames;
	private BigInteger miscNoOfTrxn;
	private Date fromDate;
	private Date toDate;
	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public String getUlbName() {
		return ulbName;
	}
	public void setUlbName(String ulbName) {
		this.ulbName = ulbName;
	}
	public BigInteger getvNoOfTrxn() {
		return vNoOfTrxn;
	}
	public void setvNoOfTrxn(BigInteger vNoOfTrxn) {
		this.vNoOfTrxn = vNoOfTrxn;
	}
	public BigInteger getbNoOfTrxn() {
		return bNoOfTrxn;
	}
	public void setbNoOfTrxn(BigInteger bNoOfTrxn) {
		this.bNoOfTrxn = bNoOfTrxn;
	}
	public List<String> getUlbNames() {
		return ulbNames;
	}
	public void setUlbNames(List<String> ulbNames) {
		this.ulbNames = ulbNames;
	}
	public BigInteger getMiscNoOfTrxn() {
		return miscNoOfTrxn;
	}
	public void setMiscNoOfTrxn(BigInteger miscNoOfTrxn) {
		this.miscNoOfTrxn = miscNoOfTrxn;
	}
	
	
}
