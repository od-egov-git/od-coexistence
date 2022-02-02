package org.egov.egf.web.controller.cashbook;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementResultObject;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.services.report.IncomeExpenditureService;
import org.egov.services.report.ReportService;
import org.egov.utils.Constants;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

public class CashFlowReportService {
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	AppConfigValueService appConfigValuesService;
	Statement statement = new Statement();
	@Autowired
	IncomeExpenditureService incomeExpenditureService;
	// even though it is instance variable it is fine to make it prototype.
	// Minor code length is constant for implementation
	int minorCodeLength;
	List<Character> coaType = new ArrayList<Character>();
	private static final String I = "I";
	private static final String E = "E";
	private static final String IE = "IE";
	final static Logger LOGGER = Logger.getLogger(CashFlowReportService.class);
	@PersistenceContext
	protected EntityManager entityManager;

	public List<Fund> getFunds() {
		final Criteria voucherHeaderCriteria = persistenceService.getSession().createCriteria(CVoucherHeader.class);
		final List fundIdList = voucherHeaderCriteria
				.setProjection(Projections.distinct(Projections.property("fundId.id"))).list();
		if (!fundIdList.isEmpty())
			return persistenceService.getSession().createCriteria(Fund.class).add(Restrictions.in("id", fundIdList))
					.list();
		return new ArrayList<Fund>();
	}

