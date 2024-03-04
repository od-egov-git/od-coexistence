<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>
<html>
<head>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/calendar.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/dateValidation.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"> </script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/autocomplete-debug.js"></script>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252" />
<title>Journal voucher Modify</title>
</head>
<body
	onload="loadDropDownCodes();loadDropDownCodesFunction();onLoadTask();documentdep();">
	<s:form theme="simple" name="jvmodifyform"
		enctype="multipart/form-data">
		<s:push value="model">
			<div
				style="position: absolute; left: 25%; top: 70%; padding: 2px; z-index: 20001; height: auto; width: 500px; display: none;">
				<div class="loading-indicator"
					style="background: white; color: #444; font: bold 13px tohoma, arial, helvetica; padding: 10px; margin: 0; height: auto;">
					<img src="/services/egi/resources/erp2/images/loading.gif"
						width="32" height="32"
						style="margin-right: 8px; vertical-align: top;" /> Loading...
				</div>
			</div>
			<jsp:include page="../budget/budgetHeader.jsp">
				<jsp:param name="heading" value="Journal voucher -Modify" />
			</jsp:include>
			<span class="mandatory1"> <font
				style='color: red; font-weight: bold'> <s:actionerror /> <s:fielderror />
					<s:actionmessage />
			</font>
			</span>
			<div class="formmainbox">
				<div class="subheadnew">Journal Voucher</div>
				<div id="listid" style="display: block">
					<br />
					<div align="center">
						<font style='color: red; font-weight: bold'>
							<p class="error-block" id="lblError"></p>
						</font> <input type="hidden" name="selectedDate" id="selectedDate" />
						<table border="0" width="100%">
							<tr>
							</tr>
							<tr>
								<td class="greybox">&nbsp;</td>

								<td class="greybox"><s:text name="voucher.date" /><span
									class="mandatory1"> *</span></td>
								<!--  <td class="greybox">
                                    <s:date name="voucherDate" var="voucherDateId" format="dd/MM/yyyy" />
                                    <s:textfield name="voucherDate" id="voucherDate" value="%{voucherDateId}" maxlength="10" onkeyup="DateFormat(this,this.value,event,false,'3')" />
                                    <a href="javascript:show_calendar('jvmodifyform.voucherDate');" style="text-decoration: none">&nbsp;<img tabIndex=-1 src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)
                                </td>-->
								<td class="bluebox"><s:date name="voucherDate"
										var="voucherDateId" format="dd/MM/yyyy" /> <s:textfield
										readonly="true" id="voucherDate" value="%{voucherDateId}"
										name="voucherDate" data-date-end-date="0d"
										placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
										data-inputmask="'mask': 'd/m/y'" /></td>

							</tr>
							<jsp:include page="voucherSubType.jsp" />
							<jsp:include page="vouchertrans-filter.jsp" />
							<jsp:include page="loadYIDataTable.jsp" />
							<tr>
								<td class="greybox">&nbsp;</td>
								<td class="greybox"><s:text name="voucher.narration" /></td>
								<td class="greybox" colspan="3"><s:textarea id="narration"
										name="description" style="width:580px"
										onblur="checkVoucherNarrationLen(this)" /></td>
							</tr>
							<tr>
								<td style="width: 5%"></td>
								<td class="greybox"><s:text name="backdated.entry" /><span
									class="mandatory1">*</span></td>
								<td class="greybox" colspan="1"><s:select
										name="backdateentry" headerKey="-1" headerValue="Select"
										value="%{backdateentry}" list="#{'Y':'Yes' ,'N':'No'}"
										id="backlogEntry" /></td>
								<td class="bluebox">File No</td>
								<td class="bluebox"><s:textfield name="fileno" id="fileno" /></td>
							</tr>
						</table>
					</div>
					<br />
					<div id="labelAD" align="center">
						<table width="80%" border=0 id="labelid">
							<th>Account Details</th>
						</table>
					</div>
					<div class="yui-skin-sam" align="center">
						<div id="billDetailTable"></div>
					</div>
					<script type="text/javascript">
                        makeVoucherDetailTable();
                        document.getElementById('billDetailTable').getElementsByTagName('table')[0].width="90%";
                    </script>
					<div id="codescontainer"></div>
					<br />
					<div id="labelSL" align="center">
						<table width="80%" border=0 id="labelid">
							<th>Sub-Ledger Details</th>
						</table>
					</div>
					<div class="yui-skin-sam" align="center">
						<div id="subLedgerTable"></div>
					</div>
					<script type="text/javascript">
                        makeSubLedgerTable();
                        document.getElementById('subLedgerTable').getElementsByTagName('table')[0].width="90%";
                    </script>
					<s:if test="%{voucherHeader.documentMode=='ADDVIEW'}">
						<div align="center">
							<jsp:include page="common-documentsView.jsp" />
							<jsp:include page="common-documentsUpload.jsp" />
						</div>
					</s:if>
					<br />
					<div class="subheadsmallnew"></div>
					<div id="wfHistoryDiv">
						<%--   	<c:import url="/WEB-INF/jsp/workflow/workflowHistory.jsp" context="/egi">
                            <c:param name="stateId" value="${voucherHeader.state.id}"></c:param>
                        </c:import> --%>
					</div>
					<s:if test='%{! wfitemstate.equalsIgnoreCase("END")}'>
						<%@include file="voucherWorkflow.jsp"%>
					</s:if>
					<div align="center">
						<table border="0" width="100%">
							<tr>
								<td class="bluebox">&nbsp;</td>
								<td class="bluebox">Comments</td>
								<td class="bluebox"><s:textarea name="comments"
										id="comments" cols="150" rows="3" onblur="checkLength(this)" /></td>
								<td><s:hidden id="methodName" name="methodName"
										value="save" /></td>
								<s:hidden id="vhid" name="vhid" value="%{voucherHeader.id}" />
								<s:hidden name="actionName" id="actionName" />
							</tr>
							<br />
						</table>
					</div>
					<br />
				</div>
			</div>
			<div id="codescontainer"></div>
			<%@ include file='../workflow/commonWorkflowMatrix.jsp'%>
			<%@ include file='../workflow/commonWorkflowMatrix-button.jsp'%>

			<s:hidden id="cgn" name="cgn"></s:hidden>
			<s:hidden name="saveMode" id="saveMode" />
			<s:hidden name="actionName" id="actionName" />
			<input type="hidden" id="worksVoucherRestrictedDate"
				name="worksVoucherRestrictedDate"
				value="${worksVoucherRestrictedDate}" />
			<div id="codescontainer"></div>
			<s:hidden name="actionName" id="actionName" />
			<input type="hidden" id="voucherTypeBean.fundnew"
				name="voucherTypeBean.fundnew" value="${voucherTypeBean.fundnew}" />
			<input type="hidden" id="voucherTypeBean.departmentnew"
				name="voucherTypeBean.departmentnew"
				value="${voucherTypeBean.departmentnew}" />
			<input type="hidden" id="voucherTypeBean.functionnew"
				name="voucherTypeBean.functionnew"
				value="${voucherTypeBean.functionnew}" />
			<input type="hidden" id="voucherTypeBean.voucherName"
				name="voucherTypeBean.voucherName"
				value="${voucherTypeBean.voucherName}" />
			<input type="hidden" id="voucherTypeBean.voucherType"
				name="voucherTypeBean.voucherType" value="Journal Voucher" />
			<input type="hidden" id="voucherTypeBean.voucherNumType"
				name="voucherTypeBean.voucherNumType"
				value="${voucherTypeBean.voucherNumType}" />
			<input type="hidden" id="voucherTypeBean.cgnType"
				name="voucherTypeBean.cgnType" value="JVG" />
			<input type="hidden" id="buttonValue" name="buttonValue" />


		</s:push>
	</s:form>
	<script type="text/javascript">
        function validateApproverUser(name,value){
            document.getElementById("actionName").value= name;
            <s:if test='%{! wfitemstate.equalsIgnoreCase("END")}'>
                if(!validateUser(name,value)){
                    return false;
                }
            </s:if> 
            return true;
        }
        function onSubmit() {
        	console.log("inside submit...");
            if(checkdate()){
                if(validateJV()) {
                	var backlog=document.getElementById('backlogEntry');
                    document.forms[0].action='${pageContext.request.contextPath}/voucher/journalVoucher-create.action?backlogEntry='+backlog.value;
                    document.forms[0].submit();
                    return true;
                } else {
                    return false;
                }
            } else {
                bootbox.alert("Please select back dated entry option correctly");
                return false;
            }
        }
        //jayanta for save as draft
        function onSubmitDraft() {
            document.forms[0].action='${pageContext.request.contextPath}/voucher/journalVoucher-create.action?backlogEntry='+backlog.value;
            document.forms[0].submit();
        }
        function deleteDocument(objid,id) {
            document.forms[0].action='/services/EGF/voucher/journalVoucherModify-deleteVoucherDoc.action?voucherHeaderId='+objid+'&fileid='+id;
            document.forms[0].submit();
        }
        function inAccountCodeArray(accountCode, accountCodeArray) {
            var length = accountCodeArray.length;
            for(var i = 0; i < length; i++) {
                if(accountCodeArray[i] == accountCode) {
                    return false;
                    break;
                }
            }
            return true;
        }
        function validateAccDtls() {
        	console.log("inside validate acc details...");
            var y =document.getElementById('billDetailTable').getElementsByTagName('tr');
            var x =document.getElementById('subLedgerTable').getElementsByTagName('tr');
            var totalDebitAmt= 0;
            var totalCreditAmt = 0;
            var accountCodeArray = new Array();     
            var rowIndexLength = y.length - 2;
            var rowIndexSubLedgLength = x.length - 2;
            for (i = 0; i < rowIndexLength -1 ; i++) {
                var debitAmt = document.getElementById('billDetailslist['+i+'].debitAmountDetail').value;
                var creditAmt = document.getElementById('billDetailslist['+i+'].creditAmountDetail').value;
                var accountCode = document.getElementById('billDetailslist['+i+'].glcodeDetail').value;
                if(debitAmt == '') {
                    debitAmt = 0;
                }
                if(creditAmt == '') {
                    creditAmt = 0;
                }
                debitAmt= parseFloat(debitAmt);
                creditAmt= parseFloat(creditAmt);
                if(accountCode == '') {                  
                    bootbox.alert("Account code  is missing for credit or debit supplied field in account grid : "+(i+1));
                    return false;
                } else {
                    if(!inAccountCodeArray(accountCode,accountCodeArray)) {
                        bootbox.alert("Function is missing for the repeated account code,check account code : "+accountCode);
                        return false;
                    } else {
                        accountCodeArray.push(accountCode);
                    }
                    if(debitAmt > 0 && creditAmt >0) {
                        bootbox.alert("One account can have only credit or debit for the account code :"+accountCode);
                        return false;
                    }
                    if(debitAmt == 0 && creditAmt == 0) {
                        bootbox.alert("Enter debit/credit amount for the account code : "+accountCode);
                        return false;
                    }
                    if(debitAmt > 0 && creditAmt == 0) {
                        totalDebitAmt = totalDebitAmt + debitAmt;                     
                    }
                    if(creditAmt > 0 && debitAmt == 0) {
                        totalCreditAmt = totalCreditAmt + creditAmt;         
                    }
                }
            }
            if(totalDebitAmt != totalCreditAmt) {
                bootbox.alert("Total Credit and Total Debit amount must be same");
                return false;
            }
            return true;
        }
        function printJV() {        
            var voucherHeaderId = '<s:property value="voucherHeader.id"/>';
            window.location="${pageContext.request.contextPath}/voucher/journalVoucherPrint-print.action?id="+voucherHeaderId;      
        }
        function validateJV() {
            document.getElementById('lblError').innerHTML ="";
            var cDate = new Date();
            var currDate = cDate.getDate()+"/"+(parseInt(cDate.getMonth())+1)+"/"+cDate.getYear();
            var vhDate=document.getElementById('voucherDate').value;
            var VhType= document.getElementById('vType').value;
            if(vhDate == '' )  {
                bootbox.alert("Please enter a voucher date ");
                document.getElementById('voucherDate').focus();
                return false;
            }
            var varVType = document.getElementById('vType').value;
            if( varVType != 'JVGeneral' && varVType != '-1' )  {
                if(document.getElementById('voucherTypeBean.partyName').value == '' ) {
                    bootbox.alert("Please enter a Party Name ");
                    document.getElementById('voucherTypeBean.partyName').focus();
                    return false;
                }
            }
            if(!validateMIS()) return false;
            if(!validateAccDtls()) {
                return false;
            }           
            return true;
        }
        function onLoadTask() {
        	
        	var VTypeFromBean = '<s:property value="voucherTypeBean.voucherSubType"/>';
        	if(VTypeFromBean == "") 
        		VTypeFromBean = '-1';
        	document.getElementById('vType').value = VTypeFromBean;
        	if('<s:property value="voucherTypeBean.voucherSubType"/>' == 'JVGeneral' || '<s:property value="voucherTypeBean.voucherSubType"/>'== ""){
        		document.getElementById('voucherTypeBean.partyBillNum').readOnly=true;
        		document.getElementById('voucherTypeBean.partyName').readOnly=true;
        	//document.getElementById('partyBillDate').readOnly=true;
        		document.getElementById('voucherTypeBean.billNum').readOnly=true;
        		// document.getElementById('billDate').readOnly=true;
        	}
        	//document.getElementById('vouchermis.function').style.display="none";
        	//document.getElementById('functionnametext').style.display="none";


        	var message = '<s:property value="message"/>';//commented 
        	if(message != null && message != '')
        		showMessage(message);
        	<s:if test="%{voucherTypeBean.voucherNumType == null}">
        		document.getElementById('voucherTypeBean.voucherNumType').value ="Journal";
        	</s:if>
        	<s:if test="%{voucherTypeBean.voucherName == null}">
        		document.getElementById('voucherTypeBean.voucherName').value ="JVGeneral";
        	</s:if>
        	<s:if test="%{voucherTypeBean.voucherSubType == null}">
        		document.getElementById('voucherTypeBean.voucherSubType').value = "JVGeneral";
        	</s:if>
        	if(message == null || message == '')
        		populateslDropDown(); // to load the subledger detils when page loads, required when validation fails.
        	if(document.getElementById('approverDepartment'))
        		document.getElementById('approverDepartment').value = "-1";
        	
            document.getElementById('vType').value='<s:property value="voucherTypeBean.voucherSubType"/>';
            if('<s:property value="voucherTypeBean.voucherSubType"/>' == 'JVGeneral' ){
                document.getElementById('voucherTypeBean.partyBillNum').readOnly=true;
                document.getElementById('voucherTypeBean.partyName').readOnly=true;
            }
            var varVType = document.getElementById('vType').value;
            if(varVType == 'JVGeneral' || varVType == '-1') {
                document.getElementById('partyNameDivId').style.display='none';
            } else {
                document.getElementById('partyNameDivId').style.display='inline';
            }
            document.getElementById('vType').disabled=false; 
            var target = '<s:property value="target"/>';
            var saveMode='<s:property value="saveMode"/>';
            var voucherNumber = '<s:property value='%{voucherHeader.voucherNumber}'/>' ;
            var cgn = '<s:property value='%{cgn}'/>' ;
            if(target == 'success' ){
                if(saveMode == 'saveclose'){
                    bootbox.alert("Voucher saved sucessfully with voucher number =  "+voucherNumber );
                    window.close();
                } else if(saveMode == 'saveview'){
                    bootbox.alert("Voucher saved sucessfully with voucher number =  "+voucherNumber);
                    window.open('preApprovedVoucher-loadvoucherview.action?vhid=<s:property value='%{voucherHeader.id}'/>','Search','resizable=yes,scrollbars=yes,left=300,top=40,width=900, height=700');
                } else if(saveMode == 'saveprint'){
                    bootbox.alert("Voucher saved sucessfully with voucher number =  "+voucherNumber);
                    window.open('journalVoucherPrint-print.action?id=<s:property value='%{voucherHeader.id}'/>','','resizable=yes,scrollbars=yes,left=300,top=40,width=900, height=700');
                }
            }
            <s:if test="%{shouldShowHeaderField('vouchernumber')}">
                var tempVoucherNumber='<s:property value="voucherHeader.voucherNumber"/>';
                var prefixLength='<s:property value="voucherNumberPrefixLength"/>';
                document.getElementById('voucherNumberPrefix').value=tempVoucherNumber.substring(0,prefixLength);
                document.getElementById('voucherNumber').value=tempVoucherNumber.substring(prefixLength,tempVoucherNumber.length);
            </s:if>
            populateslDropDown(); // to load the subledger detils when page loads, required when validation fails.
            if(document.getElementById('approverDepartment'))
                document.getElementById('approverDepartment').value = "-1";
        }
        function loadBank(fund) {
        }
        function checkdate() {
        	console.log("inside checkdate...");
            var backlog=document.getElementById('backlogEntry').value;
            var date2=document.getElementById('voucherDate').value;
            var parts = date2.split("/");
            var date = new Date(parts[1] + "/" + parts[0] + "/" + parts[2]);
            var curdate = new Date();
            if(backlog!='Y'){
                if(date.setHours(0,0,0,0) == curdate.setHours(0,0,0,0)) {
                    if(backlog == 'N'){
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            } else {
                if(date.setHours(0,0,0,0) < curdate.setHours(0,0,0,0)){
                    console.log(":::: backdated");
                    return true;
                } else {
                    console.log(":::: not backdated");
                    return false;
                }
            }
        }
    </script>
</body>
</html>
