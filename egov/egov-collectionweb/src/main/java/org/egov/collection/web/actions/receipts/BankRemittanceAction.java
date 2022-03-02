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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.displaytag.pagination.PaginatedList;
import org.egov.collection.bean.ReceiptBean;
import org.egov.collection.bean.SubDivison;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.CollectionBankRemittanceReport;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.service.RemittanceServiceImpl;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.infra.microservice.models.RemitancePOJO;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFinancialYear;
import org.egov.commons.DocumentUploads;
import org.egov.commons.dao.BankaccountHibernateDAO;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.BankAccountServiceMapping;
import org.egov.infra.microservice.models.BusinessDetails;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.DocumentUpload;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.web.utils.EgovPaginatedList;

@Results({ @Result(name = BankRemittanceAction.NEW, location = "bankRemittance-new.jsp"),
		@Result(name = BankRemittanceAction.PRINT_BANK_CHALLAN, type = "redirectAction", location = "remittanceStatementReport-printCashBankChallan.action", params = {
				"namespace", "/reports", "totalCashAmount", "${totalCashAmount}", "totalChequeAmount",
				"${totalChequeAmount}", "bank", "${bank}", "bankAccount", "${bankAccount}", "remittanceDate",
				"${remittanceDate}" }),
		@Result(name = BankRemittanceAction.INDEX, location = "bankRemittance-index.jsp") })
@ParentPackage("egov")
public class BankRemittanceAction extends BaseFormAction {
	protected static final String PRINT_BANK_CHALLAN = "printBankChallan";
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(BankRemittanceAction.class);
	private static final String BANK_ACCOUNT_NUMBER_QUERY = "select distinct ba.accountnumber from BANKACCOUNT ba where ba.accountnumber =:accountNumberId";
	private static final String SERVICE_FUND_QUERY = new StringBuilder()
			.append("select distinct sd.code as servicecode,fd.code as fundcode from BANKACCOUNT ba,")
			.append("EGCL_BANKACCOUNTSERVICEMAPPING asm,EGCL_SERVICEDETAILS sd,FUND fd where asm.BANKACCOUNT=ba.ID ")
			.append("and asm.servicedetails=sd.ID and fd.ID=ba.FUNDID and ba.id= :accountNumberId").toString();
	private transient List<HashMap<String, Object>> paramList = null;
	private final ReceiptHeader receiptHeaderIntsance = new ReceiptHeader();
	private List<ReceiptHeader> voucherHeaderValues = new ArrayList<>(0);
	private String[] serviceNameArray;
	private String[] totalCashAmountArray;
	private String[] totalChequeAmountArray;
	private String[] totalCardAmountArray;
	private String[] receiptDateArray;
	private String[] receiptNumberArray;
	private String[] fundCodeArray;
	private String[] departmentCodeArray;
	private String[] instrumentIdArray;
	private String accountNumberId;
	private transient CollectionsUtil collectionsUtil;
	private Integer branchId;
	private static final String ACCOUNT_NUMBER_LIST = "accountNumberList";
	private Boolean isListData = false;
	// Added for Manual Work Flow
	private Integer positionUser;
	private Integer designationId;
	private Date remittanceDate;
	private String narration;
	private String deptIdnew;
	private String functionNew;
	private String subdivisonNew;
	
