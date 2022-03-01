package org.egov.egf.web.controller.report;

import java.util.ArrayList;
import java.util.List;

import org.egov.model.report.TransactionReport;
import org.egov.model.service.TransactionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transactionReport")
public class TransactionReportController {
	
	@Autowired
	private TransactionReportService service;
	
	@ModelAttribute
	public void addDropDownValuesToModel(Model model) {
		model.addAttribute("ulbList", service.getUlbList());
		
	}

	@PostMapping("search")
	public String loadTransactionReportForm(Model model) {
		TransactionReport transactionReport=new TransactionReport();
		model.addAttribute("transactionReport", transactionReport);
		return "transaction-report";
	}
	@PostMapping("generate")
	public String generateReportForm(@ModelAttribute("transactionReport") TransactionReport transactionReport,Model model) {
		model.addAttribute("transactionReports", service.getTransactionReports(transactionReport));
		return "transaction-report";
	}
}
