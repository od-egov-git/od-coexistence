<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<link href="/services/EGF/resources/css/budget.css?rnd=${app_release_no}" rel="stylesheet"
	type="text/css" />
<style type="text/css">
@media print {
	#non-printable {
		display: none;
	}
}
</style>

<style>
th.bluebgheadtd {
	padding: 0px;
	margin: 0px;
}

.extracontent {
	font-weight: bold;
	font-size: xx-small;
	color: #CC0000;
}
</style>

<script>

var callback = {
		success: function(o){
			document.getElementById('result').innerHTML=o.responseText;
			undoLoadingMask();
			},
			failure: function(o) {
				undoLoadingMask();
		    }
		}
		
function disableAsOnDate(){
	if(document.getElementById('period').value != "Date"){
		//document.getElementById('asOndate').disabled = true;
		document.getElementById('fromDate').disabled = true;
		document.getElementById('toDate').disabled = true;
		document.getElementById('financialYear').disabled = false;
		document.getElementById('fromDate').value= '';
		document.getElementById('toDate').value= '';
		document.getElementById("dateinputs").style.display = 'none';
	}else{
		document.getElementById('financialYear').disabled = true;
		//document.getElementById('asOndate').disabled = false;
		document.getElementById('fromDate').disabled = false;
		document.getElementById('toDate').disabled = false;
		document.getElementById("financialYear").selectedIndex = 0;
		document.getElementById("dateinputs").style.display = '';
	}
}

function validateMandatoryFields(){

	if(document.getElementById('period').value=="Select")
	{
		bootbox.alert('<s:text name="msg.please.select.period"/>');
		return false;
	}
		
	if(document.getElementById('period').value!="Date"){
		if(document.getElementById('financialYear').value==0){
			bootbox.alert('<s:text name="msg.please.select.financial.year"/>');
			return false;
		}
	}
	
	//if(document.getElementById('period').value=="Date" && document.getElementById('asOndate').value==""){
		//bootbox.alert('<s:text name="msg.please.enter.as.onDate"/>');
		//return false;
	//}
	if( document.getElementById('period').value=="Date" && document.getElementById('fromDate').value==""){
		bootbox.alert('<s:text name="msg.please.select.from.date"/>');
		return false;
	}
	if( document.getElementById('period').value=="Date" && document.getElementById('toDate').value==""){
		bootbox.alert('<s:text name="msg.please.select.toDate"/>');
		return false;
	}
	return true;
} 

///report/receiptsAndPaymentsAccountReport-ajaxPrintReceiptsAndPaymentsAccountReport
function getData(){
	if(validateMandatoryFields()){
		doLoadingMask();
		//var url = '/services/EGF/report/incomeExpenditureReport-ajaxPrintIncomeExpenditureReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.currency='+document.getElementById('currency').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.department.code='+document.getElementById('department').value+'&model.function.id='+document.getElementById('function').value+'&model.asOndate='+document.getElementById('asOndate').value+'&model.fund.id='+document.getElementById('fund').value;
		var url = '/services/EGF/report/receiptsAndPaymentsAccountReport-ajaxPrintReceiptsAndPaymentsAccountReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.fromDate='+document.getElementById('fromDate').value+'&model.toDate='+document.getElementById('toDate').value+'&model.fund.id='+document.getElementById('fund').value;;
		YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
		
		//document.receiptsAndPaymentsAccountReport.action='/services/EGF/report/receiptsAndPaymentsAccountReport-ajaxPrintReceiptsAndPaymentsAccountReport.action';
		//document.receiptsAndPaymentsAccountReport.submit();
		return true;
    }
    
	return false;
}

</script>

