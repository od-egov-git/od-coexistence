<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>

<style>
    .file-ellipsis {
        width : auto !Important;
    }

    .padding-10
    {
        padding:10px;
    }
</style>

<form:form role="form" action="${pageContext.request.contextPath}/expensebill/update/${egBillregister.id}" method="POST"  modelAttribute="egBillregister" id="refundCreate" cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
  <input type="hidden" name="vhid" value="${vhid}"/>
  <input type="hidden" name="citizen" value="${citizen}"/>
  <div class="row">
	<div class="col-md-12">
	 <div>
	  <div class="panel panel-primary" data-collapsed="0">
		<div>
		 <div class="panel-title">
			<div class="subheadnew">Voucher View</div>
			</div>
		 </div>
		 <div>
	<c:if test="${glcodedetailIdmsg !=null && glcodedetailIdmsg !=''}">
	  <p style="color:red;">  ${glcodedetailIdmsg} <p>
	 </c:if>
	</div>
		 <div class="form-group">
			 <label class="col-sm-3 control-label text-right"><spring:message code="lbl.billnumber" text="Bill Number"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="billnumber" path="billnumber" maxlength="60" readonly="true" />
				</div>
				<label class="col-sm-3 control-label text-right"><spring:message code="lbl.billdate"  text="Bill Date"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
				   <c:choose>
	                 <c:when test="${refundable != null && !refundable.isEmpty()}">
	                   <form:input id="billdate" path="billdate" class="form-control datepicker" readonly="true" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
					   <form:errors path="billdate" cssClass="add-margin error-msg" />
	                 </c:when>
	                 <c:otherwise>
					<form:input id="billdate" path="billdate" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
					<form:errors path="billdate" cssClass="add-margin error-msg" />
	                 </c:otherwise>
	               </c:choose>
				</div>
		</div>
			 
			<div class="form-group">
			<label class="col-sm-3 control-label text-right">Fund <span class="mandatory"></span>: </label>
				<div class="col-sm-3 add-margin">
					<form:select path="egBillregistermis.fund" data-first-option="false" id="egBillregistermis.fund" class="form-control" required="required"  >
						<form:option value=""><spring:message code="lbl.select" /></form:option>
						<form:options items="${fundList}" itemValue="id" itemLabel="name" />
					</form:select>
					<form:errors path="egBillregistermis.fund" cssClass="add-margin error-msg" />
				</div>
				<label class="col-sm-3 control-label text-right">Fund Source: </label>
				<div class="col-sm-3 add-margin">
				<select name="egBillregistermis.fundsource" id="egBillregistermis.fundsource"  class="form-control">
					<option value="">-Select-</option>
					<c:forEach items="${fundsourceList}" var="fundSource" varStatus="loop">
						<option value="${fundSource.id}">${fundSource.name}</option>
					</c:forEach>
				</select>
				</div>	
			</div>
			
			<div class="form-group">
			<label class="col-sm-3 control-label text-right">scheme: </label>
				<div class="col-sm-3 add-margin">
				<select name="egBillregistermis.schemeId" id="egBillregistermis.schemeId"  class="form-control schemeType">
					<option value="">-Select-</option>
					<c:forEach items="${schemeList}" var="scheme" varStatus="loop">
						<option value="${scheme.id}">${scheme.name}</option>
					</c:forEach>
				</select>
				</div>	
			<label class="col-sm-3 control-label text-right">Sub Scheme :</b></label>
				<div class="col-sm-3 add-margin">
				<select name="egBillregistermis.subSchemeId" id="egBillregistermis.subSchemeId"  class="form-control subSchemeType">
					<option value="">-Select-</option>
					<c:forEach items="${SubSchemeList}" var="subScheme" varStatus="loop">
						<option value="${subScheme.id}">${subScheme.name}</option>
					</c:forEach>
				</select>
				</div>				
			</div>
			
			<div class="form-group">
				<label class="col-sm-3 control-label text-right">Department <span class="mandatory"></span> :</b></label>
				<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.departmentcode"  data-first-option="false" id="egBillregistermis.departmentcode" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${departments}" itemValue="code" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.departmentcode" cssClass="add-margin error-msg" />
					</div>
			  	<%-- <label class="col-sm-3 control-label text-right"><b>Sub division <span class="mandatory"></span> :</b></label>
				<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.subdivision" data-first-option="false" id="egBillregistermis.subdivision" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${subdivision}" itemValue="subdivisionName" itemLabel="subdivisionName" />
						</form:select>
						<form:errors path="egBillregistermis.subdivision" cssClass="add-margin error-msg" />
				</div> --%>				
			</div>	
			<div class="form-group">
				<label class="col-sm-3 control-label text-right"><b>Function<span class="mandatory"></span> :</b></label>
				<div class="col-sm-3 add-margin">
					<form:select path="egBillregistermis.function" id="egBillregistermis.function"  required="required" class="form-control">
						<form:option value="">-Select-</form:option>
						<form:options items="${cFunctions}" itemValue="id" itemLabel="name"/>  
					</form:select>
				</div>
			
				<label class="col-sm-3 control-label text-right">Bill Type <span class="mandatory"></span> :  </b></label>
				<div class="col-sm-3 add-margin">
					<form:select path="egBillregistermis.egBillSubType"  data-first-option="false" id="billSubType" class="form-control" required="required">
					<form:option value=""><spring:message code="lbl.select" text="Select"/></form:option>
					<form:options items="${billSubTypes}" itemValue="id" itemLabel="name" />
				</form:select>
				<form:errors path="egBillregistermis.egBillSubType" cssClass="add-margin error-msg" />
			    </div>				
			</div>
			
			<div class="form-group">
				<label class="col-sm-3 control-label text-right"><b>Narration :</label>
				<div class="col-sm-3 add-margin">
				    <textarea name="egBillregistermis.narration" id="narration"  class="form-control" maxlength="1024" ></textarea>
				</div>					
			</div>
	</div>
	
	<%-- <input type="hidden" name="egBillregistermis.fund" id="egBillregistermis.fund" value="${fundid}"/>
	<input type="hidden" name="egBillregistermis.departmentcode" id="egBillregistermis.departmentcode" value="${departcode}"/> --%>
	<%-- <input type="hidden" name="egBillregistermis.narration" id="egBillregistermis.narration" value="${voucherDetails.narration}"/> --%>
	<%-- <input type="hidden" name="egBillregistermis.budgetaryAppnumber" id="egBillregistermis.budgetaryAppnumber" value="${voucherDetails.banNumber}"/>
	<input type="hidden" name="egBillregistermis.fundsource" id="egBillregistermis.fundsource" value="${fundsource}"/>
	<input type="hidden" name="egBillregistermis.egBillSubType" id="egBillregistermis.egBillSubType" value="${billsubtype}"/> 
	<c:if test="${voucherDetails.subdivision !=null && voucherDetails.subdivision !=''}">
	<input type="hidden" name="egBillregistermis.subdivision" id="egBillregistermis.subdivision" value="${voucherDetails.subdivision}"/> 
	</c:if> --%>
	
	
	
	<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.accountdetails" text="Account Details"/>
		</div>
	</div>
	
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblaccountdetails">
			<thead>
				<tr>
				    <%-- <th><spring:message code="lbl.function.name" text="Function Name"/></th> --%>
					<th><spring:message code="lbl.account.code" text="Account Code"/></th>
					<th><spring:message code="lbl.account.head" text="Account Head"/></th>
					<th><spring:message code="lbl.debit.amount" text="Debit Amount"/></th>
					<th><spring:message code="lbl.credit.amount" text="Credit Amount"/></th>
					<th><spring:message code="lbl.previous.debit.amount" text="privious Amount"/></th>
					<th><spring:message code="lbl.refund.debit.amount" text="Refund Debit Amount"/></th>
					<th><spring:message code="lbl.action" text="Action"/></th>				
					<%-- <th><spring:message code="lbl.refund.credit.amount" text="Refund Credit Amount"/></th> --%>
					
				</tr>
			</thead>
			<tbody>
			
			 <c:choose>
			  <c:when test="${!accountDetails.isEmpty()}">
				<c:forEach items="${accountDetails}" var="accountDetail" varStatus="status">
				 <tr id="creditdetailsrow">				  
				    <input type="hidden" id="billDetails[${status.index}].glcodeid" readonly value="${accountDetail.glcodeid}">
				    <input type="hidden" id="billDetails[${status.index}].glcode" readonly value="${accountDetail.glcode}"> 
				    <td><input type="hidden" name="billDetails[${status.index}].glcodeid" value="${accountDetail.glcodeid}">${accountDetail.glcode}</td>
					<td><input type="hidden" name="billDetails[${status.index}].chartOfAccounts.name" value="${accountDetail.chartOfAccounts.name}">${accountDetail.accounthead}</td>
					<td>${accountDetail.debitamount}</td>
					<td>${accountDetail.creditamount}</td>
					<td>${accountDetail.previousAmount}</td>
					<td><input type="text" name="billDetails[${status.index}].debitamount" id="billDetails[${status.index}].debitamount" oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');"  class="form-control"/></td>
					<td></td>
				 </tr>
				</c:forEach>
				 <tr>
				  <td style="text-align: right" colspan="3"><b>Total</b></td>
				  <td><fmt:formatNumber value="${dbAmount}" pattern="#0.00" /></td>
				  <td><fmt:formatNumber value="${crAmount}" pattern="#0.00" /></td>	
				  <td colspan="1"></td>				
				 </tr>
			  </c:when>
			<c:otherwise>
				<tr id="creditdetailsrow">
				<td>
					<input type="text" id="billDetails[0].creditGlcode" name="billDetails[0].creditGlcode" class="form-control table-input creditDetailGlcode creditGlcode"  data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" >
 				
                    <form:hidden path="" name="billDetails[0].glcode" id="billDetails[0].glcode" class="form-control table-input hidden-input creditaccountcode"/> 
					<form:hidden path="" name="billDetails[0].glcodeid" id="billDetails[0].glcodeid"  class="form-control table-input hidden-input creditdetailid"/>
					<form:hidden path="" name="billDetails[0].isSubLedger" id="billDetails[0].isSubLedger" class="form-control table-input hidden-input creditIsSubLedger"/>
					<form:hidden path="" name="billDetails[0].detailTypeId" id="billDetails[0].detailTypeId" class="form-control table-input hidden-input creditDetailTypeId"/>
					<form:hidden path="" name="billDetails[0].detailKeyId" id="billDetails[0].detailKeyId" class="form-control table-input hidden-input creditDetailKeyId"/> 
					<form:hidden path="" name="billDetails[0].detailTypeName" id="billDetails[0].detailTypeName" class="form-control table-input hidden-input creditDetailTypeName"/>
					<form:hidden path="" name="billDetails[0].detailKeyName" id="billDetails[0].detailKeyName" class="form-control table-input hidden-input creditDetailKeyName"/>  
				
				    <!-- <input type="hidden" id="billDetails[0].glcodeid" name="billDetails[0].glcodeid" >-->
				    <input type="hidden" name="billDetails[0].creditamount"  value="0" >  
				</td> 
			   
			   <td>	<input type="text" id="billDetails[0].creditAccountHead" name="billDetails[0].creditAccountHead"  class="form-control creditdetailname" disabled> </td>				
				
				
