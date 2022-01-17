package org.egov.egf.expensebill.service;

import org.egov.commons.Vouchermis;
import org.egov.egf.expensebill.repository.VouchermisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VouchermisService {
	
	@Autowired
	private VouchermisRepository vouchermisRepository;

	public Vouchermis getVouchermisByReceiptNumber(String receiptNumber) {
		
		return vouchermisRepository.getVouchermisByReceiptNumber(receiptNumber);
	}
	
public Vouchermis getVouchermisByVoucherId(Long id) {
		
		return vouchermisRepository.getVouchermisByVoucherId(id);
	}

}
