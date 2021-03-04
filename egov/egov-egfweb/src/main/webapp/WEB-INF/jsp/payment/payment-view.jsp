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
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<html>
<head>
<link href="/services/EGF/resources/css/budget.css?rnd=${app_release_no}" rel="stylesheet"
	type="text/css" />
<link href="/services/EGF/resources/css/commonegovnew.css?rnd=${app_release_no}" rel="stylesheet"
	type="text/css" />
<link rel="stylesheet" href="/services/EGF/resources/css/tabber.css?rnd=${app_release_no}"
	TYPE="text/css">
<script type="text/javascript" src="/services/EGF/resources/javascript/tabber.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/tabber2.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"> </script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>

<script>
function fetchDeptId() {
	var id = '<s:property value="defaultDept"/>';
	var did = '<s:property value="%{paymentheader.voucherheader.vouchermis.departmentid.id}"/>';
	<s:if test="%{wfitemstate !='END'}">
		if(id != null && id != '-1' && id != '' ) {
			document.getElementById('departmentid').value = id;
			//document.getElementById('departmentid').disabled=true;
		} else if(did != null && did != '-1' && did != '' ) {
			if(document.getElementById('departmentid')!=null)
			document.getElementById('departmentid').value = did;
			//document.getElementById('departmentid').disabled=true;
		}
	</s:if>
}

