package org.egov.asset.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.Depreciation;
import org.egov.asset.model.DepreciationInputs;
import org.egov.asset.model.DepreciationList;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.service.AssetCatagoryService;
import org.egov.asset.service.DepreciationService;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
@RequestMapping("/depreciation")
public class DepreciationController {
	private static final String STATE_TYPE = "stateType";
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	
	@Autowired
    protected EgovMasterDataCaching masterDataCache;
	
	private List<DepreciationList>  depreciationList = null;
	
	@Autowired
	MicroserviceUtils microserviceUtils;
	
	@Autowired
	private AssetCatagoryRepository categoryRepo;
	@Autowired
	private AssetCatagoryService assetCatagoryService;
	@Autowired
    private DepreciationService depreciationService;
	
	 @RequestMapping(value = "/createAssetDepreciation", method = RequestMethod.POST)
		public String newDepreciationForm(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request) {
			System.out.println("calling");
			List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();	
			assetCategoryList = categoryRepo.findAll();
		  	model.addAttribute("categoryName", assetCategoryList);
		    model.addAttribute("categoryType", assetCatagoryService.getAssetCatagoryType());
			model.addAttribute("departments",microserviceUtils.getDepartments());
			model.addAttribute(STATE_TYPE, depreciation.getClass().getSimpleName());
			return "depreciation-create";
		}
	 	
	    @RequestMapping(value = "/searchDepreciation",params="search", method = RequestMethod.POST)
	    //@Action(value = "/listData")
	    public String searchDepreciation(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request,
				final BindingResult resultBinder) {
		
	    final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> list= null;
		SQLQuery queryMain =  null;
		query1
		      .append("select ac.name,ah.department,ac.asset_code,ah.asset_name,am.current_value ,ac.depriciation_rate from	asset_master am,asset_header ah,asset_category ac,asset_revaluation ar where am.id=ar.asset_master_id and am.asset_header=ah.id	and ah.asset_category =ac.id and TO_CHAR(ar.rev_date,'dd-mm-yyyy') = '"+depreciation.getDepreciationDate()+"' ");
		System.out.println("categoryName "+depreciation.getCategoryName());
		if(depreciation.getCategoryName()!=null) {
			query1.append(" and ac.name = '"+depreciation.getCategoryName()+"'");
		}
		System.out.println("categoryType "+depreciation.getCategoryType());
		if(depreciation.getCategoryType()!=null) {
			query1.append(" and ac.asset_catagory_type_id='"+depreciation.getCategoryType()+"'");
		}
		System.out.println("department "+depreciation.getDepartment());
		if(depreciation.getDepartment()!=null) {
			query1.append(" and ah.department='"+depreciation.getDepartment()+"'");
		}
		System.out.println("assetCode "+depreciation.getAssetCode());
		if(depreciation.getAssetCode()!=null) {
			query1.append(" and ac.asset_code LIKE '%"+depreciation.getAssetCode()+"%'");
		}
		System.out.println("assetName "+depreciation.getAssetName());
		if(depreciation.getAssetName()!=null) {
			query1.append(" and ah.asset_name LIKE '%"+depreciation.getAssetName()+"%'");
		}
		
		queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
		list = queryMain.list();  
	    DepreciationList result =null;
	       if (list.size() != 0) {
	       	depreciationList = new ArrayList<DepreciationList>();
	       	System.out.println("Size :: "+list.size() );
	       	int i=1;
	           for (final Object[] object : list) {
	        	   result = new DepreciationList();
	        	   result.setSlNo(i);
	        	   result.setAssetCategoryName(object[0]!=null?object[0].toString():"");
	        	   result.setDepartment(object[1]!=null?object[1].toString():"");
	        	   result.setAssetCode(object[2]!=null?object[2].toString():"");
	        	   result.setAssetName(object[3]!=null?object[3].toString():"");
	        	   result.setCurrentGrossValue(object[4]!=null?new BigDecimal(object[4].toString()):new BigDecimal(0));
	        	   result.setDepreciationRate(object[5]!=null?Double.parseDouble(object[5].toString()):0);
	        	   depreciationList.add(result);
	        	   i++;
	           }
	       }
	       else
	    	   depreciationList=null;
	       depreciation.setResultList(depreciationList);
	    	System.out.println("listData method");
	    	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();	
			assetCategoryList = categoryRepo.findAll();
		  	model.addAttribute("categoryName", assetCategoryList);
		    model.addAttribute("categoryType", assetCatagoryService.getAssetCatagoryType());
			model.addAttribute("departments", microserviceUtils.getDepartments());
	    	return "depreciation-create";
	    }
	
