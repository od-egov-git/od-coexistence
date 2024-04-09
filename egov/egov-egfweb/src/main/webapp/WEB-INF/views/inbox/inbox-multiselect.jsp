<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/struts-tags.tld" prefix="s"%>
<%@ include file="/includes/taglibs.jsp"%>
<link href="/services/EGF/resources/css/budget.css?rnd=3.0.0-COE-SNAPSHOT_2024-01-29 16:52" rel="stylesheet" type="text/css" />
<link href="/services/EGF/resources/css/commonegovnew.css?rnd=3.0.0-COE-SNAPSHOT_2024-01-29 16:52" rel="stylesheet" type="text/css" />
<link href="/services/EGF/resources/css/error.css?rnd=3.0.0-COE-SNAPSHOT_2024-01-29 16:52" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/EGF'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/EGF'/>">
<link rel="stylesheet" href="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/css/datatables.responsive.css' context='/services/EGF'/>"/>
<link rel="stylesheet" type="text/css" href="/services/EGF/resources/commonyui/yui2.8/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="/services/EGF/resources/commonyui/yui2.8/datatable/assets/skins/sam/datatable.css" />
<link rel="stylesheet" type="text/css" href="/services/EGF/resources/commonyui/yui2.8/assets/skins/sam/autocomplete.css" />
<link rel="stylesheet" type="text/css" href="/services/egi/resources/global/js/jquery-ui/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="/services/egi/resources/global/css/bootstrap/bootstrap.css" />
<link rel="stylesheet" type="text/css" href="/services/egi/resources/global/css/egov/custom.css?rnd=${app_release_no}" />

<script type="text/javascript" src="/services/egi/resources/global/js/bootstrap/bootstrap.js"></script>
<script type="text/javascript" src="/services/EGF/resources/commonyui/yui2.8/yuiloader/yuiloader-min.js"></script>
<script type="text/javascript" src="/services/EGF/resources/commonyui/yui2.8/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>
<script type="text/javascript" src="/services/EGF/resources/javascript/jsCommonMethods.js?rnd=${app_release_no}"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript" src="<cdn:url value='/resources/app/js/journalvoucher/journalvoucher.js?rnd=${app_release_no}'/>"></script>
<script type="text/javascript" src="/services/EGF/resources/javascript/budgetHelper.js??rnd=${app_release_no}"></script>
	
<script>
var contextPath = "${pageContext.request.contextPath}"
</script>

<div style="margin-top: 15px;">
	<table class="table table-bordered" id="official_inbox">
		<thead>
			 <tr>
				<th><input name="select_all" type="checkbox" id="select_all">Select All</th>
				<th><spring:message code="lbl.created.date"/></th>
				<th><spring:message code="lbl.sender"/></th>
				<th><spring:message code="lbl.natureoftask"/></th>
				<th><spring:message code="lbl.status"/></th>
				<th><spring:message code="lbl.details"/></th>
				<th><spring:message code="lbl.elapsed.days"/></th>
				<th></th>
			 </tr>
		</thead>
	</table>
    <div id="approve_section" > 
    	<div id = "workflowForwardDiv">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<div class="headingsmallbg" align="center">
						<span class="bold"> Approval Details </span>
					</div>
				</tr>
				<br></br>
				<tr>
					<td class="greybox" width="5%">&nbsp;</td>
					<td class="greybox" id="deptLabel" width="10%">Approver Department : </td>
					<td class="greybox" width="14%">
						<select name="approverDepartment" id="approverDepartment" disabled="disabled">
							<option value="DEPT_25">Accounts Branch</option>
					    </select>
					</td>
					<td class="greybox" width="3%">&nbsp;</td>
					<td class="greybox" width="14%"> Approver Designation : </td>
					<td class="greybox" width="14%">
						<select name="approverDesignation" id="approverDesignation">
							<option value="">  ----Choose---- </option>
					    </select>
					</td>
					<td class="greybox" width="8%">&nbsp;</td>
					<td class="greybox" width="5%"> Approver : </td>
					<td class="greybox" width="14%">
						<select name="approverPosition" id="approverPosition" class="dropDownCss">
							<option value="">  ----Choose---- </option>
					    </select>
					</td>
					<td class="greybox" width="5%">&nbsp;</td>
				</tr>
			</table>
	    </div>
	    
		<div id="workflowCommentsDiv" align="center">
			<s:hidden id="workFlowAction" name="workFlowAction" />
			<table width="100%">
				<tr>
					<td width="10%" class="greybox">&nbsp;</td>
					<td width="25%" class="greybox">&nbsp;</td>
					<td width="5%" class="greybox"> Remarks :</td>
					<td class="greybox"><textarea id="approverComments" name="approverComments" rows="3" cols="45"></textarea></td>
					<td class="greybox">&nbsp;</td>
					<td class="greybox">&nbsp;</td>
				</tr>
			</table>
		</div>
		
		<div class="buttonbottom" align="center" id='approvebutton'>
			<table style="width: 100%; text-align: center;">
				<tr>
					<td>
						<input type="button" value="Approve" id="Approve" name="Approve" class="buttonsubmit" onclick="return validateWorkFlowApprover('Approve','jsValidationErrors');">
						<input type="button" value="Cancel" id="ApproveCancel" name="Cancel" class="buttonsubmit">
					</td>
				</tr>
			</table>
		</div>
		
		<div class="buttonbottom" align="center" id='forwardbutton'>
			<s:hidden id="approverName" name="approverName" />
			<table style="width: 100%; text-align: center;">
				<tr>
					<td>
						<input type="button" value="Forward" id="Forward" name="Forward" class="buttonsubmit" onclick="return validateWorkFlowApprover('Approve','jsValidationErrors');">
						<input type="button" value="Cancel" id="ForwardCancel" name="Cancel" class="buttonsubmit">
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/moment.min.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/inbox/inbox-multiselect.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
