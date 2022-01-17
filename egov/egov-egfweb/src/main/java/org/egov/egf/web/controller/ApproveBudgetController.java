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

package org.egov.egf.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FundService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetReAppropriation;
import org.egov.model.budget.BudgetUploadReport;
import org.egov.model.budget.BudgetWrapper;
import org.egov.model.voucher.WorkflowBean;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.budget.BudgetReAppropriationService;
import org.egov.services.budget.BudgetService;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/approvebudget")
public class ApproveBudgetController extends BaseBillController{
   

	public ApproveBudgetController(AppConfigValueService appConfigValuesService) {
		super(appConfigValuesService);
		// TODO Auto-generated constructor stub
	}

	private static final String APPROVAL_POSITION = "approvalPosition";
	private static final String APPROVAL_DESIGNATION = "approvalDesignation";
	private final static String APPROVEBUDGET_SEARCH = "approvebudget-search";
    private final static String APPROVEBUDGET_SEARCH_CAO = "approvebudget-searchCAO";
    private final static String APPROVEBUDGET_SEARCH_CAO_NEW = "approvebudget-searchCAO_New";
    private final static String APPROVEBUDGET_SEARCH_SO = "approvebudget-searchSO";
    private final static String APPROVEBUDGET_SEARCH_ACMC = "approvebudget-searchACMC";
    private final static String APPROPRIATION_NEW = "appropriation_New";
    private final static String APPROPRIATION_EDIT = "budgetReAppropriation_EditNew_bkp";
    private List<BudgetDetail> budgetDetailsList=new ArrayList<BudgetDetail>();

    @Autowired
    @Qualifier("budgetService")
    private BudgetService budgetService;
	/*
	 * @Autowired private final BudgetUploadReportRepository
	 * budgetUploadReportRepository;
	 */
    @Autowired
    @Qualifier("budgetDetailService")
    private BudgetDetailService budgetDetailService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
    public WorkflowBean workflowBean = new WorkflowBean();
    private BudgetDetail budgetDetail;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;
    
    @Autowired
    private FunctionService functionService; 
    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    @Autowired
    private FundService fundService;  
    @Autowired
    private FinancialUtils financialUtils;
    
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    BudgetReAppropriationService budgetReAppropriationService;
    
    private void prepareNewForm(Model model) {
        model.addAttribute("budgets", budgetService.getBudgetsForUploadReport());
        budgetDetailsList=budgetDetailService.getBudgetDetailsForUploadReport();
        if(null != budgetDetailsList && !budgetDetailsList.isEmpty())
        {
        	for(BudgetDetail bd:budgetDetailsList)
            {
        		Department dept=departmentService.getDepartmentById(Long.parseLong(bd.getExecutingDepartment()));
        		if(null != dept)
        		{
        			bd.setExecDeptName(dept.getName());
        		}
        		else
        		{
        			bd.setExecDeptName("");
        		}
            	
            }
        }        
        model.addAttribute("budgetDetails",budgetDetailsList);
    }
    
    private void prepareNewFormCAO(Model model) {
        model.addAttribute("budgets", budgetService.getBudgetsForUploadReportCAO());
        budgetDetailsList=budgetDetailService.getBudgetDetailsForUploadReportCAO();
        if(null != budgetDetailsList && !budgetDetailsList.isEmpty())
        {
        	for(BudgetDetail bd:budgetDetailsList)
            {
        		Department dept=departmentService.getDepartmentByCode(bd.getExecutingDepartment());
        		if(null != dept)
        		{
        			bd.setExecDeptName(dept.getName());
        		}
        		else
        		{
        			bd.setExecDeptName("");
        		}
            	
            }
        }
        model.addAttribute("budgetDetails",budgetDetailsList);
        
    }
    
