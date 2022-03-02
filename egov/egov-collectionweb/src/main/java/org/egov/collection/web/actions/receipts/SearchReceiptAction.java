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
package org.egov.collection.web.actions.receipts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.codehaus.jackson.map.ObjectMapper;
import org.egov.collection.bean.ReceiptReportBean;
import org.egov.collection.bean.SubDivison;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.eis.service.AssignmentService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.Amount;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.BillDetailAdditional;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.models.RemittanceDepositWorkDetail;
import org.egov.infra.microservice.models.RemittanceResponseDepositWorkDetails;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ParentPackage("egov")
@Results({ @Result(name = SearchReceiptAction.SUCCESS, location = "searchReceipt.jsp"),
		@Result(name = SearchReceiptAction.SUCCESSNEW, location = "searchReceiptNew.jsp"),
		@Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
				"contentType", "application/xls", "contentDisposition", "no-cache;filename=ReceiptReport.xls" }),
		@Result(name = SearchReceiptAction.DAYBOOKREPORTNEW, location = "dayBookReportNew.jsp"),
		@Result(name = SearchReceiptAction.DAYBOOKREPORT, location = "dayBookReport.jsp") })
public class SearchReceiptAction extends SearchFormAction {

	private static final long serialVersionUID = 1L;
	protected static final String DAYBOOKREPORT = "dayBookReport";
	protected static final String DAYBOOKREPORTNEW = "dayBookReportNew";
	protected static final String SUCCESSNEW = "searchReceiptNew";
	private String serviceTypeId = null;
	private String serviceTypeIdforExcel = null;
	private String serviceTypeDuringDownload = null;

	public String getServiceTypeDuringDownload() {
		return serviceTypeDuringDownload;
	}

	public void setServiceTypeDuringDownload(String serviceTypeDuringDownload) {
		this.serviceTypeDuringDownload = serviceTypeDuringDownload;
	}

	private String deptId = null;
	private Long userId = (long) -1;
	private String instrumentType;
	private String receiptNumber;
	private String collectedBy = "";
	private String modeOfPayment = "";
	private String searchAmount = "0";
	private Date fromDate;
	private Date toDate;
	private Integer searchStatus = -1;
	private String target = "new";
	private String manualReceiptNumber;
	private List resultList = new ArrayList();
	private String serviceClass = "-1";
	private TreeMap<String, String> serviceClassMap = new TreeMap<String, String>();
	private CollectionsUtil collectionsUtil;
	private Integer branchId;
	private String reportId;
	private ReportService reportService;
	private InputStream inputStream;
	private String receiptType;
	private String serviceId;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd/MMM/yyyy", LOCALE);
	@Autowired
	private AssignmentService assignmentService;

	@Autowired
	protected EgovMasterDataCaching masterDataCache;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	private String collectionVersion;

	private String subdivison;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AppConfigValueService appConfigValuesService;

	@Override
	public Object getModel() {
		return null;
	}