<!-- 				<td><input type="hidden" name="billDetails[0].debitamount" id="billDetails[0].debitamount" data-errormsg="debitamount Amount is mandatory!" onkeyup="decimalvalue(this);" onblur="calcualteNetpaybleAmount();" data-pattern="decimalvalue" data-idx="0" data-optional="0" class="form-control table-input text-right creditAmount"   maxlength="12"  /></td>
 -->				<!-- <td>
					<input path="" name="billDetails[0].creditamount" id="billDetails[0].creditamount" data-errormsg="Credit Amount is mandatory!" onkeyup="decimalvalue(this);" onblur="calcualteNetpaybleAmount();" data-pattern="decimalvalue" data-idx="0" data-optional="0" class="form-control table-input text-right creditAmount"   maxlength="12"  />
				</td> -->
				<td>0</td>
				<td></td>
				<td></td>
				<td><input type="text" name="billDetails[0].debitamount" id="billDetails[0].debitamount" onchange="changeGlCodeBlank(this)"  oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');"
			   class="form-control"/></td> 
								
				<td class="text-center"><span style="cursor:pointer;" onclick="addCreditDetailsRow();" tabindex="0" id="tempCreditDetails[0].addButton" data-toggle="tooltip" title="" data-original-title="press ENTER to Add!" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding credit-delete-row" onclick="deleteCreditDetailsRow(this);"><i class="fa fa-trash" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			</tr>
			</c:otherwise>
		 </c:choose>
		</tbody> 
	 </table>
	</div>
	
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.subledgerdetails" text="Sub-ledger Details"/>
		</div>
	</div>
	
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblSubledgerAdd">
			<thead>
				<tr>
				  
					<th><spring:message code="lbl.account.code" text="Account Code"/></th>
					<th><spring:message code="lbl.detailed.type" text="Detailed Type"/></th>
					<th><spring:message code="lbl.detailed.code" text="Detailed Code"/></th>
					<th><spring:message code="lbl.detailed.key" text="Detailed Key"/></th>
					<th><spring:message code="lbl.rpamount" text="Amount(Rs.)"/></th>
					<th><spring:message code="lbl.action" text="Action"/></th>
				</tr>
			</thead>
			<tbody>
			 <c:choose>
			  <c:when test="${!subLedgerlist.isEmpty()}">
				<c:forEach items="${subLedgerlist}" var="subLedger" varStatus="count">
			  	<tr id="subledgerdetailsrow">
				  <td>
				  <!-- <input type="hidden" name="billPayeedetails[0].id" id="subLedgerDetailsId_0" class ="subLedgerDetailsId"/>
				  <input type="hidden" name="billPayeedetails[0].isDebit" value="false" id="subLedgerIsDebit_0" class ="subledgerisdebit"/>
				  <input type="hidden" name="billPayeedetails[0].debitAmount" value="0" id="subLedgerDebitAmount_0" class ="subledgerdebitamount"/>
				   -->
				  
				  <select id="tempSubLedger[${count.index}].netPayableAccountCode" data-first-option="false" name="billPayeedetails[${count.index}].egBilldetailsId.glcodeid" class="form-control" required="required">
				  <option value="${subLedger.glcode.id}">${subLedger.glcode.glcode}</option>
				  <c:forEach var="accountDetail" items="${accountDetails}">
				    <c:if test="${not empty accountDetail.creditamount && accountDetail.creditamount ne '0.00'}">
				    <option value="${accountDetail.glcodeid}">${accountDetail.glcode}</option>
				    </c:if>
                    </c:forEach>
				  
				                     
				  </select>
				  </td>
				  <td>
				   <select name="billPayeedetails[${count.index}].egBilldetailsId.id" data-first-option="false" id="tempSubLedger[${count.index}].subLedgerType" data-idx="0" onchange="subledgerChange(this);" class="form-control subledgerGlType" >
					<option value="${subLedger.detailType.id}">${subLedger.detailType.description}</option>
					<c:forEach items="${subLedgerTypes}" var="subLedgerType">
						<option value="${subLedgerType.id}">${subLedgerType.name}</option>
					</c:forEach>
					
					
				  </td>
				  <td>
				    <input type="hidden" name="billPayeedetails[${count.index}].accountDetailTypeId" id="tempSubLedger[${count.index}].detailTypeId"  class="form-control table-input hidden-input subLedgerDetailTypeId" value="${subLedger.detailType.id}"/>
					<input type="hidden" name="billPayeedetails[${count.index}].accountDetailKeyId" id="tempSubLedger[${count.index}].detailkeyId" class="debitDetailKeyId" value="${subLedger.detailKeyId}">
					<input type="text" name="billPayeedetails[${count.index}].detailTypeName" id="tempSubLedger[${count.index}].subLedgerCode" data-idx="0" class="form-control subledger_code subLedgerCodeOT" value="${subLedger.detailKey}" placeholder="Type any letters of SubLedger name"  />
				  </td>
				  <td>
				  <input type="text" class="form-control subledger_Payto" id="tempSubLedger[${count.index}].payTo" name="egBillregistermis.payto"  data-idx="0" maxlength="350" value="${subLedger.detailKey}"/>
				  
				  </td>
				  <td>
				  <input type="text" id="tempSubLedger[${count.index}].netPayable_Amount" name="billPayeedetails[${count.index}].creditAmount" 
				     data-idx="0" class="form-control text-right netPayable_Amount" oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');" data-pattern="decimalvalue" value=""> 
				  </td>
				  <td class="text-center"><span style="cursor:pointer;" onclick="addSubledgerRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding subledge-delete-row" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			
				</tr>
				
				</c:forEach>	
				
			</c:when>
		  <c:otherwise>
			<tr id="subledgerdetailsrow">
			  <td>
				  <select id="glcodeid" data-first-option="false" name="billPayeedetails[0].egBilldetailsId.glcodeid" class="form-control netPayableAccount_Code" required="required">
				  <option value=""><spring:message code="lbl.select" text="Select"/>
				  <c:forEach var="accountDetail" items="${accountDetails}">
				    <c:if test="${not empty accountDetail.creditamount && accountDetail.creditamount ne '0.00'}">
				    <option value="${accountDetail.glcodeid}">${accountDetail.glcode}</option>
				    </c:if>
				    </c:forEach>
				  </select>
				  </td>
				  <td>
				   <select name="billPayeedetails[0].egBilldetailsId.id" data-first-option="false" id="tempSubLedger[0].subLedgerType" data-idx="0" class="form-control subledgerGlType" >
					<option value=""><spring:message code="lbl.select" text="Select"/></option>
					<c:forEach items="${subLedgerTypes}" var="subLedgerType">
						<option value="${subLedgerType.id}">${subLedgerType.name}</option>
					</c:forEach>
				  </select>
				  </td>
				  <td>
				    <input type="hidden" name="billPayeedetails[0].accountDetailTypeId" id="tempSubLedger[0].detailTypeId"  class="form-control table-input hidden-input subLedgerDetailTypeId"/>
					<input type="hidden" name="billPayeedetails[0].accountDetailKeyId" id="tempSubLedger[0].detailkeyId" class="debitDetailKeyId">
					<input type="text" name="billPayeedetails[0].detailTypeName" id="tempSubLedger[0].subLedgerCode" data-idx="0" class="form-control subledger_code subLedgerCodeOT" value="" placeholder="Type any letters of SubLedger name"  />
				  </td>
				  <td>
				  <input type="text" class="form-control subledger_Payto" id="tempSubLedger[0].payTo" name="egBillregistermis.payto" value="" data-idx="0" maxlength="350" />
				  
				  </td>
				  <td>
				  <input type="text" id="tempSubLedger[0].netPayable_Amount" name="billPayeedetails[0].creditAmount" 
				     data-idx="0" class="form-control text-right netPayable_Amount" oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');" data-pattern="decimalvalue"> 
				  </td>
				  <td class="text-center"><span style="cursor:pointer;" onclick="addSubledgerRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding subledge-delete-row" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			
			</tr>
		  </c:otherwise>
		</c:choose>
		</tbody> 
	 </table>
	</div>
	<%-- <div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.accountdetails" text="Account Details"/>
		</div>
	</div>
	
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblcreditdetails">
			<thead>
				<tr>
				    <th><spring:message code="lbl.function.name" text="Function Name"/></th>
					<th><spring:message code="lbl.account.code" text="Account Code"/></th>
					<th><spring:message code="lbl.account.head" text="Account Head"/></th>
					<th><spring:message code="lbl.debit.amount" text="Debit Amount"/></th>
					<th><spring:message code="lbl.credit.amount" text="Credit Amount"/></th>
					<th><spring:message code="lbl.previous.debit.amount" text="privious Amount"/></th>
					<th><spring:message code="lbl.refund.debit.amount" text="Refund Debit Amount"/></th>
					<th><spring:message code="lbl.refund.credit.amount" text="Refund Credit Amount"/></th>
					
				</tr>
			</thead>
			<tbody>
			 <c:forEach items="${egBillregister.billDetails}" var="billDeatils" varStatus="item">
				<tr id="creditdetailsrow">
				<td>
					<!-- <input type="text" id="billDetails[0].creditGlcode" name="billDetails[0].creditGlcode" class="form-control table-input creditDetailGlcode creditGlcode"  data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"   placeholder="Type any letters of Account code" > -->
 					<span class="accountDetailsGlCode_${item.index }" id="billDetails[0].creditGlcode">${billDeatils.chartOfAccounts.glcode }</span>
 					<form:hidden path="" name="billDetails[0].glcode" id="billDetails[0].glcode" value="${billDeatils.chartOfAccounts.glcode }" class="form-control table-input hidden-input creditaccountcode"/>
                    <form:hidden path="billDetails[${item.index }].glcodeid" id="billDetails[0].glcodeid" class="accountDetailsGlCodeId" value="${billDeatils.glcodeid }"/> 
					<form:hidden path="" name="billDetails[0].glcodeid" id="billDetails[0].glcodeid"  class="form-control table-input hidden-input creditdetailid"/>
					<form:hidden path="" name="billDetails[0].isSubLedger" id="billDetails[0].isSubLedger" class="form-control table-input hidden-input creditIsSubLedger"/>
					<form:hidden path="" name="billDetails[0].detailTypeId" id="billDetails[0].detailTypeId" class="form-control table-input hidden-input creditDetailTypeId"/>
					<form:hidden path="" name="billDetails[0].detailKeyId" id="billDetails[0].detailKeyId" class="form-control table-input hidden-input creditDetailKeyId"/> 
					<form:hidden path="" name="billDetails[0].detailTypeName" id="billDetails[0].detailTypeName" class="form-control table-input hidden-input creditDetailTypeName"/>
					<form:hidden path="" name="billDetails[0].detailKeyName" id="billDetails[0].detailKeyName" class="form-control table-input hidden-input creditDetailKeyName"/>  
					<!-- <input type="hidden" name="billDetails[0].debitamount"  value="0" >   -->
				</td> 
			   <td>	
			   		<span class="billDetails[0].creditAccountHead_${item.index }" name="billDetails[0].creditAccountHead" disabled>${billDeatils.chartOfAccounts.name }</span>
			   </td>				
				<td>${billDeatils.debitamount}</td>
				<td>${billDeatils.creditamount}</td>
				<td>0</td>
				<td>
			   		<input type="text" style="border:1px" class="billDetails[0].debitamount_${item.index } billDetails[0].debitamount" id="billDetails[0].debitamount" onchange="changeGlCodeBlank(this)" oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');" class="form-control"/> 
			   </td> 			
				<!-- <td class="text-center"><span style="cursor:pointer;" onclick="addCreditDetailsRow();" tabindex="0" id="tempCreditDetails[0].addButton" data-toggle="tooltip" title="" data-original-title="press ENTER to Add!" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 
				 <span class="add-padding credit-delete-row" onclick="deleteCreditDetailsRow(this);"><i class="fa fa-trash" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td> -->
			</tr>
			</c:forEach>
		</tbody> 
	 </table>
	</div>
	
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.subledgerdetails" text="Sub-ledger Details"/>
		</div>
	</div>
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblSubledgerAdd">
			<thead>
				<tr>
					<th><spring:message code="lbl.account.code" text="Account Code"/></th>
					<th><spring:message code="lbl.detailed.type" text="Detailed Type"/></th>
					<th><spring:message code="lbl.detailed.code" text="Detailed Code"/></th>
					<th><spring:message code="lbl.detailed.key" text="Detailed Key"/></th>
					<th><spring:message code="lbl.rpamount" text="Amount(Rs.)"/></th>
					<th><spring:message code="lbl.action" text="Action"/></th>
				</tr>
			</thead>
			<tbody>
			 <c:choose>
			  <c:when test="${!subLedgerlist.isEmpty()}">
				<tr id="subledgerdetailsrow">
				  <td>				  
				  <select id="glcodeid" data-first-option="false" name="billPayeedetails[0].egBilldetailsId.glcodeid" class="form-control netPayableAccount_Code" required="required">
				  <option value=""><spring:message code="lbl.select" text="Select"/>		
				  </select>
				  </td>
				  <td>
				   <select name="billPayeedetails[0].egBilldetailsId.id" data-first-option="false" id="tempSubLedger[0].subLedgerType" data-idx="0" class="form-control subledgerGlType subledgerGl_code" >
					<option value=""><spring:message code="lbl.select" text="Select"/></option>
					<c:forEach items="${subLedgerTypes}" var="subLedgerType">
						<option value="${subLedgerType.id}">${subLedgerType.name}</option>
					</c:forEach>
				  </select>
				  </td>
				  <td>
				    <input type="hidden" name="billPayeedetails[0].accountDetailTypeId" id="tempSubLedger[0].detailTypeId"  class="form-control table-input hidden-input subLedgerDetailTypeId"/>
					<input type="hidden" name="billPayeedetails[0].accountDetailKeyId" id="tempSubLedger[0].detailkeyId" class="debitDetailKeyId">
					<input type="text" name="billPayeedetails[0].detailTypeName" id="tempSubLedger[0].subLedgerCode" data-idx="0" class="form-control subledger_code subLedgerCodeOT" value="" placeholder="Type any letters of SubLedger name"  />
				  </td>
				  <td>
				  <input type="text" class="form-control subledger_Payto" id="tempSubLedger[0].payTo" name="egBillregistermis.payto" value="" data-idx="0" maxlength="350" />
				  
				  </td>
				  <td>
				  <input type="text" id="billPayeedetails[0].creditAmount" name="billPayeedetails[0].creditAmount" 
				     data-idx="0" class="form-control text-right netPayable_Amount" oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');" data-pattern="decimalvalue"> 
				  </td>
				  <td class="text-center"><span style="cursor:pointer;" onclick="addSubledgerRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding subledge-delete-row" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			
				</tr>
			</c:when>
		  <c:otherwise>
			<tr>
			  	<td>
				  <select data-first-option="false" name="billPayeedetails[0].egBilldetailsId.glcodeid" class="form-control netPayableAccount_Code" required="required">
				  <option value=""><spring:message code="lbl.select" text="Select"/>
				  <c:forEach var="accountDetail" items="${accountDetails}">
				    <c:if test="${not empty accountDetail.creditamount && accountDetail.creditamount ne '0.00'}">
				    <option value="${accountDetail.glcodeid}">${accountDetail.glcode}</option>
				    </c:if>
				    </c:forEach>
				  </select>
				</td>
				  <td>
				   <select name="billPayeedetails[0].egBilldetailsId.id" data-first-option="false" id="tempSubLedger[0].subLedgerType" data-idx="0" class="form-control subledgerGlType subledgerGl_code" >
					<option value=""><spring:message code="lbl.select" text="Select"/></option>
					<c:forEach items="${subLedgerTypes}" var="subLedgerType">
						<option value="${subLedgerType.id}">${subLedgerType.name}</option>
					</c:forEach>
				  </select>
				  </td>
				  <td>
				    <input type="hidden" name="billPayeedetails[0].accountDetailTypeId" id="tempSubLedger[0].detailTypeId"  class="form-control table-input hidden-input subLedgerDetailTypeId"/>
					<input type="hidden" name="billPayeedetails[0].accountDetailKeyId" id="tempSubLedger[0].detailkeyId" class="debitDetailKeyId">
					<input type="text" name="billPayeedetails[0].detailTypeName" id="tempSubLedger[0].subLedgerCode" data-idx="0" class="form-control subledger_code subLedgerCodeOT" value="" placeholder="Type any letters of SubLedger name"  />
				  </td>
				  <td>
				  <input type="text" class="form-control subledger_Payto" id="tempSubLedger[0].payTo" name="egBillregistermis.payto" value="" data-idx="0" maxlength="350" />
				  
				  </td>
				  <td>
				  <input type="text" id="billPayeedetails[0].creditAmount" name="billPayeedetails[0].creditAmount" 
				     data-idx="0" class="form-control text-right netPayable_Amount" oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');" data-pattern="decimalvalue"> 
				  </td>
				  <td class="text-center"><span style="cursor:pointer;" onclick="addSubledgerRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding subledge-delete-row" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			
			</tr>
		  </c:otherwise>
		</c:choose>
		</tbody> 
	 </table>
	</div> --%>
	
 </div>
 
