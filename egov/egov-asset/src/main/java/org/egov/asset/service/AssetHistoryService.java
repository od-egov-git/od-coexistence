package org.egov.asset.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.repository.AssetHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetHistoryService {

	@Autowired
	private AssetHistoryRepository assetHistoryRepository;
	@PersistenceContext
    private EntityManager em;
	public List<AssetMaster> getAssetMasterDetails(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		 Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			Long deptId=null;
			if(null!=assetBean.getAssetHeader().getDepartment()) {
				deptId=assetBean.getAssetHeader().getDepartment().getId();
			}
			
				 List<AssetMaster> assetMasetrHisory = assetHistoryRepository.getAssetMasetrHisory(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					deptId, statusId);
				 
				return assetMasetrHisory;
	 }
	public List<AssetMaster> getAssetMasterDetail(AssetMaster assetBean){
		 /*String statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = String.valueOf(assetBean.getAssetStatus().getId());
			}*/
		
		 String defaultQuery="From AssetMaster am where";
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
			String finalQuery = defaultQuery.replace("where and", "where");
			System.out.println("finalQuery "+finalQuery);
			List<AssetMaster> list=em.createQuery(finalQuery).getResultList();
			
			return list;

	 }
	public List<AssetHistory> getAssetHistoryByAssetId(Long id){
		List<AssetHistory> findAllById = assetHistoryRepository.findAllByAssetId(id);
		
		return findAllById;
	}
}
