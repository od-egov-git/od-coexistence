package org.egov.egf.web.controller.expensebill;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asset")
public class AssetCatagoryController {

	@PostMapping("/createAssetCatagory")
	public String loadCreateAssetCatagoryForm() {
		System.out.println("calling");
		return "assetcatagory-form";
	}
}
