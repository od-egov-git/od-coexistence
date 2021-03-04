
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

<%@ include file="/includes/taglibs.jsp" %>
<%@ taglib prefix="egov-authz" uri="/WEB-INF/taglib/egov-authz.tld" %> 
<link rel="stylesheet" type="text/css" href="<egov:url path='/yui/assets/skins/sam/autocomplete.css'/>" />
<head>
	<title><s:text name="searchreceipts.title"/></title>
<script  >

jQuery.noConflict();
jQuery(document).ready(function() {
	//$("#toDate").datepicker().datepicker("setDate", new Date());
     jQuery(" form ").submit(function( event ) {
    	 doLoadingMask();
    	 onBodyLoad();
    });
     doLoadingMask();
     onBodyLoad();
 });

jQuery(window).load(function () {
	undoLoadingMask();
	onBodyLoad();
});

function isChecked(chk) {
	if (chk.length == undefined) {
 	if (chk.checked == true)
  	return true;
 	else return false;	
 } else {
 	for (i = 0; i < chk.length; i++)
		{
			if (chk[i].checked == true ) return true;
		}
	return false;
 }
}

<jsp:useBean id="now" class="java.util.Date" />

	<fmt:formatDate var = "currDate" pattern="dd/MM/yyyy" value="${now}" />
		var currDate = "${currDate}";


function checkselectedreceiptcount(obj)
{
	var cnt=document.getElementsByName('selectedReceipts');
	var receiptstatus=document.getElementsByName('receiptstatus');
	var j=0;
	for (i = 0; i < cnt.length; i++)
	{
		if (cnt[i].checked == true )
		{
			j++; 
			if(obj=='cancel' && receiptstatus[i].value=="Cancelled")
			{
				dom.get("selectedcancelledreceiptserror").style.display="block";
				return -1;
			}
			else
			{
				dom.get("selectedcancelledreceiptserror").style.display="none";
			}
		}
		else
		{
			dom.get("selectedcancelledreceiptserror").style.display="none";
		}
	}
	if(j==0)
		return 0;
	else if(j>1)
		return 2;
	else 
		return 1;
}

function checkcancelforselectedrecord()
{
    dom.get("pendingreceiptcancellationerror").style.display="none";receipts/receipt-newform
	dom.get("selectcancelerror").style.display="none";
	var check=checkselectedreceiptcount('cancel');
	// more than one receipt has been chosen. Should not allow cancellation
	if(check==2)
	{
		dom.get("norecordselectederror").style.display="none";
		dom.get("selectcancelerror").style.display="block";
		dom.get("selectprinterror").style.display="none";
		window.scroll(0,0);
		return false;
	}
	// no receipts have been chosen. should not allow cancellation
	else if(check==0)
	{
		dom.get("selectcancelerror").style.display="none";
		dom.get("norecordselectederror").style.display="block";
		dom.get("selectprinterror").style.display="none";
		window.scroll(0,0);
		return false;
	}
	// one or more cancelled receipts have been chosen. should not allow cancellation
	else if(check==-1)
	{
		dom.get("selectcancelerror").style.display="none";
		dom.get("norecordselectederror").style.display="none";
		dom.get("selectprinterror").style.display="none";
		window.scroll(0,0);
		return false;
	}
	//one receipt has been chosen. Cancellation is allowed
	else
	{
		var cnt=document.getElementsByName('selectedReceipts');
		var receiptstatus=document.getElementsByName('receiptstatus');
		var instrumenttype=document.getElementsByName('instrumenttype');
		var j=0;
		for (m = 0; m < cnt.length; m++)
		{
			if (cnt[m].checked == true )
			{
				if(receiptstatus[m].value=="Pending")
				{
					dom.get("pendingreceiptcancellationerror").style.display="block";
					window.scroll(0,0);
					return false;
				}

				else if(receiptstatus[m].value=="Instrument Bounced")
				{
					dom.get("instrumentbouncedreceiptcancellationerror").style.display="block";
					window.scroll(0,0);
					return false;
				}
				else if(receiptstatus[m].value=="Remitted" || receiptstatus[m].value=="Partial Remitted")
				{
					dom.get("remittedreceiptcancellationerror").style.display="block";
					window.scroll(0,0);
					return false;
				}

				if(instrumenttype[m].value=="online")
				{
					dom.get("onlinereceiptcancellationerror").style.display="block";
					window.scroll(0,0);
					return false;
				}
				
			}
		}
		dom.get("selectcancelerror").style.display="none";
		var receipttype=document.getElementsByName('receipttype');
		var cnt=document.getElementsByName('selectedReceipts');
		
		for (m = 0; m < cnt.length; m++)
		{
			if (cnt[m].checked == true )
			{
				if(receipttype[m].value=="A" || receipttype[m].value=="B")
				{
					document.searchReceiptForm.action="receipt-cancel.action";
				}
				if(receipttype[m].value=='C')
				{
					document.searchReceiptForm.action="challan-cancelReceipt.action";
				}
				
			}
		}
		
		document.searchReceiptForm.submit();
	}
}

