package org.egov.asset.service;

import java.util.List;

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
import org.springframework.stereotype.Service;


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
    
    //@Transactional
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
    	LOG.info("FIle No.............."+savedAssetMaster.getFileno());
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
    
    public AssetMaster update(final AssetMaster assetMaster) {
    	
    	LOG.info("Asset Id.."+assetMaster.getId());
        return assetMasterRepository.save(assetMaster);
    }
    
    public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
        if (documentDetailsList != null && !documentDetailsList.isEmpty())
            for (final DocumentUpload doc : documentDetailsList)
                documentUploadRepository.save(doc);
    }
    
    public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
        return documentUploadRepository.findByObjectForAssetMasterDocument(objectId, objectType);//findByObjectIdAndObjectType(objectId, objectType);
    }
    
    public AssetMaster getById(final Long id) {
        return assetMasterRepository.findOne(id);
    }
    
}