<div id="non-printable">
	<s:form name="receiptsAndPaymentsAccountReport" action="receiptsAndPaymentsAccountReport"
		theme="simple">
		<div class="formmainbox">
			<div class="formheading"></div>
			<div class="subheadnew"><s:text name="Receipts And Payments Account"/> </div>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="10%" class="bluebox">&nbsp;</td>
					<td width="15%" class="bluebox"><s:text name="report.period" />:<span
						class="mandatory1">*</span></td>
					<td width="22%" class="bluebox"><s:select name="period"
							id="period"
							list="#{'Select':'---Choose---','Date':'Date','Yearly':'Yearly'}"
							onclick="disableAsOnDate()" value="%{model.period}" /></td>
					<td class="bluebox" width="12%"><s:text
							name="report.financialYear" />:<span class="mandatory1">*</span></td>
					<td width="41%" class="bluebox"><s:select name="financialYear"
							id="financialYear" list="dropdownData.financialYearList"
							listKey="id" listValue="finYearRange" headerKey="0"
							headerValue="%{getText('lbl.choose.options')}" value="%{model.financialYear.id}" />
					</td>
				</tr>
				
				<tr id ="dateinputs">
					<td class="greybox">&nbsp;</td>
					<td class="greybox"><s:text name="report.fromDate" />:</td>
					<td class="greybox"><s:textfield name="fromDate" id="fromDate"
							cssStyle="width:100px" /><a
						href="javascript:show_calendar('receiptsAndPaymentsAccountReport.fromDate');"
						style="text-decoration: none">&nbsp;<img
							src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)
					</td>
					<td class="greybox"><s:text name="report.toDate" />:</td>
					<td class="greybox"><s:textfield name="toDate" id="toDate"
							cssStyle="width:100px" /><a
						href="javascript:show_calendar('receiptsAndPaymentsAccountReport.toDate');"
						style="text-decoration: none">&nbsp;<img
							src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)
					</td>
					<%--<td class="greybox"><s:text name="report.asOnDate" />:</td>
					<td class="greybox"><s:textfield name="asOndate" id="asOndate"
							cssStyle="width:100px" /><a
						href="javascript:show_calendar('incomeExpenditureReport.asOndate');"
						style="text-decoration: none">&nbsp;<img
							src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)
					</td>
					<td class="greybox"><s:text name="report.rupees" />:<span
						class="mandatory1">*</span></td>
					<td class="greybox"><s:select name="currency" id="currency"
							list="#{'Rupees':'Rupees','Thousands':'Thousands','Lakhs':'Lakhs'}"
							value="%{model.currency}" /></td>--%>
				</tr>
				
				<tr>
					<td class="bluebox">&nbsp;</td>
					<td class="greybox"><s:text name="report.rupees" />:<span
						class="mandatory1">*</span></td>
					<td class="greybox"><s:select name="currency" id="currency"
							list="#{'Rupees':'Rupees','Thousands':'Thousands','Lakhs':'Lakhs'}"
							value="%{model.currency}" /></td>
					<td class="bluebox"><s:text name="report.department" />:</td>
					<td class="bluebox"><s:select name="department"
							id="department" list="dropdownData.departmentList" listKey="code"
							listValue="name" headerKey="" headerValue="%{getText('lbl.choose.options')}"
							value="model.department.code" /></td>
					
					<%-- <td class="bluebox"><s:text name="report.fund" />:</td>
					<td class="bluebox"><s:select name="fund" id="fund"
							list="dropdownData.fundDropDownList" listKey="id"
							listValue="name" headerKey="0" headerValue="%{getText('lbl.choose.options')}"
							value="model.fund.id" /></td>--%>
				</tr>
				<tr>
					<td class="greybox">&nbsp;</td>
					<td class="bluebox"><s:text name="report.fund" />:</td>
					<td class="bluebox"><s:select name="fund" id="fund"
							list="dropdownData.fundDropDownList" listKey="id"
							listValue="name" headerKey="0" headerValue="%{getText('lbl.choose.options')}"
							value="model.fund.id" /></td>
					<td class="greybox"><s:text name="report.function" />:</td>
					<td class="greybox"><s:select name="function" id="function"
							list="dropdownData.functionList" listKey="id" listValue="name"
							headerKey="0" headerValue="%{getText('lbl.choose.options')}"
							value="model.function.id" /></td>
					<%--<td class="greybox"><s:text name="report.functionary" />:</td>
					<td class="greybox"><s:select name="functionary"
							id="functionary" list="dropdownData.functionaryList" listKey="id"
							listValue="name" headerKey="0" headerValue="%{getText('lbl.choose.options')}"
							value="model.functionary.id" /></td> --%>
				</tr>
				<%-- tr>
					<td class="bluebox">&nbsp;</td>
					<td class="bluebox"><s:text name="report.field" />:</td>
					<td class="bluebox"><s:select name="field" id="field"
							list="dropdownData.fieldList" listKey="id" listValue="name"
							headerKey="0" headerValue="%{getText('lbl.choose.options')}" value="model.field.id" />
					</td>
				</tr> --%>
				<tr>
					<td></td>
				</tr>
			</table>
			<div align="left" class="mandatory1">
				*
				<s:text name="report.mandatory.fields" />
			</div>
			<div class="buttonbottom" style="padding-bottom: 10px;">
				<input type="button" value="<s:text name='lbl.submit'/>" class="buttonsubmit"
					onclick="return getData()" />  <%--  <input name="button" type="button"
					class="buttonsubmit" id="button3" value="<s:text name='lbl.print'/>"
					onclick="window.print()" />  <input type="button"
					value="<s:text name='lbl.view.all.minor.schedules'/>" class="buttonsubmit"
					onclick="return showAllMinorSchedules()" style="width: 170px;" /> <input
					type="button" value="<s:text name='lbl.view.all.schedules'/>" class="buttonsubmit"
					onclick="return showAllSchedules()" /> --%>
			</div>
			<div align="left" class="extracontent">
				To print the report, please ensure the following settings:<br /> 1.
				Paper size: A4<br /> 2. Paper Orientation: Landscape <br />
			</div>
		</div>
	</s:form>
</div>
<div id="result"></div>
