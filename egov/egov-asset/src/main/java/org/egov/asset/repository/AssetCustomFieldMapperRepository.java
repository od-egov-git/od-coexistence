package org.egov.asset.repository;

import org.egov.asset.model.AssetCustomFieldMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetCustomFieldMapperRepository extends JpaRepository<AssetCustomFieldMapper, Long> {

}
