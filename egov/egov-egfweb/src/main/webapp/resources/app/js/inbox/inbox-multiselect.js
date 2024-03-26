$(document).ready(function () {
//    $('#new-pass').popover({trigger: "focus", placement: "bottom"});

//    $(document).on("keydown", disableRefresh);

//    preventBack();

    $.fn.dataTable.moment('DD/MM/YYYY h:mm a');

    $('.page-container.horizontal-menu header.navbar .navbar-right > li, .page-container.horizontal-menu header.navbar .navbar-right > li ul li').hover(
        function () {
            $(this).children('ul').show();
        },
        function () {
            $(this).children('ul').removeAttr('style');
            $(this).children('ul').hide();
        });
    worklist();
    isValidApprover();
});

var response_json = [];
var counts = {};
var now_json = [];
var currentCondition;

Array.prototype.find = Array.prototype.find || function (callback) {
    for (var i = 0; i < this.length; i++) {
        if (callback(this[i], i)) {
            return this[i];
        }
    }
};

function clearnow() {
    $('#natureofwork').html('');
    response_json = [];
    counts = {};
    now_json = [];
    currentCondition = undefined;
}

var dataTable;

//common ajax functions for worklist, drafts and notifications
function worklist() {
    tableContainer1 = $("#official_inbox");
    dataTable = tableContainer1.DataTable({
        "sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row buttons-margin'<'col-md-5 col-xs-12'i><'col-md-3 col-xs-6'l><'col-md-4 col-xs-6 text-right'p>>",
        "scrollY":        350,
	    "scrollX":        true,
	    "scrollCollapse": true,
	    "fixedHeader":    false,
	    "scrollResize":   false,
        "paging": false,
        "bDestroy": true,
        "autoWidth": false,
        "aaSorting": [],
        "ajax": {
            "url": "items",
            "dataSrc": ""
        },
        "deferRender": true,
        "columns": [
        	{
                "data": "",
                "width": "7%",
                "defaultContent": '',
                "className": "text-center",
                "targets": 0,
                "orderable": false,
            },
            {"data": "date", "width": "15%"},
            {"data": "sender", "width": "15%"},
            {"data": "task", "width": "15%"},
            {"data": "status", "width": "20%"},
            {"data": "details", "width": "20%"},
            {"data": "elapsed", "className": "text-center", "width": "12%"},
            {"data": "id", "visible": false, "searchable": false},
            {"data": "link", "visible": false, "searchable": false}
        ],
        "columnDefs": [
        	{
        		"render": function (data, type, row) {
                    return '<input type="checkbox" name="select-checkbox" class="select-checkbox">';
                },
	        	"targets": 0, 
	        	"checkboxes": {"selectRow": true},
	        	'width': '1%',
         		'className': 'dt-body-center',
        	}
        ],
        "select": {
	        "style":    'multi',
	        "selector": 'td:first-child'
	    },
        'order': [[ 1, 'asc' ]]
    });
}


function inboxloadmethod() {
    clearnow();
    worklist();
    isValidApprover();
}



var totalCheckedItems ;
// Handle click on "Select all" control
$('#select_all').on('click', function(){
	$('input[name="select-checkbox"]').prop('checked', this.checked);
	totalCheckedItems = $('input[name="select-checkbox"]').length;
});
   

// Handle click on checkbox to set state of "Select all" control
$("#official_inbox").on('change', 'tbody tr input[type="checkbox"]', function() {
   	if($('input[name="select-checkbox"]:checked').length == totalCheckedItems) {
   		$('#select_all').prop('checked', 'checked');
   	} else {
   		$('#select_all').removeAttr('checked');
   	}
});

   
   
   // Handle form submission event
   $('#Approve').on('click', function(e){
   		validateWorkFlowApprover('Approve');
   		var _table = $('#official_inbox').DataTable();
   		var selectedItem = [];
   		
      	$("#official_inbox input[type=checkbox]:checked").each(function () {
      		var currentRowData = _table.row($(this).parents('tr')).data();
      		currentRowData.remark = document.getElementById("approverComments").value;
            selectedItem.push(currentRowData);
        });
      	console.log(selectedItem);
      	if(selectedItem.length == 0) {
	      	alert("Please select atleast one row.");
	      	return false;
      	} else {
      		$.ajax({
		        url: "preApprovedVoucher-bulkUpdate",
		        data: JSON.stringify(selectedItem),
		        dataType: "json",
		        contentType: 'application/json; charset=utf-8',
		        type: "POST",
		        cache: false,
		        crossDomain: true,
		        error: function (e) {
		        	alert("error");
		        	console.log(e);
		        },
		        success: function (data) {
		         	console.log(data);
		         	alert("Your request has been approved Sucessfully");
		         	var redirectUrl = contextPath + '/inbox';
					console.log(redirectUrl);
					window.location = redirectUrl;
		        }
		    });
      	}
   });
   
   
function validateWorkFlowApprover(name) {
    document.getElementById("workFlowAction").value=name;
    var approverPosId = document.getElementById("approverPositionId");
    var approverDesId = document.getElementById("approverDesignation");
    if(approverPosId && approverPosId.value != -1 && approverPosId.value != "") {
		var approver = approverPosId.options[approverPosId.selectedIndex].text; 
		document.getElementById("approverName").value= approver.split('~')[0];
	}   
    if ((name=="Reject" || name=="reject")) {
    	var approverComments = document.getElementById("approverComments").value;
    	if (approverComments == null || approverComments == "") {
    		bootbox.alert("Please enter approver remark");
			return false;
    	}
	}
}

function isValidApprover() {
	$('#approve_section').hide();
	$.ajax({
        url: "isValidApprover",
        dataType: "json",
        contentType: 'application/json; charset=utf-8',
        type: "GET",
        cache: false,
        crossDomain: true,
        error: function (e) {
        	alert("error");
        	console.log(e);
        },
        success: function (data) {
         	console.log(data);
         	if(data) {
				$('#approve_section').show();
			} else {
				$('#approve_section').hide();
			}
        }
    });
}


$('#Cancel').on('click', function(e){
	var redirectUrl = contextPath + '/inbox';
	console.log(redirectUrl);
	window.location = redirectUrl;
});
