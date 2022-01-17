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


<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<html>
<head>
<title><s:text name="budget.reappropriation.verify.title" /></title>
<link rel="stylesheet" href="/services/EGF/resources/css/tabber.css?rnd=${app_release_no}"
	TYPE="text/css">
<script type="text/javascript" src="/services/EGF/resources/javascript/tabber.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/tabber2.js?rnd=${app_release_no}"></script>
<STYLE type="text/css">
.yui-dt-liner {
	text-align: right;
}
</STYLE>
</head>
<body>
	<%-- <%@ include file='common-includes.jsp'%>
	<jsp:include page="budgetHeader.jsp" /> --%>
	<%@ include file='budgetReAppropriationSetUpVerify.jsp'%>
	<script>
					
	function populateSubSchemes(scheme){
		populatebudgetReAppropriation_subScheme({schemeId:scheme.options[scheme.selectedIndex].value})
	}
	
	function onHeaderSubSchemePopulation(req,res){
		if(budgetDetailsTable != null){
			headerSubScheme=dom.get('budgetReAppropriation_subScheme');
			pattern = 'budgetDetailList[{index}].subScheme.id'
			processGrid(budgetDetailsTable,function(element,grid){
				if(element) copyOptions(headerSubScheme,element)
			},pattern)
		}
		if(typeof preselectSubScheme=='function') preselectSubScheme()
    }
    
    <s:if test="%{shouldShowHeaderField('scheme') and shouldShowHeaderField('subScheme')}">
	populateSubSchemes(document.getElementById('budgetReAppropriation_scheme'))
	function preselectSubScheme(){
		subSchemes =  document.getElementById('budgetReAppropriation_subScheme');
		selectedValue="<s:property value='subScheme.id'/>"
		for(i=0;i<subSchemes.options.length;i++){
		  if(subSchemes.options[i].value==selectedValue){
			subSchemes.selectedIndex=i;
			break;
		  }
		}
		updateGrid('subScheme.id',document.getElementById('budgetReAppropriation_subScheme').selectedIndex);
	}
	</s:if>
