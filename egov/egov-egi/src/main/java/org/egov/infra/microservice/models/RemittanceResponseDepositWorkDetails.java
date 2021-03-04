package org.egov.infra.microservice.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RemittanceResponseDepositWorkDetails   {

  
  @JsonProperty("RemittanceDepositWorkDetail")
  private List<RemittanceDepositWorkDetail> RemittanceDepositWorkDetail;

public List<RemittanceDepositWorkDetail> getRemittanceDepositWorkDetail() {
	return RemittanceDepositWorkDetail;
}

public void setRemittanceDepositWorkDetail(List<RemittanceDepositWorkDetail> remittanceDepositWorkDetail) {
	RemittanceDepositWorkDetail = remittanceDepositWorkDetail;
}
  
}
