package org.egov.egf.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.CVoucherHeader;
import org.egov.egf.dashboard.event.FinanceEventType;
import org.egov.egf.dashboard.event.listener.FinanceDashboardService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.model.bills.EgBillregister;
import org.egov.model.payment.Paymentheader;
import org.egov.model.voucher.WorkflowBean;
import org.egov.payment.services.PaymentActionHelper;
import org.egov.services.bills.BillsService;
import org.egov.services.payment.PaymentService;
import org.egov.services.voucher.PreApprovedActionHelper;
import org.egov.services.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inbox")
public class MultiSelectInboxController {
	
	@Autowired
	private VoucherService voucherService;
	
	@Autowired
	private PreApprovedActionHelper preApprovedActionHelper;
	
	@Autowired
	private FinanceDashboardService finDashboardService;
	
	@Autowired
	private BillsService billsService;
	
	@Autowired
	private MicroserviceUtils microserviceUtils;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentActionHelper paymentActionHelper;
	
	private static final Logger LOGGER = Logger.getLogger(MultiSelectInboxController.class);
	
	private static final String TASK_TYPR_VOUCHER = "Voucher";
	private static final String ACCOUNT_DEPT = "DEPT_25";
	private static final List<String> VALID_APPROVER_DESIGNATIONS = Arrays.asList("DESIG_208", "DESIG_210", "DESIG_218", "DESIG_219");
	
	@RequestMapping(value = "/inbox-multiselect", method = RequestMethod.GET)
	public String multiSelectInbox() {
		return "inbox-multiselect";
	}

	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/preApprovedVoucher-bulkUpdate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String preApprovedVoucherBulkUpdate(@Valid @RequestBody List<Map<String, Object>> inboxItems, final RedirectAttributes redirectAttributes) {
		LOGGER.info("inboxItems >> " + inboxItems);

		for(Map<String, Object> items : inboxItems) {
			LOGGER.info("items >> " + items);
			if(TASK_TYPR_VOUCHER.equalsIgnoreCase((String)items.get("task"))) {
				String voucherId = getVoucherId(items);
				
				try {
					CVoucherHeader voucherHeader = (CVoucherHeader) voucherService.findById(Long.parseLong(voucherId), false);
					EgBillregister egBillregister = voucherService.getegBillRegister(voucherHeader);
					if(!ObjectUtils.isEmpty(egBillregister)) {
						voucherHeader.setBillNumber(egBillregister.getBillnumber());
					}
					
					WorkflowBean workflowBean = populateWorkflowBean(items);
					voucherHeader = preApprovedActionHelper.sendForApproval(voucherHeader, workflowBean);
					
					String type = billsService.getBillTypeforVoucher(voucherHeader);
					if (null == type) {
						type = "default";
					}
					
					// Update the ES index
					if (voucherHeader.getId() != null && voucherHeader.getId().compareTo(new Long(0)) > 0) {
						finDashboardService.publishEvent(FinanceEventType.voucherUpdateById, new HashSet<>(Arrays.asList(voucherHeader.getId())));
					}
				} catch (ValidationException e) {

					List<ValidationError> errors = new ArrayList<>();
					errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
					throw new ValidationException(errors);
				} catch (Exception e) {

					List<ValidationError> errors = new ArrayList<>();
					errors.add(new ValidationError("exp", e.getMessage()));
					throw new ValidationException(errors);
				}
				
			} else {
				Paymentheader paymentheader = new Paymentheader();
				String paymentid = getPaymentId(items);
				if (paymentid != null)
					paymentheader = paymentService.findById(Long.valueOf(paymentid), false);
				if (paymentheader == null || paymentheader.getId() == null)
					paymentheader = new Paymentheader();

				WorkflowBean workflowBean = populateWorkflowBean(items);
				paymentheader = paymentActionHelper.sendForApproval(paymentheader, workflowBean);
			}
		}
		
	    return "success";
	}
	
	
	private String getPaymentId(Map<String, Object> items) {
		String paymentLink = (String) items.get("link");
		String paymentId  = "";
		paymentId = paymentLink.substring(paymentLink.indexOf("paymentid=") + 10, paymentLink.length());
		return paymentId;
	}


	private String getVoucherId(Map<String, Object> items) {
		String voucherLink = (String) items.get("link");
		String voucherId  = "";
		if(StringUtils.isNotEmpty(voucherLink)) {
			voucherId = voucherLink.substring(voucherLink.indexOf("vhid=") + 5 , voucherLink.indexOf("&from"));
		}
		return voucherId;
	}


	private WorkflowBean populateWorkflowBean(Map<String, Object> items) {
		WorkflowBean workflowBean = new WorkflowBean();
		Long positionId = populatePosition();
		workflowBean.setApproverPositionId(positionId);
		String approverComments = (String) items.get("remark");
		
		if(!StringUtils.isEmpty(approverComments)) {
			workflowBean.setApproverComments(approverComments);
		}
		workflowBean.setWorkFlowAction("Approve");
//		workflowBean.setCurrentState(currentState);
		return workflowBean;
	}

	
	private Long populatePosition() {
		Long empId = ApplicationThreadLocals.getUserId();
		Long pos = null;
		List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null, null, null);
		if (null != employs && employs.size() > 0) {
			pos = employs.get(0).getAssignments().get(0).getPosition();
		}
		return pos;
	}


	@RequestMapping(value = "/isValidApprover", method = RequestMethod.GET)
	@ResponseBody
	public boolean isValidApprover() {
		Long empId = ApplicationThreadLocals.getUserId();
		List<EmployeeInfo> employees = microserviceUtils.getEmployee(empId, null, null, null);
		if (null != employees && employees.size() > 0) {
			if(ACCOUNT_DEPT.equalsIgnoreCase(employees.get(0).getAssignments().get(0).getDepartment())
					&& VALID_APPROVER_DESIGNATIONS.contains(employees.get(0).getAssignments().get(0).getDesignation())) {
				return true;
			}
		}
		return false;
	}
	
}
