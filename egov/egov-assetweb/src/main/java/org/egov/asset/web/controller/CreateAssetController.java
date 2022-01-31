package org.egov.asset.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
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
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping(value = "/assetcreate")
public class CreateAssetController {// extends BaseAssetController{

	/*
	 * public CreateAssetController(AppConfigValueService appConfigValuesService) {
	 * super(appConfigValuesService); }
	 */

	private static final Logger LOGGER = Logger.getLogger(CreateAssetController.class);
	private static final int BUFFER_SIZE = 4096;
	//private static final long serialVersionUID = 1L;
	protected String showMode;

	private AssetMaster assetBean;
	//private AssetHeader assetHeaderBean;
	//private AssetLocation assetLocationBean;
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
	List<Department> departmentList = new ArrayList<Department>();
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
	
	@GetMapping("/newform")
	public String newform(Model model) {
		LOGGER.info("Fresh Operation..................");

		assetBean = new AssetMaster();
		assetBean.setAssetHeader(new AssetHeader());
		assetBean.setAssetLocation(new AssetLocation());
		model.addAttribute("assetBean", assetBean);
		//model.addAttribute("assetHeaderBean", assetHeaderBean);
		//model.addAttribute("assetLocationBean", assetLocationBean);
		
		try {
			fundList = fundHibernateDAO.findAllActiveFunds();
			departmentList = microserviceUtils.getDepartments();
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
			LOGGER.info("Fund Id...:"+fundId);
			
			schemeList = schemeService.getByFundId(fundId);
		}catch(Exception e) {
			e.printStackTrace();
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

		return "asset-create";
	}

	@PostMapping(value = "/create", params = "create")
	public String create(@ModelAttribute("assetBean") AssetMaster assetBean,Model model, HttpServletRequest request) {

		LOGGER.info("Creating Asset Object");
		long userId = ApplicationThreadLocals.getUserId();
		LOGGER.info("userId..." + userId);
		LOGGER.info("Asset Header..." + assetBean.toString());
		LOGGER.info("Asset Header..." + assetBean.getAssetHeader().toString());
		//File
		List<DocumentUpload> list = new ArrayList<>();
		try {
			String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
	        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
	        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
	        if(uploadedFiles!=null)
	        for (int i = 0; i < uploadedFiles.length; i++) {

	            Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
	            byte[] fileBytes = Files.readAllBytes(path);
	            ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
	            DocumentUpload upload = new DocumentUpload();
	            upload.setInputStream(bios);
	            upload.setFileName(fileName[i]);
	            upload.setContentType(contentType[i]);
	            list.add(upload);
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		assetBean.setDocumentDetail(list);
		//assetBean.setFileno();
        
		
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
			LOGGER.info("Custom Fields.."+customeFields);
			if(!customeFields.isEmpty()) {
				for(int i=0; i<customeFields.size(); i++) {
					item = new AssetCustomFieldMapper();
					String name = customeFields.get(i).getName();
					item.setName(name);
					String values = request.getParameter("customField_"+i);
					LOGGER.info(name+"...Value..."+values);
					item.setVal(values);
					item.setCreatedDate(new Date());
					item.setCreatedBy(String.valueOf(userId));
					item.setAssetCatagory(assetBean.getAssetHeader().getAssetCategory());
					item.setCustomeFields(customeFields.get(i));
					
					//assetBean = masterRepo.findOne(Long.valueOf(nextVal));
					//item.setAssetMaster(assetBean);
					//customFieldRepo.save(item);
					mapperList.add(item);
				}
				assetBean.setAssetCustomFieldMappers(mapperList);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Custom Fields Ends...");
		try {
			final StringBuilder generatedCode = new StringBuilder(String.format("%06d", nextVal));
			assetCode = generatedCode.toString();
			assetBean.setCode(assetCode);
			assetBean = assetService.create(assetBean);
			LOGGER.info("Created Successfully");
		    message = getMessageByStatus(assetBean,"create", true);
		}catch(Exception e) {
			LOGGER.error("Error Occured : Asset Creation Failed");
			message = getMessageByStatus(assetBean,"create", false);
			e.printStackTrace();
		}
		model.addAttribute("message", message);
		
		/*try {
			LOGGER.info("Custom Fields Starts...");
			String customFieldsCounts = request.getParameter("customFieldsCounts");
			LOGGER.info("CustomFields Counts ..:"+customFieldsCounts);
			String customName1 = request.getParameter("customField_0_name");//customField_0_name
			LOGGER.info("CustomFields Name ..:"+customName1);*/
			
			
		/*}catch(Exception e) {
			e.printStackTrace();
		}*/
		
		return "asset-success";
	}
	
	public Long generateNextVal() {
		//String code = "";
		long id = 0;
		try {
			id = masterRepo.getNextValMySequence();
			//final StringBuilder generatedCode = new StringBuilder(String.format("%06d", id));
			//code = generatedCode.toString();
			//code = String.valueOf(id);
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
	/*
	 * @GetMapping("/editform/{assetid}") public String
	 * editform(@PathVariable("assetid") String assetId, Model model) {
	 */
	
	@PostMapping("/viewform/{param}")
	public String viewform(@PathVariable("param") String param, Model model) {
		LOGGER.info("View Form..................");
		assetBean = new AssetMaster();
		model.addAttribute("assetBean", assetBean);
		try {
			departmentList = microserviceUtils.getDepartments();
			assetStatusList = statusRepo.findAll();
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
		model.addAttribute("localityList", localityList);
		model.addAttribute("mode", "add");
		model.addAttribute("disabled", "");
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
			assetList = masterRepo.getAssetMasterDetails(assetBean.getCode(), 
					assetBean.getAssetHeader().getAssetName(),
					assetBean.getAssetHeader().getAssetCategory().getId(),
					assetBean.getAssetHeader().getDepartment(),
					assetBean.getAssetStatus().getId());
			LOGGER.info("Asset Lists..."+assetList.toString());
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
		LOGGER.info("Edit Operation.................."+assetId);
	
		assetBean = new AssetMaster();
		assetBean = masterRepo.findOne(Long.valueOf(assetId));
		try {
			//final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(assetId));
			List<DocumentUpload> documents = assetService.findByObjectIdAndObjectType(assetBean.getId(),
	                AssetConstant.FILESTORE_MODULEOBJECT_ASSET);
	        assetBean.setDocumentDetail(documents);
	        LOGGER.info(assetBean.getDocumentDetail().get(0).getFileName());
	        LOGGER.info(assetBean.getDocumentDetail().get(0).getFileStore().getFileName());
		}catch(Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("assetBean", assetBean);
		
		mapperList = assetBean.getAssetCustomFieldMappers();
		try {
			/*Fund fund = fundHibernateDAO.fundById(Integer.parseInt(assetBean.getAssetHeader().getFund()), true);
			fundList.add(fund);
			
			Department department = microserviceUtils.getDepartmentByCode(assetBean.getAssetHeader().getDepartment());
			departmentList.add(department);
			
			CFunction function = functionDAO.getFunctionById(Long.parseLong(assetBean.getAssetHeader().getFunction()));
			functionList.add(function);
			
			AssetStatus assetStatus = statusRepo.getOne(assetBean.getAssetStatus().getId());
			assetStatusList.add(assetStatus);
			
			AssetCategory assetCategory = categoryRepo.getOne(Long.parseLong(assetBean.getAssetHeader().getAssetCategory()));
			assetCategoryList.add(assetCategory);
			
			AssetLocality locality = localityRepo.getOne(Long.parseLong(assetBean.getAssetLocation().getLocation()));
			localityList.add(locality);
			
			blockList = blockRepo.findAll();
			electionWardList = electionWardRepo.findAll();
			revenueWardList = revenueWardRepo.findAll();
			streetList = streetRepo.findAll();
			zoneList = zoneRepo.findAll();*/
			
			//AssetModeOfAcquisition modeOfAcquisition = acqRepo.getOne(Long.parseLong(assetBean.getAssetHeader().getModeOfAcquisition()));
			//modeOfAcquisitionList.add(modeOfAcquisition);
			//modeOfAcquisitionList = acqRepo.findAll();
			
			fundList = fundHibernateDAO.findAllActiveFunds();
			departmentList = microserviceUtils.getDepartments();
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
			LOGGER.info("Fund Id...:"+fundId);
			int schemeId = 0;
			if(!assetBean.getAssetHeader().getScheme().equalsIgnoreCase(null) || assetBean.getAssetHeader().getScheme() != null){
				schemeId = Integer.parseInt(assetBean.getAssetHeader().getScheme());
			}
			LOGGER.info("Scheme Id...:"+schemeId);
			schemeList = schemeService.getByFundId(fundId);
			subSchemeList = subSchemeService.getBySchemeId(schemeId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String updateParam = "update";
		try {
			updateParam = request.getParameter("viewmode");
		}catch(Exception e) {
			e.printStackTrace();
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
		model.addAttribute("mode", updateParam);
		model.addAttribute("disabled", "disabled");
		model.addAttribute("mapperList", mapperList);
		
		return "asset-update";
	}
	
	
	@PostMapping(value = "/update", params = "update")
	public String update(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {

		LOGGER.info("Creating Asset Object");
		LOGGER.info("Asset Header..." + assetBean.toString());
		LOGGER.info("Asset Header..." + assetBean.getId());
		String message = "";
		
		long userId = ApplicationThreadLocals.getUserId();
		LOGGER.info("userId..." + userId);
		assetBean.setUpdatedBy(String.valueOf(userId));
		//assetBean.getAssetHeader().setUpdatedBy(String.valueOf(userId));
		assetBean.getAssetLocation().setUpdatedBy(String.valueOf(userId));
		
		assetBean.setUpdatedDate(new Date());
		assetBean.getAssetHeader().setUpdatedDate(new Date());
		assetBean.getAssetLocation().setUpdatedDate(new Date());
		
		String assetid = request.getParameter("id");
		LOGGER.info("ID ..:"+assetid);
		String assetHeaderId = request.getParameter("assetHeaderId");
		LOGGER.info("assetHeaderId ..:"+assetHeaderId);
		String assetLocationId = request.getParameter("assetLocationId");
		LOGGER.info("assetLocationId ..:"+assetLocationId);
		
		assetBean.setId(Long.parseLong(assetid));
		//Custom Fields
		List<AssetCustomFieldMapper> mapperList = new ArrayList<AssetCustomFieldMapper>();
		mapperList = assetBean.getAssetCustomFieldMappers();
		AssetCustomFieldMapper item = new AssetCustomFieldMapper();
		try {
			List<CustomeFields> customeFields = assetBean.getAssetHeader().getAssetCategory().getCustomeFields();
			LOGGER.info("Custom Fields.."+customeFields);
			if(!customeFields.isEmpty()) {
				for(int i=0; i<customeFields.size(); i++) {
					String name = customeFields.get(i).getName();
					String values = request.getParameter("customField_"+i);
					LOGGER.info(name+"...Value..."+values);
					LOGGER.info(request.getParameter("customField_0"));
					String customFieldId = request.getParameter("customField_id_"+i);
					LOGGER.info("CustomFieldId..."+customFieldId);
					Long id = Long.valueOf(customFieldId);
					item = customFieldRepo.findOne(id);
					item.setVal(values);
					item.setUpdatedDate(new Date());
					item.setUpdatedBy(String.valueOf(userId));
					//assetBean.getAssetCustomFieldMappers()
					//customFieldRepo.save(item);
					/*item = new AssetCustomFieldMapper();
					for(AssetCustomFieldMapper obj : mapperList) {
						if(obj.getId() == id) {
							item = obj;
						}
					}
					item.setVal(values);
					
					//item.set
					mapperList.add(item);*/
					mapperList.add(item);
				}
				assetBean.getAssetCustomFieldMappers().clear();
				assetBean.setAssetCustomFieldMappers(mapperList);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Custom Fields Ends...");
		//File
		LOGGER.info("File Starts...");
		List<DocumentUpload> list = new ArrayList<>();
		try {
			LOGGER.debug(assetBean.getFileno());
			String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
	        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
	        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
	        if(uploadedFiles!=null) {
		        for (int i = 0; i < uploadedFiles.length; i++) {
	
		            Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
		            byte[] fileBytes = Files.readAllBytes(path);
		            ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
		            DocumentUpload upload = new DocumentUpload();
		            upload.setInputStream(bios);
		            upload.setFileName(fileName[i]);
		            upload.setContentType(contentType[i]);
		            list.add(upload);
		        }
		    	assetBean.setDocumentDetail(list);
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("File Ends...");
		try {
			/*String id = request.getParameter("id");
			LOGGER.info("ID ..:"+id);
			String assetHeaderId = request.getParameter("assetHeaderId");
			LOGGER.info("assetHeaderId ..:"+assetHeaderId);
			String assetLocationId = request.getParameter("assetLocationId");
			LOGGER.info("assetLocationId ..:"+assetLocationId);
			
			assetBean.setId(Long.parseLong(id));*/
			//assetBean.getAssetHeader().setId(Long.parseLong(assetHeaderId));
			//assetBean.getAssetLocation().setId(Long.parseLong(assetLocationId));
			
			assetService.update(assetBean);
			LOGGER.info("Updated Successfully");
		    message = getMessageByStatus(assetBean,"update", true);
	        
		}catch(Exception e) {
			LOGGER.error("Error Occured : Asset Creation Failed");
			message = getMessageByStatus(assetBean,"update", false);
			e.printStackTrace();
		}
		model.addAttribute("message", message);
		return "asset-success";
}
	
	
	//Search
	/*
	 * @GetMapping("/search/{assetid}") public String
	 * search(@PathVariable("assetid") String assetId, Model model) {
	 */
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
			assetStatusList = statusRepo.findAll();
			assetCategoryList = categoryRepo.findAll();
		} catch (Exception e) {
			e.getMessage();
		}
		model.addAttribute("assetBean", assetBean);
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("assetStatusList", assetStatusList);
		model.addAttribute("assetCategoryList", assetCategoryList);
		
		return "asset-view";
	}
	
	private List<Object[]> getAssetDetails(AssetMaster assetBean) {
		
		String code = assetBean.getCode();
		String name = assetBean.getAssetHeader().getAssetName();
		//String category = assetBean.getAssetHeader().getAssetCategory();
		//from AssetMaster am where am.code=:code or am.assetHeader.assetCategory=:cat
		String queryString = "from AssetMaster am where am.code=:code or am.assetHeader.assetName=:name";
				
		/*
		 * "select adk.detailname as detailkeyname,adt.name as detailtypename from accountdetailkey adk inner "
		 * +
		 * "join accountdetailtype adt on adk.detailtypeid=adt.id where adk.detailtypeid=:detailtypeid and adk.detailkey=:detailkey"
		 * ;
		 */
		
		LOGGER.info("Query String..:"+queryString);
        SQLQuery sqlQuery = persistenceService.getSession().createSQLQuery(queryString);
        sqlQuery.setString("code", code);
        sqlQuery.setString("name", name);
        return sqlQuery.list();
    }
	
	/*
	 * 
		@GetMapping("/editform/{assetid}")
		public String editform(@PathVariable("assetid") String assetId, Model model) {
	 */
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
	        
	        // final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
	        // assetBean.setDocumentDetail(documents);
	        
	        
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
		LOGGER.info("::::::::  :::::: " + docid);
		String deletefile = "delete from egf_documents where id=" + Long.valueOf(docid);
		final SQLQuery totalSQLQuery = persistenceService.getSession().createSQLQuery(deletefile.toString());

		LOGGER.info(":::query: " + deletefile);
		int de = totalSQLQuery.executeUpdate();
		LOGGER.info(":::::::::Delete files:::" + de);
		if (de == 1) {
			return "success";
		}
		return "fail";
	}
	 
	//View Only
	 @GetMapping("/assetReference/{assetBean}")
	 public String assetReference(@PathVariable("assetBean") AssetMaster assetBean, Model model) {
	/* @PostMapping(value = "/assetReference", params = "search")
     public String assetReference(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {*/
			LOGGER.info("assetReference Operation.................."+assetBean);
		
		/*
		  assetBean = new AssetMaster(); assetBean =
		  masterRepo.findOne(Long.valueOf(assetId));
		 */
			model.addAttribute("assetBean", assetBean);
			try {
				fundList = fundHibernateDAO.findAllActiveFunds();
				departmentList = microserviceUtils.getDepartments();
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
		/*
		 * try { int fundId = fundList.get(0).getId();
		 * LOGGER.info("Fund Id...:"+fundId); int schemeId = 0;
		 * if(!assetBean.getAssetHeader().getScheme().equalsIgnoreCase(null) ||
		 * assetBean.getAssetHeader().getScheme() != null){ schemeId =
		 * Integer.parseInt(assetBean.getAssetHeader().getScheme()); }
		 * LOGGER.info("Scheme Id...:"+schemeId); schemeList =
		 * schemeService.getByFundId(fundId); subSchemeList =
		 * subSchemeService.getBySchemeId(schemeId); }catch(Exception e) {
		 * e.printStackTrace(); }
		 */
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
	 
	 public AssetMaster bindValueObject(AssetMaster assetBean){
		 // Generate Current Value
		 /*final AssetCurrentValue currentValue = AssetCurrentValue.builder().assetId(assetBean.getId()).assetTranType(TransactionType.CREATE).build();

        final Long grossValue = asset.getGrossValue();
        final Long accumulatedDepreciation = asset.getAccumulatedDepreciation();

        if (grossValue != null && accumulatedDepreciation != null)
            currentValue.setCurrentAmount(grossValue.subtract(accumulatedDepreciation));
        else if (grossValue != null)
            currentValue.setCurrentAmount(grossValue);

        if (grossValue != null || accumulatedDepreciation != null) {
            final List<AssetCurrentValue> assetCurrentValueList = new ArrayList<>();
            assetCurrentValueList.add(currentValue);
            currentValueService.createCurrentValue(AssetCurrentValueRequest.builder()
                    .assetCurrentValues(assetCurrentValueList).requestInfo(assetRequest.getRequestInfo()).build());
        }*/
	   return assetBean;	
	}
	 
	//@GetMapping("/categorydetails/{categoryid}")
	@RequestMapping(value = "/categorydetails/{categoryid}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String categoryDetails(@PathVariable("categoryid") String assetCategoryId, Model model) {
		LOGGER.info("Category Details.................."+assetCategoryId);
		AssetCatagory assetCategory = new AssetCatagory();
		List<CustomeFields> customeFields=new ArrayList<>();
		
		try {
			assetCategory = categoryRepo.findOne(Long.valueOf(assetCategoryId));
			customeFields = assetCategory.getCustomeFields();
			LOGGER.info("Custom Fields.."+customeFields);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*ObjectMapper mapper = new ObjectMapper();
		String response = "";
		try {
			response = mapper.writeValueAsString(customeFields);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("Mapoper.........."+response);*/
		//model.addAttribute("customeFields", customeFields);
		//final List<Bank> searchResultList = createBankService.search(bank);
		String retVal = "";
		try {
			retVal = new StringBuilder("{ \"data\":")
	                .append(toSearchResultJson(customeFields)).append("}")
	                .toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Return Value..."+retVal);
        return retVal;
		//return customeFields;
	}
	
	 public Object toSearchResultJson(final Object object) {
	        final GsonBuilder gsonBuilder = new GsonBuilder();
	        final Gson gson = gsonBuilder.registerTypeAdapter(CustomeFields.class, new AssetJsonAdaptor()).create();
	        return gson.toJson(object);
	 }
	 
		//@RequestMapping(value = "/fetchdetails", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
		@GetMapping("/fetchdetails")
		@ResponseBody
		public String fetchDetails(HttpServletRequest request) {
			LOGGER.info("Fetching Details..................");
			String statusId = request.getParameter("status");
			String modeId = request.getParameter("mode");
			LOGGER.info("Fetching Details.................."+statusId+"..."+modeId);
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
				e.printStackTrace();
			}
			LOGGER.info("Return Value..."+retVal);
	        return obj.toString();
			//return customeFields;
		}
		
		@GetMapping("/test")
		public String test(Model model) {
			LOGGER.info("Fresh Operation..................");
			
			return "asset-create";
			
		}
		
		@PostMapping(value = "/searchregister", params = "search")
		public String searchRegister(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
			LOGGER.info("Search Register Report Operation..................");
			assetList = new ArrayList<>();
			try {
				Long statusId = null;
				if(null != assetBean.getAssetStatus()) {
					statusId = assetBean.getAssetStatus().getId();
				}
				assetList = masterRepo.getAssetMasterRegisterDetails(assetBean.getCode(), 
						assetBean.getAssetHeader().getAssetName(),
						assetBean.getAssetHeader().getAssetCategory().getId(), 
						assetBean.getAssetLocation().getId(), assetBean.getAssetHeader().getDescription(), statusId);
				LOGGER.info("Asset Lists..."+assetList.toString());
			} catch (Exception e) {
				e.getMessage();
			}
			model.addAttribute("assetList", assetList);
			try {
				localityList = localityRepo.findAll();
				assetStatusList = statusRepo.findAll();
				assetCategoryList = categoryRepo.findAll();
			} catch (Exception e) {
				e.getMessage();
			}
			model.addAttribute("assetBean", assetBean);
			model.addAttribute("localityList", localityList);
			model.addAttribute("assetStatusList", assetStatusList);
			model.addAttribute("assetCategoryList", assetCategoryList);
			
			return "asset-view";
		}
	
}
