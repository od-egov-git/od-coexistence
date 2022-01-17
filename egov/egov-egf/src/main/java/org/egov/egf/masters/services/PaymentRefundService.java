package org.egov.egf.masters.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bank;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.AccountDetailKeyService;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FundService;
import org.egov.egf.autonumber.ExpenseBillNumberGenerator;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.budget.service.BudgetControlTypeService;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.egf.commons.bank.repository.BankRepository;
import org.egov.egf.commons.bank.service.CreateBankService;
import org.egov.egf.contract.model.MisReceiptsDetailsResponse;
import org.egov.egf.contract.model.RefundLedgerPojo;
import org.egov.egf.contract.model.RefundReceiptRequest;
import org.egov.egf.contract.model.RefundReceiptRest;
import org.egov.egf.expensebill.repository.ExpenseBillRepository;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.expensebill.service.RefundBillService;
import org.egov.egf.expensebill.service.VouchermisService;
import org.egov.egf.masters.repository.OtherpartyRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigService;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.masters.OtherParty;
import org.egov.pims.commons.Position;
import org.egov.services.masters.BankService;
import org.egov.services.masters.SchemeService;
import org.egov.services.masters.SubSchemeService;
import org.egov.utils.FinancialConstants;
import org.egov.utils.PaymentRefundUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
public class PaymentRefundService {

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
	BankService bankService;

	@Autowired
	BankRepository bankRepository;

	@Autowired
	ChartOfAccountsService chartOfAccountsService;
	
	@Autowired
	FunctionService functionService;
	
	@Autowired
	AccountDetailKeyService accountDetailKeyService;
	
	@Autowired
	AppConfigService appConfigService;
	
	 @Autowired
	 private SchemeService schemeService;
	 @Autowired
	 private SubSchemeService subSchemeService;
	 
	  @Autowired
	 private FundService fundService;
	  
	  @Autowired
	  UserService userService;
	  
	  @Autowired
	  OtherpartyRepository otherpartyRepo;
	  
	  @Autowired
	  ExpenseBillRepository expenseBillRepository;
	
	
	
