package org.egov.infra.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TaskController {

	@PostMapping("/task/{module}")
	public String showInbox(@PathVariable("module") final String module, final Model model){
		model.addAttribute("moduleName", module);
		return "inbox-view";
	}
}
