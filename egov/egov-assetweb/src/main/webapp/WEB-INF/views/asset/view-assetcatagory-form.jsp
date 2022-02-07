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
<script src="<cdn:url value='/resources/app/js/assetcategory.js?rnd=${app_release_no}' context='/services/asset'/>"></script>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<style>
.form-popup {
  display: none;
}
</style>

<div class="container">
<form:form action="${contextPath}/assetcategory/updateAssetCategory" modelAttribute="assetCatagory" method="POST">
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<c:if test="${not empty successMsg}">
			<div class="alert alert-success" role="alert">${successMsg}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">${errorMessage}</div>
		</c:if>
			<div class="panel-title">
				<spring:message code="lbl.asset.catagory" text="View Asset Category" />
			</div>
	</div>


	<div class="panel-body">
	<div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.name" text="Name" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="name" required="required" disabled="true" />
		</div>
		<label class="col-sm-3 control-label text-right"> <spring:message code="lbl.asset.catagory.type" text="Asset Category Type" />
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="assetCatagoryType" class="form-control" disabled="true"> 
				<form:option value="" readonly="true">-select value-</form:option>
				<form:options items="${assetCatagoryTypes}" itemLabel="description" itemValue="id"/>
			</form:select>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.parent.catagory" text="Parent Category" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="parentCatagory" class="form-control" disabled="true"> 
			<form:option value="">-select value-</form:option>
			<form:options items="${parentCatagory}" itemLabel="description" itemValue="id"/>
			</form:select>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.method" text="Depreciation Method" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="depriciationMethod" class="form-control" disabled="true"> 
				<form:option value="">-select value-</form:option>
				<form:options items="${depriciationMethod}" itemLabel="description" itemValue="id"/>
			</form:select>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.asset.account.code" text="Asset Account Code" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="assetaccountcode" name="code" class="form-control table-input creditDetailGlcode assetaccountcode" readonly="true" data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" />
		<form:hidden path="assetAccountCode.id" value="" name=""  class="form-control table-input hidden-input assetaccountcodeid"/>
		<form:hidden path="assetAccountCode.glcode" id="assetaccountcodeglcode" value="" name=""  class="form-control table-input hidden-input assetaccountcodeglcode"/>
		<form:hidden path="assetAccountCode.name" id="assetaccountcodename" value="" name=""  class="form-control table-input hidden-input assetaccountcodename"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.accumulated.depriciation.code" text="Accumulated Depreciation Code" />
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="accumulateddepriciationcode" name="code" class="form-control table-input creditDetailGlcode accumulateddepriciationcode" readonly="true" data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" />
		<form:hidden path="accumulatedDepriciationCode.id" value="" name=""  class="form-control table-input hidden-input accumulateddepriciationcodeid"/>
		<form:hidden path="accumulatedDepriciationCode.glcode" id="accumulateddepriciationcodeglcode" value="" name=""  class="form-control table-input hidden-input accumulateddepriciationcodeglcode"/>
		<form:hidden path="accumulatedDepriciationCode.name" id="accumulateddepriciationcodename" value="" name=""  class="form-control table-input hidden-input accumulateddepriciationcodename"/>
		
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.revolution.reserve.account.code" text="Revaluation Reserve Account Code" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="revalutionreserveaccountcode" name="code" class="form-control table-input creditDetailGlcode revalutionreserveaccountcode" readonly="true" data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" />
		<form:hidden path="revolutionReserveAccountCode.id" value="" name=""  class="form-control table-input hidden-input revalutionreserveaccountcodeid"/>
		<form:hidden path="revolutionReserveAccountCode.glcode" id="revalutionreserveaccountcodeglcode" value="" name=""  class="form-control table-input hidden-input revalutionreserveaccountcodeglcode"/>
		<form:hidden path="revolutionReserveAccountCode.name" id="revalutionreserveaccountcodename" value="" name=""  class="form-control table-input hidden-input revalutionreserveaccountcodename"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.expense.account" text="Depreciation Expense Account" />
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
		<input type="text" id="depriciationexpenseaccount" name="code" class="form-control table-input creditDetailGlcode depriciationexpenseaccount" readonly="true" data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" />
		<form:hidden path="depriciationExpenseAccount.id" value="" name=""  class="form-control table-input hidden-input depriciationexpenseaccountid"/>
		<form:hidden path="depriciationExpenseAccount.glcode" id="depriciationexpenseaccountglcode" value="" name=""  class="form-control table-input hidden-input depriciationexpenseaccountglcode"/>
		<form:hidden path="depriciationExpenseAccount.name" id="depriciationexpenseaccountname" value="" name=""  class="form-control table-input hidden-input depriciationexpenseaccountname"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.asset.uom" text="UOM" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="unitOfMeasurement" class="form-control" disabled="true"> 
			<form:option value="">-select value-</form:option>
			<form:options items="${unitOfMeasurement}" itemLabel="description" itemValue="id"/>
			</form:select>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.version" text="Version" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="version" disabled="true"/>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.rate" text="Depreciation Rate" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" path="depriciationRate" disabled="true"/>
		</div>	
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.used.for.lease.and.agreement" text="Used For Lease and Aggrement" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:checkbox class="form-check-input" path="leaseAndAgreement" value="true" disabled="true"/> 
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<form:hidden path="id" value="${assetCatagory.id}"/>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.depriciation.rate" text="Life of the Asset" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" path="lifeOfAsset" disabled="true"/>
		</div>	
	</div>	
	</div>
	</div>
	<c:if test="${not empty assetCatagory.customeFields}">
	<div class="panel panel-primary" data-collapsed="0">	
	<div class="panel-heading">
	<div class="panel-title">
		<spring:message code="lbl.asset.catagory.custome.feilds" text="Custom Feilds"/>
	</div>
	</div>
	
	
	<div class="panel-body">
	<div class="form-group">
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
				<%-- <th><spring:message code="lbl.action" text="Action"/></th> --%> 					
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty assetCatagory.customeFields}">
		<c:forEach items="${assetCatagory.customeFields}" var="customeField" varStatus="tagStatus">
			<tr id="debitdetailsrow">
				<td>${tagStatus.index+1}</td>
				<td>${customeField.name}</td>
				<td>${customeField.dataType}</td>
				<td>${customeField.active}</td> 
				<td>${customeField.mandatory}</td>
				<td>${customeField.vlaues}</td>
				<td>${customeField.orders}</td>
				<td>${customeField.columns}</td>
				<%-- <td class="text-center">
					<a class="btn btn-primary" href="${contextPath}/assetcategory/editCustomField/${customeField.id}/${assetCatagory.name}">Update</a>
					<a class="btn btn-primary" href="${contextPath}/asset/deleteCustomeField/${customeField.id}">Delete</a>
					
					<form:hidden path="id" name="id" value="${assetCatagory.id}"/>
					<form:hidden path="customeField.id" name="cusId" value="${customeField.id}"/>
				<input type="submit" class="btn btn-primary" name="delete" value="Delete"/>
			
			
				</td> --%>
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
	</table>
		<div class="form-popup"  id="customForm">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.custom.name" text="Name" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control"  path="customeField.name" disabled="true"/>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.parent.data.type" text="Data Type" /> 
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="customeField.customFieldDataType" class="form-control" disabled="true"> 
			<form:option value="">-select value-</form:option>
			<form:options items="${customFieldDataType}" itemLabel="DataTypes" itemValue="id"/>
			</form:select>
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.used.for.lease.and.mandatory" text="Mandatory"/>
		</label>
		<div class="col-sm-3 add-margin">
			<form:checkbox class="form-check-input" path="customeField.mandatory" value="true" disabled="true"/> 
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.used.for.lease.and.active" text="Active" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:checkbox class="form-check-input" path="customeField.active" value="true" disabled="true"/> 
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.bill.custom.order" text="Order"/>
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" data-pattern="alphanumerichyphenbackslash"  path="customeField.orders" maxlength="50" disabled="true" />
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-3 control-label text-right">
			<spring:message code="lbl.bill.custom.value" text="Value"/>
		</label>
		<div class="col-sm-3 add-margin">
			<form:textarea class="form-control" path = "customeField.vlaues" rows = "5" cols = "30" disabled="true"/>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
		<div align="center">
			<input type="submit" class="btn btn-primary" name="updateCustomeField" value="Update Custome Field" disabled="true"/>
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
		
		
		
</div>
	
	
		</div>
	
	</div>
	
		</div>
		</c:if>
		<%-- <c:if test="${ empty assetCatagory.customeFields}">
		<div align="center">
		<a class="btn btn-primary" href="${contextPath}/assetcategory/editAssetCategory/${assetCatagory.id}">Edit</a>
		</div>
		</c:if> --%>
		<div align="center"><input type="button" name="button2" id="button2" value="Close" class="btn btn-primary" onclick="window.parent.postMessage('close','*');window.close();"/></div>
		</form:form>
	</div>	
		
	<script>
			function openForm() {
				console.log("Calling")
			  document.getElementById("customForm").style.display = "block";
			}

			function closeForm() {
			  document.getElementById("customForm").style.display = "none";
			}
		</script>
	