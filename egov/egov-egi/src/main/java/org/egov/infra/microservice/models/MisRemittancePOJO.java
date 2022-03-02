package org.egov.infra.microservice.models;

import java.math.BigDecimal;

public class MisRemittancePOJO {
	
	private Long id;
	private String voucherNumber;
	private String voucherDate;
	private String bankaccount;
	private String amount;
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
	
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public String getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
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
