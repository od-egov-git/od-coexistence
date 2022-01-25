package org.egov.asset.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="custom_field_data_type")
public class CustomFieldDataType {

	@Id
	private Long id;
	@Column(name="data_types")
	private String dataTypes;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDataTypes() {
		return dataTypes;
	}
	public void setDataTypes(String dataTypes) {
		dataTypes = dataTypes;
	}
	
	
}
