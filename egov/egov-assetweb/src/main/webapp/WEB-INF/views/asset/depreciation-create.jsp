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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.min.css">
<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/buttons/2.1.0/css/buttons.dataTables.min.css">
<script type="text/javascript" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>
 <link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/base/jquery-ui.css" rel="stylesheet" />
       <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
<script type="text/javascript">

/* function validate(){
	alert("validate called");
	var slNos=document.getElementsByName('slNo');
	alert("slNo "+slNos.length);
	alert("slNo val "+document.getElementById("resultList[0].slNo").value);
	
	var cnt=0;
	for (i = 0; i < slNos.length; i++) {
		alert("inside for");
		if(document.getElementById("selected_"+i).checked){
			alert("inside if")
			cnt++;
		}
	}
	alert("cnt "+cnt);
	return false;
} */

</script>
<div class="container">
    <form:form name="Depreciation" role="form" method="post" action="searchDepreciation" modelAttribute="Depreciation" id="depreciation" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
    <div class="panel-heading">
		<c:if test="${not empty successMsg}">
			<div class="alert alert-info" role="alert">${successMsg}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">${errorMessage}</div>
		</c:if>
	</div>
    <div class="panel-heading">
    	<c:if test="${not empty successMsg}">
			<div class="alert alert-info" role="alert">${successMsg}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">${errorMessage}</div>
		</c:if>
		<div class="panel-title">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<th class="bluebgheadtd" width="100%" ><strongstyle="font-size: 15px;">
					<spring:message code="lbl.asset.depreciation" text="Asset Depreciation"/></strong></th>
				</tr>
			</table>
		</div>
	</div>
    
    <div class="tab-content">
        <div>
            <label class="col-sm-2 control-label text-right">Date Of Depreciation<span class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
			  <form:input type="text" id="depreciationDate" path="depreciationDate" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
	        </div>
      		<label class="col-sm-2 control-label text-right">Asset Category Type</label>
			<div class="col-sm-3 add-margin">
				<form:select path="categoryType" data-first-option="false" id="categoryType" class="form-control" >
					<form:option value="">-select value-</form:option>
					<form:options items="${categoryType}" itemLabel="description" itemValue="id"/>
				</form:select>
				<form:errors path="categoryType" cssClass="add-margin error-msg" />
			</div>   
    	</div> 
    	<div>
            <label class="col-sm-2 control-label text-right">Asset Category Name</label>
			<div class="col-sm-3 add-margin">
				<form:select path="categoryName" data-first-option="false" id="categoryName" class="form-control">
					<form:option value=""><spring:message code="lbl.select" /></form:option>
					<form:options items="${categoryName}" itemValue="name" itemLabel="name" />
				</form:select>
				<form:errors path="categoryName" cssClass="add-margin error-msg" />
			</div>
      		<label class="col-sm-2 control-label text-right">Department</label>
			<div class="col-sm-3 add-margin">
				<form:select path="department" data-first-option="false" id="department" class="form-control">
					<form:option value=""><spring:message code="lbl.select" /></form:option>
					<form:options items="${departments}" itemValue="code" itemLabel="name" />
				</form:select>
				<form:errors path="department" cssClass="add-margin error-msg" />
			</div>	  
    	</div>
    	<div>
            <label class="col-sm-2 control-label text-right">Asset Created From Date</label>
			<div class="col-sm-3 add-margin">
			  <form:input id="fromDate" path="fromDate" class="form-control datepicker" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
	        </div>
      		<label class="col-sm-2 control-label text-right">Asset Created To Date</label>
			<div class="col-sm-3 add-margin">
			  <form:input id="toDate" path="toDate" class="form-control datepicker" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
	        </div>	  
    	</div>
    	<div>
    		<label class="col-sm-2 control-label text-right">Asset Code</label>
			<div class="col-sm-3 add-margin">
				<form:input class="form-control" id="assetCode" path="assetCode"/>
			</div>
			<label class="col-sm-2 control-label text-right">Asset Name</label>
			<div class="col-sm-3 add-margin">
				<form:input class="form-control" id="assetName" path="assetName"/>
			</div>
    	</div> 
    	
		<div class="buttonbottom" align="center">
        	<input type="submit" id="search" class="btn btn-primary btn-wf-primary" name="search"  value="Search" />
        </div>
        
        <br>
        <br>
        <br>
        <div class="tab-pane fade in active" id="resultheader">
        <h3> Search Result</h3>
	        <div class="panel panel-primary" data-collapsed="0">
	        <form:hidden path="counter" id="counter" />
	        	<div style="padding: 0 15px;">
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th>Select</th>
						<th><spring:message code="lbl.serial" text="SL.No."/></th>
						<th>Asset Category Name</th>
						<th>Department</th>
						<th>Asset Code</th>
						<th>Asset Name</th>
						<th>Current Gross Value(Rs)</th>
						<th>Depreciation Rate(%)</th>
					</tr>
					</thead>
`					 <c:if test="${Depreciation.resultList != null &&  !Depreciation.resultList.isEmpty()}">
					<tbody>
					<c:forEach items="${Depreciation.resultList}" var="result" varStatus="status">
						<tr>
							<td>
								<form:checkbox path="resultList[${status.index}].checked" id="selected_[${status.index}]"/>
								<form:hidden path="resultList[${status.index}].slNo" id="resultList[${status.index}].slNo"/>
						    </td>
						    <td>
						    <form:hidden path="resultList[${status.index}].slNo" id="resultList[${status.index}].slNo"/>
								${result.slNo }
						    </td>
							<td>
								<form:hidden path="resultList[${status.index}].assetCategoryName" id="resultList[${status.index}].assetCategoryName"/>
								${result.assetCategoryName }
							</td>
							<td>
							<form:hidden path="resultList[${status.index}].department" id="resultList[${status.index}].department"/>
							${result.department }
							</td>
							<td>
							<form:hidden path="resultList[${status.index}].assetCode" id="resultList[${status.index}].assetCode"/>
							${result.assetCode }</a>
							</td>
							<td>
							<form:hidden path="resultList[${status.index}].assetName" id="resultList[${status.index}].assetName"/>
							${result.assetName }
							</td>
							<td>
							<form:hidden path="resultList[${status.index}].currentGrossValue" id="resultList[${status.index}].currentGrossValue"/>
							${result.currentGrossValue }
							</td>
							<td>
							<form:hidden path="resultList[${status.index}].depreciationRate" id="resultList[${status.index}].depreciationRate"/>
							${result.depreciationRate }
							</td>
							
						</tr>
						</c:forEach>
					<tbody>
					</c:if>	
							
				</table>
				</div>
			<br>
			<div class="buttonbottom" align="center">
		        	<input type="submit" id="save" class="btn btn-primary btn-wf-primary" name="save"  value="Submit" />
		        </div>
			<br>
	        </div>
        </div>
        
    </div>

</form:form>
</div>
<script
	src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script>
	$(document).ready(function() {
    $('#searchResult').DataTable( {
        dom: 'Bfrtip',
        buttons: [
            'excel', 'pdf', 'print'
        ]
    } );
} );
	</script> 


