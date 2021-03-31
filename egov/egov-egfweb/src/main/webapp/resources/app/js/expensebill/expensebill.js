/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

var $netPayableAccountCodeId = 0;
var subLedgerDisplayName;
var detailTypeName;
var detailKeyName;
var debitCodes = new Array();
var billamount = 0;
var debitamount = 0;
var creditamount = 0;
var netpayableamount = 0;
var debitAmountrowcount=0;
var creditAmoutrowcount=0;
var subledgerrowcount=0;

$(document).ready(function(){
	$.i18n.properties({ 
		name: 'message', 
		path: '/services/EGF/resources/app/messages/', 
		mode: 'both',
		async: true,
	    cache: true,
		language: getLocale("locale"),
		callback: function() {
			console.log('File loaded successfully');
		}
	});
	
	//$('#subLedgerType').trigger("change");
	worksDiv();
	$netPayableAccountCodeId = $('#netPayableId').val();
	patternvalidation(); 
	debitGlcode_initialize();
	creditGlcode_initialize();
	$('#fund').val($('#fund').val());
	loadCheckListTable();
	if($("#mode").val() == 'edit'){
		netpayableamount = $("#netPayableAmount").val();
		billamount = $("#billamount").val();
		creditamount = Number($("#billamount").val());
		//creditamount = Number($("#billamount").val()) - Number(netpayableamount);
		debitamount = $("#billamount").val();
		
		$("#expenseNetPayableAmount").html(netpayableamount);
		$("#expenseBillTotalDebitAmount").html(debitamount);
		$("#expenseBillTotalCreditAmount").html(creditamount);
		$("#billamount").val(billamount);
	}
	else{
		calculateBillAmount();
	}
	
	var entityName = new Bloodhound({
		datumTokenizer : function(datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer : Bloodhound.tokenizers.whitespace,
		remote : {
			url : '/services/EGF/common/getentitesbyaccountdetailtype?name=',
			replace: function (url, query) {
				var subLedgerType = $('.subledgerGl_code').val();
				if(subLedgerType == null || subLedgerType == "")
					bootbox.alert($.i18n.prop('msg.select.subledger.type'));
				return url + query + '&accountDetailType=' + subLedgerType ;
			},
			filter : function(data) {
				return $.map(data, function(ct) {
					return {
						code:ct.split("~")[0].split("-")[0],
						name : ct.split("~")[0].split("-")[1],
						id : ct.split("~")[1],
						codeAndName:ct
					};
				});
			}
		}
	});

	entityName.initialize();
$('.subLedgerCodeOT').typeahead({
	
		hint : true,
		highlight : true,
		minLength : 1
	}, {
		displayKey : 'codeAndName',
		source : entityName.ttAdapter()
	}).on('typeahead:selected', function (event, data) {
		
		document.getElementById('tempSubLedger[0].subLedgerCode').value=data.code;
			detailKeyName = data.name;
			document.getElementById('tempSubLedger[0].payTo').value=data.name;
			document.getElementById('tempSubLedger[0].detailkeyId').value=data.id;
			if(document.getElementById('tempSubLedger[0].netPayableDetailKeyId').value==""){
				document.getElementById('tempSubLedger[0].netPayableDetailKeyId').value=document.getElementById('tempSubLedger[0].detailkeyId').value;
				document.getElementById('tempSubLedger[0].netPayableDetailKeyName').value=detailKeyName;
			}

			
			
			
		/*$("#subLedgerCode").val(data.code);
		detailKeyName = data.name;
		$("#payTo").val(data.name);
		$("#detailkeyId").val(data.id);*/
	});
});

$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	if (button != null && (button == 'Forward')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	 }
	else if (button != null && (button == 'Reject')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	 }
	   else if (button != null && (button == 'SaveAsDraft')) {
	      
	       if(!validateWorkFlowApprover(button))
	           {
	           
	            return false;
	           }
	        if(true){
	             
	            deleteHiddenSubledgerRow();
	            //$('approvalDesignation').attr('required', false);
	            //$('approvalDepartment').attr('required', false);
	            //$('approvalPosition').attr('required', false);
	            return true;
	        }else
	            {
	             //alert('button-----2.2------'+button);
	            return false;
	            
	            }
	        
	    
	}else if (button != null && (button == 'Create And Approve')) {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').removeAttr('required');
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	} else{
		if(!validateWorkFlowApprover(button))
			return false;
		if($("form").valid()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	}
	return false;
});

function getCookie(name){
	let cookies = document.cookie;
	if(cookies.search(name) != -1){
		var keyValue = cookies.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return keyValue ? keyValue[2] : null;
	}
}

function getLocale(paramName){
	return getCookie(paramName) ? getCookie(paramName) : navigator.language;
}

function deleteHiddenSubledgerRow(){
	var subLedgerCount = $("#tblsubledgerdetails > tbody > tr:visible[id='subledhgerrow']").length;
	if(subLedgerCount==0){
		deleteRow($(".subLedgerAmount_0")[0],'tblsubledgerdetails');
	}
}
function debitGlcode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
	            url: '/services/EGF/common/getaccountcodesforaccountdetailtype?glcode=',
	            dataType: "json",
	            replace: function (url, query) {
					var subLedgerType = $('#subLedgerType').val();
					if(subLedgerType == null || subLedgerType == "")
						subLedgerType = "0";
					if(subLedgerType != null || subLedgerType != "")
					return url + query + '&accountDetailType=' + subLedgerType ;
				},
	            filter: function (data) {
	            	var responseObj = JSON.parse(data);
	                return $.map(responseObj, function (ct) {
	                    return {
	                        id: ct.id,
	                        name: ct.name,
	                        glcode: ct.glcode,
	                        issubledger: ct.isSubLedger,
	                        glcodesearch: ct.glcode+' ~ '+ct.name
	                    };
	                });
	            }
	        }
   });

   custom.initialize();
   var dt = $('.debitGlcode').typeahead({
   	hint : true,
		highlight : true,
		minLength : 1
		
	}, {		    
         displayKey: 'glcodesearch',
         source: custom.ttAdapter()
   }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
	   
	   var originalglcodeid = data.id;
	   var originaldetailtypeid = $('#subLedgerType').val();
	   var originaldetailkeyid = $("#detailkeyId").val();
	   var flag = false;
	   $('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function(index) {
		   var glcodeid =document.getElementById('tempDebitDetails['+index+'].glcodeid').value;
		   var detailtypeid = document.getElementById('tempDebitDetails['+index+'].detailTypeId').value;
		   var detailkeyid = document.getElementById('tempDebitDetails['+index+'].detailKeyId').value;
			if( glcodeid!= "" && originalglcodeid == glcodeid &&  originaldetailtypeid == detailtypeid && originaldetailkeyid == detailkeyid) {
				flag = true;
			}
	   });
	    if(data.issubledger && originaldetailtypeid!='' && originaldetailkeyid=='')
	    {
		   bootbox.alert($.i18n.prop('msg.please.enter',subLedgerDisplayName),function() {
			    var index= dt.length - 1;
			    if(document.getElementById('tempDebitDetails['+index+'].debitGlcode'))
			    	document.getElementById('tempDebitDetails['+index+'].debitGlcode').value = "";
			});
	    }else if(flag){
			bootbox.alert($.i18n.prop('msg.debit.code.already.added'), function() {
				var index= dt.length - 1;
				if(document.getElementById('tempDebitDetails['+index+'].debitGlcode'))
					document.getElementById('tempDebitDetails['+index+'].debitGlcode').value = "";
			});
		}else{
			$(this).parents("tr:first").find('.debitdetailname').val(data.name);
		   	$(this).parents("tr:first").find('.debitaccountcode').val(data.glcode);
		   	$(this).parents("tr:first").find('.debitdetailid').val(data.id);
		   	$(this).parents("tr:first").find('.debitIsSubLedger').val(data.issubledger);
			$(this).parents("tr:first").find('.debitDetailTypeId').val($('#subLedgerType').val());
			$(this).parents("tr:first").find('.debitDetailKeyId').val($('#detailkeyId').val());
			$(this).parents("tr:first").find('.debitDetailTypeName').val(detailTypeName);
			$(this).parents("tr:first").find('.debitDetailKeyName').val(detailKeyName);
		}
   });
}

