package org.egov.asset.repository;

import org.egov.asset.model.AssetLocality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetLocalityRepository extends JpaRepository<AssetLocality, Long> {

}
