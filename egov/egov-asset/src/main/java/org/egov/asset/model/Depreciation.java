package org.egov.asset.model;

import java.util.ArrayList;
import java.util.List;

public class Depreciation {
	private String depreciationDate;
	private String department;
	private String categoryType;
	private String categoryName;
	private String fromDate;
	private String toDate;
	private String assetCode;
	private String assetName;
	private String location;
	private int counter = 0;
	private List<DepreciationList> resultList= new ArrayList<DepreciationList>();
	private String description;
	private String depreciationRate;
	private String currentDepreciation;
	private String afterDepreciation;
	private String voucherNumber;
	private int id;
	private String successFailure;
	private String reasonForFailure;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
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
	public List<DepreciationList> getResultList() {
		return resultList;
	}
	public void setResultList(List<DepreciationList> resultList) {
		this.resultList = resultList;
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
	
}
