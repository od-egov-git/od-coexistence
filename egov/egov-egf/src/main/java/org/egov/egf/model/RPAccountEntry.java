package org.egov.egf.model;

import java.math.BigDecimal;

public class RPAccountEntry {
	
	private String glcode1;
	private String accName1;
	private BigDecimal currentAmt1;
	private BigDecimal previousAmt1;
	private String glcode2;
	private String accName2;
	private BigDecimal currentAmt2;
	private BigDecimal previousAmt2;
	private Character type;
	
	public RPAccountEntry(String glcode1, String accName1, BigDecimal currentAmt1, BigDecimal previousAmt1,
			String glcode2, String accName2, BigDecimal currentAmt2, BigDecimal previousAmt2, Character type) {
		super();
		this.glcode1 = glcode1;
		this.accName1 = accName1;
		this.currentAmt1 = currentAmt1;
		this.previousAmt1 = previousAmt1;
		this.glcode2 = glcode2;
		this.accName2 = accName2;
		this.currentAmt2 = currentAmt2;
		this.previousAmt2 = previousAmt2;
		this.type = type;
	}
	public String getGlcode1() {
		return glcode1;
	}
	public void setGlcode1(String glcode1) {
		this.glcode1 = glcode1;
	}
	public String getAccName1() {
		return accName1;
	}
	public void setAccName1(String accName1) {
		this.accName1 = accName1;
	}
	public BigDecimal getCurrentAmt1() {
		return currentAmt1;
	}
	public void setCurrentAmt1(BigDecimal currentAmt1) {
		this.currentAmt1 = currentAmt1;
	}
	public BigDecimal getPreviousAmt1() {
		return previousAmt1;
	}
	public void setPreviousAmt1(BigDecimal previousAmt1) {
		this.previousAmt1 = previousAmt1;
	}
	public String getGlcode2() {
		return glcode2;
	}
	public void setGlcode2(String glcode2) {
		this.glcode2 = glcode2;
	}
	public String getAccName2() {
		return accName2;
	}
	public void setAccName2(String accName2) {
		this.accName2 = accName2;
	}
	public BigDecimal getCurrentAmt2() {
		return currentAmt2;
	}
	public void setCurrentAmt2(BigDecimal currentAmt2) {
		this.currentAmt2 = currentAmt2;
	}
	public BigDecimal getPreviousAmt2() {
		return previousAmt2;
	}
	public void setPreviousAmt2(BigDecimal previousAmt2) {
		this.previousAmt2 = previousAmt2;
	}
	public Character getType() {
		return type;
	}
	public void setType(Character type) {
		this.type = type;
	}
}