	    @RequestMapping(value = "/searchDepreciation",params="save", method = RequestMethod.POST)
	    //@Action(value = "/listData")
	    public String save(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request,
				final BindingResult resultBinder) {
	    	System.out.println("create called");
	    	List<DepreciationList> resultList=depreciation.getResultList();
	    	DepreciationInputs inputList=new DepreciationInputs();
	    	List<DepreciationInputs> finalList= new ArrayList();
	    	Depreciation dep=new Depreciation();
	    	depreciationList = new ArrayList<DepreciationList>();
	    	int cnt=1;
	    	for(DepreciationList i:resultList) {
	    		System.out.println(("checked "+i.isChecked()));
	    		if(i.isChecked()==true) {
	    			inputList=depreciationService.saveDepreciationAsset(i,depreciation.getDepreciationDate(),cnt,depreciationList);
	    			finalList.add(inputList);
	    			cnt++;
	    		}
	    	}
	    	depreciation.setResultList(depreciationList);
	    	return "depreciation-success";
	    }
	    @RequestMapping(value = "/viewAssetDepreciation", method = RequestMethod.POST)
		public String viewDepreciationForm(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request) {
			System.out.println("view main calling");
			
			List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();	
			assetCategoryList = categoryRepo.findAll();
		  	model.addAttribute("categoryName", assetCategoryList);
		    return "depreciation-search";
		}
	    
	    @RequestMapping(value = "/searchViewDepreciation", method = RequestMethod.POST)
	    //@Action(value = "/listData")
	    public String searchViewDepreciation(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request,
				final BindingResult resultBinder) {
		
	    final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> list= null;
		SQLQuery queryMain =  null;
		query1
		      .append("select ad.id,ad.assetcode,ad.assetname,ad.categoryname,ad.location,ad.afterdepreciation from asset_depreciation ad where ad.vouchernumber notnull ");
		System.out.println("categoryName "+depreciation.getCategoryName());
		if(depreciation.getCategoryName()!=null) {
			query1.append(" and ad.categoryname='"+depreciation.getCategoryName()+"'");
		}
		System.out.println("location "+depreciation.getLocation());
		if(depreciation.getLocation()!=null) {
			query1.append(" and ad.location LIKE '%"+depreciation.getLocation()+"%'");
		}
		System.out.println("assetCode "+depreciation.getAssetCode());
		if(depreciation.getAssetCode()!=null) {
			query1.append(" and ac.assetcode LIKE '%"+depreciation.getAssetCode()+"%'");
		}
		System.out.println("assetName "+depreciation.getAssetName());
		if(depreciation.getAssetName()!=null) {
			query1.append(" and ah.assetname LIKE '%"+depreciation.getAssetName()+"%'");
		}
		
		queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
		list = queryMain.list();  
	    DepreciationList result =null;
	       if (list.size() != 0) {
	       	depreciationList = new ArrayList<DepreciationList>();
	       	System.out.println("Size :: "+list.size() );
	       	int i=1;
	           for (final Object[] object : list) {
	        	   result = new DepreciationList();
	        	   result.setSlNo(i);
	        	   result.setId(object[0]!=null?Integer.valueOf(object[0].toString()):0);
	        	   result.setAssetCode(object[1]!=null?object[1].toString():"");
	        	   result.setAssetName(object[2]!=null?object[2].toString():"");
	        	   result.setAssetCategoryName(object[3]!=null?object[3].toString():"");
	        	   result.setLocation(object[4]!=null?object[4].toString():"");
	        	   result.setCurrentGrossValue(object[5]!=null?new BigDecimal(object[5].toString()):new BigDecimal(0));
	        	   depreciationList.add(result);
	        	   i++;
	           }
	       }
	       else
	    	   depreciationList=null;
	       depreciation.setResultList(depreciationList);
	    	System.out.println("listData method");
	    	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();	
			assetCategoryList = categoryRepo.findAll();
		  	model.addAttribute("categoryName", assetCategoryList);
		        
	    	return "depreciation-search";
	    }
		    
	    
	    @RequestMapping(value = "/viewDepreciation/{id}", method = RequestMethod.GET)
	    //@Action(value = "/listData")
	    public String viewDepreciation(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request,
				final BindingResult resultBinder,@PathVariable final String id) {
	    	System.out.println("id "+id);
	    	final StringBuffer query2 = new StringBuffer(500); 
			  List<Object[]> list2= null;
			  SQLQuery queryMain2 = null; 
			  query2
			  .append("select ad.id,ad.assetcode,ad.assetname,ad.categoryname,ad.depreciationrate,ad.currentdepreciation, " + 
			  		" ad.depreciationdate,ad.afterdepreciation,ad.vouchernumber,ad.description from asset_depreciation ad where ad.id="+id);
			  queryMain2=this.persistenceService.getSession().createSQLQuery(query2.toString()); 
			  list2 = queryMain2.list();
			  Depreciation result=null;
			  for (final Object[] object : list2) {
	        	   result = new Depreciation();
	        	   result.setId(object[0]!=null?Integer.valueOf(object[0].toString()):0);
	        	   result.setAssetCode(object[1]!=null?object[1].toString():"");
	        	   result.setAssetName(object[2]!=null?object[2].toString():"");
	        	   result.setCategoryName(object[3]!=null?object[3].toString():"");
	        	   result.setDepreciationRate(object[4]!=null?object[4].toString():"");
	        	   result.setCurrentDepreciation(object[5]!=null?object[5].toString():"");
	        	   result.setDepreciationDate(object[6]!=null?object[6].toString():"");
	        	   result.setAfterDepreciation(object[7]!=null?object[7].toString():"");
	        	   result.setVoucherNumber(object[8]!=null?object[8].toString():"");
	        	   result.setDescription(object[9]!=null?object[9].toString():"");
	           }
			  model.addAttribute("Depreciation", result);
	    	return "depreciation-view";
	    }
	   
