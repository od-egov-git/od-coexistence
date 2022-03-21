package org.egov.asset.repository;

import org.egov.asset.model.CustomeFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomeFieldsRepository extends JpaRepository<CustomeFields, Long> {
public void deleteById(Long id);
@Query(value = "SELECT nextval('SEQ_CUSTOM_FIELDS')", nativeQuery =true)
public Long getNextValMySequence();
}
