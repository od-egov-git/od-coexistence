package org.egov.asset.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/revaluate")
public class RevaluationController {

	@RequestMapping(value = "/newform", method = RequestMethod.GET)
	public String showNewForm(final Model model, HttpServletRequest request) {
		System.out.println("XXXX");

		return "test";

	}

}