function creditGlcode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
			   url: '/services/EGF/common/getAllaccountcodes?glcode=',
	            dataType: "json",
	            replace: function (url, query) {
					var subLedgerType = $('#subLedgerType').val();
					if(subLedgerType == null || subLedgerType == "")
						subLedgerType = "0";
					return url + query + '&accountDetailType=' + 0 ;
				},
	            filter: function (data) {
	            	var responseObj = JSON.parse(data);
	                return $.map(responseObj, function (ct) {
	                    return {
	                        id: ct.id,
	                        name: ct.name,
	                        glcode: ct.glcode,
	                        issubledger: ct.isSubLedger,
	                        glcodesearch: ct.glcode+' ~ '+ct.name
	                    };
	                });
	            }
	        }
  });

  custom.initialize();

  var dt=$('.creditGlcode').typeahead({
  	hint : true,
		highlight : true,
		minLength : 1
		
	}, {		    
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
  }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
	  
	  var originalglcodeid = data.id;
	   var originaldetailtypeid = $('#subLedgerType').val();
	   var originaldetailkeyid = $("#detailkeyId").val();
	   var flag = false;
	   $('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function(index) {
		   var glcodeid =document.getElementById('tempCreditDetails['+index+'].glcodeid').value;
		   var detailtypeid = document.getElementById('tempCreditDetails['+index+'].detailTypeId').value;
		   var detailkeyid = document.getElementById('tempCreditDetails['+index+'].detailKeyId').value;
			if( glcodeid!= "" && originalglcodeid == glcodeid &&  originaldetailtypeid == detailtypeid && originaldetailkeyid == detailkeyid) {
				flag = true;
			}
	   });
	   if(data.issubledger && originaldetailtypeid!='' && originaldetailkeyid=='')
	    {
		   bootbox.alert($.i18n.prop('msg.please.enter',subLedgerDisplayName),function() {
			   var index= dt.length - 1;
				document.getElementById('tempCreditDetails['+index+'].creditGlcode').value = "";
			});
	    }else if(flag){
			bootbox.alert($.i18n.prop('msg.credit.code.already.added'), function() {
				var index= dt.length - 1;
				document.getElementById('tempCreditDetails['+index+'].creditGlcode').value = "";
			});
		}else{
			$(this).parents("tr:first").find('.creditdetailname').val(data.name);
		  	$(this).parents("tr:first").find('.creditdetailid').val(data.id);    
		  	$(this).parents("tr:first").find('.creditIsSubLedger').val(data.issubledger);
		  	$(this).parents("tr:first").find('.creditaccountcode').val(data.glcode);
		  	$(this).parents("tr:first").find('.creditDetailTypeId').val($('#subLedgerType').val());
			$(this).parents("tr:first").find('.creditDetailKeyId').val($('#detailkeyId').val());
			$(this).parents("tr:first").find('.creditDetailTypeName').val(detailTypeName);
			$(this).parents("tr:first").find('.creditDetailKeyName').val(detailKeyName);
		}
  });
}

function subledger_initialize(dataId){
	var index=dataId;
	var entityName = new Bloodhound({
		datumTokenizer : function(datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer : Bloodhound.tokenizers.whitespace,
		remote : {
			url : '/services/EGF/common/getentitesbyaccountdetailtype?name=',
			replace: function (url, query) {
				var subLedgerType =document.getElementById('tempSubLedger['+index+'].subLedgerType').value;
			//	var subLedgerType = $('.subledgerGl_code').val();
				if(subLedgerType == null || subLedgerType == "")
					bootbox.alert($.i18n.prop('msg.select.subledger.type'));
				return url + query + '&accountDetailType=' + subLedgerType ;
			},
			filter : function(data) {
				return $.map(data, function(ct) {
					return {
						code:ct.split("~")[0].split("-")[0],
						name : ct.split("~")[0].split("-")[1],
						id : ct.split("~")[1],
						codeAndName:ct
					};
				});
			}
		}
	});

	entityName.initialize();
$('.subLedgerCodeOT').typeahead({
		hint : true,
		highlight : true,
		minLength : 1
	}, {
		displayKey : 'codeAndName',
		source : entityName.ttAdapter()
	}).on('typeahead:selected', function (event, data) {
				   

		document.getElementById('tempSubLedger['+index+'].subLedgerCode').value=data.code;
			detailKeyName = data.name;
			document.getElementById('tempSubLedger['+index+'].payTo').value=data.name;
			document.getElementById('tempSubLedger['+index+'].detailkeyId').value=data.id;
			if(document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value==""){
				document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value=document.getElementById('tempSubLedger['+index+'].detailkeyId').value;
				document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyName').value=detailKeyName;
			}
				
		
	});



$('.subledgerGl_code').change(function () {
	

	var index=dataId;
	var subLedgerType =document.getElementById('tempSubLedger['+index+'].subLedgerType').value;
	var sel=document.getElementById('tempSubLedger['+index+'].subLedgerType');
	var subLedgerTypetext= sel.options[sel.selectedIndex].text;
//	$('.subledgerGl_code').val("");
//	$("#payTo").val("");
//	$("#detailkeyId").val("");
	
	
	if($('.subledgerGl_code').val() != ""){
	
		subLedgerDisplayName=subLedgerTypetext + " Code";
		detailTypeName=subLedgerTypetext;
		$("#subLedgerCodeSection").removeClass("display-hide");
		$('#subLedgerNameLabel').html(subLedgerTypetext + " Code");
		
		//clearAllDetails();
	}
	else{
	
		$("#subLedgerCodeSection").addClass("display-hide");
		$('#subLedgerNameLabel').html("Code");
		
	}
	
	document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value="";
	

	var netPayableAccountCodeNode=document.getElementById('tempSubLedger['+index+'].netPayableAccountCode');
	netPayableAccountCodeNode.options.length = 0;
	netPayableAccountCodeNode.options[netPayableAccountCodeNode.options.length] = new Option('Select from below','')
		
		 $('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function(index) {
						var selected="";
			
			 var glcodeid =document.getElementById('tempCreditDetails['+index+'].glcodeid').value;
			 var name =document.getElementById('tempCreditDetails['+index+'].creditAccountHead').value;
			 var issubledger =document.getElementById('tempCreditDetails['+index+'].isSubLedger').value;
			 var glcode =document.getElementById('tempCreditDetails['+index+'].glcode').value;
			 
			 if(glcodeid!='' && glcode!='' && name!='' && issubledger!='false'){
				 
			netPayableAccountCodeNode.options[netPayableAccountCodeNode.options.length] = new Option(glcode + '-' +name + '~' + issubledger,  glcodeid);
			
		 }
				});
		
});



$('#billSubType').change(function () {
	
	worksDiv();
	$("#selectedCheckList").val("");
	loadCheckListTable();
});

	



$('.netPayableAccount_Code').change(function () {
	var index=dataId;
	
	if (document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value != '') {
		var sel=document.getElementById('tempSubLedger['+index+'].netPayableAccountCode');
		var netpayGlcode=sel.options[sel.selectedIndex].text;
		document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeId').value=document.getElementById('tempSubLedger['+index+'].subLedgerType').value;
		document.getElementById('tempSubLedger['+index+'].netPayableIsSubLedger').value=netpayGlcode.split('~')[1];
		document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value=document.getElementById('tempSubLedger['+index+'].detailkeyId').value;
		document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeName').value=detailTypeName;
		document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyName').value=detailKeyName;
		document.getElementById('tempSubLedger['+index+'].netPayableGlcode').value=netpayGlcode.substr(0,netpayGlcode.indexOf('-'));
		document.getElementById('tempSubLedger['+index+'].netPayableAccountHead').value=netpayGlcode.substr(netpayGlcode.indexOf('-')+1);
		
	}
});



}

