package org.egov.egf.web.controller.microservice;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.EgModules;
import org.egov.commons.Migration;
import org.egov.egf.contract.model.AccountDetailContract;
import org.egov.egf.contract.model.TransactionDetail;
import org.egov.egf.contract.model.MigrationRequest;
import org.egov.egf.contract.model.MigrationResponse;
import org.egov.egf.contract.model.SubledgerDetailContract;
import org.egov.egf.contract.model.Voucher;
import org.egov.egf.contract.model.VoucherRequest;
import org.egov.egf.contract.model.VoucherResponse;
import org.egov.egf.contract.model.VoucherSearchRequest;
import org.egov.egf.voucher.repository.MigrationRepo;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.egov.services.voucher.VoucherService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoucherController {
	private static final Logger LOGGER = Logger.getLogger(VoucherController.class);
	@Autowired
	private CreateVoucher createVoucher;
	@Autowired
	private VoucherService voucherService;
	
	@Autowired
	MigrationRepo mRepo;
	
	 @Autowired
	 @Qualifier("persistenceService")
	 private PersistenceService persistenceService;

	@PostMapping(value = "/rest/voucher/_search")
	@ResponseBody
	public VoucherResponse create(@RequestBody VoucherSearchRequest voucherSearchRequest) {
		try {
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
                        referenceDocument = URLDecoder.decode(referenceDocument, "UTF-8");
                        List<CVoucherHeader> cVoucherHeaders = voucherService.getVoucherByServiceNameAndReferenceDocument(serviceCode, referenceDocument);
                        VoucherResponse res = new VoucherResponse();
                        if(cVoucherHeaders == null){
                            res.setResponseInfo(MicroserviceUtils.getResponseInfo(null,
                                    HttpStatus.SC_NOT_FOUND, null));
                        }else{
                            res.setVouchers(cVoucherHeaders.stream().map(cv -> new Voucher(cv)).collect(Collectors.toList()));
                        }
                        return res;
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
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
}