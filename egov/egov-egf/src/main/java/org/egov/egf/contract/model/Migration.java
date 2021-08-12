package org.egov.egf.contract.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_csv")
public class Migration {

	@Id
	private Long trn_id;
	private String voucher_name;
	private String voucher_type;
	private String voucher_description;
	private String voucher_no;
	private Long transaction_no;
	private String transaction_no_for_data_migration;
	private String voucher_date;
	private String transaction_date;
	private String fund_name;
	private String financial_year;
	private String voucher_status;
	private String created_by;
	private String voucher_first_signatory;
	private String voucher_second_signatory;
	private String department_name;
	private String scheme_name;
	private String sub_scheme_name;
	private String budgetary_application_no;
	private String budget_cheque_request;
	private String function_name;
	private String file_no;
	private String service_name;
	private String receipt_no;
	private String remittance_date;
	private String trans_id_receipt_no;
	private String glcode;
	private String bank_account_name;
	private String bank_account_code;
	private BigDecimal debit_amount;
	private BigDecimal credit_amount;
	private String contractor_name;
	private String supplier_name;
	private String party_details;
	private String other_party;
	private BigDecimal payment_amount_to_party;
	private String migration;
	private String reason;
	public Long getTrn_id() {
		return trn_id;
	}
	public void setTrn_id(Long trn_id) {
		this.trn_id = trn_id;
	}
	public String getVoucher_name() {
		return voucher_name;
	}
	public void setVoucher_name(String voucher_name) {
		this.voucher_name = voucher_name;
	}
	public String getVoucher_type() {
		return voucher_type;
	}
	public void setVoucher_type(String voucher_type) {
		this.voucher_type = voucher_type;
	}
	public String getVoucher_description() {
		return voucher_description;
	}
	public void setVoucher_description(String voucher_description) {
		this.voucher_description = voucher_description;
	}
	public String getVoucher_no() {
		return voucher_no;
	}
	public void setVoucher_no(String voucher_no) {
		this.voucher_no = voucher_no;
	}
	public Long getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(Long transaction_no) {
		this.transaction_no = transaction_no;
	}
	public String getTransaction_no_for_data_migration() {
		return transaction_no_for_data_migration;
	}
	public void setTransaction_no_for_data_migration(String transaction_no_for_data_migration) {
		this.transaction_no_for_data_migration = transaction_no_for_data_migration;
	}
	public String getVoucher_date() {
		return voucher_date;
	}
	public void setVoucher_date(String voucher_date) {
		this.voucher_date = voucher_date;
	}
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getFund_name() {
		return fund_name;
	}
	public void setFund_name(String fund_name) {
		this.fund_name = fund_name;
	}
	public String getFinancial_year() {
		return financial_year;
	}
	public void setFinancial_year(String financial_year) {
		this.financial_year = financial_year;
	}
	public String getVoucher_status() {
		return voucher_status;
	}
	public void setVoucher_status(String voucher_status) {
		this.voucher_status = voucher_status;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getVoucher_first_signatory() {
		return voucher_first_signatory;
	}
	public void setVoucher_first_signatory(String voucher_first_signatory) {
		this.voucher_first_signatory = voucher_first_signatory;
	}
	public String getVoucher_second_signatory() {
		return voucher_second_signatory;
	}
	public void setVoucher_second_signatory(String voucher_second_signatory) {
		this.voucher_second_signatory = voucher_second_signatory;
	}
	public String getDepartment_name() {
		return department_name;
	}
	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
	public String getScheme_name() {
		return scheme_name;
	}
	public void setScheme_name(String scheme_name) {
		this.scheme_name = scheme_name;
	}
	public String getSub_scheme_name() {
		return sub_scheme_name;
	}
	public void setSub_scheme_name(String sub_scheme_name) {
		this.sub_scheme_name = sub_scheme_name;
	}
	public String getBudgetary_application_no() {
		return budgetary_application_no;
	}
	public void setBudgetary_application_no(String budgetary_application_no) {
		this.budgetary_application_no = budgetary_application_no;
	}
	public String getBudget_cheque_request() {
		return budget_cheque_request;
	}
	public void setBudget_cheque_request(String budget_cheque_request) {
		this.budget_cheque_request = budget_cheque_request;
	}
	public String getFunction_name() {
		return function_name;
	}
	public void setFunction_name(String function_name) {
		this.function_name = function_name;
	}
	public String getFile_no() {
		return file_no;
	}
	public void setFile_no(String file_no) {
		this.file_no = file_no;
	}
	public String getService_name() {
		return service_name;
	}
	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
	public String getReceipt_no() {
		return receipt_no;
	}
	public void setReceipt_no(String receipt_no) {
		this.receipt_no = receipt_no;
	}
	public String getRemittance_date() {
		return remittance_date;
	}
	public void setRemittance_date(String remittance_date) {
		this.remittance_date = remittance_date;
	}
	public String getTrans_id_receipt_no() {
		return trans_id_receipt_no;
	}
	public void setTrans_id_receipt_no(String trans_id_receipt_no) {
		this.trans_id_receipt_no = trans_id_receipt_no;
	}
	public String getGlcode() {
		return glcode;
	}
	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}
	public String getBank_account_name() {
		return bank_account_name;
	}
	public void setBank_account_name(String bank_account_name) {
		this.bank_account_name = bank_account_name;
	}
	public String getBank_account_code() {
		return bank_account_code;
	}
	public void setBank_account_code(String bank_account_code) {
		this.bank_account_code = bank_account_code;
	}
	public BigDecimal getDebit_amount() {
		return debit_amount;
	}
	public void setDebit_amount(BigDecimal debit_amount) {
		this.debit_amount = debit_amount;
	}
	public BigDecimal getCredit_amount() {
		return credit_amount;
	}
	public void setCredit_amount(BigDecimal credit_amount) {
		this.credit_amount = credit_amount;
	}
	public String getContractor_name() {
		return contractor_name;
	}
	public void setContractor_name(String contractor_name) {
		this.contractor_name = contractor_name;
	}
	public String getSupplier_name() {
		return supplier_name;
	}
	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}
	public String getParty_details() {
		return party_details;
	}
	public void setParty_details(String party_details) {
		this.party_details = party_details;
	}
	public String getOther_party() {
		return other_party;
	}
	public void setOther_party(String other_party) {
		this.other_party = other_party;
	}
	public BigDecimal getPayment_amount_to_party() {
		return payment_amount_to_party;
	}
	public void setPayment_amount_to_party(BigDecimal payment_amount_to_party) {
		this.payment_amount_to_party = payment_amount_to_party;
	}
	public String getMigration() {
		return migration;
	}
	public void setMigration(String migration) {
		this.migration = migration;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
	
	
	
}
