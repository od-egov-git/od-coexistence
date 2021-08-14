package org.egov.egf.voucher.repository;

import org.egov.commons.Migration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationRepo extends JpaRepository<Migration, Long>{
	
	
}
