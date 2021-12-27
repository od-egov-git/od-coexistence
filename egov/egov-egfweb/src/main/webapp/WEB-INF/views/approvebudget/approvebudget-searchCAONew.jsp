<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~     Copyright (C) 2017  eGovernments Foundation
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>

<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<form:form role="form" action="updateCAO" method="post"
	modelAttribute="budgetUploadReport" id="approvebudgetsearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="main-content">
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<c:if test="${not empty message}">
						<div id="message" class="success"
							style="color: green; margin-top: 15px;">
							<spring:message code="${message}" />
						</div>
					</c:if>
					<div class="panel-heading">
						<div class="panel-title">Verify Uploaded Budget</div>
					</div>
					<div class="panel-body">
						<div class="form-group" id="toogleDiv">
							<c:if test="${budgetDetails !=null && !budgetDetails.isEmpty() }">
								<table border="0" width="100%" class="table table-bordered" cellspacing="0" cellpadding="0">
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
											
											<td><input type="hidden" name="budgetDetailsList[${item.index }].id" value=${details.id }>
											<input type="text" name="budgetDetailsList[${item.index }].originalAmount" value=${details.originalAmount } style="border:0.5px solid;"></td>
											<td><input type="text" name="budgetDetailsList[${item.index }].anticipatoryAmount" value=${details.anticipatoryAmount } style="border:0.5px solid;"></td>
											<td><input type="text" name="budgetDetailsList[${item.index }].planningPercent" value=${details.planningPercent } style="border:0.5px solid;"></td>
											<td><input type="text" name="budgetDetailsList[${item.index }].quarterpercent" value=${details.quarterpercent } style="border:0.5px solid;"></td>
											<td><input type="text" name="budgetDetailsList[${item.index }].quartertwopercent" value=${details.quartertwopercent } style="border:0.5px solid;"></td>
											<td><input type="text" name="budgetDetailsList[${item.index }].quarterthreepercent" value=${details.quarterthreepercent } style="border:0.5px solid;"></td>
											<td><input type="text" name="budgetDetailsList[${item.index }].quarterfourpercent" value=${details.quarterfourpercent } style="border:0.5px solid;"></td>
										</tr>
									</c:forEach>
								</table>
							</c:if>
						</div>
						<br> <br>
						<%-- <div class="form-group">
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
								<form:hidden path="" id="workAction" name="workAction" />
							</div>
						</div> --%>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<jsp:include page="../common/commonworkflowmatrix.jsp" />
		<div class="buttonbottom" align="center">
			<jsp:include page="../common/commonworkflowmatrix-button.jsp" />
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
		alert("reject button");
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
	$('.btn-wf-primary').click(function(){
		var button = $(this).attr('id');
		document.getElementById("workFlowAction").value = button;
		var department=document.getElementById("approvalDepartment").value;
		var designation=document.getElementById("approvalDesignation").value;
		var position=document.getElementById("approvalPosition").value;
		var comment=document.getElementById("approvalComent").value;
		if (button != null && button == 'Reject') {
			if(comment=='' || comment==null)
			{
				bootbox.alert("Enter Comments");
				return false;
			}
			
		}
		if (button != null && button == 'Forward') {
		 	if(department=='-1'||department=='')
		 	{
		 		bootbox.alert("Select Approver Department Details");
		 		return false;
		 	}
			if(designation=='-1'||designation=='')
			{
				bootbox.alert("Select Approver Designation Details"); 
				return false;
			}
			if(position=='-1'||position=='')
			{
				bootbox.alert("Select Approver Details");
				return false;
			}
		}
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	})

	
	
</script>

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>" />

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">

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

