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
package org.egov.egf.web.controller.cashbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.egov.commons.CFinancialYear;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.egf.model.StatementResultObject;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infstr.services.PersistenceService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class BalanceSheetServiceCB extends ReportServiceCB {
	private static final String BS = "BS";
	private static final String L = "L";
	private static final BigDecimal NEGATIVE = new BigDecimal(-1);
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	private String removeEntrysWithZeroAmount = "";
	private BigDecimal adjustedIncomeCurrYear = new BigDecimal(0);
	private BigDecimal adjustedIncomePrevYear = new BigDecimal(0);
	private BigDecimal sundryDebtorsCurrYear = new BigDecimal(0);
	private BigDecimal sundryDebtorsPrevYear = new BigDecimal(0);
	private BigDecimal stockInhandCurrYear = new BigDecimal(0);
	private BigDecimal stockInhandPrevYear = new BigDecimal(0);
	private BigDecimal prepaidExpenseCurrYear = new BigDecimal(0);
	private BigDecimal prepaidExpensePrevYear = new BigDecimal(0);
	private BigDecimal otherCurrentAssetsCurrYear = new BigDecimal(0);
	private BigDecimal otherCurrentAssetsPrevYear = new BigDecimal(0);
	private BigDecimal increaseInProvisionsCurrentYear = new BigDecimal(0);
	private BigDecimal increaseInProvisionsPrevYear = new BigDecimal(0);
	private BigDecimal depositsReceivedCurrYear = new BigDecimal(0);
	private BigDecimal depositsReceivedPrevYear = new BigDecimal(0);
	private BigDecimal depositWorksCurrYear = new BigDecimal(0);
	private BigDecimal depositWorksPrevYear = new BigDecimal(0);
	private BigDecimal otherCurrentLiabilitiesCurrYear = new BigDecimal(0);
	private BigDecimal otherCurrentLiabilitiesPrevYear = new BigDecimal(0);
	private BigDecimal specialFundsGrantsCurrYear = new BigDecimal(0);
	private BigDecimal specialFundsGrantsPrevYear = new BigDecimal(0);
	private BigDecimal cwipCurrYear = new BigDecimal(0);
	private BigDecimal cwipPrevYear = new BigDecimal(0);
	private BigDecimal investmentsCurrYear = new BigDecimal(0);
	private BigDecimal investmentsPrevYear = new BigDecimal(0);
	private BigDecimal disposalOfAssetsCurrYear = new BigDecimal(0);
	private BigDecimal disposalOfAssetsPrevYear = new BigDecimal(0);
	private BigDecimal disposalOfInvestmentsCurrYear = new BigDecimal(0);
	private BigDecimal disposalOfInvestmentsPrevYear = new BigDecimal(0);
	private BigDecimal investmentIncomeReceivedCurrYear = new BigDecimal(0);
	private BigDecimal investmentIncomeReceivedPrevYear = new BigDecimal(0);
	private BigDecimal interestIncomeReceivedCurrYear = new BigDecimal(0);
	private BigDecimal interestIncomeReceivedPrevYear = new BigDecimal(0);
	private BigDecimal aCurrYear = new BigDecimal(0);
	private BigDecimal aPrevYear = new BigDecimal(0);
	private BigDecimal bCurrYear = new BigDecimal(0);
	private BigDecimal bPrevYear = new BigDecimal(0);
	private BigDecimal cCurrYear = new BigDecimal(0);
	private BigDecimal cPrevYear = new BigDecimal(0);
	private BigDecimal abcCurrYear = new BigDecimal(0);
	private BigDecimal abcPrevYear = new BigDecimal(0);
	private BigDecimal atBeginingPeriodCurrYear = new BigDecimal(0);
	private BigDecimal atBeginingPeriodPrevYear = new BigDecimal(0);
	private BigDecimal atEndPeriodPrevYear = new BigDecimal(0);
	private BigDecimal atEndPeriodCurrYear = new BigDecimal(0);
	private BigDecimal totalCurrYear;
	private BigDecimal totalPrevYear;
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private FinancialYearHibernateDAO financialYearDAO;

	@Override
	protected void addRowsToStatement(final Statement balanceSheet, final Statement assets,
			final Statement liabilities) {
		if (liabilities.size() > 0) {
			balanceSheet.add(new StatementEntry(null, Constants.LIABILITIES, "", null, null, true));
			balanceSheet.addAll(liabilities);
			balanceSheet.add(new StatementEntry(null, Constants.TOTAL_LIABILITIES, "", null, null, true));
		}
		if (assets.size() > 0) {
			balanceSheet.add(new StatementEntry(null, Constants.ASSETS, "", null, null, true));
			balanceSheet.addAll(assets);
			balanceSheet.add(new StatementEntry(null, Constants.TOTAL_ASSETS, "", null, null, true));
		}
	}

	public void addCurrentOpeningBalancePerFund(final Statement balanceSheet, final List<Fund> fundList,
			final String transactionQuery) {
		try {
			final BigDecimal divisor = balanceSheet.getDivisor();
			final CFinancialYear financialYr = financialYearDAO.getFinancialYearByDate(balanceSheet.getFromDate());
			final Query query = persistenceService.getSession().createSQLQuery(
					"select sum(openingdebitbalance)- sum(openingcreditbalance),ts.fundid,coa.majorcode,coa.type FROM transactionsummary ts,chartofaccounts coa  WHERE ts.glcodeid = coa.ID  AND ts.financialyearid="
							+ financialYr.getId()

							+ transactionQuery + " GROUP BY ts.fundid,coa.majorcode,coa.type");
			final List<Object[]> openingBalanceAmountList = query.list();
			for (final Object[] obj : openingBalanceAmountList)
				if (obj[0] != null && obj[1] != null) {
					BigDecimal total = (BigDecimal) obj[0];
					if (L.equals(obj[3].toString()))
						total = total.multiply(NEGATIVE);
					for (final StatementEntry entry : balanceSheet.getEntries())
						if (obj[2].toString().equals(entry.getGlCode()))
							if (entry.getFundWiseAmount().isEmpty())
								entry.getFundWiseAmount().put(
										getFundNameForId(fundList, new Integer(obj[1].toString())),
										divideAndRound(total, divisor));
							else {
								boolean shouldAddNewFund = true;
								for (final Entry<String, BigDecimal> object : entry.getFundWiseAmount().entrySet())
									if (object.getKey().equalsIgnoreCase(
											getFundNameForId(fundList, new Integer(obj[1].toString())))) {
										entry.getFundWiseAmount().put(object.getKey(),
												object.getValue().add(divideAndRound(total, divisor)));
										shouldAddNewFund = false;
									}
								if (shouldAddNewFund)
									entry.getFundWiseAmount().put(
											getFundNameForId(fundList, new Integer(obj[1].toString())),
											divideAndRound(total, divisor));
							}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addOpeningBalancePrevYear(final Statement balanceSheet, final String transactionQuery,
			final Date fromDate) {
		try {
			final BigDecimal divisor = balanceSheet.getDivisor();
			final CFinancialYear prevFinancialYr = financialYearDAO.getPreviousFinancialYearByDate(fromDate);
			final String prevFinancialYearId = prevFinancialYr.getId().toString();
			final Query query = persistenceService.getSession().createSQLQuery(
					"select sum(openingdebitbalance)- sum(openingcreditbalance),coa.majorcode,coa.type FROM transactionsummary ts,chartofaccounts coa  WHERE ts.glcodeid = coa.ID  AND ts.financialyearid="
							+ prevFinancialYearId + transactionQuery + " GROUP BY coa.majorcode,coa.type");
			final List<Object[]> openingBalanceAmountList = query.list();
			for (final Object[] obj : openingBalanceAmountList)
				if (obj[0] != null && obj[1] != null) {
					BigDecimal total = (BigDecimal) obj[0];
					if (L.equals(obj[2].toString()))
						total = total.multiply(NEGATIVE);
					for (final StatementEntry entry : balanceSheet.getEntries())
						if (obj[1].toString().equals(entry.getGlCode())) {
							BigDecimal prevYrTotal = entry.getPreviousYearTotal();
							prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
							entry.setPreviousYearTotal(prevYrTotal.add(divideAndRound(total, divisor)));
						}
				}
		} catch (final Exception exp) {

		}
	}

	public void addExcessIEForCurrentYear(final Statement balanceSheet, final List<Fund> fundList,
			final String glCodeForExcessIE, final String filterQuery, final Date toDate, final Date fromDate) {
		final BigDecimal divisor = balanceSheet.getDivisor();
		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
		StringBuffer qry = new StringBuffer(256);
		// TODO- We are only grouping by fund here. Instead here grouping should happen
		// based on the filter like -department and Function also
		qry = qry.append("select sum(g.creditamount)-sum(g.debitamount),v.fundid from voucherheader v,");
		if (balanceSheet.getDepartment() != null && !"null".equals(balanceSheet.getDepartment().getCode()))
			qry.append("VoucherMis mis ,");
		qry.append("generalledger g, chartofaccounts coa   where  v.ID=g.VOUCHERHEADERID and " + "v.status not in("
				+ voucherStatusToExclude + ") and  v.voucherdate>='" + getFormattedDate(fromDate)
				+ "' and v.voucherdate<='" + getFormattedDate(toDate) + "'");
		if (balanceSheet.getDepartment() != null && !"null".equals(balanceSheet.getDepartment().getCode()))
			qry.append(" and v.id= mis.voucherheaderid  and mis.departmentcode= '"
					+ balanceSheet.getDepartment().getCode() + "'");
		qry.append(" and coa.ID=g.glcodeid and coa.type in ('I','E') " + filterQuery + " group by v.fundid");
		final Query query = persistenceService.getSession().createSQLQuery(qry.toString());
		final List<Object[]> excessieAmountList = query.list();

		for (final StatementEntry entry : balanceSheet.getEntries())
			if (entry.getGlCode() != null && glCodeForExcessIE.equals(entry.getGlCode()))
				for (final Object[] obj : excessieAmountList) {

					if (obj[0] != null && obj[1] != null) {
						final String fundNameForId = getFundNameForId(fundList, Integer.valueOf(obj[1].toString()));
						if (entry.getFundWiseAmount().containsKey(fundNameForId))
							entry.getFundWiseAmount().put(fundNameForId, entry.getFundWiseAmount().get(fundNameForId)
									.add(divideAndRound((BigDecimal) obj[0], divisor)));
						else
							entry.getFundWiseAmount().put(fundNameForId, divideAndRound((BigDecimal) obj[0], divisor));
					}
				}
	}

	public void addExcessIEForPreviousYear(final Statement balanceSheet, final List<Fund> fundList,
			final String glCodeForExcessIE, final String filterQuery, final Date toDate, final Date fromDate) {
		final BigDecimal divisor = balanceSheet.getDivisor();
		BigDecimal sum = BigDecimal.ZERO;
		String formattedToDate = "";
		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
		if ("Yearly".equalsIgnoreCase(balanceSheet.getPeriod())) {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			formattedToDate = getFormattedDate(cal.getTime());
		} else
			formattedToDate = getFormattedDate(getPreviousYearFor(toDate));
		StringBuffer qry = new StringBuffer(256);
		qry = qry.append(
				"		select sum(g.creditamount)-sum(g.debitamount),v.fundid  from voucherheader v,generalledger g, ");
		if (balanceSheet.getDepartment() != null && !"null".equals(balanceSheet.getDepartment().getCode()))
			qry.append("  VoucherMis mis ,");
		qry.append(" chartofaccounts coa   where  v.ID=g.VOUCHERHEADERID and v.status not in(" + voucherStatusToExclude
				+ ") and  " + "v.voucherdate>='" + getFormattedDate(getPreviousYearFor(fromDate))
				+ "' and v.voucherdate<='" + formattedToDate + "' and coa.ID=g.glcodeid ");
		if (balanceSheet.getDepartment() != null && !"null".equals(balanceSheet.getDepartment().getCode()))
			qry.append(" and v.id= mis.voucherheaderid");

		qry.append(" and coa.type in ('I','E') " + filterQuery + " group by v.fundid,g.functionid");
		final Query query = persistenceService.getSession().createSQLQuery(qry.toString());
		final List<Object[]> excessieAmountList = query.list();
		for (final Object[] obj : excessieAmountList)
			sum = sum.add((BigDecimal) obj[0]);
		for (int index = 0; index < balanceSheet.size(); index++)
			if (balanceSheet.get(index).getGlCode() != null
					&& glCodeForExcessIE.equals(balanceSheet.get(index).getGlCode())) {
				BigDecimal prevYrTotal = balanceSheet.get(index).getPreviousYearTotal();
				prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
				balanceSheet.get(index).setPreviousYearTotal(prevYrTotal.add(divideAndRound(sum, divisor)));
			}
	}
	public void populateBalanceSheet(final Statement balanceSheet) {

		try {
			final List<AppConfigValues> configValues = appConfigValuesService.getConfigValuesByModuleAndKey(
					FinancialConstants.MODULE_NAME_APPCONFIG,
					FinancialConstants.REMOVE_ENTRIES_WITH_ZERO_AMOUNT_IN_REPORT);

			for (final AppConfigValues appConfigVal : configValues)
				removeEntrysWithZeroAmount = appConfigVal.getValue();
		} catch (final Exception e) {
			throw new ApplicationRuntimeException(
					"Appconfig value for remove entries with zero amount in report is not defined in the system");
		}
		minorCodeLength = Integer.valueOf(getAppConfigValueFor(Constants.EGF, "coa_minorcode_length"));
		System.out.println("### minorCodeLength in balancesheet ::" + minorCodeLength);
		coaType.add('A');
		coaType.add('L');
		final Date fromDate = balanceSheet.getFromDate();
		final Date toDate = balanceSheet.getToDate();
		/*
		 * final Date fromDate = getFromDate(balanceSheet); final Date toDate =
		 * getToDate(balanceSheet);
		 */
		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
		final List<Fund> fundList = balanceSheet.getFunds();
		final String filterQuery = getFilterQuery(balanceSheet);
		populateCurrentYearAmountPerFund(balanceSheet, fundList, filterQuery, toDate, fromDate, BS);
		populatePreviousYearTotals(balanceSheet, filterQuery, toDate, fromDate, BS, "'L','A'");
		addCurrentOpeningBalancePerFund(balanceSheet, fundList, getTransactionQuery(balanceSheet));
		addOpeningBalancePrevYear(balanceSheet, getTransactionQuery(balanceSheet), fromDate);
		final String glCodeForExcessIE = getGlcodeForPurposeCode(7);// purpose is ExcessIE
		addExcessIEForCurrentYear(balanceSheet, fundList, glCodeForExcessIE, filterQuery, toDate, fromDate);
		addExcessIEForPreviousYear(balanceSheet, fundList, glCodeForExcessIE, filterQuery, toDate, fromDate);
		computeCurrentYearTotals(balanceSheet, Constants.LIABILITIES, Constants.ASSETS);
		populateSchedule(balanceSheet, BS);
		removeFundsWithNoData(balanceSheet);
		groupBySubSchedule(balanceSheet);
		computeTotalAssetsAndLiabilities(balanceSheet);
		if (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
			removeEntrysWithZeroAmount(balanceSheet);
	}

	private void computeTotalAssetsAndLiabilities(final Statement balanceSheet) {
		BigDecimal currentYearTotal = BigDecimal.ZERO;
		BigDecimal previousYearTotal = BigDecimal.ZERO;
		for (int index = 0; index < balanceSheet.size(); index++) {
			if (Constants.TOTAL.equalsIgnoreCase(balanceSheet.get(index).getAccountName())
					|| Constants.LIABILITIES.equals(balanceSheet.get(index).getAccountName())
					|| Constants.ASSETS.equals(balanceSheet.get(index).getAccountName()))
				continue;
			if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(balanceSheet.get(index).getAccountName())
					|| Constants.TOTAL_ASSETS.equalsIgnoreCase(balanceSheet.get(index).getAccountName())) {
				balanceSheet.get(index).setCurrentYearTotal(currentYearTotal);
				currentYearTotal = BigDecimal.ZERO;
				balanceSheet.get(index).setPreviousYearTotal(previousYearTotal);
				previousYearTotal = BigDecimal.ZERO;
			} else {
				if (balanceSheet.get(index).getCurrentYearTotal() != null)
					currentYearTotal = currentYearTotal.add(balanceSheet.get(index).getCurrentYearTotal());
				if (balanceSheet.get(index).getPreviousYearTotal() != null)
					previousYearTotal = previousYearTotal.add(balanceSheet.get(index).getPreviousYearTotal());
			}
		}
	}

	private void groupBySubSchedule(final Statement balanceSheet) {
		final List<StatementEntry> list = new LinkedList<StatementEntry>();
		final Map<String, String> schedueNumberToNameMap = getSubSchedule(BS);
		final Set<String> grouped = new HashSet<String>();
		BigDecimal previousTotal = BigDecimal.ZERO;
		BigDecimal currentTotal = BigDecimal.ZERO;
		Map<String, BigDecimal> fundTotals = new HashMap<String, BigDecimal>();
		boolean isLastEntryAHeader = true;
		// this loop assumes entries are ordered by major codes and have implicit
		// grouping
		for (final StatementEntry entry : balanceSheet.getEntries()) {
			if (!grouped.contains(schedueNumberToNameMap.get(entry.getScheduleNo()))) {
				// hack to take care of liabilities and asset rows
				if (!isLastEntryAHeader) {
					final StatementEntry balanceSheetEntry = new StatementEntry(null, Constants.TOTAL, "",
							previousTotal, currentTotal, true);
					balanceSheetEntry.setFundWiseAmount(fundTotals);
					fundTotals = new HashMap<String, BigDecimal>();
					list.add(balanceSheetEntry);
				}
				// the current schedule number is not grouped yet, we'll start grouping it now.
				// Before starting the group we have to add total row for the last group
				addTotalRowToPreviousGroup(list, schedueNumberToNameMap, entry);
				previousTotal = BigDecimal.ZERO;
				currentTotal = BigDecimal.ZERO;
				// now this is grouped, so add it to to grouped set
				grouped.add(schedueNumberToNameMap.get(entry.getScheduleNo()));
			}
			if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(entry.getAccountName())) {
				final StatementEntry balanceSheetEntry = new StatementEntry(null, Constants.TOTAL, "", previousTotal,
						currentTotal, true);
				balanceSheetEntry.setFundWiseAmount(fundTotals);
				fundTotals = new HashMap<String, BigDecimal>();
				list.add(balanceSheetEntry);
			}
			list.add(entry);
			addFundAmount(entry, fundTotals);
			previousTotal = previousTotal.add(zeroOrValue(entry.getPreviousYearTotal()));
			currentTotal = currentTotal.add(zeroOrValue(entry.getCurrentYearTotal()));
			isLastEntryAHeader = entry.getGlCode() == null;
			if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(entry.getAccountName())) {
				previousTotal = BigDecimal.ZERO;
				currentTotal = BigDecimal.ZERO;
			}
		}
		// add the total row for the last grouping
		final StatementEntry sheetEntry = new StatementEntry(null, Constants.TOTAL, "", previousTotal, currentTotal,
				true);
		sheetEntry.setFundWiseAmount(fundTotals);
		list.add(list.size() - 1, sheetEntry);
		balanceSheet.setEntries(list);
	}

	private void removeEntrysWithZeroAmount(final Statement balanceSheet) {
		final List<StatementEntry> list = new LinkedList<StatementEntry>();
		Boolean check;
		Map<String, BigDecimal> FundWiseAmount = new HashMap<String, BigDecimal>();
		for (final StatementEntry entry : balanceSheet.getEntries())
			if (entry.getGlCode() != null && !entry.getGlCode().equalsIgnoreCase("")) {
				FundWiseAmount = entry.getFundWiseAmount();
				if (FundWiseAmount != null) {
					check = false;
					for (final String keyGroup : FundWiseAmount.keySet())
						if (!(entry.getPreviousYearTotal() != null
								&& FundWiseAmount.get(keyGroup).compareTo(BigDecimal.ZERO) == 0
								&& entry.getPreviousYearTotal().compareTo(BigDecimal.ZERO) == 0)) {
							check = true;
							break;
						}
					if (check.equals(true))
						list.add(entry);
				} else
					list.add(entry);
			} else
				list.add(entry);
		balanceSheet.setEntries(new LinkedList<StatementEntry>());
		balanceSheet.setEntries(list);
	}

	public void removeScheduleEntrysWithZeroAmount(final Statement balanceSheet) {
		final List<StatementEntry> list = new ArrayList<StatementEntry>();
		for (final StatementEntry entry : balanceSheet.getEntries())
			if (entry.getGlCode() != null && !entry.getGlCode().equalsIgnoreCase("")) {
				if (!(entry.getCurrentYearTotal() != null && entry.getPreviousYearTotal() != null
						&& entry.getCurrentYearTotal().compareTo(BigDecimal.ZERO) == 0
						&& entry.getPreviousYearTotal().compareTo(BigDecimal.ZERO) == 0))
					list.add(entry);
			} else
				list.add(entry);
		balanceSheet.setEntries(new LinkedList<StatementEntry>());
		balanceSheet.setEntries(list);
	}

	public void populateCurrentYearAmountPerFund(final Statement statement, final List<Fund> fundList,
			final String filterQuery, final Date toDate, final Date fromDate, final String scheduleReportType) {
		final Statement assets = new Statement();
		final Statement liabilities = new Statement();
		final BigDecimal divisor = statement.getDivisor();
		final List<StatementResultObject> allGlCodes = getAllGlCodesFor(scheduleReportType);
		final List<StatementResultObject> results = getTransactionAmount(filterQuery, toDate, fromDate, "'L','A'",
				"BS");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("row.getGlCode()--row.getFundId()--row.getAmount()--row.getBudgetAmount()");
		for (final StatementResultObject queryObject : allGlCodes) {
			if (queryObject.getGlCode() == null)
				queryObject.setGlCode("");
			final List<StatementResultObject> rows = getRowWithGlCode(results, queryObject.getGlCode());

			if (rows.isEmpty()) {
				if (queryObject.isLiability())
					liabilities.add(new StatementEntry(queryObject.getGlCode(), queryObject.getScheduleName(),
							queryObject.getScheduleNumber(), BigDecimal.ZERO, BigDecimal.ZERO, false));
				else
					assets.add(new StatementEntry(queryObject.getGlCode(), queryObject.getScheduleName(),
							queryObject.getScheduleNumber(), BigDecimal.ZERO, BigDecimal.ZERO, false));
			} else
				for (final StatementResultObject row : rows) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(row.getGlCode() + "--" + row.getFundId() + "--" + row.getAmount() + "--"
								+ row.getBudgetAmount());
					if (row.isLiability())
						row.negateAmount();
					if (liabilities.containsBalanceSheetEntry(row.getGlCode())
							|| assets.containsBalanceSheetEntry(row.getGlCode())) {
						if (row.isLiability())
							addFundAmount(fundList, liabilities, divisor, row);
						else
							addFundAmount(fundList, assets, divisor, row);
					} else {
						final StatementEntry balanceSheetEntry = new StatementEntry();
						if (row.getAmount() != null && row.getFundId() != null)
							balanceSheetEntry.getFundWiseAmount().put(
									getFundNameForId(fundList, Integer.valueOf(row.getFundId())),
									divideAndRound(row.getAmount(), divisor));
						if (queryObject.getGlCode() != null) {
							balanceSheetEntry.setGlCode(queryObject.getGlCode());
							balanceSheetEntry.setAccountName(queryObject.getScheduleName());
							balanceSheetEntry.setScheduleNo(queryObject.getScheduleNumber());
						}

						if (row.isLiability())
							liabilities.add(balanceSheetEntry);
						else
							assets.add(balanceSheetEntry);
					}
				}
		}
		addRowsToStatement(statement, assets, liabilities);
	}

	public void populatePreviousYearTotals(final Statement balanceSheet, final String filterQuery, final Date toDate,
			final Date fromDate, final String reportSubType, final String coaType) {
		final boolean newbalanceSheet = balanceSheet.size() > 2 ? false : true;
		final BigDecimal divisor = balanceSheet.getDivisor();
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
		final List<StatementResultObject> results = getTransactionAmount(filterQuery, formattedToDate,
				getPreviousYearFor(fromDate), coaType, reportSubType);
		for (final StatementResultObject row : results)
			if (balanceSheet.containsBalanceSheetEntry(row.getGlCode())) {
				for (int index = 0; index < balanceSheet.size(); index++)
					if (balanceSheet.get(index).getGlCode() != null
							&& row.getGlCode().equals(balanceSheet.get(index).getGlCode())) {
						if (row.isLiability())
							row.negateAmount();
						BigDecimal prevYrTotal = balanceSheet.get(index).getPreviousYearTotal();
						prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
						balanceSheet.get(index)
								.setPreviousYearTotal(prevYrTotal.add(divideAndRound(row.getAmount(), divisor)));
					}
			} else {
				if (row.isLiability())
					row.negateAmount();
				final StatementEntry balanceSheetEntry = new StatementEntry();
				if (row.getAmount() != null && row.getFundId() != null) {
					balanceSheetEntry.setPreviousYearTotal(divideAndRound(row.getAmount(), divisor));
					balanceSheetEntry.setCurrentYearTotal(BigDecimal.ZERO);
				}
				if (row.getGlCode() != null)
					balanceSheetEntry.setGlCode(row.getGlCode());
				if (row.isLiability())
					liabilities.add(balanceSheetEntry);
				else
					assets.add(balanceSheetEntry);
			}
		if (newbalanceSheet)
			addRowsToStatement(balanceSheet, assets, liabilities);
	}

	public String getRemoveEntrysWithZeroAmount() {
		return removeEntrysWithZeroAmount;
	}

	public void setRemoveEntrysWithZeroAmount(final String removeEntrysWithZeroAmount) {
		this.removeEntrysWithZeroAmount = removeEntrysWithZeroAmount;
	}

	public List<CashFlowReportDataBean> getFinalVBalanceSheetList(List<CashFlowReportDataBean> balanceSheetLNow,
			List<CashFlowReportDataBean> balanceSheetLPrev) {
		List<CashFlowReportDataBean> finalBalanceSheetL = new ArrayList<CashFlowReportDataBean>();
		CashFlowReportDataBean finalObj = new CashFlowReportDataBean();
		CashFlowReportDataBean currobj = new CashFlowReportDataBean();
		CashFlowReportDataBean prevobj = new CashFlowReportDataBean();
		currobj = populateCashFlowReportBeanObject(balanceSheetLNow, "current");
		prevobj = populateCashFlowReportBeanObject(balanceSheetLPrev, "prev");
		if (currobj != null) {
			finalObj.setSundryDebtorsCurrYear(
					currobj.getSundryDebtorsPrevYear().subtract(currobj.getSundryDebtorsCurrYear()));
			finalObj.setStockInhandCurrYear(
					currobj.getStockInhandPrevYear().subtract(currobj.getStockInhandCurrYear()));
			finalObj.setPrepaidExpenseCurrYear(
					currobj.getPrepaidExpensePrevYear().subtract(currobj.getPrepaidExpenseCurrYear()));
			finalObj.setOtherCurrentAssetsCurrYear(
					currobj.getOtherCurrentAssetsPrevYear().subtract(currobj.getOtherCurrentAssetsCurrYear()));
			finalObj.setDepositsReceivedCurrYear(
					currobj.getDepositsReceivedCurrYear().subtract(currobj.getDepositsReceivedPrevYear()));
			finalObj.setDepositWorksCurrYear(
					currobj.getDepositWorksCurrYear().subtract(currobj.getDepositWorksPrevYear()));
			finalObj.setOtherCurrentLiabilitiesCurrYear(currobj.getOtherCurrentLiabilitiesCurrYear()
					.subtract(currobj.getOtherCurrentLiabilitiesPrevYear()));
			finalObj.setIncreaseInProvisionCurrYear(
					currobj.getIncreaseInProvisionCurrYear().subtract(currobj.getIncreaseInProvisionPrevYear()));
			finalObj.setSpecialFundsGrantsCurrYear(
					currobj.getSpecialFundsGrantsPrevYear().subtract(currobj.getSpecialFundsGrantsCurrYear()));
			finalObj.setCwipCurrYear(currobj.getCwipCurrYear());
			finalObj.setInvestmentsCurrYear(currobj.getInvestmentsCurrYear());
			finalObj.setDisposalOfAssetsCurrYear(currobj.getDisposalOfAssetsCurrYear());
			finalObj.setDisposalOfInvestmentsCurrYear(currobj.getDisposalOfInvestmentsCurrYear());
			finalObj.setAtBeginingPeriodCurrYear(currobj.getAtBeginingPeriodCurrYear());
			finalObj.setAtEndPeriodCurrYear(currobj.getAtEndPeriodCurrYear());
		}
		if (prevobj != null) {
			finalObj.setSundryDebtorsPrevYear(
					prevobj.getSundryDebtorsPrevYear().subtract(prevobj.getSundryDebtorsCurrYear()));
			finalObj.setStockInhandPrevYear(
					prevobj.getStockInhandPrevYear().subtract(prevobj.getStockInhandCurrYear()));
			finalObj.setPrepaidExpensePrevYear(
					prevobj.getPrepaidExpensePrevYear().subtract(prevobj.getPrepaidExpenseCurrYear()));
			finalObj.setOtherCurrentAssetsPrevYear(
					prevobj.getOtherCurrentAssetsPrevYear().subtract(prevobj.getOtherCurrentAssetsCurrYear()));
			finalObj.setDepositsReceivedPrevYear(
					prevobj.getDepositsReceivedCurrYear().subtract(prevobj.getDepositsReceivedPrevYear()));
			finalObj.setDepositWorksPrevYear(
					prevobj.getDepositWorksCurrYear().subtract(prevobj.getDepositWorksPrevYear()));
			finalObj.setOtherCurrentLiabilitiesPrevYear(prevobj.getOtherCurrentLiabilitiesCurrYear()
					.subtract(prevobj.getOtherCurrentLiabilitiesPrevYear()));
			finalObj.setIncreaseInProvisionPrevYear(
					prevobj.getIncreaseInProvisionCurrYear().subtract(prevobj.getIncreaseInProvisionPrevYear()));
			finalObj.setSpecialFundsGrantsPrevYear(
					prevobj.getSpecialFundsGrantsPrevYear().subtract(prevobj.getSpecialFundsGrantsCurrYear()));
			finalObj.setCwipPrevYear(prevobj.getCwipPrevYear());
			finalObj.setInvestmentsPrevYear(prevobj.getInvestmentsPrevYear());
			finalObj.setDisposalOfAssetsPrevYear(prevobj.getDisposalOfAssetsPrevYear());
			finalObj.setDisposalOfInvestmentsPrevYear(prevobj.getDisposalOfInvestmentsPrevYear());
			finalObj.setAtBeginingPeriodPrevYear(prevobj.getAtBeginingPeriodPrevYear());
			finalObj.setAtEndPeriodPrevYear(prevobj.getAtEndPeriodPrevYear());
		}
		finalBalanceSheetL.add(finalObj);
		return finalBalanceSheetL;
	}

	private CashFlowReportDataBean populateCashFlowReportBeanObject(List<CashFlowReportDataBean> balanceSheetL,
			String yearType) {
		CashFlowReportDataBean obj = new CashFlowReportDataBean();
		for (CashFlowReportDataBean lstObj : balanceSheetL) {
			if (lstObj.getSundryDebtorsCurrYear() == null) {
				sundryDebtorsCurrYear = new BigDecimal(0);

			} else {
				sundryDebtorsCurrYear = lstObj.getSundryDebtorsCurrYear();
			}
			if (lstObj.getSundryDebtorsPrevYear() == null) {
				sundryDebtorsPrevYear = new BigDecimal(0);
			} else {
				sundryDebtorsPrevYear = lstObj.getSundryDebtorsPrevYear();
			}
			if (lstObj.getStockInhandCurrYear() == null) {
				stockInhandCurrYear = new BigDecimal(0);
			} else {
				stockInhandCurrYear = lstObj.getStockInhandCurrYear();
			}
			if (lstObj.getStockInhandPrevYear() == null) {
				stockInhandPrevYear = new BigDecimal(0);
			} else {
				stockInhandPrevYear = lstObj.getStockInhandPrevYear();
			}
			if (lstObj.getPrepaidExpenseCurrYear() == null) {
				prepaidExpenseCurrYear = new BigDecimal(0);
			} else {
				prepaidExpenseCurrYear = lstObj.getPrepaidExpenseCurrYear();
			}
			if (lstObj.getPrepaidExpensePrevYear() == null) {
				prepaidExpensePrevYear = new BigDecimal(0);
			} else {
				prepaidExpensePrevYear = lstObj.getPrepaidExpensePrevYear();
			}
			if (lstObj.getOtherCurrentAssetsCurrYear() == null) {
				otherCurrentAssetsCurrYear = new BigDecimal(0);
			} else {
				otherCurrentAssetsCurrYear = lstObj.getOtherCurrentAssetsCurrYear();
			}
			if (lstObj.getOtherCurrentAssetsPrevYear() == null) {
				otherCurrentAssetsPrevYear = new BigDecimal(0);
			} else {
				otherCurrentAssetsPrevYear = lstObj.getOtherCurrentAssetsPrevYear();
			}
			if (lstObj.getDepositsReceivedCurrYear() == null) {
				depositsReceivedCurrYear = new BigDecimal(0);
			} else {
				depositsReceivedCurrYear = lstObj.getDepositsReceivedCurrYear();
			}
			if (lstObj.getDepositsReceivedPrevYear() == null) {
				depositsReceivedPrevYear = new BigDecimal(0);
			} else {
				depositsReceivedPrevYear = lstObj.getDepositsReceivedPrevYear();
			}
			if (lstObj.getDepositWorksCurrYear() == null) {
				depositWorksCurrYear = new BigDecimal(0);

			} else {
				depositWorksCurrYear = lstObj.getDepositWorksCurrYear();
			}
			if (lstObj.getDepositWorksPrevYear() == null) {
				depositWorksPrevYear = new BigDecimal(0);
			} else {
				depositWorksPrevYear = lstObj.getDepositWorksPrevYear();
			}
			if (lstObj.getOtherCurrentLiabilitiesCurrYear() == null) {
				otherCurrentLiabilitiesCurrYear = new BigDecimal(0);
			} else {
				otherCurrentLiabilitiesCurrYear = lstObj.getOtherCurrentLiabilitiesCurrYear();
			}
			if (lstObj.getOtherCurrentLiabilitiesPrevYear() == null) {
				otherCurrentLiabilitiesPrevYear = new BigDecimal(0);
			} else {
				otherCurrentLiabilitiesPrevYear = lstObj.getOtherCurrentLiabilitiesPrevYear();
			}
			if (lstObj.getIncreaseInProvisionCurrYear() == null) {
				increaseInProvisionsCurrentYear = new BigDecimal(0);
			} else {
				increaseInProvisionsCurrentYear = lstObj.getIncreaseInProvisionCurrYear();
			}
			if (lstObj.getIncreaseInProvisionPrevYear() == null) {
				increaseInProvisionsPrevYear = new BigDecimal(0);
			} else {
				increaseInProvisionsPrevYear = lstObj.getIncreaseInProvisionPrevYear();
			}
			if (lstObj.getSpecialFundsGrantsCurrYear() == null) {
				specialFundsGrantsCurrYear = new BigDecimal(0);
			} else {
				specialFundsGrantsCurrYear = lstObj.getSpecialFundsGrantsCurrYear();
			}
			if (lstObj.getSpecialFundsGrantsPrevYear() == null) {
				specialFundsGrantsPrevYear = new BigDecimal(0);
			} else {
				specialFundsGrantsPrevYear = lstObj.getSpecialFundsGrantsPrevYear();
			}
			if (yearType.equalsIgnoreCase("current")) {
				if (lstObj.getCwipCurrYear() == null) {
					cwipCurrYear = new BigDecimal(0);
				} else {
					cwipCurrYear = lstObj.getCwipCurrYear();
				}
				if (lstObj.getInvestmentsCurrYear() == null) {
					investmentsCurrYear = new BigDecimal(0);
				} else {
					investmentsCurrYear = lstObj.getInvestmentsCurrYear();
				}
				if (lstObj.getDisposalOfAssetsCurrYear() == null) {
					disposalOfAssetsCurrYear = new BigDecimal(0);
				} else {
					disposalOfAssetsCurrYear = lstObj.getDisposalOfAssetsCurrYear();
				}
				if (lstObj.getDisposalOfInvestmentsCurrYear() == null) {
					disposalOfInvestmentsCurrYear = new BigDecimal(0);
				} else {
					disposalOfInvestmentsCurrYear = lstObj.getDisposalOfInvestmentsCurrYear();
				}
				obj.setDisposalOfInvestmentsCurrYear(disposalOfInvestmentsCurrYear);
				obj.setDisposalOfAssetsCurrYear(disposalOfAssetsCurrYear);
				obj.setCwipCurrYear(cwipCurrYear);
				obj.setInvestmentsCurrYear(investmentsCurrYear);
				if(lstObj.getAtBeginingPeriodCurrYear() == null) {
					atBeginingPeriodCurrYear = new BigDecimal(0);
				}else {
					atBeginingPeriodCurrYear  = lstObj.getAtBeginingPeriodCurrYear();
				}
				if(lstObj.getAtEndPeriodCurrYear() == null) {
					atEndPeriodCurrYear = new BigDecimal(0);
				}else {
					atEndPeriodCurrYear = lstObj.getAtEndPeriodCurrYear();
				}
				obj.setAtBeginingPeriodCurrYear(atBeginingPeriodCurrYear);
				obj.setAtEndPeriodCurrYear(atEndPeriodCurrYear);
			}
			if (yearType.equalsIgnoreCase("prev")) {
				if (lstObj.getCwipPrevYear() == null) {
					cwipPrevYear = new BigDecimal(0);
				} else {
					cwipPrevYear = lstObj.getCwipPrevYear();
				}

				if (lstObj.getInvestmentsPrevYear() == null) {
					investmentsPrevYear = new BigDecimal(0);
				} else {
					investmentsPrevYear = lstObj.getInvestmentsPrevYear();
				}
				if (lstObj.getDisposalOfInvestmentsPrevYear() == null) {
					disposalOfInvestmentsPrevYear = new BigDecimal(0);
				} else {
					disposalOfInvestmentsPrevYear = lstObj.getDisposalOfInvestmentsPrevYear();
				}
				if (lstObj.getDisposalOfAssetsPrevYear() == null) {
					disposalOfAssetsPrevYear = new BigDecimal(0);
				} else {
					disposalOfAssetsPrevYear = lstObj.getDisposalOfAssetsPrevYear();
				}
				obj.setDisposalOfAssetsPrevYear(disposalOfAssetsPrevYear);
				obj.setDisposalOfInvestmentsPrevYear(disposalOfInvestmentsPrevYear);
				obj.setCwipPrevYear(cwipPrevYear);
				obj.setInvestmentsPrevYear(investmentsPrevYear);
				if(lstObj.getAtBeginingPeriodPrevYear() == null) {
					atBeginingPeriodPrevYear = new BigDecimal(0);
				}else {
					atBeginingPeriodPrevYear = lstObj.getAtBeginingPeriodPrevYear();
				}
				if(lstObj.getAtEndPeriodPrevYear() == null) {
					atEndPeriodPrevYear = new BigDecimal(0);
				}
				obj.setAtBeginingPeriodPrevYear(atBeginingPeriodPrevYear);
				obj.setAtEndPeriodPrevYear(atEndPeriodPrevYear);
			}
			obj.setSundryDebtorsCurrYear(sundryDebtorsCurrYear);
			obj.setSundryDebtorsPrevYear(sundryDebtorsPrevYear);
			obj.setStockInhandCurrYear(stockInhandCurrYear);
			obj.setStockInhandPrevYear(stockInhandPrevYear);
			obj.setPrepaidExpenseCurrYear(prepaidExpenseCurrYear);
			obj.setPrepaidExpensePrevYear(prepaidExpensePrevYear);
			obj.setOtherCurrentAssetsCurrYear(otherCurrentAssetsCurrYear);
			obj.setOtherCurrentAssetsPrevYear(otherCurrentAssetsPrevYear);
			obj.setDepositsReceivedCurrYear(depositsReceivedCurrYear);
			obj.setDepositsReceivedPrevYear(depositsReceivedPrevYear);
			obj.setDepositWorksCurrYear(depositWorksCurrYear);
			obj.setDepositWorksPrevYear(depositWorksPrevYear);
			obj.setOtherCurrentLiabilitiesCurrYear(otherCurrentLiabilitiesCurrYear);
			obj.setOtherCurrentLiabilitiesPrevYear(otherCurrentLiabilitiesPrevYear);
			obj.setIncreaseInProvisionCurrYear(increaseInProvisionsCurrentYear);
			obj.setIncreaseInProvisionPrevYear(increaseInProvisionsPrevYear);
			obj.setSpecialFundsGrantsCurrYear(specialFundsGrantsCurrYear);
			obj.setSpecialFundsGrantsPrevYear(specialFundsGrantsPrevYear);

		}
		return obj;
	}

	public Map<String, Object> prepareMapForCashFlowReport(List<CashFlowReportDataBean> lst1,
			List<CashFlowReportDataBean> finalBalanceSheetL, CashBookReportBean cashFlowReportBean) {
		Map<String, Object> reportMap = new HashMap<String, Object>();
		try {
			for (CashFlowReportDataBean obj : lst1) {
				reportMap.put("incomeOverExpenditureCurr", obj.getIncomeOverExpenditureCurr());
				reportMap.put("incomeOverExpenditurePrevYear", obj.getIncomeOverExpenditurePrevYear());
				reportMap.put("depreciationCurr", obj.getDepreciationCurr());
				reportMap.put("depreciationPrevYear", obj.getDepreciationPrevYear());
				reportMap.put("interestFinanceChargesCurrYear", obj.getInterestFinanceChargesCurrYear());
				reportMap.put("interestFinanceChargesPrevYear", obj.getInterestFinanceChargesPrevYear());
				reportMap.put("profitOnDisposalOfAssetsCurrYear", obj.getProfitOnDisposalOfAssetsCurrYear());
				reportMap.put("profitOnDisposalOfAssetsPrevYear", obj.getProfitOnDisposalOfAssetsPrevYear());
				reportMap.put("dividendIncomeCurrYear", obj.getDividendIncomeCurrYear());
				reportMap.put("dividendIncomePrevYear", obj.getDividendIncomePrevYear());
				reportMap.put("investmentIncomeCurrYear", obj.getInvestmentIncomeCurrYear());
				reportMap.put("investmentIncomePrevYear", obj.getInvestmentIncomePrevYear());
				reportMap.put("adjustedIncomeCurrYear", obj.getAdjustedIncomeCurrYear());
				reportMap.put("adjustedIncomePrevYear", obj.getAdjustedIncomePrevYear());
				reportMap.put("currentLiabilitiesCurrYear", obj.getCurrentLiabilitiesCurrYear());
				reportMap.put("currentLiabilitiesPrevYear", obj.getCurrentLiabilitiesPrevYear());
				reportMap.put("investmentIncomeReceivedCurrYear", obj.getInvestmentIncomeReceivedCurrYear());
				reportMap.put("investmentIncomeReceivedPrevYear", obj.getInvestmentIncomeReceivedPrevYear());
				reportMap.put("interestIncomeReceivedCurrYear", obj.getInterestIncomeReceivedCurrYear());
				reportMap.put("interestIncomeReceivedPrevYear", obj.getInterestIncomeReceivedPrevYear());
			}
			for (CashFlowReportDataBean obj1 : finalBalanceSheetL) {
				reportMap.put("sundryDebtorsCurrYear", obj1.getSundryDebtorsCurrYear());
				reportMap.put("sundryDebtorsPrevYear", obj1.getSundryDebtorsPrevYear());
				reportMap.put("stockInhandCurrYear", obj1.getStockInhandCurrYear());
				reportMap.put("stockInhandPrevYear", obj1.getStockInhandPrevYear());
				reportMap.put("prepaidExpenseCurrYear", obj1.getPrepaidExpenseCurrYear());
				reportMap.put("prepaidExpensePrevYear", obj1.getPrepaidExpensePrevYear());
				reportMap.put("otherCurrentAssetsCurrYear", obj1.getOtherCurrentAssetsCurrYear());
				reportMap.put("otherCurrentAssetsPrevYear", obj1.getOtherCurrentAssetsPrevYear());
				reportMap.put("depositsReceivedCurrYear", obj1.getDepositsReceivedCurrYear());
				reportMap.put("depositsReceivedPrevYear", obj1.getDepositsReceivedPrevYear());
				reportMap.put("depositWorksCurrYear", obj1.getDepositWorksCurrYear());
				reportMap.put("depositWorksPrevYear", obj1.getDepositWorksPrevYear());
				reportMap.put("otherCurrentLiabilitiesCurrYear", obj1.getOtherCurrentLiabilitiesCurrYear());
				reportMap.put("otherCurrentLiabilitiesPrevYear", obj1.getOtherCurrentLiabilitiesPrevYear());

				reportMap.put("increaseInProvisionCurrYear", obj1.getIncreaseInProvisionCurrYear());
				reportMap.put("increaseInProvisionPrevYear", obj1.getIncreaseInProvisionCurrYear());
				reportMap.put("cwipCurrYear", obj1.getCwipCurrYear());
				reportMap.put("cwipPrevYear", obj1.getCwipPrevYear());
				reportMap.put("specialFundsGrantsCurrYear", obj1.getSpecialFundsGrantsCurrYear());
				reportMap.put("specialFundsGrantsPrevYear", obj1.getSpecialFundsGrantsPrevYear());
				reportMap.put("investmentsCurrYear", obj1.getInvestmentsCurrYear());
				reportMap.put("investmentsPrevYear", obj1.getInvestmentsPrevYear());
				reportMap.put("disposalOfAssetsCurrYear", obj1.getDisposalOfAssetsCurrYear());
				reportMap.put("disposalOfAssetsPrevYear", obj1.getDisposalOfAssetsPrevYear());
				reportMap.put("disposalOfInvestmentsCurrYear", obj1.getDisposalOfInvestmentsCurrYear());
				reportMap.put("disposalOfInvestmentsPrevYear", obj1.getDisposalOfInvestmentsPrevYear());
				reportMap.put("atBeginingPeriodCurrYear", obj1.getAtBeginingPeriodCurrYear());
				reportMap.put("atBeginingPeriodPrevYear", obj1.getAtBeginingPeriodPrevYear());
				reportMap.put("atEndPeriodCurrYear", obj1.getAtEndPeriodCurrYear());
				reportMap.put("atEndPeriodPrevYear", obj1.getAtEndPeriodPrevYear());

			}
			reportMap.put("loansFromBankReceivedCurrYear", null);
			reportMap.put("loansFromBankReceivedPrevYear", null);
			reportMap.put("loansRepaidDuringCurrYear", null);
			reportMap.put("loansRepaidDuringPrevYear", null);
			reportMap.put("advanceToEmployeesCurrYear", null);
			reportMap.put("advanceToEmployeesPrevYear", null);
			reportMap.put("loansToOthersCurrYear", null);
			reportMap.put("loansToOthersPrevYear", null);
			reportMap.put("financeExpenseCurrYear", null);
			reportMap.put("financeExpensePrevYear", null);
			reportMap.put("financeExpensePrevYear", null);
			reportMap.put("earmarkedFundsCurrYear", null);
			reportMap.put("earmarkedFundsPrevYear", null);
			reportMap.put("extraOrdinaryCurrYear", null);
			reportMap.put("extraOrdinaryPrevYear", null);
			reportMap.put("extraDepositReceivedCurrYear", null);
			reportMap.put("extraDepositReceivedPrevYear", null);
			reportMap.put("extraDepositWorksCurrYear", null);
			reportMap.put("extraDepositWorksPrevYear", null);

			reportMap.put("totalCurrYear", null);
			reportMap.put("totalPrevYear", null);
			reportMap.put("extraOtherCurrentLiabilitiesCurrYear", null);
			reportMap.put("extraOtherCurrentLiabilitiesPrevYear", null);
			reportMap.put("aCurrYear", cashFlowReportBean.getaCurrentYear());
			reportMap.put("aPrevYear", cashFlowReportBean.getaPrevYear());
			reportMap.put("bCurrYear", cashFlowReportBean.getbCurrentYear());
			reportMap.put("bPrevYear", cashFlowReportBean.getbPrevYear());
			reportMap.put("cCurrYear", cashFlowReportBean.getcCurrentYear());
			reportMap.put("cPrevYear", cashFlowReportBean.getcPrevYear());
			reportMap.put("abcCurrYear", cashFlowReportBean.getAbcCurrentYear());
			reportMap.put("abcPrevYear", cashFlowReportBean.getAbcPrevYear());
			reportMap.put("cashFlowDataSource", finalBalanceSheetL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportMap;
	}

	public JasperPrint createCashFlowPdfReport(Map<String, Object> reportParams, CashBookReportBean cashBookReportBeanm,
			List<CashFlowReportDataBean> lst) throws JRException, IOException {
		final InputStream stream = this.getClass().getResourceAsStream("/reports/templates/cashFlowNew.jrxml");
		final JasperReport report = JasperCompileManager.compileReport(stream);
		final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(lst);
		final JasperPrint print = JasperFillManager.fillReport(report, reportParams, source);
		return print;
	}

	public BigDecimal getFixedAssetDEbitAmount(String string, Date fromDate, Date toDate) {
		BigDecimal debitTotal = new BigDecimal(0);
		final StringBuffer query = new StringBuffer(500);

		List<BigDecimal> recqueryList = null;

		try {
			query.append("SELECT sum(debit) from assest_vouchers_view m where ")
					.append(getCashFlowDateQuery(fromDate, toDate));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(query.toString());
			recqueryList = rec.list();
			if (recqueryList != null) {
				debitTotal = recqueryList.get(0);
			}
			if (debitTotal == null) {
				debitTotal = new BigDecimal(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return debitTotal;
	}

	public BigDecimal getFixedAssetCreditAmount(String string, Date fromDate, Date toDate) {
		BigDecimal creditTotal = new BigDecimal(0);
		final StringBuffer query = new StringBuffer(500);

		List<BigDecimal> recqueryList = null;

		try {
			query.append("SELECT sum(credit) from assest_vouchers_view m where ")
					.append(getCashFlowDateQuery(fromDate, toDate));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(query.toString());
			recqueryList = rec.list();
			if (recqueryList != null) {
				creditTotal = recqueryList.get(0);
			}
			if (creditTotal == null) {
				creditTotal = new BigDecimal(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return creditTotal;
	}

	public String getCashFlowDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" m.voucherDate>='").append(DDMMYYYYFORMAT1.format(billDateFrom)).append("'");
			if (null != billDateTo)
				numDateQuery.append(" and m.voucherDate<='").append(DDMMYYYYFORMAT1.format(billDateTo)).append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}

	public BigDecimal getInvestmentDEbitAmount(String string, Date fromDate, Date toDate) {
		BigDecimal debitTotal = new BigDecimal(0);
		final StringBuffer query = new StringBuffer(500);

		List<BigDecimal> recqueryList = null;

		try {
			query.append("SELECT sum(debit) from investment_vouchers_view m where ")
					.append(getCashFlowDateQuery(fromDate, toDate));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(query.toString());
			recqueryList = rec.list();
			if (recqueryList != null) {
				debitTotal = recqueryList.get(0);
			}
			if (debitTotal == null) {
				debitTotal = new BigDecimal(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return debitTotal;
	}

	public BigDecimal getInvestmentCreditAmount(String string, Date fromDate, Date toDate) {
		BigDecimal creditTotal = new BigDecimal(0);
		final StringBuffer query = new StringBuffer(500);

		List<BigDecimal> recqueryList = null;

		try {
			query.append("SELECT sum(credit) from investment_vouchers_view m where ")
					.append(getCashFlowDateQuery(fromDate, toDate));
			SQLQuery rec = persistenceService.getSession().createSQLQuery(query.toString());
			recqueryList = rec.list();
			if (recqueryList != null) {
				creditTotal = recqueryList.get(0);
			}
			if (creditTotal == null) {
				creditTotal = new BigDecimal(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return creditTotal;
	}

	public BigDecimal getACurrentYear(List<CashFlowReportDataBean> lst1,
			List<CashFlowReportDataBean> finalBalanceSheetL) {
		BigDecimal aCurrentYear = new BigDecimal(0);
		try {
			if (lst1.get(0).getAdjustedIncomeCurrYear() == null) {
				adjustedIncomeCurrYear = new BigDecimal(0);
			} else {
				adjustedIncomeCurrYear = lst1.get(0).getAdjustedIncomeCurrYear();
			}
			if (finalBalanceSheetL.get(0).getSundryDebtorsCurrYear() == null) {
				sundryDebtorsCurrYear = new BigDecimal(0);
			} else {
				sundryDebtorsCurrYear = finalBalanceSheetL.get(0).getSundryDebtorsCurrYear();
			}
			if (finalBalanceSheetL.get(0).getStockInhandCurrYear() == null) {
				stockInhandCurrYear = new BigDecimal(0);
			} else {
				stockInhandCurrYear = finalBalanceSheetL.get(0).getStockInhandCurrYear();
			}
			if (finalBalanceSheetL.get(0).getPrepaidExpenseCurrYear() == null) {
				prepaidExpenseCurrYear = new BigDecimal(0);
			} else {
				prepaidExpenseCurrYear = finalBalanceSheetL.get(0).getPrepaidExpenseCurrYear();
			}
			if (finalBalanceSheetL.get(0).getOtherCurrentAssetsCurrYear() == null) {
				otherCurrentAssetsCurrYear = new BigDecimal(0);
			} else {
				otherCurrentAssetsCurrYear = finalBalanceSheetL.get(0).getOtherCurrentAssetsCurrYear();
			}
			if (finalBalanceSheetL.get(0).getDepositsReceivedCurrYear() == null) {
				depositsReceivedCurrYear = new BigDecimal(0);
			} else {
				depositsReceivedCurrYear = finalBalanceSheetL.get(0).getDepositsReceivedCurrYear();
			}
			if (finalBalanceSheetL.get(0).getDepositWorksCurrYear() == null) {
				depositWorksCurrYear = new BigDecimal(0);
			} else {
				depositWorksCurrYear = finalBalanceSheetL.get(0).getDepositWorksCurrYear();
			}
			if (finalBalanceSheetL.get(0).getOtherCurrentLiabilitiesCurrYear() == null) {
				otherCurrentLiabilitiesCurrYear = new BigDecimal(0);
			} else {
				otherCurrentLiabilitiesCurrYear = finalBalanceSheetL.get(0).getOtherCurrentLiabilitiesCurrYear();
			}
			if (finalBalanceSheetL.get(0).getIncreaseInProvisionCurrYear() == null) {
				increaseInProvisionsCurrentYear = new BigDecimal(0);
			} else {
				increaseInProvisionsCurrentYear = finalBalanceSheetL.get(0).getIncreaseInProvisionCurrYear();
			}
			aCurrentYear = adjustedIncomeCurrYear.add(sundryDebtorsCurrYear).add(stockInhandCurrYear)
					.add(prepaidExpenseCurrYear).add(otherCurrentAssetsCurrYear).add(depositsReceivedCurrYear)
					.add(depositWorksCurrYear).add(otherCurrentLiabilitiesCurrYear)
					.add(increaseInProvisionsCurrentYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aCurrentYear;
	}

	public BigDecimal getAPreviousYear(List<CashFlowReportDataBean> lst1,
			List<CashFlowReportDataBean> finalBalanceSheetL) {
		BigDecimal aPreviousYear = new BigDecimal(0);
		try {
			if (lst1.get(0).getAdjustedIncomePrevYear() == null) {
				adjustedIncomePrevYear = new BigDecimal(0);
			} else {
				adjustedIncomePrevYear = lst1.get(0).getAdjustedIncomePrevYear();
			}
			if (finalBalanceSheetL.get(0).getSundryDebtorsPrevYear() == null) {
				sundryDebtorsPrevYear = new BigDecimal(0);
			} else {
				sundryDebtorsPrevYear = finalBalanceSheetL.get(0).getSundryDebtorsPrevYear();
			}
			if (finalBalanceSheetL.get(0).getStockInhandPrevYear() == null) {
				stockInhandPrevYear = new BigDecimal(0);
			} else {
				stockInhandPrevYear = finalBalanceSheetL.get(0).getStockInhandPrevYear();
			}
			if (finalBalanceSheetL.get(0).getPrepaidExpensePrevYear() == null) {
				prepaidExpensePrevYear = new BigDecimal(0);
			} else {
				prepaidExpensePrevYear = finalBalanceSheetL.get(0).getPrepaidExpensePrevYear();
			}
			if (finalBalanceSheetL.get(0).getOtherCurrentAssetsPrevYear() == null) {
				otherCurrentAssetsPrevYear = new BigDecimal(0);
			} else {
				otherCurrentAssetsPrevYear = finalBalanceSheetL.get(0).getOtherCurrentAssetsPrevYear();
			}
			if (finalBalanceSheetL.get(0).getDepositsReceivedPrevYear() == null) {
				depositsReceivedPrevYear = new BigDecimal(0);
			} else {
				depositsReceivedPrevYear = finalBalanceSheetL.get(0).getDepositsReceivedPrevYear();
			}
			if (finalBalanceSheetL.get(0).getDepositWorksPrevYear() == null) {
				depositWorksPrevYear = new BigDecimal(0);
			} else {
				depositWorksPrevYear = finalBalanceSheetL.get(0).getDepositWorksPrevYear();
			}
			if (finalBalanceSheetL.get(0).getOtherCurrentLiabilitiesPrevYear() == null) {
				otherCurrentLiabilitiesPrevYear = new BigDecimal(0);
			} else {
				otherCurrentLiabilitiesPrevYear = finalBalanceSheetL.get(0).getOtherCurrentLiabilitiesPrevYear();
			}
			if (finalBalanceSheetL.get(0).getIncreaseInProvisionPrevYear() == null) {
				increaseInProvisionsPrevYear = new BigDecimal(0);
			} else {
				increaseInProvisionsPrevYear = finalBalanceSheetL.get(0).getIncreaseInProvisionPrevYear();
			}
			aPreviousYear = adjustedIncomePrevYear.add(sundryDebtorsPrevYear).add(stockInhandPrevYear)
					.add(prepaidExpensePrevYear).add(otherCurrentAssetsPrevYear).add(depositsReceivedPrevYear)
					.add(depositWorksPrevYear).add(otherCurrentLiabilitiesPrevYear).add(increaseInProvisionsPrevYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aPreviousYear;
	}

	public BigDecimal getbCurrentYear(List<CashFlowReportDataBean> lst1,
			List<CashFlowReportDataBean> finalBalanceSheetL) {
		BigDecimal bCurrentYear = new BigDecimal(0);
		try {
			if (finalBalanceSheetL.get(0).getCwipCurrYear() == null) {
				cwipCurrYear = new BigDecimal(0);
			} else {
				cwipCurrYear = finalBalanceSheetL.get(0).getCwipCurrYear();
			}
			if (finalBalanceSheetL.get(0).getSpecialFundsGrantsCurrYear() == null) {
				specialFundsGrantsCurrYear = new BigDecimal(0);
			} else {
				specialFundsGrantsCurrYear = finalBalanceSheetL.get(0).getSpecialFundsGrantsCurrYear();
			}
			if (finalBalanceSheetL.get(0).getInvestmentsCurrYear() == null) {
				investmentsCurrYear = new BigDecimal(0);
			} else {
				investmentsCurrYear = finalBalanceSheetL.get(0).getInvestmentsCurrYear();
			}
			if (finalBalanceSheetL.get(0).getDisposalOfAssetsCurrYear() == null) {
				disposalOfAssetsCurrYear = new BigDecimal(0);
			} else {
				disposalOfAssetsCurrYear = finalBalanceSheetL.get(0).getDisposalOfAssetsCurrYear();
			}
			if (finalBalanceSheetL.get(0).getDisposalOfInvestmentsCurrYear() == null) {
				disposalOfInvestmentsCurrYear = new BigDecimal(0);
			} else {
				disposalOfInvestmentsCurrYear = finalBalanceSheetL.get(0).getDisposalOfInvestmentsCurrYear();
			}
			if (lst1.get(0).getInvestmentIncomeReceivedCurrYear() == null) {
				investmentIncomeReceivedCurrYear = new BigDecimal(0);
			} else {
				investmentIncomeReceivedCurrYear = lst1.get(0).getInvestmentIncomeReceivedCurrYear();
			}
			if (lst1.get(0).getInterestIncomeReceivedCurrYear() == null) {
				interestIncomeReceivedCurrYear = new BigDecimal(0);
			} else {
				interestIncomeReceivedCurrYear = lst1.get(0).getInterestIncomeReceivedCurrYear();
			}
			bCurrentYear = cwipCurrYear.add(specialFundsGrantsCurrYear).add(investmentsCurrYear)
					.add(disposalOfAssetsCurrYear).add(disposalOfInvestmentsCurrYear)
					.add(investmentIncomeReceivedCurrYear).add(interestIncomeReceivedCurrYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bCurrentYear;
	}

	public BigDecimal getbPreviousYear(List<CashFlowReportDataBean> lst1,
			List<CashFlowReportDataBean> finalBalanceSheetL) {
		BigDecimal bPreviousYear = new BigDecimal(0);
		try {
			if (finalBalanceSheetL.get(0).getCwipPrevYear() == null) {
				cwipPrevYear = new BigDecimal(0);
			} else {
				cwipPrevYear = finalBalanceSheetL.get(0).getCwipPrevYear();
			}
			if (finalBalanceSheetL.get(0).getSpecialFundsGrantsPrevYear() == null) {
				specialFundsGrantsPrevYear = new BigDecimal(0);
			} else {
				specialFundsGrantsPrevYear = finalBalanceSheetL.get(0).getSpecialFundsGrantsPrevYear();
			}
			if (finalBalanceSheetL.get(0).getInvestmentsPrevYear() == null) {
				investmentsPrevYear = new BigDecimal(0);
			} else {
				investmentsPrevYear = finalBalanceSheetL.get(0).getInvestmentsPrevYear();
			}
			if (finalBalanceSheetL.get(0).getDisposalOfAssetsPrevYear() == null) {
				disposalOfAssetsPrevYear = new BigDecimal(0);
			} else {
				disposalOfAssetsPrevYear = finalBalanceSheetL.get(0).getDisposalOfAssetsPrevYear();
			}
			if (finalBalanceSheetL.get(0).getDisposalOfInvestmentsPrevYear() == null) {
				disposalOfInvestmentsPrevYear = new BigDecimal(0);
			} else {
				disposalOfInvestmentsPrevYear = finalBalanceSheetL.get(0).getDisposalOfInvestmentsPrevYear();
			}
			if (lst1.get(0).getInvestmentIncomeReceivedPrevYear() == null) {
				investmentIncomeReceivedPrevYear = new BigDecimal(0);
			} else {
				investmentIncomeReceivedPrevYear = lst1.get(0).getInvestmentIncomeReceivedPrevYear();
			}
			if (lst1.get(0).getInterestIncomeReceivedPrevYear() == null) {
				interestIncomeReceivedPrevYear = new BigDecimal(0);
			} else {
				interestIncomeReceivedPrevYear = lst1.get(0).getInterestIncomeReceivedPrevYear();
			}
			bPreviousYear = cwipPrevYear.add(specialFundsGrantsPrevYear).add(investmentsPrevYear)
					.add(disposalOfAssetsPrevYear).add(disposalOfInvestmentsPrevYear)
					.add(investmentIncomeReceivedPrevYear).add(interestIncomeReceivedPrevYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bPreviousYear;
	}

	public BigDecimal getabcCurrentYear(CashBookReportBean cashBookReportBean) {
		BigDecimal abcCurrent = new BigDecimal(0);
		try {
			if (cashBookReportBean.getaCurrentYear() == null) {
				aCurrYear = new BigDecimal(0);
			}else {
				aCurrYear = cashBookReportBean.getaCurrentYear();
			}
			if (cashBookReportBean.getbCurrentYear() == null) {
				bCurrYear = new BigDecimal(0);
			}else {
				bCurrYear = cashBookReportBean.getbCurrentYear();
			}
			if (cashBookReportBean.getcCurrentYear() == null) {
				cCurrYear = new BigDecimal(0);
			}else {
				cCurrYear = cashBookReportBean.getcCurrentYear();
			}
			abcCurrent = aCurrYear.add(bCurrYear).add(cCurrYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return abcCurrent;
	}

	public BigDecimal getabcPreviousYear(CashBookReportBean cashBookReportBean) {
		BigDecimal abcPrev = new BigDecimal(0);
		try {
			if (cashBookReportBean.getaPrevYear() == null) {
				aPrevYear = new BigDecimal(0);
			} else {
				aPrevYear = cashBookReportBean.getaPrevYear();
			}
			if (cashBookReportBean.getbPrevYear() == null) {
				bPrevYear = new BigDecimal(0);
			} else {
				bPrevYear = cashBookReportBean.getbPrevYear();
			}
			if (cashBookReportBean.getcPrevYear() == null) {
				cPrevYear = new BigDecimal(0);
			} else {
				cPrevYear = cashBookReportBean.getcPrevYear();
			}

			abcPrev = aPrevYear.add(bPrevYear).add(cPrevYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return abcPrev;
	}

}