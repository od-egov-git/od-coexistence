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

package org.egov.collection.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.collection.bean.ReceiptBean;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.BranchUserMap;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.entity.Remittance;
import org.egov.collection.entity.RemittanceDetail;
import org.egov.collection.entity.RemittanceInstrument;
import org.egov.collection.integration.services.RemittanceSchedulerService;
import org.egov.collection.utils.CollectionsNumberGenerator;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.collection.utils.FinancialsUtil;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.EgwStatus;
import org.egov.commons.Fund;
import org.egov.commons.dao.ChartOfAccountsDAO;
import org.egov.commons.dao.FunctionHibernateDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.infra.admin.master.service.DepartmentService;															 
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.BillDetailAdditional;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.BusinessServiceCriteria;
import org.egov.infra.microservice.models.BusinessServiceMapping;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Instrument;
import org.egov.infra.microservice.models.InstrumentAccountCode;
import org.egov.infra.microservice.models.InstrumentResponse;
import org.egov.infra.microservice.models.PaymentStatusEnum;
import org.egov.infra.microservice.models.PaymentWorkflow;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.models.RemitancePOJO;
import org.egov.infra.microservice.models.MisRemittanceDetails;
import org.egov.infra.microservice.models.RemittanceReceipt;
import org.egov.infra.microservice.models.RemittanceResponse;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.models.ServiceDetails;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.services.voucher.VoucherService;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

@Transactional(readOnly = true)
public class RemittanceServiceImpl extends RemittanceService {
    private static final long serialVersionUID = 5581301494846870670L;
    private static final Logger LOGGER = Logger.getLogger(ReceiptHeaderService.class);
    private CollectionsUtil collectionsUtil;
    private FinancialsUtil financialsUtil;
    private ReceiptHeaderService receiptHeaderService;
    private PersistenceService persistenceService;
    private CollectionsNumberGenerator collectionsNumberGenerator;
    
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd/MMM/yyyy", LOCALE);
	
    @Autowired
    private FundHibernateDAO fundHibernateDAO;
    @Autowired
    private FunctionHibernateDAO functionHibernateDAO;
    @Autowired
	private DepartmentService departmentService;
    @Autowired
    private ChartOfAccountsDAO chartOfAccountsDAO;
    private PersistenceService<Remittance, Long> remittancePersistService;
    @Autowired
    @Qualifier("branchUserMapService")
    private PersistenceService<BranchUserMap, Long> branchUserMapService;
    @Autowired
    @Qualifier("remittanceInstrumentService")
    private PersistenceService<RemittanceInstrument, Long> remittanceInstrumentService;
    @Autowired
    private transient RemittanceSchedulerService remittanceSchedulerService;
    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    @Autowired
    @Qualifier("misRemittanceDetailService")
    private MisRemittanceDetailService misRemittanceDetailService;
    
    private List resultList = new ArrayList();
    Map<String, String> serviceCategoryNames = new HashMap<String, String>();
    Map<String, Map<String, String>> serviceTypeMap = new HashMap<>();
    
    /**
     * Create Contra Vouchers
     *
     * @return List of Contra Vouchers Created
     */
    
    @Transactional
    public ReceiptBean createCashBankRemittance(ReceiptBean receiptBean, List<RemitancePOJO> rp, Date remittanceDate, String narration, String deptIdnew, String functionNew, String subdivisonNew, String receiptNumbers) {   

        final SimpleDateFormat dateFomatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        InstrumentAccountCode accountCode = microserviceUtils.getInstrumentAccountGlCodeByType(CollectionConstants.INSTRUMENTTYPE_NAME_CASH);
        final String cashInHandQueryString = "SELECT COA.GLCODE FROM CHARTOFACCOUNTS COA WHERE COA.GLCODE = '"
                + accountCode.getGlcode()+ "'";
        final Query cashInHand = persistenceService.getSession().createSQLQuery(cashInHandQueryString);
        String cashInHandGLCode = null;

        if (!cashInHand.list().isEmpty())
            cashInHandGLCode = cashInHand.list().get(0).toString();

        Map<String,BigDecimal> bankAccountMap=new HashMap<String,BigDecimal>();
        BigDecimal amt=null;
        for(RemitancePOJO row:rp)
        {
        	if(bankAccountMap.get(row.getBankaccount()) == null )
        	{
        		amt=new BigDecimal(row.getAmount());
        		bankAccountMap.put(row.getBankaccount(),amt);
        	}
        	else
        	{
        		BigDecimal total=bankAccountMap.get(row.getBankaccount()).add(new BigDecimal(row.getAmount()));
        		bankAccountMap.put(row.getBankaccount(), total);
        	}
        }

        String createVoucher = "N";
        Boolean showRemitDate = true;
        BigDecimal totalCashVoucherAmt = BigDecimal.ZERO;
        String fundCode = "";
        Date voucherDate = null;
        List<Bankaccount> depositedBankAccount = new ArrayList<Bankaccount>();
        Bankaccount b=null;
        Set<String> keys=bankAccountMap.keySet();
        Map<String,BigDecimal> serviceGlCodes = new HashMap<String,BigDecimal>();
        List<BigDecimal> totalCashAmt = new ArrayList<BigDecimal>();
        BigDecimal amt1=new BigDecimal(0);
        for(String key : keys)
        {
        	String[] accNum=key.split("-");
        	b=(Bankaccount) persistenceService.find("from Bankaccount where accountnumber=?",accNum[2]);
        	depositedBankAccount.add(b);
        	amt1=new BigDecimal(bankAccountMap.get(key).toString());
        	totalCashAmt.add(amt1);
        	totalCashVoucherAmt=totalCashVoucherAmt.add(amt1);
        	serviceGlCodes.put(b.getChartofaccounts().getGlcode(),amt1);
        }
        
            if (receiptBean.getSelected() != null) {
                if (receiptBean.getFund() != null && !receiptBean.getFund().isEmpty())
                {
                	if(receiptBean.getFund().equalsIgnoreCase("Municipal (General) Fund"))
                		fundCode="01";
                	else
                	fundCode = receiptBean.getFund();
                }
                if (showRemitDate && remittanceDate != null)
                    voucherDate = remittanceDate;
                else
                {
                	try {
                        voucherDate = collectionsUtil.getRemittanceVoucherDate(dateFomatter.parse(receiptBean.getReceiptDate()));
                    } catch (final ParseException e) {
                        LOGGER.error("Error Parsing Date", e);
                    }
                }
                if (receiptBean.getService() != null && receiptBean.getService().length() > 0) {
                    // If Cash Amount is present
                    if (receiptBean.getInstrumentAmount() != null && cashInHandGLCode != null) {
                        createVoucher = "Y";
                        String functionCode=functionNew;//receiptBean.getFunctionCode();
						//String deptCode=receiptBean.getDepartment();												   
                        
                        final Remittance remittance = populateAndPersistRemittanceNew(totalCashAmt, null, fundCode,
                                cashInHandGLCode, null, serviceGlCodes, functionCode, receiptBean, createVoucher,
                                narration,voucherDate, depositedBankAccount, totalCashVoucherAmt, BigDecimal.ZERO, Collections.EMPTY_LIST,
                                null,deptIdnew,subdivisonNew);
                        
                        receiptBean.setRemittanceReferenceNumber(remittance.getReferenceNumber());
                        receiptBean.setRemittanceVouherNumber(remittance.getReferenceVoucherNumber());
                        receiptBean.setVoucherid(remittance.getVoucherid());
                        for(String key : keys)
                        {
                        	try {
	                        MisRemittanceDetails mrd= new MisRemittanceDetails();
	                        mrd.setVoucher_number(remittance.getReferenceVoucherNumber());
	                        mrd.setVoucher_date(voucherDate);
	                        //mrd.setMis_receipt_id(Long.valueOf(receiptBean.getReceiptId()));
	                        mrd.setBankaccount(key);
	                        BigDecimal bankamt=new BigDecimal(bankAccountMap.get(key).toString());
	                        mrd.setAmount(bankamt);
	                        mrd.setDepartment(deptIdnew);
	                        mrd.setFunction(functionCode);
	                        mrd.setNarration(narration);
	                        mrd.setSubdivison(subdivisonNew);
	                        mrd.setReceiptnumbers(receiptNumbers);
	                        misRemittanceDetailService.create(mrd);
                        	}
                        	catch(Exception e)
                        	{
                        		e.printStackTrace();
                        	}
                        }
                    }
                }
            }
        
        return receiptBean;
    }
    
    @Transactional
    @Override
    public List<Receipt> createCashBankRemittance(List<ReceiptBean> receiptList, final String accountNumberId,
            final Date remittanceDate) {

        final Set<Receipt> bankRemittanceList = new HashSet<>();
        final SimpleDateFormat dateFomatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Set<String> paymentIdSet = null;
        InstrumentAccountCode accountCode = microserviceUtils
                .getInstrumentAccountGlCodeByType(CollectionConstants.INSTRUMENTTYPE_NAME_CASH);

        final String cashInHandQueryString = "SELECT COA.GLCODE FROM CHARTOFACCOUNTS COA WHERE COA.GLCODE = '"
                + accountCode.getGlcode()
                + "'";
        final Query cashInHand = persistenceService.getSession().createSQLQuery(cashInHandQueryString);

        String cashInHandGLCode = null;

        if (!cashInHand.list().isEmpty())
            cashInHandGLCode = cashInHand.list().get(0).toString();

        String createVoucher = "N";

        String functionCode = collectionsUtil.getAppConfigValue(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                CollectionConstants.APPCONFIG_VALUE_COLLECTION_BANKREMITTANCE_FUNCTIONCODE);
     // TODO : need to make this call to mdms
//        FinancialStatus instrumentStatusNew = microserviceUtils
//                .getInstrumentStatusByCode(CollectionConstants.INSTRUMENT_NEW_STATUS);

        Boolean showRemitDate = true;
        BigDecimal totalCashAmt = BigDecimal.ZERO;
        BigDecimal totalCashVoucherAmt = BigDecimal.ZERO;
        String fundCode = "";
        Date voucherDate = null;
        String description="";
        if (collectionsUtil
                .getAppConfigValue(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                        CollectionConstants.APPCONFIG_VALUE_COLLECTION_BANKREMITTANCE_SHOWREMITDATE)
                .equals(CollectionConstants.YES))
            showRemitDate = true;

        final Bankaccount depositedBankAccount = (Bankaccount) persistenceService.find("from Bankaccount where accountnumber=?",
                accountNumberId);
        final String serviceGlCode = depositedBankAccount.getChartofaccounts().getGlcode();
        List<Receipt> receipts;
        Set<Instrument> instruments;
        Map<String, Receipt> receiptMap = new HashMap<>();
        Map<String, Set<Instrument>> receiptInstrumentMap = new HashMap<>();
        final HashSet<String> receiptIds = new HashSet<>(0);
        List<BusinessService> businessServiceList = microserviceUtils.getBusinessService(null);
        Map<String, BusinessService> businessDetailsMap = new HashMap<>();
        for (BusinessService bd : businessServiceList) {
            businessDetailsMap.put(bd.getCode(), bd);
        }
        BusinessService businessDetails;
        InstrumentResponse instrumentResponse;
        List<Instrument> instrumentsList;
        for (ReceiptBean receipt : receiptList) {
            if (receipt.getSelected() != null && receipt.getSelected()) {
                if (receipt.getFund() != null && !receipt.getFund().isEmpty())
                {
                	if(receipt.getFund().equalsIgnoreCase("Municipal (General) Fund"))
                		fundCode="01";
                	else
                	fundCode = receipt.getFund();
                }
                if (showRemitDate && remittanceDate != null)
                    voucherDate = remittanceDate;
                else
                    try {
                        voucherDate = collectionsUtil.getRemittanceVoucherDate(dateFomatter.parse(receipt.getReceiptDate()));
                    } catch (final ParseException e) {
                        LOGGER.error("Error Parsing Date", e);
                    }
                if (receipt.getService() != null && receipt.getService().length() > 0) {
                    businessDetails = businessDetailsMap.get(receipt.getService());
                    // If Cash Amount is present
                    if (receipt.getInstrumentAmount() != null && cashInHandGLCode != null) {
                        switch (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) {
                        case "V2":
                        case "VERSION":
							System.out.println("PaymentStatusEnum.NEW.name() "+PaymentStatusEnum.NEW.name());
							System.out.println("service "+receipt.getService());
							System.out.println("fund "+receipt.getFund());
							System.out.println("department "+receipt.getDepartment());
							System.out.println("receiptDate "+receipt.getReceiptDate());
							System.out.println("receiptNumber "+receipt.getReceiptNumber());
							 receipts = null;/*microserviceUtils.getReceipts(PaymentStatusEnum.NEW.name(),
							 receipt.getService(), receipt.getFund(), receipt.getDepartment(),
							 receipt.getReceiptDate(),receipt.getReceiptNumber()); 
							 if (receipts != null) 
							 { 
								 paymentIdSet = new HashSet<>(); 
								 for (Receipt r : receipts) 
								 { 
									 receiptMap.put(r.getPaymentId(),r); 
									 receiptIds.add(r.getPaymentId());
									 paymentIdSet.add(r.getPaymentId());
								 }
								
							 }
		  */
							 
                            break;

                        default:
                            receipts = microserviceUtils.getReceipts(CollectionConstants.RECEIPT_STATUS_APPROVED,
                                    receipt.getService(),
                                    receipt.getFund(), receipt.getDepartment(), receipt.getReceiptDate());
                            if (receipts != null) {
                                for (Receipt r : receipts) {
                                    receiptMap.put(r.getBill().get(0).getBillDetails().get(0).getReceiptNumber(), r);
                                    receiptIds.add(r.getBill().get(0).getBillDetails().get(0).getId());
                                }
                            }
                            break;
                        }
///comment by abhishek on 14032021
						/*
						 * instrumentsList = microserviceUtils.getInstrumentsByReceiptIds(
						 * CollectionConstants.INSTRUMENTTYPE_NAME_CASH,
						 * CollectionConstants.INSTRUMENT_NEW_STATUS, StringUtils.join(receiptIds,
						 * ","));
						 */
                        totalCashAmt = totalCashAmt.add(receipt.getInstrumentAmount());
                        if (businessDetails.isVoucherCreationEnabled()) {
                            createVoucher = "Y";
                            totalCashVoucherAmt = totalCashVoucherAmt.add(receipt.getInstrumentAmount());
						} /*
							 * else { instrumentResponse =
							 * microserviceUtils.reconcileInstruments(instrumentsList, accountNumberId); }
							 */
                        //comment by Abhishek on 14032021 
                        /*for (Instrument i : instrumentsList) {
                            for (InstrumentVoucher iv : i.getInstrumentVouchers()) {
                                if(iv.getVoucherHeaderId() != null){
                                    if (receiptInstrumentMap.get(iv.getReceiptHeaderId()) != null) {
                                        instruments = new HashSet(receiptInstrumentMap.get(iv.getReceiptHeaderId()));
                                        instruments.add(i);
                                        receiptInstrumentMap.put(iv.getReceiptHeaderId(), instruments);
                                    } else {
                                        receiptInstrumentMap.put(iv.getReceiptHeaderId(), Collections.singleton(i));
                                    }
                                    if(paymentIdSet  != null){
                                        paymentIdSet.add(iv.getReceiptHeaderId());
                                    }
                                    bankRemittanceList.add(receiptMap.get(iv.getReceiptHeaderId()));
                                    List<CVoucherHeader> voucher = this.getVoucher(iv.getVoucherHeaderId());
                                    if(!voucher.isEmpty()){
                                        fundCode = voucher.get(0).getFundId().getCode();
                                        functionCode = voucher.get(0).getVouchermis().getFunction().getCode();
                                    }else{
                                        String validationMessage = "Voucher is not exist for receipt: "+receipt.getReceiptNumber()+", contact tosystem administrator.";
                                        throw new ValidationException(Arrays.asList(new ValidationError(validationMessage, validationMessage)));
                                    }
                                }
                            }
                        }
                        */

                    }
                }
            }
        }
        
        if (totalCashVoucherAmt.compareTo(totalCashAmt) != 0) {
            String validationMessage = "There is a difference of amount " + totalCashAmt.subtract(totalCashVoucherAmt)
                    + " between bank challan and the remittance voucher , please contact system administrator ";
            throw new ValidationException(Arrays.asList(new ValidationError(validationMessage, validationMessage)));
        }
        String subdivisonNew="";
        final Remittance remittance = populateAndPersistRemittance(totalCashAmt, BigDecimal.ZERO, fundCode,
                cashInHandGLCode, null, serviceGlCode, functionCode, bankRemittanceList, createVoucher,
                voucherDate, depositedBankAccount, totalCashVoucherAmt, BigDecimal.ZERO, Collections.EMPTY_LIST,
                receiptInstrumentMap,subdivisonNew);

        switch (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) {
        case "V2":
        case "VERSION2":
            if(!paymentIdSet.isEmpty()){
                microserviceUtils.performWorkflow(paymentIdSet,PaymentWorkflow.PaymentAction.REMIT, "Payment got remitted from finance");
            }
            break;

        default:
            for (final Receipt receiptHeader : bankRemittanceList) {
                receiptHeader.getBill().get(0).getBillDetails().get(0).setStatus("Remitted");
                receiptHeader.getInstrument().setTenantId(receiptHeader.getTenantId());
                receiptHeader.getBill().get(0).setPayerName(receiptHeader.getBill().get(0).getPaidBy());
                receiptHeader.setReceiptNumber(remittance.getReferenceNumber());
            }
            //ReceiptResponse response = microserviceUtils.updateReceipts(new ArrayList<>(bankRemittanceList));
            break;
        }
        
        List<Instrument> instrumentList = receiptInstrumentMap.values().stream().flatMap(Set::stream).collect(Collectors.toList());
        if(!instrumentList.isEmpty()){
            microserviceUtils.reconcileInstrumentsWithPayinSlipId(instrumentList, accountNumberId,remittance.getVoucherHeader().getVoucherNumber());            
        }
        receiptList.stream().forEach(receipt -> {
            receipt.setRemittanceReferenceNumber(remittance.getReferenceNumber());
        });
        return new ArrayList(bankRemittanceList);
    }

