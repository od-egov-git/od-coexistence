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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.EgwStatus;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.eis.entity.Assignment;
import org.egov.eis.entity.Employee;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.DatabaseSequenceProvider;
import org.egov.infra.script.entity.Script;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infra.workflow.service.WorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetGroup;
import org.egov.model.budget.BudgetUpload;
import org.egov.model.budget.BudgetUploadReport;
import org.egov.model.repository.BudgetDetailRepository;
import org.egov.model.voucher.WorkflowBean;
import org.egov.infra.microservice.models.Designation;
import org.egov.pims.commons.Position;
import org.egov.pims.model.PersonalInformation;
import org.egov.utils.BudgetAccountType;
import org.egov.utils.BudgetingType;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

@Service
@Transactional(readOnly = true)
public class BudgetDetailService extends PersistenceService<BudgetDetail, Long> {
    private static final String BE = "BE";
    private static final String RE = "RE";
    @Autowired
    protected EisCommonService eisCommonService;
    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<BudgetDetail> budgetDetailWorkflowService;
   // protected WorkflowService<BudgetDetail> budgetDetailWorkflowService;
    private ScriptService scriptExecutionService;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;

    
    @Autowired
    @Qualifier("masterDataCache")
    private EgovMasterDataCaching masterDataCache;
    
    @Autowired
    @Qualifier("budgetService")
    private BudgetService budgetService;

    @Autowired
    @Qualifier("budgetGroupService")
    private BudgetGroupService budgetGroupService;

    @Autowired
    private DatabaseSequenceProvider databaseSequenceProvider;

    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;

    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private MicroserviceUtils microServiceUtil;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<BudgetDetail> budgetDetailWFService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BudgetDetailRepository budgetDetailRepository;
    
    @Autowired
    public MicroserviceUtils microserviceUtils;

    private static final String DUPLICATE = "budgetDetail.duplicate";
    private static final String EXISTS = "budgetdetail.exists";

    private static final Logger LOGGER = Logger.getLogger(BudgetDetailService.class);
    private static final String BUDGET_STATES_INSERT = "insert into eg_wf_states (ID,TYPE,VALUE,CREATEDBY,CREATEDDATE,LASTMODIFIEDDATE,LASTMODIFIEDBY,DATEINFO,OWNER_POS,STATUS,VERSION) values (:stateId,'Budget','NEW',1,current_date,current_date,1,current_date,1,1,0)";
    private static final String BUDGETDETAIL_STATES_INSERT = "insert into eg_wf_states (ID,TYPE,VALUE,CREATEDBY,CREATEDDATE,LASTMODIFIEDDATE,LASTMODIFIEDBY,DATEINFO,OWNER_POS,STATUS,VERSION) values (:stateId,'BudgetDetail','NEW',1,current_date,current_date,1,current_date,1,1,0)";

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public BudgetDetailService() {
        super(BudgetDetail.class);
    }

    public BudgetDetailService(final Class<BudgetDetail> type) {
        super(type);
    }

    public Long getCountByBudget(final Long budgetId) {
        return ((BigInteger) persistenceService.getSession()
                .createSQLQuery("select count(*) from egf_budgetdetail where budget = " + budgetId).uniqueResult())
                        .longValue();
    }

    public boolean canViewApprovedAmount(final PersistenceService persistenceService, final Budget budget) {
        final Script script = (Script) persistenceService
                .findAllByNamedQuery(Script.BY_NAME, "budget.report.view.access").get(0);
        final ScriptContext context = ScriptService.createContext("wfItem", budget, "eisCommonServiceBean",
                eisCommonService, "userId", ApplicationThreadLocals.getUserId().intValue());
        final Integer result = (Integer) scriptExecutionService.executeScript(script, context);
        if (result == 1)
            return true;
        return false;
    }

    public BudgetDetail createBudgetDetail(final BudgetDetail detail, final Position position,
            final PersistenceService service) {
        try {
            setRelatedEntitesOn(detail,true);

            return detail;
        } catch (final ConstraintViolationException e) {
            throw new ValidationException(
                    Arrays.asList(new ValidationError(DUPLICATE, EXISTS)));
        }
    }

    public List<BudgetDetail> searchBy(final BudgetDetail detail) {
        return constructCriteria(detail).list();

    }

    public List<BudgetDetail> searchByCriteriaAndFY(final Long financialYear, final BudgetDetail detail,
            final boolean isApprove, final Position pos) {
        final Criteria criteria = constructCriteria(detail).createCriteria(Constants.BUDGET)
                .add(Restrictions.eq("financialYear.id", financialYear));
        if (isApprove)
            criteria.createCriteria(Constants.STATE).add(Restrictions.eq("owner", pos));
        else
            criteria.createCriteria(Constants.STATE).add(Restrictions.eq("value", "NEW"));
        return criteria.list();
    }

    public List<BudgetDetail> searchByCriteriaWithTypeAndFY(final Long financialYear, final String type,
            final BudgetDetail detail) {
        if (detail.getBudget() != null && detail.getBudget().getId() != 0l) {
        	System.out.println("aa1");
            final Map<String, Object> map = new HashMap<String, Object>();
            addCriteriaExcludingBudget(detail, map);
            final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
            addBudgetDetailCriteria(map, criteria);
            criteria.addOrder(Order.asc("id"));

            return criteria.createCriteria(Constants.BUDGET).add(Restrictions.eq("financialYear.id", financialYear))
                    .add(Restrictions.eq("isbere", type)).list();
        } else{
        	System.out.println("aa2");
            Criteria constructCriteria = constructCriteria(detail);
            constructCriteria.add(Restrictions.eq("executingDepartment", detail.getExecutingDepartment()));
            return constructCriteria.createCriteria(Constants.BUDGET)
                    .add(Restrictions.eq("financialYear.id", financialYear)).add(Restrictions.eq("isbere", type))
                    .list();            
        }
    }

    private Map<String, Object> createCriteriaMap(final BudgetDetail detail) {
        final Map<String, Object> map = new HashMap<String, Object>();
        addCriteriaExcludingBudget(detail, map);
        map.put(Constants.BUDGET, detail.getBudget() == null ? 0l : detail.getBudget().getId());
        return map;
    }

    protected void addCriteriaExcludingBudget(final BudgetDetail detail, final Map<String, Object> map) {
        map.put("budgetGroup", detail.getBudgetGroup() == null ? 0l : detail.getBudgetGroup().getId());
        map.put("function", detail.getFunction() == null ? 0l : detail.getFunction().getId());
        map.put("functionary", detail.getFunctionary() == null ? 0 : detail.getFunctionary().getId());
        map.put("scheme", detail.getScheme() == null ? 0 : detail.getScheme().getId());
        map.put("subScheme", detail.getSubScheme() == null ? 0 : detail.getSubScheme().getId());
//        map.put("executingDepartmentCode",
//                detail.getExecutingDepartmentCode() == null ? 0 : detail.getExecutingDepartmentCode());
        map.put("boundary", detail.getBoundary() == null ? 0 : detail.getBoundary().getId());
        map.put("fund", detail.getFund() == null ? 0 : detail.getFund().getId());
        map.put("status", detail.getStatus() == null ? 0 : detail.getStatus().getId());
        //map.put("id", detail.getId() == null ? 0 : detail.getId());
    }

    public List<BudgetDetail> findAllBudgetDetailsFor(final Budget budget, final BudgetDetail example) {
        final List<Budget> budgets = new ArrayList<Budget>();
        collectLeafBudgets(budget, budgets);
        budgets.add(findBudget(budget));
        final Criteria criteria = constructCriteria(example);
        criteria.add(Restrictions.in(Constants.BUDGET, budgets));
        criteria.addOrder(Property.forName("budget").asc());
        criteria.createAlias("budgetGroup", "bg");
        criteria.addOrder(Property.forName("bg.name").asc());
        return criteria.list();
    }

    public List<BudgetDetail> findAllBudgetDetailsForParent(Budget budget, final BudgetDetail example,
            final PersistenceService persistenceService) {
        if (budget == null || budget.getId() == null)
            return Collections.EMPTY_LIST;
        budget = (Budget) persistenceService.find("from Budget where id=?", budget.getId());
        final BudgetDetail detail = new BudgetDetail();
        detail.copyFrom(example);
        detail.setBudget(null);
        final String materializedPath = budget.getMaterializedPath();
        return constructCriteria(detail).addOrder(Property.forName("executingDepartment").asc())
                .createCriteria(Constants.BUDGET).add(Restrictions.like("materializedPath",
                        materializedPath == null ? "" : materializedPath.concat("%")))
                .list();
    }

