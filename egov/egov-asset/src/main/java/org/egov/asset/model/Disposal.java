package org.egov.asset.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.commons.CChartOfAccounts;
import org.egov.model.bills.DocumentUpload;

public class Disposal {
	

	  
	    private Long id = null;

	    
	    private Long assetId = null;
	    private AssetMaster asset;

	    
	    private String buyerName = null;

	    
	    private String buyerAddress = null;

	    
	    private String disposalReason = null;

	    
	    //private Long disposalDate = null;
	    private String disposalDate = null;
	    private Date disDate = null;

	    
	    private String panCardNumber = null;

	    
	    private String aadharCardNumber = null;

	    
	    private BigDecimal assetCurrentValue = null;

	    
	    private BigDecimal saleValue = null;

	    
	    //private TransactionType transactionType = null;
	    private String transactionType = null;

	  
	    //private Long assetSaleAccount = null;
	    private CChartOfAccounts assetSaleAccount = null;
	    //private AuditDetails auditDetails = null;
	    private Date createDate;
	    private Long createdBy;

	    
	    private String profitLossVoucherReference;
	    
	    private List<DocumentUpload> documents=new ArrayList<>();

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getAssetId() {
			return assetId;
		}

		public void setAssetId(Long assetId) {
			this.assetId = assetId;
		}

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

		public String getDisposalDate() {
			return disposalDate;
		}

		public void setDisposalDate(String disposalDate) {
			this.disposalDate = disposalDate;
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

		public Date getDisDate() {
			return disDate;
		}

		public void setDisDate(Date disDate) {
			this.disDate = disDate;
		}
		
	    
	    
	    
	    

}