	public String getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(final String serviceType) {
		serviceTypeId = serviceType;
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(final String instrumentType) {
		this.instrumentType = instrumentType;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(final String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	Map<String, String> serviceCategoryNames = new HashMap<String, String>();
	Map<String, String> serviceCategoryNames2 = new HashMap<String, String>();
	Map<String, Map<String, String>> serviceTypeMap = new HashMap<>();

	@Action(value = "/receipts/searchReceipt-reset")
	public String reset() {
		setPage(1);
		serviceTypeId = null;
		userId = (long) -1;
		receiptNumber = "";
		fromDate = null;
		toDate = null;
		instrumentType = "";
		searchStatus = -1;
		manualReceiptNumber = "";
		serviceClass = "-1";
		branchId = -1;
		return SUCCESS;
	}

	@Override
	public void prepare() {
		super.prepare();
		// if(searchResult==null)
		// searchResult = new EgovPaginatedList();
		this.getServiceCategoryList();

		setupDropdownDataExcluding();
		// addDropdownData("instrumentTypeList",
		// getPersistenceService().findAllBy("from InstrumentType i where i.isActive =
		// true order by type"));
		// addDropdownData("userList",
		// getPersistenceService().findAllByNamedQuery(CollectionConstants.QUERY_CREATEDBYUSERS_OF_RECEIPTS));

		// serviceClassMap.putAll(CollectionConstants.SERVICE_TYPE_CLASSIFICATION);
		// serviceClassMap.remove(CollectionConstants.SERVICE_TYPE_PAYMENT);
		// addDropdownData("serviceTypeList", Collections.EMPTY_LIST);
		// addDropdownData("businessCategorylist",
		// microserviceUtils.getBusinessCategories());

		addDropdownData("serviceTypeList", microserviceUtils.getBusinessService(null));
		addDropdownData("departmentList", masterDataCache.get("egi-department"));
		// addDropdownData("bankBranchList",
		// collectionsUtil.getBankCollectionBankBranchList());
		List<AppConfigValues> appConfigValuesList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"receipt_sub_divison");
		List<SubDivison> subdivisonList = new ArrayList<SubDivison>();
		SubDivison subdivison = null;
		for (AppConfigValues value : appConfigValuesList) {
			subdivison = new SubDivison();
			subdivison.setSubdivisonCode(value.getValue());
			subdivison.setSubdivisonName(value.getValue());
			subdivisonList.add(subdivison);
		}
		addDropdownData("subdivisonList", subdivisonList);
	}

	private void getServiceCategoryList() {
		List<BusinessService> businessService = microserviceUtils.getBusinessService(null);
		for (BusinessService bs : businessService) {
			String[] splitServName = bs.getBusinessService().split(Pattern.quote("."));
			String[] splitSerCode = bs.getCode().split(Pattern.quote("."));
			if (splitServName.length == 2 && splitSerCode.length == 2) {
				if (!serviceCategoryNames.containsKey(splitSerCode[0])) {
					serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
				}
				if (serviceTypeMap.containsKey(splitSerCode[0])) {
					Map<String, String> map = serviceTypeMap.get(splitSerCode[0]);
					map.put(splitSerCode[1], splitServName[1]);
					serviceTypeMap.put(splitSerCode[0], map);
				} else {
					Map<String, String> map = new HashMap<>();
					map.put(splitSerCode[1], splitServName[1]);
					serviceTypeMap.put(splitSerCode[0], map);
				}
			} else {
				serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
			}
		}
	}

	@Override
	@Action(value = "/receipts/searchReceipt")
	public String execute() {
		return SUCCESS;
	}

	@Action(value = "/receipts/searchReceiptNew")
	public String executeNew() {
		return SUCCESSNEW;
	}

	@Action(value = "/receipts/daybookReport")
	public String dayBookReport() {
		return DAYBOOKREPORT;
	}

	// Added by Anshuman
	@Action(value = "/receipts/daybookReportNew")
	public String dayBookReportNew() {
		return DAYBOOKREPORTNEW;
	}

	public List getReceiptStatuses() {
		return persistenceService.findAllBy("from EgwStatus s where moduletype=? and code != ? order by description",
				ReceiptHeader.class.getSimpleName(), CollectionConstants.RECEIPT_STATUS_CODE_PENDING);
	}

	@Override
	@Action(value = "/receipts/searchReceipt-search")
	public String search() {
		target = "searchresult";
		collectionVersion = ApplicationThreadLocals.getCollectionVersion();

		List<ReceiptHeader> receiptList = new ArrayList<>();
		System.out.println("receipt :::" + getReceiptNumber());
		String type = "search";
		if (getServiceTypeId() != null && getServiceTypeId().equalsIgnoreCase("-1")) {
			setServiceTypeId(null);
		}
		List<Receipt> receipts = microserviceUtils.searchRecieptsFinance("MISCELLANEOUS", getFromDate(), getToDate(),
				getServiceTypeId(), (getReceiptNumber() != null && !getReceiptNumber().isEmpty()
						&& !"".equalsIgnoreCase(getReceiptNumber())) ? getReceiptNumber() : null,
				type);

		for (Receipt receipt : receipts) {

			for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

				for (BillDetail billDetail : bill.getBillDetails()) {

					ReceiptHeader receiptHeader = new ReceiptHeader();
					receiptHeader.setPaymentId(receipt.getPaymentId());
					receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
					populateBifurcationAmount(receiptHeader);
					receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
					receiptHeader.setService(
							microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
					receiptHeader.setReferencenumber(billDetail.getBillNumber());
					receiptHeader.setReferenceDesc(bill.getNarration());
					receiptHeader.setPayeeAddress(bill.getPayerAddress());
					receiptHeader.setPaidBy((bill.getPaidBy()).split("&")[0] + "  " + bill.getPayerAddress());
					receiptHeader.setPayeeName(bill.getPayerName());
					System.out.println("subdivison ::: " + receipt.getSubdivison());
					System.out.println("gst ::: " + receipt.getGstNo());
					receiptHeader.setSubdivison(receipt.getSubdivison());
					receiptHeader.setGstno(receipt.getGstNo());
					// receiptHeader.setTotalAmount(billDetail.getTotalAmount());
					receiptHeader.setCurretnStatus(receipt.getPaymentStatus());
					receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
					if (null != billDetail.getManualReceiptNumber()) {
						receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
						receiptHeader.setG8data(billDetail.getManualReceiptNumber());
					}
					if (billDetail.getManualReceiptDate() != null && billDetail.getManualReceiptDate() != 0) {
						receiptHeader.setManualreceiptdate(new Date(billDetail.getManualReceiptDate()));
						if (null != billDetail.getManualReceiptNumber()) {
							receiptHeader.setG8data(billDetail.getManualReceiptNumber() + "/"
									+ new Date(billDetail.getManualReceiptDate()).toString());
						} else
							receiptHeader.setG8data(new Date(billDetail.getManualReceiptDate()).toString());
					}
					receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());

					JsonNode jsonNode = billDetail.getAdditionalDetails();
					BillDetailAdditional additional = null;
					try {
						if (null != jsonNode)
							additional = (BillDetailAdditional) new ObjectMapper().readValue(jsonNode.toString(),
									BillDetailAdditional.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (null != additional) {
						// if (null != additional.getBusinessReason()) {
						// if (additional.getBusinessReason().contains("-")) {
						// receiptHeader.setService(additional.getBusinessReason().split("-")[0]);
						// } else {
						// receiptHeader.setService(additional.getBusinessReason());
						// }
						// }

						// if (null != additional.getNarration())
						// receiptHeader.setReferenceDesc(additional.getNarration());
						// if (null != additional.getPayeeaddress())
						// receiptHeader.setPayeeAddress(additional.getPayeeaddress());
					}

					receiptList.add(receiptHeader);

					List<ReceiptHeader> beerDrinkers = receiptList.stream().filter(p -> p.getModOfPayment() == "")
							.collect(Collectors.toList());
				}
			}

		}

		if (searchResult == null) {
			Page page = new Page<ReceiptHeader>(1, receiptList.size(), receiptList);
			searchResult = new EgovPaginatedList(page, receiptList.size());
		} else {
			searchResult.getList().clear();
			searchResult.getList().addAll(receiptList);
		}

		resultList = searchResult.getList();
		return SUCCESS;
	}

	@Action(value = "/receipts/searchReceipt-searchNew")
	public String searchNew() {
		target = "searchresult";
		collectionVersion = ApplicationThreadLocals.getCollectionVersion();

		long start = System.currentTimeMillis();
		String servicecategory1 = null;
		String servicecategory = null;
		String sertype = null;
		String sertype1 = null;
		String servicesearch = null;
		List<ReceiptHeader> receiptList = new ArrayList<>();
		List<ReceiptHeader> receiptListfinal = new ArrayList<>();
		resultList.clear();

		System.out.println("::::getFromDate:::: " + getFromDate());
		System.out.println("getToDate()() :::" + getToDate());
		System.out.println(":::::getServiceTypeId:::: " + getServiceTypeId());
		System.out.println(":::::getReceiptNumber:::: " + getReceiptNumber());
		servicesearch = getServiceTypeId();
		if (getServiceTypeId().equalsIgnoreCase("")) {
			setServiceTypeId(null);
		}
		serviceTypeIdforExcel = getServiceTypeId();
		List<Receipt> receipts = new ArrayList<>();

		Map<String, Amount> gllist = populateBifurcationAmountNew(getDeptId());
		Map<String, String> dplist = getAlldepartment();

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;

		query.append(
				"select mrd.total_amt_paid,mrd.receipt_number,to_char(mrd.receipt_date,'dd/mm/yyyy'),mrd.paid_by,mrd.payer_address,mrd.narration,mrd.payment_status,mrd.bank_name,mrd.bank_branch,mrd.subdivison,mrd.servicename,mrd.collectedbyname,mrd.gstno,mrd.payment_mode,mrd.payments_id from mis_receipts_details mrd  ");
				//"select mrd.total_amt_paid,mrd.receipt_number,to_char(mrd.receipt_date,'dd/mm/yyyy'),mrd.paid_by,mrd.payer_address,mrd.narration,mrd.payment_status,mrd.bank_name,mrd.bank_branch,mrd.subdivison,mrd.servicename,mrd.collectedbyname,mrd.gstno,mrd.payment_mode,mrd.payments_id,mrd.chequeddno,mrd.to_char(chequedddate, 'dd/mm/yyyy') from mis_receipts_details mrd  ");
		if (getFromDate() != null || getToDate() != null
				|| (getServiceTypeId() != null && !getServiceTypeId().isEmpty() && !getServiceTypeId().equals("-1"))
				|| (getReceiptNumber() != null && !getReceiptNumber().isEmpty())) {
			query.append(" where ");
			query.append(getDateQuery(getFromDate(), getToDate()));
			if (getFromDate() != null || getToDate() != null) {
				if (getServiceTypeId() != null && !getServiceTypeId().isEmpty() && !getServiceTypeId().equals("-1")) {
					query.append(" and mrd.servicename='").append(servicesearch).append("'");
				}
				if (getReceiptNumber() != null && !getReceiptNumber().isEmpty()) {
					query.append(" and lower(mrd.receipt_number)  like lower('%").append(getReceiptNumber())
							.append("%')");
				}
			} else if (getServiceTypeId() != null && !getServiceTypeId().isEmpty() && !getServiceTypeId().equals("-1")
					&& (getReceiptNumber() != null && !getReceiptNumber().isEmpty())) {
				if (getServiceTypeId() != null && !getServiceTypeId().isEmpty() && !getServiceTypeId().equals("-1")) {
					query.append(" mrd.servicename='").append(servicesearch).append("'");
				}
				if (getReceiptNumber() != null && !getReceiptNumber().isEmpty()) {
					query.append(" and lower(mrd.receipt_number)  like lower('%").append(getReceiptNumber())
							.append("%')");
				}
			} else {
				if (getServiceTypeId() != null && !getServiceTypeId().isEmpty() && !getServiceTypeId().equals("-1")) {
					query.append(" mrd.servicename='").append(servicesearch).append("'");
				}
				if (getReceiptNumber() != null && !getReceiptNumber().isEmpty()) {
					query.append(" lower(mrd.receipt_number)  like lower('%").append(getReceiptNumber()).append("%')");
				}
			}
		}

		System.out.println(">>> " + query);
		try {
			Query q = entityManager.createNativeQuery(query.toString());
			list = q.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">List Size>> " + list.size());
		int i = 1;
		if (list.size() != 0) {
			try {
				for (final Object[] ob : list) {

					String depcode = "";
					String service = null;
					String serviceType = null;
					ReceiptHeader receiptHeader = new ReceiptHeader();
					// receiptHeader.setPaymentId(receipt.getPaymentId());
					receiptHeader.setReceiptnumber(ob[1].toString());

					receiptHeader.setReceiptdatenew((ob[2] != null ? ob[2].toString() : ""));
					if (ob[10] != null && ob[10].toString() != null) {
						String s3 = null;
						String s4 = null;
						String s1 = null;
						String s2 = null;

						String[] split = ob[10].toString().split(Pattern.quote("."));
						s1 = split[0];
						if (split.length == 2) {
							s2 = split[1];

						}
						if (serviceCategoryNames.containsKey(s1)) {
							s3 = serviceCategoryNames.get(s1);
						}
						if (serviceCategoryNames.containsKey(s2)) {
							s4 = serviceCategoryNames.get(s2);
						}

						/*
						 * if (s4 != null) { if (s3 != null) { service = s3 + "." + s4; } else { service
						 * = s4; }
						 * 
						 * } else { service = s3; }
						 */
						service=s3;
						serviceType=s2;
					}
					receiptHeader.setServiceType(serviceType!= null ? serviceType : "");
					receiptHeader.setService((service != null ? service : ""));
					//receiptHeader.setChequeddno(ob[14]!=null?ob[14].toString():"");
					//receiptHeader.setChequedddate(ob[15]!=null?ob[15].toString():"");
					receiptHeader.setReferencenumber("");

					if (ob[5] != null) {
						receiptHeader.setReferenceDesc((ob[5] != null ? ob[5].toString() : ""));
					}

					receiptHeader.setPayeeName("");

					if (ob[4] != null) {
						receiptHeader.setPayeeAddress((ob[4] != null ? ob[4].toString() : ""));
					}

					// System.out.println("10");
					receiptHeader.setPaidBy((ob[3] != null ? ob[3].toString() : ""));

					receiptHeader.setCurretnStatus((ob[6] != null ? ob[6].toString() : ""));
					if (ob[9] != null) {
						receiptHeader.setSubdivison((ob[9] != null ? ob[9].toString() : ""));
					}

					if (ob[12] != null) {
						receiptHeader.setGstno((ob[12] != null ? ob[12].toString() : ""));
					}

					receiptHeader.setCurrentreceipttype("test");

					receiptHeader.setModOfPayment((ob[13] != null ? ob[13].toString() : ""));

					if (ob[0] != null) {
						receiptHeader.setDepositAmount((ob[0] != null ? ob[0].toString() : ""));
					}
					if (ob[14] != null) {
						receiptHeader.setPaymentId((ob[14] != null ? ob[14].toString() : ""));
					}
					//if (ob[15] != null) {
						receiptHeader.setBank((ob[7] != null ? ob[7].toString() : ""));
						//receiptHeader.setBank((ob[15] != null ? ob[15].toString() : ""));
					//}
					EmployeeInfo empInfo = null;
					if (ob[11] != null) {
						receiptHeader.setCreatedUser((ob[11] != null ? ob[11].toString() : ""));
					}

					String dep = "";

					if (gllist != null && !gllist.isEmpty()) {

						if (gllist.containsKey(ob[1].toString())) {

							receiptHeader.setPrincipalAmount(gllist.get(ob[1].toString()).getPaAmount());
							receiptHeader.setGstAmount(gllist.get(ob[1].toString()).getGstAmount());
							receiptHeader.setTotalAmount(
									(receiptHeader.getPrincipalAmount()).add(receiptHeader.getGstAmount()));
							receiptHeader.setDepartment(dplist.get(gllist.get(ob[1].toString()).getDepartment()));
							depcode = gllist.get(ob[1].toString()).getDepartment();
						}

					}

					if (getDeptId() != null && !getDeptId().isEmpty()) {
						if (getDeptId().equals(depcode)) {
							receiptList.add(receiptHeader);
						}

					} else {

						receiptList.add(receiptHeader);
						i++;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		long end = System.currentTimeMillis();
		System.out.println("total time taken :: " + (end - start));
		System.out.println("Total Record found " + i);
		System.out.println(":::::: page  " + getPage());

		if (getPage() >= 1) {
			int showlist = 490 * getPage();
			int fromIndex = 0;
			int toIndex = 0;
			if (showlist > receiptList.size()) {
				fromIndex = (490 * (getPage() - 1));
				toIndex = receiptList.size();
			} else {
				if (receiptList.size() < 490) {
					fromIndex = 0;
					toIndex = receiptList.size();
				} else {
					fromIndex = showlist - 490;
					toIndex = showlist;
				}
			}

			System.out.println("from::: " + fromIndex + "to:::: " + toIndex);
			receiptListfinal = receiptList.subList(fromIndex, toIndex);
		}

		System.out.println("::::final list size:  " + receiptListfinal.size());
		if (searchResult == null) {
			// Page page = new Page<ReceiptHeader>(getPage(), 490, receiptList);
			Page page = new Page<ReceiptHeader>(getPage(), 490, receiptListfinal);
			searchResult = new EgovPaginatedList(page, receiptList.size());
			// searchResult = new EgovPaginatedList(page, receiptListfinal.size());
		} else {
			searchResult.getList().clear();
			searchResult.getList().addAll(receiptList);
		}

		resultList = searchResult.getList();
		return SUCCESSNEW;
	}

	@Action(value = "/receipts/searchReceipt-searchDayBookReport")
	public String searchDayBookReport() {
		target = "searchresult";
		collectionVersion = ApplicationThreadLocals.getCollectionVersion();

		List<ReceiptHeader> receiptList = new ArrayList<>();
		System.out.println("from date ::" + getFromDate());
		System.out.println("to date :::" + getToDate());
		System.out.println("getServiceTypeId() :::" + getServiceTypeId());
		if (getServiceTypeId().equalsIgnoreCase("")) {
			setServiceTypeId(null);
		}
		serviceTypeIdforExcel = getServiceTypeId();
		List<Receipt> receipts = microserviceUtils.searchReciepts("MISCELLANEOUS", getFromDate(), getToDate(),
				getServiceTypeId(), getDeptId(), (getReceiptNumber() != null && !getReceiptNumber().isEmpty()
						&& !"".equalsIgnoreCase(getReceiptNumber())) ? getReceiptNumber() : null);

		List<RemittanceDepositWorkDetail> remittanceResponselist = null;

		BigDecimal totalReciptAmount = BigDecimal.ZERO;
		BigDecimal totalReciptAmount2 = BigDecimal.ZERO;
		totalReciptAmount2 = totalReciptAmount.setScale(2, BigDecimal.ROUND_DOWN);
		System.out.println("check total amount=" + totalReciptAmount2);

		BigDecimal totalDepositAmount = BigDecimal.ZERO;
		BigDecimal totalDepositAmount2 = BigDecimal.ZERO;
		totalDepositAmount2 = totalDepositAmount2.setScale(2, BigDecimal.ROUND_DOWN);

		if (receipts != null && !receipts.isEmpty()) {
			remittanceResponselist = getDepositWork(receipts);
		}

		for (Receipt receipt : receipts) {
			System.out.println("1");
			for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {
				System.out.println("2");
				for (BillDetail billDetail : bill.getBillDetails()) {
					System.out.println("3");
					ReceiptHeader receiptHeader = new ReceiptHeader();
					receiptHeader.setPaymentId(receipt.getPaymentId());
					receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
					populateBifurcationAmount(receiptHeader);
					System.out.println("4");
					receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
					System.out.println("5");
					receiptHeader.setService(
							microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
					System.out.println("6");
					receiptHeader.setReferencenumber("");
					System.out.println("7");
					receiptHeader.setReferenceDesc(bill.getNarration());
					System.out.println("8");
					receiptHeader.setPayeeName(bill.getPayerName());
					System.out.println("9");
					receiptHeader.setPayeeAddress(bill.getPayerAddress());
					System.out.println("10");
					receiptHeader.setPaidBy(bill.getPaidBy().split("&")[0]);
					System.out.println("11");
					if (subdivison != null && !subdivison.isEmpty() && !subdivison.equalsIgnoreCase("-1")
							&& !subdivison.contains("-1") && !subdivison.equalsIgnoreCase(receipt.getSubdivison())) {
						continue;
					}
					receiptHeader.setCurretnStatus(receipt.getPaymentStatus());
					if (receiptType != null && !receiptType.isEmpty() && !receiptType.equalsIgnoreCase("-1")
							&& !receiptType.contains("-1")
							&& !receiptType.equalsIgnoreCase(receiptHeader.getCurretnStatus())) {
						continue;
					}
					receiptHeader.setSubdivison(receipt.getSubdivison());
					System.out.println("12");
					receiptHeader.setGstno(receipt.getGstNo());
					System.out.println("13");

					System.out.println("14");
					receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
					System.out.println("15");
					receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());
					System.out.println("16");
					EmployeeInfo empInfo = null;
					try {
						empInfo = microserviceUtils
								.getEmployee(Long.parseLong(receipt.getAuditDetails().getCreatedBy()), null, null, null)
								.get(0);
					} catch (Exception e) {
						System.out.println("employee not found");
					}
					System.out.println("17");
					if (null != empInfo && empInfo.getUser().getUserName() != null
							&& !empInfo.getUser().getUserName().isEmpty()) {
						receiptHeader.setCreatedUser(empInfo.getUser().getName());
					} else {
						receiptHeader.setCreatedUser("");
					}
					System.out.println("18");

					// add Work Deposit

					if (remittanceResponselist != null && remittanceResponselist != null) {
						for (RemittanceDepositWorkDetail remittance : remittanceResponselist) {

							if (billDetail.getReceiptNumber().equals(remittance.getReciptNumber())) {
								receiptHeader.setReferencenumber(remittance.getReferenceNumber());
								receiptHeader.setRrDate(new Date(remittance.getReferenceDate()));
								receiptHeader.setDepositAmount(remittance.getCreditAmount());

								BigDecimal depositAmount = new BigDecimal(remittance.getCreditAmount());
								receiptHeader.setTotalDepositAmount(depositAmount);
								// totalDepositAmount2=totalDepositAmount2.add(depositAmount.setScale(4,
								// BigDecimal.ROUND_DOWN));

							} else {
								receiptHeader.setReferencenumber("");
							}

						}
					}

					System.out.println("19");
					receiptList.add(receiptHeader);

				}
			}

		}
		System.out.println("20");
		System.out.println("receiptList  :::::" + receiptList.size());
		/// Bhushan :added filter to list
		List<ReceiptHeader> receiptListfilterList = receiptList;
		List<ReceiptHeader> receiptListnew = new ArrayList<>();
		// receiptList.clear();
		System.out.println("receiptListfilterList  :::::" + receiptListfilterList.size());
		System.out.println("searchAmount  ::::" + searchAmount);
		System.out.println("modeOfPayment  ::::" + modeOfPayment);
		System.out.println("collectedBy  ::::" + collectedBy);
		for (ReceiptHeader receiptHeader2 : receiptListfilterList) {

			BigDecimal searchamt = new BigDecimal(searchAmount);
			System.out.println("inside1  ::::");
			BigDecimal searchamtbig = null;
			if (receiptHeader2.getTotalAmount() != null) {
				System.out.println("inside3  ::::");
				searchamtbig = receiptHeader2.getTotalAmount().setScale(4, BigDecimal.ROUND_DOWN);
				System.out.println("inside4  ::::");
			}
			System.out.println("inside5  ::::");
			searchamt = searchamt.setScale(4, BigDecimal.ROUND_DOWN);

			if (searchAmount != null && !searchAmount.equals("0") && !searchAmount.isEmpty()) {
				if (collectedBy != null && !collectedBy.isEmpty()) {
					if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
						if (searchamtbig.equals(searchamt)
								&& (receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase())
								&& receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
							if (receiptHeader2.getTotalDepositAmount() != null) {
								totalDepositAmount2 = totalDepositAmount2
										.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							if (receiptHeader2.getTotalAmount() != null) {
								totalReciptAmount2 = totalReciptAmount2
										.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							receiptListnew.add(receiptHeader2);

						}
					} else {
						if (searchamtbig.equals(searchamt) && (receiptHeader2.getCreatedUser().toLowerCase())
								.contains(collectedBy.toLowerCase())) {
							if (receiptHeader2.getTotalDepositAmount() != null) {
								totalDepositAmount2 = totalDepositAmount2
										.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							if (receiptHeader2.getTotalAmount() != null) {
								totalReciptAmount2 = totalReciptAmount2
										.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							receiptListnew.add(receiptHeader2);

						}

					}
				} else if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
					if (searchamtbig.equals(searchamt)
							&& receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				} else {
					if (searchamtbig.equals(searchamt)) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				}
			} else if (collectedBy != null && !collectedBy.isEmpty()) {
				if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
					if ((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase())
							&& receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				} else {
					if ((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase())) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				}

			} else if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
				if (receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
					if (receiptHeader2.getTotalDepositAmount() != null) {
						totalDepositAmount2 = totalDepositAmount2
								.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if (receiptHeader2.getTotalAmount() != null) {
						totalReciptAmount2 = totalReciptAmount2
								.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
					}
					receiptListnew.add(receiptHeader2);

				}

			} else {
				if (receiptHeader2.getTotalDepositAmount() != null) {
					totalDepositAmount2 = totalDepositAmount2
							.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
				}
				if (receiptHeader2.getTotalAmount() != null) {
					totalReciptAmount2 = totalReciptAmount2
							.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
				}
				receiptListnew.add(receiptHeader2);
			}

		}

		System.out.println("receiptListnew  ::::" + receiptListnew.size());
		for (ReceiptHeader receiptHeadern : receiptListnew) {
			receiptHeadern.setTotalreciptAmount(totalReciptAmount2);
			receiptHeadern.setTotalDepositAmount(totalDepositAmount2);
		}

		if (searchResult == null) {
			Page page = new Page<ReceiptHeader>(1, receiptListnew.size(), receiptListnew);
			searchResult = new EgovPaginatedList(page, receiptListnew.size());
		} else {
			searchResult.getList().clear();
			searchResult.getList().addAll(receiptListnew);
		}

		resultList = searchResult.getList();
		return DAYBOOKREPORT;
	}

	private void populateBifurcationAmount(ReceiptHeader receiptHeader) {
		SQLQuery query = null;
		List<Object[]> rows = null;
		BigDecimal pAmount = new BigDecimal("0");
		BigDecimal gstAmount = new BigDecimal("0");
		BigDecimal totalAmount = new BigDecimal("0");
		try {
			query = this.persistenceService.getSession().createSQLQuery(
					"select gl.id,gl.glcode,gl.debitamount,gl.creditamount from generalledger gl where voucherheaderid =(select vmis.voucherheaderid from vouchermis vmis where vmis.reciept_number =:receipt_no)");
			query.setString("receipt_no", receiptHeader.getReceiptnumber());
			rows = query.list();

			if (rows != null && !rows.isEmpty()) {
				for (Object[] element : rows) {
					if (!element[3].toString().equalsIgnoreCase("0.00")) {
						if (element[1].toString().equalsIgnoreCase("3502020")
								|| element[1].toString().equalsIgnoreCase("3502019")) {
							gstAmount = gstAmount.add(new BigDecimal(element[3].toString()));
						} else {
							pAmount = pAmount.add(new BigDecimal(element[3].toString()));
						}
					} else {
						totalAmount = totalAmount.add(new BigDecimal(element[2].toString()));
					}

				}
				receiptHeader.setPrincipalAmount(pAmount);
				receiptHeader.setGstAmount(gstAmount);
				receiptHeader.setTotalAmount(totalAmount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	@Action(value = "/receipts/searchReceipt-searchDayBookReportNew")
	public String searchDayBookReportNew() {
		target = "searchresult";
		collectionVersion = ApplicationThreadLocals.getCollectionVersion();

		long start = System.currentTimeMillis();
		String servicecategory1 = null;
		String servicecategory = null;
		String sertype = null;
		String sertype1 = null;
		String servicesearch = null;
		List<ReceiptHeader> receiptList = null;
		List<ReceiptHeader> receiptListfinal = new ArrayList<>();
		//receiptList.clear();

		System.out.println("::::Service Search:::: " + servicesearch);
		System.out.println("getServiceTypeId() :::" + getServiceTypeId());
		System.out.println(":::::getDeptId:::: " + getDeptId());
		System.out.println(":::::getCollectedBy:::: " + getCollectedBy());
		System.out.println(":::::Amount:::: " + getSearchAmount());
		System.out.println(":::::getModeOfPayment:::: " + getModeOfPayment());
		System.out.println(":::::getSubdivison:::: " + getSubdivison());
		System.out.println(":::::Amount:::: " + getReceiptType());
		System.out.println(":::::servicetype:::: " + getServiceId());
		if (getServiceTypeId().equalsIgnoreCase("")) {
			setServiceTypeId(null);
		}
		serviceTypeIdforExcel = getServiceTypeId();
		List<Receipt> receipts = new ArrayList<>();
		servicesearch = getServiceTypeId();

		Map<String, Amount> gllist = populateBifurcationAmountNew(getDeptId());
		Map<String, String> dplist = getAlldepartment();

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;

		query.append(
				"select mrd.total_amt_paid,mrd.receipt_number,to_char(mrd.receipt_date,'dd/mm/yyyy') as recDate,mrd.paid_by,mrd.payer_address,mrd.narration,mrd.payment_status,mrd.bank_name,mrd.bank_branch,mrd.subdivison,mrd.servicename,mrd.collectedbyname,mrd.gstno,mrd.payment_mode from mis_receipts_details mrd where ");
		//"select mrd.total_amt_paid,mrd.receipt_number,to_char(mrd.receipt_date,'dd/mm/yyyy') as recDate,mrd.paid_by,mrd.payer_address,mrd.narration,mrd.payment_status,mrd.bank_name,mrd.bank_branch,mrd.subdivison,mrd.servicename,mrd.collectedbyname,mrd.gstno,mrd.payment_mode,mrd.chequeddno ,to_char(mrd.chequedddate, 'dd/mm/yyyy') from mis_receipts_details mrd where ");
		query.append(getDateQuery(getFromDate(), getToDate()));
		query.append(getMisQuery(servicesearch, null, null, getCollectedBy(), getSearchAmount(), getModeOfPayment(),
				getSubdivison(), getReceiptType()));

		System.out.println("receipt query>>> " + query);
		try {
			Query q = entityManager.createNativeQuery(query.toString());
			list = q.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("::::Size of receipt list::: " + list.size());
		final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> remittlist = null;
		HashMap<String, List<ReceiptHeader>> receiptMap = new HashMap<>();
		HashMap<String, List<ReceiptHeader>> remittMap = new HashMap<>();
		query1.append(
				"select mrd.voucher_number,to_char(mrd.voucher_date,'dd/mm/yyyy'),mrd.bankaccount,mrd.amount,mrd.receiptnumbers,mrd.department from mis_remittance_details mrd where ");
		query1.append(getDateQuery1(getFromDate(), getToDate()));
		if (getSubdivison() != null && !getSubdivison().isEmpty() && !getSubdivison().equals("-1")) {
			query1.append(" and mrd.subdivison='").append(getSubdivison()).append("'");
		}
		System.out.println("remittance query>>> " + query1);
		try {
			Query q1 = entityManager.createNativeQuery(query1.toString());
			remittlist = q1.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("::::Size of remitt list::: " + remittlist.size());
		if (list.size() != 0) {
			for (final Object[] ob : list) {
				//receipytHeader -->receipt
					String depcode = "";
					String service = null;
					String serviceType = null;
					ReceiptHeader receiptHeader = new ReceiptHeader();
						receiptHeader.setReceiptnumber(ob[1].toString());
						receiptHeader.setReceiptdatenew(ob[2] != null ? ob[2].toString() : "");
						if (ob[10] != null) {
							String s3 = null;
							String s4 = null;
							String s1 = null;
							String s2 = null;
							String[] split = ob[10].toString().split(Pattern.quote("."));
							s1 = split[0];
							if (split.length == 2) {
								s2 = split[1];
							}
							if (serviceCategoryNames.containsKey(s1)) {
								s3 = serviceCategoryNames.get(s1);
							}
							if (serviceCategoryNames.containsKey(s2)) {
								s4 = serviceCategoryNames.get(s2);
							}
					/*
					 * if (s4 != null) { if (s3 != null) { service = s3 + "." + s4; } else { service
					 * = s4; } } else { service = s3; }
					 */
							service=s3;
							serviceType=s2;
						}
						receiptHeader.setServiceType(serviceType!= null ? serviceType : "");
						receiptHeader.setService((service != null ? service : ""));
						//receiptHeader.setChequeddno(ob[14]!=null?ob[14].toString():"");
						//receiptHeader.setChequedddate(ob[15]!=null?ob[15].toString():"");
						receiptHeader.setBank(ob[7]!=null?ob[7].toString():"");
						receiptHeader.setReferencenumber("");
						if (ob[5] != null) {
							receiptHeader.setReferenceDesc(ob[5].toString());
						}
						receiptHeader.setPayeeName("");
						if (ob[4] != null) {
							receiptHeader.setPayeeAddress(ob[4].toString());
						}
						if (ob[3] != null) {
							receiptHeader.setPaidBy(ob[3].toString());
						}
						receiptHeader.setCurretnStatus(ob[6].toString());
						if (ob[9] != null) {
							receiptHeader.setSubdivison(ob[9].toString());
						}
						if (ob[12] != null) {
							receiptHeader.setGstno(ob[12].toString());
						}
						receiptHeader.setCurrentreceipttype("test");
						receiptHeader.setModOfPayment(ob[13].toString());
						if (ob[11] != null) {
							receiptHeader.setCreatedUser(ob[11].toString());
						}
						if (gllist != null && !gllist.isEmpty()) {
							if (gllist.containsKey(ob[1].toString())) {
								receiptHeader.setPrincipalAmount(gllist.get(ob[1].toString()).getPaAmount());
								receiptHeader.setGstAmount(gllist.get(ob[1].toString()).getGstAmount());
						if (receiptHeader.getGstAmount() != null && receiptHeader.getPrincipalAmount() != null) {
									receiptHeader.setTotalAmount(
											(receiptHeader.getPrincipalAmount()).add(receiptHeader.getGstAmount()));
								} else if (receiptHeader.getGstAmount() == null
										&& receiptHeader.getPrincipalAmount() != null) {
									receiptHeader.setTotalAmount(receiptHeader.getPrincipalAmount());
								} else if (receiptHeader.getGstAmount() != null
										&& receiptHeader.getPrincipalAmount() == null) {
									receiptHeader.setTotalAmount(receiptHeader.getGstAmount());
								}
								receiptHeader.setDepartment(dplist.get(gllist.get(ob[1].toString()).getDepartment()));
								depcode = gllist.get(ob[1].toString()).getDepartment();
							}
						}
						if (getDeptId() != null && !getDeptId().isEmpty()) {
					if (!getDeptId().equals(depcode)) {
						//receiptList.add(receiptHeader);
						continue;
					}
							}
				if(receiptMap.get(receiptHeader.getReceiptdatenew()) == null ){
					receiptList=new ArrayList<>();
							receiptList.add(receiptHeader);
					receiptMap.put(receiptHeader.getReceiptdatenew(),receiptList);
						}
				else{
					receiptMap.get(receiptHeader.getReceiptdatenew()).add(receiptHeader);//map.get().add();
						}
							}
						}
		if (remittlist.size() != 0) {
			for (final Object[] ob : remittlist) {
					String depcode = "";
					ReceiptHeader receiptHeader = new ReceiptHeader();
				try {
					receiptHeader.setReferencenumber(ob[0]!=null ? ob[0].toString(): "");
					receiptHeader.setDepositDate(ob[1]!=null ? ob[1].toString(): "");
					receiptHeader.setBankAccountNumber(ob[2]!=null ? ob[2].toString(): "");
					receiptHeader.setDepositAmount(ob[3]!=null ? ob[3].toString(): "");
					receiptHeader.setReceiptnumber(ob[4]!=null ? ob[4].toString(): "");
					receiptHeader.setDepartment(ob[5]!=null ? dplist.get(ob[5].toString()): "");
					if (getDeptId() != null && !getDeptId().isEmpty()) {
						if (!getDeptId().equals(receiptHeader.getDepartment())) {
							continue;//receiptList.add(receiptHeader);
						}
						}
					if(remittMap.get(receiptHeader.getDepositDate()) == null ){
						receiptList=new ArrayList<>();
						receiptList.add(receiptHeader);
						remittMap.put(receiptHeader.getDepositDate(),receiptList);//map.put
						}
					else{
						remittMap.get(receiptHeader.getDepositDate()).add(receiptHeader);//map.get().add();
						}
				} catch (Exception e) {
					e.printStackTrace();
					}

					}
					}
		List<ReceiptHeader> finalList = new ArrayList<>();
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = null;
		Date endDate = null;
					try {
			startDate = getFromDate();// (Date)formatter.parse(str_date);
			endDate = getToDate();// (Date)formatter.parse(end_date);
					} catch (Exception e) {
						e.printStackTrace();
					}
		long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
		long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		for (Date d:dates) {
			Date lDate = (Date) d;
			String ds = formatter.format(lDate);
			System.out.println(" Date is ..." + ds);
			if(receiptMap.get(ds)!=null)
				finalList.addAll(receiptMap.get(ds));
			if(remittMap.get(ds)!=null)
			finalList.addAll(remittMap.get(ds));
					}
		System.out.println("finalList.size  :::::" + finalList.size());
		/// Bhushan :added filter to list
		List<ReceiptHeader> receiptListfilterList = finalList;
		List<ReceiptHeader> receiptListnew = new ArrayList<>();
		// receiptList.clear();

		System.out.println("receiptListfilterList  :::::" + receiptListfilterList.size());

		if (getDeptId() != null && !getDeptId().isEmpty()) {
			setDeptId(getDeptId());
		}
		if (getPage() >= 1) {
			int showlist = 490 * getPage();
			int fromIndex = 0;
			int toIndex = 0;
			if (showlist > finalList.size()) {
				fromIndex = (490 * (getPage() - 1));
				toIndex = finalList.size();
			} else {
				if (finalList.size() < 490) {
					fromIndex = 0;
					toIndex = finalList.size();
				} else {
					fromIndex = showlist - 490;
					toIndex = showlist;
				}
			}

			System.out.println("from::: " + fromIndex + "to:::: " + toIndex);
			receiptListfinal = finalList.subList(fromIndex, toIndex);
		}

		System.out.println("::::final list size:  " + receiptListfinal.size());

		if (searchResult == null) {
			Page page = new Page<ReceiptHeader>(getPage(), 490, receiptListfinal);
			searchResult = new EgovPaginatedList(page, finalList.size());
		} else {
			searchResult.getList().clear();
			searchResult.getList().addAll(receiptListnew);
		}
		long end = System.currentTimeMillis();
		System.out.println(":::::total time taken:: " + (end - start));
		resultList = searchResult.getList();
		return DAYBOOKREPORTNEW;
	}

	private Map<String, Amount> populateBifurcationAmountNew(String dept) {
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> rows = null;
		BigDecimal pAmount = null;
		BigDecimal gstAmount = null;
		BigDecimal totalAmount = null;
		Map<String, Amount> amtReceiptMap = new HashMap<>();
		try {
			query.append(
					"select gl.id,gl.glcode,gl.debitamount,gl.creditamount,v2.departmentcode,v2.reciept_number from generalledger gl left join vouchermis v2 on gl.voucherheaderid =v2.voucherheaderid where v2.reciept_number notnull ");
			if (dept != null && !dept.isEmpty()) {
				query.append(" and v2.departmentcode ='" + dept + "'");
			}
			System.out.println("Query>>>> " + query);
			Query q = entityManager.createNativeQuery(query.toString());
			rows = q.getResultList();
			System.out.println(":::::::General Ledger List size::: " + rows.size());
			Amount amt = null;
			for (Object[] element : rows) {
				if (amtReceiptMap.get(element[5].toString()) == null) {
					pAmount = new BigDecimal(0);
					gstAmount = new BigDecimal(0);
					totalAmount = new BigDecimal(0);
					if (!element[3].toString().equalsIgnoreCase("0.00")) {
						if (element[1].toString().equalsIgnoreCase("3502020")
								|| element[1].toString().equalsIgnoreCase("3502019")) {
							gstAmount = gstAmount.add(new BigDecimal(element[3].toString()));
						} else {
							pAmount = pAmount.add(new BigDecimal(element[3].toString()));
						}
					} else {
						totalAmount = totalAmount.add(new BigDecimal(element[2].toString()));
					}
					amt = new Amount();
					amt.setPaAmount(pAmount);
					amt.setGstAmount(gstAmount);
					amt.setTotalAmount(totalAmount);
					amt.setDepartment(element[4].toString());
					amtReceiptMap.put(element[5].toString(), amt);
				} else {
					pAmount = amtReceiptMap.get(element[5].toString()).getPaAmount();
					gstAmount = amtReceiptMap.get(element[5].toString()).getGstAmount();
					totalAmount = amtReceiptMap.get(element[5].toString()).getGstAmount();
					if (!element[3].toString().equalsIgnoreCase("0.00")) {
						if (element[1].toString().equalsIgnoreCase("3502020")
								|| element[1].toString().equalsIgnoreCase("3502019")) {
							gstAmount = gstAmount.add(new BigDecimal(element[3].toString()));
						} else {
							pAmount = pAmount.add(new BigDecimal(element[3].toString()));
						}
					} else {
						totalAmount = totalAmount.add(new BigDecimal(element[2].toString()));
					}
					amt = new Amount();
					amt.setPaAmount(pAmount);
					amt.setGstAmount(gstAmount);
					amt.setTotalAmount(totalAmount);
					amt.setDepartment(element[4].toString());
					amtReceiptMap.put(element[5].toString(), amt);
				}

			}
			return amtReceiptMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amtReceiptMap;
	}

	public Map<String, String> getAlldepartment() {
		List<Object[]> dep = null;
		Map<String, String> depar = new HashMap<>();
		final StringBuffer query = new StringBuffer(500);
		try {
			query.append("select ed.code ,ed.name from eg_department ed ");

			System.out.println("Query>>>> " + query);
			Query q = entityManager.createNativeQuery(query.toString());
			dep = q.getResultList();
			if (dep.size() > 0) {
				for (final Object[] ob : dep) {
					depar.put(ob[0].toString(), ob[1].toString());
				}
			}

			return depar;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return depar;
	}

	public String getMisQuery(String serviceCategory, String serviceType, String dep, String collectedby, String amount,
			String mop, String subDivision, String status) {

		final StringBuffer misQuery = new StringBuffer(300);

		if (collectedby != null && !collectedby.isEmpty()) {
			misQuery.append(" and lower(mrd.collectedbyname)  like lower('%").append(collectedby).append("%')");
		}
		if (serviceCategory != null && !serviceCategory.isEmpty()) {
			misQuery.append(" and mrd.servicename='").append(serviceCategory).append("'");
		}
		if (amount != null && !amount.isEmpty() && !amount.equals("0")) {
			misQuery.append(" and mrd.total_amt_paid='").append(amount).append("'");
		}
		if (mop != null && !mop.isEmpty() && !mop.equals("-1")) {
			misQuery.append(" and mrd.payment_mode='").append(mop).append("'");
		}
		if (subDivision != null && !subDivision.isEmpty() && !subDivision.equals("-1")) {
			misQuery.append(" and mrd.subdivison='").append(subDivision).append("'");
		}
		if (status != null && !status.isEmpty() && !status.equals("-1")) {
			misQuery.append(" and mrd.payment_status='").append(status).append("'");
		}

		return misQuery.toString();

	}

	@Action(value = "/receipts/searchReceipt-downloadDayBookReport")
	public String downloadDayBookReport() {
		target = "searchresult";
		collectionVersion = ApplicationThreadLocals.getCollectionVersion();
		System.out.println("from date ::" + getFromDate());
		System.out.println("to date :::" + getToDate());
		System.out.println("getServiceTypeId() :::" + getServiceTypeId());
		System.out.println("serviceTypeDuringDownload :::" + serviceTypeDuringDownload);

		if (serviceTypeDuringDownload.equalsIgnoreCase("") || serviceTypeDuringDownload.contains("0")
				|| serviceTypeDuringDownload.contains("-1")) {
			serviceTypeDuringDownload = null;
		}

		List<ReceiptHeader> receiptList = new ArrayList<>();
		List<Receipt> receipts = microserviceUtils.searchReciepts("MISCELLANEOUS", getFromDate(), getToDate(),
				serviceTypeDuringDownload, getDeptId(), (getReceiptNumber() != null && !getReceiptNumber().isEmpty()
						&& !"".equalsIgnoreCase(getReceiptNumber())) ? getReceiptNumber() : null);

		List<RemittanceDepositWorkDetail> remittanceResponselist = null;

		BigDecimal totalReciptAmount = BigDecimal.ZERO;
		BigDecimal totalReciptAmount2 = BigDecimal.ZERO;
		totalReciptAmount2 = totalReciptAmount.setScale(2, BigDecimal.ROUND_DOWN);

		BigDecimal totalDepositAmount = BigDecimal.ZERO;
		BigDecimal totalDepositAmount2 = BigDecimal.ZERO;
		totalDepositAmount2 = totalDepositAmount2.setScale(2, BigDecimal.ROUND_DOWN);
		if (receipts != null && !receipts.isEmpty()) {
			remittanceResponselist = getDepositWork(receipts);
		}

		System.out.println("subdivison ::::" + subdivison);
		for (Receipt receipt : receipts) {
			System.out.println("R");
			for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {
				System.out.println("R1");
				for (BillDetail billDetail : bill.getBillDetails()) {
					System.out.println("R2");
					ReceiptHeader receiptHeader = new ReceiptHeader();
					receiptHeader.setPaymentId(receipt.getPaymentId());
					receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
					System.out.println("11");
					populateBifurcationAmount(receiptHeader);
					System.out.println("22");
					receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
					System.out.println("33");
					receiptHeader.setService(
							microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
					System.out.println("44");
					receiptHeader.setReferencenumber("");
					System.out.println("55");
					receiptHeader.setReferenceDesc(bill.getNarration());
					System.out.println("66");
					receiptHeader.setPayeeAddress(bill.getPayerAddress());
					System.out.println("77");
					receiptHeader.setPaidBy(bill.getPaidBy().split("&")[0]);
					System.out.println("88");
					if (subdivison != null && !subdivison.isEmpty() && !subdivison.equalsIgnoreCase("-1")
							&& !subdivison.contains("-1") && !subdivison.equalsIgnoreCase(receipt.getSubdivison())) {
						continue;
					}

					System.out.println("99");
					receiptHeader.setSubdivison(receipt.getSubdivison());
					System.out.println("10");
					receiptHeader.setGstno(receipt.getGstNo());
					System.out.println("11");
					receiptHeader.setTotalreciptAmount(totalReciptAmount);
					System.out.println("12");
					receiptHeader.setCurretnStatus(receipt.getPaymentStatus());
					System.out.println("receiptType  ::::" + receiptType);
					System.out.println("receiptHeader.getCurretnStatus()  ::::" + receiptHeader.getCurretnStatus());
					if (receiptType != null && !receiptType.isEmpty() && !receiptType.equalsIgnoreCase("-1")
							&& !receiptType.contains("-1")
							&& !receiptType.equalsIgnoreCase(receiptHeader.getCurretnStatus())) {
						continue;
					}
					System.out.println("13");
					receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
					System.out.println("Mid");
					receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());
					EmployeeInfo empInfo = null;
					try {
						empInfo = microserviceUtils
								.getEmployee(Long.parseLong(receipt.getAuditDetails().getCreatedBy()), null, null, null)
								.get(0);
					} catch (Exception e) {
						System.out.println("employee not found");
					}
					System.out.println("after employee");
					if (null != empInfo && empInfo.getUser().getUserName() != null
							&& !empInfo.getUser().getUserName().isEmpty()) {
						receiptHeader.setCreatedUser(empInfo.getUser().getName());
					} else {
						receiptHeader.setCreatedUser("");
					}

					// add Work Deposit
					System.out.println("before remmittance");

					if (remittanceResponselist != null && !remittanceResponselist.isEmpty()) {
						for (RemittanceDepositWorkDetail remittance : remittanceResponselist) {

							if (billDetail.getReceiptNumber().equals(remittance.getReciptNumber())) {
								receiptHeader.setReferencenumber(remittance.getReferenceNumber());
								receiptHeader.setRrDate(new Date(remittance.getReferenceDate()));
								receiptHeader.setDepositAmount(remittance.getCreditAmount());

								BigDecimal depositAmount = new BigDecimal(remittance.getCreditAmount());
								receiptHeader.setTotalDepositAmount(depositAmount);

							} else {
								receiptHeader.setReferencenumber("");
							}

						}
					}
					System.out.println("list addition");
					receiptList.add(receiptHeader);

				}
			}

		}

		System.out.println("receiptList.size() ::::" + receiptList.size());
		/// Bhushan
		List<ReceiptHeader> receiptListfilterList = receiptList;
		System.out.println("receiptListfilterList.size() ::::" + receiptListfilterList.size());
		List<ReceiptHeader> receiptListnew = new ArrayList<>();
		// receiptList.clear();

		System.out.println("searchAmount :::" + searchAmount);
		for (ReceiptHeader receiptHeader2 : receiptListfilterList) {

			BigDecimal searchamt = new BigDecimal(searchAmount);
			BigDecimal searchamtbig = null;
			if (receiptHeader2.getTotalAmount() != null) {
				searchamtbig = receiptHeader2.getTotalAmount().setScale(4, BigDecimal.ROUND_DOWN);
			}
			searchamt = searchamt.setScale(4, BigDecimal.ROUND_DOWN);

			if (searchAmount != null && !searchAmount.equals("0") && !searchAmount.isEmpty()) {
				System.out.println("111111");
				if (collectedBy != null && !collectedBy.isEmpty()) {
					System.out.println("222222");
					if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
						System.out.println("333333");
						if (searchamtbig.equals(searchamt)
								&& (receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase())
								&& receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
							if (receiptHeader2.getTotalDepositAmount() != null) {
								totalDepositAmount2 = totalDepositAmount2
										.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							if (receiptHeader2.getTotalAmount() != null) {
								totalReciptAmount2 = totalReciptAmount2
										.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							receiptListnew.add(receiptHeader2);

						}
					} else {
						System.out.println("55555");
						if (searchamtbig.equals(searchamt) && (receiptHeader2.getCreatedUser().toLowerCase())
								.contains(collectedBy.toLowerCase())) {
							if (receiptHeader2.getTotalDepositAmount() != null) {
								totalDepositAmount2 = totalDepositAmount2
										.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							if (receiptHeader2.getTotalAmount() != null) {
								totalReciptAmount2 = totalReciptAmount2
										.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
							}
							receiptListnew.add(receiptHeader2);

						}

					}
				} else if (modeOfPayment != null && !modeOfPayment.equals("")) {
					System.out.println("XXXXX");
					if (searchamtbig.equals(searchamt)
							&& receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				} else {
					System.out.println("AAAA");
					if (searchamtbig.equals(searchamt)) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				}
			} else if (collectedBy != null && !collectedBy.isEmpty()) {
				System.out.println("777777");
				if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
					System.out.println("888888");
					if ((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase())
							&& receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				} else {
					System.out.println("9999");
					if ((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase())) {
						if (receiptHeader2.getTotalDepositAmount() != null) {
							totalDepositAmount2 = totalDepositAmount2
									.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (receiptHeader2.getTotalAmount() != null) {
							totalReciptAmount2 = totalReciptAmount2
									.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
						}
						receiptListnew.add(receiptHeader2);

					}
				}

			} else if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
				System.out.println("WWWWWW");
				if (receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment)) {
					if (receiptHeader2.getTotalDepositAmount() != null) {
						totalDepositAmount2 = totalDepositAmount2
								.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if (receiptHeader2.getTotalAmount() != null) {
						totalReciptAmount2 = totalReciptAmount2
								.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
					}
					receiptListnew.add(receiptHeader2);

				}

			} else {
				System.out.println("GGGG");
				if (receiptHeader2.getTotalDepositAmount() != null) {
					totalDepositAmount2 = totalDepositAmount2
							.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
				}
				if (receiptHeader2.getTotalAmount() != null) {
					totalReciptAmount2 = totalReciptAmount2
							.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
				}
				receiptListnew.add(receiptHeader2);
			}

		}
		System.out.println("receiptListnew :::::" + receiptListnew.size());
		List<ReceiptReportBean> receiptReportList = new ArrayList<ReceiptReportBean>();
		ReceiptReportBean bean = null;
		int i = 1;
		if (receiptListnew != null && !receiptListnew.isEmpty()) {
			System.out.println("receipt non empty");
			for (ReceiptHeader header : receiptListnew) {
				bean = new ReceiptReportBean();
				bean.setSlNo(String.valueOf(i++));
				bean.setParamDate(getDateString(header.getReceiptdate()));
				bean.setReceiptNo(header.getReceiptnumber());
				bean.setCollectedBy(header.getCreatedUser());
				bean.setPayeeName(header.getPaidBy().split("&")[0]);
				bean.setServiceType(header.getService());
				bean.setModeOfPayment(header.getModOfPayment());
				bean.setParticulars(header.getReferenceDesc());
				bean.setTotalReceiptAmount(header.getTotalAmount());
				bean.setStatus(header.getCurretnStatus());
				if (header.getPrincipalAmount() != null) {
					bean.setPrincipalAmt(header.getPrincipalAmount());
				} else {
					bean.setPrincipalAmt(new BigDecimal("0"));
				}
				if (header.getGstAmount() != null) {
					bean.setGstAmount(header.getGstAmount());
				} else {
					bean.setPrincipalAmt(new BigDecimal("0"));
				}
				if (header.getRrDate() != null) {
					bean.setDateOfDeposite(getDateString(header.getRrDate()));
				} else {
					bean.setDateOfDeposite("");
				}
				if (header.getRemittanceReferenceNumber() != null) {
					bean.setRemitanceNo(String.valueOf(header.getReferencenumber()));
				} else {
					bean.setRemitanceNo("");
				}
				if (header.getBankAccountNumber() != null) {
					bean.setBankAccountNo(header.getBankAccountNumber());
				} else {
					bean.setBankAccountNo("");
				}

				if (header.getDepositAmount() != null) {
					bean.setDepositAmount(new BigDecimal(header.getDepositAmount()));
				} else {
					bean.setDepositAmount(new BigDecimal("0"));
				}
				receiptReportList.add(bean);
			}

		}
		System.out.println("report size ::" + receiptReportList.size());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String jasperName = "CollectionReport";
		paramMap.put("CollectionReportDataSource", getDataSource(receiptReportList));
		paramMap.put("HeaderParameter", "Detail of Collection Report between " + getDateString(getFromDate()) + " and "
				+ getDateString(getToDate()));
		System.out.println("Start of report");
		try {
			byte[] fileContent = populateExcel(receiptReportList, paramMap);
			inputStream = new ByteArrayInputStream(fileContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("END");
		return "XLS";
	}

	private byte[] populateExcel(List<ReceiptReportBean> receiptReportList, Map<String, Object> paramMap)
			throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Receipt Report");
		HSSFRow row = sheet.createRow(1);
		HSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue((String) paramMap.get("HeaderParameter"));
		HSSFRow rowhead = sheet.createRow(5);
		rowhead.createCell(0).setCellValue("Sl No.");
		rowhead.createCell(1).setCellValue("Date");
		rowhead.createCell(2).setCellValue("Receipt No.");
		rowhead.createCell(3).setCellValue("GST No.");
		rowhead.createCell(4).setCellValue("Collected By");
		rowhead.createCell(5).setCellValue("Payee Name");
		//rowhead.createCell(6).setCellValue("Service Type");
		//rowhead.createCell(7).setCellValue("Service Category");
		//rowhead.createCell(8).setCellValue("Cheque/DD No");
		//rowhead.createCell(9).setCellValue("Cheque/DD Date");
		//rowhead.createCell(10).setCellValue("Bank");
		rowhead.createCell(6).setCellValue("Mode of Payment");
		rowhead.createCell(7).setCellValue("Particulars");
		rowhead.createCell(8).setCellValue("Prinicpal Amount");
		rowhead.createCell(9).setCellValue("GST");
		rowhead.createCell(10).setCellValue("Total Receipt Amount");
		rowhead.createCell(11).setCellValue("Date of Deposit");
		rowhead.createCell(12).setCellValue("Remittance No.");
		rowhead.createCell(13).setCellValue("Bank Account No.");
		rowhead.createCell(14).setCellValue("Deposit Amount");
		rowhead.createCell(15).setCellValue("Status");
		int index = 1;
		int rowCount = 6;
		HSSFRow details;
		BigDecimal principalAmt = new BigDecimal("0");
		BigDecimal gstAmt = new BigDecimal("0");
		BigDecimal receiptAmt = new BigDecimal("0");
		BigDecimal depositAmt = new BigDecimal("0");
		for (ReceiptReportBean bean : receiptReportList) {
			details = sheet.createRow(rowCount++);
			details.createCell(0).setCellValue(index++);
			details.createCell(1).setCellValue(bean.getParamDate());
			details.createCell(2).setCellValue(bean.getReceiptNo());
			details.createCell(3).setCellValue(bean.getGstNo());
			details.createCell(4).setCellValue(bean.getCollectedBy());
			details.createCell(5).setCellValue(bean.getPayeeName());
			//details.createCell(6).setCellValue(bean.getServiceCategory());
			//details.createCell(7).setCellValue(bean.getServiceType());
			//details.createCell(8).setCellValue(bean.getChequeddno());
			//details.createCell(9).setCellValue(bean.getChequedddate());
			//details.createCell(10).setCellValue(bean.getBank());
			details.createCell(6).setCellValue(bean.getModeOfPayment());
			details.createCell(7).setCellValue(bean.getParticulars());
			if (bean.getPrincipalAmt() != null) {
				details.createCell(8).setCellValue(bean.getPrincipalAmt().doubleValue());
				principalAmt = principalAmt.add(bean.getPrincipalAmt());
			} else {
				details.createCell(8).setCellValue("");
			}
			if (bean.getGstAmount() != null) {
				details.createCell(9).setCellValue(bean.getGstAmount().doubleValue());
				gstAmt = gstAmt.add(bean.getGstAmount());
			} else {
				details.createCell(9).setCellValue("");
			}
			if (bean.getTotalReceiptAmount() != null) {
				details.createCell(10).setCellValue(bean.getTotalReceiptAmount().doubleValue());
				receiptAmt = receiptAmt.add(bean.getTotalReceiptAmount());
			} else {
				details.createCell(10).setCellValue("");
			}

			details.createCell(11).setCellValue(bean.getDateOfDeposite());
			details.createCell(12).setCellValue(bean.getRemitanceNo());
			details.createCell(13).setCellValue(bean.getBankAccountNo());
			if (bean.getDepositAmount() != null) {
				details.createCell(14).setCellValue(bean.getDepositAmount().doubleValue());
				depositAmt = depositAmt.add(bean.getDepositAmount());
			} else {
				details.createCell(14).setCellValue("");
			}
			details.createCell(15).setCellValue(bean.getStatus());
		}
		details = sheet.createRow(rowCount);
		details.createCell(7).setCellValue("Total");
		if (principalAmt != null) {
			details.createCell(8).setCellValue(principalAmt.doubleValue());
		} else {
			details.createCell(8).setCellValue("");
		}

		if (gstAmt != null) {
			details.createCell(9).setCellValue(gstAmt.doubleValue());
		} else {
			details.createCell(9).setCellValue("");
		}
		if (receiptAmt != null) {
			details.createCell(10).setCellValue(receiptAmt.doubleValue());
		} else {
			details.createCell(10).setCellValue("");
		}
		details.createCell(13).setCellValue("Total");
		if (depositAmt != null) {
			details.createCell(14).setCellValue(depositAmt.doubleValue());
		} else {
			details.createCell(14).setCellValue("");
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		System.out.println("XYZ");
		wb.write(os);
		System.out.println("UVW");
		byte[] fileContent = os.toByteArray();
		System.out.println("CCCC");

		return fileContent;
	}

	@Action(value = "/receipts/searchReceipt-downloadDayBookReportNew")
	public String downloadDayBookReportNew() {
		target = "searchresult";
		collectionVersion = ApplicationThreadLocals.getCollectionVersion();
		System.out.println("from date ::" + getFromDate());
		System.out.println("to date :::" + getToDate());
		System.out.println("getServiceTypeId() :::" + getServiceTypeId());
		System.out.println("serviceTypeDuringDownload :::" + serviceTypeDuringDownload);
		System.out.println("DepartmentDownload :::" + getDeptId());
		// getServiceCategoryList2();
		String dep=getDeptId();
		if(dep.equalsIgnoreCase(", "))
			dep=null;
		String[] dept = dep!=null?dep.split(","):null;

		String servicecategory1 = null;
		String servicecategory = null;
		String sertype = null;
		String sertype1 = null;
		String servicesearch = null;

		servicesearch = serviceTypeDuringDownload;
		Map<String, Amount> gllist = populateBifurcationAmountNew(dept!=null?dept[0]:"");
		Map<String, String> dplist = getAlldepartment();
		if (serviceTypeDuringDownload.equalsIgnoreCase("") || serviceTypeDuringDownload.contains("0")
				|| serviceTypeDuringDownload.contains("-1")) {
			serviceTypeDuringDownload = null;
		}

		List<ReceiptHeader> receiptList = new ArrayList<>();
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;

		query.append(
				//"select mrd.total_amt_paid,mrd.receipt_number,to_char(mrd.receipt_date,'dd/mm/yyyy') as recDate,mrd.paid_by,mrd.payer_address,mrd.narration,mrd.payment_status,mrd.bank_name,mrd.bank_branch,mrd.subdivison,mrd.servicename,mrd.collectedbyname,mrd.gstno,mrd.payment_mode,mrd.chequeddno,to_char(mrd.chequedddate,'dd/mm/yyyy') from mis_receipts_details mrd where ");
				"select mrd.total_amt_paid,mrd.receipt_number,to_char(mrd.receipt_date,'dd/mm/yyyy') as recDate,mrd.paid_by,mrd.payer_address,mrd.narration,mrd.payment_status,mrd.bank_name,mrd.bank_branch,mrd.subdivison,mrd.servicename,mrd.collectedbyname,mrd.gstno,mrd.payment_mode from mis_receipts_details mrd where ");
		query.append(getDateQuery(getFromDate(), getToDate()));
		query.append(getMisQuery(servicesearch, null, null, getCollectedBy(), getSearchAmount(), getModeOfPayment(),
				getSubdivison(), getReceiptType()));

		System.out.println("receipt query>>> " + query);
		try {
			Query q = entityManager.createNativeQuery(query.toString());
			list = q.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("::::Size of receipt list::: " + list.size());
		final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> remittlist = null;
		HashMap<String, List<ReceiptHeader>> receiptMap = new HashMap<>();
		HashMap<String, List<ReceiptHeader>> remittMap = new HashMap<>();
		query1.append(
				"select mrd.voucher_number,to_char(mrd.voucher_date,'dd/mm/yyyy'),mrd.bankaccount,mrd.amount,mrd.receiptnumbers,mrd.department from mis_remittance_details mrd where ");
		query1.append(getDateQuery1(getFromDate(), getToDate()));
		if (getSubdivison() != null && !getSubdivison().isEmpty() && !getSubdivison().equals("-1")) {
			query1.append(" and mrd.subdivison='").append(getSubdivison()).append("'");
			}
		System.out.println("remittance query>>> " + query1);
		try {
			Query q1 = entityManager.createNativeQuery(query1.toString());
			remittlist = q1.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("::::Size of remitt list::: " + remittlist.size());
		if (list.size() != 0) {
			for (final Object[] ob : list) {
				//receipytHeader -->receipt
				String depcode = "";
				String service = null;
				String serviceType=null;
				ReceiptHeader receiptHeader = new ReceiptHeader();
				receiptHeader.setReceiptnumber(ob[1].toString());
				receiptHeader.setReceiptdatenew(ob[2] != null ? ob[2].toString() : "");
				if (ob[10] != null) {
					String s3 = null;
					String s4 = null;
					String s1 = null;
					String s2 = null;
					String[] split = ob[10].toString().split(Pattern.quote("."));
					s1 = split[0];
					if (split.length == 2) {
						s2 = split[1];
					}
					if (serviceCategoryNames.containsKey(s1)) {
						s3 = serviceCategoryNames.get(s1);
					}
					if (serviceCategoryNames.containsKey(s2)) {
						s4 = serviceCategoryNames.get(s2);
					}
					/*
					 * if (s4 != null) { if (s3 != null) { service = s3 + "." + s4; } else { service
					 * = s4; } } else { service = s3; }
					 */
					service=s3;
					serviceType=s2;
				}
				receiptHeader.setServiceType(serviceType!= null ? serviceType : "");
				receiptHeader.setService((service != null ? service : ""));
			//	receiptHeader.setChequeddno(ob[14]!=null?ob[14].toString():"");
			//	receiptHeader.setChequedddate(ob[15]!=null?ob[15].toString():"");
				receiptHeader.setBank(ob[7]!=null?ob[7].toString():"");
				receiptHeader.setService(service != null ? service : "");
				receiptHeader.setReferencenumber("");
				if (ob[5] != null) {
					receiptHeader.setReferenceDesc(ob[5].toString());
				}
				receiptHeader.setPayeeName("");
				if (ob[4] != null) {
					receiptHeader.setPayeeAddress(ob[4].toString());
				}
				if (ob[3] != null) {
					receiptHeader.setPaidBy(ob[3].toString());
				}
				receiptHeader.setCurretnStatus(ob[6].toString());
				if (ob[9] != null) {
					receiptHeader.setSubdivison(ob[9].toString());
				}
				if (ob[12] != null) {
					receiptHeader.setGstno(ob[12].toString());
				}
				receiptHeader.setCurrentreceipttype("test");
				receiptHeader.setModOfPayment(ob[13].toString());
				if (ob[11] != null) {
					receiptHeader.setCreatedUser(ob[11].toString());
				}
				if (gllist != null && !gllist.isEmpty()) {
					if (gllist.containsKey(ob[1].toString())) {
						receiptHeader.setPrincipalAmount(gllist.get(ob[1].toString()).getPaAmount());
						receiptHeader.setGstAmount(gllist.get(ob[1].toString()).getGstAmount());
						if (receiptHeader.getGstAmount() != null && receiptHeader.getPrincipalAmount() != null) {
							receiptHeader.setTotalAmount(
									(receiptHeader.getPrincipalAmount()).add(receiptHeader.getGstAmount()));
						} else if (receiptHeader.getGstAmount() == null
								&& receiptHeader.getPrincipalAmount() != null) {
							receiptHeader.setTotalAmount(receiptHeader.getPrincipalAmount());
						} else if (receiptHeader.getGstAmount() != null
								&& receiptHeader.getPrincipalAmount() == null) {
							receiptHeader.setTotalAmount(receiptHeader.getGstAmount());
						}
						receiptHeader.setDepartment(dplist.get(gllist.get(ob[1].toString()).getDepartment()));
						depcode = gllist.get(ob[1].toString()).getDepartment();
					}
				}
				if(dept!=null)
				if (dept[0] != null && !dept[0].isEmpty()) {
					if (!dept[0].equals(depcode)) {
						//receiptList.add(receiptHeader);
						continue;
					}
				}
				if(receiptMap.get(receiptHeader.getReceiptdatenew()) == null ){
					receiptList=new ArrayList<>();
						receiptList.add(receiptHeader);
					receiptMap.put(receiptHeader.getReceiptdatenew(),receiptList);
				}
				else{
					receiptMap.get(receiptHeader.getReceiptdatenew()).add(receiptHeader);//map.get().add();
				}
			}
		}
		if (remittlist.size() != 0) {
			for (final Object[] ob : remittlist) {
				String depcode = "";
				ReceiptHeader receiptHeader = new ReceiptHeader();
				try {
					receiptHeader.setReferencenumber(ob[0]!=null ? ob[0].toString(): "");
					receiptHeader.setDepositDate(ob[1]!=null ? ob[1].toString(): "");
					receiptHeader.setBankAccountNumber(ob[2]!=null ? ob[2].toString(): "");
					receiptHeader.setDepositAmount(ob[3]!=null ? ob[3].toString(): "");
					receiptHeader.setReceiptnumber(ob[4]!=null ? ob[4].toString(): "");
					receiptHeader.setDepartment(ob[5]!=null ? dplist.get(ob[5].toString()): "");
					if (dep != null && !dep.isEmpty()) {
						if (!dep.equals(receiptHeader.getDepartment())) {
							continue;//receiptList.add(receiptHeader);
						}
					}
					if(remittMap.get(receiptHeader.getDepositDate()) == null ){
						receiptList=new ArrayList<>();
					receiptList.add(receiptHeader);
						remittMap.put(receiptHeader.getDepositDate(),receiptList);//map.put
					}
					else{
						remittMap.get(receiptHeader.getDepositDate()).add(receiptHeader);//map.get().add();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				}
			}
		List<ReceiptHeader> finalList = new ArrayList<>();
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = getFromDate();// (Date)formatter.parse(str_date);
			endDate = getToDate();// (Date)formatter.parse(end_date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
		long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		for (Date d:dates) {
			Date lDate = (Date) d;
			String ds = formatter.format(lDate);
			System.out.println(" Date is ..." + ds);
			if(receiptMap.get(ds)!=null)
				finalList.addAll(receiptMap.get(ds));
			if(remittMap.get(ds)!=null)
			finalList.addAll(remittMap.get(ds));
		}

		System.out.println("finalList.size() ::::" + finalList.size());
		/// Bhushan
		List<ReceiptHeader> receiptListfilterList = finalList;
		System.out.println("receiptListfilterList.size() ::::" + receiptListfilterList.size());
		List<ReceiptHeader> receiptListnew = new ArrayList<>();
		// receiptList.clear();

		List<ReceiptReportBean> receiptReportList = new ArrayList<ReceiptReportBean>();
		ReceiptReportBean bean = null;
		int i = 1;
		try {
		if (finalList != null && !finalList.isEmpty()) {
			System.out.println("receipt non empty");
			for (ReceiptHeader header : finalList) {
				bean = new ReceiptReportBean();
				bean.setSlNo(String.valueOf(i++));
				// bean.setParamDate(getDateString(header.getReceiptdate()));
				bean.setParamDate(header.getReceiptdatenew()!=null?header.getReceiptdatenew():"");
				bean.setReceiptNo(header.getReceiptnumber()!=null?header.getReceiptnumber():"");
				bean.setCollectedBy(header.getCreatedUser()!=null?header.getCreatedUser():"");
				bean.setPayeeName(header.getPaidBy()!=null?header.getPaidBy().split("&")[0]:"");
				bean.setServiceType(header.getService()!=null?header.getService():"");
				//bean.setServiceCategory(header.getServiceType()!=null?header.getServiceType():"");
				//bean.setChequeddno(header.getChequeddno()!=null?header.getChequeddno():"");
				//bean.setChequedddate(header.getChequedddate()!=null?header.getChequedddate():"");
				//bean.setBank(header.getBank()!=null?header.getBank():"");
				bean.setModeOfPayment(header.getModOfPayment()!=null?header.getModOfPayment():"");
				bean.setParticulars(header.getReferenceDesc()!=null?header.getReferenceDesc():"");
				bean.setTotalReceiptAmount(header.getTotalAmount());
				bean.setGstNo(header.getGstno()!=null?header.getGstno():"");
				bean.setStatus(header.getCurretnStatus()!=null?header.getCurretnStatus():"");
				bean.setDepartment(header.getDepartment()!=null?header.getDepartment():"");
				if (header.getPrincipalAmount() != null) {
					bean.setPrincipalAmt(header.getPrincipalAmount());
				} else {
					bean.setPrincipalAmt(new BigDecimal("0"));
				}
				if (header.getGstAmount() != null) {
					bean.setGstAmount(header.getGstAmount());
				} else {
					bean.setPrincipalAmt(new BigDecimal("0"));
				}
				if (header.getDepositDate() != null) {
					bean.setDateOfDeposite(header.getDepositDate());
				} else {
					bean.setDateOfDeposite("");
				}
				if (header.getReferencenumber() != null) {
					bean.setRemitanceNo(header.getReferencenumber());
				} else {
					bean.setRemitanceNo("");
				}
				if (header.getBankAccountNumber() != null) {
					bean.setBankAccountNo(header.getBankAccountNumber());
				} else {
					bean.setBankAccountNo("");
				}

				if (header.getDepositAmount() != null) {
					bean.setDepositAmount(new BigDecimal(header.getDepositAmount()));
				} else {
					bean.setDepositAmount(new BigDecimal("0"));
				}
				receiptReportList.add(bean);
			}

		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("report size ::" + receiptReportList.size());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String jasperName = "CollectionReport";
		paramMap.put("CollectionReportDataSource", getDataSource(receiptReportList));
		paramMap.put("HeaderParameter", "Detail of Collection Report between " + getDateString(getFromDate()) + " and "
				+ getDateString(getToDate()));
		System.out.println("Start of report");
		try {
			byte[] fileContent = populateExcel(receiptReportList, paramMap);
			inputStream = new ByteArrayInputStream(fileContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("END");
		return "XLS";
	}

	private List<RemittanceDepositWorkDetail> getDepositWork(List<Receipt> receipts) {

		Set<String> reciptNumber = new HashSet<>();

		for (Receipt receipt : receipts) {

			for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

				for (BillDetail billDetail : bill.getBillDetails()) {

					reciptNumber.add(billDetail.getReceiptNumber());

				}

			}
		}

		RemittanceResponseDepositWorkDetails remittanceResponse = microserviceUtils.getDayWorkHistory(reciptNumber);
		return remittanceResponse.getRemittanceDepositWorkDetail();
	}

	private List<RemittanceDepositWorkDetail> getDepositWorknew(List<String> receipts) {

		Set<String> reciptNumber = new HashSet<>();

		for (String receipt : receipts) {

			reciptNumber.add(receipt);
		}

		RemittanceResponseDepositWorkDetails remittanceResponse = microserviceUtils.getDayWorkHistory(reciptNumber);
		return remittanceResponse.getRemittanceDepositWorkDetail();
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public Map<String, String> getServiceCategoryNames() {
		return serviceCategoryNames;
	}

	public void setServiceCategoryNames(Map<String, String> serviceCategoryNames) {
		this.serviceCategoryNames = serviceCategoryNames;
	}

	public Map<String, Map<String, String>> getServiceTypeMap() {
		return serviceTypeMap;
	}

	public void setServiceTypeMap(Map<String, Map<String, String>> serviceTypeMap) {
		this.serviceTypeMap = serviceTypeMap;
	}

	private String getDateQuery(final Date dateFrom, final Date dateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != dateFrom)
				numDateQuery.append(" date(mrd.receipt_date) >='").append(DDMMYYYYFORMAT1.format(dateFrom)).append("'");
			if (null != dateTo)
				numDateQuery.append(" and date(mrd.receipt_date)  <='").append(DDMMYYYYFORMAT1.format(dateTo))
						.append("'");

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}

	private String getDateQuery1(final Date dateFrom, final Date dateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != dateFrom)
				numDateQuery.append(" date(mrd.voucher_date) >='").append(DDMMYYYYFORMAT1.format(dateFrom)).append("'");
			if (null != dateTo)
				numDateQuery.append(" and date(mrd.voucher_date)  <='").append(DDMMYYYYFORMAT1.format(dateTo))
						.append("'");

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}

	@Override
	public SearchQuery prepareQuery(final String sortField, final String sortDir) {
		final ArrayList<Object> params = new ArrayList<Object>(0);
		final StringBuilder searchQueryString = new StringBuilder("select distinct receipt ");
		final StringBuilder countQueryString = new StringBuilder("select count(distinct receipt) ");
		final StringBuilder fromString = new StringBuilder(" from org.egov.collection.entity.ReceiptHeader receipt ");
		final String orderByString = " group by receipt.receiptdate,receipt.id  order by receipt.receiptdate desc";

		// Get only those receipts whose status is NOT PENDING
		final StringBuilder criteriaString = new StringBuilder(" where receipt.status.code != ? ");
		params.add(CollectionConstants.RECEIPT_STATUS_CODE_PENDING);

		if (StringUtils.isNotBlank(getInstrumentType())) {
			fromString.append(" inner join receipt.receiptInstrument as instruments ");
			criteriaString.append(" and instruments.instrumentType.type = ? ");
			params.add(getInstrumentType());
		}

		if (StringUtils.isNotBlank(getReceiptNumber())) {
			criteriaString.append(" and upper(receiptNumber) like ? ");
			params.add("%" + getReceiptNumber().toUpperCase() + "%");
		}
		if (StringUtils.isNotBlank(getManualReceiptNumber())) {
			criteriaString.append(" and upper(receipt.manualreceiptnumber) like ? ");
			params.add("%" + getManualReceiptNumber().toUpperCase() + "%");
		}
		if (getSearchStatus() != -1) {
			criteriaString.append(" and receipt.status.id = ? ");
			params.add(getSearchStatus());
		}
		if (getFromDate() != null) {
			criteriaString.append(" and receipt.receiptdate >= ? ");
			params.add(fromDate);
		}
		/*
		 * if (getToDate() != null) {
		 * criteriaString.append(" and receipt.receiptdate < ? ");
		 * params.add(DateUtils.add(toDate, Calendar.DATE, 1)); }
		 */
		if (getToDate() != null) {
			criteriaString.append(" and receipt.receiptdate < ? ");
			params.add(toDate);
		}
		if (getServiceTypeId() != null) {
			criteriaString.append(" and receipt.service.id = ? ");
			params.add(Long.valueOf(getServiceTypeId()));
		}

		if (!getServiceClass().equals("-1")) {
			criteriaString.append(" and receipt.service.serviceType = ? ");
			params.add(getServiceClass());
		}

		if (getUserId() != -1) {
			criteriaString.append(" and receipt.createdBy.id = ? ");
			params.add(userId);
		}
		if (getBranchId() != -1) {
			criteriaString.append(" and receipt.receiptMisc.depositedBranch.id = ? ");
			params.add(getBranchId());
		}

		final String searchQuery = searchQueryString.append(fromString).append(criteriaString).append(orderByString)
				.toString();
		final String countQuery = countQueryString.append(fromString).append(criteriaString).toString();

		return new SearchQueryHQL(searchQuery, countQuery, params);
	}

	public Integer getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(final Integer searchStatus) {
		this.searchStatus = searchStatus;
	}

	public SearchQuery prepareQuery() {

		return null;
	}

	public String getManualReceiptNumber() {
		return manualReceiptNumber;
	}

	public void setManualReceiptNumber(final String manualReceiptNumber) {
		this.manualReceiptNumber = manualReceiptNumber;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public TreeMap<String, String> getServiceClassMap() {
		return serviceClassMap;
	}

	public void setServiceClassMap(TreeMap<String, String> serviceClassMap) {
		this.serviceClassMap = serviceClassMap;
	}

	/**
	 * @param collectionsUtil the collectionsUtil to set
	 */
	public void setCollectionsUtil(final CollectionsUtil collectionsUtil) {
		this.collectionsUtil = collectionsUtil;
	}

	public Integer getBranchId() {
		return branchId;
	}

	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
	}

	public String getCollectionVersion() {
		return collectionVersion;
	}

	public void setCollectionVersion(String collectionVersion) {
		this.collectionVersion = collectionVersion;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	private static JRBeanCollectionDataSource getDataSource(List<ReceiptReportBean> reportList) {
		return new JRBeanCollectionDataSource(reportList);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getServiceTypeIdforExcel() {
		return serviceTypeIdforExcel;
	}

	public void setServiceTypeIdforExcel(String serviceTypeIdforExcel) {
		this.serviceTypeIdforExcel = serviceTypeIdforExcel;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	private String getDateString(Date date) {
		Date d = new Date();
		String dateString = null;
		SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");

		try {
			dateString = sdfr.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dateString;
	}

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public String getSearchAmount() {
		return searchAmount;
	}

	public void setSearchAmount(String searchAmount) {
		this.searchAmount = searchAmount;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getSubdivison() {
		return subdivison;
	}

	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

}