	@Autowired
	private transient FinancialYearDAO financialYearDAO;
	@Autowired
	private transient BankaccountHibernateDAO bankaccountHibernateDAO;

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	protected EgovMasterDataCaching masterDataCache;
	@Autowired
	private AppConfigValueService appConfigValuesService;
	private Double totalCashAmount;
	private Double totalChequeAmount;
	private List<CollectionBankRemittanceReport> bankRemittanceList;
	private String bank;
	private String bankAccount;
	private Boolean showCardAndOnlineColumn = false;
	private Boolean showRemittanceDate = false;
	private Long finYearId;
	private RemittanceServiceImpl remittanceService;
	private String voucherNumber;
	private Date fromDate;
	private Date toDate;
	//private Integer pageSize;
	private String remittanceAmount;
	private static final String REMITTANCE_LIST = "REMITTANCE_LIST";
	private Boolean isBankCollectionRemitter;
	private String remitAccountNumber;
	private List resultList = new ArrayList<>();
	private List resultListNew = new ArrayList<>();
	private List resultListFinal = new ArrayList<>();
	private List<ReceiptBean> finalList = new ArrayList<>();
	private File[] file; // added abhishek
	private String[] fileContentType; // added abhishek
	private String[] fileFileName; // added abhishek
	private List<DocumentUpload> documentDetail = new ArrayList<>(); // added abhishek
	private String deptId = "-1";
	private String collectedBy = "";
	private String modeOfPayment = "";
	private String searchAmount;
	private String subdivison = "-1";
	private String receiptNo;
	private List<RemitancePOJO> remittance = new ArrayList<>();
	private int pageNum = 1;
	private int pageSize = 20;
	protected PaginatedList searchResult;
	
