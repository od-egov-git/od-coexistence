package org.egov.collection.service;

import org.egov.infra.microservice.models.MisRemittanceDetails;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.egov.collection.repository.MisRemittanceDetailRepo;
import org.egov.infra.microservice.models.MisRemittancePOJO;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.voucher.VoucherDraftDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MisRemittanceDetailService extends PersistenceService<MisRemittanceDetails, Long> {

		@Autowired
		MisRemittanceDetailRepo misRemittanceDetailRepo;
		
		@PersistenceContext
	    private EntityManager entityManager;
		
		@Autowired
		@Qualifier("persistenceService")
		protected PersistenceService persistenceService;
		
		public MisRemittanceDetailService(Class<MisRemittanceDetails> MisRemittanceDetails) {
			super(MisRemittanceDetails);
			// TODO Auto-generated constructor stub
		}
	    
	    public MisRemittanceDetailService() {
			super(MisRemittanceDetails.class);
		}
		
		public MisRemittanceDetails save(MisRemittanceDetails MisRemittanceDetail) {
			MisRemittanceDetails m =null;
			try {
				m = null;//misRemittanceDetailRepo.create(MisRemittanceDetail);
			}catch(Exception ex) {
				m=null;
				ex.printStackTrace();
			}
			return m;
			
		}
}
