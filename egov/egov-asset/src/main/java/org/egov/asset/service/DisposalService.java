package org.egov.asset.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.DepreciationInputs;
import org.egov.asset.model.Disposal;
import org.egov.asset.repository.AccountcodePurposeRepository;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetHistoryRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.AssetStatusRepository;
import org.egov.asset.repository.DepreciationRepository;
import org.egov.asset.repository.DisposalRepository;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.repository.DepartmentRepository;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.voucher.VoucherDetails;
import org.egov.model.voucher.VoucherTypeBean;
import org.egov.model.voucher.WorkflowBean;
import org.egov.services.voucher.JournalVoucherActionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
@Service
public class DisposalService {

	@Autowired
    private FinancialUtils financialUtils;
	@Autowired
    private DocumentUploadRepository documentUploadRepository;
	@Autowired
	private DisposalRepository disposalRepository;
	@Autowired
	private AssetMasterRepository assetRepository;
	final private static String FILESTORE_MODULEOBJECT="disposal";
	final private static String ASSET_PROFIT="ASSET_PROFIT";
	final private static String ASSET_LOSS="ASSET_LOSS";
	final private static String DISPOSED="DISPOSED";
	@Autowired
	private CChartOfAccountsRepository cChartOfAccountsRepository;
	@Autowired
	private AccountcodePurposeRepository accountcodePurposeRepository;
	@Autowired
	private MicroserviceUtils microserviceUtils;
	@Autowired
	private AssetStatusRepository statusRepo;
	@Autowired
	private AssetMasterRepository masterRepo;
	@Autowired
	private AssetCatagoryRepository assetCategoryRepo;
	@Autowired
	private AssetHistoryRepository assetHistoryRepository;
	@Autowired
	private FunctionDAO functionDAO;

	@Autowired
	private FundHibernateDAO fundHibernateDAO;
	
	@Autowired
	private VoucherTypeBean voucherTypeBean;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private DepreciationRepository depreciationRepository;
	
	@PersistenceContext
    private EntityManager em;
	public transient CVoucherHeader voucherHeader = new CVoucherHeader();
	
	public transient WorkflowBean workflowBean = new WorkflowBean();
	private List<VoucherDetails> billDetailslist;
    private List<VoucherDetails> subLedgerlist;
	public VoucherTypeBean getVoucherTypeBean() {
		return voucherTypeBean;
	}
	public void setVoucherTypeBean(VoucherTypeBean voucherTypeBean) {
		this.voucherTypeBean = voucherTypeBean;
	}
	public CVoucherHeader getVoucherHeader() {
		return voucherHeader;
	}
	public void setVoucherHeader(CVoucherHeader voucherHeader) {
		this.voucherHeader = voucherHeader;
	}
	public WorkflowBean getWorkflowBean() {
		return workflowBean;
	}
	public void setWorkflowBean(WorkflowBean workflowBean) {
		this.workflowBean = workflowBean;
	}
	public List<VoucherDetails> getSubLedgerlist() {
		return subLedgerlist;
	}
	public void setSubLedgerlist(List<VoucherDetails> subLedgerlist) {
		this.subLedgerlist = subLedgerlist;
	}

