package org.egov.egf.contract.model;

import org.egov.egf.contract.model.Migration;
import org.egov.egf.contract.model.MigrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {

		@Autowired
		MigrationRepo mRepo;
		
		public Migration save(Migration migration) {
			Migration m =null;
			try {
				m = mRepo.save(migration);
			}catch(Exception ex) {
				m=null;
				ex.printStackTrace();
			}
			return m;
			
		}
		
		
}
