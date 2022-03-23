package org.egov.asset.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.CustomeFields;
import org.egov.asset.repository.AssetCatagoryTypeRepository;
import org.egov.asset.service.AssetCatagoryService;
import org.egov.asset.service.AssetService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assetcategory")
public class AssetCatagoryController {

	private static final Logger LOGGER = Logger.getLogger(AssetCatagoryController.class);
	@Autowired
	private AssetCatagoryService assetCatagoryService;
	@Autowired
	private AssetCatagoryTypeRepository repo;
	@Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

	
	@ModelAttribute
	public void addDropDownValuesToModel(Model model) {
		model.addAttribute("depriciationMethod", assetCatagoryService.getDepriciationMethod());
		model.addAttribute("parentCatagory", assetCatagoryService.getParentCatagory());
		model.addAttribute("unitOfMeasurement", assetCatagoryService.getUnitOfMeasurement());
		model.addAttribute("customFieldDataType", assetCatagoryService.getCustomFieldDataType());
		model.addAttribute("assetCatagoryTypes", assetCatagoryService.getAssetCatagoryType());
	}
	@PostMapping("/createAssetCategory")
	public String loadCreateAssetCatagoryForm(Model model) {
		model.addAttribute("assetCatagory", new AssetCatagory());
		return "assetcatagory-form";
		//return "create-asset-category";
	}
	@PostMapping("/createAssetCategoryNew")
	public String loadCreateAssetCatagoryFormNew(Model model) {
		model.addAttribute("assetCatagory", new AssetCatagory());
		//return "assetcatagory-form";
		return "create-asset-category";
	}

	@PostMapping(value = "/createAssetCategory", params = "add")
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

	@PostMapping(value = "/createAssetCategory", params = "create")
	public String createAssetCatagory(@ModelAttribute("assetCatagory") AssetCatagory assetCatagory, Model model,
			HttpServletRequest request) {

		LOGGER.info("crate method assetCatagory request " + assetCatagory);
		String message=null;
		
		
		Long userid = ApplicationThreadLocals.getUserId();
		assetCatagory.setUserid(String.valueOf(userid));

		AssetCatagory createAssetCatagory = assetCatagoryService.createAssetCatagory(assetCatagory);
		if (null != createAssetCatagory.getErrorMessage()) {
			message = messageSource.getMessage("msg.asset.category.duplicate",
                    new String[]{String.valueOf(createAssetCatagory.getName())}, null);
        
			model.addAttribute("assetCatagory", assetCatagory);
			//model.addAttribute("errorMessage", createAssetCatagory.getErrorMessage());
			model.addAttribute("errorMessage", message);
			return "assetcatagory-form";
		}
		if (createAssetCatagory != null) {
			model.addAttribute("assetCatagory", new AssetCatagory());
			//model.addAttribute("successMsg", createAssetCatagory.getName() + " created successfully..!!");
			message = messageSource.getMessage("msg.asset.category.create.success",
                    new String[]{String.valueOf(createAssetCatagory.getAssetCode()),String.valueOf(createAssetCatagory.getName())}, null);
			//model.addAttribute("message", message);
			model.addAttribute("successMsg", message);
		}

		//return "asset-success";
		return "assetcatagory-form";
	}

