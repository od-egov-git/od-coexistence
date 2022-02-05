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
package org.egov.asset.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.DepreciationInputs;
import org.egov.asset.model.DepreciationList;
import org.egov.asset.model.ReasonForFailure;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.DepreciationRepository;
import org.egov.commons.CFunction;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.service.CFinancialYearService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.FundService;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigService;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.voucher.VoucherDetails;
import org.egov.model.voucher.VoucherTypeBean;
import org.egov.model.voucher.WorkflowBean;
import org.egov.services.masters.SchemeService;
import org.egov.services.masters.SubSchemeService;
import org.egov.services.voucher.JournalVoucherActionHelper;
import org.egov.services.voucher.VoucherService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DepreciationService {
	private final DepreciationRepository depreciationRepository;
	public transient CVoucherHeader voucherHeader = new CVoucherHeader();
	@Autowired
    @Qualifier("journalVoucherActionHelper")
    private JournalVoucherActionHelper journalVoucherActionHelper;
	@Autowired
	private VoucherTypeBean voucherTypeBean;
	public transient WorkflowBean workflowBean = new WorkflowBean();
	
	private List<VoucherDetails> billDetailslist;
    private List<VoucherDetails> subLedgerlist;
    private static final Logger LOG = LoggerFactory.getLogger(DepreciationService.class);
    private final ScriptService scriptExecutionService;
    @Autowired
    protected AppConfigValueService appConfigValuesService;
    @Autowired
    private DocumentUploadRepository documentUploadRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private SubSchemeService subSchemeService;
    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
    private AutonumberServiceBeanResolver beanResolver;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    @Qualifier(value = "voucherService")
    private VoucherService voucherService;
    @Autowired
    private FundService fundService;
    @Autowired
    private ChartOfAccountDetailService chartOfAccountDetailService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private CFinancialYearService cFinancialYearService;
    
    @Autowired
    private MicroserviceUtils microServiceUtil;
    
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @Autowired
	protected MicroserviceUtils microserviceUtils;
    @Autowired
	private DepartmentService departmentService;
    @Autowired
	private FunctionDAO functionDAO;
    
    @Autowired
	private FundHibernateDAO fundHibernateDAO;
    
    @Autowired
	private AssetMasterRepository masterRepo;
    
    @Autowired
    public DepreciationService(final DepreciationRepository depreciationRepository, final ScriptService scriptExecutionService) {
        this.depreciationRepository = depreciationRepository;
        this.scriptExecutionService = scriptExecutionService;
    }

    public List<VoucherDetails> getBillDetailslist() {
		return billDetailslist;
	}

	public void setBillDetailslist(List<VoucherDetails> billDetailslist) {
		this.billDetailslist = billDetailslist;
	}

	public List<VoucherDetails> getSubLedgerlist() {
		return subLedgerlist;
	}

	public void setSubLedgerlist(List<VoucherDetails> subLedgerlist) {
		this.subLedgerlist = subLedgerlist;
	}

	public CVoucherHeader getVoucherHeader() {
		return voucherHeader;
	}

	public void setVoucherHeader(CVoucherHeader voucherHeader) {
		this.voucherHeader = voucherHeader;
	}

	public VoucherTypeBean getVoucherTypeBean() {
		return voucherTypeBean;
	}

	public void setVoucherTypeBean(VoucherTypeBean voucherTypeBean) {
		this.voucherTypeBean = voucherTypeBean;
	}

	public WorkflowBean getWorkflowBean() {
		return workflowBean;
	}

	public void setWorkflowBean(WorkflowBean workflowBean) {
		this.workflowBean = workflowBean;
	}

	public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public DepreciationInputs getById(final Long id) {
        return depreciationRepository.findById(id);
    }

	public DepreciationInputs saveDepreciationAsset(DepreciationList dl,String depreciationDate,Integer cnt,List<DepreciationList> depreciationList) {
		System.out.println("inside save Depreciation Asset");
		AssetMaster assetBean=null;
		final BigDecimal minValue = BigDecimal.ONE;
        ReasonForFailure reason = null;
        String reason1="N/A";
        String status = "SUCCESS";//"FAIL";
        BigDecimal amtToBeDepreciated = BigDecimal.ZERO;
        BigDecimal valueAfterDep = BigDecimal.ZERO;

        BigDecimal valueAfterDepRounded = BigDecimal.ZERO;
        BigDecimal amtToBeDepreciatedRounded = BigDecimal.ZERO;
        Double depreciationRate;
        depreciationRate = dl.getDepreciationRate();
		/*
		 * if (dl.getCurrentGrossValue() != null &&
		 * dl.getCurrentGrossValue().compareTo(BigDecimal.ZERO) != 0) if
		 * (depreciationRate == 0.0) { reason =
		 * ReasonForFailure.DEPRECIATION_RATE_NOT_FOUND;
		 * reason1=ReasonForFailure.DEPRECIATION_RATE_NOT_FOUND.toString(); } else if
		 * (dl.getCurrentGrossValue().compareTo(minValue) <= 0) { reason =
		 * ReasonForFailure.ASSET_IS_FULLY_DEPRECIATED_TO_MINIMUN_VALUE; reason1 =
		 * ReasonForFailure.ASSET_IS_FULLY_DEPRECIATED_TO_MINIMUN_VALUE.toString(); }
		 * else if (dl.getCurrentGrossValue().compareTo(new BigDecimal(5000)) < 0) {
		 * reason = ReasonForFailure.ASSET_IS_FULLY_DEPRECIATED_TO_MINIMUN_VALUE;
		 * reason1=
		 * ReasonForFailure.ASSET_IS_FULLY_DEPRECIATED_TO_MINIMUN_VALUE.toString(); }
		 * else { status = "SUCCESS"; }
		 */
                amtToBeDepreciated = getAmountToBeDepreciated(dl);
                System.out.println("amtToBeDepreciated "+amtToBeDepreciated);
                amtToBeDepreciatedRounded = new BigDecimal(amtToBeDepreciated.setScale(2, BigDecimal.ROUND_HALF_UP).toString());  
                String currentDepreciation=amtToBeDepreciated.toString();
                valueAfterDep = dl.getCurrentGrossValue().subtract(amtToBeDepreciated);
                valueAfterDepRounded = new BigDecimal(valueAfterDep.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                String afterDepreciation=valueAfterDep.toString();
                System.out.println("valueAfterDepRounded------------" + valueAfterDepRounded);
                final StringBuffer query1 = new StringBuffer(500);
        		List<Object[]> list= null;
        		SQLQuery queryMain =  null;
        		query1
        		.append("select ac.name,act.code,ah.department,ac.asset_code,ah.asset_name,am.gross_value,ac.depriciation_rate," + 
        				" (select c.glcode from chartofaccounts c,asset_category ac where c.id = ac.asset_account_code_id and ac.asset_code = '"+dl.getAssetCode()+"') as accumulated,am.accumulated_depreciation, " + 
        				" (select c.glcode from chartofaccounts c,asset_category ac where c.id = ac.depriciation_expense_account_id and ac.asset_code = '"+dl.getAssetCode()+"') as expense, " + 
        				" (select c.glcode from chartofaccounts c, asset_category ac where c.id = ac.revolution_reserve_account_code_id and ac.asset_code = '"+dl.getAssetCode()+"') as revolution_reverse, " + 
        				" ah.function,ah.fund,ah.description,al.location,am.id from asset_category ac, asset_catagory_type act, asset_header ah, asset_master am,asset_revaluation ar, asset_location al, " + 
        				" asset_location_locality all2 where am.id = ar.asset_master_id and am.asset_header =ah.id and ah.asset_category =ac.id and ac.asset_catagory_type_id =act.id and am.asset_location =al.id " + 
        				/*"--and al.location =all2.id" +*/ 
        				" and ac.asset_code = '"+dl.getAssetCode()+"'");
        		System.out.println("query1 "+query1.toString());
        		queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
        		list = queryMain.list();  
        	    DepreciationInputs dI =null;
        	       if (list.size() != 0) {
        	       	dI = new DepreciationInputs();
        	       	   for (final Object[] object : list) {
        	       		dI.setCategoryName(object[0]!=null?object[0].toString():"");
        	       		dI.setCategoryType(object[1]!=null?object[1].toString():"");
        	       		dI.setDepartment(object[2]!=null?object[2].toString():"");
        	       		dI.setAssetCode(object[3]!=null?object[3].toString():"");
        	       		dI.setAssetName(object[4]!=null?object[4].toString():"");
        	       		dI.setCurrentValue(object[5]!=null?new BigDecimal(object[5].toString()):new BigDecimal(0));
        	       		dI.setDepreciationRate(object[6]!=null?object[6].toString():"");
        	       		dI.setAccumulatedDepreciationAccount(object[7]!=null?Long.valueOf(object[7].toString()):0l);
        	       		dI.setAccumulatedDepreciation(object[8]!=null?new BigDecimal(object[8].toString()):new BigDecimal(0));
        	       		dI.setDepreciationExpenseAccount(object[9]!=null?Long.valueOf(object[9].toString()):0l);
        	       		dI.setRevaluationreserveaccount(object[10]!=null?object[10].toString():"");
        	       		dI.setFunction(object[11]!=null?object[11].toString():"");
        	       		dI.setFund(object[12]!=null?object[12].toString():"");
        	       		dI.setDescription(object[13]!=null?object[13].toString():"");
        	       		dI.setLocation(object[14]!=null?object[14].toString():"");
        	       		dI.setAssetId(object[15]!=null?Long.valueOf(object[15].toString()):0);
        	       		dI.setCurrentDepreciation(currentDepreciation);
        	       		dI.setBeforeDepreciation(dl.getCurrentGrossValue().toString());
        	       		dI.setAfterDepreciation(afterDepreciation);
        	       		dI.setCreated_date(new Date());
        	       		dI.setLastmodified_date(new Date());
        	       		dI.setCreatedby(ApplicationThreadLocals.getUserId());
        	       		dI.setSuccessFailure(status);
        	       		dI.setReasonForFailure(reason1);
        	       		dI.setDepreciationDate(depreciationDate);
        	       	   }
        	       }
        	    Department department = new Department(); 
        	    department=departmentService.getDepartmentById(Long.valueOf(dI.getDepartment()));
        	    dI.setDepartment(department.getCode());
        	    CFunction function = new CFunction();
       			Fund fund = new Fund();
       			fund = fundHibernateDAO.fundByCode(dI.getFund());
        	    CFunction fun=functionDAO.getFunctionById(Long.valueOf(dI.getFunction()));  
                voucherTypeBean.setVoucherName("JVGeneral");
                voucherTypeBean.setVoucherType("Journal Voucher");
                voucherTypeBean.setVoucherSubType("JVGeneral");
                voucherHeader=new CVoucherHeader();
                voucherHeader.setBackdateentry("N");
                voucherHeader.setFileno("");
                voucherHeader.setDescription(dI.getDescription());
                voucherHeader.setFundId(fund);
                voucherHeader.setVoucherDate(new Date());
                voucherHeader.setVouchermis(new Vouchermis());
                voucherHeader.getVouchermis().setFunction(fun);
                voucherHeader.getVouchermis().setDepartmentcode(dI.getDepartment());
                workflowBean.setWorkFlowAction("CreateAndApprove");
                billDetailslist = new ArrayList<VoucherDetails>();
                VoucherDetails vd1=new VoucherDetails();
                vd1.setCreditAmountDetail(new BigDecimal(afterDepreciation));
                vd1.setDebitAmountDetail(new BigDecimal(0));
                vd1.setGlcodeDetail(dI.getDepreciationExpenseAccount().toString());
                billDetailslist.add(vd1);
                VoucherDetails vd2=new VoucherDetails();
                vd2.setCreditAmountDetail(new BigDecimal(0));
                vd2.setDebitAmountDetail(new BigDecimal(afterDepreciation));
                vd2.setGlcodeDetail(dI.getAccumulatedDepreciationAccount().toString());
                billDetailslist.add(vd2);
                subLedgerlist = new ArrayList<VoucherDetails>();
                voucherHeader = journalVoucherActionHelper.createVcouher(billDetailslist, subLedgerlist, voucherHeader,
                        voucherTypeBean, workflowBean);
		
                dI.setVoucherNumber(voucherHeader.getVoucherNumber());
                try {    
                dI=depreciationRepository.save(dI);
                assetBean = new AssetMaster();
        		assetBean = masterRepo.findOne(dI.getAssetId());
        		assetBean.setCurrentValue(valueAfterDep.longValue());
        		masterRepo.save(assetBean);
                persistenceService.getSession().flush();
                }
                catch(Exception e) {
                	e.printStackTrace();
                }
                DepreciationList result = new DepreciationList();
	        	   result.setSlNo(cnt);
	        	   result.setAssetCategoryName(dI.getCategoryName());
	        	   result.setDepartment(dI.getDepartment());
	        	   result.setAssetCode(dI.getAssetCode());
	        	   result.setAssetName(dI.getAssetName());
	        	   result.setDepreciationRate(Double.valueOf(dI.getDepreciationRate()));
	        	   result.setCurrentDepreciation(currentDepreciation);
	        	   result.setAfterDepreciation(afterDepreciation);
	        	   result.setSuccessFailure(status);
   	       			result.setReasonForFailure(reason1);
   	       			result.setVoucherNumber(voucherHeader.getVoucherNumber());
	        	   depreciationList.add(result);
         return dI;//depreciation;
	}

        private BigDecimal getAmountToBeDepreciated(final DepreciationList dl) 
        {
        	BigDecimal Amount= new BigDecimal(0);
        	System.out.println("dl.getCurrentGrossValue().doubleValue() "+dl.getCurrentGrossValue().doubleValue());
        	System.out.println("Integer.parseInt(dl.getDepreciationRate()) "+dl.getDepreciationRate());
        	System.out.println("(Integer.parseInt(dl.getDepreciationRate()) / 100) "+(dl.getDepreciationRate() / 100));
        	System.out.println("final "+BigDecimal.valueOf(dl.getCurrentGrossValue().doubleValue() * (dl.getDepreciationRate() / 100)));
        	return BigDecimal.valueOf(dl.getCurrentGrossValue().doubleValue() * (dl.getDepreciationRate() / 100));
        	//System.out.println("Amount "+Amount);
        	//return Amount;
        }


}