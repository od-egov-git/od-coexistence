<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


<div class="formmainbox">
	<form:form name="assetBean" method="post" action="search" modelAttribute="assetBean" 
		class="form-horizontal form-groups-bordered" enctype="multipart/form-data">

		<br />
		<div class="panel-title" align="center">
			<spring:message code="view-asset" text="View Asset"/>
		</div>
		<br />
		<div id="search-asset">
		
			<table border="0" width="100%">
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-code" text="assetCode"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" path="code"/>
					</div>
				</td>
				
				<td>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-name" text="assetName"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" path="assetHeader.assetName"/>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-cat" text="assetCategory"/>
					    <span class="mandatory"></span>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetHeader.assetCategory" id="assetHeader.assetCategory" class="form-control">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${assetCategoryList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
				
				<td>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="asset-dept" text="department"/>
					</label>
					<div class="col-sm-6 add-margin">						
						<form:select path="assetHeader.department" id="department" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${departmentList}" itemValue="name" itemLabel="name"/>  
						</form:select>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<label class="col-sm-6 control-label text-right">
						<spring:message code="asset-status" text="status"/>
					</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetStatus" id="assetStatus" class="form-control">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${assetStatusList}" itemValue="id" itemLabel="description"/>  
						</form:select>
					</div>
				</td>
			</tr>		
		</table>
		
			<div align="center" class="buttonbottom">
				<div class="row text-center">
					<input type="submit" class="btn btn-primary" name="search" value="Search"/>
					<input type="button" name="button2" id="button2" value="Close" class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();"/>
				</div>
			</div>
		</div>
	</form:form>
	<br />
	<br />
	<!-- Result Table -->
	<div style="padding:5%">
		<table class="table table-bordered" id="resultHeader">
		<thead>
			<tr>
				<th><spring:message code="lbl-sl-no" text="Sr. No."/></th>
				<th><spring:message code="code" text="Code"/></th>
				<th><spring:message code="name" text="Name"/></th>
				<th><spring:message code="asset-cat" text="Asset Category Type"/></th>
				<th><spring:message code="asset-dept" text="Department"/></th>
				<th><spring:message code="asset-status" text="Status"/></th>
				<th><spring:message code="lbl.action" text="Action"/></th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${assetList!=null && assetList.size() > 0}">
					 <c:forEach items="${assetList}" var="asset" varStatus="item">
						
						<tr id="assetView">
							<td>
								<span class="assetView_id_${item.index + 1}">
								<a href="${contextPath}/asset/editform/${asset.id}">${item.index + 1}</a></span>
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
</div>

<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/assetHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>     
<style>
@media (max-width: 768px) {
  .table-bordered tbody > tr {
    border-bottom: 1px solid #ebebeb;
  }
}
@media (max-width: 768px) {
  .table-bordered tbody > tr td {
    border: none;
  }
}
</style>
<script>


</script>