function downloadExcel()
{
	var fromDate=document.getElementById('fromDate').value;
	var toDate=document.getElementById('toDate').value;
	var service=document.getElementById('serviceTypeIdforExcel').value;
	var dept=document.getElementById('deptId').value;
	var subdivison=document.getElementById('subdivison').value;
	var receiptType=document.getElementById('receiptType').value;

	document.searchReceiptForm.action="searchReceipt-downloadDayBookReport.action?fromDate="+fromDate+"&toDate="+toDate+"&serviceTypeDuringDownload="+service+"&deptId="+dept;
	document.searchReceiptForm.submit();
}

function checkprintforselectedrecord()
{
	var check=checkselectedreceiptcount('print');
	// more than one receipts have been chosen. should not print
	if(check==2)
	{
		dom.get("norecordselectederror").style.display="none";
		dom.get("selectprinterror").style.display="block";
		dom.get("selectcancelerror").style.display="none";
		window.scroll(0,0);
		return false;
	}
	// no receipts ahev been chosen for print
	else if(check==0)
	{
		dom.get("selectprinterror").style.display="none";
		dom.get("norecordselectederror").style.display="block";
		dom.get("selectcancelerror").style.display="none";
		window.scroll(0,0);
		return false;
	}
	// single receipt has been chosen. Print is allowed
	else
	{
		dom.get("selectprinterror").style.display="none";
		document.searchReceiptForm.action="receipt-printReceipts.action";
		document.searchReceiptForm.submit();
	}
	//document.searchReceiptForm.action="receipt-printReceipts.action";
	//document.searchReceiptForm.submit();
}

