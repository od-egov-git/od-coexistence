package org.egov.asset.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetHeader;
import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetLocation;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetRevaluation;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetHistoryRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.AssetStatusRepository;
import org.egov.asset.repository.RevaluationRepository;
import org.egov.asset.service.AssetService;
import org.egov.asset.service.RevaluationService;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
//import org.egov.infra.admin.master.repository.DepartmentRepository;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.repository.DepartmentRepository;
import org.egov.infra.config.core.ApplicationThreadLocals;
//import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/revaluate")
public class RevaluationController {
	
	private static final Logger LOGGER = Logger.getLogger(RevaluationController.class);
	@Autowired
	private MicroserviceUtils microserviceUtils;
	@Autowired
	private AssetStatusRepository statusRepo;
	@Autowired
	private AssetCatagoryRepository categoryRepo;
	@Autowired
	private FunctionDAO functionDAO;
	@Autowired
	private AssetMasterRepository masterRepo;
	@Autowired
	private FundHibernateDAO fundHibernateDAO;
	@Autowired
	private RevaluationService revaluationService;
	@Autowired
	private RevaluationRepository revaluationRepository;
	@Autowired
	private AssetHistoryRepository assetHistoryRepository;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private AssetService assetService;
	
	private AssetMaster assetBean;
	private AssetRevaluation assetRevaluation;
	
	List<Department> departmentList = new ArrayList<Department>();
	List<AssetStatus> assetStatusList = new ArrayList<AssetStatus>();
	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();
	List<AssetMaster> assetList = new ArrayList<AssetMaster>();
	List<AssetMaster> assetTempList = new ArrayList<AssetMaster>();
	List<AssetRevaluation> revAssetList = new ArrayList<AssetRevaluation>();
	List<CFunction> functionList = new ArrayList<CFunction>();
	List<Fund> fundList = new ArrayList<Fund>();
	

