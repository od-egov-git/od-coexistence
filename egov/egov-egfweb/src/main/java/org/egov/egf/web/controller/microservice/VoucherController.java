package org.egov.egf.web.controller.microservice;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.collection.entity.MisReceiptDetail;
import org.egov.collection.service.MisReceiptDetailService;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bank;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.EgModules;
import org.egov.commons.Fund;
import org.egov.commons.Migration;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.AccountDetailKeyService;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.autonumber.ExpenseBillNumberGenerator;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.commons.bank.service.CreateBankService;
import org.egov.egf.contract.model.AccountDetailContract;
import org.egov.egf.contract.model.MigrationRequest;
import org.egov.egf.contract.model.MigrationResponse;
import org.egov.egf.contract.model.MisReceiptsDetailsRequest;
import org.egov.egf.contract.model.MisReceiptsDetailsResponse;
import org.egov.egf.contract.model.MisReceiptsPOJO;
import org.egov.egf.contract.model.RefundRequest;
import org.egov.egf.contract.model.RefundResponse;
import org.egov.egf.contract.model.SubledgerDetailContract;
import org.egov.egf.contract.model.TransactionDetail;
import org.egov.egf.contract.model.Voucher;
import org.egov.egf.contract.model.VoucherDetailsResponse;
import org.egov.egf.contract.model.VoucherRequest;
import org.egov.egf.contract.model.VoucherResponse;
import org.egov.egf.contract.model.VoucherSearchRequest;
import org.egov.egf.expensebill.service.RefundBillService;
import org.egov.egf.expensebill.service.VouchermisService;
import org.egov.egf.masters.services.OtherPartyService;
import org.egov.egf.voucher.repository.MigrationRepo;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.egov.model.masters.OtherParty;
import org.egov.model.voucher.PreApprovedVoucher;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.FinancialConstants;
import org.egov.utils.PaymentRefundUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.exilant.eGov.src.domain.GeneralLedger;
@RestController
public class VoucherController extends BaseBillController{
	private static final Logger LOGGER = Logger.getLogger(VoucherController.class);
	public VoucherController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }
	@Autowired
	MigrationRepo mRepo;
	@Autowired
	private CreateVoucher createVoucher;
	@Autowired
	private VoucherService voucherService;
	@Autowired
	MisReceiptDetailService misReceiptDetailService;
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
    private RefundBillService refundBillService;
    @Autowired
	private FunctionDAO functionDAO;
    @Autowired
    private OtherPartyService otherPartyService;
    @Autowired 
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
	private AutonumberServiceBeanResolver beanResolver;
    @Autowired
    private VouchermisService vouchermisService;
    @Autowired
	private PaymentRefundUtils paymentRefundUtils;
    @Autowired
    private CreateBankService createBankService;
    @Autowired
    private AccountDetailKeyService accountDetailKeyService;
    @Autowired
    private AccountdetailtypeService accountdetailtypeService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private EgBillSubTypeService egBillSubTypeService;
    @Autowired
    private ChartOfAccountDetailService chartOfAccountDetailService;
	@PostMapping(value = "/rest/voucher/_search")
	@ResponseBody
	public VoucherResponse create(@RequestBody VoucherSearchRequest voucherSearchRequest) {
		try {

    		LOGGER.info("Inside /rest/voucher/_search ");
			VoucherResponse response = voucherService.findVouchers(voucherSearchRequest);
			response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherSearchRequest.getRequestInfo(),
					HttpStatus.SC_OK, null));
			
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationRuntimeException(e.getMessage());
		}

	}
	
	@PostMapping(value = "/rest/voucher/_ismanualreceiptdateenabled")
        @ResponseBody
        public AppConfigValues getManualReceiptDateConsiderationForVoucher() {
                try {
                    return voucherService.isManualReceiptDateEnabledForVoucher();
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
        }
	
	@PostMapping(value = "/rest/voucher/_getmoduleidbyname")
        @ResponseBody
        public EgModules getEgModuleIdByName(@Param("moduleName") String moduleName) {
                try {
                        return voucherService.getModulesIdByName(moduleName);
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
        }

	@PostMapping(value = "/rest/voucher/_create")
	@ResponseBody
	public VoucherResponse create(@RequestBody VoucherRequest voucherRequest) {
		
		LOGGER.info("Inside /rest/voucher/_create ");

		VoucherResponse response = new VoucherResponse();
		final HashMap<String, Object> headerDetails = new HashMap<String, Object>();
		HashMap<String, Object> detailMap = null;
		HashMap<String, Object> subledgertDetailMap = null;
		final List<HashMap<String, Object>> accountdetails = new ArrayList<>();
		final List<HashMap<String, Object>> subledgerDetails = new ArrayList<>();

		for (Voucher voucher : voucherRequest.getVouchers()) {
			try {
				SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
				Date vDate = fm.parse(voucher.getVoucherDate());
				headerDetails.put(VoucherConstant.DEPARTMENTCODE, voucher.getDepartment());
				headerDetails.put(VoucherConstant.VOUCHERNAME, voucher.getName());
				headerDetails.put(VoucherConstant.VOUCHERTYPE, voucher.getType());
				headerDetails.put(VoucherConstant.VOUCHERNUMBER, voucher.getVoucherNumber());
				headerDetails.put(VoucherConstant.VOUCHERDATE, vDate);
				headerDetails.put(VoucherConstant.DESCRIPTION, voucher.getDescription());
				headerDetails.put(VoucherConstant.MODULEID, voucher.getModuleId());
				String source = voucher.getSource();
				headerDetails.put(VoucherConstant.SOURCEPATH, source);
				headerDetails.put(VoucherConstant.RECEIPTNUMBER, voucher.getReceiptNumber());
//				String receiptNumber = !source.isEmpty() & source != null ? source.indexOf("?selectedReceipts=") != -1 ? source.substring(source.indexOf("?selectedReceipts=")).split("=")[1]: "" : "";
				if(voucher.getReferenceDocument() != null && !voucher.getReferenceDocument().isEmpty()){
				    headerDetails.put(VoucherConstant.REFERENCEDOC, voucher.getReferenceDocument());
				}
				if(voucher.getServiceName() != null && !voucher.getServiceName().isEmpty()){
				    headerDetails.put(VoucherConstant.SERVICE_NAME, voucher.getServiceName());
				}
				// headerDetails.put(VoucherConstant.BUDGETCHECKREQ, voucher());
				if (voucher.getFund() != null)
					headerDetails.put(VoucherConstant.FUNDCODE, voucher.getFund().getCode());

				if (voucher.getFunction() != null)
					headerDetails.put(VoucherConstant.FUNCTIONCODE, voucher.getFunction().getCode());

				if (voucher.getFunctionary() != null)
					headerDetails.put(VoucherConstant.FUNCTIONARYCODE, voucher.getFunctionary().getCode());
				if (voucher.getScheme() != null)
					headerDetails.put(VoucherConstant.SCHEMECODE, voucher.getScheme().getCode());
				if (voucher.getSubScheme() != null)
					headerDetails.put(VoucherConstant.SUBSCHEMECODE, voucher.getSubScheme().getCode());

				for (AccountDetailContract ac : voucher.getLedgers()) {

					detailMap = new HashMap<>();
					detailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
					detailMap.put(VoucherConstant.DEBITAMOUNT, ac.getDebitAmount());
					detailMap.put(VoucherConstant.CREDITAMOUNT, ac.getCreditAmount());
					if (ac.getFunction() != null)
						detailMap.put(VoucherConstant.FUNCTIONCODE, ac.getFunction().getCode());

					accountdetails.add(detailMap);

					for (SubledgerDetailContract sl : ac.getSubledgerDetails()) {

						subledgertDetailMap = new HashMap<>();
						subledgertDetailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
						subledgertDetailMap.put(VoucherConstant.DETAILAMOUNT, sl.getAmount());
						subledgertDetailMap.put(VoucherConstant.DETAIL_TYPE_ID, sl.getAccountDetailType().getId());
						subledgertDetailMap.put(VoucherConstant.DETAIL_KEY_ID, sl.getAccountDetailKey().getId());
						subledgerDetails.add(subledgertDetailMap);
					}
				}
				CVoucherHeader voucherHeader = createVoucher.createVoucher(headerDetails, accountdetails,
						subledgerDetails);
				voucher.setId(voucherHeader.getId());
				voucher.setVoucherNumber(voucherHeader.getVoucherNumber());
				response.getVouchers().add(voucher);
				response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherRequest.getRequestInfo(),
						HttpStatus.SC_CREATED, null));
			} catch (ValidationException e) {
				throw e;

			} catch (ApplicationRuntimeException e) {

				throw e;
			} catch (ParseException e) {

				throw new ApplicationRuntimeException(e.getMessage());
			}

		}
		return response;
	}
	@PostMapping(value = "/rest/voucher/_cancel")
	@ResponseBody
	public VoucherResponse cancel(@RequestBody VoucherSearchRequest voucherSearchRequest) {
		try {
			VoucherResponse response = voucherService.cancel(voucherSearchRequest);
			response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherSearchRequest.getRequestInfo(),
					HttpStatus.SC_OK, null));
			
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationRuntimeException(e.getMessage());
		}

	}
	
		@PostMapping(value = "/rest/voucher/_searchbyserviceandreference",produces="application/json")
        @ResponseBody
        public VoucherResponse searchVoucherByServiceCodeAndReferenceDoc(@RequestParam(name="servicecode",required=false)  String serviceCode, @RequestParam("referencedocument")  String referenceDocument) {
                try {
                		LOGGER.info("Inside searchVoucherByServiceCodeAndReferenceDoc method.");
                        referenceDocument = URLDecoder.decode(referenceDocument, "UTF-8");
                        List<CVoucherHeader> cVoucherHeaders = voucherService.getVoucherByServiceNameAndReferenceDocument(serviceCode, referenceDocument); 
                        VoucherResponse res = new VoucherResponse();
                        if(cVoucherHeaders == null){
                        	LOGGER.info("vouchers not found inside searchVoucherByServiceCodeAndReferenceDoc");
                            res.setResponseInfo(MicroserviceUtils.getResponseInfo(null,
                                    HttpStatus.SC_NOT_FOUND, null));
                        }else{
                        	LOGGER.info("cVoucherHeaders inside searchVoucherByServiceCodeAndReferenceDoc = "+cVoucherHeaders.toString());
                            res.setVouchers(cVoucherHeaders.stream().map(cv -> new Voucher(cv)).collect(Collectors.toList()));
                        }
                        return res;
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
        }
	
	@PostMapping(value = "/rest/voucher/_misreceipt")
	@ResponseBody
	public MisReceiptsDetailsResponse  createMiscReceiptDetails(@RequestBody MisReceiptsDetailsRequest misReceiptsDetailsRequest ){
		//System.out.println("1....nEW");
		LOGGER.info("Inside /rest/voucher/_misreceipt");
		ModelMap model =null;
		MisReceiptsDetailsResponse res = new MisReceiptsDetailsResponse();
		res.setSuccess(false);
		MisReceiptDetail misReceiptDetail = null;
		MisReceiptsPOJO m = misReceiptsDetailsRequest.getMisReceiptsPOJO();
		Date date   = null;
		 try {
			 model= new ModelMap();
			date   = new Date(m.getReceipt_date());
			misReceiptDetail = new MisReceiptDetail();
         	misReceiptDetail.setBank_branch(m.getBank_branch());
         	misReceiptDetail.setBank_name(m.getBank_name());
         	misReceiptDetail.setCollectedbyname(m.getCollectedbyname());
         	misReceiptDetail.setGstno(m.getGstno());
         	misReceiptDetail.setNarration(m.getNarration());
         	misReceiptDetail.setPaid_by(m.getPaid_by());
         	misReceiptDetail.setPayer_address(m.getPayer_address());
         	misReceiptDetail.setPayment_mode(m.getPayment_mode());
         	misReceiptDetail.setPayment_status(m.getPayment_status());
         	misReceiptDetail.setPayments_id(m.getPayments_id());
         	misReceiptDetail.setReceipt_number(m.getReceipt_number());
         	misReceiptDetail.setReceipt_date(date);
         	misReceiptDetail.setServicename(m.getServicename());
         	misReceiptDetail.setSubdivison(m.getSubdivison());
         	misReceiptDetail.setTotal_amt_paid(m.getTotal_amt_paid());
         	misReceiptDetail.setChequeddno(m.getChequeddno());
         	misReceiptDetail.setChequedddate(new Date(m.getChequedddate()));
         	System.out.println("Details :::"+misReceiptDetail.toString());
         	misReceiptDetail  = misReceiptDetailService.save(misReceiptDetail);
         	System.out.println("Saved Successfully");
         	res.setSuccess(true);
         	res.setMessage("Saved Successfully");
         	model.addAttribute("response", res);
        	model.addAttribute("data",misReceiptDetail);
         } catch (Exception e) {
                 LOGGER.error(e.getMessage(), e);
               //  throw new ApplicationRuntimeException(e.getMessage());
                 e.printStackTrace();
         }
			 return res;
	       
	 
	}
	
	@PostMapping(value = "/rest/voucher/migration_data_save")
	@ResponseBody
	public MigrationResponse migration_data_save(@RequestBody MigrationRequest voucherRequest) {
		System.out.println("XX");
		MigrationResponse response = new MigrationResponse();
		List<TransactionDetail> mirationList=voucherRequest.getVouchers();
		Migration m = null;
		int counter=1;
		try
		{
		//save
			for(TransactionDetail k:mirationList)
			{
				System.out.println("counter :::"+counter++);
				if (counter > 0 && counter % 50 == 0) {
					mRepo.flush();
					persistenceService.getSession().flush();
					persistenceService.getSession().clear();
		        }
				m = new Migration();
				m.setTrn_id(Long.parseLong(k.getTrn_id()));
				m.setVoucher_name(k.getVOUCHER_NAME()!=null?k.getVOUCHER_NAME():"");
				m.setVoucher_type(k.getVOUCHER_TYPE()!=null?k.getVOUCHER_TYPE():"");
				m.setVoucher_description(k.getVOUCHER_DESCRIPTION()!=null?k.getVOUCHER_DESCRIPTION():"");
				m.setVoucher_no(k.getVOUCHER_NO()!=null?k.getVOUCHER_NO():"");
				m.setTransaction_no((k.getTRANSACTION_NO()!=null && !k.getTRANSACTION_NO().isEmpty())?Long.parseLong(k.getTRANSACTION_NO()):null);
				m.setVoucher_date(k.getVOUCHER_DATE()!=null?k.getVOUCHER_DATE():"");
				m.setTransaction_date(k.getTRANSACTION_DATE()!=null?k.getTRANSACTION_DATE():"");
				m.setFund_name(k.getFUND_NAME()!=null?k.getFUND_NAME():"");
				m.setFinancial_year(k.getFINANCIAL_YEAR()!=null?k.getFINANCIAL_YEAR():"");
				m.setTransaction_no_for_data_migration(k.getTRANSACTION_NO()+"/"+m.getFinancial_year());
				m.setVoucher_status(k.getVOUCHER_STATUS()!=null? k.getVOUCHER_STATUS():"");
				m.setCreated_by(k.getCREATED_BY()!=null ? k.getCREATED_BY():"");
				m.setVoucher_first_signatory(k.getVOUCHER_FIRST_SIGNATORY()!=null ? k.getVOUCHER_FIRST_SIGNATORY():"");
				m.setVoucher_second_signatory(k.getVOUCHER_SECOND_SIGNATORY()!=null ? k.getVOUCHER_SECOND_SIGNATORY():"");
				m.setDepartment_name(k.getDEPARTMENT_NAME()!=null ? k.getDEPARTMENT_NAME():"");
				m.setScheme_name(k.getSCHEME_NAME()!=null ? k.getSCHEME_NAME():"");
				m.setSub_scheme_name(k.getSUB_SCHEME_NAME()!=null ? k.getSUB_SCHEME_NAME():"");
				m.setBudgetary_application_no(k.getBUDGETARY_APPLICATION_NO()!=null ? k.getBUDGETARY_APPLICATION_NO():"");
				m.setBudget_cheque_request(k.getBUDGET_CHEQUE_REQUEST()!=null ? k.getBUDGET_CHEQUE_REQUEST():"");
				m.setFunction_name(k.getFUNCTION_NAME()!=null ? k.getFUNCTION_NAME():"");
				m.setFile_no(k.getFILE_NO()!=null ? k.getFILE_NO():"");
				m.setService_name(k.getSERVICE_NAME()!=null ? k.getSERVICE_NAME():"");
				m.setReceipt_no(k.getRECEIPT_NO()!=null ? k.getRECEIPT_NO():"");
				m.setRemittance_date(k.getREMITTANCE_DATE()!=null ? k.getREMITTANCE_DATE():"");
				m.setTrans_id_receipt_no((k.getTRANS_ID_RECEIPT_NO()!=null && !k.getTRANS_ID_RECEIPT_NO().isEmpty())?Long.parseLong(k.getTRANS_ID_RECEIPT_NO()):null);
				m.setGlcode(k.getGLCODE()!=null?k.getGLCODE():"");
				m.setBank_account_name(k.getBANK_ACCOUNT_NAME()!=null ? k.getBANK_ACCOUNT_NAME():"");
				m.setBank_account_code(k.getBANK_ACCOUNT_CODE()!=null ? k.getBANK_ACCOUNT_CODE():"");
				m.setDebit_amount((k.getDEBIT_AMOUNT()!=null && !k.getDEBIT_AMOUNT().isEmpty())? new BigDecimal(k.getDEBIT_AMOUNT()) : null);
				m.setCredit_amount((k.getCREDIT_AMOUNT()!=null && !k.getCREDIT_AMOUNT().isEmpty())? new BigDecimal(k.getCREDIT_AMOUNT()): null);
				m.setContractor_name(k.getCONTRACTOR_NAME()!=null ? k.getCONTRACTOR_NAME() : "");
				m.setSupplier_name(k.getSUPPLIER_NAME()!=null ? k.getSUPPLIER_NAME() : "");
				m.setParty_details(k.getPARTY_DETAILS()!=null ? k.getPARTY_DETAILS() : "");
				m.setOther_party(k.getOTHER_PARTY()!=null ? k.getOTHER_PARTY() : "");
				m.setPayment_amount_to_party((k.getPAYMENT_AMOUNT_TO_PARTY()!=null && !k.getPAYMENT_AMOUNT_TO_PARTY().isEmpty())? new BigDecimal(k.getPAYMENT_AMOUNT_TO_PARTY()) : null);
				m.setMigration("");
				m.setReason("");
				
				mRepo.save(m);
				
			}
			
			response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherRequest.getRequestInfo(),
						HttpStatus.SC_CREATED, null));
			} catch (ValidationException e) {
				e.printStackTrace();

			} catch (ApplicationRuntimeException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}

		return response;
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
	
	
	@GetMapping(value = "/rest/voucher/migration_data_truncate")
	public ResponseEntity<ResponseInfoWrapper> migration_data_truncate(@RequestParam(required = false) final String code) {
		boolean result1=false;
		List<Boolean> resultList=new ArrayList<Boolean>();
		System.out.println("code ::::"+code);
		try
		{
			result1=truncate(code);
			System.out.println("result1 ::::"+result1);
			resultList.add(result1);
		}catch (Exception e) {
			resultList.add(result1);
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status("Success").build())
				.responseBody(resultList).build(), org.springframework.http.HttpStatus.OK);
	}
	
	private boolean truncate(String code) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String response="";
    	boolean result=false;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select generic.f_schema_truncate('"+code+"')");
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	response=rows.toString();
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	if(response != null && !response.isEmpty() && response.contains("true"))
    	{
    		result=true;
    	}
    	return result;
	}

	@GetMapping(value = "/rest/voucher/migration_data_trigger")
	public ResponseEntity<ResponseInfoWrapper> migration_data_trigger(@RequestParam(required = false) final String code) {
		boolean result1=false;
		boolean result2=false;
		List<Boolean> resultList=new ArrayList<Boolean>();
		System.out.println("code ::::"+code);
		try
		{
			result1=trigger1(code);
			System.out.println("result1 ::::"+result1);
			resultList.add(result1);
			if(result1)
			{
				result2=trigger2(code);
				System.out.println("result2 ::::"+result2);
				resultList.add(result2);
			}
			
			
		}catch (Exception e) {
			resultList.add(result1);
			resultList.add(result2);
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status("Success").build())
				.responseBody(resultList).build(), org.springframework.http.HttpStatus.OK);
	}
	
	private boolean trigger2(String code) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String response="";
    	boolean result=false;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select generic.f_mig_tran('"+code+"')");
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    		response=rows.toString();
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	if(response != null && !response.isEmpty() && response.contains("true"))
    	{
    		result=true;
    	}
    	return result;
	}

	private boolean trigger1(String code) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String response="";
    	boolean result=false;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select generic.f_mig_tran_chk('"+code+"')");
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    		response=rows.toString();
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	if(response != null && !response.isEmpty() && response.contains("true"))
    	{
    		result=true;
    	}
    	return result;
	}

	@GetMapping(value = "/rest/voucher/migration_data_extract")
	public ResponseEntity<ResponseInfoWrapper> migration_data_extract(@RequestParam(required = false) final String code) {
		boolean result1=true;
		List<TransactionDetail> migrationDetailList=new ArrayList<TransactionDetail>();
		System.out.println("code ::::"+code);
		try
		{
			migrationDetailList=extract(code);
			System.out.println("result1 ::::"+result1);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status("Success").build())
				.responseBody(migrationDetailList).build(), org.springframework.http.HttpStatus.OK);
	}

	private List<TransactionDetail> extract(String code) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	List<TransactionDetail> resultList=new ArrayList<TransactionDetail>();
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select * from "+code+".transaction_csv ");
    	    rows = query.list();
    	    TransactionDetail k =null;
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] data : rows)
    	    	{
    	    		k = new TransactionDetail();
					k.setTrn_id(data[0] !=null ?data[0].toString():"");
					k.setVOUCHER_NAME(data[1] !=null ?data[1].toString():"");
					k.setVOUCHER_TYPE(data[2] !=null ?data[2].toString():"");
					k.setVOUCHER_DESCRIPTION(data[3] !=null ?data[3].toString():"");
					k.setVOUCHER_NO(data[4] !=null ?data[4].toString():"");
					k.setTRANSACTION_NO(data[5] !=null ?data[5].toString():"");
					k.setTRANSACTION_NO_FOR_DATA_MIGRATION(data[6] !=null ?data[6].toString():"");
					k.setVOUCHER_DATE(data[7] !=null ?data[7].toString():"");
					k.setTRANSACTION_DATE(data[8] !=null ?data[8].toString():"");
					k.setFUND_NAME(data[9] !=null ?data[9].toString():"");
					k.setFINANCIAL_YEAR(data[10] !=null ?data[10].toString():"");
					k.setVOUCHER_STATUS(data[11] !=null ?data[11].toString():"");
					k.setCREATED_BY(data[12] !=null ?data[12].toString():"");
					k.setVOUCHER_FIRST_SIGNATORY(data[13] !=null ?data[13].toString():"");
					k.setVOUCHER_SECOND_SIGNATORY(data[14] !=null ?data[14].toString():"");
					k.setDEPARTMENT_NAME(data[15] !=null ?data[15].toString():"");
					k.setSCHEME_NAME(data[16] !=null ?data[16].toString():"");
					k.setSUB_SCHEME_NAME(data[17] !=null ?data[17].toString():"");
					k.setBUDGETARY_APPLICATION_NO(data[18] !=null ?data[18].toString():"");
					k.setBUDGET_CHEQUE_REQUEST(data[19] !=null ?data[19].toString():"");
					k.setFUNCTION_NAME(data[20] !=null ?data[20].toString():"");
					k.setFILE_NO(data[21] !=null ?data[21].toString():"");
					k.setSERVICE_NAME(data[22] !=null ?data[22].toString():"");
					k.setRECEIPT_NO(data[23] !=null ?data[23].toString():"");
					k.setREMITTANCE_DATE(data[24] !=null ?data[24].toString():"");
					k.setTRANS_ID_RECEIPT_NO(data[25] !=null ?data[25].toString():"");
					k.setGLCODE(data[26] !=null ?data[26].toString():"");
					k.setBANK_ACCOUNT_NAME(data[27] !=null ?data[27].toString():"");
					k.setBANK_ACCOUNT_CODE(data[28] !=null ?data[28].toString():"");
					k.setDEBIT_AMOUNT(data[29] !=null ?data[29].toString():"");
					k.setCREDIT_AMOUNT(data[30] !=null ?data[30].toString():"");
					k.setCONTRACTOR_NAME(data[31] !=null ?data[31].toString():"");
					k.setSUPPLIER_NAME(data[32] !=null ?data[32].toString():"");
					k.setPARTY_DETAILS(data[33] !=null ?data[33].toString():"");
					k.setOTHER_PARTY(data[34] !=null ?data[34].toString():"");
					k.setPAYMENT_AMOUNT_TO_PARTY(data[35] !=null ?data[35].toString():"");
					k.setMIGRATION(data[36] !=null ?data[36].toString():"");
					k.setREASON(data[37] !=null ?data[37].toString():"");
					resultList.add(k);
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return resultList;
	}