function validate()
{
	var fromdate=dom.get("fromDate").value;
	var todate=dom.get("toDate").value;
	document.getElementById("toDate").value = todate;
	console.log("todate :::"+todate);
	var serviceCategoryid=dom.get("serviceCategoryid").value;
	var serviceId=dom.get("serviceId").value;
	var valueservicetype="";
	console.log("serviceCategoryid : "+serviceCategoryid);
	console.log("serviceId : "+serviceId);
	
	if(serviceCategoryid==-1|| serviceCategoryid==0)
	{
		valueservicetype=null;
		
		}
	else{
			if(serviceId==0 || serviceId==""){
				
				valueservicetype=serviceCategoryid;
				
				}
			else{
				  valueservicetype = serviceCategoryid.concat('.',serviceId); 
	console.log("serviceIdserviceCategoryid : "+valueservicetype);
				}
		}
	
	//var serviceType=dom.get("serviceType").value;
	var serviceType=valueservicetype;
	document.getElementById("serviceType").value = serviceType;
	console.log("serviceType : "+serviceType);
	var valSuccess = true;
	/* if(null!= document.getElementById('serviceClass') && document.getElementById('serviceClass').value == '-1'){
		dom.get("error_area").style.display="block";
		dom.get("error_area").innerHTML = '<s:text name="service.servictype.null" />' + '<br>';
		window.scroll(0,0);
		valSuccess=false;
		return false;
	} */

	if(serviceType==-1){
		valSuccess=false;
		dom.get("error_area").style.display="block";
		dom.get("error_area").innerHTML = '<s:text name="service.servictype.null" />' + '<br>';
		window.scroll(0,0);
		return false;
	}

	if(fromdate=="" || todate=="" )
	{
		
			dom.get("FromDatedatemessage").style.display="block";
			window.scroll(0,0);
			valSuccess= false;
			return false;
		
	}
	
	if(fromdate!="" && todate!="" && fromdate!=todate)
	{
		if(!checkFdateTdate(fromdate,todate))
		{ 
			dom.get("comparedatemessage").style.display="block";
			window.scroll(0,0);
			valSuccess= false;
			return false;
		}
	}
	else
	{		
		dom.get("comparedatemessage").style.display="none";
		doLoadingMask('#loadingMask');
		valSuccess= true;
		return true;
	}
	return valSuccess;
	
}

/* var receiptNumberSelectionEnforceHandler = function(sType, arguments) {
      		warn('improperreceiptNumberSelection');
}
var receiptNumberSearchSelectionHandler = function(sType, arguments) { 
			var oData = arguments[2];
			dom.get("receiptNumberSearch").value=oData[0];
}


var manualReceiptNumberSearchSelectionHandler = function(sType, arguments) { 
	var oData = arguments[2];
	dom.get("manualReceiptNumberSearch").value=oData[0];
}
var manualReceiptNumberSelectionEnforceHandler = function(sType, arguments) {
		warn('impropermanualReceiptNumberSelectionWarning');
} */
function checkviewforselectedrecord()
{
	dom.get("norecordselectederror").style.display="none";
	dom.get("selectprinterror").style.display="none";
	dom.get("selectcancelerror").style.display="none";
	var cnt=document.getElementsByName('selectedReceipts');
	var receiptstatus=document.getElementsByName('receiptstatus');
	var j=0;
	for (i = 0; i < cnt.length; i++)
	{
		if (cnt[i].checked == true )
		{
			j++; 
		}
	}
	//no records have been selected for view
	if(j==0)
	{
		dom.get("norecordselectederror").style.display="block";
		window.scroll(0,0);
		return false;
	}
	// multiple records have been chosen . Viewing is allowed
	else
	{	
		doLoadingMask('#loadingMask');
		document.searchReceiptForm.action="receipt-viewReceipts.action";
		document.searchReceiptForm.submit();
	}	

}
function populateServiceType(selected){
	addServiceTypeDropdown('serviceTable');
    var isServiceTypeExist = false;
   // document.getElementById('serviceTable').innerHTML='';
    if(selected == -1){
		return;
    }
    <s:iterator value="serviceCategoryNames" var="obj">
    var serTypeKey = '<s:property value="#obj.key"/>';
    var serTypeValue = '<s:property value="serviceTypeMap[#obj.key]"/>';
    if(selected == serTypeKey && serTypeValue != ''){
    	isServiceTypeExist = true;
    	addServiceTypeDropdown('serviceTable');
			<s:iterator value="serviceTypeMap[#obj.key]" status="stat" var="names">
				var stKey = '<s:property value="#names.key"/>';
				var stValue = '<s:property value="#names.value"/>';
				document.getElementById('serviceId').options[<s:property value="#stat.index+1"/>]= new Option(stValue,stKey);
		</s:iterator>
    }
	 </s:iterator>
	 if(!isServiceTypeExist){
		 loadFinDetails(this);
	 }
}
function addServiceTypeDropdown(tableId){
    /* var table = document.getElementById(tableId);
    var row = table.insertRow(0);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    cell1.className='bluebox';
    cell1.width="45%";
    cell2.className='bluebox';
    cell2.width="50%";
    cell1.innerHTML = '<s:text name="miscreceipt.service" /><span class="mandatory"/>';
    cell2.innerHTML = '<select name="serviceId" id="serviceId" onchange="loadFinDetails(this)"/>'; */
	document.getElementById('serviceId').options.length=0;
	document.getElementById('serviceId').options[0]= new Option('--------Choose--------','0');
	console.log('ok');

}
function onChangeServiceClass(obj)
{
    if(obj!=null && obj.value!=null && obj.value!='-1'){
    	populateserviceType({serviceClass:obj.value});
    }
}

