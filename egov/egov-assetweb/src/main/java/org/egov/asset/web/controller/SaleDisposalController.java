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
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.Disposal;
import org.egov.asset.service.DisposalService;
import org.egov.asset.service.DisposalValidator;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.model.bills.DocumentUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/sale")
public class SaleDisposalController {

	
	private DisposalValidator disposalValidator;
	@Autowired
	private DisposalService disposalService;
	@Autowired
    private FileStoreService fileStoreService;
	public static final String FILESTORE_MODULECODE = "EGF";
	final private static String FILESTORE_MODULEOBJECT="disposal";
	private static final int BUFFER_SIZE = 4096;
	
	
	
	@ModelAttribute
	public void addDropDownValuesToModel(Model model) {
		//model.addAttribute("assetList", disposalService.getAssets());
		model.addAttribute("departmentList", disposalService.getDepartments());
		model.addAttribute("assetStatusList", disposalService.getAssetStatus());
		model.addAttribute("assetCategoryList", disposalService.getAssetCategories());
		model.addAttribute("mode", "add");
		model.addAttribute("disabled", "");
	}
	@GetMapping("/createDisposal/{assetId}")
	public String loadDisposalForm(@PathVariable Long assetId,Model model) {
		Disposal disposal=new Disposal();
		AssetMaster asset=disposalService.getAssetById(assetId);
		disposal.setAsset(asset);
		model.addAttribute("disposal",disposal);
		model.addAttribute("assetAccounts", disposalService.getAssetAccount());
		return "disposal-sale-form";
	}
	@PostMapping("/createDisposal")
	public String createDisposal(@ModelAttribute("disposal") Disposal disposal,Model model,final HttpServletRequest request )throws IOException {
		//disposalValidator.validateDisposal(disposal);
		
		String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        List<DocumentUpload> list = new ArrayList<>();
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
        System.out.println(list);
        Long createdBy=ApplicationThreadLocals.getUserId();
        disposal.setCreatedBy(createdBy);
        disposal.setDocuments(list);
			disposalService.createDisposal(disposal);
		//Disposal disposalResponse = disposalService.saveDisposal(disposal);
		return "disposal-sale-form";
	}
	 @RequestMapping(value = "/downloadSaleDisposalDoc", method = RequestMethod.GET)
	    public void getSaleDisposalDoc(final HttpServletRequest request, final HttpServletResponse response)
	            throws IOException {
	        final ServletContext context = request.getServletContext();
	        final String fileStoreId = request.getParameter("fileStoreId");
	        String fileName = "";
	        final File downloadFile = fileStoreService.fetch(fileStoreId,FILESTORE_MODULECODE);
	        final FileInputStream inputStream = new FileInputStream(downloadFile);
	        Disposal disposal = disposalService.getById(Long.parseLong(request.getParameter("disposalId")));
	        disposal = getBillDocuments(disposal);

	        for (final DocumentUpload doc : disposal.getDocuments())
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
	 	private Disposal getBillDocuments(final Disposal disposal) {
	        List<DocumentUpload> documentDetailsList = disposalService.findByObjectIdAndObjectType(disposal.getId(),
	                FILESTORE_MODULEOBJECT);
	        disposal.setDocuments(documentDetailsList);
	        return disposal;
	    }
	 	@GetMapping("view/{id}")
	 	public String viewSalaDisposal(@PathVariable("id") Long disposalId,Model model) {
	 		Disposal disposal = disposalService.getById(disposalId);
	 		Disposal disposalResponse = getBillDocuments(disposal);
	 		model.addAttribute("disposal", disposalResponse);
	 		model.addAttribute("assetAccounts", disposalService.getAssetAccount());
	 		return "view-sale-disposal";
	 	}
	 	/*@GetMapping("search")
	 	public String searchSalaDisposal(Model model) {
	 		model.addAttribute("assetBean", new AssetMaster());
	 		return "search-asset-for-sale-disposal";
	 	}*/
	 	@GetMapping("/search")
		public String viewform(Model model) {
			
	 		AssetMaster assetBean = new AssetMaster();
			model.addAttribute("assetBean", assetBean);
			
			return "search-asset-for-sale-disposal";
		}
	 	@PostMapping(value = "/search", params = "search")
		public String search(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
			List<AssetMaster> assetList = disposalService.getAssetMasterDetails(assetBean);
			model.addAttribute("assetList", assetList);
			model.addAttribute("assetBean", assetBean);
			return "search-asset-for-sale-disposal";
		}
	 	@PostMapping(value = "/searchsaledisposal", params = "search")
		public String searchSaleDisposal(@ModelAttribute("assetBean") AssetMaster assetBean, Model model, HttpServletRequest request) {
			List<Disposal> assetList = disposalService.getFromDisposal(assetBean);
			model.addAttribute("assetList", assetList);
			model.addAttribute("assetBean", assetBean);
			return "search-sale-disposal";
		}
	 	@GetMapping("/searchsaledisposal")
		public String searchSaleDisposalform(Model model) {
			
	 		AssetMaster assetBean = new AssetMaster();
			model.addAttribute("assetBean", assetBean);
			return "search-sale-disposal";
		}
		
}