    @Transactional
    public CVoucherHeader createVoucherForRemittance(final String cashInHandGLCode, final String chequeInHandGLcode,
            final String serviceGLCode, final String functionCode, final BigDecimal totalCashVoucherAmt,
            final BigDecimal totalChequeVoucherAmt, final Date voucherDate, final String fundCode, final String narration, String subdivisonNew) {
        CVoucherHeader voucherHeader;
        final List<HashMap<String, Object>> accountCodeList = new ArrayList<>(0);
        HashMap<String, Object> accountcodedetailsHashMap;
        if (totalCashVoucherAmt.compareTo(BigDecimal.ZERO) > 0 && !cashInHandGLCode.isEmpty()) {
            accountcodedetailsHashMap = prepareAccountCodeDetails(cashInHandGLCode, functionCode, totalCashVoucherAmt,
                    BigDecimal.ZERO);
            accountCodeList.add(accountcodedetailsHashMap);
        }
        if (totalChequeVoucherAmt.compareTo(BigDecimal.ZERO) > 0 && !chequeInHandGLcode.isEmpty()) {
            accountcodedetailsHashMap = prepareAccountCodeDetails(chequeInHandGLcode, functionCode,
                    totalChequeVoucherAmt, BigDecimal.ZERO);
            accountCodeList.add(accountcodedetailsHashMap);
        }
        final BigDecimal totalDebitAmount = totalChequeVoucherAmt.add(totalCashVoucherAmt);
        if (!serviceGLCode.isEmpty()) {
            accountcodedetailsHashMap = prepareAccountCodeDetails(serviceGLCode, functionCode, BigDecimal.ZERO,
                    totalDebitAmount);
            accountCodeList.add(accountcodedetailsHashMap);
        }
        voucherHeader = financialsUtil.createRemittanceVoucher(prepareHeaderDetails(fundCode, functionCode, voucherDate,narration,null,subdivisonNew),
                accountCodeList, new ArrayList<HashMap<String, Object>>(0));
        return voucherHeader;
    }

    @Transactional
    public CVoucherHeader createVoucherForRemittanceNew(final String cashInHandGLCode, final String chequeInHandGLCode,
            final Map<String, BigDecimal> serviceGlCodes, final String functionCode, final List<BigDecimal> totalCashAmount,
            final List<BigDecimal> totalChequeAmount, final Date voucherDate, final String fundCode, final String narration, String deptIdnew,String subdivisonNew) {
        CVoucherHeader voucherHeader;
        final List<HashMap<String, Object>> accountCodeList = new ArrayList<>(0);
        HashMap<String, Object> accountcodedetailsHashMap;
        BigDecimal totalDebitAmount = new BigDecimal(0);
        if (!serviceGlCodes.isEmpty()) 
        {
        	Set<String> keys=serviceGlCodes.keySet();
        	for(String glCode:keys)
        	{
	        	if(!cashInHandGLCode.isEmpty())
	            {
	            	accountcodedetailsHashMap = prepareAccountCodeDetails(glCode, functionCode, BigDecimal.ZERO,serviceGlCodes.get(glCode));
	                    accountCodeList.add(accountcodedetailsHashMap);
	                    totalDebitAmount=totalDebitAmount.add(serviceGlCodes.get(glCode));
	            	
	            }
	        	else //if(!chequeInHandGLCode.isEmpty())
	            {
	            	accountcodedetailsHashMap = prepareAccountCodeDetails(glCode, functionCode, BigDecimal.ZERO, serviceGlCodes.get(glCode));
	                    accountCodeList.add(accountcodedetailsHashMap);
	                    totalDebitAmount=totalDebitAmount.add(serviceGlCodes.get(glCode));
	            }
        	}
        }
        if(!cashInHandGLCode.isEmpty())
        {
        		accountcodedetailsHashMap = prepareAccountCodeDetails(cashInHandGLCode, functionCode, totalDebitAmount,
                        BigDecimal.ZERO);
                accountCodeList.add(accountcodedetailsHashMap);
             
        }
        else //if(!chequeInHandGLCode.isEmpty())
        {
        	accountcodedetailsHashMap = prepareAccountCodeDetails(cashInHandGLCode, functionCode, totalDebitAmount,
                        BigDecimal.ZERO);
                accountCodeList.add(accountcodedetailsHashMap);
        }
		
       
        voucherHeader = financialsUtil.createRemittanceVoucher(prepareHeaderDetails(fundCode, functionCode, voucherDate,narration,deptIdnew,subdivisonNew),
                accountCodeList, new ArrayList<HashMap<String, Object>>(0));
        return voucherHeader;
    }
    
