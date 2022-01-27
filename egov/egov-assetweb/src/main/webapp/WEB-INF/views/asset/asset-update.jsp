 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div class="panel-title" align="center">
	<spring:message code="asset-update" text="Update Asset"/>
</div>
<br />

<!-- <div class="form-group"> -->
<form:form name="assetBean" method="post" action="${contextPath}/asset/update" modelAttribute="assetBean" 
	class="form-horizontal form-groups-bordered" enctype="multipart/form-data">

	<div class="formmainbox">
		<br />
			<div id="labelAD" align="center">
				<table width="89%" border=0 id="labelid">
					<th> <spring:message code="lbl-asset-header" text="Header Asset"/></th>
				</table>
			</div>
		<br />
		<br />
		<table border="0" width="100%">
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-dept" text="department"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">						
						<form:select path="assetHeader.department" id="department" required="required" readonly="true" 
							class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${departmentList}" itemValue="name" itemLabel="name"/>  
						</form:select>
					</div>
				</td>
				
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-cat" text="assetCategory"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.assetCategory" id="assetHeader.assetCategory" required="required" readonly="true" class="form-control">
							<form:options items="${assetCategoryList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
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
					<label class="col-sm-6 control-label text-right">
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
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-name" text="assetName"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" path="assetHeader.assetName" readonly="true" required="required"/>
					</div>
				</td>
				
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-mode-acquisition" text="modeOfAcquisition"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.modeOfAcquisition" id="assetHeader.modeOfAcquisition"  readonly="true" required="required"  class="form-control">
							<form:options items="${modeOfAcquisitionList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-ref" text="assetReference"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" path="assetHeader.assetReference"/>
					</div>
				</td>
				
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-fund" text="fund"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.fund" id="fund"  required="required" disabled="disabled"
						class="form-control">
							<form:options items="${fundList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-dep-rate" text="depreciationRate"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" path="assetHeader.depreciationRate"/>
					</div>
				</td>
				
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-attach-doc" text="file"/>
					</label>
					<div class="col-sm-6 add-margin">
					 <c:forEach items="${assetBean.documentDetail}" var="document">
						<c:choose>
	  						<c:when test="${document != null}">
					       		<a href="/services/EGF/asset/downloadBillDoc?assetId=${assetBean.id}&fileStoreId=${document.fileStore.fileStoreId }">${document.fileStore.fileName }</a>
	        					
	        					<span><input type="button" id="remove" style="background: #265988" value="Remove"
									onclick="deletedoc(${assetBean.id},${document.id});"></span>
							</c:when>
							<c:otherwise>
								<input type="file" name="file" id="file1" class="padding-10">
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-function" text="function"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.function" id="assetHeader.function" readonly="true" class="form-control">
							<form:options items="${functionList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
				</td>
				
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-na-sale" text="applicableForSale"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:checkbox path="assetHeader.applicableForSale" />
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-scheme" text="scheme"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.scheme" id="scheme" class="form-control">
							<form:options items="${schemeList}" itemValue="id" itemLabel="name"/>  
						</form:select>
					</div>
				</td>
				
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-sub-scheme" text="subScheme"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.subScheme" id="subScheme" 
						onChange="getSubSchemelist(this)" class="form-control">
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
				<table width="89%" border=0 id="labelid">
					<th> <spring:message code="location-details" text="Location Details"/></th>
				</table>
			</div>
			<br />
		<table>
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-location" text="locality"/>
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetLocation.location" id="assetLocation.location" required="required" readonly="true" class="form-control">
							<form:options items="${localityList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-revenue-ward" text="revenueWard"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetLocation.revenueWard" id="assetLocation.revenueWard" class="form-control">
							<form:options items="${revenueWardList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-block-number" text="block"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetLocation.block" id="assetLocation.block" class="form-control">
							<form:options items="${blockList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-street" text="street"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetLocation.street" id="assetLocation.street" class="form-control">
							<form:options items="${streetList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-election-ward" text="ward"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetLocation.ward" id="assetLocation.ward" class="form-control">
							<form:options items="${electionWardList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-door" text="door"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" path="assetLocation.door"/>
					</div>
				</td>
			</tr>
			
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-zone-no" text="zone"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetLocation.zone" id="assetLocation.zone" class="form-control">
							<form:options items="${zoneList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
				<td>
					<label class="col-sm-6 control-label text-right">
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
		
		<!-- File Upload Section -->
		<%-- <div class="formmainbox">
			<jsp:include page="asset-document-upload.jsp"/>
		</div> --%>
		<!-- Asset Status -->
		<br />
		<br />
		
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
				<c:choose>
					<c:when test="${mapperList!=null && mapperList.size() > 0}">
						 <c:forEach items="${mapperList}" var="asset" varStatus="item">
							<tr id="assetView">
								<td>
                                    ${item.index + 1} 
	                            </td>
								<td>
									${asset.name}
								</td>
								<td>
									${asset.val}
								</td>
							</tr>
						</c:forEach> 
					</c:when>
					<c:otherwise>
			            <td colspan="6">No Records Found..</td>
			         </c:otherwise>
				</c:choose>
			</tbody>
		</table>	
		</div>
		<!-- Result Table Ends -->
		<!-- Asset Status -->
		<br />
		<br />
		<div class="formmainbox">
			<br />
			<div id="labelAD" align="center">
				<table width="89%" border=0 id="labelid">
					<th> <spring:message code="asset-value-summary" text="Asset Value Summary"/> </th>
				</table>
			</div>
			<br />
			<div id="listid" style="display: block">
				<table>
					<tr>
						<td>
							<label class="col-sm-6 control-label text-right">
								<spring:message code="asset-status" text="status"/>
							    <span class="mandatory"></span>
							</label>
							<div class="col-sm-6 add-margin">
								<form:select path="assetStatus" id="assetStatus"  required="required"  class="form-control">
									<form:options items="${assetStatusList}" itemValue="id" itemLabel="description"/>  
								</form:select>
							</div>
						</td>
					</tr>
				</table>
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
		
		<form:hidden path="id" id="assetId" value="${assetBean.id}" />
		<form:hidden path="assetHeader.id" id="assetHeaderId" value="${assetBean.assetHeader.id}" />
		<form:hidden path="assetLocation.id" id="assetLocationId" value="${assetBean.assetLocation.id}" />
		<%-- <input type="hidden" id="id" value="${assetBean.id }"/>
		<input type="hidden" id="assetHeaderId" value="${assetBean.assetHeader.id }"/>
		<input type="hidden" id="assetLocationId" value="${assetBean.assetLocation.id }"/> --%>
	</form:form>

<!-- Read Only view section -->
 <c:if test="${mode != 'readOnly' }">
 
 
 </c:if>


<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<%-- <script src="<cdn:url value='/resources/app/js/common/voucherBillHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>  
<script src="<cdn:url value='/resources/app/js/common/assetHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script> --%>
<script src="<cdn:url value='/resources/app/js/common/assetHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/expensebill/documents-upload.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>