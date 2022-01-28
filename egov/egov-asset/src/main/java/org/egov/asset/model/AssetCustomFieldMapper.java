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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Arnab Saha
 */

@Entity
@Table(name = "asset_custom_field_mapper")
@SequenceGenerator(name = AssetCustomFieldMapper.SEQ_asset_custom_field_mapper, sequenceName = AssetCustomFieldMapper.SEQ_asset_custom_field_mapper, allocationSize = 1)
public class AssetCustomFieldMapper implements Serializable{
	//extend AbstractAuditable    
	//Apply auditing
	private static final long serialVersionUID = -7417009746720581613L;
	public static final String SEQ_asset_custom_field_mapper = "SEQ_asset_custom_field_mapper";
	
	@Id
    @GeneratedValue(generator = SEQ_asset_custom_field_mapper, strategy = GenerationType.SEQUENCE)
	private Long id;

	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="asset_category")
	private AssetCatagory assetCatagory;
	
	@OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="custom_fields_ref")
	private CustomeFields customeFields;
	
	@Column(name = "field_name")
	private String name;
	
	@Column(name = "field_value")
	private String val;
	
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
	public AssetCatagory getAssetCatagory() {
		return assetCatagory;
	}
	public void setAssetCatagory(AssetCatagory assetCatagory) {
		this.assetCatagory = assetCatagory;
	}
	public CustomeFields getCustomeFields() {
		return customeFields;
	}
	public void setCustomeFields(CustomeFields customeFields) {
		this.customeFields = customeFields;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
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
	
	@Override
	public String toString() {
		return "AssetCustomFieldMapper [id=" + id + ", assetCatagory=" + assetCatagory + ", customeFields="
				+ customeFields + ", name=" + name + ", val=" + val + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + "]";
	}
}