	@PostMapping("/newform")
	public String viewform(Model model) {
		LOGGER.info("Search revaluate for create");
		
		assetBean = new AssetMaster();
		assetBean.setAssetHeader(new AssetHeader());
		assetBean.setAssetLocation(new AssetLocation());
		model.addAttribute("assetBean", assetBean);
		
		try {
			departmentList = deptRepo.findAll();
			assetStatusList = statusRepo.findByCode("CAPITALIZED");
			assetCategoryList = categoryRepo.findAll();
			
			assetTempList = masterRepo.findAll();
			assetList.clear();
			for(AssetMaster master:assetTempList)
			{
				if(master.getAssetStatus().getCode().equalsIgnoreCase("CAPITALIZED"))
				{
					assetList.add(master);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("assetList", assetList);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("mode", "create");
		model.addAttribute("disabled", "");
		return "asset-search-revaluate";
	}
	
	@PostMapping(value = "/search", params = "search")
	public String search(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
		LOGGER.info("Search Operation..................");
		assetList = new ArrayList<>();
		try {
			assetList = assetService.searchAssets(assetBean);
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("assetList", assetList);
		try {
			departmentList = deptRepo.findAll();
			//departmentList = microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findByCode("CAPITALIZED");
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("assetBean", assetBean);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		
		return "asset-search-revaluate";
	}
	
	@GetMapping("/create/{assetid}")
	public String editform(@PathVariable("assetid") String assetId, Model model, HttpServletRequest request) {
		LOGGER.info("Create Operation.................."+assetId);
		try {
		assetBean = new AssetMaster();
		assetBean = masterRepo.findOne(Long.valueOf(assetId));
		model.addAttribute("assetBean", assetBean);
		assetRevaluation= new AssetRevaluation();
		assetRevaluation.setAssetMaster(assetBean);
		assetRevaluation.setMasterId(assetBean.getId());
		assetRevaluation.setUpdatedCurrentValue(new BigDecimal(assetBean.getGrossValue())); // change to current value
		assetRevaluation.setCurrent_value(new BigDecimal(assetBean.getGrossValue())); // change to current value
		functionList = functionDAO.getAllActiveFunctions();
		fundList = fundHibernateDAO.findAllActiveFunds();
		model.addAttribute("fundList", fundList);
		model.addAttribute("functionList", functionList);
		model.addAttribute("assetRevaluation", assetRevaluation);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return "asset-create-revaluate";
	}
	
	@PostMapping(value = "/create/createReval", params = "create")
	public String create(@ModelAttribute("assetRevaluation") AssetRevaluation assetRevaluation,Model model, HttpServletRequest request) {

		LOGGER.info("Creating Asset Object");
		long userId = ApplicationThreadLocals.getUserId();
		assetBean = new AssetMaster();
		assetBean = masterRepo.findOne(assetRevaluation.getMasterId());
		assetRevaluation.setAssetMaster(assetBean);
		String voucherNumber=revaluationService.createVoucher(assetRevaluation);
		assetRevaluation.setVoucher(voucherNumber);
		applyAuditing(assetRevaluation,userId);
		AssetRevaluation savedAssetRevaluation=revaluationService.create(assetRevaluation);
		assetBean = new AssetMaster();
		assetBean = masterRepo.findOne(savedAssetRevaluation.getAssetMaster().getId());
		assetBean.setCurrentValue(savedAssetRevaluation.getValue_after_revaluation().longValue());
		masterRepo.save(assetBean);
		AssetHistory history=new AssetHistory();
        history.setAsset(assetBean);
        history.setCreatedBy(savedAssetRevaluation.getCreatedBy());
        history.setRecDate(new Date());
        history.setRevId(savedAssetRevaluation.getId());
        history.setTransactionDate(savedAssetRevaluation.getRev_date());
        history.setTransactionType("REVALUATION");
        history.setValueAfterTrxn(savedAssetRevaluation.getValue_after_revaluation());
        history.setTrxnValue(savedAssetRevaluation.getAdd_del_amt());
        history.setValueBeforeTrxn(savedAssetRevaluation.getCurrent_value());
        assetHistoryRepository.save(history);
		String message="Asset has been revaluated with voucher Number "+voucherNumber;
		model.addAttribute("message", message);
		
		
		return "asset-reval-success";
	}

	public AssetRevaluation getAssetRevaluation() {
		return assetRevaluation;
	}

	public void setAssetRevaluation(AssetRevaluation assetRevaluation) {
		this.assetRevaluation = assetRevaluation;
	}
	
	public void applyAuditing(AbstractAuditable auditable, Long createdBy) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(createdBy);
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(createdBy);
		auditable.setLastModifiedDate(currentDate);
	}
	
	@PostMapping("/viewform")
	public String reviewform(Model model) {
		LOGGER.info("Search revaluate for view");
		assetRevaluation = new AssetRevaluation();
		model.addAttribute("assetRevaluation", assetRevaluation);
		try {
			departmentList = deptRepo.findAll();
			//departmentList = microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findByCode("CAPITALIZED");
			assetCategoryList = categoryRepo.findAll();
			
			revAssetList = revaluationRepository.findAll();
			//revAssetList = assetService.searchAssets(assetBean);
			LOGGER.info("Asset Lists..."+revAssetList.toString());
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("revAssetList", revAssetList);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("mode", "view");
		model.addAttribute("disabled", "");
		return "asset-view-revaluate";
	}
	
	@PostMapping(value = "/view", params = "search")
	public String view(@ModelAttribute("assetRevaluation") AssetRevaluation assetBean, Model model, HttpServletRequest request) {
		LOGGER.info("Search Operation..................");
		assetList = new ArrayList<>();
		revAssetList = new ArrayList<>();
		try {
			/*assetList = assetService.searchAssets(assetBean.getAssetMaster());
			LOGGER.info("Asset Lists..."+assetList);
			for(AssetMaster asset : assetList) {
				AssetRevaluation rev = new AssetRevaluation();
				rev.setAssetMaster(asset);
				revAssetList.add(rev);
			}*/
			revAssetList = revaluationService.searchAssets(assetBean);//searchAssets
			LOGGER.info("Asset Rev Lists..."+revAssetList);
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("revAssetList", revAssetList);
		try {
			departmentList = deptRepo.findAll();
			//departmentList = microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findByCode("CAPITALIZED");
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("assetBean", assetBean);
		model.addAttribute("assetRevaluation", assetRevaluation);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("isViewPage", false);
		
		return "asset-view-revaluate";
	}
	
	@GetMapping("/view/{assetid}")
	public String revViewform(@PathVariable("assetid") String assetId, Model model, HttpServletRequest request) {
		LOGGER.info("View Operation.................."+assetId);
		try {
		assetRevaluation= new AssetRevaluation();
		assetRevaluation = revaluationRepository.findOne(Long.valueOf(assetId));
		assetRevaluation.setMasterId(assetRevaluation.getAssetMaster().getId());
		functionList = functionDAO.getAllActiveFunctions();
		fundList = fundHibernateDAO.findAllActiveFunds();
		model.addAttribute("assetRevaluation", assetRevaluation);
		model.addAttribute("fundList", fundList);
		model.addAttribute("functionList", functionList);
		model.addAttribute("assetRevaluation", assetRevaluation);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return "asset-view-revaluate-detail";
	}

}
