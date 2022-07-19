package org.egov.egf.web.controller.cashbook;

import java.math.BigDecimal;
import java.util.List;

import org.egov.model.instrument.InstrumentVoucher;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class CashBookViewEntry {
    String receiptVoucherDate;
    String receiptVoucherNumber;
    String receiptParticulars;
    BigDecimal receiptCash;
   	BigDecimal receiptAmount;
    String receiptChequeDetail;
    String paymentVoucherDate;
    String paymentVoucherNumber;
    String paymentParticulars;
    BigDecimal paymentAmount;
    BigDecimal paymentCash;
    String paymentChequeDetail;
    String instrumentStatus;
    String glCode;
    private Long voucherId;
    // string of cheque number and dates
    private String chequeNumber;

    private List<InstrumentVoucher> instrumentVouchers;

    public List<InstrumentVoucher> getInstrumentVouchers() {
        return instrumentVouchers;
    }

    public void setInstrumentVouchers(final List<InstrumentVoucher> instrumentVouchers) {
        this.instrumentVouchers = instrumentVouchers;
    }

    public CashBookViewEntry() {
    };
    public BigDecimal getReceiptCash() {
		return receiptCash;
	}

	public void setReceiptCash(BigDecimal receiptCash) {
		this.receiptCash = receiptCash;
	}

	public BigDecimal getPaymentCash() {
		return paymentCash;
	}

	public void setPaymentCash(BigDecimal paymentCash) {
		this.paymentCash = paymentCash;
	}

    public CashBookViewEntry(final String voucherNumber, final String voucherDate, final String particulars,
            final BigDecimal amount,
            final String chequeDetail, final String type) {
        super();
        if ("Payment".equalsIgnoreCase(type)) {
            paymentVoucherDate = voucherDate;
            paymentVoucherNumber = voucherNumber;
            paymentParticulars = particulars;
            paymentAmount = amount;
            paymentChequeDetail = chequeDetail;
        } else {
            receiptVoucherDate = voucherDate;
            receiptVoucherNumber = voucherNumber;
            receiptParticulars = particulars;
            receiptAmount = amount;
            receiptChequeDetail = chequeDetail;
        }
    }

    public CashBookViewEntry(final String voucherNumber, final String voucherDate, final String particulars,
            final BigDecimal amount,
            final String chequeDetail, final String type, final String chequeNumber) {
        super();
        if ("Payment".equalsIgnoreCase(type)) {
            paymentVoucherDate = voucherDate;
            paymentVoucherNumber = voucherNumber;
            paymentParticulars = particulars;
            paymentAmount = amount;
            paymentChequeDetail = chequeDetail;
            instrumentVouchers = instrumentVouchers;
            this.chequeNumber = chequeNumber;
        } else {
            receiptVoucherDate = voucherDate;
            receiptVoucherNumber = voucherNumber;
            receiptParticulars = particulars;
            receiptAmount = amount;
            receiptChequeDetail = chequeDetail;
            instrumentVouchers = instrumentVouchers;
            this.chequeNumber = chequeNumber;
        }
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(final String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getReceiptVoucherDate() {
        return receiptVoucherDate;
    }

    public void setReceiptVoucherDate(final String receiptVoucherDate) {
        this.receiptVoucherDate = receiptVoucherDate;
    }

    public String getReceiptVoucherNumber() {
        return receiptVoucherNumber;
    }

    public void setReceiptVoucherNumber(final String receiptVoucherNumber) {
        this.receiptVoucherNumber = receiptVoucherNumber;
    }

    public String getReceiptParticulars() {
        return receiptParticulars;
    }

    public void setReceiptParticulars(final String receiptParticulars) {
        this.receiptParticulars = receiptParticulars;
    }

    public BigDecimal getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(final BigDecimal receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getReceiptChequeDetail() {
        return receiptChequeDetail;
    }

    public void setReceiptChequeDetail(final String receiptChequeDetail) {
        this.receiptChequeDetail = receiptChequeDetail;
    }

    public String getPaymentVoucherDate() {
        return paymentVoucherDate;
    }

    public void setPaymentVoucherDate(final String paymentVoucherDate) {
        this.paymentVoucherDate = paymentVoucherDate;
    }

    public String getPaymentVoucherNumber() {
        return paymentVoucherNumber;
    }

    public void setPaymentVoucherNumber(final String paymentVoucherNumber) {
        this.paymentVoucherNumber = paymentVoucherNumber;
    }

    public String getPaymentParticulars() {
        return paymentParticulars;
    }

    public void setPaymentParticulars(final String paymentParticulars) {
        this.paymentParticulars = paymentParticulars;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(final BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentChequeDetail() {
        return paymentChequeDetail;
    }

    public void setPaymentChequeDetail(final String paymentChequeDetail) {
        this.paymentChequeDetail = paymentChequeDetail;
    }

    public String getInstrumentStatus() {
        return instrumentStatus;
    }

    public void setInstrumentStatus(final String instrumentStatus) {
        this.instrumentStatus = instrumentStatus;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(final String glCode) {
        this.glCode = glCode;
    }

    public void setVoucherId(final Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getVoucherId() {
        return voucherId;
    }
}
