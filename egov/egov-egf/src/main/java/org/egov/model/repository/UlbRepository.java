package org.egov.model.repository;

import java.util.List;

import org.egov.model.report.Ulb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UlbRepository extends JpaRepository<Ulb, Long>{
	
	@Query(
			  value = "SELECT ULB_NAME FROM GENERIC.ULB_LIST", 
			  nativeQuery = true
			  )
	public List<String> getUlbNames();

}