//only on change
$('.subledgerGl_code').change(function () {
	var index=0;
	var subLedgerType =document.getElementById('tempSubLedger['+index+'].subLedgerType').value;
	
	
	var sel=document.getElementById('tempSubLedger['+index+'].subLedgerType');
	var subLedgerTypetext= sel.options[sel.selectedIndex].text;
//	$('.subledgerGl_code').val("");
//	$("#payTo").val("");
//	$("#detailkeyId").val("");
	
	
	if($('.subledgerGl_code').val() != ""){
	
		subLedgerDisplayName=subLedgerTypetext + " Code";
		detailTypeName=subLedgerTypetext;
		$("#subLedgerCodeSection").removeClass("display-hide");
		$('#subLedgerNameLabel').html(subLedgerTypetext + " Code");
		
		//clearAllDetails();
	}
	else{
	
		$("#subLedgerCodeSection").addClass("display-hide");
		$('#subLedgerNameLabel').html("Code");
		
	}
	
		
					document.getElementById('tempSubLedger[0].netPayableAccountCode').value="";
					
				
					var netPayableAccountCodeNode=document.getElementById('tempSubLedger[0].netPayableAccountCode');
					netPayableAccountCodeNode.options.length = 0;
					netPayableAccountCodeNode.options[netPayableAccountCodeNode.options.length] = new Option('Select from below','')
					
					 $('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function(index) {
						var selected="";
						
						 var glcodeid =document.getElementById('tempCreditDetails['+index+'].glcodeid').value;
						 var name =document.getElementById('tempCreditDetails['+index+'].creditAccountHead').value;
						 var issubledger =document.getElementById('tempCreditDetails['+index+'].isSubLedger').value;
						 var glcode =document.getElementById('tempCreditDetails['+index+'].glcode').value;
						 if(glcodeid!='' && glcode!='' && name!='' && issubledger!='false'){
							 
						netPayableAccountCodeNode.options[netPayableAccountCodeNode.options.length] = new Option(glcode + '-' +name + '~' + issubledger,  glcodeid);
					
							 }
					});

		
});

$('#billSubType').change(function () {
	
	worksDiv();
	
	$("#selectedCheckList").val("");
	loadCheckListTable();
});

function loadCheckListTable(){
	if($('#billSubType').val()!=""){
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getchecklistbybillsubtype",
			data : {
				billSubType : $('#billSubType').val()
			},
			async : true
		}).done(
				function(response) {
					$.each(response, function(index, value) {
						$('#tblchecklist tbody').empty();
						var output = '';
						$.each(response, function(index, value) {
							var selected = "";
							var selectedCheckValue = "";
							if($("#selectedCheckList").val()){
							var selectedCheckList = $("#selectedCheckList").val();
							var selectedCheckListArray= selectedCheckList.split(',');
								for (var i in selectedCheckListArray) {
									if(value.id == selectedCheckListArray[i].split('-')[0]){
										selected = "selected";
										selectedCheckValue = selectedCheckListArray[i].split('-')[1];
									}
								}
							}
							output = '<tr id="tblchecklistrow">';
							output = output + '<td class="text-left">' + value.value + '</td>'
							output = output + '<td class="text-right">'
							output = output + '<input id="checkLists['+index+'].appconfigvalue.id" name="checkLists['+index+'].appconfigvalue.id" type="hidden" value="'+ value.id +'"/>'
							output = output + '<select id="checkLists['+index+'].checklistvalue" name="checkLists['+index+'].checklistvalue" data-first-option="false" class="form-control">'
							if(selected!="" && selectedCheckValue == "na")
								output = output + '<option value="na" selected = "selected">N/A</option>'
							else
								output = output + '<option value="na">N/A</option>'
							if(selected!="" && selectedCheckValue == "Yes")
								output = output + '<option value="Yes" selected = "selected">Yes</option>'
							else
								output = output + '<option value="Yes">Yes</option>'
							if(selected!="" && selectedCheckValue == "No")
								output = output + '<option value="No" selected = "selected">No</option>'
							else
								output = output + '<option value="No">No</option>'
							
							output = output + '</select>'
							output = output + '</td>'
							output = output + '</tr>';
							$('#tblchecklist tbody').append(output);
					});
				});
			
		});
	}
}

$('.netPayableAccount_Code').change(function () {
	var index=0;
	
	if (document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value != '') {
		var sel=document.getElementById('tempSubLedger['+index+'].netPayableAccountCode');
		var netpayGlcode=sel.options[sel.selectedIndex].text;
		
		document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeId').value=document.getElementById('tempSubLedger['+index+'].subLedgerType').value;
		document.getElementById('tempSubLedger['+index+'].netPayableIsSubLedger').value=netpayGlcode.split('~')[1];
		document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value=document.getElementById('tempSubLedger['+index+'].detailkeyId').value;
		document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeName').value=detailTypeName;
		document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyName').value=detailKeyName;
		document.getElementById('tempSubLedger['+index+'].netPayableGlcode').value=netpayGlcode.substr(0,netpayGlcode.indexOf('-'));
		document.getElementById('tempSubLedger['+index+'].netPayableAccountHead').value=netpayGlcode.substr(netpayGlcode.indexOf('-')+1);
	}
});



function addSubledgerRow(x) { 
	var dataId = $(x).attr("data-idx");

	
  if (dataId==undefined)
  {
  	dataId=1;
  }
   else{
   	dataId++;
   }  
	
     
     
	$('.subLedgerCodeOT').typeahead('destroy');
	$('.subLedgerCodeOT').unbind();
	var rowcount = $("#tblSubledgerAdd tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('subledgerdetailsrow') != null) {
			addRow('tblSubledgerAdd','subledgerdetailsrow');
			$('#tblSubledgerAdd tbody tr:eq('+rowcount+')').find('.subledgerGlType').val('');
			$('#tblSubledgerAdd tbody tr:eq('+rowcount+')').find('.subledger_code').val('');
			$('#tblSubledgerAdd tbody tr:eq('+rowcount+')').find('.netPayableAccount_Code').val('');
			$('#tblSubledgerAdd tbody tr:eq('+rowcount+')').find('.subledger_Payto').val('');
			$('#tblSubledgerAdd tbody tr:eq('+rowcount+')').find('.netPayable_Amount').val('');
			
			//$('#tblsubledgerdetails tbody tr:eq('+rowcount+')').blur(calcualteNetpaybleAmount);
			++subledgerrowcount;
			subledger_initialize(dataId);
		 	//addSubledger();
			
			//addCustomEvent(rowcount,'tempDebitDetails[index].debitamount','keydown',shortKeyFunForAltIComb);
			addCustomEvent(rowcount,'tempSubLedger[index].addButton','keydown',shortKeyFunForAddButton);
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteSubledgerRow(obj) {
	var rowcount=$("#tblSubledgerAdd tbody tr").length;
    if(rowcount<=1) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'tblSubledgerAdd');
		--subledgerrowcount;
		return true;
	}
    
   // resetDebitCodes();
}


