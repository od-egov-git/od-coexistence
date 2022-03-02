package org.egov.infra.microservice.models;

import java.math.BigDecimal;

public class Amount {

BigDecimal paAmount;
BigDecimal gstAmount;
BigDecimal totalAmount;
String department;
public BigDecimal getPaAmount() {
return paAmount;
}
public void setPaAmount(BigDecimal paAmount) {
this.paAmount = paAmount;
}
public BigDecimal getGstAmount() {
return gstAmount;
}
public void setGstAmount(BigDecimal gstAmount) {
this.gstAmount = gstAmount;
}
public BigDecimal getTotalAmount() {
return totalAmount;
}
public void setTotalAmount(BigDecimal totalAmount) {
this.totalAmount = totalAmount;
}
public String getDepartment() {
	return department;
}
public void setDepartment(String department) {
	this.department = department;
}
}