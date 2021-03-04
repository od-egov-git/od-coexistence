
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
<style type="text/css">
	
.modal {
  display: none; 
  position: fixed;
  z-index: 9999; 
  left: 0;
  top: 0;
  width: 100%; 
  height: 100%;
  overflow: auto;
  background-color: rgb(0,0,0); 
  background-color: rgba(0,0,0,0.4);
}


.modal-content {
	 /*    opacity: 0.7;
	    margin-right: 350px;
  background-color: #d8d7e4; */
  margin: 15% auto; /* 15% from the top and centered */
/*   padding: 45px;
  border: 1px solid #888;*/
  width: 80%;  /* Could be more or less, depending on screen size */
}


.close {
 /*  color: #aaa; */
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
.modal-header {
    padding: 9px;
}

.modal in{
	padding-left: 8px;
    display: none;
}

</style>	
<script  >

jQuery.noConflict();
jQuery(document).ready(function() {
  	 
     jQuery(" form ").submit(function( event ) {
    	 doLoadingMask();
    });
     doLoadingMask();
 });

jQuery(window).load(function () {
	undoLoadingMask();
});
/* Modal Hide */
jQuery("#myModal").modal({backdrop: false});
jQuery('#myModal').modal('hide');  

jQuery(".showModal").click(function(){
	
	  jQuery("#myModal").modal('show');
	});
jQuery(document).ready(function($) {
	jQuery("#myModal").modal({
		backdrop : false
	});
	jQuery('#myModal').modal('hide');  
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
			if(obj=='cancel' && receiptstatus[i].value=="CANCELLED")
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
    dom.get("pendingreceiptcancellationerror").style.display="none";
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
	var serviceType=dom.get("serviceType").value;
	console.log("serviceType : "+serviceType);
	var valSuccess = true;
	/* if(null!= document.getElementById('serviceClass') && document.getElementById('serviceClass').value == '-1'){
		dom.get("error_area").style.display="block";
		dom.get("error_area").innerHTML = '<s:text name="service.servictype.null" />' + '<br>';
		window.scroll(0,0);
		valSuccess=false;
		return false;
	} */

	/* if(serviceType==-1){
		valSuccess=false;
		dom.get("error_area").style.display="block";
		dom.get("error_area").innerHTML = '<s:text name="service.servictype.null" />' + '<br>';
		window.scroll(0,0);
		return false;
	} */
	
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

function checkCheckboxValidator(){
	console.log("In Cancel button");
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
	else if(j>1)
	{
		dom.get("nocancelselectederror").style.display="block";
		window.scroll(0,0);
		return false;
	}
	// multiple records have been chosen . Viewing is allowed
	else
	{	
		console.log("inside else block>>>"+j);		
		doLoadingMask('#loadingMask');
		document.searchReceiptForm.action="receipt-viewReceiptsChangeStatus.action";
		document.searchReceiptForm.submit();
	}	

	
}
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
			//added for check Status CANCELLED
			if(receiptstatus[i].value=="CANCELLED")
			{
				dom.get("selectedcancelledreceiptserror").style.display="block";
				j--;
		}
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

function onChangeServiceClass(obj)
{
    if(obj!=null && obj.value!=null && obj.value!='-1'){
    	populateserviceType({serviceClass:obj.value});
    }
}

function ConfirmDialog(message) {
	  $('<div></div>').appendTo('body')
	    .html('<div><h6>' + message + '?</h6></div>')
	    .dialog({
	      modal: true,
	      title: 'Delete message',
	      zIndex: 10000,
	      autoOpen: true,
	      width: 'auto',
	      resizable: false,
	      buttons: {
	        Yes: function() {
	          // $(obj).removeAttr('onclick');                                
	          // $(obj).parents('.Parent').remove();

	          $('body').append('<h1>Confirm Dialog Result: <i>Yes</i></h1>');

	          $(this).dialog("close");
	        },
	        No: function() {
	          $('body').append('<h1>Confirm Dialog Result: <i>No</i></h1>');

	          $(this).dialog("close");
	        }
	      },
	      close: function(event, ui) {
	        $(this).remove();
	      }
	    });
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
<span align="center" style="display: none" id="nocancelselectederror">
  <li>
     <font size="2" color="red"><b><s:text name="error.nocancelselected"/></b></font>
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

<s:form theme="simple" name="searchReceiptForm" action="searchReceipt-search.action">
<div class="formmainbox"><div class="subheadnew"><s:text name="searchreceipts.title"/>
</div>
<div class="subheadsmallnew"><span class="subheadnew"><s:text name="searchreceipts.criteria"/></span></div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

	    <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td class="bluebox"><s:text name="service.master.classification"/> <span class="mandatory"></td>
			<td class="bluebox"> 
				<%-- <s:select list="serviceClassMap" headerKey="-1" headerValue="%{getText('miscreceipt.select')}"
				name="serviceClass" id="serviceClass" onchange="onChangeServiceClass(this);"></s:select> --%>
				<s:select name='type' list="#{'type':'MISCELLANEOUS' }"></s:select>
			</td>
			<%--  <egov:ajaxdropdown id="serviceTypeDropdown" fields="['Text','Value']" dropdownId='serviceType'
                url='receipts/ajaxReceiptCreate-ajaxLoadServiceByClassification.action' /> --%>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.servicetype"/> </td>
	      <td width="24%" class="bluebox"><s:select headerKey="-1"  headerValue="%{getText('searchreceipts.servicetype.select')}"  name="serviceTypeId" id="serviceType" cssClass="selectwk" list="dropdownData.serviceTypeList" listKey="code" listValue="businessService" value="%{serviceTypeId}" /> </td>
	      
	      <%-- <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.counter"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="-1" headerValue="%{getText('searchreceipts.counter.select')}" name="counterId" id="counter" cssClass="selectwk" list="dropdownData.counterList" listKey="id" listValue="name" value="%{counterId}" /> </td> --%>
	    </tr>
	     <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.fromdate"/></td>
		  <s:date name="fromDate" var="cdFormat" format="dd/MM/yyyy"/>
		  <td width="24%" class="bluebox"><s:textfield id="fromDate" name="fromDate" value="%{cdFormat}" onfocus="javascript:vDateType='3';" onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('forms[0].fromDate');" onmouseover="window.status='Date Picker';return true;"  onmouseout="window.status='';return true;"  ><img src="/services/egi/resources/erp2/images/calendaricon.gif" alt="Date" width="18" height="18" border="0" align="absmiddle" /></a><div class="highlight2" style="width: 80px">DD/MM/YYYY</div></td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.todate"/></td>
	      <s:date name="toDate" var="cdFormat1" format="dd/MM/yyyy"/>
		  <td width="30%" class="bluebox"><s:textfield id="toDate" name="toDate" value="%{cdFormat1}" onfocus="javascript:vDateType='3';" onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('forms[0].toDate');" onmouseover="window.status='Date Picker';return true;"  onmouseout="window.status='';return true;"  ><img src="/services/egi/resources/erp2/images/calendaricon.gif" alt="Date" width="18" height="18" border="0" align="absmiddle" /></a><div class="highlight2" style="width: 80px">DD/MM/YYYY</div></td>
	    </tr>
	    <tr>
	      <td width="4%" class="bluebox">&nbsp;</td>
	      <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.receiptno"/></td>
	      <td width="24%" class="bluebox">
	      <div class="yui-skin-sam"><s:textfield id="receiptNumber" type="text" name="receiptNumber"/></td>
	     <%--  <td width="21%" class="bluebox"><s:text name="searchreceipts.criteria.user"/></td>
	      <td width="30%" class="bluebox"><s:select headerKey="-1" headerValue="%{getText('searchreceipts.user.select')}" name="userId" id="user" cssClass="selectwk" list="dropdownData.userList" listKey="id" listValue="name" value="%{userId}" /> </td>
	    --%>
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

<div align="center">		
<display:table name="searchResult" uid="currentRow"  style="width:100%;border-left: 1px solid #DFDFDF;" cellpadding="0" cellspacing="0" export="false" requestURI="">
<display:caption media="pdf">&nbsp;</display:caption>
<display:column headerClass="bluebgheadtd"  class="blueborderfortd" style="width:3%">
<s:if test='%{collectionVersion eq "V2"}'>
<input name="selectedReceipts" type="checkbox" id="selectedReceipts"
				value="${currentRow.paymentId}"/>
</s:if>
<s:else>
<input name="selectedReceipts" type="checkbox" id="selectedReceipts"
				value="${currentRow.receiptnumber}"/>
</s:else>
<input type="hidden" name="receiptstatus" id="receiptstatus" value="${currentRow.curretnStatus}" />
<input type="hidden" name="receipttype" id="receipttype" value="${currentreceipttype}" />
</display:column>
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Receipt No." style="width:10%;text-align:right" property="receiptnumber"/>
<display:column headerClass="bluebgheadtd" class="blueborderfortd" property="receiptdate" title="Receipt Date" format="{0,date,dd/MM/yyyy}" style="width:10%;text-align: center" />
<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" title="G8 Receipt number/Date" style="width:8%;text-align:right" property="g8data"/>--%>
<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" property="manualreceiptdate" title="G8 Receipt Date" format="{0,date,dd/MM/yyyy}" style="width:8%;text-align: center" /> --%>
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Service" style="width:35%;text-align:left" property="service" />
<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Bill Number" style="width:8%;text-align:right" property="referencenumber" /> --%>
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Particulars" style="width:27%;text-align:left" property="referenceDesc" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Payee name & address" style="width:15%;text-align:left" property="paidBy" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Sub Divison" style="width:27%;text-align:left" property="subdivison" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="GST No." style="width:27%;text-align:left" property="gstno" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Amount (Rs.)" property="totalAmount" style="width:10%; text-align: right" format="{0, number, #,##0.00}" />
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Mode of Payment" style="width:10%"  property="modOfPayment"/>
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
<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Status" style="width:8%;text-align:center" property="curretnStatus"></display:column>
<%-- <display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Owner" style="width:8%;text-align:center" property="workflowUserName"></display:column> --%>
</display:table>	 
</div>
<br/>
<div class="buttonbottom">
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="View" onclick="return checkviewforselectedrecord()"/>
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="Print" onclick="return checkprintforselectedrecord()"/> 
   <%-- <egov-authz:authorize actionName="CancelReceipt">
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="Cancel Receipt" onclick="return checkcancelforselectedrecord()"/>
  </egov-authz:authorize> --%>
  <input name="button32" type="button" class="button" id="button32" value="<s:text name='lbl.close'/>" onclick="window.parent.postMessage('close','*');window.close();"/>
  <input name="buttonCancel" type="button" class="buttonsubmit showModal" id="buttonCancel" value="Cancel" data-toggle='modal' data-target='#myModal'/>
  
  
</div>
</s:if>
</s:form>
 <!-- Modal --> 
	<div class="modal fade" id="myModal" role="dialog" style="display: none;">
		<div class="modal-dialog" role="dailog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><b>Cancel Confirmation</b></h4>
				</div>
				<div class="modal-body">
				<p>Do You Really Want To Change Status?</p>
					<!-- <font style="align: center;">Are You Sure To Delete Record?</font> -->
					</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" onclick="return checkCheckboxValidator()" data-dismiss="modal" style="width: 100px;">Yes</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal" style="width: 100px;">No</button>
				</div>
			</div>
		</div>
	</div>
</body>

	
