package org.egov.model.bills;

import java.math.BigDecimal;

public class EgBillPayeedetailsPojo {
	
	 	
	    
	    private String accounDetailTypeName;
	    private String accounDetailKeyName;
	    
	    private BigDecimal debitAmount;
	    private BigDecimal creditAmount;
	    
		
		public String getAccounDetailTypeName() {
			return accounDetailTypeName;
		}
		public void setAccounDetailTypeName(String accounDetailTypeName) {
			this.accounDetailTypeName = accounDetailTypeName;
		}
		
		public BigDecimal getDebitAmount() {
			return debitAmount;
		}
		public void setDebitAmount(BigDecimal debitAmount) {
			this.debitAmount = debitAmount;
		}
		public BigDecimal getCreditAmount() {
			return creditAmount;
		}
		public void setCreditAmount(BigDecimal creditAmount) {
			this.creditAmount = creditAmount;
		}
		
		public String getAccounDetailKeyName() {
			return accounDetailKeyName;
		}
		public void setAccounDetailKeyName(String accounDetailKeyName) {
			this.accounDetailKeyName = accounDetailKeyName;
		}
		
		
		
	    
}

