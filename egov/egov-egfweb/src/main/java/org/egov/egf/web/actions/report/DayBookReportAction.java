/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.egf.web.actions.report;

import com.exilant.GLEngine.DayBook;
import com.exilant.eGov.src.reports.DayBookReportBean;
import com.exilant.exility.common.TaskFailedException;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.SubScheme;
import org.egov.egf.model.VoucherDetailMiscMapping;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.services.PersistenceService;
import org.egov.utils.FinancialConstants;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ParentPackage("egov")
@Results({
        @Result(name = "result", location = "dayBookReport-result.jsp"),
        @Result(name = FinancialConstants.STRUTS_RESULT_PAGE_SEARCH, location = "dayBookReport-"
                + FinancialConstants.STRUTS_RESULT_PAGE_SEARCH + ".jsp")
})
public class DayBookReportAction extends BaseFormAction {
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;

    /**
     *
     */
    private static final long serialVersionUID = 641276108961752283L;
    private static final Logger LOGGER = Logger.getLogger(DayBookReportAction.class);
    private DayBookReportBean dayBookReport = new DayBookReportBean();
    protected DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    protected List<DayBook> dayBookDisplayList = new ArrayList<DayBook>();
    String heading = "";
    private Date todayDate = new Date();
    private String currentDate;
    private String titleName = "";
    private String scheme;

    public DayBookReportAction() {
        super();
    }

    @Override
    public Object getModel() {
        return dayBookReport;
    }

    public void prepareNewForm() {
        super.prepare();
        persistenceService.getSession().setDefaultReadOnly(true);
        
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
        addDropdownData("fundList",persistenceService.findAllBy(" from Fund where isactive=true and isnotleaf=false order by name"));
        addDropdownData("schemeList",persistenceService.findAllBy(" from Scheme where isactive=true order by name"));
        
        currentDate = formatter.format(todayDate);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Inside  Prepare ........");

    }

