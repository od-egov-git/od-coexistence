 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div class="container">
<form:form name="assetBean" method="post" action="${contextPath}/assetcreate/update" modelAttribute="assetBean" 
	class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<!-- Header Details -->
		<div class="panel panel-primary" data-collapsed="0">	
				<div class="panel-heading">
					<div class="panel-title" style="text-align: center;">
							<spring:message code="asset-details" text="Asset Details"/>
					</div>
				</div>
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="lbl-asset-header" text="Header Asset"/>
					</div>
				</div>
			<div class="panel-body">
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-dept" text="department"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">						
						<form:select path="assetHeader.department" id="department" required="required" readonly="true" 
							class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${departmentList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-cat" text="assetCategory"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetHeader.assetCategory" id="assetHeader.assetCategory" required="required" readonly="true" class="form-control">
							<form:options items="${assetCategoryList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-date-creation" text="dateOfCreation"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input id="dateOfCreation" path="assetHeader.dateOfCreation" class="form-control datepicker" 
							data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
						<form:errors path="assetHeader.dateOfCreation" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-desc" text="description"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:textarea path="assetHeader.description" id="description" class="form-control" maxlength="100" ></form:textarea>
						<form:errors path="assetHeader.description" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-name" text="assetName"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control" path="assetHeader.assetName" readonly="true" required="required"/>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-mode-acquisition" text="modeOfAcquisition"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetHeader.modeOfAcquisition" id="assetHeader.modeOfAcquisition"  readonly="true" required="required"  class="form-control">
							<form:options items="${modeOfAcquisitionList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-ref" text="assetReference"/>
					</label>
					<div class="col-sm-3 add-margin"style="display: inline-flex">
						<form:input class="form-control" id="assetReference" path="assetHeader.assetReference"/>
							<c:if test="${mode == 'update' }">
								<%-- <input class="form-control search" type="button" id="assetRef"
									style="width: 25px; margin-left: 10px;" onclick="viewPop('${id}')"/> --%>
								<button class="form-control search" type="button" id="assetRef" 
										style="width: 35px; margin-left: 10px;text-align: center;" onclick="viewPop('${id}')">
									<span class="glyphicon glyphicon-search"></span>
								</button>
							</c:if>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-fund" text="fund"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetHeader.fund" id="fund"  required="required" disabled="disabled"
						class="form-control">
							<form:options items="${fundList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-dep-rate" text="depreciationRate"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control" path="assetHeader.depreciationRate"/>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-function" text="function"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetHeader.function" id="assetHeader.function" readonly="true" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${functionList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-na-sale" text="applicableForSale"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:checkbox path="assetHeader.applicableForSale" />
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-scheme" text="scheme"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetHeader.scheme" id="scheme" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${schemeList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-sub-scheme" text="subScheme"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetHeader.subScheme" id="subScheme" 
						onChange="getSubSchemelist(this)" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${subSchemeList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
					
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-attach-doc" text="file"/>
					</label>
					<div class="col-sm-3 add-margin">
						<c:choose>
	  						<c:when test="${assetBean.documentDetail !=null}">
					       		<a href="/services/asset/assetcreate/downloadBillDoc?assetId=${assetBean.id}&fileStoreId=${assetBean.documentDetail[0].fileStore.fileStoreId }">${assetBean.documentDetail[0].fileStore.fileName }</a>
	        					<!-- <input type="hidden" name="file" class="padding-10"> -->
	        					<%-- <span><input type="button" id="remove" style="background: #265988" value="Remove" onclick="deletedoc(${assetBean.id},${assetBean.documentDetail[0].id});"></span> --%>
							</c:when>
							<c:otherwise>
								<input type="file" name="file" id="file1" class="padding-10">
							</c:otherwise>
						</c:choose>
					</div>
			</div>
	</div>
	<!-- Location Details -->
	<div class="panel panel-primary" data-collapsed="0">	
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="location-details" text="Location Details"/>
					</div>
				</div>
			<div class="panel-body">
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-location" text="locality"/>
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetLocation.location" id="assetLocation.location" required="required" readonly="true" class="form-control">
							<form:options items="${localityList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-revenue-ward" text="revenueWard"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetLocation.revenueWard" id="assetLocation.revenueWard" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${revenueWardList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-block-number" text="block"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetLocation.block" id="assetLocation.block" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${blockList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-street" text="street"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetLocation.street" id="assetLocation.street" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${streetList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-election-ward" text="ward"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetLocation.ward" id="assetLocation.ward" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${electionWardList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-door" text="door"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control" path="assetLocation.door"/>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-zone-no" text="zone"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="assetLocation.zone" id="assetLocation.zone" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${zoneList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-pin" text="pin"/>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="assetLocation.pin" class="form-control text-left patternvalidation" 
							data-pattern="alphanumeric" maxlength="6"/>
						<form:errors path="assetLocation.pin" cssClass="error-msg" />
					</div>
			</div>
 
		</div>
				
		<!-- Category Details -->
		<!-- Result Table -->
		<div style="padding:5%">
			<table class="table table-bordered" id="resultHeader">
			<thead>
				<tr>
					<th><spring:message code="lbl-sl-no" text="Sr. No."/></th>
					<th><spring:message code="name" text="Name"/></th>
					<th><spring:message code="value" text="Value"/></th>
				</tr>
			</thead>
			<tbody>
				 <form:input type="hidden" path="assetCustomFieldMappers"/>
				 <c:forEach items="${assetBean.assetCustomFieldMappers}" var="x" varStatus="item">
					<tr id="assetView_${item.index}">
						<td> ${item.index + 1} </td>
						<td>${x.name}</td>
						<td>
						 	<input type="text" id="customField_${x.id}" name="customField_${x.id}" value="${x.val}"/>
							<input type="hidden" id="customField_id_${item.index}" name="customField_id_${item.index}" value="${x.id}"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>	
		</div>
		<!-- Result Table Ends -->
		<!-- Asset Status -->
		<div class="panel panel-primary" data-collapsed="0">	
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="asset-value-summary" text="Asset Value Summary"/>
					</div>
				</div>
			<div class="panel-body">
				<label class="col-sm-3 control-label text-right">
					<spring:message code="asset-status" text="status"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetStatus" id="assetStatus"  required="required"  class="form-control"
						onchange="loadValues();">
						<form:options items="${assetStatusList}" itemValue="id" itemLabel="description"/>  
					</form:select>
				</div>
			</div>
			
			<!-- Value Section -->
			<br />
			<div class="panel-body" id="valueSection" style="display:none;">
					<div id="capitalized" style="display:none;">
							<label class="col-sm-3 control-label text-right">
								<spring:message code="gross-value" text="grossValue"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="grossValue" />
							</div>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="market-value" text="marketValue"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="marketValue" />
							</div>
					</div>
					<div id="capitalized2" style="display:none;">
							<label class="col-sm-3 control-label text-right">
								<spring:message code="accumulated-depreciation" text="accumulatedDepreciation"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="accumulatedDepreciation" />
							</div>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="survey-number" text="surveyNumber"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="surveyNumber" />
							</div>
					</div>
					<div id="acqPurchase" style="display:none;">
							<label class="col-sm-3 control-label text-right">
								<spring:message code="purchase-value" text="purchaseValue"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="purchaseValue" />
							</div>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="purchase-date" text="purchaseDate"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control datepicker" path="purchaseDate"  data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
								<form:errors path="purchaseDate" cssClass="add-margin error-msg" />
							</div>
					</div>
					<div id="acqDonation" style="display:none;">
							<label class="col-sm-3 control-label text-right">
								<spring:message code="donation-date" text="donationDate"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control datepicker" path="donationDate"  data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
								<form:errors path="donationDate" cssClass="add-margin error-msg" />
							</div>
							<div class="col-sm-6 add-margin"></div>
					</div>
					<div id="acqConstruction" style="display:none;">
							<label class="col-sm-3 control-label text-right">
								<spring:message code="construction-value" text="constructionValue"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="constructionValue" />
							</div>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="construction-date" text="constructionDate"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control datepicker" path="constructionDate" 
									 data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
								<form:errors path="constructionDate" cssClass="add-margin error-msg" />
							</div>
					</div>
					<div id="acqAcquired" style="display:none;">
							<label class="col-sm-3 control-label text-right">
								<spring:message code="acquisition-value" text="acquisitionValue"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control" path="acquisitionValue" />
							</div>
							<label class="col-sm-3 control-label text-right">
								<spring:message code="acquisition-date" text="acquisitionDate"/>
								
							</label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control datepicker" path="acquisitionDate"  data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
								<form:errors path="acquisitionDate" cssClass="add-margin error-msg" />
							</div>
					</div>
			</div> 
		</div>
		<div align="center" class="buttonbottom">
			<div class="row text-center">
			<c:if test="${mode == 'update' }">
					<input type="submit" class="btn btn-primary" name="update" value="Update"/>
			</c:if>
			<input type="button" name="button2" id="button2" value="Close" class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();"/>
			</div>
		</div>
		<input type="hidden" id="statusCode" name="statusCode" value="${assetBean.assetStatus.id}"/>
		<input type="hidden" id="mode" name="mode" value="${assetBean.assetHeader.modeOfAcquisition.id}"/>
		<form:hidden path="code" id="assetCodde" value="${assetBean.code}" />
		<form:hidden path="id" id="assetId" value="${assetBean.id}" />
		<form:hidden path="assetHeader.id" id="assetHeaderId" value="${assetBean.assetHeader.id}" />
		<form:hidden path="assetLocation.id" id="assetLocationId" value="${assetBean.assetLocation.id}" />
	</form:form>
</div>


<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/assetHelper.js?rnd=${app_release_no}' context='/services/asset'/>"></script>
<script src="<cdn:url value='/resources/app/js/expensebill/documents-upload.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>


<script>
$(document).ready(function(){
	var statusCode = $('#statusCode').val();
	var mode = $('#mode').val();
	console.log($('#statusCode').val());
	console.log($('#mode').val());
	fetchdetails1(statusCode,mode);
});

function fetchdetails1(status,mode){
	console.log(status);
	console.log(mode);
	$.ajax({
		type : "GET",
        url: "/services/asset/assetcreate/fetchdetails",
        data: {status: status, mode: mode},
        async : false,
        success: function(res){      
           console.log("output............"+res);
           var jsonObj = JSON.parse(res);
           var assetStatusCode = jsonObj.status;
           var modeOfAcq = jsonObj.mode;
           console.log(assetStatusCode+"..."+modeOfAcq);
           
	        var flag = false;   
	        if(assetStatusCode == 'CREATED' || assetStatusCode == 'CAPITALIZED'){
	       		$("#valueSection").css("display", "block");
	       		flag = true;
	       	}else{
	       		console.log("else Part");
	       		$("#valueSection").css("display", "none");
	       		flag = false;
	       	}
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
	       			console.log("select modeOfAcq");
	       		}
	       	}
           //return res;
        }
    });
}
</script>