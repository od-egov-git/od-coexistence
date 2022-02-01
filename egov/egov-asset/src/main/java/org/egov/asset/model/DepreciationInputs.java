package org.egov.asset.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
@Entity
@Table(name = "ASSET_DEPRECIATION")
@SequenceGenerator(name = DepreciationInputs.SEQ_DEPRECIATION, sequenceName = DepreciationInputs.SEQ_DEPRECIATION, allocationSize = 1)
public class DepreciationInputs implements java.io.Serializable{
	public static final String SEQ_DEPRECIATION = "SEQ_DEPRECIATION";
    private static final long serialVersionUID = -4312140421386028968L;
    @Id
    @GeneratedValue(generator = SEQ_DEPRECIATION, strategy = GenerationType.SEQUENCE)
	private int id;
	private String depreciationDate;
	private String department;
	private String categoryType;
	private String categoryName;
	private String fromDate;
	private String toDate;
	private String assetCode;
	private String assetName;
	private String location;
	private String description;
	private String depreciationRate;
	private String currentDepreciation;
	private String beforeDepreciation;
	private String afterDepreciation;
	private String voucherNumber;
	private String successFailure;
	private String reasonForFailure;
    private Long assetId;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal currentValue;
    private Long accumulatedDepreciationAccount;
    private Long depreciationExpenseAccount;
    private String revaluationreserveaccount;
    private String financialyear;
    private String fund;
    private String function;
    private Date created_date;
	private Long createdby;
	private Date lastmodified_date;
	private Long lastmodifiedby;
	
   
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Date getLastmodified_date() {
		return lastmodified_date;
	}
	public void setLastmodified_date(Date lastmodified_date) {
		this.lastmodified_date = lastmodified_date;
	}
	
	public Long getCreatedby() {
		return createdby;
	}
	public void setCreatedby(Long createdby) {
		this.createdby = createdby;
	}
	public Long getLastmodifiedby() {
		return lastmodifiedby;
	}
	public void setLastmodifiedby(Long lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}
	public String getDepreciationDate() {
		return depreciationDate;
	}
	public void setDepreciationDate(String depreciationDate) {
		this.depreciationDate = depreciationDate;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDepreciationRate() {
		return depreciationRate;
	}
	public void setDepreciationRate(String depreciationRate) {
		this.depreciationRate = depreciationRate;
	}
	public String getCurrentDepreciation() {
		return currentDepreciation;
	}
	public void setCurrentDepreciation(String currentDepreciation) {
		this.currentDepreciation = currentDepreciation;
	}
	public String getBeforeDepreciation() {
		return beforeDepreciation;
	}
	public void setBeforeDepreciation(String beforeDepreciation) {
		this.beforeDepreciation = beforeDepreciation;
	}
	public String getAfterDepreciation() {
		return afterDepreciation;
	}
	public void setAfterDepreciation(String afterDepreciation) {
		this.afterDepreciation = afterDepreciation;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSuccessFailure() {
		return successFailure;
	}
	public void setSuccessFailure(String successFailure) {
		this.successFailure = successFailure;
	}
	public String getReasonForFailure() {
		return reasonForFailure;
	}
	public void setReasonForFailure(String reasonForFailure) {
		this.reasonForFailure = reasonForFailure;
	}
	public Long getAssetId() {
		return assetId;
	}
	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}
	public BigDecimal getAccumulatedDepreciation() {
		return accumulatedDepreciation;
	}
	public void setAccumulatedDepreciation(BigDecimal accumulatedDepreciation) {
		this.accumulatedDepreciation = accumulatedDepreciation;
	}
	public BigDecimal getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}
	public Long getAccumulatedDepreciationAccount() {
		return accumulatedDepreciationAccount;
	}
	public void setAccumulatedDepreciationAccount(Long accumulatedDepreciationAccount) {
		this.accumulatedDepreciationAccount = accumulatedDepreciationAccount;
	}
	public Long getDepreciationExpenseAccount() {
		return depreciationExpenseAccount;
	}
	public void setDepreciationExpenseAccount(Long depreciationExpenseAccount) {
		this.depreciationExpenseAccount = depreciationExpenseAccount;
	}
	public String getRevaluationreserveaccount() {
		return revaluationreserveaccount;
	}
	public void setRevaluationreserveaccount(String revaluationreserveaccount) {
		this.revaluationreserveaccount = revaluationreserveaccount;
	}

	/*
	 * public DepreciationMethod getDepreciationMethod() { return
	 * depreciationMethod; } public void setDepreciationMethod(DepreciationMethod
	 * depreciationMethod) { this.depreciationMethod = depreciationMethod; }
	 */
	public String getFinancialyear() {
		return financialyear;
	}
	public void setFinancialyear(String financialyear) {
		this.financialyear = financialyear;
	}
	public String getFund() {
		return fund;
	}
	public void setFund(String fund) {
		this.fund = fund;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
    
}
