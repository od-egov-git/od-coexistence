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
package org.egov.egf.web.actions.report;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.commons.utils.EntityType;
import org.egov.deduction.model.EgRemittanceDetail;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.model.TDSEntry;
import org.egov.egf.model.VoucherDetailMain;
import org.egov.egf.model.VoucherDetailMiscMapping;
import org.egov.infra.admin.master.service.DepartmentService;
//import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.deduction.DeductionReportBean;
import org.egov.model.deduction.RemittanceBean;
import org.egov.model.instrument.InstrumentVoucher;
import org.egov.model.masters.Contractor;
import org.egov.model.masters.Supplier;
import org.egov.model.recoveries.Recovery;
import org.egov.services.deduction.RemitRecoveryService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


@Results(value = {
        @Result(name = "PDF", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
                "application/pdf", "contentDisposition", "no-cache;filename=DeductionDetailedReport.pdf" }),
        @Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
                "application/xls", "contentDisposition", "no-cache;filename=DeductionDetailedReport.xls" }),
        @Result(name = "deductionXLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
                "application/xls", "contentDisposition", "no-cache;filename=DeductionReports.xls" }),
        @Result(name = "summary-PDF", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
                "contentType", "application/pdf", "contentDisposition", "no-cache;filename=DeductionsRemittanceSummary.pdf" }),
        @Result(name = "summary-XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
                "contentType", "application/xls", "contentDisposition", "no-cache;filename=DeductionsRemittanceSummary.xls" }),
        @Result(name = "results", location = "pendingTDSReport-results.jsp"),
        @Result(name = "deductionResults", location = "pendingTDSReport-deductionResults.jsp"),
        @Result(name = "entities", location = "pendingTDSReport-entities.jsp"),
        @Result(name = "summaryForm", location = "pendingTDSReport-summaryForm.jsp"),
        @Result(name = "deductionForm", location = "pendingTDSReport-deductionForm.jsp"),
        @Result(name = "reportForm", location = "pendingTDSReport-reportForm.jsp"),
        @Result(name = "summaryResults", location = "pendingTDSReport-summaryResults.jsp")
})

@ParentPackage("egov")
public class PendingTDSReportAction extends BaseFormAction {
    /**
     *
     */
    private static final long serialVersionUID = 4077974966135536959L;
    String jasperpath = "pendingTDSReport";
    String summaryJasperpath = "summaryTDSReport";
    private Date asOnDate = new Date();
    private Date fromDate;
    private InputStream inputStream;
    private ReportService reportService;
    private String partyName = "";
    private String type = "";
    private Integer detailKey;
    private boolean showRemittedEntries = false;
    private List<RemittanceBean> pendingTDS = new ArrayList<RemittanceBean>();
    private List<TDSEntry> remittedTDS = new ArrayList<TDSEntry>();
    private List<TDSEntry> inWorkflowTDS = new ArrayList<TDSEntry>();
    private Recovery recovery = new Recovery();
    private Fund fund = new Fund();
    private Department department = new Department();
   
 @Autowired
 @Qualifier("persistenceService")
 private PersistenceService persistenceService;
 @Autowired
    private EgovCommon egovCommon;
    private final List<EntityType> entitiesList = new ArrayList<EntityType>();
    private RemitRecoveryService remitRecoveryService;
    private FinancialYearHibernateDAO financialYearDAO;
    private String message = "";
    private String mode = "";
    private static Logger LOGGER = Logger.getLogger(PendingTDSReportAction.class);
    @Autowired
    private EgovMasterDataCaching masterDataCache;
    
    @Autowired
    private Environment environment;
    
    @Autowired
    private DepartmentService departmentService;
    
    public void setFinancialYearDAO(final FinancialYearHibernateDAO financialYearDAO) {
        this.financialYearDAO = financialYearDAO;
    }

    public void setRemitRecoveryService(final RemitRecoveryService remitRecoveryService) {
        this.remitRecoveryService = remitRecoveryService;
    }

    @Override
    public String execute() throws Exception {
        mode = "deduction";
        return "reportForm";
    }

    @Action(value = "/report/pendingTDSReport-summaryReport")
    public String summaryReport() throws Exception {
        return "summaryForm";
    }

    @Action(value = "/report/pendingTDSReport-deductionReport")
    public String deductionReport() throws Exception {
        return "deductionForm";
    }

    @Override
    public void prepare() {
        persistenceService.getSession().setDefaultReadOnly(true);
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
        super.prepare();
//        addDropdownData("departmentList", persistenceService.findAllBy("from Department order by name"));
        addDropdownData("departmentList",this.masterDataCache.get("egi-department"));
        addDropdownData("fundList", persistenceService.findAllBy(" from Fund where isactive=true and isnotleaf=false order by name"));  

        addDropdownData("recoveryList",
                persistenceService.findAllBy(" from Recovery where isactive=true order by chartofaccounts.glcode"));
        addDropdownData("recoveryListReport",
                persistenceService.findAllBy(" from Recovery where isreport ='Y' and isactive=true order by chartofaccounts.glcode"));
    }

    @Action(value = "/report/pendingTDSReport-ajaxLoadData")
    public String ajaxLoadData() {
        populateData();
        return "results";
    }

    @Action(value = "/report/pendingTDSReport-ajaxLoadDeductionData")
    public String ajaxLoadDeductionData() {
    	try
    	{
    		populateData();
    	}catch (Exception e) {
			e.printStackTrace();
		}
        
        System.out.println("END");
        return "deductionResults";
    }

    @Action(value = "/report/pendingTDSReport-ajaxLoadSummaryData")
    public String ajaxLoadSummaryData() {
        populateSummaryData();
        return "summaryResults";
    }

    public void setAsOnDate(final Date startDate) {
        asOnDate = startDate;
    }

    public Date getAsOnDate() {
        return asOnDate;
    }

    public String getFormattedDate(final Date date) {
        return Constants.DDMMYYYYFORMAT2.format(date);
    }

    @Action(value = "/report/pendingTDSReport-exportPdf")
    public String exportPdf() throws JRException, IOException {
        generateReport();
        return "PDF";
    }

    @Action(value = "/report/pendingTDSReport-exportSummaryPdf")
    public String exportSummaryPdf() throws JRException, IOException {
        generateSummaryReport();
        return "summary-PDF";
    }

    private void generateReport() {
        populateData();
        final ReportRequest reportInput = new ReportRequest(jasperpath, pendingTDS, getParamMap());
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
    }

    private void generateSummaryReport() {
        populateSummaryData();
        final ReportRequest reportInput = new ReportRequest(summaryJasperpath, remittedTDS, getParamMap());
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
    }

    Map<String, Object> getParamMap() {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("remittedTDSJasper", this.getClass().getResourceAsStream("/reports/templates/remittedTDSReport.jasper"));
        paramMap.put("inWorkflowTDSJasper", this.getClass().getResourceAsStream("/reports/templates/inWorkflowTDSReport.jasper"));
        paramMap.put("inWorkflowTDS", inWorkflowTDS);
        if (showRemittedEntries)
            paramMap.put("remittedTDS", remittedTDS);
        else
            paramMap.put("remittedTDS", null);
        final String formatedAsOndate = Constants.DDMMYYYYFORMAT2.format(asOnDate);
        paramMap.put("asOnDate", formatedAsOndate);
        if (fromDate != null)
        {
            final String formatedFromDate = Constants.DDMMYYYYFORMAT2.format(fromDate);
            paramMap.put("fromDate", formatedFromDate);
            paramMap.put("heading", "Deduction detailed report for "+ recovery.getType() +" From " + formatedFromDate + "  to " + formatedAsOndate);
            paramMap.put("summaryheading", "Deductions remittance summary for "+ recovery.getType() +" From " + formatedFromDate + "  to " + formatedAsOndate);
            paramMap.put("fromDateText", "From Date :      " + formatedFromDate);
        } else{
            paramMap.put("heading", "Deduction detailed report for "+ recovery.getType() +" as on " + formatedAsOndate);
            paramMap.put("summaryheading", "Deductions remittance summary for "+ recovery.getType() +" as on " + formatedAsOndate);
        }
        fund = (Fund) persistenceService.find("from Fund where id=?", fund.getId());
        paramMap.put("fundName", fund.getName());
        paramMap.put("partyName", partyName);
        if (department.getCode() != null && !department.getCode().equals("-1") ) {
          //TO-DO Get department from MS
            department = this.microserviceUtils.getDepartmentByCode(department.getCode());
//            department = (Department) persistenceService.find("from Department where id=?", department.getId());
            paramMap.put("departmentName", department.getName());
        }
        recovery = (Recovery) persistenceService.find("from Recovery where id=?", recovery.getId());
        paramMap.put("recoveryName", recovery.getRecoveryName());
        String ulbName = microserviceUtils.getHeaderNameForTenant().toUpperCase();
        paramMap.put("ulbName", environment.getProperty(ulbName,ulbName));
        return paramMap;
    }

