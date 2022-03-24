<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<script>
function exportToPDF() {
	alert('ok');
	var fromDate = document.getElementById('fromDate');
	var toDate = document.getEl;emetById('toDate');
	alert(fromDate);
	alert(toDate);
	var url = "/services/EGF/cashBookReport/cashFlow/searchCashFlowReportExportData?fromDate="+fromDate+"&toDate="+toDate;
	
	window.location.href(url);
}
</script>
<form:form name="cashBookReport" role="form" method="post"
	action="searchCashFlowReportData" modelAttribute="cashFlowReport"
	id="cashFlowReport" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
	<div class="tab-pane fade in active">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="form-group" style="padding: 50px 20px 35px;">
				<label class="col-sm-3 control-label text-left-audit1">From Date<span
					class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<form:input id="fromDate" path="fromDate"
						class="form-control datepicker" data-date-end-date="0d"
						placeholder="DD/MM/YYYY" />

				</div>
				<label class="col-sm-3 control-label text-left-audit1">
				To Date<span class="mandatory"></span></label>
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
	<!-- <div class="tab-pane fade in active" id="resultheader"> -->
	<div id="resultheader">
		<h3>Search Result</h3>
		<div class="panel panel-primary" data-collapsed="0">

			<div style="padding: 0 15px;">
			<div style="padding: 0 15px;">
	        	<c:if test="${cashFlowReport.titleName != null &&  !cashFlowReport.titleName.isEmpty()}">
	        		<table width="100%" border="1" cellspacing="0" cellpadding="0">
				<tr>
					<th class="bluebgheadtd" width="100%" colspan="5"><strong
						style="font-size: 15px;"> ${cashFlowReport.titleName}</strong></th>
				</tr>
			</table>
	        	</c:if>
	        	<c:if test="${cashFlowReport.header != null &&  !cashFlowReport.header.isEmpty()}">
	        		<table width="100%" border="1" cellspacing="0" cellpadding="0">
				<tr>
					<th class="bluebgheadtd" width="100%" colspan="5"><strong
						style="font-size: 15px;"> ${cashFlowReport.header}</strong></th>
				</tr>
			</table>
	        	</c:if>
				<table class="table table-bordered" id="searchResult">
					<thead>
						<tr>
							<th width="70%"></th>
							<th width="15%" aligh="center" colspan="2">Current Year</th>
							<th width="15%" aligh="center" colspan="2">Previous Year</th>
						</tr>

					</thead>

					<tbody>
						<c:if
							test="${cashFlowReport.cashFlowResultList != null &&  !cashFlowReport.cashFlowResultList.isEmpty()}">
							<c:forEach items="${cashFlowReport.cashFlowResultList}"
								var="result" varStatus="status">
								<tr>

									<td colspan="5">A. Cash flows from operating activities</td>
								</tr>
								<tr>
									<td>Gross Surplus/(deficit) over expenditure</td>
									<td></td>
									<td aligh="center">${result.incomeOverExpenditureCurr}</td>
									<td></td>
									<td aligh="center">${result.incomeOverExpenditurePrevYear }</td>
								</tr>
								<tr>
									<td colspan="5">Add:</td>
									
								</tr>
								<tr>
									<td>Depreciation</td>
									<td aligh="center">${result.depreciationCurr}</td>
									<td></td>
									<td aligh="center">${result.depreciationPrevYear }</td>
									<td></td>
								</tr>
								<tr>
									<td>Interest and finance charges</td>
									<td aligh="center">${result.interestFinanceChargesCurrYear }</td>
									<td></td>
									<td aligh="center">${result.interestFinanceChargesPrevYear }</td>
									<td></td>
								</tr>
								<tr>
									<td colspan="5">Less:</td>
									
								</tr>
								<tr>
									<td>Profit on disposal of assets</td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Dividend Income</td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Investment Income</td>
									<td>${result.investmentIncomeCurrYear }</td>
									<td></td>
									<td>${result.investmentIncomePrevYear }</td>
									<td></td>
								</tr>
								<tr>
									<td>Adjusted income over expenditure before effecting
										changes in current assets and current</td>
										<td></td>
									<td aligh="center">${result.adjustedIncomeCurrYear}</td>
									<td></td>
									<td aligh="center">${result.adjustedIncomePrevYear }</td>
								</tr>
								<tr>
									<td colspan="5">Changes in Current assets and Current liabilities</td>
									
								</tr>
							</c:forEach>
						</c:if>
						<c:if
							test="${cashFlowReport.finalBalanceSheetL != null &&  !cashFlowReport.finalBalanceSheetL.isEmpty()}">
							<c:forEach items="${cashFlowReport.finalBalanceSheetL}"
								var="result" varStatus="status">
								<tr>
									<td>(Increase) / decrease in Sundry debtors</td>
									<td></td>
									<td aligh="center">${result.sundryDebtorsCurrYear }</td>
									<td></td>
									<td aligh="center">${result.sundryDebtorsPrevYear }</td>
								</tr>
								<tr>
									<td>(Increase) / decrease in Stock in hand</td>
									<td></td>
									<td aligh="center">${result.stockInhandCurrYear }</td>
									<td></td>
									<td aligh="center">${result.stockInhandPrevYear }</td>
								</tr>
								<tr>
									<td>(Increase) / decrease in prepaid expenses</td>
									<td></td>
									<td aligh="center">${result.prepaidExpenseCurrYear }</td>
									<td></td>
									<td aligh="center">${result.prepaidExpensePrevYear }</td>
								</tr>
								<tr>
									<td>(Increase) / decrease in other current assets</td>
									<td></td>
									<td aligh="center">${result.otherCurrentAssetsCurrYear }</td>
									<td></td>
									<td aligh="center">${result.otherCurrentAssetsPrevYear }</td>
								</tr>
								<tr>
									<td>(Decrease)/ increase in Deposits received</td>
									<td></td>
									<td aligh="center">${result.depositsReceivedCurrYear }</td>
									<td></td>
									<td aligh="center">${result.depositsReceivedPrevYear }</td>
								</tr>
								<tr>
									<td>(Decrease)/ increase in Deposits works</td>
									<td></td>
									<td aligh="center">${result.depositWorksCurrYear }</td>
									<td></td>
									<td aligh="center">${result.depositWorksPrevYear }</td>
								</tr>
								<tr>
									<td>(Decrease)/ increase in other current liabilities</td>
									<td></td>
									<td aligh="center">${result.otherCurrentLiabilitiesCurrYear }</td>
									<td></td>
									<td aligh="center">${result.otherCurrentLiabilitiesPrevYear }</td>
								</tr>
								<tr>
									<td>(Decrease)/ increase in provisions</td>
									<td></td>
									<td aligh="center">${result.increaseInProvisionCurrYear }</td>
									<td></td>
									<td aligh="center">${result.increaseInProvisionPrevYear }</td>
								</tr>
							</c:forEach>
						
						<tr>
							<td colspan="5">Extra ordinary items (Specify)</td>
							
						</tr>
						<tr>
							<td>(Decrease)/ increase in Deposits received</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td>(Decrease)/ increase in Deposits works</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td>(Decrease)/ increase in other current liabilities</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						
						</c:if>
						<c:if
							test="${cashFlowReport.aCurrentYear != 0 ||  cashFlowReport.aPrevYear != 0}">
						<tr>
							<td>Net cash generated from/ (used in) operating activities
								(a)</td>
								<td></td>
							<td aligh="center">${cashFlowReport.aCurrentYear}</td>
							<td></td>
							<td aligh="center">${cashFlowReport.aPrevYear }</td>
						</tr>
						</c:if>
						
						<c:if
							test="${cashFlowReport.finalBalanceSheetL != null &&  !cashFlowReport.finalBalanceSheetL.isEmpty()}">
							<tr>
							<td colspan="5">B. Cash flows from investing activities</td>
						</tr>
							<c:forEach items="${cashFlowReport.finalBalanceSheetL}"
								var="result" varStatus="status">
								<tr>
									<td>(Purchase) of fixed assets & CWIP</td>
									<td aligh="center">${result.cwipCurrYear }</td>
									<td></td>
									<td aligh="center">${result.cwipPrevYear }</td>
									<td></td>
								</tr>
								<tr>
									<td>(Increase) / Decrease in Special funds/grants</td>
									<td aligh="center">${result.specialFundsGrantsCurrYear }</td>
									<td></td>
									<td aligh="center">${result.specialFundsGrantsPrevYear }</td>
									<td></td>
								</tr>
								<tr>
									<td>(Increase) / Decrease in Earmarked funds</td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>(Purchase) of Investments</td>
									<td>${result.investmentsCurrYear }</td>
									<td></td>
									<td>${result.investmentsPrevYear }</td>
									<td></td>

								</tr>
								<tr>
									<td colspan="5">Add:</td>
									
								</tr>
								<tr>
									<td>Proceeds from disposal of assets</td>
									<td>${result.disposalOfAssetsCurrYear }</td>
									<td></td>
									<td>${result.disposalOfAssetsPrevYear }
									<td></td>
								</tr>
								<tr>
									<td>Proceeds from disposal of investments</td>
									<td aligh="center">${result.disposalOfInvestmentsCurrYear}</td>
									<td></td>
									<td aligh="center">${result.disposalOfInvestmentsPrevYear }</td>
									<td></td>
								</tr>

							</c:forEach>
						</c:if>
						<c:if
							test="${cashFlowReport.cashFlowResultList != null &&  !cashFlowReport.cashFlowResultList.isEmpty()}">
							<c:forEach items="${cashFlowReport.cashFlowResultList}"
								var="result" varStatus="status">
								<tr>
									<td>Investment income received</td>
									<td aligh="center">${result.investmentIncomeReceivedCurrYear }</td>
									<td></td>
									<td aligh="center">${result.investmentIncomeReceivedPrevYear }</td>
									<td></td>
								</tr>
								<tr>
									<td>Interest income received</td>
									<td aligh="center">${result.interestIncomeReceivedCurrYear }</td>
									<td></td>
									<td aligh="center">${result.interestIncomeReceivedPrevYear }</td>
									<td></td>
								</tr>
							</c:forEach>
						</c:if>
						<c:if
							test="${cashFlowReport.flag == show}">
							<tr>
								<td>Net cash generated from/ (used in) investing activities (b)</td>
								<td></td>
								<td aligh="center">${cashFlowReport.bCurrentYear }</td>
								<td></td>
								<td aligh="center">${cashFlowReport.bPrevYear }</td>
							</tr>
							</c:if>
							<c:if
							test="${cashFlowReport.flag == show}">
							<tr>
								<td colspan="5">C. Cash flows from financing activities</td>
							</tr>
							<tr>
								<td colspan="5">Add:</td>
								
							</tr>
							<tr>
								<td>Loans from banks/others received</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Less:</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Loans repaid during the period</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Loans & advances to employees</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Loans to others</td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Finance expenses</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							
							<tr>
								<td>Net cash generated from (used in) financing activities (c)</td>
								<td></td>
								<td aligh="center">${cashFlowReport.cCurrentYear }</td>
								<td></td>
								<td aligh="center">${cashFlowReport.cPrevYear }</td>
							</tr>
							</c:if>
							<c:if
							test="${cashFlowReport.abcCurrentYear != 0 &&  cashFlowReport.abcPrevYear != 0}">
							
							<tr>
								<td>Net increase/ (decrease) in cash and cash equivalents (a + b + c)</td>
								<td></td>
								<td aligh="center">${cashFlowReport.abcCurrentYear }</td>
								<td></td>
								<td aligh="center">${cashFlowReport.abcPrevYear }</td>
							</tr>
							</c:if>
							<c:if
							test="${cashFlowReport.atBeginingCurr != 0 ||  cashFlowReport.atBeginingPrev != 0}">
							<tr>
								<td>Cash and cash equivalents at beginning of period</td>
								<td></td>
								<td aligh="center">${cashFlowReport.atBeginingCurr }</td>
								<td></td>
								<td aligh="center">${cashFlowReport.atBeginingPrev }</td>
							</tr>
							</c:if>
							<c:if
							test="${cashFlowReport.atEndCurr != 0 ||  cashFlowReport.atEndPrev != 0}">
							<tr>
								<td>Cash and cash equivalents at end of period</td>
								<td></td>
								<td aligh="center">${cashFlowReport.atEndCurr }</td>
								<td></td>
								<td aligh="center">${cashFlowReport.atEndPrev }</td>
							</tr>
							</c:if>
							<c:if
							test="${cashFlowReport.flag == show}">
							<tr>
								<td>Total</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							</c:if>
					</tbody>

				</table>
			</div>
		</div>
		<!-- <div class="buttonbottom" align="center">
			<input type="button" class="btn btn-primary btn-wf-primary"
				id="exportpdf" name="exportpdf" value="EXPORT" onclick="exportToPDF();"/>
		</div> -->
		<div class="buttonbottom" align="center">
			<input type="submit" class="btn btn-primary btn-wf-primary"
				id="exportpdf" name="exportpdf" value="EXPORT"/>
		</div>
	</div>
</form:form>