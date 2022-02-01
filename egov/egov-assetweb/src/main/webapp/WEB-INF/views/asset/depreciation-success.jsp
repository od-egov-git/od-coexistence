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
  ~     but WITHresultList ANY WARRANTY; withresultList even the implied warranty of
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
    <form:form name="Depreciation" role="form" method="post" action="Depreciation" modelAttribute="Depreciation" id="depreciation" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
    
        <div class="tab-pane fade in active" id="resultheader">
        <h3> Asset Depreciation</h3>
	        <div class="panel panel-primary" data-collapsed="0">
	        <form:hidden path="counter" id="counter" />
	        	<div style="padding: 0 15px;">
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th><spring:message code="lbl.serial" text="SL.No."/></th>
						<th>Asset Category Name</th>
						<th>Department</th>
						<th>Asset Code</th>
						<th>Asset Name</th>
						<th>Depreciation Rate(%)</th>
						<th>Current Depreciation(Rs)</th>
						<th>Value after Depreciation(Rs)</th>
						<th>Success/Failure</th>
						<th>Reason For Failure</th>
						<th>Voucher Number</th>
					</tr>
					</thead>
`					 <c:if test="${Depreciation.resultList != null &&  !Depreciation.resultList.isEmpty()}">
					<tbody>
					<c:forEach items="${Depreciation.resultList}" var="result" varStatus="status">
						<tr>
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
								<form:hidden path="resultList[${status.index}].depreciationRate" id="resultList[${status.index}].depreciationRate"/>
								${result.depreciationRate }
							</td>
							<td>
								<form:hidden path="resultList[${status.index}].currentDepreciation" id="resultList[${status.index}].currentDepreciation"/>
								${result.currentDepreciation }
							</td>
							<td>
								<form:hidden path="resultList[${status.index}].afterDepreciation" id="resultList[${status.index}].afterDepreciation"/>
								${result.afterDepreciation }
							</td>
							<td>
								<form:hidden path="resultList[${status.index}].successFailure" id="resultList[${status.index}].successFailure"/>
								${result.successFailure }
							</td>
							<td>
								<form:hidden path="resultList[${status.index}].reasonForFailure" id="resultList[${status.index}].reasonForFailure"/>
								${result.reasonForFailure }
							</td>
							<td>
								<form:hidden path="resultList[${status.index}].voucherNumber" id="resultList[${status.index}].voucherNumber"/>
								${result.voucherNumber }
							</td>
							
							
						</tr>
						</c:forEach>
					<tbody>
					</c:if>	
					<c:if test="${Depreciation.resultList == null ||  Depreciation.resultList.isEmpty()}">
						No records found
					</c:if>			
				</table>
				</div>
			<br>
			
			<br>
	        </div>
        </div>
        
    </div>

</form:form>
<script>
	$(document).ready(function() {
    $('#searchResult').DataTable( {
        dom: 'Bfrtip',
        buttons: [
            'pdf', 'print'
        ]
    } );
} );
	</script> 
