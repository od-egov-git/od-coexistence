package org.egov.asset.repository;

import org.egov.asset.model.AssetLocationStreet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetLocationStreetRepository extends JpaRepository<AssetLocationStreet, Long> {

}
