package org.egov.asset.model;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "asset_header")
@SequenceGenerator(name = AssetHeader.SEQ_asset_header, sequenceName = AssetHeader.SEQ_asset_header, allocationSize = 1)
public class AssetHeader implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3402739045880074417L;
	public static final String SEQ_asset_header = "SEQ_asset_header";
	
	@Id
	@GeneratedValue(generator = SEQ_asset_header, strategy = GenerationType.SEQUENCE)
	private Long id;	
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="asset_category")
	private AssetCatagory assetCategory;
	@Column(name="date_of_creation")
	private String dateOfCreation;
	@Column(name="description")
	private String description;
	@Column(name="asset_name")
	private String assetName;
	@Column(name="asset_reference")
	private String assetReference;
	@Column(name="depreciation_rate")
	private String depreciationRate;
	@Column(name="is_applicable_for_sale")
	private Boolean applicableForSale;
	
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="mode_of_acquisition")
	private AssetModeOfAcquisition modeOfAcquisition;
	@Column(name="department")
	private String department;
	@Column(name="fund")
	private String fund;
	@Column(name="function")
	private String function;
	@Column(name="scheme")
	private String scheme;
	@Column(name="sub_scheme")
	private String subScheme;
	
	//Common Entries
	@Column(name="created_date")
	public Date createdDate;
	@Column(name="updated_date")
	public Date updatedDate;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDateOfCreation() {
		return dateOfCreation;
	}
	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getAssetReference() {
		return assetReference;
	}
	public void setAssetReference(String assetReference) {
		this.assetReference = assetReference;
	}
	public String getDepreciationRate() {
		return depreciationRate;
	}
	public void setDepreciationRate(String depreciationRate) {
		this.depreciationRate = depreciationRate;
	}
	public Boolean getApplicableForSale() {
		return applicableForSale;
	}
	public void setApplicableForSale(Boolean applicableForSale) {
		this.applicableForSale = applicableForSale;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
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
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getSubScheme() {
		return subScheme;
	}
	public void setSubScheme(String subScheme) {
		this.subScheme = subScheme;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public AssetCatagory getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(AssetCatagory assetCategory) {
		this.assetCategory = assetCategory;
	}
	public AssetModeOfAcquisition getModeOfAcquisition() {
		return modeOfAcquisition;
	}
	public void setModeOfAcquisition(AssetModeOfAcquisition modeOfAcquisition) {
		this.modeOfAcquisition = modeOfAcquisition;
	}
	@Override
	public String toString() {
		return "AssetHeader [id=" + id + ", assetCategory=" + assetCategory + ", dateOfCreation=" + dateOfCreation
				+ ", description=" + description + ", assetName=" + assetName + ", assetReference=" + assetReference
				+ ", depreciationRate=" + depreciationRate + ", applicableForSale=" + applicableForSale
				+ ", modeOfAcquisition=" + modeOfAcquisition + ", department=" + department + ", fund=" + fund
				+ ", function=" + function + ", scheme=" + scheme + ", subScheme=" + subScheme + ", createdDate="
				+ createdDate + ", updatedDate=" + updatedDate + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ "]";
	}	
		
}