function addDebitDetailsRow() { 
	
	$('.debitGlcode').typeahead('destroy');
	$('.debitGlcode').unbind();
	var rowcount = $("#tbldebitdetails tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('debitdetailsrow') != null) {
			addRow('tbldebitdetails','debitdetailsrow');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitDetailGlcode').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitdetailname').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitAmount').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').blur(calcualteNetpaybleAmount);
			debitGlcode_initialize();
			++debitAmountrowcount;
			addCustomEvent(rowcount,'tempDebitDetails[index].debitamount','keydown',shortKeyFunForAltIComb);
			addCustomEvent(rowcount,'tempDebitDetails[index].addButton','keydown',shortKeyFunForAddButton);
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}


function deleteDebitDetailsRow(obj) {
	var rowcount=$("#tbldebitdetails tbody tr").length;
    if(rowcount<=1) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'tbldebitdetails');
		--debitAmountrowcount;
		return true;
	}
    
    resetDebitCodes();
}

function addCreditDetailsRow() { 
	
	$('.creditGlcode').typeahead('destroy');
	$('.creditGlcode').unbind();
	var rowcount = $("#tblcreditdetails tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('creditdetailsrow') != null) {
			addRow('tblcreditdetails','creditdetailsrow');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditDetailGlcode').val('');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditdetailname').val('');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditAmount').val('');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditAmount').blur(calcualteNetpaybleAmount);
			creditGlcode_initialize();
			++creditAmoutrowcount;
			addCustomEvent(rowcount,'tempCreditDetails[index].creditamount','keydown',shortKeyFunForAltIComb);
			addCustomEvent(rowcount,'tempCreditDetails[index].addButton','keydown',shortKeyFunForAddButton);
		}
	} else {
		bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteCreditDetailsRow(obj) {
	var rowcount=$("#tblcreditdetails tbody tr").length;
    if(rowcount<=1) {
    	bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'tblcreditdetails');
		--creditAmoutrowcount;
		return true;
	}	
}

function deleteAccountDetails(obj) {
	var index = obj.id.split('_')[1];
	var glcodeid = document.getElementById('accountDetailsGlCodeId_'+index).value;
	if($netPayableAccountCodeId && $netPayableAccountCodeId==glcodeid)
	{
			$netPayableAccountCodeId=0;
			netpayableamount = 0;
	}
	var rowcount=$("#tblaccountdetails tbody tr").length;
    if(rowcount<=1) {
    	$('#accountdetailsrow').prop("hidden","true");
		$('#accountdetailsrow').prop('accountdetailsinvisible',"true");
		$('#accountDetailsGlCodeId_0').val("");    
	    $('.accountDetailsGlCode_0').html("");
		$('.accountDetailsAccountHead_0').html("");
		$('.accountDetailsDebitAmount_0').html("");
		$('.accountDetailsCreditAmount_0').html("");
		$('#accountDetailsDebitAmount_0').val("");
		$('#accountDetailsCreditAmount_0').val("");
		$('.accountDetailsDebitDetailTypeId_0').val("");
		$('.accountDetailsDebitDetailKeyId_0').val("");
		deleteSubLedgerDetails(glcodeid);
		calculateBillAmount();
	} else {
		deleteSubLedgerDetails(glcodeid);
		deleteRow(obj,'tblaccountdetails');
		calculateBillAmount();
		return true;
	}	
}

function deleteSubLedgerDetails(glcodeid) {
	$('#tblsubledgerdetails  > tbody > tr:visible[id="subledhgerrow"]').each(function() {
		if($(this).find(".subLedgerGlCodeIdId").val()==glcodeid){
			var rowcount=$("#tblsubledgerdetails tbody tr").length;
		    if(rowcount<=1) {
		    	$('#subledhgerrow').prop("hidden","true");
		    	$('#subledhgerrow').prop('subledgerinvisible',"true");
		    	$('#subLedgerGlCodeId_0').val("");  
		    	$('#subLedgerIsDebit_0').val("");
		    	$('#subLedgerDebitAmount_0').val("");
		    	$('#subLedgerCreditAmount_0').val("");
		        $('.subLedgerGlCode_0').html("");
		        $('#subLedgerDetailTypeId_0').val("");
		    	$('#subLedgerDetailKeyId_0').val("");
		    	$('.subLedgerType_0').html("");
		    	$('.subLedgerName_0').html("");
		    	$('.subLedgerAmount_0').html("");
			}else{
				deleteRow($(this).find(".subLedgerAmount")[0],'tblsubledgerdetails');
			}
		}
			
	});
}