    @SuppressWarnings("unchecked")
    public List<ReceiptHeader> getRemittanceList(final ServiceDetails serviceDetails,
            final List<InstrumentHeader> instrumentHeaderList) {
        final List<Long> instHeaderList = new ArrayList<>();
        for (final InstrumentHeader instHead : instrumentHeaderList)
            instHeaderList.add(instHead.getId());
        final List<ReceiptHeader> bankRemittanceList = new ArrayList<>();
        final List<ReceiptHeader> receiptHeaders = persistenceService.findAllByNamedQuery(
                CollectionConstants.QUERY_RECEIPTS_BY_INSTRUMENTHEADER_AND_SERVICECODE, serviceDetails.getCode(),
                instHeaderList);
        bankRemittanceList.addAll(receiptHeaders);
        return bankRemittanceList;
    }

    
    @Transactional
    public Remittance populateAndPersistRemittanceNew(final List<BigDecimal> totalCashAmount, final List<BigDecimal> totalChequeAmount,
            final String fundCode, final String cashInHandGLCode, final String chequeInHandGLcode,
            final Map<String, BigDecimal> serviceGlCodes, final String functionCode, final ReceiptBean receiptBean,
            final String createVoucher,final String narration, final Date voucherDate, final List<Bankaccount> depositedBankAccount,
            final BigDecimal totalCashVoucherAmt, final BigDecimal totalChequeVoucherAmt, List<String> instrumentId,
            Map<String, Set<Instrument>> receiptInstrumentMap, String deptIdnew, String subdivisonNew) {
    	System.out.println("fundCode "+fundCode);
    	System.out.println("cashInHandGLCode "+cashInHandGLCode);
    	System.out.println("chequeInHandGLcode "+chequeInHandGLcode);
    	System.out.println("serviceGLCode "+serviceGlCodes);
    	System.out.println("functionCode "+functionCode);
    	
        CVoucherHeader voucherHeader;
        final CFinancialYear financialYear = collectionsUtil.getFinancialYearforDate(new Date());
        BigDecimal totalAmount = BigDecimal.ZERO;
        final Remittance remittance = new Remittance();
        final List<RemittanceDetail> remittanceDetailsList = new ArrayList<>();
        Boolean isChequeAmount = Boolean.FALSE;
        remittance.setReferenceDate(voucherDate);
        final EgwStatus receiptStatusApproved = collectionsUtil.getStatusForModuleAndCode(
                CollectionConstants.MODULE_NAME_REMITTANCE, CollectionConstants.REMITTANCE_STATUS_CODE_APPROVED);
        remittance.setStatus(receiptStatusApproved);
        remittance.setReferenceNumber(collectionsNumberGenerator.generateRemittanceNumber(financialYear));
        remittance.setFund(fundHibernateDAO.fundByCode(fundCode));
        remittance.setFunction(functionHibernateDAO.getFunctionByCode(functionCode));
        remittance.setCollectionRemittance(new HashSet<ReceiptHeader>());
        if(totalCashAmount!=null)
        {
        	for(BigDecimal amount:totalCashAmount)
        	{
        		if(totalCashAmount!=null)
        		{
        			if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && cashInHandGLCode != null) {
    		            remittanceDetailsList
    		                    .addAll(getRemittanceDetailsList(amount, BigDecimal.ZERO, cashInHandGLCode, remittance));
    		            totalAmount = totalAmount.add(amount);
    		        }
        		}
        	}
        }
        else
        {
        	for(BigDecimal amount:totalChequeAmount)
        	{
        		if(totalChequeAmount!=null)
        		{
    			if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && chequeInHandGLcode != null) {
		            remittanceDetailsList.addAll(
		                    getRemittanceDetailsList(amount, BigDecimal.ZERO, chequeInHandGLcode, remittance));
		            totalAmount = totalAmount.add(amount);
		            isChequeAmount = Boolean.TRUE;
		        }
        		}
        	}
        }
		
        remittanceDetailsList.addAll(getRemittanceDetailsListNew(BigDecimal.ZERO, totalAmount, serviceGlCodes, remittance));
        remittance.setRemittanceDetails(new HashSet<RemittanceDetail>(remittanceDetailsList));
        if (CollectionConstants.YES.equalsIgnoreCase(createVoucher)
                && (totalCashVoucherAmt.compareTo(BigDecimal.ZERO) > 0
                        || totalChequeVoucherAmt.compareTo(BigDecimal.ZERO) > 0)) {
            voucherHeader = createVoucherForRemittanceNew(cashInHandGLCode, chequeInHandGLcode, serviceGlCodes,
                    functionCode, totalCashAmount, totalChequeAmount, voucherDate, fundCode,narration,deptIdnew,subdivisonNew);
            remittance.setVoucherHeader(voucherHeader);
            remittance.setVoucherid(voucherHeader.getId());
            remittance.setReferenceVoucherNumber(voucherHeader.getVoucherNumber());
        }
        
        return remittance;
    }
    public void applyAuditing(AbstractAuditable auditable) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(ApplicationThreadLocals.getUserId());
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(ApplicationThreadLocals.getUserId());
		auditable.setLastModifiedDate(currentDate);
	}
    
    @Transactional
    public Remittance populateAndPersistRemittance(final BigDecimal totalCashAmount, final BigDecimal totalChequeAmount,
            final String fundCode, final String cashInHandGLCode, final String chequeInHandGLcode,
            final String serviceGLCode, final String functionCode, final Set<Receipt> receiptHeadList,
            final String createVoucher, final Date voucherDate, final Bankaccount depositedBankAccount,
            final BigDecimal totalCashVoucherAmt, final BigDecimal totalChequeVoucherAmt, List<String> instrumentId,
            Map<String, Set<Instrument>> receiptInstrumentMap,String subdivisonNew) {
    	System.out.println("fundCode "+fundCode);
    	System.out.println("cashInHandGLCode "+cashInHandGLCode);
    	System.out.println("chequeInHandGLcode "+chequeInHandGLcode);
    	System.out.println("serviceGLCode "+serviceGLCode);
    	System.out.println("functionCode "+functionCode);
    	
        CVoucherHeader voucherHeader;
        final CFinancialYear financialYear = collectionsUtil.getFinancialYearforDate(new Date());
        BigDecimal totalAmount = BigDecimal.ZERO;
        final Remittance remittance = new Remittance();
        final List<RemittanceDetail> remittanceDetailsList = new ArrayList<>();
        Boolean isChequeAmount = Boolean.FALSE;
        remittance.setReferenceDate(voucherDate);
        final EgwStatus receiptStatusApproved = collectionsUtil.getStatusForModuleAndCode(
                CollectionConstants.MODULE_NAME_REMITTANCE, CollectionConstants.REMITTANCE_STATUS_CODE_APPROVED);
        remittance.setStatus(receiptStatusApproved);
        remittance.setReferenceNumber(collectionsNumberGenerator.generateRemittanceNumber(financialYear));
        remittance.setFund(fundHibernateDAO.fundByCode(fundCode));
        remittance.setFunction(functionHibernateDAO.getFunctionByCode(functionCode));
        remittance.setCollectionRemittance(new HashSet<ReceiptHeader>());
        remittance.setBankAccount(depositedBankAccount);
        if (totalCashAmount != null && totalCashAmount.compareTo(BigDecimal.ZERO) > 0 && cashInHandGLCode != null) {
            remittanceDetailsList
                    .addAll(getRemittanceDetailsList(totalCashAmount, BigDecimal.ZERO, cashInHandGLCode, remittance));
            totalAmount = totalAmount.add(totalCashAmount);
        }
        if (totalChequeAmount != null && totalChequeAmount.compareTo(BigDecimal.ZERO) > 0
                && chequeInHandGLcode != null) {
            remittanceDetailsList.addAll(
                    getRemittanceDetailsList(totalChequeAmount, BigDecimal.ZERO, chequeInHandGLcode, remittance));
            totalAmount = totalAmount.add(totalChequeAmount);
            isChequeAmount = Boolean.TRUE;
        }
        remittanceDetailsList.addAll(getRemittanceDetailsList(BigDecimal.ZERO, totalAmount, serviceGLCode, remittance));
        remittance.setRemittanceDetails(new HashSet<RemittanceDetail>(remittanceDetailsList));
        HashSet<RemittanceInstrument> remittanceInstrumentSet = new HashSet<RemittanceInstrument>();
        if (CollectionConstants.YES.equalsIgnoreCase(createVoucher)
                && (totalCashVoucherAmt.compareTo(BigDecimal.ZERO) > 0
                        || totalChequeVoucherAmt.compareTo(BigDecimal.ZERO) > 0)) {
            voucherHeader = createVoucherForRemittance(cashInHandGLCode, chequeInHandGLcode, serviceGLCode,
                    functionCode, totalCashVoucherAmt, totalChequeVoucherAmt, voucherDate, fundCode,null,subdivisonNew);
            remittance.setVoucherHeader(voucherHeader);
			/*
			 * for (Receipt receiptHeader : receiptHeadList) { Set<Instrument> instSet =
			 * Collections.EMPTY_SET; switch
			 * (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) { case "V2":
			 * case "VERSION2": //instSet =
			 * receiptInstrumentMap.get(receiptHeader.getPaymentId()); break;
			 * 
			 * default: //instSet =
			 * receiptInstrumentMap.get(receiptHeader.getBill().get(0).getBillDetails().get(
			 * 0).getReceiptNumber()); break; } for (Instrument instHead : instSet) { if
			 * (!isChequeAmount || (isChequeAmount &&
			 * instrumentId.contains(instHead.getId().toString()))) { RemittanceInstrument
			 * ri = prepareRemittanceInstrument(remittance, instHead); if (isChequeAmount) {
			 * ri.setReconciled(Boolean.TRUE); } remittanceInstrumentSet.add(ri); }
			 * 
			 * } }
			 */
            remittance.setRemittanceInstruments(remittanceInstrumentSet);
            //RemittanceResponse response = create(remittance, receiptHeadList);
        }
        return remittance;
    }

    private RemittanceResponse create(Remittance remittance, final Set<Receipt> receiptHeadList) {
        org.egov.infra.microservice.models.Remittance r = new org.egov.infra.microservice.models.Remittance();
        r.setBankaccount(remittance.getBankAccount().getAccountnumber());
        r.setFunction(remittance.getFunction().getCode());
        r.setFund(remittance.getFund().getCode());
        r.setReasonForDelay(remittance.getReasonForDelay());
        r.setReferenceNumber(remittance.getReferenceNumber());
        r.setStatus("Approved");
        r.setReferenceDate(remittance.getReferenceDate().getTime());
        r.setRemarks(remittance.getRemarks());
        r.setTenantId(microserviceUtils.getTenentId());
        r.setVoucherHeader(remittance.getVoucherHeader().getVoucherNumber());
        r.setRemittanceDetails(new HashSet<>());
        r.setRemittanceInstruments(new HashSet<>());
        r.setRemittanceReceipts(new HashSet<>());
        org.egov.infra.microservice.models.RemittanceDetail rd;
        for (RemittanceDetail detail : remittance.getRemittanceDetails()) {
            rd = new org.egov.infra.microservice.models.RemittanceDetail();
            rd.setChartOfAccount(detail.getChartOfAccount().getGlcode());
            rd.setCreditAmount(detail.getCreditAmount());
            rd.setDebitAmount(detail.getDebitAmount());
            rd.setTenantId(microserviceUtils.getTenentId());
            r.getRemittanceDetails().add(rd);
        }
        org.egov.infra.microservice.models.RemittanceInstrument ri;
        for (RemittanceInstrument instrument : remittance.getRemittanceInstruments()) {
            ri = new org.egov.infra.microservice.models.RemittanceInstrument();
            ri.setInstrument(instrument.getInstrument().getId());
            ri.setReconciled(instrument.getReconciled());
            ri.setTenantId(microserviceUtils.getTenentId());
            r.getRemittanceInstruments().add(ri);
        }
        RemittanceReceipt rr;
        for (Receipt receipt : receiptHeadList) {
            rr = new RemittanceReceipt();
            rr.setReceipt(receipt.getBill().get(0).getBillDetails().get(0).getId());
            rr.setTenantId(microserviceUtils.getTenentId());
            r.getRemittanceReceipts().add(rr);
        }
        return microserviceUtils.createRemittance(Collections.singletonList(r));
    }

    private RemittanceInstrument prepareRemittanceInstrument(Remittance remittance, Instrument instrumentHeader) {
        RemittanceInstrument remittanceInstrument = new RemittanceInstrument();
        remittanceInstrument.setRemittance(remittance);
        remittanceInstrument.setInstrument(instrumentHeader);
        remittanceInstrument.setReconciled(Boolean.FALSE);
        return remittanceInstrument;
    }

    public HashMap<String, Object> prepareAccountCodeDetails(final String glCode, final String functionCode,
            final BigDecimal creditAmount, final BigDecimal debitAmount) {
        final HashMap<String, Object> accountcodedetailsHashMap = new HashMap<>(0);
        accountcodedetailsHashMap.put(VoucherConstant.GLCODE, glCode);
        accountcodedetailsHashMap.put(VoucherConstant.FUNCTIONCODE, functionCode);
        accountcodedetailsHashMap.put(VoucherConstant.CREDITAMOUNT, creditAmount);
        accountcodedetailsHashMap.put(VoucherConstant.DEBITAMOUNT, debitAmount);
        return accountcodedetailsHashMap;
    }
    
    public HashMap<String, Object> prepareAccountCodeDetailsNew(final Map<String, String> serviceGlCodes, final String functionCode,
            final BigDecimal creditAmount, final BigDecimal debitAmount) {
        final HashMap<String, Object> accountcodedetailsHashMap = new HashMap<>(0);
        Set<String> keys=serviceGlCodes.keySet();
        for(String key : keys)
        { 
        	accountcodedetailsHashMap.put(VoucherConstant.GLCODE, key);
        	accountcodedetailsHashMap.put(VoucherConstant.FUNCTIONCODE, functionCode);
        	accountcodedetailsHashMap.put(VoucherConstant.CREDITAMOUNT, creditAmount);
        	accountcodedetailsHashMap.put(VoucherConstant.DEBITAMOUNT, debitAmount);
        }
        return accountcodedetailsHashMap;
    }

    public HashMap<String, Object> prepareHeaderDetails(final String fundCode, final String functionCode,
            final Date voucherDate,final String narration, String deptIdnew,String subdivisonNew) {
        final HashMap<String, Object> headerdetails = new HashMap<>(0);

        //final String deptCode = departmentService.getDepartmentByCode(deptIdnew).getCode();

        if (collectionsUtil.getVoucherType()) {
            headerdetails.put(VoucherConstant.VOUCHERNAME, CollectionConstants.FINANCIAL_RECEIPTS_VOUCHERNAME);
            headerdetails.put(VoucherConstant.VOUCHERTYPE, CollectionConstants.FINANCIAL_RECEIPTS_VOUCHERTYPE);
        } else {
            headerdetails.put(VoucherConstant.VOUCHERNAME, CollectionConstants.FINANCIAL_CONTRATVOUCHER_VOUCHERNAME);
            headerdetails.put(VoucherConstant.VOUCHERTYPE, CollectionConstants.FINANCIAL_CONTRAVOUCHER_VOUCHERTYPE);
        }
        //headerdetails.put(VoucherConstant.DESCRIPTION, CollectionConstants.FINANCIAL_VOUCHERDESCRIPTION);
        headerdetails.put(VoucherConstant.DESCRIPTION, narration);
        headerdetails.put(VoucherConstant.VOUCHERDATE, voucherDate);
        headerdetails.put(VoucherConstant.FUNDCODE, fundCode);
        headerdetails.put(VoucherConstant.DEPARTMENTCODE, deptIdnew);
        headerdetails.put(VoucherConstant.FUNCTIONCODE, functionCode);
        headerdetails.put(VoucherConstant.SUBDIVISON, subdivisonNew);
        return headerdetails;
    }

    public List<RemittanceDetail> getRemittanceDetailsList(final BigDecimal creditAmount, final BigDecimal debitAmount,
            final String glCode, final Remittance remittance) {
        final List<RemittanceDetail> remittanceDetailsList = new ArrayList<>();
        final RemittanceDetail remittanceDetail = new RemittanceDetail();
        remittanceDetail.setCreditAmount(creditAmount);
        remittanceDetail.setDebitAmount(debitAmount);
        remittanceDetail.setRemittance(remittance);
        remittanceDetail.setChartOfAccount(chartOfAccountsDAO.getCChartOfAccountsByGlCode(glCode));
        remittanceDetailsList.add(remittanceDetail);
        return remittanceDetailsList;
    }
    
    public List<RemittanceDetail> getRemittanceDetailsListNew(final BigDecimal creditAmount, final BigDecimal debitAmount,
            final Map<String, BigDecimal> serviceGlCodes, final Remittance remittance) {
        final List<RemittanceDetail> remittanceDetailsList = new ArrayList<>();
        Set<String> keys=serviceGlCodes.keySet();
        for(String key : keys)
        {
	        final RemittanceDetail remittanceDetail = new RemittanceDetail();
	        remittanceDetail.setCreditAmount(creditAmount);
	        remittanceDetail.setDebitAmount(debitAmount);
	        remittanceDetail.setRemittance(remittance);
	        remittanceDetail.setChartOfAccount(chartOfAccountsDAO.getCChartOfAccountsByGlCode(serviceGlCodes.get(key).toString()));
	        remittanceDetailsList.add(remittanceDetail);
        }
        return remittanceDetailsList;
    }

    /**
     * Method to find all the Cash instruments with status as :new and
     *
     * @return List of HashMap
     */
	/*
	 * @Override public List<ReceiptBean>
	 * findCashRemittanceDetailsForServiceAndFund(final String boundaryIdList, final
	 * String serviceCodes, final String fundCodes, final Date startDate, final Date
	 * endDate, String serviceTypeId) { // TODO : need to make this call to mdms //
	 * FinancialStatus status =
	 * microserviceUtils.getInstrumentStatusByCode(CollectionConstants.
	 * INSTRUMENT_NEW_STATUS); List<Instrument> instruments =
	 * microserviceUtils.getInstruments(CollectionConstants.
	 * INSTRUMENTTYPE_NAME_CASH, TransactionType.Debit,
	 * CollectionConstants.INSTRUMENT_NEW_STATUS); List<String> receiptIds = new
	 * ArrayList<>(); for (Instrument i : instruments) {
	 * 
	 * //added for debug Instrument
	 * if(i.getInstrumentType().getName()=="Cash"||i.getInstrumentType().getName()==
	 * "CASH") {
	 * System.out.println("INstrument Type Description>>>>"+i.getInstrumentType().
	 * getDescription());
	 * System.out.println("INstrument Type Name>>>>"+i.getInstrumentType().getName()
	 * ); }
	 * 
	 * if (i.getInstrumentVouchers() != null) for (InstrumentVoucher iv :
	 * i.getInstrumentVouchers()) { receiptIds.add(iv.getReceiptHeaderId()); } }
	 * List<ReceiptBean> resultList = new ArrayList<>(); if(!receiptIds.isEmpty()){
	 * List<Receipt> receipts = Collections.EMPTY_LIST; switch
	 * (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) { case "V2":
	 * case "VERSION2": receipts =
	 * microserviceUtils.getReceipts(StringUtils.join(receiptIds, ","),
	 * PaymentStatusEnum.NEW.name(), serviceCodes,startDate, endDate,
	 * serviceTypeId); break;
	 * 
	 * default: receipts =
	 * microserviceUtils.getReceipts(StringUtils.join(receiptIds, ","),
	 * CollectionConstants.RECEIPT_STATUS_APPROVED, serviceCodes,startDate,
	 * endDate,serviceTypeId); break; } Map<String, List<Receipt>>
	 * receiptDateWiseMap = new HashMap<>(); Map<String, List<Receipt>>
	 * serviceWiseMap = new HashMap<>(); Map<String, List<Receipt>>
	 * instrumentWiseMap = new HashMap<>(); Map<String, List<Receipt>> fundWiseMap =
	 * new HashMap<>(); Map<String, List<Receipt>> departmentWiseMap = new
	 * HashMap<>();
	 * 
	 * groupByReceiptDate(receiptDateWiseMap, receipts);
	 * 
	 * for (String key : receiptDateWiseMap.keySet()) { List<Receipt> tempList =
	 * receiptDateWiseMap.get(key); groupByService(key, serviceWiseMap, tempList); }
	 * 
	 * for (String key : serviceWiseMap.keySet()) { List<Receipt> tempList =
	 * serviceWiseMap.get(key); groupByInstrument(key, instrumentWiseMap, tempList);
	 * }
	 * 
	 * for (String key : instrumentWiseMap.keySet()) { List<Receipt> tempList =
	 * instrumentWiseMap.get(key); groupByFund(key, fundWiseMap, tempList); }
	 * 
	 * for (String key : fundWiseMap.keySet()) { List<Receipt> tempList =
	 * fundWiseMap.get(key); groupByDepartment(key, departmentWiseMap, tempList); }
	 * 
	 * for (String key : departmentWiseMap.keySet()) { List<Receipt> tempList =
	 * departmentWiseMap.get(key); populateResultList(key, resultList, tempList); }
	 * 
	 * populateNames(resultList); } return resultList; }
	 */
    
    @Override
    public List<ReceiptBean> findCashRemittanceDetailsForServiceAndFund(String classification, Date fromDate, Date toDate, String businessCode,
            String receiptNo,String type) {
    	List<ReceiptBean> resultList = new ArrayList<>();
    	   List<ReceiptHeader> receiptList = new ArrayList<>();
    	 List<Receipt> receipts  = microserviceUtils.searchRecieptsFin(classification, fromDate, toDate, businessCode, null, type);
    	 System.out.println("Inside METHOD  after >>"+receipts);
    	int i=1;
    	 for (Receipt receipt : receipts) {

             for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                 for (BillDetail billDetail : bill.getBillDetails()) {
                	 ReceiptBean receiptHeader = new ReceiptBean();
       
                	 receiptHeader.setInstrumentId(receipt.getInstrument().getInstrumentType().getId());
                 	 receiptHeader.setInstrumentAmount(billDetail.getTotalAmount());
                     receiptHeader.setInstrumentNumber(receipt.getInstrument().getInstrumentNumber());
                     receiptHeader.setInstrumentType(receipt.getInstrument().getInstrumentType().getName());
                      if(receipt.getInstrument().getTransactionDate() !=null)
                    	  receiptHeader.setInstrumentDate(DateUtils.toDefaultDateFormat(receipt.getInstrument().getTransactionDate()));
                            
                      receiptHeader.setBankBranch(receipt.getInstrument().getBranchName());
                     org.egov.infra.microservice.models.Bank bank = receipt.getInstrument().getBank();
                     receiptHeader.setBank(bank != null ? bank.getName() : "");
                     receiptHeader.setReceiptId(receipt.getReceiptNumber());	
                     receiptHeader.setReceiptNumber(receipt.getReceiptNumber());
                     
                     receiptHeader.setReceiptId(receipt.getPaymentId());
                     receiptHeader.setReceiptNumber(billDetail.getReceiptNumber());
                    String receiptDate = DateUtils
                             .toDefaultDateFormat(new Date(receipt.getBill().get(0).getBillDetails().get(0).getReceiptDate()));
                    receiptHeader.setReceiptDate(receiptDate);

                    receiptHeader.setService(billDetail.getBusinessService());
                    receiptHeader.setRemittanceReferenceNumber(billDetail.getBillNumber());
                    //receiptHeader.setFund(billDetail.getFund());
                    receiptHeader.setFund(billDetail.getFund()!=null ? billDetail.getFund() :"Municipal (General) Fund");//abhishek
                    receiptHeader.setDepartment(billDetail.getDepartment());

                     JsonNode jsonNode = billDetail.getAdditionalDetails();
                     BillDetailAdditional additional = null;
//                     try {
//                         if (null != jsonNode)
//                             additional = (BillDetailAdditional) new ObjectMapper().readValue(jsonNode.toString(),
//                                     BillDetailAdditional.class);
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     }
                     if((receipt.getInstrument().getInstrumentType().getName())=="CASH") {
                     resultList.add(receiptHeader);
        }
                     

                 }
        }

        }
