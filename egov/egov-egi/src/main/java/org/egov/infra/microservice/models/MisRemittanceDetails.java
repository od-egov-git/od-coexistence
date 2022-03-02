package org.egov.infra.microservice.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "MIS_REMITTANCE_DETAILS")
@SequenceGenerator(name = MisRemittanceDetails.SEQ_MIS_REMITTANCE_DETAILS, sequenceName = MisRemittanceDetails.SEQ_MIS_REMITTANCE_DETAILS, allocationSize = 1)
	public class MisRemittanceDetails {
	
	public static final String SEQ_MIS_REMITTANCE_DETAILS = "SEQ_MIS_REMITTANCE_DETAILS";
	
	@Id
	    @GeneratedValue(generator = SEQ_MIS_REMITTANCE_DETAILS, strategy = GenerationType.SEQUENCE)
	private Long id;
	private String voucher_number;
	private Date voucher_date;
	private String bankaccount;
	private BigDecimal amount;
	private String department;
	private String function;
	private String narration;
	private String subdivison;
	private String receiptnumbers;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getVoucher_number() {
		return voucher_number;
	}
	public void setVoucher_number(String voucher_number) {
		this.voucher_number = voucher_number;
	}
	public Date getVoucher_date() {
		return voucher_date;
	}
	public void setVoucher_date(Date voucher_date) {
		this.voucher_date = voucher_date;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getSubdivison() {
		return subdivison;
	}
	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}
	public String getReceiptnumbers() {
		return receiptnumbers;
	}
	public void setReceiptnumbers(String receiptnumbers) {
		this.receiptnumbers = receiptnumbers;
	}
	
	
}
