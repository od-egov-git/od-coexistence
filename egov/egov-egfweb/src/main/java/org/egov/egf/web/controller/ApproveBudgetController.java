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
import java.util.List;

import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FundService;
import org.egov.dao.budget.BudgetDetailsDAO;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetUploadReport;
import org.egov.model.budget.BudgetWrapper;
import org.egov.model.repository.BudgetDetailRepository;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.budget.BudgetService;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/approvebudget")
public class ApproveBudgetController {
   
    private final static String APPROVEBUDGET_SEARCH = "approvebudget-search";
    private final static String APPROVEBUDGET_SEARCH_CAO = "approvebudget-searchCAO";
    private final static String APPROVEBUDGET_SEARCH_SO = "approvebudget-searchSO";
    private final static String APPROVEBUDGET_SEARCH_ACMC = "approvebudget-searchACMC";
    private List<BudgetDetail> budgetDetailsList=new ArrayList<BudgetDetail>();

    @Autowired
    @Qualifier("budgetService")
    private BudgetService budgetService;
    @Autowired
    @Qualifier("budgetDetailService")
    private BudgetDetailService budgetDetailService;
    
   
    
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
    private DepartmentService departmentService;

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
        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.success");

    	       
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
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cao.Return");

 	      
    		
    		
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
 	        redirectAttrs.addFlashAttribute("message", "msg.uploaded.budget.cancel");

 	      
    		
    		
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

}