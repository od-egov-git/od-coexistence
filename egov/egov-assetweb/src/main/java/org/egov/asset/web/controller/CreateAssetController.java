package org.egov.asset.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetCustomFieldMapper;
import org.egov.asset.model.AssetHeader;
import org.egov.asset.model.AssetLocality;
import org.egov.asset.model.AssetLocation;
import org.egov.asset.model.AssetLocationBlock;
import org.egov.asset.model.AssetLocationElectionWard;
import org.egov.asset.model.AssetLocationRevenueWard;
import org.egov.asset.model.AssetLocationStreet;
import org.egov.asset.model.AssetLocationZone;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetModeOfAcquisition;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.CustomeFields;
import org.egov.asset.repository.AssetBlockRepository;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetCustomFieldMapperRepository;
import org.egov.asset.repository.AssetElectionWardRepository;
import org.egov.asset.repository.AssetLocalityRepository;
import org.egov.asset.repository.AssetLocationStreetRepository;
import org.egov.asset.repository.AssetLocationZoneRepository;
import org.egov.asset.repository.AssetMasterRepository;
import org.egov.asset.repository.AssetModeOfAcquisitionRepository;
import org.egov.asset.repository.AssetRevenueWardRepository;
import org.egov.asset.repository.AssetStatusRepository;
import org.egov.asset.service.AssetService;
import org.egov.asset.utils.AssetConstant;
import org.egov.asset.web.controller.adaptor.AssetJsonAdaptor;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.repository.DepartmentRepository;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.services.masters.SchemeService;
import org.egov.services.masters.SubSchemeService;
import org.egov.utils.FinancialConstants;
import org.hibernate.SQLQuery;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Arnab Saha
 */

@Controller
@RequestMapping(value = "/assetcreate")
public class CreateAssetController {// extends BaseAssetController{

	private static final Logger LOGGER = Logger.getLogger(CreateAssetController.class);
	private static final int BUFFER_SIZE = 4096;
	protected String showMode;

	private AssetMaster assetBean;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@Autowired
	private FundHibernateDAO fundHibernateDAO;
	@Autowired
	private FunctionDAO functionDAO;
	@Autowired
	private AssetModeOfAcquisitionRepository acqRepo;
	@Autowired
	private AssetCatagoryRepository categoryRepo;
	@Autowired
	private AssetStatusRepository statusRepo;
	@Autowired
	private MicroserviceUtils microserviceUtils;
	@Autowired
	private AssetLocalityRepository localityRepo;
	@Autowired
	private AssetBlockRepository blockRepo;
	@Autowired
	private AssetElectionWardRepository electionWardRepo;
	@Autowired
	private AssetRevenueWardRepository revenueWardRepo;
	@Autowired
	private AssetLocationStreetRepository streetRepo;
	@Autowired
	private AssetLocationZoneRepository zoneRepo;
	@Autowired
	private AssetCustomFieldMapperRepository customFieldRepo;
	
	@Autowired
	private DepartmentRepository deptRepo;
	
	@Autowired
	private AssetService assetService;
	@Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;
	@Autowired
    @Qualifier("schemeService")
    private SchemeService schemeService;

    @Autowired
    @Qualifier("subSchemeService")
    private SubSchemeService subSchemeService;
    
    @Autowired
	private AssetMasterRepository masterRepo;
    
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @Autowired
    private FileStoreService fileStoreService;

    List<AssetMaster> assetList = new ArrayList<AssetMaster>();
	List<Department> departmentList = new ArrayList<>();
	List<Fund> fundList = new ArrayList<Fund>();
	List<CFunction> functionList = new ArrayList<CFunction>();
	List<AssetCatagory> assetCategoryList = new ArrayList<AssetCatagory>();
	List<Scheme> schemeList = new ArrayList<Scheme>();
	List<SubScheme> subSchemeList = new ArrayList<SubScheme>();
	List<AssetModeOfAcquisition> modeOfAcquisitionList = new ArrayList<AssetModeOfAcquisition>();
	List<AssetStatus> assetStatusList = new ArrayList<AssetStatus>();
	
