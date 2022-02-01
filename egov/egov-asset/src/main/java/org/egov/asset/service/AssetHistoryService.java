package org.egov.asset.service;

import java.util.List;

import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.repository.AssetHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetHistoryService {

	@Autowired
	private AssetHistoryRepository assetHistoryRepository;
	
	public List<AssetMaster> getAssetMasterDetails(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			
			
				 List<AssetMaster> assetMasetrHisory = assetHistoryRepository.getAssetMasetrHisory(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					assetBean.getAssetHeader().getDepartment(), statusId);
				 
				return assetMasetrHisory;
	 }
	public List<AssetHistory> getAssetHistoryByAssetId(Long id){
		List<AssetHistory> findAllById = assetHistoryRepository.findAllByAssetId(id);
		
		return findAllById;
	}
}
