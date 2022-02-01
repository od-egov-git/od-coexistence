package org.egov.asset.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetRevaluation;
import org.egov.asset.repository.RevaluationRepository;
import org.egov.commons.CFunction;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.infra.script.service.ScriptService;
import org.egov.model.voucher.VoucherDetails;
import org.egov.model.voucher.VoucherTypeBean;
import org.egov.model.voucher.WorkflowBean;
import org.egov.services.voucher.JournalVoucherActionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RevaluationService {

	@Autowired
	private RevaluationRepository revaluationRepository;
	@Autowired
	private FunctionDAO functionDAO;

	@Autowired
	private FundHibernateDAO fundHibernateDAO;
	
	@Autowired
	private VoucherTypeBean voucherTypeBean;
	@Autowired
    @Qualifier("journalVoucherActionHelper")
    private JournalVoucherActionHelper journalVoucherActionHelper;

	private final ScriptService scriptExecutionService;
	public transient CVoucherHeader voucherHeader = new CVoucherHeader();
	public transient WorkflowBean workflowBean = new WorkflowBean();
	private List<VoucherDetails> billDetailslist;
    private List<VoucherDetails> subLedgerlist;

	@Autowired
	public RevaluationService(final RevaluationRepository revaluationRepository,
			final ScriptService scriptExecutionService) {
		this.revaluationRepository = revaluationRepository;
		this.scriptExecutionService = scriptExecutionService;
	}

	public AssetRevaluation create(final AssetRevaluation assetRevaluation) {

		return revaluationRepository.save(assetRevaluation);
	}

	public String createVoucher(AssetRevaluation savedAssetRevaluation) {
		String voucherNumber = "";
		CFunction function = new CFunction();
		Fund fund = new Fund();
		fund = fundHibernateDAO.fundByCode("01");
		CFunction fun = functionDAO.getFunctionById(Long.valueOf(savedAssetRevaluation.getFunction()));
		voucherTypeBean.setVoucherName("JVGeneral");
		voucherTypeBean.setVoucherType("Journal Voucher");
		voucherTypeBean.setVoucherSubType("JVGeneral");
		voucherHeader = new CVoucherHeader();
		voucherHeader.setBackdateentry("N");
		voucherHeader.setFileno("");
		voucherHeader.setDescription(savedAssetRevaluation.getReason());
		voucherHeader.setFundId(fund);
		voucherHeader.setVoucherDate(new Date());
		voucherHeader.setVouchermis(new Vouchermis());
		voucherHeader.getVouchermis().setFunction(fun);
		voucherHeader.getVouchermis().setDepartmentcode(savedAssetRevaluation.getAssetMaster().getAssetHeader().getDepartment());
		workflowBean.setWorkFlowAction("CreateAndApprove");
		billDetailslist = new ArrayList<VoucherDetails>();
		VoucherDetails vd1 = new VoucherDetails();
		vd1.setCreditAmountDetail(savedAssetRevaluation.getAdd_del_amt());
		vd1.setDebitAmountDetail(new BigDecimal(0));
		vd1.setGlcodeDetail(savedAssetRevaluation.getAssetMaster().getAssetHeader().getAssetCategory().getAssetAccountCode().getGlcode());
		billDetailslist.add(vd1);
		VoucherDetails vd2 = new VoucherDetails();
		vd2.setCreditAmountDetail(new BigDecimal(0));
		vd2.setDebitAmountDetail(savedAssetRevaluation.getAdd_del_amt());
		vd2.setGlcodeDetail(savedAssetRevaluation.getAssetMaster().getAssetHeader().getAssetCategory().getRevolutionReserveAccountCode().getGlcode());
		billDetailslist.add(vd2);
		subLedgerlist = new ArrayList<VoucherDetails>();
		voucherHeader = journalVoucherActionHelper.createVcouher(billDetailslist, subLedgerlist, voucherHeader,
				voucherTypeBean, workflowBean);
		voucherNumber=voucherHeader.getVoucherNumber();
		return voucherNumber;
	}

	public VoucherTypeBean getVoucherTypeBean() {
		return voucherTypeBean;
	}

	public void setVoucherTypeBean(VoucherTypeBean voucherTypeBean) {
		this.voucherTypeBean = voucherTypeBean;
	}

	public CVoucherHeader getVoucherHeader() {
		return voucherHeader;
	}

	public void setVoucherHeader(CVoucherHeader voucherHeader) {
		this.voucherHeader = voucherHeader;
	}

	public WorkflowBean getWorkflowBean() {
		return workflowBean;
	}

	public void setWorkflowBean(WorkflowBean workflowBean) {
		this.workflowBean = workflowBean;
	}

	public List<VoucherDetails> getBillDetailslist() {
		return billDetailslist;
	}

	public void setBillDetailslist(List<VoucherDetails> billDetailslist) {
		this.billDetailslist = billDetailslist;
	}

	public List<VoucherDetails> getSubLedgerlist() {
		return subLedgerlist;
	}

	public void setSubLedgerlist(List<VoucherDetails> subLedgerlist) {
		this.subLedgerlist = subLedgerlist;
	}

}
