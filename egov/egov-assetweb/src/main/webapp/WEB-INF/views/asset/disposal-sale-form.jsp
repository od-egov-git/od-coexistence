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
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<style>
/* .sale-form-popup {
  display: none;
}
.disposal-form-popup {
  display: none;
}  */
</style>

<div class="container">
<form:form action="${contextPath}/sale/createDisposal" modelAttribute="disposal" method="POST" enctype="multipart/form-data" >
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<c:if test="${not empty successMsg}">
			<div class="alert alert-info" role="alert">${successMsg}</div>
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
		
		<form:hidden path="asset.id"/>
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
			<form:select id="saleOrDisposalSelect"  onchange="displaySaleOrDisposal()" path="transactionType" class="form-control" required="required"> 
			<form:option value="">-select value-</form:option>
			<form:option value="Disposal">Disposal</form:option>
			<form:option value="Sale">Sale</form:option>
			</form:select>
		</div>
		</div>
		<div class="disposal-form-popup" style="display: none;" id="disposal">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.date" text="Disposal date" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="disposalDate"  class="form-control datepicker" id="ddisposaldate" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
			<p cssClass="add-margin error-msg" id="ddisposaldateError" style="color:red"/>
		</div>
		
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.reason" text="Disposal reason" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea path="disposalReason" onchange="removeDisposalErrorMsg()"  class="form-control" id="ddisposalreason"/>
			<p cssClass="add-margin error-msg" id="ddisposalreasonError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.assetcurrentvalue" text="Current Value Of The Asset" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" value="${disposal.asset.currentValue}" readonly="true" path="assetCurrentValue"/>
		</div>
		
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.accountcode" text="Asset Disposal Account Code" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="assetSaleAccount" class="form-control" id="ddisposalaccount" onchange="removeDisposalErrorMsg()" > 
			<form:option value="">-select value-</form:option>
			<c:forEach var="acc" items="${assetAccounts}">
        <form:option value="${acc.id}"><c:out value="${acc.glcode}-${acc.name} "/></form:option>
   	 	</c:forEach>
			<<%-- form:options items="${assetAccounts}" itemLabel="name" itemValue="id"/> --%>
			</form:select>
			<p cssClass="add-margin error-msg" id="ddisposalaccountError" style="color:red"/>
		</div>
		</div>
		
		<div class="sale-form-popup" style="display: none;" id="sale">
		
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.date" text="Sale date" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="saleDate" id="ssaledate" class="form-control datepicker" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
			<p cssClass="add-margin error-msg" id="ssaledateError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.reason" text="Sale reason" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea path="saleReason" id="ssalereason" onchange="removeSaleErrorMsg()"  class="form-control" />
			<p cssClass="add-margin error-msg" id="ssalereasonError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.buyername" text="Sale party name" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="buyerName" id="sbuyername" onchange="removeSaleErrorMsg()" class="form-control"/>
			<p cssClass="add-margin error-msg" id="sbuyernameError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.buyeraddress" text="Sale party address" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea path="buyerAddress" id="sbuyeraddress" onchange="removeSaleErrorMsg()" class="form-control" />
			<p cssClass="add-margin error-msg" id="sbuyeraddressError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.pancardnumber" text="Pan Card Number" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="panCardNumber" class="form-control" onchange="removeSaleErrorMsg()" id="spancard"/>
			<p cssClass="add-margin error-msg" id="spancardError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.aadharnumber" text="Aadhar Card Number" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input path="aadharCardNumber" class="form-control" onchange="removeSaleErrorMsg()" id="saadhar"/>
			<p cssClass="add-margin error-msg" id="saadharError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.assetcurrentvalue" text="Current Value Of The Asset" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" value="${disposal.asset.currentValue}" readonly="true" id="assetcurrentvalue" path="assetCurrentValueSale"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.disposal.accountcode" text="Asset Sale Account Code" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="assetSaleAccountSale" class="form-control" id="ssaleaccount" onchange="removeSaleErrorMsg()"> 
			<form:option value="">-select value-</form:option>
			<c:forEach var="acc" items="${assetAccounts}">
        <form:option value="${acc.id}"><c:out value="${acc.glcode}-${acc.name} "/></form:option>
   	 	</c:forEach>
			<%-- <form:options items="${assetAccounts}" itemLabel="name" itemValue="id"/> --%>
			</form:select>
			<p cssClass="add-margin error-msg" id="ssaleaccountError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.salevalue" text="Sale Value" /> 
		<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" onkeypress="javascript:return isNumber(event)" onchange="removeSaleErrorMsg()"  id="ssalevalue" oninput="calculateProfitLoss()" path="saleValue"/>
			<p cssClass="add-margin error-msg" id="salevalueError" style="color:red"/>
		</div>
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sale.profiloss" text="Profit/Loss" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" id="profitloss" path="" readonly="true"/>
	
		</div>
		</div>
		
		
		
		</div>
		</div>
		<%-- <jsp:include page="sale-disposal-upload.jsp"/> --%>
		<div class="sale-create-button" style="display: none;">
		<div align="center">
			<input type="submit" onclick="return validateSaleForm()" class="btn btn-primary" name="create" value="Create" />
		</div>
		</div>
		<div class="disposal-create-button" style="display: none;">
		<div align="center">
			<input type="submit" onclick="return validateDisposalForm()" class="btn btn-primary" name="create" value="Create" />
		</div>
		</div>
		</form:form>
		
		</div>
		
	
	
	
	<script>
	function displaySaleOrDisposal() {
	  var x = document.getElementById("saleOrDisposalSelect").value;
	  var sale="Sale";
	  var disposal="Disposal";
	  console.log("calling");
	  console.log(x);
	  if(x==sale){
		  console.log("if sale");
		  /* document.getElementById("sale").style.display = "block";
		  document.getElementById("disposal").style.display = "none"; */
		  $('.sale-form-popup').css("display", "block");
			$('.disposal-form-popup').css("display", "none");
			$('.sale-create-button').css("display", "block");
			$('.disposal-create-button').css("display", "none");
		  }else if(x==disposal){
			  console.log("if disposal");
		  	/* document.getElementById("sale").style.display = "none";
		    document.getElementById("disposal").style.display = "block"; */
			  $('.sale-form-popup').css("display", "none");
				$('.disposal-form-popup').css("display", "block");
				$('.sale-create-button').css("display", "none");
				$('.disposal-create-button').css("display", "block");
		  }else{
			  console.log("else");
		  	/* document.getElementById("sale").style.display = "none";
		    document.getElementById("disposal").style.display = "none"; */
			  $('.sale-form-popup').css("display", "none");
				$('.disposal-form-popup').css("display", "none");
				$('.sale-create-button').css("display", "none");
				$('.disposal-create-button').css("display", "none");
		  }
	  console.log("executed");
	}
	/* $("#sale").css("display", "block");
	$("#disposal").css("display", "block"); */


	/* $("#sale").attr("style", "display:block");
	$("#disposal").attr("style", "display:block"); */
	function calculateProfitLoss(){
		console.log("before")
		const  assetValue= document.getElementById("assetcurrentvalue").value;
		const  saleValue= document.getElementById("ssalevalue").value;
		const profitloss=saleValue-assetValue;
		document.getElementById("profitloss").value=profitloss;
		console.log("after")
	}
	function saleValueValidation() {
		  let x = document.getElementById("ssalevalue").value;
		  let text;
		  if (isNaN(x)) {
		    text = "Please Enter valid Number";
		  } else {
		    text = "";
		  }
		  document.getElementById("salevaluevalidation").innerHTML = text;
		}
	function isNumber(evt)
    {
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode != 46 && charCode > 31 
          && (charCode < 48 || charCode > 57))
           return false;

        return true;
     }

	function validateSaleForm() {
		  let ssaledate = document.getElementById("ssaledate").value;
		  let ssalereason = document.getElementById("ssalereason").value;
		  let sbuyername = document.getElementById("sbuyername").value;
		  let sbuyeraddress = document.getElementById("sbuyeraddress").value;
		  let spancard = document.getElementById("spancard").value;
		  let saadhar = document.getElementById("saadhar").value;
		  let ssaleaccount = document.getElementById("ssaleaccount").value;
		  let ssalevalue = document.getElementById("ssalevalue").value;
		  
		  
		  let errorMessage;
		  if (ssaledate == '') {
			  errorMessage="please enter Sale Date";
			    document.getElementById("ssaledateError").innerHTML = errorMessage;
			    return false;
			  }if (ssalereason == '') {
			  errorMessage="please enter sale reason";
			    document.getElementById("ssalereasonError").innerHTML = errorMessage;
			    return false;
			  }if (sbuyername == '') {
				  errorMessage="please enter buyer name";
				    document.getElementById("sbuyernameError").innerHTML = errorMessage;
				    return false;
				  }if (sbuyeraddress == '') {
					  errorMessage="please enter buyer address";
					    document.getElementById("sbuyeraddressError").innerHTML = errorMessage;
					    return false;
					  }if (spancard == '') {
						  errorMessage="please enter pan number";
						    document.getElementById("spancardError").innerHTML = errorMessage;
						    return false;
						  }if (saadhar == '') {
							  errorMessage="please enter aadhar number";
							    document.getElementById("saadharError").innerHTML = errorMessage;
							    return false;
							  }if (ssaleaccount == '') {
								  	errorMessage="please select sale account";
								    document.getElementById("ssaleaccountError").innerHTML = errorMessage;
								    console.log("ssaleaccount calling");
								    return false;
								  }if (ssalevalue == '') {
									  errorMessage="please enter sale value";
									    document.getElementById("salevalueError").innerHTML = errorMessage;
									    console.log("ssalevalue calling");
									    return false;
									  }
		  
			  document.getElementById("ssaledateError").innerHTML = '';
			  document.getElementById("ssalereasonError").innerHTML = '';
			  document.getElementById("sbuyernameError").innerHTML = '';
			  document.getElementById("sbuyeraddressError").innerHTML ='';
			  document.getElementById("spancardError").innerHTML = '';
			  document.getElementById("saadharError").innerHTML = '';
			  document.getElementById("ssaleaccountError").innerHTML = '';
			  document.getElementById("salevalueError").innerHTML = '';
			  return true;
			
		}
	function validateDisposalForm() {
		  let ddisposaldate = document.getElementById("ddisposaldate").value;
		  let ddisposalreason = document.getElementById("ddisposalreason").value;
		  let ddisposalaccount = document.getElementById("ddisposalaccount").value;		  
		  
		  let errorMsg;
		  if (ddisposaldate == '') {
			  errorMsg="please enter disposal date";
			    document.getElementById("ddisposaldateError").innerHTML = errorMsg;
			    return false;
			  }if (ddisposalreason == '') {
			  errorMsg="please enter disposal reason";
			    document.getElementById("ddisposalreasonError").innerHTML = errorMsg;
			    return false;
			  }if (ddisposalaccount == '') {
				  errorMsg="please select disposal account";
				    document.getElementById("ddisposalaccountError").innerHTML = errorMsg;
				    console.log("ddisposalaccount calling");
				    return false;
				  }
			  document.getElementById("ddisposaldateError").innerHTML = '';
			  document.getElementById("ddisposalreasonError").innerHTML = '';
			  document.getElementById("ddisposalaccountError").innerHTML = '';
			  return true;
			
		}
	function removeSaleErrorMsg(){
		document.getElementById("ssaledateError").innerHTML = '';
		  document.getElementById("ssalereasonError").innerHTML = '';
		  document.getElementById("sbuyernameError").innerHTML ='';
		  document.getElementById("sbuyeraddressError").innerHTML = '';
		  document.getElementById("spancardError").innerHTML = '';
		  document.getElementById("saadharError").innerHTML = '';
		  document.getElementById("ssaleaccountError").innerHTML = '';
		  document.getElementById("salevalueError").innerHTML = '';

		}
	function removeDisposalErrorMsg(){
		  document.getElementById("ddisposaldateError").innerHTML = '';
		  document.getElementById("ddisposalreasonError").innerHTML = '';
		  document.getElementById("ddisposalaccountError").innerHTML = '';
		}
	</script>
		
	