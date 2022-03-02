package org.egov.model.report;

import java.math.BigInteger;
import java.util.List;

public class TransactionReport {
	private String ulbName;
	private BigInteger vNoOfTrxn;
	private BigInteger bNoOfTrxn;
	private List<String> ulbNames;
	
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
	
	
}
