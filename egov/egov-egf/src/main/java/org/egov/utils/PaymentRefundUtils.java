package org.egov.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.utils.EntityType;
import org.egov.egf.commons.EgovCommon;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.model.payment.Paymentheader;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentRefundUtils {
	
	@Autowired
	private FinancialYearDAO financialYearDAO;
	
	@Autowired
    @Qualifier("persistenceService")
    protected transient PersistenceService persistenceService;
	
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	
	@Autowired
    private EgovCommon egovCommon;
	
	private static final String VOUCHERQUERY = " from CVoucherHeader where id=?";
	private static final String ACCDETAILTYPEQUERY = " from Accountdetailtype where id=?";
	
	public Date finYearDate() {
		final String financialYearId = financialYearDAO.getCurrYearFiscalId();
		if (financialYearId == null || financialYearId.equals(""))
			return new Date();
		else
			return (Date) persistenceService.find("select startingDate  from CFinancialYear where id=?",
					Long.parseLong(financialYearId));
	}
	
	public List<Fund> getAllFunds(){
		return persistenceService.findAllBy(" from Fund where isactive=true and isnotleaf=false order by name");
	}
	//added abhishek on 2Dec2021
	public List<Accountdetailtype> getAllActiveAccounts(){
		return persistenceService.findAllBy(" from Accountdetailtype where isactive=true order by name");
	}
	
	public List<String> getVoucherNamesByType(String voucherType){
	    Query query = this.persistenceService.getSession().createSQLQuery("select distinct(name) from voucherheader vh where vh.type = :type");
	    query.setString("type", voucherType);
	    List<String> list = query.list();
	    return list;
	}
	
	public Map<Integer, String> populateSourceMap() {
		Map<Integer, String> sourceMap = new HashMap<Integer, String>();
		List<Object[]> sourceList = new ArrayList<Object[]>();
		sourceList = persistenceService.findAllBy(
				" select distinct m.id,m.name from CVoucherHeader  vh, EgModules m where m.id=vh.moduleId and vh.status!=4 order by m.name");

		for (final Object[] obj : sourceList)
			sourceMap.put((Integer) obj[0], (String) obj[1]);
		// For vouchers created from the financial module
		sourceMap.put(-2, "Internal");
		
		return sourceMap;
	}
	
	public String getVoucherStatus(final int status) {
		if (FinancialConstants.CREATEDVOUCHERSTATUS.equals(status))
			return "Approved";
		if (FinancialConstants.REVERSEDVOUCHERSTATUS.equals(status))
			return "Reversed";
		if (FinancialConstants.REVERSALVOUCHERSTATUS.equals(status))
			return "Reversal";
		if (FinancialConstants.CANCELLEDVOUCHERSTATUS.equals(status))
			return "Cancelled";
		if (FinancialConstants.PREAPPROVEDVOUCHERSTATUS.equals(status))
			return "Created";
		return "";
	}
	
	public Map<Long,String> populateVoucherMap(List<CVoucherHeader> list) {
		Map<Long,String> paymentVoucherMap=new HashMap<Long,String>();
		List<Long> vhIds=new ArrayList<Long>();
		List<Paymentheader> paymentList =null;
		for(CVoucherHeader vh:list)
		{
			if((vh.getName().equals("Remittance Payment") || vh.getName().equals("Bill Payment") || vh.getName().equals("Direct Bank Payment")))
			{
				vhIds.add(vh.getId());
			}
		}
		if(vhIds!=null && !vhIds.isEmpty())
		{
			 paymentList = persistenceService.findAllByNamedQuery("getPaymentList", vhIds);
		}
		if(paymentList!=null && !paymentList.isEmpty())
		{
			for(Paymentheader ph : paymentList)
			{
				if(ph.getState() != null)
				{
					paymentVoucherMap.put(ph.getVoucherheader().getId(),getEmployeeName(ph.getState().getOwnerPosition()));
				}
			}
		}
		
		return paymentVoucherMap;
	}
	
	public String getEmployeeName(Long empId){        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	}
	
	public CVoucherHeader getVoucherHeader(Long vhid) {
		return (CVoucherHeader) persistenceService.find(VOUCHERQUERY,  vhid);
	}
	
	public List<CGeneralLedger> getAccountDetails(Long vhid) {
		final List<CGeneralLedger> gllist = persistenceService.findAllBy(
                " from CGeneralLedger where voucherHeaderId.id=? order by id asc",
                Long.valueOf(vhid + ""));
		return gllist;
	}
	
	public CFunction getFunction(Long id) {
		return (CFunction)persistenceService.find("from CFunction where id=?", id);
	}
	
	public CChartOfAccounts getChartOfAccount(String glCode) {
		return (CChartOfAccounts)persistenceService.find("from CChartOfAccounts where glcode=?", glCode);
	}
	
	public Accountdetailtype getAccountdetailtype(int id) {
		return (Accountdetailtype)persistenceService.find(ACCDETAILTYPEQUERY, id);
	}
	
	public EgBillregister getEgBillregister(CVoucherHeader voucherHeader) {
		return (EgBillregister)persistenceService.find(" from EgBillregister br where br.egBillregistermis.voucherHeader=?", voucherHeader);
	}
	
	public Map<String, Object> getAccountDetails(final Integer detailtypeid, final Integer detailkeyid, final Map<String, Object> tempMap){
    	try {
	        final Accountdetailtype detailtype = (Accountdetailtype)persistenceService.find(ACCDETAILTYPEQUERY, detailtypeid);
	        tempMap.put("detailtype", detailtype.getDescription());
	        tempMap.put("detailtypeid", detailtype.getId());
	        tempMap.put("detailkeyid", detailkeyid);

	        egovCommon.setPersistenceService(persistenceService);
	        final EntityType entityType = egovCommon.getEntityType(detailtype, detailkeyid);
	        if (entityType == null) {
	            tempMap.put(Constants.DETAILKEY, detailkeyid + " " + Constants.MASTER_DATA_DELETED);
	            tempMap.put(Constants.DETAILCODE, Constants.MASTER_DATA_DELETED);
	        } else {
	            tempMap.put(Constants.DETAILKEY, entityType.getName());
	            tempMap.put(Constants.DETAILCODE, entityType.getCode());
	        }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
        return tempMap;
    }
}
