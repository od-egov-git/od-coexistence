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


<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<link
	href="<egov:url path='/resources/css/displaytagFormatted.css?rnd=${app_release_no}'/>"
	rel="stylesheet" type="text/css" />
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="/services/EGF/resources/css/ccMenu.css?rnd=${app_release_no}" />
<title>PEX Reference No Assignment</title>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
</head>

<body>
	<script>
		
	</script>
	<s:form action="chequeAssignment" theme="simple">
		<jsp:include page="../budget/budgetHeader.jsp">
			<jsp:param name="heading" value="PEX Ref. No Assignment Search" />
		</jsp:include>
		<span class="mandatory1"> <s:actionerror /> <s:fielderror />
			<s:actionmessage />
		</span>
		<div class="formmainbox">
			<div class="subheadnew">PEX Ref. No Assignment Search</div>
			<table align="center" width="100%" cellpadding="0" cellspacing="0"
				id="paymentTable">
				<tr>
					<th class="bluebgheadtdnew"><s:text
							name="chq.assignment.select" /> <s:checkbox id="selectall"
							name="selectall" onclick="checkAll(this)" /></th>
					<th class="bluebgheadtdnew"><s:text name="Sl No" /></th>

					<th class="bluebgheadtdnew"><s:text
							name="chq.assignment.department.name" /></th>
					<th class="bluebgheadtdnew"><s:text
							name="chq.assignment.payment.voucherno" /></th>
					<th class="bluebgheadtdnew"><s:text
							name="chq.assignment.payment.voucherdate" /></th>
					<th class="bluebgheadtdnew"><s:text
							name="chq.assignment.partycode" /></th>
					<th class="bluebgheadtdnew"><s:text
							name="chq.assignment.payment.amount" /></th>

				</tr>
				<s:set var="counter" value="0" />
				<s:iterator value="accountNoAndPexEntryMap" status="stat">
					<tr>
						<td class="greybox"><s:property
								value="key.bankbranch.bank.name" /> - <s:property
								value="key.bankbranch.branchname" /></td>
						<td class="greybox"><s:property value="key.accountnumber" />
							<s:set var="count" value="key.id" /></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>

						<s:iterator value="value" status="s">

							<td style="text-align: center" class="blueborderfortdnew"><s:hidden
									id="voucherHeaderId"
									name="pexList[%{#counter}].voucherHeaderId"
									value="%{voucherHeaderId}" /> <s:hidden id="billId"
									name="pexList[%{#counter}].billId" value="%{billId}" /> <s:hidden
									id="billNumber" name="pexList[%{#counter}].billNumber"
									value="%{billNumber}" /> <s:hidden id="expenditureType"
									name="pexList[%{#counter}].expenditureType"
									value="%{expenditureType}" /> <s:checkbox
									name="pexList[%{#counter}].isSelected"
									id="isSelected%{#counter}" onclick="update(this)" /></td>
							<td align="left" style="text-align: center"
								class="blueborderfortdnew" />
							<s:property value="#s.index+1" />
							</td>
							<td style="text-align: center" class="blueborderfortdnew"><s:hidden
									id="departmentName" name="pexList[%{#counter}].departmentName"
									value="%{departmentName}" /> <s:property
									value="%{departmentName}" /></td>
							<td style="text-align: center" class="blueborderfortdnew"><s:hidden
									id="voucherNumber" name="pexList[%{#counter}].voucherNumber"
									value="%{voucherNumber}" /><a href="javascript:void(0);"
								onclick='viewVoucher(<s:property value="%{voucherHeaderId}"/>);'><s:property
										value="%{voucherNumber}" /></a>&nbsp;</td>
							<td style="text-align: center" class="blueborderfortdnew"><s:hidden
									id="voucherDate" name="pexList[%{#counter}].voucherDate"
									value="%{voucherDate}" /> <s:date name="%{voucherDate}"
									var="tempPaymentDate" format="dd/MM/yyyy" /> <s:date
									name="%{voucherDate}" format="dd/MM/yyyy" /> <s:hidden
									name="pexList[%{#counter}].tempPaymentDate"
									value="%{tempPaymentDate}"></s:hidden></td>
							<td style="text-align: center" class="blueborderfortdnew"><s:hidden
									id="paidTo" name="pexList[%{#counter}].paidTo"
									value="%{paidTo}" /> <s:property value="%{paidTo}" /></td>
							<td style="text-align: right" class="blueborderfortdnew"><s:hidden
									id="paidAmount" name="pexList[%{#counter}].paidAmount"
									value="%{paidAmount}" /> <s:text name="format.number">
									<s:param value="%{paidAmount}" />
								</s:text></td>
							<s:hidden id="bankAccountId"
								name="pexList[%{#counter}].bankAccountId"
								value="%{bankAccountId}" />
							<s:set var="counter" value="%{#counter+1}" />
					</tr>
				</s:iterator>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<s:if test="%{ paymentMode=='pex'}">
						<!-- <td class="greybox">
							<s:text name="chq.assignment.rtgs.refno"/><span class="mandatory1">*</span>
							<s:textfield id="rtgsRefNoMap['%{#count}']" name="rtgsRefNoMap['%{#count}']" value=""/>       
						</td>         -->
						<td class="greybox"><s:text name="chq.assignment.pex.date" /><span
							class="mandatory1">*</span> <s:date name="pexdateMap[%{#count}]"
								var="pexdateMap[%{#count}]" format="dd/MM/yyyy" /> <s:textfield
								id="pexdateMapId" name="pexdateMap[%{#count}]"
								value="%{pexdateMap[%{#count}]}" data-date-end-date="0d"
								onkeyup="DateFormat(this,this.value,event,false,'3')"
								placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
								data-inputmask="'mask': 'd/m/y'" /></td>
					</s:if>
				</tr>
				</s:iterator>
			</table>
			<div class="subheadsmallnew" id="noRecordsDiv"
				style="visibility: hidden">No Records Found</div>
			<br />
			<div>
			Total Amount (Rs) : <input type="text" name="caltotalamount" readonly="readonly" id="caltotalamount">
			</div>

			<div class="buttonbottom">
				<s:hidden id="selectedRows" name="selectedRows"
					value="%{selectedRows}" />
				<s:hidden id="pexContractorAssignment"
					name="pexContractorAssignment" value="%{pexContractorAssignment}" />
				<s:hidden id="paymentMode" name="paymentMode" value="%{paymentMode}" />
				<s:hidden name="billSubType" id="billSubType" value="%{billSubType}" />

				<s:submit id="assignChequeBtn" method="updatePex"
					value="Assign PEX Number" cssClass="buttonsubmit"
					onclick="return validate();" />
				<input type="button" value="Close"
					onclick="javascript:window.close()" class="button" />
			</div>
		</div>
		<s:token />
	</s:form>
	<script>
	var sum=0;
			function update(obj)
			{
				//alert(obj)		
 				var id ="";
 				var index = "";
				
				if(obj.checked){
					
					 id = obj.id;
					 index  = parseInt(id.replace("isSelected",""));					
					
					sum = sum + parseInt(document.getElementsByName("pexList["+index+"].paidAmount")[0].value);
					document.getElementById("caltotalamount").value = sum;
					document.getElementById('selectedRows').value=parseInt(document.getElementById('selectedRows').value)+1;
				}
				else{
					 id = obj.id;
					 index  = parseInt(id.replace("isSelected",""));
					sum = sum - parseInt(document.getElementsByName("pexList["+index+"].paidAmount")[0].value);
					document.getElementById("caltotalamount").value = sum;
					document.getElementById('selectedRows').value=parseInt(document.getElementById('selectedRows').value)-1;
				}
			}
			function updateDate(obj)
			{
				//bootbox.alert("Before"+obj); 
				//bootbox.alert("obj.name"+obj.name);
				//bootbox.alert("obj.value"+obj.value); 
				document.getElementById(obj).value=obj.value;         
			//	bootbox.alert("After"+obj);            
				//bootbox.alert("obj.name"+obj.name);          
			//	bootbox.alert("obj.value"+obj.value);         
			}
			function validate()
			{
				var result=true;
				
				if(document.getElementById('selectedRows').value=='' || document.getElementById('selectedRows').value==0)
				{
					bootbox.alert('Please select the payment voucher');
					return false;
				}
				if(document.getElementById('pexdateMapId').value=='')
					{
					
					bootbox.alert('Please select PEX Date ');
					return false;
					}
				<s:if test="%{paymentMode=='pex'}">
					//result= validateForRtgsMode();  
				</s:if>    
				document.chequeAssignment.action='/services/EGF/payment/chequeAssignment-updatePex.action';
	    		document.chequeAssignment.submit();
								 
				return true;                   
			}
		function validateForPexMode(){
				var noOfSelectedRows=document.getElementById('selectedRows').value;
				//bootbox.alert("sizseled"+noOfSelectedRows);      
				var chkCount=0;     
				var index;
				var isSelected=0;
				<s:set var="listCount" value="0"/>
				var chequeSize='<s:property value ="%{accountNoAndPexEntryMap.size()}"/>';
				
					<s:iterator value="accountNoAndPexEntryMap" status="stat">
					<s:set var="accountId" value="key.id"/>
				
					<s:iterator value="value" status="s">
					index='<s:property value="%{#listCount}"/>';       
					chequeDate='<s:property value="%{pexdateMap[#accountId]}"/>';     
					var paymentDate= document.getElementsByName("value["+index+"].tempPaymentDate")[0].value; 
						if(document.getElementById('isSelected'+index).checked){
							chkCount++;                 
							bootbox.alert("cheque Date"+chequeDate +"paymentDate"+paymentDate) ;   
							bootbox.alert(compareDate(paymentDate,chequeDate))  ;     
							if( compareDate(paymentDate,chequeDate) == -1){     
								bootbox.alert('Cheque Date cannot be less than  payment Date');
								return false;           
							 } 
							if(chkCount==noOfSelectedRows){ 
								//bootbox.alert("hi"); 
								return true;}                      
						}                               
					<s:set var="listCount" value="%{#listCount+1}"/>                      
					</s:iterator>          
				</s:iterator>
				return true;           
			}
		
			                
			function checkAll(obj)
			{        
				var listSize='<s:property value ="%{#counter}"/>';                              
				if(obj.checked)
				{                     
					for(var j=0;j<listSize;j++)  {     
						document.getElementById("isSelected"+j).checked=true;     
					} document.getElementById('selectedRows').value=listSize;                       
				}
				else
				{ 
					for(var i=0;i<listSize;i++){      
						document.getElementById('isSelected'+i).checked=false;
					}document.getElementById('selectedRows').value=0;
				}     
			}
			function viewVoucher(vid){
				var url = '../voucher/preApprovedVoucher-loadvoucherview.action?vhid='+vid;
				window.open(url,'Search','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');
		}
			                
		</script>


</body>
</html>
