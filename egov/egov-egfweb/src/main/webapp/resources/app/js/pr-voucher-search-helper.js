$('#voucherType').change(function(){
	$("#receiptNumber").val("");
	$("#partyName").val("");
	$("#serviceType").val("");
	var type=$('#voucherType').val();
	if(type=="Receipt"){
		$(".receipt-info").show();		
	}else{
		$(".receipt-info").hide();
	}
	getVoucherNameByType();
});

function getVoucherNameByType(){
	var type=$('#voucherType').val();
	jQuery('#voucherName').val('');
	if(type!=""){
		$.ajax({
			url: "/services/EGF/refund/ajax/_getVoucherNameByType",
			type: "GET",
			data: {
				type:type,
			},
			cache: false,
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				jQuery('#voucherName').html("");
				jQuery('#voucherName').append("<option value=''>Select</option>");
				jQuery.each(response, function(index, value) {
					jQuery('#voucherName').append($('<option>').text(value).attr('value', value));
				});
			}, 
			error: function (response) {
				jQuery('#voucherName').html("");
				jQuery('#voucherName').append("<option value=''>Select</option>");
			}
		});	
	}else{
		jQuery('#voucherName').html("");
		jQuery('#voucherName').append("<option value=''>Select</option>");
	}
}

$('#btnsearch').click(function(e) {
	if ($('form').valid()) {
		callAjaxSearch();
	} else {
		e.preventDefault();
	}
});

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};
    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });
    return indexed_array;
}

function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	
	reportdatatable = drillDowntableContainer.dataTable({
		ajax : {
			url : "/services/EGF/refund/ajax/_voucherSearch",      
			type: "POST",
			"data":  getFormData(jQuery('form'))
		},
		"bDestroy" : true,
		'bAutoWidth': false,
		"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
		"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
		"iDisplayLength": 50, 
		"oTableTools" : {
			"sSwfPath" : "../../../../../../services/egi/resources/global/swf/copy_csv_xls_pdf.swf",
			"aButtons" : [ "xls", "pdf", "print" ]
		},		
		aaSorting: [],				
		columns : [
			{
				"data" : null,
				"searchable": false,
	            "orderable": false
			},
			{
				"data" : "vouchernumber",
				"className" : "text-left",
				"render" : function(data, type, full, meta) {
					if(full.receiptNo != 'NA'){
						return '<a href="javascript:void(0);" onclick="viewVoucher(\''+ full.id +'\')">' + full.vouchernumber + '</a>';
					}else{
						return 'NA';
					}					
				}
			},
			{
				"data" : "receiptNumber",
				"className" : "text-left"
			},
			{
				"data" : "type",
				"className" : "text-left"
			},
			{
				"data" : "name",
				"className" : "text-left"
			},
			{
				"data" : "voucherdate",
				"className" : "text-left",
				"render" : function(data, type, full, meta) {
					var date = new Date(full.voucherdate);	
					var dateString = date.toLocaleDateString();
					return dateString;
				}
			},
			{
				"data" : "fundname",
				"className" : "text-left"
			},
			{
				"data" : "deptName",
				"className" : "text-left"
			},
			{
				"data" : "service",
				"className" : "text-left"
			},
			{
				"data" : "payeeName",
				"className" : "text-left"
			},
			{
				"data" : "amount",
				"className" : "text-left"
			},
			{
				"data" : "status",
				"className" : "text-left"
			},
			{
				"data" : "pendingWith",
				"className" : "text-left"
			},
			{
				"data" : "id",
				"className" : "text-left",
				"render" : function(data, type, full, meta) {
					if(full.receiptNo != 'NA'){
						return '<a href="javascript:void(0);" onclick="refundRequest(\''+ full.id +'\')">Refund Request</a>';
					}else{
						return 'NA';
					}					
				}
			}
		],
		"fnRowCallback": function (nRow, aData, iDisplayIndex) {
			var oSettings = this.fnSettings();
			 $("td:first", nRow).html(oSettings._iDisplayStart+iDisplayIndex +1);
			 return nRow;
		}
	});
}

function viewVoucher(vhid){
	window.open('/services/EGF/refund/_viewVoucher?vhid=' + vhid,'','width=1200, height=800');
}

function refundRequest(vhid){
	window.open('/services/EGF/refund/_paymentRequestForm?vhid=' + vhid,'','width=1200, height=800');
}



