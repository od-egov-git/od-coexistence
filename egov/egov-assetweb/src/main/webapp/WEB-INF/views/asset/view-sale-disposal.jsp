<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/app/js/assetcategory.js?rnd=${app_release_no}' context='/services/asset'/>"></script>

<style>
/* .sale-form-popup {
  display: none;
}
.disposal-form-popup {
  display: none;
}  */
</style>

<div class="container">
<form:form action="createDisposal" modelAttribute="disposal" method="POST" enctype="multipart/form-data">
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<c:if test="${not empty successMsg}">
			<div class="alert alert-success" role="alert">${successMsg}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">${errorMessage}</div>
		</c:if>
			<div class="panel-title">
				<spring:message code="lbl.asset.disposal" text="Asset Sale Or Disposal" />
			</div>
	</div>


<div class="panel-body">
	<div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.asset.code" text="Asset Code" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="asset.code" value="${asset.code}" readonly="true"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.asset.name" text="Asset Name" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="asset.assetHeader.assetName" value="${asset.assetHeader.assetName}" readonly="true"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.asset.description" text="Asset Description" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="asset.assetHeader.description" value="${asset.assetHeader.description}" readonly="true"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.assetcategory.name" text="Asset Category Name" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="asset.assetHeader.assetCategory.name" value="${asset.assetHeader.assetCategory.name}" readonly="true"/>
		</div>
		
	
		</div>
		
		</div>
		</div>
		
		
		
	<div class="panel panel-primary" data-collapsed="0">	
	<div class="panel-heading">
	<div class="panel-title">
		<spring:message code="lbl.asset.disposal.details" text="Asset Sale or Disposal details"/>
	</div>
	</div>
	
	<div class="panel-body">
	
		<div class="form-popup">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.type" text="Type" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:select id="saleOrDisposalSelect"  onchange="displaySaleOrDisposal()" path="transactionType" class="form-control"> 
			<form:option value="">-select value-</form:option>
			<form:option value="Disposal">Disposal</form:option>
			<form:option value="Sale">Sale</form:option>
			</form:select>
		</div>
		</div>
		<c:if test="${disposal.transactionType == 'Disposal'}">
		<div class="disposal-form-popup" id="disposal">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.date" text="Disposal date" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="disposalDate" class="form-control datepicker" readonly="true" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
		</div>
		
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.reason" text="Disposal reason" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea path="disposalReason" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.assetcurrentvalue" text="Current Value Of The Asset" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="assetCurrentValue" readonly="true"/>
		</div>
		
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.accountcode" text="Asset Disposal Account Code" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<%-- <form:select path="assetSaleAccount" class="form-control" readonly="true" > 
			<form:option value="">-select value-</form:option>
			<form:options items="${assetAccounts}" itemLabel="name" itemValue="id"/>
			<c:forEach var="acc" items="${assetAccounts}">
        <form:option value="${acc.id}"><c:out value="${acc.glcode}-${acc.name} "/></form:option>
   	 	</c:forEach>
			</form:select> --%>
			<form:input path="assetSaleAccount.name" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.voucherreferencenumber" text="Voucher Reference number" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" id="profitLossVoucherReference" path="profitLossVoucherReference" readonly="true"/>
	
		</div>
		</div>
		</c:if>
		<c:if test="${disposal.transactionType == 'Sale'}">
		<div class="sale-form-popup" id="sale">
		
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.date" text="Sale date" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="saleDate" class="form-control datepicker" data-date-end-date="0d" readonly="true" placeholder="DD/MM/YYYY"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.reason" text="Sale reason" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea path="saleReason" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.buyername" text="Sale party name" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="buyerName" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.buyeraddress" text="Sale party address" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea path="buyerAddress" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.pancardnumber" text="Pan Card Number" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="panCardNumber" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.aadharnumber" text="Aadhar Card Number" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="aadharCardNumber" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.assetcurrentvalue" text="Current Value Of The Asset" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" readonly="true" data-pattern="alphanumericwithspecialcharacters" id="assetcurrentvalue" path="assetCurrentValueSale"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.accountcode" text="Asset Disposal Account Code" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<%-- <form:select path="assetSaleAccountSale" readonly="true" class="form-control"> 
			<form:option value="">-select value-</form:option>
			<form:options items="${assetAccounts}" itemLabel="name" itemValue="id"/>
			<c:forEach var="acc" items="${assetAccounts}">
        <form:option value="${acc.id}"><c:out value="${acc.glcode}-${acc.name} "/></form:option>
   	 	</c:forEach>
			</form:select> --%>
			<form:input path="assetSaleAccount.name" class="form-control" readonly="true" />
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.salevalue" text="Sale Value" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" readonly="true" onkeyup="saleValueValidation()" data-pattern="alphanumericwithspecialcharacters" id="salevalue" oninput="calculateProfitLoss()" path="saleValue"/>
			<p style="color:red" id="salevaluevalidation"></p>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.profiloss" text="Profit/Loss" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" id="profitloss" value="${disposal.assetCurrentValueSale - disposal.saleValue}" path="" readonly="true"/>
	
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.voucherreferencenumber" text="Voucher Reference number" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" id="profitLossVoucherReference" path="profitLossVoucherReference" readonly="true"/>
	
		</div>
		</div>
		</c:if>
		
		
		
		</div>
		</div>
		<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="lbl.asset.disposal" text="Documents Details" />
			</div>
	</div>


	<div class="panel-body">
		<div class="form-group">
			<table class="table table-bordered">
			<thead>
			<tr>
				<th><spring:message code="lbl.asset.custome.sino" text="SI No."/></th>
				<th><spring:message code="lbl.asset.custome.name" text="Uploaded File"/></th>	
			</tr>
		</thead>
			<tbody>
			<c:if test="${not empty disposal.documents}">
			<c:forEach items="${disposal.documents}" var="document" varStatus="tagStatus">
			
			<tr>
			<td>${tagStatus.index+1}</td>
			<td>
			<a href="/services/EGF/sale/downloadSaleDisposalDoc?disposalId=${disposal.id }&fileStoreId=${document.fileStore.fileStoreId }">${document.fileStore.fileName }</a>
			</td>
			
			</tr>
			
			</c:forEach>
			</c:if>
			
			</tbody>
			</table>
		
	
		</div>
		
	</div>
</div>
		<!-- <div align="center">
			<input type="submit" class="btn btn-primary" disabled="true" name="create" value="Create" />
		</div> -->
		<div align="center"><input type="button" name="button2" id="button2" value="Close" class="btn btn-primary" onclick="window.parent.postMessage('close','*');window.close();"/></div>
		</form:form>
		
		</div>
		
	
	
	
	<script>
	
	/* $(document).ready(function(){
		const  assetValue= document.getElementById("assetcurrentvalue").value;
		const  saleValue= document.getElementById("salevalue").value;
		const profitloss=assetValue-saleValue;
		document.getElementById("profitloss").value=profitloss;
	} */
	
	</script>
		
	