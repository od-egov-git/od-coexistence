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

<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<form:form role="form" action="updateACMC"
	modelAttribute="budgetUploadReport" id="approvebudgetsearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="main-content">
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<c:if test="${not empty message}">
						<div id="message" class="success" style="color: green;margin-top:15px;">
							<spring:message code="${message}" />
						</div>
					</c:if>
					<div class="panel-heading">
						<div class="panel-title">Verify Uploaded Budget</div>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<label class="col-sm-3 control-label text-right"><spring:message
									code="lbl.budget" /> <span class="mandatory1">*</span></label>
							<div class="col-sm-3 add-margin">
								<form:select path="reBudget.id" required="required"
									id="reBudget" cssClass="form-control"
									cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${budgets}" itemValue="id"
										itemLabel="name" />
								</form:select>
								<form:errors path="reBudget" cssClass="error-msg" />
							</div>
							<label class="col-sm-2 control-label text-right"><spring:message
									code="lbl.referenceBudget" /> </label>
							<div class="col-sm-3 add-margin">
								<div id="referenceBudget"></div>
							</div>
						</div>
						<br>
						<br>
						<div class="form-group" id="toogleDiv" style="display:none">
							
							<c:if test="${budgetDetails !=null && !budgetDetails.isEmpty() }">
							<table border="0" width="100%" class="table table-bordered"  cellspacing="0" cellpadding="0">
				<tr>
				<th>Executing Department</th>
				<th>Fund</th>
				<th>Function Name</th>
				<th>Budget Name</th>
				<th>Budget Group</th>
				<th>Original Amount</th>
				<th>Anticipatory Amount</th>
				<th>Planning Percentage</th>
				<th>Quarter-One Percentage</th>
				<th>Quarter-Two Percentage</th>
				<th>Quarter-Three Percentage</th>
				<th>Quarter-Four Percentage</th>				
				</tr>
				
					 <c:forEach items="${budgetDetails}" var="details" varStatus="item">
					 <tr>
					 <td>${details.execDeptName }</td>
					 <td>${details.fund.name }</td>
					 <td>${details.function.name }</td>
					 <td>${details.budget.name }</td>
					 <td>${details.budgetGroup.name }</td>
					 <td>${details.originalAmount }</td>
					 <td>${details.anticipatoryAmount }</td>
					 <td>${details.planningPercent }</td>
					 <td>${details.quarterpercent }</td>
					 <td>${details.quartertwopercent }</td>
					 <td>${details.quarterthreepercent }</td>
					 <td>${details.quarterfourpercent }</td>
					 </tr>
					 </c:forEach>
						 
				</table>
							</c:if>
							
						</div>
						<br>
						<br>
						<div class="form-group">
							<div class="text-center">
								<button type='submit' class='btn btn-primary' id="btnsearch">
									<spring:message code='lbl.verify' />
								</button>
								
								<button type='submit' class='btn btn-primary' id="rejectbutton">

									<spring:message code='lbl.return' />

								</button>
								
								<button type='submit' class='btn btn-primary' id="cancelbutton">

									<spring:message code='lbl.cancel' />

								</button>
								<a href='javascript:void(0)' class='btn btn-default'
									onclick='self.close()'><spring:message code='lbl.close' /></a>
										<form:hidden path="" id="workAction" name="workAction"/> 
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form:form>
<script>
	$('#btnsearch').click(function(e) {

	document.getElementById("workAction").value = "VERIFY";

	

		if ($('form').valid()) {

		} else {

		e.preventDefault();

	}  

});

$('#rejectbutton').click(function(e) {

	document.getElementById("workAction").value = "REJECT";

	

	 if ($('form').valid()) {

	} else {

			e.preventDefault();

		}

});
$('#cancelbutton').click(function(e) {

	document.getElementById("workAction").value = "CANCEL";

	

	 if ($('form').valid()) {

	} else {

		e.preventDefault();

	}  

	});
</script>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"
	type="text/javascript"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/budgetUploadReportHelper.js?rnd=${app_release_no}'/>"></script>