	private final String OTHERPARTY = "OtherParty";
	
	 
	
	
	
	 
	@Transactional
	public MisReceiptsDetailsResponse createRefund(RefundReceiptRequest req) {
		System.out.println("Inside Function");
		RefundReceiptRest r= req.getRefundReceiptRest();
		EgBillregister egbill = new EgBillregister();
		Vouchermis vmis = null;
		CVoucherHeader vh = null;
		OtherParty otherparty = null;
		CFunction function = null;
		AppConfig appconfig =new AppConfig();
		Fund fund = null;
		MisReceiptsDetailsResponse res = new MisReceiptsDetailsResponse();
		Accountdetailtype accountdetailtype = null;
		res.setSuccess(false);
		
		System.out.println("Getting Voucher From Receipt");
		 vmis = vouchermisService.getVouchermisByReceiptNumber(r.getReceiptNumber());
		 if (null == vmis) {
				res.setMessage("Invalid Receipt Number No Voucher Found");
				return res;
		 }
		 System.out.println("Voucher");
		 System.out.println("voucher header"+vmis.getVoucherheaderid().getId());
		 
		 System.out.println("Department"+vmis.getDepartmentcode());
		 String searchConfig = "refund_"+vmis.getDepartmentcode();
		 appconfig =null;
		 appconfig = appConfigService.getAppConfigByKeyName(searchConfig);
		  if (null == appconfig) {
				res.setMessage("No Data Found In Appconfig For the Deapartment---> "+vmis.getDepartmentcode());
				return res;
		 }
		  
		  //Checvk Bank For the user Specified
		  
		  otherparty = otherPartyService.getByBankAccount(r.getBankAccount());
		  if(null!=otherparty) {
			  if(!otherparty.getName().equals(r.getCitizenName())) {
				  res.setMessage("The Bank Account is mapped for another user please use different bank account");
					return res;
				  
			  }
		  } 
		  otherparty = checkforOtherPartyDatandCreate(r,appconfig);
			
			if (null == otherparty) {
				res.setMessage("Failed Creating Other Party Data ... ");
				return res;
			}
		  
		 System.out.println("Voucher Found");
		 vh = vmis.getVoucherheaderid();
		 System.out.println("Vouchee ID"+vh.getId());
		 function =  new CFunction();
		 function = vmis.getFunction();
		 System.out.println("Function ID"+function.getId());
		 fund = new Fund();
		
		egbill = new EgBillregister();
		
		
		System.out.println("Getting Account Detail Type");
		//Getting the TypeId and Account ID
		accountdetailtype  = accountdetailtypeService.findByName(OTHERPARTY);
		if(null==accountdetailtype) {
			res.setMessage("Invalid Account Detail");
			return res;
			
		}
		System.out.println(" Account Detail Type Found");
		//Check if account detail key exist or else create
		Accountdetailkey accountDetailKey = new Accountdetailkey();
		accountDetailKey=null;
		System.out.println(" Finding Account Detail Key ");
		accountDetailKey = accountDetailKeyService.findByDetailNameNew(accountdetailtype.getId(), otherparty.getName());
		if(null==accountDetailKey) {
			System.out.println(" Finding Account Detail Key Failed , Creating New Entry");
			
			accountDetailKey = new Accountdetailkey();
			accountDetailKey.setDetailname(otherparty.getName());
			accountDetailKey.setAccountdetailtype(accountdetailtype);
			accountDetailKey.setDetailkey(Integer.parseInt(otherparty.getId().toString()));
			accountDetailKey.setGroupid(1);
			accountDetailKey =	accountDetailKeyService.create(accountDetailKey);
		}
		
		System.out.println("  Account Detail Key  New Entry Created");
		EgBillPayeedetails pd =null;
		EgBilldetails bd = null;
		CChartOfAccounts glc =null;
		CChartOfAccountDetail coaDetail =null;
		List<EgBilldetails>popluatedBills = new ArrayList<EgBilldetails>();
		List<EgBillPayeedetails> populatedPayee = new ArrayList<EgBillPayeedetails>();
		System.out.println(" Before the Loop");
		for(RefundLedgerPojo l:r.getRefundledgerPojo()) {
			pd = new EgBillPayeedetails();
			bd = new EgBilldetails();
			glc = new CChartOfAccounts();
			coaDetail = new CChartOfAccountDetail();
			System.out.println(" Finding Glcode-->> "+l.getGlcode());
			glc = chartOfAccountsService.getByGlCode(l.getGlcode());
			if(null==glc) {
				System.out.println("Invalid Glcode");
				res.setMessage("Invalid Glode Code found Failed processing");
				return res;
			}
			System.out.println(" Found Glcode "+glc.getGlcode());
			
			System.out.println("Checking for Glocde and Detailtype Mapping ");
			
			//Check for Glocde and accountdetailtype entry in chartofaccountdetails if not available create entry
			 coaDetail=null;
			 coaDetail = chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(
					 glc.getId(),accountdetailtype.getId());
			 if(null==coaDetail) {
				 System.out.println("Mapping Not Found Creating ");
				 coaDetail = new CChartOfAccountDetail();
				 coaDetail.setCreatedDate(new Date());
				 coaDetail.setGlCodeId(glc);
				 coaDetail.setDetailTypeId(accountdetailtype);
				 chartOfAccountDetailService.persist(coaDetail);
				 
				 System.out.println("Created GLcode mapping ");
			 }
			 System.out.println("Building Bill Details");
		   
			 bd.setCreditamount(l.getCreditAmount());
			 bd.setDebitamount(l.getDebitAmount());
			 bd.setFunction(function);
			 
			
			 bd.setFunctionid(new BigDecimal(function.getId()));
			 bd.setGlcodeid(new BigDecimal(glc.getId()));
			 
			 System.out.println("Building Payee Details");
			 pd.setAccountDetailTypeId(accountdetailtype.getId());
			 pd.setAccountDetailKeyId(accountDetailKey.getDetailkey());
			 pd.setCreditAmount(l.getDebitAmount());
			 pd.setDebitAmount(l.getCreditAmount());
			 pd.setEgBilldetailsId(bd);
			 
			 popluatedBills.add(bd);
			 populatedPayee.add(pd);
			
		}
		
		EgBillregistermis m = new EgBillregistermis();
		m.setFunction(function);
		m.setDepartmentcode(vmis.getDepartmentcode());
		m.setVoucherHeader(vh);
		m.setFund(vh.getFundId());
		egbill.setEgBillregistermis(m);
		egbill.setBillDetails(popluatedBills);
		egbill.setBillPayeedetails(populatedPayee);
		
		egbill = getUpdatedEgBillRegister(egbill,appconfig,function);
		
		System.out.println(egbill);
		
		res.setMessage("Reached Created");
		res.setSuccess(true);
		try {
			//expenseBillRepository.save(egbill);
			persistenceService.persist(egbill);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//expenseBillRepository.save(egbill);
		return res;

	}
	
	
	public EgBillregister getUpdatedEgBillRegister(EgBillregister egBillregister,AppConfig appconfig,CFunction function) {
		
		 egBillregister.setRefundable("Y"); 
		 egBillregister.setIsCitizenRefund("Y");
         egBillregister.setBilldate(new Date());
         egBillregister.setCreatedDate(new Date());
         BigDecimal totalCrAmt = BigDecimal.ZERO;
         Position owenrPos = new Position();
         
        
         String username = "";
         String userid = "";
         String splitedData[];
         AppConfigValues appconfigValues= appConfigValuesService.getConfigValuesByModuleAndKey(appconfig.getModule().getName(), appconfig.getKeyName()).get(0);
         splitedData=appconfigValues.getValue().split("-");
         userid = splitedData[0];
         username = splitedData[1];
         owenrPos.setId(Long.parseLong(userid));
        
         //egBillregister.getState().setNatureOfTask("Refund Bill");
              for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
                    
                    totalCrAmt=totalCrAmt.add(checknull(egbilldetail.getDebitamount()));
                    }
          
             List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
         for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
                     
               if(checknullBigDecimal(egbilldetail.getDebitamount())==true || checknullBigDecimal(egbilldetail.getCreditamount())==true) {
                      egbilldetailCusList.add(egbilldetail);
               }
            }
       
      egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);
      egBillregister.setBillamount(totalCrAmt);
      egBillregister.setBillDetails(egbilldetailCusList);       
     // EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
     
      
      egBillregister.setCreatedBy(owenrPos.getId());
      ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class); 
      
      String billNumber = v.getNextNumber(egBillregister);
      System.out.println(egBillregister.getEgBillregistermis().getFunction().getName());
      System.out.println("Billl number"+billNumber);
      egBillregister.setBillnumber(billNumber);
      egBillregister.setBilltype(FinancialConstants.BILLTYPE_FINAL_BILL);
      
      egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
      populateBillDetails(egBillregister);
      egBillregister.setPassedamount(egBillregister.getBillamount());
      egBillregister.getEgBillregistermis().setEgBillregister(egBillregister);
      egBillregister.getEgBillregistermis().setLastupdatedtime(new Date());

      if (egBillregister.getEgBillregistermis().getFund() != null
              && egBillregister.getEgBillregistermis().getFund().getId() != null)
          egBillregister.getEgBillregistermis().setFund(
                  fundService.findOne(egBillregister.getEgBillregistermis().getFund().getId()));
      if (egBillregister.getEgBillregistermis().getEgBillSubType() != null
              && egBillregister.getEgBillregistermis().getEgBillSubType().getId() != null)
          egBillregister.getEgBillregistermis().setEgBillSubType(
                  egBillSubTypeService.getById(egBillregister.getEgBillregistermis().getEgBillSubType().getId()));
      if (egBillregister.getEgBillregistermis().getSchemeId() != null)
          egBillregister.getEgBillregistermis().setScheme(
                  schemeService.findById(egBillregister.getEgBillregistermis().getSchemeId().intValue(), false));
      else
          egBillregister.getEgBillregistermis().setScheme(null);
      if (egBillregister.getEgBillregistermis().getSubSchemeId() != null)
          egBillregister.getEgBillregistermis().setSubScheme(
                  subSchemeService.findById(egBillregister.getEgBillregistermis().getSubSchemeId().intValue(), false));
      else
          egBillregister.getEgBillregistermis().setSubScheme(null);

      egBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.REFUNDBILL_FIN,
                  FinancialConstants.CONTINGENCYBILL_CREATED_STATUS));
      
    
      egBillregister.transition().start().withSenderName(username+ "::" +username)
                      .withComments("")
                      .withStateValue(FinancialConstants.BUTTONSAVEASDRAFT).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? username:"")
                      .withNextAction("")
                      .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_REFUND_BILL_DISPLAYNAME)
                      .withCreatedBy(owenrPos.getId())
                      .withtLastModifiedBy(owenrPos.getId());
		
		
		return egBillregister;
	}

	
	
	  
	public String getEmployeeName(Long empId){
	        
	        return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	     }
	@Transactional
	public OtherParty checkforOtherPartyDatandCreate(RefundReceiptRest req,AppConfig appconfig) {
		System.out.println("Other Party Excution");
		 AppConfigValues appconfigValues= appConfigValuesService.getConfigValuesByModuleAndKey(appconfig.getModule().getName(), appconfig.getKeyName()).get(0);
		RefundReceiptRest r = req;
		System.out.println(appconfigValues.getValue());
		OtherParty o = null;
		Bank b = null;
		if (null != r) {
			b = new Bank();
			b = bankRepository.findBankByName(r.getBankName());
			
			if (null != r.getCitizenName() && null != b) {	
				OtherParty otherparty = otherPartyService.getByNameOrAccount(r.getCitizenName(), r.getBankAccount());
				System.out.println(null==otherparty);
				if (null == otherparty) {
					o = new OtherParty();
					OtherParty other_party = new OtherParty();
					other_party.setIfscCode(r.getIfscCode());
					other_party.setRegistrationNumber(r.getBankAccount());
					other_party.setCode(r.getBankAccount());
					other_party.setBankAccount(r.getBankAccount());
					other_party.setName(r.getCitizenName());
					other_party.setRegistrationNumber(r.getBankAccount());
					other_party.setCreatedDate(new Date());
					other_party.setLastModifiedDate(new Date());
					other_party.setCorrespondenceAddress(r.getCorrespondingAddress());
					other_party.setCreatedBy(Long.parseLong(appconfigValues.getValue()));	
					try {
						persistenceService.persist(other_party);
						o=otherPartyService.getByNameOrAccount(other_party.getName(), other_party.getBankAccount());
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
					
					
					System.out.println("Other Party Created");
				}
				return otherparty;
			}

		}
		return o;
	}

	public boolean checkforValidBankData(RefundReceiptRequest req) {
		RefundReceiptRest r = req.getRefundReceiptRest();
		Bank b = null;
		OtherParty o = null;
		if (null != r) {
			if (null == r.getBankName()) {
				return false;
			}
			b = bankRepository.findBankByName(r.getBankName());
			if (null != b) {
				return true;
			}

		}
		return false;
	}

	protected void validateBillNumber(final EgBillregister egBillregister, final BindingResult resultBinder) {
		if (!expenseBillService.isBillNumberGenerationAuto() && egBillregister.getId() == null)
			if (!expenseBillService.isBillNumUnique(egBillregister.getBillnumber()))
				resultBinder.reject("msg.expense.bill.duplicate.bill.number",
						new String[] { egBillregister.getBillnumber() }, null);
	}

	protected EgBillregister populateBillDetails( EgBillregister egBillregister) {
		egBillregister.getEgBilldetailes().clear();

		if (egBillregister.getExpendituretype()
				.equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT)) {
			egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
		} else if (egBillregister.getExpendituretype()
				.equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND)) {
			egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
		} else {
			egBillregister.getEgBilldetailes().addAll(egBillregister.getDebitDetails());
			egBillregister.getEgBilldetailes().addAll(egBillregister.getCreditDetails());
			egBillregister.getEgBilldetailes().addAll(egBillregister.getNetPayableDetails());
		}

		for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
			if (details.getGlcodeid() != null) {
				if (egBillregister.getEgBillregistermis().getFunction() != null) {
					details.setFunctionid(
							BigDecimal.valueOf(egBillregister.getEgBillregistermis().getFunction().getId()));
					details.setFunction(egBillregister.getEgBillregistermis().getFunction());
				}
				details.setEgBillregister(egBillregister);
				details.setLastupdatedtime(new Date());
				details.setChartOfAccounts(chartOfAccountsService.findById(details.getGlcodeid().longValue(), false));
			}
		}
		if (!egBillregister.getBillPayeedetails().isEmpty())
			egBillregister = populateBillPayeeDetails(egBillregister);	
		return egBillregister;
	}

	protected EgBillregister populateBillPayeeDetails(final EgBillregister egBillregister) {
		EgBillPayeedetails payeeDetail;
		for (final EgBilldetails details : egBillregister.getEgBilldetailes())
			for (final EgBillPayeedetails payeeDetails : egBillregister.getBillPayeedetails())
				if (details.getGlcodeid().equals(payeeDetails.getEgBilldetailsId().getGlcodeid())) {
					List<Object[]> accountDetails = this.getAccountDetails(payeeDetails.getAccountDetailKeyId(),
							payeeDetails.getAccountDetailTypeId());
					payeeDetail = new EgBillPayeedetails();
					payeeDetail.setEgBilldetailsId(details);
					payeeDetail.setAccountDetailTypeId(payeeDetails.getAccountDetailTypeId());
					payeeDetail.setAccountDetailKeyId(payeeDetails.getAccountDetailKeyId());
					payeeDetail.setDebitAmount(payeeDetails.getDebitAmount());
					payeeDetail.setCreditAmount(payeeDetails.getCreditAmount());
					payeeDetail.setDetailKeyName(!accountDetails.isEmpty() ? (String) accountDetails.get(0)[0] : "");
					payeeDetail.setDetailTypeName(!accountDetails.isEmpty() ? (String) accountDetails.get(0)[1] : "");
					payeeDetail.setLastUpdatedTime(new Date());
					details.getEgBillPaydetailes().add(payeeDetail);
				}
		return egBillregister;
	}

	private List<Object[]> getAccountDetails(Integer accountDetailKeyId, Integer accountDetailTypeId) {
		String queryString = "select adk.detailname as detailkeyname,adt.name as detailtypename from accountdetailkey adk inner join accountdetailtype adt on adk.detailtypeid=adt.id where adk.detailtypeid=:detailtypeid and adk.detailkey=:detailkey";
		SQLQuery sqlQuery = persistenceService.getSession().createSQLQuery(queryString);
		sqlQuery.setInteger("detailtypeid", accountDetailTypeId);
		sqlQuery.setInteger("detailkey", accountDetailKeyId);
		return sqlQuery.list();
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

}
