package org.egov.asset.repository;

import org.egov.asset.model.CustomeFields;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomeFieldsRepository extends JpaRepository<CustomeFields, Long> {
public void deleteById(Long id);
}