function onBodyLoad(){
	addServiceTypeDropdown('serviceTable');
	console.log('inbodyload');
	//document.getElementById("toDate").value=currDate;
	//document.getElementById("toDate").disabled = true;
	//var todate=document.getElementById("toDate").value;
	//console.log(todate);
	
};
</script> 
</head>
<body>
<div class="errorstyle" id="error_area" style="display:none;"></div>
<span align="center" style="display: none" id="pendingreceiptcancellationerror">
  <li>
     <font size="2" color="red"><b><s:text name="error.pendingreceipt.cancellation"/></b></font>
  </li>
</span>
<span align="center" style="display: none" id="instrumentbouncedreceiptcancellationerror">
  <li>
     <font size="2" color="red"><b><s:text name="error.instrumentbouncedreceipt.cancellation"/></b></font>
  </li>
</span>
<span align="center" style="display: none" id="remittedreceiptcancellationerror">
  <li>
     <font size="2" color="red"><b><s:text name="error.remittedreceipt.cancellation"/></b></font>
  </li>
</span>
<span align="center" style="display: none" id="onlinereceiptcancellationerror">
  <li>
     <font size="2" color="red"><b><s:text name="error.onlinereceipt.cancellation"/></b></font>
  </li>
</span>
<span align="center" style="display: none" id="selectprinterror">
  <li>
     <font size="2" color="red"><b><s:text name="error.print.nomultipleprintreceipts"/>  </b></font>
  </li>
</span>
<span align="center" style="display: none" id="selectcancelerror">
  <li>
     <font size="2" color="red"><b><s:text name="error.print.nomultiplecancelreceipts"/>  </b></font>
  </li>
</span>
<span align="center" style="display: none" id="norecordselectederror">
  <li>
     <font size="2" color="red"><b><s:text name="error.norecordselected"/></b></font>
  </li>
</span>
<span align="center" style="display: none" id="selectedcancelledreceiptserror">
  <li>
     <font size="2" color="red"><b><s:text name="error.selectedcancelledreceiptserror"/></b></font>
  </li>
</span>
<span align="center" style="display: none" id="invaliddateformat">
  <li>
     <font size="2" color="red"><b>
		<s:text name="common.dateformat.errormessage"/>
	</b></font>
  </li>
</span>
<span align="center" style="display: none" id="comparedatemessage">
  <li>
     <font size="2" color="red"><b>
		<s:text name="common.comparedate.errormessage"/>
	</b></font>
  </li>
</span>

<span align="center" style="display: none" id="FromDatedatemessage">
  <li>
     <font size="2" color="red"><b>
		<s:text name="Please Select FromDate"/>
	</b></font>
  </li>
</span>

