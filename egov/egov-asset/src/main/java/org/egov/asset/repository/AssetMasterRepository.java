package org.egov.asset.repository;

import java.util.List;

import org.egov.asset.model.AssetMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetMasterRepository extends JpaRepository<AssetMaster, Long> {

	@Query(value = "From AssetMaster am where am.code=:code or am.assetHeader.assetName=:name or am.assetHeader.assetCategory.id=:category or am.assetHeader.department=:department  or am.assetStatus.id=:status")
	public List<AssetMaster> getAssetMasterDetails(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("department") String department, @Param("status") Long status);
	
	@Query(value = "SELECT nextval('SEQ_asset_master')", nativeQuery =true)
	public Long getNextValMySequence();
	
	@Query(value = "From AssetMaster am where am.code=:code or am.assetHeader.assetName=:name or am.assetHeader.assetCategory.id=:category or am.assetLocation.id=:locationId or am.assetHeader.description=:description  or am.assetStatus.id=:status")
	public List<AssetMaster> getAssetMasterRegisterDetails(@Param("code") String code, @Param("name") String name, @Param("category") Long category, @Param("locationId") Long locationId, @Param("description") String description, @Param("status") Long status);
}

