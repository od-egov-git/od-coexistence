package org.egov.services.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.egf.model.BSStatementEntry;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.OpeningAndClosingBalanceEntry;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.egf.model.StatementResultObject;
import org.egov.infstr.services.PersistenceService;
import org.egov.utils.Constants;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.exilant.eGov.src.reports.TrialBalanceBean;

@Service
public class ReceiptsAndPaymentsService extends ReportService {

	int minorCodeLength;

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	List<Character> coaType = new ArrayList<Character>();
	private static final String I = "I";
	private static final String E = "E";
	private static final String IE = "IE";
	private static final String BS = "BS";

	int CoaClassification = 1;

	@Override
	protected void addRowsToStatement(Statement statement, Statement assets, Statement liabilities) {

		Date fromDate = getFromDate(statement);
		Date toDate = getToDate(statement);

		IEStatementEntry incomeEntry = new IEStatementEntry();
		IEStatementEntry expenseEntry = new IEStatementEntry();
		IEStatementEntry totalCashOpeningEntry = new IEStatementEntry();
		IEStatementEntry totalBankOpeningEntry = new IEStatementEntry();

		totalCashOpeningEntry = getTotalOpening(statement, fromDate, toDate);
		totalBankOpeningEntry = getTotalBankOpening(statement, fromDate, toDate);
		statement.addIE(new IEStatementEntry("", "Opening Balances", null, null, true, 'O'));
		statement.addIE(totalCashOpeningEntry);
		statement.addIE(totalBankOpeningEntry);
		statement.addIE(new IEStatementEntry("", "", null, null, true, 'O'));

		if (assets.sizeIE() > 0) {
			statement.addIE(new IEStatementEntry(null, Constants.EXPENDITURE, "", true));
			expenseEntry = getTotalExpenseFundwise(assets);
			statement.addAllIE(assets);
			statement.addIE(expenseEntry);
			statement.addIE(new IEStatementEntry(null, "", "", false));
		}

		if (liabilities.sizeIE() > 0) {
			statement.addIE(new IEStatementEntry(null, Constants.INCOME, "", true));
			incomeEntry = getTotalIncomeFundwise(liabilities);
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addAllIE(liabilities);
			statement.addIE(incomeEntry);
			statement.addIE(new IEStatementEntry(null, "", "", false));
		}

	}