<s:form theme="simple" name="searchReceiptForm" action="searchReceipt-searchDayBookReport.action">
<div class="formmainbox"><div class="subheadnew"><s:text name="Day Book Receipts Report"/>
</div>
<%-- <div class="subheadsmallnew"><span class="subheadnew"><s:text name="searchreceipts.criteria"/></span></div> --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

  		<tr>
	     <!--  <td width="4%" class="bluebox">&nbsp;</td> -->
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.fromdate"/><span class="mandatory" /></td>
		  <s:date name="fromDate" var="cdFormat" format="dd/MM/yyyy" />
		  <td width="24%" class="bluebox"><s:textfield id="fromDate" name="fromDate" value="%{cdFormat}" onfocus="javascript:vDateType='3';" onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('forms[0].fromDate');" onmouseover="window.status='Date Picker';return true;"  onmouseout="window.status='';return true;"  ><img src="/services/egi/resources/erp2/images/calendaricon.gif" alt="Date" width="18" height="18" border="0" align="absmiddle" /></a><div class="highlight2" style="width: 80px">DD/MM/YYYY</div></td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.todate"/><span class="mandatory" /></td>
	      <s:date name="toDate" var="cdFormat1" format="dd/MM/yyyy" />
		  <td width="30%" class="bluebox"><s:textfield id="toDate" name="toDate" value="%{cdFormat1}" onfocus="javascript:vDateType='3';" onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('forms[0].toDate');" onmouseover="window.status='Date Picker';return true;"  onmouseout="window.status='';return true;"  ><img src="/services/egi/resources/erp2/images/calendaricon.gif" alt="Date" width="18" height="18" border="0" align="absmiddle" /></a><div class="highlight2" style="width: 80px">DD/MM/YYYY</div></td>
	    </tr>
	    <tr>
	     <td width="21%" class="bluebox"><s:text	name="miscreceipt.service.category" />
					</td>
					<td width="30%" class="bluebox"><s:select headerKey="-1"
							headerValue="----Choose----" name="serviceCategory"
							id="serviceCategoryid" cssClass="selectwk"
							list="serviceCategoryNames" value="%{service.serviceCategory}"
							onChange="populateServiceType(this.value);" /></td>
					
					 <td width="21%" class="bluebox"><s:text	name="miscreceipt.service" />
					</td>
					<td width="30%" class="bluebox"><s:select headerKey="-1"
							headerValue="----Choose----" name="serviceId"
							id="serviceId" cssClass="selectwk"
							list="serviceCategoryNames"  /></td>
							
					<td class="bluebox" colspan='2'>
						<table width="100%" id='serviceTable'>
						</table>
					</td>
	      
	     
	      <%-- <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.counter"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="-1" headerValue="%{getText('searchreceipts.counter.select')}" name="counterId" id="counter" cssClass="selectwk" list="dropdownData.counterList" listKey="id" listValue="name" value="%{counterId}" /> </td> --%>
	    </tr>
	   
	    <tr>
		<td width="21%" class="bluebox2"><s:text name="challan.department"/></td>
		  <td width="24%" class="bluebox2"><s:select headerKey=""
							headerValue="----Choose----" name="deptId" id="deptId" cssClass="selectwk" list="dropdownData.departmentList" listKey="code" listValue="name"  value="%{deptId}"/> </td>
	    
	    
	    <!-- add by bhushan -->
	    <td width="21%" class="bluebox"><s:text name="Collected By"/></td>
	      <td width="24%" class="bluebox">
	      <div class="yui-skin-sam"><s:textfield id="collectedBy" type="text" name="collectedBy"/></td>
	      
	     
	    
	    
	      <td><s:hidden  name="serviceTypeId" id="serviceType"   value="0" />
	      		<input type="hidden" name="serviceTypeIdforExcel" value="${serviceTypeIdforExcel}" id="serviceTypeIdforExcel">
	      	 </td>
	    </tr>	    
	    
	    
	    <!-- add by bhushan -->
	    <tr>
	     
	      <td width="21%" class="bluebox"><s:text name="Amount"/></td>
	      <td width="24%" class="bluebox">
	      <div class="yui-skin-sam"><s:textfield id="searchAmount" type="text" name="searchAmount"/></td>
	    
	    
	    <td width="21%" class="bluebox2"><s:text name="Mode Of Payment"/></td>
		  <td width="24%" class="bluebox2"><select name="modeOfPayment">
   <option value="">----Choose----</option>
    <option value="cash">CASH</option>
     <option value="cheque">CHEQUE</option>
    <option value="DD" >DD</option> 
    <option value="card" >Card</option>
    <option value="posmohbd"> POS MOH B&D</option> 
    <option value="posmohcattle"> POS MOH Cattle</option>
    <option value="posmohslh">POS MOH SLH</option>