<%--  <div class="panel panel-primary" data-collapsed="0" style=" scrollable:true;">
    <div class="panel-heading">
    
    <br>
    <br>
    </div>
    <div class="show-row form-group">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.refund.comments" text="Refund Comments"/></label>
		<div class="col-sm-8 add-margin">
			<textarea class="form-control"  id="refundnarration" name="refundnarration"></textarea>
		</div>
	</div>
   </div> --%>
 
 
 
     <div class="panel panel-primary" data-collapsed="0" style=" scrollable:true;">
    <div class="panel-heading">
        <div class="panel-title">
                <spring:message code="lbl.view.documents" text="Documents"/>
        </div>
    <c:if test="${egBillregister.documentDetail != null &&  !egBillregister.documentDetail.isEmpty()}">
        <c:forEach items="${egBillregister.documentDetail }" var="documentDetials">
            <a href="/services/EGF/expensebill/downloadBillDoc?egBillRegisterId=${egBillregister.id }&fileStoreId=${documentDetials.fileStore.fileStoreId }">${documentDetials.fileStore.fileName }</a><br />
        </c:forEach>
    </c:if>
    <br>
    <br>
    </div>
    <input type="hidden" value="${fn:length(egBillregister.documentDetail)}" id="documentsSize">
    <c:if test="${mode != 'readOnly' }">
        <div>
            <table width="100%">
                        <tbody>
                        <tr>
                            <td valign="top">
                                <table id="uploadertbl" width="100%"><tbody>
                                <tr id="row1">
                                    <td>
                                        <input type="file" name="file" id="file1" onchange="isValidFile(this.id)" class="padding-10">
                                    </td>
                                </tr>
                                </tbody></table>
                            </td>
                        </tr>
                        <tr>
                            <td align="center">
                                <button id="attachNewFileBtn" type="button" class="btn btn-primary" onclick="addFileInputField()"><spring:message code="lbl.addfile" text="Add File"/></button>
                            </td>
                        </tr>
                        </tbody>
            </table>
        </div>
    </c:if>
