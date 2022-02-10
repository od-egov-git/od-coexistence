<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="cashBookReport" role="form"
	method="post" action="searchCashBookReportData"
	modelAttribute="cashBookReport"
	id="cashBookReport"
	class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
<div class="tab-pane fade in active">
<div class="panel panel-primary" data-collapsed="0">
		<div class="form-group" style="padding: 50px 20px 250px;">
			<label class="col-sm-3 control-label text-left-audit1">From Date <span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input id="fromDate" path="fromDate" 
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
								
					</div>
				<label class="col-sm-3 control-label text-left-audit1">To Date<span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input id="toDate" path="toDate" 
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
								
					</div>
		</div>
</div>
</div>
<div class="buttonbottom" align="center">
        <input type="submit" id="search" class="btn btn-primary btn-wf-primary" name="search"  value="Search"/>
        </div>
<br>
 <div class="tab-pane fade in active" id="resultheader">
        <h3> Search Result</h3>
	        <div class="panel panel-primary" data-collapsed="0">
	        
	        	<div style="padding: 0 15px;">
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
`					 <c:if test="${cashBookReport.cashBookResultList != null &&  !cashBookReport.cashBookResultList.isEmpty()}">
					<tbody>
					<c:forEach items="${cashBookReport.cashBookResultList}" var="result" varStatus="status">
						<tr>
							<td>
								${result.receiptDate}
						    </td>
							<td>
								${result.recVoucherNo }
							</td>
							<td>
								${result.recParticulars }
							</td>
							<td>
							${result.recLF }
							</td>
							<td>
							${result.recCash}
							</td>
							<td>
							${result.recChequeNo }
							</td>
							<td>
							${result.recAmount }
							</td>
							<td>
							${result.payDate }
							</td>
							<td>
								${result.payVoucherNo}
						    </td>
							<td>
								${result.payParticulars }
							</td>
							<td>
							${result.payLF }
							</td>
							<td>
							${result.payCash}
							</td>
							<td>
							${result.payChequeNo }
							</td>
							<td>
							${result.payAmount }
							</td>
						</tr>
						</c:forEach>
					<tbody>
					</c:if>	
					<c:if test="${cashBookReport.cashBookResultList == null ||  cashBookReport.cashBookResultList.isEmpty()}">
					No records found
					</c:if>				
				</table>
				</div>
			<br>
			<br>
	        </div>
	        
			<div class="buttonbottom" align="center">
        <input type="submit" class="btn btn-primary btn-wf-primary" id="exportpdf" name="exportpdf" value="EXPORT"/>
        </div>
        </div>     
        
</form:form>

		