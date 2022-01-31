package org.egov.asset.repository;

import org.egov.asset.model.AssetRevaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevaluationRepository extends JpaRepository<AssetRevaluation, Long>{

}
