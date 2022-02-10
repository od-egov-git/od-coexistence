package org.egov.asset.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.asset.model.AssetHeader;
import org.egov.asset.model.AssetLocation;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.repository.AssetHeaderRepository;
import org.egov.asset.repository.AssetLocationRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.utils.AssetConstant;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.script.service.ScriptService;
import org.egov.model.bills.DocumentUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
//@Transactional(readOnly = true)
public class AssetService {

	private static final Logger LOG = LoggerFactory.getLogger(AssetService.class);
	
	@Autowired
    private AssetMasterRepository assetMasterRepository;
	@Autowired
    private AssetHeaderRepository assetHeaderRepository;
	@Autowired
    private AssetLocationRepository assetLocationRepository;
	
	@PersistenceContext
	private EntityManager em;
	
    private final ScriptService scriptExecutionService;
    
    @Autowired
    private DocumentUploadRepository documentUploadRepository;
    
    @Autowired
    private FinancialUtils financialUtils;
    
    @Autowired
    public AssetService(final AssetMasterRepository assetMasterRepository, 
    		final AssetHeaderRepository assetHeaderRepository, 
    		final AssetLocationRepository assetLocationRepository,
    		final ScriptService scriptExecutionService) {
        this.assetMasterRepository = assetMasterRepository;
        this.assetHeaderRepository = assetHeaderRepository;
        this.assetLocationRepository = assetLocationRepository;
        this.scriptExecutionService = scriptExecutionService;
    }
    
    @Transactional
    public AssetMaster create(final AssetMaster assetMaster) {
    	
    	AssetMaster savedAssetMaster = assetMasterRepository.save(assetMaster);
    	 List<DocumentUpload> files = assetMaster.getDocumentDetail() == null ? null : assetMaster.getDocumentDetail();
         final List<DocumentUpload> documentDetails;
         documentDetails = financialUtils.getDocumentDetails(files, savedAssetMaster,
                 AssetConstant.FILESTORE_MODULEOBJECT_ASSET);
         if (!documentDetails.isEmpty()) {
        	 savedAssetMaster.setDocumentDetail(documentDetails);
             persistDocuments(documentDetails);
             //documentDetails.get(0).get
         }
        return assetMasterRepository.save(savedAssetMaster);
    }
    
    //@Transactional
    public AssetHeader create(final AssetHeader assetHeader) {
        return assetHeaderRepository.save(assetHeader);
    }
    
    //@Transactional
    public AssetLocation create(final AssetLocation assetLocation) {
        return assetLocationRepository.save(assetLocation);
    }
    
    @Transactional
    public AssetMaster update(final AssetMaster assetMaster) {
    	
        return assetMasterRepository.save(assetMaster);
    }
    
    public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
    	DocumentUpload docs = new DocumentUpload();
        if (documentDetailsList != null && !documentDetailsList.isEmpty())
            for (final DocumentUpload doc : documentDetailsList) {
            	docs = documentUploadRepository.save(doc);
            }
          //docs.getFileStore().getFileStoreId();      
    }
    
    public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
        return documentUploadRepository.findByObjectForAssetMasterDocument(objectId, objectType);//findByObjectIdAndObjectType(objectId, objectType);
    }
    
    public AssetMaster getById(final Long id) {
        return assetMasterRepository.findOne(id);
    }
    
    
    public List<AssetMaster> searchAssets(final AssetMaster assetBean) {
    	String defaultQuery = "";
    	//StringBuilder queryBuilder = new StringBuilder("FROM AssetMaster am where am.assetHeader.assetCategory.id=1 
    	//and am.assetHeader.assetName LIKE '%Test%'");
    	try {
    		defaultQuery = "FROM AssetMaster am where am.assetHeader.assetCategory.id="+assetBean.getAssetHeader().getAssetCategory().getId();
    		if(null != assetBean.getAssetHeader().getAssetName()) {
    			defaultQuery += " and am.assetHeader.assetName LIKE '%"+assetBean.getAssetHeader().getAssetName()+"%'";
    		}
    		if(null != assetBean.getCode()) {
    			defaultQuery += " and am.code LIKE '%"+assetBean.getCode()+"%'";
    		}
    		if(null != assetBean.getAssetHeader().getDepartment()) {
    			defaultQuery += " and am.assetHeader.department="+assetBean.getAssetHeader().getDepartment().getId();
    		}
    		if(null != assetBean.getAssetStatus()) {
    			defaultQuery += " and am.assetStatus.id="+assetBean.getAssetStatus().getId();
    		}
    		//For Register Search
    		if(null != assetBean.getAssetHeader().getDescription()) {
    			defaultQuery += " and am.assetHeader.description LIKE '%"+assetBean.getAssetHeader().getDescription()+"%'";
    		}
    		if(null != assetBean.getAssetLocation()) {
    			defaultQuery += " and am.assetLocation.location.id="+assetBean.getAssetLocation().getLocation().getId();
    		}
    	}catch(Exception e) {
    		LOG.error("Error Occured : While Search Results. Error -> "+e.getMessage());
    	}
    	List<AssetMaster> assetList=em.createQuery(defaultQuery).getResultList();
        return assetList;
    }
}