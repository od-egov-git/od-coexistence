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

<form:form role="form" action="refundCreateBlank" method="post" modelAttribute="egBillregister" id="refundCreate" cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
  
  
  <div class="row">
	<div class="col-md-12">
	 <div>
	  <div class="panel panel-primary" data-collapsed="0">
		<div>
		 <div class="panel-title">
			<div class="subheadnew"></div>
			</div>
		 </div>
	<div>
	<c:if test="${glcodedetailIdmsg !=null && glcodedetailIdmsg !=''}">
	  <p style="color:red;">  ${glcodedetailIdmsg} <p>
	 </c:if>
	</div>
	<div class="panel-body">
			 
			<div class="form-group">
				<label class="col-sm-3 control-label text-right"><b>Date<span class="mandatory"></span> :</b></label>
				<div class="col-sm-3 add-margin">
				<input id="billdate" path="billdate" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
				</div>
			
				<label class="col-sm-3 control-label text-right">Fund <span class="mandatory"></span>: </label>
				<div class="col-sm-3 add-margin">
			 <select name="egBillregistermis.fund" id="egBillregistermis.fund" required="required" class="form-control fundType">
					<option value="">-Select-</option>
					<c:forEach items="${fundList}" var="fund" varStatus="loop">
						<option value="${fund.id}">${fund.name}</option>
					</c:forEach>
			</select>
				</div>				
			</div>
			 
			<div class="form-group">
				<label class="col-sm-3 control-label text-right">Fund Source: </label>
				<div class="col-sm-3 add-margin">
				<select name="egBillregistermis.fundsource" id="egBillregistermis.fundsource"  class="form-control">
					<option value="">-Select-</option>
					<c:forEach items="${fundsourceList}" var="fundSource" varStatus="loop">
						<option value="${fundSource.id}">${fundSource.name}</option>
					</c:forEach>
				</select>
				</div>	
				
				<label class="col-sm-3 control-label text-right">scheme: </label>
				<div class="col-sm-3 add-margin">
				<select name="egBillregistermis.schemeId" id="egBillregistermis.schemeId"  class="form-control schemeType">
					<option value="">-Select-</option>
					<c:forEach items="${schemeList}" var="scheme" varStatus="loop">
						<option value="${scheme.id}">${scheme.name}</option>
					</c:forEach>
				</select>
				</div>			
			</div>
			
			<div class="form-group">
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
			  <select name="egBillregistermis.departmentcode" id="egBillregistermis.departmentcode" required="required" class="form-control">
					<option value="">-Select-</option>
					<c:forEach items="${departments}" var="dept" varStatus="loop">
						<option value="${dept.code}">${dept.name}</option>
					</c:forEach>
			</select>
				</div>
			 
				<%-- <label class="col-sm-3 control-label text-right"><b>Sub division <span class="mandatory"></span> :</b></label>
				<div class="col-sm-3 add-margin">
			    <select name="egBillregistermis.subdivision" id="egBillregistermis.subdivision" required="required" class="form-control">
					<option value="">-Select-</option>
					<c:forEach items="${subdivisionList}" var="subdivision"
						varStatus="loop">
						<option value="${subdivision.subdivisionCode}">${subdivision.subdivisionName}</option>
					</c:forEach>
			</select> --%>
					</div>				
				</div>			
			    
				<div class="form-group">
				<label class="col-sm-3 control-label text-right"><b>Function<span class="mandatory"></span> :</b></label>
				<div class="col-sm-3 add-margin">
				<select name="egBillregistermis.function"	id="egBillregistermis.function" required="required"	class="form-control">
					<option value="">-Select-</option>
					<c:forEach items="${cFunctions}" var="function"	varStatus="loop">
						<option value="${function.id}">${function.name}</option>
					</c:forEach>
			</select>
				</div>
			
				<label class="col-sm-3 control-label text-right">BiLL Type <span class="mandatory"></span> :  </b></label>
				<div class="col-sm-3 add-margin">
				<%--  <select name="egBillregistermis.egBillSubType" data-first-option="false" id="billSubType" class="form-control" required="required">
				<option value=""><spring:message code="lbl.select" text="Select"/></option>
					<c:forEach items="${billSubTypes}" var="subType" varStatus="loop">
							<option value="${subType.id}">${subType.value}</option>
										</c:forEach>
			   </select> --%>
			   
			    <select name="egBillregistermis.subType" data-first-option="false" id="billSubType" class="form-control" required="required">
				<option value=""><spring:message code="lbl.select" text="Select"/></option>
				<c:forEach items="${billSubTypes}" var="subType"
											varStatus="loop">
											<option value="${subType.id}">${subType.name}</option>
										</c:forEach>
				<%-- <options items="${billSubTypes}" itemValue="id" itemLabel="name" /> --%>
			</select>
				</div>				
			</div>
			
			<div class="form-group">
				<label class="col-sm-3 control-label text-right"><b>Narration :</label>
				<div class="col-sm-3 add-margin">
				    <textarea name="egBillregistermis.narration" id="narration"  class="form-control" maxlength="1024" ></textarea>
				</div>					
			</div>					
	 </div>
	</div>
	
	<%-- <input type="hidden" name="egBillregistermis.fund" id="egBillregistermis.fund" value="${fundid}"/>
	<input type="hidden" name="egBillregistermis.departmentcode" id="egBillregistermis.departmentcode" value="${departcode}"/>
	<input type="hidden" name="egBillregistermis.narration" id="egBillregistermis.narration" value="${voucherDetails.narration}"/>
	<input type="hidden" name="egBillregistermis.budgetaryAppnumber" id="egBillregistermis.budgetaryAppnumber" value="${voucherDetails.banNumber}"/>
	<input type="hidden" name="egBillregistermis.fundsource" id="egBillregistermis.fundsource" value="${fundsource}"/>
	 --%>
	<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.accountdetails" text="Account Details"/>
		</div>
	</div>
	
	<div style="padding: 0 15px;">
	 <div class="panel-body">
	<table class="table table-bordered" id="tblcreditdetails">
		<thead>
			<tr>
				<%-- <th><spring:message code="lbl.account.code" text="Account Code"/></th>
				<th><spring:message code="lbl.account.head" text="Account Head"/></th>				
				<th><spring:message code="lbl.credit.amount" text="Credit Amount"/></th>
				<th><spring:message code="lbl.debit.amount" text="Debit Amount"/></th>
				<th><spring:message code="lbl.previous.debit.amount" text="Previous Debit Amount"/></th>
				<th><spring:message code="lbl.action" text="Action"/></th> --%> 
				 
					<th><spring:message code="lbl.account.code" text="Account Code"/></th>
					<th><spring:message code="lbl.account.head" text="Account Head"/></th>
					<%-- <th><spring:message code="lbl.debit.amount" text="Debit Amount"/></th> --%>
					<%-- <th><spring:message code="lbl.credit.amount" text="Credit Amount"/></th> --%>										
					<%-- <th><spring:message code="lbl.previous.debit.amount" text="privious Debit Amount"/></th> --%>
					<th><spring:message code="lbl.refund.debit.amount" text="Refund Debit Amount"/></th>	
					<th><spring:message code="lbl.action" text="Action"/></th>				
			</tr>
		</thead>
		<tbody>
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
				<!-- <td>0</td> -->
				<td><input type="text" name="billDetails[0].debitamount" id="billDetails[0].debitamount" onchange="changeGlCodeBlank(this)"  oninput="this.value=this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');"
			   class="form-control"/></td> 
								
				<td class="text-center"><span style="cursor:pointer;" onclick="addCreditDetailsRow();" tabindex="0" id="tempCreditDetails[0].addButton" data-toggle="tooltip" title="" data-original-title="press ENTER to Add!" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding credit-delete-row" onclick="deleteCreditDetailsRow(this);"><i class="fa fa-trash" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			</tr>
		</tbody>
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
				  <input type="text" id="tempSubLedger[0].netPayable_Amount" name="billPayeedetails[0].creditAmount" 
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
<%--  
 <div class="panel panel-primary" data-collapsed="0" style=" scrollable:true;">
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
						<input type="button" id="${validButtons}" class="btn btn-primary btn-wf-primary"  value="${validButtons}" onclick="return validateFormGlcode(this.value);"/>
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
				  <c:forEach var="banks" items="${bankList}">
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
$(".fundType").on("change", function () {  
	var fund=$(".fundType option:selected").val();
	loadSchemeNew(fund);
});
function loadSchemeNew(fund){
	if (!fund) {
		$('.schemeType').empty();
		$('.schemeType').append($('<option>').text('Select from below').attr('value', ''));
		$('.subSchemeType').empty();
		$('.subSchemeType').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getschemesbyfundid",
			data : {
				fundId : fund
			},
			async : true
		}).done(
				function(response) {
					$('.schemeType').empty();
					$('.schemeType').append($("<option value=''>Select from below</option>"));
					$.each(response, function(index, value) {
						var selected="";
						if($schemeId && $schemeId==value.id)
						{
								selected="selected";
						}
						$('.schemeType').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
					});
				});

	}
}
$(".schemeType").on("change", function () {  
	var scheme=$(".schemeType option:selected").val();
	loadSubSchemeNew(scheme);
});
function loadSubSchemeNew(scheme){
	if (!scheme) {
		$('#subScheme').empty();
		$('#subScheme').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getsubschemesbyschemeid",
			data : {
				schemeId : scheme
			},
			async : true
		}).done(
				function(response) {
					$('#subScheme').empty();
					$('#subScheme').append($("<option value=''>Select from below</option>"));
					$.each(response, function(index, value) {
						var selected="";
						if($subSchemeId && $subSchemeId==value.id)
						{
								selected="selected";
						}
						$('.subSchemeType').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
					});
				});
		
	}
}
	 $(".subledgerGlType").on("change", function () {        
	      $modal = $('#myModal');
	      var subtype=$(".subledgerGlType option:selected").text();
	      //if($(this).val() == '23'){
	    	 if(subtype=='OtherParty'){  
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
<%-- <script
        src="<cdn:url value='/resources/app/js/common/voucherBillHelper_refund.js?rnd=${app_release_no}' context='/services/EGF'/>"></script> --%>        
<script
        src="<cdn:url value='/resources/app/js/expensebill/refundbill_blankvoucher.js?rnd=${app_release_no}' context='/services/EGF'/>"></script> 
<script
        src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script
        src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
      <!--   <script src="/services/EGF/resources/app/js/expensebill/expensebill.js?rnd=3.0.0-COE-SNAPSHOT_2021-07-07 14:20"></script> -->
        




	