function validateAccountDetails()
{	
	var flag = true;
	
	$('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function(index) {
		if($(this).find(".debitdetailid").val()!='' && $(this).find(".debitAmount").val()=='')
		{
			bootbox.alert($.i18n.prop('msg.please.enter.debit.amount',(index+1)));
			flag = false;
			return false;
		}
		/*if(!validateDuplicateAccountDetail($(this).find(".debitdetailid").val(),$(this).find(".debitDetailTypeId").val(),
				$(this).find(".debitDetailKeyId").val())){
			bootbox.alert($.i18n.prop('msg.already.added.debit.detail', $(this).find(".debitaccountcode").val()));
			flag = false;
			return false;
		}*/
	});
	
	$('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function(index) {
		if($(this).find(".creditdetailid").val()!='' && $(this).find(".creditAmount").val()=='')
		{
			bootbox.alert($.i18n.prop('msg.please.enter.credit.amount',(index+1)));
			flag = false;
			return false;
		}
		
		/*if(!validateDuplicateAccountDetail($(this).find(".creditdetailid").val(),$(this).find(".creditDetailTypeId").val(),
				$(this).find(".creditDetailKeyId").val())){
			bootbox.alert($.i18n.prop('msg.already.added.credit.detail', $(this).find(".creditaccountcode").val()));
			flag = false;
			return false;
		}*/
	});
	
	
	/*$('#tblSubledgerAdd  > tbody > tr:visible[id="subledgerdetailsrow"]').each(function(index) {
		if(document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value!='' && $netPayableAccountCodeId){
			bootbox.alert($.i18n.prop('msg.only.one.net.payable.account.can.select'));
			flag = false;
			return false
		}
		if(document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value!='' && document.getElementById('tempSubLedger['+index+'].netPayableAmount').value=='')
		{
			bootbox.alert($.i18n.prop('msg.please.enter.net.apyable.amount'));
			flag = false;
			return false
		}
	});*/
	
	
	return flag;
}
function validateDuplicateAccountDetail(glcodeid,detailtypeid,detailkeyid){
	var existInSL = false;
	var flag= true;
	$('#tblsubledgerdetails  > tbody > tr:visible[id="subledhgerrow"]').each(function() {
		if(glcodeid!="" && $(this).find(".subLedgerGlCodeIdId").val()==glcodeid){
			existInSL = true;
		}
			
		if (glcodeid!="" && $(this).find(".subLedgerGlCodeIdId").val() ==glcodeid && $(this).find(".subLedgerDetailTypeId").val()==detailtypeid && $(this).find(".subLedgerDetailKeyId").val()==detailkeyid)
		{
			flag = false;
			return false
		}
	});
	if(!existInSL){
		$('#tblaccountdetails  > tbody > tr:visible[id="accountdetailsrow"]').each(function() {
			if( glcodeid!="" && $(this).find(".accountDetailsGlCodeId").val() == glcodeid)
			{
				flag = false;
				return false
			}
		});
	}
	return flag;
}
function addSubledger(){
	
	var isMerged= false;
	var accountDetailsCount = $("#tblaccountdetails > tbody > tr:visible[id='accountdetailsrow']").length;
	if(validateAccountDetails())
		{
		$('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function() {
			 if($(this).find(".debitdetailid").val()!='' && $(this).find(".debitAmount").val()!='')
				{
				 isMerged= false;
				 var glcodeid = $(this).find(".debitdetailid").val();
				 var currentdebitamount = $(this).find(".debitAmount").val();
					if(accountDetailsCount!=0){
						$('#tblaccountdetails  > tbody > tr:visible[id="accountdetailsrow"]').each(function() {
							if($(this).find(".accountDetailsGlCodeId").val() == glcodeid)
							{
								$(this).find(".debitamount").val(parseFloat(Number($(this).find(".debitamount").val())+Number(currentdebitamount)).toFixed());
								$(this).find(".accountDetailsDebitAmount").html($(this).find(".debitamount").val());
								isMerged = true;
							}
						});
						if(!isMerged){
							addRow('tblaccountdetails', 'accountdetailsrow');
						}
					}
					if(!isMerged){
						$('#accountdetailsrow').removeAttr("hidden");
						$('#accountdetailsrow').removeAttr('accountdetailsinvisible');
						$('#accountDetailsGlCodeId_' + accountDetailsCount).val($(this).find(".debitdetailid").val());    
					    $('.accountDetailsGlCode_' + accountDetailsCount).html($(this).find(".debitaccountcode").val());
						$('.accountDetailsAccountHead_' + accountDetailsCount).html($(this).find(".debitdetailname").val());
						$('.accountDetailsDebitAmount_' + accountDetailsCount).html(Number($(this).find(".debitAmount").val()).toFixed(2));
						$('.accountDetailsCreditAmount_' + accountDetailsCount).html('0.00');
						$('#accountDetailsDebitAmount_' + accountDetailsCount).val(Number($(this).find(".debitAmount").val()).toFixed(2));
						$('#accountDetailsCreditAmount_' + accountDetailsCount).val('0.00');
						$('.accountDetailsDebitDetailTypeId_' + accountDetailsCount).val($(this).find(".debitDetailTypeId").val());
						$('.accountDetailsDebitDetailKeyId_' + accountDetailsCount).val($(this).find(".debitDetailKeyId").val());
						accountDetailsCount++;
					}
					if($(this).find(".debitIsSubLedger").val()!="false" && $(this).find(".debitDetailTypeId").val()!="" && $(this).find(".debitDetailKeyId").val()!=""){
						populateSubLedgerDetails($(this).find(".debitdetailid").val(),$(this).find(".debitaccountcode").val(),
								$(this).find(".debitDetailTypeId").val(),$(this).find(".debitDetailKeyId").val(),
								$(this).find(".debitDetailTypeName").val(),$(this).find(".debitDetailKeyName").val(),
								$(this).find(".debitAmount").val(),true);
					}
			 }
		 });
		$('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function() {
			if($(this).find(".creditdetailid").val()!='' && $(this).find(".creditAmount").val()!='')
			{
				isMerged= false;
				var glcodeid = $(this).find(".creditdetailid").val();
				var currentcreditamount = $(this).find(".creditAmount").val();
				if(accountDetailsCount!=0){
					$('#tblaccountdetails  > tbody > tr:visible[id="accountdetailsrow"]').each(function() {
						if($(this).find(".accountDetailsGlCodeId").val() == glcodeid)
						{
							$(this).find(".creditamount").val(parseFloat(Number($(this).find(".creditamount").val())+Number(currentcreditamount)).toFixed());
							$(this).find(".accountDetailsCreditAmount").html($(this).find(".creditamount").val());
							isMerged = true;
						}
					});
					if(!isMerged){
						addRow('tblaccountdetails', 'accountdetailsrow');
					}
				}
				if(!isMerged){
					$('#accountdetailsrow').removeAttr("hidden");
					$('#accountdetailsrow').removeAttr('accountdetailsinvisible');
					$('#accountDetailsGlCodeId_' + accountDetailsCount).val($(this).find(".creditdetailid").val());    
				    $('.accountDetailsGlCode_' + accountDetailsCount).html($(this).find(".creditaccountcode").val());
					$('.accountDetailsAccountHead_' + accountDetailsCount).html($(this).find(".creditdetailname").val());
					$('.accountDetailsDebitAmount_' + accountDetailsCount).html('0.00');
					$('.accountDetailsCreditAmount_' + accountDetailsCount).html(Number($(this).find(".creditAmount").val()).toFixed(2));
					$('#accountDetailsDebitAmount_' + accountDetailsCount).val('0.00');
					$('#accountDetailsCreditAmount_' + accountDetailsCount).val(Number($(this).find(".creditAmount").val()).toFixed(2));
					$('.accountDetailsDebitDetailTypeId_' + accountDetailsCount).val($(this).find(".creditDetailTypeId").val());
					$('.accountDetailsDebitDetailKeyId_' + accountDetailsCount).val($(this).find(".creditDetailKeyId").val());
					accountDetailsCount++;
				}
				if($(this).find(".creditIsSubLedger").val()!="false"  && $(this).find(".creditDetailTypeId").val()!="" && $(this).find(".creditDetailKeyId").val()!=""){
					populateSubLedgerDetails($(this).find(".creditdetailid").val(),$(this).find(".creditaccountcode").val(),
							$(this).find(".creditDetailTypeId").val(),$(this).find(".creditDetailKeyId").val(),
							$(this).find(".creditDetailTypeName").val(),$(this).find(".creditDetailKeyName").val(),
							$(this).find(".creditAmount").val(),false);
				}
			}
		 });
		
		$('#tblSubledgerAdd  > tbody > tr:visible[id="subledgerdetailsrow"]').each(function() {
			if($(this).find("#netPayableAccountCode").val()!='' && $(this).find("#netPayableAmount").val()!='')
			{
				flag= false;
				if(accountDetailsCount!=0){
					addRow('tblaccountdetails', 'accountdetailsrow');
				}
				netpayableamount =  parseFloat(Number($(this).find("#expense-netPayableAmount").val())).toFixed(2);
				$('#accountdetailsrow').removeAttr("hidden");
				$('#accountdetailsrow').removeAttr('accountdetailsinvisible');
				$('#accountDetailsGlCodeId_' + accountDetailsCount).val($(this).find("#netPayableAccountCode").val());    
			    $('.accountDetailsGlCode_' + accountDetailsCount).html($(this).find("#netPayableGlcode").val());
				$('.accountDetailsAccountHead_' + accountDetailsCount).html($(this).find("#netPayableAccountHead").val().split("~")[0]);
				$('.accountDetailsDebitAmount_' + accountDetailsCount).html('0.00');
				$('.accountDetailsCreditAmount_' + accountDetailsCount).html(Number($(this).find("#expense-netPayableAmount").val()).toFixed(2));
				$('#accountDetailsDebitAmount_' + accountDetailsCount).val('0.00');
				$('#accountDetailsCreditAmount_' + accountDetailsCount).val(Number($(this).find("#expense-netPayableAmount").val()).toFixed(2));
				$('.accountDetailsDebitDetailTypeId_' + accountDetailsCount).val($(this).find("#netPayableDetailTypeId").val());
				$('.accountDetailsDebitDetailKeyId_' + accountDetailsCount).val($(this).find("#netPayableDetailKeyId").val());
				$netPayableAccountCodeId = $(this).find("#netPayableAccountCode").val();
				$("#netPayableId").val($(this).find("#netPayableAccountCode").val());
				accountDetailsCount++;
				if( $(this).find("#netPayableDetailTypeId").val()!="" && $(this).find("#netPayableDetailKeyId").val()!=""){
					populateSubLedgerDetails($(this).find("#netPayableAccountCode").val(),$(this).find("#netPayableGlcode").val(),
							$(this).find("#netPayableDetailTypeId").val(),$(this).find("#netPayableDetailKeyId").val(),
							$(this).find("#netPayableDetailTypeName").val(),$(this).find("#netPayableDetailKeyName").val(),
							$(this).find("#expense-netPayableAmount").val(),false);
				}
			}
		 });
		//clearAllDetails();
	}
	calculateBillAmount();

}
	

