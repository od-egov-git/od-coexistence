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
package org.egov.egf.web.controller.expensebill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.model.BudgetAppDisplay;
import org.egov.egf.model.BudgetVarianceEntry;
import org.egov.egf.web.actions.report.BudgetAppropriationRegisterReportAction;
import org.egov.egf.web.controller.voucher.BaseVoucherController;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.BillType;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetGroup;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.budget.BudgetService;
import org.egov.utils.BudgetingType;
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
import org.springframework.validation.BindingResult;

/**
 * @author venki
 *
 */

@Controller
public abstract class BaseBillController extends BaseVoucherController {
	private static final Logger LOGGER = Logger.getLogger(BaseBillController.class);

    @Autowired
    private EgBillSubTypeService egBillSubTypeService;

    @Autowired
    private AccountdetailtypeService accountdetailtypeService;

    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;

    @Autowired
    private ExpenseBillService expenseBillService;

    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;

    protected boolean isBillDateDefaultValue;
    @Autowired
    protected BudgetDetailService budgetDetailService;
    
    @Autowired
	private FunctionDAO functionDAO;

    @Autowired
    private FinancialYearHibernateDAO financialYearDAO;
    
    @Autowired
    private BudgetService budgetService;
    
    protected List<String> headerFields = new ArrayList<String>();
    private BudgetDetail budgetDetail = new BudgetDetail();
    protected List<String> gridFields = new ArrayList<String>();    
    private final Map<String, String> queryParamMap = new HashMap<String, String>();
    protected List<String> mandatoryFields = new ArrayList<String>();
    
    
    
    @Autowired
    public MicroserviceUtils microserviceUtils;
    
    private List<BudgetVarianceEntry> budgetVarianceEntries = new ArrayList<BudgetVarianceEntry>();

    public BaseBillController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
List<String> billtype=new ArrayList<>();
    	
    	for(BillType bill:BillType.values()) {
    		billtype.add(bill.getValue());
    		//System.out.println("::::::::: "+bill.getValue());
    	}
        model.addAttribute("billTypes", billtype);
        
