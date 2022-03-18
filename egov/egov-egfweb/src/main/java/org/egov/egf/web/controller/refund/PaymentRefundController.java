package org.egov.egf.web.controller.refund;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bank;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CGeneralLedgerDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.service.AccountDetailKeyService;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.CheckListService;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.autonumber.ExpenseBillNumberGenerator;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.budget.model.BudgetControlType;
import org.egov.egf.budget.service.BudgetControlTypeService;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.egf.commons.bank.service.CreateBankService;
import org.egov.egf.contract.model.BankRefund;
import org.egov.egf.contract.model.RefundLedger;
import org.egov.egf.contract.model.RefundRequest;
import org.egov.egf.contract.model.RefundResponse;
import org.egov.egf.contract.model.VoucherDetailsResponse;
import org.egov.egf.contract.model.VoucherResponse;
import org.egov.egf.contract.model.VoucherSearch;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.expensebill.service.RefundBillService;
import org.egov.egf.expensebill.service.VouchermisService;
import org.egov.egf.masters.services.OtherPartyService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.egf.web.controller.expensebill.CreateExpenseBillController;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.ChartOfAccounts;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infstr.models.EgChecklists;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.BillType;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.masters.Contractor;
import org.egov.model.masters.OtherParty;
import org.egov.model.voucher.PreApprovedVoucher;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.PaymentRefundUtils;
import org.geotools.filter.IsNullImpl;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.python.netty.util.internal.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exilant.eGov.src.domain.GeneralLedger;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/refund")
public class PaymentRefundController extends BaseBillController {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentRefundController.class);
	private CVoucherHeader voucherHeader = new CVoucherHeader();
	 private static final String ACCDETAILTYPEQUERY = " from Accountdetailtype where id=?";
	private static final String STATE_TYPE = "stateType";
	private static final String BILL_TYPES = "billTypes";
	private static final String EG_BILLREGISTER = "egBillregister";
	
	private static final String NET_PAYABLE_ID = "netPayableId";
	private static final String DESIGNATION = "designation";
    private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    private static final String EXPENSEBILL_VIEW = "expensebill-view";
    private static final String NET_PAYABLE_AMOUNT = "netPayableAmount";
    private static final String REFUNDFOROLDVOUCHER_FORM = "refund-requestoldvoucher-form";

    private static final int BUFFER_SIZE = 4096;
	
	private final String datePattern="dd/MM/yyyy";
	private final String VOUCHER_SEARCH="voucherSearch";
	private final String PR_VOUCHER_SEARCH="pr-voucher-search";
	private final String PR_VOUCHER_VIEW="pr-voucher-view";
	private final String PR_REQUEST_FORM="pr-request-form";
	
	private final String RE_PR_APPROVEDVOUCHER="re-pr-approvedvoucher";
	
	
	
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@Autowired
	private FundHibernateDAO fundHibernateDAO;
	@Autowired
	private CheckListService checkListService;
	
	@Autowired
	private MicroserviceUtils microServiceUtil;
	
	
	@Qualifier("chartOfAccountsService")
	@Autowired
	private ChartOfAccountsService chartOfAccountsService;
	 
	 private void prepareCheckList(final EgBillregister egBillregister) {
	        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
	        egBillregister.getCheckLists().addAll(checkLists);
	    }
	
	    private String cutOffDate;
	    protected DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	    DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
	    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    private boolean finanicalYearAndClosedPeriodCheckIsClosed=false;
	    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
	    Date date;
	
	private final Map<String, String> VOUCHER_TYPES = new HashMap<String, String>();
	@Autowired
    private AccountDetailKeyService accountDetailKeyService;
	@Autowired
	 private EgovMasterDataCaching masterDataCache;
	 @Autowired
	 private EgovCommon egovCommon;
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
	protected MicroserviceUtils microserviceUtils;
		
	@Autowired
	private PaymentRefundUtils paymentRefundUtils;
	@Autowired
	private VoucherSearchUtil voucherSearchUtil;
	@Autowired
    private ChartOfAccountDetailService chartOfAccountDetailService;	
	@Autowired
    private EgBillSubTypeService egBillSubTypeService;
    @Autowired
    private AccountdetailtypeService accountdetailtypeService;
    @Autowired
    private ExpenseBillService expenseBillService;
    @Autowired
    private RefundBillService refundBillService;
    @Autowired
	private FunctionDAO functionDAO;
    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
	private AutonumberServiceBeanResolver beanResolver;
    
    @Autowired
    private VouchermisService vouchermisService;
    @Autowired
    private ApplicationContext applicationContext;
    
    private static final String BANK = "bank";

    @Autowired
    private CreateBankService createBankService;
    @Autowired
    private OtherPartyService otherPartyService;
    @Autowired
    private BudgetControlTypeService budgetControlTypeService;

    @Autowired 
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;
    
    @Autowired
    VoucherService voucherService;
    
    private String fundnew="";
    private String departmentnew="";
    
    public String getFundnew() {
		return fundnew;
	}

	public void setFundnew(String fundnew) {
		this.fundnew = fundnew;
	}

	public String getDepartmentnew() {
		return departmentnew;
	}

	public void setDepartmentnew(String departmentnew) {
		this.departmentnew = departmentnew;
	}

	public PaymentRefundController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
        model.addAttribute("departments", this.getDepartmentsFromMs(""));
        model.addAttribute("bankList", createBankService.getByIsActiveTrueOrderByName());//getallBank());
        model.addAttribute("fundList", fundHibernateDAO.findAllActiveFunds());
    }
  
    public List<BankRefund> getallBank(){
    	final StringBuffer query1 = new StringBuffer(500);
    	List<Object[]> list1= null;
    	SQLQuery queryMain =  null;
    	List<BankRefund> banklist=new ArrayList<BankRefund>();
    	
    	query1
        //.append("select distinct b2.bankid as id,b3.\"name\" as name from bankaccount b left join bankbranch b2 on b.branchid =b2.id left join bank b3 on b2.bankid =b3.id");
    	.append("select distinct (bank0_.id) as col_0_0_, ((bank0_.name||' ')||bankbranch1_.branchname) as col_1_0_ from BANK bank0_ cross join BANKBRANCH bankbranch1_ cross join BANKACCOUNT bankaccoun2_ where bank0_.isactive=true and bankbranch1_.isactive=true and bankaccoun2_.isactive=true and bank0_.id=bankbranch1_.bankid and bankbranch1_.id=bankaccoun2_.branchid and bankaccoun2_.fundid=1 and (bankaccoun2_.type in ('PAYMENTS' , 'RECEIPTS_PAYMENTS')) order by 2");
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list1 = queryMain.list();
    	System.out.println("::Size:: "+list1.size());
    	if(list1.size()!=0) {
    		for(Object[] e : list1)
	    	{
    			BankRefund bank=new BankRefund();
    			bank.setId(e[0].toString());
    			bank.setName(e[1].toString());
    			banklist.add(bank);
	    	}
    	}
    	return banklist;
    }

    
	private void prepareSearchForm(final Model model) {
		VoucherSearch voucherSearch = new VoucherSearch(); 
		voucherSearch.setFromDate(DateUtils.getFormattedDate(paymentRefundUtils.finYearDate(), datePattern));
		VOUCHER_TYPES.put(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL, FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
		VOUCHER_TYPES.put(FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT, FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT);		
		model.addAttribute("voucherTypeList", VOUCHER_TYPES);
		
		//model.addAttribute("departmentList", masterDataCache.get("egi-department"));
		//model.addAttribute("fundList",	paymentRefundUtils.getAllFunds());
		Fund fund = new Fund();
		List<Fund> fund1 = new ArrayList();
		List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","fund");
        for(AppConfigValues value:appConfigValuesList)
        {
	       	fund = fundHibernateDAO.fundByCode(value.getValue());
	       	setFundnew(String.valueOf(fund.getId()));
	       	fund1.add(fundHibernateDAO.fundByCode(value.getValue()));
        }
        
        model.addAttribute("fundList", fund1);
        appConfigValuesList=null;
        List dept1=new ArrayList();
		appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","department");
        for(AppConfigValues value:appConfigValuesList)
        {
        	dept1=microserviceUtils.getDepartments(value.getValue());
        }
        model.addAttribute("departmentList", dept1);
        for(AppConfigValues value:appConfigValuesList)
        {
        	setDepartmentnew(value.getValue());
        }
        appConfigValuesList=null;
        model.addAttribute("fundnew",getFundnew());
        model.addAttribute("departmentnew",getDepartmentnew());
		model.addAttribute("serviceTypeList", microserviceUtils.getBusinessService(null));
		model.addAttribute(VOUCHER_SEARCH, voucherSearch);
		model.addAttribute("bankList", createBankService.getByIsActiveTrueOrderByName());//getallBank());
    }
	
	@RequestMapping(value = "/_searchForm", method = {RequestMethod.GET,RequestMethod.POST})
    public String prVoucherSearch(final Model model) {
		prepareSearchForm(model);
        return PR_VOUCHER_SEARCH;
    }
	
	@RequestMapping(value = "/ajax/_voucherSearch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JSONObject getVouchers(final Model model, @ModelAttribute final VoucherSearch voucherSearch) {
		List<Map<String, Object>> voucherList = new ArrayList<Map<String, Object>>();		
		Map<String, Object> voucherMap = null;
		
		CVoucherHeader voucherHeader = new CVoucherHeader();		
		voucherHeader.setVouchermis(new Vouchermis());
				
		Date fromDate = DateUtils.getDate(voucherSearch.getFromDate(), datePattern);
		Date toDate = DateUtils.getDate(voucherSearch.getToDate(), datePattern);
		
		if(!StringUtils.isEmpty(voucherSearch.getVoucherType())) {
			voucherHeader.setType(voucherSearch.getVoucherType());
		}		
		if(!StringUtils.isEmpty(voucherSearch.getVoucherName())) {
			voucherHeader.setName(voucherSearch.getVoucherName());
		}		
		if(!StringUtils.isEmpty(voucherSearch.getVoucherNumber())) {
			voucherHeader.setVoucherNumber(voucherSearch.getVoucherNumber());
		}
		if(!StringUtils.isEmpty(voucherSearch.getDeptCode())) {
			voucherHeader.getVouchermis().setDepartmentcode(voucherSearch.getDeptCode());
		}
		if(!StringUtils.isEmpty(voucherSearch.getReceiptNumber())) {
			voucherHeader.getVouchermis().setRecieptNumber(voucherSearch.getReceiptNumber());
		}
		if(!StringUtils.isEmpty(voucherSearch.getFundId())) {
			Fund fund = new Fund();
			fund.setId(Integer.valueOf(voucherSearch.getFundId()));
			voucherHeader.setFundId(fund);
		}
		if(!StringUtils.isEmpty(voucherSearch.getServiceType())) {
			voucherHeader.getVouchermis().setServiceName(voucherSearch.getServiceType());
		}
		System.out.println("test");
		List<CVoucherHeader> list = null;
		try {
			list = voucherSearchUtil.search(voucherHeader, fromDate, toDate, "");
		} catch (ApplicationException | ParseException e) {
			e.printStackTrace();
		}
		
		if(null!=list) {
			List<Receipt> receipts = null;
			if(!StringUtils.isEmpty(voucherSearch.getPartyName())) {
				receipts = microserviceUtils.searchRecieptsFinance("MISCELLANEOUS", fromDate, toDate, null,
		                (voucherSearch.getReceiptNumber() != null && !voucherSearch.getReceiptNumber().isEmpty() && !"".equalsIgnoreCase(voucherSearch.getReceiptNumber()))
		                        ? voucherSearch.getReceiptNumber() : null,"search");
			}			
			//for partyname & payee address
		  	SQLQuery queryparty =  null;
		  	List partyList= new ArrayList();
		  	List<Object[]> list1=null;
		  	Map<String,List<String>> partyMap=new HashMap();
		  	final StringBuffer query2 = new StringBuffer(500);
		  	query2
		      .append("select mrd.receipt_number ,mrd.paid_by,mrd.payer_address from mis_receipts_details mrd ");
		  	queryparty=this.persistenceService.getSession().createSQLQuery(query2.toString());
		   	list1 = queryparty.list();
		  	if (list1.size() != 0) {
		  		for (final Object[] e : list1)
		  		{
		  			partyList.add((null != e[1] ? e[1].toString() : ""));
					partyList.add((null != e[2] ? e[2].toString() : ""));
					partyMap.put(e[0].toString(),partyList);
		  		}
		  	}
			Map<Integer, String> sourceMap = paymentRefundUtils.populateSourceMap();
			Map<Long,String> paymentVoucherMap = paymentRefundUtils.populateVoucherMap(list);
			boolean isReceiptNoExist=false;
			for (final CVoucherHeader voucherheader : list) {				
				if(null!=receipts && !receipts.isEmpty()) {	
					isReceiptNoExist=false;
					for (Receipt receipt : receipts) {
						if(!StringUtils.isEmpty(voucherheader.getVouchermis().getRecieptNumber())
								&& !StringUtils.isEmpty(receipt.getReceiptNumber())
									&& receipt.getReceiptNumber().equalsIgnoreCase(voucherheader.getVouchermis().getRecieptNumber())) {							
							for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {
								if((!StringUtils.isEmpty(bill.getPayerName()) && bill.getPayerName().toLowerCase().contains(voucherSearch.getPartyName().toLowerCase()))
										|| (!StringUtils.isEmpty(bill.getPayerAddress()) &&  bill.getPayerAddress().toLowerCase().contains(voucherSearch.getPartyName().toLowerCase()))) {
									voucherMap.put("payeeName", bill.getPayerName());
									isReceiptNoExist=true;
								}							
							}
						}
					}					
					if(!isReceiptNoExist) {
						continue;
					}
				}
				
				voucherMap = new HashMap<String, Object>();
				final BigDecimal amt = voucherheader.getTotalAmount();
				voucherMap.put("id", voucherheader.getId());
				voucherMap.put("vouchernumber", voucherheader.getVoucherNumber());
				voucherMap.put("type", voucherheader.getType());
				voucherMap.put("name", voucherheader.getName());
				if (voucherheader.getVouchermis() != null && voucherheader.getVouchermis().getDepartmentcode() != null
						&& !voucherheader.getVouchermis().getDepartmentcode().equals("-1")) {
					org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(voucherheader.getVouchermis().getDepartmentcode());
					voucherMap.put("deptName", depList.getName());
				}else {
					voucherMap.put("deptName", "-");
				}
				voucherMap.put("voucherdate", voucherheader.getVoucherDate());
				voucherMap.put("fundname", voucherheader.getFundId().getName());
				if (voucherheader.getModuleId() == null)
					voucherMap.put("source", "Internal");
				else
					voucherMap.put("source", sourceMap.get(voucherheader.getModuleId()));

				voucherMap.put("amount", amt.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
				System.out.println(paymentRefundUtils.getVoucherStatus(voucherheader.getStatus()));
				voucherMap.put("status", paymentRefundUtils.getVoucherStatus(voucherheader.getStatus()));
				if(!(voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() != null) {
					//voucherMap.put("pendingWith", paymentRefundUtils.getEmployeeName(voucherheader.getState().getOwnerPosition()));
					voucherMap.put("pendingWith", "NA");
				}else if((voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() == null) {
					if(paymentVoucherMap.get(voucherheader.getId()) != null) {
						voucherMap.put("pendingWith", paymentVoucherMap.get(voucherheader.getId()));
					}
					else {
						voucherMap.put("pendingWith", "-");
					}					
				}else {
					voucherMap.put("pendingWith", "-");
				}
				if(!ObjectUtils.isEmpty(voucherheader.getVouchermis())) {
				if(!StringUtils.isEmpty(voucherheader.getVouchermis().getRecieptNumber())) {
					voucherMap.put("receiptNumber", voucherheader.getVouchermis().getRecieptNumber());
				}else {
					voucherMap.put("receiptNumber", "-");
				}
				if(!StringUtils.isEmpty(voucherHeader.getVouchermis().getServiceName())) {
					voucherMap.put("service", voucherheader.getVouchermis().getServiceName());
				}else {
					voucherMap.put("service", "-");
				}
				
				}
				
								
				if(!voucherMap.containsKey("payeeName")) {
					voucherMap.put("payeeName", "-");
				}
				if(partyMap.containsKey(voucherheader.getVouchermis().getRecieptNumber())) {
					String name="",address="";
					List<String> partyMapList=partyMap.get(voucherheader.getVouchermis().getRecieptNumber());
					for(int i=0;i<partyMapList.size();i+=2) {
						name=name=partyMapList.get(i);
						address=partyMapList.get(i+1);
						voucherMap.put("payeeName",name);
						voucherMap.put("payeeAddress", address);
					}
				}
				
				//voucherList.add(voucherMap);
				if(paymentRefundUtils.getVoucherStatus(voucherheader.getStatus())=="Approved"){
				voucherList.add(voucherMap);
				}
			}
			
			JSONArray jsonArr=new JSONArray();
		    for (Map<String, Object> map : voucherList) {
		        JSONObject jsonObj=new JSONObject();
		        for (Map.Entry<String, Object> entry : map.entrySet()) {
		            String key = entry.getKey();
		            Object value = entry.getValue();
		            jsonObj.put(key,value);                           
		        }
		        jsonArr.add(jsonObj);
		    }
		    
		    JSONObject data=new JSONObject();
		    data.put("data", jsonArr);
		    return data;
		}
		
		JSONObject data=new JSONObject();
	    data.put("data", "[]");
	    return data;
	}
	
	@RequestMapping(value = "/ajax/_getVoucherNameByType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getVoucherNameByType(@RequestParam final String type, final HttpServletResponse response) throws IOException{
		return paymentRefundUtils.getVoucherNamesByType(type);
	}
	
	@RequestMapping(value = "/_viewVoucher", method = {RequestMethod.GET, RequestMethod.POST})
	public String prViewVoucher(@RequestParam(name = "vhid") final Long vhid, final Model model) {
		VoucherDetailsResponse voucherDetails = new VoucherDetailsResponse();
		final List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();
        Map<String, Object> temp = null;
        Map<String, Object> payeeMap = null;
        PreApprovedVoucher subledger = null;
        CChartOfAccounts coa = null;
        final List<Long> glcodeIdList = new ArrayList<Long>();
		voucherDetails.setId(vhid);
		CVoucherHeader voucherHeader = paymentRefundUtils.getVoucherHeader(vhid);
		final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
		BigDecimal dbAmount = BigDecimal.ZERO;
		BigDecimal crAmount = BigDecimal.ZERO;
		
		if(null!=voucherHeader) {
			voucherDetails.setName(voucherHeader.getName());
			voucherDetails.setType(voucherHeader.getType());
			voucherDetails.setDescription(voucherHeader.getDescription());
			voucherDetails.setEffectiveDate(voucherHeader.getEffectiveDate());
			voucherDetails.setVoucherNumber(voucherHeader.getVoucherNumber());
			voucherDetails.setVoucherDate(voucherHeader.getVoucherDate());
			voucherDetails.setFund(voucherHeader.getFundId());
			voucherDetails.setFiscalPeriodId(voucherHeader.getFiscalPeriodId());
			voucherDetails.setStatus(voucherHeader.getStatus());			
			voucherDetails.setOriginalvcId(voucherHeader.getOriginalvcId());
			voucherDetails.setIsConfirmed(voucherHeader.getIsConfirmed());
			voucherDetails.setRefvhId(voucherHeader.getRefvhId());
			voucherDetails.setCgvn(voucherHeader.getCgvn());
			voucherDetails.setModuleId(voucherHeader.getModuleId());
			voucherDetails.setVouchermis(voucherHeader.getVouchermis());
			
			EgBillregister egBillregister = paymentRefundUtils.getEgBillregister(voucherHeader);
			
			if(null != egBillregister && null != egBillregister.getEgBillregistermis()) {
				if (egBillregister.getEgBillregistermis().getFund() != null) {
					voucherDetails.setFundName(egBillregister.getEgBillregistermis().getFund().getName());
				}
		        if (egBillregister.getEgBillregistermis().getDepartmentcode() != null) {
		            org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(egBillregister.getEgBillregistermis().getDepartmentcode());
		            voucherDetails.setDeptName(depList != null ? depList.getName() : "");
		        }
		        if (egBillregister.getEgBillregistermis().getScheme() != null) {
		        	voucherDetails.setScheme(egBillregister.getEgBillregistermis().getScheme().getName());
		        }
		        if (egBillregister.getEgBillregistermis().getSubScheme() != null) {
		        	voucherDetails.setSubScheme(egBillregister.getEgBillregistermis().getSubScheme().getName());
		        }
		        voucherDetails.setNarration(egBillregister.getEgBillregistermis().getNarration());
		        voucherDetails.setBanNumber(egBillregister.getEgBillregistermis().getBudgetaryAppnumber());
		        if (egBillregister.getEgBillregistermis().getFundsource() != null) {
		        	voucherDetails.setFinanceSource(egBillregister.getEgBillregistermis().getFundsource().getName());
		        }
		        
			}
			
			final List<CGeneralLedger> gllist = paymentRefundUtils.getAccountDetails(vhid);
			for (final CGeneralLedger gl : gllist) {
                temp = new HashMap<String, Object>();
                if (gl.getFunctionId() != null) {
                    temp.put(Constants.FUNCTION, paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())).getName());
                    temp.put("functionid", gl.getFunctionId());
                }
                else if (voucherHeader.getVouchermis() != null && voucherHeader.getVouchermis().getFunction() !=null && voucherHeader.getVouchermis().getFunction().getName() != null)
                {
                	temp.put(Constants.FUNCTION, voucherHeader.getVouchermis().getFunction().getName());
                    temp.put("functionid", voucherHeader.getVouchermis().getFunction().getId());
                }
                coa = paymentRefundUtils.getChartOfAccount(gl.getGlcode());
                temp.put("glcodeid", coa.getId());
                glcodeIdList.add(coa.getId());
                temp.put(Constants.GLCODE, coa.getGlcode());
                temp.put("accounthead", coa.getName());
                temp.put(Constants.DEBITAMOUNT, gl.getDebitAmount() == null ? 0 : gl.getDebitAmount());
                temp.put(Constants.CREDITAMOUNT, gl.getCreditAmount() == null ? 0 : gl.getCreditAmount());
                temp.put("billdetailid", gl.getId());
                tempList.add(temp);
                for (CGeneralLedgerDetail gldetail : gl.getGeneralLedgerDetails()) {
                    if (chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(gl.getGlcodeId().getId(), gldetail.getDetailTypeId().getId().intValue()) != null) {
                        subledger = new PreApprovedVoucher();
                        subledger.setGlcode(coa);
                        final Accountdetailtype detailtype = paymentRefundUtils.getAccountdetailtype(gldetail.getDetailTypeId().getId());
                        detailtypeIdList.add(detailtype);
                        subledger.setDetailType(detailtype);
                        payeeMap = new HashMap<>();
                        payeeMap = paymentRefundUtils.getAccountDetails(gldetail.getDetailTypeId().getId(), gldetail.getDetailKeyId(), payeeMap);
                        subledger.setDetailKey(payeeMap.get(Constants.DETAILKEY) + "");
                        subledger.setDetailCode(payeeMap.get(Constants.DETAILCODE) + "");
                        subledger.setDetailKeyId(gldetail.getDetailKeyId());
                        subledger.setAmount(gldetail.getAmount());
                        subledger.setFunctionDetail(temp.get("function") != null ? temp.get("function").toString() : "");
                        if (gl.getDebitAmount() == null || gl.getDebitAmount()  == 0) {
                            subledger.setDebitAmount(BigDecimal.ZERO);
                            subledger.setCreditAmount(gldetail.getAmount());
                            crAmount = crAmount.add(gldetail.getAmount());
                        } else {
                            subledger.setDebitAmount(gldetail.getAmount());
                            subledger.setCreditAmount(BigDecimal.ZERO);
                            dbAmount = dbAmount.add(gldetail.getAmount());
                        }
                        payeeList.add(subledger);
                    }
                }
			}
		}
		
		model.addAttribute("voucherDetails", voucherDetails);
		model.addAttribute("accountDetails", tempList);
		model.addAttribute("subLedgerlist", payeeList);
		model.addAttribute("dbAmount", dbAmount);
		model.addAttribute("crAmount", crAmount);
		
		return PR_VOUCHER_VIEW;
	}
	
	@RequestMapping(value = "/_paymentRequestForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String paymentRequestForm(@RequestParam(name = "vhid") final String vhid, final Model model,@ModelAttribute("message") String message) {
		
		if(message!=null) {
			model.addAttribute("glcodedetailIdmsg", message);
		}else {
			message="";
		}
		model.addAttribute("glCodeDetailIdList","");
		 List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
		 EgBillregister egBillregister = new EgBillregister();
		 
		VoucherDetailsResponse voucherDetails = new VoucherDetailsResponse();
		final List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();
        Map<String, Object> temp = null;
        Map<String, Object> payeeMap = null;
        PreApprovedVoucher subledger = null;
        CChartOfAccounts coa = null;
        final List<Long> glcodeIdList = new ArrayList<Long>();
		voucherDetails.setId(Long.valueOf(vhid));
		CVoucherHeader voucherHeader = paymentRefundUtils.getVoucherHeader(Long.valueOf(vhid));
		final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
		BigDecimal dbAmount = BigDecimal.ZERO;
		BigDecimal crAmount = BigDecimal.ZERO;
		SQLQuery queryMain =  null;
		final StringBuffer query1 = new StringBuffer(500);
    	
    	List<Object[]> list= null;
    	query1
        .append("select id,billid from eg_billregistermis eb where paymentvoucherheaderid ="+voucherHeader.getId());
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list = queryMain.list();
    	EgBillregister bill = null;
    	List<EgBilldetails> egBilldetailsList = new ArrayList<EgBilldetails>();
    	if (list.size() != 0) {
    		LOGGER.info("size ::: "+list.size());
    		for (final Object[] object : list)
    		{
    			bill=new EgBillregister();
    			bill= expenseBillService.getById(Long.parseLong(object[1].toString()));
    			if(bill != null)
    			{
    				egBilldetailsList.addAll(bill.getEgBilldetailes());
    			}
    		}
    	}
    	
		
		//egBilldetailsList.add(egBill);
		//egBilldetailsList.add(egBill2);
		
		
		HashMap<BigDecimal,BigDecimal> hs = new HashMap<BigDecimal,BigDecimal>();
		for(EgBilldetails eg : egBilldetailsList) {
			
			if(hs.containsKey(eg.getGlcodeid())) {
				hs.put(eg.getGlcodeid(), (eg.getDebitamount()).add(hs.get(eg.getGlcodeid())));
			}else {
				hs.put(eg.getGlcodeid(), eg.getDebitamount());
			}
			
		}
		
		if(null!=voucherHeader) {
			voucherDetails.setName(voucherHeader.getName());
			voucherDetails.setType(voucherHeader.getType());
			voucherDetails.setDescription(voucherHeader.getDescription());
			voucherDetails.setEffectiveDate(voucherHeader.getEffectiveDate());
			voucherDetails.setVoucherNumber(voucherHeader.getVoucherNumber());
			voucherDetails.setVoucherDate(voucherHeader.getVoucherDate());
			voucherDetails.setFund(voucherHeader.getFundId());
			voucherDetails.setFiscalPeriodId(voucherHeader.getFiscalPeriodId());
			voucherDetails.setStatus(voucherHeader.getStatus());			
			voucherDetails.setOriginalvcId(voucherHeader.getOriginalvcId());
			voucherDetails.setIsConfirmed(voucherHeader.getIsConfirmed());
			voucherDetails.setRefvhId(voucherHeader.getRefvhId());
			voucherDetails.setCgvn(voucherHeader.getCgvn());
			voucherDetails.setModuleId(voucherHeader.getModuleId());
			voucherDetails.setVouchermis(voucherHeader.getVouchermis());
			
			
			//EgBillregister egBillregister2 =paymentRefundUtils.getEgBillregisterByVoucherHeaderId(voucherHeader);			
			//List<EgBillregister> egBillregisterList = expenseBillService.findByVoucherHeaderId(Long.valueOf(vhid));
			
			
			
			if(paymentRefundUtils.getEgBillregister(voucherHeader)!=null) {
			 egBillregister = paymentRefundUtils.getEgBillregister(voucherHeader);
			}else {
				
				if(voucherDetails.getVouchermis().getDepartmentcode()!=null) {
				 org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(voucherDetails.getVouchermis().getDepartmentcode());
				 voucherDetails.setDeptName(depList != null ? depList.getName() : "");
				model.addAttribute("departcode",voucherDetails.getVouchermis().getDepartmentcode());
				}
			}
			
			
			if(null != egBillregister && null != egBillregister.getEgBillregistermis()) {
				
			}else {
				voucherDetails.setFundName(voucherDetails.getFund().getName());
				model.addAttribute("fundid", voucherDetails.getFund().getId());
			}
			
			if(null != egBillregister && null != egBillregister.getEgBillregistermis()) {
				if (egBillregister.getEgBillregistermis().getFund() != null) {
					voucherDetails.setFundName(egBillregister.getEgBillregistermis().getFund().getName());
					model.addAttribute("fundid", egBillregister.getEgBillregistermis().getFund().getId());
				}
		        if (egBillregister.getEgBillregistermis().getDepartmentcode() != null) {
		            org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(egBillregister.getEgBillregistermis().getDepartmentcode());
		            voucherDetails.setDeptName(depList != null ? depList.getName() : "");
		            model.addAttribute("departcode", egBillregister.getEgBillregistermis().getDepartmentcode());
		        }
		        if (egBillregister.getEgBillregistermis().getScheme() != null) {
		        	voucherDetails.setScheme(egBillregister.getEgBillregistermis().getScheme().getName());
		        }
		        if (egBillregister.getEgBillregistermis().getSubScheme() != null) {
		        	voucherDetails.setSubScheme(egBillregister.getEgBillregistermis().getSubScheme().getName());
		        }
		        
		        
		        if(egBillregister.getEgBillregistermis().getNarration()!=null && !egBillregister.getEgBillregistermis().getNarration().isEmpty()) {
		        voucherDetails.setNarration(egBillregister.getEgBillregistermis().getNarration());
		        }else {
		        	voucherDetails.setNarration(voucherHeader.getDescription());
		        }
		        voucherDetails.setBanNumber(egBillregister.getEgBillregistermis().getBudgetaryAppnumber());
		        if (egBillregister.getEgBillregistermis().getFundsource() != null) {
		        	voucherDetails.setFinanceSource(egBillregister.getEgBillregistermis().getFundsource().getName());
		        	model.addAttribute("fundsource", egBillregister.getEgBillregistermis().getFundsource());
		        }
		        
		        
		        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
				prepareWorkflow(model, egBillregister, new WorkflowContainer());
			}
			
			final List<CGeneralLedger> gllist = paymentRefundUtils.getAccountDetails(Long.valueOf(vhid));
			for (final CGeneralLedger gl : gllist) {
                temp = new HashMap<String, Object>();
                if (gl.getFunctionId() != null) {
                    temp.put(Constants.FUNCTION, paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())).getName());
                    temp.put("functionid", gl.getFunctionId());
                    //temp.put("functionObj",paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())));
                }
                else if (voucherHeader.getVouchermis() != null && voucherHeader.getVouchermis().getFunction() !=null && voucherHeader.getVouchermis().getFunction().getName() != null)
                {
                	temp.put(Constants.FUNCTION, voucherHeader.getVouchermis().getFunction().getName());
                    temp.put("functionid", voucherHeader.getVouchermis().getFunction().getId());
                    //temp.put("functionObj",voucherHeader.getVouchermis().getFunction());
                }
                coa = paymentRefundUtils.getChartOfAccount(gl.getGlcode());
                temp.put("glcodeid", coa.getId());
                glcodeIdList.add(coa.getId());
                temp.put(Constants.GLCODE, coa.getGlcode());
                temp.put("accounthead", coa.getName());
                temp.put(Constants.DEBITAMOUNT, gl.getDebitAmount() == null ? 0 : gl.getDebitAmount());
                temp.put(Constants.CREDITAMOUNT, gl.getCreditAmount() == null ? 0 : gl.getCreditAmount());
                temp.put("billdetailid", gl.getId());
                
                for(Entry<BigDecimal,BigDecimal>bb : hs.entrySet()) {
					System.out.println(BigDecimal.valueOf(coa.getId())+""+bb.getKey());
					if(BigDecimal.valueOf(coa.getId()).equals(bb.getKey())) {
						temp.put("previousAmount", bb.getValue());
						break;
					}else {
						temp.put("previousAmount", 0);
					}
				}
                tempList.add(temp);
                for (CGeneralLedgerDetail gldetail : gl.getGeneralLedgerDetails()) {
                    if (chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(gl.getGlcodeId().getId(), gldetail.getDetailTypeId().getId().intValue()) != null) {
                        subledger = new PreApprovedVoucher();
                        subledger.setGlcode(coa);
                        //subledger.setGlcodeIdDetail(coa.getId());
                        final Accountdetailtype detailtype = paymentRefundUtils.getAccountdetailtype(gldetail.getDetailTypeId().getId());
                        detailtypeIdList.add(detailtype);
                        subledger.setDetailType(detailtype);
                        payeeMap = new HashMap<>();
                        payeeMap = paymentRefundUtils.getAccountDetails(gldetail.getDetailTypeId().getId(), gldetail.getDetailKeyId(), payeeMap);
                        subledger.setDetailKey(payeeMap.get(Constants.DETAILKEY) + "");
                        subledger.setDetailCode(payeeMap.get(Constants.DETAILCODE) + "");
                        subledger.setDetailKeyId(gldetail.getDetailKeyId());
                        subledger.setAmount(gldetail.getAmount());
                        subledger.setFunctionDetail(temp.get("function") != null ? temp.get("function").toString() : "");
                        if (gl.getDebitAmount() == null || gl.getDebitAmount() == 0) {
                            subledger.setDebitAmount(BigDecimal.ZERO);
                            subledger.setCreditAmount(gldetail.getAmount());
                            crAmount = crAmount.add(gldetail.getAmount());
                        } else {
                            subledger.setDebitAmount(gldetail.getAmount());
                            subledger.setCreditAmount(BigDecimal.ZERO);
                            dbAmount = dbAmount.add(gldetail.getAmount());
                        }
                        payeeList.add(subledger);
                    }
                }
			}	
			populateDropDownValues(model);
			
			  model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
			  prepareWorkflow(model, egBillregister, new WorkflowContainer());
			  model.addAttribute("validActionList", validActions);
			  model.addAttribute(BILL_TYPES, BillType.values());
			  prepareValidActionListByCutOffDate(model);
		}
		//final List<Bank> banks = createBankService.getAll();
		final List<Bank> banks = createBankService.getByIsActiveTrueOrderByName();
		EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
		System.out.println(egbillSubtype.getId());
		model.addAttribute("voucherDetails", voucherDetails);
		model.addAttribute("accountDetails", tempList);
		model.addAttribute("subLedgerlist", payeeList);
		model.addAttribute("dbAmount", dbAmount);
		model.addAttribute("crAmount", crAmount);
		model.addAttribute("vhid", vhid);
		model.addAttribute("banks", banks);
		model.addAttribute("billsubtype", egbillSubtype.getId());
		//return PR_REQUEST_FORM;
		return "payRefund-request-form";
	}
	
	@RequestMapping(value = "/updateForm/{billId}", method = RequestMethod.GET)
    public String updateForm(final Model model, @PathVariable String billId,
            final HttpServletRequest request) throws ApplicationException {
		System.out.println("update refund from saveasdraft");
        if (billId.contains("showMode")) {
            String[] billIds = billId.split("\\&");
            billId = billIds[0];
        }
        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
        List<String>  validActions =null;
        if(!egBillregister.getStatus().getDescription().equals("Pending for Cancellation"))
        {
        	validActions = Arrays.asList("Forward","SaveAsDraft");
            prepareWorkflow(model, egBillregister, new WorkflowContainer());
        }
        final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
        egBillregister.setDocumentDetail(documents);
        String departmentCode = this.getDepartmentName(egBillregister.getEgBillregistermis().getDepartmentcode());
        egBillregister.getEgBillregistermis().setDepartmentName(departmentCode);
        setDropDownValues(model);
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        model.addAttribute("mode", "edit");
        model.addAttribute("type", "refundbill");
        model.addAttribute("egBillregister", egBillregister);
        model.addAttribute("validActionList", validActions);
        model.addAttribute("viewBudget", "Y");
        model.addAttribute(BILL_TYPES, BillType.values());
        model.addAttribute("accountDetails", egBillregister);
		//model.addAttribute("subLedgerlist", payeeList);
	//	model.addAttribute("dbAmount", dbAmount);
		//model.addAttribute("crAmount", crAmount);
		//model.addAttribute("vhid", vhid);
		//model.addAttribute("banks", banks);
        EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
		System.out.println(egbillSubtype.getId());
		model.addAttribute("billsubtype", egbillSubtype.getId());
        prepareBillDetailsForView(egBillregister);
        prepareCheckList(egBillregister);
        
        final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService.getNetPayableCodesByAccountDetailType(0);
        
        for (final EgBilldetails details : egBillregister.getBillDetails())
            if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
                    && expensePayableAccountList.contains(details.getChartOfAccounts()))
                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
        model.addAttribute(EG_BILLREGISTER, egBillregister);
        return "payRefund-request-form-update";
    }
	
    private void populateDropDownValues(final Model model) {
    	
    	List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"RefundBillType");
    	List<String> billNames=new ArrayList<String>();
    	for(AppConfigValues row:appConfigValuesList)
    	{
    		billNames.add(row.getValue());
    	}
    	List<EgBillSubType> billSubtypes=new ArrayList<EgBillSubType>();
    	for(EgBillSubType row:egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT))
    	{
    		if(billNames.contains(row.getName()))
    		{
    			billSubtypes.add(row);
    		}
    	}
		
         List<Scheme> schemeList = new ArrayList<Scheme>();
         List<SubScheme> subSchemes = new ArrayList<SubScheme>();
         List<Fundsource> fundSource = new ArrayList<Fundsource>();
    	
        model.addAttribute("billNumberGenerationAuto", refundBillService.isBillNumberGenerationAuto());
        model.addAttribute("billSubTypes", billSubtypes);
        //model.addAttribute("subLedgerTypes", accountdetailtypeService.findAll());
        model.addAttribute("subLedgerTypes", paymentRefundUtils.getAllActiveAccounts());//added Abhishek on 02Dec2021
        model.addAttribute("cFunctions", functionDAO.getAllActiveFunctions());
        model.addAttribute("fundList",	paymentRefundUtils.getAllFunds());
        model.addAttribute("bankList", createBankService.getByIsActiveTrueOrderByName());//getallBank());
        model.addAttribute("subSchemeList",subSchemes);
        model.addAttribute("schemeList",schemeList);
        model.addAttribute("fundsourceList",fundSource);
    }
    
    public List<EgBillSubType> getBillSubTypesRef() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);
    }
    
    
    public List<EgBillSubType> getBillSubTypes() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
    }
    
    public BigDecimal checknull(BigDecimal value) {
    	if(value==null) {
    		return BigDecimal.ZERO;
    	}
    	else {
		return value;
    	}
    }
    public boolean checknullBigDecimal(BigDecimal value) {
    	if(value==null) {
    		return false;
    	}
    	else {
		return true;
    	}
    }
    
    @RequestMapping(value = "/refundCreate", method = RequestMethod.POST)
    public String createRefund(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
         final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction,RedirectAttributes redirectAttributes)
         throws IOException {
            LOGGER.info("RefundBill is creating with user ::"+ApplicationThreadLocals.getUserId());
            String vhid=request.getParameter("vhid");
            System.out.println(vhid);
            Long bg1=null;
            BigDecimal bg2=new BigDecimal(0);
           CVoucherHeader voucher = new CVoucherHeader();
            Vouchermis vouchermis = new Vouchermis();
           egBillregister.setIsCitizenRefund("Y");
           egBillregister.setRefundable("Y"); 
           egBillregister.setBilldate(new Date());
           BigDecimal totalCrAmt = BigDecimal.ZERO;
           
		 for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
			 totalCrAmt=totalCrAmt.add(checknull(egbilldetail.getDebitamount()));
		 }
		 
		 if(null!=egBillregister.getEgBillregistermis().getFunction()) {
			 bg1 = egBillregister.getEgBillregistermis().getFunction().getId();
		 }else {
			 vouchermis = vouchermisService.getVouchermisByVoucherId(Long.parseLong(vhid));
			 bg1 = vouchermis.getFunction().getId();
		 }
		 bg2=new BigDecimal(bg1);
		 List<EgBillPayeedetails> billPayeeDetails = new ArrayList<EgBillPayeedetails>();
		 List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
		 EgBillPayeedetails payeeDetail=new EgBillPayeedetails();
           for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
			   if(checknullBigDecimal(egbilldetail.getDebitamount())==true || checknullBigDecimal(egbilldetail.getCreditamount())==true) {
        		   egbilldetail.setFunctionid(bg2);
        		   egbilldetail.setEgBillregister(egBillregister);
        		   egbilldetail.setLastupdatedtime(new Date());
				   egbilldetailCusList.add(egbilldetail);
				   for (final EgBillPayeedetails payeeDetails : egBillregister.getBillPayeedetails()) 
				   {
			 			  payeeDetail.setEgBilldetailsId(egbilldetail);
			 			  payeeDetail.setLastUpdatedTime(new Date());
			 			  payeeDetail.setAccountDetailKeyId(payeeDetails.getAccountDetailKeyId());
			 			  payeeDetail.setAccountDetailTypeId(payeeDetails.getAccountDetailTypeId());
			 			  payeeDetail.setCreditAmount(payeeDetails.getCreditAmount());
			 			  egbilldetail.getEgBillPaydetailes().add(payeeDetail);
						  billPayeeDetails.add(payeeDetail);
				   }
        	   }
			 }
           
 		  
		  egBillregister.setBillamount(totalCrAmt);
		  egBillregister.setBillDetails(egbilldetailCusList);
		  egBillregister.setBillPayeedetails(billPayeeDetails);
		LOGGER.info("totalCrAmt "+totalCrAmt);
		  EgBillSubType egbillSubtype=null;

		   final String subType = (null!=egBillregister.getEgBillregistermis().getEgBillSubType())?egBillregister.getEgBillregistermis().getEgBillSubType().getName():null;
		  if(null!=subType && !subType.isEmpty() &&!subType.equals("")) {
		 
			  egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase(subType)).findFirst().orElse(null);
		  }
           
         egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());
         ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);
         
		  CFunction function= paymentRefundUtils.getFunction(bg1.longValue());
		  egBillregister.getEgBillregistermis().setFunction(function);
		  egBillregister.getEgBillregistermis().setEgBillSubType(egbillSubtype);
		  LOGGER.info("function "+function);
        String billNumber = v.getNextNumber(egBillregister);
        System.out.println(billNumber);
        	egBillregister.setBillnumber(billNumber);
        if (StringUtils.isEmpty(egBillregister.getExpendituretype()))
        egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);

        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        List<DocumentUpload> list = new ArrayList<>();
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
        if(uploadedFiles!=null)
        for (int i = 0; i < uploadedFiles.length; i++) {

        Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
        byte[] fileBytes = Files.readAllBytes(path);
        ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
        DocumentUpload upload = new DocumentUpload();
        upload.setInputStream(bios);
        upload.setFileName(fileName[i]);
        upload.setContentType(contentType[i]);
        list.add(upload);
        }
        
	    if(egBillregister.getEgBilldetailes()!=null)
	    {
	    	LOGGER.info("bill details done");
	       	egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
	       	if(!egBillregister.getDebitDetails().isEmpty())
		    {
		    	populateBillDetails(egBillregister);
		    }
	    } 
	    
        validateBillNumber(egBillregister, resultBinder);
        
        if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
    	{ 
        	LOGGER.info("bill mis populate done");
        	  populateEgBillregistermisDetails(egBillregister);
    	}
        
		  if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
			  LOGGER.info("bill subledger populate done");
			  refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
			  //validateSubledgeDetails(egBillregister); 
		  }
		  
		  System.out.println("------------------------------"+egBillregister.getBillPayeeDetailsNotLink().isEmpty());	  
	if(egBillregister.getBillPayeeDetailsNotLink().isEmpty()) {

        if (resultBinder.hasErrors()) {
        	System.out.println("from ResultBinder Error");
        	for (Object object : resultBinder.getAllErrors()) {
        	    if(object instanceof FieldError) {
        	        FieldError fieldError = (FieldError) object;

        	        System.out.println(fieldError.getCode());
        	    }

        	    if(object instanceof ObjectError) {
        	        ObjectError objectError = (ObjectError) object;

        	        System.out.println(objectError.getCode());
        	    }
        	}
          return "redirect:/refund/_paymentRequestForm?vhid=" + vhid;
         //return "redirect:/refund/_paymentRequestForm";
          } else {
                Long approvalPosition = 0l;
                String approvalComment = "";
                String approvalDesignation = "";
                if (request.getParameter("approvalComent") != null)
                 approvalComment = request.getParameter("approvalComent");
                if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
                  {
                   	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                      	{            		
                         approvalPosition =populatePosition();            		
                        }
                      else
                         approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
                    }
                 else {
                    if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                     {            		
                      approvalPosition =populatePosition();            		
                     }		            	
                  }
                 if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
                     approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
                        		            
                 EgBillregister savedEgBillregister;
                 egBillregister.setDocumentDetail(list);
                      try {
                    	  LOGGER.info("From egbillregister Save method calling");
                           savedEgBillregister = refundBillService.create(egBillregister, approvalPosition, approvalComment, null, 
                           workFlowAction,approvalDesignation,vhid);
                           LOGGER.info("refund created");
                           
                         } catch (ValidationException e) {
                        	 System.out.println("From Exception saving time");
                        	 e.printStackTrace();
                        
                        return "redirect:/refund/_paymentRequestForm?vhid=" + vhid;
                        //return "redirect:/refund/_paymentRequestForm";
                        }
                       String approverName =null;
                       if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                        {        		
                        approverName =populateEmpName();        		
                        }
                       else
                        approverName = String.valueOf(request.getParameter("approverName"));
                        final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                                     savedEgBillregister.getState(), savedEgBillregister.getId(), approvalPosition,approverName);
                     		              
                        return "redirect:/refund/successRefund?approverDetails=" + approverDetails + "&billNumber="
                        		                    + savedEgBillregister.getBillnumber()+"&billId="
                        		                            + savedEgBillregister.getId();
                       }
                 
	}else {
		StringBuilder message = new StringBuilder();
		for(int i=0;i<egBillregister.getBillPayeeDetailsNotLink().size();i++) {
			
			message.append("account detial key "+egBillregister.getBillPayeeDetailsNotLink().get(i).getAccountDetailTypeId()+" not mapped with  glcodeid "+egBillregister.getBillPayeeDetailsNotLink().get(i).getEgBilldetailsId().getGlcodeid()+"\n");
			
		}
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/refund/_paymentRequestForm?vhid=" + vhid;
	}
                 
            }
    
    
    @RequestMapping(value = "/successRefund", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("billNumber") final String billNumber, final Model model,
                                  final HttpServletRequest request, @RequestParam("billId") final String billId) {
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
//                currentUserDesgn = keyNameArray[2];
            } else {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
//                currentUserDesgn = keyNameArray[2];
//                nextDesign = keyNameArray[3];
            }
