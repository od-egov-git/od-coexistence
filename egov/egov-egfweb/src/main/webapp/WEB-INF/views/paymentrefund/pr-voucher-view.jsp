<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>

<div class="row">
	<div class="col-md-12">
		<div>
			<div class="panel panel-primary" data-collapsed="0">
				<div>
					<div class="panel-title">
						<div class="subheadnew">Voucher View</div>
					</div>
				</div>
				<div class="panel-body">
					<table border="0" width="100%" cellspacing="0">
						<tr>
							<td width="10%" class="greybox"><b>Voucher Number :  </b></td>
							<td width="25%" class="greybox">
								${voucherDetails.name}
							</td>
							<td width="10%" class="greybox"><b>Date :</b></td>
							<td width="25%" class="greybox">
								<fmt:formatDate pattern="dd/MM/yyyy" value="${voucherDetails.voucherDate}" var="voucherDate" />
								<c:out value="${voucherDate}" />
							</td>
						</tr>
						<tr>
							<td width="10%" class="greybox"><b>Fund :  </b></td>
							<td width="25%" class="greybox">
								${voucherDetails.fundName}
							</td>
							<td width="10%" class="greybox"><b>Scheme :</b></td>
							<td width="25%" class="greybox">
								${voucherDetails.scheme}
							</td>
						</tr>
						<tr>
							<td width="10%" class="greybox"><b>Sub Scheme :  </b></td>
							<td width="25%" class="greybox">
								${voucherDetails.subScheme}
							</td>
							<td width="10%" class="greybox"><b>Financing Source :</b></td>
							<td width="25%" class="greybox">
								${voucherDetails.financeSource}
							</td>
						</tr>
						<tr>
							<td width="10%" class="greybox"><b>Department :  </b></td>
							<td width="25%" class="greybox">
								${voucherDetails.deptName}
							</td>
							<td width="10%" class="greybox"><b>Narration :</b></td>
							<td width="25%" class="greybox">
								${voucherDetails.narration}
							</td>
						</tr>
						<tr>
						    <td width="10%" class="greybox"><b>Sub Division :  </b></td>
							<td width="25%" class="greybox">
								${voucherDetails.subdivision}
							</td>
							<td width="10%" class="greybox"><b>BAN Number :  </b></td>
							<td width="25%" class="greybox">
								${voucherDetails.banNumber}
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div align="center">
			<br />
			<table border="1" width="100%" cellspacing="0">
				<tr>
					<th colspan="5">
						<div class="subheadsmallnew">Account Details</div>
					</th>
				</tr>
				<tr>
					<th class="bluebgheadtd" width="18%">Function Name</th>
					<th class="bluebgheadtd" width="17%">Account&nbsp;Code</th>
					<th class="bluebgheadtd" width="19%">Account Head</th>
					<th class="bluebgheadtd" width="17%">Debit&nbsp;Amount(Rs)</th>
					<th class="bluebgheadtd" width="16%">Credit&nbsp;Amount(Rs)</th>
				</tr>
				<c:choose>
					<c:when test="${!accountDetails.isEmpty()}">
						<c:forEach items="${accountDetails}" var="accountDetail">
							<tr>
								<td width="18%" class="bluebox setborder" style="text-align: center">
									<c:out value="${accountDetail.function}" />
								</td>
								<td width="17%" class="bluebox setborder" style="text-align: center">
									<c:out value="${accountDetail.glcode}" />
								</td>
								<td width="19%" class="bluebox setborder">
									<c:out value="${accountDetail.accounthead}" />
								</td>
								<td width="17%" class="bluebox setborder" style="text-align: right">
									<c:out value="${accountDetail.debitamount}" />
								</td>
								<td width="16%" class="bluebox setborder" style="text-align: right">
									<c:out value="${accountDetail.creditamount}" />
								</td>
							</tr>
						</c:forEach>
						<tr>
							<td class="greybox setborder" style="text-align: right" colspan="3">
								<b>Total</b>
							</td>
							<td class="greybox setborder" style="text-align: right">
								<fmt:formatNumber value="${dbAmount}" pattern="#0.00" />
							</td>
							<td class="greybox setborder" id="voucherAmount" style="text-align: right" >
								<fmt:formatNumber value="${crAmount}" pattern="#0.00" />
							</td>					
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="bluebox setborder" style="text-align: center" colspan="5">No Record Found.</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
			<br />
			<table border="1" width="100%" cellspacing="0">
				<tr>
					<th colspan="5">
						<div class="subheadsmallnew">
							Sub-ledger Details
						</div>
					</th>
				</tr>
				<tr>
					<th class="bluebgheadtd" width="18%">Function Name</th>
					<th class="bluebgheadtd" width="18%">Account Code</th>
					<th class="bluebgheadtd" width="17%">Detailed Type</th>
					<th class="bluebgheadtd" width="19%">Detailed Key</th>
					<th class="bluebgheadtd" width="17%">Amount(Rs)</th>
				</tr>
				<c:choose>
					<c:when test="${!subLedgerlist.isEmpty()}">
						<c:forEach items="${subLedgerlist}" var="subLedger">
							<tr>
								<td width="18%" class="bluebox setborder" style="text-align: center">
									<c:out value="${subLedger.functionDetail}" />
								</td>
								<td width="17%" class="bluebox setborder" style="text-align: center">
									<c:out value="${subLedger.glcode.glcode}" />
								</td>
								<td width="19%" class="bluebox setborder">
									<c:out value="${subLedger.detailType.description}" />
								</td>
								<td width="17%" class="bluebox setborder" style="text-align: right">
									<c:out value="${subLedger.detailKey}" />
								</td>
								<td width="16%" class="bluebox setborder" style="text-align: right">
									<c:out value="${subLedger.amount}" />
								</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="bluebox setborder" style="text-align: center" colspan="5">No Record Found.</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</div>
	</div>
</div>