function printVoucher(){
	document.forms[0].action='../report/billPaymentVoucherPrint-print.action?id=<s:property value="paymentheader.id"/>';
	document.forms[0].submit();
} 
function showHistory(stateId)
{
var url="../voucher/common-showHistory.action?stateId="+stateId;
		window.open(url,'Search','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');
}
function openVoucher(vid)
{
	var url = "/services/EGF/voucher/preApprovedVoucher-loadvoucherview.action?vhid="+ vid;
	window.open(url,'','width=900, height=700');
}
function openBudgetDtls()
{
	vhId=document.getElementById('pId').value;
	if(vhId != null && vhId != '')
	{
	var today = new Date();
	var date = today.getDate()+'/'+(today.getMonth()+1)+'/'+today.getFullYear();
	var url1 = '/services/EGF/report/budgetVarianceReport-loadData.action?asOnDate='+date+'&vtype=bpw&vhId='+vhId;
	window.open(url1,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700')
	}
	
	}
</script>

</head>

<body onload="loadVoucherDate();">
	<br>
	<s:form action="payment" theme="simple">
		<s:push value="model">
			<jsp:include page="../budget/budgetHeader.jsp">
				<jsp:param name="heading" value="Bill Payment View" />
			</jsp:include>
			
			<font style='color: red;' size="2">
				<p id="lblError" style="font: bold"></p>
				
			<s:if test="%{finanicalYearAndClosedPeriodCheckIsClosed}">
				<s:text name="financialyear.closedperiod.closed" ></s:text>
				</s:if>
				</font>
			
			
			<span class="mandatory1"> <s:actionerror /> <s:fielderror /></span>
			<span style="color: green"><s:actionmessage /></span>
			<div class="formmainbox">
				<div class="subheadnew">Bill Payment View</div>
				<table width="100%" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td>
							<div align="left">
								<br />
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td>
											<div class="tabber">
												<div class="tabbertab">
													<h2>Payment Details</h2>
													<span>
														<table width="100%" border="0" cellspacing="0"
															cellpadding="0">
															<div class="subheadsmallnew">Payment Details</div>
															<tr>
																<td width="9%" class="bluebox">&nbsp;</td>
																<s:if test="%{shouldShowHeaderField('fund')}">
																	<td width="12%" class="bluebox"><strong><s:text
																				name="voucher.fund" /></strong></td>
																	<td width="20%" class="bluebox"><s:property
																			value="%{paymentheader.voucherheader.fundId.name}" /></td>
																</s:if>
																<s:if test="%{shouldShowHeaderField('fundsource')}">
																	<td width="17%" class="bluebox"><strong><s:text
																				name="voucher.fundsource" /></strong></td>
																	<td width="33%" class="bluebox"><s:property
																			value="%{paymentheader.voucherheader.fundsourceId.name}" /></td>
																</s:if>
															</tr>
															<tr>
																<td class="greybox">&nbsp;</td>
																<s:if test="%{shouldShowHeaderField('department')}">
																	<td class="greybox"><strong><s:text
																				name="voucher.department" /></strong> <s:if
																			test="%{isFieldMandatory('department')}">
																			<span class="bluebox"><span class="mandatory1">*</span></span>
																		</s:if></td>
																	<%--<td class="greybox"><s:property value="%{getMasterName('department')}" /></td>--%>
																	
																	<td class="greybox"><s:select name="department"
																			id="department" list="dropdownData.departmentList"
																			listKey="code" listValue="name" headerKey="-1"
																			headerValue="%{getText('lbl.choose.options')}"
																			value="%{paymentheader.voucherheader.vouchermis.departmentcode}" /></td>
																</s:if>
																<s:if test="%{shouldShowHeaderField('functionary')}">
																	<td class="greybox"><strong><s:text
																				name="voucher.functionary" /></strong></td>
																	<td class="greybox" colspan="2"><s:property
																			value="%{paymentheader.voucherheader.vouchermis.functionary.name}" /></td>--%>
																			
																</s:if>
															</tr>
															<tr>
																<td class="bluebox">&nbsp;</td>
																<s:if test="%{shouldShowHeaderField('scheme')}">
																	<td class="bluebox"><strong><s:text
																				name="voucher.scheme" /></strong></td>
																	<td class="bluebox"><s:property
																			value="%{paymentheader.voucherheader.vouchermis.schemeid.name}" /></td>
																</s:if>
																<s:if test="%{shouldShowHeaderField('subscheme')}">
																	<td class="bluebox"><strong><s:text
																				name="voucher.subscheme" /></strong></td>
																	<td class="bluebox"><s:property
																			value="%{paymentheader.voucherheader.vouchermis.subschemeid.name}" /></td>
																</s:if>
															</tr>
															<tr>
																<td class="greybox">&nbsp;</td>
																<%--<s:if test="%{shouldShowHeaderField('function')}">
																	<td class="greybox"><strong><s:text
																				name="voucher.function" /></strong> <s:if
																			test="%{isFieldMandatory('function')}">
																			<span class="mandatory1">*</span>
																		</s:if></td>
																	<td class="greybox"><s:hidden
																			name="paymentheader.voucherheader.vouchermis.function.name" />
																		<s:property
																			value="%{paymentheader.voucherheader.vouchermis.function.name}" /></td>
																</s:if>--%>
                                                                <s:if test="%{shouldShowHeaderField('function')}">
																	<td class="greybox"><strong><s:text
																				name="voucher.function" /></strong> <s:if
																			test="%{isFieldMandatory('function')}">
																			<span class="mandatory1">*</span>
																		</s:if></td>
																	<td class="greybox"><s:select name="function"
																			id="function" list="dropdownData.functionList"
																			listKey="id" listValue="name" headerKey="-1"
																			headerValue="%{getText('lbl.choose.options')}"
																			value="%{paymentheader.voucherheader.vouchermis.function.id}" />
																		<%--  <s:property
																		value="%{billregister.egBillregistermis.function.name}" /> --%></td>
																</s:if>
																<s:else>
																	<td class="greybox">
																	<td class="greybox">
																</s:else>     

																<s:if test="%{shouldShowHeaderField('field')}">
																	<td class="greybox"><strong><s:text
																				name="voucher.field" /></strong></td>
																	<td class="greybox" colspan="4"><s:property
																			value="%{paymentheader.voucherheader.vouchermis.divisionid.name}" /></td>
																</s:if>
															</tr>
															<tr>
																<td class="bluebox">&nbsp;</td>
																<td class="bluebox"><strong><s:text
																			name="payment.voucherno" /></strong></td>
																<td class="bluebox"><s:property
																		value="%{paymentheader.voucherheader.voucherNumber}" /></td>
																<td class="bluebox"><s:text
																		name="payment.voucherdate" /><span class="mandatory1">*</span></td>
																<%--><td class="bluebox"><s:date
																		name="%{paymentheader.voucherheader.voucherDate}"
																		format="dd/MM/yyyy" /></td>--%>
																		<td class="greybox"><s:textfield
																		id="voucherdate" name="voucherdate"
																		value="%{paymentheader.voucherheader.voucherDate}" format="dd/MM/yyyy"
																		onkeyup="DateFormat(this,this.value,event,false,'3')"																		
																		class="form-control datepicker"
																		data-inputmask="'mask': 'd/m/y'" /></td>
															</tr>
															<%--<tr>
																<td class="greybox">&nbsp;</td>
																<td class="greybox"><strong><s:text
																			name="payment.bank" /></strong></td>
																<td class="greybox"><s:property
																		value="%{paymentheader.bankaccount.bankbranch.bank.name+'-'+paymentheader.bankaccount.bankbranch.branchname}" /></td>
																<td class="greybox"><strong><s:text
																			name="payment.bankaccount" /></strong></td>
																<td class="greybox" colspan="2"><s:property
																		value="%{paymentheader.bankaccount.accountnumber+'---'+paymentheader.bankaccount.bankbranch.bank.name}" /></td>
															</tr>
															<tr id="bankbalanceRow" style="visibility: hidden">
																<td class="bluebox">&nbsp;</td>
																<td class="bluebox" width="15%"><strong><s:text
																			name="payment.balance" />(Rs)</strong></td>
																<td class="bluebox" colspan="4"><span id="balance" /></td>
															</tr>--%>
															<s:if test='%{paymentheader.type.equalsIgnoreCase("TNEB")}'>
																<tr>
																	<td class="bluebox">&nbsp;</td>
																	<td class="bluebox"><s:text name="payment.bank" /><span
																		class="mandatory1">*</span></td>
																	<td class="bluebox"><s:property
																			value="%{paymentheader.bankaccount.bankbranch.bank.name+'-'+paymentheader.bankaccount.bankbranch.branchname}" /></td>
																	<td class="bluebox"><s:text
																			name="payment.bankaccount" /><span
																		class="mandatory1">*</span></td>
																	<td class="bluebox"><s:property
																			value="%{paymentheader.bankaccount.accountnumber+'---'+paymentheader.bankaccount.bankbranch.bank.name}" /></td>
																	<s:hidden name="bankbranch" id="bankbranch" />
																	<s:hidden name="bank_branch" id="bank_branch" /> 
																	<s:hidden name="bank_account" id="bank_account" />
																	<s:hidden name="bankaccount" id="bankaccount" />
															</tr>
															</s:if>
															<s:else>
															<tr>
																<td class="bluebox">&nbsp;</td>
																	<td class="bluebox"><s:text name="payment.bank" /><span
																		class="mandatory1">*</span></td>
																	<td class="bluebox"><s:select name="bankbranch"
																			id="bankbranch" list="dropdownData.bankbranchList"
																			listKey="id" listValue="bank.name+'-'+branchname"
																			headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
																			onchange="loadBankAccount(this)"
																			value="%{paymentheader.bankaccount.bankbranch.id}" /></td>
																	<egov:ajaxdropdown id="bankaccount"
																		fields="['Text','Value']" dropdownId="bankaccount"
																		url="voucher/common-ajaxLoadBankAccounts.action" />
																	<td class="bluebox"><s:text
																			name="payment.bankaccount" /><span
																		class="mandatory1">*</span></td>
																	<td class="bluebox" colspan="2"><s:select
																			name="bankaccount" id="bankaccount"
																			list="dropdownData.bankaccountList" listKey="id"
																			listValue="accountnumber+'---'+accounttype"
																			headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
																			onChange="populateAvailableBalance(this);"
																			value="%{paymentheader.bankaccount.id}" /></td>
																	<egov:updatevalues id="availableBalance"
																		fields="['Text']"
																		url="payment/payment-ajaxGetAccountBalance.action" />
																</tr>
																<tr id="bankbalanceRow">
																	<td class="bluebox">&nbsp;</td>
																	<td class="bluebox"><s:text
																				name="bank.onlinelink" /></td>
																	<td class="bluebox"><a id="onlineLink" href="#" target="_blank" style="font-size: 15px;">Click</a></td>
																<td class="bluebox" width="15%"><strong><s:text
																				name="payment.balance" />(Rs)</strong></td>
																	<td class="bluebox" colspan="4"><s:textfield
																			name="availableBalance" id="availableBalance" value="%{balance}"
																			readonly="true" style="text-align:right" /></td>
																</tr>
																
															</s:else>
	
															<tr>
																<td class="bluebox">&nbsp;</td>
																<%--<td class="bluebox" width="15%"><strong><s:text
																			name="payment.narration" /></strong></td>
																<td class="bluebox" colspan="4"><s:property
																		value="%{paymentheader.voucherheader.description}" /></td>--%>
																		<td class="greybox" width="15%"><s:text
																		name="payment.narration" /><span
																		class="mandatory1">*</span></td>
																<td class="greybox" colspan="4"><s:textarea
																		name="description" id="description"  cols="70" rows="4"
																		onblur="checkLength(this)" value="%{paymentheader.voucherheader.description}" /></td>
															</tr>
															<tr>
																<td class="greybox">&nbsp;</td>
																<td class="greybox"><strong><s:text
																			name="payment.mode" /></strong></td>
																<td class="greybox"><s:if
																		test="%{paymentheader.type == 'cash' || paymentheader.type == 'Cash'}">
																		<s:text name="cash.consolidated.cheque" />
																	</s:if> <s:else>
																		<s:text name="%{paymentheader.type}" />
																	</s:else></td>
																	<s:hidden name="billSubType" id="billSubType" value="%{billSubType}" />
																<td class="greybox"><strong><s:text
																			name="payment.amount" />(Rs)</strong></td>
																<td class="greybox" colspan="2"><span
																	id="paymentAmountspan" /></td>
															</tr>
															<s:hidden name="functionSel" id="functionSel"
																value="%{functionSel}" />
															<tr>
																<td class="bluebox">&nbsp;</td>
																<td class="bluebox"><strong>Comments</strong></td>
																<td class="bluebox" colspan="4"><s:textarea
																		name="comments" id="comments" cols="100" rows="3" readonly="true"
																		onblur="checkLength(this)" value="%{getComments()}" /></td>
															</tr>
															<tr>
																<td class="bluebox">&nbsp;</td>
																<td class="bluebox"><strong>Budget Details</strong></td>
																<td class="bluebox" colspan="4"><a href="#" onclick="openBudgetDtls('<s:property value='%{billVoucherHeader.id}'/>')">Click</a></td>
															</tr>
															
														</table> 

													</span>

												</div>


												<div class="tabbertab">
													<h2>Bill Details</h2>
													<span>
														<table align="center" border="0" cellpadding="0"
															cellspacing="0" class="newtable">
															<tr>
																<td colspan="6"><div class="subheadsmallnew">Bill
																		Details</div></td>
															</tr>
															<tr>
																<td colspan="6">
																	<div style="float: left; width: 100%;">
																		<table id="miscBillTable" align="center" border="0"
																			cellpadding="0" cellspacing="0" width="100%">
																			<tr>
																				<th class="bluebgheadtdnew">Bill Number
																				</td>
																				<th class="bluebgheadtdnew">Bill Date
																				</td>
																				<th class="bluebgheadtdnew">Bill Voucher Number
																				</th>
																				<th class="bluebgheadtdnew">Bill Voucher Date
																				</th>
																				<th class="bluebgheadtdnew">Party Name
																				</td>
																				<th class="bluebgheadtdnew">Bill Amount(Rs)
																				</td>
																				<th class="bluebgheadtdnew">Passed Amount(Rs)
																				</td>
																				<th class="bluebgheadtdnew">Paid Amount(Rs)
																				</td>
																			</tr>
																			<s:if test="%{miscBillList.size>0}">
																				<s:iterator var="p" value="miscBillList" status="s">
																					<tr>
																						<td style="text-align: center"
																							class="blueborderfortdnew"><s:property
																								value="%{billnumber}" /></td>
																						<td style="text-align: center"
																							class="blueborderfortdnew"><s:date
																								name="%{billdate}" format="dd/MM/yyyy" /></td>
																						<td style="text-align: center"
																							class="blueborderfortdnew">
																							<a href="#" onclick="openVoucher('<s:property value='%{billVoucherHeader.id}'/>');">
																								 <s:property value="%{billVoucherHeader.voucherNumber}" />
																						    </a>
																						</td>		 
																						<td style="text-align: center"
																							class="blueborderfortdnew"><s:date
																								name="%{billVoucherHeader.voucherDate}" format="dd/MM/yyyy" /></td>			
																						<td style="text-align: center"
																							class="blueborderfortdnew"><s:property
																								value="%{paidto}" /></td>
																						<td style="text-align: right"
																							class="blueborderfortdnew"><s:text
																								name="format.number">
																								<s:param value="%{billamount}" />
																							</s:text></td>
																						<td style="text-align: right"
																							class="blueborderfortdnew"><s:text
																								name="format.number">
																								<s:param value="%{passedamount}" />
																							</s:text></td>
																						<td style="text-align: right"
																							class="blueborderfortdnew"><s:text
																								name="format.number">
																								<s:param value="%{paidamount}" />
																							</s:text></td>
																						<c:set var="totalAmt"
																							value="${totalAmt+paidamount}" />
																					</tr>
																				</s:iterator>
																			</s:if>
																			<tr>
																				<td style="text-align: right" colspan="7"
																					class="blueborderfortdnew"><strong>Grand
																						Total</strong></td>
																				<td style="text-align: right"
																					class="blueborderfortdnew"><input type="text" name="grandTotal"
																							id="grandTotal"
																							value='<fmt:formatNumber value='${totalAmt}' pattern='#0.00' />'
																							style="text-align: right" readonly /></td>
																			</tr>
																			
																		</table>
																	</div>
																</td>
															</tr>
														</table>
													</span>
												</div>
												<!-- <div class="tabbertab" id="viewtab"> -->
													<h3>Payment Details</h3>
													<span>
														<table align="center" border="0" cellpadding="0"
															cellspacing="0" class="newtable">
															<tr>
																<td colspan="6"><div class="subheadsmallnew">Payment
																		Details</div></td>
															</tr>
															<tr>
																<td colspan="4">
																	<div style="float: left; width: 100%;">
																		<table id="chequeTable" align="center" border="0"
																			cellpadding="0" cellspacing="0" width="100%">
																			<tr>
																				<s:if
																					test="%{paymentheader.type == 'cash' || paymentheader.type == 'Cash'|| paymentheader.type == 'Cheque' || paymentheader.type == 'cheque'}">
																					<th class="bluebgheadtdnew">Cheque Number
																					</td>
																					<th class="bluebgheadtdnew">Cheque Date
																					</td>
																				</s:if>
																				<s:if
																					test="%{paymentheader.type == 'pex' || paymentheader.type == 'PEX' || paymentheader.type == 'Pex'}">
																					<th class="bluebgheadtdnew">PEX Number
																					</td>
																					<th class="bluebgheadtdnew">PEX Date
																					</td>
																				</s:if>
																				<s:if
																					test="%{paymentheader.type == 'rtgs' || paymentheader.type == 'RTGS' || paymentheader.type == 'Rtgs' paymentheader.type == 'advice'}">
																					<th class="bluebgheadtdnew">RTGS Number
																					</td>
																					<th class="bluebgheadtdnew">RTGS Date
																					</td>
																				</s:if>
																				<th class="bluebgheadtdnew">Party Code
																				</td>
																				<th class="bluebgheadtdnew"> Amount(Rs)
																				</td>
																				<th class="bluebgheadtdnew"> Status
																				</td>
																			</tr>
																			<s:if test="%{instrumentHeaderList.size>0}">
																				<s:iterator var="p" value="instrumentHeaderList"
																					status="s">
																					<tr>
																						<s:if
																							test="%{paymentheader.type == 'cash' || paymentheader.type == 'Cash' || paymentheader.type == 'Cheque' || paymentheader.type == 'cheque'}">
																							<td style="text-align: center"
																								class="blueborderfortdnew"><s:property
																									value="%{instrumentNumber}" /></td>
																							<td style="text-align: center"
																								class="blueborderfortdnew"><s:date
																									name="%{instrumentDate}" format="dd/MM/yyyy" /></td>
																						</s:if>
																						<s:else>
																							<td style="text-align: center"
																								class="blueborderfortdnew"><s:property
																									value="%{transactionNumber}" /></td>
																							<td style="text-align: center"
																								class="blueborderfortdnew"><s:date
																									name="%{transactionDate}" format="dd/MM/yyyy" /></td>
																						</s:else>
																						<td style="text-align: center"
																							class="blueborderfortdnew"><s:property
																								value="%{payTo}" /></td>
																						<td style="text-align: right"
																							class="blueborderfortdnew"><s:text
																								name="format.number">
																								<s:param value="%{instrumentAmount}" />
																							</s:text></td>
																						<td style="text-align: center"
																							class="blueborderfortdnew"><s:property
																								value="%{statusId.description}" /></td>
																					</tr>
																				</s:iterator>
																			</s:if>
																		</table>
																		<s:if
																			test="%{instrumentHeaderList==null || instrumentHeaderList.size==0}">
																			<div class="bottom" align="center">No Details Found !</div>
																		</s:if>
																	</div>
																</td>
															</tr>
														</table>
													</span>
												</div>
												<!-- individual tab -->
											</div> <!-- tabbber div -->
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
			
			<div class="buttonbottom" id="buttondiv">
			<%@ include file='../workflow/commonworkflowhistory.jsp'%>
				<s:hidden id="pId" name="paymentid" value="%{paymentheader.id}" />
				<s:hidden name="actionname" id="actionName" value="%{action}" />
				<s:hidden name="finanicalYearAndClosedPeriodCheckIsClosed" id="finanicalYearAndClosedPeriodCheckIsClosed" value="%{finanicalYearAndClosedPeriodCheckIsClosed}" />
				<s:if test="%{mode!='view'}">
				<s:if test="%{!finanicalYearAndClosedPeriodCheckIsClosed}">
					<%@ include file='../payment/commonWorkflowMatrix.jsp'%>
					<%@ include file='../workflow/commonWorkflowMatrix-button.jsp'%>
					<s:submit cssClass="button" id="printPreview" value="Print Preview"
						onclick="printVoucher()" />
				</s:if>
				<s:else>
				<input type="button" name="button2" id="button2" value="Close"
						class="button" onclick="window.parent.postMessage('close','*');window.close();" />
				</s:else>
				
				</s:if>
				<s:else>
					<s:submit cssClass="button" id="printPreview" value="Print Preview"
						onclick="printVoucher()" />
					<input type="button" name="button2" id="button2" value="Close"
						class="button" onclick="window.parent.postMessage('close','*');window.close();" />
				</s:else>
			</div>

			<script>
			
		document.getElementById('paymentAmountspan').innerHTML = "<fmt:formatNumber value='${totalAmt}' pattern='#0.00'/>";
		if('<%=request.getParameter("paymentid")%>'==null || '<%=request.getParameter("paymentid")%>'=='null'){
			//document.getElementById('backbtnid').style.display='inline';
			document.getElementById('printPreview').disabled=true;
		}
		else{
			//document.getElementById('backbtnid').style.display='none';
			document.getElementById('printPreview').disabled=false;
		}
		function loadVoucherDate()
		{
			var voucherDate = "";
			voucherDate='<s:date
			name="%{paymentheader.voucherheader.voucherDate}"
			format="dd/MM/yyyy" />';
			document.getElementById('voucherdate').value =voucherDate;
			
		}
			
		function checkLength(obj)
		{
			if(obj.value.length>1024)
			{
				bootbox.alert('Max 1024 characters are allowed for comments. Remaining characters are truncated.')
				obj.value = obj.value.substring(1,1024);
			}
		}
		//function refreshInbox()
		{
			if(opener && opener.top && opener.top.document.getElementById('inboxframe'))
				opener.top.document.getElementById('inboxframe').contentWindow.egovInbox.refresh();
		}
		
		if(document.getElementById('actionName').value!='' ||( '<%=request.getParameter("showMode")%>'!=null && '<%=request.getParameter("showMode")%>'=='view'))
		{
			//document.getElementById('backbtnid').style.display='none';
			if(document.getElementById('wfBtn0'))
				document.getElementById('wfBtn0').style.display='none';
			if(document.getElementById('wfBtn1'))
				document.getElementById('wfBtn1').style.display='none';
		}
          
		function loadBankAccount(obj)
		{
			var selectedBranchId=document.getElementById('bankbranch').value;
			//alert("selectedBranchId:"+selectedBranchId);
			document.getElementById("onlineLink").href='#';
			<s:iterator value="bankBranchList">
				var branchIdFromList='<s:property value="id"/>';
				if(branchIdFromList == selectedBranchId)
					{
						var link = '<s:property value="bank.onlinelink"/>';
						document.getElementById("onlineLink").href=link;
					}
			</s:iterator>
			var fund = 0;
			//alert("fund name::"+'<s:property value="%{paymentheader.voucherheader.fundId.name}"/>');
			//alert("fund id::"+'<s:property value="%{paymentheader.voucherheader.fundId.id}"/>');
			<s:if test="%{shouldShowHeaderField('fund')}">
				fund = <s:property value="%{paymentheader.voucherheader.fundId.id}"/>;
			</s:if>
			var vTypeOfAccount = '<s:property value="%{typeOfAccount}"/>';
			var billSubType = '<s:property value="%{billSubType}"/>';
			populatebankaccount({branchId:obj.options[obj.selectedIndex].value+'&date='+new Date(), typeOfAccount:vTypeOfAccount,fundId:fund,billSubType:billSubType} );
			//populatebankaccount({branchId:obj.options[obj.selectedIndex].value+'&date='+new Date()});
		}
		function calcGrandTotal(obj)
		{
			var vBillListSize = document.getElementById('billListSize').value;
			var index = obj.id.substring(10,obj.id.length);
			var putBackAmount = parseFloat(document.getElementById('payableAmt'+index).value);
			var paymentAmount = obj.value;
			if(paymentAmount == '' || isNaN(paymentAmount)) {
				bootbox.alert('<s:text name="msg.payment.amount.should.be.numeric.value"/> ');
				obj.value = putBackAmount.toFixed(vFixedDecimal);
			}
			
			if(paymentAmount > parseFloat(document.getElementById('payableAmt'+index).value) ) {
				bootbox.alert('<s:text name="msg.payment.should.not.be.greater.than.payable.amount"/>');
				obj.value = putBackAmount.toFixed(vFixedDecimal);
			}
			
			paymentAmount = obj.value;
			var vFinalGrandTotal = 0;
			obj.value = parseFloat(paymentAmount).toFixed(vFixedDecimal);
			for(var i = 0; i < vBillListSize; i++) {
				if(index == i) vFinalGrandTotal += parseFloat(paymentAmount);
				else vFinalGrandTotal += parseFloat(document.getElementById('paymentAmt'+i).value);
			}
			document.getElementById('grandTotal').value = vFinalGrandTotal.toFixed(vFixedDecimal);
			document.getElementById('paymentAmountspan').innerHTML = document.getElementById('grandTotal').value;
		}


		function populateAvailableBalance(accnumObj) 
		{
					if (document.getElementById('voucherdate').value == '') {
						bootbox.alert("<s:text name='msg.please.select.voucher.date'/>");
						accnumObj.options.value = -1;
						return;
					}
					if (accnumObj.options[accnumObj.selectedIndex].value == -1)
						document.getElementById('availableBalance').value = '';
					else
						populateavailableBalance({
							bankaccount : accnumObj.options[accnumObj.selectedIndex].value,
							voucherDate : document.getElementById('voucherdate').value
									+ '&date=' + new Date()
						});

		}
		var callback = {
				success : function(o) {
				console.log("success");
				document.getElementById('availableBalance').value = o.responseText;
				},
				failure : function(o) {
					console.log("failed");
				}
		}
			
		/*function onLoad(){
			if (jQuery("#bankBalanceCheck") == null || jQuery("#bankBalanceCheck").val() == "") {
				disableForm();
			}
		}*/
		
		function balanceCheck(obj, name, value)
		{
			
			if(!validateAppoveUser(name,value))
				return false;
			if(obj.id=='wfBtn1') // in case of Reject
				return true;
			if(document.getElementById('balance'))
			{
				if(parseFloat(document.getElementById('paymentAmountspan').innerHTML)>parseFloat(document.getElementById('balance').innerHTML))
				{
					bootbox.alert('Insufficient bank balance');
					return false;
				}
			}

	
			return true;
		}
		function onSubmit()
		{

		var balanceCheckMandatory='<s:text name="payment.mandatory"/>';
		var balanceCheckWarning='<s:text name="payment.warning"/>';
		var noBalanceCheck='<s:text name="payment.none"/>';

		if(document.getElementById('department').value=='-1')
		{
		bootbox.alert("<s:text name='msg.please.select.department'/>");

		return false;
		}



		if(document.getElementById('voucherdate').value=='')
		{
		bootbox.alert("<s:text name='msg.please.select.voucher.date'/>");

		return false;
		}
		if(document.getElementById('description') == null || document.getElementById('description').value == '')
		{
		bootbox.alert("<s:text name='msg.please.select.voucher.narration'/>");

		return false;
		}
		if(document.getElementById('billSubType').value!='TNEB')
		{
		if(document.getElementById('bankbranch').options[document.getElementById('bankbranch').selectedIndex].value==-1)
		{
		bootbox.alert("<s:text name='msg.please.select.bank'/>");

		return false;
		}
		if(document.getElementById('bankaccount').options[document.getElementById('bankaccount').selectedIndex].value==-1)
		{
		bootbox.alert("<s:text name='msg.please.select.bank.account'/>");

		return false;
		}
		}

		if(document.getElementById('grandTotal').value==0 || document.getElementById('grandTotal').value=='NaN')
		{
		bootbox.alert('<s:text name="msg.payment.amount.should.be.greater.than.zero"/>');
		document.getElementById('tabber1').onclick();

		return false;
		}

		document.forms[0].action='${pageContext.request.contextPath}/payment/payment-sendForApproval.action';
		    document.forms[0].submit();
		    return false;
		   

					/*}*/
		}	
		/*function balanceCheck() {

			if (document.getElementById('availableBalance')) {
				console.log("ins did");
				console.log(parseFloat(document.getElementById('grandTotal').value));
				console.log(parseFloat(document.getElementById('availableBalance').value));
				
				if(parseFloat(document.getElementById('grandTotal').value)>parseFloat(document.getElementById('availableBalance').value))
				{
					console.log("ins 44");
					return false;
				}
		}
			return true;
		} */
		/*function billIdsToPaymentAmountsMap(billTypeObj,id){
			alert("popoulate bill");
			var length = 0;
				length = <s:property value="%{billList.size()}"/>;
			alert("bill length::"+length);
			var selectedRowsArr = new Array();
			for(var index=0;index<length;index++){
					selectedRowsArr.push(
				document.getElementsByName(billTypeObj+"["+index+"].csBillId")[0].value+":"+
				document.getElementsByName(billTypeObj+"["+index+"].paymentAmt")[0].value);
				}
			document.getElementById(id).value = selectedRowsArr;
			//disableSelectedRows();
		}*/

		function validateAppoveUser(name,value){
			document.getElementById('lblError').innerHTML ="";
			document.getElementById("actionName").value= name;

			<s:if test="%{wfitemstate =='END'}">
				if(value == 'Approve' || value == 'Reject') {
					//document.getElementById("approverUserId").value=-1; //--Since we Are hiding Workflow apporval.
					return true;
				}
			</s:if>
			<s:else>
				if( (value == 'Approve' || value == 'Save And Forward' || value=='Forward' ) && null != document.getElementById("approverUserId") && document.getElementById("approverUserId").value == -1){
					document.getElementById('lblError').innerHTML ="Please Select the user";
					return false;
				}
			</s:else>
			
			return true;
		}
		
		
		function validateTab(indexx)
		{
			if(indexx==0)
			{
				document.getElementById('buttondiv').style.display='block';
			}
			else
			{
				document.getElementById('buttondiv').style.display='none';
			}
			return true;
		}
		
		var temp = window.setInterval(load,1);
		function load()
		{
			try{ if('<%=request.getParameter("showMode")%>'==null || '<%=request.getParameter("showMode")%>'=='null') document.getElementById('tabber2').style.display='none'; window.clearInterval(temp);}catch(e){}
		}
		if(document.getElementById('actionName').value=='modify')
			document.getElementById('wfHistoryDiv').style.display='none';
	</script>
			<s:if test="%{showMode!='view'}">
				<s:if test="%{validateUser('balancecheck')}">
					<script>
			if(document.getElementById('bankbalanceRow'))
			{
				document.getElementById('bankbalanceRow').style.visibility='visible';
				document.getElementById('balance').innerHTML='<s:property value="%{balance}"/>'
			}
		</script>
				</s:if>
				<s:if test="%{balance=='-1'}">
					<script>
	bootbox.alert("FundFlow Report not Generated to check Bank Balance. Please generate Report First");
	for(var i=0;i<document.forms[0].length;i++)
	if(document.forms[0].elements[i].id!='Close')
		document.forms[0].elements[i].disabled =true;
	</script>
				</s:if>
			</s:if>
			<script>fetchDeptId();</script>
		</s:push>
	</s:form>
</body>

</html>