//        approverName= keyNameArray[0];
        if (id != null)
            model.addAttribute("approverName", approverName);
//        model.addAttribute("currentUserDesgn", currentUserDesgn);
//        model.addAttribute("nextDesign", nextDesign);
        model.addAttribute("billd", billId);
        model.addAttribute("type", "refund");
        final EgBillregister expenseBill = refundBillService.getByBillnumber(billNumber);

        final String message = getMessageByStatus(expenseBill, approverName, nextDesign);

        model.addAttribute("message", message);
        System.out.println("message========="+ message);
        return "expensebill-success";
    }
    
    
    private String getMessageByStatus(final EgBillregister expenseBill, final String approverName, final String nextDesign) {
        String message = "";
          System.out.println("expenseBill.getStatus().getCode()================== "+expenseBill.getStatus().getCode());
          
        if (FinancialConstants.CONTINGENCYBILL_CREATED_STATUS.equals(expenseBill.getStatus().getCode())
                                     || FinancialConstants.CONTINGENCYBILL_PENDING_FINANCE.equals(expenseBill.getStatus().getCode())) {
            if (org.apache.commons.lang.StringUtils
                    .isNotBlank(expenseBill.getEgBillregistermis().getBudgetaryAppnumber())
                    && !BudgetControlType.BudgetCheckOption.NONE.toString()
                    .equalsIgnoreCase(budgetControlTypeService.getConfigValue())) {
                                message = messageSource.getMessage("msg.expense.refund.bill.create.success.with.budgetappropriation",
                                                    new String[]{expenseBill.getBillnumber(), approverName, nextDesign,
                                expenseBill.getEgBillregistermis().getBudgetaryAppnumber()},
                        null);
               }
            else if(expenseBill.getState().getValue()!=null && expenseBill.getState().getValue().equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
                System.out.println(expenseBill.getState().getValue());
            	message = messageSource.getMessage("msg.expense.refund.bill.saveasdraft.success",
                            new String[]{expenseBill.getBillnumber()}, null);
            }
            else {
                message = messageSource.getMessage("msg.expense.refund.bill.create.success",
                        new String[]{expenseBill.getBillnumber(), approverName, nextDesign}, null);
            }

        } else if (FinancialConstants.CONTINGENCYBILL_PENDING_AUDIT.equals(expenseBill.getStatus().getCode())) {
            message = messageSource.getMessage("msg.expense.refund.bill.approved.success",
                    new String[]{expenseBill.getBillnumber()}, null);
        }
        else if (FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS.equals(expenseBill.getStatus().getCode()))
        {    
        	System.out.println("approved message going to set");
        	message = messageSource.getMessage("msg.expense.refund.bill.approved.success",
                    new String[]{expenseBill.getBillnumber()}, null);
        }
        else if (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(expenseBill.getState().getValue())) {
            message = messageSource.getMessage("msg.expense.refund.bill.reject",
                    new String[]{expenseBill.getBillnumber(), approverName, nextDesign}, null);
        }
        else if (FinancialConstants.WORKFLOW_STATE_CANCELLED.equals(expenseBill.getStatus().getCode())) {
        	expenseBill.setState(null);
            refundBillService .saveEgBillregister_afterStateNull(expenseBill);
            message = messageSource.getMessage("msg.expense.refund.bill.cancel",
                    new String[]{expenseBill.getBillnumber()}, null);
        }
        else if ("Pending for Cancellation".equals(expenseBill.getStatus().getCode())) {
        	message = messageSource.getMessage("msg.expense.refund.bill.cancel.success",
                    new String[]{expenseBill.getBillnumber(), approverName, nextDesign}, null);
        }
        return message;
    }
    
    
	
	@RequestMapping(value = "/refundBill", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public RefundResponse refundBill(@RequestBody RefundRequest refundRequest, final Model model,
			final BindingResult resultBinder, final HttpServletRequest request) throws IOException {

		//ApplicationThreadLocals.setUserId(refundRequest.getRequestInfo().getUserInfo().getId());
		//ApplicationThreadLocals.setUserToken(refundRequest.getRequestInfo().getAuthToken());
		//ApplicationThreadLocals.setTenantID(refundRequest.getTenantId());
		//LOGGER.info("RefundBill is creating with user ::" + ApplicationThreadLocals.getUserId());
		Long approver=0l;
		 List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","RefundApprover");
         for(AppConfigValues value:appConfigValuesList)
         {
      	   	approver = Long.valueOf(value.getValue());
         }
		List<EgBilldetails> billDetails = new ArrayList<EgBilldetails>();
		List<EgBillPayeedetails> billPayeeDetails = new ArrayList<EgBillPayeedetails>();
		VoucherDetailsResponse voucherDetails = new VoucherDetailsResponse();

		final List<String> entityNames = new ArrayList<>();
		List<EntityType> entitiesList = new ArrayList<>();

		final Vouchermis vouchermis = vouchermisService
				.getVouchermisByReceiptNumber(refundRequest.getReceipt().getReceiptNumber());

		final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();

		PreApprovedVoucher subledger = null;
		CChartOfAccounts coa = null;
		voucherDetails.setId(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		CVoucherHeader voucherHeader = paymentRefundUtils
				.getVoucherHeader(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
		BigDecimal dbAmount = BigDecimal.ZERO;
		BigDecimal crAmount = BigDecimal.ZERO;
		List<Accountdetailtype> accountdetailtypelist = accountdetailtypeService.findAll();
		Accountdetailtype accountdetailtype = (Accountdetailtype) accountdetailtypeService.findAll().stream()
				.filter(e -> e.getName().equalsIgnoreCase("OtherParty")).findFirst().orElse(null);
		
		EgBillregister egBillregister = new EgBillregister();

		if (null != egBillregister && null != egBillregister.getEgBillregistermis()) {
			prepareWorkflow(model, egBillregister, new WorkflowContainer());
		}

		egBillregister.setRefundable("Y");
		egBillregister.setIsCitizenRefund(refundRequest.getReceipt().getIsCitizenRefund());
		egBillregister.setBilldate(new Date());
		BigDecimal totalCrAmt = BigDecimal.ZERO;
		totalCrAmt = new BigDecimal(0); 
	 	egBillregister.setBillamount(totalCrAmt);
	 	
	 	final Long OtherCount=(Long) persistenceService.find("select count(*) from OtherParty where bankaccount='"+refundRequest.getReceipt().getBankAccount()+"'");	
		if(OtherCount==0)
		{
			System.out.println("inside no otherparty found"); 
		  	System.out.println(refundRequest.getReceipt().getBankName());
			System.out.println(refundRequest.getReceipt().getCitizenName());
			System.out.println(refundRequest.getReceipt().getBankAccount());
			try {
			//Bank bank=createBankService.getByName(refundRequest.getReceipt().getBankName());
				int bankid=(int)persistenceService.find("select id from Bank where name='"+refundRequest.getReceipt().getBankName()+"'");
				Bank bank=createBankService.getById(bankid);
				OtherParty otherParty=new OtherParty();
				otherParty.setBank(bank);
				otherParty.getBank().setId(bankid);
				otherParty.setCorrespondenceAddress(refundRequest.getReceipt().getCorrespondingAddress());
				otherParty.setBankAccount(refundRequest.getReceipt().getBankAccount());
				otherParty.setIfscCode(refundRequest.getReceipt().getIfscCode());
				otherParty.setCode(refundRequest.getReceipt().getCitizenName());
				otherPartyService.create1(otherParty,approver);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		List<Object[]> list= null;
		int otherpartyid=0;
		final StringBuffer query = new StringBuffer(500);
    	SQLQuery queryMain =  null;
    	query
        .append("select id from OtherParty where code='"+refundRequest.getReceipt().getCitizenName()+"'");
    	LOGGER.info("Query 1 :: "+query.toString());
    	queryMain=this.persistenceService.getSession().createSQLQuery(query.toString());
    	list = queryMain.list();
    	if(list!=null)
    	{	
    		for (final Object[] object : list)
    		{
    			otherpartyid=Integer.parseInt(object[1].toString());
    		}
    	}
		final List<CGeneralLedger> gllist = paymentRefundUtils
				.getAccountDetails(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		
		List<GeneralLedger> ledger=new ArrayList();
		List<Object[]> list1= null;
		final StringBuffer query1 = new StringBuffer(500);
    	queryMain =  null;
    	query1
        .append("select g.id, g.glcodeid,g.glcode,g.debitamount,g.creditamount,g.functionid from generalledger g,vouchermis v " + 
        		" where g.voucherheaderid = v.voucherheaderid and v.reciept_number ='"+refundRequest.getReceipt().getReceiptNumber()+"'");
    	LOGGER.info("Query 1 :: "+query1.toString());
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list1 = queryMain.list();
    	System.out.println(":::list size::::: "+list1.size());	    	
    	if(list1!=null)
    	{	
    		for (final Object[] object : list1)
    		{
    			EgBilldetails billdetail = new EgBilldetails();
    			EgBillPayeedetails billpayeedetail = new EgBillPayeedetails();
				CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
				coa = paymentRefundUtils.getChartOfAccount(object[1].toString());//glcode
				billdetail.setGlcodeid(new BigDecimal(coa.getId()));//glcodeid
				billdetail.setFunctionid(new BigDecimal(object[5].toString()));//functionid
				chartOfAccounts.setName(coa.getName());//glcodename
				chartOfAccounts.setGlcode(coa.getGlcode());////glcode
				billdetail.setChartOfAccounts(chartOfAccounts);
				billdetail.setDebitamount(new BigDecimal(object[3].toString()));//debit
				billdetail.setCreditamount(new BigDecimal(object[4].toString()));//credit
				billDetails.add(billdetail);
				billpayeedetail.setAccountDetailKeyId(accountdetailtype.getId());
				billpayeedetail.setAccountDetailTypeId(otherpartyid);
				billpayeedetail.setCreditAmount(new BigDecimal(0));
				billPayeeDetails.add(billpayeedetail);
    		}
    	}
    	egBillregister.setBillDetails(billDetails);
    	egBillregister.setBillPayeedetails(billPayeeDetails);
		egBillregister.setBillamount(totalCrAmt);
		
		try {
		EgBillSubType egbillSubtype = (EgBillSubType) getBillSubTypes().stream()
				.filter(e -> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
		BigDecimal bg1 = egBillregister.getBillDetails().get(0).getFunctionid();
		CFunction function = paymentRefundUtils.getFunction(bg1.longValue());
		EgBillregistermis egbillregistermis = egBillregister.getEgBillregistermis();
		egbillregistermis.setFunction(function);
		egbillregistermis.setDepartmentcode(egBillregister.getNarration());
		egbillregistermis.setNarration(vouchermis.getDepartmentcode());
		egbillregistermis.setEgBillSubType(egbillSubtype);
		egBillregister.setEgBillregistermis(egbillregistermis);

		ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);

		String billNumber = v.getNextNumber(egBillregister);
		System.out.println(billNumber);
		egBillregister.setBillnumber(billNumber);

		egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());

		///save
		EgBillregister savedEgBillregister;
             try {
           	  System.out.println("From egbillregister Save method calling");
                  savedEgBillregister = refundBillService.create(egBillregister, approver, null, null, 
                  "SaveAsDraft",null,vouchermis.getVoucherheaderid().getId().toString());
                  
                  
                } catch (ValidationException e) {
               	 System.out.println("From Exception saving time");
               	 e.printStackTrace();
                }
		//
		
			final String table = accountdetailtype.getFullQualifiedName();
			final Class<?> service = Class.forName(table);
			String simpleName = service.getSimpleName();
			simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

			final EntityTypeService entityService = (EntityTypeService) applicationContext.getBean(simpleName);
			entitiesList = (List<EntityType>) entityService.filterActiveEntities(accountdetailtype.getName(), 20,
					accountdetailtype.getId());
		} catch (final Exception e) {
			e.printStackTrace();
			entitiesList = new ArrayList<>();
		}
		for (final EntityType entity : entitiesList) {
			entityNames.add(entity.getCode() + " - " + entity.getName() + "~" + entity.getEntityId());
		}
		// System.out.println(name); //System.out.println(accountDetailType);
		for (String responcename : entityNames) {
			System.out.println(responcename);
		}
		RefundResponse refundresponse=new RefundResponse();
		refundresponse.setResponseStatus("Created");
		//refundresponse.setResponseInfo(responseInfo);
		return new RefundResponse();
	}
	 
    private Long populatePosition() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		pos=employs.get(0).getAssignments().get(0).getPosition();
    	}
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
    
    
    private void refundvalidateLedgerAndSubledger(final EgBillregister egBillregister, final BindingResult resultBinder) {
        BigDecimal totalDrAmt = BigDecimal.ZERO;
        BigDecimal totalCrAmt = BigDecimal.ZERO;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
            if (details.getDebitamount() != null)
                totalDrAmt = totalDrAmt.add(details.getDebitamount());
            if (details.getCreditamount() != null)
                totalCrAmt = totalCrAmt.add(details.getCreditamount());
            if (details.getGlcodeid() == null)
                resultBinder.reject("msg.expense.bill.accdetail.accmissing", new String[] {}, null);

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
       // if (totalDrAmt.compareTo(totalCrAmt) != 0)
            //resultBinder.reject("msg.expense.bill.accdetail.drcrmatch", new String[] {}, null);
        refundvalidateSubledgerDetails(egBillregister, resultBinder);
    }

    private void refundvalidateSubledgerDetails(final EgBillregister egBillregister, final BindingResult resultBinder) {
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

           // if (detailAmt.compareTo(payeeDetailAmt) != 0 && !details.getEgBillPaydetailes().isEmpty())
               // resultBinder.reject("msg.expense.bill.subledger.amtnotmatchinng",
                        //new String[] { details.getChartOfAccounts().getGlcode() }, null);
        }
    }
    
    
    
	@RequestMapping(value="/saveotherParty", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
	@ResponseBody
    public OtherParty submittedFromData(@RequestBody OtherParty otherParty, HttpServletRequest request) {
		try {
		System.out.println(otherParty.getBankname());
		System.out.println(otherParty.getName());
		System.out.println(otherParty.getBankAccount());
		//System.out.println(request.getParameter("bank"));
		OtherParty other_party=otherPartyService.getByNameOrAccount(otherParty.getName(),otherParty.getBankAccount());
		if(other_party==null) {
			Bank bank=createBankService.getById(otherParty.getBank().getId());
			otherParty.setBank(bank);
			otherParty.setCode(otherParty.getName());
			other_party=otherPartyService.create(otherParty);
		}
		saveAccountDetailKey(otherParty);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return otherParty;
	}	
    
	@Transactional
    public void saveAccountDetailKey(OtherParty otherParty) {

        Accountdetailkey accountdetailkey = new Accountdetailkey();
        accountdetailkey.setDetailkey(otherParty.getId().intValue());
        accountdetailkey.setDetailname(otherParty.getName());
        accountdetailkey.setAccountdetailtype(accountdetailtypeService.findByName(otherParty.getClass().getSimpleName()));
        accountdetailkey.setGroupid(1);
        accountDetailKeyService.create(accountdetailkey);
    }
	
	 public void setupDropDownForSL(final List<Long> glcodeIdList,final Model model) {
	        List<CChartOfAccounts> glcodeList = null;
	        if (!glcodeIdList.isEmpty())
	        {
	        final Query glcodeListQuery = persistenceService.getSession().createQuery(
	                " from CChartOfAccounts where id in (select glCodeId from CChartOfAccountDetail) and id in  ( :IDS )");
	        glcodeListQuery.setParameterList("IDS", glcodeIdList);
	        glcodeList = glcodeListQuery.list();
	        }
	        if (glcodeIdList.isEmpty())
	        	model.addAttribute("glcodeList", Collections.EMPTY_LIST);
	            //dropdownData.put("glcodeList", Collections.EMPTY_LIST);
	        else
	        	model.addAttribute("glcodeList", glcodeList);
	            //dropdownData.put("glcodeList", glcodeList);
		}
    	
	    public void setupDropDownForSLDetailtype(final List<Accountdetailtype> detailtypeIdList,final Model model) {
	    	model.addAttribute("detailTypeList", detailtypeIdList);
	    	//dropdownData.put("detailTypeList", detailtypeIdList);
    }
	    
	    
		@RequestMapping(value = "/view/{billId}", method = RequestMethod.GET)
	    public String view(final Model model, @PathVariable String billId,
	            final HttpServletRequest request) throws ApplicationException {
	        if (billId.contains("showMode")) {
	            String[] billIds = billId.split("\\&");
	            billId = billIds[0];
	        }
	        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
	        final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
	        egBillregister.setDocumentDetail(documents);
	        String departmentCode = this.getDepartmentName(egBillregister.getEgBillregistermis().getDepartmentcode());
	        egBillregister.getEgBillregistermis().setDepartmentName(departmentCode);
	        setDropDownValues(model);
	        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
	        model.addAttribute("mode", "readOnly");
	        model.addAttribute("type", "refundbill");
	        model.addAttribute(BILL_TYPES, BillType.values());
	        prepareBillDetailsForView(egBillregister);
	        prepareCheckList(egBillregister);
	        
	        final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService.getNetPayableCodesByAccountDetailType(0);
	        
	        for (final EgBilldetails details : egBillregister.getBillDetails())
	            if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
	                    && expensePayableAccountList.contains(details.getChartOfAccounts()))
	                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
	        model.addAttribute(EG_BILLREGISTER, egBillregister);
	        return EXPENSEBILL_VIEW;
	    }
		
		  private String getDepartmentName(String departmentCode) {

		        List<Department> deptlist = this.masterDataCache.get("egi-department");
		        String departmentName = null;

		        if (null != deptlist && !deptlist.isEmpty()) {

		            List<Department> dept = deptlist.stream()
		                    .filter(department -> departmentCode.equalsIgnoreCase(department.getCode()))
		                    .collect(Collectors.toList());
		            if (null != dept && dept.size() > 0)
		                departmentName = dept.get(0).getName();
		        }

		        if (null == departmentName) {
		            Department dept = this.microServiceUtil.getDepartmentByCode(departmentCode);
		            if (null != dept)
		                departmentName = dept.getName();
		        }

		        return departmentName;
		    }
		  
		    public void validateSubledgeDetails(EgBillregister egBillregister) {
		        final List<EgBillPayeedetails> payeeDetails = new ArrayList<>();
		        final List<EgBillPayeedetails> payeeDetailsNotMatched = new ArrayList<>();
		        for (final EgBillPayeedetails payeeDetail : egBillregister.getBillPayeedetails()) {
		            CChartOfAccountDetail coaDetail = chartOfAccountDetailService
		                    .getByGlcodeIdAndDetailTypeId(payeeDetail.getEgBilldetailsId().getGlcodeid().longValue(),
		                            payeeDetail.getAccountDetailTypeId().intValue());
		            if (coaDetail != null) {
		                payeeDetails.add(payeeDetail);
		        }else {
		        	payeeDetailsNotMatched.add(payeeDetail);
		        }
		        }
		        egBillregister.getBillPayeedetails().clear();
		        egBillregister.setBillPayeedetails(payeeDetails);
		        
		        egBillregister.getBillPayeeDetailsNotLink().clear();
		        egBillregister.setBillPayeeDetailsNotLink(payeeDetailsNotMatched);
		    }
		    
		    
		    @RequestMapping(value = "/newform", method = RequestMethod.POST)
		    public String showNewForm(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,HttpServletRequest request) {
		        LOGGER.info("New expensebill creation request created");
		        Cookie[] cookies = request.getCookies();
		       List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
		    	
		    	if(null!=cookies && cookies.length>0)
		    	{
		    	   for(Cookie ck:cookies) {
		    		   System.out.println("Name:"+ck.getName()+" value"+ck.getValue());
		    	   }
		    	}
		        setDropDownValues(model);
		        
		        
		        
		        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
		        prepareWorkflow(model, egBillregister, new WorkflowContainer());
		       model.addAttribute("validActionList", validActions);
		       model.addAttribute(BILL_TYPES, BillType.values());
		        prepareValidActionListByCutOffDate(model);
		        if(isBillDateDefaultValue){
		            egBillregister.setBilldate(new Date());            
		        }
//		        User createdBy = new User();
//		        createdBy.setId(ApplicationThreadLocals.getUserId());
//		        egBillregister.setCreatedBy(createdBy);
		        return REFUNDFOROLDVOUCHER_FORM;
		    }
		    
		    @RequestMapping(value = "/_paymentRequestblankvoucherForm", method = {RequestMethod.GET, RequestMethod.POST})
			public String paymentRequestFormBlankVoucher(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,@ModelAttribute("message") String message) {
				setDropDownValues(model);
		    	List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
		        prepareWorkflow(model, egBillregister, new WorkflowContainer());       
		        prepareValidActionListByCutOffDate(model);
		        if(isBillDateDefaultValue){
		            egBillregister.setBilldate(new Date());            
		        }
		        List<AppConfigValues> appConfigValuesList1 =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
						"RefundBillType");
		    	List<String> billNames=new ArrayList<String>();
		    	for(AppConfigValues row:appConfigValuesList1)
		    	{
		    		billNames.add(row.getValue());
		    	}
		    	List<EgBillSubType> billSubtypes=new ArrayList<EgBillSubType>();
		    	for(EgBillSubType row:egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT))
		    	{
		    		if(billNames.contains(row.getName()))
		    		{
		    			billSubtypes.add(row);
		    		}
		    	}
		    	model.addAttribute("billSubTypes", billSubtypes);
		        model.addAttribute("validActionList", validActions);
			    model.addAttribute(BILL_TYPES, BillType.values());	        
		        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
		    	
		        if(message!=null) {
					model.addAttribute("glcodedetailIdmsg", message);
				}else {
					message="";
				}
		    		populateDropDownValues(model);
					  model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
					  prepareWorkflow(model, egBillregister, new WorkflowContainer());
					  model.addAttribute("validActionList", validActions);
					  model.addAttribute(BILL_TYPES, BillType.values());
					  //model.addAttribute("subLedgerTypes", accountdetailtypeService.findAll());
					  model.addAttribute("subLedgerTypes", paymentRefundUtils.getAllActiveAccounts());//added Abhishek on 02Dec2021
					  prepareValidActionListByCutOffDate(model);
				
				return "ol-payRefund-request-form";
}
			


@RequestMapping(value = "/refundCreateBlank", method = RequestMethod.POST)
public String createRefundBYBlank(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
     final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction,RedirectAttributes redirectAttributes)
     throws IOException {
        LOGGER.info("RefundBill is creating with user ::"+ApplicationThreadLocals.getUserId());
        String vhid=request.getParameter("vhid");
        System.out.println(vhid);
       System.out.println("billtype----> "+egBillregister.getEgBillregistermis().getSubType());
       System.out.println("function---> "+egBillregister.getEgBillregistermis().getFunction().getId());
       System.out.println("scheme----> "+egBillregister.getEgBillregistermis().getScheme());
       System.out.println("schemeId----> "+egBillregister.getEgBillregistermis().getSchemeId());
       System.out.println("subscheme----> "+egBillregister.getEgBillregistermis().getSubScheme());
       System.out.println("subschemeId----> "+egBillregister.getEgBillregistermis().getSubSchemeId());
        
        
       egBillregister.setRefundable("Y"); 
       egBillregister.setBilldate(new Date());
       BigDecimal totalCrAmt = BigDecimal.ZERO;
       
       //egBillregister.getState().setNatureOfTask("Refund Bill");
	
	 for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
		 
		 totalCrAmt=totalCrAmt.add(checknull(egbilldetail.getDebitamount()));
		 }
	 //BigDecimal bg1 = egBillregister.getBillDetails().get(0).getFunctionid();
	 
	 Long bg1 = egBillregister.getEgBillregistermis().getFunction().getId();
	 
	 List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
       for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
		  
    	   if(checknullBigDecimal(egbilldetail.getDebitamount())==true || checknullBigDecimal(egbilldetail.getCreditamount())==true) {
    		   egbilldetailCusList.add(egbilldetail);
    	   }
		 }
	  
	  egBillregister.setBillamount(totalCrAmt);
	  egBillregister.setBillDetails(egbilldetailCusList);
	
	 System.out.println(totalCrAmt);

	 EgBillSubType egbillSubtype=null;
	  if (egBillregister.getEgBillregistermis().getSubType() != null)
	  {
		  egbillSubtype=egBillSubTypeService.getById(Long.valueOf(egBillregister.getEgBillregistermis().getSubType())); 
	  }
     egBillregister.getEgBillregistermis().setEgBillSubType(egbillSubtype);  
     egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());
     ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);
     
	
	 
	  CFunction function= paymentRefundUtils.getFunction(bg1.longValue());
	  egBillregister.getEgBillregistermis().setFunction(function);
	  
    String billNumber = v.getNextNumber(egBillregister);
    System.out.println(billNumber);
    
    
    	egBillregister.setBillnumber(billNumber);
    
    if (StringUtils.isEmpty(egBillregister.getExpendituretype()))
    egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);

    String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
    List<DocumentUpload> list = new ArrayList<>();
    UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
    String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
    if(uploadedFiles!=null)
    for (int i = 0; i < uploadedFiles.length; i++) {

    Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
    byte[] fileBytes = Files.readAllBytes(path);
    ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
    DocumentUpload upload = new DocumentUpload();
    upload.setInputStream(bios);
    upload.setFileName(fileName[i]);
    upload.setContentType(contentType[i]);
    list.add(upload);
    }
    if(egBillregister.getEgBilldetailes()!=null)
    {
	    egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
	    if(!egBillregister.getDebitDetails().isEmpty()&&!egBillregister.getCreditDetails().isEmpty())
	    {
	    	populateBillDetails(egBillregister);
	    }
	    if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
		{ 
	    	populateBillDetails(egBillregister);
	    }
	    validateBillNumber(egBillregister, resultBinder);
    }
    
    if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
	{ 
    	  populateEgBillregistermisDetails(egBillregister);
	}
    
	  if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
		  refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
		  validateSubledgeDetails(egBillregister);
	  }
	  if(!egBillregister.getDebitDetails().isEmpty()&&!egBillregister.getCreditDetails().isEmpty())
	  {
		  refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
		  validateSubledgeDetails(egBillregister);
	  }
	  
	    
	  System.out.println("------------------------------"+egBillregister.getBillPayeeDetailsNotLink().isEmpty());	  