    @SkipValidation
    @Action(value = "/report/dayBookReport-newForm")
    public String newForm() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("..Inside NewForm method..");
        return FinancialConstants.STRUTS_RESULT_PAGE_SEARCH;
    }

    @Validations(requiredFields = {
            @RequiredFieldValidator(fieldName = "startDate", message = "", key = FinancialConstants.REQUIRED),
            @RequiredFieldValidator(fieldName = "endDate", message = "", key = FinancialConstants.REQUIRED),
            @RequiredFieldValidator(fieldName = "fundId", message = "", key = FinancialConstants.REQUIRED), })
    @ValidationErrorPage(value = FinancialConstants.STRUTS_RESULT_PAGE_SEARCH)
    @ReadOnly
    @Action(value = "/report/dayBookReport-ajaxSearch")
    public String ajaxSearch() throws TaskFailedException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("dayBookAction | Search | start");
        prepareResultList();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("dayBookAction | list | End");
        heading = getGLHeading();
        titleName = microserviceUtils.getHeaderNameForTenant().toUpperCase()+" \\n";
        prepareNewForm();

        persistenceService.getSession().setFlushMode(FlushMode.AUTO);
        return "result";
    }

    @Action(value = "/report/daybookReport-subscheme-ajaxsearch")
    public String subschemeSearch() throws TaskFailedException {  
    	System.out.println("ajax ::::"+scheme);
        //addDropdownData("subSchemeList",persistenceService.findAllBy(" from SubScheme where isactive=true and schemeid='"+id+"' order by name"));
    	//persistenceService.getSession().setFlushMode(FlushMode.AUTO);
    	return "result";
    }
    
    private String getQuery() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String startDate = "", endDate = "", fundId = "";
        fundId = dayBookReport.getFundId();
        String schemeId=dayBookReport.getSchemeId();
    	System.out.println("scheme id"+dayBookReport.getSchemeId());
    	String naration=dayBookReport.getNarration();
    	System.out.println("naration ::: "+dayBookReport.getNarration());
    	String oderBy=" ORDER BY vdate,vouchernumber";
        try {
            startDate = sdf.format(formatter.parse(dayBookReport.getStartDate()));
            endDate = sdf.format(formatter.parse(dayBookReport.getEndDate()));
        } catch (ParseException e) {

        }
        String query = "SELECT voucherdate as vdate, TO_CHAR(voucherdate, 'dd-Mon-yyyy')  AS  voucherdate, vouchernumber as vouchernumber , gd.glcode AS glcode,ca.name AS particulars ,vh.name ||' - '|| vh.TYPE AS type"
                + ", CASE WHEN vh.description is null THEN ' ' ELSE vh.description END AS narration, CASE  WHEN status=0 THEN ( 'Approved') ELSE ( case WHEN status=1 THEN 'Reversed' else (case WHEN status=2 THEN 'Reversal' else ' ' END) END ) END as status , debitamount  , "
                + " creditamount,vh.CGVN ,vh.isconfirmed as \"isconfirmed\",vh.id as vhId,(select dep.name from eg_department dep where dep.code=vmis.departmentcode) as dept,(select fun.name from function fun where fun.id=vmis.functionid) as func FROM voucherheader vh,vouchermis vmis, generalledger gd, chartofaccounts ca WHERE vh.ID=gd.VOUCHERHEADERID AND vh.id=vmis.voucherheaderid"
                + " AND ca.GLCODE=gd.GLCODE AND voucherdate >= '"
                + startDate
                + "' and voucherdate <= '"
                + endDate
                + "' and vh.status not in (4,5)  and vh.fundid = " + fundId ;
        
        if(schemeId != null && !schemeId.isEmpty())
        {
        	if(naration != null && !naration.isEmpty())
        	{
        		query=query+" and vh.description like '%"+naration+"%' ";
        	}
        	query=query+" and vmis.schemeid = "+schemeId+ oderBy;
        	
        }
        else
        {
        	if(naration != null && !naration.isEmpty())
        	{
        		query=query+" and vh.description like '%"+naration+"%' ";
        	}
        	query=query+oderBy;
        }
        return query;
    }

    private void prepareResultList() {
    	//view start
    	Map<Long,CVoucherHeader> voucherDetailPartyMapping=new HashMap<Long,CVoucherHeader>();
    	populatePartyNames(voucherDetailPartyMapping);
    	Map<Long,List<VoucherDetailMiscMapping>> voucherDetailMiscMapping=new HashMap<Long,List<VoucherDetailMiscMapping>>();
    	populateMiscDetail(voucherDetailMiscMapping);
    	Map<Long,CVoucherHeader> voucherDetailBpvMapping=new HashMap<Long,CVoucherHeader>();
    	populateBpv(voucherDetailBpvMapping);
    	Map<Long,CVoucherHeader> voucherDetailIntrumentMapping=new HashMap<Long,CVoucherHeader>();
    	populateInstrument(voucherDetailIntrumentMapping);
    	
    	//view end
        String voucherDate = "", voucherNumber = "", voucherType = "", narration = "", status = "";
        Query query = null;
        query = persistenceService.getSession().createSQLQuery(getQuery())
                .addScalar("voucherdate", StringType.INSTANCE)
                .addScalar("vouchernumber", StringType.INSTANCE)
                .addScalar("glcode", StringType.INSTANCE)
                .addScalar("particulars", StringType.INSTANCE)
                .addScalar("type", StringType.INSTANCE)
                .addScalar("narration", StringType.INSTANCE)
                .addScalar("status", StringType.INSTANCE)
                .addScalar("creditamount", StringType.INSTANCE)
                .addScalar("debitamount", StringType.INSTANCE)
                .addScalar("vhId", StringType.INSTANCE)
                .addScalar("dept", StringType.INSTANCE)
                .addScalar("func", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DayBook.class));
        dayBookDisplayList = query.list();
        for (DayBook bean : dayBookDisplayList) {
            bean.setDebitamount(new BigDecimal(bean.getDebitamount()).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
            bean.setCreditamount(new BigDecimal(bean.getCreditamount()).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
            if (voucherDate != null && !voucherDate.equalsIgnoreCase("") && voucherDate.equalsIgnoreCase(bean.getVoucherdate())
                    && voucherNumber.equalsIgnoreCase(bean.getVouchernumber())) {
                bean.setVoucherdate("");
            } else {
                voucherDate = bean.getVoucherdate();
            }
            if (voucherType != null && !voucherType.equalsIgnoreCase("") && voucherType.equalsIgnoreCase(bean.getType())
                    && voucherNumber.equalsIgnoreCase(bean.getVouchernumber())) {
                bean.setType("");
            } else {
                voucherType = bean.getType();
            }
            if (status != null && !status.equalsIgnoreCase("") && status.equalsIgnoreCase(bean.getStatus())
                    && voucherNumber.equalsIgnoreCase(bean.getVouchernumber())) {
                bean.setStatus("");
            } else {
                status = bean.getStatus();
            }
            if (voucherNumber != null && !voucherNumber.equalsIgnoreCase("")
                    && voucherNumber.equalsIgnoreCase(bean.getVouchernumber())) {
                bean.setVouchernumber("");
            } else {
                voucherNumber = bean.getVouchernumber();
                bean.setDivision(bean.getDept());
                bean.setBudgethead(bean.getFunc());
                //view
                if(voucherDetailPartyMapping.get(Long.parseLong(bean.getVhId())) != null)
                {
                	bean.setPartyname(voucherDetailPartyMapping.get(Long.parseLong(bean.getVhId())).getVoucherNumber());
                }
                if(voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())) != null && !voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())).isEmpty())
                {
                	bean.setBpvno(voucherDetailBpvMapping.get(voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())).get(0).getBpvId()).getVoucherNumber());
                	bean.setBpvDate(voucherDetailBpvMapping.get(voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())).get(0).getBpvId()).getPartyBillNumber());
                	if(voucherDetailIntrumentMapping.get(voucherDetailBpvMapping.get(voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())).get(0).getBpvId()).getId()) != null)
                	{
                		bean.setPexno(voucherDetailIntrumentMapping.get(voucherDetailBpvMapping.get(voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())).get(0).getBpvId()).getId()).getVoucherNumber());
                		bean.setPexDate(voucherDetailIntrumentMapping.get(voucherDetailBpvMapping.get(voucherDetailMiscMapping.get(Long.parseLong(bean.getVhId())).get(0).getBpvId()).getId()).getApprovalComent());
                	}
                }
                if(voucherDetailIntrumentMapping.get(Long.parseLong(bean.getVhId())) != null)
                {
                	bean.setPexno(voucherDetailIntrumentMapping.get(Long.parseLong(bean.getVhId())).getVoucherNumber());
                	bean.setPexDate(voucherDetailIntrumentMapping.get(Long.parseLong(bean.getVhId())).getApprovalComent());
                }
                
            }

            if (narration != null && !narration.equalsIgnoreCase("") && narration.equalsIgnoreCase(bean.getNarration())) {
                bean.setNarration("");
            } else {
                narration = bean.getNarration();
            }

        }

    }

    private void populateInstrument(Map<Long, CVoucherHeader> voucherDetailIntrumentMapping) {
    	SQLQuery queryInstru =  null;
       	final StringBuffer query5 = new StringBuffer(500);
      	List<Object[]> list= null;
      	query5
          .append("select vdi.id,vdi.transactionnumber, vdi.transactiondate ,vdi.voucherheaderid,vdi.accountnumber from voucher_detail_instrument vdi   ");
      	LOGGER.info("Query 5 :: "+query5.toString());
      	queryInstru=this.persistenceService.getSession().createSQLQuery(query5.toString());
       	list = queryInstru.list();
      	LOGGER.info("pex map");
      	CVoucherHeader pexDetail=null;
      	
      	if (list.size() != 0) {
      		LOGGER.info("size ::: "+list.size());
      		for (final Object[] object : list)
      		{
      			pexDetail=new CVoucherHeader();
      			if(object[3] != null)
      			{
      				pexDetail.setId(Long.parseLong(object[3].toString()));
      			}
      			if(object[1] != null)
      			{
      				pexDetail.setVoucherNumber(object[1].toString());
      			}
      			if(object[2] != null)
      			{
      				pexDetail.setApprovalComent(object[2].toString());
      			}
      			if(object[4] != null)
      			{
      				pexDetail.setCgvn(object[4].toString());
      			}
      			voucherDetailIntrumentMapping.put(pexDetail.getId(), pexDetail);
      		}
      		
      	}
		
	}

	private void populateBpv(Map<Long, CVoucherHeader> voucherDetailBpvMapping) {
		CVoucherHeader bpvMapping =null;
    	SQLQuery queryBpv =  null;
    	final StringBuffer query2 = new StringBuffer(500);
    	List<Object[]> list= null;
    	query2
        .append("select vdb.id,vdb.vouchernumber,vdb.voucherdate from voucher_detail_bpvmapping vdb");
    	LOGGER.info("Query 2 :: "+query2.toString());
    	queryBpv=this.persistenceService.getSession().createSQLQuery(query2.toString());
    	list = queryBpv.list();
    	LOGGER.info("after exe");
    	if (list.size() != 0) {
    		LOGGER.info("size ::: "+list.size());
    		for (final Object[] object : list)
    		{
    			bpvMapping=new CVoucherHeader();
    			bpvMapping.setId(Long.parseLong(object[0].toString()));
    			bpvMapping.setVoucherNumber(object[1].toString());
    			bpvMapping.setPartyBillNumber(object[2].toString());
    			voucherDetailBpvMapping.put(bpvMapping.getId(), bpvMapping);
    		}
    	}
		
	}

	private void populateMiscDetail(Map<Long, List<VoucherDetailMiscMapping>> voucherDetailMiscMapping) {
		
    	SQLQuery queryMisc =  null;
    	final StringBuffer query3 = new StringBuffer(500);
   	 List<Object[]> list= null;
   	query3
       .append("select vdm.id,vdm.billvhid,vdm.payvhid,vdm.paidamount from voucher_detail_miscbill vdm ");
   	LOGGER.info("Query 3 :: "+query3.toString());
   	queryMisc=this.persistenceService.getSession().createSQLQuery(query3.toString());
   	list = queryMisc.list();
   	LOGGER.info("1 map");
   	VoucherDetailMiscMapping voucherDetailMisc=null;
   	List<VoucherDetailMiscMapping> miscbillList=null;
   	if (list.size() != 0) {
   		LOGGER.info("size ::: "+list.size());
   		for (final Object[] object : list)
   		{
   			voucherDetailMisc=new VoucherDetailMiscMapping();
   			voucherDetailMisc.setId(Long.parseLong(object[0].toString()));
   			if(object[1] != null)
   			{
   				voucherDetailMisc.setVoucherId(Long.parseLong(object[1].toString()));
   			}
   			if(object[2] != null)
   			{
   				voucherDetailMisc.setBpvId(Long.parseLong(object[2].toString()));
   			}
   			if(object[3] != null )
   			{
   				voucherDetailMisc.setAmountPaid(new BigDecimal(object[3].toString()));
   			}
   			if(voucherDetailMiscMapping.get(voucherDetailMisc.getVoucherId()) == null)
   			{
   				miscbillList=new ArrayList<VoucherDetailMiscMapping>();
   				miscbillList.add(voucherDetailMisc);
   				voucherDetailMiscMapping.put(voucherDetailMisc.getVoucherId(), miscbillList);
   			}
   			else
   			{
   				voucherDetailMiscMapping.get(voucherDetailMisc.getVoucherId()).add(voucherDetailMisc);
   			}
   		}
   		
   	}
		
	}

	private void populatePartyNames(Map<Long, CVoucherHeader> voucherDetailPartyMapping) {
    	SQLQuery queryParty =  null;
       	final StringBuffer query4 = new StringBuffer(500);
       	List<Object[]> list= null;
      	query4
          .append("select vdp.id,vdp.detailname from daybook_detail_party vdp  ");
      	LOGGER.info("Query 4 :: "+query4.toString());
      	queryParty=this.persistenceService.getSession().createSQLQuery(query4.toString());
       	list = queryParty.list();
      	LOGGER.info("1 map");
      	CVoucherHeader partyDetail=null;
      	
      	if (list.size() != 0) {
      		LOGGER.info("size ::: "+list.size());
      		for (final Object[] object : list)
      		{
      			partyDetail=new CVoucherHeader();
      			partyDetail.setId(Long.parseLong(object[0].toString()));
      			partyDetail.setVoucherNumber(object[1].toString());
      			voucherDetailPartyMapping.put(partyDetail.getId(), partyDetail);
      		}
      	}
		
	}

	private String getGLHeading() {

        String heading = "Day Book report from " + dayBookReport.getStartDate() + " to " + dayBookReport.getEndDate();
        Fund fund = new Fund();
        if (checkNullandEmpty(dayBookReport.getFundId())) {
            fund = (Fund) persistenceService.find("from Fund where id = ?", Integer.parseInt(dayBookReport.getFundId()));
            heading = heading + " under " + fund.getName() + " ";
        }
        return heading;
    }

    private boolean checkNullandEmpty(final String column)
    {
        if (column != null && !column.isEmpty() && !column.equalsIgnoreCase("0"))
            return true;
        else
            return false;

    }

    public DayBookReportBean getDayBookReport() {
        return dayBookReport;
    }

    public void setDayBookReport(final DayBookReportBean dayBookReport) {
        this.dayBookReport = dayBookReport;
    }

    public List<DayBook> getDayBookDisplayList() {
        return dayBookDisplayList;
    }

    public void setDayBookDisplayList(List<DayBook> dayBookDisplayList) {
        this.dayBookDisplayList = dayBookDisplayList;
    }

    public void setDayBookDisplayList(final LinkedList dayBookDisplayList) {
        this.dayBookDisplayList = dayBookDisplayList;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(final String heading) {
        this.heading = heading;
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
    
}