	    @RequestMapping(value = "/reportAssetDepreciation", method = RequestMethod.POST)
		public String reportDepreciationForm(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request) {
			System.out.println("view report calling");
			
			List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();	
			assetCategoryList = categoryRepo.findAll();
		  	model.addAttribute("categoryName", assetCategoryList);
		  	model.addAttribute("categoryType", assetCatagoryService.getAssetCatagoryType());
		    return "depreciation-report";
		}
	    
	    @RequestMapping(value = "/viewReportDepreciation", method = RequestMethod.POST)
	    public String viewReportDepreciation(@ModelAttribute("Depreciation") final Depreciation depreciation,final Model model,HttpServletRequest request,
				final BindingResult resultBinder) {
		
	    final StringBuffer query1 = new StringBuffer(500);
		List<Object[]> list= null;
		SQLQuery queryMain =  null;
		query1
		      .append("select ad.id,ad.assetcode,ad.assetname,ad.categoryname,ad.department,ad.categorytype,ad.depreciationrate,ad.beforedepreciation,ad.currentdepreciation,ad.afterdepreciation,ad.voucherNumber from asset_depreciation ad where ad.vouchernumber notnull ");
		System.out.println("categoryName "+depreciation.getCategoryName());
		if(depreciation.getCategoryName()!=null) {
			query1.append(" and ad.categoryname='"+depreciation.getCategoryName()+"'");
		}
		System.out.println("categoryType "+depreciation.getCategoryType());
		if(depreciation.getCategoryType()!=null) {
			query1.append(" and ad.categorytype='"+depreciation.getCategoryType()+"'");
		}
		System.out.println("assetCode "+depreciation.getAssetCode());
		if(depreciation.getAssetCode()!=null) {
			query1.append(" and ac.assetcode LIKE '"+depreciation.getAssetCode()+"'");
		}
		System.out.println("assetName "+depreciation.getAssetName());
		if(depreciation.getAssetName()!=null) {
			query1.append(" and ah.assetname LIKE '"+depreciation.getAssetName()+"'");
		}
		
		queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
		list = queryMain.list();  
	    DepreciationList result =null;
	       if (list.size() != 0) {
	       	depreciationList = new ArrayList<DepreciationList>();
	       	System.out.println("Size :: "+list.size() );
	       	int i=1;
	           for (final Object[] object : list) {
	        	   result = new DepreciationList();
	        	   result.setSlNo(i);
	        	   result.setId(object[0]!=null?Integer.valueOf(object[0].toString()):0);
	        	   result.setAssetCode(object[1]!=null?object[1].toString():"");
	        	   result.setAssetName(object[2]!=null?object[2].toString():"");
	        	   result.setAssetCategoryName(object[3]!=null?object[3].toString():"");
	        	   result.setDepartment(object[4]!=null?object[4].toString():"");
	        	   result.setCategoryType(object[5]!=null?object[5].toString():"");
	        	   result.setDepreciationRate(object[6]!=null?Double.parseDouble(object[6].toString()):0);
	        	   result.setCurrentGrossValue(object[7]!=null?new BigDecimal(object[7].toString()):new BigDecimal(0));
	        	   result.setCurrentDepreciation(object[8]!=null?object[8].toString():"");
	        	   result.setAfterDepreciation(object[9]!=null?object[9].toString():"");
	        	   result.setVoucherNumber(object[10]!=null?object[10].toString():"");
	        	   depreciationList.add(result);
	        	   i++;
	           }
	       }
	       else
	    	   depreciationList=null;
	       depreciation.setResultList(depreciationList);
	    	System.out.println("listData method");
	    	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();	
			assetCategoryList = categoryRepo.findAll();
		  	model.addAttribute("categoryName", assetCategoryList);
		        
	    	return "depreciation-report";
	    }
}
