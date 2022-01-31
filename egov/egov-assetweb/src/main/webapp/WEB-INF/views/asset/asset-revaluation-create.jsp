<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>

<%@ page isELIgnored="false"%>
<div class="panel-title" align="center">
	<spring:message code="lbl.asset.revaluate"
		text="Create Asset Revaluation" />
</div>
<br />

<!-- <div class="form-group"> -->
<form:form name="assetRevaluation" method="post" action="create"
	modelAttribute="assetRevaluation"
	class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<input type="hidden" name="viewmode" id="viewmode" value="readonly" />
	<form:hidden id="updatedCurrentValue"
		path="assetRevaluation.updatedCurrentValue" />
	<!-- Header Details -->
	<br />
	<br />
	<div class="formmainbox">
		<br />
		<div id="labelAD" align="center">
			<table width="89%" border=0>
				<th><spring:message code="lbl-asset-details"
						text="Asset Details" /></th>
			</table>
		</div>
		<br />
		<table>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-code" text="Asset Code" />
				</label>
					<div class="col-sm-6 add-margin">
						<a href="#" id="assetLink"
							onclick="return openAsset('${assetRevaluation.assetMaster.id}');">${assetRevaluation.assetMaster.code}</a>
					</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-name" text="Asset Name" />
				</label>
					<div class="col-sm-6 add-margin">
						${assetRevaluation.assetMaster.assetHeader.assetName }</div></td>

			</tr>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-desc" text="Description" />
				</label>
					<div class="col-sm-6 add-margin">
						${assetRevaluation.assetMaster.assetHeader.description }</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-cat" text="Asset Category Name" />
				</label>
					<div class="col-sm-6 add-margin">
						${assetRevaluation.assetMaster.assetHeader.assetCategory.name }</div></td>
			</tr>

		</table>
	</div>
	<!-- Revaluation Details -->
	<br />
	<br />
	<div class="formmainbox">
		<br />
		<div id="labelRD" align="center">
			<table width="89%" border=0>
				<th><spring:message code="lbl-reval-details"
						text="Asset Revaluation Details" /></th>
			</table>
		</div>
		<br />
		<table>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-order-no" text="Revaluation Order No." /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:input class="form-control" required="required"
							path="assetRevaluation.rev_order_no" required="required" />
					</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-date" text="Revaluation Order Date" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:input id="rev_order_date"
							path="assetRevaluation.rev_order_date"
							class="form-control datepicker" data-date-end-date="0d"
							required="required" placeholder="DD/MM/YYYY" />
					</div></td>

			</tr>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-value" text="Value After Revaluation" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:input id="value_after_revaluation"
							class="form-control text-left" required="required"
							readonly="true" data-pattern="numeric"
							path="assetRevaluation.value_after_revaluation" />
					</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-change" text="Type Of Change" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetRevaluation.type_of_change"
							onchange="changeValues();" id="type_of_change"
							required="required" class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:option value="Increased">Increased</form:option>
							<form:option value="Decreased">
								<spring:message code="Decreased" />
							</form:option>
						</form:select>
					</div></td>
			</tr>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-add-sub" text="Addition/Deduction" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:input id="add_del_amt" class="form-control text-left"
							required="required" onchange="changeValues();"
							data-pattern="numeric" path="assetRevaluation.add_del_amt" />
					</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-cuurent-value" text="Current Value of the Asset" />
						<span class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:input id="current_value" class="form-control text-left"
							required="required" readonly="true" data-pattern="numeric"
							path="assetRevaluation.current_value" />
					</div></td>
			</tr>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-reason" text="Reason For Revaluation" />
				</label>
					<div class="col-sm-6 add-margin">
						<form:textarea path="assetHeader.reason" id="reason"
							class="form-control" maxlength="1000"></form:textarea>
					</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-date-no" text="Date of Revaluation" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:input id="rev_date" path="assetRevaluation.rev_date"
							class="form-control datepicker" data-date-end-date="0d"
							required="required" placeholder="DD/MM/YYYY" />
					</div></td>
			</tr>

		</table>
	</div>
	<!-- Accounting Details -->
	<br />
	<br />
	<div class="formmainbox">
		<br />
		<div id="labelAccD" align="center">
			<table width="89%" border=0>
				<th><spring:message code="asset-acc-details"
						text="Accounting Details" /></th>
			</table>
		</div>
		<br />
		<table>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-acc-code" text="Asset Account Code" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">${ assetRevaluation.assetMaster.assetHeader.assetCategory.assetAccountCode.glcode}
						- ${ assetRevaluation.assetMaster.assetHeader.assetCategory.assetAccountCode.name}
					</div></td>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-res-acc-code"
							text="Revaluation Reserve Account Code" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">${ assetRevaluation.assetMaster.assetdocument.getElementById("add_del_amt");Header.assetCategory.revolutionReserveAccountCode.glcode}
						- ${ assetRevaluation.assetMaster.assetHeader.assetCategory.revolutionReserveAccountCode.name}
					</div></td>

			</tr>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-fund" text="fund" /> <span class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetRevaluation.fund" id="fund"
							required="required" class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${fundList}" itemValue="id" itemLabel="name" />
						</form:select>
					</div></td>
				<td>> <label class="col-sm-3 control-label text-right">
						<spring:message code="asset-function" text="function" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:select path="assetRevaluation.function"
							id="assetRevaluation.function" class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${functionList}" itemValue="id"
								itemLabel="name" />
						</form:select>
					</div>
				</td>
			</tr>

		</table>
	</div>
	<!-- Asset Status -->
	<br />
	<br />
	<div class="formmainbox">
		<br />
		<div id="labelAppD" align="center">
			<table width="89%" border=0>
				<th><spring:message code="asset-approve-details"
						text="Approved Details" /></th>
			</table>
		</div>
		<br />
		<table>
			<tr>
				<td><label class="col-sm-6 control-label text-right"> <spring:message
							code="asset-reval-acc-comment" text="Comment" /> <span
						class="mandatory"></span>
				</label>
					<div class="col-sm-6 add-margin">
						<form:textarea path="assetHeader.comment" id="comment"
							class="form-control" maxlength="1000"></form:textarea>
					</div></td>

			</tr>

		</table>
	</div>


	<div align="center" class="buttonbottom">
		<div class="row text-center">
			<input type="submit" class="btn btn-primary" name="create"
				value="Create" /> <input type="button" name="button2" id="button2"
				value="Close" class="btn btn-default"
				onclick="window.parent.postMessage('close','*');window.close();" />
		</div>
	</div>
</form:form>


<script
	src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script>
	function openAsset(assetId) {
		var sourcepath = "/services/asset/assetcreate/editform/" + assetId;
		window
				.open(sourcepath, 'Asset',
						'resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');
	}

	function changeValues() {
		var type = document.getElementById("type_of_change");
		var add_del = document.getElementById("add_del_amt");
		var curr = document.getElementById("current_value").value;
		var updatedRevaluatedData = 0;
		if (!(type != null && type != '')) {
			bootbox.alert("Please select Type of Change");
			updatedRevaluatedData = 0;
		}
		else
			{
				if (!(add_del != null && add_del != '')) {
					bootbox.alert("Please Fill Addition/Deduction");
					add_del = 0;
				}
	
				if (type == 'Increased') {
					updatedRevaluatedData = curr + add_del
				} else {
					updatedRevaluatedData = curr - add_del
				}
			}
		if(updatedRevaluatedData < 0)
			{
				bootbox.alert("Deduction cannot be more than current value");
			}
		if(updatedRevaluatedData == 0)
			{
			updatedRevaluatedData ="";
			}
		document.getElementById("value_after_revaluation").value=updatedRevaluatedData;
		
		
	}
</script>