</select> </td>
	    
	    </tr>
	    <tr>
	     
	      <td width="21%" class="bluebox"><s:text name="misc.receipt.sub.divison"/></td>
	      <td width="24%" class="bluebox">
	      <div class="yui-skin-sam"><s:select headerKey="-1"
							headerValue="----Choose----" name="subdivison" id="subdivison"  cssClass="selectwk" list="dropdownData.subdivisonList" listKey="subdivisonCode" listValue="subdivisonName"  value="%{subdivison}"/></td>
	    
	    
	    <td width="21%" class="bluebox2"><s:text name="misc.receipt.type"/></td>
		  <td width="24%" class="bluebox2"><s:select name="receiptType"
							headerKey="-1" headerValue="Select"
							value="%{receiptType}"
							list="#{'NEW':'NEW' ,'DEPOSITED':'DEPOSITED' ,'CANCELLED':'CANCELLED'}"
							id="receiptType" /></td>
	    
	    </tr>	    
	 <%--    <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.status"/></td>
	      <td width="24%" class="bluebox"><s:select id="searchStatus" name="searchStatus" headerKey="-1" headerValue="%{getText('searchreceipts.status.select')}" cssClass="selectwk" list="%{receiptStatuses}" value="%{searchStatus}" listKey="id" listValue="description" /> </td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.paymenttype"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="" headerValue="%{getText('searchreceipts.paymenttype.select')}" name="instrumentType" id="instrumentType" cssClass="selectwk" list="dropdownData.instrumentTypeList" listKey="type" listValue="type" value="%{instrumentType}" /> </td>	
	    </tr>
	    <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.manual.receiptno"/></td>
	      <td width="24%" class="bluebox"><s:textfield id="manualReceiptNumber" type="text" name="manualReceiptNumber"/></td>
	      <td width="21%" class="bluebox"> &nbsp; </td>
	      <td width="30%" class="bluebox"> &nbsp; </td>   
	    </tr> --%>
	   <%--  
	    <tr>
					<td>
						<div class="subheadsmallnew"><span class="subheadnew">
											<s:text name="bankcollection.title" />
						</span>		
						</div>
					</td>
		</tr>
	     <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.bankbranch"/></td>
	      <td width="24%" class="bluebox"><s:select headerKey="-1"
								headerValue="Select Bank Branch" name="branchId" id="branchId"
								cssClass="selectwk" list="dropdownData.bankBranchList"
								listKey="id" listValue="branchname"
								value="%{branchId}" /> </td>
	      <td width="21%" class="bluebox">&nbsp;</td>
	      <td width="30%" class="bluebox">&nbsp;</td>
	    </tr> --%>
	    </table>
		<%-- <div align="left" class="mandatory1">
		              <s:text name="report.bankbranch.note"/>
		</div> --%>
</div>
<div id="loadingMask" style="display: none; overflow: hidden; text-align: center"><img src="/services/collection/resources/images/bar_loader.gif"/> <span style="color: red">Please wait....</span></div>
    <div class="buttonbottom">
      <label><s:submit type="submit" cssClass="buttonsubmit" id="button" key="lbl.search" onclick="return validate();"/></label>
      <label><s:submit type="submit" cssClass="button" key="lbl.reset" onclick="document.searchReceiptForm.action='searchReceipt-reset.action'"/></label>
      <s:if test="%{results.isEmpty()}">
      	<input name="closebutton" type="button" class="button" id="closebutton" value="<s:text name='lbl.close'/>" onclick="window.close();"/>
      </s:if>
      