</div>

   
   
  <input type="hidden" name="stateType" id="stateType" value="${stateType}"/>	
  <input type="hidden" id="workFlowAction" name="workFlowAction"/>    
  
 <div class="panel panel-primary" data-collapsed="0" >	
   <c:if test="${nextAction !='END'}" > 
	<div class="panel-heading">
	  <div class="panel-title">
		<spring:message code="lbl.approverdetails" text="Approval Details"/>
	  </div>					
	</div>
   </c:if>
	<div class="panel-body">
		<c:if test="${currentState!= 'null' && !'Closed'.equalsIgnoreCase(currentState)}">
		  <input type="hidden" id="currentState" name="currentState" value="${currentState}"/>
		</c:if> 
		<c:if test="${currentState!= 'null' && 'Closed'.equalsIgnoreCase(currentState)}">
		  <input type="hidden"" id="currentState" name="currentState" value=""/>
		</c:if> 
																																																									
	    <input type="hidden" id="currentDesignation" name="currentDesignation" value="${currentDesignation}"/>
		<input type="hidden" id="additionalRule" name="additionalRule" value="${additionalRule}"/>
		<input type="hidden" id="amountRule" name="amountRule" value="${amountRule}"/>
		<input type="hidden" id="workFlowDepartment" name="workFlowDepartment" value="${workFlowDepartment}"/>
		<input type="hidden" id="pendingActions" name="pendingActions" value="${pendingActions}"/>
		<%-- <form:hidden path="" id="approverName" name="approverName" /> --%>
        <div class="row show-row"  id="approverDetailHeading">
		 <c:if test="${nextAction !='END'}" > 
			<div class="show-row form-group" >
			  <label class="col-sm-3 control-label text-right"><spring:message code="lbl.approverdepartment" text="Approver Department"/><span class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
				 <select  data-first-option="false" name="approvalDepartment"
						id="approvalDepartment" Class="form-control"
						cssErrorClass="form-control error" required="required">
					<option value="">
						<spring:message code="lbl.select" text="Select"/>
					</option>
					<c:forEach var="approvalDepartment" items="${approvalDepartmentList}">
				    <option value="${approvalDepartment.code}">${approvalDepartment.name}</option>
                    </c:forEach>     
				 </select>
				</div>
				  <label class="col-sm-2 control-label text-right"><spring:message code="lbl.approverdesignation" text="Approver Designation"/><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
					<input type="hidden" id="approvalDesignationValue" value="${approvalDesignation }" />
					<select  data-first-option="false" name="approvalDesignation"
						id="approvalDesignation" Class="form-control" onfocus="callAlertForDepartment();"
						cssErrorClass="form-control error" required="required">  
					<option value="">
						<spring:message code="lbl.select" text="Select"/>
					</option>
								
					</select>					
				    </div>
			 </div>
			<div class="show-row form-group">
			  <label class="col-sm-3 control-label text-right"><spring:message code="lbl.approver" text="Approver"/><span class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<input type="hidden" id="approvalPositionValue" value="${approvalPosition }" />
					<input type="hidden" id="approverName" name="approverName" />
					<select data-first-option="false" 
						id="approvalPosition" name="approvalPosition" Class="form-control" onfocus="callAlertForDesignation();" 
						cssErrorClass="form-control error" required="required">  
					<option value="">
						<spring:message code="lbl.select" text="Select"/>
					</option>
					</select>		
				</div> 
			</div>
			</c:if>
			  <div class="show-row form-group">
				<label class="col-sm-3 control-label text-right"><spring:message code="lbl.comments" text="Comments"/></label>
				 <div class="col-sm-8 add-margin">
					<textarea class="form-control"  id="approvalComent" name="approvalComent"></textarea>
				 </div>
			  </div>
		</div>
	</div>	

