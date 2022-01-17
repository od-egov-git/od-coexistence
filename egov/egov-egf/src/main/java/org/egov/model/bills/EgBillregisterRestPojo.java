package org.egov.model.bills;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.User;
import org.egov.infstr.models.EgChecklists;
import org.hibernate.validator.constraints.Length;

public class EgBillregisterRestPojo {
	

   
    private String billnumber;
    private String billtype;
    private Date billdate;
    private BigDecimal billamount;
    private String billstatus;
    private String narration;
    private String billapprovalstatus;
    private Date billpasseddate;
    
    private String refundnarration;
    private BigDecimal passedamount;
    
    private String expendituretype;
    private BigDecimal advanceadjusted;
    private String division;
 
    private String status;
    private User approver; 
    private Date approvedOn;
    private String approvalComent;
    
    
    private String departmentName;
    private String fundSource;
    private String functionname;
    private String fundname;
    private String scheme;
    private String subscheme;
    private String partyBillNumber;
    private Date partyBillDate;
    private String pendingWith;
    
    private Set<EgBilldetailsPojo> egBilldetailespojo = new LinkedHashSet<>();

	public String getBillnumber() {
		return billnumber;
	}

	public void setBillnumber(String billnumber) {
		this.billnumber = billnumber;
	}

	public Date getBilldate() {
		return billdate;
	}

	public void setBilldate(Date billdate) {
		this.billdate = billdate;
	}

	public BigDecimal getBillamount() {
		return billamount;
	}

	public void setBillamount(BigDecimal billamount) {
		this.billamount = billamount;
	}

	public String getBillstatus() {
		return billstatus;
	}

	public void setBillstatus(String billstatus) {
		this.billstatus = billstatus;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getRefundnarration() {
		return refundnarration;
	}

	public void setRefundnarration(String refundnarration) {
		this.refundnarration = refundnarration;
	}

	public BigDecimal getPassedamount() {
		return passedamount;
	}

	public void setPassedamount(BigDecimal passedamount) {
		this.passedamount = passedamount;
	}

	public String getBilltype() {
		return billtype;
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public String getExpendituretype() {
		return expendituretype;
	}

	public void setExpendituretype(String expendituretype) {
		this.expendituretype = expendituretype;
	}

	public BigDecimal getAdvanceadjusted() {
		return advanceadjusted;
	}

	public void setAdvanceadjusted(BigDecimal advanceadjusted) {
		this.advanceadjusted = advanceadjusted;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	

	public String getBillapprovalstatus() {
		return billapprovalstatus;
	}

	public void setBillapprovalstatus(String billapprovalstatus) {
		this.billapprovalstatus = billapprovalstatus;
	}

	
	public Date getBillpasseddate() {
		return billpasseddate;
	}

	public void setBillpasseddate(Date billpasseddate) {
		this.billpasseddate = billpasseddate;
	}

	

	
	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getApprovalComent() {
		return approvalComent;
	}

	public void setApprovalComent(String approvalComent) {
		this.approvalComent = approvalComent;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getFundSource() {
		return fundSource;
	}
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFundSource(String fundSource) {
		this.fundSource = fundSource;
	}

	public String getFunctionname() {
		return functionname;
	}

	public void setFunctionname(String functionname) {
		this.functionname = functionname;
	}

	public String getFundname() {
		return fundname;
	}

	public void setFundname(String fundname) {
		this.fundname = fundname;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getSubscheme() {
		return subscheme;
	}

	public void setSubscheme(String subscheme) {
		this.subscheme = subscheme;
	}

	public String getPartyBillNumber() {
		return partyBillNumber;
	}

	public void setPartyBillNumber(String partyBillNumber) {
		this.partyBillNumber = partyBillNumber;
	}

	public Date getPartyBillDate() {
		return partyBillDate;
	}

	public void setPartyBillDate(Date partyBillDate) {
		this.partyBillDate = partyBillDate;
	}

	public Set<EgBilldetailsPojo> getEgBilldetailespojo() {
		return egBilldetailespojo;
	}

	public void setEgBilldetailespojo(Set<EgBilldetailsPojo> egBilldetailespojo) {
		this.egBilldetailespojo = egBilldetailespojo;
	}

	public String getPendingWith() {
		return pendingWith;
	}

	public void setPendingWith(String pendingWith) {
		this.pendingWith = pendingWith;
	}
    
    

}