//////////////////////////////abhishek
    	 Map<String, List<Receipt>> receiptDateWiseMap = new HashMap<>();	
         Map<String, List<Receipt>> serviceWiseMap = new HashMap<>();	
         Map<String, List<Receipt>> instrumentWiseMap = new HashMap<>();	
         Map<String, List<Receipt>> fundWiseMap = new HashMap<>();	
         Map<String, List<Receipt>> departmentWiseMap = new HashMap<>();	
         LOGGER.info("after request response processing");	
         groupByReceiptDate(receiptDateWiseMap, receipts);	
         LOGGER.info("after group by receipt");	
         for (String key : receiptDateWiseMap.keySet()) {	
             List<Receipt> tempList = receiptDateWiseMap.get(key);	
             groupByService(key, serviceWiseMap, tempList);	
         }	
         LOGGER.info("after group by service");	
         for (String key : serviceWiseMap.keySet()) {	
             List<Receipt> tempList = serviceWiseMap.get(key);	
             groupByInstrument(key, instrumentWiseMap, tempList);	
         }	
         LOGGER.info("after group by instrument");	
         for (String key : instrumentWiseMap.keySet()) {	
             List<Receipt> tempList = instrumentWiseMap.get(key);	
             groupByFund(key, fundWiseMap, tempList);	
         }	
         LOGGER.info("after group by fund");	
         for (String key : fundWiseMap.keySet()) {	
             List<Receipt> tempList = fundWiseMap.get(key);	
             groupByDepartment(key, departmentWiseMap, tempList);	
         }	
         LOGGER.info("after group by dept");	
         for (String key : departmentWiseMap.keySet()) {	
             List<Receipt> tempList = departmentWiseMap.get(key);	
             populateResultList(key, resultList, tempList);	
         }	
         LOGGER.info("after group by resultlist");	
         populateNames(resultList);	
         LOGGER.info("after group by populateNames");	
         
