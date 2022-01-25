package org.egov.asset.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;

import org.egov.model.bills.DocumentUpload;

/**
 * @author Arnab Saha
 */

@Entity
@Table(name = "asset_master")
@SequenceGenerator(name = AssetMaster.SEQ_asset_master, sequenceName = AssetMaster.SEQ_asset_master, allocationSize = 1)
public class AssetMaster implements Serializable{
	//extend AbstractAuditable    
	//Apply auditing
	private static final long serialVersionUID = -7417009746720581613L;
	public static final String SEQ_asset_master = "SEQ_asset_master";
	
	@Id
    @GeneratedValue(generator = SEQ_asset_master, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="code")
	private String code;
	
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="asset_header")
	private AssetHeader assetHeader;
	
	@OneToOne(fetch=FetchType.LAZY,  cascade = CascadeType.ALL)
	@JoinColumn(name="asset_location")
	private AssetLocation assetLocation;
	
	//from AssetMaster am where am.code=:code or am.assetHeader.assetCategory=:cat
	
	@OneToOne(fetch=FetchType.LAZY,  cascade = CascadeType.ALL)
	@JoinColumn(name="asset_status")
	private AssetStatus assetStatus;
	
	@Transient
	/* private List<AssetDocumentUpload> documentDetail = new ArrayList<>(); */
	private List<DocumentUpload> documentDetail = new ArrayList<>();
	@Column(name="file_no")
	private String fileno;
	
	 //Common Entries
    @Column(name="created_date")
    public Date createdDate;
    @Column(name="updated_date")
    public Date updatedDate;
    @Column(name="created_by")
    private String createdBy;
    @Column(name="updated_by")
    private String updatedBy;
    
    // New Added Fields
    @Column(name="gross_value")
    private Long grossValue;
    @Column(name="market_value")
    private Long marketValue;
    @Column(name="survey_number")
    private String surveyNumber;
    @Column(name="accumulated_depreciation")
    private String accumulatedDepreciation;
    @Column(name="purchase_value")
    private Long purchaseValue;
    @Column(name="purchase_date")
    private Date purchaseDate;
    @Column(name="construction_value")
    private Long constructionValue;
    @Column(name="construction_date")
    private Date constructionDate;
    @Column(name="acquisition_value")
    private Long acquisitionValue;
    @Column(name="acquisition_date")
    private Date acquisitionDate;
    @Column(name="donation_date")
    private Date donationDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
			
	public AssetHeader getAssetHeader() {
		return assetHeader;
	}
	public void setAssetHeader(AssetHeader assetHeader) {
		this.assetHeader = assetHeader;
	}
	
	public AssetLocation getAssetLocation() {
		return assetLocation;
	}
	public void setAssetLocation(AssetLocation assetLocation) {
		this.assetLocation = assetLocation;
	}
	
	public AssetStatus getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(AssetStatus assetStatus) {
		this.assetStatus = assetStatus;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}
	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}
	public String getFileno() {
		return fileno;
	}
	public void setFileno(String fileno) {
		this.fileno = fileno;
	}
	public Long getGrossValue() {
		return grossValue;
	}
	public void setGrossValue(Long grossValue) {
		this.grossValue = grossValue;
	}
	public Long getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(Long marketValue) {
		this.marketValue = marketValue;
	}
	public String getSurveyNumber() {
		return surveyNumber;
	}
	public void setSurveyNumber(String surveyNumber) {
		this.surveyNumber = surveyNumber;
	}
	public String getAccumulatedDepreciation() {
		return accumulatedDepreciation;
	}
	public void setAccumulatedDepreciation(String accumulatedDepreciation) {
		this.accumulatedDepreciation = accumulatedDepreciation;
	}
	public Long getPurchaseValue() {
		return purchaseValue;
	}
	public void setPurchaseValue(Long purchaseValue) {
		this.purchaseValue = purchaseValue;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public Long getConstructionValue() {
		return constructionValue;
	}
	public void setConstructionValue(Long constructionValue) {
		this.constructionValue = constructionValue;
	}
	public Date getConstructionDate() {
		return constructionDate;
	}
	public void setConstructionDate(Date constructionDate) {
		this.constructionDate = constructionDate;
	}
	public Long getAcquisitionValue() {
		return acquisitionValue;
	}
	public void setAcquisitionValue(Long acquisitionValue) {
		this.acquisitionValue = acquisitionValue;
	}
	public Date getAcquisitionDate() {
		return acquisitionDate;
	}
	public void setAcquisitionDate(Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}
	public Date getDonationDate() {
		return donationDate;
	}
	public void setDonationDate(Date donationDate) {
		this.donationDate = donationDate;
	}
	
	@Override
	public String toString() {
		return "AssetMaster [id=" + id + ", code=" + code + ", assetHeader=" + assetHeader + ", assetLocation="
				+ assetLocation + ", assetStatus=" + assetStatus + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", grossValue=" + grossValue
				+ ", marketValue=" + marketValue + ", surveyNumber=" + surveyNumber + ", accumulatedDepreciation="
				+ accumulatedDepreciation + ", purchaseValue=" + purchaseValue + ", purchaseDate=" + purchaseDate
				+ ", constructionValue=" + constructionValue + ", constructionDate=" + constructionDate
				+ ", acquisitionValue=" + acquisitionValue + ", acquisitionDate=" + acquisitionDate + ", donationDate="
				+ donationDate + "]";
	}
}