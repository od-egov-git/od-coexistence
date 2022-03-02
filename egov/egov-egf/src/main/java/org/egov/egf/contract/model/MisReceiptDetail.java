package org.egov.egf.contract.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "MIS_RECEIPTS_DETAILS")
@SequenceGenerator(name = MisReceiptDetail.SEQ_MIS_RECEIPTS_DETAILS, sequenceName = MisReceiptDetail.SEQ_MIS_RECEIPTS_DETAILS, allocationSize = 1)
public class MisReceiptDetail {
	
	public static final String SEQ_MIS_RECEIPTS_DETAILS = "SEQ_MIS_RECEIPTS_DETAILS";
	
	@Id
    @GeneratedValue(generator = SEQ_MIS_RECEIPTS_DETAILS, strategy = GenerationType.SEQUENCE)
	private Long id ;
	private String payments_id	;
	private BigDecimal total_amt_paid ;
	private String receipt_number ;
	private Date receipt_date ;
	private String paid_by ;
	private String payer_address ;
	private String narration ;
	private String payment_status ;
	private String bank_name ;
	private String bank_branch ;
	private String subdivison ;
	private String servicename ;
	private String collectedbyname ;
	private String gstno ;
	private String payment_mode ;
	private String chequeddno;
	private Date chequedddate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPayments_id() {
		return payments_id;
	}
	public void setPayments_id(String payments_id) {
		this.payments_id = payments_id;
	}
	public BigDecimal getTotal_amt_paid() {
		return total_amt_paid;
	}
	public void setTotal_amt_paid(BigDecimal total_amt_paid) {
		this.total_amt_paid = total_amt_paid;
	}
	public String getReceipt_number() {
		return receipt_number;
	}
	public void setReceipt_number(String receipt_number) {
		this.receipt_number = receipt_number;
	}
	
	public String getPaid_by() {
		return paid_by;
	}
	public void setPaid_by(String paid_by) {
		this.paid_by = paid_by;
	}
	public String getPayer_address() {
		return payer_address;
	}
	public void setPayer_address(String payer_address) {
		this.payer_address = payer_address;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getPayment_status() {
		return payment_status;
	}
	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_branch() {
		return bank_branch;
	}
	public void setBank_branch(String bank_branch) {
		this.bank_branch = bank_branch;
	}
	public String getSubdivison() {
		return subdivison;
	}
	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}
	public String getServicename() {
		return servicename;
	}
	public void setServicename(String servicename) {
		this.servicename = servicename;
	}
	public String getCollectedbyname() {
		return collectedbyname;
	}
	public void setCollectedbyname(String collectedbyname) {
		this.collectedbyname = collectedbyname;
	}
	public String getGstno() {
		return gstno;
	}
	public void setGstno(String gstno) {
		this.gstno = gstno;
	}
	public String getPayment_mode() {
		return payment_mode;
	}
	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}
	public static String getSeqMisReceiptsDetails() {
		return SEQ_MIS_RECEIPTS_DETAILS;
	}
	public Date getReceipt_date() {
		return receipt_date;
	}
	public void setReceipt_date(Date receipt_date) {
		this.receipt_date = receipt_date;
	}
	public String getChequeddno() {
		return chequeddno;
	}
	public void setChequeddno(String chequeddno) {
		this.chequeddno = chequeddno;
	}
	public Date getChequedddate() {
		return chequedddate;
	}
	public void setChequedddate(Date chequedddate) {
		this.chequedddate = chequedddate;
	}

}
