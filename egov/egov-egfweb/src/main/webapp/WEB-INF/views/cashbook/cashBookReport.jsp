<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="cashBookReport" role="form" method="post"
	action="searchCashBookReportData" modelAttribute="cashBookReport"
	id="cashBookReport" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
	<div class="tab-pane fade in active">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="form-group" style="padding: 50px 20px 35px;">
				<label class="col-sm-3 control-label text-left-audit1">From
					Date <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="fromDate" path="fromDate"
						class="form-control datepicker" data-date-end-date="0d"
						placeholder="DD/MM/YYYY" />

				</div>
				<label class="col-sm-3 control-label text-left-audit1">To
					Date<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="toDate" path="toDate"
						class="form-control datepicker" data-date-end-date="0d"
						placeholder="DD/MM/YYYY" />

				</div>
			</div>
		</div>
	</div>
	<div class="buttonbottom" align="center">
		<input type="submit" id="search"
			class="btn btn-primary btn-wf-primary" name="search" value="Search" />
	</div>
	<br>
	<div class="tab-pane fade in active" id="resultheader">
		<h3>Search Result</h3>
		<div class="panel panel-primary" data-collapsed="0">

			<div style="padding: 0 15px;">
				<c:if
					test="${cashBookReport.titleName != null &&  !cashBookReport.titleName.isEmpty()}">
					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<tr>
							<th class="bluebgheadtd" width="100%" colspan="5"><strong
								style="font-size: 15px;"> ${cashBookReport.titleName}</strong></th>
						</tr>
					</table>
				</c:if>
				<c:if
					test="${cashBookReport.header != null &&  !cashBookReport.header.isEmpty()}">
					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<tr>
							<th class="bluebgheadtd" width="100%" colspan="5"><strong
								style="font-size: 15px;"> ${cashBookReport.header}</strong></th>
						</tr>
					</table>
				</c:if>
				<table class="table table-bordered" id="searchResult">
					<thead>
						<tr>
							<th colspan="7">Receipt Side</th>
							<th colspan="7">Payment Side</th>
						</tr>
						<tr>
							<th>Date</th>
							<th>Voucher No.</th>
							<th>Particulars</th>
							<th>L.F</th>
							<th>Cash</th>
							<th>Chq No.</th>
							<th>Amt</th>
							<th>Date</th>
							<th>Voucher No.</th>
							<th>Particulars</th>
							<th>L.F</th>
							<th>Cash</th>
							<th>Chq No.</th>
							<th>Amt</th>
						</tr>
					</thead>
					`
					<c:if
						test="${cashBookReport.cashBookResultList != null &&  !cashBookReport.cashBookResultList.isEmpty()}">
						<tbody>
							<c:forEach items="${cashBookReport.cashBookResultList}"
								var="result" varStatus="status">
								
								<tr>
									<td>${result.receiptVoucherDate}</td>
									<td>${result.receiptVoucherNumber }</td>
									<td><c:choose><c:when test="${result.receiptParticulars == 'Total'}">
											<strong>${result.receiptParticulars}&nbsp;</strong>
										</c:when>
										<c:otherwise>
										${result.receiptParticulars}&nbsp;
										</c:otherwise></c:choose>
										</td>
									<td></td>
									<td>
										<div align="right">
										<c:choose>
											<c:when test="${result.receiptParticulars == 'Total'}">
												<strong>${result.receiptCash}</strong>
											</c:when>
											<c:otherwise>
												<c:if test="${result.receiptCash != null}">
												${result.receiptCash}&nbsp;
											</c:if>
											</c:otherwise>
											</c:choose>
										</div>
									</td>
									<td><c:choose><c:when
											test="${result.receiptChequeDetail == 'MULTIPLE'}">
										${result.receiptChequeDetail}&nbsp;
						</c:when> <c:otherwise>
										${result.receiptChequeDetail}&nbsp;</c:otherwise>
										</c:choose></td>
									<td>
										<div align="right">
										<c:choose>
											<c:when test="${result.receiptParticulars == 'Total'}">
												<strong>${result.receiptAmount}</strong>
											</c:when>
											<c:otherwise>
												<c:if test="${result.receiptAmount != null}">
												${result.receiptAmount}&nbsp;
											</c:if>
											</c:otherwise>
											</c:choose>
										</div>
									</td>
									<td>${result.paymentVoucherDate }</td>
									<td>${result.paymentVoucherNumber}</td>
									<td><c:choose><c:when test="${result.paymentParticulars == 'Total'}">
											<strong>${result.paymentParticulars}&nbsp;
										</c:when> <c:otherwise>
										${result.paymentParticulars}&nbsp;
						</c:otherwise></c:choose></td>
									<td></td>
									<td>
										<div align="right">
										<c:choose>
											<c:when test="${result.paymentParticulars == 'Total'}">
												<strong>${result.paymentCash}</strong>
											</c:when>
											<c:otherwise>
												<c:if test="${result.paymentCash != null}">
												${result.paymentCash}&nbsp;
											</c:if>
											</c:otherwise>
											</c:choose>
										</div>
									</td>
									<td><c:choose><c:when
											test="${result.paymentChequeDetail == 'MULTIPLE'}">
										${result.paymentChequeDetail}&nbsp;
						</c:when> <c:otherwise>
										${result.paymentChequeDetail}&nbsp;</c:otherwise></c:choose></td>
									<td>
										<div align="right">
										<c:choose>
											<c:when test="${result.paymentParticulars == 'Total'}">
												<strong>${result.paymentAmount}</strong>
											</c:when>
											<c:otherwise>
												<c:if test="${result.paymentAmount != null}">
												${result.paymentAmount}&nbsp;
											</c:if>
											</c:otherwise>
											</c:choose>
										</div>
									</td>
								</tr>
							</c:forEach>
						
						<tbody>
					</c:if>
					<c:if
						test="${cashBookReport.cashBookResultList == null ||  cashBookReport.cashBookResultList.isEmpty()}">
					No records found
					</c:if>
				</table>
			</div>
			<br> <br>
		</div>


	</div>
	<!-- <div class="buttonbottom" align="center">
		<input type="submit" class="btn btn-primary btn-wf-primary"
			id="exportpdf" name="exportpdf" value="EXPORT" />
	</div> -->

</form:form>
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
	    $('#searchResult').DataTable( {
	        dom: 'Bfrtip',
	        aaSorting : [],
	        buttons: [
		        {
	        	extend: 'pdfHtml5',
	        	orientation : 'landscape',
	            pageSize : 'A3', // You can also use "A1","A2" or "A0", most of the time "A3" works the best.
	            text : '<i class="fa fa-file-pdf-o"> PDF</i>',
	            titleAttr : 'PDF'
		        }
	        ]
	    } );
	} );
</script>
