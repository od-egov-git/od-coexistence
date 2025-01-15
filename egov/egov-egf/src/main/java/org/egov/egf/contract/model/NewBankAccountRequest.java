package org.egov.egf.contract.model;

import org.egov.infra.microservice.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewBankAccountRequest {
	
	 	private String tenantId;
	    private Integer offset;
	    private Integer pageSize;
	    private String sortBy;
	    
	    @JsonProperty("RequestInfo")
	    private RequestInfo requestInfo;
	    
		public String getTenantId() {
			return tenantId;
		}
		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}
		public Integer getOffset() {
			return offset;
		}
		public void setOffset(Integer offset) {
			this.offset = offset;
		}
		public Integer getPageSize() {
			return pageSize;
		}
		public void setPageSize(Integer pageSize) {
			this.pageSize = pageSize;
		}
		public String getSortBy() {
			return sortBy;
		}
		public void setSortBy(String sortBy) {
			this.sortBy = sortBy;
		}
		public RequestInfo getRequestInfo() {
			return requestInfo;
		}
		public void setRequestInfo(RequestInfo requestInfo) {
			this.requestInfo = requestInfo;
		}
	
}
