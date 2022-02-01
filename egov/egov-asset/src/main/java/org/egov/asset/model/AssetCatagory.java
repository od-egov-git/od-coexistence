package org.egov.asset.model;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Transient;

import org.egov.commons.CChartOfAccounts;




@Entity
@SequenceGenerator(name = AssetCatagory.SEQ_ASSET_CATAGORY, sequenceName = AssetCatagory.SEQ_ASSET_CATAGORY,initialValue=1, allocationSize = 1)
@Table(name="asset_category")
public class AssetCatagory {

	public static final String SEQ_ASSET_CATAGORY = "SEQ_ASSET_CATAGORY";

	@Id
	@GeneratedValue(generator = SEQ_ASSET_CATAGORY, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "name")
	private String name;
	@OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "asset_catagory_type_id")
	private AssetCatagoryType assetCatagoryType;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "parent_catagory_id")
	private ParentCatagory parentCatagory;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "depriciation_method_id")
	private DepriciationMethod depriciationMethod;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "asset_account_code_id")
	private CChartOfAccounts assetAccountCode;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "accumulated_depriciation_code_id")
	private CChartOfAccounts accumulatedDepriciationCode;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "revolution_reserve_account_code_id")
	private CChartOfAccounts revolutionReserveAccountCode;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "depriciation_expense_account_id")
	private CChartOfAccounts depriciationExpenseAccount;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "unit_of_measurement_id")
	private UnitOfMeasurement unitOfMeasurement;
	@Column(name = "version")
	private String version;
	@Column(name = "depriciation_rate")
	private Double depriciationRate;
	@Column(name = "lease_and_agreement")
	private boolean leaseAndAgreement;
	@Column(name = "create_date")
	private String createDate;
	@Column(name = "update_date")
	private String updateDate;
	@Column(name = "asset_code")
	private String assetCode;
	//@OneToMany(mappedBy="assetCatagory",cascade = CascadeType.PERSIST,fetch=FetchType.LAZY)
	@OneToMany(targetEntity=CustomeFields.class,cascade = CascadeType.PERSIST)
	@JoinColumn(name="asset_catagory_id",referencedColumnName="id")
	//@OneToMany(mappedBy="assetCatagory",cascade = CascadeType.PERSIST,fetch=FetchType.EAGER)
	private List<CustomeFields> customeFields=new ArrayList<>();
	@Column(name = "userid")
	private String userid;
	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "updated_by")
	private Long updatedBy;
	@Column(name = "life_of_asset")
	private String lifeOfAsset;
	
	@Transient
	private CustomeFields customeField;
	
	@Transient
	private String errorMessage;

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

	public AssetCatagoryType getAssetCatagoryType() {
		return assetCatagoryType;
	}

	public void setAssetCatagoryType(AssetCatagoryType assetCatagoryType) {
		this.assetCatagoryType = assetCatagoryType;
	}

	public ParentCatagory getParentCatagory() {
		return parentCatagory;
	}

	public void setParentCatagory(ParentCatagory parentCatagory) {
		this.parentCatagory = parentCatagory;
	}

	public DepriciationMethod getDepriciationMethod() {
		return depriciationMethod;
	}

	public void setDepriciationMethod(DepriciationMethod depriciationMethod) {
		this.depriciationMethod = depriciationMethod;
	}

	public CChartOfAccounts getAssetAccountCode() {
		return assetAccountCode;
	}

	public void setAssetAccountCode(CChartOfAccounts assetAccountCode) {
		this.assetAccountCode = assetAccountCode;
	}

	public CChartOfAccounts getAccumulatedDepriciationCode() {
		return accumulatedDepriciationCode;
	}

	public void setAccumulatedDepriciationCode(CChartOfAccounts accumulatedDepriciationCode) {
		this.accumulatedDepriciationCode = accumulatedDepriciationCode;
	}

	public CChartOfAccounts getRevolutionReserveAccountCode() {
		return revolutionReserveAccountCode;
	}

	public void setRevolutionReserveAccountCode(CChartOfAccounts revolutionReserveAccountCode) {
		this.revolutionReserveAccountCode = revolutionReserveAccountCode;
	}

	public CChartOfAccounts getDepriciationExpenseAccount() {
		return depriciationExpenseAccount;
	}

	public void setDepriciationExpenseAccount(CChartOfAccounts depriciationExpenseAccount) {
		this.depriciationExpenseAccount = depriciationExpenseAccount;
	}

	public UnitOfMeasurement getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Double getDepriciationRate() {
		return depriciationRate;
	}

	public void setDepriciationRate(Double depriciationRate) {
		this.depriciationRate = depriciationRate;
	}

	public boolean isLeaseAndAgreement() {
		return leaseAndAgreement;
	}

	public void setLeaseAndAgreement(boolean leaseAndAgreement) {
		this.leaseAndAgreement = leaseAndAgreement;
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

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public List<CustomeFields> getCustomeFields() {
		return customeFields;
	}

	public void setCustomeFields(List<CustomeFields> customeFields) {
		this.customeFields = customeFields;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
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

	public CustomeFields getCustomeField() {
		return customeField;
	}

	public void setCustomeField(CustomeFields customeField) {
		this.customeField = customeField;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getLifeOfAsset() {
		return lifeOfAsset;
	}

	public void setLifeOfAsset(String lifeOfAsset) {
		this.lifeOfAsset = lifeOfAsset;
	}

	@Override
	public String toString() {
		return "AssetCatagory [id=" + id + ", name=" + name + ", assetCatagoryType=" + assetCatagoryType
				+ ", parentCatagory=" + parentCatagory + ", depriciationMethod=" + depriciationMethod
				+ ", assetAccountCode=" + assetAccountCode + ", accumulatedDepriciationCode="
				+ accumulatedDepriciationCode + ", revolutionReserveAccountCode=" + revolutionReserveAccountCode
				+ ", depriciationExpenseAccount=" + depriciationExpenseAccount + ", unitOfMeasurement="
				+ unitOfMeasurement + ", version=" + version + ", depriciationRate=" + depriciationRate
				+ ", leaseAndAgreement=" + leaseAndAgreement + ", createDate=" + createDate + ", updateDate="
				+ updateDate + ", assetCode=" + assetCode + ", customeFields=" + customeFields + ", userid=" + userid
				+ ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", lifeOfAsset=" + lifeOfAsset
				+ ", customeField=" + customeField + ", errorMessage=" + errorMessage + "]";
	}

	

	
	
}
