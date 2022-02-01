package org.egov.asset.model;

import java.math.BigDecimal;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SequenceGenerator(name = AssetHistory.SEQ_ASSET_HISTORY, sequenceName = AssetHistory.SEQ_ASSET_HISTORY,initialValue=1, allocationSize = 1)
@Table(name="asset_history")
public class AssetHistory {
	
	public static final String SEQ_ASSET_HISTORY = "SEQ_ASSET_HISTORY";

	@Id
	@GeneratedValue(generator = SEQ_ASSET_HISTORY, strategy = GenerationType.SEQUENCE)
	private Long id;
	@OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "asset_id")
	private AssetMaster asset;
	@Column(name = "transaction_type")
	private String transactionType;
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;
	@Column(name = "value_before_trxn")
	private BigDecimal valueBeforeTrxn;
	@Column(name = "value_after_trxn")
	private BigDecimal valueAfterTrxn;
	@Column(name = "trxn_value")
	private BigDecimal trxnValue;
	@Column(name = "rev_id")
	private Double revId;
	@Column(name = "sale_disposal_id")
	private Long saleDisposalId;
	@Column(name = "dep_id")
	private Double depId;
	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "rec_date")
	@Temporal(TemporalType.DATE)
	private Date recDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public AssetMaster getAsset() {
		return asset;
	}
	public void setAsset(AssetMaster asset) {
		this.asset = asset;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public BigDecimal getValueBeforeTrxn() {
		return valueBeforeTrxn;
	}
	public void setValueBeforeTrxn(BigDecimal valueBeforeTrxn) {
		this.valueBeforeTrxn = valueBeforeTrxn;
	}
	public BigDecimal getValueAfterTrxn() {
		return valueAfterTrxn;
	}
	public void setValueAfterTrxn(BigDecimal valueAfterTrxn) {
		this.valueAfterTrxn = valueAfterTrxn;
	}
	public BigDecimal getTrxnValue() {
		return trxnValue;
	}
	public void setTrxnValue(BigDecimal trxnValue) {
		this.trxnValue = trxnValue;
	}
	public Double getRevId() {
		return revId;
	}
	public void setRevId(Double revId) {
		this.revId = revId;
	}
	public Long getSaleDisposalId() {
		return saleDisposalId;
	}
	public void setSaleDisposalId(Long saleDisposalId) {
		this.saleDisposalId = saleDisposalId;
	}
	public Double getDepId() {
		return depId;
	}
	public void setDepId(Double depId) {
		this.depId = depId;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Date getRecDate() {
		return recDate;
	}
	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}
	
	
	

}
