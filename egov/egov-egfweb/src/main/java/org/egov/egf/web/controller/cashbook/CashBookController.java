package org.egov.egf.web.controller.cashbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.egov.collection.entity.MisReceiptDetail;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infstr.services.PersistenceService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping(value = "/cashBookReport")
public class CashBookController {
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private CityService cityService;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	private static final long MILLIS_IN_A_YEAR = (long) 1000 * 60 * 60 * 24 * 365;
	private static final long MILLIS_IN_A_DAY = (long) 1000*60*60*24;
	private static final Logger LOGGER = Logger.getLogger(CashBookController.class);
	private List<MisReceiptDetail> misReceiptDetails = new ArrayList<MisReceiptDetail>();
	private List<MisRemittanceDetails> misRemittanceDetails = new ArrayList<MisRemittanceDetails>();
	List<CashBookReportDataBean> finalList = new ArrayList<CashBookReportDataBean>();
	List<CashFlowReportDataBean> cashFlowFinalList = new ArrayList<CashFlowReportDataBean>();
	Statement balanceSheet = new Statement();
	@Autowired
	BalanceSheetServiceCB balanceSheetService;
	private BigDecimal incomeOverExpenditureCurr = new BigDecimal(0);
	private BigDecimal incomeOverExpenditurePrevYear = new BigDecimal(0);
	private BigDecimal depreciationCurr = new BigDecimal(0);
	private BigDecimal depreciationPrevYear = new BigDecimal(0);
	private BigDecimal interestFinanceChargesCurrYear = new BigDecimal(0);
	private BigDecimal interestFinanceChargesPrevYear = new BigDecimal(0);
	private BigDecimal profitOnDisposalOfAssetsCurrYear = new BigDecimal(0);
	private BigDecimal profitOnDisposalOfAssetsPrevYear = new BigDecimal(0);
	private BigDecimal dividendIncomeCurrYear = new BigDecimal(0);
	private BigDecimal dividendIncomePrevYear = new BigDecimal(0);
	private BigDecimal investmentIncomeCurrYear = new BigDecimal(0);
	private BigDecimal investmentIncomePrevYear = new BigDecimal(0);
	private BigDecimal adjustedIncomeCurrYear = new BigDecimal(0);
	private BigDecimal adjustedIncomePrevYear = new BigDecimal(0);
	private BigDecimal investmentIncomeReceivedCurrYear = new BigDecimal(0);
	private BigDecimal investmentIncomeReceivedPrevYear = new BigDecimal(0);
	private BigDecimal interestIncomeReceivedCurrYear = new BigDecimal(0);
	private BigDecimal interestIncomeReceivedPrevYear = new BigDecimal(0);
	private BigDecimal fixedAssetDebitAmt = new BigDecimal(0);
	private BigDecimal fixedAssetCreditAmt = new BigDecimal(0);
	private BigDecimal investmentDebitAmt = new BigDecimal(0);
	private BigDecimal investmentCreditAmt = new BigDecimal(0);
	private String titleName = "";

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public BigDecimal getIncomeOverExpenditureCurr() {
		return incomeOverExpenditureCurr;
	}

	public void setIncomeOverExpenditureCurr(BigDecimal incomeOverExpenditureCurr) {
		this.incomeOverExpenditureCurr = incomeOverExpenditureCurr;
	}

	public BigDecimal getIncomeOverExpenditurePrevYear() {
		return incomeOverExpenditurePrevYear;
	}

	public void setIncomeOverExpenditurePrevYear(BigDecimal incomeOverExpenditurePrevYear) {
		this.incomeOverExpenditurePrevYear = incomeOverExpenditurePrevYear;
	}

	public BigDecimal getDepreciationCurr() {
		return depreciationCurr;
	}

	public void setDepreciationCurr(BigDecimal depreciationCurr) {
		this.depreciationCurr = depreciationCurr;
	}

	public BigDecimal getDepreciationPrevYear() {
		return depreciationPrevYear;
	}

	public void setDepreciationPrevYear(BigDecimal depreciationPrevYear) {
		this.depreciationPrevYear = depreciationPrevYear;
	}

	public BigDecimal getInterestFinanceChargesCurrYear() {
		return interestFinanceChargesCurrYear;
	}

	public void setInterestFinanceChargesCurrYear(BigDecimal interestFinanceChargesCurrYear) {
		this.interestFinanceChargesCurrYear = interestFinanceChargesCurrYear;
	}

	public BigDecimal getInterestFinanceChargesPrevYear() {
		return interestFinanceChargesPrevYear;
	}

	public void setInterestFinanceChargesPrevYear(BigDecimal interestFinanceChargesPrevYear) {
		this.interestFinanceChargesPrevYear = interestFinanceChargesPrevYear;
	}

	public BigDecimal getProfitOnDisposalOfAssetsCurrYear() {
		return profitOnDisposalOfAssetsCurrYear;
	}

	public void setProfitOnDisposalOfAssetsCurrYear(BigDecimal profitOnDisposalOfAssetsCurrYear) {
		this.profitOnDisposalOfAssetsCurrYear = profitOnDisposalOfAssetsCurrYear;
	}

	public BigDecimal getProfitOnDisposalOfAssetsPrevYear() {
		return profitOnDisposalOfAssetsPrevYear;
	}

