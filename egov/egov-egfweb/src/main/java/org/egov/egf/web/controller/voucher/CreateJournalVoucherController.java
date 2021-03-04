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
package org.egov.egf.web.controller.voucher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.egov.commons.CVoucherHeader;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.egf.model.BillRegisterReportBean;
import org.egov.egf.model.VoucherDetailLedger;
import org.egov.egf.model.VoucherDetailMain;
import org.egov.egf.model.VoucherDetailMiscMapping;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.voucher.service.JournalVoucherService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.ChartOfAccounts;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.Miscbilldetail;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.utils.FinancialConstants;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author venki
 *
 */

@Controller
@RequestMapping(value = "/journalvoucher")
public class CreateJournalVoucherController extends BaseVoucherController {
	 private static final Logger LOGGER = Logger.getLogger(CreateJournalVoucherController.class);
    private static final String JOURNALVOUCHER_FORM = "journalvoucher-form";
    private static final String JOURNALVOUCHER_SEARCH = "journalvoucher-search";
    private static final String VOUCHER_NUMBER_GENERATION_AUTO = "voucherNumberGenerationAuto";
    private static  final String chqdelimitSP = "/";
    private static final String chqdelimitDP = "//";
    public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMATS = new SimpleDateFormat("dd/MM/yyyy", LOCALE);
    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
    private static final String STATE_TYPE = "stateType";
    private static Map<String, List<String>> netAccountCode = new HashMap<String, List<String>>(); // have list of all net payable
    @Autowired	
    private  AppConfigValueService appConfigValueService;
    private static final String APPROVAL_POSITION = "approvalPosition";

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;
    @Autowired
	private MicroserviceUtils microserviceUtils;

    @Autowired
    private JournalVoucherService journalVoucherService;

    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
    private VoucherSearchUtil voucherSearchUtil;
    @Autowired
    @Qualifier("persistenceService")
    protected transient PersistenceService persistenceService;

    
    @Autowired
    private EgovMasterDataCaching masterDataCache;

