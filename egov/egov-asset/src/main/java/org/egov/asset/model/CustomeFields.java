package org.egov.asset.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity
@Table(name="custome_fields")
@SequenceGenerator(name = CustomeFields.SEQ_CUSTOM_FIELDS, sequenceName = CustomeFields.SEQ_CUSTOM_FIELDS, initialValue=1,allocationSize = 1)
public class CustomeFields {

	
	public static final String SEQ_CUSTOM_FIELDS = "SEQ_CUSTOM_FIELDS";
	
	@Id
	@GeneratedValue(generator = CustomeFields.SEQ_CUSTOM_FIELDS, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	
	@Column(name = "name")
	private String name;
	@Column(name = "data_type")
	private String dataType;
	@Column(name = "active")
	private boolean active;
	@Column(name = "vlaues")
	private String vlaues;
	@Column(name = "orders")
	private String orders;
	@Column(name = "columns")
	private String columns;
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OneToOne
    @JoinColumn(name = "custom_field_type_id")
	private CustomFieldDataType customFieldDataType;
	/*@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="asset_catagory_id",referencedColumnName = "id")
    private AssetCatagory assetCatagory;*/
	@Column(name = "mandatory")
	private boolean mandatory;
	@Column(name = "create_date")
	private String createDate;
	@Column(name = "update_date")
	private String updateDate;
	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "updated_by")
	private Long updatedBy;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getVlaues() {
		return vlaues;
	}
	public void setVlaues(String vlaues) {
		this.vlaues = vlaues;
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public CustomFieldDataType getCustomFieldDataType() {
		return customFieldDataType;
	}
	public void setCustomFieldDataType(CustomFieldDataType customFieldDataType) {
		this.customFieldDataType = customFieldDataType;
	}
	/*public AssetCatagory getAssetCatagory() {
		return assetCatagory;
	}
	public void setAssetCatagory(AssetCatagory assetCatagory) {
		this.assetCatagory = assetCatagory;
	}*/

	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Long getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
	@Override
	public String toString() {
		return "CustomeFields [id=" + id + ", name=" + name + ", dataType=" + dataType + ", active=" + active
				+ ", vlaues=" + vlaues + ", orders=" + orders + ", columns=" + columns + ", customFieldDataType="
				+ customFieldDataType + ", mandatory=" + mandatory + ", createDate=" + createDate + ", updateDate="
				+ updateDate + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + "]";
	}
	
	
	
	
	
}
