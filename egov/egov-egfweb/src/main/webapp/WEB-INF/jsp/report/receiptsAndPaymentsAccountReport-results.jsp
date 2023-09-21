

<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<link
	href="/services/EGF/resources/css/budget.css?rnd=${app_release_no}"
	rel="stylesheet" type="text/css" />
<style type="text/css">
@media print {
	div#ieReport {
		display: none;
	}
}
</style>
<div align="center">
	<h6><strong><s:property value="statementheading" /></strong></h6>
</div>
<div id="budgetSearchGrid"
	style="width: 1250px; overflow-x: auto; overflow-y: hidden;">
	<br />
	<div style="overflow-x: scroll; overflow-y: scroll;">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td>
					<div align="center">
						<br />
						<table border="0" cellspacing="0" cellpadding="0"
							class="tablebottom" width="50%" style="float: left">
							<%-- <tr>
								<td colspan="12">
									<div class="subheadsmallnew">
										<strong><s:property value="statementheading" /></strong>
									</div>
								</td>
							</tr>--%>
							<tr>
								<th class="bluebgheadtd"><s:text name="report.accountCode" /></th>
								<th class="bluebgheadtd"><s:text
										name="Perticulars" /></th>

								<s:if
									test="%{receiptsAndPaymentsAccountStatement.getFunds().size()==1}">
									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:text name="Current Year" />( Rs)</th>
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:text name="Previous Year" />( Rs)</th>

									</s:iterator>
								</s:if>
								<s:else>
									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:property value="currentYearToDate" /></th>
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:property value="previousYearToDate" /></th>
										<%--<th class="bluebgheadtd"><s:text name="report.currentYear" /></th>	
										<th class="bluebgheadtd"><s:text name="report.previousYear" /></th>	--%>
									</s:iterator>
								</s:else>

							</tr>
							<s:iterator value="receiptsAndPaymentsAccountStatement.ieEntries"
								status="stat">
								<s:if test="%{type == 'O'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>

									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<td class="blueborderfortd">
											<div align="right">

												<s:if test='%{displayBold == true}'>
													<strong><s:property value="netAmount[name]" />&nbsp;</strong>
												</s:if>
												<s:else>
													<s:property value="netAmount[name]" />&nbsp;</s:else>
											</div>
										</td>
										<td class="blueborderfortd">
											<div align="right">
												<s:if test='%{displayBold == true}'>
													<strong><s:property
															value="previousYearAmount[name]" />&nbsp;</strong>
												</s:if>
												<s:else>
													<s:property value="previousYearAmount[name]" />&nbsp;</s:else>
											</div>
										</td>
									</s:iterator>

								</tr>
								</s:if>
								<s:elseif test="%{type == 'I'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>

									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<td class="blueborderfortd">
											<div align="right">

												<s:if test='%{displayBold == true}'>
													<strong><s:property value="netAmount[name]" />&nbsp;</strong>
												</s:if>
												<s:else>
													<s:property value="netAmount[name]" />&nbsp;</s:else>
											</div>
										</td>
										<td class="blueborderfortd">
											<div align="right">
												<s:if test='%{displayBold == true}'>
													<strong><s:property
															value="previousYearAmount[name]" />&nbsp;</strong>
												</s:if>
												<s:else>
													<s:property value="previousYearAmount[name]" />&nbsp;</s:else>
											</div>
										</td>
									</s:iterator>

								</tr>
								</s:elseif>
							</s:iterator>
						</table>
						
						<table border="0" cellspacing="0" cellpadding="0"
							class="tablebottom" width="50%" style="float: left">
							
							<%--<tr>
								<td colspan="12">
									<div class="subheadsmallnew">
										<strong><s:property value="statementheading" /></strong>
									</div>
								</td>							
							</tr>--%>
							<tr>
								<th class="bluebgheadtd"><s:text name="report.accountCode" /></th>
								<th class="bluebgheadtd"><s:text
										name="Perticulars" /></th>

								<s:if
									test="%{receiptsAndPaymentsAccountStatement.getFunds().size()==1}">
									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:text name="Current Year" />( Rs)</th>
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:text name="Previous Year" />( Rs)</th>

									</s:iterator>
								</s:if>
								<s:else>
									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:property value="currentYearToDate" /></th>
										<th class="bluebgheadtd" width="15%" align="center"
											colspan="1"><s:property value="previousYearToDate" /></th>
										<%--<th class="bluebgheadtd"><s:text name="report.currentYear" /></th>	
										<th class="bluebgheadtd"><s:text name="report.previousYear" /></th>	--%>
									</s:iterator>
								</s:else>

							</tr>
							
							<s:iterator value="receiptsAndPaymentsAccountStatement.ieEntries"
								status="stat">
								<s:if test="%{type == 'E'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>

									<s:iterator value="receiptsAndPaymentsAccountStatement.funds"
										status="stat">
										<td class="blueborderfortd">
											<div align="right">

												<s:if test='%{displayBold == true}'>
													<strong><s:property value="netAmount[name]" />&nbsp;</strong>
												</s:if>
												<s:else>
													<s:property value="netAmount[name]" />&nbsp;</s:else>
											</div>
										</td>
										<td class="blueborderfortd">
											<div align="right">
												<s:if test='%{displayBold == true}'>
													<strong><s:property
															value="previousYearAmount[name]" />&nbsp;</strong>
												</s:if>
												<s:else>
													<s:property value="previousYearAmount[name]" />&nbsp;</s:else>
											</div>
										</td>
									
									</s:iterator>

								</tr>
								</s:if>
							</s:iterator>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
