package org.egov.asset.repository;

import org.egov.asset.model.AssetLocationZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetLocationZoneRepository extends JpaRepository<AssetLocationZone, Long> {

}
