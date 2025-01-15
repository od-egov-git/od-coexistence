package org.egov.egf.contract.model;

import java.io.Serializable;
import java.util.List;

import org.egov.infra.microservice.contract.Pagination;
import org.egov.infra.microservice.models.ResponseInfo;

public class NewBankAccountResponse implements Serializable {
	
    private ResponseInfo responseInfo;
    private List<NewBankAccount> bankaccounts;
    private Pagination page;
    
    public NewBankAccountResponse(ResponseInfo responseInfo, List<NewBankAccount> bankaccounts, Pagination page) {
        this.setResponseInfo(responseInfo);
        this.bankaccounts = bankaccounts;
        this.page = page;
    }
    public NewBankAccountResponse() {
    }
    public List<NewBankAccount> getBankaccounts() {
        return bankaccounts;
    }
    public void setBankaccounts(List<NewBankAccount> bankaccounts) {
        this.bankaccounts = bankaccounts;
    }
    public Pagination getPage() {
        return page;
    }
    public void setPage(Pagination page) {
        this.page = page;
    }
	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}
	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}
    
}
