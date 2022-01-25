package org.egov.asset.web.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;
import org.egov.egf.contract.model.AccumulatedDepriciationCode;
import org.egov.egf.contract.model.AssetAccountCode;
import org.egov.egf.contract.model.AssetCatagory;
import org.egov.egf.contract.model.AssetCatagoryType;
import org.egov.egf.contract.model.CustomFieldDataType;
import org.egov.egf.contract.model.CustomeFields;
import org.egov.egf.contract.model.DepriciationExpenseAccount;
import org.egov.egf.contract.model.DepriciationMethod;
import org.egov.egf.contract.model.ParentCatagory;
import org.egov.egf.contract.model.RevolutionReserveAccountCode;
import org.egov.egf.contract.model.UnitOfMeasurement;
import org.egov.egf.expensebill.repository.AssetCatagoryTypeRepository;
import org.egov.egf.expensebill.service.AssetCatagoryService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asset")
public class AssetCatagoryController {

	private static final Logger LOGGER = Logger.getLogger(AssetCatagoryController.class);
	@Autowired
	private AssetCatagoryService assetCatagoryService;
	@Autowired
	private AssetCatagoryTypeRepository repo;

	
	@ModelAttribute
	public void addDropDownValuesToModel(Model model) {
		model.addAttribute("accumulatedDepriciationCode", assetCatagoryService.getAccumulatedDepriciationCode());
		model.addAttribute("assetAccountCode", assetCatagoryService.getAssetAccountCode());
		model.addAttribute("depriciationExpenseAccount", assetCatagoryService.getDepriciationExpenseAccount());
		model.addAttribute("depriciationMethod", assetCatagoryService.getDepriciationMethod());
		model.addAttribute("parentCatagory", assetCatagoryService.getParentCatagory());
		model.addAttribute("revolutionReserveAccountCode", assetCatagoryService.getRevolutionReserveAccountCode());
		model.addAttribute("unitOfMeasurement", assetCatagoryService.getUnitOfMeasurement());
		model.addAttribute("customFieldDataType", assetCatagoryService.getCustomFieldDataType());
		model.addAttribute("assetCatagoryTypes", assetCatagoryService.getAssetCatagoryType());
	}
	@GetMapping("/createAssetCatagory")
	public String loadCreateAssetCatagoryForm(Model model) {
		model.addAttribute("assetCatagory", new AssetCatagory());
		return "assetcatagory-form";
	}

	@PostMapping(value = "/createAssetCatagory", params = "add")
	public String createCustomField(@ModelAttribute("assetCatagory") AssetCatagory catagory, Model model,
			HttpServletRequest request) {

		List<CustomeFields> customeFields = catagory.getCustomeFields();

		System.out.println("add method customeFields list request: " + customeFields);
		CustomeFields customeField = catagory.getCustomeField();
		String dataType=customeField.getCustomFieldDataType().getDataTypes();
		customeField.setDataType(dataType);
		System.out.println(" add method custome field added data: " + customeField);
		customeFields.add(customeField);

		System.out.println("After Adding customefield data to list: " + customeFields);

		AssetCatagory assetCatagory = catagory;
		assetCatagory.setCustomeFields(customeFields);
		assetCatagory.setCustomeField(new CustomeFields());

		model.addAttribute("assetCatagory", assetCatagory);

		System.out.println("calling oadCreateAssetCatagoryForm()");
		System.out.println("add method Request " + catagory);
		System.out.println("add method Response " + assetCatagory);
		return "assetcatagory-form";
	}

	@PostMapping(value = "/createAssetCatagory", params = "create")
	public String createAssetCatagory(@ModelAttribute("assetCatagory") AssetCatagory assetCatagory, Model model,
			HttpServletRequest request) {

		LOGGER.info("crate method assetCatagory request " + assetCatagory);

		
		Long userid = ApplicationThreadLocals.getUserId();
		assetCatagory.setUserid(String.valueOf(userid));

		AssetCatagory createAssetCatagory = assetCatagoryService.createAssetCatagory(assetCatagory);
		if (null != createAssetCatagory.getErrorMessage()) {
			model.addAttribute("assetCatagory", assetCatagory);
			model.addAttribute("errorMessage", createAssetCatagory.getErrorMessage());
			return "assetcatagory-form";
		}
		if (createAssetCatagory != null) {
			model.addAttribute("assetCatagory", new AssetCatagory());
			model.addAttribute("successMsg", createAssetCatagory.getName() + " created successfully..!!");
		}

		return "assetcatagory-form";
	}