</div>   
	
	<div class="buttonbottom" align="center">
	 <table>
		<tr>
			<td id="actionButtons">
				<c:if test="${mode != 'readOnly'}">
					<c:forEach items="${validActionList}" var="validButtons">
						<input type="submit" id="${validButtons}" class="btn btn-primary btn-wf-primary"  value="${validButtons}"  onclick="return validateFormGlcode(this.value);"/>
					</c:forEach>
				</c:if>
				<input type="button" name="button2" id="button2" value='<spring:message code="lbl.close" text="Close"/>' class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();" />
			</td>
		</tr>
	</table>
   </div>
	
	
 </div>
		
	</div>
</div>
	
</form:form>



<div id="myModal" class="modal fade" role="dialog">
      <div class="modal-dialog modal-lg">

        <!-- Modal content-->
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title">Other Details</h4>
          </div>
          
          
          <div class="modal-body">
          <form id="otherpartyDetails" cssClass="form-horizontal form-groups-bordered">
           <input type="hidden" id="indexRef" name="indexRef" value=""/>
           <div class="show-row form-group" >
			  <label class="col-sm-3 control-label text-right">Name</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="name" id="name" class="form-control"/>
				</div>
			  <label class="col-sm-2 control-label text-right">Mobile No.</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="mobileNumber" id="mobileNumber" class="form-control"/>					
				</div>
		   </div>
		   <div class="show-row form-group" >
			  <label class="col-sm-3 control-label text-right">Email</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="email" id="email" class="form-control"/>
				</div>
			  <label class="col-sm-2 control-label text-right">Correspondence Address</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="correspondenceAddress" id="correspondenceAddress" class="form-control"/>					
				</div>
		   </div>
		   <div class="show-row form-group" >
			  <label class="col-sm-3 control-label text-right">Contact Person</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="contactPerson" id="contactPerson" class="form-control"/>
				</div>
			  <label class="col-sm-2 control-label text-right">EPF Number</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="epfNumber" id="epfNumber" class="form-control"/>					
				</div>
		   </div>
		   <div class="show-row form-group" >
			  <label class="col-sm-3 control-label text-right">Bank Name</label>
				<div class="col-sm-3 add-margin">
				<select data-first-option="false" name="bank" id="bank" class="form-control">
				  <option value="">select</option>
				  <c:forEach var="banks" items="${banks}">
				    <option value="${banks.id}">${banks.name}</option>
                    </c:forEach>
				  </select>
				</div>
			  <label class="col-sm-2 control-label text-right">Account No.</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="bankAccount" id="bankAccount" class="form-control"/>					
				</div>
		   </div>
		   <div class="show-row form-group" >
			  <label class="col-sm-3 control-label text-right">IFSC Code</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="ifscCode" id="ifscCode" class="form-control"/>
				</div>
			  <label class="col-sm-2 control-label text-right">PAN Card No.</label>
				<div class="col-sm-3 add-margin">
				 <input type="text" name="panNumber" id="panNumber" class="form-control"/>					
				</div>
		   </div>
		   </form>
          </div>
          
          <div class="modal-footer">
            <button type="button" id="otherdetails" class="btn btn-default" >Save</button>
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          </div>
          
        </div>

      </div>
    </div>