$("#populateAccountDetails").click(function () {
	var isMerged= false;
	var accountDetailsCount = $("#tblaccountdetails > tbody > tr:visible[id='accountdetailsrow']").length;
	if(validateAccountDetails())
		{
		$('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function() {
			 if($(this).find(".debitdetailid").val()!='' && $(this).find(".debitAmount").val()!='')
				{
				 isMerged= false;
				 var glcodeid = $(this).find(".debitdetailid").val();
				 var currentdebitamount = $(this).find(".debitAmount").val();
					if(accountDetailsCount!=0){
						$('#tblaccountdetails  > tbody > tr:visible[id="accountdetailsrow"]').each(function() {
							if($(this).find(".accountDetailsGlCodeId").val() == glcodeid)
							{
								$(this).find(".debitamount").val(parseFloat(Number($(this).find(".debitamount").val())+Number(currentdebitamount)).toFixed());
								$(this).find(".accountDetailsDebitAmount").html($(this).find(".debitamount").val());
								isMerged = true;
							}
						});
						if(!isMerged){
							addRow('tblaccountdetails', 'accountdetailsrow');
						}
					}
					if(!isMerged){
						$('#accountdetailsrow').removeAttr("hidden");
						$('#accountdetailsrow').removeAttr('accountdetailsinvisible');
						$('#accountDetailsGlCodeId_' + accountDetailsCount).val($(this).find(".debitdetailid").val());    
					    $('.accountDetailsGlCode_' + accountDetailsCount).html($(this).find(".debitaccountcode").val());
						$('.accountDetailsAccountHead_' + accountDetailsCount).html($(this).find(".debitdetailname").val());
						$('.accountDetailsDebitAmount_' + accountDetailsCount).html(Number($(this).find(".debitAmount").val()).toFixed(2));
						$('.accountDetailsCreditAmount_' + accountDetailsCount).html('0.00');
						$('#accountDetailsDebitAmount_' + accountDetailsCount).val(Number($(this).find(".debitAmount").val()).toFixed(2));
						$('#accountDetailsCreditAmount_' + accountDetailsCount).val('0.00');
						$('.accountDetailsDebitDetailTypeId_' + accountDetailsCount).val($(this).find(".debitDetailTypeId").val());
						$('.accountDetailsDebitDetailKeyId_' + accountDetailsCount).val($(this).find(".debitDetailKeyId").val());
						accountDetailsCount++;
					}
					if($(this).find(".debitIsSubLedger").val()!="false" && $(this).find(".debitDetailTypeId").val()!="" && $(this).find(".debitDetailKeyId").val()!=""){
						populateSubLedgerDetails($(this).find(".debitdetailid").val(),$(this).find(".debitaccountcode").val(),
								$(this).find(".debitDetailTypeId").val(),$(this).find(".debitDetailKeyId").val(),
								$(this).find(".debitDetailTypeName").val(),$(this).find(".debitDetailKeyName").val(),
								$(this).find(".debitAmount").val(),true);
					}
			 }
		 });
		$('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function() {
			if($(this).find(".creditdetailid").val()!='' && $(this).find(".creditAmount").val()!='')
			{
				isMerged= false;
				var glcodeid = $(this).find(".creditdetailid").val();
				var currentcreditamount = $(this).find(".creditAmount").val();
				if(accountDetailsCount!=0){
					$('#tblaccountdetails  > tbody > tr:visible[id="accountdetailsrow"]').each(function() {
						if($(this).find(".accountDetailsGlCodeId").val() == glcodeid)
						{
							$(this).find(".creditamount").val(parseFloat(Number($(this).find(".creditamount").val())+Number(currentcreditamount)).toFixed());
							$(this).find(".accountDetailsCreditAmount").html($(this).find(".creditamount").val());
							isMerged = true;
						}
					});
					if(!isMerged){
						addRow('tblaccountdetails', 'accountdetailsrow');
					}
				}
				if(!isMerged){
					$('#accountdetailsrow').removeAttr("hidden");
					$('#accountdetailsrow').removeAttr('accountdetailsinvisible');
					$('#accountDetailsGlCodeId_' + accountDetailsCount).val($(this).find(".creditdetailid").val());    
				    $('.accountDetailsGlCode_' + accountDetailsCount).html($(this).find(".creditaccountcode").val());
					$('.accountDetailsAccountHead_' + accountDetailsCount).html($(this).find(".creditdetailname").val());
					$('.accountDetailsDebitAmount_' + accountDetailsCount).html('0.00');
					$('.accountDetailsCreditAmount_' + accountDetailsCount).html(Number($(this).find(".creditAmount").val()).toFixed(2));
					$('#accountDetailsDebitAmount_' + accountDetailsCount).val('0.00');
					$('#accountDetailsCreditAmount_' + accountDetailsCount).val(Number($(this).find(".creditAmount").val()).toFixed(2));
					$('.accountDetailsDebitDetailTypeId_' + accountDetailsCount).val($(this).find(".creditDetailTypeId").val());
					$('.accountDetailsDebitDetailKeyId_' + accountDetailsCount).val($(this).find(".creditDetailKeyId").val());
					accountDetailsCount++;
				}
				if($(this).find(".creditIsSubLedger").val()!="false"  && $(this).find(".creditDetailTypeId").val()!="" && $(this).find(".creditDetailKeyId").val()!=""){
					populateSubLedgerDetails($(this).find(".creditdetailid").val(),$(this).find(".creditaccountcode").val(),
							$(this).find(".creditDetailTypeId").val(),$(this).find(".creditDetailKeyId").val(),
							$(this).find(".creditDetailTypeName").val(),$(this).find(".creditDetailKeyName").val(),
							$(this).find(".creditAmount").val(),false);
				}
			}
		 });
		
		$('#tblSubledgerAdd  > tbody > tr:visible[id="subledgerdetailsrow"]').each(function(index) {
			if(document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value!='' && document.getElementById('tempSubLedger['+index+'].expense-netPayableAmount').value!='')
			{
				flag= false;
			//	if(accountDetailsCount!=0){
				//	addRow('tblaccountdetails', 'accountdetailsrow');
				//}
				netpayableamount =  parseFloat(Number(document.getElementById('tempSubLedger['+index+'].expense-netPayableAmount').value)).toFixed(2);
				$('#accountdetailsrow').removeAttr("hidden");
				$('#accountdetailsrow').removeAttr('accountdetailsinvisible');
				$('#accountDetailsGlCodeId_' + accountDetailsCount).val(document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value);    
			    $('.accountDetailsGlCode_' + accountDetailsCount).html(document.getElementById('tempSubLedger['+index+'].netPayableGlcode').value);
				$('.accountDetailsAccountHead_' + accountDetailsCount).html(document.getElementById('tempSubLedger['+index+'].netPayableAccountHead').value.split("~")[0]);
				$('.accountDetailsDebitAmount_' + accountDetailsCount).html('0.00');
				$('.accountDetailsCreditAmount_' + accountDetailsCount).html(Number(document.getElementById('tempSubLedger['+index+'].expense-netPayableAmount').value).toFixed(2));
				$('#accountDetailsDebitAmount_' + accountDetailsCount).val('0.00');
				$('#accountDetailsCreditAmount_' + accountDetailsCount).val(Number(document.getElementById('tempSubLedger['+index+'].expense-netPayableAmount').value).toFixed(2));
				$('.accountDetailsDebitDetailTypeId_' + accountDetailsCount).val(document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeId').value);
				$('.accountDetailsDebitDetailKeyId_' + accountDetailsCount).val(document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value);
				$netPayableAccountCodeId =document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value;
				//where this id
				$("#netPayableId").val(document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value);
				accountDetailsCount++;
				
				if(document.getElementById('tempSubLedger['+index+'].netPayableIsSubLedger').value!="false"  && document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeId').value!="" && document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value!=""){
					populateSubLedgerDetails(document.getElementById('tempSubLedger['+index+'].netPayableAccountCode').value,document.getElementById('tempSubLedger['+index+'].netPayableGlcode').value,
							document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeId').value,document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyId').value,
									document.getElementById('tempSubLedger['+index+'].netPayableDetailTypeName').value,document.getElementById('tempSubLedger['+index+'].netPayableDetailKeyName').value,
											document.getElementById('tempSubLedger['+index+'].expense-netPayableAmount').value,false);
				}
			}
		 });
		clearAllDetails();
	}
	calculateBillAmount();
	
});