if(egBillregister.getBillPayeeDetailsNotLink().isEmpty()) {

    if (resultBinder.hasErrors()) {
    	System.out.println("from ResultBinder Error");
    	for (Object object : resultBinder.getAllErrors()) {
    	    if(object instanceof FieldError) {
    	        FieldError fieldError = (FieldError) object;

    	        System.out.println(fieldError.getCode());
    	    }

    	    if(object instanceof ObjectError) {
    	        ObjectError objectError = (ObjectError) object;

    	        System.out.println(objectError.getCode());
    	    }
    	}
    	return "redirect:/refund/_paymentRequestblankvoucherForm";
     //return "redirect:/refund/_paymentRequestForm";
      } else {
            Long approvalPosition = 0l;
            String approvalComment = "";
            String approvalDesignation = "";
            if (request.getParameter("approvalComent") != null)
             approvalComment = request.getParameter("approvalComent");
            if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
              {
               	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                  	{            		
                     approvalPosition =populatePosition();            		
                    }
                  else
                     approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
                }
             else {
                if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                 {            		
                  approvalPosition =populatePosition();            		
                 }		            	
              }
             if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
                 approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
                    		            
             EgBillregister savedEgBillregister;
             egBillregister.setDocumentDetail(list);
                  try {
                	  System.out.println("From egbillregister Save method calling");
                       savedEgBillregister = refundBillService.createByBlankVoucher(egBillregister, approvalPosition, approvalComment, null, 
                       workFlowAction,approvalDesignation,vhid);
                       
                       
                     } catch (ValidationException e) {
                    	 System.out.println("From Exception saving time");
                    	 e.printStackTrace();
                    
                    	 return "redirect:/refund/_paymentRequestblankvoucherForm";
                    //return "redirect:/refund/_paymentRequestForm";
                    }
                   String approverName =null;
                   if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                    {        		
                    approverName =populateEmpName();        		
                    }
                   else
                    approverName = String.valueOf(request.getParameter("approverName"));
                    final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                                 savedEgBillregister.getState(), savedEgBillregister.getId(), approvalPosition,approverName);
                 		              
                    return "redirect:/refund/successRefund?approverDetails=" + approverDetails + "&billNumber="
                    		                    + savedEgBillregister.getBillnumber()+"&billId="
                    		                            + savedEgBillregister.getId();
                   }
    
			}else {
				StringBuilder message = new StringBuilder();
				for(int i=0;i<egBillregister.getBillPayeeDetailsNotLink().size();i++) {
					
					message.append("account detial key "+egBillregister.getBillPayeeDetailsNotLink().get(i).getAccountDetailTypeId()+" not mapped with  glcodeid "+egBillregister.getBillPayeeDetailsNotLink().get(i).getEgBilldetailsId().getGlcodeid()+"\n");
					
				}
				redirectAttributes.addFlashAttribute("message",message);
				return "redirect:/refund/_paymentRequestblankvoucherForm";
			}
             
        }