	List<AssetLocality> localityList = new ArrayList<AssetLocality>();
	List<AssetLocationBlock> blockList = new ArrayList<AssetLocationBlock>();
	List<AssetLocationElectionWard> electionWardList = new ArrayList<AssetLocationElectionWard>();
	List<AssetLocationRevenueWard> revenueWardList = new ArrayList<AssetLocationRevenueWard>();
	List<AssetLocationStreet> streetList = new ArrayList<AssetLocationStreet>();
	List<AssetLocationZone> zoneList = new ArrayList<AssetLocationZone>();
	List<AssetCustomFieldMapper> mapperList = new ArrayList<AssetCustomFieldMapper>();
	
	@PostMapping("/newform")
	public String newform(Model model) {
		LOGGER.info("New Form.Fresh Operation Starts.");

		assetBean = new AssetMaster();
		assetBean.setAssetHeader(new AssetHeader());
		assetBean.setAssetLocation(new AssetLocation());
		model.addAttribute("assetBean", assetBean);
		try {
			fundList = fundHibernateDAO.findAllActiveFunds();
			departmentList = deptRepo.findAll(); //masterDataCache.get("egi-department");		//microserviceUtils.getDepartments();
			modeOfAcquisitionList = acqRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			functionList = functionDAO.getAllActiveFunctions();
			
			localityList = localityRepo.findAll();
			blockList = blockRepo.findAll();
			electionWardList = electionWardRepo.findAll();
			revenueWardList = revenueWardRepo.findAll();
			streetList = streetRepo.findAll();
			zoneList = zoneRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		
		try {
			int fundId = fundList.get(0).getId();
			schemeList = schemeService.getByFundId(fundId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("departmentList", departmentList);
		//model.addAttribute("departmentList", masterDataCache.get("egi-department"));
		model.addAttribute("fundList", fundList);
		model.addAttribute("modeOfAcquisitionList", modeOfAcquisitionList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("functionList", functionList);
		model.addAttribute("schemeList", schemeList);
		model.addAttribute("subSchemeList", subSchemeList);
		 
		model.addAttribute("localityList", localityList);
		model.addAttribute("blockList", blockList);
		model.addAttribute("electionWardList", electionWardList);
		model.addAttribute("revenueWardList", revenueWardList);
		model.addAttribute("streetList", streetList);
		model.addAttribute("zoneList", zoneList);

		return "asset-create";
	}

	@PostMapping(value = "/create", params = "create", consumes = {"multipart/form-data"})
	public String create(@RequestParam("file") MultipartFile file, @ModelAttribute("assetBean") AssetMaster assetBean,Model model, HttpServletRequest request) {

		LOGGER.info("Creating Asset Object");
		long userId = ApplicationThreadLocals.getUserId();
		//File
		List<DocumentUpload> list = new ArrayList<>();
        try {
        	if(!file.isEmpty()) {
	        	DocumentUpload upload = new DocumentUpload();
	        	ByteArrayInputStream bios = (ByteArrayInputStream) file.getInputStream();//new ByteArrayInputStream(FileUtils.readFileToByteArray(file.getInputStream()));
	        	upload.setInputStream(bios);
	        	upload.setFileName(file.getOriginalFilename());
	            upload.setContentType(file.getContentType());
	            list.add(upload);
        	}
        }catch(Exception e) {
        	e.printStackTrace();
        }
        assetBean.setDocumentDetail(list);
        
		assetBean.setCreatedBy(String.valueOf(userId));
		assetBean.getAssetHeader().setCreatedBy(String.valueOf(userId));
		assetBean.getAssetLocation().setCreatedBy(String.valueOf(userId));
		
		assetBean.setCreatedDate(new Date());
		assetBean.getAssetHeader().setCreatedDate(new Date());
		assetBean.getAssetLocation().setCreatedDate(new Date());
		
		String message = "";
		String assetCode = "";
		long nextVal = generateNextVal();
		
		//Custom Fields
		List<AssetCustomFieldMapper> mapperList = new ArrayList<AssetCustomFieldMapper>();
		AssetCustomFieldMapper item = new AssetCustomFieldMapper();
		try {
			List<CustomeFields> customeFields = assetBean.getAssetHeader().getAssetCategory().getCustomeFields();
			if(!customeFields.isEmpty()) {
				for(int i=0; i<customeFields.size(); i++) {
					item = new AssetCustomFieldMapper();
					String name = customeFields.get(i).getName();
					item.setName(name);
					String values = request.getParameter("customField_"+i);
					item.setVal(values);
					item.setCreatedDate(new Date());
					item.setCreatedBy(String.valueOf(userId));
					item.setAssetCatagory(assetBean.getAssetHeader().getAssetCategory());
					item.setCustomeFields(customeFields.get(i));
					
					mapperList.add(item);
				}
				assetBean.setAssetCustomFieldMappers(mapperList);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		//Custom Fields Ends
		//Calculating Current Value
		try {
			if(null != assetBean.getGrossValue() && null != assetBean.getAccumulatedDepreciation()) {
				long currentValue =  assetBean.getGrossValue() - assetBean.getAccumulatedDepreciation();//            currentValue.setCurrentAmount(grossValue.subtract(accumulatedDepreciation));
				assetBean.setCurrentValue(currentValue);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		//Calculating Current Value Ends
		try {
			final StringBuilder generatedCode = new StringBuilder(String.format("%06d", nextVal));
			assetCode = generatedCode.toString();
			assetBean.setCode(assetCode);
			assetBean = assetService.create(assetBean);
			LOGGER.info("Asset Created Successfully");
		    message = getMessageByStatus(assetBean,"create", true);
		}catch(Exception e) {
			LOGGER.error("Error Occured : Asset Creation Failed");
			message = getMessageByStatus(assetBean,"create", false);
			e.printStackTrace();
		}
		model.addAttribute("message", message);
		
		return "asset-success";
	}
	
	public Long generateNextVal() {
		long id = 0;
		try {
			id = masterRepo.getNextValMySequence();
		}catch(Exception e) {
			LOGGER.error("Error Occured while generating Asset Code");
		}
		return id;
	}
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String showSuccessPage(@RequestParam("id") final String od, final Model model,
	    final HttpServletRequest request) {
	      
		model.addAttribute("message", "SuccessFully created");
        return "asset-success";
	}
	
	private String getMessageByStatus(final AssetMaster assetMaster, String item, boolean isSucess) {
        String message = "";

        if(isSucess) {
        	if(item.equalsIgnoreCase("create")) {
            	message = messageSource.getMessage("msg.asset.create.success",
                        new String[]{String.valueOf(assetMaster.getCode()),String.valueOf(assetMaster.getAssetHeader().getAssetName())}, null);
            }else if(item.equalsIgnoreCase("update")) {
            	message = messageSource.getMessage("msg.asset.update.success",
                        new String[]{String.valueOf(assetMaster.getCode()),String.valueOf(assetMaster.getAssetHeader().getAssetName())}, null);
            }else {
            }
        }else {
        	if(item.equalsIgnoreCase("create")) {
            	message = messageSource.getMessage("msg.asset.create.failed",
                        new String[]{String.valueOf(assetMaster.getId())}, null);
            }else if(item.equalsIgnoreCase("update")) {
            	message = messageSource.getMessage("msg.asset.update.failed",
                        new String[]{String.valueOf(assetMaster.getId())}, null);
            }else {
            }
        }
        return message;
    }
	
	@PostMapping("/viewform/{param}")
	public String viewform(@PathVariable("param") String param, Model model) {
		LOGGER.info("View Form..................");
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		try {
			departmentList = deptRepo.findAll(); //microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			
			//assetList = masterRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		//model.addAttribute("assetList", assetList);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("localityList", localityList);
		model.addAttribute("mode", "add");
		model.addAttribute("disabled", "");
		model.addAttribute("isViewPage", true);
		if(param.equalsIgnoreCase("ref")) {
			model.addAttribute("isReference", true);
		}else {
			model.addAttribute("isReference", false);
		}
		return "asset-view";
	}
	
	@GetMapping("/assetRef/{param}")
	public String assetRef(@PathVariable("param") String param, Model model) {
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		try {
			departmentList = deptRepo.findAll(); //microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			
			//assetList = masterRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		//model.addAttribute("assetList", assetList);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("localityList", localityList);
		model.addAttribute("mode", "add");
		model.addAttribute("disabled", "");
		model.addAttribute("isViewPage", true);
		if(param.equalsIgnoreCase("ref")) {
			model.addAttribute("isReference", true);
		}else {
			model.addAttribute("isReference", false);
		}
		return "asset-view";
	}
	
	@RequestMapping(value = "/getassetRef", method = RequestMethod.GET)
    @ResponseBody
    public List<AssetMaster> getAllSchemesByFundId(@RequestParam("code") final String code, @RequestParam("name") final String name
    		,@RequestParam("assetCategory") final String assetCategory, @RequestParam("department") final String department, 
    		@RequestParam("status") final String status){
		
		try {
			/*Long statusId = null;
			Long departmentId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			if(null != assetBean.getAssetHeader().getDepartment()) {
				departmentId = assetBean.getAssetHeader().getDepartment().getId();
			}
			assetList = masterRepo.getAssetMasterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(),
					departmentId, statusId);*/
			assetList = assetService.searchAssets(assetBean);
		} catch (Exception e) {
			e.getMessage();
			assetList = new ArrayList<>();
		}
		//model.addAttribute("assetList", assetList);
		
        return assetList;
    }
	
	@RequestMapping(value = "/getsubschemesbyschemeid", method = RequestMethod.GET)
    @ResponseBody
    public List<SubScheme> getAllSubSchemesBySchemeId(@RequestParam("schemeId") final String schemeId)
            throws ApplicationException {
        return subSchemeService.getBySchemeId(Integer.parseInt(schemeId));
    }
	
	@GetMapping("/editform/{assetid}")
	public String editform(@PathVariable("assetid") String assetId, Model model, HttpServletRequest request) {
		LOGGER.info("Edit Operation Starts..................");
		model.addAttribute("isViewPage", false);
		assetBean = new AssetMaster();
		assetBean = masterRepo.findOne(Long.valueOf(assetId));
		try {
			//final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(assetId));
			List<DocumentUpload> documents = assetService.findByObjectIdAndObjectType(assetBean.getId(),
	                AssetConstant.FILESTORE_MODULEOBJECT_ASSET);
	        assetBean.setDocumentDetail(documents);
		}catch(Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("assetBean", assetBean);
		mapperList = assetBean.getAssetCustomFieldMappers();
		try {
			fundList = fundHibernateDAO.findAllActiveFunds();
			departmentList = deptRepo.findAll(); //microserviceUtils.getDepartments();
			modeOfAcquisitionList = acqRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			functionList = functionDAO.getAllActiveFunctions();
			
			localityList = localityRepo.findAll();
			blockList = blockRepo.findAll();
			electionWardList = electionWardRepo.findAll();
			revenueWardList = revenueWardRepo.findAll();
			streetList = streetRepo.findAll();
			zoneList = zoneRepo.findAll();
			int fundId = fundList.get(0).getId();
			int schemeId = 0;
			if(!assetBean.getAssetHeader().getScheme().equalsIgnoreCase(null) || assetBean.getAssetHeader().getScheme() != null){
				schemeId = Integer.parseInt(assetBean.getAssetHeader().getScheme());
			}
			schemeList = schemeService.getByFundId(fundId);
			subSchemeList = subSchemeService.getBySchemeId(schemeId);
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for editform. Error -> "+e.getMessage());
		}
		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("fundList", fundList);
		model.addAttribute("modeOfAcquisitionList", modeOfAcquisitionList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("functionList", functionList);
		model.addAttribute("schemeList", schemeList);
		model.addAttribute("subSchemeList", subSchemeList);
		 
		model.addAttribute("localityList", localityList);
		model.addAttribute("blockList", blockList);
		model.addAttribute("electionWardList", electionWardList);
		model.addAttribute("revenueWardList", revenueWardList);
		model.addAttribute("streetList", streetList);
		model.addAttribute("zoneList", zoneList);
		model.addAttribute("mode", "view");
		model.addAttribute("disabled", "disabled");
		model.addAttribute("mapperList", mapperList);
		
		return "asset-update";
	}
	
	@GetMapping("/viewupdateform/{assetid}")
	public String viewUpdate(@PathVariable("assetid") String assetId, Model model, HttpServletRequest request) {
		LOGGER.info("Edit Operation.................."+assetId);
		model.addAttribute("isViewPage", false);
		assetBean = new AssetMaster();
		assetBean = masterRepo.findOne(Long.valueOf(assetId));
		try {
			//final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(assetId));
			List<DocumentUpload> documents = assetService.findByObjectIdAndObjectType(assetBean.getId(),
	                AssetConstant.FILESTORE_MODULEOBJECT_ASSET);
	        assetBean.setDocumentDetail(documents);
		}catch(Exception e) {
			LOGGER.error("Error Occured : While fetching File details for viewupdateform. Error -> "+e.getMessage());
		}
		model.addAttribute("assetBean", assetBean);
		mapperList = assetBean.getAssetCustomFieldMappers();
		try {
			fundList = fundHibernateDAO.findAllActiveFunds();
			departmentList = deptRepo.findAll(); //microserviceUtils.getDepartments();
			modeOfAcquisitionList = acqRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			functionList = functionDAO.getAllActiveFunctions();
			
			localityList = localityRepo.findAll();
			blockList = blockRepo.findAll();
			electionWardList = electionWardRepo.findAll();
			revenueWardList = revenueWardRepo.findAll();
			streetList = streetRepo.findAll();
			zoneList = zoneRepo.findAll();
			
			int fundId = fundList.get(0).getId();
			int schemeId = 0;
			if(!assetBean.getAssetHeader().getScheme().equalsIgnoreCase(null) || assetBean.getAssetHeader().getScheme() != null){
				schemeId = Integer.parseInt(assetBean.getAssetHeader().getScheme());
			}
			schemeList = schemeService.getByFundId(fundId);
			subSchemeList = subSchemeService.getBySchemeId(schemeId);
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for viewupdateform. Error -> "+e.getMessage());
		}
		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("fundList", fundList);
		model.addAttribute("modeOfAcquisitionList", modeOfAcquisitionList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("functionList", functionList);
		model.addAttribute("schemeList", schemeList);
		model.addAttribute("subSchemeList", subSchemeList);
		model.addAttribute("localityList", localityList);
		model.addAttribute("blockList", blockList);
		model.addAttribute("electionWardList", electionWardList);
		model.addAttribute("revenueWardList", revenueWardList);
		model.addAttribute("streetList", streetList);
		model.addAttribute("zoneList", zoneList);
		model.addAttribute("mode", "update");
		model.addAttribute("viewmode", "update");
		model.addAttribute("disabled", "disabled");
		model.addAttribute("mapperList", mapperList);
		
		return "asset-update";
	}
	
	@PostMapping(value = "/update", params = "update", consumes = {"multipart/form-data"})
	public String update(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
		LOGGER.info("Updating Asset Object Starts");
		String message = "";
		
		long userId = ApplicationThreadLocals.getUserId();
		assetBean.setUpdatedBy(String.valueOf(userId));
		assetBean.getAssetHeader().setUpdatedBy(String.valueOf(userId));
		assetBean.getAssetLocation().setUpdatedBy(String.valueOf(userId));
		
		assetBean.setUpdatedDate(new Date());
		assetBean.getAssetHeader().setUpdatedDate(new Date());
		assetBean.getAssetLocation().setUpdatedDate(new Date());
		
		String assetid = request.getParameter("id");
		String assetHeaderId = request.getParameter("assetHeaderId");
		String assetLocationId = request.getParameter("assetLocationId");
		
		assetBean.setId(Long.parseLong(assetid));
		//Custom Fields
		List<AssetCustomFieldMapper> mapperList = new ArrayList<AssetCustomFieldMapper>();
		mapperList = assetBean.getAssetCustomFieldMappers();
		AssetCustomFieldMapper item = new AssetCustomFieldMapper();
		try {
			List<CustomeFields> customeFields = assetBean.getAssetHeader().getAssetCategory().getCustomeFields();
			if(!mapperList.isEmpty()) {
				for(int i=0; i<customeFields.size(); i++) {
					item = assetBean.getAssetCustomFieldMappers().get(i);
					String name = item.getName();
					String id = String.valueOf(item.getId());
					String values = request.getParameter("customField_"+id);
					item.setVal(values);
				}
			}
		}catch(Exception e) {
			LOGGER.error("Error Occured : While updating custom fields values for update. Error -> "+e.getMessage());
		}
		//Custom Fields Ends
		//File Starts
		List<DocumentUpload> list = new ArrayList<>();
		/*try {
			if(!file.isEmpty()) {
				DocumentUpload upload = new DocumentUpload();
	        	ByteArrayInputStream bios = (ByteArrayInputStream) file.getInputStream();
	        	upload.setInputStream(bios);
	        	upload.setFileName(file.getOriginalFilename());
	            upload.setContentType(file.getContentType());
	            list.add(upload);
	            assetBean.setDocumentDetail(list);
			}
        }catch(Exception e) {
        	e.printStackTrace();
        }*/
		//LOGGER.info("File Ends...");
		try {
			assetService.update(assetBean);
			LOGGER.info("Updated Successfully");
		    message = getMessageByStatus(assetBean,"update", true);
		}catch(Exception e) {
			message = getMessageByStatus(assetBean,"update", false);
			LOGGER.error("Error Occured : Updating asset. Error -> "+e.getMessage());
		}
		model.addAttribute("message", message);
		return "asset-success";
}
	
	
	//Search
	@PostMapping(value = "/searchview", params = "search")
	public String searchview( @ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
		List<AssetMaster> assetList = new ArrayList<AssetMaster>();
		try {
			//assetList = searchResult(assetBean);
			assetList = assetService.searchAssets(assetBean);
			model.addAttribute("assetList", assetList);
			departmentList = deptRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for searchview. Error -> "+e.getMessage());
		}
		model.addAttribute("assetBean", assetBean);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("viewmode", "view");
		
		String isRef = request.getParameter("isReference");
		if("true".equalsIgnoreCase(isRef)) {
			model.addAttribute("isReference", true);
		}else {
			model.addAttribute("isReference", false);
		}

		return "asset-view";
	}
	
	@PostMapping(value = "/searchedit")
	public String searchedit( @ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
		List<AssetMaster> assetList = new ArrayList<AssetMaster>();
		try {
			assetList = assetService.searchAssets(assetBean);
			model.addAttribute("assetList", assetList);
			departmentList = deptRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for searchedit. Error -> "+e.getMessage());
		}
		model.addAttribute("assetBean", assetBean);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("viewmode", "update");
		
		return "asset-modify";
	}
	
	@PostMapping(value = "/searchreport", params = "search")
	public String searchreport( @ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
		List<AssetMaster> assetList = new ArrayList<AssetMaster>();
		
		try {
			/*Long statusId = null;
			Long locationId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			if(null != assetBean.getAssetLocation()) {
				locationId = assetBean.getAssetLocation().getId();
			}
			assetList = masterRepo.getAssetMasterRegisterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					locationId, assetBean.getAssetHeader().getDescription(), statusId);
					*/
			assetList = assetService.searchAssets(assetBean);
			model.addAttribute("assetList", assetList);
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching SearchResult for report. Error -> "+e.getMessage());
		}
		try {
			localityList = localityRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for searchResult. Error -> "+e.getMessage());
		}
		model.addAttribute("assetBean", assetBean);
		model.addAttribute("localityList", localityList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("viewmode", "view");
		
		return "asset-register-report";
	}

	@Deprecated
	public List<AssetMaster> searchResult(AssetMaster assetBean){
		List<AssetMaster> assetList = new ArrayList<AssetMaster>();
		try {
			/*Long statusId = null;
			Long departmentId = null;
			if(null != assetBean.getAssetStatus()) {
				statusId = assetBean.getAssetStatus().getId();
			}
			if(null != assetBean.getAssetHeader().getDepartment()) {
				departmentId = assetBean.getAssetHeader().getDepartment().getId();
			}
			assetList = masterRepo.getAssetMasterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(), 
					departmentId, statusId);*/
			assetList = assetService.searchAssets(assetBean);
		} catch (Exception e) {
			e.getMessage();
		}
		return assetList;
	}
	
	private List<Object[]> getAssetDetails(AssetMaster assetBean) {
		
		String code = assetBean.getCode();
		String name = assetBean.getAssetHeader().getAssetName();
		String queryString = "from AssetMaster am where am.code=:code or am.assetHeader.assetName=:name";
				
		/*
		 * "select adk.detailname as detailkeyname,adt.name as detailtypename from accountdetailkey adk inner "
		 * +
		 * "join accountdetailtype adt on adk.detailtypeid=adt.id where adk.detailtypeid=:detailtypeid and adk.detailkey=:detailkey"
		 * ;
		 */
		
        SQLQuery sqlQuery = persistenceService.getSession().createSQLQuery(queryString);
        sqlQuery.setString("code", code);
        sqlQuery.setString("name", name);
        return sqlQuery.list();
    }
	
	 @RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	    public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response)
	            throws IOException {
	        final ServletContext context = request.getServletContext();
	        final String fileStoreId = request.getParameter("fileStoreId");
	        String fileName = "";
	        final File downloadFile = fileStoreService.fetch(fileStoreId, FinancialConstants.FILESTORE_MODULECODE);
	        final FileInputStream inputStream = new FileInputStream(downloadFile);
	        AssetMaster assetBean = assetService.getById(Long.parseLong(request.getParameter("assetId")));
	        assetBean = getBillDocuments(assetBean);
	        
	        for (final DocumentUpload doc : assetBean.getDocumentDetail())
	            if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
	                fileName = doc.getFileStore().getFileName();

	        // get MIME type of the file
	        String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
	        if (mimeType == null)
	            // set to binary type if MIME mapping not found
	            mimeType = "application/octet-stream";

	        // set content attributes for the response
	        response.setContentType(mimeType);
	        response.setContentLength((int) downloadFile.length());

	        // set headers for the response
	        final String headerKey = "Content-Disposition";
	        final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
	        response.setHeader(headerKey, headerValue);

	        // get output stream of the response
	        final OutputStream outStream = response.getOutputStream();

	        final byte[] buffer = new byte[BUFFER_SIZE];
	        int bytesRead = -1;

	        // write bytes read from the input stream into the output stream
	        while ((bytesRead = inputStream.read(buffer)) != -1)
	            outStream.write(buffer, 0, bytesRead);

	        inputStream.close();
	        outStream.close();
	    }
	 
	 private AssetMaster getBillDocuments(final AssetMaster assetBean) {
	        List<DocumentUpload> documentDetailsList = assetService.findByObjectIdAndObjectType(assetBean.getId(),
	                AssetConstant.FILESTORE_MODULEOBJECT_ASSET);
	        assetBean.setDocumentDetail(documentDetailsList);
	        return assetBean;
	    }

	@RequestMapping(value = "/deleteAssetDoc/{docid}", method = RequestMethod.GET)
	public @ResponseBody String deleteAssetDoc(@PathVariable("docid") String docid)
			throws ApplicationException, IOException {
		String deletefile = "delete from egf_documents where id=" + Long.valueOf(docid);
		final SQLQuery totalSQLQuery = persistenceService.getSession().createSQLQuery(deletefile.toString());

		int de = totalSQLQuery.executeUpdate();
		if (de == 1) {
			return "success";
		}
		return "fail";
	}
	 
	//View Only
	 @GetMapping("/assetReference/{assetBean}")
	 public String assetReference(@PathVariable("assetBean") AssetMaster assetBean, Model model) {
		LOGGER.info("assetReference Operation.................."+assetBean);
		model.addAttribute("assetBean", assetBean);
		try {
			fundList = fundHibernateDAO.findAllActiveFunds();
			departmentList = deptRepo.findAll(); //microserviceUtils.getDepartments();
			modeOfAcquisitionList = acqRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			functionList = functionDAO.getAllActiveFunctions();
			
			localityList = localityRepo.findAll();
			blockList = blockRepo.findAll();
			electionWardList = electionWardRepo.findAll();
			revenueWardList = revenueWardRepo.findAll();
			streetList = streetRepo.findAll();
			zoneList = zoneRepo.findAll();
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching details for asset Reference. Error -> "+e.getMessage());
		}
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("fundList", fundList);
		model.addAttribute("modeOfAcquisitionList", modeOfAcquisitionList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("functionList", functionList);
		model.addAttribute("schemeList", schemeList);
		model.addAttribute("subSchemeList", subSchemeList);
		 
		model.addAttribute("localityList", localityList);
		model.addAttribute("blockList", blockList);
		model.addAttribute("electionWardList", electionWardList);
		model.addAttribute("revenueWardList", revenueWardList);
		model.addAttribute("streetList", streetList);
		model.addAttribute("zoneList", zoneList);
		model.addAttribute("mode", "view-only");
		model.addAttribute("disabled", "disabled");
		return "asset-view";
	}
	 
	@RequestMapping(value = "/categorydetails/{categoryid}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String categoryDetails(@PathVariable("categoryid") String assetCategoryId, Model model) {
		LOGGER.info("Category Details.................."+assetCategoryId);
		AssetCatagory assetCategory = new AssetCatagory();
		List<CustomeFields> customeFields=new ArrayList<>();
		double depRate = 0.0;
		try {
			assetCategory = categoryRepo.findOne(Long.valueOf(assetCategoryId));
			depRate = assetCategory.getDepriciationRate();
			customeFields = assetCategory.getCustomeFields();
		}catch(Exception e) {
			LOGGER.error("Error Occured : While fetching default CategoryDetails. Error -> "+e.getMessage());
		}
		String retVal = "";
		try {
			retVal = new StringBuilder("{ \"data\":")
	                .append(toSearchResultJson(customeFields))
	                .append(",\"depRate\":"+depRate)
	                .append("}")
	                .toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
        return retVal;
	}
	
	 public Object toSearchResultJson(final Object object) {
	        final GsonBuilder gsonBuilder = new GsonBuilder();
	        final Gson gson = gsonBuilder.registerTypeAdapter(CustomeFields.class, new AssetJsonAdaptor()).create();
	        return gson.toJson(object);
	 }
	 
	@GetMapping("/fetchdetails")
	@ResponseBody
	public String fetchDetails(HttpServletRequest request) {
		LOGGER.info("Fetching Details..................");
		String statusId = request.getParameter("status");
		String modeId = request.getParameter("mode");
		AssetStatus status = new AssetStatus();
		AssetModeOfAcquisition modeAcq = new AssetModeOfAcquisition();
		JSONObject obj=new JSONObject();
		try {
			status = statusRepo.findOne(Long.valueOf(statusId));
			modeAcq = acqRepo.findOne(Long.valueOf(modeId));
		}catch(Exception e) {
			e.printStackTrace();
		}
		obj.put("status",status.getCode());
		obj.put("mode",modeAcq.getCode());
		String retVal = "";
		try {
			retVal = new StringBuilder("{ \"data\":")
	                .append(obj).append("}")
	                .toString();
		}catch(Exception e) {
			LOGGER.error("Error Occured : While fetching details w.r.t status. Error -> "+e.getMessage());
		}
        return obj.toString();
	}
		
	@PostMapping(value = "/searchregister")
	public String searchRegister(Model model) {
		LOGGER.info("Search Register Report Operation..................");
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		
		try {
			localityList = localityRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for SearchRegister. Error -> "+e.getMessage());
		}
		model.addAttribute("localityList", localityList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("isViewPage", true);
		
		return "asset-register-report";
	}
	
	@PostMapping("/searchform/{param}")
	public String searchform(@PathVariable("param") String param, Model model) {
		LOGGER.info("Search Form..................");
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		try {
			localityList = localityRepo.findAll();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			
			//assetList = masterRepo.findAll();
		} catch (Exception e) {
			LOGGER.error("Error Occured : While fetching default dropdown values for modifyform. Error -> "+e.getMessage());
		}
		//model.addAttribute("assetList", assetList);
		model.addAttribute("localityList", localityList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("localityList", localityList);
		model.addAttribute("mode", "add");
		model.addAttribute("disabled", "");
		if(param.equalsIgnoreCase("ref")) {
			model.addAttribute("isReference", true);
		}else {
			model.addAttribute("isReference", false);
		}
		model.addAttribute("isViewPage", true);
		return "asset-register-report";
	}
		
	@PostMapping("/modifyform")
	public String modifyform(Model model) {
		LOGGER.info("View Form..................");
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		try {
			departmentList = deptRepo.findAll(); //microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
			
			//assetList = masterRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		//model.addAttribute("assetList", assetList);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		model.addAttribute("localityList", localityList);
		model.addAttribute("mode", "update");
		model.addAttribute("viewmode", "update");
		model.addAttribute("disabled", "");
		model.addAttribute("isViewPage", true);
		
		return "asset-modify";
		}
}
