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
<script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>
<script type="text/javascript">

</script>
    <form:form name="Depreciation" role="form" method="post" action="searchViewDepreciation" modelAttribute="Depreciation" id="depreciation" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
    <div class="panel-heading">
		<div class="panel-title">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<th class="bluebgheadtd" width="100%" ><strongstyle="font-size: 15px;">
					<spring:message code="lbl.view.asset.depreciation" text="View Asset Depreciation"/></strong></th>
				</tr>
			</table>
		</div>
	</div>
    
    
        
        <table border="0" width="100%" id="view">
        <tr><th> Asset Details</th></tr>
	        <tr>
				<td style="width: 5%"></td>
	        	<td class="greybox">Asset Code</td>
				<td class="greybox">
					<form:input class="form-control" id="assetCode" path="assetCode" value="${depreciation.assetCode }" readonly="true"/>
				</td>
				<td class="greybox">Asset Name</td>
				<td class="greybox">
					<form:input class="form-control" id="assetName" path="assetName" value="${depreciation.assetName }" readonly="true" />
				</td>
	        </tr>
	        <tr>
	        	<td style="width: 5%"></td>
	        	<td class="greybox">Description</td>
				<td class="greybox">
					<form:input class="form-control" id="description" path="description" value="${depreciation.description }" readonly="true"/>
				</td>
				<td class="greybox">Asset Category Name</td>
				<td class="greybox">
					<form:input class="form-control" id="categoryName" path="categoryName" value="${depreciation.categoryName }" readonly="true" />
				</td>
	        </tr>
	        <tr><td></td></tr>
        <tr><th> Asset Depreciation Details</th></tr>
            <tr>
				<td style="width: 5%"></td>
	        	<td class="greybox">Depreciation Rate</td>
				<td class="greybox">
					<form:input class="form-control" id="depreciationRate" path="depreciationRate" value="${depreciation.depreciationRate }" readonly="true"/>
				</td>
				<td class="greybox">Current Depreciation(Rs)</td>
				<td class="greybox">
					<form:input class="form-control" id="currentDepreciation" path="currentDepreciation" value="${depreciation.currentDepreciation }" readonly="true"/>
				</td>
        	</tr>
        	<tr>
        		<td style="width: 5%"></td>
	        	<td class="greybox">Last Depreciation Date</td>
				<td class="greybox">
					<form:input class="form-control" id="depreciationDate" path="depreciationDate" value="${depreciation.depreciationDate }" readonly="true"/>
				</td>
				<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
        	</tr>
        	<tr>
        		<td style="width: 5%"></td>
	        	<td class="greybox">Current Depreciation(Rs)</td>
				<td class="greybox">
					<form:input class="form-control" id="afterDepreciation" path="afterDepreciation" value="${depreciation.afterDepreciation }" readonly="true"/>
				</td>
	        	<td class="greybox">Voucher Number</td>
				<td class="greybox">
					<form:input class="form-control" id="voucherNumber" path="voucherNumber" value="${depreciation.voucherNumber }" readonly="true"/>
				</td>
        	</tr>
        </table>
</form:form>
<script>
	
	</script> 