	@GetMapping("/searchAssetCatagory")
	public String loadSearchAssetCatagoryForm(Model model) {
		
		List<AssetCatagory> assetCategories=new ArrayList<AssetCatagory>();
		model.addAttribute("assetCategories", assetCategories);
		model.addAttribute("assetCatagory", new AssetCatagory());
		model.addAttribute("assetCatagoryTypes", assetCatagoryService.getAssetCatagoryType());
		return "search-assetcatagory-form";
	}
	@PostMapping("/searchAssetCatagory")
	public String loadSearchAssetCatagoryForm(@ModelAttribute AssetCatagory assetCategory,Model model) {
		String name="";
		Long id=null;
		if(null!=assetCategory.getName()) {
			name=assetCategory.getName();
		}
		if(null!=assetCategory.getAssetCatagoryType().getId()) {
			id=assetCategory.getAssetCatagoryType().getId();
		}
		List<AssetCatagory> assetCategories=assetCatagoryService.findBynameorAssetCataType(name, id);
		model.addAttribute("assetCategories", assetCategories);
		return "search-assetcatagory-form";
	}
	@GetMapping("/viewAssetCategory/{id}")
	public String viewAssetCategory(@PathVariable("id") Long id,Model model) {
		AssetCatagory assetCategory = assetCatagoryService.getAssetCategory(id);
		
		

		
		model.addAttribute("assetCatagory", assetCategory);
		System.out.println("viewAssetCategory is calling with id "+id);
		return "view-assetcatagory-form";
	}
	
	@PostMapping(value = "/updateAssetCatagory", params = "delete")
	public String deleteCustomField(@ModelAttribute("assetCatagory") AssetCatagory assetCatagory, Model model,HttpServletRequest request) {
		String id=request.getParameter("id");
		Long assCatId = Long.parseLong(id);
		Long cusId = assetCatagory.getCustomeField().getId();
		AssetCatagory deleteCustomField = assetCatagoryService.deleteCustomField(assCatId,cusId);
		model.addAttribute("successMsg", deleteCustomField.getErrorMessage());
		model.addAttribute("assetCatagory", deleteCustomField);
		
		return "view-assetcatagory-form";
	}
	@GetMapping(value = "/editAssetCategory/{id}")
	public String editAssetCategory(@PathVariable Long id, Model model) {
		AssetCatagory assetCatagory = assetCatagoryService.updateAssetcategory(id);
		if(null!=assetCatagory.getId()) {
		System.out.println("id sending "+assetCatagory.getId());
		}else {
			System.out.println("id is null");
		}
		model.addAttribute("assetCatagory",assetCatagory);
		return "update-assetcatagory-form";
	}
	@GetMapping(value = "/editCustomeField/{id}/{name}")
	public String editCustomField(@PathVariable Long id,@PathVariable String name, Model model) {
		AssetCatagory assetCatagory = assetCatagoryService.updateCustomField(id, name);
		if(null!=assetCatagory.getId()) {
		System.out.println("id sending "+assetCatagory.getId());
		}else {
			System.out.println("id is null");
		}
		model.addAttribute("assetCatagory",assetCatagory);
		return "update-assetcatagory-form";
	}
	@GetMapping(value = "/deleteCustomeField/{id}")
	public String updateAssetCatagory(@PathVariable Long id, Model model) {
		/*AssetCatagory deleteCustomField = assetCatagoryService.deleteCustomField(id);
		model.addAttribute("successMsg", deleteCustomField.getErrorMessage());
		model.addAttribute("assetCatagory", deleteCustomField);*/
		
		return "view-assetcatagory-form";
	}
	@PostMapping(value = "/updateAssetCatagory")
	public String updateAssetCategory(@ModelAttribute("assetCatagory") AssetCatagory assetCatagory, Model model,HttpServletRequest request) {
		Long updatedBy=ApplicationThreadLocals.getUserId();
		Long id=Long.parseLong(request.getParameter("id"));
		assetCatagory.setId(id);
		assetCatagory.setUpdatedBy(updatedBy);
		AssetCatagory updateAssetCategory = assetCatagoryService.updateAssetCategory(assetCatagory);
		model.addAttribute("successMsg", updateAssetCategory.getErrorMessage());
		model.addAttribute("assetCatagory", updateAssetCategory);
		
		return "view-assetcatagory-form";
	}
}
