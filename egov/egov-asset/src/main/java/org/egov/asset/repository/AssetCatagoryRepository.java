package org.egov.asset.repository;

import java.util.List;

import org.egov.asset.model.AssetCatagory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetCatagoryRepository extends JpaRepository<AssetCatagory, Long> {
public AssetCatagory findByName(String name);
@Query(value = "SELECT nextval('SEQ_ASSET_CODE')", nativeQuery =true)
public Long getNextValMySequence();
@Query(value="FROM  AssetCatagory ac where ac.name= :name or ac.assetCatagoryType.id= :id")
public List<AssetCatagory> findBynameorAssetCataType(@Param("name") String name,@Param("id") Long id);
@Query(value="FROM  AssetCatagory ac where ac.name like %:name% or ac.assetCatagoryType.id= :id")
public List<AssetCatagory> findBynameContainingOrAssetCataType(@Param("name") String name,@Param("id") Long id);
}