addCustomEvent('0','tempDebitDetails[index].debitamount','keydown',shortKeyFunForAltIComb);
addCustomEvent('0','tempCreditDetails[index].creditamount','keydown',shortKeyFunForAltIComb);
addCustomEvent('0','tempDebitDetails[index].addButton','keydown',shortKeyFunForAddButton);
addCustomEvent('0','tempCreditDetails[index].addButton','keydown',shortKeyFunForAddButton);

function shortKeyFunForAltIComb (zEvent) {
	var currId = zEvent.target.id;
    if (currId.endsWith('.debitamount') && zEvent.altKey  &&  zEvent.key === "i") {  // case sensitive
    	zEvent.preventDefault ();
        addDebitDetailsRow();
    }else if (currId.endsWith('.creditamount') && zEvent.altKey  &&  zEvent.key === "i") {  // case sensitive
    	zEvent.preventDefault ();
    	addCreditDetailsRow();
    }
    zEvent.stopPropagation ();
}
function shortKeyFunForAddButton (zEvent) {
	var currId = zEvent.target.id;
	if(currId.startsWith('tempDebitDetails') && zEvent.keyCode == 32){
		zEvent.preventDefault ();
    	addDebitDetailsRow();
    }else if(currId.startsWith('tempCreditDetails') && zEvent.keyCode == 32){
    	zEvent.preventDefault ();
    	addCreditDetailsRow();
    }
//	$('[data-toggle="tooltip"]').tooltip("hide");
    zEvent.stopPropagation ();
}

function addCustomEvent(index,target,type,func){
	target = target.replace('index',index);
	addCustomEventListener(target, type, func);
}

function addCustomEventListener(target,type,func){
	var targArea = document.getElementById (target);
	if(targArea != null){
		targArea.addEventListener(type,  func);	
	}
}


function validate(){
	if(billamount == 0){
		bootbox.alert($.i18n.prop('msg.please.select.account.details'));
		return false;
	}
	
	/*if(debitamount != Number(Number(creditamount) + Number(netpayableamount))){
		bootbox.alert($.i18n.prop('msg.debit.and.credit.amount.is.not.matching'));
		return false;
	}*/
	
	if(debitamount == 0){
		bootbox.alert($.i18n.prop('msg.please.select.atleast.one.debit.details'));
		return false;
	}
	
	if(!$netPayableAccountCodeId)
	{
		bootbox.alert($.i18n.prop('msg.please.select.one.net.payable.account.detail'));
		return false;
	}
	return true;
}

function clearAllDetails() {
	var debitDetailsCount = $("#tbldebitdetails > tbody > tr:visible[id='debitdetailsrow']").length;
	for (var i = debitDetailsCount; i >= 1; i--) {
		if(1 == i){
			document.getElementById('tempDebitDetails[0].debitGlcode').value = "";
			document.getElementById('tempDebitDetails[0].glcode').value = "";
			document.getElementById('tempDebitDetails[0].glcodeid').value = "";
			document.getElementById('tempDebitDetails[0].detailTypeId').value = "";
			document.getElementById('tempDebitDetails[0].detailKeyId').value = "";
			document.getElementById('tempDebitDetails[0].debitAccountHead').value = "";
			document.getElementById('tempDebitDetails[0].debitamount').value = "";
		}else{
			var objects = $('.debit-delete-row');
			deleteRow(objects[i-1],'tbldebitdetails');
		}
	}
	
	var creditDetailsCount = $("#tblcreditdetails > tbody > tr:visible[id='creditdetailsrow']").length;
	for (var i = creditDetailsCount; i >= 1; i--) {
		if(1 == i){
			document.getElementById('tempCreditDetails[0].creditGlcode').value = "";
			document.getElementById('tempCreditDetails[0].glcode').value = "";
			document.getElementById('tempCreditDetails[0].glcodeid').value = "";
			document.getElementById('tempCreditDetails[0].detailTypeId').value = "";
			document.getElementById('tempCreditDetails[0].detailKeyId').value = "";
			document.getElementById('tempCreditDetails[0].creditAccountHead').value = "";
			document.getElementById('tempCreditDetails[0].creditamount').value = "";
		}else{
			var objects = $('.credit-delete-row');
			deleteRow(objects[i-1],'tblcreditdetails');
		}
	}
	
	
	var subledgerCount = $('#tblSubledgerAdd  > tbody > tr:visible[id="subledgerdetailsrow"]').length;
	for (var i = subledgerCount; i >= 1; i--) {
		if(1 == i){
			document.getElementById('tempCreditDetails[0].creditGlcode').value = "";
			document.getElementById('tempSubLedger[0].subLedgerType').value = "";
			document.getElementById('tempSubLedger[0].netPayableAccountCode').value = "";
			document.getElementById('tempSubLedger[0].detailkeyId').value = "";
			document.getElementById('tempSubLedger[0].subLedgerCode').value = "";
			document.getElementById('tempSubLedger[0].payTo').value = "";
			document.getElementById('tempSubLedger[0].expense-netPayableAmount').value = "";
		}else{
			var objects = $('.subledge-delete-row');
			deleteRow(objects[i-1],'tblSubledgerAdd');
		}
	}
	
	document.getElementById('tempSubLedger[0].netPayableAccountId').value = "";
	document.getElementById('tempSubLedger[0].netPayableAccountCodeId').value = "";
	document.getElementById('tempSubLedger[0].netPayableDetailTypeId').value = "";
	document.getElementById('tempSubLedger[0].netPayableDetailKeyId').value = "";
	document.getElementById('tempSubLedger[0].netPayableAccountCode').value = "";
	document.getElementById('tempSubLedger[0].expense-netPayableAmount').value = "";
	
	
	
}


