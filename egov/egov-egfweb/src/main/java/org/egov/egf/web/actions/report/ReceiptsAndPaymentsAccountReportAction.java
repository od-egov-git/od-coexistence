package org.egov.egf.web.actions.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.egf.model.Statement;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.report.ReceiptsAndPaymentsService;
import org.egov.utils.Constants;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@ParentPackage("egov")
@Results({ @Result(name = "report", location = "receiptsAndPaymentsAccountReport-report.jsp"),
		@Result(name = "result", location = "receiptsAndPaymentsAccountReport-results.jsp"),
		@Result(name = "PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
				Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
				"no-cache;filename=ReceiptsAndPaymentsAccountReport.pdf" }),
		@Result(name = "XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
				Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
				"no-cache;filename=ReceiptsAndPaymentsAccountReport.xls" }) })
public class ReceiptsAndPaymentsAccountReportAction extends BaseFormAction {

	private static final long serialVersionUID = 1L;

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@SuppressWarnings("deprecation")
	@Autowired
	private EgovMasterDataCaching masterDataCache;

	@Autowired
	private ReceiptsAndPaymentsService ReceiptsAndPaymentsService;

	Statement receiptsAndPaymentsAccountStatement = new Statement();
	private Date todayDate;
	private final StringBuffer heading = new StringBuffer();
	private StringBuffer statementheading = new StringBuffer();

	public ReceiptsAndPaymentsAccountReportAction() {
		addRelatedEntity("department", Department.class);
		addRelatedEntity("function", CFunction.class);
		addRelatedEntity("functionary", Functionary.class);
		addRelatedEntity("financialYear", CFinancialYear.class);
		addRelatedEntity("field", Boundary.class);
		addRelatedEntity("fund", Fund.class);
	}

	@Override
	public Object getModel() {
		return receiptsAndPaymentsAccountStatement;
	}

	@Override
	public void prepare() {
		persistenceService.getSession().setDefaultReadOnly(true);
		persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		if (!parameters.containsKey("showDropDown")) {
			addDropdownData("departmentList", masterDataCache.get("egi-department"));
			addDropdownData("functionList", masterDataCache.get("egi-function"));
			addDropdownData("fundDropDownList", masterDataCache.get("egi-fund"));
			addDropdownData("financialYearList", getPersistenceService()
					.findAllBy("from CFinancialYear where isActive=true  order by finYearRange desc "));
		}
	}

	protected void setRelatedEntitesOn() {
		setTodayDate(new Date());
		if (receiptsAndPaymentsAccountStatement.getFinancialYear() != null
				&& receiptsAndPaymentsAccountStatement.getFinancialYear().getId() != null
				&& receiptsAndPaymentsAccountStatement.getFinancialYear().getId() != 0) {

			receiptsAndPaymentsAccountStatement
					.setFinancialYear((CFinancialYear) getPersistenceService().find("from CFinancialYear where id=?",
							receiptsAndPaymentsAccountStatement.getFinancialYear().getId()));
			heading.append(" for the Financial Year "
					+ receiptsAndPaymentsAccountStatement.getFinancialYear().getFinYearRange());
		}
	}

	@SkipValidation
	@Action(value = "/report/receiptsAndPaymentsAccountReport-generateReport")
	public String generateReceiptsAndPaymentsAccountReport() {
		return "report";
	}

	@Action(value = "/report/receiptsAndPaymentsAccountReport-ajaxPrintReceiptsAndPaymentsAccountReport")
	public String ajaxPrintReceiptsAndPaymentsAccountReport() {
		try {
			populateDataSource();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "result";
	}

	private void populateDataSource() {

		setRelatedEntitesOn();
		statementheading.append("Receipts and Payments Account Report").append(heading);
		if (receiptsAndPaymentsAccountStatement.getFund() != null
				&& receiptsAndPaymentsAccountStatement.getFund().getId() != null
				&& receiptsAndPaymentsAccountStatement.getFund().getId() != 0) {
			final List<Fund> fundlist = new ArrayList<Fund>();
			fundlist.add(receiptsAndPaymentsAccountStatement.getFund());
			receiptsAndPaymentsAccountStatement.setFunds(fundlist);
			ReceiptsAndPaymentsService.populateRPStatement(receiptsAndPaymentsAccountStatement);
		} else {
			receiptsAndPaymentsAccountStatement.setFunds(ReceiptsAndPaymentsService.getFunds());
			ReceiptsAndPaymentsService.populateRPStatement(receiptsAndPaymentsAccountStatement);
		}
	}

	public void setReceiptsAndPaymentsAccountStatement(Statement receiptsAndPaymentsAccountStatement) {
		this.receiptsAndPaymentsAccountStatement = receiptsAndPaymentsAccountStatement;
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public StringBuffer getStatementheading() {
		return statementheading;
	}

	public void setStatementheading(StringBuffer statementheading) {
		this.statementheading = statementheading;
	}

	public String getCurrentYearToDate() {
		if ("Date".equalsIgnoreCase(receiptsAndPaymentsAccountStatement.getPeriod())) {
			return ReceiptsAndPaymentsService.getFormattedDate(receiptsAndPaymentsAccountStatement.getFromDate())
					+ " To "
					+ ReceiptsAndPaymentsService.getFormattedDate(receiptsAndPaymentsAccountStatement.getToDate());
		} else {
			return ReceiptsAndPaymentsService
					.getFormattedDate(ReceiptsAndPaymentsService.getToDate(receiptsAndPaymentsAccountStatement));
		}
	}
	
	public String getPreviousYearToDate() {
		if ("Date".equalsIgnoreCase(receiptsAndPaymentsAccountStatement.getPeriod())) {
			return ReceiptsAndPaymentsService.getFormattedDate(
					ReceiptsAndPaymentsService.getPreviousYearFor(receiptsAndPaymentsAccountStatement.getFromDate()))
					+ " To " + ReceiptsAndPaymentsService.getFormattedDate(ReceiptsAndPaymentsService
							.getPreviousYearFor(receiptsAndPaymentsAccountStatement.getToDate()));
		} else {
			return ReceiptsAndPaymentsService.getFormattedDate(ReceiptsAndPaymentsService
					.getPreviousYearFor(ReceiptsAndPaymentsService.getToDate(receiptsAndPaymentsAccountStatement)));
		}
	}
	
    public Statement getReceiptsAndPaymentsAccountStatement() {
        return receiptsAndPaymentsAccountStatement;
    }

}
