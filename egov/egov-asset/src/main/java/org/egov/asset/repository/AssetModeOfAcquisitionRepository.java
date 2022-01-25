package org.egov.asset.repository;

import org.egov.asset.model.AssetModeOfAcquisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetModeOfAcquisitionRepository extends JpaRepository<AssetModeOfAcquisition, Long> {

}