function populateSubLedgerDetails(glCodeId,glCode,detailTypeId,detailKeyId,detailTypeName,detailKeyName,amount,isdebit){
	
	$('.subLedgerGlCode_' + subLedgerCount).html('');
    $('#subLedgerDetailTypeId_' + subLedgerCount).val('');
	$('#subLedgerDetailKeyId_' + subLedgerCount).val('');
	$('.subLedgerType_' + subLedgerCount).html('');
	$('.subLedgerName_' + subLedgerCount).html('');
	$('.subLedgerAmount_' + subLedgerCount).html('');
	$('#subLedgerGlCodeId_' + subLedgerCount).val('');  
	$('#subLedgerIsDebit_' + subLedgerCount).val('');
	$('#subLedgerDebitAmount_' + subLedgerCount).val('');
	$('#subLedgerCreditAmount_' + subLedgerCount).val("0");
	
	var subLedgerCount = $("#tblsubledgerdetails > tbody > tr:visible[id='subledhgerrow']").length;
	if(subLedgerCount!=0){
		addRow('tblsubledgerdetails', 'subledhgerrow');
	}
	$('#subledhgerrow').removeAttr("hidden");
	$('#subledhgerrow').removeAttr('subledgerinvisible');
	$('#subLedgerGlCodeId_' + subLedgerCount).val(glCodeId);  
	$('#subLedgerIsDebit_' + subLedgerCount).val(isdebit);
	if(isdebit){
		$('#subLedgerDebitAmount_' + subLedgerCount).val(amount);
		$('#subLedgerCreditAmount_' + subLedgerCount).val("0");
	}else{
		$('#subLedgerDebitAmount_' + subLedgerCount).val("0");
		$('#subLedgerCreditAmount_' + subLedgerCount).val(amount);
	}
    $('.subLedgerGlCode_' + subLedgerCount).html(glCode);
    $('#subLedgerDetailTypeId_' + subLedgerCount).val(detailTypeId);
	$('#subLedgerDetailKeyId_' + subLedgerCount).val(detailKeyId);
	$('.subLedgerType_' + subLedgerCount).html(detailTypeName);
	$('.subLedgerName_' + subLedgerCount).html(detailKeyName);
	$('.subLedgerAmount_' + subLedgerCount).html(amount);
	
}

function calculateBillAmount(){
	billamount = 0;
	debitamount = 0;
	creditamount = 0;
	$('#tblaccountdetails  > tbody > tr:visible[id="accountdetailsrow"]').each(function() {
		billamount = parseFloat(Number(billamount) + Number($(this).find(".accountDetailsDebitAmount").html())).toFixed(2);
		debitamount = parseFloat(Number(debitamount) + Number($(this).find(".accountDetailsDebitAmount").html())).toFixed(2);
		creditamount = parseFloat(Number(creditamount) + Number($(this).find(".accountDetailsCreditAmount").html())).toFixed(2);
	});
	//creditamount = amountConverter(creditamount - netpayableamount);
	$("#expenseNetPayableAmount").html(netpayableamount);
	$("#expenseBillTotalDebitAmount").html(debitamount);
	$("#expenseBillTotalCreditAmount").html(creditamount);
	$("#billamount").val(billamount);
}

function validateWorkFlowApprover(name) {
	document.getElementById("workFlowAction").value = name;
	var button = document.getElementById("workFlowAction").value;
	if (button != null && button == 'Submit') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Reject') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').attr('required', 'required');
	}
	 if (button != null && button == 'SaveAsDraft') {
	        
	        $('#approvalDepartment').removeAttr('required');
	        $('#approvalDesignation').removeAttr('required');
	        $('#approvalPosition').removeAttr('required');
	        $('#approvalComent').removeAttr('required');
	     
	        $('#narration').removeAttr('required');
	          $('#billSubType').removeAttr('required');
	    }
	if (button != null && button == 'Cancel') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Forward') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Verify') {
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Create And Approve') {
		return validateCutOff();
	}else
		return true;
	
	return true;
}
function validateCutOff()
{
	var cutofdate = $("#cutOffDate").val();
	var billdate = $("#billdate").val();
	var cutOffDateArray=cutofdate.split("/");
	var billDateArray=billdate.split("/");
	var cutOffDate = new Date(cutOffDateArray[1] + "/" + cutOffDateArray[0] + "/"
			+ cutOffDateArray[2]);
	var billDate = new Date(billDateArray[1] + "/" + billDateArray[0] + "/"
			+ billDateArray[2]);
	if(billDate<=cutOffDate)
	{
		return true;
	}
	else
	{
		bootbox.alert($.i18n.prop('msg.cutoff.warnig.message',cutofdate));
		return false;
	}
	return false;
}
function calcualteNetpaybleAmount(){
	

	var debitamt = 0;
	var creditamt = 0;
	// var debitAmountrowcount=0;
	// var creditAmoutrowcount=0;
	for (var count = 0; count <=debitAmountrowcount; ++count) {

		if (null != document.getElementById("tempDebitDetails[" + count
				+ "].debitamount")) {
			var val = document.getElementById("tempDebitDetails[" + count
					+ "].debitamount").value;
			if (val != "" && !isNaN(val)) {
				debitamt = parseFloat(Number(debitamt) + Number(val)).toFixed(2);
			}
		}
	}

	for (var count = 0; count <=creditAmoutrowcount; ++count) {

		if (null != document.getElementById("tempCreditDetails[" + count
				+ "].rate") && document.getElementById("tempCreditDetails[" + count
						+ "].rate").value != 0) {
			var val = document.getElementById("tempCreditDetails[" + count
					+ "].rate").value;
			if (val != "" && !isNaN(val)) {
				var amt=(val*debitamt)/100;
				document.getElementById("tempCreditDetails[" + count
						+ "].creditamount").value=parseFloat(Number(amt)).toFixed(2);
				creditamt = parseFloat(Number(creditamt) + Number(amt)).toFixed(2);
			}
		}
		else if (null != document.getElementById("tempCreditDetails[" + count
				+ "].creditamount"))
			{
				var val = document.getElementById("tempCreditDetails[" + count
						+ "].creditamount").value;
				if (val != "" && !isNaN(val)) {
					creditamt = parseFloat(Number(creditamt) + Number(val)).toFixed(2);
				}
			}
	}
	netPayableAmount=amountConverter(debitamt-creditamt);
//	$("#expense-netPayableAmount").val(netPayableAmount);
	$("#expenseNetPayableAmount").html(netPayableAmount);
	$("#expenseBillTotalDebitAmount").html(debitamt);
	$("#expenseBillTotalCreditAmount").html(creditamt);
//	document.getElementById("expense-netPayableAmount").value= amountConverter(debitamt-creditamt);
}

function amountConverter(amt) {
	var formattedAmt = amt.toFixed(2);
	return formattedAmt;
}

function worksDiv(){
	var billsubtype=$( "#billSubType option:selected" ).text();
	if(billsubtype=='Works'){
		
		$('.works').show();
	}
	else{
		$('.works').hide();
	}
}

function populateBdgetDetails()
{
	var dept=document.getElementById('department').value;
	var fund=document.getElementById('fund').value;
	var func=document.getElementById('egBillregistermis.function').value;
	var accCode='';
	var status = false;
	if(dept == null || dept == '')
	{
		bootbox.alert('Please select department to view budget details');
		status = true;
	}
	else if(fund == null || fund == '')
	{
		bootbox.alert('Please select fund to view budget details');
		status = true;
	}
	else if(func == null || func == '')
	{
		bootbox.alert('Please select fund to view budget details');
		status = true;
	}
	if(status == false)
	{
		accCode=document.getElementById('tempDebitDetails[0].glcodeid').value;
		var amt =document.getElementById('tempDebitDetails[0].debitamount').value;
		if(accCode == null || accCode == '' || amt == null || amt == '')
		{
			bootbox.alert('Please select Debit Account Code/Amount to view budget details');
		}
		else
		{
			var today = new Date();
			var date = today.getDate()+'/'+(today.getMonth()+1)+'/'+today.getFullYear();
			var url1 = '/services/EGF/report/budgetVarianceReport-loadData.action?asOnDate='+date+'&dept='+dept+'&funds='+fund+'&func='+func+'&accCode='+accCode+'&vtype=pr';
			window.open(url1,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700')
		}
	}
	}