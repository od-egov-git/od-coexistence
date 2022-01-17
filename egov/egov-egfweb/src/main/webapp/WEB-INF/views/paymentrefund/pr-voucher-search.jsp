<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>

<form:form role="form" action="/ajax/_voucherSearch" modelAttribute="voucherSearch" id="voucherSearchform" cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="lbl.voucher.search" text="Voucher Search"/>
					</div>
				</div>
				<div class="panel-body">
					<div class="form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.type" text="Voucher Type"/> <span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:select path="voucherType" id="voucherType" cssClass="form-control" required="required" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${voucherTypeList}"/>
							</form:select>
						</div>
						 <label class="col-sm-3 control-label text-right"></label>
						<div class="col-sm-3 add-margin">
							<a href="/services/EGF/refund/_paymentRequestblankvoucherForm">Refund request for amount collected before 01/06/2020</a>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.number" text="Voucher Number"/> </label>
						<div class="col-sm-3 add-margin">
							<form:input cssClass="form-control patternvalidation" data-pattern="address" maxlength="100" id="voucherNumber" onblur="changeField();"  path="voucherNumber" />
						</div>	
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.name" text="Voucher Name"/> </label>
						<div class="col-sm-3 add-margin">
							<form:select path="voucherName" id="voucherName" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
							</form:select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.fromdate" text="From Date"/> <span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:input path="fromDate" cssClass="form-control datepicker" title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}"
										data-date-end-date="0d" id="fromDate" data-inputmask="'mask': 'd/m/y'" required="required" />
							<form:errors path="fromDate" cssClass="add-margin error-msg" />
						</div>
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.todate" text="To Date"/> <span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:input path="toDate" cssClass="form-control datepicker" title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}"
										data-date-end-date="0d" id="toDate" data-inputmask="'mask': 'd/m/y'" required="required" />
							<form:errors path="toDate" cssClass="add-margin error-msg" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.fund" text="Fund"/> <span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:select path="fundId" id="fundId" cssClass="form-control" required="required" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${fundList}" itemValue="id" itemLabel="name" />
							</form:select>
						</div>
						<label class="col-sm-3 control-label text-right"><spring:message code="voucher.department" text="Department"/> </label>
						<div class="col-sm-3 add-margin">
							<form:select path="deptCode" id="deptCode" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${departmentList}" itemValue="code" itemLabel="name" />
							</form:select>
						</div>
					</div>					
					<div class="form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.payee.name" text="Payee Name"/></label>
						<div class="col-sm-3 add-margin">
							<form:input path="partyName" id="partyName" class="form-control text-left" maxlength="50" />
						</div>
					</div>					
					<div class="form-group receipt-info" style="display: none;">
					   <label class="col-sm-3 control-label text-right"><spring:message code="lbl.receipt.no" text="Receipt Number"/></label>
						<div class="col-sm-3 add-margin">
							<form:input path="receiptNumber" id="receiptNumber" class="form-control text-left" maxlength="50" />
						</div>
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.service.type" text="Service Type"/></label>
						<div class="col-sm-3 add-margin">
							<form:select path="serviceType" id="serviceType" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${serviceTypeList}" itemValue="code" itemLabel="businessService"/>
							</form:select>
						</div>
					</div>					
				</div>
			</div>
		</div>
	</div>
	<div class="text-center">
		<button type='button' class='btn btn-primary' id="btnsearch">
			<spring:message code='lbl.search' text="Search"/>
		</button>		
	</div>
</form:form>

<div class="row display-hide report-section">
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl" id="resultTable">
			<thead>
				<tr>
					<th>Sl No</th>
					<th>Action</th>
					<th>Voucher Number</th>
					<th>Receipt Number</th>
					<th>Voucher Type</th>
					<th>Voucher Name</th>
					<th>Voucher Date</th>
					<th>Fund Name</th>
					<th>Department Name</th>
					<th>Service</th>
					<th>Payee Name</th>
					<th>Payee Address</th>
					<th>Total Amount</th>
					<th>Status</th>
					<th>Pending With</th>
				</tr>
			</thead>
		</table>
	</div>
</div>

<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"	type="text/javascript"></script>
<script src="<cdn:url value='/resources/app/js/pr-voucher-search-helper.js?rnd=${app_release_no}'/>"></script>