</div>
<s:if test='%{resultList.isEmpty()}'>
		<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="tablebottom">
		<tr> 
			<div>&nbsp;</div>
			<div class="subheadnew"><s:text name="searchresult.norecord"/></div>
		</tr>
		</table>
</s:if>
<s:if test='%{!resultList.isEmpty()}'>
<c:set var="count" value="0" scope="page" />
<div align="center">		
<display:table name="searchResult" uid="currentRow"  style="width:100%;border-left: 1px solid #DFDFDF;" cellpadding="0" cellspacing="0" export="false" requestURI="">
<display:caption media="pdf">&nbsp;</display:caption>
<display:column headerClass="bluebgheadtd"  class="blueborderfortd" title="S.No." style="width:3%">


<c:set var="count" value="${count + 1}" scope="page"/>
<c:out value = "${count}"/>
<!-- <input name="selectedReceipts" type="text" id="selectedReceipts"
				value="1"/> -->

<input type="hidden" name="receiptstatus" id="receiptstatus" value="${currentRow.curretnStatus}" />
<input type="hidden" name="receipttype" id="receipttype" value="${currentreceipttype}" />
</display:column>
<display:column headerClass="bluebgheadtd" class="blueborderfortd" property="receiptdate" title="Date" format="{0,date,dd/MM/yyyy}" style="width:8%;text-align: center" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Receipt No." style="width:8%;text-align:right" property="receiptnumber"/>
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="GST No." style="width:8%;text-align:left" property="gstno" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Collected By" style="width:8%;text-align:right" property="createdUser"/> 
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Payee Name" style="width:8%;text-align:right" property="paidBy"/>

<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Service" style="width:12%;text-align:left" property="service" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Mode of payment" style="width:12%;text-align:left" property="modOfPayment" />

<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Particulars" style="width:12%;text-align:left" property="referenceDesc" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Prinicpal Amount (Rs.)" property="principalAmount" style="width:8%; text-align: right" format="{0, number, #,##0.00}" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="GST (Rs.)" property="gstAmount" style="width:8%; text-align: right" format="{0, number, #,##0.00}" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Amount (Rs.)" property="totalAmount" style="width:8%; text-align: right" format="{0, number, #,##0.00}" />

<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Date Of Deposit"  format="{0,date,dd/MM/yyyy}" style="width:12%;text-align:left" property="rrDate" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Remitance No." style="width:16%;text-align:left" property="referencenumber" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Bank Account No." style="width:12%;text-align:left" property="bankAccountNumber" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Deposit Amount." style="width:27%;text-align:left" property="depositAmount" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Status" style="width:27%;text-align:left" property="curretnStatus" />



<%-- <div align="center">
<s:set var="instrtype" value="" />
<s:iterator status="stat1" value="#attr.currentRow.receiptInstrument">
<s:if test="instrumentType.type!=null">
<s:property value="instrumentType.type"/>
<s:set var="instrtype" value="%{instrumentType.type}" />
</s:if>
<s:if test="!#stat1.last">, </s:if>
</s:iterator>&nbsp;
</div>
<input type="hidden" name="instrumenttype" id="instrumenttype" value="${instrtype}" />
</display:column> --%>
<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Owner" style="width:8%;text-align:center" property="workflowUserName"></display:column> --%>
</display:table>	 
</div>
<br/>
<div class="buttonbottom">
  
    <input name="button32" type="button" class="buttonsubmit" id="button32" value="Excel" onclick="return downloadExcel()"/> 
  
   <%-- <egov-authz:authorize actionName="CancelReceipt">
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="Cancel Receipt" onclick="return checkcancelforselectedrecord()"/>
  </egov-authz:authorize> --%>
  <input name="button32" type="button" class="button" id="button32" value="<s:text name='lbl.close'/>" onclick="window.parent.postMessage('close','*');window.close();"/>
</div>
</s:if>
</s:form>
</body>

	
