<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%@ page isELIgnored = "false" %>
<div class="panel-title" align="center">
	<spring:message code="lbl-asset-create" text="Create Asset"/>
</div>
<br />

<!-- <div class="form-group"> -->
<form:form name="assetBean" method="post" action="create" modelAttribute="assetBean" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
		<!-- Header Details -->
		<br />
		<br />
		<div class="formmainbox">
		<br />
			<div id="labelAD" align="center">
				<table width="89%" border=0>
					<th> <spring:message code="lbl-asset-header" text="Header Asset"/></th>
				</table>
			</div>
			<br />
			<table>
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-dept" text="department"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">						
							<form:select path="assetHeader.department" id="department" required="required"  class="form-control">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${departmentList}" itemValue="name" itemLabel="name"/>  
							</form:select>
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-cat" text="assetCategory"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetHeader.assetCategory" id="assetHeader.assetCategory" class="form-control"
								onchange="showCategoryDetails(this);">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${assetCategoryList}" itemValue="id" itemLabel="name"/>  
							</form:select>
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-date-creation" text="dateOfCreation"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:input id="dateOfCreation" path="assetHeader.dateOfCreation" class="form-control datepicker" 
								data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
							<form:errors path="assetHeader.dateOfCreation" cssClass="add-margin error-msg" />
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-desc" text="description"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:textarea path="assetHeader.description" id="description" class="form-control" maxlength="100" ></form:textarea>
							<form:errors path="assetHeader.description" cssClass="add-margin error-msg" />
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-name" text="assetName"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:input class="form-control" path="assetHeader.assetName" required="required"/>
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-mode-acquisition" text="modeOfAcquisition"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetHeader.modeOfAcquisition"
							onchange="loadValues();" id="modeOfAcquisition" required="required"  class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${modeOfAcquisitionList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
				</tr>
				
				<tr>
					<td class="col-md-6">
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-ref" text="assetReference"/>
						</label>
						<div class="col-sm-6 add-margin" style="display:inline-flex">
							<form:input class="form-control" id="assetReference" path="assetHeader.assetReference"/>
							<input class="form-control" type="button" id="assetRef" onclick="viewPop('${id}')"/> 
						</div>
						<%-- <div class="col-sm-1">
							<input class="form-control" type="button" id="assetRef" onclick="viewPop('${id}')"/> 
						</div> --%>
					</td>
					<td class="col-md-6">
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-fund" text="fund"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetHeader.fund" id="fund" required="required" readony="readonly"
							class="form-control">
									<form:options items="${fundList}" itemValue="id" itemLabel="name"/>  
							</form:select>
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-dep-rate" text="depreciationRate"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:input  class="form-control text-left patternvalidation" 
								data-pattern="alphanumeric" path="assetHeader.depreciationRate"/>
						</div>
					</td>
					
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-attach-doc" text="file"/>
						</label>
						<div class="col-sm-4 add-margin">
							<input type="file" name="file" id="file1" onchange="isValidFile(this.id)" class="padding-10">
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-function" text="function"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetHeader.function" id="assetHeader.function" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${functionList}" itemValue="id" itemLabel="name"/>  
							</form:select>
						</div>
					</td>
					
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-na-sale" text="applicableForSale"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:checkbox path="assetHeader.applicableForSale" />
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-scheme" text="scheme"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetHeader.scheme" id="scheme" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${schemeList}" itemValue="id" itemLabel="name"/>  
							</form:select>
						</div>
					</td>
					
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-sub-scheme" text="subScheme"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetHeader.subScheme" id="subScheme" 
							onChange="getSubSchemelist(this)" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${subSchemeList}" itemValue="id" itemLabel="name"/>  
							</form:select>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<!-- Location Details -->
		<br />
		<br />
		<div class="formmainbox">
			<br />
			<div id="labelAD" align="center">
				<table width="89%" border=0>
					<th> <spring:message code="location-details" text="Location Details"/></th>
				</table>
			</div>
			<br />
			<table border="0" width="100%">
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-location" text="locality"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetLocation.location" id="assetLocation.location" required="required" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${localityList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-revenue-ward" text="revenueWard"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetLocation.revenueWard" id="assetLocation.revenueWard" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${revenueWardList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-block-number" text="block"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetLocation.block" id="assetLocation.block" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${blockList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-street" text="street"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetLocation.street" id="assetLocation.street" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${streetList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-election-ward" text="ward"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetLocation.ward" id="assetLocation.ward" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${electionWardList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-door" text="door"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:input class="form-control" path="assetLocation.door"/>
						</div>
					</td>
				</tr>
				
				<tr>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-zone-no" text="zone"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:select path="assetLocation.zone" id="assetLocation.zone" class="form-control">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${zoneList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
					<td>
						<label class="col-sm-3 control-label text-right">
							<spring:message code="asset-pin" text="pin"/>
						</label>
						<div class="col-sm-6 add-margin">
							<form:input path="assetLocation.pin" class="form-control text-left patternvalidation" 
								data-pattern="alphanumeric" maxlength="6"/>
							<form:errors path="assetLocation.pin" cssClass="error-msg" />
						</div>
					</td>
				</tr>
			</table>
		</div>

		<!-- Category Details -->
		<br />
		<br />
		<div class="formmainbox" id="categoryDetails" style="display:none">
			<br />
			<div id="labelAD" align="center">
				<table width="89%" border=0>
					<th> <spring:message code="category-details" text="Category Details"/></th>
				</table>
			</div>
			<br />
			<table border="0" width="100%">
				<tbody id="result">
				
				</tbody>
			</table>
		</div>
		
		<!-- Asset Status -->
		<br />
		<br />
		<div class="formmainbox">
			<br />
			<div id="labelAD" align="center">
				<table width="89%" border=0>
					<th> <spring:message code="asset-value-summary" text="Asset Value Summary"/> </th>
				</table>
			</div>
			<br />
			<table border="0" width="100%">
				<tr>
					<td>
						<label class="col-sm-4 control-label text-right">
							<spring:message code="asset-status" text="status"/>
							<span class="mandatory"></span>
						</label>
						<div class="col-sm-4 add-margin">
							<form:select path="assetStatus" id="assetStatus"  required="required"  class="form-control"
							onchange="loadValues();">
									<form:option value=""><spring:message code="lbl.select" /></form:option>
									<form:options items="${assetStatusList}" itemValue="id" itemLabel="description"/>  
							</form:select>
						</div>
					</td>
				</tr>
			</table>
			<!-- Value Section -->
			<br />
			<br />
			<%-- <div id="valueSection" style="display:none;">
				<table width="100%">
					<tr id="capitalized" style="display:none;">
						<td>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="gross-value" text="grossValue"/>
								<span class="mandatory"></span>
							</label>
							<div class="col-sm-6 add-margin">
								<form:input class="form-control" path="grossValue" required="required"/>
							</div>
						</td>
						<td>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="market-value" text="marketValue"/>
								<span class="mandatory"></span>
							</label>
							<div class="col-sm-6 add-margin">
								<form:input class="form-control" path="marketValue" required="required"/>
							</div>
						</td>
					</tr>
					<tr id="capitalized2" style="display:none;">
						<td>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="accumulated-depreciation" text="accumulatedDepreciation"/>
								<span class="mandatory"></span>
							</label>
							<div class="col-sm-6 add-margin">
								<form:input class="form-control" path="accumulatedDepreciation" required="required"/>
							</div>
						</td>
						<td>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="survey-number" text="surveyNumber"/>
								<span class="mandatory"></span>
							</label>
							<div class="col-sm-6 add-margin">
								<form:input class="form-control" path="surveyNumber" required="required"/>
							</div>
						</td>
					</tr>
					<tr id="acqPurchase" style="display:none;">
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="purchase-value" text="purchaseValue"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control" path="purchaseValue" required="required"/>
								</div>
							</td>
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="purchase-date" text="purchaseDate"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control datepicker" path="purchaseDate" required="required" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
									<form:errors path="purchaseDate" cssClass="add-margin error-msg" />
								</div>
							</td>
						</tr>
						<tr id="acqDonation" style="display:none;">
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="donation-date" text="donationDate"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control datepicker" path="donationDate" required="required" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
									<form:errors path="donationDate" cssClass="add-margin error-msg" />
								</div>
							</td>
							<td>
							</td>
						</tr>
						<tr id="acqConstruction" style="display:none;">
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="construction-value" text="constructionValue"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control" path="constructionValue" required="required"/>
								</div>
							</td>
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="construction-date" text="constructionDate"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control datepicker" path="constructionDate" required="required" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
									<form:errors path="constructionDate" cssClass="add-margin error-msg" />
								</div>
							</td>
						</tr>
						<tr id="acqAcquired" style="display:none;">
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="acquisition-value" text="acquisitionValue"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control" path="acquisitionValue" required="required"/>
								</div>
							</td>
							<td>
								<label class="col-sm-3 control-label text-right">
									<spring:message code="acquisition-date" text="acquisitionDate"/>
									<span class="mandatory"></span>
								</label>
								<div class="col-sm-6 add-margin">
									<form:input class="form-control datepicker" path="acquisitionDate" required="required" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
									<form:errors path="acquisitionDate" cssClass="add-margin error-msg" />
								</div>
							</td>
						</tr>
				</table>
			</div> --%>
			
		</div>
		<div align="center" class="buttonbottom">
			<div class="row text-center">
				<input type="submit" class="btn btn-primary" name="create" value="create"/>
				<input type="button" name="button2" id="button2" value="Close" class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();"/>
			</div>
		</div>
<input type="hidden" id="customFieldsCounts" name="customFieldsCounts"/>
</form:form>


<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/voucherBillHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>  
<script src="<cdn:url value='/resources/app/js/common/assetHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/expensebill/documents-upload.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>


<script>

 function getSchemelist(obj)
{
	if(document.getElementById('schemeid'))
		populateschemeid({fundId:obj.value});
}
function getSubSchemelist(obj)
{
	if(document.getElementById('subschemeid'))
		populatesubschemeid({schemeId:obj.value});
} 
//window.open('popup.jsp?parameter='+param,'mywindow','width=500,height=350,toolba‌​r=no,resizable=no,menubar=no');
function onPopupClose(returnParameter) {

	   // Process returnParameter here
	   console.log("Return Param.."+returnParameter);
	   $('#assetReference').val(returnParameter);
	}

function viewPop(id){
	console.log(id);
	/* var url1 = '/services/EGF/report/budgetVarianceReport-loadData.action?asOnDate='+date+'&dept='+dept+'&funds='+fund+'&func='+func+'&accCode='+accCode+'&vtype=pr';
	window.open(url1,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700') */
	var x = window.open('/services/EGF/asset/viewform/ref','popup','width=800,height=600');
	console.log("Return Obj..:"+x.value); 
	console.log("Return Obj..:"+Object.values(x)); 
	//$('#assetReference').val(x);
}

function loadValues(){//obj
	//console.log(obj.value);
	var assetStatusCode = $('#assetStatus').val();//obj.value;
	var flag = false;
	//default set
	$("#valueSection").css("display", "none");
	$("#capitalized").css("display", "none");
	$("#capitalized2").css("display", "none");
	$("#acqPurchase").css("display", "none");
	$("#acqDonation").css("display", "none");
	$("#acqConstruction").css("display", "none");
	$("#acqAcquired").css("display", "none");
	
	/*if(assetStatusCode == 'CREATED' || assetStatusCode == 'CAPITALIZED'){
		$("#valueSection").css("display", "block");
		flag = true;
	}
	if(assetStatusCode == 'CAPITALIZED'){
		$("#capitalized").css("display", "block");
		$("#capitalized2").css("display", "block");
	}
	var modeOfAcq = $('#modeOfAcquisition').val();
	console.log(modeOfAcq);
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
			console.log("select modeOfAcq");
		}
	}*/
}

function showCategoryDetails(obj){
	console.log("Show id..."+obj.value);
	$('#result').html('');
	if(obj.value == ''){
		$("#categoryDetails").css("display", "none");
	}else{
		id = obj.value;
		fetchCustomFieldData(id);
		//var ret = generateInputField('text','testValue','required');
		//$('#categoryDetails tbody').html(ret);
		$("#categoryDetails").css("display", "block");
	}
}

function fetchCustomFieldData(id){
	console.log("Custom id..."+id);
	 $.ajax({
		 type : "POST",
         url: "/services/EGF/asset/categorydetails/"+id,
         success: function(res){      
	           console.log("output............"+res);
	           var jsonObj = JSON.parse(res);
	           var json = jsonObj.data;
	           //var output = evaluateJson(json);
	           var retHtml = "<tr>";
	           var len = 0;
			    for (var key in json) {
			       if (json.hasOwnProperty(key)) {
			    	   len++;
			    	  console.log(json[key].id);
			    	  retHtml += generateCustomField(key, json[key].dataType, json[key].name, json[key].mandatory);
			    	  if(key > 1 && key%2==0){
						console.log('Even');
						retHtml += "</tr><tr>";
				      }
			       }
			    }
			    retHtml += "</tr>";
			    console.log("Result..."+retHtml);
			    $('#customFieldsCounts').val(len);
	           $('#result').html(retHtml);
         }
     });
}
</script>