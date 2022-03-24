package org.egov.asset.repository;

import java.util.List;

import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long>{

	@Query(value = "From AssetMaster am where am.code=:code or am.assetHeader.assetName=:name or am.assetHeader.assetCategory.id=:category or am.assetHeader.department.id=:department  or am.assetStatus.id=:status")
	public List<AssetMaster> getAssetMasetrHisory(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") Long department, @Param("status") Long status);
	
	@Query(value = "From AssetHistory ah where ah.asset.id=:id")
	public List<AssetHistory> findAllByAssetId(@Param("id")Long id);
}