public boolean checknullBigDecimal(BigDecimal value) {
    	if(value==null) {
    		return false;
    	}
    	else {
		return true;
    	}
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
                    
                }
            }
        }
    }
    
    private List<Object[]> getAccountDetails(Integer accountDetailKeyId, Integer accountDetailTypeId) {
        String queryString = "select adk.detailname as detailkeyname,adt.name as detailtypename from accountdetailkey adk inner join accountdetailtype adt on adk.detailtypeid=adt.id where adk.detailtypeid=:detailtypeid and adk.detailkey=:detailkey";
        SQLQuery sqlQuery = persistenceService.getSession().createSQLQuery(queryString);
        sqlQuery.setInteger("detailtypeid", accountDetailTypeId);
        sqlQuery.setInteger("detailkey", accountDetailKeyId);
        return sqlQuery.list();
    }
    
	//@PostMapping(value = "/rest/voucher/refundBill")//, consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/rest/voucher/refundBill", method = {RequestMethod.POST})
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
		String billNumber="";
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
		egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);
		egBillregister.setIsCitizenRefund(refundRequest.getReceipt().getIsCitizenRefund());
		egBillregister.setBilldate(new Date());
		BigDecimal totalCrAmt = BigDecimal.ZERO;
		
		
		totalCrAmt = new BigDecimal(0); 
	 	egBillregister.setBillamount(totalCrAmt);
	 	final Long OtherCount=(Long) persistenceService.find("select count(*) from OtherParty where bankaccount='"+refundRequest.getReceipt().getBankAccount()+"' and name='"+refundRequest.getReceipt().getCitizenName()+"'");	
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
				otherParty.setName(refundRequest.getReceipt().getCitizenName());
				otherPartyService.create1(otherParty,approver);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		final List<CGeneralLedger> gllist = paymentRefundUtils
				.getAccountDetails(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		try {
			final Long otherpartyid=(Long) persistenceService.find("select id from OtherParty where bankaccount='"+refundRequest.getReceipt().getBankAccount()+"' and name='"+refundRequest.getReceipt().getCitizenName()+"'");
	    	List<GeneralLedger> ledger=new ArrayList();
			List<Object[]> list1= null;
			final StringBuffer query1 = new StringBuffer(500);
			SQLQuery queryMain =  null;
			query1
		    .append("select g.id, g.glcodeid,g.glcode,g.debitamount,g.creditamount,v.functionid from generalledger g,vouchermis v " + 
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
					CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
					coa = paymentRefundUtils.getChartOfAccount(object[2].toString());//glcode
					billdetail.setGlcodeid(new BigDecimal(coa.getId()));//glcodeid
					billdetail.setFunctionid(new BigDecimal(object[5].toString()));//functionid
					chartOfAccounts.setName(coa.getName());//glcodename
					chartOfAccounts.setGlcode(coa.getGlcode());////glcode
					billdetail.setChartOfAccounts(chartOfAccounts);
					billdetail.setDebitamount(new BigDecimal(object[3].toString()));//debit
					billdetail.setCreditamount(new BigDecimal(object[4].toString()));//credit
					System.out.println("object3 "+new BigDecimal(object[3].toString()));
					totalCrAmt=totalCrAmt.add(checknull(billdetail.getDebitamount()));
					System.out.println("totalCrAmt "+totalCrAmt);
					billdetail.setEgBillregister(egBillregister);
					billDetails.add(billdetail);
					EgBillPayeedetails payeeDetail = new EgBillPayeedetails();
                    payeeDetail.setEgBilldetailsId(billdetail);
                    payeeDetail.setAccountDetailKeyId(otherpartyid.intValue());
                    payeeDetail.setAccountDetailTypeId(accountdetailtype.getId());
                    payeeDetail.setCreditAmount(new BigDecimal(0));
                    payeeDetail.setLastUpdatedTime(new Date());
                    billdetail.getEgBillPaydetailes().add(payeeDetail);
                    billPayeeDetails.add(payeeDetail);
				}
			}
			
			egBillregister.setBillDetails(billDetails);
			egBillregister.setBillPayeedetails(billPayeeDetails);
			egBillregister.setBillamount(totalCrAmt);
			////////////////////
			
	        egBillregister.setBillPayeedetails(billPayeeDetails);
			List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
	       for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
	    	   if(checknullBigDecimal(egbilldetail.getDebitamount())==true || checknullBigDecimal(egbilldetail.getCreditamount())==true) {
	    		   egbilldetailCusList.add(egbilldetail);
	    	   }
			 }
		  egBillregister.setBillDetails(egbilldetailCusList);
		  
		 System.out.println(totalCrAmt);
		if(egBillregister.getEgBilldetailes()!=null)
	    {
		    egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
		    if(!egBillregister.getDebitDetails().isEmpty()&&!egBillregister.getCreditDetails().isEmpty())
			{
		    	populateBillDetails(egBillregister);
		    	validateBillNumber(egBillregister, resultBinder);
		    	refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
		    	//validateSubledgeDetails(egBillregister);
			}
	    }	
		////
		
		EgBillSubType egbillSubtype = (EgBillSubType) getBillSubTypes().stream()
				.filter(e -> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
		System.out.println("function "+egBillregister.getBillDetails().get(0).getFunctionid());
		Fund fund = new Fund();
		fund.setId(Integer.valueOf(1));
		BigDecimal bg1 = egBillregister.getBillDetails().get(0).getFunctionid();
		CFunction function = paymentRefundUtils.getFunction(bg1.longValue());
		EgBillregistermis egbillregistermis = new EgBillregistermis();
		egbillregistermis.setFund(fund);
		egbillregistermis.setFunction(function);
		egbillregistermis.setDepartmentcode(vouchermis.getDepartmentcode());
		egbillregistermis.setNarration(egBillregister.getNarration());
		egbillregistermis.setEgBillSubType(egbillSubtype);
		egBillregister.setEgBillregistermis(egbillregistermis);
		
		ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);
		billNumber = v.getNextNumber(egBillregister);
		System.out.println(billNumber);
		egBillregister.setBillnumber(billNumber);
		egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());	
		///save
		EgBillregister savedEgBillregister;
	         try {
	       	  System.out.println("From egbillregister Save method calling");
	              savedEgBillregister = refundBillService.create(egBillregister, approver, "", null, 
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
	
				System.out.println("accountdetailtype.getName() "+accountdetailtype.getName());
				System.out.println("accountdetailtype.getId() "+accountdetailtype.getId());
			final EntityTypeService entityService = (EntityTypeService) applicationContext.getBean(simpleName);
			entitiesList = (List<EntityType>) entityService.filterActiveEntities(refundRequest.getReceipt().getCitizenName(), 20,
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
		refundresponse.setBillNumber(billNumber);
		//refundresponse.setResponseInfo(responseInfo);
		return refundresponse;
	}


}