	public void setProfitOnDisposalOfAssetsPrevYear(BigDecimal profitOnDisposalOfAssetsPrevYear) {
		this.profitOnDisposalOfAssetsPrevYear = profitOnDisposalOfAssetsPrevYear;
	}

	public BigDecimal getDividendIncomeCurrYear() {
		return dividendIncomeCurrYear;
	}

	public void setDividendIncomeCurrYear(BigDecimal dividendIncomeCurrYear) {
		this.dividendIncomeCurrYear = dividendIncomeCurrYear;
	}

	public BigDecimal getDividendIncomePrevYear() {
		return dividendIncomePrevYear;
	}

	public void setDividendIncomePrevYear(BigDecimal dividendIncomePrevYear) {
		this.dividendIncomePrevYear = dividendIncomePrevYear;
	}

	public BigDecimal getInvestmentIncomeCurrYear() {
		return investmentIncomeCurrYear;
	}

	public void setInvestmentIncomeCurrYear(BigDecimal investmentIncomeCurrYear) {
		this.investmentIncomeCurrYear = investmentIncomeCurrYear;
	}

	public BigDecimal getInvestmentIncomePrevYear() {
		return investmentIncomePrevYear;
	}

	public void setInvestmentIncomePrevYear(BigDecimal investmentIncomePrevYear) {
		this.investmentIncomePrevYear = investmentIncomePrevYear;
	}

	public BigDecimal getAdjustedIncomeCurrYear() {
		return adjustedIncomeCurrYear;
	}

	public void setAdjustedIncomeCurrYear(BigDecimal adjustedIncomeCurrYear) {
		this.adjustedIncomeCurrYear = adjustedIncomeCurrYear;
	}

	public BigDecimal getAdjustedIncomePrevYear() {
		return adjustedIncomePrevYear;
	}

	public void setAdjustedIncomePrevYear(BigDecimal adjustedIncomePrevYear) {
		this.adjustedIncomePrevYear = adjustedIncomePrevYear;
	}

	public Statement getBalanceSheet() {
		return balanceSheet;
	}

	public void setBalanceSheet(Statement balanceSheet) {
		this.balanceSheet = balanceSheet;
	}

	Statement incomeExpenditureStatement = new Statement();

	public Statement getIncomeExpenditureStatement() {
		return incomeExpenditureStatement;
	}

	public void setIncomeExpenditureStatement(Statement incomeExpenditureStatement) {
		this.incomeExpenditureStatement = incomeExpenditureStatement;
	}

	@Autowired
	IncomeExpenditureServiceCB incomeExpenditureService;

	@RequestMapping(value = "/newForm", method = RequestMethod.POST)
	public String cashBookForm(@ModelAttribute("cashBookReport") final CashBookReportBean cashBookReportBean,
			final Model model, HttpServletRequest request) {

		return "cashBookReport";
	}

	@RequestMapping(value = "/searchCashBookReportData", method = RequestMethod.POST, params = "search")
	public String cashBookSearchResult(@ModelAttribute("cashBookReport") final CashBookReportBean cashBookReportBean,
			final Model model, HttpServletRequest request) {
		try {
			titleName = getUlbName().toUpperCase() + " ";
			final StringBuffer recQuery = new StringBuffer(500);

			List<Object[]> recqueryList = null;
			recQuery.append("SELECT receiptdate, recvoucherno, recparticulars, paymentmode, recchequeno, recamount\n"
					+ " FROM receipt_report_view m where ")
					.append(getCashBooDateQuery(cashBookReportBean.getFromDate(), cashBookReportBean.getToDate()));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(recQuery.toString());
			recqueryList = rec.list();
			final StringBuffer payQuery = new StringBuffer(500);
			List<Object[]> payqueryList = null;
			payQuery.append("SELECT paydate, payvoucherno, payparticulars, payamount, paymentmode, paychequeno\n"
					+ "FROM payment_report_view m where ")
					.append(getpaySideCashBooDateQuery(cashBookReportBean.getFromDate(),
							cashBookReportBean.getToDate()));
			SQLQuery pay = persistenceService.getSession().createSQLQuery(payQuery.toString());
			payqueryList = pay.list();
			// populating receipt object
			if (recqueryList != null) {
				for (Object[] recObj : recqueryList) {
					CashBookReportDataBean obj = new CashBookReportDataBean();
					obj.setReceiptDate(recObj[0].toString() == null ? "" : DDMMYYYYFORMAT1.format(recObj[1]));
					obj.setRecVoucherNo(recObj[1].toString() == null ? "" : recObj[1].toString());
					obj.setRecParticulars(recObj[2].toString() == null ? "" : recObj[2].toString());
					obj.setRecLF("");
					if (recObj[3].toString().equalsIgnoreCase("cash")) {
						obj.setRecCash(recObj[5].toString());
						obj.setRecChequeNo("");
					}
					if (recObj[3].toString().equalsIgnoreCase("cheque")) {
						obj.setRecCash("");
						obj.setRecChequeNo(recObj[4].toString());
					}
					if (recObj[3].toString().equalsIgnoreCase("DD")) {
						obj.setRecCash("");
						obj.setRecChequeNo(recObj[4].toString());
					}

					obj.setRecAmount(recObj[5].toString() == null ? "" : recObj[5].toString());
					obj.setPayDate("");
					obj.setPayVoucherNo("");
					obj.setPayParticulars("");
					obj.setPayAmount("");
					obj.setPayLF("");
					obj.setPayCash("");
					obj.setPayChequeNo("");

					finalList.add(obj);
					// receiptMap.put(recObj[0].toString(),obj);
				}
			}
			// populating payment object
			if (payqueryList != null) {
				for (Object[] payObj : payqueryList) {
					CashBookReportDataBean obj = new CashBookReportDataBean();
					obj.setReceiptDate("");
					obj.setRecVoucherNo("");
					obj.setRecParticulars("");
					obj.setRecAmount("");
					obj.setRecLF("");
					obj.setRecCash("");
					obj.setRecChequeNo("");
					obj.setPayDate(payObj[0].toString() == null ? "" : DDMMYYYYFORMAT1.format(payObj[0]));
					obj.setPayVoucherNo(payObj[1].toString() == null ? "" : payObj[1].toString());
					obj.setPayParticulars(payObj[2].toString() == null ? "" : payObj[2].toString());
					obj.setPayAmount(payObj[3].toString() == null ? "" : payObj[3].toString());
					obj.setPayLF("");
					if (payObj[4].toString().equalsIgnoreCase("Cash")) {
						obj.setPayCash(payObj[3].toString() == null ? "" : payObj[3].toString());
						obj.setPayChequeNo("");
					}
					if (payObj[4].toString().equalsIgnoreCase("Cheque")) {
						obj.setPayCash("");
						obj.setPayChequeNo(payObj[5].toString() == null ? "" : payObj[5].toString());
					}
					if (payObj[4].toString().equalsIgnoreCase("DD")) {
						obj.setPayCash("");
						obj.setPayChequeNo(payObj[5].toString() == null ? "" : payObj[5].toString());
					}
					finalList.add(obj);
				}
			}
			cashBookReportBean.setCashBookResultList(finalList);
			cashBookReportBean.setTitleName(titleName + " Cash Book Report");
			cashBookReportBean
					.setHeader("Cash Book Report from " + DDMMYYYYFORMAT1.format(cashBookReportBean.getFromDate())
							+ " to " + DDMMYYYYFORMAT1.format(cashBookReportBean.getToDate()));
			model.addAttribute("cashBookReport", cashBookReportBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "cashBookReport";
	}

	private String getpaySideCashBooDateQuery(Date fromDate, Date toDate) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != fromDate)
				numDateQuery.append(" m.paydate>='").append(DDMMYYYYFORMAT1.format(fromDate)).append("'");
			if (null != toDate)
				numDateQuery.append(" and m.paydate<='").append(DDMMYYYYFORMAT1.format(toDate)).append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}