</div>

<div id="budgetSearchGrid"
	style="width: 1250px; overflow-x: auto; overflow-y: hidden;">
	<br />
	<div style="overflow-x: scroll; overflow-y: scroll;">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td>
					<div align="center">
						<br />
						<table border="0" cellspacing="0" cellpadding="0"
							class="tablebottom" width="100%">
							<tr>
								<th class="bluebgheadtd"><s:text name="report.accountCode" /></th>
								<th class="bluebgheadtd"><s:text
										name="Perticulars" /></th>
								<th class="bluebgheadtd" width="15%" align="center"
									colspan="1"><s:text name="Current Period" />( Rs)</th>
											
								<th class="bluebgheadtd" width="15%" align="center"
									colspan="1"><s:text name="Previous Year" />( Rs)</th>
									
								<%-- <th width="1%" align="center"></th> --%>
									
								<th class="bluebgheadtd"><s:text name="report.accountCode" /></th>
								<th class="bluebgheadtd"><s:text
										name="Perticulars" /></th>
								<th class="bluebgheadtd" width="15%" align="center"
									colspan="1"><s:text name="Current Period" />( Rs)</th>
								<th class="bluebgheadtd" width="15%" align="center"
									colspan="1"><s:text name="Previous Year" />( Rs)</th>									

							</tr>
							<s:iterator value="receiptsAndPaymentsAccountStatement.bsEntries"
								status="stat">
								<s:if test="%{type == 'L'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="creditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="creditamount" />&nbsp;</s:else>
										</div>
									</td>								
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevCreditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevCreditamount" />&nbsp;</s:else>
										</div>
									</td>
									<%-- <td></td>--%>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>	
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="debitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="debitamount" />&nbsp;</s:else>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevDebitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevDebitamount" />&nbsp;</s:else>
										</div>
									</td>																		

								</tr>
								</s:if>
								
								
								
								<s:elseif test="%{type == 'A'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="debitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="debitamount" />&nbsp;</s:else>
										</div>
									</td>								
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevDebitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevDebitamount" />&nbsp;</s:else>
										</div>
									</td>
									<%--<td></td> --%>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>	
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="creditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="creditamount" />&nbsp;</s:else>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevCreditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevCreditamount" />&nbsp;</s:else>
										</div>
									</td>																		

								</tr>
								</s:elseif>
								
								<s:elseif test="%{type == 'T'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="creditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="creditamount" />&nbsp;</s:else>
										</div>
									</td>								
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevCreditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevCreditamount" />&nbsp;</s:else>
										</div>
									</td>
									<%-- <td></td>--%>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>	
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="debitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="debitamount" />&nbsp;</s:else>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevDebitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevDebitamount" />&nbsp;</s:else>
										</div>
									</td>																		

								</tr>
								</s:elseif>
								
								<s:elseif test="%{type == 'R'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:text name="Total of Receipts" />
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="creditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="creditamount" />&nbsp;</s:else>
										</div>
									</td>								
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevCreditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevCreditamount" />&nbsp;</s:else>
										</div>
									</td>
									<%-- <td></td>--%>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:text name="Total of Payments" />
											&nbsp;
										</div>
									</td>	
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="debitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="debitamount" />&nbsp;</s:else>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevDebitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevDebitamount" />&nbsp;</s:else>
										</div>
									</td>																		

								</tr>
								</s:elseif>
								<s:elseif test="%{type == 'C'}">
								<tr>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="" />
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="creditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="creditamount" />&nbsp;</s:else>
										</div>
									</td>								
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevCreditamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevCreditamount" />&nbsp;</s:else>
										</div>
									</td>
									<%-- <td></td>--%>
									<td class="blueborderfortd">
										<div align="center">
											<s:if test='%{glCode != ""}'>
												<s:if test='%{displayBold == true}'>

													<strong><s:property value="glCode" /></strong>

												</s:if>
												<s:else>
													<s:property value="glCode" />
												</s:else>
											</s:if>
											&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="left">
											<s:property value="accountName" />
											&nbsp;
										</div>
									</td>	
									<td class="blueborderfortd">
										<div align="right">

											<s:if test='%{displayBold == true}'>
												<strong><s:property value="debitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="debitamount" />&nbsp;</s:else>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<s:if test='%{displayBold == true}'>
												<strong><s:property value="prevDebitamount" />&nbsp;</strong>
											</s:if>
											<s:else>
												<s:property value="prevDebitamount" />&nbsp;</s:else>
										</div>
									</td>																		

								</tr>
								</s:elseif>
																
							</s:iterator>
						</table>
					</div>
				</td>
			</tr>

		</table>

	</div>
		<div class="buttonbottom" id="ieReport">
			<s:text name="report.export.options" />
			: <a
				href='/services/EGF/report/incomeExpenditureReport-generateIncomeExpenditureXls.action?showDropDown=false&model.period=<s:property value="model.period"/>&model.currency=<s:property value="model.currency"/>&model.financialYear.id=<s:property value="model.financialYear.id"/>&model.department.id=<s:property value="model.department.id"/>&model.fromDate=<s:property value="model.fromDate"/>&model.toDate=<s:property value="model.toDate"/>&model.fund.id=<s:property value="model.fund.id"/>&model.function.id=<s:property value="model.function.id"/>&model.functionary.id=<s:property value="model.functionary.id"/>&model.field.id=<s:property value="model.field.id"/>'>Excel</a>
			| <a
				href='/services/EGF/report/receiptsAndPaymentsAccountReport-generateReceiptAndPaymentPdf.action?showDropDown=false&model.period=<s:property value="model.period"/>&model.currency=<s:property value="model.currency"/>&model.financialYear.id=<s:property value="model.financialYear.id"/>&model.department.id=<s:property value="model.department.id"/>&model.fromDate=<s:property value="model.fromDate"/>&model.toDate=<s:property value="model.toDate"/>&model.fund.id=<s:property value="model.fund.id"/>&model.function.id=<s:property value="model.function.id"/>&model.functionary.id=<s:property value="model.functionary.id"/>&model.field.id=<s:property value="model.field.id"/>'>PDF</a>
		</div>
</div>