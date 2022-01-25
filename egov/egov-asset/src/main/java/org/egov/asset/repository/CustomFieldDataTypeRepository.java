package org.egov.asset.repository;

import org.egov.asset.model.CustomFieldDataType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomFieldDataTypeRepository extends JpaRepository<CustomFieldDataType, Long> {

	public CustomFieldDataType findByDataTypes(String dataTypes);
}
