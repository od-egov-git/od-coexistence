package org.egov.services.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.egf.model.BSStatementEntry;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.OpeningAndClosingBalanceEntry;
import org.egov.egf.model.RPAccountEntry;
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

	BigDecimal totalReceipts = BigDecimal.ZERO;
	BigDecimal totalPayments = BigDecimal.ZERO;
	BigDecimal prevTotalReceipts = BigDecimal.ZERO;
	BigDecimal prevTotalPayments = BigDecimal.ZERO;

	int CoaClassification = 1;

	@Override
	protected void addRowsToStatement(Statement statement, Statement assets, Statement liabilities) {

		Date fromDate = getFromDate(statement);
		Date toDate = getToDate(statement);

		IEStatementEntry incomeEntry = new IEStatementEntry();
		IEStatementEntry expenseEntry = new IEStatementEntry();
		IEStatementEntry totalCashOpeningEntry = new IEStatementEntry();
		IEStatementEntry totalBankOpeningEntry = new IEStatementEntry();
		IEStatementEntry totalOpeningEntry = new IEStatementEntry();

		totalCashOpeningEntry = getTotalCashOpening(statement, fromDate, toDate);
		totalBankOpeningEntry = getTotalBankOpening(statement, fromDate, toDate);
		totalOpeningEntry = getTotalOpeningFinal(statement, totalCashOpeningEntry, totalBankOpeningEntry);

		statement.addIE(new IEStatementEntry("", "Opening Balances", null, null, true, 'O'));
		statement.addIE(totalCashOpeningEntry);
		statement.addIE(totalBankOpeningEntry);
		statement.addIE(totalOpeningEntry);
		statement.addIE(new IEStatementEntry("", "", null, null, true, 'O'));

		if (assets.sizeIE() > 0) {
			statement.addIE(new IEStatementEntry(null, Constants.EXPENDITURE, "", true));
			expenseEntry = getTotalExpenseFundwise(statement, assets);
			statement.addAllIE(assets);
			statement.addIE(expenseEntry);
			statement.addIE(new IEStatementEntry(null, "", "", false));
		}

		if (liabilities.sizeIE() > 0) {
			statement.addIE(new IEStatementEntry(null, Constants.INCOME, "", true));
			incomeEntry = getTotalIncomeFundwise(statement, liabilities);
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addIE(new IEStatementEntry("", "", null, null, true, 'E'));
			statement.addAllIE(liabilities);
			statement.addIE(incomeEntry);
			statement.addIE(new IEStatementEntry(null, "", "", false));
		}

	}

	protected void addRowsToStatementBS(Statement statement, Statement assets, Statement liabilities) {

		Date fromDate = getFromDate(statement);
		Date toDate = getToDate(statement);

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

		BSStatementEntry totalEntry = new BSStatementEntry();
		totalEntry = getTotalBS(statement);
		statement.addBS(totalEntry);
		statement.addBS(new BSStatementEntry("", "", totalPayments, totalReceipts, prevTotalPayments, prevTotalReceipts,
				true, 'R'));

		BSStatementEntry totalCashOpeningEntry = new BSStatementEntry();
		BSStatementEntry totalBankOpeningEntry = new BSStatementEntry();
		BSStatementEntry totalOpeningEntry = new BSStatementEntry();

		totalCashOpeningEntry = getTotalCashOpeningForClosing(statement, fromDate, toDate);
		totalBankOpeningEntry = getTotalBankOpeningForClosing(statement, fromDate, toDate);
		totalOpeningEntry = getTotalClosingFinal(totalCashOpeningEntry, totalBankOpeningEntry);

		statement.addBS(new BSStatementEntry("", "", null, null, null, null, true, 'C'));
		statement.addBS(new BSStatementEntry("", "Closing Balances", null, null, null, null, true, 'C'));
		statement.addBS(totalCashOpeningEntry);
		statement.addBS(totalBankOpeningEntry);
		statement.addBS(totalOpeningEntry);

	}

	private IEStatementEntry getTotalOpeningFinal(Statement statement, IEStatementEntry totalCashOpeningEntry,
			IEStatementEntry totalBankOpeningEntry) {

		int fundid = 1;
		final IEStatementEntry resultEntry = new IEStatementEntry();
		BigDecimal totalOp = BigDecimal.ZERO;
		BigDecimal prevTotalOp = BigDecimal.ZERO;

		totalOp = totalCashOpeningEntry.getNetAmount().get(getFundNameForId(statement.getFunds(), fundid))
				.add(totalBankOpeningEntry.getNetAmount().get(getFundNameForId(statement.getFunds(), fundid)));

		prevTotalOp = totalCashOpeningEntry.getPreviousYearAmount().get(getFundNameForId(statement.getFunds(), fundid))
				.add(totalBankOpeningEntry.getPreviousYearAmount().get(getFundNameForId(statement.getFunds(), fundid)));
		totalReceipts = BigDecimal.ZERO;
		prevTotalReceipts = BigDecimal.ZERO;

		totalReceipts = totalReceipts.add(totalOp != null ? totalOp : BigDecimal.ZERO);
		prevTotalReceipts = prevTotalReceipts.add(prevTotalOp != null ? prevTotalOp : BigDecimal.ZERO);

		resultEntry.getNetAmount().put(getFundNameForId(statement.getFunds(), fundid), totalOp);
		resultEntry.getPreviousYearAmount().put(getFundNameForId(statement.getFunds(), fundid), prevTotalOp);
		resultEntry.setGlCode("");
		resultEntry.setType('O');
		resultEntry.setAccountName("Total Opening Balance");
		return resultEntry;
	}

	private BSStatementEntry getTotalClosingFinal(BSStatementEntry totalCashOpeningEntry,
			BSStatementEntry totalBankOpeningEntry) {

		final BSStatementEntry resultEntry = new BSStatementEntry();
		BigDecimal totalOp = BigDecimal.ZERO;
		BigDecimal prevTotalOp = BigDecimal.ZERO;

		totalOp = totalCashOpeningEntry.getDebitamount().add(totalBankOpeningEntry.getDebitamount());

		prevTotalOp = totalCashOpeningEntry.getPrevDebitamount().add(totalBankOpeningEntry.getPrevDebitamount());

		resultEntry.setDebitamount(totalOp);
		resultEntry.setPrevDebitamount(prevTotalOp);
		resultEntry.setGlCode("");
		resultEntry.setType('C');
		resultEntry.setAccountName("Total Closing Balance");
		return resultEntry;
	}

	private IEStatementEntry getTotalCashOpening(Statement statement, Date fromDate, Date toDate) {

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

		final List<OpeningAndClosingBalanceEntry> tillDateResults = getTillDateOpeningBalForOpening(statement, fromDate,
				toDate);
		final List<OpeningAndClosingBalanceEntry> tillDatePrevResults = getTillDateOpeningBalForOpening(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		BigDecimal tillDateCashOpeningBalance = BigDecimal.ZERO;
		BigDecimal tillDatePrevCashOpeningBalance = BigDecimal.ZERO;

		if (!tillDateResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDateResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4501) {
						tillDateCashOpeningBalance = tillDateCashOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));

					}

				}

			}
		}

		if (!tillDatePrevResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDatePrevResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4501) {
						tillDatePrevCashOpeningBalance = tillDatePrevCashOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}

				}
			}
		}

		BigDecimal finalCashOpeningBalance = BigDecimal.ZERO;
		BigDecimal finalPrevCashOpeningBalance = BigDecimal.ZERO;

		finalCashOpeningBalance = tillDateCashOpeningBalance.add(totalCashOpeningBalance);
		finalPrevCashOpeningBalance = tillDatePrevCashOpeningBalance.add(totalPrevCashOpeningBalance);

		resultEntry.getPreviousYearAmount().put(getFundNameForId(statement.getFunds(), fundid),
				finalPrevCashOpeningBalance);
		resultEntry.getNetAmount().put(getFundNameForId(statement.getFunds(), fundid), finalCashOpeningBalance);
		resultEntry.setGlCode("");
		resultEntry.setType('O');
		resultEntry.setGlCode("");
		resultEntry.setAccountName("a. Cash Balances");

		return resultEntry;

	}

	private BSStatementEntry getTotalCashOpeningForClosing(Statement statement, Date fromDate, Date toDate) {

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<OpeningAndClosingBalanceEntry> results = getOpeningBalanceForRP(statement, fromDate, toDate);
		final List<OpeningAndClosingBalanceEntry> prevResults = getOpeningBalanceForRP(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		final BSStatementEntry resultEntry = new BSStatementEntry();

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

		final List<OpeningAndClosingBalanceEntry> tillDateResults = getTillDateOpeningBalanceForRP(statement, fromDate,
				toDate);
		final List<OpeningAndClosingBalanceEntry> tillDatePrevResults = getTillDateOpeningBalanceForRP(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		BigDecimal tillDateCashOpeningBalance = BigDecimal.ZERO;
		BigDecimal tillDatePrevCashOpeningBalance = BigDecimal.ZERO;

		if (!tillDateResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDateResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4501) {
						tillDateCashOpeningBalance = tillDateCashOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));

					}

				}

			}
		}

		if (!tillDatePrevResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDatePrevResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4501) {
						tillDatePrevCashOpeningBalance = tillDatePrevCashOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}

				}
			}
		}

		BigDecimal finalCashOpeningBalance = BigDecimal.ZERO;
		BigDecimal finalPrevCashOpeningBalance = BigDecimal.ZERO;

		finalCashOpeningBalance = tillDateCashOpeningBalance.add(totalCashOpeningBalance);
		finalPrevCashOpeningBalance = tillDatePrevCashOpeningBalance.add(totalPrevCashOpeningBalance);

		resultEntry.setDebitamount(finalCashOpeningBalance);
		resultEntry.setPrevDebitamount(finalPrevCashOpeningBalance);
		resultEntry.setGlCode("");
		resultEntry.setType('C');
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

		final List<OpeningAndClosingBalanceEntry> tillDateResults = getTillDateOpeningBalForOpening(statement, fromDate,
				toDate);
		final List<OpeningAndClosingBalanceEntry> tillDatePrevResults = getTillDateOpeningBalForOpening(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		BigDecimal tillDateBankOpeningBalance = BigDecimal.ZERO;
		BigDecimal tillDatePrevBankOpeningBalance = BigDecimal.ZERO;

		if (!tillDateResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDateResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4502) {
						tillDateBankOpeningBalance = tillDateBankOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));

					}

				}

			}
		}

		if (!tillDatePrevResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDatePrevResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4502) {
						tillDatePrevBankOpeningBalance = tillDatePrevBankOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}

				}
			}
		}

		BigDecimal finalBankOpeningBalance = BigDecimal.ZERO;
		BigDecimal finalPrevBankOpeningBalance = BigDecimal.ZERO;

		finalBankOpeningBalance = tillDateBankOpeningBalance.add(totalBankOpeningBalance);
		finalPrevBankOpeningBalance = tillDatePrevBankOpeningBalance.add(totalPrevBankOpeningBalance);

		resultEntry.getPreviousYearAmount().put(getFundNameForId(statement.getFunds(), fundid),
				finalPrevBankOpeningBalance);
		resultEntry.getNetAmount().put(getFundNameForId(statement.getFunds(), fundid), finalBankOpeningBalance);
		resultEntry.setGlCode("");
		resultEntry.setType('O');
		resultEntry.setGlCode("");
		resultEntry.setAccountName("b. Bank Balances");

		return resultEntry;

	}

	private BSStatementEntry getTotalBankOpeningForClosing(Statement statement, Date fromDate, Date toDate) {

		Date prevFormattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(statement.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			prevFormattedToDate = cal.getTime();
		} else
			prevFormattedToDate = getPreviousYearFor(toDate);

		final List<OpeningAndClosingBalanceEntry> results = getOpeningBalanceForRP(statement, fromDate, toDate);
		final List<OpeningAndClosingBalanceEntry> prevResults = getOpeningBalanceForRP(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

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

		final List<OpeningAndClosingBalanceEntry> tillDateBankResults = getTillDateOpeningBalanceForRP(statement,
				fromDate, toDate);
		final List<OpeningAndClosingBalanceEntry> prevTillDateBankResults = getTillDateOpeningBalanceForRP(statement,
				getPreviousYearFor(fromDate), prevFormattedToDate);

		final BSStatementEntry resultEntry = new BSStatementEntry();

		BigDecimal tillDatePrevBankOpeningBalance = BigDecimal.ZERO;
		BigDecimal tillDateBankOpeningBalance = BigDecimal.ZERO;

		if (!prevTillDateBankResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : prevTillDateBankResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4502) {
						tillDatePrevBankOpeningBalance = tillDatePrevBankOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}
				}

			}
		}

		if (!tillDateBankResults.isEmpty()) {
			for (OpeningAndClosingBalanceEntry entry : tillDateBankResults) {

				String cashGlCode = entry.getGlcode().substring(0, 4);

				if (cashGlCode != null) {
					int shortGlcode = Integer.parseInt(cashGlCode);

					if (shortGlcode == 4502) {
						tillDateBankOpeningBalance = tillDateBankOpeningBalance
								.add(entry.getOpeningDebitBalance().subtract(entry.getOpeningCreditBalance()));
					}

				}

			}
		}

		BigDecimal finalPrevBankOpeningBalance = BigDecimal.ZERO;
		BigDecimal finalBankOpeningBalance = BigDecimal.ZERO;

		finalBankOpeningBalance = tillDateBankOpeningBalance.add(totalBankOpeningBalance);
		finalPrevBankOpeningBalance = tillDatePrevBankOpeningBalance.add(totalPrevBankOpeningBalance);

		resultEntry.setDebitamount(finalBankOpeningBalance);
		resultEntry.setPrevDebitamount(finalPrevBankOpeningBalance);
		resultEntry.setGlCode("");
		resultEntry.setType('C');
		resultEntry.setAccountName("b. Bank Balances");

		return resultEntry;

	}

	private List<OpeningAndClosingBalanceEntry> getOpeningBalanceForRP(Statement statement, Date fromDate,
			Date toDate) {

		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");

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

	private List<OpeningAndClosingBalanceEntry> getTillDateOpeningBalanceForRP(Statement statement, Date fromDate,
			Date toDate) {

		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");

		final String tillDateopeningBalanceStr = "SELECT coa.glcode AS glcode ,coa.name  AS accName, SUM(gl.creditAmount) as openingCreditBalance,sum(gl.debitAmount) as openingDebitBalance"
				+ " FROM generalledger  gl,chartofaccounts coa,financialyear fy,Voucherheader vh "
				+ " WHERE gl.glcodeid=coa.id and vh.id=gl.voucherheaderid "
				+ " AND vh.voucherdate>=fy.startingdate AND vh.voucherdate<= :toDate1"
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate" + " AND vh.status not in ("
				+ voucherStatusToExclude + ")" + " GROUP BY gl.glcodeid,coa.glcode,coa.name ORDER BY coa.glcode ASC";

		final Query tillDateopeningBalanceQry = persistenceService.getSession()
				.createSQLQuery(tillDateopeningBalanceStr).addScalar("glcode").addScalar("accName")
				.addScalar("openingCreditBalance", BigDecimalType.INSTANCE)
				.addScalar("openingDebitBalance", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(OpeningAndClosingBalanceEntry.class));

		tillDateopeningBalanceQry.setDate("toDate1", toDate);
		tillDateopeningBalanceQry.setDate("fromDate", fromDate);
		tillDateopeningBalanceQry.setDate("toDate", toDate);

		return tillDateopeningBalanceQry.list();
	}

	private List<OpeningAndClosingBalanceEntry> getTillDateOpeningBalForOpening(Statement statement, Date fromDate,
			Date toDate) {

		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");

		final String tillDateopeningBalanceStr = "SELECT coa.glcode AS glcode ,coa.name  AS accName, SUM(gl.creditAmount) as openingCreditBalance,sum(gl.debitAmount) as openingDebitBalance"
				+ " FROM generalledger  gl,chartofaccounts coa,financialyear fy,Voucherheader vh "
				+ " WHERE gl.glcodeid=coa.id and vh.id=gl.voucherheaderid "
				+ " AND vh.voucherdate>=fy.startingdate AND vh.voucherdate<= :fromDateMinus1"
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate" + " AND vh.status not in ("
				+ voucherStatusToExclude + ")" + " GROUP BY gl.glcodeid,coa.glcode,coa.name ORDER BY coa.glcode ASC";

		final Query tillDateopeningBalanceQry = persistenceService.getSession()
				.createSQLQuery(tillDateopeningBalanceStr).addScalar("glcode").addScalar("accName")
				.addScalar("openingCreditBalance", BigDecimalType.INSTANCE)
				.addScalar("openingDebitBalance", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(OpeningAndClosingBalanceEntry.class));

		tillDateopeningBalanceQry.setDate("fromDate", fromDate);
		tillDateopeningBalanceQry.setDate("toDate", toDate);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, -1);
		tillDateopeningBalanceQry.setDate("fromDateMinus1", cal.getTime());

		return tillDateopeningBalanceQry.list();
	}

	private BSStatementEntry getTotalBS(Statement statement) {

		List<BSStatementEntry> bsEntries = statement.getBsEntries();

		BigDecimal totalNOReceipts = BigDecimal.ZERO;
		BigDecimal totalNOPayments = BigDecimal.ZERO;
		BigDecimal totalNOPrevReceipts = BigDecimal.ZERO;
		BigDecimal totalNOPrevPayments = BigDecimal.ZERO;

		for (BSStatementEntry entry : bsEntries) {

			totalNOReceipts = totalNOReceipts.add(
					entry.getCreditamount() != null
							? entry.getCreditamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getCreditamount().negate()
									: entry.getCreditamount()
							: BigDecimal.ZERO);

			totalNOPrevReceipts = totalNOPrevReceipts.add(entry.getPrevCreditamount() != null
					? entry.getPrevCreditamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getPrevCreditamount().negate()
							: entry.getPrevCreditamount()
					: BigDecimal.ZERO);

			totalNOPayments = totalNOPayments.add(
					entry.getDebitamount() != null
							? entry.getDebitamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getDebitamount().negate()
									: entry.getDebitamount()
							: BigDecimal.ZERO);

			totalNOPrevPayments = totalNOPrevPayments.add(entry.getPrevDebitamount() != null
					? entry.getPrevDebitamount().compareTo(BigDecimal.ZERO) < 0 ? entry.getPrevDebitamount().negate()
							: entry.getPrevDebitamount()
					: BigDecimal.ZERO);

		}
		totalReceipts = totalReceipts.add(totalNOReceipts != null ? totalNOReceipts : BigDecimal.ZERO);
		prevTotalReceipts = prevTotalReceipts.add(totalNOPrevReceipts != null ? totalNOPrevReceipts : BigDecimal.ZERO);

		totalPayments = totalPayments.add(totalNOPayments != null ? totalNOPayments : BigDecimal.ZERO);
		prevTotalPayments = prevTotalPayments.add(totalNOPrevPayments != null ? totalNOPrevPayments : BigDecimal.ZERO);

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

		getColsolidatedStatement2(receiptsAndPaymentsAccountStatement);

	}

	public void getColsolidatedStatement2(Statement receiptsAndPaymentsAccountStatement) {

		int fundId = 1;
		List<IEStatementEntry> ieEntries = receiptsAndPaymentsAccountStatement.getIeEntries();

		List<IEStatementEntry> incomeEntries = ieEntries.stream().filter(entry -> entry.getType() != null)
				.filter(entry -> entry.getType() == 'I').collect(Collectors.toList());

		List<IEStatementEntry> expenseEntries = ieEntries.stream().filter(entry -> entry.getType() != null)
				.filter(entry -> entry.getGlCode() != null && !entry.getGlCode().equals(""))
				.filter(entry -> entry.getAccountName() != null && !entry.getAccountName().equals(""))
				.filter(entry -> entry.getType() == 'E').collect(Collectors.toList());

		if (incomeEntries.size() > expenseEntries.size()) {
			int diff = incomeEntries.size() - expenseEntries.size();

			for (int i = 0; i < diff; i++) {
				expenseEntries.add(new IEStatementEntry());
			}
		} else if (incomeEntries.size() < expenseEntries.size()) {
			int diff = expenseEntries.size() - incomeEntries.size();

			for (int i = 0; i < diff; i++) {
				incomeEntries.add(new IEStatementEntry());
			}
		} else {

		}

		List<RPAccountEntry> resultList = new ArrayList<RPAccountEntry>();

		for (int i = 0; i < ieEntries.size(); i++) {
			if (ieEntries.get(i).getType() != null && ieEntries.get(i).getType() == 'O') {
				resultList.add(new RPAccountEntry("", ieEntries.get(i).getAccountName(),
						ieEntries.get(i).getNetAmount() != null
								? ieEntries.get(i).getNetAmount()
										.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
								: null,
						ieEntries.get(i).getPreviousYearAmount() != null
								? ieEntries.get(i).getPreviousYearAmount()
										.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
								: null,
						"", "", null, null, 'X'));
			}
		}

		for (int i = 0; i < incomeEntries.size(); i++) {

			resultList.add(new RPAccountEntry(incomeEntries.get(i).getGlCode(), incomeEntries.get(i).getAccountName(),
					incomeEntries.get(i).getNetAmount() != null
							? incomeEntries.get(i).getNetAmount()
									.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
							: BigDecimal.ZERO,
					incomeEntries.get(i).getPreviousYearAmount() != null
							? incomeEntries.get(i).getPreviousYearAmount()
									.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
							: BigDecimal.ZERO,

					expenseEntries.get(i).getGlCode(), expenseEntries.get(i).getAccountName(),

					expenseEntries.get(i).getNetAmount() != null
							? expenseEntries.get(i).getNetAmount()
									.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
							: BigDecimal.ZERO,
					expenseEntries.get(i).getPreviousYearAmount() != null
							? expenseEntries.get(i).getPreviousYearAmount()
									.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
							: BigDecimal.ZERO,
					'X'));
		}

		List<BSStatementEntry> bsEntries = receiptsAndPaymentsAccountStatement.getBsEntries();

		List<BSStatementEntry> assetLiabilitiesEntries = bsEntries.stream()
				.filter(entry -> entry.getType() == 'A' || entry.getType() == 'L').collect(Collectors.toList());

		for (int i = 0; i < assetLiabilitiesEntries.size(); i++) {
			resultList.add(new RPAccountEntry(assetLiabilitiesEntries.get(i).getGlCode(),
					assetLiabilitiesEntries.get(i).getAccountName(), assetLiabilitiesEntries.get(i).getCreditamount(),
					assetLiabilitiesEntries.get(i).getPrevCreditamount(), assetLiabilitiesEntries.get(i).getGlCode(),
					assetLiabilitiesEntries.get(i).getAccountName(), assetLiabilitiesEntries.get(i).getDebitamount(),
					assetLiabilitiesEntries.get(i).getPrevDebitamount(), 'Y'));
		}

		List<BSStatementEntry> totalsEntries = bsEntries.stream().filter(entry -> entry.getType() == 'T')
				.collect(Collectors.toList());
		resultList.add(new RPAccountEntry(totalsEntries.get(0).getGlCode(), totalsEntries.get(0).getAccountName(),
				totalsEntries.get(0).getCreditamount(), totalsEntries.get(0).getPrevCreditamount(),
				totalsEntries.get(0).getGlCode(), totalsEntries.get(0).getAccountName(),
				totalsEntries.get(0).getDebitamount(), totalsEntries.get(0).getPrevDebitamount(), 'Y'));

		List<BSStatementEntry> grandTotalsEntries = bsEntries.stream().filter(entry -> entry.getType() == 'R')
				.collect(Collectors.toList());
		resultList.add(new RPAccountEntry(grandTotalsEntries.get(0).getGlCode(),
				"Total of Receipts", grandTotalsEntries.get(0).getCreditamount(),
				grandTotalsEntries.get(0).getPrevCreditamount(), grandTotalsEntries.get(0).getGlCode(),
				"Total of Payments", grandTotalsEntries.get(0).getDebitamount(),
				grandTotalsEntries.get(0).getPrevDebitamount(), 'Y'));

		List<BSStatementEntry> closingEntries = bsEntries.stream().filter(entry -> entry.getType() == 'C')
				.collect(Collectors.toList());

		for (int i = 0; i < closingEntries.size(); i++) {
			resultList.add(new RPAccountEntry(null,
					(closingEntries.get(i).getAccountName().equalsIgnoreCase("Closing Balances")
							|| closingEntries.get(i).getAccountName().equalsIgnoreCase("a. Cash Balances")
							|| closingEntries.get(i).getAccountName().equalsIgnoreCase("b. Bank Balances")
							|| closingEntries.get(i).getAccountName().equalsIgnoreCase("Total Closing Balance")) ? ""
									: closingEntries.get(i).getAccountName(),
					closingEntries.get(i).getCreditamount(), closingEntries.get(i).getPrevCreditamount(),
					null, closingEntries.get(i).getAccountName(),
					closingEntries.get(i).getDebitamount(), closingEntries.get(i).getPrevDebitamount(), 'Y'));
		}

		receiptsAndPaymentsAccountStatement.setRpEntries(resultList);

	}

	public void getColsolidatedStatement(Statement receiptsAndPaymentsAccountStatement) {

		int fundId = 1;

		List<IEStatementEntry> ieEntries = receiptsAndPaymentsAccountStatement.getIeEntries();
		List<BSStatementEntry> bsEntries = receiptsAndPaymentsAccountStatement.getBsEntries();
		List<StatementEntry> resultList = new ArrayList<StatementEntry>();

		for (int i = 0; i < ieEntries.size(); i++) {

			resultList
					.add(new StatementEntry(ieEntries.get(i).getGlCode() != null ? ieEntries.get(i).getGlCode() : "",
							ieEntries.get(i).getAccountName() != null ? ieEntries.get(i).getAccountName() : "",
							ieEntries.get(i).getNetAmount() != null
									? ieEntries.get(i).getNetAmount()
											.get(getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(),
													fundId))
									: BigDecimal.ZERO,
							ieEntries.get(i).getPreviousYearAmount() != null
									? ieEntries.get(i).getPreviousYearAmount().get(
											getFundNameForId(receiptsAndPaymentsAccountStatement.getFunds(), fundId))
									: BigDecimal.ZERO,
							ieEntries.get(i).getType() != null ? ieEntries.get(i).getType() : Character.MIN_VALUE));
		}
		for (int i = 0; i < bsEntries.size(); i++) {

			resultList.add(new StatementEntry(bsEntries.get(i).getGlCode() != null ? bsEntries.get(i).getGlCode() : "",
					bsEntries.get(i).getAccountName() != null ? bsEntries.get(i).getAccountName() : "",
					bsEntries.get(i).getCreditamount() != null ? bsEntries.get(i).getCreditamount() : BigDecimal.ZERO,
					bsEntries.get(i).getPrevCreditamount() != null ? bsEntries.get(i).getPrevCreditamount()
							: BigDecimal.ZERO,
					bsEntries.get(i).getType() != null ? bsEntries.get(i).getType() : Character.MIN_VALUE));
		}

		for (int i = 0; i < bsEntries.size(); i++) {
			resultList.add(new StatementEntry(bsEntries.get(i).getGlCode() != null ? bsEntries.get(i).getGlCode() : "",
					bsEntries.get(i).getAccountName() != null ? bsEntries.get(i).getAccountName() : "",
					bsEntries.get(i).getDebitamount() != null ? bsEntries.get(i).getDebitamount() : BigDecimal.ZERO,
					bsEntries.get(i).getPrevDebitamount() != null ? bsEntries.get(i).getPrevDebitamount()
							: BigDecimal.ZERO,
					bsEntries.get(i).getType() != null ? bsEntries.get(i).getType() : Character.MIN_VALUE));
		}

		receiptsAndPaymentsAccountStatement.setEntries(resultList);

	}

	public void populateCurrentAndPreviousYearAmountForIE(final Statement statement, final String filterQuery,
			final Date toDate, final Date fromDate, final String scheduleReportType) {

		final BigDecimal divisor = statement.getDivisor();
		final Statement income = new Statement();
		final Statement expenditure = new Statement();

		final List<StatementResultObject> allGlCodesWithoutFilter = getAllGlCodesForRP(scheduleReportType);
		final List<StatementResultObject> allGlCodes = filterIEGlCodes(allGlCodesWithoutFilter);

		// get all net amount total
		final List<StatementResultObject> resultsIE = getTransactionAmtNewIE(filterQuery, toDate, fromDate, "'I','E'");
		final List<StatementResultObject> preYearResultsIE = getTransactionAmtNewIE(filterQuery,
				getPreviousYearFor(toDate), getPreviousYearFor(fromDate), "'I','E'");

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

		final List<StatementResultObject> allGlCodesWithoutFilter = getAllGlCodesForRP(scheduleReportType);
		final List<StatementResultObject> allGlCodes = filterBSCodes(allGlCodesWithoutFilter);

		final List<StatementResultObject> results = getTransactionAmtNew(filterQuery, toDate, fromDate, "'L','A'");

		final List<StatementResultObject> prevResults = getTransactionAmtNew(filterQuery, prevFormattedToDate,
				getPreviousYearFor(fromDate), "'L','A'");

		for (final StatementResultObject queryObject : allGlCodes) {
			if (queryObject.getGlCode() == null)
				queryObject.setGlCode("");
			final List<StatementResultObject> rows = getRowWithGlCodeBS(results, queryObject.getGlCode());

			if (rows.isEmpty() && queryObject.getGlCode() != null) {
				if (contains(prevResults, queryObject.getGlCode())) {
					final List<StatementResultObject> prerows = getRowWithGlCodeBS(prevResults,
							queryObject.getGlCode());

					for (final StatementResultObject pre : prerows) {

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
	private IEStatementEntry getTotalIncomeFundwise(final Statement statement, final Statement income_expense) {
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

		totalPayments = BigDecimal.ZERO;
		prevTotalPayments = BigDecimal.ZERO;

		totalPayments = totalPayments.add(fundNetTotals.get(getFundNameForId(statement.getFunds(), 1)) != null
				? fundNetTotals.get(getFundNameForId(statement.getFunds(), 1))
				: BigDecimal.ZERO);
		prevTotalPayments = prevTotalPayments.add(fundPreTotals.get(getFundNameForId(statement.getFunds(), 1)) != null
				? fundPreTotals.get(getFundNameForId(statement.getFunds(), 1))
				: BigDecimal.ZERO);

		return new IEStatementEntry(" ", "Sub Total", fundNetTotals, fundPreTotals, true, 'E');
	}

	/*
	 * Calculate total Expenditure of current year and previous year
	 */
	private IEStatementEntry getTotalExpenseFundwise(final Statement statement, final Statement income_expense) {

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

		totalReceipts = totalReceipts.add(fundNetTotals.get(getFundNameForId(statement.getFunds(), 1)) != null
				? fundNetTotals.get(getFundNameForId(statement.getFunds(), 1))
				: BigDecimal.ZERO);
		prevTotalReceipts = prevTotalReceipts.add(fundPreTotals.get(getFundNameForId(statement.getFunds(), 1)) != null
				? fundPreTotals.get(getFundNameForId(statement.getFunds(), 1))
				: BigDecimal.ZERO);

		return new IEStatementEntry(" ", "Sub Total", fundNetTotals, fundPreTotals, true, 'I');
	}

	/*
	 * Computes income over expenditure and vise versa for current year amount and
	 * previous year amount
	 */

	/*
	 * Returns All Fund id for which transaction is made previous year or this year
	 */

	private List<StatementResultObject> filterIEGlCodes(List<StatementResultObject> allGlCodes) {

		List<StatementResultObject> results = new ArrayList<>();

		ArrayList<String> desiredGlCodes = new ArrayList<String>(
				Arrays.asList("110", "120", "130", "140", "150", "160", "170", "171", "180", "185", "190", "191", "192",
						"210", "220", "230", "240", "250", "260", "270", "271", "272", "285", "290", "291", "292"));

		for (StatementResultObject glcode : allGlCodes) {
			if (desiredGlCodes.contains(glcode.getGlCode().substring(0, 3))) {
				results.add(glcode);
			}
		}

		return results;
	}

	private List<StatementResultObject> filterBSCodes(List<StatementResultObject> allGlCodes) {
		List<StatementResultObject> results = new ArrayList<>();

		ArrayList<String> desiredGlCodes = new ArrayList<String>(Arrays.asList("310", "312", "320", "330", "331", "340",
				"341", "350", "360", "410", "411", "412", "420", "430", "431", "432", "440", "460", "470", "471"));

		for (StatementResultObject glcode : allGlCodes) {
			if (desiredGlCodes.contains(glcode.getGlCode().substring(0, 3))) {
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