	public void populateCurrentYearAmountPerFund(final Date toDate, final Date fromDate) {
		try {
			/*
			 * minorCodeLength = Integer.valueOf(getAppConfigValueFor(Constants.EGF,
			 * "coa_minorcode_length"));
			 * System.out.println("### minorCodeLength ::"+minorCodeLength);
			 */
			minorCodeLength = 5;
		} catch (Exception e) {
			e.printStackTrace();
		}
		coaType.add('I');
		coaType.add('E');
		final String filterQuery = "";
		System.out.println("populateIEStatement");
		final BigDecimal divisor = statement.getDivisor();
		final Statement expenditure = new Statement();
		final Statement income = new Statement();
		List<StatementResultObject> allGlCodes = new ArrayList<StatementResultObject>();

		allGlCodes = getAllGlCodesFor(IE);
		statement.setFunds(getFunds());
		List<StatementResultObject> results = new ArrayList<StatementResultObject>();
		List<StatementResultObject> PreYearResults = new ArrayList<StatementResultObject>();
		results = getTransactionAmount(filterQuery, toDate, fromDate, "'I','E'", IE, minorCodeLength);
		PreYearResults = getTransactionAmount(filterQuery, getPreviousYearFor(toDate), getPreviousYearFor(fromDate),
				"'I','E'", IE, minorCodeLength);

		for (final StatementResultObject queryObject : allGlCodes) {

			if (queryObject.getGlCode() == null)
				queryObject.setGlCode("");
			final List<StatementResultObject> rows = getRowWithGlCode(results, queryObject.getGlCode());
			if (rows.isEmpty() && queryObject.getGlCode() != null) {
				if (contains(PreYearResults, queryObject.getGlCode())) {
					final List<StatementResultObject> preRow = getRowWithGlCode(PreYearResults,
							queryObject.getGlCode());
					final IEStatementEntry preentry = new IEStatementEntry();
					for (final StatementResultObject pre : preRow)
						if (I.equalsIgnoreCase(queryObject.getType().toString())) {
							if (pre.isIncome())
								pre.negateAmount();
							preentry.getPreviousYearAmount().put(
									getFundNameForId(statement.getFunds(), Integer.valueOf(pre.getFundId())),
									divideAndRound(pre.getAmount(), divisor));
						} else if (E.equalsIgnoreCase(queryObject.getType().toString())) {
							if (pre.isIncome())
								pre.negateAmount();
							preentry.getPreviousYearAmount().put(
									getFundNameForId(statement.getFunds(), Integer.valueOf(pre.getFundId())),
									divideAndRound(pre.getAmount(), divisor));
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
			} else
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
							if (queryObject.getGlCode() != null && contains(PreYearResults, row.getGlCode())) {
								final List<StatementResultObject> preRow = getRowWithGlCode(PreYearResults,
										queryObject.getGlCode());
								for (final StatementResultObject pre : preRow) {
									if (pre.isIncome())
										pre.negateAmount();
									if (pre.getGlCode() != null && pre.getGlCode().equals(row.getGlCode()))
										entry.getPreviousYearAmount()
												.put(getFundNameForId(statement.getFunds(),
														Integer.valueOf(pre.getFundId())),
														divideAndRound(pre.getAmount(), divisor));
								}
							}
						}
						if (queryObject.getGlCode() != null) {
							entry.setGlCode(queryObject.getGlCode());
							entry.setAccountName(queryObject.getScheduleName());
							entry.setScheduleNo(queryObject.getScheduleNumber());
						}
						if (I.equalsIgnoreCase(row.getType().toString()))
							income.addIE(entry);
						else if (E.equalsIgnoreCase(row.getType().toString()))
							expenditure.addIE(entry);
					}
				}

		}
		addRowsToStatement(statement, expenditure, income);
	}

	protected void addRowsToStatement(final Statement balanceSheet, final Statement assets,
			final Statement liabilities) {
		IEStatementEntry incomeEntry = new IEStatementEntry();
		IEStatementEntry expenseEntry = new IEStatementEntry();
		List<IEStatementEntry> totalIncomeOverExpense = new ArrayList<IEStatementEntry>();

		if (liabilities.sizeIE() > 0) {
			balanceSheet.addIE(new IEStatementEntry(null, Constants.INCOME, "", true));
			incomeEntry = getTotalIncomeFundwise(liabilities);
			balanceSheet.addAllIE(liabilities);
			balanceSheet.addIE(incomeEntry);
		}
		if (assets.sizeIE() > 0) {
			balanceSheet.addIE(new IEStatementEntry(null, Constants.EXPENDITURE, "", true));
			expenseEntry = getTotalExpenseFundwise(assets);
			balanceSheet.addAllIE(assets);
			balanceSheet.addIE(expenseEntry);
		}
		totalIncomeOverExpense = computeTotalsIncomeExpense(incomeEntry, expenseEntry);
		for (final IEStatementEntry exp : totalIncomeOverExpense)
			balanceSheet.addIE(exp);

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
		return new IEStatementEntry("A", Constants.TOTAL_INCOME, fundNetTotals, fundPreTotals, true);
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
		return new IEStatementEntry("B", Constants.TOTAL_EXPENDITURE, fundNetTotals, fundPreTotals, true);
	}

	protected BigDecimal zeroOrValue(final BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
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

	public Statement addBudgetDetails(final Statement ie) {
		final List<StatementResultObject> budgetForMajorCodes = getBudgetForMajorCodes(ie);

		print(budgetForMajorCodes);
		final List<StatementResultObject> budgetReappForMajorCodes = getBudgetReappMinorCodes(ie);
		// if(LOGGER.isDebugEnabled())
		LOGGER.error("Budget Reapp Amounts...........................");
		print(budgetReappForMajorCodes);
		BigDecimal totalBudget = BigDecimal.ZERO;
		for (final StatementResultObject ent : budgetForMajorCodes)
			for (final StatementResultObject stm : budgetReappForMajorCodes)
				if (ent.getGlCode() != null && ent.getGlCode().equalsIgnoreCase(stm.getGlCode()))
					if (ent.getAmount() != null) {
						if (stm.getAmount() != null)
							ent.setAmount(ent.getAmount().add(stm.getAmount()));
					} else if (stm.getAmount() != null)
						ent.setAmount(stm.getAmount());

		for (final IEStatementEntry ent : ie.getIeEntries())
			inner: for (final StatementResultObject stm : budgetForMajorCodes)
				if (ent.getGlCode() != null && ent.getGlCode().equalsIgnoreCase(stm.getGlCode())) {
					ent.setBudgetAmount(stm.getAmount().setScale(2));
					totalBudget = totalBudget.add(ent.getBudgetAmount());
				}

		for (final IEStatementEntry ent : ie.getIeEntries())
			if (ent.getAccountName() != null && ent.getAccountName().equalsIgnoreCase(Constants.TOTAL_EXPENDITURE))
				ent.setBudgetAmount(totalBudget);
		return ie;
	}

	private List<StatementResultObject> getBudgetForMajorCodes(final Statement incomeExpenditureStatement) {

		final StringBuffer queryStr = new StringBuffer(1024);

		queryStr.append(" select coa.majorCode as glcode, sum(bd.approvedamount) as amount ");

		queryStr.append(
				" from egf_budgetdetail bd , egf_budgetgroup bg,egf_budget b, chartofaccounts coa, eg_wf_states wfs ");

		queryStr.append(
				"where ((bg.maxcode<=coa.id and bg.mincode>=coa.id) or bg.majorcode=coa.id ) and bd.budgetgroup= bg.id "
						+ " and bd.budget=b.id and  bd.state_id=wfs.id  and wfs.value='END'  and b.isbere=:isBeRe and b.financialyearid=:finYearId   ");
		if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
				&& incomeExpenditureStatement.getFund().getId() != 0)
			queryStr.append(" and bd.fund=" + incomeExpenditureStatement.getFund().getId());
		if (incomeExpenditureStatement.getDepartment() != null
				&& incomeExpenditureStatement.getDepartment().getCode() != null
				&& !incomeExpenditureStatement.getDepartment().getCode().isEmpty()) {
			queryStr.append(
					" and bd.executing_department='" + incomeExpenditureStatement.getDepartment().getCode() + "' ");
		}
		if (incomeExpenditureStatement.getFunction() != null && incomeExpenditureStatement.getFunction().getId() != null
				&& incomeExpenditureStatement.getFunction().getId() != 0)
			queryStr.append("  and bd.function= " + incomeExpenditureStatement.getFunction().getId());

		queryStr.append(" and coa.majorcode is not null  group by coa.majorCode ");

		queryStr.append(" order by 1");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("query is " + queryStr.toString());
		SQLQuery budgteQuery = null;
		Session session = null;
		if (incomeExpenditureStatement.isRestData()) {
			session = entityManager.unwrap(Session.class);
			budgteQuery = session.createSQLQuery(queryStr.toString());
			budgteQuery.addScalar("glCode").addScalar("amount")
					.setResultTransformer(Transformers.aliasToBean(StatementResultObject.class));
			budgteQuery.setLong("finYearId", incomeExpenditureStatement.getFinancialYear().getId()).setString("isBeRe",
					"RE");
		} else {
			budgteQuery = persistenceService.getSession().createSQLQuery(queryStr.toString());
			budgteQuery.addScalar("glCode").addScalar("amount")
					.setResultTransformer(Transformers.aliasToBean(StatementResultObject.class));
			budgteQuery.setLong("finYearId", incomeExpenditureStatement.getFinancialYear().getId()).setString("isBeRe",
					"RE");
		}

		final List<StatementResultObject> list = budgteQuery.list();
		return list;

	}

	private void print(final List<StatementResultObject> list) {

		for (final StatementResultObject stm : list)
			LOGGER.error(stm.getGlCode() + "         " + stm.getAmount());
	}

	private List<StatementResultObject> getBudgetReappMinorCodes(final Statement incomeExpenditureStatement) {
		final StringBuffer queryStr = new StringBuffer(1024);

		queryStr.append(" select coa.majorcode as glCode, sum(bdr.addition_amount- bdr.deduction_amount) as amount ");

		queryStr.append(
				" from egf_budgetdetail bd , egf_budgetgroup bg,egf_budget b, chartofaccounts coa,eg_wf_states wfs,egf_budget_reappropriation bdr where ((bg.maxcode<=coa.id and bg.mincode>=coa.id) or bg.majorcode=coa.id ) and bd.budgetgroup= bg.id "
						+ "  and bdr.budgetdetail=bd.id and bd.budget=b.id and bdr.state_id=wfs.id  and wfs.value='END' and b.isbere=:isBeRe and b.financialyearid=:finYearId  ");

		if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
				&& incomeExpenditureStatement.getFund().getId() != 0)
			queryStr.append(" and bd.fund=" + incomeExpenditureStatement.getFund().getId());
		if (incomeExpenditureStatement.getDepartment() != null
				&& incomeExpenditureStatement.getDepartment().getCode() != null
				&& !incomeExpenditureStatement.getDepartment().getCode().isEmpty()) {
			queryStr.append(
					" and bd.executing_department='" + incomeExpenditureStatement.getDepartment().getCode() + "' ");
		}
		if (incomeExpenditureStatement.getFunction() != null && incomeExpenditureStatement.getFunction().getId() != null
				&& incomeExpenditureStatement.getFunction().getId() != 0)
			queryStr.append("  and bd.function= " + incomeExpenditureStatement.getFunction().getId());
		queryStr.append("  group by coa.majorCode ");

		queryStr.append(" order by 1 asc");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("query is " + queryStr.toString());

		SQLQuery budgteReappQuery = null;
		Session session = null;
		if (incomeExpenditureStatement.isRestData()) {
			session = entityManager.unwrap(Session.class);
			budgteReappQuery = session.createSQLQuery(queryStr.toString());
			budgteReappQuery.addScalar("glCode").addScalar("amount")
					.setResultTransformer(Transformers.aliasToBean(StatementResultObject.class));
			budgteReappQuery.setLong("finYearId", incomeExpenditureStatement.getFinancialYear().getId())
					.setString("isBeRe", "RE");
		} else {

			budgteReappQuery = persistenceService.getSession().createSQLQuery(queryStr.toString());
			budgteReappQuery.addScalar("glCode").addScalar("amount")
					.setResultTransformer(Transformers.aliasToBean(StatementResultObject.class));
			budgteReappQuery.setLong("finYearId", incomeExpenditureStatement.getFinancialYear().getId())
					.setString("isBeRe", "RE");
		}

		final List<StatementResultObject> list = budgteReappQuery.list();
		return list;
	}

	protected List<StatementResultObject> getAllGlCodesFor(final String scheduleReportType) {
		final Query query = persistenceService.getSession()
				.createSQLQuery("select distinct coa.majorcode as glCode,s.schedule as scheduleNumber,"
						+ "s.schedulename as scheduleName,coa.type as type from chartofaccounts coa, schedulemapping s "
						+ "where s.id=coa.scheduleid and coa.classification=2 and s.reporttype = '" + scheduleReportType
						+ "' order by coa.majorcode")
				.addScalar("glCode").addScalar("scheduleNumber").addScalar("scheduleName").addScalar("type")
				.setResultTransformer(Transformers.aliasToBean(StatementResultObject.class));
		return query.list();
	}

	public String getFormattedDate(final Date date) {
		final SimpleDateFormat formatter = Constants.DDMMYYYYFORMAT1;
		return formatter.format(date);
	}

	List<StatementResultObject> getTransactionAmount(final String filterQuery, final Date toDate, final Date fromDate,
			final String coaType, final String subReportType, final int minorCodeLength) {
		// String voucherStatusToExclude = getAppConfigValueFor("EGF",
		// "statusexcludeReport");

		final Query query = persistenceService.getSession().createSQLQuery(
				"select c.majorcode as glCode,v.fundid as fundId,c.type as type,sum(debitamount)-sum(creditamount) as amount"
						+ " from generalledger g,chartofaccounts c,voucherheader v ,vouchermis mis where v.id=mis.voucherheaderid and "
						+ "v.id=g.voucherheaderid and c.type in(" + coaType
						+ ") and c.id=g.glcodeid and v.status not in(4,5)  AND v.voucherdate <= '"
						+ getFormattedDate(toDate) + "' and v.voucherdate >='" + getFormattedDate(fromDate)
						+ "' and substr(c.glcode,1," + minorCodeLength + ") in "
						+ "(select distinct coa2.glcode from chartofaccounts coa2, schedulemapping s where s.id=coa2.scheduleid and "
						+ "coa2.classification=2 and s.reporttype = '" + subReportType + "') " + filterQuery
						+ " group by c.majorcode,v.fundid,c.type order by c.majorcode")
				.addScalar("glCode").addScalar("fundId", BigDecimalType.INSTANCE).addScalar("type")
				.addScalar("amount", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(StatementResultObject.class));

		System.out.println("Executing Query------------>>>" + query.toString());
		return query.list();
	}

	public Date getPreviousYearFor(final Date date) {
		final GregorianCalendar previousYearToDate = new GregorianCalendar();
		previousYearToDate.setTime(date);
		final int prevYear = previousYearToDate.get(Calendar.YEAR) - 1;
		previousYearToDate.set(Calendar.YEAR, prevYear);
		return previousYearToDate.getTime();
	}

	public String getAppConfigValueFor(final String module, final String key) {
		String returnValue = "";
		try {
			returnValue = appConfigValuesService.getConfigValuesByModuleAndKey(module, key).get(0).getValue();
		} catch (final Exception e) {

			new ValidationException(Arrays.asList(
					new ValidationError(key + "is not defined in appconfig", key + "is not defined in appconfig")));
		}
		return returnValue;
	}

	List<StatementResultObject> getRowWithGlCode(final List<StatementResultObject> results, final String glCode) {
		final List<StatementResultObject> resultList = new ArrayList<StatementResultObject>();
		for (final StatementResultObject balanceSheetQueryObject : results)
			if (glCode.equalsIgnoreCase(balanceSheetQueryObject.getGlCode())
					&& balanceSheetQueryObject.getAmount().compareTo(BigDecimal.ZERO) != 0)
				resultList.add(balanceSheetQueryObject);
		return resultList;
	}

	boolean contains(final List<StatementResultObject> result, final String glCode) {
		for (final StatementResultObject row : result)
			if (row.getGlCode() != null && row.getGlCode().equalsIgnoreCase(glCode))
				return true;
		return false;
	}

	public String getFundNameForId(final List<Fund> fundList, final Integer id) {
		for (final Fund fund : fundList)
			if (id.equals(fund.getId()))
				return fund.getName();
		return "";
	}

	public BigDecimal divideAndRound(BigDecimal value, final BigDecimal divisor) {
		value = value.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
		return value;
	}

	void addFundAmountIE(final List<Fund> fundList, final Statement type, final BigDecimal divisor,
			final StatementResultObject row) {
		for (int index = 0; index < type.sizeIE(); index++) {
			final BigDecimal amount = divideAndRound(row.getAmount(), divisor);

			if (type.getIE(index).getGlCode() != null && row.getGlCode().equals(type.getIE(index).getGlCode()))
				type.getIE(index).getNetAmount().put(getFundNameForId(fundList, Integer.valueOf(row.getFundId())),
						amount);
		}
	}
}
