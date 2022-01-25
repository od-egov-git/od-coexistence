package org.egov.asset.repository;

import org.egov.asset.model.AssetLocationElectionWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetElectionWardRepository extends JpaRepository<AssetLocationElectionWard, Long> {

}