	private IEStatementEntry getTotalOpening(Statement statement, Date fromDate, Date toDate) {

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();
		int fundid = 1;

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<OpeningAndClosingBalanceEntry> results = getOpeningBalanceForRP(statement, fromDate, toDate);
		final List<OpeningAndClosingBalanceEntry> prevResults = getOpeningBalanceForRP(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		final IEStatementEntry resultEntry = new IEStatementEntry();

		BigDecimal totalCashOpeningBalance = BigDecimal.ZERO;
		BigDecimal totalPrevCashOpeningBalance = BigDecimal.ZERO;

		if (!results.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : results) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4501) {
						totalCashOpeningBalance = totalCashOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));

					}

				}

			}
		}

		if (!prevResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : prevResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4501) {
						totalPrevCashOpeningBalance = totalPrevCashOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}

				}
			}
		}
		resultEntry.getPreviousYearAmount().put(getFundNameForId(statement.getFunds(), fundid),
				totalPrevCashOpeningBalance);
		resultEntry.getNetAmount().put(getFundNameForId(statement.getFunds(), fundid), totalCashOpeningBalance);
		resultEntry.setGlCode("");
		resultEntry.setType('O');
		resultEntry.setAccountName("a. Cash Balances");

		return resultEntry;

	}

	private IEStatementEntry getTotalBankOpening(Statement statement, Date fromDate, Date toDate) {

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();
		int fundid = 1;

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<OpeningAndClosingBalanceEntry> results = getOpeningBalanceForRP(statement, fromDate, toDate);
		final List<OpeningAndClosingBalanceEntry> prevResults = getOpeningBalanceForRP(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		final IEStatementEntry resultEntry = new IEStatementEntry();

		BigDecimal totalPrevBankOpeningBalance = BigDecimal.ZERO;
		BigDecimal totalBankOpeningBalance = BigDecimal.ZERO;

		if (!prevResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : prevResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4502) {
						totalPrevBankOpeningBalance = totalPrevBankOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}
				}

			}
		}

		if (!results.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : results) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4502) {
						totalBankOpeningBalance = totalBankOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}

				}

			}
		}
		resultEntry.getPreviousYearAmount().put(getFundNameForId(statement.getFunds(), fundid),
				totalPrevBankOpeningBalance);
		resultEntry.getNetAmount().put(getFundNameForId(statement.getFunds(), fundid), totalBankOpeningBalance);
		resultEntry.setGlCode("");
		resultEntry.setType('O');
		resultEntry.setAccountName("b. Bank Balances");

		return resultEntry;

	}

	private List<OpeningAndClosingBalanceEntry> getOpeningBalanceForRP(Statement statement, Date fromDate,
			Date toDate) {

		final String openingBalanceStr = "SELECT coa.glcode AS glcode ,coa.name  AS accName, SUM(ts.openingcreditbalance) as openingCreditBalance,"
				+ "sum(ts.openingdebitbalance) as openingDebitBalance"
				+ " FROM transactionsummary ts,chartofaccounts coa,financialyear fy "
				+ " WHERE ts.glcodeid=coa.id  AND ts.financialyearid=fy.id "
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate "
				+ " GROUP BY ts.glcodeid,coa.glcode,coa.name ORDER BY coa.glcode ASC";

		final Query openingBalanceQry = persistenceService.getSession().createSQLQuery(openingBalanceStr)
				.addScalar("glcode").addScalar("accName").addScalar("openingCreditBalance", BigDecimalType.INSTANCE)
				.addScalar("openingDebitBalance", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(OpeningAndClosingBalanceEntry.class));

		openingBalanceQry.setDate("fromDate", fromDate);
		openingBalanceQry.setDate("toDate", toDate);

		return openingBalanceQry.list();
	}

	private List<OpeningAndClosingBalanceEntry> getOpeningBalanceForCB(Statement statement, Date fromDate, Date toDate,
			int glcode) {

		final String openingBalanceStr = "SELECT coa.glcode AS glcode ,coa.name  AS accName, SUM(ts.openingcreditbalance) as openingCreditBalance,"
				+ "sum(ts.openingdebitbalance) as openingDebitBalance"
				+ " FROM transactionsummary ts,chartofaccounts coa,financialyear fy "
				+ " WHERE ts.glcodeid=coa.id  AND ts.financialyearid=fy.id "
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate AND substring(coa.glcode, 0, 5)=' "
				+ glcode + "' GROUP BY ts.glcodeid,coa.glcode,coa.name ORDER BY coa.glcode ASC";

		final Query openingBalanceQry = persistenceService.getSession().createSQLQuery(openingBalanceStr)
				.addScalar("glcode").addScalar("accName").addScalar("openingCreditBalance", BigDecimalType.INSTANCE)
				.addScalar("openingDebitBalance", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(OpeningAndClosingBalanceEntry.class));

		openingBalanceQry.setDate("fromDate", fromDate);
		openingBalanceQry.setDate("toDate", toDate);

		return openingBalanceQry.list();
	}

	protected void addRowsToStatementBS(Statement statement, Statement assets, Statement liabilities) {

		Date fromDate = getFromDate(statement);
		Date toDate = getToDate(statement);

		BSStatementEntry totalEntry = new BSStatementEntry();
		BSStatementEntry closingCashBalEntry = new BSStatementEntry();
		BSStatementEntry closingBankBalEntry = new BSStatementEntry();

		if (liabilities.sizeBS() > 0) {
			statement.addBS(new BSStatementEntry(null, Constants.LIABILITIES, null, null, null, null, true,
					Character.MIN_VALUE));
			statement.addAllBS(liabilities);
			statement.addBS(new BSStatementEntry(null, "", null, null, null, null, true, Character.MIN_VALUE));

		}
		if (assets.sizeBS() > 0) {
			statement.addBS(
					new BSStatementEntry(null, Constants.ASSETS, null, null, null, null, true, Character.MIN_VALUE));
			statement.addAllBS(assets);
			statement.addBS(new BSStatementEntry(null, "", null, null, null, null, true, Character.MIN_VALUE));

		}

		totalEntry = getTotalBS(statement);
		statement.addBS(totalEntry);

		closingCashBalEntry = getClosingCashBalance(statement, fromDate, toDate);
		closingBankBalEntry = getClosingBankBalance(statement, fromDate, toDate);
		statement.addBS(new BSStatementEntry("", "", null, null, null,null, true, 'C'));
		statement.addBS(new BSStatementEntry("", "Closing Balances", null, null, null,null, true, 'C'));
		statement.addBS(closingCashBalEntry);
		statement.addBS(closingBankBalEntry);

	}

	private BSStatementEntry getClosingCashBalance(Statement statement, Date fromDate, Date toDate) {

		BigDecimal totalCashOpeningBalance = BigDecimal.ZERO;
		BigDecimal totalPrevCashOpeningBalance = BigDecimal.ZERO;

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<OpeningAndClosingBalanceEntry> results = getOpeningBalanceForCB(statement, fromDate, toDate, 4501);
		final List<OpeningAndClosingBalanceEntry> prevResults = getOpeningBalanceForCB(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate, 4501);

		for (OpeningAndClosingBalanceEntry oc : results) {

			totalCashOpeningBalance = totalCashOpeningBalance
					.add(oc.getOpeningDebitBalance().subtract(oc.getOpeningCreditBalance()));

		}

		for (OpeningAndClosingBalanceEntry oc : prevResults) {
			totalPrevCashOpeningBalance = totalPrevCashOpeningBalance
					.add(oc.getOpeningDebitBalance().subtract(oc.getOpeningCreditBalance()));
		}

		// get total debit and credit
		final List<OpeningAndClosingBalanceEntry> debitCreditCash = getDebitCreditForCB(statement, fromDate, toDate,
				4501);
		final List<OpeningAndClosingBalanceEntry> prevDebitCreditCash = getDebitCreditForCB(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate, 4501);
		BigDecimal totalCreditCash = BigDecimal.ZERO;
		BigDecimal totalDebitCash = BigDecimal.ZERO;

		BigDecimal totalPrevCreditCash = BigDecimal.ZERO;
		BigDecimal totalPrevDebitCash = BigDecimal.ZERO;

		for (OpeningAndClosingBalanceEntry dc : debitCreditCash) {
			totalCreditCash = totalCreditCash.add(dc.getOpeningCreditBalance());
			totalDebitCash = totalDebitCash.add(dc.getOpeningDebitBalance());
		}

		for (OpeningAndClosingBalanceEntry dc : prevDebitCreditCash) {
			totalPrevCreditCash = totalPrevCreditCash.add(dc.getOpeningCreditBalance());
			totalPrevDebitCash = totalPrevDebitCash.add(dc.getOpeningDebitBalance());
		}

		// total closing

		BigDecimal totalCashClosing = totalCashOpeningBalance.add(totalDebitCash.subtract(totalCreditCash));
		BigDecimal totalPrevCashClosing = totalPrevCashOpeningBalance
				.add(totalPrevDebitCash.subtract(totalPrevCreditCash));

		BSStatementEntry entry = new BSStatementEntry("","",totalCashClosing,null,totalPrevCashClosing,null,true,'C');

		return entry;

	}

	private BSStatementEntry getClosingBankBalance(Statement statement, Date fromDate, Date toDate) {

		BigDecimal totalBankOpeningBalance = BigDecimal.ZERO;
		BigDecimal totalPrevBankOpeningBalance = BigDecimal.ZERO;

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<OpeningAndClosingBalanceEntry> resultsBank = getOpeningBalanceForCB(statement, fromDate, toDate,
				4502);
		final List<OpeningAndClosingBalanceEntry> prevResultsBank = getOpeningBalanceForCB(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate, 4502);

		for (OpeningAndClosingBalanceEntry oc : resultsBank) {
			totalBankOpeningBalance = totalBankOpeningBalance
					.add(oc.getOpeningDebitBalance().subtract(oc.getOpeningCreditBalance()));
		}

		for (OpeningAndClosingBalanceEntry oc : prevResultsBank) {
			totalPrevBankOpeningBalance = totalPrevBankOpeningBalance
					.add(oc.getOpeningDebitBalance().subtract(oc.getOpeningCreditBalance()));
		}

		// get total debit and credit
		final List<OpeningAndClosingBalanceEntry> debitCreditCash = getDebitCreditForCB(statement, fromDate, toDate,
				4502);
		final List<OpeningAndClosingBalanceEntry> prevDebitCreditCash = getDebitCreditForCB(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate, 4502);
		BigDecimal totalCreditBank = BigDecimal.ZERO;
		BigDecimal totalDebitBank = BigDecimal.ZERO;

		BigDecimal totalPrevCreditBank = BigDecimal.ZERO;
		BigDecimal totalPrevDebitBank = BigDecimal.ZERO;

		for (OpeningAndClosingBalanceEntry dc : debitCreditCash) {
			totalCreditBank = totalCreditBank.add(dc.getOpeningCreditBalance());
			totalDebitBank = totalDebitBank.add(dc.getOpeningDebitBalance());
		}

		for (OpeningAndClosingBalanceEntry dc : prevDebitCreditCash) {
			totalPrevCreditBank = totalPrevCreditBank.add(dc.getOpeningCreditBalance());
			totalPrevDebitBank = totalPrevDebitBank.add(dc.getOpeningDebitBalance());
		}

		// total closing

		BigDecimal totalBankClosing = totalBankOpeningBalance.add(totalDebitBank.subtract(totalCreditBank));
		BigDecimal totalPrevBankClosing = totalPrevBankOpeningBalance
				.add(totalPrevDebitBank.subtract(totalPrevCreditBank));

		BSStatementEntry entry = new BSStatementEntry("","",totalBankClosing,null,totalPrevBankClosing,null,true,'C');
		
		return entry;
	}

	private List<OpeningAndClosingBalanceEntry> getDebitCreditForCB(Statement statement, Date fromDate, Date toDate,
			int glcode) {

		final String debitCreditString = "SELECT substring(coa.glcode,0,5) AS accName, SUM(gl.creditAmount) as openingCreditBalance,sum(gl.debitAmount) as openingDebitBalance "
				+ "FROM generalledger gl,chartofaccounts coa,financialyear fy, Voucherheader vh "
				+ "WHERE gl.glcodeid=coa.id and vh.id= gl.voucherheaderid and substring(coa.glcode,0,5)='" + glcode
				+ "' AND vh.voucherdate>=:fromDate AND vh.voucherdate<=:toDate "
				+ "AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate AND vh.status not in (4,5,6) "
				+ "GROUP BY substring(coa.glcode,0,5)";

		final Query creditDebitQuery = persistenceService.getSession().createSQLQuery(debitCreditString)
				.addScalar("accName").addScalar("openingCreditBalance", BigDecimalType.INSTANCE)
				.addScalar("openingDebitBalance", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(OpeningAndClosingBalanceEntry.class));

		creditDebitQuery.setDate("fromDate", fromDate);
		creditDebitQuery.setDate("toDate", toDate);

		return creditDebitQuery.list();
	}

	private BSStatementEntry getTotalBS(Statement statement) {

		List<BSStatementEntry> bsEntries = statement.getBsEntries();

		BigDecimal totalNOReceipts = BigDecimal.ZERO;
		BigDecimal totalNOPayments = BigDecimal.ZERO;
		BigDecimal totalNOPrevReceipts = BigDecimal.ZERO;
		BigDecimal totalNOPrevPayments = BigDecimal.ZERO;

		for (BSStatementEntry entry : bsEntries) {

			if (entry.getType() == 'L') {
				totalNOReceipts = totalNOReceipts.add(entry.getCreditamount() != null
						? entry.getCreditamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getCreditamount().negate()
								: entry.getCreditamount()
						: BigDecimal.ZERO);

				totalNOPrevReceipts = totalNOPrevReceipts
						.add(entry.getPrevCreditamount() != null
								? entry.getPrevCreditamount().compareTo(BigDecimal.ZERO) < 0
										? entry.getPrevCreditamount().negate()
										: entry.getPrevCreditamount()
								: BigDecimal.ZERO);

				totalNOPayments = totalNOPayments.add(entry.getDebitamount() != null
						? entry.getDebitamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getDebitamount().negate()
								: entry.getDebitamount()
						: BigDecimal.ZERO);

				totalNOPrevPayments = totalNOPrevPayments
						.add(entry.getPrevDebitamount() != null
								? entry.getPrevDebitamount().compareTo(BigDecimal.ZERO) < 0
										? entry.getPrevDebitamount().negate()
										: entry.getPrevDebitamount()
								: BigDecimal.ZERO);

			} else if (entry.getType() == 'A') {
				totalNOReceipts = totalNOReceipts.add(entry.getDebitamount() != null
						? entry.getDebitamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getDebitamount().negate()
								: entry.getDebitamount()
						: BigDecimal.ZERO);

				totalNOPrevReceipts = totalNOPrevReceipts
						.add(entry.getPrevDebitamount() != null
								? entry.getPrevDebitamount().compareTo(BigDecimal.ZERO) < 0
										? entry.getPrevDebitamount().negate()
										: entry.getPrevDebitamount()
								: BigDecimal.ZERO);

				totalNOPayments = totalNOPayments.add(entry.getCreditamount() != null
						? entry.getCreditamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getCreditamount().negate()
								: entry.getCreditamount()
						: BigDecimal.ZERO);

				totalNOPrevPayments = totalNOPrevPayments
						.add(entry.getPrevCreditamount() != null
								? entry.getPrevCreditamount().compareTo(BigDecimal.ZERO) < 0
										? entry.getPrevCreditamount().negate()
										: entry.getPrevCreditamount()
								: BigDecimal.ZERO);
			}

		}

		return new BSStatementEntry("", "Sub Total", totalNOPayments, totalNOReceipts, totalNOPrevPayments,
				totalNOPrevReceipts, true, 'T');
	}

	public void populateRPStatement(Statement receiptsAndPaymentsAccountStatement) {
		minorCodeLength = Integer.valueOf(getAppConfigValueFor(Constants.EGF, "coa_minorcode_length"));

		Date fromDate = getFromDate(receiptsAndPaymentsAccountStatement);
		Date toDate = getToDate(receiptsAndPaymentsAccountStatement);
		final List<Fund> fundList = receiptsAndPaymentsAccountStatement.getFunds();
		final String filterQuery = getFilterQuery(receiptsAndPaymentsAccountStatement);

		populateCurrentAndPreviousYearAmountForIE(receiptsAndPaymentsAccountStatement, filterQuery, toDate, fromDate,
				IE);
		removeFundsWithNoDataIE(receiptsAndPaymentsAccountStatement);

		populateCurrentAndPrevYearAmountForBS(receiptsAndPaymentsAccountStatement, fundList, filterQuery, toDate,
				fromDate, BS);

		removeEntrysWithZeroAmount(receiptsAndPaymentsAccountStatement);

	}

	public void populateCurrentAndPreviousYearAmountForIE(final Statement statement, final String filterQuery,
			final Date toDate, final Date fromDate, final String scheduleReportType) {

		final BigDecimal divisor = statement.getDivisor();
		final Statement income = new Statement();
		final Statement expenditure = new Statement();

		final List<StatementResultObject> allGlCodesWithoutFilter = getAllGlCodesFor(scheduleReportType);
		final List<StatementResultObject> allGlCodes = filterIEGlCodes(allGlCodesWithoutFilter);

		// get all net amount total
		final List<StatementResultObject> resultsIE = getTransactionAmount(filterQuery, toDate, fromDate, "'I','E'",
				IE);
		final List<StatementResultObject> preYearResultsIE = getTransactionAmount(filterQuery,
				getPreviousYearFor(toDate), getPreviousYearFor(fromDate), "'I','E'", scheduleReportType);

		for (final StatementResultObject queryObject : allGlCodes) {

			if (queryObject.getGlCode() == null)
				queryObject.setGlCode("");
			final List<StatementResultObject> rows = getRowWithGlCodeForRPAccount(resultsIE, queryObject.getGlCode());
			if (rows.isEmpty() && queryObject.getGlCode() != null) {
				if (contains(preYearResultsIE, queryObject.getGlCode())) {
					final List<StatementResultObject> preRow = getRowWithGlCode(preYearResultsIE,
							queryObject.getGlCode());
					final IEStatementEntry preentry = new IEStatementEntry();
					for (final StatementResultObject pre : preRow)
						if (I.equalsIgnoreCase(queryObject.getType().toString())) {
							if (pre.isIncome())
								pre.negateAmount();
							preentry.getPreviousYearAmount().put(
									getFundNameForId(statement.getFunds(), Integer.valueOf(pre.getFundId())),
									divideAndRound(pre.getAmount(), divisor));
							preentry.setType(pre.getType());
						} else if (E.equalsIgnoreCase(queryObject.getType().toString())) {
							if (pre.isIncome())
								pre.negateAmount();
							preentry.getPreviousYearAmount().put(
									getFundNameForId(statement.getFunds(), Integer.valueOf(pre.getFundId())),
									divideAndRound(pre.getAmount(), divisor));
							preentry.setType(pre.getType());
						}
					if (queryObject.getGlCode() != null) {
						preentry.setGlCode(queryObject.getGlCode());
						preentry.setAccountName(queryObject.getScheduleName());
						preentry.setScheduleNo(queryObject.getScheduleNumber());
					}
					if (I.equalsIgnoreCase(queryObject.getType().toString()))
						income.addIE(preentry);
					else if (E.equalsIgnoreCase(queryObject.getType().toString()))
						expenditure.addIE(preentry);
				}
			} else {

				for (final StatementResultObject row : rows) {
					if (row.isIncome())
						row.negateAmount();
					if (income.containsIEStatementEntry(row.getGlCode())
							|| expenditure.containsIEStatementEntry(row.getGlCode())) {
						if (I.equalsIgnoreCase(row.getType().toString()))
							addFundAmountIE(statement.getFunds(), income, divisor, row);
						else if (E.equalsIgnoreCase(row.getType().toString()))
							addFundAmountIE(statement.getFunds(), expenditure, divisor, row);
					} else {
						final IEStatementEntry entry = new IEStatementEntry();
						if (row.getAmount() != null && row.getFundId() != null) {
							entry.getNetAmount().put(
									getFundNameForId(statement.getFunds(), Integer.valueOf(row.getFundId())),
									divideAndRound(row.getAmount(), divisor));
							if (queryObject.getGlCode() != null && contains(preYearResultsIE, row.getGlCode())) {
								final List<StatementResultObject> preRow = getRowWithGlCode(preYearResultsIE,
										queryObject.getGlCode());
								for (final StatementResultObject pre : preRow) {
									if (pre.isIncome())
										pre.negateAmount();
									if (pre.getGlCode() != null && pre.getGlCode().equals(row.getGlCode())) {
										entry.getPreviousYearAmount()
												.put(getFundNameForId(statement.getFunds(),
														Integer.valueOf(pre.getFundId())),
														divideAndRound(pre.getAmount(), divisor));
									}
								}
							}
						}
						if (queryObject.getGlCode() != null) {
							entry.setGlCode(queryObject.getGlCode());
							entry.setAccountName(queryObject.getScheduleName());
							entry.setScheduleNo(queryObject.getScheduleNumber());
							entry.setType(queryObject.getType());
						}
						if (I.equalsIgnoreCase(row.getType().toString()))
							income.addIE(entry);
						else if (E.equalsIgnoreCase(row.getType().toString()))
							expenditure.addIE(entry);

					}
				}
			}
		}

		addRowsToStatement(statement, income, expenditure);
	}

	public void populateCurrentAndPrevYearAmountForBS(final Statement statement, final List<Fund> fundList,
			final String filterQuery, final Date toDate, final Date fromDate, final String scheduleReportType) {
		final Statement assets = new Statement();
		final Statement liabilities = new Statement();
		final BigDecimal divisor = statement.getDivisor();

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<StatementResultObject> allGlCodesWithoutFilter = getAllGlCodesFor(scheduleReportType);
		final List<StatementResultObject> allGlCodes = filterBSCodes(allGlCodesWithoutFilter);

		final List<StatementResultObject> results = getTransactionAmountBS(filterQuery, toDate, fromDate, "'L','A'",
				BS);

		final List<StatementResultObject> prevResults = getTransactionAmountBS(filterQuery, prevFormattedToDate,
				getPreviousYearFor(fromDate), "'L','A'", BS);

		for (final StatementResultObject queryObject : allGlCodes) {
			if (queryObject.getGlCode() == null)
				queryObject.setGlCode("");
			final List<StatementResultObject> rows = getRowWithGlCodeBS(results, queryObject.getGlCode());

			if (rows.isEmpty() && queryObject.getGlCode() != null) {
				if (contains(prevResults, queryObject.getGlCode())) {
					final List<StatementResultObject> prerows = getRowWithGlCodeBS(prevResults,
							queryObject.getGlCode());

					for (final StatementResultObject pre : prerows) {

						if (pre.isLiability())
							pre.negateCreditAmount();

						if (liabilities.containsBSStatementEntry(pre.getGlCode())
								|| assets.containsBSStatementEntry(pre.getGlCode())) {
							if (pre.isLiability())
								addFundAmountBSPre(fundList, liabilities, divisor, pre);
							else
								addFundAmountBSPre(fundList, assets, divisor, pre);
						} else {
							final BSStatementEntry balanceSheetEntry = new BSStatementEntry();
							if (pre.getCreditamount() != null || pre.getDebitamount() != null) {
								balanceSheetEntry.setPrevCreditamount(
										pre.getCreditamount() == null ? BigDecimal.ZERO : pre.getCreditamount());
								balanceSheetEntry.setPrevDebitamount(
										pre.getDebitamount() == null ? BigDecimal.ZERO : pre.getDebitamount());
							}

							balanceSheetEntry.setType(pre.getType());

							if (queryObject.getGlCode() != null) {
								balanceSheetEntry.setGlCode(queryObject.getGlCode());
								balanceSheetEntry.setAccountName(queryObject.getScheduleName());
							}

							if (pre.isLiability())
								liabilities.addBS(balanceSheetEntry);
							else
								assets.addBS(balanceSheetEntry);

						}

					}

				}
			} else
				for (final StatementResultObject row : rows) {
					if (row.isLiability())
						row.negateCreditAmount();
					if (liabilities.containsBSStatementEntry(row.getGlCode())
							|| assets.containsBSStatementEntry(row.getGlCode())) {
						if (row.isLiability())
							addFundAmountBS(fundList, liabilities, divisor, row);
						else
							addFundAmountBS(fundList, assets, divisor, row);
					} else {
						final BSStatementEntry balanceSheetEntry = new BSStatementEntry();

						if (row.getCreditamount() != null || row.getDebitamount() != null) {
							balanceSheetEntry.setCreditamount(
									row.getCreditamount() == null ? BigDecimal.ZERO : row.getCreditamount());
							balanceSheetEntry.setDebitamount(
									row.getDebitamount() == null ? BigDecimal.ZERO : row.getDebitamount());
						}

						if (queryObject.getGlCode() != null && contains(prevResults, row.getGlCode())) {
							final List<StatementResultObject> preRow = getRowWithGlCodeBS(prevResults,
									queryObject.getGlCode());
							for (final StatementResultObject pre : preRow) {
								if (pre.isLiability())
									pre.negateCreditAmount();
								if (pre.getGlCode() != null && pre.getGlCode().equals(pre.getGlCode())) {
									balanceSheetEntry.setPrevCreditamount(
											pre.getCreditamount() == null ? BigDecimal.ZERO : pre.getCreditamount());
									balanceSheetEntry.setPrevDebitamount(
											pre.getDebitamount() == null ? BigDecimal.ZERO : pre.getDebitamount());

								}
							}
						}

						balanceSheetEntry.setType(row.getType());

						if (queryObject.getGlCode() != null) {
							balanceSheetEntry.setGlCode(queryObject.getGlCode());
							balanceSheetEntry.setAccountName(queryObject.getScheduleName());
						}

						if (row.isLiability())
							liabilities.addBS(balanceSheetEntry);
						else
							assets.addBS(balanceSheetEntry);
					}
				}

		}
		addRowsToStatementBS(statement, assets, liabilities);

	}

	public void populatePreviousYearTotalsBS(final Statement balanceSheet, final String filterQuery, final Date toDate,
			final Date fromDate, final String reportSubType, final String coaType) {

		final boolean newbalanceSheet = balanceSheet.sizeBS() > 2 ? false : true;
		final Statement assets = new Statement();
		final Statement liabilities = new Statement();
		Date formattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(balanceSheet.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			formattedToDate = cal.getTime();
		} else
			formattedToDate = getPreviousYearFor(toDate);

		final List<StatementResultObject> results = getTransactionAmountBS(filterQuery, formattedToDate,
				getPreviousYearFor(fromDate), coaType, reportSubType);
		for (final StatementResultObject row : results)
			if (balanceSheet.containsBSStatementEntry(row.getGlCode())) {
				for (int index = 0; index < balanceSheet.sizeBS(); index++)
					if (balanceSheet.getBS(index).getGlCode() != null
							&& row.getGlCode().equals(balanceSheet.getBS(index).getGlCode())) {
						if (row.isLiability())
							row.negateCreditAmount();

						BigDecimal prevCredit = row.getCreditamount();
						prevCredit = prevCredit == null ? BigDecimal.ZERO : prevCredit;

						BigDecimal prevDebit = row.getDebitamount();
						prevDebit = prevDebit == null ? BigDecimal.ZERO : prevDebit;

						balanceSheet.getBS(index).setPrevCreditamount(prevCredit);
						balanceSheet.getBS(index).setPrevDebitamount(prevDebit);

					}
			} else {
				if (row.isLiability())
					row.negateCreditAmount();
				final BSStatementEntry balanceSheetEntry = new BSStatementEntry();
				if (row.getCreditamount() != null || row.getDebitamount() != null) {
//					balanceSheetEntry.setPreviousYearTotal(divideAndRound(row.getAmount(), divisor));
					balanceSheetEntry.setPrevCreditamount(row.getCreditamount());
					balanceSheetEntry.setPrevDebitamount(row.getDebitamount());
					balanceSheetEntry.setCreditamount(BigDecimal.ZERO);
					balanceSheetEntry.setDebitamount(BigDecimal.ZERO);
				}
				if (row.getGlCode() != null) {
					balanceSheetEntry.setGlCode(row.getGlCode());
					balanceSheetEntry.setAccountName(row.getScheduleName());
				}
				if (row.isLiability())
					liabilities.addBS(balanceSheetEntry);
				else
					assets.addBS(balanceSheetEntry);
			}
		if (newbalanceSheet)
			addRowsToStatementBS(balanceSheet, assets, liabilities);
	}

	boolean contains(final List<StatementResultObject> result, final String glCode) {
		for (final StatementResultObject row : result)
			if (row.getGlCode() != null && row.getGlCode().equalsIgnoreCase(glCode))
				return true;
		return false;
	}

	/*
	 * Calculate total Income of current year and previous year
	 */
	private IEStatementEntry getTotalIncomeFundwise(final Statement income_expense) {
		final Map<String, BigDecimal> fundNetTotals = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> fundPreTotals = new HashMap<String, BigDecimal>();
		BigDecimal netAmount = BigDecimal.ZERO;
		BigDecimal preAmount = BigDecimal.ZERO;
		for (final IEStatementEntry entry : income_expense.getIeEntries()) {

			for (final Entry<String, BigDecimal> row : entry.getNetAmount().entrySet()) {
				if (fundNetTotals.get(row.getKey()) == null)
					fundNetTotals.put(row.getKey(), BigDecimal.ZERO);
				netAmount = zeroOrValue(row.getValue());
				fundNetTotals.put(row.getKey(), netAmount.add(zeroOrValue(fundNetTotals.get(row.getKey()))));
			}
			for (final Entry<String, BigDecimal> prerow : entry.getPreviousYearAmount().entrySet()) {
				if (fundPreTotals.get(prerow.getKey()) == null)
					fundPreTotals.put(prerow.getKey(), BigDecimal.ZERO);
				preAmount = zeroOrValue(prerow.getValue());
				fundPreTotals.put(prerow.getKey(), preAmount.add(zeroOrValue(fundPreTotals.get(prerow.getKey()))));
			}
		}
		// return new IEStatementEntry("A", Constants.TOTAL_INCOME, fundNetTotals,
		// fundPreTotals, true);
		return new IEStatementEntry(" ", "Sub Total", fundNetTotals, fundPreTotals, true, 'E');
	}

	/*
	 * Calculate total Expenditure of current year and previous year
	 */
	private IEStatementEntry getTotalExpenseFundwise(final Statement income_expense) {

		final Map<String, BigDecimal> fundNetTotals = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> fundPreTotals = new HashMap<String, BigDecimal>();
		BigDecimal netAmount = BigDecimal.ZERO;
		BigDecimal preAmount = BigDecimal.ZERO;
		for (final IEStatementEntry entry : income_expense.getIeEntries()) {

			for (final Entry<String, BigDecimal> row : entry.getNetAmount().entrySet()) {
				if (fundNetTotals.get(row.getKey()) == null)
					fundNetTotals.put(row.getKey(), BigDecimal.ZERO);
				netAmount = zeroOrValue(row.getValue());
				fundNetTotals.put(row.getKey(), netAmount.add(zeroOrValue(fundNetTotals.get(row.getKey()))));
			}
			for (final Entry<String, BigDecimal> prerow : entry.getPreviousYearAmount().entrySet()) {
				if (fundPreTotals.get(prerow.getKey()) == null)
					fundPreTotals.put(prerow.getKey(), BigDecimal.ZERO);
				preAmount = zeroOrValue(prerow.getValue());
				fundPreTotals.put(prerow.getKey(), preAmount.add(zeroOrValue(fundPreTotals.get(prerow.getKey()))));
			}
		}
		// return new IEStatementEntry("B", Constants.TOTAL_EXPENDITURE, fundNetTotals,
		// fundPreTotals, true);
		return new IEStatementEntry(" ", "Sub Total", fundNetTotals, fundPreTotals, true, 'I');
	}

	private BSStatementEntry getTotalBS_old(final Statement statement, final String type1, final String type2) {

		BigDecimal totalCurrentCreditAmount = BigDecimal.ZERO;
		BigDecimal totalCurrentDebitAmount = BigDecimal.ZERO;
		BigDecimal totalPrevCreditAmount = BigDecimal.ZERO;
		BigDecimal totalPrevDebitAmount = BigDecimal.ZERO;

		for (final BSStatementEntry balanceSheetEntry : statement.getBsEntries()) {

			if (type1.equals(balanceSheetEntry.getAccountName()) || type2.equals(balanceSheetEntry.getAccountName())
					|| balanceSheetEntry.isDisplayBold())
				continue;
			totalCurrentCreditAmount = balanceSheetEntry.getCreditamount() != null
					? (balanceSheetEntry.getCreditamount().compareTo(BigDecimal.ZERO) < 0
							? balanceSheetEntry.getCreditamount().negate().add(totalCurrentCreditAmount)
							: balanceSheetEntry.getCreditamount().add(totalCurrentCreditAmount))
					: totalCurrentCreditAmount;
			totalCurrentDebitAmount = balanceSheetEntry.getDebitamount() != null
					? balanceSheetEntry.getDebitamount().compareTo(BigDecimal.ZERO) < 0
							? balanceSheetEntry.getDebitamount().negate().add(totalCurrentDebitAmount)
							: balanceSheetEntry.getDebitamount().add(totalCurrentDebitAmount)
					: totalCurrentDebitAmount;
		}

		for (final BSStatementEntry balanceSheetEntry : statement.getBsEntries()) {

			if (type1.equals(balanceSheetEntry.getAccountName()) || type2.equals(balanceSheetEntry.getAccountName())
					|| balanceSheetEntry.isDisplayBold())
				continue;
			totalPrevCreditAmount = balanceSheetEntry.getPrevCreditamount() != null
					? balanceSheetEntry.getPrevCreditamount().compareTo(BigDecimal.ZERO) < 0
							? balanceSheetEntry.getPrevCreditamount().negate().add(totalPrevCreditAmount)
							: balanceSheetEntry.getPrevCreditamount().add(totalPrevCreditAmount)
					: totalPrevCreditAmount;
			totalPrevDebitAmount = balanceSheetEntry.getPrevDebitamount() != null
					? balanceSheetEntry.getPrevDebitamount().compareTo(BigDecimal.ZERO) < 0
							? balanceSheetEntry.getPrevDebitamount().negate().add(totalPrevDebitAmount)
							: balanceSheetEntry.getPrevDebitamount().add(totalPrevDebitAmount)
					: totalPrevDebitAmount;

		}
		return new BSStatementEntry(" ", "Sub Total", totalCurrentDebitAmount, totalCurrentCreditAmount,
				totalPrevDebitAmount, totalPrevCreditAmount, true, Character.MIN_VALUE);
	}

	/*
	 * Computes income over expenditure and vise versa for current year amount and
	 * previous year amount
	 */
	private List<IEStatementEntry> computeTotalsIncomeExpense(final IEStatementEntry incomeFundTotals,
			final IEStatementEntry expenditureFundTotals) {
		final Map<String, BigDecimal> netTotal = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> preTotal = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> netTotalin_ex = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> preTotalin_ex = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> netTotalex_in = new HashMap<String, BigDecimal>();
		final Map<String, BigDecimal> preTotalex_in = new HashMap<String, BigDecimal>();
		Set<String> netFundSet = new HashSet<String>();
		Set<String> preFundSet = new HashSet<String>();
		BigDecimal curAmount = BigDecimal.ZERO;
		final String prevoius = "PREVIOUS";
		final String current = "CURRENT";
		netFundSet = getAllKey(incomeFundTotals, expenditureFundTotals, current);
		preFundSet = getAllKey(incomeFundTotals, expenditureFundTotals, prevoius);
		// Entry<String, BigDecimal> prerow;
		final IEStatementEntry income = new IEStatementEntry();
		final IEStatementEntry expense = new IEStatementEntry();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Calculating income over expenses");
		final List<IEStatementEntry> incomeOverExpenditure = new ArrayList<IEStatementEntry>();
		for (final String str : netFundSet)
			if (incomeFundTotals.getNetAmount().containsKey(str)) {
				final BigDecimal amount = zeroOrValue(incomeFundTotals.getNetAmount().get(str));
				netTotal.put(str, amount.subtract(zeroOrValue(expenditureFundTotals.getNetAmount().get(str))));
			} else if (expenditureFundTotals.getNetAmount().containsKey(str)
					&& !incomeFundTotals.getNetAmount().containsKey(str)) {
				final BigDecimal amount = zeroOrValue(incomeFundTotals.getNetAmount().get(str));
				netTotal.put(str, amount.subtract(zeroOrValue(expenditureFundTotals.getNetAmount().get(str))));
			}
		for (final String pstr : preFundSet)
			if (incomeFundTotals.getPreviousYearAmount().containsKey(pstr)) {
				final BigDecimal amount = zeroOrValue(incomeFundTotals.getPreviousYearAmount().get(pstr));
				preTotal.put(pstr,
						amount.subtract(zeroOrValue(expenditureFundTotals.getPreviousYearAmount().get(pstr))));
			} else if (expenditureFundTotals.getPreviousYearAmount().containsKey(pstr)
					&& !incomeFundTotals.getPreviousYearAmount().containsKey(pstr)) {
				zeroOrValue(incomeFundTotals.getPreviousYearAmount().get(pstr));
				preTotal.put(pstr, expenditureFundTotals.getPreviousYearAmount().get(pstr));
			}

		for (final String str : netFundSet) {
			final int isIncome = netTotal.get(str).signum();
			if (isIncome > 0) {
				netTotalin_ex.put(str, netTotal.get(str));
				income.setGlCode("A-B");
				income.setAccountName("Income Over Expenditure");
				income.setDisplayBold(true);
				income.setNetAmount(netTotalin_ex);
			} else {
				curAmount = zeroOrValue(netTotal.get(str)).negate();
				netTotalex_in.put(str, curAmount);
				expense.setGlCode("B-A");
				expense.setAccountName("Expenditure Over Income");
				expense.setNetAmount(netTotalex_in);
				expense.setDisplayBold(true);
			}

		}

		for (final String str : preFundSet) {
			final int isIncome = preTotal.get(str).signum();
			if (isIncome > 0) {
				if (income.getGlCode() != null) {
					preTotalin_ex.put(str, preTotal.get(str));
					income.setPreviousYearAmount(preTotalin_ex);
				} else {
					preTotalin_ex.put(str, preTotal.get(str));
					income.setPreviousYearAmount(preTotalin_ex);
					income.setGlCode("A-B");
					income.setAccountName("Income Over Expenditure");
					income.setDisplayBold(true);
					preTotalin_ex.put(str, preTotal.get(str));
					income.setPreviousYearAmount(preTotalin_ex);
				}
			} else if (expense.getGlCode() != null) {
				preTotalex_in.put(str, preTotal.get(str).negate());
				expense.setPreviousYearAmount(preTotalex_in);
			} else {
				curAmount = zeroOrValue(preTotal.get(str)).negate();
				preTotalex_in.put(str, curAmount);
				expense.setGlCode("B-A");
				expense.setAccountName("Expenditure Over Income");
				expense.setDisplayBold(true);
				preTotalex_in.put(str, curAmount);
				expense.setPreviousYearAmount(preTotalex_in);

			}
		}
		incomeOverExpenditure.add(income);
		incomeOverExpenditure.add(expense);
		return incomeOverExpenditure;
	}

	/*
	 * Returns All Fund id for which transaction is made previous year or this year
	 */
	private HashSet<String> getAllKey(final IEStatementEntry incomeFundTotals,
			final IEStatementEntry expenditureFundTotals, final String amtType) {

		final Set<String> allFundSet = new HashSet<String>();
		if (amtType.equals("CURRENT")) {
			for (final Entry<String, BigDecimal> row : incomeFundTotals.getNetAmount().entrySet())
				allFundSet.add(row.getKey());
			for (final Entry<String, BigDecimal> row : expenditureFundTotals.getNetAmount().entrySet())
				allFundSet.add(row.getKey());
		} else {
			for (final Entry<String, BigDecimal> row : incomeFundTotals.getPreviousYearAmount().entrySet())
				allFundSet.add(row.getKey());
			for (final Entry<String, BigDecimal> row : expenditureFundTotals.getPreviousYearAmount().entrySet())
				allFundSet.add(row.getKey());
		}

		return (HashSet) allFundSet;

	}

	private List<StatementResultObject> filterIEGlCodes(List<StatementResultObject> allGlCodes) {

		List<StatementResultObject> results = new ArrayList<>();

		ArrayList<String> desiredGlCodes = new ArrayList<String>(Arrays.asList("110", "120", "130", "140", "150", "160",
				"170", "171", "180", "210", "220", "230", "240", "250", "260"));

		for (StatementResultObject glcode : allGlCodes) {
			if (desiredGlCodes.contains(glcode.getGlCode())) {
				results.add(glcode);
			}
		}

		return results;
	}

	private List<StatementResultObject> filterBSCodes(List<StatementResultObject> allGlCodes) {
		List<StatementResultObject> results = new ArrayList<>();

		ArrayList<String> desiredGlCodes = new ArrayList<String>(Arrays.asList("310", "312", "320", "330", "331", "340",
				"341", "350", "360", "410", "412", "420", "430", "431", "440", "460", "470"));

		for (StatementResultObject glcode : allGlCodes) {
			if (desiredGlCodes.contains(glcode.getGlCode())) {
				results.add(glcode);
			}
		}

		return results;
	}

	public List<Fund> getFunds() {
		final Criteria voucherHeaderCriteria = persistenceService.getSession().createCriteria(CVoucherHeader.class);
		final List fundIdList = voucherHeaderCriteria
				.setProjection(Projections.distinct(Projections.property("fundId.id"))).list();
		if (!fundIdList.isEmpty())
			return persistenceService.getSession().createCriteria(Fund.class).add(Restrictions.in("id", fundIdList))
					.list();
		return new ArrayList<Fund>();
	}

	private void removeEntrysWithZeroAmount(final Statement balanceSheet) {
		final List<BSStatementEntry> list = new LinkedList<BSStatementEntry>();
		Boolean check;
//		Map<String, BigDecimal> FundWiseAmount = new HashMap<String, BigDecimal>();
		BigDecimal totalCurrentCreditAmount = BigDecimal.ZERO;
		BigDecimal totalCurrentDebitAmount = BigDecimal.ZERO;
		BigDecimal totalPrevCreditAmount = BigDecimal.ZERO;
		BigDecimal totalPrevDebitAmount = BigDecimal.ZERO;

		for (final BSStatementEntry entry : balanceSheet.getBsEntries())
			if (entry.getGlCode() != null && !entry.getGlCode().equalsIgnoreCase("")) {

				check = false;

				totalCurrentCreditAmount = entry.getCreditamount();
				totalCurrentDebitAmount = entry.getDebitamount();
				totalPrevCreditAmount = entry.getPrevCreditamount();
				totalPrevDebitAmount = entry.getPrevDebitamount();

				if (totalCurrentCreditAmount != null || totalCurrentDebitAmount != null || totalPrevCreditAmount != null
						|| totalPrevDebitAmount != null) {

					check = true;

				}

				if (check) {
					list.add(entry);
				}

			} else
				list.add(entry);
		balanceSheet.setBsEntries(new LinkedList<BSStatementEntry>());
		balanceSheet.setBsEntries(list);
	}

}