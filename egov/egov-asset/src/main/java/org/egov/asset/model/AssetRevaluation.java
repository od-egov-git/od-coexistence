package org.egov.asset.model;

import java.math.BigDecimal;
import java.util.Date;

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

import org.egov.infra.persistence.entity.AbstractAuditable;




@Entity
@SequenceGenerator(name = AssetRevaluation.SEQ_ASSET_REVALUATION, sequenceName = AssetRevaluation.SEQ_ASSET_REVALUATION,initialValue=1, allocationSize = 1)
@Table(name="asset_revaluation")
public class AssetRevaluation extends AbstractAuditable{

	public static final String SEQ_ASSET_REVALUATION = "SEQ_ASSET_REVALUATION";

	@Id
	@GeneratedValue(generator = SEQ_ASSET_REVALUATION, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "asset_master_id")
	private AssetMaster assetMaster;
	
	@Column(name = "rev_order_no")
	private String rev_order_no;
	
	@Column(name = "rev_order_date")
	private Date rev_order_date;
	
	@Column(name = "value_after_revaluation")
	private BigDecimal value_after_revaluation;
	
	@Column(name = "type_of_change")
	private String type_of_change;
	
	@Column(name = "rev_date")
	private Date rev_date;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "add_del_amt")
	private BigDecimal add_del_amt;
	
	@Column(name = "current_value")
	private BigDecimal current_value;
	
	@Column(name="department")
	private String department;
	
	@Column(name="function")
	private int function;
	
	@Column(name="voucher")
	private String voucher;
	
	@Column(name="fund")
	private int fund;
	
	@Column(name="comment")
	private String comment;
	
	@Transient
	private BigDecimal updatedCurrentValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AssetMaster getAssetMaster() {
		return assetMaster;
	}

	public void setAssetMaster(AssetMaster assetMaster) {
		this.assetMaster = assetMaster;
	}

	public String getRev_order_no() {
		return rev_order_no;
	}

	public void setRev_order_no(String rev_order_no) {
		this.rev_order_no = rev_order_no;
	}

	public Date getRev_order_date() {
		return rev_order_date;
	}

	public void setRev_order_date(Date rev_order_date) {
		this.rev_order_date = rev_order_date;
	}

	public BigDecimal getValue_after_revaluation() {
		return value_after_revaluation;
	}

	public void setValue_after_revaluation(BigDecimal value_after_revaluation) {
		this.value_after_revaluation = value_after_revaluation;
	}

	public String getType_of_change() {
		return type_of_change;
	}

	public void setType_of_change(String type_of_change) {
		this.type_of_change = type_of_change;
	}

	public Date getRev_date() {
		return rev_date;
	}

	public void setRev_date(Date rev_date) {
		this.rev_date = rev_date;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public BigDecimal getAdd_del_amt() {
		return add_del_amt;
	}

	public void setAdd_del_amt(BigDecimal add_del_amt) {
		this.add_del_amt = add_del_amt;
	}

	public BigDecimal getCurrent_value() {
		return current_value;
	}

	public void setCurrent_value(BigDecimal current_value) {
		this.current_value = current_value;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}


	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}

	public int getFund() {
		return fund;
	}

	public void setFund(int fund) {
		this.fund = fund;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public BigDecimal getUpdatedCurrentValue() {
		return updatedCurrentValue;
	}

	public void setUpdatedCurrentValue(BigDecimal updatedCurrentValue) {
		this.updatedCurrentValue = updatedCurrentValue;
	}

	
	
}
