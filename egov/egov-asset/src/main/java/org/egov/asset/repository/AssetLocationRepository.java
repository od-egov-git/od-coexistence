package org.egov.asset.repository;

import org.egov.asset.model.AssetLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetLocationRepository extends JpaRepository<AssetLocation, Long> {

}
