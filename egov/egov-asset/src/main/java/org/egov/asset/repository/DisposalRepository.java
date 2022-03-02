package org.egov.asset.repository;

import java.util.List;

import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.DepreciationInputs;
import org.egov.asset.model.Disposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisposalRepository extends JpaRepository<Disposal, Long> {
	@Query(value = "From AssetMaster am left join Disposal dp on dp.asset.id=am.id where am.code=:code or am.assetHeader.assetName=:name or am.assetHeader.assetCategory.id=:category or am.assetHeader.department=:department  or am.assetStatus.id=:status")
	public List<AssetMaster> getSaleOrDisposalDetails(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") String department, @Param("status") Long status);


	@Query(value = "From AssetMaster am inner join DepreciationInputs di on di.assetId=am.id where am.code=:code or am.assetHeader.assetName=:name or am.assetHeader.assetCategory.id=:category or am.assetHeader.department=:department  or am.assetStatus.id=:status and am.assetHeader.applicableForSale='true'")
	public List<AssetMaster> getAssetMasterDetails(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") Long department, @Param("status") Long status);

	/*@Query(value = "From AssetMaster am where am.code=:code or am.assetHeader.assetName=:name or am.assetHeader.assetCategory.id=:category or am.assetHeader.department.id=:department  or am.assetStatus.id=:status")
	public List<AssetMaster> getAssetMasterDetails(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") Long department, @Param("status") Long status);
*/
	@Query(value = "From Disposal dp where dp.asset.code=:code or dp.asset.assetHeader.assetName=:name or dp.asset.assetHeader.assetCategory.id=:category or dp.asset.assetHeader.department=:department  or dp.asset.assetStatus.id=:status")
	public List<Disposal> getSaleAndDisposalList(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") Long department, @Param("status") Long status);
	
	@Query("FROM DepreciationInputs di WHERE di.assetId IN (:assetId)")
	public List<DepreciationInputs> getAssetForSaleDisposal(@Param("assetId") List<Long> assetId);
	
	/*@Query(value = ":query")
	public List<AssetMaster> getAssetMasterDetails1(@Param("query") String query);*/

}