    public CreateJournalVoucherController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
        model.addAttribute("voucherSubTypes", FinancialUtils.VOUCHER_SUBTYPES);
    }

    @RequestMapping(value = "/newform", method = RequestMethod.GET)
    public String showNewForm(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        setDropDownValues(model);
        model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
        prepareWorkflow(model, voucherHeader, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        voucherHeader.setVoucherDate(new Date());
        model.addAttribute(VOUCHER_NUMBER_GENERATION_AUTO, isVoucherNumberGenerationAuto(voucherHeader, model));
        return JOURNALVOUCHER_FORM;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,
            final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction) {

        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        voucherHeader.setEffectiveDate(voucherHeader.getVoucherDate());

        populateVoucherName(voucherHeader);
        populateAccountDetails(voucherHeader);

        if (resultBinder.hasErrors()) {
            setDropDownValues(model);
            model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
            prepareWorkflow(model, voucherHeader, new WorkflowContainer());
            prepareValidActionListByCutOffDate(model);
            voucherHeader.setVoucherDate(new Date());
            model.addAttribute(VOUCHER_NUMBER_GENERATION_AUTO, isVoucherNumberGenerationAuto(voucherHeader, model));

            return JOURNALVOUCHER_FORM;
        } else {
            Long approvalPosition = 0l;
            String approvalComment = "";
            if (request.getParameter("approvalComment") != null)
                approvalComment = request.getParameter("approvalComent");
            if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
            CVoucherHeader savedVoucherHeader;
            try {
                savedVoucherHeader = journalVoucherService.create(voucherHeader, approvalPosition, approvalComment, null,
                        workFlowAction);
            } catch (final ValidationException e) {
                setDropDownValues(model);
                model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
                prepareWorkflow(model, voucherHeader, new WorkflowContainer());
                prepareValidActionListByCutOffDate(model);
                voucherHeader.setVoucherDate(new Date());
                model.addAttribute(VOUCHER_NUMBER_GENERATION_AUTO, isVoucherNumberGenerationAuto(voucherHeader, model));
                resultBinder.reject("", e.getErrors().get(0).getMessage());
                return JOURNALVOUCHER_FORM;
            }

            final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                    savedVoucherHeader.getState(), savedVoucherHeader.getId(), approvalPosition,"");

            return "redirect:/journalvoucher/success?approverDetails= " + approverDetails + "&voucherNumber="
                    + savedVoucherHeader.getVoucherNumber() + "&workFlowAction=" + workFlowAction;

        }
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("voucherNumber") final String voucherNumber, final Model model,
            final HttpServletRequest request) {
        final String workFlowAction = request.getParameter("workFlowAction");
        final String[] keyNameArray = request.getParameter("approverDetails").split(",");
        Long id = 0L;
        String approverName = "";
        String currentUserDesgn = "";
        String nextDesign = "";
        if (keyNameArray.length != 0 && keyNameArray.length > 0)
            if (keyNameArray.length == 1)
                id = Long.parseLong(keyNameArray[0].trim());
            else if (keyNameArray.length == 3) {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
            } else {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
                nextDesign = keyNameArray[3];
            }

        if (id != null)
            model.addAttribute("approverName", approverName);
        model.addAttribute("currentUserDesgn", currentUserDesgn);
        model.addAttribute("nextDesign", nextDesign);

        final CVoucherHeader voucherHeader = journalVoucherService.getByVoucherNumber(voucherNumber);

        final String message = getMessageByStatus(voucherHeader, approverName, nextDesign, workFlowAction);

        model.addAttribute("message", message);

        return "expensebill-success";
    }

    
    
    @RequestMapping(value = "/searchVoucher", method = RequestMethod.POST)
    public String searchVoucher(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
    	voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
        prepareWorkflow(model, voucherHeader, new WorkflowContainer());
       
        
        voucherHeader.setVoucherDate(new Date());
        return JOURNALVOUCHER_SEARCH;
    }
    
    @RequestMapping(value = "/searchVoucherResult",params = "search", method = RequestMethod.POST)
    public String searchVoucherResult(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
       
    	LOGGER.info("Start");
    	List<BillRegisterReportBean> billRegReportList = new ArrayList<BillRegisterReportBean>()  ;
    	Map<Long,VoucherDetailMain> voucherDetailMainMapping=new HashMap<Long,VoucherDetailMain>();
    	Map<Long,CVoucherHeader> voucherDetailPartyMapping=new HashMap<Long,CVoucherHeader>();
    	Map<Long,List<VoucherDetailMiscMapping>> voucherDetailMiscMapping=new HashMap<Long,List<VoucherDetailMiscMapping>>();
    	Map<Long,CVoucherHeader> voucherDetailBpvMapping=new HashMap<Long,CVoucherHeader>();
    	Map<Long,CVoucherHeader> voucherDetailIntrumentMapping=new HashMap<Long,CVoucherHeader>();
    	Map<Long,List<VoucherDetailLedger>> voucherDetailLedgerInstrument=new HashMap<Long,List<VoucherDetailLedger>>();
    	SQLQuery queryMain =  null;
    	final StringBuffer query1 = new StringBuffer(500);
    	
    	List<Object[]> list= null;
    	query1
        .append("select vdm.voucherid ,vdm.vouchernumber ,vdm.status,vdm.head,vdm.department from voucher_detail_main vdm where vdm.fund ="+voucherHeader.getFundId().getId())
        		.append(getDateQuery(voucherHeader.getBillFrom(), voucherHeader.getBillTo()))
        		.append(getMisQuery(voucherHeader));
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
    			voucherDetailMainMapping.put(voucherDetailMain.getId(), voucherDetailMain);
    		}
    	}
    	LOGGER.info("after main map");
    	//voucher detail bpv mapping
    	CVoucherHeader bpvMapping =null;
    	SQLQuery queryBpv =  null;
    	final StringBuffer query2 = new StringBuffer(500);
    	 list= null;
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
    			voucherDetailBpvMapping.put(bpvMapping.getId(), bpvMapping);
    		}
    	}
    	LOGGER.info("after map");
    	//misc bill detail mapping
    	SQLQuery queryMisc =  null;
    	final StringBuffer query3 = new StringBuffer(500);
   	 list= null;
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
   	LOGGER.info("afterr map");
   	//party
   	SQLQuery queryParty =  null;
   	final StringBuffer query4 = new StringBuffer(500);
  	 list= null;
  	query4
      .append("select vdp.id,vdp.detailname from voucher_detail_party vdp  ");
  	LOGGER.info("Query 4 :: "+query4.toString());
  	queryParty=this.persistenceService.getSession().createSQLQuery(query4.toString());
   	list = queryParty.list();
  	LOGGER.info("1 map");
  	CVoucherHeader partyDetail=null;
  	
  	if (list.size() != 0) {
  		LOGGER.info("size ::: "+list.size());
  		for (final Object[] object : list)
  		{
  			partyDetail=new CVoucherHeader();
  			partyDetail.setId(Long.parseLong(object[0].toString()));
  			partyDetail.setVoucherNumber(object[1].toString());
  			voucherDetailPartyMapping.put(partyDetail.getId(), partyDetail);
  		}
  	}
  	LOGGER.info("after party");
   	//ledger
  	SQLQuery queryledger =  null;
   	final StringBuffer query7 = new StringBuffer(500);
  	 list= null;
  	query7
      .append("select vdl.glcode,vdl.creditamount,vdl.debitamount,vdl.voucherheaderid from voucher_detail_ledger  vdl  ");
  	LOGGER.info("Query 7 :: "+query7.toString());
  	queryledger=this.persistenceService.getSession().createSQLQuery(query7.toString());
   	list = queryledger.list();
  	LOGGER.info("1 map");
  	VoucherDetailLedger ledgerDetail=null;
  	List<VoucherDetailLedger> ledgerList=null;
  	if (list.size() != 0) {
  		LOGGER.info("size ::: "+list.size());
  		for (final Object[] object : list)
  		{
  			ledgerDetail=new VoucherDetailLedger();
  			ledgerDetail.setGlCode(object[0].toString());
  			ledgerDetail.setCreditAmount(new BigDecimal((object[1].toString())));
  			ledgerDetail.setDebitAmount(new BigDecimal((object[2].toString())));
  			ledgerDetail.setVoucherId(Long.parseLong(object[3].toString()));
  			
  			if(voucherDetailLedgerInstrument.get(ledgerDetail.getVoucherId()) == null)
  			{
  				ledgerList=new ArrayList<VoucherDetailLedger>();
  				ledgerList.add(ledgerDetail);
  				voucherDetailLedgerInstrument.put(ledgerDetail.getVoucherId(), ledgerList);
  			}
  			else
  			{
  				voucherDetailLedgerInstrument.get(ledgerDetail.getVoucherId()).add(ledgerDetail);
  			}
  			
  		}
  	}
  	//tds
  	SQLQuery querytds =  null;
   	final StringBuffer query10 = new StringBuffer(500);
  	 list= null;
  	query10
      .append("select tds.id,tds.type from tds where isactive =true");
  	LOGGER.info("Query 10 :: "+query10.toString());
  	querytds=this.persistenceService.getSession().createSQLQuery(query10.toString());
   	list = querytds.list();
  	LOGGER.info("pex map");
  	List<String> tds=new ArrayList<String>();
  	
  	if (list.size() != 0) {
  		LOGGER.info("size ::: "+list.size());
  		for (final Object[] object : list)
  		{
  			tds.add(object[1].toString());
  		}
  		
  	}	
  	
   	//pex
  	SQLQuery queryInstru =  null;
   	final StringBuffer query5 = new StringBuffer(500);
  	 list= null;
  	query5
      .append("select vdi.id,vdi.transactionnumber, vdi.transactiondate ,vdi.voucherheaderid from voucher_detail_instrument vdi   ");
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
  			voucherDetailIntrumentMapping.put(pexDetail.getId(), pexDetail);
  		}
  		
  	}	
   	//results
   	BillRegisterReportBean resultset=null;
   	Set<Long> keys=voucherDetailMainMapping.keySet();
   	VoucherDetailMain result=null;
   	for(Long key : keys)
   	{
   		result=voucherDetailMainMapping.get(key);
   		if(voucherDetailMiscMapping.get(key) != null )
   		{
   			for(VoucherDetailMiscMapping row :voucherDetailMiscMapping.get(key))
   			{
   				resultset=new BillRegisterReportBean();
   				if(voucherDetailPartyMapping.get(key) != null)
   				{
   					resultset.setPartyName(voucherDetailPartyMapping.get(key).getVoucherNumber());
   				}
   				resultset.setDepartmentCode(result.getDepartment());
   				resultset.setBudgetHead(result.getHead());
   				resultset.setVoucherNumber(result.getVoucherNumber());
   				if(row.getBpvId() != null)
   				{
   					resultset.setPaymentVoucherNumber(voucherDetailBpvMapping.get(row.getBpvId()).getVoucherNumber());
   					if(voucherDetailIntrumentMapping.get(row.getBpvId()) != null && voucherDetailIntrumentMapping.get(row.getBpvId()).getVoucherNumber() != null && !voucherDetailIntrumentMapping.get(row.getBpvId()).getVoucherNumber().isEmpty())
   					{
   						resultset.setPexNo(voucherDetailIntrumentMapping.get(row.getBpvId()).getVoucherNumber());
   					}
   					if(voucherDetailIntrumentMapping.get(row.getBpvId()) != null && voucherDetailIntrumentMapping.get(row.getBpvId()).getApprovalComent() != null && !voucherDetailIntrumentMapping.get(row.getBpvId()).getApprovalComent().isEmpty())
   					{
   						resultset.setPexNodate(voucherDetailIntrumentMapping.get(row.getBpvId()).getApprovalComent());
   					}
   				}
   				if(row.getAmountPaid() != null)
   				{
   					resultset.setPaidAmount(row.getAmountPaid());
   				}
   				populateTax(resultset,result,voucherDetailLedgerInstrument,tds);
   				resultset.setStatus(getVoucherStatus(Integer.parseInt(result.getStatus())));
   				billRegReportList.add(resultset);
   			}
   		}
   		else
   		{
   			resultset=new BillRegisterReportBean();
				if(voucherDetailPartyMapping.get(key) != null)
				{
					resultset.setPartyName(voucherDetailPartyMapping.get(key).getVoucherNumber());
				}
				resultset.setDepartmentCode(result.getDepartment());
				resultset.setBudgetHead(result.getHead());
				resultset.setVoucherNumber(result.getVoucherNumber());
				populateTax(resultset,result,voucherDetailLedgerInstrument,tds);
				resultset.setStatus(getVoucherStatus(Integer.parseInt(result.getStatus())));
				billRegReportList.add(resultset);
   		}
   		
   	}
    	model.addAttribute("billRegReportList", billRegReportList);
        return JOURNALVOUCHER_SEARCH;
    }
    
    
    private void populateTax(BillRegisterReportBean resultset, VoucherDetailMain result, Map<Long, List<VoucherDetailLedger>> voucherDetailLedgerInstrument, List<String> tds) {
		
    	List<VoucherDetailLedger> detail=voucherDetailLedgerInstrument.get(result.getId());
    	BigDecimal tdsTax=new BigDecimal("0");
    	BigDecimal igstTax=new BigDecimal("0");
    	BigDecimal cgstTax=new BigDecimal("0");
    	BigDecimal laborTax=new BigDecimal("0");
    	BigDecimal colTax=new BigDecimal("0");
    	BigDecimal qualCessTax=new BigDecimal("0");
    	BigDecimal waterTax=new BigDecimal("0");
    	BigDecimal fineTax=new BigDecimal("0");
    	BigDecimal secTax=new BigDecimal("0");
    	BigDecimal anyTax=new BigDecimal("0");
    	BigDecimal net=new BigDecimal("0");
    	BigDecimal decTax=new BigDecimal("0");
    	List<String> taxNonMatching=new ArrayList<String>();
    	List<BigDecimal> taxNonMatchingAmount=new ArrayList<BigDecimal>();
    	if(detail != null && !detail.isEmpty())
    	{
    		for(VoucherDetailLedger row:detail)
        	{
        		if(row.getDebitAmount().compareTo(BigDecimal.ZERO) == 0)
        		{
        			if(row.getGlCode().equalsIgnoreCase("3502007") || row.getGlCode().equalsIgnoreCase("3502009") || row.getGlCode().equalsIgnoreCase("3502010") || row.getGlCode().equalsIgnoreCase("3502011") || row.getGlCode().equalsIgnoreCase("3502012") || row.getGlCode().equalsIgnoreCase("3502008") )
        			{
        				tdsTax=tdsTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("3502055"))
        			{
        				igstTax=igstTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("3502054"))
        			{
        				cgstTax=cgstTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("3502018"))
        			{
        				laborTax=laborTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("1408055"))
        			{
        				colTax=colTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("1405014"))
        			{
        				waterTax=waterTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("3502058"))
        			{
        				qualCessTax=qualCessTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("1402003"))
        			{
        				fineTax=fineTax.add(row.getCreditAmount());
        			}
        			else if(row.getGlCode().equalsIgnoreCase("3401004"))
        			{
        				secTax=secTax.add(row.getCreditAmount());
        			}
        			else
        			{
        				taxNonMatching.add(row.getGlCode());
        				taxNonMatchingAmount.add(row.getCreditAmount());
        			}
        		}
        		else
        		{
        			resultset.setGrossAmount(row.getDebitAmount());
        		}
        	}
        	resultset.setTaxAmount(tdsTax);
        	resultset.setIgstAmount(igstTax);
        	resultset.setCgstAmount(cgstTax);
        	resultset.setLabourcessAmount(laborTax);
        	resultset.setCollectionchargesAmount(colTax);
        	resultset.setWaterChargesAmount(waterTax);
        	resultset.setQualityAmount(qualCessTax);
        	resultset.setPenaltyAmount(fineTax);
        	resultset.setSecuritAmount(secTax);
        	
        	int i=0;
        	for(String rowTds:taxNonMatching) {
        		if(tds.contains(rowTds))
        		{
        			anyTax = anyTax.add(taxNonMatchingAmount.get(i));
        		}
        		i++;
        	}
        	resultset.setDeductionAmount(anyTax);
        	
        	decTax=decTax.add(tdsTax).add(igstTax).add(cgstTax).add(laborTax).add(colTax).add(waterTax).add(qualCessTax).add(fineTax).add(secTax).add(anyTax);
        	net=resultset.getGrossAmount().subtract(decTax);
        	resultset.setNetAmount(net);
    	}
    	
    	
	}

	@RequestMapping(value = "/searchVoucherResult",params = "export", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<InputStreamResource> voucherExport(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) throws IOException {
       LOGGER.info("export");
       List<BillRegisterReportBean> billRegReportList = new ArrayList<BillRegisterReportBean>()  ;
   	Map<Long,VoucherDetailMain> voucherDetailMainMapping=new HashMap<Long,VoucherDetailMain>();
   	Map<Long,CVoucherHeader> voucherDetailPartyMapping=new HashMap<Long,CVoucherHeader>();
   	Map<Long,List<VoucherDetailMiscMapping>> voucherDetailMiscMapping=new HashMap<Long,List<VoucherDetailMiscMapping>>();
   	Map<Long,CVoucherHeader> voucherDetailBpvMapping=new HashMap<Long,CVoucherHeader>();
   	Map<Long,CVoucherHeader> voucherDetailIntrumentMapping=new HashMap<Long,CVoucherHeader>();
   	Map<Long,List<VoucherDetailLedger>> voucherDetailLedgerInstrument=new HashMap<Long,List<VoucherDetailLedger>>();
   	SQLQuery queryMain =  null;
   	final StringBuffer query1 = new StringBuffer(500);
   	
   	List<Object[]> list= null;
   	query1
       .append("select vdm.voucherid ,vdm.vouchernumber ,vdm.status,vdm.head,vdm.department from voucher_detail_main vdm where vdm.fund ="+voucherHeader.getFundId().getId())
       		.append(getDateQuery(voucherHeader.getBillFrom(), voucherHeader.getBillTo()))
       		.append(getMisQuery(voucherHeader));
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
   			voucherDetailMainMapping.put(voucherDetailMain.getId(), voucherDetailMain);
   		}
   	}
   	LOGGER.info("after main map");
   	//voucher detail bpv mapping
   	CVoucherHeader bpvMapping =null;
   	SQLQuery queryBpv =  null;
   	final StringBuffer query2 = new StringBuffer(500);
   	 list= null;
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
   			voucherDetailBpvMapping.put(bpvMapping.getId(), bpvMapping);
   		}
   	}
   	LOGGER.info("after map");
   	//misc bill detail mapping
   	SQLQuery queryMisc =  null;
   	final StringBuffer query3 = new StringBuffer(500);
  	 list= null;
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
  	LOGGER.info("afterr map");
  	//party
  	SQLQuery queryParty =  null;
  	final StringBuffer query4 = new StringBuffer(500);
 	 list= null;
 	query4
     .append("select vdp.id,vdp.detailname from voucher_detail_party vdp  ");
 	LOGGER.info("Query 4 :: "+query4.toString());
 	queryParty=this.persistenceService.getSession().createSQLQuery(query4.toString());
  	list = queryParty.list();
 	LOGGER.info("1 map");
 	CVoucherHeader partyDetail=null;
 	
 	if (list.size() != 0) {
 		LOGGER.info("size ::: "+list.size());
 		for (final Object[] object : list)
 		{
 			partyDetail=new CVoucherHeader();
 			partyDetail.setId(Long.parseLong(object[0].toString()));
 			partyDetail.setVoucherNumber(object[1].toString());
 			voucherDetailPartyMapping.put(partyDetail.getId(), partyDetail);
 		}
 	}
 	LOGGER.info("after party");
  	//ledger
 	SQLQuery queryledger =  null;
  	final StringBuffer query7 = new StringBuffer(500);
 	 list= null;
 	query7
     .append("select vdl.glcode,vdl.creditamount,vdl.debitamount,vdl.voucherheaderid from voucher_detail_ledger  vdl  ");
 	LOGGER.info("Query 7 :: "+query7.toString());
 	queryledger=this.persistenceService.getSession().createSQLQuery(query7.toString());
  	list = queryledger.list();
 	LOGGER.info("1 map");
 	VoucherDetailLedger ledgerDetail=null;
 	List<VoucherDetailLedger> ledgerList=null;
 	if (list.size() != 0) {
 		LOGGER.info("size ::: "+list.size());
 		for (final Object[] object : list)
 		{
 			ledgerDetail=new VoucherDetailLedger();
 			ledgerDetail.setGlCode(object[0].toString());
 			ledgerDetail.setCreditAmount(new BigDecimal((object[1].toString())));
 			ledgerDetail.setDebitAmount(new BigDecimal((object[2].toString())));
 			ledgerDetail.setVoucherId(Long.parseLong(object[3].toString()));
 			
 			if(voucherDetailLedgerInstrument.get(ledgerDetail.getVoucherId()) == null)
 			{
 				ledgerList=new ArrayList<VoucherDetailLedger>();
 				ledgerList.add(ledgerDetail);
 				voucherDetailLedgerInstrument.put(ledgerDetail.getVoucherId(), ledgerList);
 			}
 			else
 			{
 				voucherDetailLedgerInstrument.get(ledgerDetail.getVoucherId()).add(ledgerDetail);
 			}
 			
 		}
 	}
 	//tds
 	SQLQuery querytds =  null;
  	final StringBuffer query10 = new StringBuffer(500);
 	 list= null;
 	query10
     .append("select tds.id,tds.type from tds where isactive =true");
 	LOGGER.info("Query 10 :: "+query10.toString());
 	querytds=this.persistenceService.getSession().createSQLQuery(query10.toString());
  	list = querytds.list();
 	LOGGER.info("pex map");
 	List<String> tds=new ArrayList<String>();
 	
 	if (list.size() != 0) {
 		LOGGER.info("size ::: "+list.size());
 		for (final Object[] object : list)
 		{
 			tds.add(object[1].toString());
 		}
 		
 	}	
 	
  	//pex
 	SQLQuery queryInstru =  null;
  	final StringBuffer query5 = new StringBuffer(500);
 	 list= null;
 	query5
     .append("select vdi.id,vdi.transactionnumber, vdi.transactiondate ,vdi.voucherheaderid from voucher_detail_instrument vdi   ");
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
 			voucherDetailIntrumentMapping.put(pexDetail.getId(), pexDetail);
 		}
 		
 	}	
  	//results
  	BillRegisterReportBean resultset=null;
  	Set<Long> keys=voucherDetailMainMapping.keySet();
  	VoucherDetailMain result=null;
  	for(Long key : keys)
  	{
  		result=voucherDetailMainMapping.get(key);
  		if(voucherDetailMiscMapping.get(key) != null )
  		{
  			for(VoucherDetailMiscMapping row :voucherDetailMiscMapping.get(key))
  			{
  				resultset=new BillRegisterReportBean();
  				if(voucherDetailPartyMapping.get(key) != null)
  				{
  					resultset.setPartyName(voucherDetailPartyMapping.get(key).getVoucherNumber());
  				}
  				resultset.setDepartmentCode(result.getDepartment());
  				resultset.setBudgetHead(result.getHead());
  				resultset.setVoucherNumber(result.getVoucherNumber());
  				if(row.getBpvId() != null)
  				{
  					resultset.setPaymentVoucherNumber(voucherDetailBpvMapping.get(row.getBpvId()).getVoucherNumber());
  					if(voucherDetailIntrumentMapping.get(row.getBpvId()) != null && voucherDetailIntrumentMapping.get(row.getBpvId()).getVoucherNumber() != null && !voucherDetailIntrumentMapping.get(row.getBpvId()).getVoucherNumber().isEmpty())
  					{
  						resultset.setPexNo(voucherDetailIntrumentMapping.get(row.getBpvId()).getVoucherNumber());
  					}
  					if(voucherDetailIntrumentMapping.get(row.getBpvId()) != null && voucherDetailIntrumentMapping.get(row.getBpvId()).getApprovalComent() != null && !voucherDetailIntrumentMapping.get(row.getBpvId()).getApprovalComent().isEmpty())
  					{
  						resultset.setPexNodate(voucherDetailIntrumentMapping.get(row.getBpvId()).getApprovalComent());
  					}
  				}
  				if(row.getAmountPaid() != null)
  				{
  					resultset.setPaidAmount(row.getAmountPaid());
  				}
  				populateTax(resultset,result,voucherDetailLedgerInstrument,tds);
  				resultset.setStatus(getVoucherStatus(Integer.parseInt(result.getStatus())));
  				billRegReportList.add(resultset);
  			}
  		}
  		else
  		{
  			resultset=new BillRegisterReportBean();
				if(voucherDetailPartyMapping.get(key) != null)
				{
					resultset.setPartyName(voucherDetailPartyMapping.get(key).getVoucherNumber());
				}
				resultset.setDepartmentCode(result.getDepartment());
				resultset.setBudgetHead(result.getHead());
				resultset.setVoucherNumber(result.getVoucherNumber());
				populateTax(resultset,result,voucherDetailLedgerInstrument,tds);
				resultset.setStatus(getVoucherStatus(Integer.parseInt(result.getStatus())));
				billRegReportList.add(resultset);
  		}
  		
  	}
   	
   	String[] COLUMNS = {"S.no.", "Party Name", "DIVISION", "BUDGET HEAD", "Gross Amount", "TDS/I", "TDS ON IGST", "TDS ON CGST/UTGST", "Labour Cess", "Collection charges", "Water charges", "Quality Cess", "Penalty/Fine", "Security/Amt withheld", "Any other deduction", "Net Amount", "Paid Amount", "Journal Voucher number", "Payment voucher number", "PEX NUMBER", "PEX DATE", "Status"};
	
	ByteArrayInputStream in = resultToExcel(billRegReportList, COLUMNS);
	
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=VoucherDetailReport.xls");
	return ResponseEntity
            .ok()
            .headers(headers)
            .body(new InputStreamResource(in));
    }
    
    
    
    protected StringBuffer getQuery(CVoucherHeader voucherHeader) {
        final StringBuffer query = new StringBuffer(1000);
        final StringBuffer whereQuery = new StringBuffer(200);
        new StringBuffer(50);

        

        if (null != voucherHeader.getFundId())
            whereQuery.append(" and vh.fundid=" + voucherHeader.getFundId().getId());
        if (null != voucherHeader.getVouchermis().getDepartmentcode() && !voucherHeader.getVouchermis().getDepartmentcode().equals("-1"))
            whereQuery.append(" and mis.departmentcode='" + voucherHeader.getVouchermis().getDepartmentcode()+"'");
       
      
       
        if (null != voucherHeader.getBillFrom())
        	whereQuery.append(" and vh.voucherdate >='")
					.append(DDMMYYYYFORMAT1.format(voucherHeader.getBillFrom()))
					.append("'");
        if (null != voucherHeader.getBillTo())
        	whereQuery.append(" and vh.voucherdate <='")
					.append(DDMMYYYYFORMAT1.format(voucherHeader.getBillTo()))
					.append("'");
        
        
        query.append(getQueryByExpndType("Expense", whereQuery.toString(), voucherHeader));
      

        return query;
    }
   private String getDepartmentcode(String departmentCode) {
	   String departname="";

	   Department department=microserviceUtils.getDepartmentByCode(departmentCode);
	   if(department!=null)
		  departname=department.getName();
	   
	   return departname;
    
   }
    protected String getQueryByExpndType(final String expndType, final String whereQuery,CVoucherHeader voucherHeader) {

    	
        String voucherQry = "";
       
        // voucher header condition for complete bill register report
        if (voucherHeader.getVoucherNumber() != null && !StringUtils.isEmpty(voucherHeader.getVoucherNumber()))
            voucherQry = " and vh.vouchernumber like '%" + voucherHeader.getVoucherNumber() + "%'";
        final StringBuffer query = new StringBuffer(500);

        query.append(
                " select  vh.vouchernumber, mis.departmentcode, vh.id as voucherid, mis.functionid")
                .
        		append(" from   voucherheader vh,vouchermis mis ")
                .
        		append(" where vh.type='Journal Voucher'   and mis.voucherheaderid =vh.id ")
                .
                append(voucherQry)
              
             //   append("  and vh.status = 4")
                .append(whereQuery);
              

        if (voucherHeader.getVoucherNumber() == null || StringUtils.isEmpty(voucherHeader.getVoucherNumber())) {
            query.append(" UNION ");

            // query to get bills for voucher is not created
            
            query.append(" select  vh.vouchernumber, mis.departmentcode, vh.id as voucherid, mis.functionid")
            		.
    		append(" from   voucherheader vh,vouchermis mis ")
            		.
    		append(" where vh.type='Journal Voucher'   and mis.voucherheaderid =vh.id ").
              append(voucherQry)
            		.
               append(whereQuery);
                   
        }

        return query.toString();
    }
 
 
 
 private List<InstrumentHeader> getPexNumber(Long voucherheaderid,List<Object[]> rows) {
 	String deducvh="";
 	
 	List<InstrumentHeader> instrumentHeaderList=new ArrayList<>();
 	
 	try
 	{
 	    
 	   MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[3] !=null)
 	    		{
 	    			generalLedger.put(Long.valueOf(null != element[3] ? element[3].toString(): "0"), element);
 	    		}
 	    		
 	    	}
 	    	
 	    	
 	    	 List<Object[]> list = (List<Object[]>) generalLedger.get(voucherheaderid);
	 	    	
 	    	 for (Object[] element : list) {

 	    		InstrumentHeader instrumentHeader=new InstrumentHeader();
 	    		if(element[1] !=null)
 	    		{
 	    			deducvh= element[1].toString();
 	    			instrumentHeader.setTransactionNumber(deducvh);
 	    		}
 	    		else
 	    		{
 	    			deducvh= "";
 	    			instrumentHeader.setTransactionNumber(deducvh);
 	    		}
 	    		if(element[2] !=null)
 	    		{
 	    			deducvh= element[2].toString();
 	    			instrumentHeader.setVoucherNumber(deducvh);
 	    		}
 	    		else
 	    		{
 	    			deducvh= "";
 	    			instrumentHeader.setVoucherNumber(deducvh);
 	    		}
 	    		
 	    		instrumentHeaderList.add(instrumentHeader);
 	    	}
 	    	
 	    	
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return instrumentHeaderList;
 }

 
 private BigDecimal getnetAmount( Long voucherheaderid,BigDecimal grossamt,List<Object[]> rows, List<Object[]> otherDeductionAmtrows) {
	// SQLQuery query =  null;
	// 	List<Object[]> rows = null;
	 	String deducvh="";
	 	
	 	 BigDecimal totaltax=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	 BigDecimal netamount=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	 BigDecimal otherDeductionAmount=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	 
	 	try
	 	{
	 	   MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		
	 	    		
	 	    		if(element[0] !=null)
	 	    		{
	 	    			
	 	    		}
	 	    		if(element[3] !=null)
	 	    		{
	 	    			generalLedger.put(Long.valueOf(null != element[3] ? element[3].toString(): "0"), element);
	 	    			
	 	    		}
	 	    	}
	 	    	
	 	    	 List<Object[]> list = (List<Object[]>) generalLedger.get(voucherheaderid);
	 	    	
	 	    	 for (Object[] element : list) {
	 	    		 
	 	    		BillDetail billDetail=new BillDetail();
	 	    		 
	 	    		if(element[0] !=null)
	 	    		{
	 	    			billDetail.setId(null != element[0] ? element[0].toString(): "");
	 	    		}
	 	    		if(element[1] !=null)
	 	    		{
	 	    			billDetail.setConsumerCode(null != element[1] ? element[1].toString() : "");
	 	    			
	 	    			if(!billDetail.getConsumerCode().equalsIgnoreCase("")) {
	 	    				if(
	 	    						/*tax codes*/
	 	    						billDetail.getConsumerCode().equals("3502007")||billDetail.getConsumerCode().equals("3502009")||
	 	    						billDetail.getConsumerCode().equals("3502010")||billDetail.getConsumerCode().equals("3502011")||
	 	    						billDetail.getConsumerCode().equals("3502012") ||
	 	    						/*other glcodes*/
	 	    						billDetail.getConsumerCode().equals("3502055")||billDetail.getConsumerCode().equals("3502054")||
	 	    						billDetail.getConsumerCode().equals("3502018")||billDetail.getConsumerCode().equals("1408055")||
	 	    						billDetail.getConsumerCode().equals("1405014")||billDetail.getConsumerCode().equals("3502058")||billDetail.getConsumerCode().equals("1402003")||
	 	    						billDetail.getConsumerCode().equals("3401004")
	 	    						) {
	 	    					
	 	    					totaltax =totaltax.add(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 	    					billDetail.setConsumerType("Tax");
	 	    					
	 	    				}
	 	    			}
	 	    			
	 	    		}
	 	    	}
	 	    	
	 	    	otherDeductionAmount=otherdeductionAmt(voucherheaderid,otherDeductionAmtrows);
	 	    	totaltax=totaltax.add(otherDeductionAmount);
	 	    	
	 	    	netamount=grossamt.subtract(totaltax);
	 	    }
	 	}catch(Exception e) {e.printStackTrace();}
	 	
	 	return netamount;
 }

 
 private BigDecimal getgrossAmount( Long voucherheaderid,List<Object[]> rows) {
	// SQLQuery query =  null;
	 	String deducvh="";
	 	
	 	 BigDecimal grossamt=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	try
	 	{
	 		 MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		if(element[2] !=null)
	 	    		{
	 	    			generalLedger.put(Long.valueOf(null != element[2] ? element[2].toString(): "0"), element);
	 	    		}
	 	    		
	 	    			}
	 	    		}
	 	    		
	 	    
	 	   List<Object[]> list = (List<Object[]>) generalLedger.get(voucherheaderid);
	    	
	    	 for (Object[] element : list) {
	 	    		if(element[1] !=null)
	 	    		{
	 	    			grossamt =null != element[1] ? new BigDecimal(element[1].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	    				}
	 	    			}
	 	    			
	 	    	
	 	}catch(Exception e) {e.printStackTrace();}
	 	
	 	return grossamt;
 }
 
 
 private List<BillDetail> getbillDetails(Long billid,BigDecimal netAmount,List<Object[]> rows, List<Object[]> otherDedictionAmtrows) {
	 	String deducvh="";
	 	List<ChartOfAccounts> ChartOfAccountsList=new ArrayList<>();
	 	ChartOfAccounts ChartOfAccounts=new ChartOfAccounts();
	 	
	 	List<BillDetail> billDetailList=new ArrayList<>();
	 	
	 BigDecimal totaltax=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	try
	 	{
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		if(element[3] !=null)
	 	    		{
	 	    			generalLedger.put(Long.valueOf(null != element[3] ? element[3].toString(): "0"), element);
	 	    		}
	 	    	}
	 	    	
	 	    	 List<Object[]> list = (List<Object[]>) generalLedger.get(billid);
	 	    	 for (Object[] element : list) {
	 	    		BillDetail billDetail=new BillDetail();
	 	    		if(element[0] !=null)
	 	    		{
	 	    			billDetail.setId(null != element[0] ? element[0].toString(): "");
	 	    		}
	 	    		if(element[1] !=null)
	 	    		{
	 	    			billDetail.setConsumerCode(null != element[1] ? element[1].toString() : "");
	 	    			
	 	    			if(!billDetail.getConsumerCode().equalsIgnoreCase("")) {
	 	    				if(billDetail.getConsumerCode().equals("3502007")||billDetail.getConsumerCode().equals("3502009")||
	 	    						billDetail.getConsumerCode().equals("3502010")||billDetail.getConsumerCode().equals("3502011")||
	 	    						billDetail.getConsumerCode().equals("3502012")) {
	 	    					
	 	    					totaltax =totaltax.add(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 	    					billDetail.setConsumerType("Tax");
	 	    					
	 	    				}
	 	    				
 	    		}
 	    		
	 	    		}
	 	    		if(element[2] !=null)
	 	    		{
	 	    			netAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	    			BigDecimal anyotherDeduction=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	    			if(!billDetail.getConsumerCode().equalsIgnoreCase("")) {
	 	    				if(!(billDetail.getConsumerCode().equals("3502007")||billDetail.getConsumerCode().equals("3502009")||
	 	    						billDetail.getConsumerCode().equals("3502010")||billDetail.getConsumerCode().equals("3502011")||
	 	    						billDetail.getConsumerCode().equals("3502012")||
	 	    						
	 	    						billDetail.getConsumerCode().equals("3502055")||billDetail.getConsumerCode().equals("3502054")||
	 	    						billDetail.getConsumerCode().equals("3502018")||billDetail.getConsumerCode().equals("1408055")||
	 	    						billDetail.getConsumerCode().equals("1405014")||billDetail.getConsumerCode().equals("3502058")||billDetail.getConsumerCode().equals("1402003")||
	 	    						billDetail.getConsumerCode().equals("3401004"))) {
	 	    					
	 	    					 LOGGER.debug("...........beforeotherDeductionAmount........");
		 	    					billDetail.setAmountPaid(otherdeductionAmt(billid,otherDedictionAmtrows));
	 	    					billDetail.setConsumerType("AnyOtherDeduction");
	 	    				}
	 	    				else {
	 		 	    			
	 		 	    			
	 		 	    			billDetail.setAmountPaid(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 		 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 		 	    			}
	 	    				
	 	    			}
	 	    			
	 	    		}
	 	    		
	 	    	
	 	    		
	 	    		billDetailList.add(billDetail);
 	    	}
 	    }
	 	    
	 	    for (BillDetail billDetail : billDetailList) {
	 	    	billDetail.setTotalAmount(totaltax);
			}
 	}catch (Exception e) {
			e.printStackTrace();
		}
	 	
		    return billDetailList;
	 }

 private BigDecimal otherdeductionAmt(Long voucherheaderid,List<Object[]> rows) {
	 
	 	 BigDecimal totaltax=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	try
	 	{
	 		 
	   
	  MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
	    if(rows != null && !rows.isEmpty())
	    {
	    	for(Object[] element : rows)
	    	{
	    		if(element[2] !=null)
 	    		{
 	    			generalLedger.put(Long.valueOf(null != element[2] ? element[2].toString(): "0"), element);
 	    		}
	    		
	    	}
	    	
	    	
	    	List<Object[]> list = (List<Object[]>) generalLedger.get(voucherheaderid);
 	    	
	    	 for (Object[] element : list) {

	    		if(element[1] !=null)
 	    		{
	    			totaltax=null != element[1] ? new BigDecimal(element[1].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
 	    		}
	    	}
	    	
	    	
	    	
	    }
	    
	 	}
	 	catch(Exception e) {e.printStackTrace();}
		return totaltax;
 }

 
	private Long getPayId(String voucherNumber) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	Long deducvh=0L;
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select id,payvhid,billnumber from miscbilldetail m where m.billvhid in (select v.id from voucherheader v where v.vouchernumber =:vouchernumber)");
 	    query.setString("vouchernumber", voucherNumber);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[1] !=null)
 	    		{
 	    			deducvh= Long.parseLong(element[1].toString());
 	    		}
 	    		else
 	    		{
 	    			deducvh= 0L;
 	    		}
 	    		
 	    	}
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

	private List<Miscbilldetail> getbillnum(Long voucherheaderid,List<Object[]> rows,List<Object[]> bpvrows) {
	 	String deducvh="";
	 	
		List<Miscbilldetail> miscbilldetailList=new ArrayList<>();
	 	try
	 	{
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	 MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		if(element[4] !=null)
	 	    		{
	 	    			generalLedger.put(Long.valueOf(null != element[4] ? element[4].toString(): "0"), element);
	 	    		}
	 	    	}
	 	    	
	 	    	
	 	    	List<Object[]> list = (List<Object[]>) generalLedger.get(voucherheaderid);
	 	    	
	 	    	 for (Object[] element : list) {
	 	    		 
	 	    		Miscbilldetail miscbilldetail=new Miscbilldetail();
	 	    		if(element[3] !=null)
	 	    		{
	 	    			deducvh= getBPVNumber(element[3].toString(),bpvrows);
	 	    			miscbilldetail.setBillnumber(deducvh);
	 	    			miscbilldetail.setId(Long.valueOf(element[3].toString()));
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		if(element[2] !=null)
	 	    		{
	 	    			deducvh= element[2].toString();
	 	    			miscbilldetail.setPaidamount(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 		 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		
	 	    		miscbilldetailList.add(miscbilldetail);
	 	    	 }
	 	    	
	 	    		
	 	    	}
	 	}catch (Exception e) {
				e.printStackTrace();
			}
		    return miscbilldetailList;
	 }	
	
	
	private String  getBPVNumber(String voucherNumber,List<Object[]> rows) {
	 	String deducvh="";
	 	Long pavhid=0L;
	 	if(voucherNumber.equals(""))
	 	{
	 		pavhid=0L;
	 	}
	 	else {
	 		pavhid=Long.valueOf(voucherNumber);
	 		}
	 	
	 	try
	 	{
	 	   
	 	   MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>(); 	    
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		if(element[0] !=null)
	 	    		{
	 	    			generalLedger.put(Long.valueOf(null != element[0] ? element[0].toString(): "0"), element);
	 	    		}
	 	    	}
	 	    	
	 	    	List<Object[]> list = (List<Object[]>) generalLedger.get(pavhid);
	 	    	
	 	    	 for (Object[] element : list) {
	 	    		 
	 	    		if(element[1] !=null)
	 	    		{
	 	    			deducvh= element[1].toString();
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		
	 	    	 }
	 	    		
	 	    }
	 	}catch (Exception e) {
				e.printStackTrace();
			}
		    return deducvh;
	 }	
	
	
	
	private String getPartyName(Long voucherheaderid,List<Object[]> rows) {
	 	String deducvh="";
	 	Long deailskey=0L;
	 	
	 	try
	 	{
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	 MultiValuedMap<Long,Object[]> generalLedger = new ArrayListValuedHashMap<>();
	 	    	  
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		if(element[0] !=null)
	 	    		{
	 	    			generalLedger.put(Long.valueOf(null != element[0] ? element[0].toString(): "0"), element);
	 	    		}
	 	    		
	 	    	}
	
	
	 	   	List<Object[]> list = (List<Object[]>) generalLedger.get(voucherheaderid);
 	    
	    	 for (Object[] element : list) {
	    		 if(element[1] !=null)
 	    {
	 	    			deducvh= element[1].toString();
 	    		}
 	    		else
 	    		{
	 	    			deducvh= "";
 	    		}
 	    		
 	    	}
	 	    	
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

	
	private Long getVoucId(String voucherNumber) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	Long deducvh=0L;
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select vh.id,vh.vouchernumber from voucherheader vh where vh.vouchernumber=:vouchernumber");
 	    query.setString("vouchernumber", voucherNumber);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[0] !=null)
 	    		{
 	    			deducvh= Long.parseLong(element[0].toString());
 	    		}
 	    		else
 	    		{
 	    			deducvh= 0L;
 	    		}
 	    		
 	    	}
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

 	    
	 
 
    private String getMessageByStatus(final CVoucherHeader voucherHeader, final String approverName, final String nextDesign,
            final String workFlowAction) {
        String message;

        if (FinancialConstants.PREAPPROVEDVOUCHERSTATUS.equals(voucherHeader.getStatus()))
            message = messageSource.getMessage("msg.journal.voucher.create.success",
                    new String[] { voucherHeader.getVoucherNumber(), approverName, nextDesign }, null);
        else if (FinancialConstants.CREATEDVOUCHERSTATUS.equals(voucherHeader.getStatus()))
            message = messageSource.getMessage("msg.journal.voucher.approved.success",
                    new String[] { voucherHeader.getVoucherNumber() }, null);
        else if (FinancialConstants.WORKFLOW_STATE_CANCELLED.equals(workFlowAction))
            message = messageSource.getMessage("msg.journal.voucher.cancel",
                    new String[] { voucherHeader.getVoucherNumber() }, null);
        else
            message = messageSource.getMessage("msg.journal.voucher.reject",
                    new String[] { voucherHeader.getVoucherNumber(), approverName, nextDesign }, null);

        return message;
    }
    
    public  String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and vdm.voucherDate>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and vdm.voucherDate<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}
    
    public String getMisQuery(final CVoucherHeader voucherHeader) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != voucherHeader) {
			if ( voucherHeader.getVouchermis() != null && voucherHeader.getVouchermis().getDepartmentcode() != null && !voucherHeader.getVouchermis().getDepartmentcode().isEmpty())
			{
				misQuery.append(" and vdm.department='")
						.append(voucherHeader.getVouchermis().getDepartmentcode()+"'");
			}		
			if (null != voucherHeader.getVoucherNumber()) {
				misQuery.append(" and vdm.vouchernumber='");
				misQuery.append(voucherHeader.getVoucherNumber()+"'");
			}
		}
		return misQuery.toString();

	}
    
    public static ByteArrayInputStream resultToExcel(List<BillRegisterReportBean> billRegReportList,String[] COLUMNS)
			throws IOException {
		
    	HSSFWorkbook workbook = new HSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			CreationHelper createHelper = workbook.getCreationHelper();
	 
			Sheet sheet = workbook.createSheet("Voucher Detail Report");
	 
			// Row for Header
			Row headerRow = sheet.createRow(0);
			int sl=1;
			// Header
			for (int col = 0; col < COLUMNS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(COLUMNS[col]);
			}
	 
			int rowIdx = 1;
			for (BillRegisterReportBean detail : billRegReportList) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(String.valueOf(sl++));
				if(detail.getPartyName() != null ) {
					row.createCell(1).setCellValue(detail.getPartyName());
				}
				if(detail.getDepartmentCode() != null) {
					row.createCell(2).setCellValue(detail.getDepartmentCode());
				}
				if(detail.getBudgetHead() != null) {
					row.createCell(3).setCellValue(detail.getBudgetHead());
				}
				if(detail.getGrossAmount() != null) {
					row.createCell(4).setCellValue(detail.getGrossAmount().doubleValue());
				}
				if(detail.getTaxAmount() != null) {
					row.createCell(5).setCellValue(detail.getTaxAmount().doubleValue());
				}
				if(detail.getIgstAmount() != null) {
					row.createCell(6).setCellValue(detail.getIgstAmount().doubleValue());
				}
				if(detail.getCgstAmount() != null) {
					row.createCell(7).setCellValue(detail.getCgstAmount().doubleValue());
				}
				if(detail.getLabourcessAmount() != null) {
					row.createCell(8).setCellValue(detail.getLabourcessAmount().doubleValue());
				}
				if(detail.getCollectionchargesAmount() != null) {
					row.createCell(9).setCellValue(detail.getCollectionchargesAmount().doubleValue());
				}
				if(detail.getWaterChargesAmount() != null) {
					row.createCell(10).setCellValue(detail.getWaterChargesAmount().doubleValue());
				}
				if(detail.getQualityAmount() != null) {
					row.createCell(11).setCellValue(detail.getQualityAmount().doubleValue());
				}
				if(detail.getPenaltyAmount() != null) {
					row.createCell(12).setCellValue(detail.getPenaltyAmount().doubleValue());
				}
				if(detail.getSecuritAmount() !=null) {
					row.createCell(13).setCellValue(detail.getSecuritAmount().doubleValue());
				}
				if(detail.getDeductionAmount() != null) {
					row.createCell(14).setCellValue(detail.getDeductionAmount().doubleValue());
				}
				if(detail.getNetAmount() != null) {
					row.createCell(15).setCellValue(detail.getNetAmount().doubleValue());
				}
				if(detail.getPaidAmount() !=null) {
					row.createCell(16).setCellValue(detail.getPaidAmount().doubleValue());
				}
				if(detail.getVoucherNumber() != null) {
					row.createCell(17).setCellValue(detail.getVoucherNumber());
				}
				if(detail.getPaymentVoucherNumber() != null) {
					row.createCell(18).setCellValue(detail.getPaymentVoucherNumber());
				}
				if(detail.getPexNo() != null) {
					row.createCell(19).setCellValue(detail.getPexNo());
				}
				if(detail.getPexNodate() != null) {
					row.createCell(20).setCellValue(detail.getPexNodate());
				}
				if(detail.getStatus() != null) {
					row.createCell(21).setCellValue(detail.getStatus());
				}
			}
	 
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		
	}
    
    private String getVoucherStatus(final int status) {
		if (FinancialConstants.CREATEDVOUCHERSTATUS.equals(status))
			return "Approved";
		if (FinancialConstants.REVERSEDVOUCHERSTATUS.equals(status))
			return "Reversed";
		if (FinancialConstants.REVERSALVOUCHERSTATUS.equals(status))
			return "Reversal";
		if (FinancialConstants.CANCELLEDVOUCHERSTATUS.equals(status))
			return "Cancelled";
		if (FinancialConstants.PREAPPROVEDVOUCHERSTATUS.equals(status))
			return "Created";
		return "";
	}
}