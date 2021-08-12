/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.egf.contract.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.egov.commons.CGeneralLedger;
import org.egov.commons.CVoucherHeader;

public class Kendrapara {
	private String trn_id;
	private String VOUCHER_NAME;
	private String VOUCHER_TYPE;
	private String VOUCHER_DESCRIPTION;
	private String VOUCHER_NO;
	private String TRANSACTION_NO;
	private String TRANSACTION_NO_FOR_DATA_MIGRATION;
	private String VOUCHER_DATE;
	private String TRANSACTION_DATE;
	private String FUND_NAME;
	private String FINANCIAL_YEAR;
	private String VOUCHER_STATUS;
	private String CREATED_BY;
	private String VOUCHER_FIRST_SIGNATORY;
	private String VOUCHER_SECOND_SIGNATORY;
	private String DEPARTMENT_NAME;
	private String SCHEME_NAME;
	private String SUB_SCHEME_NAME;
	private String BUDGETARY_APPLICATION_NO;
	private String BUDGET_CHEQUE_REQUEST;
	private String FUNCTION_NAME;
	
	private String FILE_NO;
	private String SERVICE_NAME;
	private String RECEIPT_NO;
	private String REMITTANCE_DATE;
	private String TRANS_ID_RECEIPT_NO;
	private String GLCODE;
	private String BANK_ACCOUNT_NAME;
	private String BANK_ACCOUNT_CODE;
	private String DEBIT_AMOUNT;
	private String CREDIT_AMOUNT;
	private String CONTRACTOR_NAME;
	private String SUPPLIER_NAME;
	private String PARTY_DETAILS;
	private String OTHER_PARTY;
	private String PAYMENT_AMOUNT_TO_PARTY;										
	private String MIGRATION;
	
	
	
