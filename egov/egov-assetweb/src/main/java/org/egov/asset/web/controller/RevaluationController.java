package org.egov.asset.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.AssetStatusRepository;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	
	private AssetMaster assetBean;
	
	List<Department> departmentList = new ArrayList<Department>();
	List<AssetStatus> assetStatusList = new ArrayList<AssetStatus>();
	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();
	List<AssetMaster> assetList = new ArrayList<AssetMaster>();
	@Autowired
	private AssetMasterRepository masterRepo;

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

}
