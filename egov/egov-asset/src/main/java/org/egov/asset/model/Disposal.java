package org.egov.asset.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.egov.commons.CChartOfAccounts;
import org.egov.model.bills.DocumentUpload;

@Entity
@SequenceGenerator(name = Disposal.SEQ_SALE_DISPOSAL, sequenceName = Disposal.SEQ_SALE_DISPOSAL,initialValue=1, allocationSize = 1)
@Table(name="sale_disposal")
public class Disposal {
	
		public static final String SEQ_SALE_DISPOSAL = "SEQ_SALE_DISPOSAL";
	  
		@Id
		@GeneratedValue(generator = SEQ_SALE_DISPOSAL, strategy = GenerationType.SEQUENCE)
	    private Long id;

		/*@Column(name = "asset_id")
	    private Long assetId;*/
		@OneToOne(fetch=FetchType.LAZY)
	    @JoinColumn(name = "asset_id")
	    private AssetMaster asset;

		@Column(name = "buyer_Name")
	    private String buyerName;

		@Column(name = "buyer_address")
	    private String buyerAddress;

		@Column(name = "reason")
	    private String disposalReason;

	    
	    //private Long disposalDate = null;
		@Column(name = "disposal_date")
		@Temporal(TemporalType.DATE)
	    private Date disposalDate;

		@Column(name = "pan_card_number")
	    private String panCardNumber;

		@Column(name = "aadhar_card_number")
	    private String aadharCardNumber;

		@Column(name = "asset_current_value")
	    private BigDecimal assetCurrentValue;

		@Column(name = "sale_value")
	    private BigDecimal saleValue;

	    
	    //private TransactionType transactionType = null;
		@Column(name = "transaction_type")
	    private String transactionType;

	  
	    //private Long assetSaleAccount = null;
		@OneToOne(fetch=FetchType.LAZY)
		@JoinColumn(name = "asset_sale_account_id")
	    private CChartOfAccounts assetSaleAccount;
	    //private AuditDetails auditDetails = null;
		@Column(name = "create_date")
		@Temporal(TemporalType.DATE)
	    private Date createDate;
		@Column(name = "created_by")
	    private Long createdBy;

		@Column(name = "voucher_reference_number")
	    private String profitLossVoucherReference;
	    
		@Transient
	    private List<DocumentUpload> documents=new ArrayList<>();
		@Transient
		private CChartOfAccounts assetSaleAccountSale;
		@Transient
		private String saleReason;
		@Transient
		private Date saleDate;
		@Transient
		private BigDecimal assetCurrentValueSale;
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		/*public Long getAssetId() {
			return assetId;
		}

		public void setAssetId(Long assetId) {
			this.assetId = assetId;
		}*/

		public String getBuyerName() {
			return buyerName;
		}

		public void setBuyerName(String buyerName) {
			this.buyerName = buyerName;
		}

		public String getBuyerAddress() {
			return buyerAddress;
		}

		public void setBuyerAddress(String buyerAddress) {
			this.buyerAddress = buyerAddress;
		}

		public String getDisposalReason() {
			return disposalReason;
		}

		public void setDisposalReason(String disposalReason) {
			this.disposalReason = disposalReason;
		}

		
		public String getPanCardNumber() {
			return panCardNumber;
		}

		public void setPanCardNumber(String panCardNumber) {
			this.panCardNumber = panCardNumber;
		}

		public String getAadharCardNumber() {
			return aadharCardNumber;
		}

		public void setAadharCardNumber(String aadharCardNumber) {
			this.aadharCardNumber = aadharCardNumber;
		}

		public BigDecimal getAssetCurrentValue() {
			return assetCurrentValue;
		}

		public void setAssetCurrentValue(BigDecimal assetCurrentValue) {
			this.assetCurrentValue = assetCurrentValue;
		}

		public BigDecimal getSaleValue() {
			return saleValue;
		}

		public void setSaleValue(BigDecimal saleValue) {
			this.saleValue = saleValue;
		}

		

		public String getProfitLossVoucherReference() {
			return profitLossVoucherReference;
		}

		public void setProfitLossVoucherReference(String profitLossVoucherReference) {
			this.profitLossVoucherReference = profitLossVoucherReference;
		}

		public List<DocumentUpload> getDocuments() {
			return documents;
		}

		public void setDocuments(List<DocumentUpload> documents) {
			this.documents = documents;
		}

		

		public String getTransactionType() {
			return transactionType;
		}

		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}

		public CChartOfAccounts getAssetSaleAccount() {
			return assetSaleAccount;
		}

		public void setAssetSaleAccount(CChartOfAccounts assetSaleAccount) {
			this.assetSaleAccount = assetSaleAccount;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public Long getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(Long createdBy) {
			this.createdBy = createdBy;
		}

		public AssetMaster getAsset() {
			return asset;
		}

		public void setAsset(AssetMaster asset) {
			this.asset = asset;
		}

		public Date getDisposalDate() {
			return disposalDate;
		}

		public void setDisposalDate(Date disposalDate) {
			this.disposalDate = disposalDate;
		}

		public CChartOfAccounts getAssetSaleAccountSale() {
			return assetSaleAccountSale;
		}

		public void setAssetSaleAccountSale(CChartOfAccounts assetSaleAccountSale) {
			this.assetSaleAccountSale = assetSaleAccountSale;
		}

		public String getSaleReason() {
			return saleReason;
		}

		public void setSaleReason(String saleReason) {
			this.saleReason = saleReason;
		}

		public Date getSaleDate() {
			return saleDate;
		}

		public void setSaleDate(Date saleDate) {
			this.saleDate = saleDate;
		}

		public BigDecimal getAssetCurrentValueSale() {
			return assetCurrentValueSale;
		}

		public void setAssetCurrentValueSale(BigDecimal assetCurrentValueSale) {
			this.assetCurrentValueSale = assetCurrentValueSale;
		}

		
		
	    
	    
	    
	    

}