	@PostMapping("/searchAssetCategoryPage")
	public String loadSearchAssetCatagoryForm(Model model) {
		
		List<AssetCatagory> assetCategories=new ArrayList<AssetCatagory>();
		model.addAttribute("assetCategories", assetCategories);
		model.addAttribute("assetCatagory", new AssetCatagory());
		model.addAttribute("assetCatagoryTypes", assetCatagoryService.getAssetCatagoryType());
		return "search-assetcatagory-form";
	}
	@PostMapping("/searchAssetCategory")
	public String loadSearchAssetCatagoryForm(@ModelAttribute AssetCatagory assetCategory,Model model) {
		String name="";
		Long id=null;
		if(null!=assetCategory.getName()) {
			name=assetCategory.getName();
		}
		if(null!=assetCategory.getAssetCatagoryType()) {
			id=assetCategory.getAssetCatagoryType().getId();
		}
		//List<AssetCatagory> assetCategories=assetCatagoryService.findBynameorAssetCataType(name, id);
		List<AssetCatagory> assetCategories=assetCatagoryService.findBynameContainingOrAssetCataType(name, id);
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
	
	@PostMapping(value = "/updateAssetCategory", params = "delete")
	public String deleteCustomField(@ModelAttribute("assetCatagory") AssetCatagory assetCatagory, Model model,HttpServletRequest request) {
		String id=request.getParameter("id");
		Long assCatId = Long.parseLong(id);
		Long cusId = assetCatagory.getCustomeField().getId();
		AssetCatagory deleteCustomField = assetCatagoryService.deleteCustomField(assCatId,cusId);
		model.addAttribute("successMsg", deleteCustomField.getErrorMessage());
		model.addAttribute("assetCatagory", deleteCustomField);
		
		return "view-assetcategory-modified";
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
		return "update-assetcatagory-with-no-cf-form";
	}
	@GetMapping(value = "/editCustomField/{id}/{name}")
	public String editCustomField(@PathVariable Long id,@PathVariable String name, Model model) {
		AssetCatagory assetCatagory = assetCatagoryService.updateCustomField(id, name);
		model.addAttribute("assetCatagory",assetCatagory);
		return "update-assetcatagory-form";
	}
	@GetMapping(value = "/deleteCustomField/{id}")
	public String updateAssetCatagory(@PathVariable Long id, Model model) {
		/*AssetCatagory deleteCustomField = assetCatagoryService.deleteCustomField(id);
		model.addAttribute("successMsg", deleteCustomField.getErrorMessage());
		model.addAttribute("assetCatagory", deleteCustomField);*/
		
		return "view-assetcatagory-form";
	}
	@PostMapping(value = "/updateAssetCategory")
	public String updateAssetCategory(@ModelAttribute("assetCatagory") AssetCatagory assetCatagory, Model model,HttpServletRequest request) {
		Long updatedBy=ApplicationThreadLocals.getUserId();
		Long id=Long.parseLong(request.getParameter("id"));
		assetCatagory.setId(id);
		assetCatagory.setUpdatedBy(updatedBy);
		AssetCatagory updateAssetCategory = assetCatagoryService.updateAssetCategory(assetCatagory);
		model.addAttribute("successMsg", updateAssetCategory.getErrorMessage());
		model.addAttribute("assetCatagory", updateAssetCategory);
		
		return "view-assetcategory-modified";
	}
	@PostMapping("/modifySearchAssetCategoryPage")
	public String searchAssetCategoryModifyPage(Model model) {
		
		List<AssetCatagory> assetCategories=new ArrayList<AssetCatagory>();
		model.addAttribute("assetCategories", assetCategories);
		model.addAttribute("assetCatagory", new AssetCatagory());
		model.addAttribute("assetCatagoryTypes", assetCatagoryService.getAssetCatagoryType());
		return "search-assetcategory-modified";
	}
	@PostMapping("/searchAssetCategoryModifyPage")
	public String loadsearchAssetCategoryModifyPage(@ModelAttribute AssetCatagory assetCategory,Model model) {
		String name="";
		Long id=null;
		if(null!=assetCategory.getName()) {
			name=assetCategory.getName();
		}
		if(null!=assetCategory.getAssetCatagoryType()) {
			id=assetCategory.getAssetCatagoryType().getId();
		}
		//List<AssetCatagory> assetCategories=assetCatagoryService.findBynameorAssetCataType(name, id);
		List<AssetCatagory> assetCategories=assetCatagoryService.findBynameContainingOrAssetCataType(name, id);
		model.addAttribute("assetCategories", assetCategories);
		return "search-assetcategory-modified";
	}
	@GetMapping("/viewModifyAssetCategory/{id}")
	public String viewAssetCategoryModify(@PathVariable("id") Long id,Model model) {
		AssetCatagory assetCategory = assetCatagoryService.getAssetCategory(id);
		
		

		
		model.addAttribute("assetCatagory", assetCategory);
		System.out.println("viewAssetCategory is calling with id "+id);
		return "view-assetcategory-modified";
	}
	@PostMapping(value = "/updateAssetCategory", params = "add")
	public String addCustomField(@ModelAttribute("assetCatagory") AssetCatagory catagory, Model model,
			HttpServletRequest request) {
		
		String id= request.getParameter("id");
		if(null!=id) {
			catagory.setId(Long.parseLong(id));
		}
		Long updatedBy=ApplicationThreadLocals.getUserId();
		catagory.setUpdatedBy(updatedBy);
		catagory.setCreatedBy(updatedBy);
		assetCatagoryService.addNewCustomField(catagory);
		AssetCatagory response = assetCatagoryService.getAssetCategory(catagory.getId());
		model.addAttribute("successMsg", catagory.getCustomeField().getName() + " added successfully..!!");
		model.addAttribute("assetCatagory", response);
		return "view-assetcategory-modified";
	}
}