@RequestMapping(value = "/update/{billId}", method = {RequestMethod.GET,RequestMethod.POST})
public String update(@ModelAttribute(EG_BILLREGISTER)  EgBillregister egBillregister,@PathVariable final String billId,
        final BindingResult resultBinder, final RedirectAttributes redirectAttributes, final Model model,
        final HttpServletRequest request, @RequestParam final String workFlowAction) throws IOException {
	
	System.out.println("In update controller");
	System.out.println("from update controller"+  billId);
	//egBillregister.setId(Long.parseLong(billId));
	egBillregister = expenseBillService.getById(Long.parseLong(billId));
    String mode = "";
    EgBillregister updatedEgBillregister = null;
    if (request.getParameter("mode") != null)
        mode = request.getParameter("mode");
    
    
    LOGGER.info("RefundBill is creating with user ::"+ApplicationThreadLocals.getUserId());
    
   System.out.println("billtype----> "+egBillregister.getEgBillregistermis().getSubType());
   System.out.println("function---> "+egBillregister.getEgBillregistermis().getFunction().getId());
   System.out.println("scheme----> "+egBillregister.getEgBillregistermis().getScheme());
   System.out.println("schemeId----> "+egBillregister.getEgBillregistermis().getSchemeId());
   System.out.println("subscheme----> "+egBillregister.getEgBillregistermis().getSubScheme());
   System.out.println("subschemeId----> "+egBillregister.getEgBillregistermis().getSubSchemeId());
   
		/*
		 * egBillregister.setRefundable("Y"); egBillregister.setBilldate(new Date());
		 * BigDecimal totalCrAmt = BigDecimal.ZERO;
		 * 
		 * for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) {
		 * totalCrAmt=totalCrAmt.add(checknull(egbilldetail.getDebitamount())); }
		 * 
		 * Long bg1 = egBillregister.getEgBillregistermis().getFunction().getId();
		 * 
		 * List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
		 * for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) {
		 * if(checknullBigDecimal(egbilldetail.getDebitamount())==true ||
		 * checknullBigDecimal(egbilldetail.getCreditamount())==true) {
		 * egbilldetailCusList.add(egbilldetail); } }
		 * 
		 * egBillregister.setBillamount(totalCrAmt);
		 * egBillregister.setBillDetails(egbilldetailCusList);
		 * System.out.println(totalCrAmt);
		 * 
		 * EgBillSubType egbillSubtype=null; if
		 * (egBillregister.getEgBillregistermis().getSubType() != null) {
		 * egbillSubtype=egBillSubTypeService.getById(Long.valueOf(egBillregister.
		 * getEgBillregistermis().getSubType())); }
		 * egBillregister.getEgBillregistermis().setEgBillSubType(egbillSubtype);
		 * egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());
		 * 
		 * CFunction function= paymentRefundUtils.getFunction(bg1.longValue());
		 * egBillregister.getEgBillregistermis().setFunction(function);
		 * 
		 * if (StringUtils.isEmpty(egBillregister.getExpendituretype()))
		 * egBillregister.setExpendituretype(FinancialConstants.
		 * STANDARD_EXPENDITURETYPE_REFUND);
		 */
   String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
   List<DocumentUpload> list = new ArrayList<>();
   UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
   String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
   if(uploadedFiles!=null)
   {
	   for (int i = 0; i < uploadedFiles.length; i++) {
		   Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
		   byte[] fileBytes = Files.readAllBytes(path);
		   ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
		   DocumentUpload upload = new DocumentUpload();
		   upload.setInputStream(bios);
		   upload.setFileName(fileName[i]);
		   upload.setContentType(contentType[i]);
		   list.add(upload);
	   }
   }
   
	if(egBillregister.getEgBilldetailes()!=null)
	{
	    egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
	    if(!egBillregister.getDebitDetails().isEmpty()&&!egBillregister.getCreditDetails().isEmpty())
	    {
	    	populateBillDetails(egBillregister);
	    }
	    if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
		{ 
	    	populateBillDetails(egBillregister);
	    }
	    validateBillNumber(egBillregister, resultBinder);
	}
	
	if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
	{ 
		  populateEgBillregistermisDetails(egBillregister);
	}
	if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
		refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
		validateSubledgeDetails(egBillregister);
	}
	if(!egBillregister.getDebitDetails().isEmpty()&&!egBillregister.getCreditDetails().isEmpty())
	{
		refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
		validateSubledgeDetails(egBillregister);
	}
    
  
	  /*if (resultBinder.hasErrors()) {
		  System.out.println("from ResultBinder Error");
		  for (Object object : resultBinder.getAllErrors()) {
			  if(object instanceof FieldError) {
				  FieldError fieldError = (FieldError) object;
				  System.out.println(fieldError.getCode());
			  }
			  if(object instanceof ObjectError) {
				  ObjectError objectError = (ObjectError) object;
				  System.out.println(objectError.getCode());
			  }
		  }
		  return "redirect:/refund/_paymentRequestblankvoucherForm";
		  //return "redirect:/refund/_paymentRequestForm";
	  } else {*/
		  Long approvalPosition = 0l;
		  String approvalComment = "";
		  String approvalDesignation = "";
		  if (request.getParameter("approvalComent") != null)
			  approvalComment = request.getParameter("approvalComent");
		  if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
          {
			  if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
			  {            		
				  approvalPosition =populatePosition();            		
			  }
              else
                 approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
          }
		  else {
			  if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
			  {            		
				  approvalPosition =populatePosition();            		
			  }		            	
          }
		  if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
             approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
                		            
		  try {
			  System.out.println("Refund update method calling");
			  updatedEgBillregister = refundBillService.update(egBillregister, approvalPosition, approvalComment, null,
                      workFlowAction, mode, approvalDesignation);
		  } catch (ValidationException e) {
			  System.out.println("From Exception saving time");
			  e.printStackTrace();
			  return "redirect:/refund/_paymentRequestblankvoucherForm";
			  //return "redirect:/refund/_paymentRequestForm";
		  }
		  String approverName =null;
		  if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
		  {        		
			  approverName =populateEmpName();        		
		  }
		  else
			  approverName = String.valueOf(request.getParameter("approverName"));
		  final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
				  updatedEgBillregister.getState(), updatedEgBillregister.getId(), approvalPosition,approverName);
             		              
		  return "redirect:/refund/successRefund?approverDetails=" + approverDetails + "&billNumber="
		  			+ updatedEgBillregister.getBillnumber()+"&billId="+ updatedEgBillregister.getId();
	  //}
    }
}