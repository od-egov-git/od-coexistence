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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>

<style>

</style>

<div class="container">
<form:form action="createAssetCategory" modelAttribute="assetCatagory" method="POST">
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<c:if test="${not empty successMsg}">
			<div class="alert alert-info" role="alert">${successMsg}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">${errorMessage}</div>
		</c:if>
			<div class="panel-title">
				<spring:message code="lbl.asset.catagory" text="Asset Category" />
			</div>
	</div>


<div class="panel-body">
	<div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.name" text="Name" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="name" required="required"/>
		</div>
		<label class="col-sm-3 control-label text-right"> <spring:message code="lbl.asset.catagory.type" text="Asset Category Type" />
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="assetCatagoryType" class="form-control"> 
				<form:option value="">-select value-</form:option>
				<form:options items="${assetCatagoryTypes}" itemLabel="description" itemValue="id"/>
			</form:select>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.parent.catagory" text="Parent Category" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="parentCatagory" class="form-control"> 
			<form:option value="">-select value-</form:option>
			<form:options items="${parentCatagory}" itemLabel="description" itemValue="id"/>
			</form:select>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.method" text="Depreciation Method" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="depriciationMethod" class="form-control"> 
				<form:option value="">-select value-</form:option>
				<form:options items="${depriciationMethod}" itemLabel="description" itemValue="id"/>
			</form:select>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.asset.account.code" text="Asset Account Code" /> 
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="assetaccountcode" name="code" class="form-control table-input creditDetailGlcode assetaccountcode"  data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" required/>
		<span class="mandatory"></span>
		<form:hidden path="assetAccountCode.id" value="" name=""  class="form-control table-input hidden-input assetaccountcodeid"/>
		<form:hidden path="assetAccountCode.glcode" id="assetaccountcodeglcode" value="" name=""  class="form-control table-input hidden-input assetaccountcodeglcode"/>
		<form:hidden path="assetAccountCode.name" id="assetaccountcodename" value="" name=""  class="form-control table-input hidden-input assetaccountcodename"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.accumulated.depriciation.code" text="Accumulated Depreciation Code" />
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="accumulateddepriciationcode" name="code" class="form-control table-input creditDetailGlcode accumulateddepriciationcode"  data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" required/>
		<span class="mandatory"></span>
		<form:hidden path="accumulatedDepriciationCode.id" value="" name=""  class="form-control table-input hidden-input accumulateddepriciationcodeid"/>
		<form:hidden path="accumulatedDepriciationCode.glcode" id="accumulateddepriciationcodeglcode" value="" name=""  class="form-control table-input hidden-input accumulateddepriciationcodeglcode"/>
		<form:hidden path="accumulatedDepriciationCode.name" id="accumulateddepriciationcodename" value="" name=""  class="form-control table-input hidden-input accumulateddepriciationcodename"/>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.revolution.reserve.account.code" text="Revaluation Reserve Account Code" /> 
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="revalutionreserveaccountcode" name="code" class="form-control table-input creditDetailGlcode revalutionreserveaccountcode"  data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" required/>
		<span class="mandatory"></span>
		<form:hidden path="revolutionReserveAccountCode.id" value="" name=""  class="form-control table-input hidden-input revalutionreserveaccountcodeid"/>
		<form:hidden path="revolutionReserveAccountCode.glcode" id="revalutionreserveaccountcodeglcode" value="" name=""  class="form-control table-input hidden-input revalutionreserveaccountcodeglcode"/>
		<form:hidden path="revolutionReserveAccountCode.name" id="revalutionreserveaccountcodename" value="" name=""  class="form-control table-input hidden-input revalutionreserveaccountcodename"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.expense.account" text="Depreciation Expense Account" />
		</label>
		<div class="col-sm-3 add-margin">
			<input type="text" id="depriciationexpenseaccount" name="code" class="form-control table-input creditDetailGlcode depriciationexpenseaccount"  data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" required/>
			<span class="mandatory"></span>
		<form:hidden path="depriciationExpenseAccount.id" value="" name=""  class="form-control table-input hidden-input depriciationexpenseaccountid"/>
		<form:hidden path="depriciationExpenseAccount.glcode" id="depriciationexpenseaccountglcode" value="" name=""  class="form-control table-input hidden-input depriciationexpenseaccountglcode"/>
		<form:hidden path="depriciationExpenseAccount.name" id="depriciationexpenseaccountname" value="" name=""  class="form-control table-input hidden-input depriciationexpenseaccountname"/>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.asset.uom" text="UOM" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="unitOfMeasurement" class="form-control"> 
			<form:option value="">-select value-</form:option>
			<form:options items="${unitOfMeasurement}" itemLabel="description" itemValue="id"/>
			</form:select>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.version" text="Version" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="version" />
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.rate" text="Depreciation Rate" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" path="depriciationRate" />
		</div>
		
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.used.for.lease.and.agreement" text="Used For Lease and Aggrement" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:checkbox class="form-check-input" path="leaseAndAgreement" value="true"/> 
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.rate" text="Life of the Asset" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="lifeOfAsset" />
		</div>
		</div>
		</div>
		</div>
		
		
		
	<div class="panel panel-primary" data-collapsed="0">	
	<div class="panel-heading">
	<div class="panel-title">
		<spring:message code="lbl.asset.catagory.custome.feilds" text="Custom Feilds"/>
	</div>
	</div>
	
	<div class="panel-body">
	<table class="table table-bordered" id="tbldebitdetails">
		<thead>
			<tr>
				<th><spring:message code="lbl.asset.custome.sino" text="SI No."/></th>
				<th><spring:message code="lbl.asset.custome.name" text="Name"/></th>
				<th><spring:message code="lbl.asset.custome.sino" text="Data Type"/></th>
				<th><spring:message code="lbl.asset.custome.active" text="Active"/></th>
				<th><spring:message code="lbl.asset.custome.mandatory" text="Mandatory"/></th>
				<th><spring:message code="lbl.asset.custome.values" text="Values"/></th>
				<th><spring:message code="lbl.asset.custome.order" text="Order"/></th>
				<th><spring:message code="lbl.asset.custome.column" text="Columns"/></th>
				<th><spring:message code="lbl.action" text="Action"/></th> 					
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty assetCatagory.customeFields}">
		<c:forEach items="${assetCatagory.customeFields}" var="customeField" varStatus="tagStatus">
			<tr id="debitdetailsrow">
				<td>${tagStatus.index +1}</td>
				<td>${customeField.name}</td>
				<td>${customeField.dataType}</td>
				<td>${customeField.active}</td> 
				<td>${customeField.mandatory}</td>
				<td>${customeField.vlaues}</td>
				<td>${customeField.orders}</td>
				<td>${customeField.columns}</td>
				<td class="text-center">
					<div id="addRow" class="btn btn-primary" ><spring:message code="lbl.custom.field.add" text="Add New" /> </div>
				</td>
			</tr>
		</c:forEach>
		</c:if>
		<c:if test="${empty assetCatagory.customeFields}">
		<tr id="debitdetailsrow">
				<td></td>
				<td></td>
				<td></td>
				<td></td> 
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td class="text-center">
				<div id="addRow" class="btn btn-primary" ><spring:message code="lbl.custom.field.add" text="Add New" /> </div>
				</td>
			</tr>
			</c:if>
		</tbody>
	</table>
		<div id="newRow">
		
		</div>
		</div>
	</div>
		<c:if test="${not empty assetCatagory.customeFields}">

		<c:forEach items="${assetCatagory.customeFields}" var="customeField" varStatus="vs">
		<form:hidden path="customeFields[${vs.index}].name" value="${customeField.name}" />
		<form:hidden path="customeFields[${vs.index}].dataType" value="${customeField.dataType}" />
		<form:hidden path="customeFields[${vs.index}].columns" value="${customeField.columns}" />
		<form:hidden path="customeFields[${vs.index}].mandatory" value="${customeField.mandatory}" />
		<form:hidden path="customeFields[${vs.index}].active" value="${customeField.active}" />
		<form:hidden path="customeFields[${vs.index}].orders" value="${customeField.orders}" />
		<form:hidden path="customeFields[${vs.index}].vlaues" value="${customeField.vlaues}" />
		</c:forEach>
		</c:if>
		<div align="center" id="createbotton">
			<input type="submit" class="btn btn-primary" name="create" value="Create" />
		</div>
		</form:form>
		
		</div>
		
	
	
	
	<script>
	function openForm() {
		console.log("Calling")
	  document.getElementById("customForm").style.display = "block";
	  document.getElementById("createbotton").style.display = "none";
	}
	function show() {
	  document.getElementById("createbotton").style.display = "block";
	}

	function closeForm() {
	  document.getElementById("customForm").style.display = "none";
	}
	function displayColumn() {
		console.log("calling");
		  const x = document.getElementById("columnSelect").value;
		  const table=3;
		  console.log(x);
		 
		  if(x==table){
			  
			  $('.column-form-popup').css("display", "block");
				
			  }else{
				  $('.column-form-popup').css("display", "none");
			  }
		  console.log("executed");
		}
	var i=0;
	$("#addRow").click(function () {
		var html = '';

							html += '<div class="form-popup"  id="customForm">';
							html += '<label class="col-sm-3 control-label text-right"><spring:message code="lbl.custom.name" text="Name" />';
							html += '<span class="mandatory"></span>';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<input type="text" class="form-control"  name="customeFields['+i+'].name" />';
							html += '</div>';
							html += '<label class="col-sm-3 control-label text-right">';
							html += '<spring:message code="lbl.parent.data.type" text="Data Type" />';
							html += '<span class="mandatory"></span>';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<select name="customeField.customFieldDataType.dataTypes" class="form-control" id="columnSelect" onchange="displayColumn()">';
							html += '<option value="Text">Text</option>';
							html += '<option value="Number">Number</option>';
							html += '</select>';
							html += '</div>';
							html += '<label class="col-sm-3 control-label text-right">';
							html += '<spring:message code="lbl.used.for.lease.and.mandatory" text="Mandatory" />';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<input type="checkbox" class="form-check-input" name="customeFields['+i+'].mandatory" value="true"/>';
							html += '</div>';
							html += '<label class="col-sm-3 control-label text-right">';
							html += '<spring:message code="lbl.used.for.lease.and.active" text="Active" />';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<input type="checkbox" class="form-check-input" name="customeFields['+i+'].active" value="true"/>';
							html += '</div>';
							html += '<label class="col-sm-3 control-label text-right">';
							html += '<spring:message code="lbl.bill.custom.order" text="Order"/>';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<input type="number" class="form-control patternvalidation" data-pattern="alphanumerichyphenbackslash"  name="customeFields['+i+'].orders" maxlength="50" />';
							html += '<form:errors path="" cssClass="add-margin error-msg" />';
							html += '</div>';
							html += '<div class="column-form-popup" style="display: none;">';
							html += '<label class="col-sm-3 control-label text-right">';
							html += '<spring:message code="lbl.bill.custom.value" text="No. Of Columns"/>';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<input type="number" class="form-control patternvalidation" data-pattern="alphanumerichyphenbackslash"  name="customeFields['+i+'].columns" maxlength="50" />';
							html += '</div>';
							html += '</div>';
							html += '<label class="col-sm-3 control-label text-right">';
							html += '<spring:message code="lbl.bill.custom.value" text="Value"/>';
							html += '</label>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<input type="textarea" class="form-control" name = "customeFields['+i+'].vlaues" rows = "5" cols = "30" />';
							html += '</div>';
							html += '<div class="col-sm-3 add-margin">';
							html += '<button id="removeRow" type="button" class="btn btn-danger">Remove</button>';
							html += '</div>';
							html += '</div>';
							i++;
							$('#newRow').append(html);
						});

		$(document).on('click', '#removeRow', function() {
			$(this).closest('#customForm').remove();
			i--;
		});
	</script>
		<script src="<cdn:url value='/resources/app/js/assetcategory.js?rnd=${app_release_no}' context='/services/asset'/>"></script>
	