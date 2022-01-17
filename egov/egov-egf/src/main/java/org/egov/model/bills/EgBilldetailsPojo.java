package org.egov.model.bills;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class EgBilldetailsPojo {
	
	//private BigDecimal functionid;

    private String  glcodeid;

    private BigDecimal debitamount;

    private BigDecimal creditamount;
    
    private String functionname;
    
    private Set<EgBillPayeedetailsPojo> egBillPaydetailesPojo = new HashSet<EgBillPayeedetailsPojo>();

	/*
	 * public BigDecimal getFunctionid() { return functionid; }
	 * 
	 * public void setFunctionid(BigDecimal functionid) { this.functionid =
	 * functionid; }
	 */
	

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

	public String getFunctionname() {
		return functionname;
	}

	public void setFunctionname(String functionname) {
		this.functionname = functionname;
	}

	public Set<EgBillPayeedetailsPojo> getEgBillPaydetailesPojo() {
		return egBillPaydetailesPojo;
	}

	public void setEgBillPaydetailesPojo(Set<EgBillPayeedetailsPojo> egBillPaydetailesPojo) {
		this.egBillPaydetailesPojo = egBillPaydetailesPojo;
	}

	public String getGlcodeid() {
		return glcodeid;
	}

	public void setGlcodeid(String glcodeid) {
		this.glcodeid = glcodeid;
	}

	
	
	
    
   
}
