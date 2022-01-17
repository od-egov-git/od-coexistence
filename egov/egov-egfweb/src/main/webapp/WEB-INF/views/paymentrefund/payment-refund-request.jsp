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

<form:form role="form" action="refundCreate" method="post" modelAttribute="egBillregister" id="refundCreate" cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
  <input type="hidden" name="vhid" value="${vhid}"/>
  
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
		 <div class="panel-body">
		   <table border="0" width="100%" cellspacing="0">
			<tr>
			 <td width="10%" class="greybox"><b>Voucher Number :  </b></td>
			 <td width="25%" class="greybox"><a href="javascript:void(0);" onclick="viewVoucher('${vhid}')">${voucherDetails.voucherNumber}</a></td>
			 <td width="10%" class="greybox"><b>Date :</b></td>
			 <td width="25%" class="greybox">
				<fmt:formatDate pattern="dd/MM/yyyy" value="${voucherDetails.voucherDate}" var="voucherDate" />
				 <c:out value="${voucherDate}" />
			 </td>
			</tr>
			<tr>
			 <td width="10%" class="greybox"><b>Fund :  </b></td>
			 <td width="25%" class="greybox">${voucherDetails.fundName}</td>
			 <td width="10%" class="greybox"><b>Scheme :</b></td>
			 <td width="25%" class="greybox">${voucherDetails.scheme}</td>
			</tr>
			<tr>
			 <td width="10%" class="greybox"><b>Sub Scheme :  </b></td>
			 <td width="25%" class="greybox">${voucherDetails.subScheme}</td>
			 <td width="10%" class="greybox"><b>Financing Source :</b></td>
			 <td width="25%" class="greybox">${voucherDetails.financeSource}</td>
			</tr>
			<tr>
			<%-- <!--  <td width="10%" class="greybox"><b>Sub Division :  </b></td> -->
			 <c:if test="${voucherDetails.subdivision !=null && voucherDetails.subdivision !=''}">
			 <td width="10%" class="greybox"><b>Sub Division :  </b></td>
			 <td width="25%" class="greybox">${voucherDetails.subdivision}</td>
			 </c:if>
			 <c:if test="${voucherDetails.subdivision ==null || voucherDetails.subdivision ==''}">
			    
			     <td width="10%" class="greybox"><b>Sub division</b><span class="mandatory"></span></td>
			 <td width="25%" class="greybox">
			 <select name="egBillregistermis.subdivision"
									id="egBillregistermis.subdivision" required="required"
									class="form-control">
										<option value="">-Select-</option>
										<c:forEach items="${subdivisionList}" var="subdivision"
											varStatus="loop">
											<option value="${subdivision.subdivisionCode}">${subdivision.subdivisionName}</option>
										</c:forEach>
								</select>
								</td>
			    
			    </td>
			 </c:if> --%>
			 <td width="10%" class="greybox"><b>Department :  </b></td>
			 <td width="25%" class="greybox">${voucherDetails.deptName}</td>
			</tr>
			<tr>
			 <td width="10%" class="greybox"><b>Function :</b><span class="mandatory"></span></td>
			 <td width="25%" class="greybox">
			 <select name="egBillregistermis.function"
									id="egBillregistermis.function" required="required"
									class="form-control">
										<option value="">-Select-</option>
										<c:forEach items="${cFunctions}" var="function"
											varStatus="loop">
											<option value="${function.id}">${function.name}</option>
										</c:forEach>
										<%-- <options items="${cFunctions}" itemValue="id" itemLabel="name" /> --%>
								</select>
								</td>
			 <td width="10%" class="greybox"><b>Bill Type :  </b><span class="mandatory"></span></td>
			 <td width="25%" class="greybox">
			 <select name="egBillregistermis.egBillSubType" data-first-option="false" id="billSubType" class="form-control" required="required">
				<option value=""><spring:message code="lbl.select" text="Select"/></option>
				<c:forEach items="${billSubTypes}" var="subType"
											varStatus="loop">
											<option value="${subType.id}">${subType.name}</option>
										</c:forEach>
				<%-- <options items="${billSubTypes}" itemValue="id" itemLabel="name" /> --%>
			</select>
			 </td>
			</tr>
			<tr>
			 <td width="10%" class="greybox"><b>Narration :</b></td>
			 <td width="25%" class="greybox">
			 	<textarea name="egBillregistermis.narration" id="narration"  class="form-control" maxlength="1024" >${voucherDetails.narration}</textarea>
			 </td>
			 <td width="10%" class="greybox"><b>BAN Number :  </b></td>
			 <td width="25%" class="greybox">${voucherDetails.banNumber}</td>
			</tr>
		</table>
	 </div>
	</div>
	
	<input type="hidden" name="egBillregistermis.fund" id="egBillregistermis.fund" value="${fundid}"/>
	<input type="hidden" name="egBillregistermis.departmentcode" id="egBillregistermis.departmentcode" value="${departcode}"/>
	<%-- <input type="hidden" name="egBillregistermis.narration" id="egBillregistermis.narration" value="${voucherDetails.narration}"/> --%>
	<input type="hidden" name="egBillregistermis.budgetaryAppnumber" id="egBillregistermis.budgetaryAppnumber" value="${voucherDetails.banNumber}"/>
	<input type="hidden" name="egBillregistermis.fundsource" id="egBillregistermis.fundsource" value="${fundsource}"/>
	<%-- <input type="hidden" name="egBillregistermis.egBillSubType" id="egBillregistermis.egBillSubType" value="${billsubtype}"/>  --%>
	<%-- <c:if test="${voucherDetails.subdivision !=null && voucherDetails.subdivision !=''}">
	<input type="hidden" name="egBillregistermis.subdivision" id="egBillregistermis.subdivision" value="${voucherDetails.subdivision}"/> 
	</c:if> --%>
	
	<%-- <div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message code="lbl.billsubtype" text="Bill Subtype"/>
			<span class="mandatory"></span>
		</label>
		<div class="col-sm-3 add-margin">
			<select name="egBillregistermis.egBillSubType" data-first-option="false" id="billSubType" class="form-control" required="required">
				<option value=""><spring:message code="lbl.select" text="Select"/></option>
				<c:forEach items="${billSubTypes}" var="billSubType">
						<option value="${billSubType.id}"> ${billSubType.name} </option>
				</c:forEach>
			</select>
			<errors path="egBillregistermis.egBillSubType" cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-2 control-label text-right">
			
		</label>
		<div class="col-sm-3 add-margin">
		</div>
	</div> --%>
	
	
	
	
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
					<td><input type="text" name="billDetails[${status.index}].debitamount" id="billDetails[${status.index}].debitamount" onchange="changeGlCode('${accountDetails.size()}','${accountDetail.creditamount}','${status.index}','${accountDetail.previousAmount}')"  oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');"  class="form-control"/></td>
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
				<tr>
				 <td colspan="5">No Record Found.</td>
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
/* 
 $("#refundCreate").submit(function(){
	var rowCount = $("#tblaccountdetails > tbody").children().length-1;
	for (i = 0; i < rowCount; i++) {
		if ($('#billDetails['+i+'].debitamount').val() == null && $('#billDetails['+i+'].debitamount').val() == '') {
			$('#billDetails['+i+'].debitamount').val('0.00')
			}
		if ($('#billDetails['+i+'].creditamount').val() == null && $('#billDetails['+i+'].creditamount').val() == '') {
			$('#billDetails['+i+'].creditamount').val('0.00')
			}
		} 
	var rowCount2 = $("#tblSubledgerAdd > tbody").children().length-1;
	for (i = 0; i < rowCount2; i++) {
		if ($('#billPayeedetails['+i+'].debitAmount').val() == null && $('#billPayeedetails['+i+'].debitAmount').val() == '') {
			$('#billPayeedetails['+i+'].debitAmount').val('0.00')
			}
		if ($('#billPayeedetails['+i+'].creditAmount').val() == null && $('#billPayeedetails['+i+'].creditAmount').val() == '') {
			$('#billPayeedetails['+i+'].creditAmount').val('0.00')
			}
		} 
	
	
	}); */ 
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
	               $('#myModal').on('hidden.bs.modal', function (e) {
	            	   $(this)
	            	     .find("input,textarea,select")
	            	        .val('')
	            	        .end()
	            	     .find("input[type=checkbox], input[type=radio]")
	            	        .prop("checked", "")
	            	        .end();
	            	 })
	               bootbox.alert("Other Party Details saved successfully, kindly enter Other Party Detail Code in Detailed Code Section");
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
        




	
