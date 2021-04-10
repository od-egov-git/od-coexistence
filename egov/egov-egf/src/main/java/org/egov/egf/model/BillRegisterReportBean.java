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
/**
 *
 */
package org.egov.egf.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.ChartOfAccounts;

/**
 * @author manoranjan
 *
 */
public class BillRegisterReportBean {

    private String billNumber;
    private String voucherNumber;
    private String paymentVoucherNumber;
    private String partyName;
    private BigDecimal grossAmount;
    private BigDecimal netAmount;
    private BigDecimal deductionAmount;
    private BigDecimal paidAmount;
    private String status;
    private String billDate;
    private String chequeNumAndDate;
    private String remittanceVoucherNumber;
    private String remittanceChequeNumberAndDate;
    private Date ChequeDate;
    private Long vhId;
    private Long phId;
    private Long deducVhId;
    private String deducVoucherNumber;
    private String pexNo;
    private String deducPexNo;
    private String departmentCode;
    private String pexNodate;
    private List<ChartOfAccounts> chartOfAccounts;
    private List<BillDetail> billDetailList=new ArrayList<BillDetail>();
    private String budgetHead;
    private String scheme;
    private String voucherDate;
    private String bpvDate;
    private String bankaccount;
    private BigDecimal taxAmount;
    private BigDecimal igstAmount;
    private BigDecimal cgstAmount;
    private BigDecimal labourcessAmount;
    private BigDecimal collectionchargesAmount;
    private BigDecimal waterChargesAmount;
    private BigDecimal qualityAmount;
    private BigDecimal penaltyAmount;
    private BigDecimal securitAmount;
    private BigDecimal gpfAmount;
    private BigDecimal npsAmount;
    private BigDecimal gslicAmount;
    private BigDecimal hbaAmount;
    private BigDecimal licenseAmount;
    private BigDecimal licAmount;
    private BigDecimal bankAmount;
    private BigDecimal courtAmount;
    private BigDecimal pensionAmount;
    public String getBillNumber() {
        return billNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public String getPaymentVoucherNumber() {
        return paymentVoucherNumber;
    }

    public String getPartyName() {
        return partyName;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public BigDecimal getDeductionAmount() {
        return deductionAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setBillNumber(final String billNumber) {
        this.billNumber = billNumber;
    }

    public void setVoucherNumber(final String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public void setPaymentVoucherNumber(final String paymentVoucherNumber) {
        this.paymentVoucherNumber = paymentVoucherNumber;
    }

    public String getChequeNumAndDate() {
        return chequeNumAndDate;
    }

    public void setChequeNumAndDate(final String chequeNumAndDate) {
        this.chequeNumAndDate = chequeNumAndDate;
    }

    public void setPartyName(final String partyName) {
        this.partyName = partyName;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public void setDeductionAmount(final BigDecimal deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public void setPaidAmount(final BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(final String billDate) {
        this.billDate = billDate;
    }

    public String getRemittanceVoucherNumber() {
        return remittanceVoucherNumber;
    }

    public void setRemittanceVoucherNumber(final String remittanceVoucherNumber) {
        this.remittanceVoucherNumber = remittanceVoucherNumber;
    }

    public Date getChequeDate() {
        return ChequeDate;
    }

    public void setChequeDate(final Date chequeDate) {
        ChequeDate = chequeDate;
    }

    public String getRemittanceChequeNumberAndDate() {
        return remittanceChequeNumberAndDate;
    }

    public void setRemittanceChequeNumberAndDate(
            final String remittanceChequeNumberAndDate) {
        this.remittanceChequeNumberAndDate = remittanceChequeNumberAndDate;
    }

	public Long getVhId() {
		return vhId;
	}

	public void setVhId(Long vhId) {
		this.vhId = vhId;
	}

	public Long getPhId() {
		return phId;
	}

	public void setPhId(Long phId) {
		this.phId = phId;
	}

	public Long getDeducVhId() {
		return deducVhId;
	}

	public void setDeducVhId(Long deducVhId) {
		this.deducVhId = deducVhId;
	}

	public String getDeducVoucherNumber() {
		return deducVoucherNumber;
	}

	public void setDeducVoucherNumber(String deducVoucherNumber) {
		this.deducVoucherNumber = deducVoucherNumber;
	}

	public String getPexNo() {
		return pexNo;
	}

	public void setPexNo(String pexNo) {
		this.pexNo = pexNo;
	}

	public String getDeducPexNo() {
		return deducPexNo;
	}

	public void setDeducPexNo(String deducPexNo) {
		this.deducPexNo = deducPexNo;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getPexNodate() {
		return pexNodate;
	}

	public void setPexNodate(String pexNodate) {
		this.pexNodate = pexNodate;
	}

	public List<ChartOfAccounts> getChartOfAccounts() {
		return chartOfAccounts;
	}

	public void setChartOfAccounts(List<ChartOfAccounts> chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	public List<BillDetail> getBillDetailList() {
		return billDetailList;
	}

	public void setBillDetailList(List<BillDetail> billDetailList) {
		this.billDetailList = billDetailList;
	}

	public String getBudgetHead() {
		return budgetHead;
	}

	public void setBudgetHead(String budgetHead) {
		this.budgetHead = budgetHead;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getLabourcessAmount() {
		return labourcessAmount;
	}

	public void setLabourcessAmount(BigDecimal labourcessAmount) {
		this.labourcessAmount = labourcessAmount;
	}

	public BigDecimal getCollectionchargesAmount() {
		return collectionchargesAmount;
	}

	public void setCollectionchargesAmount(BigDecimal collectionchargesAmount) {
		this.collectionchargesAmount = collectionchargesAmount;
	}

	public BigDecimal getWaterChargesAmount() {
		return waterChargesAmount;
	}

	public void setWaterChargesAmount(BigDecimal waterChargesAmount) {
		this.waterChargesAmount = waterChargesAmount;
	}

	public BigDecimal getQualityAmount() {
		return qualityAmount;
	}

	public void setQualityAmount(BigDecimal qualityAmount) {
		this.qualityAmount = qualityAmount;
	}

	public BigDecimal getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(BigDecimal penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public BigDecimal getSecuritAmount() {
		return securitAmount;
	}

	public void setSecuritAmount(BigDecimal securitAmount) {
		this.securitAmount = securitAmount;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getBpvDate() {
		return bpvDate;
	}

	public void setBpvDate(String bpvDate) {
		this.bpvDate = bpvDate;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	public BigDecimal getGpfAmount() {
		return gpfAmount;
	}

	public void setGpfAmount(BigDecimal gpfAmount) {
		this.gpfAmount = gpfAmount;
	}

	public BigDecimal getNpsAmount() {
		return npsAmount;
	}

	public void setNpsAmount(BigDecimal npsAmount) {
		this.npsAmount = npsAmount;
	}

	public BigDecimal getGslicAmount() {
		return gslicAmount;
	}

	public void setGslicAmount(BigDecimal gslicAmount) {
		this.gslicAmount = gslicAmount;
	}

	public BigDecimal getHbaAmount() {
		return hbaAmount;
	}

	public void setHbaAmount(BigDecimal hbaAmount) {
		this.hbaAmount = hbaAmount;
	}

	public BigDecimal getLicenseAmount() {
		return licenseAmount;
	}

	public void setLicenseAmount(BigDecimal licenseAmount) {
		this.licenseAmount = licenseAmount;
	}

	public BigDecimal getLicAmount() {
		return licAmount;
	}

	public void setLicAmount(BigDecimal licAmount) {
		this.licAmount = licAmount;
	}

	public BigDecimal getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(BigDecimal bankAmount) {
		this.bankAmount = bankAmount;
	}

	public BigDecimal getCourtAmount() {
		return courtAmount;
	}

	public void setCourtAmount(BigDecimal courtAmount) {
		this.courtAmount = courtAmount;
	}

	public BigDecimal getPensionAmount() {
		return pensionAmount;
	}

	public void setPensionAmount(BigDecimal pensionAmount) {
		this.pensionAmount = pensionAmount;
	}

	
}
