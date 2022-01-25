package org.egov.egf.web.controller.expensebill;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.egf.contract.model.Disposal;
import org.egov.egf.expensebill.service.DisposalService;
import org.egov.egf.expensebill.service.DisposalValidator;
import org.egov.model.bills.DocumentUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sale")
public class SaleDisposalController {

	
	private DisposalValidator disposalValidator;
	private DisposalService disposalService;
	
	@GetMapping("/createDisposal")
	public String loadDisposalForm(Model model) {
		model.addAttribute("disposal", new Disposal());
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
		
		//Disposal disposalResponse = disposalService.saveDisposal(disposal);
		return "disposal-sale-form";
	}
}
