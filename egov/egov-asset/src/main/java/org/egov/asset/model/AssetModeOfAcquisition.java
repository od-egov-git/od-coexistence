package org.egov.asset.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;
/**
 * @author Arnab Saha
 */
@Entity
@Table(name = "asset_mode_acquisition")
public class AssetModeOfAcquisition implements Serializable{
	
	private static final long serialVersionUID = 1696449155367029264L;
	
	private Long id;
	private String code = "";
	private String description = "";
	
	@Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="code", nullable=false, length=13)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="description", nullable=false, length=13)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "AssetModeOfAcquisition [id=" + id + ", description=" + description + ", code=" + code + "]";
	}

	
}
