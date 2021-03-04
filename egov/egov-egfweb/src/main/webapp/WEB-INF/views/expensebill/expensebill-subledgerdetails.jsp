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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<style>
  span { cursor: pointer; }
  :focus { outline: 1px dashed green; }
</style>
<div class="panel-heading">
	<div class="panel-title">
		<spring:message  code="lbl.subledger.details" text="SubLedger Details"/>
	</div>
</div>
<div class="panel-body">
	<table class="table table-bordered" id="tblSubledgerAdd">
		<thead>
			<tr>
				<th><spring:message code="lbl.subledgertype" text="SubLedger Type"/></th>
				<th><spring:message code="lbl.subledgeraccount.code" text="Account Code"/></th>
				<th><spring:message code="lbl.subledgercode" text="Account Code" /></th>
				<th><spring:message code="lbl.payto" text="Party Name"/></th>
				<th><spring:message code="lbl.amount" text="Amount"/></th>
				<th><spring:message code="lbl.action" text="Action"/></th> 					
			</tr>
		</thead>
		<tbody>
			<tr id="subledgerdetailsrow">
				<td>
					<select name="tempSubLedger[0].subLedgerType" data-first-option="false" id="tempSubLedger[0].subLedgerType" data-idx="0" class="form-control subledgerGlType subledgerGl_code" >
					<option value=""><spring:message code="lbl.select" text="Select"/></option>
					<c:forEach items="${subLedgerTypes}" var="subLedgerType">
						<option value="${subLedgerType.id }">${subLedgerType.name}</option>
					</c:forEach>
				</select>
				</td>
				
				
				<td>
					<input type="hidden" name="tempSubLedger[0].detailkeyId" id="tempSubLedger[0].detailkeyId" class="debitDetailKeyId">
					<input type="text" name="tempSubLedger[0].subLedgerCode" id="tempSubLedger[0].subLedgerCode" data-idx="0" class="form-control subledger_code subLedgerCodeOT" placeholder="Type any letters of SubLedger name"  />
				
					</td>
				<td>
					<form:hidden path="" name="tempSubLedger[0].netPayableAccountId" id="tempSubLedger[0].netPayableAccountId" value="${netPayableAccountId}"/>
					<form:hidden path="" name="tempSubLedger[0].netPayableAccountCodeId" id="tempSubLedger[0].netPayableAccountCodeId" value="${netPayableAccountCodeId}"/>
					<form:hidden path="" name="tempSubLedger[0].netPayableGlcode" id="tempSubLedger[0].netPayableGlcode" />
					<form:hidden path="" name="tempSubLedger[0].netPayableAccountHead" id="tempSubLedger[0].netPayableAccountHead" />
					<form:hidden path="" name="tempSubLedger[0].netPayableIsSubLedger" id="tempSubLedger[0].netPayableIsSubLedger" />
					<form:hidden path="" name="tempSubLedger[0].netPayableDetailTypeId" id="tempSubLedger[0].netPayableDetailTypeId" />
					<form:hidden path="" name="tempSubLedger[0].netPayableDetailKeyId" id="tempSubLedger[0].netPayableDetailKeyId" />
					<form:hidden path="" name="tempSubLedger[0].netPayableDetailTypeName" id="tempSubLedger[0].netPayableDetailTypeName" />
					<form:hidden path="" name="tempSubLedger[0].netPayableDetailKeyName" id="tempSubLedger[0].netPayableDetailKeyName" />
					<form:select path="" data-first-option="false" name="tempSubLedger[0].netPayableAccountCode" id="tempSubLedger[0].netPayableAccountCode" data-idx="0" class="form-control netPayableAccount_Code"  >
						<form:option value=""> <spring:message code="lbl.select" text="Select"/> </form:option>
					</form:select>
				</td>
				
				
					
				<td>
				<form:input class="form-control subledger_Payto" id="tempSubLedger[0].payTo" path="egBillregistermis.payto" data-idx="0" maxlength="350" />
				
				</td>
				<td>
				<input type="text" id="tempSubLedger[0].expense-netPayableAmount" name="tempSubLedger[0].netPayableAmount"  data-idx="0" class="form-control text-right netPayable_Amount" onkeyup="decimalvalue(this);" data-pattern="decimalvalue"> 
				</td> 
				<td class="text-center"><span style="cursor:pointer;" onclick="addSubledgerRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding subledge-delete-row" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			</tr>
		</tbody>
	</table>
</div>
