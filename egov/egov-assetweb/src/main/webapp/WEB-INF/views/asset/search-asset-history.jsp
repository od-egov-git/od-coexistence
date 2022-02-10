<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


<div class="container">
	<form:form name="assetBean" method="post" action="search"
		modelAttribute="assetBean"
		class="form-horizontal form-groups-bordered"
		enctype="multipart/form-data">

		<div class="panel panel-primary" data-collapsed="0" id="search-asset">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="view-asset" text="View Asset" />
				</div>
			</div>
			
			<div class="panel-body">
				<label class="col-sm-3 control-label text-right">
				 	<spring:message code="asset-code" text="assetCode" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control" path="code" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-name" text="assetName" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control" path="assetHeader.assetName" />
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-cat" text="assetCategory" /> <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.assetCategory"
						id="assetHeader.assetCategory" required="required"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${assetCategoryList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-dept" text="department" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetHeader.department" id="department"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${departmentList}" itemValue="id"
							itemLabel="name" />
					</form:select>
				</div>
				<label class="col-sm-3 control-label text-right"> <spring:message
						code="asset-status" text="status" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="assetStatus" id="assetStatus"
						class="form-control">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${assetStatusList}" itemValue="id"
							itemLabel="description" />
					</form:select>
				</div>
			</div>
		</div>

		<div align="center" class="buttonbottom">
			<div class="row text-center">
				<input type="submit" class="btn btn-primary" name="search"
					value="Search" /> <input type="button" name="button2" id="button2"
					value="Close" class="btn btn-default"
					onclick="window.parent.postMessage('close','*');window.close();" />
			</div>
		</div>
		<input type="hidden" name="viewmode" id="viewmode" value="${viewmode}"/>
	</form:form>
	<!-- Result Table -->
	<div class="panel panel-primary" data-collapsed="0">
		<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="asset-search-result" text="Search Result" />
			</div>
		</div>
		<div class="panel-body">
		<table class="table table-bordered" id="resultHeader">
			<thead>
				<tr>
					<th><spring:message code="lbl-sl-no" text="Sr. No." /></th>
					<th><spring:message code="lbl-asset-assetcategoryname" text="Asset Category Name" /></th>
					<th><spring:message code="lbl-asset-department" text="Department" /></th>
					<th><spring:message code="lbl-asset-code"
							text="Asset Code" /></th>
					<th><spring:message code="lbl-asset-code" text="Asset Name" /></th>
					<th><spring:message code="lbl-asset-code" text="Current Gross Value(Rs.)" /></th>
					<%-- <c:if test="${isReference}">
						<th><spring:message code="lbl-action" text="Action" /></th>
					</c:if> --%>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${assetList!=null && assetList.size() > 0}">
						<c:forEach items="${assetList}" var="asset" varStatus="item">

							<tr id="assetView">
								<%-- <td>
								<span class="assetView_id_${item.index + 1}">${item.index + 1}</span>
								</td>
								--%>
								<td><a href="searchAssetHistory/${asset.id}" target="popup"
									onclick="window.open('searchAssetHistory/${asset.id}','popup','width=700,height=600'); return false;">
										${item.index + 1} </a></td>
								<td><a href="searchAssetHistory/${asset.id}" target="popup"
									onclick="window.open('searchAssetHistory/${asset.id}','popup','width=700,height=600'); return false;">
										${asset.assetHeader.assetCategory.name } </a></td>
								<td><a href="searchAssetHistory/${asset.id}" target="popup"
									onclick="window.open('searchAssetHistory/${asset.id}','popup','width=700,height=600'); return false;">
										${asset.assetHeader.department.name }</a></td>
								<td><a href="searchAssetHistory/${asset.id}" target="popup"
									onclick="window.open('searchAssetHistory/${asset.id}','popup','width=700,height=600'); return false;">
										${asset.code }</a></td>
								<td><a href="searchAssetHistory/${asset.id}" target="popup"
									onclick="window.open('searchAssetHistory/${asset.id}','popup','width=700,height=600'); return false;">
										${asset.assetHeader.assetName}</a></td>
								<td><a href="searchAssetHistory/${asset.id}" target="popup"
									onclick="window.open('searchAssetHistory/${asset.id}','popup','width=700,height=600'); return false;">
										${asset.grossValue }</a></td>
								<%-- <td>
								<a href="${contextPath}/asset/editform/${asset.id}"><input type="button" class="btn btn-default" value="Details"/></a>
								</td> --%>
								<%-- <c:if test="${isReference}">
									<td><input type="button" class="btn btn-default"
										value="Select"
										onclick="selectAssetRef('${asset.code}','${asset.assetHeader.assetName }')" />
									</td>
								</c:if> --%>
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
	</div>
	<!-- Result Table Ends -->
</div>

<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>


<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.min.css">
<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/buttons/2.1.0/css/buttons.dataTables.min.css">
<script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>

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
    $(document).ready(function() {
   $('#resultHeader').DataTable( {
       dom: 'Bfrtip',
       aaSorting : [],
       buttons: [
           'copy', 'csv', 'excel', 'pdf', 'print'
       ]
   } );
} );

function  selectAssetRef(code, name){
console.log("My value..."+code);
console.log("My value..."+name);
var retVal = name + '~'+ code; 
window.opener.onPopupClose(retVal);//myValue is the value you want to return to main javascript
window.close();
}
</script>