    public List<BudgetDetail> findAllBudgetDetailsWithReAppropriation(final Budget budget, final BudgetDetail example) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting findAllBudgetDetailsWithReAppropriation...");
        final List<BudgetDetail> budgetDetails = findAllBudgetDetailsFor(budget, example);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Done findAllBudgetDetailsWithReAppropriation.");
        return budgetDetails;
    }

    private Budget findBudget(final Budget budget) {
        return getSession().load(Budget.class, budget.getId());
    }

    public List<Budget> findBudgetsForFY(final Long financialYear) {
        final Criteria criteria = getSession().createCriteria(Budget.class);
        return criteria.add(Restrictions.eq("financialYear.id", financialYear))
                .add(Restrictions.eq("isActiveBudget", true)).list();
    }

    public List<Budget> findApprovedBudgetsForFY(final Long financialYear) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("starting findApprovedBudgetsForFY...");
        final Criteria criteria = getSession().createCriteria(Budget.class);
        return criteria.add(Restrictions.eq("financialYear.id", financialYear))
                .add(Restrictions.eq("isActiveBudget", true)).addOrder(Property.forName("name").asc())
                .createCriteria("status", "status").add(Restrictions.eq("status.code", "Approved")).list();
    }

    public List<Budget> findBudgetsForFYWithNewState(final Long financialYear) {
        final Criteria criteria = getSession().createCriteria(Budget.class);
        criteria.createCriteria("status", "status").add(Restrictions.eq("status.code", "Created"));
        return criteria.add(Restrictions.eq("financialYear.id", financialYear))
                .add(Restrictions.eq("isActiveBudget", true)).list();
    }

    public List<Budget> findPrimaryBudgetForFY(final Long financialYear) {
        final Criteria criteria = getSession().createCriteria(Budget.class);
        return criteria.add(Restrictions.eq("financialYear.id", financialYear))
                .add(Restrictions.eq("isActiveBudget", true)).add(Restrictions.eq("isPrimaryBudget", true))
                .add(Restrictions.isNull("parent")).list();
    }

    public Budget findApprovedPrimaryParentBudgetForFY(final Long financialYear) {
        final Criteria criteria = getSession().createCriteria(Budget.class);
        List<Budget> budgetList = criteria.add(Restrictions.eq("financialYear.id", financialYear))
                .add(Restrictions.eq("isbere", RE)).add(Restrictions.eq("isActiveBudget", true))
                .add(Restrictions.eq("isPrimaryBudget", true)).add(Restrictions.isNull("parent"))
                .addOrder(Property.forName("name").asc()).createCriteria("status", "status")
                .add(Restrictions.eq("status.code", "Approved")).list();
        if (budgetList.isEmpty()) {
            final Criteria c = getSession().createCriteria(Budget.class);
            budgetList = c.add(Restrictions.eq("financialYear.id", financialYear)).add(Restrictions.eq("isbere", BE))
                    .add(Restrictions.eq("isActiveBudget", true)).add(Restrictions.eq("isPrimaryBudget", true))
                    .add(Restrictions.isNull("parent")).addOrder(Property.forName("name").asc())
                    .createCriteria("status", "status").add(Restrictions.eq("status.code", "Approved")).list();
            if (budgetList.isEmpty())
                return null;
        }
        return budgetList.get(0);
    }

    public Set<Budget> findBudgetTree(final Budget budget, final BudgetDetail example) {
        if (budget == null)
            return Collections.EMPTY_SET;
        final Criteria budgetDetailCriteria = constructCriteria(example);
        budgetDetailCriteria.createCriteria(Constants.BUDGET);
        if(!"0".equals(example.getExecutingDepartment()) && example.getExecutingDepartment() != null)
            budgetDetailCriteria.add(Restrictions.eq("executingDepartment", example.getExecutingDepartment()));
        final List<Budget> leafBudgets = budgetDetailCriteria
                .setProjection(Projections.distinct(Projections.property(Constants.BUDGET))).list();
        final List<Budget> parents = new ArrayList<Budget>();
        final Set<Budget> budgetTree = new LinkedHashSet<Budget>();
        for (Budget leaf : leafBudgets) {
            parents.clear();
            while (leaf != null && !leaf.getId().equals(budget.getId())) {
                parents.add(leaf);
                leaf = leaf.getParent();
            }
            if (leaf != null) {
                parents.add(leaf);
                budgetTree.addAll(parents);
            }    
        }
        return budgetTree;
    }

    private List<Budget> findChildren(final Budget parent) {
        return ((PersistenceService) this).findAllBy("from Budget b where b.parent=?", parent);
    }

    private void collectLeafBudgets(final Budget parent, final List<Budget> children) {
        final List<Budget> myChildren = findChildren(parent);
        for (final Budget child : myChildren) {
            collectLeafBudgets(child, children);
            if (findChildren(child).isEmpty())
                children.add(child);
        }
    }

    private Criteria constructCriteria(final BudgetDetail example) {
        final Map<String, Object> map = createCriteriaMap(example);
        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
        addBudgetDetailCriteria(map, criteria);
        return criteria;

    }

    private void addBudgetDetailCriteria(final Map<String, Object> map, final Criteria criteria) {
        for (final Entry<String, Object> criterion : map.entrySet())
            if (isIdPresent(criterion.getValue()))
                criteria.createCriteria(criterion.getKey()).add(Restrictions.idEq(criterion.getValue()));
    }

    private void addBudgetDetailCriteriaIncudingNullRestrictions(final Map<String, Object> map,
            final Criteria criteria) {
        for (final Entry<String, Object> criterion : map.entrySet())
            if (isIdPresent(criterion.getValue()))
                criteria.createCriteria(criterion.getKey()).add(Restrictions.idEq(criterion.getValue()));
            else
                criteria.add(Restrictions.isNull(criterion.getKey()));
    }

    protected boolean isIdPresent(final Object value) {
        return Long.valueOf(value.toString()) != 0l && Long.valueOf(value.toString()) != -1;
    }

    @Override
    @Transactional
    public BudgetDetail persist(final BudgetDetail detail) {
        try {
            detail.setUniqueNo(detail.getFund().getId() + "-" + detail.getExecutingDepartment() + "-"
                    + detail.getFunction().getId() + "-" + detail.getBudgetGroup().getId());
            if (!chequeUnique(detail) && detail.getId() == null)
                throw new ValidationException(
                        Arrays.asList(new ValidationError(DUPLICATE, EXISTS)));
            checkForDuplicates(detail);
            return super.persist(detail);
        } catch (final Exception e) {
            throw new ValidationException(
                    Arrays.asList(new ValidationError(DUPLICATE, EXISTS)));
        }
    }

    private Boolean chequeUnique(final BudgetDetail detail) {

        final Criteria criteria = constructCriteria(detail)
                .add(Restrictions.eq("budget.id", detail.getBudget().getId()));
        criteria.add(Restrictions.eq("budgetGroup.id", detail.getBudgetGroup().getId()));
        criteria.add(Restrictions.eq("fund.id", detail.getFund().getId()));
        criteria.add(Restrictions.eq("function.id", detail.getFunction().getId()));
//        criteria.add(Restrictions.eq("executingDepartmentCode", detail.getExecutingDepartmentCode()));

        return criteria.list().isEmpty();
    }

    public void checkForDuplicates(final BudgetDetail detail) {
        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
        final Map<String, Object> map = new HashMap<String, Object>();
        addCriteriaExcludingBudget(detail, map);
        addBudgetDetailCriteriaIncudingNullRestrictions(map, criteria);
        if (detail.getBudget() == null || detail.getBudget().getId() == null || detail.getBudget().getId() == 0
                || detail.getBudget().getId() == -1)
            return;
        // add restriction to check if budgetdetail with is combination exists
        // in the current year within a tree
        final Budget root = getRootFor(detail.getBudget());
        criteria.createCriteria(Constants.BUDGET)
                .add(Restrictions.eq("materializedPath", root == null ? "" : root.getMaterializedPath()));
        final List<BudgetDetail> existingDetails = criteria.list();
        if (!existingDetails.isEmpty() && !existingDetails.get(0).getId().equals(detail.getId()))
            throw new ValidationException(
                    Arrays.asList(new ValidationError(DUPLICATE, EXISTS)));
    }

    private Budget getRootFor(final Budget budget) {
        if (budget == null || StringUtils.isBlank(budget.getMaterializedPath()))
            return null;
        if (budget.getMaterializedPath().length() == 1)
            return budget;
        return (Budget) persistenceService.find("from Budget where materializedPath=?",
                budget.getMaterializedPath().split("\\.")[0]);
    }

    protected User getUser() {
        return (User) ((PersistenceService) this).find(" from User where id=?", ApplicationThreadLocals.getUserId());
    }

    public Position getPositionForEmployee(final Employee emp) throws ApplicationRuntimeException {
        return eisCommonService.getPrimaryAssignmentPositionForEmp(emp.getId());
    }

    public void setEisCommonService(final EisCommonService eisCommonService) {
        this.eisCommonService = eisCommonService;
    }

    public AppConfigValueService getAppConfigValuesService() {
        return appConfigValuesService;
    }

    public void setAppConfigValuesService(final AppConfigValueService appConfigValuesService) {
        this.appConfigValuesService = appConfigValuesService;
    }

    /**
     * @param detail
     * @return department of the budgetdetail
     * @throws ApplicationRuntimeException
     */
    public org.egov.infra.microservice.models.Department getDepartmentForBudget(final BudgetDetail detail) throws ApplicationRuntimeException {
        String dept = null;
        if (detail.getExecutingDepartment() != null)
            dept = detail.getExecutingDepartment();
        else
            throw new ApplicationRuntimeException("Department not found for the Budget" + detail.getId());
        return microserviceUtils.getDepartmentByCode(dept);
    }

    /**
     * returns department of the employee from assignment for the current date
     *
     * @param emp
     * @return
     */
    public Department depertmentForEmployee(final Employee emp) {
        Department dept = null;
        final Date currDate = new Date();
        try {
//            final Assignment empAssignment = eisCommonService.getLatestAssignmentForEmployeeByToDate(emp.getId(),
//                    currDate);
//            dept = empAssignment.getDepartment();
            return dept;
        } catch (final NullPointerException ne) {
            throw new ApplicationRuntimeException(ne.getMessage());
        } catch (final Exception e) {
            throw new ApplicationRuntimeException("Error while getting Department fort the employee" + emp.getName());
        }

    }

    public List<BudgetDetail> getRemainingDetailsForApproveOrReject(final Budget budget) {
        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
        // criteria.createCriteria("materializedPath",
        // "state").add(Restrictions.eq("state.value","NEW"));
        criteria.createCriteria(Constants.BUDGET, Constants.BUDGET).add(Restrictions.eq("budget.id", budget.getId()));
        return criteria.list();

    }

    public List<BudgetDetail> getRemainingDetailsForSave(final Budget budget, final Position currPos) {
        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
        criteria.createCriteria(Constants.STATE, Constants.STATE).add(Restrictions.eq("state.owner", currPos));
        criteria.createCriteria(Constants.BUDGET, Constants.BUDGET).add(Restrictions.eq("budget.id", budget.getId()));
        return criteria.list();

    }

    public BudgetDetail setRelatedEntitesOn(final BudgetDetail detail,boolean status) {
    	if(status)
    	{
    		detail.setStatus(egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "REAPP CAO"));
    	}
    	else
    	{
    		detail.setStatus(egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved"));
    	}
        
        if (detail.getBudget() != null) {
            detail.setBudget(persistenceService.getSession().load(Budget.class, detail.getBudget().getId()));
            addMaterializedPath(detail);
        }
        if (detail.getFunction() != null)
            detail.setFunction(persistenceService.getSession().load(CFunction.class, detail.getFunction().getId()));
        if (detail.getFunctionary() != null)
            detail.setFunctionary(
                    persistenceService.getSession().load(Functionary.class, detail.getFunctionary().getId()));
        if (detail.getExecutingDepartment() != null)
            detail.setExecutingDepartment(
                    detail.getExecutingDepartment());
        if (detail.getScheme() != null)
            detail.setScheme(persistenceService.getSession().load(Scheme.class, detail.getScheme().getId()));
        if (detail.getSubScheme() != null)
            detail.setSubScheme(persistenceService.getSession().load(SubScheme.class, detail.getSubScheme().getId()));
        if (detail.getFund() != null)
            detail.setFund(persistenceService.getSession().load(Fund.class, detail.getFund().getId()));
        if (detail.getBudgetGroup() != null)
            detail.setBudgetGroup(
                    persistenceService.getSession().load(BudgetGroup.class, detail.getBudgetGroup().getId()));
        if (detail.getBoundary() != null)
            detail.setBoundary(persistenceService.getSession().load(Boundary.class, detail.getBoundary().getId()));
        return detail;
    }

    private void addMaterializedPath(final BudgetDetail detail) {
        String materializedPath = "";
        String count = "";
        if (detail.getBudget() != null) {
            materializedPath = detail.getBudget().getMaterializedPath();
            final List<BudgetDetail> parallelBudgetDetails = findAllBy("from BudgetDetail bd where bd.budget=?",
                    detail.getBudget());
            if (parallelBudgetDetails != null)
                count = String.valueOf(parallelBudgetDetails.size() + 1);
            if (materializedPath != null && !materializedPath.isEmpty())
                materializedPath = materializedPath + "." + count;
            detail.setMaterializedPath(materializedPath);
        }
    }

    public void transitionToEnd(final BudgetDetail detail, final Position position) {
        detail.transition().end().withOwner(position);
    }

    public List<Object[]> fetchActualsForFYDate(final String fromDate, final String toVoucherDate,
            final List<String> mandatoryFields) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting fetchActualsForFY" + fromDate);
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "exclude_status_forbudget_actual");
        if (list.isEmpty())
            throw new ValidationException("", "exclude_status_forbudget_actual is not defined in AppConfig");
        final StringBuffer miscQuery = getMiscQuery(mandatoryFields, "vmis", "gl", "vh");
        final StringBuffer budgetGroupQuery = new StringBuffer();
        budgetGroupQuery.append(" (select bg1.id as id,bg1.accounttype as accounttype, c1.glcode "
                + "as mincode,c2.glcode as maxcode,c3.glcode as majorcode "
                + "from egf_budgetgroup bg1 left outer join chartofaccounts c1 on c1.id=bg1.mincode left outer join chartofaccounts c2 on "
                + "c2.id=bg1.maxcode left outer join chartofaccounts c3 on c3.id=bg1.majorcode ) bg ");
        final String voucherstatusExclude = list.get(0).getValue();
        StringBuffer query = new StringBuffer();
        query = query
                .append("select bd.id,SUM(gl.debitAmount)-SUM(gl.creditAmount) from egf_budgetdetail bd,generalledger gl,budgetDetail vh,"
                        + "vouchermis vmis," + budgetGroupQuery
                        + ",egf_budget b where bd.budget=b.id and vmis.budgetDetailID=vh.id and gl.budgetDetailID=vh.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and vh.status not in ("
                        + voucherstatusExclude + ") and " + "vh.voucherDate>= to_date('" + fromDate
                        + "','dd/MM/yyyy') and vh.voucherDate <= to_date('" + toVoucherDate + "','dd/MM/yyyy') "
                        + miscQuery + " and (gl.glcode = bg.mincode or gl.glcode=bg.majorcode) group by bd.id"
                        + " union "
                        + "select bd.id,SUM(gl.creditAmount)-SUM(gl.debitAmount) from egf_budgetdetail bd,generalledger gl,budgetDetail vh,"
                        + "vouchermis vmis," + budgetGroupQuery
                        + ",egf_budget b where bd.budget=b.id and vmis.budgetDetailID=vh.id and gl.budgetDetailID=vh.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and vh.status not in ("
                        + voucherstatusExclude + ") and " + "vh.voucherDate>= to_date('" + fromDate
                        + "','dd/MM/yyyy') and vh.voucherDate <= to_date('" + toVoucherDate + "','dd/MM/yyyy') "
                        + miscQuery + " and (gl.glcode = bg.mincode or gl.glcode=bg.majorcode) group by bd.id");
        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Finished fetchActualsForFY" + fromDate);
        return result;
    }

    /**
     *
     * @param detail
     * @return
     */
    public String generateUniqueNo(final BudgetDetail detail) {
        return detail.getFund().getId() + "-"
                + detail.getExecutingDepartment() + "-"
                + detail.getFunction().getId() + "-"
                + detail.getBudgetGroup().getId();

    }

    /**
     * vouchers are of the passed finaicial year budget is of passed topBudgets financialyear
     *
     * @param fy
     * @param mandatoryFields
     * @param topBudget
     * @param referingTopBudget
     * @param date
     * @param dept
     * @param fun
     * @param excludelist TODO
     * @return
     */

    public List<Object[]> fetchActualsForFY(final CFinancialYear fy, final List<String> mandatoryFields,
            final Budget topBudget, final Budget referingTopBudget, final Date date, final Integer dept,
            final Long fun) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "Starting fetchActualsForFY" + fy.getStartingDate().getYear() + "-" + fy.getEndingDate().getYear());
        String dateCondition = "";
        if (date != null)
            dateCondition = " AND vh.voucherdate <='" + Constants.DDMMYYYYFORMAT1.format(date) + "' ";
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "exclude_status_forbudget_actual");
        if (list.isEmpty())
            throw new ValidationException("", "exclude_status_forbudget_actual is not defined in AppConfig");
        StringBuffer miscQuery = getMiscQuery(mandatoryFields, "vmis", "gl", "vh");
        if (dept != null)
            miscQuery.append(" and bd.executing_department=" + dept);
        if (fun != null)
            miscQuery = miscQuery.append(" AND bd.function=" + fun);
        final StringBuffer referingUniqueNoQry = new StringBuffer(200);
        referingUniqueNoQry.append(" ");
        if (referingTopBudget != null)
            referingUniqueNoQry
                    .append(" and bd.uniqueno in (select uniqueno from egf_budgetdetail where MATERIALIZEDPATH like '"
                            + referingTopBudget.getMaterializedPath() + "%'  )");

        final StringBuffer budgetGroupQuery = new StringBuffer();
        budgetGroupQuery
                .append(" (select bg1.id as id,bg1.accounttype as accounttype,case when c1.glcode =  NULL then -1 else to_number(c1.glcode,'999999999') end "
                        + "as mincode,case when c2.glcode = null then  999999999 else c2.glcode end as maxcode,case when c3.glcode = null then -1 else to_number(c3.glcode,'999999999') end  as majorcode "
                        + "from egf_budgetgroup bg1 left outer join chartofaccounts c1 on c1.id=bg1.mincode left outer join chartofaccounts c2 on "
                        + "c2.id=bg1.maxcode left outer join chartofaccounts c3 on c3.id=bg1.majorcode ) bg ");
        final String voucherstatusExclude = list.get(0).getValue();
        StringBuffer query = new StringBuffer();

        query = query.append("  select bd.uniqueno,SUM(gl.debitAmount)-SUM(gl.creditAmount) from egf_budgetdetail bd,"
                + "vouchermis vmis,egf_budgetgroup bg,egf_budget b,financialyear f,fiscalperiod p,budgetDetail vh,generalledger gl "
                + "where bd.budget=b.id and p.financialyearid=f.id and f.id=" + fy.getId()
                + " and vh.fiscalperiodid=p.id " + dateCondition + " and " + " b.financialyearid="
                + topBudget.getFinancialYear().getId() + " and b.MATERIALIZEDPATH like '"
                + topBudget.getMaterializedPath() + "%' " + referingUniqueNoQry.toString()
                + " and  vmis.budgetDetailID=vh.id and gl.budgetDetailID=vh.id " + " and bd.budgetgroup=bg.id "
                + " and vh.status not in (" + voucherstatusExclude + ")  " + miscQuery + " "
                + " and gl.glcodeid=bg.mincode and gl.glcodeid=bg.maxcode and  bg.majorcode is null group by bd.uniqueno");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchActualsForFY " + result.size() + "      " + query.toString());
        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "==============================================================================================");
        return result;
    }

    /*
     * Copy of fetchActualsForFY passing exclude_status_forbudget_actual as list to reduce db hit
     */

    public List<Object[]> fetchActualsForFinYear(final CFinancialYear fy, final List<String> mandatoryFields,
            final Budget topBudget, final Budget referingTopBudget, final Date date, final String dept, final Long fun,
            final List<AppConfigValues> list) {

        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "Starting fetchActualsForFY" + fy.getStartingDate().getYear() + "-" + fy.getEndingDate().getYear());
        String dateCondition = "";
        if (date != null)
            dateCondition = " AND vh.voucherdate <='" + Constants.DDMMYYYYFORMAT1.format(date) + "' ";

        StringBuffer miscQuery = getMiscQuery(mandatoryFields, "vmis", "gl", "vh");
        if (dept != null)
            miscQuery.append(" and bd.executing_department=" + dept);
        if (fun != null)
            miscQuery = miscQuery.append(" AND bd.function=" + fun);
        final StringBuffer referingUniqueNoQry = new StringBuffer(200);
        referingUniqueNoQry.append(" ");
        if (referingTopBudget != null)
            referingUniqueNoQry
                    .append(" and bd.uniqueno in (select uniqueno from egf_budgetdetail where MATERIALIZEDPATH like '"
                            + referingTopBudget.getMaterializedPath() + "%'  )");

        final StringBuffer budgetGroupQuery = new StringBuffer();
        budgetGroupQuery
                .append(" (select bg1.id as id,bg1.accounttype as accounttype,case when c1.glcode =  NULL then -1 else to_number(c1.glcode,'999999999') end "
                        + "as mincode,case when c2.glcode = null then  999999999 else c2.glcode end as maxcode,case when c3.glcode = null then -1 else to_number(c3.glcode,'999999999') end  as majorcode "
                        + "from egf_budgetgroup bg1 left outer join chartofaccounts c1 on c1.id=bg1.mincode left outer join chartofaccounts c2 on "
                        + "c2.id=bg1.maxcode left outer join chartofaccounts c3 on c3.id=bg1.majorcode ) bg ");
        final String voucherstatusExclude = list.get(0).getValue();
        StringBuffer query = new StringBuffer();

        String sum = "";
        if (topBudget.getName().contains("Receipt"))
            sum = "SUM(gl.creditAmount)-SUM(gl.debitAmount)";
        else
            sum = "SUM(gl.debitAmount)-SUM(gl.creditAmount)";

        query = query.append("  select bd.uniqueno," + sum + " from egf_budgetdetail bd,"
                + "vouchermis vmis,egf_budgetgroup bg,egf_budget b,financialyear f,fiscalperiod p,budgetDetail vh,generalledger gl "
                + "where bd.budget=b.id and p.financialyearid=f.id and f.id=" + fy.getId()
                + " and vh.fiscalperiodid=p.id " + dateCondition + " and " + " b.financialyearid="
                + topBudget.getFinancialYear().getId() + " and b.MATERIALIZEDPATH like '"
                + topBudget.getMaterializedPath() + "%' " + referingUniqueNoQry.toString()
                + " and  vmis.budgetDetailID=vh.id and gl.budgetDetailID=vh.id " + " and bd.budgetgroup=bg.id "
                + " and vh.status not in (" + voucherstatusExclude + ")  " + miscQuery + " "
                + " and gl.glcodeid=bg.mincode and gl.glcodeid=bg.maxcode and  bg.majorcode is null group by bd.uniqueno");

        // if(LOGGER.isDebugEnabled())
        // LOGGER.debug("Query for fetchActualsForFY
        // "+fy.getStartingDate().getYear()+"-"+fy.getEndingDate().getYear()+"------"+query.toString());
        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchActualsForFY " + result.size() + "      " + query.toString());

        return result;
    }

    /**
     * vouchers are of the passed finaicial year budget is of passed topBudgets financialyear
     */

    public List<Object[]> fetchMajorCodeAndActuals(final CFinancialYear financialYear, final Budget topBudget,
            final Date date, final CFunction function, final String dept, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndActuals................");
        StringBuffer query = new StringBuffer();
        String dateCondition = "";
        if (date != null)
            dateCondition = " AND vh.voucherdate <='" + Constants.DDMMYYYYFORMAT1.format(date) + "' ";
        String functionCondition = "";
        if (function != null)
            functionCondition = " and gl.functionId=" + function.getId();
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "exclude_status_forbudget_actual");
        if (list.isEmpty())
            throw new ValidationException("", "exclude_status_forbudget_actual is not defined in AppConfig");
        final String voucherstatusExclude = list.get(0).getValue();
        String sum = "";
        if (topBudget.getName().contains("Receipt"))
            sum = "SUM(gl.creditAmount)-SUM(gl.debitAmount)";
        else
            sum = "SUM(gl.debitAmount)-SUM(gl.creditAmount)";

        query = query.append("SELECT substr(gl.glcode,1,3)," + sum
                + " FROM egf_budgetdetail bd, vouchermis vmis, egf_budgetgroup bg, egf_budget b, financialyear f, fiscalperiod p, budgetDetail vh, generalledger gl, eg_wf_states wf"
                + " WHERE bd.budget      =b.id AND p.financialyearid=f.id AND f.id =" + financialYear.getId()
                + " AND vh.fiscalperiodid=p.id " + dateCondition + " AND b.financialyearid="
                + topBudget.getFinancialYear().getId() + " AND b.id = " + topBudget.getId()
                + " AND vmis.budgetDetailID=vh.id AND gl.budgetDetailID  =vh.id"
                + " AND bd.budgetgroup      =bg.id  AND vh.status NOT      IN (" + voucherstatusExclude
                + ") AND vh.fundId =bd.fund AND gl.functionId =bd.function " + functionCondition + ""
                + " AND vmis.departmentid   =bd.executing_department and bd.executing_department =" + dept
                + " AND gl.glcodeid         =bg.mincode AND gl.glcodeid         =bg.maxcode AND bg.majorcode       IS NULL AND (wf.value='END' OR wf.owner_pos="
                + pos.getId() + ") AND bd.state_id = wf.id GROUP BY substr(gl.glcode,1,3)");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndActuals......." + query.toString());

        return result;
    }

    public List<Object[]> fetchMajorCodeAndName(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndName............");
        StringBuffer query = new StringBuffer();
        String functionCondition = "";
        if (function != null)
            functionCondition = " AND bd.function = " + function.getId();

        query = query
                .append("SELECT cao.majorcode, cao1.glcode||'-'||cao1.name FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, chartofaccounts cao1, financialyear f, eg_wf_states wf"
                        + " WHERE bd.budget=b.id AND f.id=" + topBudget.getFinancialYear().getId()
                        + " AND b.financialyearid=" + topBudget.getFinancialYear().getId()
                        + " AND b.MATERIALIZEDPATH LIKE '" + topBudget.getMaterializedPath()
                        + "%' AND bd.budgetgroup=bg.id "
                        + " AND cao.id=bg.mincode AND cao.id=bg.maxcode AND bg.majorcode IS NULL AND bd.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition
                        + " and cao1.glcode = cao.majorcode AND (wf.value='END' OR wf.owner_pos=" + pos.getId()
                        + ") AND bd.state_id = wf.id GROUP BY cao.majorcode, cao1.glcode||'-'||cao1.name");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndName..........." + query.toString());

        return result;
    }

    public List<Object[]> fetchMajorCodeAndBEAmount(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndBEAmount................");
        StringBuffer query = new StringBuffer();
        String functionCondition1 = "";
        String functionCondition2 = "";
        if (function != null) {
            functionCondition1 = " AND bd1.function = " + function.getId();
            functionCondition2 = " AND bd2.function = " + function.getId();
        }
        // / need to add b2.isbere='BE'
        query = query
                .append("SELECT cao.majorcode, SUM(bd2.approvedamount) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, eg_wf_states wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND f.id ="
                        + topBudget.getFinancialYear().getId() + " AND b1.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b2.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b1.MATERIALIZEDPATH LIKE '"
                        + topBudget.getMaterializedPath() + "%' and b2.isbere='BE' AND bd2.budgetgroup =bg.id  "
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd2.executing_department = "
                        + budgetDetail.getExecutingDepartment()+ functionCondition2
                        + " AND bd1.executing_department = " + budgetDetail.getExecutingDepartment()
                        + functionCondition1 + " AND bd1.uniqueno = bd2.uniqueno AND (wf.value='END' OR wf.owner_pos="
                        + pos.getId() + ") AND bd1.state_id = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndBEAmount");

        return result;
    }

    public List<Object[]> fetchUniqueNoAndBEAmount(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchUniqueNoAndBEAmount................");
        StringBuffer query = new StringBuffer();
        String functionCondition1 = "";
        String functionCondition2 = "";
        if (function != null) {
            functionCondition1 = " AND bd1.function = " + function.getId();
            functionCondition2 = " AND bd2.function = " + function.getId();
        }

        query = query
                .append("SELECT bd2.uniqueno, SUM(bd2.approvedamount) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, eg_wf_states wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND f.id ="
                        + topBudget.getFinancialYear().getId() + " AND b1.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b2.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b1.MATERIALIZEDPATH LIKE '"
                        + topBudget.getMaterializedPath() + "%' and b2.isbere='BE' AND bd2.budgetgroup =bg.id  "
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd2.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition2
                        + " AND bd1.executing_department = " + budgetDetail.getExecutingDepartment()
                        + functionCondition1 + " AND bd1.uniqueno = bd2.uniqueno AND (wf.value='END' OR wf.owner_pos="
                        + pos.getId() + ") AND bd1.state_id = wf.id GROUP BY bd2.uniqueno");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchUniqueNoAndBEAmount");

        return result;
    }

    public List<Object[]> fetchMajorCodeAndAppropriation(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos, final Date asOnDate) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndAppropriation................");
        StringBuffer query = new StringBuffer();
        String functionCondition1 = "";
        String functionCondition2 = "";
        String dateCondition = "";
        String ReappropriationTable = " ";
        if (function != null) {
            functionCondition1 = " AND bd1.function = " + function.getId();
            functionCondition2 = " AND bd2.function = " + function.getId();
        }
        if (asOnDate != null) {
            ReappropriationTable = " egf_reappropriation_misc bmisc,";
            dateCondition = " and bapp.reappropriation_misc= bmisc.id and  bmisc.reappropriation_date <= '"
                    + Constants.DDMMYYYYFORMAT1.format(asOnDate) + "'";
        }

        query = query
                .append("SELECT cao.majorcode, SUM(bapp.addition_amount)-SUM(bapp.deduction_amount) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, egf_budget_reappropriation bapp, "
                        + ReappropriationTable + " eg_wf_states wf"
                        + " WHERE bd1.budget=b1.id and bd2.budget=b2.id AND f.id   ="
                        + topBudget.getFinancialYear().getId() + " AND b1.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b2.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b1.MATERIALIZEDPATH LIKE '"
                        + topBudget.getMaterializedPath() + "%' and b2.isbere='BE' AND bd2.budgetgroup          =bg.id "
                        + dateCondition
                        + " AND cao.id=bg.mincode AND cao.id=bg.maxcode AND bg.majorcode IS NULL AND bd1.executing_department = "
                        + budgetDetail.getExecutingDepartment() + " " + functionCondition1
                        + " AND bd2.executing_department = " + budgetDetail.getExecutingDepartment() + "" + " "
                        + functionCondition2 + " AND bapp.budgetdetail  = bd2.id AND (wf.value ='END' OR wf.owner_pos ="
                        + pos.getId()
                        + ") AND bd1.state_id             = wf.id and bd1.uniqueno = bd2.uniqueno GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndAppropriation");

        return result;
    }

    public List<Object[]> fetchUniqueNoAndApprAmount(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchUniqueNoAndApprAmount................");
        StringBuffer query = new StringBuffer();
        String functionCondition1 = "";
        String functionCondition2 = "";
        if (function != null) {
            functionCondition1 = " AND bd1.function = " + function.getId();
            functionCondition2 = " AND bd2.function = " + function.getId();
        }

        query = query
                .append("SELECT bd2.uniqueno, SUM(bapp.addition_amount)-SUM(bapp.deduction_amount) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, egf_budget_reappropriation bapp, eg_wf_states wf"
                        + " WHERE bd1.budget      =b1.id and bd2.budget =b2.id AND f.id ="
                        + topBudget.getFinancialYear().getId() + " AND b1.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b2.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b1.MATERIALIZEDPATH LIKE '"
                        + topBudget.getMaterializedPath() + "%' and b2.isbere='BE' AND bd2.budgetgroup          =bg.id "
                        + " AND cao.id                  =bg.mincode AND cao.id                  =bg.maxcode AND bg.majorcode           IS NULL AND bd1.executing_department = "
                        + budgetDetail.getExecutingDepartment() + " " + functionCondition1
                        + " AND bd2.executing_department = " + budgetDetail.getExecutingDepartment() + "" + " "
                        + functionCondition2
                        + " AND bapp.budgetdetail = bd2.id AND (wf.value               ='END' OR wf.owner_pos                 ="
                        + pos.getId()
                        + ") AND bd1.state_id             = wf.id and bd1.uniqueno = bd2.uniqueno GROUP BY bd2.uniqueno");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchUniqueNoAndApprAmount");

        return result;
    }

    public List<Object[]> fetchMajorCodeAndAnticipatory(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndAnticipatory................");
        StringBuffer query = new StringBuffer();
        String functionCondition = "";
        if (function != null)
            functionCondition = " AND bd.function = " + function.getId();
        query = query
                .append("SELECT cao.majorcode, SUM(bd.anticipatory_amount) as anticipatory_amount, SUM(bd.originalamount) as originalamount, SUM(bd.approvedamount) as approvedamount FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, eg_wf_states wf"
                        + " WHERE bd.budget =b.id AND f.id =" + topBudget.getFinancialYear().getId()
                        + " AND b.financialyearid=" + topBudget.getFinancialYear().getId()
                        + " AND b.MATERIALIZEDPATH LIKE '" + topBudget.getMaterializedPath()
                        + "%' AND bd.budgetgroup =bg.id  AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition
                        + " AND (wf.value='END' OR wf.owner_pos=" + pos.getId()
                        + ") AND bd.state_id = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndAnticipatory");

        return result;
    }

    public List<Object[]> fetchMajorCodeAndOriginalAmount(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndOriginalAmount................");
        StringBuffer query = new StringBuffer();
        String functionCondition = "";
        if (function != null)
            functionCondition = " AND bd.function = " + function.getId();

        query = query
                .append("SELECT cao.majorcode, SUM(bd.originalamount) FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, eg_wf_states wf"
                        + " WHERE bd.budget =b.id AND f.id =" + topBudget.getFinancialYear().getId()
                        + " AND b.financialyearid=" + topBudget.getFinancialYear().getId()
                        + " AND b.MATERIALIZEDPATH LIKE '" + topBudget.getMaterializedPath()
                        + "%' AND bd.budgetgroup =bg.id  AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition
                        + " AND (wf.value='END' OR wf.owner_pos=" + pos.getId()
                        + ") AND bd.state_id = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndOriginalAmount");

        return result;
    }

    public List<Object[]> fetchMajorCodeAndBENextYr(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndBENextYr................");
        StringBuffer query = new StringBuffer();
        String functionCondition1 = "";
        String functionCondition2 = "";
        if (function != null) {
            functionCondition1 = " AND bd1.function = " + function.getId();
            functionCondition2 = " AND bd2.function = " + function.getId();
        }

        query = query
                .append("SELECT cao.majorcode, SUM(bd2.originalamount) as originalamount, SUM(bd2.approvedamount) as approvedamount  FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, eg_wf_states wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b1.MATERIALIZEDPATH LIKE '"
                        + topBudget.getMaterializedPath() + "%' AND bd2.budgetgroup =bg.id "
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd2.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition2
                        + " AND bd1.executing_department = " + budgetDetail.getExecutingDepartment()
                        + functionCondition1
                        + " AND bd1.uniqueno = bd2.uniqueno AND b2.reference_budget = b1.id AND (wf.value='END' OR wf.owner_pos="
                        + pos.getId() + ") AND bd1.state_id = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndBENextYr");

        return result;
    }

    public List<Object[]> fetchMajorCodeAndApprovedAmount(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndApprovedAmount................");
        StringBuffer query = new StringBuffer();
        String functionCondition = "";
        if (function != null)
            functionCondition = " AND bd.function = " + function.getId();

        query = query
                .append("SELECT cao.majorcode, SUM(bd.approvedamount) as approvedamount  FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, eg_wf_states wf"
                        + " WHERE bd.budget =b.id AND f.id =" + topBudget.getFinancialYear().getId()
                        + " AND b.financialyearid=" + topBudget.getFinancialYear().getId()
                        + " AND b.MATERIALIZEDPATH LIKE '" + topBudget.getMaterializedPath()
                        + "%' AND bd.budgetgroup =bg.id  AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition
                        + " AND (wf.value='END' OR wf.owner_pos=" + pos.getId()
                        + ") AND bd.state_id = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndApprovedAmount");

        return result;
    }

    public List<Object[]> fetchMajorCodeAndBENextYrApproved(final Budget topBudget, final BudgetDetail budgetDetail,
            final CFunction function, final Position pos) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndBENextYrApproved................");
        StringBuffer query = new StringBuffer();
        String functionCondition1 = "";
        String functionCondition2 = "";
        if (function != null) {
            functionCondition1 = " AND bd1.function = " + function.getId();
            functionCondition2 = " AND bd2.function = " + function.getId();
        }

        query = query
                .append("SELECT cao.majorcode, SUM(bd2.approvedamount) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, eg_wf_states wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.financialyearid="
                        + topBudget.getFinancialYear().getId() + " AND b1.MATERIALIZEDPATH LIKE '"
                        + topBudget.getMaterializedPath() + "%' AND bd2.budgetgroup =bg.id "
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd2.executing_department = "
                        + budgetDetail.getExecutingDepartment() + functionCondition2
                        + " AND bd1.executing_department = " + budgetDetail.getExecutingDepartment()
                        + functionCondition1
                        + " AND bd1.uniqueno = bd2.uniqueno AND b2.reference_budget = b1.id AND (wf.value='END' OR wf.owner_pos="
                        + pos.getId() + ") AND bd1.state_id = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndBENextYrApproved");

        return result;
    }

    // For Consolidate Budget Report.
    public List<Object[]> fetchMajorCodeAndNameForReport(final CFinancialYear financialYear, final String fundType,
            final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndName............");
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        final String excludeDept = " and bd.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        StringBuffer query = new StringBuffer();
        query = query
                .append("SELECT cao.majorcode, cao1.glcode||'-'||cao1.name FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, chartofaccounts cao1, financialyear f, egw_status wf"
                        + " WHERE bd.budget=b.id AND b.isbere='RE' AND f.id=" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode=bg.majorcode) AND bg.mincode!=bg.maxcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY cao.majorcode, cao1.glcode||'-'||cao1.name");

        query = query.append(" UNION ");

        query = query
                .append("SELECT cao.majorcode, cao1.glcode||'-'||cao1.name FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, chartofaccounts cao1, financialyear f, egw_status wf"
                        + " WHERE bd.budget=b.id AND b.isbere='RE' AND f.id=" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup=bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND cao.id=bg.mincode AND cao.id=bg.maxcode AND bg.majorcode IS NULL and cao1.glcode = cao.majorcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY cao.majorcode, cao1.glcode||'-'||cao1.name");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndName");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchMajorCodeAndActualsForReport(final CFinancialYear financialYear,
            final CFinancialYear prevFinYear, final String fundType, final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndActuals................");
        final String excludeDept = " and bd.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        String condition = " SUM(gl.debitAmount)-SUM(gl.creditAmount) ";
        if (budgetingType.contains("RECEIPT"))
            condition = " SUM(gl.creditAmount)-SUM(gl.debitAmount) ";
        StringBuffer query = new StringBuffer();
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "exclude_status_forbudget_actual");
        if (list.isEmpty())
            throw new ValidationException("", "exclude_status_forbudget_actual is not defined in AppConfig");
        final String voucherstatusExclude = list.get(0).getValue();
        query = query.append("SELECT substr(gl.glcode,1,3), " + condition
                + " FROM egf_budgetdetail bd, vouchermis vmis,"
                + " (SELECT bg1.id AS id, bg1.accounttype AS accounttype, case when c1.glcode =  NULL then -1 else to_number(c1.glcode,'999999999') end  AS mincode, case when c2.glcode = null then  999999999 else c2.glcode end AS maxcode, case when c3.glcode = null then -1 else to_number(c3.glcode,'999999999') end  AS majorcode"
                + " FROM egf_budgetgroup bg1 LEFT OUTER JOIN chartofaccounts c1 ON c1.id=bg1.mincode LEFT OUTER JOIN chartofaccounts c2 ON c2.id=bg1.maxcode LEFT OUTER JOIN chartofaccounts c3 ON c3.id=bg1.majorcode) bg ,"
                + " egf_budget b, financialyear f, fiscalperiod p, budgetDetail vh, generalledger gl, egw_status wf"
                + " WHERE bd.budget =b.id AND b.isbere='RE' AND p.financialyearid=f.id AND f.id =" + prevFinYear.getId()
                + " AND vh.fiscalperiodid=p.id AND b.financialyearid=" + financialYear.getId()
                + " AND vmis.budgetDetailID=vh.id AND gl.budgetDetailID  =vh.id"
                + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                + " AND vh.status NOT IN (" + voucherstatusExclude
                + ") AND vh.fundId =bd.fund AND vmis.departmentid =bd.executing_department AND gl.functionid = bd.function "
                + " AND ((gl.glcode BETWEEN bg.mincode AND bg.maxcode) OR gl.glcode =bg.majorcode) AND bg.mincode!=bg.maxcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(gl.glcode,1,3)");

        query = query.append(" UNION ");

        query = query.append("SELECT substr(gl.glcode,1,3), " + condition
                + " FROM egf_budgetdetail bd, vouchermis vmis, egf_budgetgroup bg, egf_budget b, financialyear f, fiscalperiod p, budgetDetail vh, generalledger gl, egw_status wf"
                + " WHERE bd.budget      =b.id AND b.isbere='RE' AND p.financialyearid=f.id AND f.id             ="
                + prevFinYear.getId() + " AND vh.fiscalperiodid=p.id AND b.financialyearid=" + financialYear.getId()
                + " AND vmis.budgetDetailID=vh.id AND gl.budgetDetailID  =vh.id"
                + " AND bd.budgetgroup      =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                + " AND vh.status NOT      IN (" + voucherstatusExclude
                + ") AND vh.fundId           =bd.fund AND gl.functionid = bd.function "
                + " AND vmis.departmentid   =bd.executing_department AND gl.glcodeid         =bg.mincode AND gl.glcodeid =bg.maxcode AND bg.majorcode       IS NULL AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(gl.glcode,1,3)");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndActuals");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchMajorCodeAndBEAmountForReport(final CFinancialYear financialYear, final String fundType,
            final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndBEAmount................");
        final String excludeDept = " and bd2.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();

        query = query
                .append("SELECT cao.majorcode, SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND f.id ="
                        + financialYear.getId() + " AND b1.financialyearid=" + financialYear.getId()
                        + " AND b2.financialyearid=" + financialYear.getId()
                        + " AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode   =bg.majorcode) AND bd1.uniqueno = bd2.uniqueno AND wf.code='Approved' AND bd1.status = wf.id GROUP BY cao.majorcode");

        query = query.append(" UNION ");

        query = query
                .append("SELECT cao.majorcode, SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND f.id ="
                        + financialYear.getId() + " AND b1.financialyearid=" + financialYear.getId()
                        + " AND b2.financialyearid=" + financialYear.getId()
                        + "  AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd1.uniqueno = bd2.uniqueno AND wf.value='Approved' AND bd1.status = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "------------------------------------------------------------------------------------------------------");
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndBEAmount" + query.toString());
        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "------------------------------------------------------------------------------------------------------");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchMajorCodeAndApprovedAmountForReport(final CFinancialYear financialYear,
            final String fundType, final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndApprovedAmount................");
        final String excludeDept = " and bd.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();

        query = query
                .append("SELECT cao.majorcode, SUM(round(bd.approvedamount/1000,0)) FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd.budget =b.id AND b.isbere='RE' AND f.id =" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode =bg.majorcode) AND bg.mincode! =bg.maxcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY cao.majorcode");

        query = query.append(" UNION ");

        query = query
                .append("SELECT cao.majorcode, SUM(round(bd.approvedamount/1000,0)) FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd.budget =b.id AND b.isbere='RE' AND f.id =" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND wf.code='Approved' AND bd.status = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndApprovedAmount");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchMajorCodeAndBENextYrApprovedForReport(final CFinancialYear financialYear,
            final String fundType, final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchMajorCodeAndBENextYrApproved................");
        final String excludeDept = " and bd2.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();

        query = query
                .append("SELECT cao.majorcode, SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND b1.financialyearid="
                        + financialYear.getId() + " AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType
                        + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode   =bg.majorcode) AND bd1.uniqueno = bd2.uniqueno AND b2.reference_budget = b1.id AND wf.code='Approved' AND bd1.status = wf.id GROUP BY cao.majorcode");

        query = query.append(" UNION ");

        query = query
                .append("SELECT cao.majorcode, SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND b1.financialyearid="
                        + financialYear.getId() + " AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType
                        + "'" + excludeDept
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd1.uniqueno = bd2.uniqueno AND b2.reference_budget = b1.id AND wf.code='Approved' AND bd1.status = wf.id GROUP BY cao.majorcode");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchMajorCodeAndBENextYrApproved");

        return result;
    }

    // For Consolidate Budget Report.
    public List<Object[]> fetchGlCodeAndNameForReport(final CFinancialYear financialYear, final String fundType,
            final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchGlCodeAndNameForReport............");
        final String excludeDept = " and bd.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();
        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), cao.glcode||'-'||cao.name FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, chartofaccounts cao1, financialyear f, egw_status wf"
                        + " WHERE bd.budget=b.id AND b.isbere='RE' AND f.id=" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode=bg.majorcode) AND bg.mincode!=bg.maxcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), cao.glcode||'-'||cao.name");

        query = query.append(" UNION ");

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), cao.glcode||'-'||cao.name FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, chartofaccounts cao1, financialyear f, egw_status wf"
                        + " WHERE bd.budget=b.id AND b.isbere='RE' AND f.id=" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup=bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND cao.id=bg.mincode AND cao.id=bg.maxcode AND bg.majorcode IS NULL and cao1.glcode = cao.majorcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), cao.glcode||'-'||cao.name");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchGlCodeAndNameForReport");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchActualsForReport(final CFinancialYear financialYear, final CFinancialYear prevFinYear,
            final String fundType, final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchActualsForReport................");
        final String excludeDept = " and bd.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        String condition = " SUM(gl.debitAmount)-SUM(gl.creditAmount) ";
        if (budgetingType.contains("RECEIPT"))
            condition = " SUM(gl.creditAmount)-SUM(gl.debitAmount) ";
        StringBuffer query = new StringBuffer();
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "exclude_status_forbudget_actual");
        if (list.isEmpty())
            throw new ValidationException("", "exclude_status_forbudget_actual is not defined in AppConfig");
        final String voucherstatusExclude = list.get(0).getValue();
        query = query
                .append("SELECT substr(gl.glcode,0,3)||'-'||substr(gl.glcode,4,2)||'-'||substr(gl.glcode,6,2)||'-'||substr(gl.glcode,8,2),"
                        + condition + " FROM egf_budgetdetail bd, vouchermis vmis,"
                        + " (SELECT bg1.id AS id, bg1.accounttype AS accounttype, case when c1.glcode =  NULL then -1 else to_number(c1.glcode,'999999999') end AS mincode, case when c2.glcode = null then  999999999 else c2.glcode end AS maxcode, case when c3.glcode = null then -1 else to_number(c3.glcode,'999999999') end  AS majorcode"
                        + " FROM egf_budgetgroup bg1 LEFT OUTER JOIN chartofaccounts c1 ON c1.id=bg1.mincode LEFT OUTER JOIN chartofaccounts c2 ON c2.id=bg1.maxcode LEFT OUTER JOIN chartofaccounts c3 ON c3.id=bg1.majorcode) bg ,"
                        + " egf_budget b, financialyear f, fiscalperiod p, budgetDetail vh, generalledger gl, egw_status wf"
                        + " WHERE bd.budget =b.id AND b.isbere='RE' AND p.financialyearid=f.id AND f.id ="
                        + prevFinYear.getId() + " AND vh.fiscalperiodid=p.id AND b.financialyearid="
                        + financialYear.getId() + " AND vmis.budgetDetailID=vh.id AND gl.budgetDetailID  =vh.id"
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND vh.status NOT IN (" + voucherstatusExclude
                        + ") AND vh.fundId =bd.fund AND vmis.departmentid =bd.executing_department AND gl.functionid = bd.function "
                        + " AND ((gl.glcode BETWEEN bg.mincode AND bg.maxcode) OR gl.glcode =bg.majorcode) AND bg.mincode!=bg.maxcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(gl.glcode,0,3)||'-'||substr(gl.glcode,4,2)||'-'||substr(gl.glcode,6,2)||'-'||substr(gl.glcode,8,2)");

        query = query.append(" UNION ");

        query = query
                .append("SELECT substr(gl.glcode,0,3)||'-'||substr(gl.glcode,4,2)||'-'||substr(gl.glcode,6,2)||'-'||substr(gl.glcode,8,2),"
                        + condition
                        + " FROM egf_budgetdetail bd, vouchermis vmis, egf_budgetgroup bg, egf_budget b, financialyear f, fiscalperiod p, budgetDetail vh, generalledger gl, egw_status wf"
                        + " WHERE bd.budget      =b.id AND b.isbere='RE' AND p.financialyearid=f.id AND f.id ="
                        + prevFinYear.getId() + " AND vh.fiscalperiodid=p.id AND b.financialyearid="
                        + financialYear.getId() + " AND vmis.budgetDetailID=vh.id AND gl.budgetDetailID  =vh.id"
                        + " AND bd.budgetgroup      =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND vh.status NOT      IN (" + voucherstatusExclude
                        + ") AND vh.fundId           =bd.fund AND gl.functionid = bd.function "
                        + " AND vmis.departmentid   =bd.executing_department AND gl.glcodeid  =bg.mincode AND gl.glcodeid =bg.maxcode AND bg.majorcode IS NULL AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(gl.glcode,0,3)||'-'||substr(gl.glcode,4,2)||'-'||substr(gl.glcode,6,2)||'-'||substr(gl.glcode,8,2)");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchActualsForReport");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchGlCodeAndBEAmountForReport(final CFinancialYear financialYear, final String fundType,
            final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchGlCodeAndBEAmountForReport................");
        final String excludeDept = " and bd2.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND f.id ="
                        + financialYear.getId() + " AND b1.financialyearid=" + financialYear.getId()
                        + " AND b2.financialyearid=" + financialYear.getId()
                        + " AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode   =bg.majorcode) AND bd1.uniqueno = bd2.uniqueno AND wf.code='Approved' AND bd1.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2)");

        query = query.append(" UNION ");

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND f.id ="
                        + financialYear.getId() + " AND b1.financialyearid=" + financialYear.getId()
                        + " AND b2.financialyearid=" + financialYear.getId()
                        + "  AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd1.uniqueno = bd2.uniqueno AND wf.code='Approved' AND bd1.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2)");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "------------------------------------------------------------------------------------------------------");
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchGlCodeAndBEAmountForReport" + query.toString());
        if (LOGGER.isInfoEnabled())
            LOGGER.info(
                    "------------------------------------------------------------------------------------------------------");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchGlCodeAndApprovedAmountForReport(final CFinancialYear financialYear,
            final String fundType, final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchGlCodeAndApprovedAmountForReport................");
        final String excludeDept = " and bd.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), SUM(round(bd.approvedamount/1000,0)) FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd.budget =b.id AND b.isbere='RE' AND f.id =" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode =bg.majorcode) AND bg.mincode! =bg.maxcode AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2)");

        query = query.append(" UNION ");

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), SUM(round(bd.approvedamount/1000,0)) FROM egf_budgetdetail bd, egf_budgetgroup bg, egf_budget b, chartofaccounts cao, financialyear f, egw_status wf"
                        + " WHERE bd.budget =b.id AND b.isbere='RE' AND f.id =" + financialYear.getId()
                        + " AND b.financialyearid=" + financialYear.getId()
                        + " AND bd.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType + "'" + excludeDept
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND wf.code='Approved' AND bd.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2)");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchGlCodeAndApprovedAmountForReport");

        return result;
    }

    // For Consolidated Budget Report
    public List<Object[]> fetchGlCodeAndBENextYrApprovedForReport(final CFinancialYear financialYear,
            final String fundType, final String budgetType) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting fetchGlCodeAndBENextYrApprovedForReport................");
        final String excludeDept = " and bd2.executing_department!=(Select id_dept from eg_department where dept_code='Z') ";
        final String budgetingType = fundType.toUpperCase() + "_" + budgetType.toUpperCase();
        StringBuffer query = new StringBuffer();

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND b1.financialyearid="
                        + financialYear.getId() + " AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType
                        + "'" + excludeDept
                        + " AND ((cao.id BETWEEN bg.mincode AND bg.maxcode) OR cao.majorcode   =bg.majorcode) AND bd1.uniqueno = bd2.uniqueno AND b2.reference_budget = b1.id AND wf.code='Approved' AND bd1.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2)");

        query = query.append(" UNION ");

        query = query
                .append("SELECT substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2), SUM(round(bd2.approvedamount/1000,0)) FROM egf_budgetdetail bd1, egf_budgetdetail bd2, egf_budgetgroup bg, egf_budget b1, egf_budget b2, chartofaccounts cao, egw_status wf"
                        + " WHERE bd1.budget =b1.id AND bd2.budget =b2.id AND b1.isbere='RE' AND b2.isbere='BE' AND b1.financialyearid="
                        + financialYear.getId() + " AND bd2.budgetgroup =bg.id AND bg.ACCOUNTTYPE ='" + budgetingType
                        + "'" + excludeDept
                        + " AND cao.id =bg.mincode AND cao.id =bg.maxcode AND bg.majorcode IS NULL AND bd1.uniqueno = bd2.uniqueno AND b2.reference_budget = b1.id AND wf.code='Approved' AND bd1.status = wf.id GROUP BY substr(cao.glcode,0,3)||'-'||substr(cao.glcode,4,2)||'-'||substr(cao.glcode,6,2)||'-'||substr(cao.glcode,8,2)");

        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Finished fetchGlCodeAndBENextYrApprovedForReport");

        return result;
    }

    public List<Object[]> fetchActualsForBill(final String fromDate, final String toVoucherDate,
            final List<String> mandatoryFields) {
        final StringBuffer miscQuery = getMiscQuery(mandatoryFields, "bmis", "bdetail", "bmis");
        StringBuffer query = new StringBuffer();
        query = query
                .append("select bd.id,SUM(case when bdetail.debitAmount = null then 0  else bdetail.debitAmount  end)-SUM(case when bdetail.creditAmount=null then 0 else bdetail.creditAmount end) from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                        + "egf_budgetgroup bg where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and br.billstatus != 'Cancelled'  and "
                        + "bmis.budgetDetailid is null and br.billdate>=to_date('" + fromDate
                        + "','dd/MM/yyyy') and br.billdate <= to_date('" + toVoucherDate + "','dd/MM/yyyy') "
                        + miscQuery + " and " + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and "
                        + "((bdetail.glcodeid between bg.mincode and bg.maxcode) or bdetail.glcodeid=bg.majorcode) group by bd.id"
                        + " union "
                        + "select bd.id,SUM(case when bdetail.creditAmount=null then 0 else bdetail.creditAmount end)-SUM(case when bdetail.debitAmount = null then 0  else bdetail.debitAmount  end) from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                        + "egf_budgetgroup bg where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                        + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and "
                        + "(bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and br.billstatus != 'Cancelled' and bmis.budgetDetailid "
                        + "is null and br.billdate>= to_date('" + fromDate
                        + "','dd/MM/yyyy') and br.billdate <= to_date('" + toVoucherDate + "','dd/MM/yyyy') "
                        + miscQuery + " and ((bdetail.glcodeid between bg.mincode "
                        + "and bg.maxcode) or bdetail.glcodeid=bg.majorcode) group by bd.id");
        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        return result;
    }

    public List<Object[]> fetchActualsForFYWithParams(final String fromDate, final String toVoucherDate,
            final StringBuffer miscQuery) {
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "exclude_status_forbudget_actual");
        if (list.isEmpty())
            throw new ValidationException("", "exclude_status_forbudget_actual is not defined in AppConfig");
        final StringBuffer budgetGroupQuery = new StringBuffer();
        budgetGroupQuery
                .append(" (select bg1.id as id,bg1.accounttype as accounttype ,c1.glcode as mincode, c2.glcode as maxcode,c3.glcode as majorcode "
                        + "from egf_budgetgroup bg1 left outer join chartofaccounts c1 on c1.id=bg1.mincode left outer join chartofaccounts c2 on "
                        + "c2.id=bg1.maxcode left outer join chartofaccounts  c3 on c3.id=bg1.majorcode )  bg ");
        final String voucherstatusExclude = list.get(0).getValue();
        StringBuffer query = new StringBuffer();
        query = query
                .append("select bd.id as id,(SUM(gl.debitAmount)-SUM(gl.creditAmount)) as amount from egf_budgetdetail bd,generalledger gl,budgetDetail vh,"
                        + "vouchermis vmis," + budgetGroupQuery
                        + ",egf_budget b where bd.budget=b.id and vmis.budgetDetailID=vh.id and gl.budgetDetailID=vh.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and vh.status not in ("
                        + voucherstatusExclude
                        + ") and (vmis.budgetary_appnumber  != 'null' and vmis.budgetary_appnumber is not null) and "
                        + "vh.voucherDate>= to_date('" + fromDate + "','dd/MM/yyyy') and vh.voucherDate <= to_date("
                        + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                        + " and (gl.glcode =bg.mincode or gl.glcode=bg.majorcode ) group by bd.id" + " union "
                        + "select bd.id as id,(SUM(gl.creditAmount)-SUM(gl.debitAmount)) as amount from egf_budgetdetail bd,generalledger gl,budgetDetail vh,"
                        + "vouchermis vmis," + budgetGroupQuery
                        + ",egf_budget b where bd.budget=b.id and vmis.budgetDetailID=vh.id and gl.budgetDetailID=vh.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and vh.status not in ("
                        + voucherstatusExclude
                        + ") and (vmis.budgetary_appnumber  != 'null' and vmis.budgetary_appnumber is not null) and "
                        + "vh.voucherDate>= to_date('" + fromDate + "','dd/MM/yyyy') and vh.voucherDate <= to_date("
                        + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                        + " and (gl.glcode = bg.mincode  or gl.glcode=bg.majorcode ) group by bd.id");
        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();

        return result;
    }

    public List<Object[]> fetchActualsForBillWithParams(final String fromDate, final String toVoucherDate,
            final StringBuffer miscQuery) {
        StringBuffer query = new StringBuffer();
        query = query.append("select bud,sum(amt) from ("
                + "select bd.id as bud,SUM(case when bdetail.debitAmount = null then 0  else bdetail.debitAmount  end)-SUM(case when bdetail.creditAmount=null then 0 else bdetail.creditAmount end) as amt from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                + "egf_budgetgroup bg where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                + "bmis.budgetDetailid is null and br.billdate>=to_date('" + fromDate
                + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                + " and " + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and "
                + "((bdetail.glcodeid between bg.mincode and bg.maxcode) or bdetail.glcodeid=bg.majorcode) group by bd.id"
                + " union "
                + "select bd.id as bud,SUM(case when bdetail.debitAmount = null then 0  else bdetail.debitAmount  end)-SUM(case when bdetail.creditAmount=null then 0 else bdetail.creditAmount end) as amt from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                + "egf_budgetgroup bg,budgetDetail vh where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                + "bmis.budgetDetailid =vh.id and vh.status=4 and br.billdate>=to_date('" + fromDate
                + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                + " and " + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and "
                + "((bdetail.glcodeid between bg.mincode and bg.maxcode) or bdetail.glcodeid=bg.majorcode) group by bd.id"
                + " union "
                + "select bd.id as bud,SUM(case when bdetail.creditAmount=null then 0 else bdetail.creditAmount end)-SUM(case when bdetail.debitAmount = null then 0  else bdetail.debitAmount  end) as amt from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                + "egf_budgetgroup bg,budgetDetail vh where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and "
                + "(bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                + " bmis.budgetDetailid =vh.id and vh.status=4 and br.billdate>= to_date('" + fromDate
                + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                + " and ((bdetail.glcodeid between bg.mincode "
                + "and bg.maxcode) or bdetail.glcodeid=bg.majorcode) group by bd.id" + " union "
                + "select bd.id as bud,SUM(case when bdetail.creditAmount=null then 0 else bdetail.creditAmount end)-SUM(case when bdetail.debitAmount = null then 0  else bdetail.debitAmount  end) as amt from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                + "egf_budgetgroup bg where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and "
                + "(bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and bmis.budgetDetailid "
                + "is null and br.billdate>= to_date('" + fromDate + "','dd/MM/yyyy') and br.billdate <= to_date("
                + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery + " and ((bdetail.glcodeid between bg.mincode "
                + "and bg.maxcode) or bdetail.glcodeid=bg.majorcode) group by bd.id" + " ) group by bud ");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(" Main Query :" + query);
        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        return result;
    }

    /*
     * Similar to fetchActualsForBillWithParams() except that this will only consider bills for which vouchers are present and the
     * vouchers are uncancelled and BAN numbers are present for the bills and not vouchers
     */
    public List<Object[]> fetchActualsForBillWithVouchersParams(final String fromDate, final String toVoucherDate,
            final StringBuffer miscQuery) {
        StringBuffer query = new StringBuffer();
        query = query
                .append("select bd.id as bud,SUM(case when bdetail.debitAmount is null then 0  else bdetail.debitAmount  end)   -SUM(case when bdetail.creditAmount is null then 0 else bdetail.creditAmount end)   as amt from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                        + "egf_budgetgroup bg,budgetDetail vh, vouchermis vmis where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                        + "bmis.budgetDetailid =vh.id and vh.status!=4 and br.billdate>=to_date('" + fromDate
                        + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                        + " and (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and vh.id = vmis.budgetDetailid and (bmis.budgetary_appnumber != 'null' and bmis.budgetary_appnumber is not null) "
                        + " and ((bdetail.glcodeid between bg.mincode  and bg.maxcode ) or bdetail.glcodeid=bg.majorcode ) group by bd.id"
                        + " UNION "
                        + "select bd.id as bud,SUM(case when bdetail.creditAmount is null then 0 else bdetail.creditAmount end)-SUM(case when bdetail.debitAmount is null then 0  else bdetail.debitAmount  end) as amt from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregistermis bmis, eg_billregister br,"
                        + "egf_budgetgroup bg,budgetDetail vh, vouchermis vmis where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                        + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and vh.id = vmis.budgetDetailid and (bmis.budgetary_appnumber != 'null' and bmis.budgetary_appnumber is not null) "
                        + " and (bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and br.statusid not in (select id as idd from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                        + " bmis.budgetDetailid =vh.id and vh.status!=4 and br.billdate>= to_date('" + fromDate
                        + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                        + " and ((bdetail.glcodeid between bg.mincode and bg.maxcode ) or bdetail.glcodeid=bg.majorcode  ) group by bd.id"
                        + " UNION "
                        + " select bd.id as bud,SUM(case when bdetail.debitAmount is null then 0  else bdetail.debitAmount  end)   -SUM(case when bdetail.creditAmount is null then 0 else bdetail.creditAmount end)   as amt "
                        + " from egf_budgetdetail bd,eg_billdetails bdetail, eg_billregister br,egf_budgetgroup bg, eg_billregistermis bmis left outer join budgetDetail vh on vh.id=bmis.budgetDetailid "
                        + " where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                        + "(bg.ACCOUNTTYPE='REVENUE_EXPENDITURE' or bg.ACCOUNTTYPE='CAPITAL_EXPENDITURE') and br.statusid not in (select id from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                        + "(bmis.budgetDetailid is NULL or vh.status=4) and  br.billdate>=to_date('" + fromDate
                        + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                        + " and (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and (bmis.budgetary_appnumber != 'null' and bmis.budgetary_appnumber is not null) "
                        + " and ((bdetail.glcodeid between bg.mincode  and bg.maxcode ) or bdetail.glcodeid=bg.majorcode ) group by bd.id"
                        + " UNION "
                        + "select bd.id as bud,SUM(case when bdetail.creditAmount is null then 0 else bdetail.creditAmount end)-SUM(case when bdetail.debitAmount is null then 0  else bdetail.debitAmount  end) as amt"
                        + " from egf_budgetdetail bd,eg_billdetails bdetail, egf_budgetgroup bg, eg_billregister br,eg_billregistermis bmis  left outer join budgetDetail vh on vh.id=bmis.budgetDetailid "
                        + " where bmis.billid=br.id and bdetail.billid=br.id and bd.budgetgroup=bg.id and "
                        + " (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true) and (bmis.budgetary_appnumber != 'null' and bmis.budgetary_appnumber is not null) "
                        + " and (bg.ACCOUNTTYPE='REVENUE_RECEIPTS' or bg.ACCOUNTTYPE='CAPITAL_RECEIPTS') and br.statusid not in (select id as idd from egw_status where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))  and "
                        + " (bmis.budgetDetailid is NULL or vh.status=4)  and  br.billdate>= to_date('" + fromDate
                        + "','dd/MM/yyyy') and br.billdate <= to_date(" + toVoucherDate + ",'dd/MM/yyyy') " + miscQuery
                        + " and ((bdetail.glcodeid between bg.mincode and bg.maxcode ) or bdetail.glcodeid=bg.majorcode  ) group by bd.id");

        if (LOGGER.isDebugEnabled())
            LOGGER.debug(" Main Query :" + query);
        final List<Object[]> result = getSession().createSQLQuery(query.toString()).list();
        return result;
    }

    private StringBuffer getMiscQuery(final List<String> mandatoryFields, final String mis, final String gl,
            final String detail) {
        StringBuffer miscQuery = new StringBuffer();
        if (mandatoryFields.contains(Constants.FIELD))
            miscQuery = miscQuery.append(" and " + mis + ".divisionid=bd.boundary ");
        if (mandatoryFields.contains(Constants.FUND))
            miscQuery = miscQuery.append(" and " + detail + ".fundId=bd.fund ");
        if (mandatoryFields.contains(Constants.SCHEME))
            miscQuery = miscQuery.append(" and " + mis + ".schemeid=bd.scheme ");
        if (mandatoryFields.contains(Constants.SUB_SCHEME))
            miscQuery = miscQuery.append(" and " + mis + ".subschemeid=bd.subscheme ");
        if (mandatoryFields.contains(Constants.FUNCTIONARY))
            miscQuery = miscQuery.append(" and " + mis + ".functionaryid=bd.functionary ");
        if (mandatoryFields.contains(Constants.FUNCTION))
            miscQuery = miscQuery.append(" and " + gl + ".functionId=bd.function ");
        if (mandatoryFields.contains(Constants.EXECUTING_DEPARTMENT))
            miscQuery = miscQuery.append(" and " + mis + ".departmentcode=bd.executing_department ");
        return miscQuery;
    }

    public PersonalInformation getEmpForCurrentUser() {
        return eisCommonService.getEmployeeByUserId(ApplicationThreadLocals.getUserId());
    }

    public SimpleWorkflowService<BudgetDetail> getBudgetDetailWorkflowService() {
		return budgetDetailWorkflowService;
	}

	public void setBudgetDetailWorkflowService(SimpleWorkflowService<BudgetDetail> budgetDetailWorkflowService) {
		this.budgetDetailWorkflowService = budgetDetailWorkflowService;
	}
	
    public void setPersistenceService(final PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setScriptExecutionService(final ScriptService scriptService) {
    }

    public boolean toBeConsolidated() {
        // TODO: Now employee is extending user so passing userid to get
        // assingment -- changes done by Vaibhav
        final Assignment empAssignment = eisCommonService
                .getLatestAssignmentForEmployeeByToDate(ApplicationThreadLocals.getUserId(), new Date());
        final Functionary empfunctionary = empAssignment.getFunctionary();
        final Designation designation =null;// empAssignment.getDesignation();
        Boolean consolidateBudget = Boolean.FALSE;
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "budget_toplevel_approver_designation");
        if (list.isEmpty())
            throw new ValidationException("", "budget_toplevel_approver_designation is not defined in AppConfig");

        final List<AppConfigValues> list2 = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "budget_secondlevel_approver_designation");
        if (list2.isEmpty())
            throw new ValidationException("", "budget_secondlevel_approver_designation is not defined in AppConfig");

        // String[] functionAndDesg=list2.get(0).getValue().split(",");
        final String[] functionaryDesignationObj = list2.get(0).getValue().split(",");
        for (final String strObj : functionaryDesignationObj)
            if (strObj.contains(":")) {
                final String[] functionaryName = strObj.split(":");
                if (empfunctionary != null && empfunctionary.getName().equalsIgnoreCase(functionaryName[0])) {
                    consolidateBudget = Boolean.TRUE;
                    break;
                }
            } else if (designation.getName().equalsIgnoreCase(strObj)) {
                consolidateBudget = Boolean.TRUE;
                break;
            } else
                consolidateBudget = Boolean.FALSE;

        return consolidateBudget;
    }

    @Transactional
    public List<BudgetUpload> loadBudget(List<BudgetUpload> budgetUploadList, final CFinancialYear reFYear,
            final CFinancialYear beFYear) {

        try {

            final Budget budget = budgetService.getByName("RE-" + reFYear.getFinYearRange());
            if (budget == null) {
                final Set<String> deptSet = new TreeSet<String>();
                final List<String> deptList = new ArrayList<String>();
                final List<Department> departments =   masterDataCache.get("egi-department");

                for (final Department dept : departments)
                    deptSet.add(dept.getCode());

                deptList.addAll(deptSet);
                final EgwStatus budgetStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGET", "CAO Verify");
                createRootBudget(RE, beFYear, reFYear, deptList, budgetStatus);

                createRootBudget(BE, beFYear, reFYear, deptList, budgetStatus);

            }
            final EgwStatus budgetDetailStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO Verify");

			/*
			 * budgetUploadList = createBudgetDetails(RE, budgetUploadList, reFYear,
			 * budgetDetailStatus);
			 * 
			 * budgetUploadList = createBudgetDetails(BE, budgetUploadList, beFYear,
			 * budgetDetailStatus);
			 */

        } catch (final ValidationException e) {
        	System.out.println("issue V4 :"+e.getMessage());
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        return budgetUploadList;
    }
    
    @Transactional
    public List<BudgetUpload> loadBudgetNew(BudgetDetail budgetdetail, List<BudgetUpload> budgetUploadList, final CFinancialYear reFYear,
            final CFinancialYear beFYear, WorkflowBean workflowBean) {

        try {

            final Budget budget = budgetService.getByName("RE-" + reFYear.getFinYearRange());
            if (budget == null) {
                final Set<String> deptSet = new TreeSet<String>();
                final List<String> deptList = new ArrayList<String>();
                final List<Department> departments =   masterDataCache.get("egi-department");

                for (final Department dept : departments)
                    deptSet.add(dept.getCode());

                deptList.addAll(deptSet);
                final EgwStatus budgetStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGET", "Created");
                createRootBudget(RE, beFYear, reFYear, deptList, budgetStatus);

                createRootBudget(BE, beFYear, reFYear, deptList, budgetStatus);

            }
            try {
            	
            budgetdetail = transitionWorkFlow(budgetdetail, workflowBean);
            BudgetDetailService.applyAuditingNew(budgetdetail.getState());
            }
            catch(Exception e)
            {
            	System.out.println("error "+e.getMessage());
            	e.printStackTrace();
            }
            final EgwStatus budgetDetailStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Created");

			/* old code
			 * budgetUploadList = createBudgetDetails(RE, budgetUploadList, reFYear,
			 * budgetDetailStatus);
			 * 
			 * budgetUploadList = createBudgetDetails(BE, budgetUploadList, beFYear,
			 * budgetDetailStatus);
			 */
            
            budgetUploadList = createBudgetDetails(RE, budgetUploadList, reFYear, budgetDetailStatus,budgetdetail);

            budgetUploadList = createBudgetDetails(BE, budgetUploadList, beFYear, budgetDetailStatus,budgetdetail);

        } catch (final ValidationException e) {
        	System.out.println("issue V4 :"+e.getMessage());
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        return budgetUploadList;
    }

    
        @Transactional
    public List<BudgetUpload> createBudgetDetails(final String budgetType, final List<BudgetUpload> budgetUploadList,
            final CFinancialYear fyear, final EgwStatus status, final BudgetDetail budgetDetailPrev) {
        final List<BudgetUpload> tempList = new ArrayList<BudgetUpload>();
        try {
        	System.out.println("bbbbbb");
            for (final BudgetUpload budgetUpload : budgetUploadList) {
            	System.out.println("cccccc");
                BudgetDetail budgetDetail = new BudgetDetail();
                final BudgetDetail temp = getBudgetDetail(budgetUpload.getFund().getId(),
                        budgetUpload.getFunction().getId(), budgetUpload.getDeptCode(),
                        budgetUpload.getCoa().getId(), fyear, budgetType);

                if (temp != null) {
                	System.out.println("ddddd");
                    if (temp.getStatus().getCode().equalsIgnoreCase("CAO Verify")) {
                    	System.out.println("ffff");
                        BigDecimal amount;
                        if (budgetType.equalsIgnoreCase(RE))
                            amount = budgetUpload.getReAmount();
                        else
                            amount = budgetUpload.getBeAmount();

                        if (amount.compareTo(temp.getApprovedAmount()) != 0) {
                            temp.setApprovedAmount(amount);
                            temp.setOriginalAmount(amount);
                            temp.setBudgetAvailable(temp.getApprovedAmount().multiply(temp.getPlanningPercent())
                                    .divide(new BigDecimal(String.valueOf(100))));

                            applyAuditing(temp);
                            budgetDetail = update(temp);
                            budgetUpload.setFinalStatus("Success");
                            tempList.add(budgetUpload);
                        } else {
                            budgetUpload.setFinalStatus("Already budget is defined for this combination");
                            tempList.add(budgetUpload);
                        }
                    } else {
                    	System.out.println("ggggg");
                        budgetUpload.setFinalStatus("Already budget is defined for this combination and Approved");
                        tempList.add(budgetUpload);
                    }

                } else if (temp == null) {
                	System.out.println("eeee");
                    budgetDetail.setFund(budgetUpload.getFund());
                    budgetDetail.setFunction(budgetUpload.getFunction());
                    budgetDetail.setExecutingDepartment(budgetUpload.getDeptCode());
                    budgetDetail.setAnticipatoryAmount(BigDecimal.ZERO);
                    budgetDetail.setPlanningPercent(BigDecimal.valueOf(budgetUpload.getPlanningPercentage()));
                  //  budgetDetail.setQuarterpercent(BigDecimal.valueOf(budgetUpload.getQuarterpercent()));
                    
                    //Author - Bhushan >set four  quater to budget details
                    budgetDetail.setQuarterpercent(BigDecimal.valueOf(budgetUpload.getQuarterOnepercent()));
                    budgetDetail.setQuartertwopercent(BigDecimal.valueOf(budgetUpload.getQuarterTwopercent()));
                    budgetDetail.setQuarterthreepercent(BigDecimal.valueOf(budgetUpload.getQuarterThreepercent()));
                    budgetDetail.setQuarterfourpercent(BigDecimal.valueOf(budgetUpload.getQuarterFourpercent()));
                    
                    
                    if (budgetType.equalsIgnoreCase(RE)) {
                        budgetDetail.setOriginalAmount(budgetUpload.getReAmount());
                        budgetDetail.setApprovedAmount(budgetUpload.getReAmount());
                        budgetDetail.setBudgetAvailable(
                                budgetUpload.getReAmount().multiply(budgetDetail.getPlanningPercent())
                                        .divide(new BigDecimal(String.valueOf(100))));

                    } else {
                        budgetDetail.setOriginalAmount(budgetUpload.getBeAmount());
                        budgetDetail.setApprovedAmount(budgetUpload.getBeAmount());
                        budgetDetail.setBudgetAvailable(
                                budgetUpload.getBeAmount().multiply(budgetDetail.getPlanningPercent())
                                        .divide(new BigDecimal(String.valueOf(100))));
                    }
                    budgetDetail.setBudgetGroup(createBudgetGroup(budgetUpload.getCoa()));
                    budgetDetail.setBudget(budgetService.getBudget(budgetUpload.getBudgetHead(),
                            budgetUpload.getDeptCode(), budgetType, fyear.getFinYearRange()));
                    budgetDetail.setMaterializedPath(getmaterializedpathforbudget(budgetDetail.getBudget()));
                    budgetDetail.setStatus(status);
                    budgetDetail.setState(budgetDetailPrev.getState());
                    // budgetDetail = setBudgetDetailStatus(budgetDetail);
                    applyAuditing(budgetDetail);
                    persist(budgetDetail);
                    budgetUpload.setFinalStatus("Success");
                    tempList.add(budgetUpload);
                }
            }
        } catch (final ValidationException e) {
        	System.out.println("issue V8 :"+e.getMessage());
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
        	System.out.println("issue E8 :"+e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        return tempList;
    }

    @Transactional
    public BudgetDetail setBudgetDetailStatus(final BudgetDetail budgetDetail) {
        Long stateId;
        Serializable sequenceNumber = null;
        try {
            sequenceNumber = databaseSequenceProvider.getNextSequence("seq_eg_wf_states");
        } catch (final SQLGrammarException e) {
        }
        stateId = Long.valueOf(sequenceNumber.toString());

        persistenceService.getSession().createSQLQuery(BUDGETDETAIL_STATES_INSERT).setLong("stateId", stateId)
                .executeUpdate();

        budgetDetail.setWfState((State) persistenceService.find("from State where id = ?", stateId));
        return budgetDetail;
    }

    private String getmaterializedpathforbudget(final Budget budget) {

        return budget.getMaterializedPath() + "." + (getCountByBudget(budget.getId()) + 1);
    }

    @Transactional
    public BudgetGroup createBudgetGroup(CChartOfAccounts coa) {
    	System.out.println("coa.getId() ::"+coa.getId());
        BudgetGroup budgetGroup = budgetGroupService.getBudgetGroup(coa.getId());
        System.out.println("gr1");
        try {
            Serializable sequenceNumber = null;
            try {
                sequenceNumber = databaseSequenceProvider.getNextSequence("seq_egf_budgetgroup");
            } catch (final SQLGrammarException e) {
            }
            System.out.println("gr2");
            Long.valueOf(sequenceNumber.toString());
            System.out.println("gr3");
            if (budgetGroup == null) {
            	System.out.println("gr4");
                budgetGroup = new BudgetGroup();
                budgetGroup.setName(coa.getGlcode() + "-" + coa.getName());
                budgetGroup.setDescription(coa.getName());
                budgetGroup.setIsActive(true);
                if (coa.getType().compareTo('E') == 0) {
                	System.out.println("gr5");
                    budgetGroup.setAccountType(BudgetAccountType.REVENUE_EXPENDITURE);
                    budgetGroup.setBudgetingType(BudgetingType.DEBIT);
                } else if (coa.getType().compareTo('A') == 0) {
                	System.out.println("gr6");
                    budgetGroup.setAccountType(BudgetAccountType.CAPITAL_EXPENDITURE);
                    budgetGroup.setBudgetingType(BudgetingType.DEBIT);
                } else if (coa.getType().compareTo('L') == 0) {
                	System.out.println("gr7");
                    budgetGroup.setAccountType(BudgetAccountType.CAPITAL_RECEIPTS);
                    budgetGroup.setBudgetingType(BudgetingType.CREDIT);
                } else if (coa.getType().compareTo('I') == 0) {
                	System.out.println("gr8");
                    budgetGroup.setAccountType(BudgetAccountType.REVENUE_RECEIPTS);
                    budgetGroup.setBudgetingType(BudgetingType.CREDIT);
                }
                if (coa.getClassification().compareTo(1l) == 0 || coa.getClassification().compareTo(2l) == 0
                        || coa.getClassification().compareTo(4l) == 0) {
                	System.out.println("gr9");
                    budgetGroup.setMinCode(coa);
                    budgetGroup.setMaxCode(coa);
                }
                System.out.println("gr10");
                budgetGroup.setMajorCode(null);
                budgetGroupService.applyAuditing(budgetGroup);
                budgetGroup = budgetGroupService.persist(budgetGroup);
                if (coa.getType().compareTo('E') == 0 || coa.getType().compareTo('A') == 0) {
                    coa.setBudgetCheckReq(true);
                    coa = chartOfAccountsService.update(coa);
                }
            }

        } catch (final ValidationException e) {
        System.out.println("xxcdbc :"+e.getMessage());
        e.printStackTrace();
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
        	System.out.println("xxcdbd :"+e.getMessage());
            e.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        return budgetGroup;
    }

    @Transactional
    public void createRootBudget(final String budgetType, final CFinancialYear beFYear, final CFinancialYear reFYear,
            final List<String> deptList, final EgwStatus status) {
        String budgetName, budgetDes;
        CFinancialYear budgetFinancialYear;
        String rootmaterial;
        Budget budget = new Budget();

        try {
            if (budgetType.equalsIgnoreCase(BE)) {
                budgetName = budgetType + "-" + beFYear.getFinYearRange();
                budgetDes = "Budget - " + budgetType + " for the year " + beFYear.getFinYearRange();
                budgetFinancialYear = beFYear;
            } else {
                budgetName = budgetType + "-" + reFYear.getFinYearRange();
                budgetDes = "Budget - " + budgetType + " for the year " + reFYear.getFinYearRange();
                budgetFinancialYear = reFYear;
            }
            rootmaterial = getNewRootMaterializedPath();

            if (budgetType.equalsIgnoreCase(BE)) {
                final Budget refBudget = budgetService.getByName("RE-" + reFYear.getFinYearRange());
                budget.setName(budgetName);
                budget.setIsActiveBudget(true);
                budget.setIsPrimaryBudget(true);
                budget.setDescription(budgetDes);
                budget.setFinancialYear(budgetFinancialYear);
                budget.setIsbere(budgetType);
                budget.setMaterializedPath(rootmaterial);
                budget.setReferenceBudget(refBudget);
                budgetService.applyAuditing(budget);
                // budget = setBudgetState(budget);
                budget.setStatus(status);
                budget = budgetService.persist(budget);
            } else {
                budget.setName(budgetName);
                budget.setDescription(budgetDes);
                budget.setIsActiveBudget(true);
                budget.setIsPrimaryBudget(true);
                budget.setFinancialYear(budgetFinancialYear);
                budget.setIsbere(budgetType);
                budget.setMaterializedPath(rootmaterial);
                budgetService.applyAuditing(budget);
                // budget = setBudgetState(budget);
                budget.setStatus(status);
                budget = budgetService.persist(budget);
            }

            createCapitalOrRevenueBudget(budget, "Capital", rootmaterial + ".1", budgetType, beFYear, reFYear, deptList,
                    status);

            createCapitalOrRevenueBudget(budget, "Revenue", rootmaterial + ".2", budgetType, beFYear, reFYear, deptList,
                    status);

        } catch (final ValidationException e) {
        	System.out.println("issue V6 :"+e.getMessage());
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
        	System.out.println("issue E6 :"+e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
    }

    private String getNewRootMaterializedPath() {
        String rootmaterial;
        final Query query = persistenceService.getSession()
                .createSQLQuery("select count(*)+1 from egf_budget where parent is null");

        rootmaterial = query.uniqueResult().toString();
        return rootmaterial;
    }

    @Transactional
    public Budget setBudgetState(final Budget budget) {
        State budgetState;
        Serializable sequenceNumber = null;
        Long stateId;
        try {
            sequenceNumber = databaseSequenceProvider.getNextSequence("seq_eg_wf_states");
            stateId = Long.valueOf(sequenceNumber.toString());
        } catch (final SQLGrammarException e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        persistenceService.getSession().createSQLQuery(BUDGET_STATES_INSERT).setLong("stateId", stateId)
                .executeUpdate();
        budgetState = (State) persistenceService.find("from State where id = ?", stateId);
        budget.setWfState(budgetState);
        return budget;
    }

    @Transactional
    public void createCapitalOrRevenueBudget(final Budget parent, final String capitalOrRevenue,
            final String rootmaterial, final String budgetType, final CFinancialYear beFYear,
            final CFinancialYear reFYear, final List<String> deptList, final EgwStatus status) {
        String budgetName, budgetDes;
        CFinancialYear budgetFinancialYear;
        Budget budget = new Budget();
        try {
            if (budgetType.equalsIgnoreCase(BE)) {
                budgetName = capitalOrRevenue + "-" + budgetType + "-" + beFYear.getFinYearRange();
                budgetDes = capitalOrRevenue + " Budget - " + budgetType + " for the year " + beFYear.getFinYearRange();
                budgetFinancialYear = beFYear;
            } else {
                budgetName = capitalOrRevenue + "-" + budgetType + "-" + reFYear.getFinYearRange();
                budgetDes = capitalOrRevenue + " Budget - " + budgetType + " for the year " + reFYear.getFinYearRange();
                budgetFinancialYear = reFYear;
            }

            if (budgetType.equalsIgnoreCase(BE)) {
                final Budget refBudget = budgetService.getByName(capitalOrRevenue + "-RE-" + reFYear.getFinYearRange());
                budget.setName(budgetName);
                budget.setDescription(budgetDes);
                budget.setFinancialYear(budgetFinancialYear);
                budget.setIsActiveBudget(true);
                budget.setIsPrimaryBudget(true);
                // budget = setBudgetState(refBudget);
                budget.setStatus(status);
                budget.setIsbere(budgetType);
                budget.setMaterializedPath(rootmaterial);
                budget.setReferenceBudget(refBudget);
                budget.setParent(parent);
                budgetService.applyAuditing(budget);
                budget = budgetService.persist(budget);
            } else {
                budget.setName(budgetName);
                budget.setDescription(budgetDes);
                budget.setFinancialYear(budgetFinancialYear);
                budget.setIsActiveBudget(true);
                budget.setIsPrimaryBudget(true);
                // budget = setBudgetState(refBudget);
                budget.setStatus(status);
                budget.setIsbere(budgetType);
                budget.setMaterializedPath(rootmaterial);
                budget.setParent(parent);
                budgetService.applyAuditing(budget);
                budget = budgetService.persist(budget);
            }

            createDeptBudgetHeads(budget, capitalOrRevenue, budgetType, beFYear, reFYear,
                    capitalOrRevenue.substring(0, 3), deptList, status);
        } catch (final ValidationException e) {
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
    }

    @Transactional
    public void createDeptBudgetHeads(final Budget parent, final String capitalOrRevenue, final String budgetType,
            final CFinancialYear beFYear, final CFinancialYear reFYear, final String revOrCap,
            final List<String> deptList, final EgwStatus status) {
        String budgetName, budgetDes, rootmaterial;
        CFinancialYear budgetFinancialYear;
        rootmaterial = parent.getMaterializedPath() + ".";
        String materialPath = rootmaterial;
        try {
            final Query query = persistenceService.getSession()
                    .createSQLQuery(
                            "select count(*)+1 from egf_budget c,egf_budget p where c.parent = p.id and p.name = :parentName")
                    .setString("parentName", parent.getName());

            final String count = query.uniqueResult().toString();
            Integer capOrRevCount = Integer.valueOf(count);
            for (final String deptCode : deptList) {
                Budget budget = new Budget();

                if (budgetType.equalsIgnoreCase(BE)) {
                    budgetName = deptCode + "-" + budgetType + "-" + revOrCap + "-" + beFYear.getFinYearRange();
                    budgetDes = microserviceUtils.getDepartmentByCode(deptCode).getName() + " " + budgetType + " "
                            + capitalOrRevenue + "Budget for the year " + beFYear.getFinYearRange();
                    budgetFinancialYear = beFYear;
                } else {
                    budgetName = deptCode + "-" + budgetType + "-" + revOrCap + "-" + reFYear.getFinYearRange();
                    budgetDes = microserviceUtils.getDepartmentByCode(deptCode).getName() + " " + budgetType + " "
                            + capitalOrRevenue + "Budget for the year " + reFYear.getFinYearRange();
                    budgetFinancialYear = reFYear;
                }
                if (budgetService.getByName(budgetName) == null) {
                    materialPath = rootmaterial + capOrRevCount++;

                    if (budgetType.equalsIgnoreCase(BE)) {
                        final Budget refBudget = budgetService
                                .getByName(deptCode + "-RE-" + revOrCap + "-" + reFYear.getFinYearRange());
                        budget.setName(budgetName);
                        budget.setDescription(budgetDes);
                        budget.setFinancialYear(budgetFinancialYear);
                        budget.setIsActiveBudget(true);
                        budget.setIsPrimaryBudget(true);
                        // budget = setBudgetState(budget);
                        budget.setStatus(status);
                        budget.setIsbere(budgetType);
                        budget.setMaterializedPath(materialPath);
                        budget.setReferenceBudget(refBudget);
                        budget.setParent(parent);
                        budgetService.applyAuditing(budget);
                        budget = budgetService.persist(budget);
                    } else {
                        budget.setName(budgetName);
                        budget.setDescription(budgetDes);
                        budget.setFinancialYear(budgetFinancialYear);
                        budget.setIsActiveBudget(true);
                        budget.setIsPrimaryBudget(true);
                        // budget = setBudgetState(budget);
                        budget.setStatus(status);
                        budget.setIsbere(budgetType);
                        budget.setMaterializedPath(materialPath);
                        budget.setParent(parent);
                        budgetService.applyAuditing(budget);
                        budget = budgetService.persist(budget);
                    }
                }
            }
        } catch (final ValidationException e) {
            throw new ValidationException(Arrays
                    .asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
        } catch (final Exception e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
    }

    public BudgetDetail getBudgetDetail(final Integer fundId, final Long functionId, final String deptCode,
            final Long glCodeId, final CFinancialYear fYear, final String budgetType) {
        return find(
                "from BudgetDetail bd where bd.fund.id = ? and bd.function.id = ? and bd.executingDepartment = ? and bd.budgetGroup.maxCode.id = ? and bd.budget.financialYear.id = ? and bd.budget.isbere = ?",
                fundId, functionId, deptCode, glCodeId, fYear.getId(), budgetType);

    }

    public BudgetDetail getBudgetDetailNew(final Integer fundId, final Long functionId, final String deptCode, final Long budgetId) {
		System.out.println("getBudgetDetailNew");
		return find(
                "from BudgetDetail bd where bd.fund.id = ? and bd.function.id = ? and bd.executingDepartment = ? and bd.budget.id = ? ",
                fundId, functionId, deptCode, budgetId);
	}
    
    public BudgetDetail getBudgetDetail(final Integer fundId, final Long functionId, final String deptCode,
            final Long budgetGroupId) {
        return find(
                "from BudgetDetail bd where bd.fund.id = ? and bd.function.id = ? and bd.executingDepartment = ? and bd.budgetGroup.id= ?",
                fundId, functionId, deptCode, budgetGroupId);
    }

    public List<String> getDepartmentFromBudgetDetailByFundId(final Integer fundId) {

        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);

        return criteria.add(Restrictions.eq("fund.id", fundId))
                .setProjection(Projections.distinct(Projections.property("executingDepartment")))
                .addOrder(Order.asc("executingDepartment")).list();
    }

    public List<BudgetDetail> getFunctionFromBudgetDetailByDepartmentId(final String departmentId) {
        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
        return criteria.add(Restrictions.eq("executingDepartment", departmentId))
                .setProjection(Projections.distinct(Projections.property("function"))).addOrder(Order.asc("function"))
                .list();
    }

    public List<BudgetDetail> getBudgetDetailByFunctionId(final Long functionId) {
        final Criteria criteria = getSession().createCriteria(BudgetDetail.class);
        return criteria.add(Restrictions.eq("function.id", functionId))
                .setProjection(Projections.distinct(Projections.property("budgetGroup")))
                .addOrder(Order.asc("budgetGroup")).list();
    }

    @Transactional
    public void updateByMaterializedPath(final String materializedPath) {
        final EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Approved");
        final EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Created");
        persistenceService.getSession()
                .createSQLQuery(
                        "update egf_budgetdetail  set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'")
                .setLong("approvedStatus", approvedStatus.getId()).setLong("createdStatus", createdStatus.getId())
                .executeUpdate();
    }

    public List<BudgetDetail> sortByDepartmentName(final List<BudgetDetail> budgetDetails) {
        
        List<Department> departmentList = masterDataCache.get("egi-department");
        Map<String,String> deptMap = new HashMap<>();
        for(Department dep : departmentList){
            deptMap.put(dep.getCode(), dep.getName());
        }
        
        Collections.sort(budgetDetails, (o1, o2) -> deptMap.get(o1.getExecutingDepartment()).toUpperCase()
                .compareTo(deptMap.get(o2.getExecutingDepartment()).toUpperCase()));
        return budgetDetails;
    }

    public Assignment getWorkflowInitiator(final BudgetDetail budgetDetail) {
        return assignmentService
                .findByEmployeeAndGivenDate(budgetDetail.getCreatedBy(), new Date()).get(0);
    }
    
    public BudgetDetail transitionWorkFlow(BudgetDetail budgetDetail, Long approvalPosition, String approvalComment,
			String workFlowAction, String approvalDesignation) {
    	final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        Assignment wfInitiator = null;
        Map<String, String> finalDesignationNames = new HashMap<>();
        final String currState = "";
        String stateValue = "";
        System.out.println("desig********:"+approvalDesignation);
        if (null != budgetDetail.getId())
     
        	wfInitiator = this.getCurrentUserAssignmet(budgetDetail.getCreatedBy());
            WorkFlowMatrix wfmatrix;
           Designation designation = this.getDesignationDetails(approvalDesignation);
           if(designation != null)
           {
        	   System.out.println("Designation:::::::::::"+designation.getName().toUpperCase());
           }
           Position owenrPos = new Position();
           owenrPos.setId(approvalPosition);
            wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
                    null,null, FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING, null);
            if (wfmatrix != null && wfmatrix.getCurrentDesignation() != null) {
                final List<String> finalDesignationName = Arrays.asList(wfmatrix.getCurrentDesignation().split(","));
                for (final String desgName : finalDesignationName)
                    if (desgName != null && !"".equals(desgName.trim()))
                        finalDesignationNames.put(desgName.toUpperCase(), desgName.toUpperCase());
            }
            if (null == budgetDetail.getState()) {
            	System.out.println("state value "+budgetDetail.getState().getValue());
                wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
                        null, null, budgetDetail.getState().getValue(), null);
                if (stateValue.isEmpty())
                {
                	if(!wfmatrix.getNextState().equalsIgnoreCase(FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING))
                	{
                		stateValue = wfmatrix.getNextState()+ " "+designation.getName().toUpperCase();
                	}
                	else
                	{
                		stateValue = wfmatrix.getNextState();
                	}
                }
                if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)){
                	stateValue = FinancialConstants.BUTTONSAVEASDRAFT;
            	}
                budgetDetail.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        //.withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                        .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"") //added abhishek on 05042021
                        .withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.BUDGETDETAIL)
                        .withCreatedBy(user.getId())
                        .withtLastModifiedBy(user.getId());
            } else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workFlowAction)) {
                stateValue = FinancialConstants.WORKFLOW_STATE_CANCELLED;
                budgetDetail.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                        .withNextAction("")
                        .withNatureOfTask(FinancialConstants.BUDGETDETAIL);
            } else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workFlowAction)) {
                wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
                        null, null, budgetDetail.getState().getValue(), null);

                if (stateValue.isEmpty())
                    stateValue = wfmatrix.getNextState();

                budgetDetail.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        .withStateValue(stateValue).withDateInfo(new Date())
                        .withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.BUDGETDETAIL);
            } else {

                wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
                        null, null, budgetDetail.getState().getValue(), null);
                
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
				if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
            	{
                	stateValue = FinancialConstants.BUTTONSAVEASDRAFT;
            	}
				if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workFlowAction)) {
					int size=budgetDetail.getStateHistory().size();//added abhishek on 12042021
					Position owenrPosName = new Position();
					owenrPosName.setId(approvalPosition);
					HashMap<Long, Long> positionmap = new HashMap<>();
					for(int i=0;i<size;i++)
					{
						positionmap.put(budgetDetail.getStateHistory().get(i).getOwnerPosition(),
								budgetDetail.getStateHistory().get(i).getPreviousownerposition());
					}
					if(size>0)
					{
						Long owenrPos1=0l;
						if(positionmap.containsKey(user.getId()))
							owenrPos1=positionmap.get(user.getId());
						else
							owenrPos1=(long) budgetDetail.getStateHistory().get(size-1).getOwnerPosition();
						
						if(owenrPos1==null|| owenrPos1.equals(""))
							owenrPos1=(long) budgetDetail.getCreatedBy();
						owenrPosName.setId(owenrPos1);
						
						System.out.println("E owner position "+owenrPos1);
						owenrPos.setId(owenrPos1);
					}
					else
					{
						owenrPos.setId(budgetDetail.getState().getCreatedBy());
						owenrPosName.setId(budgetDetail.getState().getCreatedBy());
					}
		            System.out.println("ownerPostion id- "+owenrPos);
		            System.out.println("ownerPostion Nameid- "+owenrPosName);
					stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
					budgetDetail.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPosName).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                    .withNextAction("")
                    .withNatureOfTask(FinancialConstants.BUDGETDETAIL);
		        
		        }
				else
				{
					//////added abhishek for forward when current state is rejected
					if(budgetDetail.getState().getValue().equalsIgnoreCase("Rejected")) 
	                {
		                HashMap<Long, String> positionmap = new HashMap<>();
		                HashMap<Long, String> positionmap1 = new HashMap<>();
		                int size=budgetDetail.getStateHistory().size();
		    			for(int i=0;i<size;i++)
		    			{
		    				positionmap.put(budgetDetail.getStateHistory().get(i).getLastModifiedBy(),
		    						budgetDetail.getStateHistory().get(i).getValue());
		    				positionmap1.put(budgetDetail.getStateHistory().get(i).getLastModifiedBy(),
		    						budgetDetail.getStateHistory().get(i).getNextAction());
		    			}
		    			if(positionmap.containsKey(user.getId()))
		    			{
		    				stateValue=positionmap.get(user.getId());
		    				wfmatrix.setNextAction(positionmap1.get(user.getId()));
		    			}
	                }
					////////end
	                
					budgetDetail.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                        .withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.BUDGETDETAIL);
            }
        }
        return budgetDetail;
	}

    @Transactional
    public BudgetDetail transitionWorkFlow(final BudgetDetail budgetDetail, WorkflowBean workflowBean) {
        final DateTime currentDate = new DateTime();
        final User user = securityUtils.getCurrentUser();
        String stateValue = "";
        Designation designation = this.getDesignationDetails(this.getEmployeeDesignation(workflowBean.getApproverPositionId()));
        EmployeeInfo info = null;
        
        if (user != null && user.getId() != null)
            info = microserviceUtils.getEmployeeById(user.getId());

        if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
            int size=budgetDetail.getStateHistory().size();//added abhishek on 12042021
			Position owenrPosName = new Position();
			Position owenrPos = new Position();
	        owenrPos.setId(workflowBean.getApproverPositionId());
			owenrPosName.setId(workflowBean.getApproverPositionId());
			HashMap<Long, Long> positionmap = new HashMap<>();
			for(int i=0;i<size;i++)
			{
				positionmap.put(budgetDetail.getStateHistory().get(i).getOwnerPosition(),
						budgetDetail.getStateHistory().get(i).getPreviousownerposition());
			}
			if(size>0)
			{
				Long owenrPos1=0l;
				if(positionmap.containsKey(user.getId()))
					owenrPos1=positionmap.get(user.getId());
				else
					owenrPos1=(long) budgetDetail.getStateHistory().get(size-1).getOwnerPosition();
				
				if(owenrPos1==null|| owenrPos1.equals(""))
					owenrPos1=(long) budgetDetail.getCreatedBy();
				owenrPosName.setId(owenrPos1);
				System.out.println("Budget owner position "+owenrPos1);
				owenrPos.setId(owenrPos1);
			}
			else
			{
				owenrPos.setId(budgetDetail.getState().getCreatedBy());
				owenrPosName.setId(budgetDetail.getState().getCreatedBy());
			}
            System.out.println("ownerPostion id- "+owenrPos);
            System.out.println("ownerPostion Nameid- "+owenrPosName);
			
            budgetDetail.transition().progressWithStateCopy().withSenderName(user.getName())
                    .withComments(workflowBean.getApproverComments())
                    //.withStateValue(stateValue).withDateInfo(currentDate.toDate())
                    //.withOwner(budgetDetail.getState().getInitiatorPosition()).withOwnerName((budgetDetail.getState().getInitiatorPosition() != null && budgetDetail.getState().getInitiatorPosition() > 0L) ? getEmployeeName(budgetDetail.getState().getInitiatorPosition()):"")
                    .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPosName).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                    //.withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
                    .withNextAction("");

        } else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            final WorkFlowMatrix wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
                    null, null, budgetDetail.getCurrentState().getValue(), null);
            Position pos=new Position();
            pos.setId(user.getId());
            budgetDetail.transition().end().withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withStateValue(wfmatrix.getCurrentDesignation() + " Approved").withDateInfo(currentDate.toDate())
                    .withOwner(pos)/*withOwner((info != null && info.getAssignments() != null && !info.getAssignments().isEmpty())
                            ? info.getAssignments().get(0).getPosition() : null)*/
                    .withNextAction(wfmatrix.getNextAction());

            budgetDetail.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                    FinancialConstants.BUDGETDETAIL_CREATED_STATUS));
        } else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
        	budgetDetail.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                    FinancialConstants.BUDGETDETAIL_CANCELLED_STATUS));
            budgetDetail.transition().end().withStateValue(FinancialConstants.WORKFLOW_STATE_CANCELLED)
                    .withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withDateInfo(currentDate.toDate());
        } else {
        	
            if (null == budgetDetail.getState()) {
                final WorkFlowMatrix wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
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
                
                budgetDetail.transition().start().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                       // .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate())
                        .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                        .withNatureOfTask(FinancialConstants.BUDGETDETAIL)
                        .withOwner(workflowBean.getApproverPositionId()).withOwnerName((workflowBean.getApproverPositionId() != null && workflowBean.getApproverPositionId() > 0L) ? getEmployeeName(workflowBean.getApproverPositionId()):"")
                        .withNextAction(wfmatrix.getNextAction())
                        .withCreatedBy(user.getId())
                        .withtLastModifiedBy(user.getId());
            } else if (budgetDetail.getCurrentState().getNextAction().equalsIgnoreCase("END"))
                budgetDetail.transition().end().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withDateInfo(currentDate.toDate());
            else {
				final WorkFlowMatrix wfmatrix = budgetDetailWorkflowService.getWfMatrix(budgetDetail.getStateType(), null,
                        null, null,budgetDetail.getCurrentState().getValue(), null);
                String ststeValue=wfmatrix.getNextState();
                Long owner = workflowBean.getApproverPositionId();
                if ("Save As Draft".equalsIgnoreCase(workflowBean.getWorkFlowAction()))
                {
                		ststeValue =FinancialConstants.WORKFLOW_STATE_SAVEASDRAFT;
                		owner = populatePosition();
                }
                if(budgetDetail.getState().getValue().equalsIgnoreCase("Rejected")) 
                {
	                HashMap<Long, String> positionmap = new HashMap<>();
	                HashMap<Long, String> positionmap1 = new HashMap<>();
	                int size=budgetDetail.getStateHistory().size();
	    			for(int i=0;i<size;i++)
	    			{
	    				positionmap.put(budgetDetail.getStateHistory().get(i).getLastModifiedBy(),
	    						budgetDetail.getStateHistory().get(i).getValue());
	    				positionmap1.put(budgetDetail.getStateHistory().get(i).getLastModifiedBy(),
	    						budgetDetail.getStateHistory().get(i).getNextAction());
	    			}
	    			if(positionmap.containsKey(user.getId()))
	    			{
	    				ststeValue=positionmap.get(user.getId());
	    				wfmatrix.setNextAction(positionmap1.get(user.getId()));
	    			}
                }
                budgetDetail.transition().progressWithStateCopy().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withStateValue(ststeValue).withDateInfo(currentDate.toDate())
                        .withOwner(owner).withOwnerName((owner != null && owner > 0L) ? getEmployeeName(owner):"")
                        .withNextAction(wfmatrix.getNextAction());
            }
        }
        return budgetDetail;
    }
    
    @Transactional
    public BudgetDetail transitionWorkFlow_old(final BudgetDetail budgetDetail, final WorkflowBean workflowBean) {
        final User user = securityUtils.getCurrentUser();
        final Assignment userAssignment = assignmentService.findByEmployeeAndGivenDate(user.getId(), new Date()).get(0);
        Position pos = null;
        Assignment wfInitiator = null;
        if (budgetDetail.getId() != null && budgetDetail.getId() != 0)
            wfInitiator = getWorkflowInitiator(budgetDetail);

        if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            if (wfInitiator.equals(userAssignment))
                budgetDetail.transition().end().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments()).withDateInfo(new Date());
            else {
                final String stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
                budgetDetail.transition().progressWithStateCopy().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments()).withStateValue(stateValue)
                        .withDateInfo(new Date()).withOwner(wfInitiator.getPosition()).withOwnerName((wfInitiator.getPosition().getId() != null && wfInitiator.getPosition().getId() > 0L) ? getEmployeeName(wfInitiator.getPosition().getId()):"")
                        .withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
            }

        } else if (FinancialConstants.BUTTONVERIFY.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            budgetDetail.transition().progressWithStateCopy().withSenderName(user.getName())
                    .withComments(workflowBean.getApproverComments())
                    .withStateValue(" Approved").withDateInfo(new Date())
                    .withOwner(pos).withOwnerName((pos.getId() != null && pos.getId() > 0L) ? getEmployeeName(pos.getId()):"");
            budgetDetail.transition().end().withSenderName(user.getName())
                    .withComments(workflowBean.getApproverComments()).withDateInfo(new Date());
            budgetDetail.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                    FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS));
        } else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            budgetDetail.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                    FinancialConstants.WORKFLOW_STATE_CANCELLED));
            budgetDetail.transition().end().withStateValue(FinancialConstants.WORKFLOW_STATE_CANCELLED)
                    .withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withDateInfo(new Date());
        } else if (FinancialConstants.BUTTONSAVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            if (budgetDetail.getState() == null) {
                budgetDetail.transition().start().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments()).withStateValue(FinancialConstants.WORKFLOW_STATE_NEW)
                        .withDateInfo(new Date()).withOwner(userAssignment.getPosition()).withOwnerName((userAssignment.getPosition().getId() != null && userAssignment.getPosition().getId() > 0L) ? getEmployeeName(userAssignment.getPosition().getId()):"");
                budgetDetail.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                        FinancialConstants.WORKFLOW_STATE_NEW));
            }
        } else {
            if (null != workflowBean.getApproverPositionId() && workflowBean.getApproverPositionId() != -1)
                pos = (Position) persistenceService.find("from Position where id=?",
                        workflowBean.getApproverPositionId());
            if (null == budgetDetail.getState()) {
                budgetDetail.transition().start().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withStateValue(FinancialConstants.BUDGETDETAIL_CREATED_STATUS)
                        .withDateInfo(new Date()).withOwner(pos).withOwnerName((pos.getId() != null && pos.getId() > 0L) ? getEmployeeName(pos.getId()):"");
                budgetDetail.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL,
                        FinancialConstants.BUDGETDETAIL_CREATED_STATUS));
            } else if (budgetDetail.getCurrentState().getNextAction() != null
                    && budgetDetail.getCurrentState().getNextAction().equalsIgnoreCase(FinancialConstants.WORKFLOWENDSTATE))
                budgetDetail.transition().end().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments()).withDateInfo(new Date());
            else
                budgetDetail.transition().progressWithStateCopy().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withStateValue(FinancialConstants.BUDGETDETAIL_CREATED_STATUS)
                        .withDateInfo(new Date()).withOwner(pos).withOwnerName((pos.getId() != null && pos.getId() > 0L) ? getEmployeeName(pos.getId()):"");
        }
        return budgetDetail;
    }

    @Transactional
    public BudgetDetail rejectWorkFlow(final BudgetDetail budgetDetail, final String comment) {
        final DateTime currentDate = new DateTime();
        final User user = securityUtils.getCurrentUser();
        Assignment wfInitiator = new Assignment();
        if (budgetDetail.getId() != null && budgetDetail.getId() != 0)
            wfInitiator = getWorkflowInitiator(budgetDetail);
        final String stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
        budgetDetail.transition().progressWithStateCopy().withSenderName(user.getName())
                .withStateValue(stateValue).withComments(comment)
                .withDateInfo(currentDate.toDate()).withOwner(wfInitiator.getPosition()).withOwnerName((wfInitiator.getPosition().getId() != null && wfInitiator.getPosition().getId() > 0L) ? getEmployeeName(wfInitiator.getPosition().getId()):"")
                .withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
        applyAuditing(budgetDetail.getState());
        return budgetDetail;
    }

    public List<Long> getBudgetIdList() {
        final String query = "select distinct bd.budget.id from BudgetDetail bd ";
        final List<Long> budgetDetailsList = persistenceService.getSession().createQuery(query).list();
        return budgetDetailsList;
    }

    public List<BudgetDetail> getBudgetDetailsByBudgetGroupId(final Long budgetGroupId) {
        final Query qry = getCurrentSession().createQuery("from BudgetDetail where budgetGroup.id=:budgetGroupId");
        qry.setLong("budgetGroupId", budgetGroupId);
        List<BudgetDetail> budgetDetails = null;
        if (!qry.list().isEmpty())
            budgetDetails = qry.list();
        else
            budgetDetails = Collections.emptyList();

        return budgetDetails;
    }

    public List<BudgetDetail> getBudgetDetailsByBudgetId(final Long budgetId) {
        final Query qry = getCurrentSession().createQuery("from BudgetDetail where budget.id=:budgetId");
        qry.setLong("budgetId", budgetId);
        List<BudgetDetail> budgetDetails = null;
        if (!qry.list().isEmpty())
            budgetDetails = qry.list();
        else
            budgetDetails = Collections.emptyList();

        return budgetDetails;
    }

    public List<Budget> getBudgetByStatusAndFinancialYearId(final Integer statusId,
            final Long financialYearId) {
        final Query qry = getCurrentSession()
                .createQuery("select distinct budgetDetail.budget from BudgetDetail budgetDetail"
                        + " where budgetDetail.status.id=:statusId and "
                        + "budgetDetail.budget.id in(select id from Budget where financialYear.id=:financialYearId)");

        qry.setInteger("statusId", statusId);
        qry.setLong("financialYearId", financialYearId);
        List<Budget> budget;
        if (!qry.list().isEmpty())
            budget = qry.list();
        else
            budget = Collections.emptyList();

        return budget;
    }

    public List<BudgetDetail> getBudgetDetails(final List<Long> budgetId) {
        return budgetDetailRepository.findByBudgetIdInAndStatusId(budgetId,
                getBudgetDetailStatus(FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS).getId());
    }

    public EgwStatus getBudgetDetailStatus(final String code) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.BUDGETDETAIL, code);
    }

    public String getDeptNameForBudgetId(final Long budgetId) {
       final BudgetDetail bg = budgetDetailRepository.findByBudgetIdAndStatusId(budgetId,
                getBudgetDetailStatus(FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS).getId()).get(0);
       String deptName = microserviceUtils.getDepartmentByCode(bg.getExecutingDepartment()).getName();
        return bg == null ? StringUtils.EMPTY : deptName;
    }

    public String getNextYrBEName(final Budget budget) {
        final BudgetDetail bg = budgetDetailRepository.findByBudgetReferenceBudgetId(budget.getId()).get(0);
        return bg == null ? StringUtils.EMPTY : bg.getBudget().getName();
    }

    public BigDecimal getREAmount(final Budget budget) {
        return budgetDetailRepository.findBudgetAmount(budget.getId(),
                getBudgetDetailStatus(FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS).getId());
    }

    public BigDecimal getBEAmount(final Budget budget) {
        final BudgetDetail bg = budgetDetailRepository.findByBudgetReferenceBudgetId(budget.getId()).get(0);
        return budgetDetailRepository.findBudgetAmount(bg.getBudget().getId(),
                getBudgetDetailStatus(FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS).getId());
    }

    public List<BudgetDetail> getNotApprovedBudgetDetails(final Long budgetId) {
        return budgetDetailRepository.findByBudgetIdInAndStatusIdNotIn(budgetId,
                getBudgetDetailStatus(FinancialConstants.WORKFLOW_STATE_APPROVED).getId());

    }

    public Long getBudgetDetailCount(final Budget budget) {
        return budgetDetailRepository.countByBudgetIdAndStatusId(budget.getId(),
                getBudgetDetailStatus(FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS).getId());
    }

    public List<BudgetDetail> getNotApprovedBudgetDetailsForBudget(final List<Long> budgetId) {
        return budgetDetailRepository.findByBudgetIdInAndStatusId(budgetId,
                getBudgetDetailStatus(FinancialConstants.BUDGETDETAIL_VERIFIED_STATUS).getId());
    }

    public BudgetDetail getBudgetDetailByReferencceBudget(final String uniqueNo, final Long budgetId) {
        return budgetDetailRepository.findByReferenceBudget(uniqueNo, budgetId);
    }
    
    public List<BudgetDetail> getBudgetDetailsForUploadReportCAO() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'CAO Verify'");
    }
    
    public List<BudgetDetail> getBudgetDetailsForUploadReportSO() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'CAO REJECTED'");
    }
    
    public List<BudgetDetail> getBudgetDetailsForUploadReportACMC() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'ACMC Verify'");
    }
    
    public List<BudgetDetail> getBudgetDetailsForUploadReport() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'Created'");
    }
    
    @Transactional
    public void updateByMaterializedPathCAO(final String materializedPath) {
        final EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "ACMC Verify");
        final EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO Verify");
        persistenceService.getSession()
                .createSQLQuery(
                        "update egf_budgetdetail  set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'")
                .setLong("approvedStatus", approvedStatus.getId()).setLong("createdStatus", createdStatus.getId())
                .executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathSO(final String materializedPath) {
        final EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO REJECTED");
        final EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO Verify");
        persistenceService.getSession()
                .createSQLQuery(
                        "update egf_budgetdetail  set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'")
                .setLong("approvedStatus", approvedStatus.getId()).setLong("createdStatus", createdStatus.getId())
                .executeUpdate();
               
    }
    
    @Transactional
    public void updateByMaterializedPathReturnByACMC(final String materializedPath) {
    	 EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO REJECTED");
          EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "ACMC Verify");
        persistenceService
                .getSession()
                .createSQLQuery(
                        "update egf_budgetdetail set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'").setLong("approvedStatus", approvedStatus.getId())
                .setLong("createdStatus", createdStatus.getId()).executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathReturnByMC(final String materializedPath) {
    	 EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO REJECTED");
          EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Created");
        persistenceService
                .getSession()
                .createSQLQuery(
                        "update egf_budgetdetail set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'").setLong("approvedStatus", approvedStatus.getId())
                .setLong("createdStatus", createdStatus.getId()).executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathSTATUSCancelBySO(final String materializedPath) {
        EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CANCEL");
        EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO REJECTED");
        persistenceService
                .getSession()
                .createSQLQuery(
                		"update egf_budgetdetail set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'").setLong("approvedStatus", approvedStatus.getId())
                .setLong("createdStatus", createdStatus.getId()).executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathSTATUSCancelByCAO(final String materializedPath) {
        EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CANCEL");
        EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO Verify");
        persistenceService
                .getSession()
                .createSQLQuery(
                		"update egf_budgetdetail set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'").setLong("approvedStatus", approvedStatus.getId())
                .setLong("createdStatus", createdStatus.getId()).executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathSTATUSCancelByACMC(final String materializedPath) {
        EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CANCEL");
        EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "ACMC Verify");
        persistenceService
                .getSession()
                .createSQLQuery(
                		"update egf_budgetdetail set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'").setLong("approvedStatus", approvedStatus.getId())
                .setLong("createdStatus", createdStatus.getId()).executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathSTATUSCancelByMC(final String materializedPath) {
        EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CANCEL");
        EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Created");
        persistenceService
                .getSession()
                .createSQLQuery(
                		"update egf_budgetdetail set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'").setLong("approvedStatus", approvedStatus.getId())
                .setLong("createdStatus", createdStatus.getId()).executeUpdate();
    }
    
    
    @Transactional
    public void updateByMaterializedPathForReVerify(final String materializedPath) {
    	 EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO Verify");
         EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "CAO REJECTED");
        persistenceService.getSession()
                .createSQLQuery(
                        "update egf_budgetdetail  set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'")
                .setLong("approvedStatus", approvedStatus.getId()).setLong("createdStatus", createdStatus.getId())
                .executeUpdate();
    }
    
    @Transactional
    public void updateByMaterializedPathACMC(final String materializedPath) {
        final EgwStatus approvedStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "Created");
        final EgwStatus createdStatus = egwStatusDAO.getStatusByModuleAndCode("BUDGETDETAIL", "ACMC Verify");
        persistenceService.getSession()
                .createSQLQuery(
                        "update egf_budgetdetail  set status = :approvedStatus where status =:createdStatus and  materializedPath like'"
                                + materializedPath + "%'")
                .setLong("approvedStatus", approvedStatus.getId()).setLong("createdStatus", createdStatus.getId())
                .executeUpdate();
    }
    
    public List<BudgetDetail> getBudgetDetailsForReAppCao() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'REAPP CAO'");
    }
    
    public List<BudgetDetail> getBudgetDetailsForReAppCaoEdit(Long id) {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'Approved' and bd.id="+id);
    }
    
    public List<BudgetDetail> getBudgetDetailsForReAppAcmc() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'REAPP ACMC'");
    }
    
    public List<BudgetDetail> getBudgetDetailsForReAppMc() {
        return findAllBy("select bd from BudgetDetail bd where bd.status.code = 'REAPP MC'");
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

    private Assignment getCurrentUserAssignmet(Long userId){
//    	Long userId = ApplicationThreadLocals.getUserId();
    	List<EmployeeInfo> emplist = microServiceUtil.getEmployee(userId, null, null, null);
    	Assignment assignment =new Assignment();
    	if(null!=emplist && emplist.size()>0 && emplist.get(0).getAssignments().size()>0){
    		Position position = new Position();
    		position.setId(emplist.get(0).getAssignments().get(0).getPosition());
    		assignment.setPosition(position);
            
    		org.egov.pims.commons.Designation designation = new org.egov.pims.commons.Designation();
            Designation _desg = this.getDesignationDetails(emplist.get(0).getAssignments().get(0).getDesignation());
            designation.setCode(_desg.getCode());
            designation.setName(_desg.getName());
            assignment.setDesignation(designation);
            
            org.egov.infra.admin.master.entity.Department department = new org.egov.infra.admin.master.entity.Department();
            Department _dept = this.getDepartmentDetails(emplist.get(0).getAssignments().get(0).getDepartment());
            department.setCode(_dept.getCode());
            department.setName(_dept.getName());
            
            return assignment;
    	}
    	return null;
    }
    private Department getDepartmentDetails(String deptCode){
    	
    	Department dept = microServiceUtil.getDepartmentByCode(deptCode);
    	return dept;
    	
    }
    
    private org.egov.infra.microservice.models.Designation getDesignationDetails(String desgnCode){
    	List<org.egov.infra.microservice.models.Designation> designation = microServiceUtil.getDesignation(desgnCode);
    	return designation.get(0);
    }   
	
}