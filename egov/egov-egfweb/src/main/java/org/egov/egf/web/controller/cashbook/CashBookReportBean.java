package org.egov.egf.web.controller.cashbook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.egf.model.BankBookViewEntry;

public class CashBookReportBean {
	private Date fromDate;
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
	private Date toDate;
	private BigDecimal aCurrentYear = new BigDecimal(0);
	private BigDecimal aPrevYear = new BigDecimal(0);
	private BigDecimal bCurrentYear = new BigDecimal(0);
	private BigDecimal bPrevYear = new BigDecimal(0);
	private BigDecimal cCurrentYear = new BigDecimal(0);
	private BigDecimal cPrevYear = new BigDecimal(0);
	private BigDecimal abcCurrentYear = new BigDecimal(0);
	private BigDecimal abcPrevYear = new BigDecimal(0);
	private BigDecimal atBeginingCurr = new BigDecimal(0);
	private BigDecimal atBeginingPrev = new BigDecimal(0);
	private BigDecimal atEndCurr = new BigDecimal(0);
	private BigDecimal atEndPrev = new BigDecimal(0);
	public BigDecimal getAtBeginingCurr() {
		return atBeginingCurr;
	}
	public void setAtBeginingCurr(BigDecimal atBeginingCurr) {
		this.atBeginingCurr = atBeginingCurr;
	}
	public BigDecimal getAtBeginingPrev() {
		return atBeginingPrev;
	}
	public void setAtBeginingPrev(BigDecimal atBeginingPrev) {
		this.atBeginingPrev = atBeginingPrev;
	}
	public BigDecimal getAtEndCurr() {
		return atEndCurr;
	}
	public void setAtEndCurr(BigDecimal atEndCurr) {
		this.atEndCurr = atEndCurr;
	}
	public BigDecimal getAtEndPrev() {
		return atEndPrev;
	}
	public void setAtEndPrev(BigDecimal atEndPrev) {
		this.atEndPrev = atEndPrev;
	}
	private String titleName= ""; 
	private String header = "";
	private String flag="";
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public BigDecimal getaCurrentYear() {
		return aCurrentYear;
	}
	public void setaCurrentYear(BigDecimal aCurrentYear) {
		this.aCurrentYear = aCurrentYear;
	}
	public BigDecimal getaPrevYear() {
		return aPrevYear;
	}
	public void setaPrevYear(BigDecimal aPrevYear) {
		this.aPrevYear = aPrevYear;
	}
	public BigDecimal getbCurrentYear() {
		return bCurrentYear;
	}
	public void setbCurrentYear(BigDecimal bCurrentYear) {
		this.bCurrentYear = bCurrentYear;
	}
	public BigDecimal getbPrevYear() {
		return bPrevYear;
	}
	public void setbPrevYear(BigDecimal bPrevYear) {
		this.bPrevYear = bPrevYear;
	}
	public BigDecimal getcCurrentYear() {
		return cCurrentYear;
	}
	public void setcCurrentYear(BigDecimal cCurrentYear) {
		this.cCurrentYear = cCurrentYear;
	}
	public BigDecimal getcPrevYear() {
		return cPrevYear;
	}
	public void setcPrevYear(BigDecimal cPrevYear) {
		this.cPrevYear = cPrevYear;
	}
	public BigDecimal getAbcCurrentYear() {
		return abcCurrentYear;
	}
	public void setAbcCurrentYear(BigDecimal abcCurrentYear) {
		this.abcCurrentYear = abcCurrentYear;
	}
	public BigDecimal getAbcPrevYear() {
		return abcPrevYear;
	}
	public void setAbcPrevYear(BigDecimal abcPrevYear) {
		this.abcPrevYear = abcPrevYear;
	}
	private List<CashBookViewEntry> cashBookResultList = new ArrayList<CashBookViewEntry>();
	private List<CashFlowReportDataBean> cashFlowResultList = new ArrayList<CashFlowReportDataBean>();
	private List<CashFlowReportDataBean> finalBalanceSheetL = new ArrayList<CashFlowReportDataBean>();
	
	public List<CashFlowReportDataBean> getFinalBalanceSheetL() {
		return finalBalanceSheetL;
	}
	public void setFinalBalanceSheetL(List<CashFlowReportDataBean> finalBalanceSheetL) {
		this.finalBalanceSheetL = finalBalanceSheetL;
	}
	public List<CashFlowReportDataBean> getCashFlowResultList() {
		return cashFlowResultList;
	}
	public void setCashFlowResultList(List<CashFlowReportDataBean> cashFlowResultList) {
		this.cashFlowResultList = cashFlowResultList;
	}
	
	
	public List<CashBookViewEntry> getCashBookResultList() {
		return cashBookResultList;
	}
	public void setCashBookResultList(List<CashBookViewEntry> cashBookResultList) {
		this.cashBookResultList = cashBookResultList;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
}
