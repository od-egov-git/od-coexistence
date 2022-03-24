package org.egov.asset.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.asset.model.AssetHistory;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.service.AssetHistoryService;
import org.egov.asset.service.DisposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/history")
public class AssetHistoryController {

	@Autowired
	private DisposalService disposalService;
	@Autowired
	private AssetHistoryService assetHistoryService;
	
	@ModelAttribute
	public void addDropDownValuesToModel(Model model) {
		//model.addAttribute("assetList", disposalService.getAssets());
		model.addAttribute("departmentList", disposalService.getDepartments());
		model.addAttribute("assetStatusList", disposalService.getAssetStatus());
		model.addAttribute("assetCategoryList", disposalService.getAssetCategories());
		model.addAttribute("mode", "add");
		model.addAttribute("disabled", "");
	}
	
	
	@PostMapping("/search")
	public String viewform(Model model) {
		
 		AssetMaster assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		
		return "search-asset-history";
	}
	@PostMapping(value = "/search", params = "search")
	public String search(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
		List<AssetMaster> assetList = assetHistoryService.getAssetMasterDetail(assetBean);
		model.addAttribute("assetList", assetList);
		model.addAttribute("assetBean", assetBean);
		return "search-asset-history";
	}
	@GetMapping("/searchAssetHistory/{asset_id}")
	public String assetHistorySearch(@PathVariable Long asset_id,Model model) {
		
 		List<AssetHistory> assetHistoryList = assetHistoryService.getAssetHistoryByAssetId(asset_id);
		model.addAttribute("assetHistoryList", assetHistoryList);
		
		return "view-asset-history";
	}
	
}