    private void prepareNewFormSO(Model model) {
        model.addAttribute("budgets", budgetService.getBudgetsForUploadReportSO());
        budgetDetailsList=budgetDetailService.getBudgetDetailsForUploadReportSO();
        if(null != budgetDetailsList && !budgetDetailsList.isEmpty())
        {
        	for(BudgetDetail bd:budgetDetailsList)
            {
        		Department dept=departmentService.getDepartmentById(Long.parseLong(bd.getExecutingDepartment()));
        		if(null != dept)
        		{
        			bd.setExecDeptName(dept.getName());
        		}
        		else
        		{
        			bd.setExecDeptName("");
        		}
            	
            }
        }
        model.addAttribute("budgetDetails",budgetDetailsList);
        
    }
    
    
    private void prepareNewFormACMC(Model model) {
        model.addAttribute("budgets", budgetService.getBudgetsForUploadReportACMC());
        budgetDetailsList=budgetDetailService.getBudgetDetailsForUploadReportACMC();
        if(null != budgetDetailsList && !budgetDetailsList.isEmpty())
        {
        	for(BudgetDetail bd:budgetDetailsList)
            {
        		Department dept=departmentService.getDepartmentById(Long.parseLong(bd.getExecutingDepartment()));
        		if(null != dept)
        		{
        			bd.setExecDeptName(dept.getName());
        		}
        		else
        		{
        			bd.setExecDeptName("");
        		}
            	
            }
        }
        model.addAttribute("budgetDetails",budgetDetailsList);
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET,RequestMethod.POST})
    public String search(Model model)
    {
        BudgetUploadReport budgetUploadReport = new BudgetUploadReport();
        prepareNewForm(model);
        model.addAttribute("budgetUploadReport", budgetUploadReport);
        return APPROVEBUDGET_SEARCH;

    }
    
    @RequestMapping(value = "/verifyCAO", method = {RequestMethod.GET,RequestMethod.POST})
    public String verifyCAO(Model model)
    {
        BudgetUploadReport budgetUploadReport = new BudgetUploadReport();
        prepareNewFormCAO(model);
        model.addAttribute("budgetUploadReport", budgetUploadReport);
        return APPROVEBUDGET_SEARCH_CAO;

    }
    
    @RequestMapping(value = "/budgetReAppropriation-edit/{budgetId}", method = {RequestMethod.GET,RequestMethod.POST})
    public String budgetCAOEdit(Model model,@PathVariable Long budgetId) {
    	System.out.println("budgetId---->> "+budgetId);
    	final BudgetReAppropriation reApp = budgetReAppropriationService.findByBudgetDetail(budgetId);
    	if (reApp.getState() != null)
        {
        	model.addAttribute("stateType", reApp.getClass().getSimpleName());
        	model.addAttribute("currentState", reApp.getState().getValue());
        }
    	prepareWorkflow(model, reApp, new WorkflowContainer());
        return APPROPRIATION_EDIT;
    }
    
    @RequestMapping(value = "/verifyCAONew/{budgetId}", method = {RequestMethod.GET,RequestMethod.POST})
    public String verifyCAONew(Model model,@PathVariable final String budgetId)
    {
    	System.out.println("budgetId---->> "+budgetId);
    	if(budgetId!=null) 
    	{
	    	budgetDetail = (BudgetDetail) persistenceService.find("from BudgetDetail where budget.id=?",
	                Long.valueOf(budgetId));
	    }
	    ////////
	    List<BudgetDetail> budgetDetailList1 = new ArrayList<BudgetDetail>();
	    try {
	        budgetDetailList1=persistenceService.findAllBy("from BudgetDetail where state.id=?",budgetDetail.getState().getId());
	        if (budgetDetail.getState() != null)
	        {
	        	model.addAttribute("stateType", budgetDetail.getClass().getSimpleName());
	        	model.addAttribute("currentState", budgetDetail.getState().getValue());
	        }
	        model.addAttribute("workflowHistory",
	                financialUtils.getHistory(budgetDetail.getState(), budgetDetail.getStateHistory()));
	        //List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
	            prepareWorkflow(model, budgetDetail, new WorkflowContainer());
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	   
		
		BudgetUploadReport budgetUploadReport = new BudgetUploadReport();
		model.addAttribute("budgetUploadReport",budgetUploadReport);
		model.addAttribute("budgetDetails",budgetDetailList1);
		 
        return APPROVEBUDGET_SEARCH_CAO_NEW;

    }
    /////budget appropriation new task
    @RequestMapping(value = "/verifyAppropriationNew/{budgetId}", method = {RequestMethod.GET,RequestMethod.POST})
    public String verifyAppropriationNew(Model model,@PathVariable final String budgetId)
    {
    	System.out.println("budgetId---->> "+budgetId);
    	if(budgetId!=null) 
    	{
	    	budgetDetail = (BudgetDetail) persistenceService.find("from BudgetDetail where budget.id=?",
	                Long.valueOf(budgetId));
	    }
	    List<BudgetDetail> budgetDetailList1 = new ArrayList<BudgetDetail>();
	    try {
	        budgetDetailList1=persistenceService.findAllBy("from BudgetDetail where state.id=?",budgetDetail.getState().getId());
	        if (budgetDetail.getState() != null)
	        {
	        	model.addAttribute("stateType", budgetDetail.getClass().getSimpleName());
	        	model.addAttribute("currentState", budgetDetail.getState().getValue());
	        }
	        model.addAttribute("workflowHistory",
	                financialUtils.getHistory(budgetDetail.getState(), budgetDetail.getStateHistory()));
	            prepareWorkflow(model, budgetDetail, new WorkflowContainer());
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
		BudgetUploadReport budgetUploadReport = new BudgetUploadReport();
		model.addAttribute("budgetUploadReport",budgetUploadReport);
		model.addAttribute("budgetDetails",budgetDetailList1);
        return APPROVEBUDGET_SEARCH_CAO_NEW;
    }
    @RequestMapping(value = "/rejectedBudgetSO", method = {RequestMethod.GET,RequestMethod.POST})
    public String rejectedBudgetSO(Model model)
    {   BudgetWrapper budgetWrapper= new BudgetWrapper();
        BudgetUploadReport budgetUploadReport = new BudgetUploadReport();
        prepareNewFormDropDown(model);
        prepareNewFormSO(model);
        model.addAttribute("budgetUploadReport", budgetUploadReport);
        model.addAttribute("budgetWrapper", budgetWrapper);
        return APPROVEBUDGET_SEARCH_SO;

    }
    
    
    private void prepareNewFormDropDown(final Model model) {
        model.addAttribute("funds", fundService.findAllActiveAndIsnotleaf());
        model.addAttribute("departments", microserviceUtils.getDepartments());
        model.addAttribute("functions",  functionService.findAllActive());
       
    }
    
    @RequestMapping(value = "/verifyACMC", method = {RequestMethod.GET,RequestMethod.POST})
    public String verifyACMC(Model model)
    {
        BudgetUploadReport budgetUploadReport = new BudgetUploadReport();
        prepareNewFormACMC(model);
        model.addAttribute("budgetUploadReport", budgetUploadReport);
        return APPROVEBUDGET_SEARCH_ACMC;

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@ModelAttribute final BudgetUploadReport budgetUploadReport, final BindingResult errors,
            final RedirectAttributes redirectAttrs,@RequestParam final String workAction) {
    	if(workAction.equalsIgnoreCase("VERIFY"))
    	{ 
        Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
        budgetService.updateByMaterializedPath(reBudget.getMaterializedPath());
        budgetDetailService.updateByMaterializedPath(reBudget.getMaterializedPath());
        budgetService.updateByMaterializedPath(beBudget.getMaterializedPath());
        budgetDetailService.updateByMaterializedPath(beBudget.getMaterializedPath());
        chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.success");
    	}
    	else if(workAction.equalsIgnoreCase("REJECT"))
    	{
    		 Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathReturnByMC(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathReturnByMC(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathReturnByMC(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathReturnByMC(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.Return");

 	      
    		
    		
    	}
    	else if(workAction.equalsIgnoreCase("CANCEL"))
    	{
    		 Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathSTATUSCancelByMC(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelByMC(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathSTATUSCancelByMC(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelByMC(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cancel");

 	      
    		

    	}
        return "redirect:/approvebudget/search";
    }
    
    public BudgetDetail budgetDetailStatusChange(final BudgetDetail budgetDetail, final String workFlowAction) {
        if (null != budgetDetail && null != budgetDetail.getStatus()
                && null != budgetDetail.getStatus().getCode())
            if (FinancialConstants.BUDGETDETAIL_CREATED_STATUS.equals(budgetDetail.getStatus().getCode())
                    && budgetDetail.getState() != null && workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONFORWARD))
            	budgetDetail.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS));
            else if (workFlowAction.equals(FinancialConstants.BUTTONREJECT))
            	budgetDetail.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                        FinancialConstants.BUDGETDETAIL_REJECTED_STATUS));
            else if (FinancialConstants.BUDGETDETAIL_REJECTED_STATUS.equals(budgetDetail.getStatus().getCode())
                    && workFlowAction.equals(FinancialConstants.BUTTONCANCEL))
            	budgetDetail.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                        FinancialConstants.BUDGETDETAIL_CANCELLED_STATUS));
            else if (FinancialConstants.BUDGETDETAIL_REJECTED_STATUS.equals(budgetDetail.getStatus().getCode())
                    && workFlowAction.equals(FinancialConstants.BUTTONFORWARD))
            	budgetDetail.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                        FinancialConstants.BUDGETDETAIL_CREATED_STATUS));
            else if (FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS.equals(budgetDetail.getStatus().getCode())
                    && workFlowAction.equals(FinancialConstants.BUTTONAPPROVE))
            	budgetDetail.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                        FinancialConstants.BUDGETDETAIL_APPROVED_STATUS));

        return budgetDetail;
    }
    
    @RequestMapping(value = "/verifyCAONew/updateCAO", method = RequestMethod.POST)
    public String updateCAONew(@ModelAttribute("budgetUploadReport") final BudgetUploadReport budgetUploadReport, final BindingResult errors,
    		final HttpServletRequest request,final RedirectAttributes redirectAttrs,final Model model,@RequestParam final String workFlowAction) {
    	try {
    		System.out.println("updateCAONew");
    		Long approvalPosition = 0l;
            String approvalComment = "";
            String apporverDesignation = "";

            if (request.getParameter("approvalComent") != null)
                approvalComment = request.getParameter("approvalComent");

            if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));

            if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                    && request.getParameter(APPROVAL_POSITION) != null
                    && !request.getParameter(APPROVAL_POSITION).isEmpty())
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
            
           
        	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
        	{
        		approvalPosition =populatePosition();    		
        	}
            if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
                apporverDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
            System.out.println("Approval designation :: "+apporverDesignation);
    		List<BudgetDetail> budgetDetailsList2 = budgetUploadReport.getBudgetDetailsList();
    		System.out.println("Size  "+budgetDetailsList2.size());
			//String currentstate="";
    		Long ownerposition=0l;
			String ApproverName=budgetDetailService.getEmployeeName(approvalPosition);
			int i=1;
			for(BudgetDetail ss:budgetDetailsList2) {
				System.out.println("originalAmount "+ss.getOriginalAmount());
				System.out.println("antipacitryAmount "+ss.getAnticipatoryAmount());
				System.out.println("planningPercent "+ss.getPlanningPercent());
				System.out.println("onepercent "+ss.getQuarterpercent());
				System.out.println("ssId "+ss.getId());
				System.out.println("budgetUploadReport.getDeptCode() "+budgetUploadReport.getDeptCode());
				BudgetDetail budgetDetail = new BudgetDetail();
				BudgetDetail temp = (BudgetDetail) persistenceService.find("from BudgetDetail where id=?",Long.valueOf(ss.getId()));

                if (temp != null) {
                	temp.setOriginalAmount(ss.getOriginalAmount());
                	temp.setAnticipatoryAmount(ss.getAnticipatoryAmount());
                	temp.setPlanningPercent(ss.getPlanningPercent());
                	temp.setQuarterpercent(ss.getQuarterpercent());
                	temp.setQuartertwopercent(ss.getQuartertwopercent());
                	temp.setQuarterthreepercent(ss.getQuarterthreepercent());
                	temp.setQuarterfourpercent(ss.getQuarterfourpercent());
                	System.out.println("before status "+temp.getStatus().getCode());
                	temp=budgetDetailStatusChange(temp,workFlowAction);
                	if(i==budgetDetailsList2.size())
                	{
                		temp=budgetDetailService.transitionWorkFlow(temp,approvalPosition, approvalComment,workFlowAction, apporverDesignation);
                    	System.out.println("after state--> "+temp.getState().getOwnerPosition());
                    	ownerposition=temp.getState().getOwnerPosition();
                    	budgetDetailService.applyAuditingNew(temp.getState());
                	}
                	System.out.println("before status "+temp.getStatus().getCode());
                	budgetDetailService.applyAuditing(temp);
                	budgetDetail = budgetDetailService.update(temp);
                	i++;
                }
	    		
			}
			if(workFlowAction.equalsIgnoreCase("Reject"))
			{
				ApproverName=getEmployeeName(ownerposition);
			}
			System.out.println("approver name "+ApproverName);
    			model.addAttribute("approverName", ApproverName);
    			String message="";
    			if (FinancialConstants.BUTTONAPPROVE.equals(workFlowAction))
    	            message = "Budget Approved Successfully";
    	        else if (FinancialConstants.BUTTONREJECT.equals(workFlowAction))
    	            message = "Budget Rejected Successfully and sent back to " +ApproverName;
    	        else
    	            message = "Budget Verified Successfully and sent to " +ApproverName;

    	        model.addAttribute("message", message);

    		
    			
    			//final EgwStatus budgetDetailStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Verified");
    			
    			//redirectAttrs.addFlashAttribute("Budget Verified successful and sent to " +ApproverName);
			/*
			 * if(FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workFlowAction)) { Budget
			 * reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(),
			 * false); Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
			 * budgetService.updateByMaterializedPathSO(reBudget.getMaterializedPath());
			 * budgetDetailService.updateByMaterializedPathSO(reBudget.getMaterializedPath()
			 * ); budgetService.updateByMaterializedPathSO(beBudget.getMaterializedPath());
			 * budgetDetailService.updateByMaterializedPathSO(beBudget.getMaterializedPath()
			 * );
			 * //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.
			 * getMaterializedPath()); //redirectAttrs.addFlashAttribute("message",
			 * "msg.uploaded.budget.cao.Return"); } else
			 * if(FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workFlowAction)) { Budget
			 * reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(),
			 * false); Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
			 * budgetService.updateByMaterializedPathSTATUSCancelByCAO(reBudget.
			 * getMaterializedPath());
			 * budgetDetailService.updateByMaterializedPathSTATUSCancelByCAO(reBudget.
			 * getMaterializedPath());
			 * budgetService.updateByMaterializedPathSTATUSCancelByCAO(beBudget.
			 * getMaterializedPath());
			 * budgetDetailService.updateByMaterializedPathSTATUSCancelByCAO(beBudget.
			 * getMaterializedPath());
			 * //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.
			 * getMaterializedPath()); //redirectAttrs.addFlashAttribute("message",
			 * "msg.uploaded.budget.cancel"); } else { Budget reBudget =
			 * budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
			 * Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
			 * budgetService.updateByMaterializedPathCAO(reBudget.getMaterializedPath());
			 * budgetDetailService.updateByMaterializedPathCAO(reBudget.getMaterializedPath(
			 * ));
			 * budgetService.updateByMaterializedPathCAO(beBudget.getMaterializedPath());
			 * budgetDetailService.updateByMaterializedPathCAO(beBudget.getMaterializedPath(
			 * ));
			 * //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.
			 * getMaterializedPath()); //redirectAttrs.addFlashAttribute("message",
			 * "msg.uploaded.budget.cao.success");
			 * redirectAttrs.addFlashAttribute("Budget Verified successful and sent to "
			 * +ApproverName); }
			 */	
			
    	       

    	        
    		
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	 return "expensebill-success";
    }
    
   

    @RequestMapping(value = "/updateCAO", method = RequestMethod.POST)
    public String updateCAO(@ModelAttribute final BudgetUploadReport budgetUploadReport, final BindingResult errors,
            final RedirectAttributes redirectAttrs,@RequestParam final String workAction) {
    	try {
    	
    		//
    	if(workAction.equalsIgnoreCase("VERIFY"))
    	{ 
	        Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
	        budgetService.updateByMaterializedPathCAO(reBudget.getMaterializedPath());
	        budgetDetailService.updateByMaterializedPathCAO(reBudget.getMaterializedPath());
	        budgetService.updateByMaterializedPathCAO(beBudget.getMaterializedPath());
	        budgetDetailService.updateByMaterializedPathCAO(beBudget.getMaterializedPath());
	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
	        //redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.success");
    	}
    	else if(workAction.equalsIgnoreCase("REJECT"))
    	{
    		Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathSO(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSO(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathSO(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSO(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        //redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.Return");
    	}
    	else if(workAction.equalsIgnoreCase("CANCEL"))
    	{
    		Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathSTATUSCancelByCAO(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelByCAO(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathSTATUSCancelByCAO(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelByCAO(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        //redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cancel");

 	      
    		
    		
    	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();}
        return "redirect:/approvebudget/verifyCAO";
    	 
    }
    
    
    
    @RequestMapping(value = "/updateBudgetSO", method = RequestMethod.POST)
    public String updateBudgetSO(@ModelAttribute("budgetWrapper") BudgetWrapper budgetWrapper, final BindingResult errors,
            final RedirectAttributes redirectAttrs,@RequestParam final String workAction) {
    	
    	if(workAction.equalsIgnoreCase("VERIFY"))
    	{ 
    	List<BudgetDetail> budgetDetails=budgetWrapper.getBudgetDetails();
    	
    	
    	for (BudgetDetail budgetDetail : budgetDetails) {
    		BudgetDetail budgetDetailOb= budgetDetailService.findById(budgetDetail.getId(), false);
    		
    		budgetDetailOb.setOriginalAmount(budgetDetail.getOriginalAmount());
    		budgetDetailOb.setPlanningPercent(budgetDetail.getPlanningPercent());
    		budgetDetailOb.setQuarterpercent(budgetDetail.getQuarterpercent());
    		budgetDetailOb.setQuartertwopercent(budgetDetail.getQuartertwopercent());
    		budgetDetailOb.setQuarterthreepercent(budgetDetail.getQuarterthreepercent());
    		budgetDetailOb.setQuarterfourpercent(budgetDetail.getQuarterfourpercent());
    		budgetDetailService.update(budgetDetailOb);
		}
    	
    	
    		 Budget reBudgeto = budgetService.findById(budgetWrapper.getReBudget().getId(), false);
    	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudgeto);
    	        budgetService.updateByMaterializedPathForReVerify(reBudgeto.getMaterializedPath());
    	        budgetDetailService.updateByMaterializedPathForReVerify(reBudgeto.getMaterializedPath());
    	        budgetService.updateByMaterializedPathForReVerify(beBudget.getMaterializedPath());
    	        budgetDetailService.updateByMaterializedPathForReVerify(beBudget.getMaterializedPath());
    	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
    	
    	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.forward");
    	        
    	       
    	}
    	else if(workAction.equalsIgnoreCase("CANCEL"))
    	{
    		 Budget reBudget = budgetService.findById(budgetWrapper.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathSTATUSCancelBySO(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelBySO(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathSTATUSCancelBySO(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelBySO(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cancel");

    	
    	
    		
    	}
    	
    	  return "redirect:/approvebudget/rejectedBudgetSO";
    }
    
    
    @RequestMapping(value = "/updateACMC", method = RequestMethod.POST)
    public String updateACMC(@ModelAttribute final BudgetUploadReport budgetUploadReport, final BindingResult errors,
            final RedirectAttributes redirectAttrs,@RequestParam final String workAction) {
    	if(workAction.equalsIgnoreCase("VERIFY"))
    	{
        Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
        budgetService.updateByMaterializedPathACMC(reBudget.getMaterializedPath());
        budgetDetailService.updateByMaterializedPathACMC(reBudget.getMaterializedPath());
        budgetService.updateByMaterializedPathACMC(beBudget.getMaterializedPath());
        budgetDetailService.updateByMaterializedPathACMC(beBudget.getMaterializedPath());
        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.acmc.success");
    	}
    	else if(workAction.equalsIgnoreCase("REJECT"))
    	{
    		 Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathReturnByACMC(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathReturnByACMC(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathReturnByACMC(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathReturnByACMC(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.Return");

 	      
    		

    	}
    	else if(workAction.equalsIgnoreCase("CANCEL"))
    	{
    		 Budget reBudget = budgetService.findById(budgetUploadReport.getReBudget().getId(), false);
 	        Budget beBudget = budgetService.getReferenceBudgetFor(reBudget);
 	        budgetService.updateByMaterializedPathSTATUSCancelByACMC(reBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelByACMC(reBudget.getMaterializedPath());
 	        budgetService.updateByMaterializedPathSTATUSCancelByACMC(beBudget.getMaterializedPath());
 	        budgetDetailService.updateByMaterializedPathSTATUSCancelByACMC(beBudget.getMaterializedPath());
 	        //chartOfAccountsService.updateActiveForPostingByMaterializedPath(reBudget.getMaterializedPath());
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cancel");

    		
    	}
        return "redirect:/approvebudget/verifyACMC";
    }

    private Long populatePosition() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		pos=employs.get(0).getAssignments().get(0).getPosition();
    		
    	}
    	//System.out.println("pos-----populatePosition---()----------------------"+pos);
		return pos;
	}
    private String populateEmpName() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	String empName=null;
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		//pos=employs.get(0).getAssignments().get(0).getPosition();
    		empName=employs.get(0).getUser().getName();
    		
    	}
		return empName;
	}
    
    public String getEmployeeName(Long empId){
        
        return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }
    
}