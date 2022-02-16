package org.egov.asset.repository;

import java.util.List;

import org.egov.asset.model.AssetRevaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RevaluationRepository extends JpaRepository<AssetRevaluation, Long>{
	
	@Query(value = "From AssetRevaluation am where am.assetMaster.code=:code or am.assetMaster.assetHeader.assetName=:name or am.assetMaster.assetHeader.assetCategory.id=:category or am.assetMaster.assetHeader.department=:department  or am.assetMaster.assetStatus.id=:status")
	public List<AssetRevaluation> getAssetMasterDetails(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") String department, @Param("status") Long status);

}