	@Override
	public String toString() {
		return "Kendrapara [VOUCHER_NAME=" + VOUCHER_NAME + ", VOUCHER_TYPE=" + VOUCHER_TYPE + ", VOUCHER_DESCRIPTION="
				+ VOUCHER_DESCRIPTION + ", VOUCHER_NO=" + VOUCHER_NO + ", TRANSACTION_NO=" + TRANSACTION_NO
				+ ", TRANSACTION_NO_FOR_DATA_MIGRATION=" + TRANSACTION_NO_FOR_DATA_MIGRATION + ", VOUCHER_DATE="
				+ VOUCHER_DATE + ", TRANSACTION_DATE=" + TRANSACTION_DATE + ", FUND_NAME=" + FUND_NAME
				+ ", FINANCIAL_YEAR=" + FINANCIAL_YEAR + ", VOUCHER_STATUS=" + VOUCHER_STATUS + ", CREATED_BY="
				+ CREATED_BY + ", VOUCHER_FIRST_SIGNATORY=" + VOUCHER_FIRST_SIGNATORY + ", VOUCHER_SECOND_SIGNATORY="
				+ VOUCHER_SECOND_SIGNATORY + ", DEPARTMENT_NAME=" + DEPARTMENT_NAME + ", SCHEME_NAME=" + SCHEME_NAME
				+ ", SUB_SCHEME_NAME=" + SUB_SCHEME_NAME + ", BUDGETARY_APPLICATION_NO=" + BUDGETARY_APPLICATION_NO
				+ ", BUDGET_CHEQUE_REQUEST=" + BUDGET_CHEQUE_REQUEST + ", FUNCTION_NAME=" + FUNCTION_NAME + ", FILE_NO="
				+ FILE_NO + ", SERVICE_NAME=" + SERVICE_NAME + ", RECEIPT_NO=" + RECEIPT_NO + ", REMITTANCE_DATE="
				+ REMITTANCE_DATE + ", TRANS_ID_RECEIPT_NO=" + TRANS_ID_RECEIPT_NO + ", GLCODE=" + GLCODE
				+ ", BANK_ACCOUNT_NAME=" + BANK_ACCOUNT_NAME + ", BANK_ACCOUNT_CODE=" + BANK_ACCOUNT_CODE
				+ ", DEBIT_AMOUNT=" + DEBIT_AMOUNT + ", CREDIT_AMOUNT=" + CREDIT_AMOUNT + ", CONTRACTOR_NAME="
				+ CONTRACTOR_NAME + ", SUPPLIER_NAME=" + SUPPLIER_NAME + ", PARTY_DETAILS=" + PARTY_DETAILS
				+ ", OTHER_PARTY=" + OTHER_PARTY + ", PAYMENT_AMOUNT_TO_PARTY=" + PAYMENT_AMOUNT_TO_PARTY
				+ ", MIGRATION=" + MIGRATION + "]";
	}
	public Kendrapara() {
		
	}
	public Kendrapara(String vOUCHER_NAME, String vOUCHER_TYPE, String vOUCHER_DESCRIPTION, String vOUCHER_NO,
			String tRANSACTION_NO, String tRANSACTION_NO_FOR_DATA_MIGRATION, String vOUCHER_DATE,
			String tRANSACTION_DATE, String fUND_NAME, String fINANCIAL_YEAR, String vOUCHER_STATUS, String cREATED_BY,
			String vOUCHER_FIRST_SIGNATORY, String vOUCHER_SECOND_SIGNATORY, String dEPARTMENT_NAME, String sCHEME_NAME,
			String sUB_SCHEME_NAME, String bUDGETARY_APPLICATION_NO, String bUDGET_CHEQUE_REQUEST, String fUNCTION_NAME,
			String fILE_NO, String sERVICE_NAME, String rECEIPT_NO, String rEMITTANCE_DATE, String tRANS_ID_RECEIPT_NO,
			String gLCODE, String bANK_ACCOUNT_NAME, String bANK_ACCOUNT_CODE, String dEBIT_AMOUNT,
			String cREDIT_AMOUNT, String cONTRACTOR_NAME, String sUPPLIER_NAME, String pARTY_DETAILS,
			String oTHER_PARTY, String pAYMENT_AMOUNT_TO_PARTY, String mIGRATION) {
		super();
		VOUCHER_NAME = vOUCHER_NAME;
		VOUCHER_TYPE = vOUCHER_TYPE;
		VOUCHER_DESCRIPTION = vOUCHER_DESCRIPTION;
		VOUCHER_NO = vOUCHER_NO;
		TRANSACTION_NO = tRANSACTION_NO;
		TRANSACTION_NO_FOR_DATA_MIGRATION = tRANSACTION_NO_FOR_DATA_MIGRATION;
		VOUCHER_DATE = vOUCHER_DATE;
		TRANSACTION_DATE = tRANSACTION_DATE;
		FUND_NAME = fUND_NAME;
		FINANCIAL_YEAR = fINANCIAL_YEAR;
		VOUCHER_STATUS = vOUCHER_STATUS;
		CREATED_BY = cREATED_BY;
		VOUCHER_FIRST_SIGNATORY = vOUCHER_FIRST_SIGNATORY;
		VOUCHER_SECOND_SIGNATORY = vOUCHER_SECOND_SIGNATORY;
		DEPARTMENT_NAME = dEPARTMENT_NAME;
		SCHEME_NAME = sCHEME_NAME;
		SUB_SCHEME_NAME = sUB_SCHEME_NAME;
		BUDGETARY_APPLICATION_NO = bUDGETARY_APPLICATION_NO;
		BUDGET_CHEQUE_REQUEST = bUDGET_CHEQUE_REQUEST;
		FUNCTION_NAME = fUNCTION_NAME;
		FILE_NO = fILE_NO;
		SERVICE_NAME = sERVICE_NAME;
		RECEIPT_NO = rECEIPT_NO;
		REMITTANCE_DATE = rEMITTANCE_DATE;
		TRANS_ID_RECEIPT_NO = tRANS_ID_RECEIPT_NO;
		GLCODE = gLCODE;
		BANK_ACCOUNT_NAME = bANK_ACCOUNT_NAME;
		BANK_ACCOUNT_CODE = bANK_ACCOUNT_CODE;
		DEBIT_AMOUNT = dEBIT_AMOUNT;
		CREDIT_AMOUNT = cREDIT_AMOUNT;
		CONTRACTOR_NAME = cONTRACTOR_NAME;
		SUPPLIER_NAME = sUPPLIER_NAME;
		PARTY_DETAILS = pARTY_DETAILS;
		OTHER_PARTY = oTHER_PARTY;
		PAYMENT_AMOUNT_TO_PARTY = pAYMENT_AMOUNT_TO_PARTY;
		MIGRATION = mIGRATION;
	}
	
	
	public String getVOUCHER_NAME() {
		return VOUCHER_NAME;
	}
	public void setVOUCHER_NAME(String vOUCHER_NAME) {
		VOUCHER_NAME = vOUCHER_NAME;
	}
	public String getVOUCHER_TYPE() {
		return VOUCHER_TYPE;
	}
	public void setVOUCHER_TYPE(String vOUCHER_TYPE) {
		VOUCHER_TYPE = vOUCHER_TYPE;
	}
	public String getVOUCHER_DESCRIPTION() {
		return VOUCHER_DESCRIPTION;
	}
	public void setVOUCHER_DESCRIPTION(String vOUCHER_DESCRIPTION) {
		VOUCHER_DESCRIPTION = vOUCHER_DESCRIPTION;
	}
	public String getVOUCHER_NO() {
		return VOUCHER_NO;
	}
	public void setVOUCHER_NO(String vOUCHER_NO) {
		VOUCHER_NO = vOUCHER_NO;
	}
	public String getTRANSACTION_NO() {
		return TRANSACTION_NO;
	}
	public void setTRANSACTION_NO(String tRANSACTION_NO) {
		TRANSACTION_NO = tRANSACTION_NO;
	}
	public String getTRANSACTION_NO_FOR_DATA_MIGRATION() {
		return TRANSACTION_NO_FOR_DATA_MIGRATION;
	}
	public void setTRANSACTION_NO_FOR_DATA_MIGRATION(String tRANSACTION_NO_FOR_DATA_MIGRATION) {
		TRANSACTION_NO_FOR_DATA_MIGRATION = tRANSACTION_NO_FOR_DATA_MIGRATION;
	}
	public String getVOUCHER_DATE() {
		return VOUCHER_DATE;
	}
	public void setVOUCHER_DATE(String vOUCHER_DATE) {
		VOUCHER_DATE = vOUCHER_DATE;
	}
	public String getTRANSACTION_DATE() {
		return TRANSACTION_DATE;
	}
	public void setTRANSACTION_DATE(String tRANSACTION_DATE) {
		TRANSACTION_DATE = tRANSACTION_DATE;
	}
	public String getFUND_NAME() {
		return FUND_NAME;
	}
	public void setFUND_NAME(String fUND_NAME) {
		FUND_NAME = fUND_NAME;
	}
	public String getFINANCIAL_YEAR() {
		return FINANCIAL_YEAR;
	}
	public void setFINANCIAL_YEAR(String fINANCIAL_YEAR) {
		FINANCIAL_YEAR = fINANCIAL_YEAR;
	}
	public String getVOUCHER_STATUS() {
		return VOUCHER_STATUS;
	}
	public void setVOUCHER_STATUS(String vOUCHER_STATUS) {
		VOUCHER_STATUS = vOUCHER_STATUS;
	}
	public String getCREATED_BY() {
		return CREATED_BY;
	}
	public void setCREATED_BY(String cREATED_BY) {
		CREATED_BY = cREATED_BY;
	}
	public String getVOUCHER_FIRST_SIGNATORY() {
		return VOUCHER_FIRST_SIGNATORY;
	}
	public void setVOUCHER_FIRST_SIGNATORY(String vOUCHER_FIRST_SIGNATORY) {
		VOUCHER_FIRST_SIGNATORY = vOUCHER_FIRST_SIGNATORY;
	}
	public String getVOUCHER_SECOND_SIGNATORY() {
		return VOUCHER_SECOND_SIGNATORY;
	}
	public void setVOUCHER_SECOND_SIGNATORY(String vOUCHER_SECOND_SIGNATORY) {
		VOUCHER_SECOND_SIGNATORY = vOUCHER_SECOND_SIGNATORY;
	}
	public String getDEPARTMENT_NAME() {
		return DEPARTMENT_NAME;
	}
	public void setDEPARTMENT_NAME(String dEPARTMENT_NAME) {
		DEPARTMENT_NAME = dEPARTMENT_NAME;
	}
	public String getSCHEME_NAME() {
		return SCHEME_NAME;
	}
	public void setSCHEME_NAME(String sCHEME_NAME) {
		SCHEME_NAME = sCHEME_NAME;
	}
	public String getSUB_SCHEME_NAME() {
		return SUB_SCHEME_NAME;
	}
	public void setSUB_SCHEME_NAME(String sUB_SCHEME_NAME) {
		SUB_SCHEME_NAME = sUB_SCHEME_NAME;
	}
	public String getBUDGETARY_APPLICATION_NO() {
		return BUDGETARY_APPLICATION_NO;
	}
	public void setBUDGETARY_APPLICATION_NO(String bUDGETARY_APPLICATION_NO) {
		BUDGETARY_APPLICATION_NO = bUDGETARY_APPLICATION_NO;
	}
	public String getBUDGET_CHEQUE_REQUEST() {
		return BUDGET_CHEQUE_REQUEST;
	}
	public void setBUDGET_CHEQUE_REQUEST(String bUDGET_CHEQUE_REQUEST) {
		BUDGET_CHEQUE_REQUEST = bUDGET_CHEQUE_REQUEST;
	}
	public String getFUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	public void setFUNCTION_NAME(String fUNCTION_NAME) {
		FUNCTION_NAME = fUNCTION_NAME;
	}
	public String getFILE_NO() {
		return FILE_NO;
	}
	public void setFILE_NO(String fILE_NO) {
		FILE_NO = fILE_NO;
	}
	public String getSERVICE_NAME() {
		return SERVICE_NAME;
	}
	public void setSERVICE_NAME(String sERVICE_NAME) {
		SERVICE_NAME = sERVICE_NAME;
	}
	public String getRECEIPT_NO() {
		return RECEIPT_NO;
	}
	public void setRECEIPT_NO(String rECEIPT_NO) {
		RECEIPT_NO = rECEIPT_NO;
	}
	public String getREMITTANCE_DATE() {
		return REMITTANCE_DATE;
	}
	public void setREMITTANCE_DATE(String rEMITTANCE_DATE) {
		REMITTANCE_DATE = rEMITTANCE_DATE;
	}
	public String getTRANS_ID_RECEIPT_NO() {
		return TRANS_ID_RECEIPT_NO;
	}
	public void setTRANS_ID_RECEIPT_NO(String tRANS_ID_RECEIPT_NO) {
		TRANS_ID_RECEIPT_NO = tRANS_ID_RECEIPT_NO;
	}
	public String getGLCODE() {
		return GLCODE;
	}
	public void setGLCODE(String gLCODE) {
		GLCODE = gLCODE;
	}
	public String getBANK_ACCOUNT_NAME() {
		return BANK_ACCOUNT_NAME;
	}
	public void setBANK_ACCOUNT_NAME(String bANK_ACCOUNT_NAME) {
		BANK_ACCOUNT_NAME = bANK_ACCOUNT_NAME;
	}
	public String getBANK_ACCOUNT_CODE() {
		return BANK_ACCOUNT_CODE;
	}
	public void setBANK_ACCOUNT_CODE(String bANK_ACCOUNT_CODE) {
		BANK_ACCOUNT_CODE = bANK_ACCOUNT_CODE;
	}
	public String getDEBIT_AMOUNT() {
		return DEBIT_AMOUNT;
	}
	public void setDEBIT_AMOUNT(String dEBIT_AMOUNT) {
		DEBIT_AMOUNT = dEBIT_AMOUNT;
	}
	public String getCREDIT_AMOUNT() {
		return CREDIT_AMOUNT;
	}
	public void setCREDIT_AMOUNT(String cREDIT_AMOUNT) {
		CREDIT_AMOUNT = cREDIT_AMOUNT;
	}
	public String getCONTRACTOR_NAME() {
		return CONTRACTOR_NAME;
	}
	public void setCONTRACTOR_NAME(String cONTRACTOR_NAME) {
		CONTRACTOR_NAME = cONTRACTOR_NAME;
	}
	public String getSUPPLIER_NAME() {
		return SUPPLIER_NAME;
	}
	public void setSUPPLIER_NAME(String sUPPLIER_NAME) {
		SUPPLIER_NAME = sUPPLIER_NAME;
	}
	public String getPARTY_DETAILS() {
		return PARTY_DETAILS;
	}
	public void setPARTY_DETAILS(String pARTY_DETAILS) {
		PARTY_DETAILS = pARTY_DETAILS;
	}
	public String getOTHER_PARTY() {
		return OTHER_PARTY;
	}
	public void setOTHER_PARTY(String oTHER_PARTY) {
		OTHER_PARTY = oTHER_PARTY;
	}
	public String getPAYMENT_AMOUNT_TO_PARTY() {
		return PAYMENT_AMOUNT_TO_PARTY;
	}
	public void setPAYMENT_AMOUNT_TO_PARTY(String pAYMENT_AMOUNT_TO_PARTY) {
		PAYMENT_AMOUNT_TO_PARTY = pAYMENT_AMOUNT_TO_PARTY;
	}
	public String getMIGRATION() {
		return MIGRATION;
	}
	public void setMIGRATION(String mIGRATION) {
		MIGRATION = mIGRATION;
	}
	public String getTrn_id() {
		return trn_id;
	}
	public void setTrn_id(String trn_id) {
		this.trn_id = trn_id;
	}
}