</script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/javascript/calenderNew.js"></script>

	<script>
			if(opener != null && opener.top != null && opener.top.document.getElementById('inboxframe')!=null){
				opener.top.document.getElementById('inboxframe').contentWindow.egovInbox.refresh();
			}
			function onSubmit(){
					if(!validate(false,'create'))
						return false;
					else
						return true;
				}
			function validate(checkUser,method){
				document.budgetDetailForm.action='/services/EGF/budget/budgetReAppropriation-'+method+'Cao.action';
				document.budgetDetailForm.submit();
				return;
			}

			

			function alertMessage(estimate,anticipatory){
				if(estimate && anticipatory){
					bootbox.alert('<s:text name="msg.estimate.and.anticipatory.amount.must.be.number"/>');
					return false;
				}else if(estimate){
					bootbox.alert('<s:text name="msg.estimate.amount.must.be.number"/>');
					return false;
				}else if(anticipatory){
					bootbox.alert('<s:text name="msg.anticipatory.amount.must.be.number"/>');
					return false;
				}
			}
			function validateMandatoryFields(){
				if(document.getElementById('financialYear').value==0){
					bootbox.alert('<s:text name="msg.please.select.financial.year"/>');
					return false;
				}
				
				return true;
			}
			var callback = {
				     success: function(o) {
						document.getElementById('beReGrid').innerHTML = o.responseText;
						element = document.getElementById('isBeRe');
						if(document.getElementById('newBeRe').value == 'RE')
							element.selectedIndex = 1;
						else
							element.selectedIndex = 0;
						//updateBudgetDropDown();
				        },
				     failure: function(o) {
				     }
			} 
			function getBeRe(){
				element = document.getElementById('financialYear')
				id = element.options[element.selectedIndex].value;
				var transaction = YAHOO.util.Connect.asyncRequest('GET', 'budgetReAppropriation-ajaxLoadBeRe.action?id='+id, callback, null);
			}
			
			function loadActuals(event){
				event.preventDefault();
				document.budgetDetailForm.action='/services/EGF/budget/budgetReAppropriation-loadActualsCao.action';
	    		document.budgetDetailForm.submit();
			}
		</script>
	<s:actionmessage theme="simple" />
	<s:actionerror />
	<s:fielderror />
	<s:form name="budgetDetailForm" action="budgetReAppropriation"
		theme="simple">
		<s:token />
		<div class="formmainbox">
			<div class="tabber">
				<div class="tabbertab">
					<h2><s:text name="lbl.additional.appropriation"/> </h2>
					<span>
						<table width="60%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<div class="subheadnew">
										<s:text name="budget.reappropriation.title" />
									</div> <br />
								</td>
							</tr>
						</table>
						<%-- <table width="50%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="10%" class="bluebox">&nbsp;</td>
								<td class="bluebox"><s:text name="budget.financialYear" /><span
									class="mandatory1">*</span></td>
								<td class="bluebox"><s:select
										list="dropdownData.financialYearList" listKey="id"
										listValue="finYearRange" name="financialYear.id"
										value="financialYear.id" id="financialYear" headerKey="0"
										headerValue="%{getText('lbl.choose.options')}" onchange="getBeRe();"></s:select></td>
								<td class="bluebox" width="19%"><s:text name="budget.bere" /></td>
								<td class="bluebox"><s:select name="isBeRe" id="isBeRe"
										list="#{'BE':'BE','RE':'RE'}" value="beRe" disabled="true" /></td>
							</tr>
							
							<tr>
								<td class="greybox">&nbsp;</td>
								<td class="greybox"><s:text
										name="budgetReAppropriation.asOnDate" /></td>
								<td class="greybox"><input type="text" id="date"
									name="appropriationMisc.reAppropriationDate"
									style="width: 200px; height: 34px;"
									value='<s:date name="appropriationMisc.reAppropriationDate" format="dd/MM/yyyy"/>' /><a
									href="javascript:show_calendar('budgetDetailForm.date');"
									style="text-decoration: none">&nbsp;<img
										src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a></td>
								<td class="greybox"><s:text
										name="budgetReAppropriation.comments" /></td>
								<td class="greybox"><s:textarea
										id="appropriationMisc.remarks"
										name="appropriationMisc.remarks" cols="50" rows="3" style="width: 200px;"/></td>
								<td class="greybox"></td>
							</tr>
						</table> <br />
						 
						 <table width="60%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<div align="center">
										<s:submit method="loadActualsCao" key="lbl.get.actuals"
											cssClass="buttonsubmit" onclick="loadActuals(event)" />
									</div>
								</td>
							</tr>
						</table>--%>
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							id="budgetDetailFormTable">
							<tr>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td colspan="9">
									<div class="subheadsmallnew">
										<strong><s:text
												name="Budget line item verification" /></strong>
									</div>
								</td>
							</tr>
						</table>
						<div class="yui-skin-sam"
							style="width: 100%; overflow-x: auto; overflow-y: hidden;">
							<div id="budgetDetailTable"></div>
							<br />
						</div> <script>
			makeBudgetDetailTable();
			hideColumns();
			document.getElementById('budgetDetailTable').getElementsByTagName('table')[0].width = "100%";
			addGridRows();
			updateAllGridValues()
			<s:if test="%{getActionErrors().size()>0 || getFieldErrors().size()>0}">
				setValues();
			</s:if>
			for(i=0;i<budgetDetailsTable.getRecordSet().getLength();i++){
				computeAvailable("budgetReAppropriationList",i);
			}
		</script> <br />
						
						
					</span>
					<table width="60%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<div class="buttonbottom" style="padding-bottom: 10px;">
								<%@ include file='../budget/commonWorkflowMatrix.jsp'%>
								<%@ include file='../workflow/commonWorkflowMatrix-button.jsp'%>
									<%-- <input type="submit" value="Approve"
										id="budgetReAppropriation__create" name="method:createCao"
										onClick="javascript: return validate(false,'create');"
										class="buttonsubmit" />
									
									<s:submit onclick="javascript: self.close()" key="lbl.close"
										cssClass="button" /> --%>
								</div>
							</td>
						</tr>
					</table>
				</div>
				
				<!-- Individual tab -->

			</div>
			<s:hidden name="budgetAppId" id="budgetAppId" />
			<s:hidden name="actionName" id="actionName" />
		</div>
	</s:form>
	<div id="beReGrid" style="display: none"></div>
</body>
</html>
