package org.egov.collection.service;

import org.egov.egf.contract.model.MisReceiptsPOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.collection.repository.MisReceiptDetailRepo;
import org.egov.egf.contract.model.MisReceiptDetail;
import org.springframework.stereotype.Service;

@Service
public class MisReceiptDetailService {

		@Autowired
		MisReceiptDetailRepo misReceiptDetailRepo;
		
		public MisReceiptDetail save(MisReceiptDetail misReceiptDetail) {
			MisReceiptDetail m =null;
			try {
				m = misReceiptDetailRepo.save(misReceiptDetail);
			}catch(Exception ex) {
				m=null;
				ex.printStackTrace();
			}
			return m;
			
		}
		
		
		
		public MisReceiptDetail findByReceipts(MisReceiptsPOJO misReceiptDetail) {
			MisReceiptDetail m =null;
			if(null!=misReceiptDetail.getReceipt_number()) {
				try {
					m = misReceiptDetailRepo.findUserByReceipt_number(misReceiptDetail.getReceipt_number());
					
				}catch(Exception ex) {
					m=null;
					ex.printStackTrace();
				}
			}
			
			return m;
			
		}
		
}