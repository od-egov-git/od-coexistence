package org.egov.asset.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.Disposal;
import org.egov.infra.microservice.models.TransactionType;

public class DisposalValidator {

//	public void validateDisposal(final DisposalRequest disposalRequest) {
	public void validateDisposal(final Disposal disposal) {
        //final Disposal disposal = disposalRequest.getDisposal();
        //final String tenantId = disposal.getTenantId();
        //final Set<Long> ids = new HashSet<>();
        //ids.add(disposal.getAssetId());

        //final Asset asset = assetService.getAsset(tenantId, disposal.getAssetId(), disposalRequest.getRequestInfo());
		AssetMaster asset = null;
		//asset = assetRepository.findOne()disposal.getAssetId();
        //log.debug("Asset For Disposal :: " + asset);
        validateAssetForCapitalizedStatus(asset);
        validateSaleAndDisposalDate(disposal,asset);
        if (StringUtils.isEmpty(disposal.getDisposalReason()))
            throw new RuntimeException("Disposal Reason should be present for disposing asset : " + asset.getName());

        verifyPanCardAndAdhaarCardForAssetSale(disposal);
        if (getEnableYearWiseDepreciation("tenantId")) {//ask rajat da
            validateAssetCategoryForVoucherGeneration(asset);

            //if (asset.getAssetCategory() != null && asset.getAssetCategory().getAssetAccount() == null)
            if (asset.getAssetCategory() != null && asset.getAssetCategory().getAssetAccountCode() == null)
                throw new RuntimeException("Asset account should be present for disposing asset : " + asset.getName());

            if (disposal.getAssetSaleAccount() == null)
                throw new RuntimeException(
                        "Asset sale account should be present for asset disposal voucher generation");
        }
        //if (asset.getAssetCategory().getAssetCategoryType().toString().equals(AssetCategoryType.LAND.toString())) {
        if (asset.getAssetCategory().getAssetCatagoryType().getDescription().equals("LAND")) {

            /*final List<Revaluation> revaluation = revaluationService.getRevaluation(tenantId, disposal.getAssetId(),
                    disposalRequest.getRequestInfo());*/ //rajat da
        	List<Revaluation> revaluation =null;
            if (revaluation.isEmpty())
                throw new RuntimeException(
                        "Asset has to be revaluation atleast once for sale/disposal");
        } else {
            /*final List<DepreciationReportCriteria> depreciation = depreciationService.getDepreciation(tenantId,
                    disposal.getAssetId(),
                    disposalRequest.getRequestInfo());*/ //rajat da and abhishek
        	List<Depreciation> depreciation =null;
            if (depreciation.isEmpty())
                throw new RuntimeException(
                        " Asset has to be depreciated atleast once for sale/disposal");
        }

    }
	
	 private void validateAssetForCapitalizedStatus(final AssetMaster asset) {
	        //final List<AssetStatus> assetStatus = assetMasterService.getStatuses(AssetStatusObjectName.ASSETMASTER,Status.CAPITALIZED, asset.getTenantId());
		 //	ask arnab regarding this
		 	List<AssetStatus> assetStatus =null;
	        //log.debug("asset status ::" + assetStatus);
	        if (!assetStatus.isEmpty()) {
	           // final String status = assetStatus.get(0).getStatusValues().get(0).getCode();
	        	String status =null;
	            if (!status.equals(asset.getAssetStatus()))
	                throw new RuntimeException("Status of Asset " + asset
	                        + " Should be CAPITALIZED for Revaluation, Depreciation and Disposal/sale");
	        } else
	            throw new RuntimeException(
	                    "Status of asset :" + asset+ "doesn't exists for tenant id : " /*+ asset.getTenantId()*/);
	    }
	 private void validateSaleAndDisposalDate(final Disposal disposal,final Asset asset) {
		 Date disposalDate=null;
		 Date assetCreationDate=null;
		try {
			disposalDate = new SimpleDateFormat("yyyy-mm-dd").parse(disposal.getDisposalDate());
			assetCreationDate=new SimpleDateFormat("yyyy-mm-dd").parse(asset.getDateOfCreation());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		  
	       //if (disposal.getDisposalDate()<asset.getDateOfCreation())
		 if (disposalDate.before(assetCreationDate))
	           throw new RuntimeException("Sale/Disposal date should not be before the date of creation of asset");
	    }
	 private void verifyPanCardAndAdhaarCardForAssetSale(final Disposal disposal) {
	        final String adhaarcardNumber = disposal.getAadharCardNumber();
	        final String pancardNumber = disposal.getPanCardNumber();
	        final Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
	        final Matcher matcher = pattern.matcher(pancardNumber);

	        final boolean transactionTypeSaleCheck = TransactionType.SALE.toString().compareTo(disposal.getTransactionType()) == 0;

	        if (transactionTypeSaleCheck && StringUtils.isEmpty(adhaarcardNumber))
	            throw new RuntimeException("Aadhar Card Number is necessary for asset sale");

	        if (transactionTypeSaleCheck && !StringUtils.isEmpty(adhaarcardNumber) && adhaarcardNumber.length() < 12
	                && !adhaarcardNumber.matches("\\d+"))
	            throw new RuntimeException("Aadhar Card Number should be numeric and should have length 12");

	        if (transactionTypeSaleCheck && StringUtils.isEmpty(pancardNumber))
	            throw new RuntimeException("PAN Card Number is necessary for asset sale");

	        if (transactionTypeSaleCheck && !StringUtils.isEmpty(pancardNumber) && !matcher.matches())
	            throw new RuntimeException("PAN Card Number Should be in Format : ABCDE1234F");
	    }
	 private boolean getEnableYearWiseDepreciation(final String tenantId) {
	        /*return assetConfigurationService.getEnabledVoucherGeneration(AssetConfigurationKeys.ENABLEVOUCHERGENERATION,
	                tenantId);*/
		 return true;
	    }
	 private void validateAssetCategoryForVoucherGeneration(final AssetMaster asset) {
	        if (asset.getAssetCategory() == null)
	            throw new RuntimeException(
	                    "Asset Category should be present for asset " + asset.getName() + " for voucher generation");
	    }
}
