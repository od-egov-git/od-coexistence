package org.egov.asset.repository;

import org.egov.asset.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetStatusRepository extends JpaRepository<AssetStatus, Long> {

}
