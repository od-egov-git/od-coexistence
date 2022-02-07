<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="container">
	<form:form name="assetMaster" method="post" action="create"
		modelAttribute="assetBean"
		class="form-horizontal form-groups-bordered"
		enctype="multipart/form-data">
		
		<!-- Header Details -->
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title" style="text-align: center;">
					<spring:message code="lbl-asset-create" text="Create Asset" />
				</div>
			</div>	
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl-asset-header" text="Header Asset" />
				</div>
			</div>
			<div class="panel-body">
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-dept" text="department" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.department" id="dept"
						required="required" class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${departmentList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-cat" text="assetCategory" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.assetCategory"
						id="assetHeader.assetCategory" class="form-control"
						onchange="showCategoryDetails(this);">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${assetCategoryList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-date-creation" text="dateOfCreation" /> <span
					class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="dateOfCreation" path="assetHeader.dateOfCreation"
						class="form-control datepicker" data-date-end-date="0d"
						required="required" placeholder="DD/MM/YYYY" />
					<form:errors path="assetHeader.dateOfCreation"
						cssClass="add-margin error-msg" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-desc" text="description" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:textarea path="assetHeader.description" id="description"
						required="required" class="form-control" maxlength="100"></form:textarea>
					<form:errors path="assetHeader.description"
						cssClass="add-margin error-msg" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-name" text="assetName" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control" path="assetHeader.assetName"
						required="required" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-mode-acquisition" text="modeOfAcquisition" /> <span
					class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.modeOfAcquisition"
						onchange="loadValues();" id="modeOfAcquisition"
						required="required" class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${modeOfAcquisitionList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-ref" text="assetReference" />
				</label>
				<div class="col-sm-3 add-margin" style="display: inline-flex">
					<form:input class="form-control" id="assetReference"
						path="assetHeader.assetReference" />
					<input class="form-control search" type="button" id="assetRef"
						style="width: 25px; margin-left: 10px;" onclick="viewPop('${id}')"><i
						class="bi bi-search"></i></input>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-fund" text="fund" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.fund" id="fund" required="required"
						readony="readonly" class="form-control">
						<form:options items="${fundList}" itemValue="id" itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-dep-rate" text="depreciationRate" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control text-left patternvalidation"
						id="AssetDep" data-pattern="alphanumeric"
						path="assetHeader.depreciationRate" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-attach-doc" text="file" />
				</label>
				<div class="col-sm-3 add-margin">
					<input type="file" name="file" id="file1"
						onchange="isValidFile(this.id)" class="padding-10">
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-function" text="function" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.function" id="assetHeader.function"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${functionList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-scheme" text="scheme" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.scheme" id="scheme"
						class="form-control" onChange="getSubSchemelist(this)">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${schemeList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-sub-scheme" text="subScheme" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.subScheme" id="subScheme"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${subSchemeList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-na-sale" text="applicableForSale" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:checkbox path="assetHeader.applicableForSale" />
				</div>
			</div>
		</div>
		<!-- Location Details -->
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="location-details" text="Location Details" />
				</div>
			</div>

			<div class="panel-body">
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-location" text="locality" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetLocation.location"
						id="assetLocation.location" required="required"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${localityList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-revenue-ward" text="revenueWard" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetLocation.revenueWard"
						id="assetLocation.revenueWard" class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${revenueWardList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-block-number" text="block" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetLocation.block" id="assetLocation.block"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${blockList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-street" text="street" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetLocation.street" id="assetLocation.street"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${streetList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-election-ward" text="ward" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetLocation.ward" id="assetLocation.ward"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${electionWardList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-door" text="door" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control" path="assetLocation.door" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-zone-no" text="zone" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetLocation.zone" id="assetLocation.zone"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${zoneList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-pin" text="pin" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:input path="assetLocation.pin"
						class="form-control text-left patternvalidation"
						data-pattern="alphanumeric" maxlength="6" />
					<form:errors path="assetLocation.pin" cssClass="error-msg" />
				</div>
			</div>
		</div>
		<!-- Category Details -->
		<div class="panel panel-primary" data-collapsed="0"
			id="categoryDetails" style="display: none">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="category-details" text="Category Details" />
				</div>
			</div>
			<div class="panel-body" id="result"></div>
		</div>

		<!-- Asset Status -->
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="asset-value-summary"
						text="Asset Value Summary" />
				</div>
			</div>

			<div class="panel-body">
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-status" text="status" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetStatus" id="assetStatus"
						required="required" class="form-control" onchange="loadValues();">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${assetStatusList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
			</div>
			<!-- Value Section -->
			<div class="panel-body" id="valueSection" style="display: none">
				<div id="capitalized" style="display: none;">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="gross-value" text="grossValue" /> <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input data-pattern="alphanumeric" id="grossValue"
							class="form-control" path="grossValue" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="market-value" text="marketValue" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input data-pattern="alphanumeric" class="form-control"
							path="marketValue" />
					</div>
				</div>
				<div id="capitalized2" style="display: none;">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="accumulated-depreciation" text="accumulatedDepreciation" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control" id="accumulatedDepreciation"
							data-pattern="alphanumeric" path="accumulatedDepreciation" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="survey-number" text="surveyNumber" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control" path="surveyNumber" />
					</div>
				</div>
				<div id="acqPurchase" style="display: none;">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="purchase-value" text="purchaseValue" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input data-pattern="alphanumeric" class="form-control"
							path="purchaseValue" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="purchase-date" text="purchaseDate" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control datepicker" path="purchaseDate"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
						<form:errors path="purchaseDate" cssClass="add-margin error-msg" />
					</div>
				</div>
				<div id="acqDonation" style="display: none;">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="donation-date" text="donationDate" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control datepicker" path="donationDate"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
						<form:errors path="donationDate" cssClass="add-margin error-msg" />
					</div>
					<div class="col-sm-6 add-margin"></div>
				</div>
				<div id="acqConstruction" style="display: none;">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="construction-value" text="constructionValue" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input data-pattern="alphanumeric" class="form-control"
							path="constructionValue" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="construction-date" text="constructionDate" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control datepicker"
							path="constructionDate" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
						<form:errors path="constructionDate"
							cssClass="add-margin error-msg" />
					</div>
				</div>
				<div id="acqAcquired" style="display: none;">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="acquisition-value" text="acquisitionValue" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input data-pattern="alphanumeric" class="form-control"
							path="acquisitionValue" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="acquisition-date" text="acquisitionDate" />

					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control datepicker" path="acquisitionDate"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
						<form:errors data-pattern="alphanumeric" path="acquisitionDate"
							cssClass="add-margin error-msg" />
					</div>
				</div>
			</div>
		</div>

		<div align="center" class="buttonbottom">
			<div class="row text-center">
				<input type="submit" class="btn btn-primary" name="create"
					id="createBtn" value="create" /> <input type="button"
					name="button2" id="button2" value="Close" class="btn btn-default"
					onclick="window.parent.postMessage('close','*');window.close();" />
			</div>
		</div>
		<input type="hidden" id="customFieldsCounts" name="customFieldsCounts" />
		<input type="hidden" id="isCapitalized" name="isCapitalized" />
	</form:form>
</div>
<script>

 function getSchemelist(obj)
{
	if(document.getElementById('schemeid'))
		populateschemeid({fundId:obj.value});
}
function getSubSchemelist(obj)
{
	loadSubScheme(obj.value);
} 
function onPopupClose(returnParameter) {
   $('#assetReference').val(returnParameter);
}

function viewPop(id){
	/* var url1 = '/services/EGF/report/budgetVarianceReport-loadData.action?asOnDate='+date+'&dept='+dept+'&funds='+fund+'&func='+func+'&accCode='+accCode+'&vtype=pr';
	window.open(url1,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700') */
	var x = window.open('/services/asset/assetcreate/assetRef/ref','popup','width=850,height=600');
}

function showCategoryDetails(obj){
	$('#result').html('');
	if(obj.value == ''){
		$("#categoryDetails").css("display", "none");
	}else{
		id = obj.value;
		fetchCustomFieldData(id);
		$("#categoryDetails").css("display", "block");
	}
}

function loadValues(){
	var assetStatusCode = $('#assetStatus').val();
	var modeOfAcq = $('#modeOfAcquisition').val();
	$("#valueSection").css("display", "none");
	$("#capitalized").css("display", "none");
	$("#capitalized2").css("display", "none");
	$("#acqPurchase").css("display", "none");
	$("#acqDonation").css("display", "none");
	$("#acqConstruction").css("display", "none");
	$("#acqAcquired").css("display", "none");
	
	if(assetStatusCode != '' && modeOfAcq != ''){
		fetchdetails(assetStatusCode,modeOfAcq);
	}
}

function fetchdetails(status,mode){
	$.ajax({
		type : "GET",
        url: "/services/asset/assetcreate/fetchdetails",
        data: {status: status, mode: mode},
        async : false,
        success: function(res){      
           var jsonObj = JSON.parse(res);
           var assetStatusCode = jsonObj.status;
           var modeOfAcq = jsonObj.mode;
           var flag = false;   
	        if(assetStatusCode == 'CREATED' || assetStatusCode == 'CAPITALIZED'){
	       		$("#valueSection").css("display", "block");
	       		flag = true;
	       	}else{
	       		$("#valueSection").css("display", "none");
	       		flag = false;
	       	}
	        $('#isCapitalized').val(assetStatusCode);
	       	if(assetStatusCode == 'CAPITALIZED'){
	       		$("#capitalized").css("display", "block");
	       		$("#capitalized2").css("display", "block");
		    }
	       	if(flag){
	       		if(modeOfAcq == 'PURCHASE'){
	       			$("#acqPurchase").css("display", "block");
	       		}else if(modeOfAcq == 'DONATION'){
	       			$("#acqDonation").css("display", "block");
	       		}else if(modeOfAcq == 'CONSTRUCTION'){
	       			$("#acqConstruction").css("display", "block");
	       		}else if(modeOfAcq == 'ACQUIRED'){
	       			$("#acqAcquired").css("display", "block");
	       		}else{
	       			bootbox.alert('Please select Mode of Acquisition');
	       		}
	       	}
        }
    });
}

function fetchCustomFieldData(id){
	 $.ajax({
		 type : "POST",
         url: "/services/asset/assetcreate/categorydetails/"+id,
         success: function(res){      
	           var jsonObj = JSON.parse(res);
	           var json = jsonObj.data;
	           $("#AssetDep").val(jsonObj.depRate);
			   var retHtml = "";
	           var len = 0;
			    for (var key in json) {
			       if (json.hasOwnProperty(key)) {
			    	   len++;
			    	   if(json[key].active)
			    	  	retHtml += generateCustomField(key, json[key].dataType, json[key].name, json[key].mandatory, json[key].values);
			       }
			    }
			    $('#customFieldsCounts').val(len);
	           $('#result').html(retHtml);
         }
     });
} 

function generateCustomField(key, dataType, name, requiredVal, values){
	var returnVal = '';
	var isMandatory = false;
	if(requiredVal){
		isMandatory = true;
	}
	switch (dataType) {
	  case 'Text':
		returnVal += "<label class='col-sm-3 control-label text-right'>";
		returnVal += name;
		if(isMandatory){
			returnVal += "<span class='mandatory'></span>";
		}
		returnVal += "</label>";
		returnVal += "<div class='col-sm-3 add-margin'>";
		returnVal += "<input type='text' value='"+values+"' class='form-control' id='customField_"+key+"' name='customField_"+key+"'";
		if(isMandatory){
			returnVal += "required='required'";
		}
		returnVal += "/>";
		returnVal += "</div>";
		break;
	  case 'Number':
		returnVal += "<label class='col-sm-3 control-label text-right'>";
		returnVal += name;
		if(isMandatory){
			returnVal += "<span class='mandatory'></span>";
		}
		returnVal += "</label>";
		returnVal += "<div class='col-sm-3 add-margin'>";
		returnVal += "<input type='number' value='"+values+"' class='form-control' id='customField_"+key+"' name='customField_"+key+"'";
		if(isMandatory){
			returnVal += "required='required'";
		}
		returnVal += "/>";
		returnVal += "</div>";
		break;
	  default:
		returnVal += "";
	}
return returnVal;

}

//File Upload Related
var maxSize = 2097152;
var inMB = maxSize/1024/1024;
var fileformatsinclude = ['doc','docx','xls','xlsx','rtf','pdf','jpeg','jpg','png','txt','zip','dxf'];

function isValidFile(id) {
    var myfile= $("#"+id).val();
    var ext = myfile.split('.').pop();
    if($.inArray(ext.toLowerCase(), fileformatsinclude) > -1){
        getTotalFileSize();
    } else {
        bootbox.alert("Please upload .doc, .docx, .xls, .xlsx, .rtf, .pdf, jpeg, .jpg, .png, .txt, .zip and .dxf format documents only");
        $("#"+id).val('');
        return false;
    }
}
function getTotalFileSize() {
    var uploaderTbl = document.getElementById("uploadertbl");
    var tbody = uploaderTbl.lastChild;
    var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
    var totalSize = 0;
    for(var i = 1; i < trNo; i++) {
        totalSize += $("#file"+i)[0].files[0].size; // in bytes
        if(totalSize > maxSize) {
            bootbox.alert('File size should not exceed '+ inMB +' MB!');
            $("#file"+i).val('');
            return;
        }
    }
}
//Till Here
$( "#createBtn" ).click(function(e) {	
	var grossVal = $('#grossValue').val();
	var accumulatedDepreciation = $('#accumulatedDepreciation').val();
	var isCapitalized = $('#isCapitalized').val();
	if(isCapitalized == 'CAPITALIZED'){
		if (grossVal == null || grossVal =='') {
			e.preventDefault();
			bootbox.alert('Gross Value shouldnot be empty!');
			$( "#grossValue" ).focus();
		  }
		if (accumulatedDepreciation == null || accumulatedDepreciation == '') {
			e.preventDefault();
			bootbox.alert('Accumulated Depreciation shouldnot be empty!');
			$( "#accumulatedDepreciation" ).focus();
		  }
	}
});

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
		}).done( function(response) {
			$('#subScheme').empty();
			$('#subScheme').append($("<option value=''>Select from below</option>"));
			$.each(response, function(index, value) {
				var selected="";
				$('#subScheme').append($('<option '+ selected +'>').text(value.name).attr('value', value.id));
			});
		});
	}
}
</script>