	@Autowired
    @Qualifier("journalVoucherActionHelper")
    private JournalVoucherActionHelper journalVoucherActionHelper;
    //public Disposal saveDisposal(final DisposalRequest disposalRequest, final HttpHeaders headers) {
	public Disposal saveDisposal(final Disposal disposal) {
        //final Disposal disposal = disposalRequest.getDisposal();

        //disposal.setId(assetCommonService.getNextId(Sequence.DISPOSALSEQUENCE));

        /*if (disposal.getAuditDetails() == null)
            disposal.setAuditDetails(assetCommonService.getAuditDetails(disposalRequest.getRequestInfo()));*/
        /*if (assetConfigurationService.getEnabledVoucherGeneration(AssetConfigurationKeys.ENABLEVOUCHERGENERATION,
                disposal.getTenantId()))*/ //as rajat da for time being making it true
			if(true) {
		
            try {
               // log.info("Commencing Voucher Generation for Asset Sale/Disposal");
                //final String voucherNumber = createVoucherForDisposal(disposalRequest, headers); rajat da
            	String voucherNumber =null;
                if (StringUtils.isNotBlank(voucherNumber))
                    disposal.setProfitLossVoucherReference(voucherNumber);
            } catch (final Exception e) {
                throw new RuntimeException("Voucher Generation is failed due to :" + e.getMessage());
            }

        /*final List<AssetCurrentValue> assetCurrentValues = new ArrayList<>();
        final AssetCurrentValue assetCurrentValue = new AssetCurrentValue();
        assetCurrentValue.setAssetId(disposal.getAssetId());
        assetCurrentValue.setAssetTranType(disposal.getTransactionType());
        assetCurrentValue.setCurrentAmount(disposal.getSaleValue() != null ? disposal.getSaleValue() : BigDecimal.ZERO);
        assetCurrentValue.setTenantId(disposal.getTenantId());
        assetCurrentValues.add(assetCurrentValue);
        final AssetCurrentValueRequest assetCurrentValueRequest = new AssetCurrentValueRequest();
        assetCurrentValueRequest.setRequestInfo(disposalRequest.getRequestInfo());
        assetCurrentValueRequest.setAssetCurrentValues(assetCurrentValues);
        currentValueService.createCurrentValue(assetCurrentValueRequest);
        create(disposalRequest);*/
            
            //save disposal after seeting required value
            
            //document upload
            List<DocumentUpload> files = disposal.getDocuments() == null ? null : disposal.getDocuments();//save
            final List<DocumentUpload> documentDetails;
            documentDetails = financialUtils.getDocumentDetails(files, disposal,
                    DisposalService.FILESTORE_MODULEOBJECT);
            if (!documentDetails.isEmpty()) {
                disposal.setDocuments(documentDetails);//save obj
                persistDocuments(documentDetails);
            }
        
    }
			return disposal;
}
	public Disposal createDisposal(Disposal disposal) {
		disposal.setCreateDate(new Date());	
		if(disposal.getTransactionType().equalsIgnoreCase("Sale")) {
		if(null!=disposal.getSaleReason()) {
			disposal.setDisposalReason(disposal.getSaleReason());
		}if(null!=disposal.getSaleDate()) {
			disposal.setDisposalDate(disposal.getSaleDate());
		}if(null!=disposal.getAssetCurrentValueSale()) {
			disposal.setAssetCurrentValue(disposal.getAssetCurrentValueSale());
		}if(null!=disposal.getAssetSaleAccountSale()) {
			disposal.setAssetSaleAccount(disposal.getAssetSaleAccountSale());
		}
		}
		AssetMaster assetMaster = assetRepository.findOne(disposal.getAsset().getId());
		disposal.setAsset(assetMaster);
		disposal.setProfitLossVoucherReference(createVoucher(disposal));
		Disposal saveDisposal = disposalRepository.save(disposal);
		
		List<DocumentUpload> files = disposal.getDocuments() == null ? null : disposal.getDocuments();
        final List<DocumentUpload> documentDetails;
        documentDetails = financialUtils.getDocumentDetails(files, saveDisposal,
                DisposalService.FILESTORE_MODULEOBJECT);//save obj
        if (!documentDetails.isEmpty()) {
        	saveDisposal.setDocuments(documentDetails);//save obj
            persistDocuments(documentDetails);
        }
        //to main asset history
        AssetHistory history=new AssetHistory();
        history.setAsset(disposal.getAsset());
        history.setCreatedBy(disposal.getCreatedBy());
        history.setRecDate(new Date());
        history.setSaleDisposalId(saveDisposal.getId());
        history.setTransactionDate(disposal.getDisposalDate());
        history.setTransactionType(disposal.getTransactionType().toUpperCase());
        if(disposal.getTransactionType().equalsIgnoreCase("Sale")) {
        history.setValueAfterTrxn(disposal.getSaleValue().subtract(disposal.getAssetCurrentValue()));
        history.setTrxnValue(disposal.getSaleValue());
        history.setValueBeforeTrxn(disposal.getAssetCurrentValue());
        //after sale updating asset current value in master
        assetMaster.setCurrentValue(disposal.getSaleValue().longValue());
        masterRepo.save(assetMaster);
        }else {
        	//after Disposal changing status in asset master
        	AssetStatus assetStatus = statusRepo.findByCode(DisposalService.DISPOSED).get(0);
        	assetMaster.setAssetStatus(assetStatus);
        	masterRepo.save(assetMaster);
        }
        assetHistoryRepository.save(history);
		
		return saveDisposal;
	}
	 public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
	        if (documentDetailsList != null && !documentDetailsList.isEmpty())
	            for (final DocumentUpload doc : documentDetailsList)
	                documentUploadRepository.save(doc);
	    }
	 public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
	        return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	    }
	 public Disposal getById(Long id) {
		 
		 Disposal disposal = disposalRepository.findOne(id);
		 
		 if(disposal.getTransactionType().equalsIgnoreCase("Sale")) {
				if(null!=disposal.getDisposalReason()) {
					disposal.setSaleReason(disposal.getDisposalReason());
				}if(null!=disposal.getDisposalDate()) {
					disposal.setSaleDate(disposal.getDisposalDate());
				}if(null!=disposal.getAssetCurrentValue()) {
					disposal.setAssetCurrentValueSale(disposal.getAssetCurrentValue());
				}if(null!=disposal.getAssetSaleAccount()) {
					disposal.setAssetSaleAccountSale(disposal.getAssetSaleAccount());
				}
				}
		 
		 return disposal;
	 }
	 public List<CChartOfAccounts> getAssetAccount(){
		 List<CChartOfAccounts> assetAccount=new ArrayList<CChartOfAccounts>();

		 CChartOfAccounts profit = cChartOfAccountsRepository.findByPurposeIdAndClassification(accountcodePurposeRepository.findByName(ASSET_PROFIT).getId(), 4l);
		 CChartOfAccounts loss = cChartOfAccountsRepository.findByPurposeIdAndClassification(accountcodePurposeRepository.findByName(ASSET_LOSS).getId(), 4l);
		 assetAccount.add(profit);
		 assetAccount.add(loss);
		return assetAccount;
	 }
	 public List<Department> getDepartments() {
	
		 return deptRepo.findAll();
		 
	 }
	 public List<AssetMaster> getAssets(){
		 return masterRepo.findAll();
	 }
	 public List<AssetStatus> getAssetStatus(){
		 return statusRepo.findAll();
	 }
	 public List<AssetCatagory> getAssetCategories(){
		 return assetCategoryRepo.findAll();
	 }
	 public List<AssetMaster> getAssetMasterDetails(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		
		 String defaultQuery="From AssetMaster am where am.assetHeader.applicableForSale='true'";
		 String queryAppender="";
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
				defaultQuery=defaultQuery+" and am.assetStatus.id="+statusId;
			}
			Long deptId=null;
			if(null!=assetBean.getAssetHeader().getDepartment()) {
				deptId=assetBean.getAssetHeader().getDepartment().getId();
				defaultQuery=defaultQuery+" and am.assetHeader.department="+deptId;
			}
			Long assetCategoryId=null;
			if(null!=assetBean.getAssetHeader().getAssetCategory()) {
				assetCategoryId=assetBean.getAssetHeader().getAssetCategory().getId();
				defaultQuery=defaultQuery+" and am.assetHeader.assetCategory.id="+assetCategoryId;
			}
			if(null!=assetBean.getCode()) {
				defaultQuery=defaultQuery+" and am.code="+"'"+assetBean.getCode()+"'";
			}
			if(null!=assetBean.getAssetHeader().getAssetName()) {
				defaultQuery=defaultQuery+" and am.assetHeader.assetName="+"'"+assetBean.getAssetHeader().getAssetName()+"'";
			}

			System.out.println("query "+defaultQuery);
			List<AssetMaster> assetMasterList=new ArrayList<>();
			List<AssetMaster> list=em.createQuery(defaultQuery).getResultList();
			if(null!=list && list.size()>0 ) {
			List<Long> assetIds = list.stream()
			.map(am->am.getId())
			.collect(Collectors.toList());
			List<DepreciationInputs> depeciatedAsset = disposalRepository.getAssetForSaleDisposal(assetIds);
			for(DepreciationInputs di:depeciatedAsset) {
				list.stream()  
		        .filter(am-> am.getId() ==di.getAssetId())
		        .map(assetMaster->assetMaster)
		        .forEach(am->
		        assetMasterList.add(am)
		        );
			}
			}
			return assetMasterList;
