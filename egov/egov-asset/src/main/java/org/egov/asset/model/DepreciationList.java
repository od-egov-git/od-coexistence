package org.egov.asset.model;

import java.math.BigDecimal;

public class DepreciationList {
	private int slNo;
	private int id;
	private String assetCategoryName;
	private String department;
	private String assetCode;
	private String assetName;
	private String location;
	private BigDecimal currentGrossValue;
	private Double depreciationRate;
	private boolean checked;
	private String currentDepreciation;
	private String afterDepreciation;
	private String voucherNumber;
	private String successFailure;
	private String reasonForFailure;
	private String categoryType;
	
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	public String getCurrentDepreciation() {
		return currentDepreciation;
	}
	public void setCurrentDepreciation(String currentDepreciation) {
		this.currentDepreciation = currentDepreciation;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}
	public String getAssetCategoryName() {
		return assetCategoryName;
	}
	public void setAssetCategoryName(String assetCategoryName) {
		this.assetCategoryName = assetCategoryName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
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
	
	public BigDecimal getCurrentGrossValue() {
		return currentGrossValue;
	}
	public void setCurrentGrossValue(BigDecimal currentGrossValue) {
		this.currentGrossValue = currentGrossValue;
	}
	public Double getDepreciationRate() {
		return depreciationRate;
	}
	public void setDepreciationRate(Double depreciationRate) {
		this.depreciationRate = depreciationRate;
	}
	
	
}