	public void setPage(final int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * @return the current page number
	 */
	public int getPage() {
		return this.pageNum;
	}
	
	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getSubdivison() {
		return subdivison;
	}

	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getSearchAmount() {
		return searchAmount;
	}

	public void setSearchAmount(String searchAmount) {
		this.searchAmount = searchAmount;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

	public File[] getFile() {
		return file;
	}

	public void setFile(File[] file) {
		this.file = file;
	}

	public String[] getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String[] fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String[] getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String[] fileFileName) {
		this.fileFileName = fileFileName;
	}

	Map<String, String> serviceCategoryNames = new HashMap<String, String>();
	Map<String, Map<String, String>> serviceTypeMap = new HashMap<>();
	private String serviceTypeId = null;

	/**
	 * @param collectionsUtil the collectionsUtil to set
	 */
	public void setCollectionsUtil(final CollectionsUtil collectionsUtil) {
		this.collectionsUtil = collectionsUtil;
	}

	@Override
	public void prepare() {
		super.prepare();

		this.getServiceCategoryList();
		setupDropdownDataExcluding();
		final String showColumn = collectionsUtil.getAppConfigValue(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
				CollectionConstants.APPCONFIG_VALUE_COLLECTION_BANKREMITTANCE_SHOWCOLUMNSCARDONLINE);
		if (!showColumn.isEmpty() && showColumn.equals(CollectionConstants.YES))
			showCardAndOnlineColumn = true;
		final String showRemitDate = collectionsUtil.getAppConfigValue(
				CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
				CollectionConstants.APPCONFIG_VALUE_COLLECTION_BANKREMITTANCE_SHOWREMITDATE);
		if (!showRemitDate.isEmpty() && showRemitDate.equals(CollectionConstants.YES))
			showRemittanceDate = true;

		isBankCollectionRemitter = collectionsUtil.isBankCollectionOperator(collectionsUtil.getLoggedInUser());
		// addDropdownData("bankBranchList", Collections.emptyList());
		// addDropdownData(ACCOUNT_NUMBER_LIST, Collections.emptyList());
		Map<String, BankAccountServiceMapping> accountNumberMap = new HashMap<>();
		for (BankAccountServiceMapping basm : microserviceUtils.getBankAcntServiceMappings()) {
			accountNumberMap.put(basm.getBankAccount(), basm);
		}
		List bank=remittanceService.getallBank();
		addDropdownData("accountNumberList", new ArrayList<>(accountNumberMap.values()));
		addDropdownData("serviceTypeList", microserviceUtils.getBusinessService(null));
		addDropdownData("departmentList", masterDataCache.get("egi-department"));
		addDropdownData("functionList", masterDataCache.get("egi-function"));
		addDropdownData("bankaccountNumberList", remittanceService.getallBank());
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

	@Action(value = "/receipts/bankRemittance-newform")
	@SkipValidation
	public String newform() {
		populateRemittanceList();
		return NEW;
	}

	private void populateRemittanceList() {
		this.getServiceCategoryList();
		setupDropdownDataExcluding();

		Map<String, BankAccountServiceMapping> accountNumberMap = new HashMap<>();
		for (BankAccountServiceMapping basm : microserviceUtils.getBankAcntServiceMappings()) {
			accountNumberMap.put(basm.getBankAccount(), basm);
		}
		addDropdownData("accountNumberList", new ArrayList<>(accountNumberMap.values()));

		addDropdownData("serviceTypeList", microserviceUtils.getBusinessService(null));
		addDropdownData("departmentList", masterDataCache.get("egi-department"));

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

	@Action(value = "/receipts/bankRemittance-listData")
	@SkipValidation
	public String listData() {
		String serviceTId = getServiceTypeId();
		System.out.println("LIST DATA ACTION >>>>" + serviceTId);

		isListData = true;
		
		populateRemittanceList();

		if (fromDate != null && toDate != null && toDate.before(fromDate))
			addActionError(getText("bankremittance.before.fromdate"));
		
		
			if (getServiceTypeId().equalsIgnoreCase("") || getServiceTypeId().equalsIgnoreCase("-1")
					|| getServiceTypeId().equalsIgnoreCase(" ")) {
				setServiceTypeId(null);
			}

			if (deptId == null || deptId.equals("")) {
				deptId = "-1";
				setDeptId("-1");
			}
			/*
			 * resultList =
			 * remittanceService.findCashRemittanceDetailsForServiceAndFund("MISCELLANEOUS",
			 * fromDate, toDate, getServiceTypeId(), receiptNo, deptId, "search",
			 * searchAmount, subdivison, collectedBy);
			 */
			try {
			resultList = remittanceService.findCashRemittanceDetailsForServiceAndFundNew("MISCELLANEOUS", fromDate, toDate,
					getServiceTypeId(), receiptNo, deptId, "search", searchAmount, subdivison, collectedBy,"Cash");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Result LIST>>>" + resultList);

			if(getPage()>=1) {
				int showlist=490*getPage();
				int fromIndex=0;
				int toIndex=0;
				if(showlist>resultList.size()) {
					fromIndex=(490*(getPage()-1));
					toIndex=resultList.size();
				}else {
					if(resultList.size()<490) {
						fromIndex=0;
						toIndex=resultList.size();
					}else {
					fromIndex=showlist-490;
					toIndex=showlist;
					}
				}
				System.out.println("from::: "+fromIndex+"to:::: "+toIndex);
				resultListFinal=resultList.subList(fromIndex, toIndex);
			}
			if (searchResult == null) {
				Page page = new Page<List>(getPage(), 490, resultListFinal);
				searchResult = new EgovPaginatedList(page, resultList.size());
			} else {
				searchResult.getList().clear();
				searchResult.getList().addAll(resultList);
			}

			resultListNew = searchResult.getList();
		
		return NEW;
	}

	@Action(value = "/receipts/bankRemittance-printBankChallan")
	@SkipValidation
	public String printBankChallan() {
		return PRINT_BANK_CHALLAN;
	}

	public String edit() {
		return EDIT;
	}

	public String save() {
		return SUCCESS;
	}

	@ValidationErrorPage(value = "error")
	@Action(value = "/receipts/bankRemittance-create")
	public String create() {
		List<ReceiptBean> eblist=new ArrayList<ReceiptBean>();
		String receiptNumbers="";
		for(ReceiptBean f: finalList)
		{
			if(f.getSelected()!=null)
			{
				eblist.add(f);
				
				if(receiptNumbers.equalsIgnoreCase(""))
					receiptNumbers=f.getReceiptNumber();
				else
					receiptNumbers+=","+f.getReceiptNumber();
			}
		}
		System.out.println("receiptnumbers ---->>> "+receiptNumbers);
		System.out.println("finalList>>>>>>>" + finalList + ".............." + accountNumberId + ".............."
				+ remittanceDate);
		final long startTimeMillis = System.currentTimeMillis();
		List<RemitancePOJO> re = getRemittance();
		System.out.println("::::::re Size::: " + re.size());
		ReceiptBean receipts =null;
		
		receipts=remittanceService.createCashBankRemittance(eblist.get(0), re, remittanceDate,narration,deptIdnew,functionNew,subdivisonNew,receiptNumbers);
		if(receipts!=null)
		{
			try 
			{
				for(ReceiptBean f: finalList)
				{
					if(f.getSelected()!=null)
					{
						eblist.add(f);
						persistenceService.getSession()
		                .createSQLQuery(
		                        "update mis_receipts_details set payment_status = 'DEPOSITED' where receipt_number ='"+f.getReceiptNumber()+"'")
		                .executeUpdate();
					}
				}
			}
            catch(Exception e)
            {
            	e.printStackTrace();
            }
		}
		final long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
		LOGGER.info("$$$$$$ Time taken to persist the remittance list (ms) = " + elapsedTimeMillis);
		if(receipts!=null)
			bankRemittanceList = remittanceService.prepareCashRemittanceReportNew(receipts);
		
		
		
		  // added by abhishek on 24032021 
		File[] uploadedFiles = getFile(); 
		String[] fileName = getFileFileName(); 
		String[] contentType = getFileContentType();
		if(uploadedFiles!=null)
		{
			System.out.println("files "+uploadedFiles[0]);
			if(uploadedFiles[0]!=null) 
			{ 
				byte[] fileBytes; 
				for (int i = 0; i<uploadedFiles.length; i++) 
				{ 
					Path path =Paths.get(uploadedFiles[i].getAbsolutePath()); 
					try 
					{ 
						fileBytes = Files.readAllBytes(path); 
						ByteArrayInputStream bios= new
						ByteArrayInputStream(fileBytes); 
						DocumentUpload upload = new DocumentUpload(); 
						upload.setInputStream(bios);
						upload.setFileName(fileName[i]); 
						upload.setContentType(contentType[i]);
						documentDetail.add(upload); 
					} 
					catch (IOException e) 
					{ // TODO Auto-generated
						e.printStackTrace(); 
					}
				} 
				receiptHeaderIntsance.setId(receipts.getVoucherid());
				receiptHeaderIntsance.setDocumentDetail(documentDetail);
				remittanceService.saveDocuments(receiptHeaderIntsance); 
			}
		} 
		// end
		return INDEX;
	}

	/*
	 * @ValidationErrorPage(value = "error")
	 * 
	 * @Action(value = "/receipts/bankRemittance-create") public String create_old()
	 * { System.out.println("finalList>>>>>>>"+finalList+".............."+
	 * accountNumberId+".............."+ remittanceDate); final long startTimeMillis
	 * = System.currentTimeMillis();
	 * 
	 * if (accountNumberId == null || accountNumberId.isEmpty() ||
	 * accountNumberId.equalsIgnoreCase("-1")) throw new
	 * ValidationException(Arrays.asList(new
	 * ValidationError("Please select Account number",
	 * "bankremittance.error.noaccountNumberselected")));
	 * 
	 * // voucherHeaderValues = List<RemitancePOJO> re = getRemittance();
	 * System.out.println("::::::re Size::: "+re.size()); for(RemitancePOJO r:re) {
	 * System.out.println("::GL Name::: "+r.getGlName()+":::Gl :: "+r.getGlcode()
	 * +"::Amount:: "+r.getAmount()+"::::BANK:: "+r.getBankaccount()); }
	 * 
	 * List<Receipt> receipts =
	 * remittanceService.createCashBankRemittance(finalList, accountNumberId,
	 * remittanceDate); final long elapsedTimeMillis = System.currentTimeMillis() -
	 * startTimeMillis;
	 * LOGGER.info("$$$$$$ Time taken to persist the remittance list (ms) = " +
	 * elapsedTimeMillis); bankRemittanceList =
	 * remittanceService.prepareCashRemittanceReport(receipts); if
	 * (getSession().get(REMITTANCE_LIST) != null)
	 * getSession().remove(REMITTANCE_LIST); getSession().put(REMITTANCE_LIST,
	 * bankRemittanceList); populateNames(bankRemittanceList); final Bankaccount
	 * bankAcc = bankaccountHibernateDAO.getByAccountNumber(accountNumberId);
	 * bankAccount = bankAcc.getAccountnumber(); bank =
	 * bankAcc.getBankbranch().getBank().getName(); totalCashAmount =
	 * getSum(finalList); //added by abhishek on 24032021 File[] uploadedFiles =
	 * getFile(); String[] fileName = getFileFileName(); String[] contentType =
	 * getFileContentType();
	 * 
	 * if(uploadedFiles!=null) { //byte[] fileBytes; for (int i = 0; i <
	 * uploadedFiles.length; i++) {
	 * 
	 * Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
	 * 
	 * try { byte[] fileBytes = Files.readAllBytes(path); ByteArrayInputStream bios
	 * = new ByteArrayInputStream(fileBytes); DocumentUploads upload = new
	 * DocumentUploads(); upload.setInputStream(bios);
	 * upload.setFileName(fileName[i]); upload.setContentType(contentType[i]);
	 * documentDetail.add(upload); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 * receiptHeaderIntsance.setDocumentDetail(documentDetail); } } //end return
	 * INDEX; }
	 */

	private void populateNames(List<CollectionBankRemittanceReport> bankRemittanceList) {
		List<BusinessService> businessServList = microserviceUtils.getBusinessService(null);
		Map<String, String> businessDetailsCodeNameMap = new HashMap<>();

		if (businessServList != null)
			for (BusinessService bd : businessServList) {
				businessDetailsCodeNameMap.put(bd.getCode(), bd.getBusinessService());
			}

		for (CollectionBankRemittanceReport rb : bankRemittanceList) {
			if (rb.getServiceType() != null && !rb.getServiceType().isEmpty())
				rb.setServiceName(businessDetailsCodeNameMap.get(rb.getServiceType()));
		}
	}

	private Double getSum(List<ReceiptBean> finalList) {
		Double sum = 0.0;
		for (final ReceiptBean r : finalList)
			if (r.getSelected() != null && r.getSelected() && r.getInstrumentAmount() != null)
				sum = sum + r.getInstrumentAmount().doubleValue();
		return sum;
	}

	private Double getSum(final String[] array) {
		Double sum = 0.0;
		for (final String num : array)
			if (!num.isEmpty())
				sum = sum + Double.valueOf(num);
		return sum;
	}

	@Override
	public void validate() {
		super.validate();
		populateRemittanceList();
		listData();
		final SimpleDateFormat dateFomatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		if (receiptDateArray != null) {
			final String[] filterReceiptDateArray = removeNullValue(receiptDateArray);
			final String receiptEndDate = filterReceiptDateArray[filterReceiptDateArray.length - 1];
			try {
				if (!receiptEndDate.isEmpty() && remittanceDate != null
						&& remittanceDate.before(dateFomatter.parse(receiptEndDate)))
					addActionError(getText("bankremittance.before.receiptdate"));
			} catch (final ParseException e) {
				LOGGER.debug("Exception in parsing date  " + receiptEndDate + " - " + e.getMessage());
				throw new ApplicationRuntimeException("Exception while parsing receiptEndDate date", e);
			}
		}
	}

	private String[] removeNullValue(String[] receiptDateArray) {
		final List<String> list = new ArrayList<>();
		for (final String s : receiptDateArray)
			if (s != null && s.length() > 0)
				list.add(s);
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Object getModel() {
		return receiptHeaderIntsance;
	}

	/**
	 * @return the paramList
	 */
	public List<HashMap<String, Object>> getParamList() {
		return paramList;
	}

	/**
	 * @param paramList the paramList to set
	 */
	public void setParamList(final List<HashMap<String, Object>> paramList) {
		this.paramList = paramList;
	}

	/**
	 * @return the serviceName
	 */
	public String[] getServiceNameArray() {
		return serviceNameArray;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceNameArray(final String[] serviceNameArray) {
		this.serviceNameArray = serviceNameArray;
	}

	/**
	 * @return the totalCashAmount
	 */
	public String[] getTotalCashAmountArray() {
		return totalCashAmountArray;
	}

	/**
	 * @param totalCashAmount the totalCashAmount to set
	 */
	public void setTotalCashAmountArray(final String[] totalCashAmountArray) {
		this.totalCashAmountArray = totalCashAmountArray;
	}

	/**
	 * @return the totalChequeAmount
	 */
	public String[] getTotalChequeAmountArray() {
		return totalChequeAmountArray;
	}

	/**
	 * @param totalChequeAmount the totalChequeAmount to set
	 */
	public void setTotalChequeAmountArray(final String[] totalChequeAmountArray) {
		this.totalChequeAmountArray = totalChequeAmountArray;
	}

	/**
	 * @return the receiptDate
	 */
	public String[] getReceiptDateArray() {
		return receiptDateArray;
	}

	/**
	 * @param receiptDate the receiptDate to set
	 */
	public void setReceiptDateArray(final String[] receiptDateArray) {
		this.receiptDateArray = receiptDateArray;
	}

	/**
	 * @return the voucherHeaderValues
	 */
	public List<ReceiptHeader> getVoucherHeaderValues() {
		return voucherHeaderValues;
	}

	/**
	 * @param voucherHeaderValues the voucherHeaderValues to set
	 */
	public void setVoucherHeaderValues(final List<ReceiptHeader> voucherHeaderValues) {
		this.voucherHeaderValues = voucherHeaderValues;
	}

	/**
	 * @return the fundCodeArray
	 */
	public String[] getFundCodeArray() {
		return fundCodeArray;
	}

	/**
	 * @param fundCodeArray the fundCodeArray to set
	 */
	public void setFundCodeArray(final String[] fundCodeArray) {
		this.fundCodeArray = fundCodeArray;
	}

	/**
	 * @return the departmentCodeArray
	 */
	public String[] getDepartmentCodeArray() {
		return departmentCodeArray;
	}

	/**
	 * @param departmentCodeArray the departmentCodeArray to set
	 */
	public void setDepartmentCodeArray(final String[] departmentCodeArray) {
		this.departmentCodeArray = departmentCodeArray;
	}

	/**
	 * @return the totalCardAmountArray
	 */
	public String[] getTotalCardAmountArray() {
		return totalCardAmountArray;
	}

	/**
	 * @param totalCardAmountArray the totalCardAmountArray to set
	 */
	public void setTotalCardAmountArray(final String[] totalCardAmountArray) {
		this.totalCardAmountArray = totalCardAmountArray;
	}

	/**
	 * @return the positionUser
	 */
	public Integer getPositionUser() {
		return positionUser;
	}

	/**
	 * @param positionUser the positionUser to set
	 */
	public void setPositionUser(final Integer positionUser) {
		this.positionUser = positionUser;
	}

	/**
	 * @return the designationId
	 */
	public Integer getDesignationId() {
		return designationId;
	}

	/**
	 * @param designationId the designationId to set
	 */
	public void setDesignationId(final Integer designationId) {
		this.designationId = designationId;
	}

	public String[] getReceiptNumberArray() {
		return receiptNumberArray;
	}

	public void setReceiptNumberArray(final String[] receiptNumberArray) {
		this.receiptNumberArray = receiptNumberArray;
	}

	public Integer getBranchId() {
		return branchId;
	}

	public void setBranchId(final Integer branchId) {
		this.branchId = branchId;
	}

	public String getAccountNumberId() {
		return accountNumberId;
	}

	public void setAccountNumberId(final String accountNumberId) {
		this.accountNumberId = accountNumberId;
	}

	public Boolean getIsListData() {
		return isListData;
	}

	public void setIsListData(final Boolean isListData) {
		this.isListData = isListData;
	}

	public Double getTotalCashAmount() {
		return totalCashAmount;
	}

	public void setTotalCashAmount(final Double totalCashAmount) {
		this.totalCashAmount = totalCashAmount;
	}

	public Double getTotalChequeAmount() {
		return totalChequeAmount;
	}

	public void setTotalChequeAmount(final Double totalChequeAmount) {
		this.totalChequeAmount = totalChequeAmount;
	}

	public List<CollectionBankRemittanceReport> getBankRemittanceList() {
		return bankRemittanceList;
	}

	public void setBankRemittanceList(final List<CollectionBankRemittanceReport> bankRemittanceList) {
		this.bankRemittanceList = bankRemittanceList;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(final String bank) {
		this.bank = bank;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(final String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Boolean getShowCardAndOnlineColumn() {
		return showCardAndOnlineColumn;
	}

	public void setShowCardAndOnlineColumn(final Boolean showCardAndOnlineColumn) {
		this.showCardAndOnlineColumn = showCardAndOnlineColumn;
	}

	public Boolean getShowRemittanceDate() {
		return showRemittanceDate;
	}

	public void setShowRemittanceDate(final Boolean showRemittanceDate) {
		this.showRemittanceDate = showRemittanceDate;
	}

	public Date getRemittanceDate() {
		return remittanceDate;
	}

	public void setRemittanceDate(final Date remittanceDate) {
		this.remittanceDate = remittanceDate;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public Long getFinYearId() {
		return finYearId;
	}

	public void setFinYearId(final Long finYearId) {
		this.finYearId = finYearId;
	}

	public void setRemittanceService(final RemittanceServiceImpl remittanceService) {
		this.remittanceService = remittanceService;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(final String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getRemittanceAmount() {
		return remittanceAmount;
	}

	public void setRemittanceAmount(String remittanceAmount) {
		this.remittanceAmount = remittanceAmount;
	}

	public Boolean getIsBankCollectionRemitter() {
		return isBankCollectionRemitter;
	}

	public void setIsBankCollectionRemitter(Boolean isBankCollectionRemitter) {
		this.isBankCollectionRemitter = isBankCollectionRemitter;
	}

	public String getRemitAccountNumber() {
		return remitAccountNumber;
	}

	public void setRemitAccountNumber(String remitAccountNumber) {
		this.remitAccountNumber = remitAccountNumber;
	}

	public String[] getInstrumentIdArray() {
		return instrumentIdArray;
	}

	public void setInstrumentIdArray(String[] instrumentIdArray) {
		this.instrumentIdArray = instrumentIdArray;
	}

	public List<ReceiptBean> getResultList() {
		return resultList;
	}

	public void setResultList(List<ReceiptBean> resultList) {
		this.resultList = resultList;
	}

	public List<ReceiptBean> getFinalList() {
		return finalList;
	}

	public void setFinalList(List<ReceiptBean> finalList) {
		this.finalList = finalList;
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

	public String getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public List<RemitancePOJO> getRemittance() {
		return remittance;
	}

	public void setRemittance(List<RemitancePOJO> remittance) {
		this.remittance = remittance;
	}

	public String getDeptIdnew() {
		return deptIdnew;
	}

	public void setDeptIdnew(String deptIdnew) {
		this.deptIdnew = deptIdnew;
	}

	public String getFunctionNew() {
		return functionNew;
	}

	public void setFunctionNew(String functionNew) {
		this.functionNew = functionNew;
	}

	public String getSubdivisonNew() {
		return subdivisonNew;
	}

	public void setSubdivisonNew(String subdivisonNew) {
		this.subdivisonNew = subdivisonNew;
	}

	

}