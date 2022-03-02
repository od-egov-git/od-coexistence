package org.egov.collection.repository;

import org.egov.infra.microservice.models.MisRemittanceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MisRemittanceDetailRepo extends JpaRepository<MisRemittanceDetails, Long>{
	
	 
}
