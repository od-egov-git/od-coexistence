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
package org.egov.services.budget;

import org.apache.log4j.Logger;
import org.egov.commons.CFinancialYear;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.dao.budget.BudgetDetailsHibernateDAO;
import org.egov.egf.autonumber.BudgetReAppropriationSequenceNumberGenerator;
import org.egov.egf.model.BudgetReAppropriationView;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.GenericSequenceNumberGenerator;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infra.workflow.service.WorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetReAppropriation;
import org.egov.model.budget.BudgetReAppropriationMisc;
import org.egov.model.voucher.WorkflowBean;
import org.egov.pims.commons.Position;
import org.egov.utils.BudgetDetailConfig;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetReAppropriationService extends PersistenceService<BudgetReAppropriation, Long> {

    private static final Logger LOGGER = Logger.getLogger(BudgetReAppropriationService.class);

    WorkflowService<BudgetReAppropriationMisc> miscWorkflowService;
    @Autowired
    @Qualifier("budgetDetailService")
    private BudgetDetailService budgetDetailService;

    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<BudgetDetail> budgetDetailWorkflowService;
    @Autowired
    private BudgetDetailConfig budgetDetailConfig;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    private GenericSequenceNumberGenerator sequenceGenerator;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    @Autowired
    private BudgetDetailsHibernateDAO budgetDetailsDAO;
    @Autowired
    protected ScriptService scriptService;
    @Autowired
    @Qualifier("budgetReAppropriationMiscService")
    private BudgetReAppropriationMiscService budgetReAppropriationMiscService;

    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;

    @Autowired
    private AutonumberServiceBeanResolver beanResolver;
    
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    public MicroserviceUtils microserviceUtils;
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
    
    public BudgetReAppropriationService() {
        super(BudgetReAppropriation.class);
    }

    public BudgetReAppropriationService(final Class<BudgetReAppropriation> type) {
        super(type);
    }

    public GenericSequenceNumberGenerator getSequenceGenerator() {
        return sequenceGenerator;
    }

    public void setSequenceGenerator(final GenericSequenceNumberGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public SimpleWorkflowService<BudgetDetail> getBudgetDetailWorkflowService() {
		return budgetDetailWorkflowService;
	}

	public void setBudgetDetailWorkflowService(SimpleWorkflowService<BudgetDetail> budgetDetailWorkflowService) {
		this.budgetDetailWorkflowService = budgetDetailWorkflowService;
	}

	public void setBudgetDetailService(final BudgetDetailService budgetDetailService) {
        this.budgetDetailService = budgetDetailService;
    }

    public void setPersistenceService(final PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setMiscWorkflowService(final WorkflowService<BudgetReAppropriationMisc> miscWorkflowService) {
        this.miscWorkflowService = miscWorkflowService;
    }

    public boolean checkRowEmpty(final BudgetReAppropriationView appropriation) {
        if ((appropriation.getBudget() == null || appropriation.getBudget().getId() == 0)
                && (appropriation.getBudgetDetail().getBudgetGroup() == null || appropriation.getBudgetDetail().getBudgetGroup()
                        .getId() == 0)
                && isMandatoryGridFieldEmpty(appropriation))
            return true;
        return false;
    }

    @Transactional
    public BudgetDetail createApprovedBudgetDetail(final BudgetReAppropriationView appropriation, final Position position) {
        final BudgetDetail detail = new BudgetDetail();
        final BudgetDetail budgetDetail = appropriation.getBudgetDetail();
        detail.copyFrom(budgetDetail);
        final BudgetDetail savedBudgetDetail = budgetDetailService.createBudgetDetail(detail, position, persistenceService);
        budgetDetailService.applyAuditing(savedBudgetDetail);
        budgetDetailService.persist(savedBudgetDetail);
        // detail.transition().end().withOwner(position);
        return savedBudgetDetail;
    }

    public void validateMandatoryFields(final List<BudgetReAppropriationView> reAppropriationList) {
        for (final BudgetReAppropriationView entry : reAppropriationList) {
            entry.setBudgetDetail(setRelatedValues(entry.getBudgetDetail()));
            if (entry.getBudgetDetail().getBudgetGroup() == null || entry.getBudgetDetail().getBudgetGroup().getId() == 0L)
                throw new ValidationException(Arrays.asList(new ValidationError("budgetDetail.budgetGroup.mandatory",
                        "budgetDetail.budgetGroup.mandatory")));
            final Map<String, Object> valueMap = constructValueMap(entry.getBudgetDetail());
            budgetDetailConfig.checkHeaderMandatoryField(valueMap);
            budgetDetailConfig.checkGridMandatoryField(valueMap);
        }
    }

    public BudgetDetail setRelatedValues(final BudgetDetail detail) {
//        if (detail.getExecutingDepartment() != null && "".equals(detail.getExecutingDepartment()))
//            detail.setExecutingDepartment(null);
        if (detail.getFunction() != null && detail.getFunction().getId() == 0)
            detail.setFunction(null);
        if (detail.getScheme() != null && detail.getScheme().getId() == 0)
            detail.setScheme(null);
        if (detail.getSubScheme() != null && detail.getSubScheme().getId() == 0)
            detail.setSubScheme(null);
        if (detail.getFunctionary() != null && detail.getFunctionary().getId() == 0)
            detail.setFunctionary(null);
        if (detail.getBoundary() != null && detail.getBoundary().getId() == 0)
            detail.setBoundary(null);
        if (detail.getFund() != null && detail.getFund().getId() == 0)
            detail.setFund(null);
        return detail;
    }

    private Map<String, Object> constructValueMap(final BudgetDetail budgetDetail) {
        final Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(Constants.EXECUTING_DEPARTMENT, budgetDetail.getExecutingDepartment());
        valueMap.put(Constants.FUNCTION, budgetDetail.getFunction());
        valueMap.put(Constants.FUNCTIONARY, budgetDetail.getFunctionary());
        valueMap.put(Constants.SCHEME, budgetDetail.getScheme());
        valueMap.put(Constants.SUB_SCHEME, budgetDetail.getSubScheme());
        valueMap.put(Constants.BOUNDARY, budgetDetail.getBoundary());
        valueMap.put(Constants.FUND, budgetDetail.getFund());
        return valueMap;
    }

    private boolean isMandatoryGridFieldEmpty(final BudgetReAppropriationView appropriation) {
        for (final String entry : budgetDetailConfig.getGridFields()) {
            if (Constants.FUNCTION.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.FUNCTION)
                    && (appropriation.getBudgetDetail().getFunction() == null || appropriation.getBudgetDetail().getFunction()
                            .getId() == 0))
                return true;
            if (Constants.EXECUTING_DEPARTMENT.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.EXECUTING_DEPARTMENT)
                    && (appropriation.getBudgetDetail().getExecutingDepartment() == null || appropriation.getBudgetDetail()
                            .getExecutingDepartment().equals("")))
                return true;
            if (Constants.FUND.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.FUND)
                    && (appropriation.getBudgetDetail().getExecutingDepartment() == null || appropriation.getBudgetDetail()
                            .getExecutingDepartment().equals("")))
                return true;
            if (Constants.SCHEME.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.SCHEME)
                    && (appropriation.getBudgetDetail().getScheme() == null || appropriation.getBudgetDetail().getScheme()
                            .getId() == 0))
                return true;
            if (Constants.SUB_SCHEME.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.SUB_SCHEME)
                    && (appropriation.getBudgetDetail().getSubScheme() == null || appropriation.getBudgetDetail().getSubScheme()
                            .getId() == 0))
                return true;
            if (Constants.BOUNDARY.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.BOUNDARY)
                    && (appropriation.getBudgetDetail().getBoundary() == null || appropriation.getBudgetDetail().getBoundary()
                            .getBndryId() == 0))
                return true;
            if (Constants.FUNCTIONARY.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.FUNCTIONARY)
                    && (appropriation.getBudgetDetail().getFunctionary() == null || appropriation.getBudgetDetail()
                            .getFunctionary().getId() == 0))
                return true;
            if (Constants.FUND.equalsIgnoreCase(entry)
                    && budgetDetailConfig.getMandatoryFields().contains(Constants.FUND)
                    && (appropriation.getBudgetDetail().getFund() == null || appropriation.getBudgetDetail().getFund().getId() == 0))
                return true;
        }
        return false;
    }

    public boolean rowsToAddExists(final List<BudgetReAppropriationView> reAppropriationList) {
        for (final BudgetReAppropriationView budgetReAppropriationView : reAppropriationList) {
            if (checkRowEmpty(budgetReAppropriationView))
                return false;
            final BudgetDetail budgetDetail = budgetReAppropriationView.getBudgetDetail();
            setRelatedValues(budgetDetail);
            if (budgetDetail.getBudgetGroup() != null && budgetDetail.getBudgetGroup().getId() == 0)
                budgetDetail.setBudgetGroup(null);
            if (!checkRowEmpty(budgetReAppropriationView))
                return true;
        }
        return false;
    }

    public void validateDuplicates(final List<BudgetReAppropriationView> budgetReAppropriationList,
            final BudgetReAppropriationView appropriation) {
        for (final BudgetReAppropriationView budgetReAppropriationView : budgetReAppropriationList)
            if (appropriation.getBudgetDetail().compareTo(budgetReAppropriationView.getBudgetDetail()))
                throw new ValidationException(
                        Arrays.asList(new ValidationError("reApp.duplicate.entry", "reApp.duplicate.entry")));
    }

    public boolean rowsToAddForExistingDetails(final List<BudgetReAppropriationView> reAppropriationList) {
        for (final BudgetReAppropriationView budgetReAppropriationView : reAppropriationList) {
            final BudgetDetail budgetDetail = budgetReAppropriationView.getBudgetDetail();
            setRelatedValues(budgetDetail);
            if (budgetDetail.getBudgetGroup() != null && budgetDetail.getBudgetGroup().getId() == 0)
                budgetDetail.setBudgetGroup(null);
            if (!(budgetReAppropriationView.getBudgetDetail().getBudgetGroup() == null && isMandatoryGridFieldEmpty(budgetReAppropriationView)))
                return true;
        }
        return false;
    }

    /**
     * This api checks whether the amount being deducted is greater than the budget available. If it is greater, a validation
     * exception is thrown.
     * @param reAppropriation - The budget reappropriation being created.(This could be the addition or the deduction
     * reappropriation)
     * @return
     */
    public void validateDeductionAmount(final BudgetReAppropriation appropriation) {
        BigDecimal multiplicationFactor;
        if (appropriation.getBudgetDetail().getPlanningPercent() != null) {
            multiplicationFactor = appropriation.getBudgetDetail().getPlanningPercent()
                    .divide(new BigDecimal(String.valueOf(100)));
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Calculating multiplicationFactor from PlanningPercent : " + multiplicationFactor);
        } else {
            multiplicationFactor = new BigDecimal(Double.parseDouble(getAppConfigFor("EGF",
                    "planning_budget_multiplication_factor")));
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("MultiplicationFactor from AppConfig(planning_budget_multiplication_factor) : "
                        + multiplicationFactor);
        }
        final BigDecimal deductionAmount = appropriation.getOriginalDeductionAmount();
        if (deductionAmount != null && BigDecimal.ZERO.compareTo(deductionAmount) == -1)
            if (deductionAmount.compareTo(appropriation.getBudgetDetail().getBudgetAvailable().divide(multiplicationFactor)) == 1
                    || !canDeduct(appropriation))
                throw new ValidationException(Arrays.asList(new ValidationError("budget.deduction.greater.than.available",
                        "budget.deduction.greater.than.available")));
    }

    // checks if the deduction amount is greater than the available amount(i.e, approved-actuals)
    private boolean canDeduct(final BudgetReAppropriation appropriation) {
        if (appropriation == null || appropriation.getOriginalDeductionAmount() == null
                || BigDecimal.ZERO.compareTo(appropriation.getOriginalDeductionAmount()) == 0)
            return true;
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        final BudgetDetail budgetDetail = appropriation.getBudgetDetail();
        if (budgetDetail.getFund() != null && budgetDetail.getFund().getId() != null)
            paramMap.put("fundid", budgetDetail.getFund().getId());
        if (budgetDetail.getExecutingDepartment() != null)
            paramMap.put("deptid", budgetDetail.getExecutingDepartment());
        if (budgetDetail.getFunction() != null && budgetDetail.getFunction().getId() != null)
            paramMap.put("functionid", budgetDetail.getFunction().getId());
        if (budgetDetail.getFunctionary() != null && budgetDetail.getFunctionary().getId() != null)
            paramMap.put("functionaryid", budgetDetail.getFunctionary().getId());
        if (budgetDetail.getScheme() != null && budgetDetail.getScheme().getId() != null)
            paramMap.put("schemeid", budgetDetail.getScheme().getId());
        if (budgetDetail.getSubScheme() != null && budgetDetail.getSubScheme().getId() != null)
            paramMap.put("subschemeid", budgetDetail.getSubScheme().getId());
        if (budgetDetail.getBoundary() != null && budgetDetail.getBoundary().getId() != null)
            paramMap.put("boundaryid", budgetDetail.getBoundary().getId());
        paramMap.put("budgetheadid", budgetDetail.getBudgetGroup().getId());
        paramMap.put("glcodeid", budgetDetail.getBudgetGroup().getMinCode().getId());
        paramMap.put(Constants.ASONDATE, appropriation.getAsOnDate());
        final BigDecimal actualBudgetUtilized = budgetDetailsDAO.getActualBudgetUtilized(paramMap);
        final BigDecimal billAmount = budgetDetailsDAO.getBillAmountForBudgetCheck(paramMap);
        BigDecimal approvedAmount = appropriation.getBudgetDetail().getApprovedAmount();
        approvedAmount = approvedAmount == null ? BigDecimal.ZERO : approvedAmount;
        BigDecimal reAppropriationsTotal = budgetDetail.getApprovedReAppropriationsTotal();
        reAppropriationsTotal = reAppropriationsTotal == null ? BigDecimal.ZERO : reAppropriationsTotal;
        approvedAmount = approvedAmount.add(reAppropriationsTotal);
        // if(LOGGER.isInfoEnabled())
        // LOGGER.info("*****************RIGHT side>"+approvedAmount.subtract(actualBudgetUtilized==null?BigDecimal.ZERO:actualBudgetUtilized).subtract(
        // billAmount==null?BigDecimal.ZERO:billAmount));
        if (appropriation.getOriginalDeductionAmount().compareTo(
                approvedAmount.subtract(actualBudgetUtilized == null ? BigDecimal.ZERO : actualBudgetUtilized).subtract(
                        billAmount == null ? BigDecimal.ZERO : billAmount)) > 0)
            return false;
        return true;
    }

    @Transactional
    public BudgetReAppropriationMisc createReAppropriationMisc(final String actionName,
            final BudgetReAppropriationMisc appropriationMisc,
            final BudgetDetail detail, final Position position) {
        BudgetReAppropriationMisc misc = new BudgetReAppropriationMisc();
        misc.setReAppropriationDate(appropriationMisc.getReAppropriationDate());
        misc.setRemarks(appropriationMisc.getRemarks());
        misc.setSequenceNumber(getSequenceNumber(detail));
        misc.setStatus(egwStatusDAO.getStatusByModuleAndCode("REAPPROPRIATIONMISC", "Approved"));
        budgetReAppropriationMiscService.applyAuditing(misc);
        budgetReAppropriationMiscService.persist(misc);
        /*
         * misc = (BudgetReAppropriationMisc) misc.start().withOwner(position); miscWorkflowService.transition(actionName, misc,
         * misc.getRemarks());
         */
        return misc;
    }
    @Transactional
    public BudgetReAppropriationMisc createReAppropriationMiscNew(final String actionName,
            final BudgetReAppropriationMisc appropriationMisc, final BudgetDetail detail, WorkflowBean workflowBean) {
        BudgetReAppropriationMisc misc = new BudgetReAppropriationMisc();
        misc.setReAppropriationDate(appropriationMisc.getReAppropriationDate());
        misc.setRemarks(appropriationMisc.getRemarks());
        misc.setSequenceNumber(getSequenceNumber(detail));
        misc.setCreatedDate(new Date());
        misc.setStatus(egwStatusDAO.getStatusByModuleAndCode("REAPPROPRIATIONMISC", "Created"));//change from Approved 
        //misc = transitionWorkFlow(misc, workflowBean);
        //budgetReAppropriationMiscService.applyAuditingNew(misc.getState());
        budgetReAppropriationMiscService.applyAuditing(misc);
        budgetReAppropriationMiscService.persist(misc);
        
        return misc;
    }
    
    @Transactional
    public BudgetReAppropriation transitionWorkFlow(final BudgetReAppropriation appropriation, WorkflowBean workflowBean) {
        final DateTime currentDate = new DateTime();
        final User user = securityUtils.getCurrentUser();
        String stateValue = "";
        Designation designation = this.getDesignationDetails(this.getEmployeeDesignation(workflowBean.getApproverPositionId()));
        EmployeeInfo info = null;
        
        if (user != null && user.getId() != null)
            info = microserviceUtils.getEmployeeById(user.getId());

        if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
            int size=appropriation.getStateHistory().size();//added abhishek on 12042021
			Position owenrPosName = new Position();
			Position owenrPos = new Position();
	        owenrPos.setId(workflowBean.getApproverPositionId());
			owenrPosName.setId(workflowBean.getApproverPositionId());
			HashMap<Long, Long> positionmap = new HashMap<>();
			for(int i=0;i<size;i++)
			{
				positionmap.put(appropriation.getStateHistory().get(i).getOwnerPosition(),
						appropriation.getStateHistory().get(i).getPreviousownerposition());
			}
			if(size>0)
			{
				Long owenrPos1=0l;
				if(positionmap.containsKey(user.getId()))
					owenrPos1=positionmap.get(user.getId());
				else
					owenrPos1=(long) appropriation.getStateHistory().get(size-1).getOwnerPosition();
				
				if(owenrPos1==null|| owenrPos1.equals(""))
					owenrPos1=(long) appropriation.getCreatedBy();
				owenrPosName.setId(owenrPos1);
				System.out.println("Budget owner position "+owenrPos1);
				owenrPos.setId(owenrPos1);
			}
			else
			{
				owenrPos.setId(appropriation.getState().getCreatedBy());
				owenrPosName.setId(appropriation.getState().getCreatedBy());
			}
            System.out.println("ownerPostion id- "+owenrPos);
            System.out.println("ownerPostion Nameid- "+owenrPosName);
			
            appropriation.transition().progressWithStateCopy().withSenderName(user.getName())
                    .withComments(workflowBean.getApproverComments())
                    //.withStateValue(stateValue).withDateInfo(currentDate.toDate())
                    //.withOwner(budgetDetail.getState().getInitiatorPosition()).withOwnerName((budgetDetail.getState().getInitiatorPosition() != null && budgetDetail.getState().getInitiatorPosition() > 0L) ? getEmployeeName(budgetDetail.getState().getInitiatorPosition()):"")
                    .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPosName).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                    //.withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
                    .withNextAction("");

        } else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
        	System.out.println("appropriation.getStateType() "+appropriation.getStateType());
        	System.out.println("appropriation.getCurrentState().getValue() "+appropriation.getCurrentState().getValue());
            final WorkFlowMatrix wfmatrix = budgetDetailWorkflowService.getWfMatrix(appropriation.getStateType(), null,
                    null, null, appropriation.getCurrentState().getValue(), null);
            Position pos=new Position();
            pos.setId(user.getId());
            appropriation.transition().end().withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withStateValue(wfmatrix.getCurrentDesignation() + " Approved").withDateInfo(currentDate.toDate())
                    .withOwner(pos)/*withOwner((info != null && info.getAssignments() != null && !info.getAssignments().isEmpty())
                            ? info.getAssignments().get(0).getPosition() : null)*/
                    .withNextAction(wfmatrix.getNextAction());

            appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                    FinancialConstants.BUDGETDETAIL_APPROVED_STATUS));
        } else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
        	appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                    FinancialConstants.BUDGETDETAIL_CANCELLED_STATUS));
            appropriation.transition().end().withStateValue(FinancialConstants.WORKFLOW_STATE_CANCELLED)
                    .withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withDateInfo(currentDate.toDate());
        } else {
        	
            if (null == appropriation.getState()) {
            	System.out.println("budgetDetail.getStateType() "+appropriation.getStateType());
            	System.out.println("workflowBean.getCurrentState() "+workflowBean.getCurrentState());
            	final WorkFlowMatrix wfmatrix = budgetDetailWorkflowService.getWfMatrix(appropriation.getStateType(), null,
                        null, null, workflowBean.getCurrentState(), null);
                if (stateValue.isEmpty())
                {
                	if(!wfmatrix.getNextState().equalsIgnoreCase(FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING) && !wfmatrix.getNextState().equalsIgnoreCase("NEW"))
                	{
                		stateValue = wfmatrix.getNextState();//+ " "+designation.getName().toUpperCase();
                	}
                	else if(wfmatrix.getNextState().equalsIgnoreCase("NEW"))
                	{
                		stateValue = "Pending With "+ designation.getName().toUpperCase();
                	}
                	else
                	{
                		stateValue = wfmatrix.getNextState();
                	}
                    
                }
                if ("Save As Draft".equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
                	stateValue =FinancialConstants.WORKFLOW_STATE_SAVEASDRAFT;
                		//budgetDetail.setStatus(6);
                }
               
                System.out.println("::::::::"+workflowBean.getApproverPositionId());
                
                appropriation.transition().start().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                       // .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate())
                        .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                        .withNatureOfTask(FinancialConstants.BUDGETDETAILAPP)
                        .withOwner(workflowBean.getApproverPositionId()).withOwnerName((workflowBean.getApproverPositionId() != null && workflowBean.getApproverPositionId() > 0L) ? getEmployeeName(workflowBean.getApproverPositionId()):"")
                        .withNextAction(wfmatrix.getNextAction())
                        .withCreatedDate(new Date())
                        .withCreatedBy(user.getId())
                        .withtLastModifiedBy(user.getId());
                appropriationStatusChange(appropriation, workflowBean.getWorkFlowAction());
            } else if (appropriation.getCurrentState().getNextAction().equalsIgnoreCase("END"))
                appropriation.transition().end().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withDateInfo(currentDate.toDate());
            else {
				final WorkFlowMatrix wfmatrix = budgetDetailWorkflowService.getWfMatrix(appropriation.getStateType(), null,
                        null, null,appropriation.getCurrentState().getValue(), null);
                String ststeValue=wfmatrix.getNextState();
                Long owner = workflowBean.getApproverPositionId();
                if ("Save As Draft".equalsIgnoreCase(workflowBean.getWorkFlowAction()))
                {
                		ststeValue =FinancialConstants.WORKFLOW_STATE_SAVEASDRAFT;
                		owner = populatePosition();
                }
                if(appropriation.getState().getValue().equalsIgnoreCase("Rejected")) 
                {
	                HashMap<Long, String> positionmap = new HashMap<>();
	                HashMap<Long, String> positionmap1 = new HashMap<>();
	                int size=appropriation.getStateHistory().size();
	    			for(int i=0;i<size;i++)
	    			{
	    				positionmap.put(appropriation.getStateHistory().get(i).getLastModifiedBy(),
	    						appropriation.getStateHistory().get(i).getValue());
	    				positionmap1.put(appropriation.getStateHistory().get(i).getLastModifiedBy(),
	    						appropriation.getStateHistory().get(i).getNextAction());
	    			}
	    			if(positionmap.containsKey(user.getId()))
	    			{
	    				ststeValue=positionmap.get(user.getId());
	    				wfmatrix.setNextAction(positionmap1.get(user.getId()));
	    			}
                }
                appropriation.transition().progressWithStateCopy().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withStateValue(ststeValue).withDateInfo(currentDate.toDate())
                        .withOwner(owner).withOwnerName((owner != null && owner > 0L) ? getEmployeeName(owner):"")
                        .withNextAction(wfmatrix.getNextAction());
                appropriationStatusChange(appropriation, workflowBean.getWorkFlowAction());
            }
        }
        return appropriation;
    }
    
    public void appropriationStatusChange(final BudgetReAppropriation appropriation, final String workFlowAction) {
        if (null == appropriation.getStatus())
        {
        	appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                    FinancialConstants.BUDGETDETAIL_CREATED_STATUS));
        }
        else {
            if (FinancialConstants.BUDGETDETAIL_APPROVED_STATUS.equals(appropriation.getStatus().getCode())
                    && appropriation.getState() != null && workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONAPPROVE))
            	appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                        FinancialConstants.BUDGETDETAIL_APPROVED_STATUS));
            else if (workFlowAction.equals(FinancialConstants.BUTTONREJECT))
            	appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                        FinancialConstants.BUDGETDETAIL_REJECTED_STATUS));
            else if (FinancialConstants.BUDGETDETAIL_REJECTED_STATUS.equals(appropriation.getStatus().getCode())
                    && workFlowAction.equals(FinancialConstants.BUTTONCANCEL))
            	appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                        FinancialConstants.BUDGETDETAIL_CANCELLED_STATUS));
            else if (FinancialConstants.BUDGETDETAIL_CREATED_STATUS.equals(appropriation.getStatus().getCode())
                    && workFlowAction.equals(FinancialConstants.BUTTONFORWARD))
            	appropriation.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAILAPP,
                        FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS));
        }
    }
    
    
    protected String getSequenceNumber(final BudgetDetail detail) {
        BudgetReAppropriationSequenceNumberGenerator b = beanResolver.getAutoNumberServiceFor(BudgetReAppropriationSequenceNumberGenerator.class);

        final String sequenceNumber = b.getNextNumber(detail);
        final ScriptContext scriptContext = ScriptService.createContext("wfItem", detail, "sequenceGenerator", sequenceGenerator);
        return sequenceNumber;
    }

    public BudgetReAppropriation findBySequenceNumberAndBudgetDetail(final String sequenceNumber, final Long budgetDetailId) {
        return (BudgetReAppropriation) persistenceService.find(
                "from BudgetReAppropriation b where b.reAppropriationMisc.sequenceNumber=? and b.budgetDetail.id=?",
                sequenceNumber, budgetDetailId);
    }
    
    public BudgetReAppropriation findByBudgetDetail(final Long budgetDetailId) {
        return (BudgetReAppropriation) persistenceService.find(
                "from BudgetReAppropriation b where b.id=?", budgetDetailId);
    }

    public BudgetReAppropriationMisc performActionOnMisc(final String action, final BudgetReAppropriationMisc reApp,
            final String comment) {
        final BudgetReAppropriationMisc misc = miscWorkflowService.transition(action, reApp, comment);
        getSession().flush();
        return misc;
    }

    /**
     * This api updates the budget available amount for which the budget reappropriation is being done. The budget available is
     * calculated as, budget available = budget available + (additional amount * multiplication factor) for addition and budget
     * available = budget available - (deduction amount * multiplication factor) for deduction
     * @param reAppropriation - The budget reappropriation being created.(This could be the addition or the deduction
     * reappropriation)
     * @return
     */
    @Transactional
    public void updatePlanningBudget(final BudgetReAppropriation reAppropriation) {
        getSession().flush();
        // BigDecimal multiplicationFactor = new
        // BigDecimal(Double.parseDouble(getAppConfigFor("EGF","planning_budget_multiplication_factor")));
        final BudgetDetail budgetDetail = budgetDetailService.find("from BudgetDetail where id=?", reAppropriation
                .getBudgetDetail().getId());
        BigDecimal budgetAvailable = budgetDetail.getBudgetAvailable() == null ? BigDecimal.ZERO : budgetDetail
                .getBudgetAvailable();
        BigDecimal Budgetapproved = BigDecimal.ZERO;
        BigDecimal planningBudgetApproved = BigDecimal.ZERO;
        BigDecimal planningBudgetUsage = BigDecimal.ZERO;
        // budgetAvailable= (be+addrelease)*planingpercent - consumption

        Budgetapproved = budgetDetail.getApprovedAmount().add(budgetDetail.getApprovedReAppropriationsTotal());

        if (reAppropriation.getAdditionAmount() != null && reAppropriation.getAdditionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.add(reAppropriation.getAdditionAmount());
        else if (reAppropriation.getDeductionAmount() != null
                && reAppropriation.getDeductionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.subtract(reAppropriation.getDeductionAmount());
        if (budgetDetail.getPlanningPercent() == null)
            planningBudgetApproved = Budgetapproved;
        else
            planningBudgetApproved = Budgetapproved.multiply(budgetDetail.getPlanningPercent()).divide(new BigDecimal(String
                    .valueOf(100)));

        planningBudgetUsage = budgetDetailsDAO.getPlanningBudgetUsage(budgetDetail);
        budgetAvailable = planningBudgetApproved.subtract(planningBudgetUsage);
        budgetDetail.setBudgetAvailable(budgetAvailable);
        EgwStatus status =  egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved");
        budgetDetail.setStatus(status);
        budgetDetailService.update(budgetDetail);
        getSession().flush();
    }
    
    @Transactional
    public void updatePlanningBudgetCao(final BudgetReAppropriation reAppropriation) {
        getSession().flush();
        // BigDecimal multiplicationFactor = new
        // BigDecimal(Double.parseDouble(getAppConfigFor("EGF","planning_budget_multiplication_factor")));
        System.out.println("B1");
        /*final BudgetDetail budgetDetail = budgetDetailService.find("from BudgetDetail where id=?", reAppropriation
                .getBudgetDetail().getId());
        BigDecimal budgetAvailable = budgetDetail.getBudgetAvailable() == null ? BigDecimal.ZERO : budgetDetail
                .getBudgetAvailable();
        BigDecimal Budgetapproved = BigDecimal.ZERO;
        BigDecimal planningBudgetApproved = BigDecimal.ZERO;
        BigDecimal planningBudgetUsage = BigDecimal.ZERO;
        // budgetAvailable= (be+addrelease)*planingpercent - consumption

        Budgetapproved = budgetDetail.getApprovedAmount().add(budgetDetail.getApprovedReAppropriationsTotal());
        System.out.println("B2");
        if (reAppropriation.getAdditionAmount() != null && reAppropriation.getAdditionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.add(reAppropriation.getAdditionAmount());
        else if (reAppropriation.getDeductionAmount() != null
                && reAppropriation.getDeductionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.subtract(reAppropriation.getDeductionAmount());
        if (budgetDetail.getPlanningPercent() == null)
            planningBudgetApproved = Budgetapproved;
        else
            planningBudgetApproved = Budgetapproved.multiply(budgetDetail.getPlanningPercent()).divide(new BigDecimal(String
                    .valueOf(100)));
        System.out.println("B");
        planningBudgetUsage = budgetDetailsDAO.getPlanningBudgetUsage(budgetDetail);
        budgetAvailable = planningBudgetApproved.subtract(planningBudgetUsage);
        budgetDetail.setBudgetAvailable(budgetAvailable);*/

        List<BudgetDetail> detailList = budgetDetailService.getBudgetDetailsForReAppCao();
       
       if(detailList != null && !detailList.isEmpty())
       {
    	   for(BudgetReAppropriation row:detailList.get(0).getBudgetReAppropriations())
           {
        	   //if(row.getStatus().getCode().equalsIgnoreCase("New"))
        	   //{
        		   EgwStatus status =  egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Approved");
        		   row.setStatus(status);
        		   
        		   applyAuditing(row);
        	        persist(row);
        		   
        	   //}
           }
       }
       
       //budgetReAppropriations.iterator().next()
        
        for(BudgetDetail bd:detailList)
        {
        	EgwStatus status =  egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "REAPP ACMC");
            bd.setStatus(status);
            budgetDetailService.update(bd);
            getSession().flush();
        }
        
        
    }
    
    @Transactional
    public void updatePlanningBudgetAcmc(final BudgetReAppropriation reAppropriation) {
        getSession().flush();
        // BigDecimal multiplicationFactor = new
        // BigDecimal(Double.parseDouble(getAppConfigFor("EGF","planning_budget_multiplication_factor")));
        /*final BudgetDetail budgetDetail = budgetDetailService.find("from BudgetDetail where id=?", reAppropriation
                .getBudgetDetail().getId());
        BigDecimal budgetAvailable = budgetDetail.getBudgetAvailable() == null ? BigDecimal.ZERO : budgetDetail
                .getBudgetAvailable();
        BigDecimal Budgetapproved = BigDecimal.ZERO;
        BigDecimal planningBudgetApproved = BigDecimal.ZERO;
        BigDecimal planningBudgetUsage = BigDecimal.ZERO;
        // budgetAvailable= (be+addrelease)*planingpercent - consumption

        Budgetapproved = budgetDetail.getApprovedAmount().add(budgetDetail.getApprovedReAppropriationsTotal());

        if (reAppropriation.getAdditionAmount() != null && reAppropriation.getAdditionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.add(reAppropriation.getAdditionAmount());
        else if (reAppropriation.getDeductionAmount() != null
                && reAppropriation.getDeductionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.subtract(reAppropriation.getDeductionAmount());
        if (budgetDetail.getPlanningPercent() == null)
            planningBudgetApproved = Budgetapproved;
        else
            planningBudgetApproved = Budgetapproved.multiply(budgetDetail.getPlanningPercent()).divide(new BigDecimal(String
                    .valueOf(100)));

        planningBudgetUsage = budgetDetailsDAO.getPlanningBudgetUsage(budgetDetail);
        budgetAvailable = planningBudgetApproved.subtract(planningBudgetUsage);
        budgetDetail.setBudgetAvailable(budgetAvailable);*/
        List<BudgetDetail> detailList = budgetDetailService.getBudgetDetailsForReAppAcmc();
        for(BudgetDetail bd : detailList)
        {
        	EgwStatus status =  egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "REAPP MC");
            bd.setStatus(status);
            budgetDetailService.update(bd);
            getSession().flush();
        }
        
    }
    
    @Transactional
    public void updatePlanningBudgetMc(final BudgetReAppropriation reAppropriation) {
        getSession().flush();
        // BigDecimal multiplicationFactor = new
        // BigDecimal(Double.parseDouble(getAppConfigFor("EGF","planning_budget_multiplication_factor")));
        /*final BudgetDetail budgetDetail = budgetDetailService.find("from BudgetDetail where id=?", reAppropriation
                .getBudgetDetail().getId());
        BigDecimal budgetAvailable = budgetDetail.getBudgetAvailable() == null ? BigDecimal.ZERO : budgetDetail
                .getBudgetAvailable();
        BigDecimal Budgetapproved = BigDecimal.ZERO;
        BigDecimal planningBudgetApproved = BigDecimal.ZERO;
        BigDecimal planningBudgetUsage = BigDecimal.ZERO;
        // budgetAvailable= (be+addrelease)*planingpercent - consumption

        Budgetapproved = budgetDetail.getApprovedAmount().add(budgetDetail.getApprovedReAppropriationsTotal());

        if (reAppropriation.getAdditionAmount() != null && reAppropriation.getAdditionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.add(reAppropriation.getAdditionAmount());
        else if (reAppropriation.getDeductionAmount() != null
                && reAppropriation.getDeductionAmount().compareTo(BigDecimal.ZERO) == 1)
            Budgetapproved = Budgetapproved.subtract(reAppropriation.getDeductionAmount());
        if (budgetDetail.getPlanningPercent() == null)
            planningBudgetApproved = Budgetapproved;
        else
            planningBudgetApproved = Budgetapproved.multiply(budgetDetail.getPlanningPercent()).divide(new BigDecimal(String
                    .valueOf(100)));

        planningBudgetUsage = budgetDetailsDAO.getPlanningBudgetUsage(budgetDetail);
        budgetAvailable = planningBudgetApproved.subtract(planningBudgetUsage);
        budgetDetail.setBudgetAvailable(budgetAvailable);*/
        List<BudgetDetail> detailList = budgetDetailService.getBudgetDetailsForReAppMc();
        for(BudgetDetail bd:detailList)
        {
        	EgwStatus status =  egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved");
            bd.setStatus(status);
            budgetDetailService.update(bd);
            getSession().flush();
        }
        
    }

    protected BigDecimal zeroOrValue(final BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String getAppConfigFor(final String module, final String key) {
        try {
            final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(module, key);
            return list.get(0).getValue().toString();
        } catch (final Exception e) {
            throw new ValidationException(Arrays.asList(new ValidationError(key + " not defined in appconfig", key
                    + " not defined in appconfig")));
        }
    }

    public List<BudgetReAppropriation> getNonApprovedReAppByUser(final Long userId, final BudgetDetail budgetDetail,
            final CFinancialYear financialYear) {
        StringBuffer query = new StringBuffer();
        query.append("from BudgetReAppropriation where state.value='NEW' and createdBy.id=" + userId
                + " and budgetDetail.budget.financialYear.id=" + financialYear.getId());
        if (budgetDetail.getExecutingDepartment() != null
                && budgetDetail.getExecutingDepartment().equals(""))
            query.append(" and budgetDetail.executingDepartment=" + budgetDetail.getExecutingDepartment());
        if (budgetDetail.getFund() != null && budgetDetail.getFund().getId() != null && budgetDetail.getFund().getId() != 0)
            query.append(" and budgetDetail.fund.id=" + budgetDetail.getFund().getId());
        if (budgetDetail.getFunction() != null && budgetDetail.getFunction().getId() != null
                && budgetDetail.getFunction().getId() != 0)
            query.append(" and budgetDetail.function.id=" + budgetDetail.getFunction().getId());
        if (budgetDetail.getFunctionary() != null && budgetDetail.getFunctionary().getId() != null
                && budgetDetail.getFunctionary().getId() != 0)
            query.append(" and budgetDetail.functionary.id=" + budgetDetail.getFunctionary().getId());
        if (budgetDetail.getScheme() != null && budgetDetail.getScheme().getId() != null && budgetDetail.getScheme().getId() != 0)
            query.append(" and budgetDetail.scheme.id=" + budgetDetail.getScheme().getId());
        if (budgetDetail.getSubScheme() != null && budgetDetail.getSubScheme().getId() != null
                && budgetDetail.getSubScheme().getId() != 0)
            query.append(" and budgetDetail.subScheme.id=" + budgetDetail.getSubScheme().getId());
        if (budgetDetail.getBoundary() != null && budgetDetail.getBoundary().getId() != null
                && budgetDetail.getBoundary().getId() != 0)
            query.append(" and budgetDetail.boundary.id=" + budgetDetail.getBoundary().getId());
        query = query.append(" order by budgetDetail.budgetGroup ");
        return findAllBy(query.toString());
    }

    public void setBudgetDetailsDAO(final BudgetDetailsHibernateDAO budgetDetailsDAO) {
        this.budgetDetailsDAO = budgetDetailsDAO;
    }

    @Transactional
    public boolean createReAppropriation(final String actionName,
            final List<BudgetReAppropriationView> budgetReAppropriationList,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        try {
        	System.out.println("Inside create");
            if (budgetReAppropriationList.isEmpty()
                    || !rowsToAddForExistingDetails(budgetReAppropriationList))
                return false;
            validateMandatoryFields(budgetReAppropriationList);
            System.out.println("After validate");
            final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
            for (final BudgetReAppropriationView appropriation : budgetReAppropriationList) {
                validateDuplicates(addedList, appropriation);
                System.out.println("Loop 1");
                saveAndStartWorkFlowForExistingdetails(actionName, appropriation, position, financialYear, beRe, misc, asOnDate);
                System.out.println("Loop 2");
                addedList.add(appropriation);
            }
        } catch (final ValidationException e)
        {
        	System.out.println("issueV "+e.getMessage());
        	e.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
        	System.out.println("issueE "+e.getMessage());
        	e.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        System.out.println("End");
        return true;
    }
    
    @Transactional
    public boolean createReAppropriationNew(final String actionName,
            final List<BudgetReAppropriationView> budgetReAppropriationList,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate,WorkflowBean workflowBean) {
        try {
        	System.out.println("Inside create");
            if (budgetReAppropriationList.isEmpty()
                    || !rowsToAddForExistingDetails(budgetReAppropriationList))
                return false;
            validateMandatoryFields(budgetReAppropriationList);
            System.out.println("After validate");
            final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
            for (final BudgetReAppropriationView appropriation : budgetReAppropriationList) {
                validateDuplicates(addedList, appropriation);
                System.out.println("Loop 1");
                saveAndStartWorkFlowForExistingdetailsNew(actionName, appropriation, position, financialYear, beRe, misc, asOnDate,workflowBean);
                System.out.println("Loop 2");
                addedList.add(appropriation);
            }
        } catch (final ValidationException e)
        {
        	System.out.println("issueV "+e.getMessage());
        	e.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
        	System.out.println("issueE "+e.getMessage());
        	e.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        System.out.println("End");
        return true;
    }
    @Transactional
    public boolean createReAppropriationCaoNew(final String actionName,final BudgetReAppropriation reApp,
            final List<BudgetReAppropriationView> budgetReAppropriationList,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate,WorkflowBean workflowBean,BudgetDetail tempBudgetDetail) {
        try {
            if (budgetReAppropriationList.isEmpty()
                    || !rowsToAddForExistingDetails(budgetReAppropriationList))
                return false;
            validateMandatoryFields(budgetReAppropriationList);
            System.out.println("CAO11");
            final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
            for (final BudgetReAppropriationView appropriation : budgetReAppropriationList) {
                validateDuplicates(addedList, appropriation);
                System.out.println("CAO22");
                saveAndStartWorkFlowForExistingdetailsCaoNew(actionName, reApp,appropriation, position, financialYear, beRe, misc, asOnDate,workflowBean,tempBudgetDetail);
                System.out.println("CAO33");
                addedList.add(appropriation);
            }
        } catch (final ValidationException e)
        {
        	System.out.println("issueV"+e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
        	System.out.println("issueE"+e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        return true;
    }
    @Transactional
    public boolean createReAppropriationCao(final String actionName,
            final List<BudgetReAppropriationView> budgetReAppropriationList,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        try {
            if (budgetReAppropriationList.isEmpty()
                    || !rowsToAddForExistingDetails(budgetReAppropriationList))
                return false;
            validateMandatoryFields(budgetReAppropriationList);
            System.out.println("CAO11");
            final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
            for (final BudgetReAppropriationView appropriation : budgetReAppropriationList) {
                validateDuplicates(addedList, appropriation);
                System.out.println("CAO22");
                saveAndStartWorkFlowForExistingdetailsCao(actionName, appropriation, position, financialYear, beRe, misc, asOnDate);
                System.out.println("CAO33");
                addedList.add(appropriation);
            }
        } catch (final ValidationException e)
        {
        	System.out.println("issueV"+e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
        	System.out.println("issueE"+e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        return true;
    }
    
    @Transactional
    public boolean createReAppropriationAcmc(final String actionName,
            final List<BudgetReAppropriationView> budgetReAppropriationList,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        try {
            if (budgetReAppropriationList.isEmpty()
                    || !rowsToAddForExistingDetails(budgetReAppropriationList))
                return false;
            validateMandatoryFields(budgetReAppropriationList);
            final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
            for (final BudgetReAppropriationView appropriation : budgetReAppropriationList) {
                validateDuplicates(addedList, appropriation);
                saveAndStartWorkFlowForExistingdetailsAcmc(actionName, appropriation, position, financialYear, beRe, misc, asOnDate);
                addedList.add(appropriation);
            }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        return true;
    }
    
    @Transactional
    public boolean createReAppropriationMc(final String actionName,
            final List<BudgetReAppropriationView> budgetReAppropriationList,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        try {
            if (budgetReAppropriationList.isEmpty()
                    || !rowsToAddForExistingDetails(budgetReAppropriationList))
                return false;
            validateMandatoryFields(budgetReAppropriationList);
            final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
            for (final BudgetReAppropriationView appropriation : budgetReAppropriationList) {
                validateDuplicates(addedList, appropriation);
                saveAndStartWorkFlowForExistingdetailsMc(actionName, appropriation, position, financialYear, beRe, misc, asOnDate);
                addedList.add(appropriation);
            }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        return true;
    }

    @Transactional
    public void saveAndStartWorkFlowForExistingdetails(final String actionName, final BudgetReAppropriationView reAppView,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        final BudgetReAppropriation appropriation = new BudgetReAppropriation();
        EgwStatus status = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved");
        reAppView.getBudgetDetail().setStatus(status);
        final List<BudgetDetail> searchBy = budgetDetailService.searchByCriteriaWithTypeAndFY(financialYear.getId(), beRe,
                reAppView.getBudgetDetail());
        if (searchBy.size() != 1)
            throw new ValidationException(Arrays.asList(new ValidationError("budget.reappropriation.invalid.combination",
                    "budget.reappropriation.invalid.combination")));
        appropriation.setBudgetDetail(searchBy.get(0));
        appropriation.setReAppropriationMisc(misc);
        appropriation.setAnticipatoryAmount(reAppView.getAnticipatoryAmount());
        try {
            appropriation.setAsOnDate(Constants.DDMMYYYYFORMAT2.parse(asOnDate));
        } catch (final Exception e) {
            LOGGER.error("Error while parsing date");
        }
        if ("Addition".equalsIgnoreCase(reAppView.getChangeRequestType()))
            appropriation.setAdditionAmount(reAppView.getDeltaAmount());
        else
            appropriation.setDeductionAmount(reAppView.getDeltaAmount());

        appropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Created"));//change from approved
        validateDeductionAmount(appropriation);
        
        /*
         * appropriation.start().withOwner(position); budgetReAppropriationWorkflowService.transition(actionName, appropriation,
         * "");
         */
        applyAuditing(appropriation);
        persist(appropriation);
        // Need to call on approve (After implementing workflow)
        updatePlanningBudget(appropriation);
    }
    
    @Transactional
    public void saveAndStartWorkFlowForExistingdetailsNew(final String actionName, final BudgetReAppropriationView reAppView,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate, WorkflowBean workflowBean) {
        final BudgetReAppropriation appropriation = new BudgetReAppropriation();
        EgwStatus status = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved");
        reAppView.getBudgetDetail().setStatus(status);
        final List<BudgetDetail> searchBy = budgetDetailService.searchByCriteriaWithTypeAndFY(financialYear.getId(), beRe,
                reAppView.getBudgetDetail());
        if (searchBy.size() != 1)
            throw new ValidationException(Arrays.asList(new ValidationError("budget.reappropriation.invalid.combination",
                    "budget.reappropriation.invalid.combination")));
        appropriation.setBudgetDetail(searchBy.get(0));
        appropriation.setReAppropriationMisc(misc);
        appropriation.setAnticipatoryAmount(reAppView.getAnticipatoryAmount());
        try {
            appropriation.setAsOnDate(Constants.DDMMYYYYFORMAT2.parse(asOnDate));
        } catch (final Exception e) {
            LOGGER.error("Error while parsing date");
        }
        if ("Addition".equalsIgnoreCase(reAppView.getChangeRequestType()))
            appropriation.setAdditionAmount(reAppView.getDeltaAmount());
        else
            appropriation.setDeductionAmount(reAppView.getDeltaAmount());

        //appropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Created"));//change from approved
        validateDeductionAmount(appropriation);
        transitionWorkFlow(appropriation, workflowBean);
        applyAuditing(appropriation);
        persist(appropriation);
        // Need to call on approve (After implementing workflow)
        updatePlanningBudget(appropriation);
    }
    
    @Transactional
    public void saveAndStartWorkFlowForExistingdetailsCao(final String actionName, final BudgetReAppropriationView reAppView,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
    	System.out.println("A1");
        final BudgetReAppropriation appropriation = new BudgetReAppropriation();
        /*EgwStatus status = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "REAPP CAO");
        reAppView.getBudgetDetail().setStatus(status);
        System.out.println("A2");
        final List<BudgetDetail> searchBy = budgetDetailService.searchByCriteriaWithTypeAndFY(financialYear.getId(), beRe,
                reAppView.getBudgetDetail());
        System.out.println("A3 "+searchBy.size());
        if (searchBy.size() != 1)
            throw new ValidationException(Arrays.asList(new ValidationError("budget.reappropriation.invalid.combination",
                    "budget.reappropriation.invalid.combination")));
        appropriation.setBudgetDetail(searchBy.get(0));
        System.out.println("A4 : "+misc);
        appropriation.setReAppropriationMisc(misc);
        System.out.println("A5 : ");
        appropriation.setAnticipatoryAmount(reAppView.getAnticipatoryAmount());
        try {
            appropriation.setAsOnDate(Constants.DDMMYYYYFORMAT2.parse(asOnDate));
        } catch (final Exception e) {
        	System.out.println("CAO Issue"+e.getMessage());
            LOGGER.error("Error while parsing date");
        }
        if ("Addition".equalsIgnoreCase(reAppView.getChangeRequestType()))
            appropriation.setAdditionAmount(reAppView.getDeltaAmount());
        else
            appropriation.setDeductionAmount(reAppView.getDeltaAmount());

        System.out.println("A6 : ");
        appropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Approved"));
        System.out.println("A7 : ");
        validateDeductionAmount(appropriation);
        System.out.println("A8 : ");
        /*
         * appropriation.start().withOwner(position); budgetReAppropriationWorkflowService.transition(actionName, appropriation,
         * "");
         */
        //applyAuditing(appropriation);
        //persist(appropriation);
        // Need to call on approve (After implementing workflow)
        updatePlanningBudgetCao(appropriation);
    }
    @Transactional
    public void saveAndStartWorkFlowForExistingdetailsCaoNew(final String actionName, final BudgetReAppropriation reApp,final BudgetReAppropriationView reAppView,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate,WorkflowBean workflowBean,BudgetDetail tempBudgetDetail) {
    	System.out.println("A1");
    	EgwStatus status = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved");
    	tempBudgetDetail.setStatus(status);
        final List<BudgetDetail> searchBy = budgetDetailService.searchByCriteriaWithTypeAndFY(financialYear.getId(), beRe,
        		tempBudgetDetail);
        final BudgetReAppropriation appropriation = new BudgetReAppropriation();
        appropriation.setBudgetDetail(searchBy.get(0));
        appropriation.setReAppropriationMisc(misc);
        appropriation.setAnticipatoryAmount(reAppView.getAnticipatoryAmount());
        try {
            //appropriation.setAsOnDate(Constants.DDMMYYYYFORMAT2.parse(asOnDate));
        	appropriation.setAsOnDate(new Date());
        } catch (final Exception e) {
            LOGGER.error("Error while parsing date");
        }
        if ("Addition".equalsIgnoreCase(reAppView.getChangeRequestType()))
            appropriation.setAdditionAmount(reAppView.getDeltaAmount());
        else
            appropriation.setDeductionAmount(reAppView.getDeltaAmount());
        
       // appropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Approved"));//change from approved
        transitionWorkFlow(reApp, workflowBean);
        appropriation.setStatus(reApp.getStatus());
        applyAuditing(reApp);
        applyAuditing(appropriation);
        //updatePlanningBudgetCao(appropriation);
        persist(appropriation);
    }
    @Transactional
    public void saveAndStartWorkFlowForExistingdetailsAcmc(final String actionName, final BudgetReAppropriationView reAppView,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        final BudgetReAppropriation appropriation = new BudgetReAppropriation();
        System.out.println("acmc");
        /*EgwStatus status = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "REAPP ACMC");
        reAppView.getBudgetDetail().setStatus(status);
        final List<BudgetDetail> searchBy = budgetDetailService.searchByCriteriaWithTypeAndFY(financialYear.getId(), beRe,
                reAppView.getBudgetDetail());
        if (searchBy.size() != 1)
            throw new ValidationException(Arrays.asList(new ValidationError("budget.reappropriation.invalid.combination",
                    "budget.reappropriation.invalid.combination")));
        appropriation.setBudgetDetail(searchBy.get(0));
        appropriation.setReAppropriationMisc(misc);
        appropriation.setAnticipatoryAmount(reAppView.getAnticipatoryAmount());
        try {
            appropriation.setAsOnDate(Constants.DDMMYYYYFORMAT2.parse(asOnDate));
        } catch (final Exception e) {
            LOGGER.error("Error while parsing date");
        }
        if ("Addition".equalsIgnoreCase(reAppView.getChangeRequestType()))
            appropriation.setAdditionAmount(reAppView.getDeltaAmount());
        else
            appropriation.setDeductionAmount(reAppView.getDeltaAmount());

        appropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Approved"));
        validateDeductionAmount(appropriation);
        /*
         * appropriation.start().withOwner(position); budgetReAppropriationWorkflowService.transition(actionName, appropriation,
         * "");
         */
        //applyAuditing(appropriation);
        //persist(appropriation);
        // Need to call on approve (After implementing workflow)
        updatePlanningBudgetAcmc(appropriation);
    }
    
    @Transactional
    public void saveAndStartWorkFlowForExistingdetailsMc(final String actionName, final BudgetReAppropriationView reAppView,
            final Position position, final CFinancialYear financialYear, final String beRe, final BudgetReAppropriationMisc misc,
            final String asOnDate) {
        final BudgetReAppropriation appropriation = new BudgetReAppropriation();
        /*EgwStatus status = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "REAPP MC");
        reAppView.getBudgetDetail().setStatus(status);
        final List<BudgetDetail> searchBy = budgetDetailService.searchByCriteriaWithTypeAndFY(financialYear.getId(), beRe,
                reAppView.getBudgetDetail());
        if (searchBy.size() != 1)
            throw new ValidationException(Arrays.asList(new ValidationError("budget.reappropriation.invalid.combination",
                    "budget.reappropriation.invalid.combination")));
        appropriation.setBudgetDetail(searchBy.get(0));
        appropriation.setReAppropriationMisc(misc);
        appropriation.setAnticipatoryAmount(reAppView.getAnticipatoryAmount());
        try {
            appropriation.setAsOnDate(Constants.DDMMYYYYFORMAT2.parse(asOnDate));
        } catch (final Exception e) {
            LOGGER.error("Error while parsing date");
        }
        if ("Addition".equalsIgnoreCase(reAppView.getChangeRequestType()))
            appropriation.setAdditionAmount(reAppView.getDeltaAmount());
        else
            appropriation.setDeductionAmount(reAppView.getDeltaAmount());

        appropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Approved"));
        validateDeductionAmount(appropriation);
        /*
         * appropriation.start().withOwner(position); budgetReAppropriationWorkflowService.transition(actionName, appropriation,
         * "");
         */
        //applyAuditing(appropriation);
        //persist(appropriation);
        // Need to call on approve (After implementing workflow)
        updatePlanningBudgetMc(appropriation);
    }

    @Transactional
    public BudgetReAppropriationMisc createBudgetReAppropriationMisc(final String actionName, final String beRe,
            final CFinancialYear financialYear, final BudgetReAppropriationMisc appropriationMisc, final Position position) {
        final Budget budget = new Budget();
        budget.setIsbere(beRe);
        budget.setFinancialYear(financialYear);
        final BudgetDetail budgetDetail = new BudgetDetail();
        budgetDetail.setBudget(budget);
        return createReAppropriationMisc(actionName, appropriationMisc, budgetDetail, position);
    }

    @Transactional
    public BudgetReAppropriationMisc createBudgetReAppropriationMiscNew(final String actionName, final String beRe,
            final CFinancialYear financialYear, final BudgetReAppropriationMisc appropriationMisc, WorkflowBean workflowBean) {
    	System.out.println("inide createBudgetReAppropriationMiscNew");
        final Budget budget = new Budget();
        budget.setIsbere(beRe);
        budget.setFinancialYear(financialYear);
        final BudgetDetail budgetDetail = new BudgetDetail();
        budgetDetail.setBudget(budget);
        return createReAppropriationMiscNew(actionName, appropriationMisc, budgetDetail, workflowBean);
    }
    
    @Transactional
    public boolean createReAppropriationForNewBudgetDetail(final String actionName,
            final List<BudgetReAppropriationView> newBudgetReAppropriationList, final Position position,
            final BudgetReAppropriationMisc misc) {
        try {
            BudgetDetail detail = null;
            if (newBudgetReAppropriationList.isEmpty()
                    || !newBudgetReAppropriationList.isEmpty() && !rowsToAddExists(newBudgetReAppropriationList))
                return false;
            try {
                final List<BudgetReAppropriationView> addedList = new ArrayList<BudgetReAppropriationView>();
                for (final BudgetReAppropriationView appropriation : newBudgetReAppropriationList) {
                    if (budgetDetailService.getBudgetDetail(appropriation.getBudgetDetail().getFund().getId(), appropriation
                            .getBudgetDetail().getFunction().getId(), appropriation.getBudgetDetail().getExecutingDepartment()
                            , appropriation.getBudgetDetail().getBudgetGroup().getId()) == null) {
                        detail = createApprovedBudgetDetail(appropriation, position);
                        if (!checkRowEmpty(appropriation)) {
                            validateMandatoryFields(newBudgetReAppropriationList);
                            validateDuplicates(addedList, appropriation);
                            saveAndStartWorkFlowForNewDetail(actionName, detail, appropriation, position, misc);
                            addedList.add(appropriation);
                        }
                    } else
                        throw new ValidationException(Arrays.asList(new ValidationError("budgetDetail.duplicate",
                                "budgetdetail.exists")));
                }
            } catch (final Exception e) {
                throw new ValidationException(Arrays.asList(new ValidationError("budgetDetail.duplicate", "budgetdetail.exists")));
            }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } catch (final Exception e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
        return true;
    }

    @Transactional
    public void saveAndStartWorkFlowForNewDetail(final String actionName, final BudgetDetail detail,
            final BudgetReAppropriationView appropriation, final Position position, final BudgetReAppropriationMisc misc) {
        final BudgetReAppropriation reAppropriation = new BudgetReAppropriation();
        detail.setPlanningPercent(appropriation.getPlanningPercent());
        detail.setQuarterpercent(appropriation.getQuarterPercent());
        detail.setBudgetAvailable(appropriation.getDeltaAmount().multiply(detail.getPlanningPercent())
                .divide(new BigDecimal(String
                        .valueOf(100))));
        reAppropriation.setBudgetDetail(detail);
        reAppropriation.setReAppropriationMisc(misc);
        reAppropriation.setAnticipatoryAmount(appropriation.getAnticipatoryAmount());
        // Since it is a new budget detail, the amount will always be addition amount
        reAppropriation.setAdditionAmount(appropriation.getDeltaAmount());
        reAppropriation.setStatus(egwStatusDAO.getStatusByModuleAndCode("BudgetReAppropriation", "Approved"));
        applyAuditing(reAppropriation);
        persist(reAppropriation);
        /*
         * reAppropriation.start().withOwner(position); budgetReAppropriationWorkflowService.transition(actionName,
         * reAppropriation, "");
         */
    }
    private org.egov.infra.microservice.models.Designation getDesignationDetails(String desgnCode){
    	List<org.egov.infra.microservice.models.Designation> designation = microserviceUtils.getDesignation(desgnCode);
    	return designation.get(0);
    }   
	
    public String getEmployeeName(Long empId){
        return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }
    public String getEmployeeDesignation(Long empId) {
    	return microserviceUtils.getEmployee(empId, null, null, null).get(0).getAssignments().get(0).getDesignation();
    }
    private String populateEmpName() {
		Long empId = ApplicationThreadLocals.getUserId();
		String name = null;
		List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null, null, null);
		if (null != employs && employs.size() > 0) {
			name = employs.get(0).getAssignments().get(0).getEmployeeName();

		}
		return name;
	}
    
    private Long populatePosition() {
		Long empId = ApplicationThreadLocals.getUserId();
		Long pos = null;
		List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null, null, null);
		if (null != employs && employs.size() > 0) {
			pos = employs.get(0).getAssignments().get(0).getPosition();

		}
		return pos;
	}
}