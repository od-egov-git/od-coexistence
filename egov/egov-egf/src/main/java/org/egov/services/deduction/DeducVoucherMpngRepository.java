package org.egov.services.deduction;

import org.egov.model.bills.DeducVoucherMpng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeducVoucherMpngRepository extends JpaRepository<DeducVoucherMpng, Long> {

	
}
