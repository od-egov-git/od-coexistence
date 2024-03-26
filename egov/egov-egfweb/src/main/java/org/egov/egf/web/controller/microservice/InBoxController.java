package org.egov.egf.web.controller.microservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InBoxController {

	@RequestMapping(value="/inbox", method = { RequestMethod.POST, RequestMethod.GET })
	public String showInbox(){
		return "inbox-view";
	}
}