        model.addAttribute("billNumberGenerationAuto", expenseBillService.isBillNumberGenerationAuto());
        model.addAttribute("billSubTypes", getBillSubTypes());
        model.addAttribute("subLedgerTypes", accountdetailtypeService.findAll());
        //model.addAttribute("cFunctions", functionDAO.getAllActiveFunctions());
        List<CFunction> func1=new ArrayList<CFunction>();
        String funCode="";
        final List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"function");
        
        for(AppConfigValues value:appConfigValuesList)
        {
        	funCode=value.getValue();
        }
        CFunction fun=functionDAO.getFunctionByCode(funCode);
        func1.add(fun);
        model.addAttribute("cFunctions", func1);
        isBillDateDefaultValue = expenseBillService.isDefaultAutoPopulateCurrDateEnable();
    }

    public List<EgBillSubType> getBillSubTypes() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
    }
    
    public List<EgBillSubType> getBillTypes() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT_CS);
    }

    protected void validateBillNumber(final EgBillregister egBillregister, final BindingResult resultBinder) {
        if (!expenseBillService.isBillNumberGenerationAuto() && egBillregister.getId() == null)
            if (!expenseBillService.isBillNumUnique(egBillregister.getBillnumber()))
                resultBinder.reject("msg.expense.bill.duplicate.bill.number",
                        new String[] { egBillregister.getBillnumber() }, null);
    }

    protected void validateLedgerAndSubledger(final EgBillregister egBillregister, final BindingResult resultBinder) {
        BigDecimal totalDrAmt = BigDecimal.ZERO;
        BigDecimal totalCrAmt = BigDecimal.ZERO;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
            if (details.getDebitamount() != null)
                totalDrAmt = totalDrAmt.add(details.getDebitamount());
            if (details.getCreditamount() != null)
                totalCrAmt = totalCrAmt.add(details.getCreditamount());
            if (details.getGlcodeid() == null)
                resultBinder.reject("msg.expense.bill.accdetail.accmissing", new String[] {}, null);

            /*
             * if (details.getDebitamount() != null && details.getCreditamount()
             * != null && details.getDebitamount().equals(BigDecimal.ZERO) &&
             * details.getCreditamount().equals(BigDecimal.ZERO) &&
             * details.getGlcodeid() != null)
             * resultBinder.reject("msg.expense.bill.accdetail.amountzero", new
             * String[] { details.getChartOfAccounts().getGlcode() }, null);
             */
            
            boolean isDebitCreditAmountEmpty = (details.getDebitamount() == null
                    || (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 0))
                    && (details.getCreditamount() == null || (details.getCreditamount() != null
                            && details.getCreditamount().compareTo(BigDecimal.ZERO) == 0));
            if (isDebitCreditAmountEmpty) {
                resultBinder.reject("msg.expense.bill.accdetail.amountzero",
                        new String[] { details.getChartOfAccounts().getGlcode() }, null);
            }

            if (details.getDebitamount() != null && details.getCreditamount() != null
                    && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1
                    && details.getCreditamount().compareTo(BigDecimal.ZERO) == 1)
                resultBinder.reject("msg.expense.bill.accdetail.amount",
                        new String[] { details.getChartOfAccounts().getGlcode() }, null);
        }
        if (totalDrAmt.compareTo(totalCrAmt) != 0)
            resultBinder.reject("msg.expense.bill.accdetail.drcrmatch", new String[] {}, null);
        validateSubledgerDetails(egBillregister, resultBinder);
    }

    protected void validateSubledgerDetails(final EgBillregister egBillregister, final BindingResult resultBinder) {
        Boolean check;
        BigDecimal detailAmt;
        BigDecimal payeeDetailAmt;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {

            detailAmt = BigDecimal.ZERO;
            payeeDetailAmt = BigDecimal.ZERO;

            if (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1)
                detailAmt = details.getDebitamount();
            else if (details.getCreditamount() != null &&
                    details.getCreditamount().compareTo(BigDecimal.ZERO) == 1)
                detailAmt = details.getCreditamount();

            for (final EgBillPayeedetails payeeDetails : details.getEgBillPaydetailes()) {
                if (payeeDetails != null) {
                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getCreditAmount() != null
                            && payeeDetails.getDebitAmount().equals(BigDecimal.ZERO)
                            && payeeDetails.getCreditAmount().equals(BigDecimal.ZERO))
                        resultBinder.reject("msg.expense.bill.subledger.amountzero",
                                new String[] { details.getChartOfAccounts().getGlcode() }, null);

                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getCreditAmount() != null
                            && payeeDetails.getDebitAmount().compareTo(BigDecimal.ZERO) == 1
                            && payeeDetails.getCreditAmount().compareTo(BigDecimal.ZERO) == 1)
                        resultBinder.reject("msg.expense.bill.subledger.amount",
                                new String[] { details.getChartOfAccounts().getGlcode() }, null);

                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getDebitAmount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetailAmt = payeeDetailAmt.add(payeeDetails.getDebitAmount());
                    else if (payeeDetails.getCreditAmount() != null
                            && payeeDetails.getCreditAmount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetailAmt = payeeDetailAmt.add(payeeDetails.getCreditAmount());

                    check = false;
                    for (final CChartOfAccountDetail coaDetails : details.getChartOfAccounts().getChartOfAccountDetails())
                        if (payeeDetails.getAccountDetailTypeId() == coaDetails.getDetailTypeId().getId())
                            check = true;
                    //if (!check)
                      //  resultBinder.reject("msg.expense.bill.subledger.mismatch",
                        //        new String[] { details.getChartOfAccounts().getGlcode() }, null);

                }

            }

            if (detailAmt.compareTo(payeeDetailAmt) != 0 && !details.getEgBillPaydetailes().isEmpty())
                resultBinder.reject("msg.expense.bill.subledger.amtnotmatchinng",
                        new String[] { details.getChartOfAccounts().getGlcode() }, null);
        }
    }

    @SuppressWarnings("unchecked")
    protected void populateBillDetails(final EgBillregister egBillregister) {
    	LOGGER.info("inside populateBillDetails ");
        egBillregister.getEgBilldetailes().clear();

        if (egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT)) {
            egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
        } 
        else if (egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND)) {
        	LOGGER.info("Expenditure type refund ");
            List<EgBilldetails> detail=new ArrayList();
            for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
            	if(details.getCreditamount()!=null && details.getDebitamount()!=null)
            	{
            		LOGGER.info("EgBillDetails add new credit "+details.getCreditamount()+" & debit detail  "+details.getDebitamount());
            		LOGGER.info("EgBillDetails add function detail "+details.getFunctionid());
            		detail.add(details);
            	}
            }
            if(egBillregister.getIsCitizenRefund()!=null)
            {
            	LOGGER.info("egBillregister.getIsCitizenRefund() "+egBillregister.getIsCitizenRefund());
            	egBillregister.getEgBilldetailes().addAll(detail);
            }
            else{
            	egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
            }
        }
        else {
            egBillregister.getEgBilldetailes().addAll(egBillregister.getDebitDetails());
            egBillregister.getEgBilldetailes().addAll(egBillregister.getCreditDetails());
            egBillregister.getEgBilldetailes().addAll(egBillregister.getNetPayableDetails());
        }

        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
        	System.out.println(":::"+egBillregister.getEgBillregistermis().getFunction().getId());
            if (details.getGlcodeid() != null) {
            	LOGGER.info("details.getGlcodeid() "+details.getGlcodeid());
                if (egBillregister.getEgBillregistermis().getFunction() != null){
                	LOGGER.info("egBillregister.getEgBillregistermis().getFunction() "+egBillregister.getEgBillregistermis().getFunction());
                    details.setFunctionid(BigDecimal.valueOf(egBillregister.getEgBillregistermis().getFunction().getId()));
                    details.setFunction(egBillregister.getEgBillregistermis().getFunction());
                }
                details.setEgBillregister(egBillregister);
                details.setLastupdatedtime(new Date());
                details.setChartOfAccounts(chartOfAccountsService.findById(details.getGlcodeid().longValue(), false));
                LOGGER.info("details egbillId "+details.getEgBillregister().getId());
                LOGGER.info("details functionId "+details.getFunctionid());
            }
            LOGGER.info("details.getGlcodeid() empty");
        }
        if (!egBillregister.getBillPayeedetails().isEmpty())
            populateBillPayeeDetails(egBillregister);
    }

    protected void populateEgBillregistermisDetails(final EgBillregister egBillregister) {
       final   String req_departmentCode = egBillregister.getEgBillregistermis().getDepartmentcode();    
       final Department req_department = microserviceUtils.getDepartmentByCode(egBillregister.getEgBillregistermis().getDepartmentcode());  
    final  CFunction req_function = egBillregister.getEgBillregistermis().getFunction();    
    final  Fund req_fund = egBillregister.getEgBillregistermis().getFund();
 
   BudgetDetail bd = new BudgetDetail();
  bd.setFunction(req_function);
  bd.setExecutingDepartment(req_departmentCode);
  bd.setFund(req_fund);
     
   BigDecimal totalAmount=new BigDecimal(0);
       Date asOnDate = new Date();
       long milliseconds = (long) 365 * 24 * 60 * 60 * 1000;
       Date oneYearBefore = new Date(asOnDate.getTime() - milliseconds);
       String type = "Budget";
       String budgetType = Constants.BE;
    System.out.println("1");
    LOGGER.info("date--1"+asOnDate);
        final CFinancialYear financialYear = financialYearDAO.getFinancialYearByDate(asOnDate);
       
        final boolean hasApprovedReForYear = budgetService.hasApprovedReForYear(financialYear.getId());
        if (hasApprovedReForYear) {
            type = "Revised";
            budgetType = Constants.RE;
        }
        final List<BudgetDetail> result = persistenceService.findAllBy("from BudgetDetail where budget.isbere='" + budgetType
                + "' and " +
                "budget.isActiveBudget=true and budget.status.code='Approved' and budget.financialYear.id="
                + financialYear.getId()
                + getMiscQuery(bd) + " order by budget.name,budgetGroup.name");
       
        System.out.println("2");
        System.out.println("debug :::: budget result size :: :: :: "+result.size());
        if(result.size()!=0) {
        if (budgetVarianceEntries == null)
            budgetVarianceEntries = new ArrayList<BudgetVarianceEntry>();
        for (final BudgetDetail budgetDetail : result) {
            final BudgetVarianceEntry budgetVarianceEntry = new BudgetVarianceEntry();            
           
            budgetVarianceEntry.setBudgetHead(budgetDetail.getBudgetGroup().getName());
           
            if (budgetDetail.getExecutingDepartment() != null) {
                budgetVarianceEntry.setDepartmentCode(budgetDetail.getExecutingDepartment());
                //LOGGER.info("department"+budgetDetail.getExecutingDepartment());
                budgetVarianceEntry.setDepartmentName(microserviceUtils.getDepartmentByCode(budgetDetail.getExecutingDepartment()).getName());
           
                System.out.println("debug :::: DepartmentCode :: ::"+budgetVarianceEntry.getDepartmentCode()+" DepartmentName:: ::"+budgetVarianceEntry.getDepartmentName());

            }
            if (budgetDetail.getFund() != null)
                budgetVarianceEntry.setFundCode(budgetDetail.getFund().getName());
            //LOGGER.info("fund"+budgetVarianceEntry.getFundCode());
            if (budgetDetail.getFunction() != null)
                budgetVarianceEntry.setFunctionCode(budgetDetail.getFunction().getName());
            //LOGGER.info("functioncode"+budgetVarianceEntry.getFunctionCode());
            budgetVarianceEntry.setDetailId(budgetDetail.getId());
            budgetVarianceEntry.setBudgetCode(budgetDetail.getBudget().getName());
           
           
            if ("RE".equalsIgnoreCase(budgetType) && !getConsiderReAppropriationAsSeperate()) {
                budgetVarianceEntry.setAdditionalAppropriation(BigDecimal.ZERO);
                final BigDecimal estimateAmount = (budgetDetail.getApprovedAmount() == null ? BigDecimal.ZERO : budgetDetail
                        .getApprovedAmount()).add(budgetDetail.getApprovedReAppropriationsTotal() == null ? BigDecimal.ZERO
                        : budgetDetail.getApprovedReAppropriationsTotal());
                budgetVarianceEntry.setEstimate(estimateAmount);
            } else {
                budgetVarianceEntry.setEstimate(budgetDetail.getApprovedAmount() == null ? BigDecimal.ZERO : budgetDetail
                        .getApprovedAmount());
                budgetVarianceEntry
                        .setAdditionalAppropriation(budgetDetail.getApprovedReAppropriationsTotal() == null ? BigDecimal.ZERO
                                : budgetDetail.getApprovedReAppropriationsTotal());
            }
            budgetVarianceEntry.setTotal(budgetVarianceEntry.getEstimate().add(budgetVarianceEntry.getAdditionalAppropriation()));
            System.out.println("debug :::: budget total :: :: "+budgetVarianceEntry.getTotal());
            totalAmount=budgetVarianceEntry.getTotal();
            System.out.println("debug :::: budget total  variable :: :: "+totalAmount);
            budgetVarianceEntries.add(budgetVarianceEntry);
        }
        List<BudgetVarianceEntry> budgetVarianceEntriesCopy = new ArrayList<BudgetVarianceEntry>();
        final BudgetDetail budgetDetail2 = result.get(0);
        //LOGGER.info("budgetDetail--2"+budgetDetail2);
       
        budgetVarianceEntriesCopy.add(budgetVarianceEntries.get(0));
        budgetVarianceEntries.clear();
        budgetVarianceEntries.addAll(budgetVarianceEntriesCopy);
       
               
       
        if (null == egBillregister.getEgBillregistermis().getBudget() ) {
        System.out.println("debug :::: budget total  variable :: :: "+totalAmount);
        LOGGER.info("debug :::: budget total set(1):: ::"+budgetVarianceEntries.get(0).getTotal());
egBillregister.getEgBillregistermis().setBudget(totalAmount);
}
     
       /* if (null != egBillregister.getEgBillregistermis().getBudget() ) {
        LOGGER.info("budget total if already set"+budgetVarianceEntries.get(0).getTotal());
egBillregister.getEgBillregistermis().setBudget(budgetVarianceEntries.get(0).getTotal());
}*/        
       
        //final String fromDate = Constants.DDMMYYYYFORMAT2.format(financialYear.getStartingDate());
       // final String toDate = Constants.DDMMYYYYFORMAT2.format(financialYear.getEndingDate());
       
        final String fromDate = Constants.DDMMYYYYFORMAT2.format(financialYear.getStartingDate());
        final String asOnDateQ = Constants.DDMMYYYYFORMAT2.format(asOnDate);
        System.out.println("4");
        if (budgetVarianceEntries != null && budgetVarianceEntries.size() != 0) {
        System.out.println("5");
            setQueryParams(budgetDetail2);
            try {
           /* final List<Object[]> resultForVoucher = budgetDetailService.fetchActualsForFYWithParams(fromDate, "'" + toDate + "'", formMiscQuery("vmis", "gl", "vh"));
            System.out.println(resultForVoucher);
           
            extractData(resultForVoucher);
            final List<Object[]> resultForBill = budgetDetailService.fetchActualsForBillWithVouchersParams(fromDate, "'" + toDate + "'", formMiscQuery("bmis", "bdetail", "bmis"));
            extractData(resultForBill);*/
           
//System.out.println("5");
//setQueryParams(budgetDetail2);
final List<Object[]> resultForVoucher = budgetDetailService.fetchActualsForFYWithParams(fromDate,
"'" + asOnDateQ + "'", formMiscQuery("vmis", "gl", "vh",budgetDetail2));
extractData(resultForVoucher);
final List<Object[]> resultForBill = budgetDetailService.fetchActualsForBillWithVouchersParams(fromDate,
"'" + asOnDateQ + "'",
formMiscQuery("bmis", "bdetail", "bmis",budgetDetail2));
extractData(resultForBill);

            }catch(Exception e) {
            e.printStackTrace();
            }
        }
//System.out.println(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;--->"+budgetVarianceEntries.get(0).getVariance());
       

  //final String departmentCode = budgetDetail2.getExecutingDepartment();
  final Department department = microserviceUtils.getDepartmentByCode(budgetDetail2.getExecutingDepartment());
    final  CFunction function = egBillregister.getEgBillregistermis().getFunction();
    final  Fund fund = egBillregister.getEgBillregistermis().getFund();
    final  BudgetGroup budgetGroup = budgetDetail2.getBudgetGroup();


if (egBillregister.getEgBillregistermis().getPreviousexpenditure() == null) {
BigDecimal cumilativeTotal =  previousexpenditure(department,function,fund,budgetGroup);
if(cumilativeTotal!=null) {
	egBillregister.getEgBillregistermis().setPreviousexpenditure(cumilativeTotal);
	LOGGER.info("debug :::: Previousexpenditure(2):: ::"+egBillregister.getEgBillregistermis().getPreviousexpenditure());
	}
}


if(null==egBillregister.getEgBillregistermis().getCurrentexpenditure()) {
egBillregister.getEgBillregistermis().setCurrentexpenditure(egBillregister.getBillDetails().get(0).getDebitamount());
LOGGER.info("debug :::: Currentexpenditure(3) :: ::"+egBillregister.getBillDetails().get(0).getDebitamount());
    }
   
if (null == egBillregister.getEgBillregistermis().getBalance() ) {
	if(null == egBillregister.getEgBillregistermis().getPreviousexpenditure())
	{
		egBillregister.getEgBillregistermis().setPreviousexpenditure(new BigDecimal(0));
	}
egBillregister.getEgBillregistermis().setBalance((egBillregister.getEgBillregistermis().getBudget().subtract(egBillregister.getEgBillregistermis().getPreviousexpenditure())).subtract(egBillregister.getEgBillregistermis().getCurrentexpenditure()));
System.out.println("debug :::: balance(4) :: :: "+egBillregister.getEgBillregistermis().getBalance());
}
        }
    }

    protected void populateBillPayeeDetails(final EgBillregister egBillregister) {
        EgBillPayeedetails payeeDetail;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes())
            for (final EgBillPayeedetails payeeDetails : egBillregister.getBillPayeedetails())
                if (details.getGlcodeid().equals(payeeDetails.getEgBilldetailsId().getGlcodeid())) {
                    List<Object[]> accountDetails = this.getAccountDetails(payeeDetails.getAccountDetailKeyId(), payeeDetails.getAccountDetailTypeId());
                    payeeDetail = new EgBillPayeedetails();
                    payeeDetail.setEgBilldetailsId(details);
                    payeeDetail.setAccountDetailTypeId(payeeDetails.getAccountDetailTypeId());
                    payeeDetail.setAccountDetailKeyId(payeeDetails.getAccountDetailKeyId());
                    payeeDetail.setDebitAmount(payeeDetails.getDebitAmount());
                    payeeDetail.setCreditAmount(payeeDetails.getCreditAmount());
                    payeeDetail.setDetailKeyName(!accountDetails.isEmpty() ? (String)accountDetails.get(0)[0] : "");
                    payeeDetail.setDetailTypeName(!accountDetails.isEmpty() ? (String)accountDetails.get(0)[1] : "");
                    payeeDetail.setLastUpdatedTime(new Date());
                    details.getEgBillPaydetailes().add(payeeDetail);
                }
    }
    
    private List<Object[]> getAccountDetails(Integer accountDetailKeyId, Integer accountDetailTypeId) {
        String queryString = "select adk.detailname as detailkeyname,adt.name as detailtypename from accountdetailkey adk inner join accountdetailtype adt on adk.detailtypeid=adt.id where adk.detailtypeid=:detailtypeid and adk.detailkey=:detailkey";
        SQLQuery sqlQuery = persistenceService.getSession().createSQLQuery(queryString);
        sqlQuery.setInteger("detailtypeid", accountDetailTypeId);
        sqlQuery.setInteger("detailkey", accountDetailKeyId);
        return sqlQuery.list();
    }

    protected void prepareBillDetailsForView(final EgBillregister egBillregister) {
        for (final EgBilldetails details : egBillregister.getBillDetails()) {
            details.setChartOfAccounts(chartOfAccountsService.findById(details.getGlcodeid().longValue(), false));
            egBillregister.getBillPayeedetails().addAll(details.getEgBillPaydetailes());
        }
        for (final EgBillPayeedetails payeeDetails : egBillregister.getBillPayeedetails()) {
            payeeDetails.getEgBilldetailsId().setChartOfAccounts(
                    chartOfAccountsService.findById(payeeDetails.getEgBilldetailsId().getGlcodeid().longValue(), false));
            final Accountdetailtype detailType = accountdetailtypeService.findOne(payeeDetails.getAccountDetailTypeId());
            EntityType entity = null;
            String dataType = "";
            try {
                final String table = detailType.getFullQualifiedName();
                final Class<?> service = Class.forName(table);
                final String tableName = service.getSimpleName();
                final java.lang.reflect.Method method = service.getMethod("getId");

                dataType = method.getReturnType().getSimpleName();
                if ("Long".equals(dataType))
                    entity = (EntityType) persistenceService.find("from " + tableName + " where id=? order by name",
                            payeeDetails.getAccountDetailKeyId()
                                    .longValue());
                else
                    entity = (EntityType) persistenceService.find("from " + tableName + " where id=? order by name",
                            payeeDetails.getAccountDetailKeyId());
            } catch (final Exception e) {
                throw new ApplicationRuntimeException(e.getMessage());
            }
            payeeDetails.setDetailTypeName(detailType.getName());
            payeeDetails.setDetailKeyName(entity.getName());

        }
    }
   
    private String getMiscQuery(final BudgetDetail budgetDetail) {
    String accountType = "";
    //BudgetDetail budgetDetail = new BudgetDetail();
   
        final StringBuilder query = new StringBuilder();
        if (budgetDetail.getExecutingDepartment() != null && !"".equals(budgetDetail.getExecutingDepartment()))
        {
        System.out.println("department : "+budgetDetail.getExecutingDepartment());
        query.append(" and executingDepartment='").append(budgetDetail.getExecutingDepartment()).append("' ");
        }
        if (budgetDetail.getBudgetGroup() != null && budgetDetail.getBudgetGroup().getId() != null
                && budgetDetail.getBudgetGroup().getId() != -1)
        {
        System.out.println("budgetDetail.getBudgetGroup().getId() :: "+budgetDetail.getBudgetGroup().getId());
        query.append(" and budgetGroup.id=").append(budgetDetail.getBudgetGroup().getId());
        }
        if (budgetDetail.getFunction() != null && budgetDetail.getFunction().getId() != null
                && budgetDetail.getFunction().getId() != -1)
        {
        System.out.println("budgetDetail.getFunction().getId() :: "+budgetDetail.getFunction().getId());
        query.append(" and function.id=").append(budgetDetail.getFunction().getId());
        }
        if (budgetDetail.getFund() != null && budgetDetail.getFund().getId() != null && budgetDetail.getFund().getId() != -1)
        {
        System.out.println("budgetDetail.getFund().getId() ::"+budgetDetail.getFund().getId());
        query.append(" and fund.id=").append(budgetDetail.getFund().getId());
        }
        if (budgetDetail.getFunctionary() != null && budgetDetail.getFunctionary().getId() != null
                && budgetDetail.getFunctionary().getId() != -1)
            query.append(" and functionary.id=").append(budgetDetail.getFunctionary().getId());
        if (budgetDetail.getScheme() != null && budgetDetail.getScheme().getId() != null
                && budgetDetail.getScheme().getId() != -1)
            query.append(" and scheme.id=").append(budgetDetail.getScheme().getId());
        if (budgetDetail.getSubScheme() != null && budgetDetail.getSubScheme().getId() != null
                && budgetDetail.getSubScheme().getId() != -1)
            query.append(" and subScheme.id=").append(budgetDetail.getSubScheme().getId());
        if (budgetDetail.getBoundary() != null && budgetDetail.getBoundary().getId() != null
                && budgetDetail.getBoundary().getId() != -1)
            query.append(" and boundary.id=").append(budgetDetail.getBoundary().getId());
        if (!"".equalsIgnoreCase(accountType) && !"-1".equalsIgnoreCase(accountType))
            query.append(" and budgetGroup.accountType='").append(accountType).append("'");
        return query.toString();
    }
    private boolean getConsiderReAppropriationAsSeperate() {
        final List<AppConfigValues> appList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
                "CONSIDER_RE_REAPPROPRIATION_AS_SEPARATE");
        String appValue = "-1";
        appValue = appList.get(0).getValue();
        return "Y".equalsIgnoreCase(appValue);
    }
   
    private void extractData(final List<Object[]> result) {
        final Map<String, String> budgetDetailIdsAndAmount = new HashMap<String, String>();
        if (result == null)
            return;
        for (final Object[] row : result)
            if (row[0] != null && row[1] != null)
                budgetDetailIdsAndAmount.put(row[0].toString(), row[1].toString());
        for (final BudgetVarianceEntry row : budgetVarianceEntries) {
            final BigDecimal actual = row.getActual();
            if (budgetDetailIdsAndAmount.get(row.getDetailId().toString()) != null) {
                if (actual == null || BigDecimal.ZERO.compareTo(actual) == 0)
                    row.setActual(new BigDecimal(budgetDetailIdsAndAmount.get(row.getDetailId().toString())));
                else
                    row.setActual(
                            row.getActual().add(new BigDecimal(budgetDetailIdsAndAmount.get(row.getDetailId().toString()))));
            } else if (actual == null)
                row.setActual(BigDecimal.ZERO);
            row.setVariance(row.getEstimate().add(
                    row.getAdditionalAppropriation().subtract(row.getActual() == null ? BigDecimal.ZERO : row.getActual())));
        }
    }
   
    public boolean shouldShowHeaderField(final String fieldName) {
        return (headerFields.contains(fieldName) || gridFields.contains(fieldName)) && mandatoryFields.contains(fieldName);
    }
   
    private void setQueryParams(final BudgetDetail budgetDetail) {
    System.out.println("7");
        if ( budgetDetail.getExecutingDepartment() != null  && budgetDetail.getExecutingDepartment() != "")
            queryParamMap.put("deptId", budgetDetail.getExecutingDepartment());
        if ( budgetDetail.getFunction() != null  && budgetDetail.getFunction().getId() != null && budgetDetail.getFunction().getId() != -1
                && budgetDetail.getFunction().getId() != 0)
            queryParamMap.put("functionId", budgetDetail.getFunction().getId().toString());
        if (budgetDetail.getFund() != null && budgetDetail.getFund().getId() != null
                && budgetDetail.getFund().getId() != -1 && budgetDetail.getFund().getId() != 0)
            queryParamMap.put("fundId", budgetDetail.getFund().getId().toString());
        if (shouldShowHeaderField(Constants.SCHEME) && budgetDetail.getScheme() != null
                && budgetDetail.getScheme().getId() != null && budgetDetail.getScheme().getId() != -1
                && budgetDetail.getScheme().getId() != 0)
            queryParamMap.put("schemeId", budgetDetail.getScheme().getId().toString());
        if (shouldShowHeaderField(Constants.SUBSCHEME) && budgetDetail.getSubScheme() != null
                && budgetDetail.getSubScheme().getId() != null && budgetDetail.getSubScheme().getId() != -1
                && budgetDetail.getSubScheme().getId() != 0)
            queryParamMap.put("subSchemeId", budgetDetail.getSubScheme().getId().toString());
        if (shouldShowHeaderField(Constants.FUNCTIONARY) && budgetDetail.getFunctionary() != null
                && budgetDetail.getFunctionary().getId() != null && budgetDetail.getFunctionary().getId() != -1
                && budgetDetail.getFunctionary().getId() != 0)
            queryParamMap.put("functionaryId", budgetDetail.getFunctionary().getId().toString());
    }
   
    private StringBuffer formMiscQuery(final String mis, final String gl, final String detail, final BudgetDetail budgetDetail ) {
        StringBuffer miscQuery = new StringBuffer();
        if (budgetDetail.getFund()!=null) {
            miscQuery = miscQuery.append(" and " + detail + ".fundId=bd.fund ");
            miscQuery = miscQuery.append(" and bd.fund= " + budgetDetail.getFund().getId());
        }
        if (shouldShowHeaderField(Constants.SCHEME) && queryParamMap.containsKey("schemeId")) {
            miscQuery = miscQuery.append(" and " + mis + ".schemeid=bd.scheme ");
            miscQuery = miscQuery.append(" and bd.scheme= " + Integer.parseInt(queryParamMap.get("schemeId")));
        }
        if (shouldShowHeaderField(Constants.SUB_SCHEME) && queryParamMap.containsKey("subSchemeId")) {
            miscQuery = miscQuery.append(" and " + mis + ".subschemeid=bd.subscheme ");
            miscQuery = miscQuery.append(" and bd.subscheme= " + Integer.parseInt(queryParamMap.get("subSchemeId")));
        }
        if (shouldShowHeaderField(Constants.FUNCTIONARY) && queryParamMap.containsKey("functionaryId")) {
            miscQuery = miscQuery.append(" and " + mis + ".functionaryid=bd.functionary ");
            miscQuery = miscQuery.append(" and bd.functionary= " + Integer.parseInt(queryParamMap.get("functionaryId")));
        }
        if (budgetDetail.getFunction().getId()!=null) {
            miscQuery = miscQuery.append(" and " + gl + ".functionId=bd.function ");
            miscQuery = miscQuery.append(" and bd.function= " + budgetDetail.getFunction().getId());
        }
        if (budgetDetail.getExecutingDepartment()!=null) {
            miscQuery = miscQuery.append(" and " + mis + ".departmentcode=bd.executing_department ");
            miscQuery = miscQuery.append(" and bd.executing_department= '" + budgetDetail.getExecutingDepartment()+"' ");
        }
        return miscQuery;
    }
    private BigDecimal previousexpenditure(final Department department,final CFunction function,final Fund fund,final BudgetGroup budgetGroup) {
   
    BigDecimal cumilativeTotal = null;
    Date dtAsOnDate = new Date();
        CFinancialYear financialYr = new CFinancialYear();
        financialYr = financialYearDAO.getFinancialYearByDate(dtAsOnDate);
        CFinancialYear financialYear = null;
        financialYear = financialYearDAO.getFinancialYearById(Long.valueOf(financialYr.getId()));
        String finYearRange = financialYear.getFinYearRange();
        final Date dStartDate = financialYear.getStartingDate();
        final String strAODate = Constants.DDMMYYYYFORMAT1.format(dtAsOnDate);
        final String strStDate = Constants.DDMMYYYYFORMAT1.format(dStartDate);
        String  budgetHead;
        Query query = null;
        List<BudgetAppDisplay> budgetAppropriationRegisterList = new ArrayList<BudgetAppDisplay>();

        if (budgetGroup != null) {
            budgetHead = budgetGroup.getName();
            StringBuilder strQuery = new StringBuilder();
            strQuery.append("select vmis.budgetary_appnumber as bdgApprNumber, vh.vouchernumber as VoucherNumber, vh.voucherdate as voucherDate, vh.description as description,vh.createddate as createdDate, ");
            strQuery.append(" null as billNumber, null as billDate,null as billCreatedDate, gl.debitamount as debitAmount, gl.creditamount as creditAmount from generalledger gl, vouchermis vmis,  ");
            strQuery.append(" voucherheader vh  where vh.id = gl.voucherheaderid and vh.id = vmis.voucherheaderid and  gl.glcodeid =:glCodeId");
            strQuery.append(" and (vmis.budgetary_appnumber  != 'null' and vmis.budgetary_appnumber is not null) and vh.status != 4 and vh.voucherdate  >=:strStDate");
            strQuery.append(" and vmis.billnumber  notnull and vh.voucherdate <=:strAODate");
            strQuery.append(getFunctionQuery("gl.functionid",function));
            strQuery.append(getDepartmentQuery("vmis.departmentcode",department));
            strQuery.append(getFundQuery("vh.fundid",fund));
            strQuery.append(" ");
            strQuery.append(" union select distinct bmis.budgetary_appnumber as bdgApprNumber, vh1.vouchernumber as VoucherNumber, vh1.voucherdate as  voucherDate , br.narration as description,vh1.createddate as createdDate, br.billnumber as billNumber, br.billdate as billDate,br.createddate as billCreatedDate ,  bd.debitamount as debitAmount, bd.creditamount as creditAmount  ");
            strQuery.append(" from eg_billdetails bd, eg_billregistermis bmis, eg_billregister br, voucherHeader vh1 where br.id = bd.billid and br.id = bmis.billid and  bd.glcodeid =:glCodeId ");
            strQuery.append(" and (bmis.budgetary_appnumber != 'null' and bmis.budgetary_appnumber is not null) and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL')) and (vh1.id = bmis.voucherheaderid )  and br.billdate  >=:strStDate");
            strQuery.append(" and br.billdate  <=:strAODate");
            strQuery.append(getFunctionQuery("bd.functionid",function));
            strQuery.append(getDepartmentQuery("bmis.departmentcode",department));
            strQuery.append(getFundQuery("bmis.fundid",fund));
            strQuery.append("  ");
            strQuery.append(" union select distinct bmis1.budgetary_appnumber as bdgApprNumber, null as VoucherNumber,cast( null as date) voucherDate , ");
            strQuery.append(" br.narration as description,cast( null as date) createdDate, br.billnumber as billNumber, br.billdate as billDate,br.createddate as billCreatedDate ,   bd1.debitamount as debitAmount, bd1.creditamount as creditAmount from eg_billdetails bd1, eg_billregistermis bmis1, eg_billregister br  ");
            strQuery.append(" where br.id = bd1.billid and br.id = bmis1.billid and  bd1.glcodeid =:glCodeId ");
            strQuery.append(" and (bmis1.budgetary_appnumber != 'null' and bmis1.budgetary_appnumber is not null) ");
            strQuery.append(" and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL')) and bmis1.voucherheaderid is null and br.billdate   >=:strStDate");
            strQuery.append(" and br.billdate <=:strAODate");
            strQuery.append(getFunctionQuery("bd1.functionid",function));
            strQuery.append(getDepartmentQuery("bmis1.departmentcode",department));
            strQuery.append(getFundQuery("bmis1.fundid",fund));
            strQuery.append("  order by bdgApprNumber ");

                LOGGER.info("BudgetAppropriationRegisterReportAction -- strQuery...." + strQuery);

            query = persistenceService.getSession().createSQLQuery(strQuery.toString())
                    .addScalar("bdgApprNumber")
                    .addScalar("voucherDate", StandardBasicTypes.DATE)
                    .addScalar("billDate", StandardBasicTypes.DATE)
                    .addScalar("createdDate",StandardBasicTypes.DATE)
                    .addScalar("billCreatedDate", StandardBasicTypes.DATE)
                    .addScalar("description")
                    .addScalar("VoucherNumber")
                    .addScalar("billNumber")
                    .addScalar("debitAmount", BigDecimalType.INSTANCE)
                    .addScalar("creditAmount", BigDecimalType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(BudgetAppDisplay.class));
            query=setParameterForBudgetAppDisplay(query,dtAsOnDate,dStartDate,department, function,fund,budgetGroup);
        }
        budgetAppropriationRegisterList = query.list();

        List<BudgetAppDisplay> budgetApprRegNewList = new ArrayList<BudgetAppDisplay>();
        final List<BudgetAppDisplay> budgetApprRegUpdatedList1 = new ArrayList<BudgetAppDisplay>();
        final HashMap<String, BudgetAppDisplay> regMap = new HashMap<String, BudgetAppDisplay>();
        if (budgetAppropriationRegisterList.size() > 0) {
            StringBuilder strsubQuery = new StringBuilder();
            strsubQuery.append("select vmis.budgetary_appnumber as bdgApprNumber, vh.vouchernumber as VoucherNumber, vh.voucherdate as voucherDate, vh.description as description,vh.createddate as createdDate, ");
            strsubQuery.append(" br.billnumber as billNumber, br.billdate as billDate,br.createddate as billCreatedDate, gl.debitamount as debitAmount, gl.creditamount as creditAmount from generalledger gl, vouchermis vmis,  ");
            strsubQuery.append(" voucherheader vh,  eg_billregistermis bmis, eg_billregister br  where vh.id = gl.voucherheaderid and vh.id = vmis.voucherheaderid and vh.id = bmis.voucherheaderid and bmis.billid = br.id ");
            strsubQuery.append(" and  gl.glcodeid =:glCodeId ");
            strsubQuery.append(" and  ");
            strsubQuery.append(" (vmis.budgetary_appnumber  != 'null' and vmis.budgetary_appnumber is not null) and vh.status != 4 and vh.voucherdate  >=:strStDate");
            strsubQuery.append(" and vh.voucherdate <=:strAODate");
            strsubQuery.append(getFunctionQuery("gl.functionid",function));
            strsubQuery.append(getDepartmentQuery("vmis.departmentcode",department));
            strsubQuery.append(getFundQuery("vh.fundid",fund));
            strsubQuery.append("  order by bdgApprNumber ");

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("BudgetAppropriationRegisterReportAction -- strsubQuery...." + strsubQuery);

            query = persistenceService.getSession().createSQLQuery(strsubQuery.toString())
                    .addScalar("bdgApprNumber")
                    .addScalar("voucherDate", StandardBasicTypes.DATE)
                    .addScalar("billDate", StandardBasicTypes.DATE)
                    .addScalar("createdDate", StandardBasicTypes.DATE)
                    .addScalar("billCreatedDate", StandardBasicTypes.DATE)
                    .addScalar("description")
                    .addScalar("VoucherNumber")
                    .addScalar("billNumber")
                    .addScalar("debitAmount", BigDecimalType.INSTANCE)
                    .addScalar("creditAmount", BigDecimalType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(BudgetAppDisplay.class));
            query=setParameterForBudgetAppDisplay(query,dtAsOnDate,dStartDate,department, function,fund,budgetGroup);
            budgetApprRegNewList = query.list();
            if (budgetApprRegNewList.size() > 0) {
                for (final BudgetAppDisplay budgetAppRtDisp : budgetApprRegNewList)
                    regMap.put(budgetAppRtDisp.getBdgApprNumber(), budgetAppRtDisp);

                for (final BudgetAppDisplay budgetAppropriationRegisterDisp : budgetAppropriationRegisterList)
                    if (regMap.containsKey(budgetAppropriationRegisterDisp.getBdgApprNumber()))
                        budgetApprRegUpdatedList1.add(regMap.get(budgetAppropriationRegisterDisp.getBdgApprNumber()));
                    else
                        budgetApprRegUpdatedList1.add(budgetAppropriationRegisterDisp);
            }
        }
        if (budgetApprRegUpdatedList1.size() > 0) {
            budgetAppropriationRegisterList.clear();
            budgetAppropriationRegisterList.addAll(budgetApprRegUpdatedList1);
        }
        cumilativeTotal = updateBdgtAppropriationList(budgetAppropriationRegisterList,department, function,fund,budgetGroup);
        LOGGER.debug("debug :::: previousexpenditure  cumilativeTotal...." + cumilativeTotal);
        return cumilativeTotal;
    }
   
    private String getFunctionQuery(final String string, final CFunction function) {
        final String query = "";
        if (function.getId() != null && function.getId() != -1)
            return " and " + string + " =:functionId ";
        return query;
    }
   
    private String getDepartmentQuery(final String string, final Department department ) {
        final String query = "";
        if (department.getCode() != null )
            return " and " + string + " =:departmentcode ";
        return query;
    }
   
    private String getFundQuery(final String string, final Fund fund ) {
        final String query = "";
        if (fund.getId() != null && fund.getId() != -1)
            return " and " + string + " =:fundId ";
        return query;
    }
    private Query setParameterForBudgetAppDisplay(Query query ,Date asOnDate,Date startDate,final Department department,final CFunction function,final Fund fund,final BudgetGroup budgetGroup)
    {
        if (function.getId() != null && function.getId() != -1)
        {
            query.setLong("functionId", function.getId()) ;
        }
        System.out.println("dept :"+department.getCode());
        if (department.getCode() != null )
        {
            query.setString("departmentcode", department.getCode()) ;
        }
        if (fund.getId() != null && fund.getId() != -1)
        {
            query.setLong("fundId", fund.getId()) ;
        }
        if (budgetGroup.getMinCode().getId() != null )
        {
            query.setLong("glCodeId", budgetGroup.getMinCode().getId()) ;
        }
        if (asOnDate != null )
        {
            query.setDate("strAODate", asOnDate) ;
        }
       
        if (startDate != null )
        {
            query.setDate("strStDate", startDate) ;
        }
       
       
        return query;
    }
   
    private BigDecimal updateBdgtAppropriationList(List<BudgetAppDisplay> budgetAppropriationRegisterList,final Department department,final CFunction function,final Fund fund,final BudgetGroup budgetGroup) {
    List<BudgetAppDisplay> updatedBdgtAppropriationRegisterList = new ArrayList<BudgetAppDisplay>();
    BigDecimal totalGrant = null;
        BigDecimal cumulativeAmt = null;
        BigDecimal balanceAvailableAmt = new BigDecimal(0.0);
        BigDecimal totalDebit = new BigDecimal(0.0);
        BigDecimal totalCredit = new BigDecimal(0.0);
        if (totalGrant == null)
            totalGrant = new BigDecimal(0.0);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("budgetAppropriationRegisterList.size() :" + budgetAppropriationRegisterList.size());
        if (budgetAppropriationRegisterList.size() > 0) {
            int iSerialNumber = 1;
            for (final BudgetAppDisplay budgetAppropriationRegisterDisp : budgetAppropriationRegisterList) {
                if (BudgetingType.DEBIT.equals(budgetGroup.getBudgetingType()))
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1) {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getDebitAmount());
                        totalDebit = totalDebit.add(budgetAppropriationRegisterDisp.getBillAmount());
                    } else {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getCreditAmount().multiply(
                                new BigDecimal("-1")));
                        totalCredit = totalCredit.add(budgetAppropriationRegisterDisp.getBillAmount().abs());
                    }
                if (BudgetingType.CREDIT.equals(budgetGroup.getBudgetingType()))
                    if (budgetAppropriationRegisterDisp.getCreditAmount() != null
                            && budgetAppropriationRegisterDisp.getCreditAmount().compareTo(BigDecimal.ZERO) == 1) {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getCreditAmount());
                        totalCredit = totalCredit.add(budgetAppropriationRegisterDisp.getBillAmount());
                    } else {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getDebitAmount().multiply(
                                new BigDecimal("-1")));
                        totalDebit = totalDebit.add(budgetAppropriationRegisterDisp.getBillAmount().abs());
                    }
                if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType()))
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1)
                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getDebitAmount());
                    else
                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getCreditAmount().multiply(
                                new BigDecimal("-1")));
                if (cumulativeAmt == null) {
                    if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType()))
                        cumulativeAmt = budgetAppropriationRegisterDisp.getBillAmount();
                    else if (BudgetingType.CREDIT.equals(budgetGroup.getBudgetingType()))
                        cumulativeAmt = totalCredit.subtract(totalDebit);
                    else if (BudgetingType.DEBIT.equals(budgetGroup.getBudgetingType()))
                        cumulativeAmt = totalDebit.subtract(totalCredit);
                    budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                } else // when budgeting type is 'ALL', to calculate the cumulative balance,
                       // if the debit amount>0, add the debit amount to cumulative amount
                       // if the credit amount>0, subtract the credit amount from the cumulative amount
                if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType())) {
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1) {
                        cumulativeAmt = budgetAppropriationRegisterDisp.getBillAmount().abs().add(cumulativeAmt);
                        budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                    } else {
                        cumulativeAmt = cumulativeAmt.subtract(budgetAppropriationRegisterDisp.getBillAmount().abs());
                        budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                    }
                } else if (BudgetingType.CREDIT.equals(budgetGroup.getBudgetingType())) {
                    cumulativeAmt = cumulativeAmt.add(totalCredit.subtract(totalDebit));
                    budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                } else if (BudgetingType.DEBIT.equals(budgetGroup.getBudgetingType())) {
                    cumulativeAmt = cumulativeAmt.add(totalDebit.subtract(totalCredit));
                    budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                }
                // when budgeting type is 'ALL', to calculate the running balance,
                // if the debit amount>0, subtract the cumulative from running balance
                // if the credit amount>0, add the cumulative to running balance
                if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType())) {
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1)
                        balanceAvailableAmt = totalGrant.subtract(budgetAppropriationRegisterDisp.getCumulativeAmount().abs());
                    else
                        balanceAvailableAmt = totalGrant.add(budgetAppropriationRegisterDisp.getCumulativeAmount());
                } else
                    balanceAvailableAmt = totalGrant.subtract(budgetAppropriationRegisterDisp.getCumulativeAmount());
                budgetAppropriationRegisterDisp.setBalanceAvailableAmount(balanceAvailableAmt);
                budgetAppropriationRegisterDisp.setSerailNumber(Integer.toString(iSerialNumber));
                updatedBdgtAppropriationRegisterList.add(budgetAppropriationRegisterDisp);
                totalCredit = BigDecimal.ZERO;
                totalDebit = BigDecimal.ZERO;
                iSerialNumber++;
            }
        }
        LOGGER.info("from updateBdgtAppropriationList cumilativeTotal...." + cumulativeAmt);
        return cumulativeAmt;
    }


}