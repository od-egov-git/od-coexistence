package org.egov.model.voucher;



import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="voucherdraftdetails")
@SequenceGenerator(name = "SEQ_VoucherDraftDetails", sequenceName = "SEQ_VoucherDraftDetails", allocationSize = 1)
public class VoucherDraftDetails {
	
	   public static final String SEQ_VoucherDraftDetails = "SEQ_VoucherDraftDetails";
	   
	   
	@Id
	@GeneratedValue(generator = SEQ_VoucherDraftDetails, strategy = GenerationType.SEQUENCE)
	private Long id;
	private Long functionIdDetail;
    private String functionDetail;	
	private String voucherNumber;
	private Long glcodeIdDetail;
	private String glcodeDetail;
	private String isSubledger;
	private String accounthead;
	private BigDecimal debitAmountDetail = BigDecimal.ZERO;
    private BigDecimal creditAmountDetail = BigDecimal.ZERO;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getCreditAmountDetail() {
		return creditAmountDetail;
	}
	public void setCreditAmountDetail(BigDecimal creditAmountDetail) {
		this.creditAmountDetail = creditAmountDetail;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public Long getGlcodeIdDetail() {
		return glcodeIdDetail;
	}
	public void setGlcodeIdDetail(Long glcodeIdDetail) {
		this.glcodeIdDetail = glcodeIdDetail;
	}
	public String getGlcodeDetail() {
		return glcodeDetail;
	}
	public void setGlcodeDetail(String glcodeDetail) {
		this.glcodeDetail = glcodeDetail;
	}
	public String getIsSubledger() {
		return isSubledger;
	}
	public void setIsSubledger(String isSubledger) {
		this.isSubledger = isSubledger;
	}
	public String getAccounthead() {
		return accounthead;
	}
	public void setAccounthead(String accounthead) {
		this.accounthead = accounthead;
	}
	public BigDecimal getDebitAmountDetail() {
		return debitAmountDetail;
	}
	public void setDebitAmountDetail(BigDecimal debitAmountDetail) {
		this.debitAmountDetail = debitAmountDetail;
	}
	public Long getFunctionIdDetail() {
		return functionIdDetail;
	}
	public void setFunctionIdDetail(Long functionIdDetail) {
		this.functionIdDetail = functionIdDetail;
	}
	public String getFunctionDetail() {
		return functionDetail;
	}
	public void setFunctionDetail(String functionDetail) {
		this.functionDetail = functionDetail;
	}
	
	

}
