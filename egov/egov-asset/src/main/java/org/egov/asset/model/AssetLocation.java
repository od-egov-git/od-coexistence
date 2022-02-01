package org.egov.asset.model;

import static org.egov.asset.model.AssetLocation.SEQ_asset_location;

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
	
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="location")
	private AssetLocality location;
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="block")
	private AssetLocationBlock block;
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="election_ward")
	private AssetLocationElectionWard ward;
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="zone")
	private AssetLocationZone zone;
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="revenue_ward")
	private AssetLocationRevenueWard revenueWard;
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)	
	@JoinColumn(name="street")
	private AssetLocationStreet street;
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
	public AssetLocality getLocation() {
		return location;
	}
	public void setLocation(AssetLocality location) {
		this.location = location;
	}
	public AssetLocationBlock getBlock() {
		return block;
	}
	public void setBlock(AssetLocationBlock block) {
		this.block = block;
	}
	public AssetLocationElectionWard getWard() {
		return ward;
	}
	public void setWard(AssetLocationElectionWard ward) {
		this.ward = ward;
	}
	public AssetLocationZone getZone() {
		return zone;
	}
	public void setZone(AssetLocationZone zone) {
		this.zone = zone;
	}
	public AssetLocationRevenueWard getRevenueWard() {
		return revenueWard;
	}
	public void setRevenueWard(AssetLocationRevenueWard revenueWard) {
		this.revenueWard = revenueWard;
	}
	public AssetLocationStreet getStreet() {
		return street;
	}
	public void setStreet(AssetLocationStreet street) {
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
		return "AssetLocation [id=" + id + ", location=" + location + ", block=" + block + ", ward=" + ward + ", zone="
				+ zone + ", revenueWard=" + revenueWard + ", street=" + street + ", door=" + door + ", pin=" + pin
				+ ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + ", createdBy=" + createdBy
				+ ", updatedBy=" + updatedBy + "]";
	}
}