	public String getCashBooDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" m.receiptdate>='").append(DDMMYYYYFORMAT1.format(billDateFrom)).append("'");
			if (null != billDateTo)
				numDateQuery.append(" and m.receiptdate<='").append(DDMMYYYYFORMAT1.format(billDateTo)).append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}

	@RequestMapping(value = "/searchCashBookReportData", params = "exportpdf")
	public @ResponseBody void auditExportToPdf(
			@ModelAttribute("cashBookReport") final CashBookReportBean cashBookReportBean, final Model model,
			final HttpServletRequest request) throws IOException, JRException {

		try {

			final Map<String, Object> reportParams = new HashMap<String, Object>();
			final StringBuffer recQuery = new StringBuffer(500);
			List<CashBookReportDataBean> finalList = new ArrayList<CashBookReportDataBean>();
			List<Object[]> recqueryList = null;
			recQuery.append("SELECT receiptdate, recvoucherno, recparticulars, paymentmode, recchequeno, recamount\n"
					+ " FROM receipt_report_view m where ")
					.append(getCashBooDateQuery(cashBookReportBean.getFromDate(), cashBookReportBean.getToDate()));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(recQuery.toString());
			recqueryList = rec.list();
			final StringBuffer payQuery = new StringBuffer(500);
			List<Object[]> payqueryList = null;
			payQuery.append("SELECT paydate, payvoucherno, payparticulars, payamount, paymentmode, paychequeno\n"
					+ "FROM payment_report_view m where ")
					.append(getpaySideCashBooDateQuery(cashBookReportBean.getFromDate(),
							cashBookReportBean.getToDate()));
			SQLQuery pay = persistenceService.getSession().createSQLQuery(payQuery.toString());
			payqueryList = pay.list();
			// populating receipt object
			if (recqueryList != null) {
				for (Object[] recObj : recqueryList) {
					CashBookReportDataBean obj = new CashBookReportDataBean();
					obj.setReceiptDate(recObj[0].toString() == null ? "" : DDMMYYYYFORMAT1.format(recObj[1]));
					obj.setRecVoucherNo(recObj[1].toString() == null ? "" : recObj[1].toString());
					obj.setRecParticulars(recObj[2].toString() == null ? "" : recObj[2].toString());
					// obj.setRecLF(recObj[3].toString() == null ? "" : recObj[3].toString());
					if (recObj[3].toString().equalsIgnoreCase("cash")) {
						obj.setRecCash(recObj[5].toString());
					}
					if (recObj[3].toString().equalsIgnoreCase("cheque")) {
						obj.setRecChequeNo(recObj[4].toString());
					}
					if (recObj[3].toString().equalsIgnoreCase("DD")) {
						obj.setRecChequeNo(recObj[4].toString());
					}

					obj.setRecAmount(recObj[5].toString() == null ? "" : recObj[5].toString());
					finalList.add(obj);
				}
			}
			// populating payment object
			if (payqueryList != null) {
				for (Object[] payObj : payqueryList) {
					CashBookReportDataBean obj = new CashBookReportDataBean();
					obj.setPayDate(payObj[0].toString() == null ? "" : DDMMYYYYFORMAT1.format(payObj[0]));
					obj.setPayVoucherNo(payObj[1].toString() == null ? "" : payObj[1].toString());
					obj.setPayParticulars(payObj[2].toString() == null ? "" : payObj[2].toString());
					obj.setPayAmount(payObj[3].toString() == null ? "" : payObj[3].toString());
					if (payObj[4].toString().equalsIgnoreCase("Cash")) {
						obj.setPayCash(payObj[3].toString() == null ? "" : payObj[3].toString());
					}
					if (payObj[4].toString().equalsIgnoreCase("Cheque")) {
						obj.setPayChequeNo(payObj[5].toString() == null ? "" : payObj[5].toString());
					}
					if (payObj[4].toString().equalsIgnoreCase("DD")) {
						obj.setPayChequeNo(payObj[5].toString() == null ? "" : payObj[5].toString());
					}
					finalList.add(obj);
				}
			}
			titleName = getUlbName().toUpperCase() + " ";
			cashBookReportBean.setTitleName(titleName + " Cash Book Report");
			cashBookReportBean
					.setHeader("Cash Book Report from " + DDMMYYYYFORMAT1.format(cashBookReportBean.getFromDate())
							+ " to " + DDMMYYYYFORMAT1.format(cashBookReportBean.getToDate()));
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
			response.setHeader("Content-Disposition", "attachment; filename=CashBookReport.pdf");
			ServletOutputStream out = response.getOutputStream();
			JasperPrint pp = createPdfReport(finalList, cashBookReportBean);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(pp, baos);
			out.write(baos.toByteArray());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JRBeanCollectionDataSource getDataSource(List<CashBookReportDataBean> subLedgerList) {
		return new JRBeanCollectionDataSource(subLedgerList);
	}

	private JasperPrint createPdfReport(final List<CashBookReportDataBean> auditList,
			CashBookReportBean cashBookReportBean) throws JRException, IOException {
		final InputStream stream = this.getClass().getResourceAsStream("/reports/templates/cashBook.jrxml");
		final JasperReport report = JasperCompileManager.compileReport(stream);
		final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(auditList);
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("cashBookDataSource", getDataSource(auditList));
		parameters.put("nameOfUlb", cashBookReportBean.getTitleName());
		parameters.put("header", cashBookReportBean.getHeader());
		parameters.put("receiptSideTotal", "Total : " + getReceiptSideTotal(cashBookReportBean) + " ");
		parameters.put("paymentSideTotal", "Total : " + getPaymentSideTotal(cashBookReportBean) + " ");
		final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);
		return print;
	}

	private BigDecimal getPaymentSideTotal(CashBookReportBean cashBookReportBean) {

		BigDecimal payTotal = new BigDecimal(0);
		final StringBuffer payQuery = new StringBuffer(500);
		List<BigDecimal> payqueryList = null;
		try {
			payQuery.append("SELECT sum(payamount) FROM payment_report_view m where ").append(
					getpaySideCashBooDateQuery(cashBookReportBean.getFromDate(), cashBookReportBean.getToDate()));
			SQLQuery pay = persistenceService.getSession().createSQLQuery(payQuery.toString());
			payqueryList = pay.list();
			if (payqueryList != null) {
				payTotal = payqueryList.get(0);
			}
			if (payTotal == null) {
				payTotal = new BigDecimal(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payTotal;
	}

	private BigDecimal getReceiptSideTotal(CashBookReportBean cashBookReportBean) {
		BigDecimal receiptTotal = new BigDecimal(0);
		final StringBuffer recQuery = new StringBuffer(500);

		List<BigDecimal> recqueryList = null;

		try {
			recQuery.append("SELECT sum(recamount) from receipt_report_view m where ")
					.append(getCashBooDateQuery(cashBookReportBean.getFromDate(), cashBookReportBean.getToDate()));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(recQuery.toString());
			recqueryList = rec.list();
			if (recqueryList != null) {
				receiptTotal = recqueryList.get(0);
			}
			if (receiptTotal == null) {
				receiptTotal = new BigDecimal(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiptTotal;
	}

	@RequestMapping(value = "/cashFlow/newForm", method = RequestMethod.POST)
	public String cashFlowNewForm(@ModelAttribute("cashFlowReport") final CashBookReportBean cashBookReportBean,
			final Model model, HttpServletRequest request) {
		cashBookReportBean.setFlag("block");
		model.addAttribute("cashFlowResult", cashBookReportBean);
		return "cashFlowReport";
	}

	@RequestMapping(value = "/cashFlow/searchCashFlowReportData", method = RequestMethod.POST, params = "search")
	public String cashFlowSearchResult(@ModelAttribute("cashFlowReport") final CashBookReportBean cashBookReportBean,
			final Model model, HttpServletRequest request) {
		try {
			List<CashFlowReportDataBean> lst1 = populateIncomeExpenditureDataSource(cashBookReportBean.getToDate(),
					cashBookReportBean.getFromDate());
			List<CashFlowReportDataBean> balanceSheetLNow = new ArrayList<CashFlowReportDataBean>();
			List<CashFlowReportDataBean> balanceSheetLPrev = new ArrayList<CashFlowReportDataBean>();
			List<CashFlowReportDataBean> finalBalanceSheetL = new ArrayList<CashFlowReportDataBean>();
			// for balance sheet
			Date prevFromDate = new Date(cashBookReportBean.getFromDate().getTime() - MILLIS_IN_A_YEAR);
			//Date prevToDate = new Date(cashBookReportBean.getToDate().getTime() - MILLIS_IN_A_YEAR);
			Date prevToDate = new Date(cashBookReportBean.getFromDate().getTime() - MILLIS_IN_A_DAY);
			// get previous year

			balanceSheetLNow = populateDataSource(cashBookReportBean.getToDate(), cashBookReportBean.getFromDate(),
					"current");
			balanceSheetLPrev = populateDataSource(prevToDate, prevFromDate, "prev");
			finalBalanceSheetL = balanceSheetService.getFinalVBalanceSheetList(balanceSheetLNow, balanceSheetLPrev);
			cashBookReportBean.setaCurrentYear(balanceSheetService.getACurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setaPrevYear(balanceSheetService.getAPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbCurrentYear(balanceSheetService.getbCurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbPrevYear(balanceSheetService.getbPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setcCurrentYear(new BigDecimal(0));
			cashBookReportBean.setcPrevYear(new BigDecimal(0));
			cashBookReportBean.setAbcCurrentYear(balanceSheetService.getabcCurrentYear(cashBookReportBean));
			cashBookReportBean.setAbcPrevYear(balanceSheetService.getabcPreviousYear(cashBookReportBean));
			cashBookReportBean.setAtEndCurr(balanceSheetService.getAtEndCurrentYear(finalBalanceSheetL,cashBookReportBean));
			cashBookReportBean.setAtBeginingCurr(finalBalanceSheetL.get(0).getAtBeginingPeriodCurrYear());
			cashBookReportBean.setAtBeginingPrev(finalBalanceSheetL.get(0).getAtBeginingPeriodPrevYear());
			cashBookReportBean.setAtEndPrev(balanceSheetService.getatendPrevYear(finalBalanceSheetL,cashBookReportBean));
			cashBookReportBean.setFinalBalanceSheetL(finalBalanceSheetL);
			cashBookReportBean.setCashFlowResultList(lst1);
			titleName = getUlbName().toUpperCase() + " ";
			cashBookReportBean.setTitleName(titleName + " Cash Flow Report");
			cashBookReportBean
					.setHeader("Cash Flow Report from " + DDMMYYYYFORMAT1.format(cashBookReportBean.getFromDate())
							+ " to " + DDMMYYYYFORMAT1.format(cashBookReportBean.getToDate()));
			cashBookReportBean.setFlag("show");
			model.addAttribute("cashFlowResult", cashBookReportBean);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "cashFlowReport";
	}

	private List<CashFlowReportDataBean> populateIncomeExpenditureDataSource(Date toDate, Date fromDate) {
		incomeExpenditureStatement.setToDate(toDate);
		incomeExpenditureStatement.setFromDate(fromDate);
		incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
		// System.out.println("FUND
		// SIZE:::::::-----"+incomeExpenditureService.getFunds().size());
		incomeExpenditureService.populateIEStatement(incomeExpenditureStatement);
		System.out.println("### hope for the best");
		System.out.println(incomeExpenditureStatement.getIeEntries());
		List<CashFlowReportDataBean> lst1 = new ArrayList<CashFlowReportDataBean>();
		List<IEStatementEntry> resultEntries = incomeExpenditureStatement.getIeEntries();
		CashFlowReportDataBean obj = new CashFlowReportDataBean();
		BigDecimal totalIncomeCurrentYear = new BigDecimal(0);
		BigDecimal totalIncomePrevYear = new BigDecimal(0);
		BigDecimal totalExpenseCurrYear = new BigDecimal(0);
		BigDecimal totalExpensePrevYear = new BigDecimal(0);
		for (IEStatementEntry ieStatementEntry : resultEntries) {
			BigDecimal thisYearAmount = new BigDecimal(0);
			BigDecimal prevYearAmount = new BigDecimal(0);
			
			Map<String, BigDecimal> thisYearAmountMap = new HashMap<String, BigDecimal>();
			Map<String, BigDecimal> prevYearAmountMap = new HashMap<String, BigDecimal>();
			thisYearAmountMap = ieStatementEntry.getNetAmount();
			prevYearAmountMap = ieStatementEntry.getPreviousYearAmount();
			for (Map.Entry<String, BigDecimal> pair : thisYearAmountMap.entrySet()) {
				System.out.println(
						String.format("Key (name) is: %s, Value (age) is : %s", pair.getKey(), pair.getValue()));
				thisYearAmount = pair.getValue();
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("A")) {
					totalIncomeCurrentYear = (thisYearAmount == null ? new BigDecimal(0) : thisYearAmount);

				}
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("B")) {
					totalExpenseCurrYear = (thisYearAmount == null ? new BigDecimal(0) : thisYearAmount);

				}
				obj.setIncomeOverExpenditureCurr(totalIncomeCurrentYear.subtract(totalExpenseCurrYear));
				incomeOverExpenditureCurr = totalIncomeCurrentYear.subtract(totalExpenseCurrYear);
				/*
				 * if (ieStatementEntry.getGlCode().equalsIgnoreCase("A-B")) {
				 * obj.setIncomeOverExpenditureCurr(thisYearAmount); incomeOverExpenditureCurr =
				 * thisYearAmount; }
				 */
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("272")) {
					obj.setDepreciationCurr(thisYearAmount);
					depreciationCurr = thisYearAmount;
				}
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("240")) {
					obj.setInterestFinanceChargesCurrYear(thisYearAmount);
					interestFinanceChargesCurrYear = thisYearAmount;
					obj.setInterestIncomeReceivedCurrYear(thisYearAmount);
					interestIncomeReceivedCurrYear = thisYearAmount;
				}
				obj.setProfitOnDisposalOfAssetsCurrYear(null);
				profitOnDisposalOfAssetsCurrYear = new BigDecimal(0);
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("171")) {
					obj.setInvestmentIncomeCurrYear(thisYearAmount);
					investmentIncomeCurrYear = thisYearAmount;
					obj.setInvestmentIncomeReceivedCurrYear(thisYearAmount);
					investmentIncomeReceivedCurrYear = thisYearAmount;
				}
				obj.setDividendIncomeCurrYear(null);
				dividendIncomeCurrYear = new BigDecimal(0);
				adjustedIncomeCurrYear = incomeOverExpenditureCurr.add(depreciationCurr)
						.add(interestFinanceChargesCurrYear).subtract(profitOnDisposalOfAssetsCurrYear)
						.subtract(dividendIncomeCurrYear).subtract(investmentIncomeCurrYear);
				obj.setAdjustedIncomeCurrYear(adjustedIncomeCurrYear);

			}
			for (Map.Entry<String, BigDecimal> pair : prevYearAmountMap.entrySet()) {
				System.out.println(
						String.format("Key (name) is: %s, Value (age) is : %s", pair.getKey(), pair.getValue()));
				prevYearAmount = pair.getValue();

				if (ieStatementEntry.getGlCode().equalsIgnoreCase("A")) {

					totalIncomePrevYear = (prevYearAmount == null ? new BigDecimal(0) : prevYearAmount);
				}
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("B")) {

					totalExpensePrevYear = (prevYearAmount == null ? new BigDecimal(0) : prevYearAmount);
				}
				obj.setIncomeOverExpenditurePrevYear(totalIncomePrevYear.subtract(totalExpensePrevYear));
				incomeOverExpenditurePrevYear = totalIncomePrevYear.subtract(totalExpensePrevYear);
				/*
				 * if (ieStatementEntry.getGlCode().equalsIgnoreCase("A-B")) {
				 * obj.setIncomeOverExpenditurePrevYear(prevYearAmount);
				 * incomeOverExpenditurePrevYear = prevYearAmount;
				 * 
				 * }
				 */
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("272")) {
					obj.setDepreciationPrevYear(prevYearAmount);
					depreciationPrevYear = prevYearAmount;
				}
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("240")) {
					obj.setInterestFinanceChargesPrevYear(prevYearAmount);
					interestFinanceChargesPrevYear = prevYearAmount;
					obj.setInterestIncomeReceivedPrevYear(prevYearAmount);
					interestIncomeReceivedPrevYear = prevYearAmount;
				}
				obj.setProfitOnDisposalOfAssetsPrevYear(null);
				profitOnDisposalOfAssetsPrevYear = new BigDecimal(0);
				if (ieStatementEntry.getGlCode().equalsIgnoreCase("171")) {
					obj.setInvestmentIncomePrevYear(prevYearAmount);
					investmentIncomePrevYear = prevYearAmount;
					obj.setInterestIncomeReceivedPrevYear(prevYearAmount);
					interestIncomeReceivedPrevYear = prevYearAmount;
				}
				obj.setDividendIncomePrevYear(null);
				dividendIncomePrevYear = new BigDecimal(0);
				adjustedIncomePrevYear = incomeOverExpenditurePrevYear.add(depreciationPrevYear)
						.add(interestFinanceChargesPrevYear).subtract(profitOnDisposalOfAssetsPrevYear)
						.subtract(dividendIncomePrevYear).subtract(investmentIncomePrevYear);
				obj.setAdjustedIncomePrevYear(adjustedIncomePrevYear);
			}
		}
		lst1.add(obj);
		return lst1;
	}

	public List<CashFlowReportDataBean> getCashFlowFinalList() {
		return cashFlowFinalList;
	}

	public void setCashFlowFinalList(List<CashFlowReportDataBean> cashFlowFinalList) {
		this.cashFlowFinalList = cashFlowFinalList;
	}

	protected List<CashFlowReportDataBean> populateDataSource(Date toDate, Date fromDate, String yearType) {
		List<CashFlowReportDataBean> lst1 = new ArrayList<CashFlowReportDataBean>();
		fixedAssetDebitAmt = balanceSheetService.getFixedAssetDEbitAmount("410", fromDate, toDate);
		fixedAssetCreditAmt = balanceSheetService.getFixedAssetCreditAmount("410", fromDate, toDate);
		investmentDebitAmt = balanceSheetService.getInvestmentDEbitAmount("420", fromDate, toDate);
		investmentCreditAmt = balanceSheetService.getInvestmentCreditAmount("420", fromDate, toDate);
		balanceSheet = new Statement();
		balanceSheet.setToDate(toDate);
		balanceSheet.setFromDate(fromDate);
		balanceSheet.setFunds(balanceSheetService.getFunds());
		balanceSheetService.populateBalanceSheet(balanceSheet);
		List<StatementEntry> resultEntries = new ArrayList<StatementEntry>();
		List<StatementEntry> ieStatementEntry = new ArrayList<StatementEntry>();
		resultEntries = balanceSheet.getEntries();
		CashFlowReportDataBean obj = new CashFlowReportDataBean();
		ieStatementEntry = resultEntries;
		// for (StatementEntry ieStatementEntry : resultEntries) {
		BigDecimal otherCurrentAssetsCurrYear1 = new BigDecimal(0);
		BigDecimal otherCurrentAssetsCurrYear2 = new BigDecimal(0);
		BigDecimal otherCurrentAssetsPrevYear1 = new BigDecimal(0);
		BigDecimal otherCurrentAssetsPrevYear2 = new BigDecimal(0);
		BigDecimal thisYearAmount = new BigDecimal(0);
		BigDecimal prevYearAmount = new BigDecimal(0);
		for (int i = 0; i < resultEntries.size(); i++) {
			System.out.println("##year::" + yearType + "### glcode::" + ieStatementEntry.get(i).getGlCode() + "::"
					+ ieStatementEntry.get(i).getCurrentYearTotal() + "::"
					+ ieStatementEntry.get(i).getPreviousYearTotal());
			LOGGER.info("##year::" + yearType + "### glcode::" + ieStatementEntry.get(i).getGlCode() + "::"
					+ ieStatementEntry.get(i).getCurrentYearTotal() + "::"
					+ ieStatementEntry.get(i).getPreviousYearTotal());

			if (ieStatementEntry != null && ieStatementEntry.get(i).getGlCode() != null) {
				if(ieStatementEntry.get(i).getCurrentYearTotal() == null) {
					thisYearAmount = new BigDecimal(0);
					
				}else {
					thisYearAmount = ieStatementEntry.get(i).getCurrentYearTotal();
				}
				if(ieStatementEntry.get(i).getPreviousYearTotal() == null) {
					prevYearAmount = new BigDecimal(0);
				}else {
					prevYearAmount = ieStatementEntry.get(i).getPreviousYearTotal();
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("431")) {
					obj.setSundryDebtorsCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("430")) {
					obj.setStockInhandCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("440")) {
					obj.setPrepaidExpenseCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("460")) {
					otherCurrentAssetsCurrYear1 = thisYearAmount;
				}
				if(ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("470")) {
					otherCurrentAssetsCurrYear2 = thisYearAmount;

				}
				obj.setOtherCurrentAssetsCurrYear(otherCurrentAssetsCurrYear1.add(otherCurrentAssetsCurrYear2));
				LOGGER.info("460 ::otherCurrentAssetsCurrYear1 ::"+otherCurrentAssetsCurrYear1);
				LOGGER.info("470 :: otherCurrentAssetsCurrYear2 ::"+otherCurrentAssetsCurrYear2);
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("340")) {
					obj.setDepositsReceivedCurrYear(thisYearAmount);

				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("341")) {
					obj.setDepositWorksCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("350")) {
					obj.setOtherCurrentLiabilitiesCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("360")) {

					obj.setIncreaseInProvisionCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("320")) {
					obj.setSpecialFundsGrantsCurrYear(thisYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("410")) {
					try {

						if (yearType.equalsIgnoreCase("current")) {
							obj.setCwipCurrYear(fixedAssetDebitAmt);
							obj.setDisposalOfAssetsCurrYear(fixedAssetCreditAmt);
						} else {
							obj.setCwipPrevYear(fixedAssetDebitAmt);
							obj.setDisposalOfAssetsPrevYear(fixedAssetCreditAmt);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("420")) {
					try {

						if (yearType.equalsIgnoreCase("current")) {
							obj.setDisposalOfInvestmentsCurrYear(investmentCreditAmt);
							obj.setInvestmentsCurrYear(investmentDebitAmt);
						} else {
							obj.setDisposalOfInvestmentsPrevYear(investmentCreditAmt);
							obj.setInvestmentsCurrYear(investmentDebitAmt);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// }
				// for previous year
				/*
				 * for (Map.Entry<String, BigDecimal> pair : prevYearAmountMap.entrySet()) {
				 * System.out.println( String.format("Key (name) is: %s, Value (age) is : %s",
				 * pair.getKey(), pair.getValue())); prevYearAmount = pair.getValue();
				 */

				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("431")) {
					obj.setSundryDebtorsPrevYear(prevYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("430")) {
					obj.setStockInhandPrevYear(prevYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("440")) {
					obj.setPrepaidExpensePrevYear(prevYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("460")) {
					otherCurrentAssetsPrevYear1 = prevYearAmount;
				}
				if(ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("470")) {
					
					otherCurrentAssetsPrevYear2 = prevYearAmount;
				}
				obj.setOtherCurrentAssetsPrevYear(otherCurrentAssetsPrevYear1.add(otherCurrentAssetsPrevYear2));
				LOGGER.info("460 ::otherCurrentAssetsPrevYear1 ::"+otherCurrentAssetsPrevYear1);
				LOGGER.info("470 :: otherCurrentAssetsPrevYear2 ::"+otherCurrentAssetsPrevYear2);
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("340")) {
					obj.setDepositsReceivedPrevYear(prevYearAmount);

				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("341")) {
					obj.setDepositWorksPrevYear(prevYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("350")) {
					obj.setOtherCurrentLiabilitiesPrevYear(prevYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("360")) {

					obj.setIncreaseInProvisionPrevYear(prevYearAmount);
				}
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("320")) {
					obj.setSpecialFundsGrantsPrevYear(prevYearAmount);
				}

				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("450")) {
					if (yearType.equalsIgnoreCase("current")) {
						obj.setAtBeginingPeriodCurrYear(prevYearAmount);
						obj.setAtEndPeriodCurrYear(thisYearAmount);
					}
					if (yearType.equalsIgnoreCase("prev")) {
						obj.setAtBeginingPeriodPrevYear(prevYearAmount);
						obj.setAtEndPeriodPrevYear(thisYearAmount);
					}
				}
			}
		}
		lst1.add(obj);
		return lst1;
	}

	public BalanceSheetServiceCB getBalanceSheetService() {
		return balanceSheetService;
	}

	public void setBalanceSheetService(BalanceSheetServiceCB balanceSheetService) {
		this.balanceSheetService = balanceSheetService;
	}

	public IncomeExpenditureServiceCB getIncomeExpenditureService() {
		return incomeExpenditureService;
	}

	public void setIncomeExpenditureService(IncomeExpenditureServiceCB incomeExpenditureService) {
		this.incomeExpenditureService = incomeExpenditureService;
	}

	@RequestMapping(value = "/cashFlow/searchCashFlowReportData", params = "exportpdf")
	//@RequestMapping(value = "/cashFlow/searchCashFlowReportExportData")
	public @ResponseBody void cashFlowExportToPdf(
			@ModelAttribute("cashFlowReport") final CashBookReportBean cashBookReportBean, final Model model,
			final HttpServletRequest request) throws IOException, JRException {
		try {
			/*
			 * cashBookReportBean.setFromDate(fromDate);
			 * cashBookReportBean.setToDate(toDate);
			 */
			List<CashFlowReportDataBean> lst1 = populateIncomeExpenditureDataSource(cashBookReportBean.getToDate(),
					cashBookReportBean.getFromDate());
			List<CashFlowReportDataBean> balanceSheetLNow = new ArrayList<CashFlowReportDataBean>();
			List<CashFlowReportDataBean> balanceSheetLPrev = new ArrayList<CashFlowReportDataBean>();
			List<CashFlowReportDataBean> finalBalanceSheetL = new ArrayList<CashFlowReportDataBean>();
			// for balance sheet
			Date prevFromDate = new Date(cashBookReportBean.getFromDate().getTime() - MILLIS_IN_A_YEAR);
			//Date prevToDate = new Date(cashBookReportBean.getToDate().getTime() - MILLIS_IN_A_YEAR);
			Date prevToDate = new Date(cashBookReportBean.getToDate().getTime() - MILLIS_IN_A_DAY);
			// get previous year

			balanceSheetLNow = populateDataSource(cashBookReportBean.getToDate(), cashBookReportBean.getFromDate(),
					"current");
			
			balanceSheetLPrev = populateDataSource(prevToDate, prevFromDate, "prev");
			finalBalanceSheetL = balanceSheetService.getFinalVBalanceSheetList(balanceSheetLNow, balanceSheetLPrev);
			cashBookReportBean.setaCurrentYear(balanceSheetService.getACurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setaPrevYear(balanceSheetService.getAPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbCurrentYear(balanceSheetService.getbCurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbPrevYear(balanceSheetService.getbPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setcCurrentYear(new BigDecimal(0));
			cashBookReportBean.setcPrevYear(new BigDecimal(0));
			cashBookReportBean.setAbcCurrentYear(balanceSheetService.getabcCurrentYear(cashBookReportBean));
			cashBookReportBean.setAbcPrevYear(balanceSheetService.getabcPreviousYear(cashBookReportBean));
			cashBookReportBean.setAtEndCurr(balanceSheetService.getAtEndCurrentYear(finalBalanceSheetL,cashBookReportBean));
			cashBookReportBean.setAtBeginingCurr(finalBalanceSheetL.get(0).getAtBeginingPeriodCurrYear());
			cashBookReportBean.setAtBeginingPrev(finalBalanceSheetL.get(0).getAtBeginingPeriodPrevYear());
			cashBookReportBean.setAtEndPrev(balanceSheetService.getatendPrevYear(finalBalanceSheetL,cashBookReportBean));
			titleName = getUlbName().toUpperCase() + " ";
			cashBookReportBean.setTitleName(titleName + " Cash Flow Report");
			cashBookReportBean
					.setHeader("Cash Flow Report from " + DDMMYYYYFORMAT1.format(cashBookReportBean.getFromDate())
							+ " to " + DDMMYYYYFORMAT1.format(cashBookReportBean.getToDate()));
			Map<String, Object> reportParams = new HashMap<String, Object>();
			reportParams = balanceSheetService.prepareMapForCashFlowReport(lst1, finalBalanceSheetL,
					cashBookReportBean);
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
			response.setHeader("Content-Disposition", "attachment; filename=CashFlowReport.pdf");
			ServletOutputStream out = response.getOutputStream();
			JasperPrint pp = balanceSheetService.createCashFlowPdfReport(reportParams, cashBookReportBean,
					finalBalanceSheetL);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(pp, baos);
			out.write(baos.toByteArray());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getUlbName() {
		return ReportUtil.getCityName() + " " + (cityService.getCityGrade() == null ? "" : cityService.getCityGrade());
	}
}
