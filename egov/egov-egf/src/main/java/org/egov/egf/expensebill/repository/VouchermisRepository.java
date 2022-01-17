package org.egov.egf.expensebill.repository;



import org.egov.commons.Vouchermis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VouchermisRepository extends JpaRepository<Vouchermis, Long>{


	@Query("from Vouchermis where recieptNumber = ?1")
	Vouchermis getVouchermisByReceiptNumber(String receiptNumber);
	
	@Query("from Vouchermis where voucherheaderid.id = ?1")
	Vouchermis getVouchermisByVoucherId(Long id);
    
}