<script>
	function changeGlCodeBlank(ele){
		var creditDetailsCount = $("#tblcreditdetails > tbody > tr:visible[id='creditdetailsrow']").length;
		$('#glcodeid').empty();
		alert("creditDetailsCount "+creditDetailsCount);

		for(var i=0;i<creditDetailsCount;++i){
		
		if(document.getElementById('billDetails['+i+'].debitamount').value != "" && document.getElementById('billDetails['+i+'].debitamount').value !=null){
	      var k= document.getElementById('billDetails['+i+'].debitamount').value;         
	      var j = document.getElementById('billDetails['+i+'].debitamount').value;
	      alert("k "+k+" j "+j );
	     if(j!=null && j!="" && parseInt(k)<=parseInt(j)){
	        var glcode_= document.getElementById('billDetails['+i+'].glcode').value;
			var glId_ = document.getElementById('billDetails['+i+'].glcodeid').value;
			  var k ='<option value="'+glId_+'">'+glcode_+'</option>';
			  alert("k "+k);
			  $('#glcodeid').append(k); 

	      }else{
	      	document.getElementById('billDetails['+i+'].debitamount').value='';
				bootbox.alert('Please fill valid debit amount for '+document.getElementById('billDetails['+i+'].glcode').value+' code.');	
				
		   }
					 
		}	
			 
	 }
		
	}
	function subledgerChange(subledger){
		$modal = $('#myModal');
	      var subtype=$(".subledgerGlType option:selected").text();
	      //alert(subtype);
	      //if($(this).val() == '23'){
	    	 if(subledger.value=='16'){ 
	    	  var id = $(this).attr("id");
	    	  $('#indexRef').val(id);
	        $modal.modal('show');
	    }
	}

 
	      
	 $(".subledgerGlType").on("change", function () {        
	     $modal = $('#myModal');
	      var subtype=$(".subledgerGlType option:selected").text();
	      //alert(subtype);
	      //if($(this).val() == '23'){
	    	 if(subtype.includes('OtherParty')){ 
	    	  var id = $(this).attr("id");
	    	  $('#indexRef').val(id);
	        $modal.modal('show');
	    }
	 });
	
	$( "#otherdetails" ).click(function() { 

	   // $("#otherpartyDetails").submit(function (event) {
          event.preventDefault();
	        var formData = {
	        	      name: $("#name").val(),
	        	      mobileNumber: $("#mobileNumber").val(),
	        	      email: $("#email").val(),
	        	      contactPerson: $("#contactPerson").val(),
	        	      correspondenceAddress: $("#correspondenceAddress").val(),
	        	      epfNumber: $("#epfNumber").val(),
	        	      bank:  {
	        	          id:$("#bank").val()
	        	      },
	        	      bankAccount: $("#bankAccount").val(),
	        	      ifscCode: $("#ifscCode").val(),
	        	      panNumber: $("#panNumber").val()
	        	    };
            var idref=$('#indexRef').val();
            var res = idref.split(".");
            var id1=res[0]+".subLedgerCode";
            var id2=res[0]+".payTo";
            var id3=res[0]+".detailkeyId";
	        $.ajax({
	            type: "POST",
	            contentType: "application/json",
	            url: "/services/EGF/refund/saveotherParty",
	            data : JSON.stringify(formData),
	            dataType: 'json',
	            timeout: 600000,
	            success : function(data){
	            	//alert(data.code+"  "+data.name+" "+data.id);
	               $('#myModal').modal('hide');
	            },
	            error: function (e) {


	            }
	        });

	    //});

	});
	
	
	
</script>


<script src="<cdn:url value='/resources/app/js/common/commonworkflowexpensebill.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/expensebill/documents-upload.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>

<script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
        
<script
        src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/common/voucherBillHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/expensebill/refundbill.js?rnd=${app_release_no}' context='/services/EGF'/>"></script> 
<script
        src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script
        src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
        




	
