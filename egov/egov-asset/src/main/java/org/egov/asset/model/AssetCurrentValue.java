package org.egov.asset.model;

import org.egov.infra.microservice.models.TransactionType;

public class AssetCurrentValue {
	
	private Long id;
    private Long assetId;
    private Long currentAmount;
    private TransactionType assetTranType;
    
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
	public Long getCurrentAmount() {
		return currentAmount;
	}
	public void setCurrentAmount(Long currentAmount) {
		this.currentAmount = currentAmount;
	}
	public TransactionType getAssetTranType() {
		return assetTranType;
	}
	public void setAssetTranType(TransactionType assetTranType) {
		this.assetTranType = assetTranType;
	}
	
	@Override
	public String toString() {
		return "AssetCurrentValue [id=" + id + ", assetId=" + assetId + ", currentAmount=" + currentAmount
				+ ", assetTranType=" + assetTranType + "]";
	}
}