    @ReadOnly
    private void populateData() {
        validateFinYear();
        if (getFieldErrors().size() > 0)
            return;
        recovery = (Recovery) persistenceService.find("from Recovery where id=?", recovery.getId());
        type = recovery.getType();
        String deptQuery = "";
        String partyNameQuery = "";
        final RemittanceBean remittanceBean = new RemittanceBean();
        remittanceBean.setRecoveryId(recovery.getId());
        
        if (fromDate != null)
            remittanceBean.setFromDate(Constants.DDMMYYYYFORMAT1.format(fromDate));
        final StringBuffer query1 = new StringBuffer(1000);
        List<EgRemittanceDetail> result1 = new ArrayList<EgRemittanceDetail>();
        if(remitRecoveryService.isNonControlledCodeTds(remittanceBean)){
            if (department.getCode() != null && !department.getCode().equals("-1") )//TO-DO change departmentid.id to departmentcode and get department code from UI and pass
                deptQuery = " and egRemittanceGl.glid.voucherHeaderId.vouchermis.departmentcode='"
                        + department.getCode()+"'";
            pendingTDS = remitRecoveryService.getRecoveryDetailsForNonControlledCode(remittanceBean, getVoucherHeader());
            query1.append("from EgRemittanceDetail where  egRemittanceGl.glid.glcodeId.id=? ")
                  .append("and egRemittance.fund.id=? and egRemittance.voucherheader.status = 5 and egRemittanceGl.glid.voucherHeaderId.status=0 and ")
                  .append("egRemittanceGl.glid.voucherHeaderId.voucherDate <= ? ");
            if (fromDate != null){
                query1.append(" and egRemittanceGl.glid.voucherHeaderId.voucherDate >= ?");
            }
            query1.append(" order by egRemittanceGl.glid.voucherHeaderId.voucherNumber ");
            if (fromDate != null){
                result1 = persistenceService.findAllBy(query1.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                        asOnDate, fromDate);
            }else{
                result1 = persistenceService.findAllBy(query1.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                        asOnDate);
            }
            Boolean createPartialRow1 = false;
            for (final EgRemittanceDetail entry : result1) {
                createPartialRow1 = false;
                for (final TDSEntry tdsExists : inWorkflowTDS)
                    if (tdsExists.getEgRemittanceGlId().intValue() == entry.getEgRemittanceGl().getId().intValue())
                        createPartialRow1 = true;
                TDSEntry tds = new TDSEntry();
                tds.setEgRemittanceGlId(entry.getEgRemittanceGl().getId());
                if (!createPartialRow1)
                    tds = createTdsForNonControlledTds(entry);
                tds.setRemittedOn(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittance().getVoucherheader().getVoucherDate()));
                tds.setAmount(entry.getRemittedamt());
                if (entry.getEgRemittance().getVoucherheader() != null)
                    tds.setPaymentVoucherNumber(entry.getEgRemittance().getVoucherheader().getVoucherNumber());
                final List<InstrumentVoucher> ivList = persistenceService.findAllBy("from InstrumentVoucher where" +
                        " instrumentHeaderId.statusId.description in(?,?,?) and voucherHeaderId=?"
                        , FinancialConstants.INSTRUMENT_DEPOSITED_STATUS, FinancialConstants.INSTRUMENT_CREATED_STATUS,
                        FinancialConstants.INSTRUMENT_RECONCILED_STATUS, entry.getEgRemittance().getVoucherheader());
                boolean isMultiple = false;
                for (final InstrumentVoucher iv : ivList)
                {
                    if (entry.getRemittedamt().compareTo(iv.getInstrumentHeaderId().getInstrumentAmount()) != 0)
                        isMultiple = true;
                    
                    tds.setChequeNumber(iv.getInstrumentHeaderId().getInstrumentNumber());
                    if (isMultiple)
                        tds.setChequeNumber(tds.getChequeNumber() + "-MULTIPLE");
                    tds.setChequeAmount(iv.getInstrumentHeaderId().getInstrumentAmount());
                    if (iv.getInstrumentHeaderId().getInstrumentDate() != null)
                        tds.setDrawnOn(Constants.DDMMYYYYFORMAT2.format(iv.getInstrumentHeaderId().getInstrumentDate()));
                }
                inWorkflowTDS.add(tds);
            }
            if (showRemittedEntries) {
                if (department.getCode() != null && !department.getCode().equals("-1") )//TO-DO change departmentid.id to departmentcode and get department code from UI and pass
                    deptQuery = " and egRemittanceGl.glid.voucherHeaderId.vouchermis.departmentcode='"
                            + department.getCode()+"'";
                final StringBuffer query = new StringBuffer(1000);
                query.append("from EgRemittanceDetail where  egRemittanceGl.glid.glcodeId.id=? ")
                     .append("and egRemittance.fund.id=? and egRemittance.voucherheader.status = 0 and egRemittanceGl.glid.voucherHeaderId.status=0 and ")
                     .append("egRemittanceGl.glid.voucherHeaderId.voucherDate <= ? ");
                
                List<EgRemittanceDetail> result = new ArrayList<EgRemittanceDetail>();
                if (fromDate != null)
                    query.append(" and egRemittanceGl.glid.voucherHeaderId.voucherDate >= ?");
                query.append(deptQuery);
                query.append(" order by egRemittanceGl.glid.voucherHeaderId.voucherNumber ");
                if (fromDate != null)
                    result = persistenceService.findAllBy(query.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                            asOnDate, fromDate);
                else
                    result = persistenceService.findAllBy(query.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                            asOnDate);
                
                Boolean createPartialRow = false;
                for (final EgRemittanceDetail entry : result) {
                    createPartialRow = false;
                    for (final TDSEntry tdsExists : remittedTDS)
                        if (tdsExists.getEgRemittanceGlId().intValue() == entry.getEgRemittanceGl().getId().intValue())
                            createPartialRow = true;
                    TDSEntry tds = new TDSEntry();
                    tds.setEgRemittanceGlId(entry.getEgRemittanceGl().getId());
                    if (!createPartialRow)
                        tds = createTdsForNonControlledTds(entry);
                    tds.setRemittedOn(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittance().getVoucherheader().getVoucherDate()));
                    tds.setAmount(entry.getRemittedamt());
                    if (entry.getEgRemittance().getVoucherheader() != null)
                        tds.setPaymentVoucherNumber(entry.getEgRemittance().getVoucherheader().getVoucherNumber());
                    final List<InstrumentVoucher> ivList = persistenceService.findAllBy("from InstrumentVoucher where" +
                            " instrumentHeaderId.statusId.description in(?,?,?) and voucherHeaderId=?"
                            , FinancialConstants.INSTRUMENT_DEPOSITED_STATUS, FinancialConstants.INSTRUMENT_CREATED_STATUS,
                            FinancialConstants.INSTRUMENT_RECONCILED_STATUS, entry.getEgRemittance().getVoucherheader());
                    boolean isMultiple = false;
                    for (final InstrumentVoucher iv : ivList)
                    {
                        if (entry.getRemittedamt().compareTo(iv.getInstrumentHeaderId().getInstrumentAmount()) != 0)
                            isMultiple = true;
                        
                        tds.setChequeNumber(iv.getInstrumentHeaderId().getInstrumentNumber());
                        if (isMultiple)
                            tds.setChequeNumber(tds.getChequeNumber() + "-MULTIPLE");
                        tds.setChequeAmount(iv.getInstrumentHeaderId().getInstrumentAmount());
                        if (iv.getInstrumentHeaderId().getInstrumentDate() != null)
                            tds.setDrawnOn(Constants.DDMMYYYYFORMAT2.format(iv.getInstrumentHeaderId().getInstrumentDate()));
                    }
                    remittedTDS.add(tds);
                }
                
            }
        }else{
            if (department.getCode() != null && !department.getCode().equals("-1") )//TO-DO change departmentid.id to departmentcode and get department code from UI and pass
                deptQuery = " and egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.vouchermis.departmentcode='"
                        + department.getCode()+"'";
            if (detailKey != null && detailKey != -1)
                partyNameQuery = " and egRemittanceGldtl.generalledgerdetail.detailKeyId=" + detailKey;
            pendingTDS = remitRecoveryService.getRecoveryDetailsForReport(remittanceBean, getVoucherHeader(), detailKey);
            query1.append("from EgRemittanceDetail where  egRemittanceGldtl.generalledgerdetail.generalLedgerId.glcodeId.id=? "
                    +
                    "and egRemittance.fund.id=? and egRemittance.voucherheader.status = 5 and egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.status=0 and "
                    +
                    "egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherDate <= ? ");
            if (fromDate != null)
                query1.append(" and egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherDate >= ?");
            query1.append(deptQuery).append(partyNameQuery);
            query1.append(" order by egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherNumber ");
            if (fromDate != null)
                result1 = persistenceService.findAllBy(query1.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                        asOnDate, fromDate);
            else
                result1 = persistenceService.findAllBy(query1.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                        asOnDate);
            Boolean createPartialRow1 = false;
            for (final EgRemittanceDetail entry : result1) {
                createPartialRow1 = false;
                for (final TDSEntry tdsExists : inWorkflowTDS)
                    if (tdsExists.getEgRemittanceGlDtlId().intValue() == entry.getEgRemittanceGldtl().getId().intValue())
                        createPartialRow1 = true;
                TDSEntry tds = new TDSEntry();
                tds.setEgRemittanceGlDtlId(entry.getEgRemittanceGldtl().getId());
                if (!createPartialRow1)
                    tds = createTds(entry);
                tds.setRemittedOn(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittance().getVoucherheader().getVoucherDate()));
                tds.setAmount(entry.getRemittedamt());
                if (entry.getEgRemittance().getVoucherheader() != null)
                    tds.setPaymentVoucherNumber(entry.getEgRemittance().getVoucherheader().getVoucherNumber());
                final List<InstrumentVoucher> ivList = persistenceService.findAllBy("from InstrumentVoucher where" +
                        " instrumentHeaderId.statusId.description in(?,?,?) and voucherHeaderId=?"
                        , FinancialConstants.INSTRUMENT_DEPOSITED_STATUS, FinancialConstants.INSTRUMENT_CREATED_STATUS,
                        FinancialConstants.INSTRUMENT_RECONCILED_STATUS, entry.getEgRemittance().getVoucherheader());
                boolean isMultiple = false;
                for (final InstrumentVoucher iv : ivList)
                {
                    if (entry.getRemittedamt().compareTo(iv.getInstrumentHeaderId().getInstrumentAmount()) != 0)
                        isMultiple = true;
                    
                    tds.setChequeNumber(iv.getInstrumentHeaderId().getInstrumentNumber());
                    if (isMultiple)
                        tds.setChequeNumber(tds.getChequeNumber() + "-MULTIPLE");
                    tds.setChequeAmount(iv.getInstrumentHeaderId().getInstrumentAmount());
                    if (iv.getInstrumentHeaderId().getInstrumentDate() != null)
                        tds.setDrawnOn(Constants.DDMMYYYYFORMAT2.format(iv.getInstrumentHeaderId().getInstrumentDate()));
                }
                inWorkflowTDS.add(tds);
            }
            if (showRemittedEntries) {
                if (department.getCode() != null && !department.getCode().equals("-1") )//TO-DO change departmentid.id to departmentcode and get department code from UI and pass
                    deptQuery = " and egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.vouchermis.departmentcode='"
                            + department.getCode()+"'";
                if (detailKey != null && detailKey != -1)
                    partyNameQuery = " and egRemittanceGldtl.generalledgerdetail.detailKeyId=" + detailKey;
                final StringBuffer query = new StringBuffer(1000);
                
                List<EgRemittanceDetail> result = new ArrayList<EgRemittanceDetail>();
                query.append("from EgRemittanceDetail where  egRemittanceGldtl.generalledgerdetail.generalLedgerId.glcodeId.id=? "
                        +
                        "and egRemittance.fund.id=? and egRemittance.voucherheader.status = 0 and egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.status=0 and "
                        +
                        "egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherDate <= ? ");
                if (fromDate != null)
                    query.append(" and egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherDate >= ?");
                query.append(deptQuery).append(partyNameQuery);
                query.append(" order by egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherNumber ");
                if (fromDate != null)
                    result = persistenceService.findAllBy(query.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                            asOnDate, fromDate);
                else
                    result = persistenceService.findAllBy(query.toString(), recovery.getChartofaccounts().getId(), fund.getId(),
                            asOnDate);
                
                Boolean createPartialRow = false;
                for (final EgRemittanceDetail entry : result) {
                    createPartialRow = false;
                    for (final TDSEntry tdsExists : remittedTDS)
                        if (tdsExists.getEgRemittanceGlDtlId().intValue() == entry.getEgRemittanceGldtl().getId().intValue())
                            createPartialRow = true;
                    TDSEntry tds = new TDSEntry();
                    tds.setEgRemittanceGlDtlId(entry.getEgRemittanceGldtl().getId());
                    if (!createPartialRow)
                        tds = createTds(entry);
                    tds.setRemittedOn(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittance().getVoucherheader().getVoucherDate()));
                    tds.setAmount(entry.getRemittedamt());
                    if (entry.getEgRemittance().getVoucherheader() != null)
                        tds.setPaymentVoucherNumber(entry.getEgRemittance().getVoucherheader().getVoucherNumber());
                    final List<InstrumentVoucher> ivList = persistenceService.findAllBy("from InstrumentVoucher where" +
                            " instrumentHeaderId.statusId.description in(?,?,?) and voucherHeaderId=?"
                            , FinancialConstants.INSTRUMENT_DEPOSITED_STATUS, FinancialConstants.INSTRUMENT_CREATED_STATUS,
                            FinancialConstants.INSTRUMENT_RECONCILED_STATUS, entry.getEgRemittance().getVoucherheader());
                    boolean isMultiple = false;
                    for (final InstrumentVoucher iv : ivList)
                    {
                        if (entry.getRemittedamt().compareTo(iv.getInstrumentHeaderId().getInstrumentAmount()) != 0)
                            isMultiple = true;
                        
                        tds.setChequeNumber(iv.getInstrumentHeaderId().getInstrumentNumber());
                        if (isMultiple)
                            tds.setChequeNumber(tds.getChequeNumber() + "-MULTIPLE");
                        tds.setChequeAmount(iv.getInstrumentHeaderId().getInstrumentAmount());
                        if (iv.getInstrumentHeaderId().getInstrumentDate() != null)
                            tds.setDrawnOn(Constants.DDMMYYYYFORMAT2.format(iv.getInstrumentHeaderId().getInstrumentDate()));
                    }
                    remittedTDS.add(tds);
                }
                
            }
        }
        org.egov.infra.admin.master.entity.Department dept =null;
        //start view implementation
        Map<String,String> partyNameMapAsView=new HashMap<String,String>();
        populatePartyViewName(partyNameMapAsView);
        Map<String,Contractor> contractorMap=new HashMap<String,Contractor>();
        populateContractorMap(contractorMap);
        Map<String,Supplier> supplierMap=new HashMap<String,Supplier>();
        populateSupplierMap(supplierMap);
        //end view implementation
        if(pendingTDS !=null && !pendingTDS.isEmpty())
        {
        	for(RemittanceBean row:pendingTDS)
            {
        		if(row.getDepartmentId() != null && !row.getDepartmentId().isEmpty())
        		{
        			dept = departmentService.getDepartmentById(Long.parseLong(row.getDepartmentId()));
        			row.setDeptName(dept.getName());
        		}
        		
            	if(row.getDetailKeyid() != null && row.getDetailKeyid() != 0 && row.getDetailTypeId() != null && row.getDetailTypeId() != 0)
            	{
            		if(partyNameMapAsView.get(String.valueOf(row.getDetailKeyid())+"-"+String.valueOf(row.getDetailTypeId())) != null)
            		{
            			row.setPartyName(partyNameMapAsView.get(String.valueOf(row.getDetailKeyid())+"-"+String.valueOf(row.getDetailTypeId())));
            		}
            		else
            		{
            			row.setPartyName("");
            		}
            		
            		
            		if(row.getDetailTypeId() == 11 && row.getPartyName() != null && !row.getPartyName().isEmpty())
            		{
            			 if(supplierMap.get(row.getPartyName()) != null)
            			 {
            				 row.setGstNo(supplierMap.get(row.getPartyName().trim()).getTinNumber());
            				 row.setPanNo(supplierMap.get(row.getPartyName().trim()).getPanNumber());
            			 }
            			
            		}
            		else if(row.getDetailTypeId() == 12 && row.getPartyName() != null && !row.getPartyName().isEmpty())
            		{
            			row.setGstNo(contractorMap.get(row.getPartyName().trim()).getTinNumber());
       				 	row.setPanNo(contractorMap.get(row.getPartyName().trim()).getPanNumber());
            		}
            	}
            }
        }
    }

    private void populateSupplierMap(Map<String, Supplier> supplierMap) {
    	SQLQuery query =  null;
    	List<Object[]> list = null;
    	Supplier supplier=null;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vw.name,vw.pannumber,tinnumber from supplier_active_view vw");
    	     list = query.list();
    	     if(list != null && !list.isEmpty())
 			{
 				for (final Object[] element : list) {
 					supplier=new Supplier();
 					if(element[1] != null && !(element[1].toString().isEmpty()))
 					{
 						supplier.setPanNumber(element[1].toString());
 					}
 					else
 					{
 						supplier.setPanNumber("");
 					}
 					if(element[2] != null && !(element[2].toString().isEmpty()))
 					{
 						supplier.setTinNumber(element[2].toString());
 					}
 					else
 					{
 						supplier.setTinNumber("");
 					}
 					supplierMap.put(element[0].toString(),supplier);
 				}
 				
 			}
    	}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void populateContractorMap(Map<String, Contractor> contractorMap) {
		SQLQuery query =  null;
    	List<Object[]> list = null;
    	Contractor contractor=null;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vw.name,vw.pannumber,tinnumber from contractor_active_view vw");
    	     list = query.list();
    	     if(list != null && !list.isEmpty())
 			{
 				for (final Object[] element : list) {
 					contractor=new Contractor();
 					if(element[1] != null && !(element[1].toString().isEmpty()))
 					{
 						contractor.setPanNumber(element[1].toString());
 					}
 					else
 					{
 						contractor.setPanNumber("");
 					}
 					if(element[2] != null && !(element[2].toString().isEmpty()))
 					{
 						contractor.setTinNumber(element[2].toString());
 					}
 					else
 					{
 						contractor.setTinNumber("");
 					}
 					contractorMap.put(element[0].toString(),contractor);
 				}
 				
 			}
    	}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void populatePartyViewName(Map<String, String> partyNameMapAsView) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String partyName="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select comp,detailname from recovery_report_party");
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		partyNameMapAsView.put(element[0].toString(),element[1].toString());
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private List<Object[]> getSupplier(String suppName) {
    	SQLQuery query =  null;
    	List<Object[]> list = null;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select pannumber,tinnumber from egf_supplier where name=:suppName");
    	    query.setString("suppName", suppName);
    	     list = query.list();
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return list;
	}
    
    private List<Object[]> getContractor(String contrName) {
    	SQLQuery query =  null;
    	List<Object[]> list = null;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select pannumber,tinnumber from egf_contractor where name=:contrName");
    	    query.setString("contrName", contrName);
    	     list = query.list();
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return list;
	}

	private String getParty(Integer dtlKey, Integer detailType) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String partyName="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select acc.detailname,acc.detailtypeid from accountdetailkey acc where acc.detailkey=:detailKey and acc.detailtypeid=:detailType");
    	    query.setInteger("detailKey", dtlKey);
    	    query.setInteger("detailType", detailType);
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	System.out.println("list :"+rows.get(0));
    	    	for(Object[] element : rows)
    	    	{
    	    		partyName= element[0].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return partyName;
    }

    private TDSEntry createTdsForNonControlledTds(EgRemittanceDetail entry) {
        final TDSEntry tds = new TDSEntry();
        tds.setEgRemittanceGlId(entry.getEgRemittanceGl().getId());
        tds.setNatureOfDeduction(entry.getGeneralLedger().getVoucherHeaderId().getName());
        tds.setVoucherNumber(entry.getGeneralLedger().getVoucherHeaderId().getVoucherNumber());
        tds.setVoucherDate(Constants.DDMMYYYYFORMAT2.format(entry.getGeneralLedger().getVoucherHeaderId().getVoucherDate()));
        tds.setAmount(entry.getEgRemittanceGl().getGlamt());
        return tds;
    }

    /**
     * show only pending TDSes
     */
    @ReadOnly
    private void populateSummaryData() {
        recovery = (Recovery) persistenceService.find("from Recovery where id=?", recovery.getId());
        type = recovery.getType();
        String deptQuery = "";
        String partyNameQuery = "";
        if (department.getCode() != null && !department.getCode().equals("-1"))
            deptQuery = " and mis.departmentcode='" + department.getCode()+"'";
        if (detailKey != null && detailKey != -1)
            partyNameQuery = " and gld.detailkeyid=" + detailKey;
        List<Object[]> result = new ArrayList<Object[]>();
        List<Object[]> resultTolDeduction = new ArrayList<Object[]>();
        try {
            RemittanceBean remittanceBean = new RemittanceBean();
            remittanceBean.setRecoveryId(recovery.getId());
            if(remitRecoveryService.isNonControlledCodeTds(remittanceBean)){
                final String qry = "select vh.name,erd.remittedamt,er.month ,erd.assign_number "
                        + "FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"
                        + "voucherheader vh,vouchermis mis,generalledger gl,fund f, eg_remittance_gl ergl WHERE erd.remittanceglid = ergl.id AND "
                        + "erd.remittanceid=er.id  AND gl.glcodeid ="+recovery.getChartofaccounts().getId()+" AND vh.id =mis.voucherheaderid AND vh1.status =0  AND "
                        + "gl.id = ergl.glid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id AND f.id = "+fund.getId()+" AND vh.status =0 AND "
                        + "vh.voucherDate <= to_date('"+Constants.DDMMYYYYFORMAT2.format(asOnDate)+"','dd/MM/yyyy') and  "
                        + "vh.voucherDate >= to_date('"+Constants.DDMMYYYYFORMAT2.format(financialYearDAO.getFinancialYearByDate(asOnDate).getStartingDate())+"','dd/MM/yyyy') "+ deptQuery
                        + " order by er.month,vh.name";
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(qry);
                result = persistenceService.getSession().createSQLQuery(qry).list();
                
             // Query to get total deduction
                final String qryTolDeduction = "SELECT type,MONTH,glamt FROM (SELECT DISTINCT er.month AS MONTH,ergl.glamt AS glamt,ergl.glid as glid,vh.name AS type "
                        + "FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"
                        + "voucherheader vh,vouchermis mis,generalledger gl,fund f, eg_remittance_gl ergl WHERE erd.remittanceglid = ergl.id AND "
                        + "erd.remittanceid=er.id  AND gl.glcodeid = "+recovery.getChartofaccounts().getId()+" AND vh.id =mis.voucherheaderid AND vh1.status =0  AND "
                        + "gl.id = ergl.glid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id AND f.id = "+fund.getId()+" AND vh.status =0 AND "
                        + "vh.voucherDate <= to_date('"+Constants.DDMMYYYYFORMAT2.format(asOnDate)+"','dd/MM/yyyy') and  "
                        + "vh.voucherDate >= to_date('"+Constants.DDMMYYYYFORMAT2.format(financialYearDAO.getFinancialYearByDate(asOnDate).getStartingDate())+"','dd/MM/yyyy')) "
                        + "as temptable order by type,month";
                resultTolDeduction = persistenceService.getSession().createSQLQuery(qryTolDeduction).list();
            }else{
                final String qry = "select vh.name,erd.remittedamt,er.month,erd.assign_number from eg_remittance_detail erd,"
                        +
                        " voucherheader vh1 right outer join eg_remittance er on vh1.id=er.paymentvhid,voucherheader vh,vouchermis mis,generalledger gl,generalledgerdetail gld,fund f,eg_remittance_gldtl ergl where "
                        +
                        " erd.remittancegldtlid= ergl.id and erd.remittanceid=er.id and gl.glcodeid="
                        + recovery.getChartofaccounts().getId()
                        + " and vh.id=mis.voucherheaderid and "
                        +
                        "  vh1.status=0 and ergl.gldtlid=gld.id and gl.id=gld.generalledgerid and gl.voucherheaderid=vh.id and er.fundid=f.id and f.id="
                        + fund.getId() +
                        " and vh.status=0 and vh.voucherDate <= to_date('" + Constants.DDMMYYYYFORMAT2.format(asOnDate)
                        + "','dd/MM/yyyy') and " + "vh.voucherDate >= to_date('"
                        + Constants.DDMMYYYYFORMAT2.format(financialYearDAO.getFinancialYearByDate(asOnDate).getStartingDate())
                        + "','dd/MM/yyyy') " + deptQuery + partyNameQuery + "  order by er.month,vh.name";
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(qry);
                result = persistenceService.getSession().createSQLQuery(qry).list();
                // Query to get total deduction
                final String qryTolDeduction = "SELECT type,MONTH,gldtamt FROM (SELECT DISTINCT er.month AS MONTH,ergl.gldtlamt AS gldtamt,"
                        +
                        "ergl.gldtlid as gldtlid,vh.name AS type FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"
                        +
                        "voucherheader vh,vouchermis mis,generalledger gl,generalledgerdetail gld,fund f, eg_remittance_gldtl ergl WHERE erd.remittancegldtlid= ergl.id"
                        +
                        " AND erd.remittanceid=er.id  AND gl.glcodeid ="
                        + recovery.getChartofaccounts().getId()
                        + " AND vh.id =mis.voucherheaderid AND vh1.status =0 "
                        +
                        " AND ergl.gldtlid =gld.id  AND gl.id = gld.generalledgerid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id"
                        +
                        " AND f.id ="
                        + fund.getId()
                        + " AND vh.status =0 AND vh.voucherDate <= to_date('"
                        + Constants.DDMMYYYYFORMAT2.format(asOnDate)
                        + "','dd/MM/yyyy') and "
                        + " vh.voucherDate >= to_date('"
                        + Constants.DDMMYYYYFORMAT2.format(financialYearDAO.getFinancialYearByDate(asOnDate).getStartingDate())
                        + "','dd/MM/yyyy') " + deptQuery + partyNameQuery + ") as temptable order by type,month";
                resultTolDeduction = persistenceService.getSession().createSQLQuery(qryTolDeduction).list();
            }
        } catch (final ApplicationRuntimeException e) {
            message = e.getMessage();
            return;
        } catch (final Exception e) {
            message = e.getMessage();
            return;
        }
        for (final Object[] entry : result)
            for (final Object[] dedentry : resultTolDeduction) {
                final TDSEntry tds = new TDSEntry();
                final String monthChk = DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(entry[2].toString()) + 1);
                if (monthChk.equalsIgnoreCase(DateUtils.getAllMonthsWithFullNames().get(
                        Integer.valueOf(dedentry[1].toString()) + 1))
                        && dedentry[0].toString().equalsIgnoreCase(entry[0].toString()) && dedentry[2].toString().equalsIgnoreCase(entry[1].toString())) {
                    tds.setNatureOfDeduction(entry[0].toString());
                    tds.setTotalRemitted(new BigDecimal(entry[1].toString()));
                    tds.setMonth(DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(entry[2].toString()) + 1));
                    if(entry[3] != null)
                    {
                    	tds.setAutoAssignNumber(entry[3].toString());
                    }
                    else
                    {
                    	tds.setAutoAssignNumber(null);
                    }
                    final BigDecimal totDeduction = new BigDecimal(dedentry[2].toString());
                    tds.setTotalDeduction(totDeduction);
                    remittedTDS.add(tds);
                }
            }
    }

    private CVoucherHeader getVoucherHeader() {
        final CVoucherHeader voucherHeader = new CVoucherHeader();
        voucherHeader.setFundId(fund);
        final Vouchermis vouchermis = new Vouchermis();
        voucherHeader.setVouchermis(vouchermis);
        voucherHeader.getVouchermis().setDepartmentcode(department.getCode());
        voucherHeader.setVoucherDate(asOnDate);
        return voucherHeader;
    }

    @Action(value = "/report/pendingTDSReport-ajaxLoadEntites")
    public String ajaxLoadEntites() throws ClassNotFoundException {
        if (parameters.containsKey("recoveryId") && parameters.get("recoveryId")[0] != null
                && !"".equals(parameters.get("recoveryId")[0])) {
            recovery = (Recovery) persistenceService.find("from Recovery where id=?",
                    Long.valueOf(parameters.get("recoveryId")[0]));
            for (final CChartOfAccountDetail detail : recovery.getChartofaccounts().getChartOfAccountDetails())
                entitiesList.addAll(egovCommon.loadEntitesFor(detail.getDetailTypeId()));
        }
        return "entities";
    }

    private TDSEntry createTds(final EgRemittanceDetail entry) {
        final TDSEntry tds = new TDSEntry();
        if (entry.getEgRemittanceGldtl().getRecovery() != null)
            tds.setPartyCode(entry.getEgRemittanceGldtl().getRecovery().getEgPartytype().getCode());
        tds.setEgRemittanceGlDtlId(entry.getEgRemittanceGldtl().getId());
        tds.setNatureOfDeduction(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralLedgerId().getVoucherHeaderId()
                .getName());
        tds.setVoucherNumber(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralLedgerId().getVoucherHeaderId()
                .getVoucherNumber());
        tds.setVoucherDate(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittanceGldtl().getGeneralledgerdetail()
                .getGeneralLedgerId().getVoucherHeaderId().getVoucherDate()));
        final EntityType entityType = getEntity(entry);
        if (entityType != null) {
            tds.setPartyName(entityType.getName());
            tds.setPartyCode(entityType.getCode());
            tds.setPanNo(entityType.getPanno());
        }
        tds.setAmount(entry.getEgRemittanceGldtl().getGldtlamt());
        return tds;
    }

    private EntityType getEntity(final EgRemittanceDetail entry) {
        egovCommon.setPersistenceService(persistenceService);
        final Integer detailKeyId = entry.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailKeyId().intValue();
        EntityType entityType = null;
        try {
            entityType = egovCommon.getEntityType(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailTypeId(),
                    detailKeyId);
        } catch (final ApplicationException e) {

        }
        return entityType;
    }

    @Action(value = "/report/pendingTDSReport-exportXls")
    public String exportXls() throws JRException, IOException {
        populateData();
        final ReportRequest reportInput = new ReportRequest(jasperpath, pendingTDS, getParamMap());
        reportInput.setReportFormat(ReportFormat.XLS);
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
        return "XLS";
    }

    @Action(value = "/report/pendingTDSReport-exportDeductionXls")
    public String exportDeductionXls() throws JRException, IOException {
        populateData();
        //view
        Map<String,VoucherDetailMain> voucherDetailMainMapping=new HashMap<String,VoucherDetailMain>();
        Map<Long,List<VoucherDetailMiscMapping>> voucherDetailMiscMapping=new HashMap<Long,List<VoucherDetailMiscMapping>>();
        Map<Long,CVoucherHeader> voucherDetailBpvMapping=new HashMap<Long,CVoucherHeader>();
        Map<Long,CVoucherHeader> voucherDetailIntrumentMapping=new HashMap<Long,CVoucherHeader>();
        populateVoucherDetailMain(voucherDetailMainMapping);
        populateMisMapping(voucherDetailMiscMapping);
        populateBpvMapping(voucherDetailBpvMapping);
        populateDetailInstrument(voucherDetailIntrumentMapping);
        List<DeductionReportBean> reportList=new ArrayList<DeductionReportBean>();
        DeductionReportBean bean=null;
        int i=1;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        populateHeadingAndNote(paramMap);
        if(pendingTDS != null && !pendingTDS.isEmpty())
        {
        	org.egov.infra.admin.master.entity.Department dept =null;
        	for(RemittanceBean row:pendingTDS)
            {
        		dept = departmentService.getDepartmentById(Long.parseLong(row.getDepartmentId()));
        		bean =new DeductionReportBean();
        		bean.setSlNo(i++);
        		bean.setDivision(dept.getName());
        		bean.setRecoveryCode(recovery.getRecoveryName());
        		bean.setVoucherNo(row.getVoucherNumber());
        		bean.setNameOfAgency(row.getPartyName());
        		bean.setAmount(row.getAmount());
        		bean.setGstNoOfAgency(row.getGstNo());
        		bean.setPanNoOfAgency(row.getPanNumber());
        		if(row.getVoucherNumber() != null && voucherDetailMainMapping.get(row.getVoucherNumber()) != null)
        		{
        			bean.setWorkDone(voucherDetailMainMapping.get(row.getVoucherNumber()).getDescription());
        			if(voucherDetailMiscMapping.get(voucherDetailMainMapping.get(row.getVoucherNumber()).getId()) != null)
        			{
        				bean.setBillVoucherNo(voucherDetailBpvMapping.get((voucherDetailMiscMapping.get(voucherDetailMainMapping.get(row.getVoucherNumber()).getId())).get(0).getBpvId()).getVoucherNumber());
        				if(voucherDetailIntrumentMapping.get((voucherDetailMiscMapping.get(voucherDetailMainMapping.get(row.getVoucherNumber()).getId())).get(0).getBpvId()) != null)
        				{
        					bean.setPexNo((voucherDetailIntrumentMapping.get((voucherDetailMiscMapping.get(voucherDetailMainMapping.get(row.getVoucherNumber()).getId())).get(0).getBpvId())).getVoucherNumber());
        				}
        			}
        		}
        		reportList.add(bean);
            }
        }
        System.out.println("report list size ::: "+reportList.size());
		try {
			byte[] fileContent=populateExcel(reportList,paramMap);
			inputStream = new ByteArrayInputStream(fileContent);
				            
        }catch (Exception e) {
        	System.out.println("ERROR2 ::: "+e.getMessage());
			e.printStackTrace();
		}
        System.out.println("END");
        return "deductionXLS";
    }

    private void populateDetailInstrument(Map<Long, CVoucherHeader> voucherDetailIntrumentMapping) {
    	SQLQuery queryInstru =  null;
       	final StringBuffer query5 = new StringBuffer(500);
       	List<Object[]> list= null;
      	query5
          .append("select vdi.id,vdi.transactionnumber, vdi.transactiondate ,vdi.voucherheaderid,vdi.accountnumber from voucher_detail_instrument vdi   ");
      	LOGGER.info("Query 5 :: "+query5.toString());
      	queryInstru=this.persistenceService.getSession().createSQLQuery(query5.toString());
       	list = queryInstru.list();
      	LOGGER.info("pex map");
      	CVoucherHeader pexDetail=null;
      	
      	if (list.size() != 0) {
      		LOGGER.info("size ::: "+list.size());
      		for (final Object[] object : list)
      		{
      			pexDetail=new CVoucherHeader();
      			if(object[3] != null)
      			{
      				pexDetail.setId(Long.parseLong(object[3].toString()));
      			}
      			if(object[1] != null)
      			{
      				pexDetail.setVoucherNumber(object[1].toString());
      			}
      			if(object[2] != null)
      			{
      				pexDetail.setApprovalComent(object[2].toString());
      			}
      			if(object[4] != null)
      			{
      				pexDetail.setCgvn(object[4].toString());
      			}
      			voucherDetailIntrumentMapping.put(pexDetail.getId(), pexDetail);
      		}
      		
      	}
		
	}

	private void populateBpvMapping(Map<Long, CVoucherHeader> voucherDetailBpvMapping) {
		
    	CVoucherHeader bpvMapping =null;
    	SQLQuery queryBpv =  null;
    	final StringBuffer query2 = new StringBuffer(500);
    	List<Object[]> list= null;
    	query2
        .append("select vdb.id,vdb.vouchernumber,vdb.voucherdate from voucher_detail_bpvmapping vdb");
    	LOGGER.info("Query 2 :: "+query2.toString());
    	queryBpv=this.persistenceService.getSession().createSQLQuery(query2.toString());
    	list = queryBpv.list();
    	LOGGER.info("after exe");
    	if (list.size() != 0) {
    		LOGGER.info("size ::: "+list.size());
    		for (final Object[] object : list)
    		{
    			bpvMapping=new CVoucherHeader();
    			bpvMapping.setId(Long.parseLong(object[0].toString()));
    			bpvMapping.setVoucherNumber(object[1].toString());
    			bpvMapping.setPartyBillNumber(object[2].toString());
    			voucherDetailBpvMapping.put(bpvMapping.getId(), bpvMapping);
    		}
    	}
		
	}

	private void populateMisMapping(Map<Long, List<VoucherDetailMiscMapping>> voucherDetailMiscMapping) {
		
    	SQLQuery queryMisc =  null;
    	final StringBuffer query3 = new StringBuffer(500);
    	List<Object[]> list= null;
   	query3
       .append("select vdm.id,vdm.billvhid,vdm.payvhid,vdm.paidamount from voucher_detail_miscbill vdm ");
   	LOGGER.info("Query 3 :: "+query3.toString());
   	queryMisc=this.persistenceService.getSession().createSQLQuery(query3.toString());
   	list = queryMisc.list();
   	LOGGER.info("1 map");
   	VoucherDetailMiscMapping voucherDetailMisc=null;
   	List<VoucherDetailMiscMapping> miscbillList=null;
   	if (list.size() != 0) {
   		LOGGER.info("size ::: "+list.size());
   		for (final Object[] object : list)
   		{
   			voucherDetailMisc=new VoucherDetailMiscMapping();
   			voucherDetailMisc.setId(Long.parseLong(object[0].toString()));
   			if(object[1] != null)
   			{
   				voucherDetailMisc.setVoucherId(Long.parseLong(object[1].toString()));
   			}
   			if(object[2] != null)
   			{
   				voucherDetailMisc.setBpvId(Long.parseLong(object[2].toString()));
   			}
   			if(object[3] != null )
   			{
   				voucherDetailMisc.setAmountPaid(new BigDecimal(object[3].toString()));
   			}
   			if(voucherDetailMiscMapping.get(voucherDetailMisc.getVoucherId()) == null)
   			{
   				miscbillList=new ArrayList<VoucherDetailMiscMapping>();
   				miscbillList.add(voucherDetailMisc);
   				voucherDetailMiscMapping.put(voucherDetailMisc.getVoucherId(), miscbillList);
   			}
   			else
   			{
   				voucherDetailMiscMapping.get(voucherDetailMisc.getVoucherId()).add(voucherDetailMisc);
   			}
   		}
   		
   	}
		
	}

	private void populateVoucherDetailMain(Map<String, VoucherDetailMain> voucherDetailMainMapping) {
    	SQLQuery queryMain =  null;
    	final StringBuffer query1 = new StringBuffer(500);
    	
    	List<Object[]> list= null;
    	query1
        .append("select vdm.voucherid ,vdm.vouchernumber ,vdm.status,vdm.head,vdm.department,vdm.voucherdate,vdm.scheme,vdm.description from voucher_detail_main vdm ");
    	LOGGER.info("Query 1 :: "+query1.toString());
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list = queryMain.list();
    	LOGGER.info("after execution");
    	VoucherDetailMain voucherDetailMain=null;
    	//voucher detail main mapping
    	if (list.size() != 0) {
    		LOGGER.info("size ::: "+list.size());
    		for (final Object[] object : list)
    		{
    			voucherDetailMain=new VoucherDetailMain();
    			voucherDetailMain.setId(Long.parseLong(object[0].toString()));
    			voucherDetailMain.setVoucherNumber(object[1].toString());
    			voucherDetailMain.setStatus(object[2].toString());
    			if(object[3] != null)
    			{
    				voucherDetailMain.setHead(object[3].toString());
    			}
    			voucherDetailMain.setDepartment(object[4].toString());
    			voucherDetailMain.setVoucherDate(object[5].toString());
    			if(object[6] != null)
    			{
    				voucherDetailMain.setScheme(object[6].toString());
    			}
    			if(object[7] != null)
    			{
    				voucherDetailMain.setDescription(object[7].toString());
    			}
    			voucherDetailMainMapping.put(voucherDetailMain.getVoucherNumber(), voucherDetailMain);
    		}
    	}
		
	}

	private byte[] populateExcel(List<DeductionReportBean> reportList, Map<String, Object> paramMap) throws IOException {
    	String jasper=(String)paramMap.get("jasper");
    	String heading=(String)paramMap.get("header");
    	String note=(String)paramMap.get("note");
    	
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(jasper);
		HSSFRow row = sheet.createRow(1);
		HSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue(heading);
		HSSFRow rowhead = sheet.createRow(5);
		if(jasper.equalsIgnoreCase("LaborCess"))
		{
			rowhead.createCell(0).setCellValue("Sl No.");  
		    rowhead.createCell(1).setCellValue("Recovery Code");  
		    rowhead.createCell(2).setCellValue("Voucher No.");  
		    rowhead.createCell(3).setCellValue("Bill Voucher No.");  
		    rowhead.createCell(4).setCellValue("PEX Number");  
		    rowhead.createCell(5).setCellValue("Division");
		    rowhead.createCell(6).setCellValue("Name of Agency");
		    rowhead.createCell(7).setCellValue("Work Done");
		    rowhead.createCell(8).setCellValue("Amount");
		    BigDecimal total=new BigDecimal("0");
		    int index=1;
		    int rowCount=6;
		    HSSFRow details ;
		    for(DeductionReportBean bean : reportList)
		    {
		    	details=sheet.createRow(rowCount++);
		    	details.createCell(0).setCellValue(index++);  
		    	details.createCell(1).setCellValue(bean.getRecoveryCode());  
		    	details.createCell(2).setCellValue(bean.getVoucherNo());  
		    	details.createCell(3).setCellValue(bean.getBillVoucherNo());  
		    	details.createCell(4).setCellValue(bean.getPexNo());  
		    	details.createCell(5).setCellValue(bean.getDivision());
		    	details.createCell(6).setCellValue(bean.getNameOfAgency());
		    	details.createCell(7).setCellValue(bean.getWorkDone());
			    total=total.add(bean.getAmount());
			    details.createCell(8).setCellValue(bean.getAmount().doubleValue());
		    	
		    }
		    details=sheet.createRow(rowCount);
		    details.createCell(7).setCellValue("Total");
		    details.createCell(8).setCellValue(total.doubleValue());
		    details=sheet.createRow(rowCount+5);
		    details.createCell(0).setCellValue(note);
		}
		else if(jasper.equalsIgnoreCase("WaterCharges"))
		{
			rowhead.createCell(0).setCellValue("Sl No.");  
		    rowhead.createCell(1).setCellValue("Recovery Code");  
		    rowhead.createCell(2).setCellValue("Voucher No.");  
		    rowhead.createCell(3).setCellValue("Bill Voucher No.");  
		    rowhead.createCell(4).setCellValue("PEX Number");  
		    rowhead.createCell(5).setCellValue("Division");
		    rowhead.createCell(6).setCellValue("Name of Agency");
		    rowhead.createCell(7).setCellValue("Work Done");
		    rowhead.createCell(8).setCellValue("Amount");
		    BigDecimal total=new BigDecimal("0");
		    int index=1;
		    int rowCount=6;
		    HSSFRow details ;
		    for(DeductionReportBean bean : reportList)
		    {
		    	details=sheet.createRow(rowCount++);
		    	details.createCell(0).setCellValue(index++);  
		    	details.createCell(1).setCellValue(bean.getRecoveryCode());  
		    	details.createCell(2).setCellValue(bean.getVoucherNo());  
		    	details.createCell(3).setCellValue(bean.getBillVoucherNo());  
		    	details.createCell(4).setCellValue(bean.getPexNo());  
		    	details.createCell(5).setCellValue(bean.getDivision());
		    	details.createCell(6).setCellValue(bean.getNameOfAgency());
		    	details.createCell(7).setCellValue(bean.getWorkDone());
			    total=total.add(bean.getAmount());
			    details.createCell(8).setCellValue(bean.getAmount().doubleValue());
		    	
		    }
		    details=sheet.createRow(rowCount);
		    details.createCell(7).setCellValue("Total");
		    details.createCell(8).setCellValue(total.doubleValue());
		    details=sheet.createRow(rowCount+5);
		    details.createCell(0).setCellValue(note);
		}
		else if(jasper.equalsIgnoreCase("TDSOnGST"))
		{
			rowhead.createCell(0).setCellValue("Sl No.");  
		    rowhead.createCell(1).setCellValue("Recovery Code");  
		    rowhead.createCell(2).setCellValue("Voucher No.");  
		    rowhead.createCell(3).setCellValue("Bill Voucher No.");  
		    rowhead.createCell(4).setCellValue("PEX Number");  
		    rowhead.createCell(5).setCellValue("Division");
		    rowhead.createCell(6).setCellValue("Name of Agency");
		    rowhead.createCell(7).setCellValue("GST no. of Agency");
		    rowhead.createCell(8).setCellValue("Work Done");
		    rowhead.createCell(9).setCellValue("Amount");
		    BigDecimal total=new BigDecimal("0");
		    int index=1;
		    int rowCount=6;
		    HSSFRow details ;
		    for(DeductionReportBean bean : reportList)
		    {
		    	details=sheet.createRow(rowCount++);
		    	details.createCell(0).setCellValue(index++);  
		    	details.createCell(1).setCellValue(bean.getRecoveryCode());  
		    	details.createCell(2).setCellValue(bean.getVoucherNo());  
		    	details.createCell(3).setCellValue(bean.getBillVoucherNo());  
		    	details.createCell(4).setCellValue(bean.getPexNo());  
		    	details.createCell(5).setCellValue(bean.getDivision());
		    	details.createCell(6).setCellValue(bean.getNameOfAgency());
		    	details.createCell(7).setCellValue(bean.getGstNoOfAgency());
		    	details.createCell(8).setCellValue(bean.getWorkDone());
			    total=total.add(bean.getAmount());
			    details.createCell(9).setCellValue(bean.getAmount().doubleValue());
		    	
		    }
		    details=sheet.createRow(rowCount);
		    details.createCell(8).setCellValue("Total");
		    details.createCell(9).setCellValue(total.doubleValue());
		    details=sheet.createRow(rowCount+5);
		    details.createCell(0).setCellValue(note);
		}
		else if(jasper.equalsIgnoreCase("CollectionCharges"))
		{
			rowhead.createCell(0).setCellValue("Sl No.");  
		    rowhead.createCell(1).setCellValue("Recovery Code");  
		    rowhead.createCell(2).setCellValue("Voucher No.");  
		    rowhead.createCell(3).setCellValue("Bill Voucher No.");  
		    rowhead.createCell(4).setCellValue("PEX Number");  
		    rowhead.createCell(5).setCellValue("Division");
		    rowhead.createCell(6).setCellValue("Name of Agency");
		    rowhead.createCell(7).setCellValue("Work Done");
		    rowhead.createCell(8).setCellValue("Amount");
		    BigDecimal total=new BigDecimal("0");
		    int index=1;
		    int rowCount=6;
		    HSSFRow details ;
		    for(DeductionReportBean bean : reportList)
		    {
		    	details=sheet.createRow(rowCount++);
		    	details.createCell(0).setCellValue(index++);  
		    	details.createCell(1).setCellValue(bean.getRecoveryCode());  
		    	details.createCell(2).setCellValue(bean.getVoucherNo());  
		    	details.createCell(3).setCellValue(bean.getBillVoucherNo());  
		    	details.createCell(4).setCellValue(bean.getPexNo());  
		    	details.createCell(5).setCellValue(bean.getDivision());
		    	details.createCell(6).setCellValue(bean.getNameOfAgency());
		    	details.createCell(7).setCellValue(bean.getWorkDone());
			    total=total.add(bean.getAmount());
			    details.createCell(8).setCellValue(bean.getAmount().doubleValue());
		    	
		    }
		    details=sheet.createRow(rowCount);
		    details.createCell(8).setCellValue("Total");
		    details.createCell(9).setCellValue(total.doubleValue());
		    details=sheet.createRow(rowCount+5);
		    details.createCell(0).setCellValue(note);
		}
		else if(jasper.equalsIgnoreCase("IncomeTax"))
		{
			rowhead.createCell(0).setCellValue("Sl No.");  
		    rowhead.createCell(1).setCellValue("Recovery Code");  
		    rowhead.createCell(2).setCellValue("Voucher No.");  
		    rowhead.createCell(3).setCellValue("Bill Voucher No.");  
		    rowhead.createCell(4).setCellValue("PEX Number");  
		    rowhead.createCell(5).setCellValue("Division");
		    rowhead.createCell(6).setCellValue("Name of Agency");
		    rowhead.createCell(7).setCellValue("PAN no. of Agency");
		    rowhead.createCell(8).setCellValue("Work Done");
		    rowhead.createCell(9).setCellValue("Amount");
		    BigDecimal total=new BigDecimal("0");
		    int index=1;
		    int rowCount=6;
		    HSSFRow details ;
		    for(DeductionReportBean bean : reportList)
		    {
		    	details=sheet.createRow(rowCount++);
		    	details.createCell(0).setCellValue(index++);  
		    	details.createCell(1).setCellValue(bean.getRecoveryCode());  
		    	details.createCell(2).setCellValue(bean.getVoucherNo());  
		    	details.createCell(3).setCellValue(bean.getBillVoucherNo());  
		    	details.createCell(4).setCellValue(bean.getPexNo());  
		    	details.createCell(5).setCellValue(bean.getDivision());
		    	details.createCell(6).setCellValue(bean.getNameOfAgency());
		    	details.createCell(7).setCellValue(bean.getPanNoOfAgency());
		    	details.createCell(8).setCellValue(bean.getWorkDone());
			    total=total.add(bean.getAmount());
			    details.createCell(9).setCellValue(bean.getAmount().doubleValue());
		    	
		    }
		    details=sheet.createRow(rowCount);
		    details.createCell(8).setCellValue("Total");
		    details.createCell(9).setCellValue(total.doubleValue());
		    details=sheet.createRow(rowCount+5);
		    details.createCell(0).setCellValue(note);
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		System.out.println("XYZ");
		wb.write(os);
		System.out.println("UVW");
		byte[] fileContent = os.toByteArray();
		System.out.println("CCCC");

		return fileContent;
	}

	private String getPexNo(String billVoucherNo) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String pexNo="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select ei2.id,ei2.transactionnumber from egf_instrumentheader ei2 where id in (select ei.instrumentheaderid from egf_instrumentvoucher ei where ei.voucherheaderid in (select m.payvhid from miscbilldetail m where m.billvhid in (select v.id from voucherheader v  where v.vouchernumber =:billVoucherNo)))");
    	    query.setString("billVoucherNo", billVoucherNo);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		if(element[1] != null)
    	    		{
    	    			pexNo= element[1].toString();
    	    		}
    	    		else
    	    		{
    	    			pexNo="";
    	    		}
    	    		
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return pexNo;
	}

	private String getBillVoucherNumber(String voucherNumber) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String desc="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vhd.id,vhd.vouchernumber from voucherheader vhd where vhd.id in  (select m.payvhid from miscbilldetail m where m.billvhid in (select v.id from voucherheader v  where v.vouchernumber =:voucherNumber))");
    	    query.setString("voucherNumber", voucherNumber);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		if(element[1] != null)
    	    		{
    	    			desc= element[1].toString();
    	    		}
    	    		else
    	    		{
    	    			desc="";
    	    		}
    	    		
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return desc;
	}

	private String getNarration(String voucherNumber) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String desc="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vh.name,vh.description from voucherheader vh where vh.vouchernumber=:voucherNumber");
    	    query.setString("voucherNumber", voucherNumber);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		if(element[1] != null)
    	    		{
    	    			desc= element[1].toString();
    	    		}
    	    		else
    	    		{
    	    			desc="";
    	    		}
    	    		
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return desc;
	}

	private void populateHeadingAndNote(Map<String, Object> paramMap) {
		String recoveryCode=recovery.getChartofaccounts().getGlcode();
		String heading="";
		String note ="";
		String fDate=Constants.DDMMYYYYFORMAT1.format(fromDate);
		String tDate=Constants.DDMMYYYYFORMAT1.format(asOnDate);
		String jasper="";
		if(recoveryCode.equals("3502018"))
		{
			heading="Detail of Labour Cess for the month of "+fDate+ " - "+tDate+ "of Engg. Wing, M.C.(Non Salary)";
			note="Note: The responsibility for work done lies with concerned Division.";
			jasper="LaborCess";
		}
		else if(recoveryCode.equals("1408055"))
		{
			heading="Detail of Collection Charges for the month of "+fDate+ " - "+tDate+ "of Engg. Wing, M.C.(Non Salary)";
			note="Note: The responsibility for work done lies with concerned Division.";
			jasper="CollectionCharges";
		}
		else if(recoveryCode.equals("1405014"))
		{
			heading="Detail of Water Charges for the month of "+fDate+ " - "+tDate+ "of Engg. Wing, M.C.(Non Salary)";
			note="Note: The responsibility for work done lies with concerned Division.";
			jasper="WaterCharges";
		}
		else if(recoveryCode.equals("3502054") || recoveryCode.equals("3502055") || recoveryCode.equals("3502002") || recoveryCode.equals("3502019"))
		{
			heading="Detail of TDS on GST for the month of "+fDate+ " - "+tDate+ "of Engg. Wing, M.C.(Non Salary)";
			note="Note: The responsibility for work done & GST No. lies with concerned Division.";
			jasper="TDSOnGST";
		}
		else if(recoveryCode.equals("3502007") || recoveryCode.equals("3502009") || recoveryCode.equals("3502010") || recoveryCode.equals("3502013") )
		{
			heading="Detail of Income Tax for the month of "+fDate+ " - "+tDate+ "of Engg. Wing, M.C.(Non Salary)";
			note="Note: The responsibility for work done & PAN No. lies with concerned Division.";
			jasper="IncomeTax";
		}
		paramMap.put("header",heading);
        paramMap.put("note",note);
        paramMap.put("jasper",jasper);
		
	}

	@Action(value = "/report/pendingTDSReport-exportSummaryXls")
    public String exportSummaryXls() throws JRException, IOException {
        populateSummaryData();
        final ReportRequest reportInput = new ReportRequest(summaryJasperpath, remittedTDS, getParamMap());
        reportInput.setReportFormat(ReportFormat.XLS);
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
        return "summary-XLS";
    }

    public void validateFinYear()
    {
        if (fromDate != null)
        {
            Constants.DDMMYYYYFORMAT2.format(fromDate);
            if (financialYearDAO.isSameFinancialYear(fromDate, asOnDate))
                return;
            else
                addFieldError("fromDate", "Dates are not within same Financial Year");
        }

    }

    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public Object getModel() {
        return null;
    }

    public void setReportService(final ReportService reportService) {
        this.reportService = reportService;
    }

    public void setPartyName(final String partyName) {
        this.partyName = partyName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setShowRemittedEntries(final boolean showRemittedEntries) {
        this.showRemittedEntries = showRemittedEntries;
    }

    public boolean getShowRemittedEntries() {
        return showRemittedEntries;
    }

    public boolean isShowRemittedEntries() {
        return showRemittedEntries;
    }

    public void setPendingTDS(final List<RemittanceBean> pendingTDS) {
        this.pendingTDS = pendingTDS;
    }

    public List<RemittanceBean> getPendingTDS() {
        return pendingTDS;
    }

    public void setRemittedTDS(final List<TDSEntry> remittedTDS) {
        this.remittedTDS = remittedTDS;
    }

    public List<TDSEntry> getRemittedTDS() {
        return remittedTDS;
    }

    public List<TDSEntry> getInWorkflowTDS() {
        return inWorkflowTDS;
    }

    public void setInWorkflowTDS(List<TDSEntry> inWorkflowTDS) {
        this.inWorkflowTDS = inWorkflowTDS;
    }

    public void setRecovery(final Recovery recovery) {
        this.recovery = recovery;
    }

    public Recovery getRecovery() {
        return recovery;
    }

    public void setFund(final Fund fund) {
        this.fund = fund;
    }

    public Fund getFund() {
        return fund;
    }

    public List<EntityType> getEntitiesList() {
        return entitiesList;
    }

    public void setDepartment(final Department department) {
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDetailKey(final Integer detailKey) {
        this.detailKey = detailKey;
    }

    public Integer getDetailKey() {
        return detailKey;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
    private static JRBeanCollectionDataSource getDataSource(List<DeductionReportBean> reportList) {
        return new JRBeanCollectionDataSource(reportList); 
    }
     
    

}