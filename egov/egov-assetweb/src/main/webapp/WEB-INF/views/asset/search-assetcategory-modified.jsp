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
<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.min.css">
<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/buttons/2.1.0/css/buttons.dataTables.min.css">
<script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>

<title>Asset Category</title>

<div class="container">
<form:form action="searchAssetCategoryModifyPage" modelAttribute="assetCatagory" method="POST">
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<c:if test="${not empty successMsg}">
			<div class="alert alert-success" role="alert">${successMsg}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">${errorMessage}</div>
		</c:if>
			<div class="panel-title">
				<spring:message code="lbl.asset.catagory" text="Seach Asset Catagory" />
			</div>
	</div>

	<div class="panel-body">
	<div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.name" text="Name" /> 
		</label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control" path="name"/>
		</div>
		
		<label class="col-sm-3 control-label text-right"> <spring:message code="lbl.asset.catagory.type" text="Asset Category Type" />
		</label>
		<div class="col-sm-3 add-margin">
			<form:select path="assetCatagoryType" class="form-control"> 
				<form:option value="">-select value-</form:option>
				<form:options items="${assetCatagoryTypes}" itemLabel="description" itemValue="id"/>
			</form:select>
			<form:errors path="" cssClass="add-margin error-msg" />
		</div>
	
		<div align="center">
			<input type="submit" class="btn btn-primary" name="Search" value="search" />
		</div>
		</div>
	</div>	
		</div>

	<c:if test="${not empty assetCategories}">
	<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="lbl.asset.catagory" text="Asset Category Details" />
			</div>
	</div>
	<div class="panel-body">
	
	<table id="viewassetcategory" class="table table-bordered">
        <thead>
            <tr>
            	<th><spring:message code="lbl.asset.custome.sino" text="Sino"/></th>
				<th><spring:message code="lbl.asset.custome.code" text="Asset Code"/></th>
				<th><spring:message code="lbl.asset.custome.name" text="Name"/></th>
				<th><spring:message code="lbl.asset.custome.assetcategorytype" text="Asset Category Type"/></th>
				<th><spring:message code="lbl.asset.custome.parentcategory" text="Parent Category"/></th>
				<th><spring:message code="lbl.asset.custome.uom" text="Unit Of Measurement"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${assetCategories}" var="assetCategory" varStatus="tagStatus">
			<tr>
				<td>${tagStatus.index +1}</td>
				<td>
				<a href="viewModifyAssetCategory/${assetCategory.id}" 
  				target="popup" 
  				onclick="window.open('viewModifyAssetCategory/${assetCategory.id}','popup','width=700,height=600'); return false;">
    				${assetCategory.assetCode}
				</a>
				</td>
				<td>
				<a href="viewModifyAssetCategory/${assetCategory.id}" 
  				target="popup" 
  				onclick="window.open('viewModifyAssetCategory/${assetCategory.id}','popup','width=700,height=600'); return false;">
    				${assetCategory.name}
				</a>
				</td>
				<td>
				<a href="viewModifyAssetCategory/${assetCategory.id}" 
  				target="popup" 
  				onclick="window.open('viewModifyAssetCategory/${assetCategory.id}','popup','width=700,height=600'); return false;">
    				${assetCategory.assetCatagoryType.description}
				</a>
				</td>
				<td>
				<a href="viewModifyAssetCategory/${assetCategory.id}" 
  				target="popup" 
  				onclick="window.open('viewModifyAssetCategory/${assetCategory.id}','popup','width=700,height=600'); return false;">
    				${assetCategory.parentCatagory.description}
				</a>
				</td>
				<td>
				<a href="viewModifyAssetCategory/${assetCategory.id}" 
  				target="popup" 
  				onclick="window.open('viewModifyAssetCategory/${assetCategory.id}','popup','width=700,height=600'); return false;">
    				${assetCategory.unitOfMeasurement.description}
				</a>
				</td>
			</tr>
		</c:forEach>
           
        </tbody>
    </table>
    </div>
	</div>
	</c:if>
	</form:form>
	</div>

	
	 <script>
	$(document).ready(function() {
    $('#viewassetcategory').DataTable( {
        dom: 'Bfrtip',
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ]
    } );
} );
	</script> 