/////////////////////    	 

       // if(!receiptIds.isEmpty()){
        	
			/*
			 * List<Receipt> receipts = Collections.EMPTY_LIST; switch
			 * (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) { case "V2":
			 * case "VERSION2": receipts =
			 * microserviceUtils.getReceipts(StringUtils.join(receiptIds, ","),
			 * PaymentStatusEnum.NEW.name(), serviceCodes,startDate, endDate,
			 * serviceTypeId); break;
			 * 
			 * default: receipts =
			 * microserviceUtils.getReceipts(StringUtils.join(receiptIds, ","),
			 * CollectionConstants.RECEIPT_STATUS_APPROVED, serviceCodes,startDate,
			 * endDate,serviceTypeId); break; }
			 */
         
        return resultList;
    }
    
    public List<ReceiptBean> findCashRemittanceDetailsForServiceAndFund(String classification, Date fromDate, Date toDate, String businessCode,
            String receiptNo,String department,String type, String searchAmount, String subDivision,String collectedBy) {
    	List<ReceiptBean> resultList = new ArrayList<>();
    	List<ReceiptHeader> receiptList = new ArrayList<>();
    	boolean amountcheck=false;
    	boolean deptcheck=false;
    	boolean subdivisioncheck=false; 
    	boolean receiptcheck=false;
    	boolean collectedcheck=false;
    	final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> list1= null;
    	SQLQuery queryMain =  null;
    	query1
        .append("select v2.reciept_number, v2.departmentcode, fnc.code as fnccode from vouchermis v2,\"function\" fnc where v2.reciept_number notnull and fnc.id  = v2.functionid");
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list1 = queryMain.list();
    	System.out.println(":::list size::::: "+list1.size());	
    	Map<String, String> deptMap = new HashMap<>();
    	Map<String,String> funMap=new HashMap<>();
    	if(list1!=null)
    	{
    		for (final Object[] object : list1)
    		{
    			deptMap.put(object[0].toString(), object[1].toString());
    			funMap.put(object[0].toString(), object[2].toString());
    		}
    	}
    	
    	 List<Receipt> receipts  = null;//microserviceUtils.searchRecieptsFinNew(classification, fromDate, toDate, businessCode, null, type);
    	 System.out.println("Inside METHOD  after >>"+receipts);
    	 int i=1;
    	 for (Receipt receipt : receipts) {
/*
	  
             for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                 for (BillDetail billDetail : bill.getBillDetails()) {
                	 ReceiptBean receiptBean = new ReceiptBean();
       
                	 receiptBean.setInstrumentId(receipt.getInstrument().getInstrumentType().getId());
                 	 receiptBean.setInstrumentAmount(billDetail.getTotalAmount());
                     receiptBean.setInstrumentNumber(receipt.getInstrument().getInstrumentNumber());
                     receiptBean.setInstrumentType(receipt.getInstrument().getInstrumentType().getName());
                      if(receipt.getInstrument().getTransactionDate() !=null)
                    	  receiptBean.setInstrumentDate(DateUtils.toDefaultDateFormat(receipt.getInstrument().getTransactionDate()));
                            
	  
                      receiptBean.setBankBranch(receipt.getInstrument().getBranchName());
                     org.egov.infra.microservice.models.Bank bank = receipt.getInstrument().getBank();
                     receiptBean.setBank(bank != null ? bank.getName() : "");
                     //receiptBean.setReceiptId(receipt.getReceiptNumber());	
                     receiptBean.setReceiptNumber(receipt.getReceiptNumber());
                     if(funMap.containsKey(receipt.getReceiptNumber()))
                     {
                    	 receiptBean.setFunctionCode(funMap.get(receipt.getReceiptNumber()));
                     }
                     else {
                    	 receiptBean.setFunctionCode(null);
                     }
                     receiptBean.setReceiptId(receipt.getPaymentId());
                     //receiptBean.setReceiptNumber(billDetail.getReceiptNumber());
                    String receiptDate = DateUtils
                             .toDefaultDateFormat(new Date(receipt.getBill().get(0).getBillDetails().get(0).getReceiptDate()));
																			   
                    receiptBean.setReceiptDate(receiptDate);
                    receiptBean.setService(microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
										
                    receiptBean.setRemittanceReferenceNumber(billDetail.getBillNumber());
                    //receiptBean.setFund(billDetail.getFund());
                    receiptBean.setFund(billDetail.getFund()!=null ? billDetail.getFund() :"Municipal (General) Fund");//abhishek
											 
                    if(deptMap.containsKey(receipt.getReceiptNumber()))
	    			{
                    	receiptBean.setDepartment(deptMap.get(receipt.getReceiptNumber()));
	    			}
                    else {
                    	receiptBean.setDepartment(billDetail.getDepartment());
                    }
                    System.out.println("dept "+receiptBean.getDepartment());
                    receiptBean.setSubDivision(receipt.getSubdivison());
                    receiptBean.setCreatedUser(receipt.getCollectedByName());
                     JsonNode jsonNode = billDetail.getAdditionalDetails();
                     BillDetailAdditional additional = null;
                     
                     BigDecimal searchamt = new BigDecimal(0);
	                 	if(searchAmount!=null && !searchAmount.equalsIgnoreCase("") && !searchAmount.equalsIgnoreCase("0")) {
	                 		searchamt=new BigDecimal(Integer.parseInt(searchAmount));
	                 		amountcheck=true;
	                 	}
	                 	if(!subDivision.equalsIgnoreCase("-1"))
	                 	{
	                 		subdivisioncheck=true;
	                 	}
	                 	if(!department.equalsIgnoreCase("-1"))
	                 	{
	                 		deptcheck=true;
	                 	}
	                 	if(receiptNo!=null && !receiptNo.equalsIgnoreCase("")) {
	                		receiptcheck=true;
	                	}
	                 	if(collectedBy!=null && !collectedBy.equalsIgnoreCase("")) {
	                		collectedcheck=true;
	                	}
                    if((receipt.getInstrument().getInstrumentType().getName()).equalsIgnoreCase("CASH")&&
                    		receipt.getPaymentStatus().equalsIgnoreCase("NEW")) 
                    {	
                    	if(amountcheck)
                    	{
                    		if(searchamt.compareTo(receipt.getInstrument().getAmount())==0) {}
                    		else
                    			continue;
                    	}
                    	if(subdivisioncheck)
                    	{
                    		if(subDivision.equalsIgnoreCase(receipt.getSubdivison())) {}
                    		else
                    			continue;
                    	}
                    	 if(deptcheck)
                    	{
                    		if(department.equalsIgnoreCase(receiptBean.getDepartment())) {}
                    		else
                    			continue;
                    	}
                    	if(receiptcheck)
                    	{
                    		if(receiptNo.equalsIgnoreCase(receipt.getReceiptNumber())) {}
                    		else
                    			continue;
                    	}
                    	if(collectedcheck)
                    	{
                    		if(collectedBy.equalsIgnoreCase(receiptBean.getCreatedUser())) {}
                    		else
                    			continue;
                    	}
                    	resultList.add(receiptBean);
                    }
                 }
             }
        }*/
    	 populateNamesForCash(resultList);
    	 }
        return resultList;
    }
    
    public List<ReceiptBean> findCashRemittanceDetailsForServiceAndFundNew(String classification, Date fromDate, Date toDate, String businessCode,
            String receiptNo,String department,String type, String searchAmount, String subDivision,String collectedBy,String mType) {
    	this.getServiceCategoryList();
    	boolean deptcheck=false;
    	List<ReceiptBean> resultList = new ArrayList<>();
    	List<Object[]> result= new ArrayList();
    	List<Object[]> misresult= new ArrayList();
    	SQLQuery misquery=null;
    	StringBuffer query1=new StringBuffer("select v2.reciept_number,fnc.code,v2.departmentcode,ed.\"name\" as departmentname from eg_department ed,\"function\" fnc,vouchermis v2 where v2.reciept_number notnull and fnc.id =v2.functionid and ed.code =v2.departmentcode");
    	misquery=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	misresult = misquery.list();
	    	System.out.println(":::misresult size::::: "+misresult.size());
	    	Map<String, String> deptMap = new HashMap<>();
	    	Map<String,String> funcMap=new HashMap<>();
	    	Map<String, String> deptNameMap = new HashMap<>();
	    	if(misresult!=null)
	    	{
	    		for (final Object[] object : misresult)
	    		{
	    			funcMap.put(object[0].toString(), object[1].toString());
	    			deptMap.put(object[0].toString(), object[2].toString());
	    			deptNameMap.put(object[0].toString(), object[3].toString());
	    		}
	    	}
	    	
    	SQLQuery searchQuery =  null;
    	StringBuffer query = new StringBuffer("select distinct mrd.payments_id, " + 
    			" mrd.receipt_number, to_char(mrd.receipt_date,'dd/mm/yyyy'), mrd.subdivison, mrd.servicename, " + 
    			" mrd.collectedbyname," + 
    			" ('01') as fundcode, ('Municipal (General) Fund') as fundname, "+ 
    			" mrd.total_amt_paid,mrd.payment_mode,mrd.id  from mis_receipts_details mrd "+
  				" where mrd.payment_status = 'NEW'");
    	
     		 BigDecimal searchamt = new BigDecimal(0);
			query.append(getDateQuery(fromDate, toDate));									
           	if(searchAmount!=null && !searchAmount.equalsIgnoreCase("") && !searchAmount.equalsIgnoreCase("0")) {
           		searchamt=new BigDecimal(Integer.parseInt(searchAmount));
           		query.append(" and mrd.total_amt_paid = "+ searchamt);
           	}
           	if(!subDivision.equalsIgnoreCase("-1")){
           		query.append(" and mrd.subdivison = '"+ subDivision+"'");
           	}		
           	if (null != receiptNo && !receiptNo.equalsIgnoreCase("")) {
           		query.append(" and mrd.receipt_number = '"+ receiptNo+"'");
           	}		
           	if(collectedBy!=null && !collectedBy.equalsIgnoreCase("")) {
           		query.append(" and lower(mrd.collectedbyname) like ('%"+ collectedBy.toLowerCase()+"%')");
           	}	 	
           	if(businessCode!=null && !businessCode.equalsIgnoreCase("-1")) {
           		query.append(" and mrd.servicename = '" + businessCode+"'");
				}
           	if(mType.equalsIgnoreCase("Cash"))
           	{
           		query.append(" and mrd.payment_mode = 'CASH' ");
           	}
           	else
           	{
           		query.append(" and mrd.payment_mode in ('CHEQUE','DD') ");														   
           	}
     	 
 	    	System.out.println("Search Query "+query);
 	    	try {
 	    	searchQuery=this.persistenceService.getSession().createSQLQuery(query.toString());
 	    	result = searchQuery.list();
 	    	System.out.println(":::list size::::: "+result.size());	
 	    	if(!department.equalsIgnoreCase("-1"))
         	{
         		deptcheck=true;
         	}
 	    	if(result!=null)
 	    	{
 	    		int i=1;
 	    		for (final Object[] object : result)
 	    		{
 	    			ReceiptBean receiptBean = new ReceiptBean();
 	    			receiptBean.setReceiptId((object[10]!=null)?object[10].toString():"");
 	    			receiptBean.setReceiptNumber((object[1]!=null)?object[1].toString():"");
 	    		 	receiptBean.setReceiptDate((object[2]!=null)?object[2].toString():"");
 	    		 	receiptBean.setSubDivision((object[3]!=null)?object[3].toString():"");
 	    		 	if(object[4]!= null) {
 						String s3 = null;
 						String s4 = null;
 						String s1 = null;
 						String s2 = null;
 						String service=null;
 						String[] split = object[4].toString().split(Pattern.quote("."));
 						s1 = split[0];
 						if (split.length == 2) {
 							s2 = split[1];
 						}
 						if (serviceCategoryNames.containsKey(s1)) {
 							s3 = serviceCategoryNames.get(s1);
 						}
 						if (serviceCategoryNames.containsKey(s2)) {
 							s4 = serviceCategoryNames.get(s2);
 						} 						
 						if (s4 != null) {
 							if (s3 != null) {
 								service = s3 + "." + s4;
 							} else {
 								service = s4;
 							}
 						} else {
 							service = s3;
 						}
 						receiptBean.setServiceName((service!=null)?service:"");
 					}
 	    		 	
 	    		 	receiptBean.setService((object[4]!=null)?object[4].toString():"-1");
 	    		 	receiptBean.setCreatedUser((object[5]!=null)?object[5].toString():"");
 	    		 	if(object[1]!=null)
 	    		 	{
 	    		 		if(deptMap.containsKey(object[1].toString())){
 	    		 			LOGGER.info("departmentCode "+deptMap.get(object[1].toString()));
 	    		 			receiptBean.setDepartment(deptMap.get(object[1].toString()));
 	    		 		}
 	    		 	}
 	    		 	else {
 	    		 		receiptBean.setDepartment("");
 	    		 	}
 	    		 	if(object[1]!=null)
 	    		 	{
 	    		 		if(deptNameMap.containsKey(object[1].toString())){
 	    		 			LOGGER.info("departmentName "+deptNameMap.get(object[1].toString()));
 	    		 			receiptBean.setDepartmentName(deptNameMap.get(object[1].toString()));
 	    		 		}
 	    		 	}
 	    		 	else {
 	    		 		receiptBean.setDepartmentName("");
 	    		 	}
 	    		 	if(object[1]!=null)
 	    		 	{
 	    		 		if(funcMap.containsKey(object[1].toString())){
 	    		 			receiptBean.setFunctionCode(funcMap.get(object[1].toString()));
 	    		 		}
 	    		 	}
 	    		 	else {
 	    		 		receiptBean.setFunctionCode("");
 	    		 	}
 	    		 	receiptBean.setFund((object[6]!=null)?object[6].toString():"-1");
 	    		 	receiptBean.setFundName((object[7]!=null)?object[7].toString():"");
 	    		 	//receiptBean.setRemittanceReferenceNumber((object[11]!=null)?object[11].toString():"");
 	    			receiptBean.setInstrumentAmount((object[8]!=null)?(new BigDecimal(object[8].toString())):new BigDecimal(0));
 	    			receiptBean.setInstrumentType((object[9]!=null)?object[9].toString():"");
 	    			if(deptcheck)
                	{
                		if(department.equalsIgnoreCase(receiptBean.getDepartment())) {}
                		else
                			continue;
                	}
 	    		 	resultList.add(receiptBean);
 	    		}
 	    	}
 	    	//populateNamesForCash(resultList);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        return resultList;
    }

    private void populateNames(List<ReceiptBean> resultList) {
        List<Fund> fundList = fundHibernateDAO.findAllActiveFunds();
        List<Department> departmentList = microserviceUtils.getDepartments();
        List<BusinessService> businessServiceList = microserviceUtils.getBusinessService(null);
        Map<String, String> fundCodeNameMap = new HashMap<>();
        Map<String, String> deptCodeNameMap = new HashMap<>();
        Map<String, String> businessDetailsCodeNameMap = new HashMap<>();

        if (fundList != null)
            for (Fund f : fundList) {
                fundCodeNameMap.put(f.getCode(), f.getName());
            }

        if (departmentList != null)
            for (Department dept : departmentList) {
                deptCodeNameMap.put(dept.getCode(), dept.getName());
            }

        if (businessServiceList != null)
            for (BusinessService bd : businessServiceList) {
                businessDetailsCodeNameMap.put(bd.getCode(), bd.getBusinessService());
            }
        Set<String> bsCodes = new HashSet<>();
        for(ReceiptBean rb : resultList){
            bsCodes.add(rb.getService());
        }
        BusinessServiceCriteria criteria = new BusinessServiceCriteria();
        criteria.setCode(StringUtils.join(bsCodes,','));
        criteria.setVoucherCreationEnabled(true);
        List<BusinessServiceMapping> businessServiceMappingList = microserviceUtils.getBusinessServiceMappingBySearchCriteria(criteria );
        Map<String,BusinessServiceMapping> bsServiceMapping = new HashMap<>();
        businessServiceMappingList.stream().forEach(bsm -> {
            bsServiceMapping.put(bsm.getCode(), bsm);
        });

        for (ReceiptBean rb : resultList) {
            String serviceCode = rb.getService();
            if (serviceCode != null && !serviceCode.isEmpty()){
                rb.setServiceName(businessDetailsCodeNameMap.get(serviceCode));
                BusinessServiceMapping serviceMapping = bsServiceMapping.get(serviceCode);
				
                if(StringUtils.isNoneBlank(serviceMapping.getDepartment())){
                    rb.setDepartmentName(deptCodeNameMap.get(serviceMapping.getDepartment()));
					 
                }
            }
        }
    }
    
    private void populateNamesForCash(List<ReceiptBean> resultList) {
        List<Fund> fundList = fundHibernateDAO.findAllActiveFunds();
        List<Department> departmentList = microserviceUtils.getDepartments();
        List<BusinessService> businessServiceList = microserviceUtils.getBusinessService(null);
        Map<String, String> fundCodeNameMap = new HashMap<>();
        Map<String, String> deptCodeNameMap = new HashMap<>();
        Map<String, String> businessDetailsCodeNameMap = new HashMap<>();

        if (fundList != null)
            for (Fund f : fundList) {
                fundCodeNameMap.put(f.getCode(), f.getName());
            }

        if (departmentList != null)
            for (Department dept : departmentList) {
                deptCodeNameMap.put(dept.getCode(), dept.getName());
            }

        if (businessServiceList != null)
            for (BusinessService bd : businessServiceList) {
                businessDetailsCodeNameMap.put(bd.getCode(), bd.getBusinessService());
            }
        Set<String> bsCodes = new HashSet<>();
        for(ReceiptBean rb : resultList){
            bsCodes.add(rb.getService());
        }
        BusinessServiceCriteria criteria = new BusinessServiceCriteria();
        criteria.setCode(StringUtils.join(bsCodes,','));
        criteria.setVoucherCreationEnabled(true);
        List<BusinessServiceMapping> businessServiceMappingList = microserviceUtils.getBusinessServiceMappingBySearchCriteria(criteria );
        Map<String,BusinessServiceMapping> bsServiceMapping = new HashMap<>();
        businessServiceMappingList.stream().forEach(bsm -> {
            bsServiceMapping.put(bsm.getCode(), bsm);
        });

        for (ReceiptBean rb : resultList) {
            String serviceCode = rb.getService();
            if (serviceCode != null && !serviceCode.isEmpty()){
                rb.setServiceName(businessDetailsCodeNameMap.get(serviceCode));
                BusinessServiceMapping serviceMapping = bsServiceMapping.get(serviceCode);
                
                if(StringUtils.isNoneBlank(rb.getDepartment())){
                    rb.setDepartmentName(deptCodeNameMap.get(rb.getDepartment()));
                }
            }
        }
    }

    private void populateResultList(String key, List<ReceiptBean> result, List<Receipt> tempList) {
        ReceiptBean receipt = new ReceiptBean();
        BigDecimal amount = BigDecimal.ZERO;

        for (Receipt r : tempList) {
            amount = amount.add(r.getInstrument().getAmount());
        }

        receipt.setReceiptDate(key.split("-")[0]);
        receipt.setService(key.split("-")[1]);
        receipt.setInstrumentType(key.split("-")[2]);
        if (key.split("-").length > 3)
            receipt.setFund(key.split("-")[3]);
        if (key.split("-").length > 4)
            receipt.setDepartment(key.split("-")[4]);
        receipt.setInstrumentAmount(amount);
        result.add(receipt);
    }

    private void groupByDepartment(String key, Map<String, List<Receipt>> departmentWiseMap,
            List<Receipt> tempList) {
        String department;
        for (Receipt r : tempList) {
            department = r.getBill().get(0).getBillDetails().get(0).getDepartment();
            if (department != null && !department.isEmpty()) {
                if (!departmentWiseMap.containsKey(key + "-" + department)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    departmentWiseMap.put(key + "-" + department, list);
                } else {
                    departmentWiseMap.get(key + "-" + department).add(r);
                }
            } else {
                if (!departmentWiseMap.containsKey(key)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    departmentWiseMap.put(key, list);
                } else {
                    departmentWiseMap.get(key).add(r);
                }
            }
        }
    }

    private void groupByFund(String key, Map<String, List<Receipt>> fundWiseMap,
            List<Receipt> tempList) {
        String fund;
        for (Receipt r : tempList) {
            fund = r.getBill().get(0).getBillDetails().get(0).getFund();
            if (fund != null && !fund.isEmpty()) {
                if (!fundWiseMap.containsKey(key + "-" + fund)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    fundWiseMap.put(key + "-" + fund, list);
                } else {
                    fundWiseMap.get(key + "-" + fund).add(r);
                }
            } else {
                if (!fundWiseMap.containsKey(key)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    fundWiseMap.put(key, list);
                } else {
                    fundWiseMap.get(key).add(r);
                }
            }
        }
    }

    private void groupByInstrument(String receiptDateAndService, Map<String, List<Receipt>> instrumentWiseMap,
            List<Receipt> tempList) {
        String instrument;
        for (Receipt r : tempList) {
            instrument = r.getInstrument().getInstrumentType().getName();
            if (!instrumentWiseMap.containsKey(receiptDateAndService + "-" + instrument)) {
                List<Receipt> list = new ArrayList<Receipt>();
                list.add(r);
                instrumentWiseMap.put(receiptDateAndService + "-" + instrument, list);
            } else {
                instrumentWiseMap.get(receiptDateAndService + "-" + instrument).add(r);
            }
        }
    }

    private void groupByService(String receiptDate, Map<String, List<Receipt>> serviceWiseMap, List<Receipt> tempList) {
        String service;
        for (Receipt r : tempList) {
            service = r.getBill().get(0).getBillDetails().get(0).getBusinessService();
            if (!serviceWiseMap.containsKey(receiptDate + "-" + service)) {
                List<Receipt> list = new ArrayList<Receipt>();
                list.add(r);
                serviceWiseMap.put(receiptDate + "-" + service, list);
            } else {
                serviceWiseMap.get(receiptDate + "-" + service).add(r);
            }
        }
    }

    private void groupByReceiptDate(Map<String, List<Receipt>> receiptDateWiseMap, List<Receipt> receipts) {
        String receiptDate;
        for (Receipt receipt : receipts) {
            receiptDate = DateUtils
                    .toDefaultDateFormat(new Date(receipt.getBill().get(0).getBillDetails().get(0).getReceiptDate()));
            if (!receiptDateWiseMap.containsKey(receiptDate)) {
                List<Receipt> list = new ArrayList<Receipt>();
                list.add(receipt);

                receiptDateWiseMap.put(receiptDate, list);
            } else {
                receiptDateWiseMap.get(receiptDate).add(receipt);
            }

        }

    }

    /**
     * Method to find all the Cheque and DD type instruments with status as :new and
     *
     * @return List of HashMap
     */
	/*
	 * @Override public List<ReceiptBean>
	 * findChequeRemittanceDetailsForServiceAndFund(final String boundaryIdList,
	 * final String serviceCodes, final String fundCodes, final Date startDate,
	 * final Date endDate, String serviceTypeId) { // TODO : need to make this call
	 * to mdms // FinancialStatus status =
	 * microserviceUtils.getInstrumentStatusByCode(CollectionConstants.
	 * INSTRUMENT_NEW_STATUS); String instrumentTypes =
	 * CollectionConstants.INSTRUMENTTYPE_NAME_CHEQUE + "," +
	 * CollectionConstants.INSTRUMENTTYPE_NAME_DD; List<Instrument> instruments =
	 * microserviceUtils.getInstruments(instrumentTypes, TransactionType.Debit,
	 * CollectionConstants.INSTRUMENT_NEW_STATUS); Map<String, Instrument>
	 * receiptInstrumentMap = new HashMap<>(); List<String> receiptIds = new
	 * ArrayList<>(); for (Instrument i : instruments) { if
	 * (i.getInstrumentVouchers() != null) for (InstrumentVoucher iv :
	 * i.getInstrumentVouchers()) {
	 * receiptInstrumentMap.put(iv.getReceiptHeaderId(), i);
	 * receiptIds.add(iv.getReceiptHeaderId()); } } ReceiptBean rb;
	 * List<ReceiptBean> finalList = new ArrayList<>();
	 * if(!receiptInstrumentMap.isEmpty() && !receiptIds.isEmpty()){ switch
	 * (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) { case "V2":
	 * case "VERSION2": List<Payment> payments = microserviceUtils.getPayments(
	 * PaymentSearchCriteria.builder() .ids(new HashSet(receiptIds))
	 * .status(Collections.singleton(PaymentStatusEnum.NEW.name()))
	 * .businessServices(Arrays.asList(serviceCodes.split(",")).stream().collect(
	 * Collectors.toSet())) .fromDate(startDate.getTime())
	 * .toDate(endDate.getTime()) .build() ); payments.stream().filter(payment ->
	 * receiptInstrumentMap.containsKey(payment.getId())).forEach(payment -> {
	 * Set<String> receiptNumbers =
	 * payment.getPaymentDetails().stream().map(PaymentDetail::getReceiptNumber).
	 * collect(Collectors.toSet()); Set<String> services =
	 * payment.getPaymentDetails().stream().map(PaymentDetail::getBusinessService).
	 * collect(Collectors.toSet()); ReceiptBean rb1 = new ReceiptBean();
	 * rb1.setInstrumentId(receiptInstrumentMap.get(payment.getId()).getId());
	 * rb1.setInstrumentAmount(receiptInstrumentMap.get(payment.getId()).getAmount()
	 * ); rb1.setInstrumentNumber(receiptInstrumentMap.get(payment.getId()).
	 * getTransactionNumber()); if
	 * (receiptInstrumentMap.get(payment.getId()).getTransactionDate() != null)
	 * rb1.setInstrumentDate(DateUtils.toDefaultDateFormat(
	 * receiptInstrumentMap.get(payment.getId()) .getTransactionDate()));
	 * rb1.setInstrumentType(
	 * receiptInstrumentMap.get(payment.getId()).getInstrumentType().getName());
	 * rb1.setBankBranch(receiptInstrumentMap.get(payment.getId()).getBranchName());
	 * // final Bank bank = (Bank) persistenceService.find("from Bank where id=?",
	 * // receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()).getBank().getId().intValue());
	 * org.egov.infra.microservice.models.Bank bank =
	 * receiptInstrumentMap.get(payment.getId()).getBank(); rb1.setBank(bank != null
	 * ? bank.getName() : ""); rb1.setReceiptId(payment.getId());
	 * rb1.setReceiptNumber(StringUtils.join(receiptNumbers,","));
	 * rb1.setReceiptDate(DateUtils.toDefaultDateTimeFormat(new
	 * Date(payment.getTransactionDate())));
	 * rb1.setService(StringUtils.join(services,",")); finalList.add(rb1); });
	 * 
	 * break;
	 * 
	 * default: List<Receipt> receipts =
	 * microserviceUtils.getReceipts(StringUtils.join(receiptIds, ","),
	 * CollectionConstants.RECEIPT_STATUS_APPROVED, serviceCodes, startDate,
	 * endDate, serviceTypeId);
	 * 
	 * for (Receipt r : receipts) { rb = new ReceiptBean();
	 * rb.setInstrumentId(receiptInstrumentMap.get(r.getBill().get(0).getBillDetails
	 * ().get(0).getReceiptNumber()).getId());
	 * rb.setInstrumentAmount(receiptInstrumentMap.get(r.getBill().get(0).
	 * getBillDetails().get(0).getReceiptNumber()).getAmount());
	 * rb.setInstrumentNumber(
	 * receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()).getTransactionNumber()); if
	 * (receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()).getTransactionDate() != null)
	 * rb.setInstrumentDate(DateUtils.toDefaultDateFormat(
	 * receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()) .getTransactionDate())); rb.setInstrumentType(
	 * receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()).getInstrumentType().getName());
	 * rb.setBankBranch(receiptInstrumentMap.get(r.getBill().get(0).getBillDetails()
	 * .get(0).getReceiptNumber()).getBranchName()); // final Bank bank = (Bank)
	 * persistenceService.find("from Bank where id=?", //
	 * receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()).getBank().getId().intValue());
	 * org.egov.infra.microservice.models.Bank bank =
	 * receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()).getBank(); rb.setBank(bank != null ? bank.getName() :
	 * "");
	 * rb.setReceiptId(r.getBill().get(0).getBillDetails().get(0).getReceiptNumber()
	 * ); rb.setReceiptNumber(r.getBill().get(0).getBillDetails().get(0).
	 * getReceiptNumber()); rb.setReceiptDate( DateUtils.toDefaultDateTimeFormat(new
	 * Date(r.getBill().get(0).getBillDetails().get(0).getReceiptDate())));
	 * rb.setService(r.getBill().get(0).getBillDetails().get(0).getBusinessService()
	 * ); rb.setFund(r.getBill().get(0).getBillDetails().get(0).getFund());
	 * rb.setDepartment(r.getBill().get(0).getBillDetails().get(0).getDepartment());
	 * finalList.add(rb); } break;
	 * 
	 * }
	 * 
	 * populateNames(finalList); } return finalList; }
	 */
    
    
    
   // Changes By Prasanta
    /**
     * Method to find all the Cheque and DD type instruments with status as :new and
     *
     * @return List of HashMap
     */
    @Override
    public List<ReceiptBean> findChequeRemittanceDetailsForServiceAndFund(String classification, Date startDate, Date endDate, String businessCode,
            String receiptNo,String type) {
        // TODO : need to make this call to mdms
//        FinancialStatus status = microserviceUtils.getInstrumentStatusByCode(CollectionConstants.INSTRUMENT_NEW_STATUS);
        String instrumentTypes = CollectionConstants.INSTRUMENTTYPE_NAME_CHEQUE + ","
                + CollectionConstants.INSTRUMENTTYPE_NAME_DD;
		/*
		 * List<Instrument> instruments =
		 * microserviceUtils.getInstruments(instrumentTypes, TransactionType.Debit,
		 * CollectionConstants.INSTRUMENT_NEW_STATUS); Map<String, Instrument>
		 * receiptInstrumentMap = new HashMap<>(); List<String> receiptIds = new
		 * ArrayList<>(); for (Instrument i : instruments) { if
		 * (i.getInstrumentVouchers() != null) for (InstrumentVoucher iv :
		 * i.getInstrumentVouchers()) {
		 * receiptInstrumentMap.put(iv.getReceiptHeaderId(), i);
		 * receiptIds.add(iv.getReceiptHeaderId()); } }
		 */
        ReceiptBean rb;
        List<ReceiptBean> finalList = new ArrayList<>();
        //if(!receiptInstrumentMap.isEmpty() && !receiptIds.isEmpty()){
		/*
		 * switch (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) { case
		 * "V2": case "VERSION2": List<Payment> payments =
		 * microserviceUtils.getPayments( PaymentSearchCriteria.builder() .ids(new
		 * HashSet(receiptIds))
		 * .status(Collections.singleton(PaymentStatusEnum.NEW.name()))
		 * //.businessServices(Arrays.asList(serviceCodes.split(",")).stream().collect(
		 * Collectors.toSet())) .fromDate(startDate.getTime())
		 * .toDate(endDate.getTime()) .build() ); payments.stream().filter(payment ->
		 * receiptInstrumentMap.containsKey(payment.getId())).forEach(payment -> {
		 * Set<String> receiptNumbers =
		 * payment.getPaymentDetails().stream().map(PaymentDetail::getReceiptNumber).
		 * collect(Collectors.toSet()); Set<String> services =
		 * payment.getPaymentDetails().stream().map(PaymentDetail::getBusinessService).
		 * collect(Collectors.toSet()); ReceiptBean rb1 = new ReceiptBean();
		 * rb1.setInstrumentId(receiptInstrumentMap.get(payment.getId()).getId());
		 * rb1.setInstrumentAmount(receiptInstrumentMap.get(payment.getId()).getAmount()
		 * ); rb1.setInstrumentNumber(receiptInstrumentMap.get(payment.getId()).
		 * getTransactionNumber()); if
		 * (receiptInstrumentMap.get(payment.getId()).getTransactionDate() != null)
		 * rb1.setInstrumentDate(DateUtils.toDefaultDateFormat(
		 * receiptInstrumentMap.get(payment.getId()) .getTransactionDate()));
		 * rb1.setInstrumentType(
		 * receiptInstrumentMap.get(payment.getId()).getInstrumentType().getName());
		 * rb1.setBankBranch(receiptInstrumentMap.get(payment.getId()).getBranchName());
		 * // final Bank bank = (Bank) persistenceService.find("from Bank where id=?",
		 * // receiptInstrumentMap.get(r.getBill().get(0).getBillDetails().get(0).
		 * getReceiptNumber()).getBank().getId().intValue());
		 * org.egov.infra.microservice.models.Bank bank =
		 * receiptInstrumentMap.get(payment.getId()).getBank(); rb1.setBank(bank != null
		 * ? bank.getName() : ""); rb1.setReceiptId(payment.getId());
		 * rb1.setReceiptNumber(StringUtils.join(receiptNumbers,","));
		 * rb1.setReceiptDate(DateUtils.toDefaultDateTimeFormat(new
		 * Date(payment.getTransactionDate())));
		 * rb1.setService(StringUtils.join(services,",")); finalList.add(rb1); });
		 * 
		 * break;
		 * 
		 * default:
		 */        	
            
            
            	 List<Receipt> receipts  = microserviceUtils.searchRecieptsFin(classification,startDate, endDate, businessCode, null, type);
            	 System.out.println("Inside METHOD  after >>"+receipts);            
            for (Receipt r : receipts) {
                for (org.egov.infra.microservice.models.Bill bill : r.getBill()) {
                    for (BillDetail billDetail : bill.getBillDetails()) {
                rb = new ReceiptBean();
                   	 	rb.setInstrumentId(r.getInstrument().getInstrumentType().getId());
                    	rb.setInstrumentAmount(billDetail.getTotalAmount());
                        rb.setInstrumentNumber(r.getInstrument().getInstrumentNumber());
                        rb.setInstrumentType(r.getInstrument().getInstrumentType().getName());
                        if(r.getInstrument().getTransactionDate() !=null)
                        	rb.setInstrumentDate(DateUtils.toDefaultDateFormat(r.getInstrument().getTransactionDate()));
                               
                        rb.setBankBranch(r.getInstrument().getBranchName());
                        org.egov.infra.microservice.models.Bank bank = r.getInstrument().getBank();
                rb.setBank(bank != null ? bank.getName() : "");
                        rb.setReceiptId(r.getReceiptNumber());	
                        rb.setReceiptNumber(r.getReceiptNumber());
                        
                        rb.setReceiptId(r.getPaymentId());
                        rb.setReceiptNumber(billDetail.getReceiptNumber());
                       String receiptDate = DateUtils
                                .toDefaultDateFormat(new Date(r.getBill().get(0).getBillDetails().get(0).getReceiptDate()));
                        rb.setReceiptDate(receiptDate);
                        
                        rb.setService(billDetail.getBusinessService());
                        rb.setRemittanceReferenceNumber(billDetail.getBillNumber());
                        rb.setFund(billDetail.getFund());
                       
                        rb.setDepartment(billDetail.getDepartment());
                        
                        if((r.getInstrument().getInstrumentType().getName())!="CASH") {
                finalList.add(rb);
            }
        
        }
                }

               
			/*
			 * } break;
			 */
        
       //}

        populateNames(finalList);
				 
						 
        }
        return finalList;
    }
    
    public List<ReceiptBean> findChequeRemittanceDetailsForServiceAndFund(String classification, Date fromDate, Date toDate, String businessCode,
            String receiptNo,String department,String type, String searchAmount, String subDivision,String collectedBy) {
        // TODO : need to make this call to mdms
//        FinancialStatus status = microserviceUtils.getInstrumentStatusByCode(CollectionConstants.INSTRUMENT_NEW_STATUS);
        String instrumentTypes = CollectionConstants.INSTRUMENTTYPE_NAME_CHEQUE + ","
                + CollectionConstants.INSTRUMENTTYPE_NAME_DD;
		
        ReceiptBean rb;
        List<ReceiptBean> finalList = new ArrayList<>();
        boolean amountcheck=false;
    	boolean deptcheck=false;
    	boolean subdivisioncheck=false; 
    	boolean receiptcheck=false;
    	boolean collectedcheck=false;
    	
    	final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> list1= null;
    	SQLQuery queryMain =  null;
    	query1
        .append("select v2.reciept_number, v2.departmentcode, fnc.code as fnccode from vouchermis v2,\"function\" fnc where v2.reciept_number notnull and fnc.id  = v2.functionid");
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list1 = queryMain.list();
    	System.out.println(":::list size::::: "+list1.size());	
    	Map<String, String> deptMap = new HashMap<>();
    	Map<String, String> funMap = new HashMap<>();
    	if(list1!=null)
    	{
    		for (final Object[] object : list1)
    		{
    			deptMap.put(object[0].toString(), object[1].toString());
    			funMap.put(object[0].toString(), object[2].toString());
    		}
    	}
    	
    	 List<Receipt> receipts  = null;//microserviceUtils.searchRecieptsFinNew(classification, fromDate, toDate, businessCode, null, type);
        //List<Receipt> receipts  = microserviceUtils.searchRecieptsFin(classification,startDate, endDate, businessCode, null, type);
            	 System.out.println("Inside METHOD  after >>"+receipts); 
            	 
            for (Receipt r : receipts) {
	 /*
                for (org.egov.infra.microservice.models.Bill bill : r.getBill()) {
                    for (BillDetail billDetail : bill.getBillDetails()) {
                rb = new ReceiptBean();
                   	 	rb.setInstrumentId(r.getInstrument().getInstrumentType().getId());
                    	rb.setInstrumentAmount(billDetail.getTotalAmount());
                        rb.setInstrumentNumber(r.getInstrument().getInstrumentNumber());
                        rb.setInstrumentType(r.getInstrument().getInstrumentType().getName());
                        if(r.getInstrument().getTransactionDate() !=null)
                        	rb.setInstrumentDate(DateUtils.toDefaultDateFormat(r.getInstrument().getTransactionDate()));
                               
	  
                        rb.setBankBranch(r.getInstrument().getBranchName());
                        org.egov.infra.microservice.models.Bank bank = r.getInstrument().getBank();
                        rb.setBank(bank != null ? bank.getName() : "");
                        rb.setReceiptId(r.getReceiptNumber());	
                        rb.setReceiptNumber(r.getReceiptNumber());
                        
                        rb.setReceiptId(r.getPaymentId());
                        rb.setReceiptNumber(billDetail.getReceiptNumber());
                        String receiptDate = DateUtils
                                .toDefaultDateFormat(new Date(r.getBill().get(0).getBillDetails().get(0).getReceiptDate()));
																		 
                        rb.setReceiptDate(receiptDate);
                        
                        rb.setService(billDetail.getBusinessService());
                        rb.setRemittanceReferenceNumber(billDetail.getBillNumber());
                        rb.setFund(billDetail.getFund()!=null ? billDetail.getFund() :"Municipal (General) Fund");
                        if(deptMap.containsKey(r.getReceiptNumber()))
		    			{
                        	rb.setDepartment(deptMap.get(r.getReceiptNumber()));
		    			}
                        else {
                        	rb.setDepartment(billDetail.getDepartment());
                        }
                        if(funMap.containsKey(r.getReceiptNumber()))
                        {
                        	rb.setFunctionCode(funMap.get(r.getReceiptNumber()));
                        }
                        else {
                        	rb.setFunctionCode(null);
                        }
                        System.out.println("dept "+rb.getDepartment());
                        rb.setCreatedUser(r.getCollectedByName());
                        BigDecimal searchamt = new BigDecimal(0);
	                 	if(searchAmount!=null && !searchAmount.equalsIgnoreCase("") && !searchAmount.equalsIgnoreCase("0")) {
														  
	                 		searchamt=new BigDecimal(Integer.parseInt(searchAmount));
	                 		amountcheck=true;
	                 	}
	                 	if(!subDivision.equalsIgnoreCase("-1"))
	                 	{
	                 		subdivisioncheck=true;
	                 	}
	                 	if(!department.equalsIgnoreCase("-1"))
	                 	{
	                 		deptcheck=true;
	                 	}
	                 	if(receiptNo!=null && !receiptNo.equalsIgnoreCase("")) {
	                		receiptcheck=true;
	                	}
	                 	if(collectedBy!=null && !collectedBy.equalsIgnoreCase("")) {
	                		collectedcheck=true;
	                	}
                        if(((r.getInstrument().getInstrumentType().getName()).equalsIgnoreCase("CHEQUE") ||
				  
                        		(r.getInstrument().getInstrumentType().getName()).equalsIgnoreCase("DD"))&&
                        		r.getPaymentStatus().equalsIgnoreCase("NEW")) 
                        {	
                            	if(amountcheck)
                            	{
                            		if(searchamt.compareTo(r.getInstrument().getAmount())==0) {}
                            		else
                            			continue;
                            	}
                            	if(subdivisioncheck)
                            	{
                            		if(subDivision.equalsIgnoreCase(r.getSubdivison())) {}
                            		else
                            			continue;
                            	}
                            	 if(deptcheck)
                            	{
                            		if(department.equalsIgnoreCase(rb.getDepartment())) {}
                            		else
                            			continue;
                            	}
                            	if(receiptcheck)
                            	{
                            		if(receiptNo.equalsIgnoreCase(r.getReceiptNumber())) {}
                            		else
                            			continue;
                            	}
                            	if(collectedcheck)
                            	{
                            		if(collectedBy.equalsIgnoreCase(r.getCollectedByName())) {}
                            		else
                            			continue;
                            	}
                            	finalList.add(rb);
                            	
                         }
			        }
                }
                populateNames(finalList);
       */ }
        return finalList;
    }

    private List<Object[]> populateOtherDetails(List<Receipt> receipts) 
    {
    	
	    	final StringBuffer query1 = new StringBuffer(500);
			List<Object[]> list1= null;
	    	SQLQuery queryMain =  null;
	    	query1
	    	.append("select gl.id,gl.glcode,gl.debitamount,gl.creditamount,v2.departmentcode,v2.reciept_number from generalledger gl left join vouchermis v2 on gl.voucherheaderid =v2.voucherheaderid where v2.reciept_number notnull ");
	    	
	    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
	    	list1 = queryMain.list();
	    	System.out.println(":::list size::::: "+list1.size());	
	    	if(list1!=null)
	    	{
	    		
	    	}
    	return list1;
    			    	    
	}
    
    public void setCollectionsUtil(final CollectionsUtil collectionsUtil) {
        this.collectionsUtil = collectionsUtil;
    }

    public void setFinancialsUtil(final FinancialsUtil financialsUtil) {
        this.financialsUtil = financialsUtil;
    }

    public void setReceiptHeaderService(final ReceiptHeaderService receiptHeaderService) {
        this.receiptHeaderService = receiptHeaderService;
    }

    public CollectionsNumberGenerator getCollectionsNumberGenerator() {
        return collectionsNumberGenerator;
    }

    public void setCollectionsNumberGenerator(final CollectionsNumberGenerator collectionsNumberGenerator) {
        this.collectionsNumberGenerator = collectionsNumberGenerator;
    }

    public void setPersistenceService(final PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setRemittancePersistService(final PersistenceService<Remittance, Long> remittancePersistService) {
        this.remittancePersistService = remittancePersistService;
    }

    @Transactional
    @Override
    public List<Receipt> createChequeBankRemittance(List<ReceiptBean> receiptBeanList, String accountNumberId,
            Date remittanceDate, String[] instrumentIdArray) {

        List<Receipt> receiptList = new ArrayList<>();
        final SimpleDateFormat dateFomatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        InstrumentAccountCode accountCode = microserviceUtils
                .getInstrumentAccountGlCodeByType(CollectionConstants.INSTRUMENTTYPE_NAME_CHEQUE);

        final String cashInHandQueryString = "SELECT COA.GLCODE FROM CHARTOFACCOUNTS COA WHERE COA.GLCODE = '"
                + accountCode.getGlcode()
                + "'";
        final Query chequeInHand = persistenceService.getSession().createSQLQuery(cashInHandQueryString);

        String chequeInHandGlcode = null;

        if (!chequeInHand.list().isEmpty()){
            chequeInHandGlcode = chequeInHand.list().get(0).toString();
        }else{
            String validationMessage = String.format("Glcode: %1$s which  is mapped with Istrument type cheque in mdms is not exist in database",  accountCode.getGlcode());
            throw new ValidationException(Arrays.asList(new ValidationError(validationMessage, validationMessage)));
        }

        String functionCode = collectionsUtil.getAppConfigValue(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                CollectionConstants.APPCONFIG_VALUE_COLLECTION_BANKREMITTANCE_FUNCTIONCODE);

        List<String> instrumentIdList = new ArrayList<>();
        Boolean showRemitDate = true;
        BigDecimal totalChequeAmount = BigDecimal.ZERO;
        BigDecimal totalChequeVoucherAmt = BigDecimal.ZERO;
        String fundCode = "";
        Date voucherDate = null;
        if (collectionsUtil
                .getAppConfigValue(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                        CollectionConstants.APPCONFIG_VALUE_COLLECTION_BANKREMITTANCE_SHOWREMITDATE)
                .equals(CollectionConstants.YES))
            showRemitDate = true;

        final Bankaccount depositedBankAccount = (Bankaccount) persistenceService.find("from Bankaccount where accountnumber=?",
                accountNumberId);
        final String serviceGlCode = depositedBankAccount.getChartofaccounts().getGlcode();

        Boolean voucherTypeForChequeDDCard = false;
        if (collectionsUtil.getAppConfigValue(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                CollectionConstants.APPCONFIG_VALUE_REMITTANCEVOUCHERTYPEFORCHEQUEDDCARD).equals(
                        CollectionConstants.FINANCIAL_RECEIPTS_VOUCHERTYPE))
            voucherTypeForChequeDDCard = true;

        List<BusinessService> businessDetailsList = microserviceUtils.getBusinessService(null);
        Map<String, BusinessService> businessDetailsMap = new HashMap<>();
        for (BusinessService bd : businessDetailsList) {
            businessDetailsMap.put(bd.getCode(), bd);
        }
        BusinessService businessDetails = null;
        String createVoucher = "N";
        StringBuffer receiptNumbers = new StringBuffer();
        Map<String, Set<Instrument>> receiptInstrumentMap = new HashMap<>();
        List<Instrument> instruments;
        Set<String> paymentIdSet = new HashSet();
        for (ReceiptBean receipt : receiptBeanList) {
            
            if (receipt.getSelected() != null && receipt.getSelected()) {
                instruments = microserviceUtils.getInstruments(receipt.getInstrumentId());
                receiptInstrumentMap.put(receipt.getReceiptId(), new HashSet<>(instruments));
                instrumentIdList.add(receipt.getInstrumentId());
                receiptNumbers.append(",");
                switch(ApplicationThreadLocals.getCollectionVersion().toUpperCase()){
                case "V2":
                case "VERSION2":
                    receiptNumbers.append(receipt.getReceiptId());
                    paymentIdSet.add(receipt.getReceiptId());
                    break;
                default:
                    receiptNumbers.append(receipt.getReceiptNumber());
                    break;
                }

                if (showRemitDate && remittanceDate != null)
                    voucherDate = remittanceDate;
                else
                    try {
                        voucherDate = collectionsUtil.getRemittanceVoucherDate(dateFomatter.parse(receipt.getReceiptDate()));
                    } catch (final ParseException e) {
                        LOGGER.error("Error Parsing Date", e);
                    }

                businessDetails = businessDetailsMap.get(receipt.getService());

                // If Cheque Amount is present
                if (receipt.getInstrumentAmount() != null && chequeInHandGlcode != null) {
                    List<CVoucherHeader> voucher = this.getVoucher(instruments.get(0).getInstrumentVouchers().get(0).getVoucherHeaderId());
                    if(!voucher.isEmpty()){
                        fundCode = voucher.get(0).getFundId().getCode();
                        functionCode = voucher.get(0).getVouchermis().getFunction().getCode();
                    }else{
                        String validationMessage = "Voucher is not exist for receipt: "+receipt.getReceiptNumber()+", contact to system administrator.";
                        throw new ValidationException(Arrays.asList(new ValidationError(validationMessage, validationMessage)));
                    }
                    totalChequeAmount = totalChequeAmount.add(receipt.getInstrumentAmount());
                    if (businessDetails.isVoucherCreationEnabled()) {
                        createVoucher = "Y";
                        totalChequeVoucherAmt = totalChequeVoucherAmt.add(receipt.getInstrumentAmount());
                    } else {
                        microserviceUtils.depositeInstruments(instruments, depositedBankAccount.getAccountnumber());
                    }
                }

            }
        }

        receiptList = microserviceUtils.getReceipts(receiptNumbers.toString());
        final Map<String, Object> instrumentDepositMap = financialsUtil.prepareForUpdateInstrumentDepositSQL();
        if (totalChequeVoucherAmt.compareTo(totalChequeAmount) != 0) {
            String validationMessage = "There is a difference of amount " + totalChequeAmount.subtract(totalChequeVoucherAmt)
                    + " between bank challan and the remittance voucher , please contact system administrator ";
            throw new ValidationException(Arrays.asList(new ValidationError(validationMessage, validationMessage)));
        }
        String subdivisonNew="";
        final Remittance remittance = populateAndPersistRemittance(BigDecimal.ZERO, totalChequeAmount, fundCode, null,
                chequeInHandGlcode, serviceGlCode, functionCode, new HashSet(receiptList), createVoucher,
                voucherDate, depositedBankAccount, BigDecimal.ZERO, totalChequeVoucherAmt,
                instrumentIdList, receiptInstrumentMap,subdivisonNew);

        // For cheque update instrument status to deposited.
        for (final RemittanceInstrument bankRemitInstrument : remittance.getRemittanceInstruments()) {
            final Map<String, Object> chequeMap = remittanceSchedulerService.constructInstrumentMap(instrumentDepositMap,
                    depositedBankAccount, bankRemitInstrument.getInstrument(), remittance.getVoucherHeader());
            if (voucherTypeForChequeDDCard)
                financialsUtil.updateCheque_DD_Card_Deposit_Receipt(chequeMap);
            else
                financialsUtil.updateCheque_DD_Card_Deposit(chequeMap, remittance.getVoucherHeader(),
                        bankRemitInstrument.getInstrumentHeader(), depositedBankAccount);
        }
        
        switch (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) {
        case "V2":
        case "VERSION2":
            if(!paymentIdSet.isEmpty()){
                microserviceUtils.performWorkflow(paymentIdSet,PaymentWorkflow.PaymentAction.REMIT, "Payment got remitted from finance");
            }
            break;

        default:
            for (final Receipt receiptHeader : receiptList) {
                receiptHeader.getBill().get(0).getBillDetails().get(0).setStatus("Remitted");
                receiptHeader.getInstrument().setTenantId(receiptHeader.getTenantId());
                receiptHeader.getBill().get(0).setPayerName(receiptHeader.getBill().get(0).getPaidBy());
//                receiptHeader.setReceiptNumber(remittance.getReferenceNumber());
            }
            //ReceiptResponse response = microserviceUtils.updateReceipts(new ArrayList<>(receiptList));
            break;
        }
        for (ReceiptBean receipt : receiptBeanList) {
            receipt.setRemittanceReferenceNumber(remittance.getReferenceNumber());
        }
        return receiptList;
    }
    
    @Transactional
    public ReceiptBean createChequeBankRemittance(ReceiptBean receiptBeanList, List<RemitancePOJO> rp, Date remittanceDate,String narration,String deptIdnew,String functionNew, String subdivisonNew,String receiptNumbers) {  

    	final SimpleDateFormat dateFomatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        InstrumentAccountCode accountCode = microserviceUtils.getInstrumentAccountGlCodeByType(CollectionConstants.INSTRUMENTTYPE_NAME_CHEQUE);
        final String cashInHandQueryString = "SELECT COA.GLCODE FROM CHARTOFACCOUNTS COA WHERE COA.GLCODE = '"
                + accountCode.getGlcode()+ "'";
        final Query chequeInHand = persistenceService.getSession().createSQLQuery(cashInHandQueryString);
		String chequeInHandGlcode = null;

        if (!chequeInHand.list().isEmpty()){
            chequeInHandGlcode = chequeInHand.list().get(0).toString();
        }

        Map<String,BigDecimal> bankAccountMap=new HashMap<String,BigDecimal>();
        BigDecimal amt=null;
        for(RemitancePOJO row:rp)
        {
        	if(bankAccountMap.get(row.getBankaccount()) == null )
        	{
        		amt=new BigDecimal(row.getAmount());
        		bankAccountMap.put(row.getBankaccount(),amt);
        	}
        	else
        	{
        		BigDecimal total=bankAccountMap.get(row.getBankaccount()).add(new BigDecimal(row.getAmount()));
        		bankAccountMap.put(row.getBankaccount(), total);
        	}
        }

        String createVoucher = "N";
        Boolean showRemitDate = true;
        BigDecimal totalChequeVoucherAmt = BigDecimal.ZERO;
        String fundCode = "";
        Date voucherDate = null;
        List<Bankaccount> depositedBankAccount = new ArrayList<Bankaccount>();
        Bankaccount b=null;
        Set<String> keys=bankAccountMap.keySet();
        Map<String,BigDecimal> serviceGlCodes = new HashMap<String,BigDecimal>();
        List<BigDecimal> totalChequeAmt = new ArrayList<BigDecimal>();
        BigDecimal amt1=new BigDecimal(0);
        for(String key : keys)
        {
        	String[] accNum=key.split("-");
        	b=(Bankaccount) persistenceService.find("from Bankaccount where accountnumber=?",accNum[2]);
        	depositedBankAccount.add(b);
        	amt1=new BigDecimal(bankAccountMap.get(key).toString());
        	totalChequeAmt.add(amt1);
        	totalChequeVoucherAmt=totalChequeVoucherAmt.add(amt1);
        	serviceGlCodes.put(b.getChartofaccounts().getGlcode(),amt1);
        }
        
            if (receiptBeanList.getSelected() != null) {
                if (receiptBeanList.getFund() != null && !receiptBeanList.getFund().isEmpty())
                {
                	if(receiptBeanList.getFund().equalsIgnoreCase("Municipal (General) Fund"))
                		fundCode="01";
                	else
                	fundCode = receiptBeanList.getFund();
                }
                if (showRemitDate && remittanceDate != null)
                    voucherDate = remittanceDate;
                else
                {
                	try {
                        voucherDate = collectionsUtil.getRemittanceVoucherDate(dateFomatter.parse(receiptBeanList.getReceiptDate()));
                    } catch (final ParseException e) {
                        LOGGER.error("Error Parsing Date", e);
                    }
                }
                if (receiptBeanList.getService() != null && receiptBeanList.getService().length() > 0) {
                    // If Cash Amount is present
                    if (receiptBeanList.getInstrumentAmount() != null && chequeInHandGlcode != null) {
                        createVoucher = "Y";
                        String functionCode=functionNew;//receiptBeanList.getFunctionCode();
                        String deptCode=deptIdnew;//receiptBeanList.getDepartment();

                        final Remittance remittance = populateAndPersistRemittanceNew(null, totalChequeAmt, fundCode,
                                chequeInHandGlcode, null, serviceGlCodes, functionCode, receiptBeanList, createVoucher,
                                narration,voucherDate, depositedBankAccount, totalChequeVoucherAmt, BigDecimal.ZERO, Collections.EMPTY_LIST,
                                null,deptCode,subdivisonNew);
                        
                        receiptBeanList.setRemittanceReferenceNumber(remittance.getReferenceNumber());
                        receiptBeanList.setRemittanceVouherNumber(remittance.getReferenceVoucherNumber());
                        receiptBeanList.setVoucherid(remittance.getVoucherid());
                        for(String key : keys)
                        {
                        	try {
	                        MisRemittanceDetails mrd= new MisRemittanceDetails();
	                        mrd.setVoucher_number(remittance.getReferenceVoucherNumber());
	                        mrd.setVoucher_date(voucherDate);
	                        //mrd.setMis_receipt_id(Long.valueOf(receiptBeanList.getReceiptId()));
	                        mrd.setBankaccount(key);
	                        BigDecimal bankamt=new BigDecimal(bankAccountMap.get(key).toString());
	                        mrd.setAmount(bankamt);
	                        mrd.setDepartment(deptIdnew);
	                        mrd.setFunction(functionCode);
	                        mrd.setNarration(narration);
	                        mrd.setSubdivison(subdivisonNew);
	                        mrd.setReceiptnumbers(receiptNumbers);
	                        misRemittanceDetailService.create(mrd);
                        	}
                        	catch(Exception e)
                        	{
                        		e.printStackTrace();
                        	}
                        }
                    }
                }
            }
        
       
        
        return receiptBeanList;
    }

    private List<CVoucherHeader> getVoucher(String voucherHeaderId) {
        final String sqlQuery = "from CVoucherHeader vh where voucherNumber=:voucherNumber";
        final Query query = persistenceService.getSession().createQuery(sqlQuery);
        query.setParameter("voucherNumber", voucherHeaderId);
        return query.list();
    }
    
    
    public List<String> getallBank(){
    	final StringBuffer query1 = new StringBuffer(500);
    	List<Object[]> list1= null;
    	SQLQuery queryMain =  null;
    	List<String> banklist=new ArrayList<String>();
    	query1
        .append("select b3.name,b2.branchcode,b.accountnumber from bankaccount b left join bankbranch b2 on b.branchid =b2.id left join bank b3 on b2.bankid =b3.id");
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list1 = queryMain.list();
    	System.out.println("::Size:: "+list1.size());
    	if(list1.size()!=0) {
    		for(Object[] e : list1)
	    	{
    			String s=null;
    			s=e[0].toString()+"-"+e[1].toString()+"-"+e[2].toString();
    			System.out.println(":::::Bank::::: "+s);
    			banklist.add(s);
    			
	    	}
    	}
    	return banklist;
    }
    
    private String getDateQuery(final Date dateFrom, final Date dateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != dateFrom)
				numDateQuery.append(" and date(mrd.receipt_date) >='").append(DDMMYYYYFORMAT1.format(dateFrom)).append("'");
			if (null != dateTo)
				numDateQuery.append(" and date(mrd.receipt_date)  <='").append(DDMMYYYYFORMAT1.format(dateTo)).append("'");

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
    
    private void getServiceCategoryList() {
		List<BusinessService> businessService = microserviceUtils.getBusinessService(null);
		for (BusinessService bs : businessService) {
			String[] splitServName = bs.getBusinessService().split(Pattern.quote("."));
			String[] splitSerCode = bs.getCode().split(Pattern.quote("."));
			if (splitServName.length == 2 && splitSerCode.length == 2) {
				if (!serviceCategoryNames.containsKey(splitSerCode[0])) {
					serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
				}
				if (serviceTypeMap.containsKey(splitSerCode[0])) {
					Map<String, String> map = serviceTypeMap.get(splitSerCode[0]);
					map.put(splitSerCode[1], splitServName[1]);
					serviceTypeMap.put(splitSerCode[0], map);
				} else {
					Map<String, String> map = new HashMap<>();
					map.put(splitSerCode[1], splitServName[1]);
					serviceTypeMap.put(splitSerCode[0], map);
				}
			} else {
				serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
			}
		}
	}
}