//			List<AssetMaster> assetMasterDetails1 = disposalRepository.getAssetMasterDetails1(defaultQuery);
				 /*List<AssetMaster> assetMasterDetails = disposalRepository.getAssetMasterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetCategoryId,deptId,statusId);
				 return assetMasterDetails;*/


	 }
	 public List<AssetMaster> getSaleDisposalDetails(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		 
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			List<AssetMaster> saleOrDisposalDetails = disposalRepository.getSaleOrDisposalDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					assetBean.getAssetHeader().getDepartment().getCode(), statusId);
				 return saleOrDisposalDetails;
	 }
	 public List<Disposal> getFromDisposal(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		 String query="From Disposal dp where";
		 boolean flag=false;
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
				if(flag) {
					query=query+" and dp.asset.assetStatus.id="+statusId;
				}else {
					query=query+" dp.asset.assetStatus.id="+statusId;
				}
				
				flag=true;
			}
			Long deptId=null;
			if(null!=assetBean.getAssetHeader().getDepartment()) {
				deptId=assetBean.getAssetHeader().getDepartment().getId();
				
				if(flag) {
					query=query+" and dp.asset.assetHeader.department="+deptId;
				}else {
					query=query+" dp.asset.assetHeader.department="+deptId;
				}
				flag=true;
			}
			Long assetCategoryId=null;
			if(null!=assetBean.getAssetHeader().getAssetCategory()) {
				assetCategoryId=assetBean.getAssetHeader().getAssetCategory().getId();
				
				if(flag) {
					query=query+" and dp.asset.assetHeader.assetCategory.id="+assetCategoryId;
				}else {
					query=query+" dp.asset.assetHeader.assetCategory.id="+assetCategoryId;
				}
				flag=true;
			}
			if(null!=assetBean.getCode()) {
				
				if(flag) {
					query=query+" and dp.asset.code="+"'"+assetBean.getCode()+"'";
				}else {
					query=query+" dp.asset.code="+"'"+assetBean.getCode()+"'";
				}
				flag=true;
			}
			if(null!=assetBean.getAssetHeader().getAssetName()) {
				
				if(flag) {
					query=query+" and dp.asset.assetHeader.assetName="+"'"+assetBean.getAssetHeader().getAssetName()+"'";
				}else {
					query=query+" dp.asset.assetHeader.assetName="+"'"+assetBean.getAssetHeader().getAssetName()+"'";
				}
				flag=true;
			}
			System.out.println("query "+query);
			
			List<Disposal> disposalList=new ArrayList<>();
			if(flag) {
				List<Disposal> list=em.createQuery(query).getResultList();
				if(list.size()>0) {
					disposalList.addAll(list);
				}
			}else {
				List<Disposal> findAll = disposalRepository.findAll();
				if(findAll.size()>0) {
					disposalList.addAll(findAll);
				}
			}
			
			
			
			return disposalList;
			/*List<Disposal> disposalList = disposalRepository.getSaleAndDisposalList(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					deptId, statusId);
			return disposalList;*/
	 }
	 public AssetMaster getAssetById(Long aasetId) {
		 
		 return masterRepo.findOne(aasetId);
	 }
	 
	 public String createVoucher(Disposal disposal) {
			String voucherNumber = "";
			CFunction function = new CFunction();
			Fund fund = new Fund();
			fund = fundHibernateDAO.fundByCode("01");
			CFunction fun = functionDAO.getFunctionById(Long.valueOf(disposal.getAsset().getAssetHeader().getFunction()));
			voucherTypeBean.setVoucherName("JVGeneral");
			voucherTypeBean.setVoucherType("Journal Voucher");
			voucherTypeBean.setVoucherSubType("JVGeneral");
			voucherHeader = new CVoucherHeader();
			voucherHeader.setBackdateentry("N");
			voucherHeader.setFileno("");
			voucherHeader.setDescription(disposal.getSaleReason());
			voucherHeader.setFundId(fund);
			voucherHeader.setVoucherDate(new Date());
			voucherHeader.setVouchermis(new Vouchermis());
			voucherHeader.getVouchermis().setFunction(fun);
			voucherHeader.getVouchermis().setDepartmentcode(disposal.getAsset().getAssetHeader().getDepartment().getCode());
			workflowBean.setWorkFlowAction("CreateAndApprove");
			billDetailslist = new ArrayList<VoucherDetails>();
			VoucherDetails vd1 = new VoucherDetails();
			if(disposal.getTransactionType().equalsIgnoreCase("sale"))
			{
				vd1.setCreditAmountDetail(disposal.getSaleValue());
			}
			else
			{
				vd1.setCreditAmountDetail(new BigDecimal(disposal.getAsset().getCurrentValue()));
			}
			
			vd1.setDebitAmountDetail(new BigDecimal(0));
			vd1.setGlcodeDetail(disposal.getAsset().getAssetHeader().getAssetCategory().getAssetAccountCode().getGlcode());
			billDetailslist.add(vd1);
			VoucherDetails vd2 = new VoucherDetails();
			vd2.setCreditAmountDetail(new BigDecimal(0));
			if(disposal.getTransactionType().equalsIgnoreCase("sale"))
			{
				vd2.setDebitAmountDetail(disposal.getSaleValue());
			}
			else
			{
				vd2.setDebitAmountDetail(new BigDecimal(disposal.getAsset().getCurrentValue()));
			}
			
			vd2.setGlcodeDetail(disposal.getAssetSaleAccount().getGlcode());
			billDetailslist.add(vd2);
			subLedgerlist = new ArrayList<VoucherDetails>();
			voucherHeader = journalVoucherActionHelper.createVcouher(billDetailslist, subLedgerlist, voucherHeader,
					voucherTypeBean, workflowBean);
			voucherNumber=voucherHeader.getVoucherNumber();
			return voucherNumber;
		}
}