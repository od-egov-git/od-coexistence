package org.egov.asset.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetRevaluation;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.AssetStatusRepository;
import org.egov.asset.service.RevaluationService;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
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
	
	private AssetMaster assetBean;
	private AssetRevaluation assetRevaluation;
	
	List<Department> departmentList = new ArrayList<Department>();
	List<AssetStatus> assetStatusList = new ArrayList<AssetStatus>();
	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();
	List<AssetMaster> assetList = new ArrayList<AssetMaster>();
	List<CFunction> functionList = new ArrayList<CFunction>();
	List<Fund> fundList = new ArrayList<Fund>();
	

	@PostMapping("/newform")
	public String viewform(Model model) {
		LOGGER.info("Search revaluate for create");
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		try {
			departmentList = microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findByCode("CAPITALIZED");
			assetCategoryList = categoryRepo.findAll();
			
			assetList = masterRepo.findAll();
			LOGGER.info("Asset Lists..."+assetList.toString());
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
			Long statusId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			assetList = masterRepo.getAssetMasterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					assetBean.getAssetHeader().getDepartment(), statusId);
			LOGGER.info("Asset Lists..."+assetList.toString());
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("assetList", assetList);
		try {
			departmentList = microserviceUtils.getDepartments();
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
	
	@PostMapping(value = "/create", params = "create")
	public String create(@ModelAttribute("assetRevaluation") AssetRevaluation assetRevaluation,Model model, HttpServletRequest request) {

		LOGGER.info("Creating Asset Object");
		long userId = ApplicationThreadLocals.getUserId();
		String voucherNumber=revaluationService.createVoucher(assetRevaluation);
		assetRevaluation.setVoucher(voucherNumber);
		AssetRevaluation savedAssetRevaluation=revaluationService.create(assetRevaluation);
		
		String message="Asset hasa been revaluated with voucher Number";
		model.addAttribute("message", message);
		
		
		return "asset-success";
	}

}
