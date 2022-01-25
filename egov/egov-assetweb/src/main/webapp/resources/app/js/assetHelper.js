


function loadScheme(fundId){
	if (!fundId) {
		$('#scheme').empty();
		$('#scheme').append($('<option>').text('Select from below').attr('value', ''));
		$('#subScheme').empty();
		$('#subScheme').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getschemesbyfundid",
			data : {
				fundId : fundId
			},
			async : true
		}).done(
				function(response) {
					$('#scheme').empty();
					$('#scheme').append($("<option value=''>Select from below</option>"));
					$.each(response, function(index, value) {
						var selected="";
						if($schemeId && $schemeId==value.id)
						{
								selected="selected";
						}
						$('#scheme').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
					});
				});

	}
}

function loadSubScheme(schemeId){
	if (!schemeId) {
		$('#subScheme').empty();
		$('#subScheme').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getsubschemesbyschemeid",
			data : {
				schemeId : schemeId
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
						$('#subScheme').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
					});
				});
		
	}
}

$('#fund').change(function () {
	$schemeId = "";
	$subSchemeId = "";
	$('#scheme').empty();
	$('#scheme').append($('<option>').text('--Choose--').attr('value', ''));
	$('#subScheme').empty();
	$('#subScheme').append($('<option>').text('Select from below').attr('value', ''));
	loadScheme($('#fund').val());
});


$('#scheme').change(function () {
	$('#subScheme').empty();
	$('#subScheme').append($('<option>').text('--Choose--').attr('value', ''));
	loadSubScheme($('#scheme').val());
});


$('#assetRef').click(function () {
     document.getElementById("refModal").style.display = "block";
     $("#refModal").modal("show");
});

function setAssetReference(assetId){
	console.log("Asset Id..:"+assetId);
	$("#assetReference").val(assetId);
}

var $schemeId = 0;
var $subSchemeId = 0;
var $fundSourceId = 0;
var $fundId = 0;
$(document).ready(function(){
	$("#refModal").modal("hide");
	$schemeId = $('#schemeId').val();
	$subSchemeId = $('#subSchemeId').val();
	$fundSourceId = $('#fundSourceId').val();
	$fundId = $('#fundId').val();
	if($fundId)
		$("#fund").val($fundId).prop('selected','selected');
	/*
	if($fundSourceId)
		$("#fundSource").val($fundSourceId).prop('selected','selected');
	loadScheme($('#fund').val());
	loadSubScheme($schemeId);
	var functionName = new Bloodhound({
		datumTokenizer : function(datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer : Bloodhound.tokenizers.whitespace,
		remote : {
			url : '/services/EGF/common/ajaxfunctionnames?name=%QUERY',
			filter : function(data) {
				return $.map(data, function(ct) {
					return {
						code:ct.split("~")[0].split("-")[0],
						name : ct.split("~")[0].split("-")[1],
						id : ct.split("~")[1],
						codeName:ct
					};
				});
			}
		}*/
	});


//Search Popup
function fetchAssetRefernce(code, name, assetCategory, department, status){
		console.log("Fetching asset Reference.."+code+"..name.."+name+"..assetCAtegory.."+assetCategory+"..dept.."+department+"..status.."+status);
		$.ajax({
			method : "GET",
			url : "/services/EGF/asset/getassetRef",
			data : {
				code : code,
				name : name,
				assetCategory : assetCategory, 
				department : department, 
				status : status
			},
			async : true
		}).done(
				function(response) {
					console.log(response)
					$('#scheme').empty();
					$('#scheme').append($("<option value=''>Select from below</option>"));
					$.each(response, function(index, value) {
						var selected="";
						if($schemeId && $schemeId==value.id)
						{
								selected="selected";
						}
						$('#scheme').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
						
						$('#scheme').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
						
						/*<td><span><a href='${contextPath}/asset/editform/${asset.id}'>${item.index + 1}</a></span>
						</td>
						<td>
							<span class="assetView_code_${item.index + 1 }">${asset.code }</span>
						</td>
						<td>
							<span class="assetView_name_${item.index + 1 }">${asset.assetHeader.assetName }</span>
						</td>
						<td>
							<span class="assetView_categpry_${item.index + 1 }">${asset.assetHeader.assetCategory }</span>
						</td>
						<td>
							<span class="assetView_department_${item.index + 1 }">${asset.assetHeader.department}</span>
						</td>
						<td>
							<span class="assetView_status_${item.index + 1 }">${asset.assetStatus.description }</span>
						</td>
						<td>
							<%-- <a href="${contextPath}/asset/assetref/${asset.id}"></a> --%>
							<input type="button" value="get" onclick="setAssetReference(${asset.id})"/> 
						</td>*/
					});
				});

}

function deletedoc(billid,docid){
	$.ajax({
			type:"GET",
			data:"html",
			url:"/services/EGF/asset/deleteAssetDoc/"+docid,
			success:function(result){
				if(result=="success"){
					location.reload();
				}
				else{
					bootbox.alert("Fill not Deleted.");
				}
			}
	});
}