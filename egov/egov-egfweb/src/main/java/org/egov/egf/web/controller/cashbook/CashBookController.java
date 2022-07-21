package org.egov.egf.web.controller.cashbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.egov.collection.entity.MisReceiptDetail;
import org.egov.commons.Bankaccount;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.model.BankBookEntry;
import org.egov.egf.model.BankBookViewEntry;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.services.report.BalanceSheetService;
import org.egov.egf.web.controller.cashbook.*;

import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StandardBasicTypes;
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
	@Autowired
	private FinancialYearDAO financialYearDAO;
	@Autowired
	private AppConfigValueService appConfigValuesService;
	private Vouchermis vouchermis = new Vouchermis();

	public Vouchermis getVouchermis() {
		return vouchermis;
	}

	public void setVouchermis(Vouchermis vouchermis) {
		this.vouchermis = vouchermis;
	}

	private List<BankBookEntry> bankBookEntries = new ArrayList<BankBookEntry>();
	private static final String EMPTY_STRING = "";
	private static final String PAYMENT = "Payment";
	private static final String RECEIPT = "Receipt";
	private static final String SURRENDERED = "Surrendered";
	private String queryFrom = "";
	private Fund fundId = new Fund();
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	private static final long MILLIS_IN_A_YEAR = (long) 1000 * 60 * 60 * 24 * 365;
	private static final long MILLIS_IN_A_DAY = (long) 1000 * 60 * 60 * 24;
	private static final Logger LOGGER = Logger.getLogger(CashBookController.class);
	private List<MisReceiptDetail> misReceiptDetails = new ArrayList<MisReceiptDetail>();
	private List<MisRemittanceDetails> misRemittanceDetails = new ArrayList<MisRemittanceDetails>();
	List<CashBookReportDataBean> finalList = new ArrayList<CashBookReportDataBean>();
	List<CashFlowReportDataBean> cashFlowFinalList = new ArrayList<CashFlowReportDataBean>();
	Statement balanceSheet = new Statement();
	@Autowired
	private BalanceSheetServiceCB balanceSheetService;
	@Autowired
	private BalanceSheetService balancesheetServiceOld;
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
	private Date todayDate;
	private Date startDate = new Date();
	private Date endDate = new Date();
	private String getInstrumentsByVoucherIdsQuery = "";
	private Map<Long, List<Object[]>> voucherIdAndInstrumentMap = new HashMap<Long, List<Object[]>>();
	private Map<Long, List<Object[]>> InstrumentHeaderIdsAndInstrumentVouchersMap = new HashMap<Long, List<Object[]>>();
	private List<CashBookViewEntry> bankBookViewEntries = new ArrayList<CashBookViewEntry>();
	private boolean isCreditOpeningBalance = false;
	@Autowired
	private EgovCommon egovCommon;

	private String chequeStatus = EMPTY_STRING;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private String voucherStr = "";
	String voucherNumber = EMPTY_STRING;
	String chequeNumber = "";
	/*
	 * BigDecimal receiptTotal = BigDecimal.ZERO; BigDecimal paymentTotal =
	 * BigDecimal.ZERO; BigDecimal initialBalance = new BigDecimal(0);
	 */
	List<BankBookEntry> entries = new ArrayList<BankBookEntry>();
	private Bankaccount bankAccount;

	public EgovCommon getEgovCommon() {
		return egovCommon;
	}

	public void setEgovCommon(final EgovCommon egovCommon) {
		this.egovCommon = egovCommon;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Autowired
	public MicroserviceUtils microserviceUtils;
	@Autowired
	@Qualifier("persistenceService")
	protected transient PersistenceService persistenceService1;
	private StringBuffer header = new StringBuffer();
	public static final SimpleDateFormat DDMMYYYYFORMATS = new SimpleDateFormat("dd/MM/yyyy", LOCALE);

	public PersistenceService getPersistenceService1() {
		return persistenceService1;
	}

	public void setPersistenceService1(PersistenceService persistenceService1) {
		this.persistenceService1 = persistenceService1;
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

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
			String errMsg = null;

			titleName = getUlbName().toUpperCase() + " ";
			startDate = cashBookReportBean.getFromDate();
			endDate = cashBookReportBean.getToDate();
			CFinancialYear financialYear = financialYearDAO.getFinYearByDate(startDate);
			Date endingDate = financialYear.getEndingDate();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String endFormat = formatter.format(endDate);
			String endFormat1 = formatter.format(endingDate);
			if (endFormat.compareTo(endFormat1) > 0) {
				errMsg = "End date should be within a financial year";

			}
			setTodayDate(new Date());
			List<Bankaccount> bankAccountL = new ArrayList<Bankaccount>();
			/*
			 * List<Object[]> objs = persistenceService.getSession().createQuery(
			 * "select DISTINCT(b.chartofaccounts.glcode) as glcode,b.fund.id as fundId,f.code as fundcode "
			 * +
			 * "from Bankaccount b,Fund f where b.fund.id = f.id group by b.fund.id,b.chartofaccounts.glcode,f.code"
			 * ) .list();
			 */
			List<Object[]> objs = persistenceService.getSession().createQuery(
					"select DISTINCT concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),bankBranch.branchname) as bankbranchname, "
							+ " c.glcode as glcode ,bankaccount.fund.id as fundId, f.code as code"
							+ " FROM Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,CChartOfAccounts c ,Fund f  "
							+ " where  bank.isactive=true  and bankBranch.isactive=true and bank.id = bankBranch.bank.id and bankBranch.id = bankaccount.bankbranch.id "
							+ " and bankaccount.chartofaccounts.id = c.id and bankaccount.fund.id  = f.id"
							+ " and bankaccount.isactive=true ")
					.list();
			for (Object[] obj : objs) {
				Fund fund = new Fund();
				fund.setId(Integer.parseInt(obj[3].toString()));
				fund.setCode(obj[4].toString());
				CChartOfAccounts c = new CChartOfAccounts();
				c.setGlcode(obj[2].toString());
				Bankaccount b = new Bankaccount();
				b.setChartofaccounts(c);
				b.setFund(fund);
				bankAccountL.add(b);
			}
			for (Bankaccount bAcc : bankAccountL) {
				List<BankBookEntry> results = new ArrayList<BankBookEntry>();
				results = getResults(bAcc.getChartofaccounts().getGlcode());
				addRowsToBankBookEntries(results, bAcc.getChartofaccounts().getGlcode(), bAcc.getFund().getCode());

			}
			prepareViewObject();

		} catch (Exception e) {
			e.printStackTrace();
		}

		cashBookReportBean.setCashBookResultList(bankBookViewEntries);
		cashBookReportBean.setTitleName(titleName + " Cash Book Report");
		cashBookReportBean.setHeader("Cash Book Report from " + DDMMYYYYFORMAT1.format(cashBookReportBean.getFromDate())
				+ " to " + DDMMYYYYFORMAT1.format(cashBookReportBean.getToDate()));

		model.addAttribute("cashBookReport", cashBookReportBean);

		return "cashBookReport";
	}

	private void addRowsToBankBookEntries(List<BankBookEntry> results, String glCodeParam, String fundCodePAram) {
		Map<String, BankBookEntry> voucherNumberAndEntryMap = new HashMap<String, BankBookEntry>();
		List<String> multipleChequeVoucherNumber = new ArrayList<String>();
		List<BankBookEntry> rowsToBeRemoved = new ArrayList<BankBookEntry>();
		List<BankBookEntry> bankBookEntriesIndiuvidual = new ArrayList<BankBookEntry>();
		Iterator<BankBookEntry> itr = results.iterator();
		while (itr.hasNext()) {
			// for (BankBookEntry row : results) {
			BankBookEntry row = itr.next();
			if (row.getType().equalsIgnoreCase(RECEIPT))
				row.setType(RECEIPT);
			else
				row.setType(PAYMENT);
			boolean shouldAddRow = true;
			if (voucherNumberAndEntryMap.containsKey(row.getVoucherNumber())) {
				if (SURRENDERED.equalsIgnoreCase(row.getInstrumentStatus())
						|| FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS
								.equalsIgnoreCase(row.getInstrumentStatus()))
					shouldAddRow = false;
				else {
					final BankBookEntry entryInMap = voucherNumberAndEntryMap.get(row.getVoucherNumber());
					if ((SURRENDERED.equalsIgnoreCase(entryInMap.getInstrumentStatus())
							|| FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS
									.equalsIgnoreCase(entryInMap.getInstrumentStatus()))
							&& (!SURRENDERED.equalsIgnoreCase(row.getInstrumentStatus())
									|| !FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS
											.equalsIgnoreCase(row.getInstrumentStatus()))) {
						rowsToBeRemoved.add(entryInMap);
						voucherNumberAndEntryMap.put(row.getVoucherNumber(), row);
					} else if (row.getVoucherDate().compareTo(entryInMap.getVoucherDate()) == 0
							&& row.getParticulars().equalsIgnoreCase(entryInMap.getParticulars())
							&& row.getAmount().equals(entryInMap.getAmount())
							&& !SURRENDERED.equalsIgnoreCase(entryInMap.getInstrumentStatus())) {
						multipleChequeVoucherNumber.add(row.getVoucherNumber());
						shouldAddRow = false;
					} else
						shouldAddRow = true;
				}
			} else
				voucherNumberAndEntryMap.put(row.getVoucherNumber(), row);
			if (shouldAddRow)
				bankBookEntriesIndiuvidual.add(row);
			bankBookEntries.add(row);
		}
		if (!bankBookEntriesIndiuvidual.isEmpty()) {
			computeTotals(bankBookEntriesIndiuvidual, glCodeParam, fundCodePAram, multipleChequeVoucherNumber,
					rowsToBeRemoved);

		}

	}

	private void getInstrumentsByVoucherIds() {
		String mainQuery = "";
		queryFrom = " FROM VOUCHERHEADER vh";
		mainQuery = "SELECT vh2.id,ih2.instrumentnumber,es2.code,ih2.id as instrumentHeaderId ,ih2.instrumentdate, ih2.transactionnumber, ih2.transactiondate";
		getInstrumentsByVoucherIdsQuery = " FROM VOUCHERHEADER vh2,egf_instrumentvoucher iv2 ,egf_instrumentheader ih2 ,egw_status es2 WHERE vh2.id = iv2.voucherheaderid AND iv2.instrumentheaderid=ih2.id"
				+ " AND ih2.id_status = es2.id AND vh2.id in (select vh.id as vhId" + queryFrom + ")";
		mainQuery = mainQuery + getInstrumentsByVoucherIdsQuery;

		final List<Object[]> objs = persistenceService.getSession().createSQLQuery(mainQuery).list();

		for (final Object[] obj : objs)
			if (voucherIdAndInstrumentMap.containsKey(getLongValue(obj[0])))
				voucherIdAndInstrumentMap.get(getLongValue(obj[0])).add(obj);
			else {
				final List<Object[]> instrumentVouchers = new ArrayList<Object[]>();
				instrumentVouchers.add(obj);
				voucherIdAndInstrumentMap.put(getLongValue(obj[0]), instrumentVouchers);
			}
	}

	private void getInstrumentVouchersByInstrumentHeaderIds() {

		final List<Object[]> objs = persistenceService.getSession()
				.createSQLQuery("SELECT ih.id,vh1.id as voucherHeaderId"
						+ " FROM VOUCHERHEADER vh1,egf_instrumentvoucher iv ,egf_instrumentheader ih,egw_status es1 WHERE vh1.id = iv.voucherheaderid AND iv.instrumentheaderid=ih.id"
						+ " AND ih.id_status = es1.id AND ih.id in (select ih2.id as instrHeaderId "
						+ getInstrumentsByVoucherIdsQuery + ")")
				.list();

		for (final Object[] obj : objs)
			if (InstrumentHeaderIdsAndInstrumentVouchersMap.containsKey(getLongValue(obj[0])))
				InstrumentHeaderIdsAndInstrumentVouchersMap.get(getLongValue(obj[0])).add(obj);
			else {
				final List<Object[]> instrumentVouchers = new ArrayList<Object[]>();
				instrumentVouchers.add(obj);
				InstrumentHeaderIdsAndInstrumentVouchersMap.put(getLongValue(obj[0]), instrumentVouchers);
			}
	}

	private BankBookEntry getInitialAccountBalance(final String glCode, final String fundCode, final String deptCode) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		BigDecimal amount = egovCommon.getAccountBalanceforDate(calendar.getTime(), glCode, fundCode, null, null,
				deptCode);
		BankBookEntry initialOpeningBalance = new BankBookEntry("To Opening Balance",
				egovCommon.getAccountBalanceforDate(calendar.getTime(), glCode, fundCode, null, null, deptCode),
				RECEIPT, BigDecimal.ZERO, BigDecimal.ZERO);
		return initialOpeningBalance;
	}

	private void computeTotals(List<BankBookEntry> bankBookEntriesIndiuvidual, String glCode, String fundCode,
			List<String> multipleChequeVoucherNumber, List<BankBookEntry> rowsToBeRemoved) {

		getInstrumentsByVoucherIds();
		getInstrumentVouchersByInstrumentHeaderIds();
		List<String> voucherNo = new ArrayList<String>();
		BankBookEntry initialOpeningBalance = getInitialAccountBalance(glCode, fundCode,
				getVouchermis().getDepartmentcode());
		entries.add(initialOpeningBalance);
		Date date = bankBookEntriesIndiuvidual.get(0).getVoucherDate();
		String voucherNumber = EMPTY_STRING;
		String chequeNumber = "";

		BigDecimal receiptTotal = BigDecimal.ZERO;
		BigDecimal paymentTotal = BigDecimal.ZERO;
		BigDecimal initialBalance = initialOpeningBalance.getAmount();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Inside computeTotals()");
		Iterator<BankBookEntry> iter = bankBookEntriesIndiuvidual.iterator();

		while (iter.hasNext()) {
			// for (final BankBookEntry bankBookEntry : bankBookEntries) {
			BankBookEntry bankBookEntry = iter.next();
			if (initialBalance.longValue() < 0)
				isCreditOpeningBalance = true;
			if (!rowsToBeRemoved.contains(bankBookEntry)) {
				/*
				 * // for a voucher // there could be // multiple // surrendered // cheques //
				 * associated // with it. remove the dupicate rows
				 */
				if (bankBookEntry.voucherDate != null && date != null) {
					if (bankBookEntry.voucherDate.compareTo(date) != 0) {
						date = bankBookEntry.getVoucherDate();
						BigDecimal closingBalance = initialBalance.add(receiptTotal).subtract(paymentTotal);
						if (closingBalance.longValue() < 0) {
							entries.add(new BankBookEntry("Closing:By Balance c/d", closingBalance, PAYMENT,
									BigDecimal.ZERO, BigDecimal.ZERO));
							if (isCreditOpeningBalance)
								entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT,
										closingBalance.abs().add(receiptTotal),
										initialBalance.abs().add(paymentTotal)));
							else
								entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT,
										initialBalance.abs().add(receiptTotal).add(closingBalance.abs()),
										paymentTotal));
							entries.add(new BankBookEntry("To Opening Balance", closingBalance, RECEIPT,
									BigDecimal.ZERO, BigDecimal.ZERO));
						} else {
							entries.add(new BankBookEntry("Closing:By Balance c/d", closingBalance, RECEIPT,
									BigDecimal.ZERO, BigDecimal.ZERO));
							if (isCreditOpeningBalance)
								entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT, receiptTotal,
										closingBalance.abs().add(paymentTotal).add(initialBalance.abs())));
							else
								entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT,
										initialBalance.abs().add(receiptTotal),
										closingBalance.abs().add(paymentTotal)));
							entries.add(new BankBookEntry("To Opening Balance", closingBalance, PAYMENT,
									BigDecimal.ZERO, BigDecimal.ZERO));
						}

						receiptTotal = BigDecimal.ZERO;
						paymentTotal = BigDecimal.ZERO;
						initialBalance = closingBalance;
						isCreditOpeningBalance = false;
					}
				}
				if (bankBookEntry.getVoucherNumber() != null && voucherNumber != null) {
					if (RECEIPT.equalsIgnoreCase(bankBookEntry.getType())
							&& !voucherNumber.equalsIgnoreCase(bankBookEntry.getVoucherNumber()))
						receiptTotal = receiptTotal.add(bankBookEntry.getAmount());
					else if (!voucherNumber.equalsIgnoreCase(bankBookEntry.getVoucherNumber()))
						paymentTotal = paymentTotal.add(bankBookEntry.getAmount());
					if (SURRENDERED.equalsIgnoreCase(bankBookEntry.getInstrumentStatus()))
						bankBookEntry.setChequeDetail(EMPTY_STRING);
					if (multipleChequeVoucherNumber.contains(bankBookEntry.getVoucherNumber())) {
						bankBookEntry.setChequeDetail("MULTIPLE");// Set the cheque
																	// details to
																	// MULTIPLE if
																	// the voucher
																	// has multiple
						// cheques assigned to it
						List<Object[]> chequeDetails = voucherIdAndInstrumentMap
								.get(bankBookEntry.getVoucherId().longValue());
						StringBuffer listofcheque = new StringBuffer(100);
						String chequeNos = " ";
						String chequeComp = " ";

						if (!voucherNo.contains(bankBookEntry.getVoucherNumber()) && chequeDetails != null) {
							for (Object[] iv : chequeDetails) {
								chequeNumber = getStringValue(iv[1]);
								chequeStatus = " ";
								chequeStatus = getStringValue(iv[2]);
								if (!(SURRENDERED.equalsIgnoreCase(chequeStatus)
										|| FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS
												.equalsIgnoreCase(chequeStatus))) {

									if (isInstrumentMultiVoucherMapped(getLongValue(iv[3]))) {
										String chqDate = sdf.format(getDateValue(iv[4]));
										chequeComp = chequeNumber + " " + chqDate + "-MULTIPLE";
									}

									listofcheque.append(getStringValue(iv[1])).append(" ")
											.append(getDateValue(iv[4]) != null ? sdf.format(getDateValue(iv[4])) : "");
									// String
									// chqDate=sdf.format(iv.getInstrumentHeaderId().getInstrumentDate());
									if (chequeComp.contains("-MULTIPLE")) {
										listofcheque.append(" ").append("-MULTIPLE,");
										chequeComp = " ";
									} else
										listofcheque.append(" ").append(",");
								}
							}
							chequeNos = listofcheque.toString();
							if (chequeNos.length() > 1)
								chequeNos = chequeNos.substring(0, chequeNos.length() - 1);

							bankBookEntry.setChequeNumber(chequeNos);
							voucherNumber = bankBookEntry.getVoucherNumber();
							entries.add(bankBookEntry);
							voucherNo.add(bankBookEntry.getVoucherNumber());
						}

					} else {
						if (bankBookEntry.getVoucherId() != null) {
							voucherStr = " ";
							List<Object[]> instrumentVoucherList = new ArrayList<Object[]>();
							instrumentVoucherList = voucherIdAndInstrumentMap
									.get(bankBookEntry.getVoucherId().longValue());
							if (instrumentVoucherList != null)
								for (Object[] instrumentVoucher : instrumentVoucherList)
									try {
										chequeNumber = getStringValue(instrumentVoucher[1]);
										chequeStatus = " ";
										chequeStatus = getStringValue(instrumentVoucher[2]);
										if (!(SURRENDERED.equalsIgnoreCase(chequeStatus)
												|| FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS
														.equalsIgnoreCase(chequeStatus)))
											if (isInstrumentMultiVoucherMapped(getLongValue(instrumentVoucher[3]))) {
												if (chequeNumber != null && !chequeNumber.equalsIgnoreCase("")) {
													String chqDate = getDateValue(instrumentVoucher[4]) != null
															? sdf.format(getDateValue(instrumentVoucher[4]))
															: "";
													voucherStr = chequeNumber + " " + chqDate + "-MULTIPLE";
												} else {
													chequeNumber = getStringValue(instrumentVoucher[5]);
													String chqDate = getDateValue(instrumentVoucher[6]) != null
															? sdf.format(getDateValue(instrumentVoucher[6]))
															: "";
													voucherStr = chequeNumber + " " + chqDate + "-MULTIPLE";
												}
											} else if (chequeNumber != null && !chequeNumber.equalsIgnoreCase("")) {
												String chqDate = getDateValue(instrumentVoucher[4]) != null
														? sdf.format(getDateValue(instrumentVoucher[4]))
														: "";
												voucherStr = chequeNumber + " " + chqDate;
											} else { // voucherStr=" ";
												chequeNumber = getStringValue(instrumentVoucher[5]);
												String chqDate = sdf.format(getDateValue(instrumentVoucher[6]));
												voucherStr = chequeNumber + " " + chqDate;
												// }
											}

									} catch (final NumberFormatException ex) {
									}
							bankBookEntry.setChequeDetail(voucherStr);
							entries.add(bankBookEntry);
							voucherNo.add(bankBookEntry.getVoucherNumber());
						}
					}
				}
				voucherNumber = bankBookEntry.getVoucherNumber();
			}

		}
		String vhNum = EMPTY_STRING;
		for (BankBookEntry bankBookEntry : bankBookEntriesIndiuvidual)
			if (bankBookEntry.voucherNumber.equalsIgnoreCase(vhNum)) { // this
																		// is to
																		// handle
																		// multiple
																		// debits
																		// or
																		// credits
																		// for a
				// single voucher.
				bankBookEntry.setVoucherDate(null);
				bankBookEntry.setAmount(null);
				bankBookEntry.setVoucherNumber(EMPTY_STRING);
			} else
				vhNum = bankBookEntry.getVoucherNumber();
		// adding total,closing and opening balance to the last group
		addTotalsSection(initialBalance, paymentTotal, receiptTotal, entries);
		bankBookEntries = entries;
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("End of computeTotals()");

	}

	private void prepareViewObject() {
		for (BankBookEntry row : bankBookEntries) {
			CashBookViewEntry bankBookViewEntry = new CashBookViewEntry();
			if ("Total".equalsIgnoreCase(row.getParticulars())) {
				bankBookViewEntry.setReceiptAmount(row.getReceiptAmount());
				bankBookViewEntry.setReceiptParticulars(row.getParticulars());
				bankBookViewEntry.setPaymentAmount(row.getReceiptAmount());
				bankBookViewEntry.setPaymentParticulars(row.getParticulars());
			} else if ("To Opening Balance".equalsIgnoreCase(row.getParticulars())) {
				BigDecimal amt = row.getAmount();
				if (amt.longValue() < 0) {
					bankBookViewEntry.setPaymentAmount(amt.abs());
					bankBookViewEntry.setPaymentParticulars(row.getParticulars());
				} else {
					bankBookViewEntry.setReceiptAmount(amt.abs());
					bankBookViewEntry.setReceiptParticulars(row.getParticulars());
				}
			} else if ("Closing:By Balance c/d".equalsIgnoreCase(row.getParticulars())) {
				BigDecimal amt = row.getAmount();
				if (amt.longValue() < 0) {
					bankBookViewEntry.setReceiptAmount(amt.abs());
					bankBookViewEntry.setReceiptParticulars(row.getParticulars());
				} else {
					bankBookViewEntry.setPaymentAmount(amt.abs());
					bankBookViewEntry.setPaymentParticulars(row.getParticulars());
				}

			} else {
				String voucherDate = row.getVoucherDate() == null ? ""
						: Constants.DDMMYYYYFORMAT2.format(row.getVoucherDate());

				if (row.getType().equalsIgnoreCase(RECEIPT)) {

					bankBookViewEntry = new CashBookViewEntry(row.getVoucherNumber(), voucherDate, row.getParticulars(),
							row.getAmount(), row.getChequeDetail(), RECEIPT, row.getChequeNumber());
					bankBookViewEntry.setVoucherId(row.getVoucherId().longValue());
					String arr[] = row.getParticulars().split("-");
					if (arr[1].toUpperCase().contains("CASH")) {
						bankBookViewEntry.setReceiptCash(row.getAmount());
						bankBookViewEntry.setReceiptAmount(new BigDecimal(0));

					} else {
						bankBookViewEntry.setReceiptCash(new BigDecimal(0));
						bankBookViewEntry.setReceiptAmount(row.getAmount());
					}
				} else {
					bankBookViewEntry = new CashBookViewEntry(row.getVoucherNumber(), voucherDate, row.getParticulars(),
							row.getAmount(), row.getChequeDetail(), PAYMENT, row.getChequeNumber());
					bankBookViewEntry.setVoucherId(row.getVoucherId().longValue());
					String arr[] = row.getParticulars().split("-");
					if (arr[1].toUpperCase().contains("CASH")) {
						bankBookViewEntry.setPaymentCash(row.getAmount());
						bankBookViewEntry.setPaymentAmount(new BigDecimal(0));

					} else {
						bankBookViewEntry.setPaymentCash(new BigDecimal(0));
						bankBookViewEntry.setPaymentAmount(row.getAmount());
					}
				}
			}
			bankBookViewEntries.add(bankBookViewEntry);
		}
	}

	private List<BankBookEntry> getResults(final String glCode1) {
		final String miscQuery = getMiscQuery();
		String OrderBy = "";
		final String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
		final String query1 = "SELECT distinct vh.id as voucherId,vh.voucherDate AS voucherDate, vh.voucherNumber AS voucherNumber,"
				+ " gl.glcode||' - ('||c.name||')-'||vh.description||'-'||case when sum(gl.debitAmount)  = 0 then (case sum(gl.creditamount) when 0 then sum(gl.creditAmount)||'.00cr' when floor(sum(gl.creditamount)) then sum(gl.creditAmount)||'.00cr' else  sum(gl.creditAmount)||'cr'  end ) else (case sum(gl.debitamount) when 0 then sum(gl.debitamount)||'.00dr' when floor(sum(gl.debitamount))  then sum(gl.debitamount)||'.00dr' else  sum(gl.debitamount)||'dr' 	 end ) end"
				+ " AS particulars,case when sum(gl1.debitAmount) = 0 then sum(gl1.creditamount) else sum(gl1.debitAmount) end AS amount, case when sum(gl1.debitAmount) = 0 then 'Payment' else 'Receipt' end AS type,"
				+ " case when (case when ch.instrumentnumber is NULL then ch.transactionnumber else ch.instrumentnumber  ||' , ' ||TO_CHAR(case when ch.instrumentdate is NULL THEN ch.transactiondate else ch.instrumentdate end,'dd/mm/yyyy') end )  is NULL then case when ch.instrumentnumber is NULL then ch.transactionnumber else ch.instrumentnumber end ||' , ' ||TO_CHAR(case when ch.instrumentdate is NULL then ch.transactiondate else ch.instrumentdate end,'dd/mm/yyyy') end"
				+ " AS chequeDetail,gl.glcode as glCode,ch.description as instrumentStatus,vh.description as narration  ";
		queryFrom = " FROM chartofaccounts c, generalLedger gl,generalLedger gl1"
				+ ",vouchermis vmis, VOUCHERHEADER vh left outer join (select iv.voucherheaderid,ih.instrumentnumber,ih.instrumentdate,"
				+ "es.description,ih.transactionnumber,ih.transactiondate from egf_instrumentheader ih,egw_status es,egf_instrumentvoucher iv where iv.instrumentheaderid=ih.id and "
				+ "ih.id_status=es.id) ch on ch.voucherheaderid=vh.id  WHERE  c.glcode = gl.glcode AND gl.voucherHeaderId = vh.id  AND vmis.VOUCHERHEADERID=vh.id  "
				+ "and gl.voucherheaderid  IN (SELECT voucherheaderid FROM generalledger gl WHERE glcode='" + glCode1
				+ "') AND gl.voucherheaderid = gl1.voucherheaderid AND gl.glcode <> '" + glCode1
				+ "' AND gl1.glcode = '" + glCode1 + "' and vh.voucherDate>='"
				+ Constants.DDMMYYYYFORMAT1.format(startDate) + "' " + "and vh.voucherDate<='"
				+ Constants.DDMMYYYYFORMAT1.format(endDate) + "' and vh.status not in(" + voucherStatusToExclude + ") "
				+ miscQuery + " ";
		OrderBy = "group by vh.id,gl.glcode,ch.instrumentnumber,ch.transactionnumber,ch.instrumentdate,ch.transactiondate,ch.description,c.name,vh.description order by voucherdate,vouchernumber";
		System.out.println("Main query :" + query1 + queryFrom + OrderBy);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Main query :" + query1 + queryFrom + OrderBy);

		final Query query = persistenceService.getSession().createSQLQuery(query1 + queryFrom + OrderBy)
				.addScalar("voucherId", new BigDecimalType()).addScalar("voucherDate").addScalar("voucherNumber")
				.addScalar("particulars").addScalar("amount", new BigDecimalType()).addScalar("type")
				.addScalar("chequeDetail").addScalar("glCode").addScalar("instrumentStatus")
				.setResultTransformer(Transformers.aliasToBean(BankBookEntry.class));
		final List<BankBookEntry> results = query.list();
		return results;
	}

	String getMiscQuery() {
		final StringBuffer query = new StringBuffer();

		/*
		 * if (fundId != null && fundId.getId() != null && fundId.getId() != -1) {
		 * query.append(" and vh.fundId=").append(fundId.getId().toString()); final Fund
		 * fnd = (Fund) persistenceService.find("from Fund where id=?", fundId.getId());
		 * header.append(" for " + fnd.getName()); }
		 */

		if (getVouchermis() != null && getVouchermis().getDepartmentcode() != null
				&& getVouchermis().getDepartmentcode() != null && !getVouchermis().getDepartmentcode().equals("-1")) {
			query.append(" and vmis.DEPARTMENTCODE='").append(getVouchermis().getDepartmentcode() + "'");
			Department department = microserviceUtils.getDepartmentByCode(getVouchermis().getDepartmentcode());
			header.append(" in " + department.getName() + " ");
		}
		if (getVouchermis() != null && getVouchermis().getFunctionary() != null
				&& getVouchermis().getFunctionary().getId() != null && getVouchermis().getFunctionary().getId() != -1)
			query.append(" and vmis.FUNCTIONARYID=").append(getVouchermis().getFunctionary().getId().toString());
		if (getVouchermis() != null && getVouchermis().getFundsource() != null
				&& getVouchermis().getFundsource().getId() != null && getVouchermis().getFundsource().getId() != -1)
			query.append(" and vmis.FUNDSOURCEID =").append(getVouchermis().getFundsource().getId().toString());
		if (getVouchermis() != null && getVouchermis().getSchemeid() != null
				&& getVouchermis().getSchemeid().getId() != null && getVouchermis().getSchemeid().getId() != -1)
			query.append(" and vmis.SCHEMEID =").append(getVouchermis().getSchemeid().getId().toString());
		if (getVouchermis() != null && getVouchermis().getSubschemeid() != null
				&& getVouchermis().getSubschemeid().getId() != null && getVouchermis().getSubschemeid().getId() != -1)
			query.append(" and vmis.SUBSCHEMEID =").append(getVouchermis().getSubschemeid().getId().toString());
		if (getVouchermis() != null && getVouchermis().getDivisionid() != null
				&& getVouchermis().getDivisionid().getId() != null && getVouchermis().getDivisionid().getId() != -1)
			query.append(" and vmis.DIVISIONID =").append(getVouchermis().getDivisionid().getId().toString());
		/*
		 * if (function != null && function.getId() != null && function.getId() != -1) {
		 * query.append(" and vmis.FUNCTIONID=" ).append(function.getId().toString());
		 * final CFunction func = (CFunction)
		 * persistenceService.find("from CFunction where id=?", function.getId());
		 * header.append(" in " + func.getName() + " "); }
		 */
		if (getVouchermis() != null && getVouchermis().getFunction() != null
				&& getVouchermis().getFunction().getId() != null && getVouchermis().getFunction().getId() != -1) {
			query.append(" and vmis.functionid=").append(getVouchermis().getFunction().getId());
			final CFunction func = (CFunction) persistenceService.find("from CFunction where id=?",
					getVouchermis().getFunction().getId());
			header.append(" in " + func.getName() + " ");
		}

		return query.toString();
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

	protected void setRelatedEntitesOn() {
		setTodayDate(new Date());
		if (balanceSheet.getFinancialYear() != null && balanceSheet.getFinancialYear().getId() != null)
			balanceSheet.setFinancialYear((CFinancialYear) getPersistenceService1()
					.find("from CFinancialYear where id=?", balanceSheet.getFinancialYear().getId()));
		if (balanceSheet.getDepartment() != null && balanceSheet.getDepartment().getCode() != null
				&& !balanceSheet.getDepartment().getCode().isEmpty()) {
			Department dept = microserviceUtils.getDepartmentByCode(balanceSheet.getDepartment().getCode());
			balanceSheet.setDepartment(dept);
			header.append(" in " + balanceSheet.getDepartment().getName());
		} else
			balanceSheet.setDepartment(null);

		if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null
				&& balanceSheet.getFund().getId() != 0) {
			balanceSheet.setFund(
					(Fund) getPersistenceService1().find("from Fund where id=?", balanceSheet.getFund().getId()));
			header.append(" for " + balanceSheet.getFund().getName());
		}
		if (balanceSheet.getFunction() != null && balanceSheet.getFunction().getId() != null
				&& balanceSheet.getFunction().getId() != 0) {
			balanceSheet.setFunction((CFunction) getPersistenceService1().find("from CFunction where id=?",
					balanceSheet.getFunction().getId()));
			header.append(" for " + balanceSheet.getFunction().getName());
		}

		if (balanceSheet.getAsOndate() != null)
			header.append(" as on " + DDMMYYYYFORMATS.format(balanceSheet.getAsOndate()));
		header.toString();
	}

	protected void populateDataSource() {

		setRelatedEntitesOn();

		if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null) {
			final List<Fund> selFund = new ArrayList<Fund>();
			selFund.add(balanceSheet.getFund());
			balanceSheet.setFunds(selFund);
		} else
			balanceSheet.setFunds(balanceSheetService.getFunds());
		balancesheetServiceOld.populateBalanceSheetForCashFlow(balanceSheet);
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
			Date prevToDate = new Date(cashBookReportBean.getToDate().getTime() - MILLIS_IN_A_YEAR);
			// Date prevToDate = new Date(cashBookReportBean.getFromDate().getTime() -
			// MILLIS_IN_A_DAY);
			// get previous year
			balanceSheet = null;
			balanceSheet = new Statement();
			balanceSheet.setFromDate(cashBookReportBean.getFromDate());
			balanceSheet.setToDate(cashBookReportBean.getToDate());
			balanceSheet.setPeriod("Date");
			balanceSheet.setFinancialYear(balancesheetServiceOld.getfinancialYear(balanceSheet.getFromDate()));
			populateDataSource();
			List<StatementEntry> balanceSheetCurrent = balanceSheet.getEntries();
			for (StatementEntry obj : balanceSheetCurrent) {
				System.out.println("Current Year Scene ::gl code ::" + obj.getGlCode() + "## current year total ::"
						+ obj.getCurrentYearTotal() + "## previous year total::" + obj.getPreviousYearTotal()
						+ "###fin year ::" + balanceSheet.getFinancialYear());
			}
			balanceSheet = null;
			balanceSheet = new Statement();
			balanceSheet.setFromDate(prevFromDate);
			balanceSheet.setToDate(prevToDate);
			balanceSheet.setPeriod("Date");
			balanceSheet.setFinancialYear(balancesheetServiceOld.getfinancialYear(balanceSheet.getFromDate()));
			populateDataSource();
			List<StatementEntry> balanceSheetPrev = balanceSheet.getEntries();
			for (StatementEntry obj : balanceSheetPrev) {
				System.out.println("Previous year scene :: gl code ::" + obj.getGlCode() + "## current year total ::"
						+ obj.getCurrentYearTotal() + "## previous year total::" + obj.getPreviousYearTotal()
						+ "###fin year ::" + balanceSheet.getFinancialYear());
			}
			balanceSheetLNow = populateDataSourceForList(balanceSheetCurrent, cashBookReportBean.getToDate(),
					cashBookReportBean.getFromDate(), "current");
			balanceSheetLPrev = populateDataSourceForList(balanceSheetPrev, prevToDate, prevFromDate, "prev");
			finalBalanceSheetL = balanceSheetService.getFinalVBalanceSheetList(balanceSheetLNow, balanceSheetLPrev);
			cashBookReportBean.setaCurrentYear(balanceSheetService.getACurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setaPrevYear(balanceSheetService.getAPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbCurrentYear(balanceSheetService.getbCurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbPrevYear(balanceSheetService.getbPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setcCurrentYear(new BigDecimal(0));
			cashBookReportBean.setcPrevYear(new BigDecimal(0));
			cashBookReportBean.setAbcCurrentYear(balanceSheetService.getabcCurrentYear(cashBookReportBean));
			cashBookReportBean.setAbcPrevYear(balanceSheetService.getabcPreviousYear(cashBookReportBean));
			cashBookReportBean
					.setAtEndCurr(balanceSheetService.getAtEndCurrentYear(finalBalanceSheetL, cashBookReportBean));
			cashBookReportBean.setAtBeginingCurr(finalBalanceSheetL.get(0).getAtBeginingPeriodCurrYear());
			cashBookReportBean.setAtBeginingPrev(finalBalanceSheetL.get(0).getAtBeginingPeriodPrevYear());
			cashBookReportBean
					.setAtEndPrev(balanceSheetService.getatendPrevYear(finalBalanceSheetL, cashBookReportBean));
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
					investmentIncomeReceivedPrevYear = prevYearAmount;
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

	protected List<CashFlowReportDataBean> populateDataSourceForList(List<StatementEntry> resultEntries, Date toDate,
			Date fromDate, String yearType) {
		List<CashFlowReportDataBean> lst1 = new ArrayList<CashFlowReportDataBean>();
		fixedAssetDebitAmt = balanceSheetService.getFixedAssetDEbitAmount("410", fromDate, toDate);
		fixedAssetCreditAmt = balanceSheetService.getFixedAssetCreditAmount("410", fromDate, toDate);
		investmentDebitAmt = balanceSheetService.getInvestmentDEbitAmount("420", fromDate, toDate);
		investmentCreditAmt = balanceSheetService.getInvestmentCreditAmount("420", fromDate, toDate);

		List<StatementEntry> ieStatementEntry = new ArrayList<StatementEntry>();
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
			/*
			 * System.out.println("##year::" + yearType + "### glcode::" +
			 * ieStatementEntry.get(i).getGlCode() + "::" +
			 * ieStatementEntry.get(i).getCurrentYearTotal() + "::" +
			 * ieStatementEntry.get(i).getPreviousYearTotal()); LOGGER.info("##year::" +
			 * yearType + "### glcode::" + ieStatementEntry.get(i).getGlCode() + "::" +
			 * ieStatementEntry.get(i).getCurrentYearTotal() + "::" +
			 * ieStatementEntry.get(i).getPreviousYearTotal());
			 */

			if (ieStatementEntry != null && ieStatementEntry.get(i).getGlCode() != null) {
				if (ieStatementEntry.get(i).getCurrentYearTotal() == null) {
					thisYearAmount = new BigDecimal(0);

				} else {
					thisYearAmount = ieStatementEntry.get(i).getCurrentYearTotal();
				}
				if (ieStatementEntry.get(i).getPreviousYearTotal() == null) {
					prevYearAmount = new BigDecimal(0);
				} else {
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
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("470")) {
					otherCurrentAssetsCurrYear2 = thisYearAmount;

				}
				obj.setOtherCurrentAssetsCurrYear(otherCurrentAssetsCurrYear1.add(otherCurrentAssetsCurrYear2));
				LOGGER.info("460 ::otherCurrentAssetsCurrYear1 ::" + otherCurrentAssetsCurrYear1);
				LOGGER.info("470 :: otherCurrentAssetsCurrYear2 ::" + otherCurrentAssetsCurrYear2);
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
							obj.setInvestmentsPrevYear(investmentDebitAmt);
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
				if (ieStatementEntry.get(i).getGlCode().equalsIgnoreCase("470")) {

					otherCurrentAssetsPrevYear2 = prevYearAmount;
				}
				obj.setOtherCurrentAssetsPrevYear(otherCurrentAssetsPrevYear1.add(otherCurrentAssetsPrevYear2));
				LOGGER.info("460 ::otherCurrentAssetsPrevYear1 ::" + otherCurrentAssetsPrevYear1);
				LOGGER.info("470 :: otherCurrentAssetsPrevYear2 ::" + otherCurrentAssetsPrevYear2);
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
						// obj.setAtEndPeriodCurrYear(thisYearAmount);
					}
					if (yearType.equalsIgnoreCase("prev")) {
						obj.setAtBeginingPeriodPrevYear(prevYearAmount);
						// obj.setAtEndPeriodPrevYear(thisYearAmount);
					}
				}
			}

			lst1.add(obj);
		}
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
	// @RequestMapping(value = "/cashFlow/searchCashFlowReportExportData")
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
			Date prevToDate = new Date(cashBookReportBean.getToDate().getTime() - MILLIS_IN_A_YEAR);
			// Date prevToDate = new Date(cashBookReportBean.getFromDate().getTime() -
			// MILLIS_IN_A_DAY);
			// get previous year
			balanceSheet = null;
			balanceSheet = new Statement();
			balanceSheet.setFromDate(cashBookReportBean.getFromDate());
			balanceSheet.setToDate(cashBookReportBean.getToDate());
			balanceSheet.setPeriod("Date");
			balanceSheet.setFinancialYear(balancesheetServiceOld.getfinancialYear(balanceSheet.getFromDate()));
			populateDataSource();
			List<StatementEntry> balanceSheetCurrent = balanceSheet.getEntries();
			for (StatementEntry obj : balanceSheetCurrent) {
				LOGGER.info("Current Year Scene ::gl code ::" + obj.getGlCode() + "## current year total ::"
						+ obj.getCurrentYearTotal() + "## previous year total::" + obj.getPreviousYearTotal()
						+ "###fin year ::" + balanceSheet.getFinancialYear());
			}
			balanceSheet = null;
			balanceSheet = new Statement();
			balanceSheet.setFromDate(prevFromDate);
			balanceSheet.setToDate(prevToDate);
			balanceSheet.setPeriod("Date");
			balanceSheet.setFinancialYear(balancesheetServiceOld.getfinancialYear(balanceSheet.getFromDate()));
			populateDataSource();
			List<StatementEntry> balanceSheetPrev = balanceSheet.getEntries();
			for (StatementEntry obj : balanceSheetPrev) {
				LOGGER.info("Previous year scene :: gl code ::" + obj.getGlCode() + "## current year total ::"
						+ obj.getCurrentYearTotal() + "## previous year total::" + obj.getPreviousYearTotal()
						+ "###fin year ::" + balanceSheet.getFinancialYear());
			}

			balanceSheetLNow = populateDataSourceForList(balanceSheetCurrent, cashBookReportBean.getToDate(),
					cashBookReportBean.getFromDate(), "current");

			balanceSheetLPrev = populateDataSourceForList(balanceSheetPrev, prevToDate, prevFromDate, "prev");
			finalBalanceSheetL = balanceSheetService.getFinalVBalanceSheetList(balanceSheetLNow, balanceSheetLPrev);
			cashBookReportBean.setaCurrentYear(balanceSheetService.getACurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setaPrevYear(balanceSheetService.getAPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbCurrentYear(balanceSheetService.getbCurrentYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setbPrevYear(balanceSheetService.getbPreviousYear(lst1, finalBalanceSheetL));
			cashBookReportBean.setcCurrentYear(new BigDecimal(0));
			cashBookReportBean.setcPrevYear(new BigDecimal(0));
			cashBookReportBean.setAbcCurrentYear(balanceSheetService.getabcCurrentYear(cashBookReportBean));
			cashBookReportBean.setAbcPrevYear(balanceSheetService.getabcPreviousYear(cashBookReportBean));
			cashBookReportBean
					.setAtEndCurr(balanceSheetService.getAtEndCurrentYear(finalBalanceSheetL, cashBookReportBean));
			cashBookReportBean.setAtBeginingCurr(finalBalanceSheetL.get(0).getAtBeginingPeriodCurrYear());
			cashBookReportBean.setAtBeginingPrev(finalBalanceSheetL.get(0).getAtBeginingPeriodPrevYear());
			cashBookReportBean
					.setAtEndPrev(balanceSheetService.getatendPrevYear(finalBalanceSheetL, cashBookReportBean));
			titleName = getUlbName().toUpperCase() + " ";
			cashBookReportBean.setTitleName(titleName + " Cash Flow Statement");
			cashBookReportBean
					.setHeader("Cash Flow Statement from " + DDMMYYYYFORMAT1.format(cashBookReportBean.getFromDate())
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

	private String getAppConfigValueFor(final String module, final String key) {
		try {
			return appConfigValuesService.getConfigValuesByModuleAndKey(module, key).get(0).getValue();
		} catch (final Exception e) {
			throw new ValidationException(EMPTY_STRING, "The key '" + key + "' is not defined in appconfig");
		}
	}

	private Long getLongValue(final Object object) {
		return object != null ? new Long(object.toString()) : 0;
	}

	private String getStringValue(final Object object) {
		return object != null ? object.toString() : "";
	}

	private boolean isInstrumentMultiVoucherMapped(final Long instrumentHeaderId) {
		final List<Object[]> instrumentVoucherList = InstrumentHeaderIdsAndInstrumentVouchersMap
				.get(instrumentHeaderId);
		boolean rep = false;
		if (instrumentVoucherList != null && instrumentVoucherList.size() != 0) {

			final Object[] obj = instrumentVoucherList.get(0);
			final Long voucherId = getLongValue(obj[1]);
			for (final Object[] instrumentVoucher : instrumentVoucherList)
				if (voucherId != getLongValue(instrumentVoucher[1])) {
					rep = true;
					break;
				}
		}
		return rep;
	}

	private Date getDateValue(final Object object) {
		return object != null ? (Date) object : null;
	}

	private void addTotalsSection(BigDecimal initialBalance, BigDecimal paymentTotal, BigDecimal receiptTotal,
			List<BankBookEntry> entries) {
		BigDecimal closingBalance = initialBalance.add(receiptTotal).subtract(paymentTotal);
		entries.add(
				new BankBookEntry("Closing:By Balance c/d", closingBalance, PAYMENT, BigDecimal.ZERO, BigDecimal.ZERO));
		// Obtain the total accordingly. Similar to how it is done in
		// computeTotals().
		if (initialBalance.longValue() < 0)
			isCreditOpeningBalance = true;
		if (closingBalance.longValue() < 0) {
			if (isCreditOpeningBalance)
				entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT, closingBalance.abs().add(receiptTotal),
						initialBalance.abs().add(paymentTotal)));
			else
				entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT,
						initialBalance.abs().add(receiptTotal).add(closingBalance.abs()), paymentTotal));
		} else if (isCreditOpeningBalance)
			entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT, receiptTotal,
					closingBalance.abs().add(paymentTotal).add(initialBalance.abs())));
		else
			entries.add(new BankBookEntry("Total", BigDecimal.ZERO, RECEIPT, initialBalance.abs().add(receiptTotal),
					closingBalance.abs().add(paymentTotal)));
		isCreditOpeningBalance = false;
		receiptTotal = BigDecimal.ZERO;
		paymentTotal = BigDecimal.ZERO;
		initialBalance = closingBalance;
	}
}
