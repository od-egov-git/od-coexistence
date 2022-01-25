package org.egov.asset.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.asset.model.Disposal;
import org.egov.egf.utils.FinancialUtils;
import org.egov.model.bills.DocumentUpload;
import org.springframework.beans.factory.annotation.Autowired;
public class DisposalService {

	@Autowired
    private FinancialUtils financialUtils;
	final private static String FILESTORE_MODULEOBJECT="disposal";
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
               // persistDocuments(documentDetails);
            }
        
    }
			return disposal;
}
}
