package org.egov.asset.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import static org.egov.asset.model.AssetLocation.SEQ_asset_location;

/**
 * @author Arnab Saha
 */
@Entity
@Table(name = "asset_location")
@SequenceGenerator(name = SEQ_asset_location, sequenceName = SEQ_asset_location, allocationSize = 1)
public class AssetLocation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3782742913262004524L;
	public static final String SEQ_asset_location = "SEQ_asset_location";
	
	@Id
    @GeneratedValue(generator = SEQ_asset_location, strategy = GenerationType.SEQUENCE)
	private Long id;
	@Column(name="location")
	private String location;
	@Column(name="block")
	private String block;
	@Column(name="election_ward")
	private String ward;
	@Column(name="zone")
	private String zone;
	@Column(name="revenue_ward")
	private String revenueWard;
	@Column(name="street")private String street;
	@Column(name="door")
	private String door;
	@Column(name="pin")
	private Long pin;
	
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
	
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block = block;
	}
	
	public String getWard() {
		return ward;
	}
	public void setWard(String ward) {
		this.ward = ward;
	}
	
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	public String getRevenueWard() {
		return revenueWard;
	}
	public void setRevenueWard(String revenueWard) {
		this.revenueWard = revenueWard;
	}
	
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getDoor() {
		return door;
	}
	public void setDoor(String door) {
		this.door = door;
	}
	public Long getPin() {
		return pin;
	}
	public void setPin(Long pin) {
		this.pin = pin;
	}
	
	
	@Override
	public String toString() {
		return "AssetLocation [id=" + id + ", location=" + location + ", block=" + block + ", ward=" + ward + ", zone="
				+ zone + ", revenueWard=" + revenueWard + ", street=" + street + ", door=" + door + ", pin=" + pin
				+ "]";
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
}