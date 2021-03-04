package com.exilant.eGov.src.reports;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.infstr.services.PersistenceService;
import org.egov.model.voucher.VoucherDraftDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JournalVoucherDraftService extends PersistenceService<VoucherDraftDetails, Long>{
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	@Qualifier("persistenceService")
	protected PersistenceService persistenceService;

    public JournalVoucherDraftService(Class<VoucherDraftDetails> voucherDraftDetails) {
		super(voucherDraftDetails);
		// TODO Auto-generated constructor stub
	}
    
    public JournalVoucherDraftService() {
		super(VoucherDraftDetails.class);
	}


	//private final JournalVoucherDraftRepository journalVoucherDraftRepository;
    
    
   // public List<VoucherDraftDetails> getByExpenditureType(final String voucherNumber) {
    //    return journalVoucherDraftRepository.findByVoucherNumber(voucherNumber);
    //}

}
