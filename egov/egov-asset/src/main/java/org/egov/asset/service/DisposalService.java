package org.egov.asset.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.Disposal;
import org.egov.asset.repository.AccountcodePurposeRepository;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetHistoryRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.AssetStatusRepository;
import org.egov.asset.repository.DisposalRepository;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.model.bills.DocumentUpload;
import org.springframework.beans.factory.annotation.Autowired;
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
		Random random = new Random(); 
		int y = random.nextInt(1000);
		disposal.setProfitLossVoucherReference("R/123/2022/18/"+y);
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
        history.setTransactionType(disposal.getTransactionType());
        if(disposal.getTransactionType().equalsIgnoreCase("Sale")) {
        history.setValueAfterTrxn(disposal.getAssetCurrentValue().subtract(disposal.getSaleValue()));
        history.setTrxnValue(disposal.getSaleValue());
        history.setValueBeforeTrxn(disposal.getAssetCurrentValue());
        }else {
        	
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
		 return microserviceUtils.getDepartments();
		 
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
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			
			
				 return disposalRepository.getAssetMasterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					assetBean.getAssetHeader().getDepartment(), statusId);
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
					assetBean.getAssetHeader().getDepartment(), statusId);
				 return saleOrDisposalDetails;
	 }
	 public List<Disposal> getFromDisposal(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			
			List<Disposal> disposalList = disposalRepository.getSaleAndDisposalList(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					assetBean.getAssetHeader().getDepartment(), statusId);
			return disposalList;
	 }
	 public AssetMaster getAssetById(Long aasetId) {
		 
		 return masterRepo.findOne(aasetId);
	 }
}