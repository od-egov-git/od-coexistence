/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.eis.web.controller.workflow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
//import org.egov.eis.entity.Assignment;
//import org.egov.eis.entity.AssignmentAdaptor;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.web.support.json.adapter.AssignmentAdaptor;
import org.egov.infra.workflow.matrix.entity.WorkFlowDeptDesgMap;
import org.egov.infra.workflow.matrix.service.CustomizedWorkFlowService;
import org.egov.infra.workflow.matrix.service.WorkFlowDeptDesgMapService;
import org.egov.pims.commons.Designation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AjaxWorkFlowController {

	private static final List<String> WF_DEPT_DESG_MAP = Arrays.asList("CouncilPreamble","MeetingMOM","CouncilMeeting","ApnimandiContractor","ApnimandiCollectionDetails");
	
    @Autowired
    private CustomizedWorkFlowService customizedWorkFlowService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    @Autowired
    private WorkFlowDeptDesgMapService workFlowDeptDesgMapService;
    
    @RequestMapping(value = "/ajaxWorkFlow-getDesignationsByObjectType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Designation> getDesignationsByObjectType(
            @ModelAttribute("designations") @RequestParam final String departmentRule, @RequestParam final String currentState,
            @RequestParam final String type,
            @RequestParam final String amountRule, @RequestParam final String additionalRule,
            @RequestParam final String pendingAction, @RequestParam final String approvalDepartment) {

    	List<Designation> designationList = new ArrayList<Designation>();
    	
    	if(WF_DEPT_DESG_MAP.contains(type)) {
    		List<WorkFlowDeptDesgMap> deptDesgMap = null;
    		
    		if(!StringUtils.isBlank(additionalRule)) {
    			deptDesgMap = workFlowDeptDesgMapService.findByObjectTypeAndCurrentStateAndAddRuleAndNextDept
    					(type, currentState, additionalRule, approvalDepartment);
    		}else {
    			deptDesgMap = workFlowDeptDesgMapService.findByObjectTypeAndCurrentStateAndNextDept
    					(type, currentState, approvalDepartment);
    		}
    		
    		if(!CollectionUtils.isEmpty(deptDesgMap)) {
				String desigCodes = deptDesgMap.stream().map(WorkFlowDeptDesgMap::getNextDesignation).collect(Collectors.joining(","));
				List<org.egov.infra.microservice.models.Designation> tempDesignationList = microserviceUtils.getDesignation(desigCodes);
				if(!CollectionUtils.isEmpty(tempDesignationList)) {
					Designation desig = null;
					for(org.egov.infra.microservice.models.Designation tempDesig : tempDesignationList) {
						desig = new Designation();
						desig.setName(tempDesig.getName());
						desig.setCode(tempDesig.getCode());
						designationList.add(desig);
					}
				}
			}
    	}else {
    		designationList = designationService.getDesignationsByNames(
                    customizedWorkFlowService.getNextDesignations(type,
                            departmentRule, null, additionalRule, currentState,
                            pendingAction, new Date()));
    	}
		
        //if (designationList.isEmpty())
          //  designationList = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
        return designationList;

    }

    @RequestMapping(value = "/ajaxWorkFlow-getDesignationsForActiveAssignmentsByObjectType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Designation> getDesignationsForActiveAssignmentsByObjectType(
            @ModelAttribute("designations") @RequestParam final String departmentRule, @RequestParam final String currentState,
            @RequestParam final String type,
            @RequestParam final String amountRule, @RequestParam final String additionalRule,
            @RequestParam final String pendingAction, @RequestParam final String approvalDepartment) {
    	System.out.println("type : "+type);
        List<Designation> designationList = assignmentService
                .getDesignationsByActiveAssignmentAndDesignationNames(
                        customizedWorkFlowService.getNextDesignationsForActiveAssignments(type,
                                departmentRule, null, additionalRule, currentState,
                                pendingAction, new Date()));
        if (designationList.isEmpty())
        {
        	System.out.println("EMPTY DESIG");
        }
            //designationList = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
        return designationList;

    }

    @RequestMapping(value = "/ajaxWorkFlow-getDesignationsForActiveAssignmentsByObjectTypeAndDesignation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Designation> getDesignationsForActiveAssignmentsByObjectTypeAndDesignation(
            @ModelAttribute("designations") @RequestParam final String departmentRule, @RequestParam final String currentState,
            @RequestParam final String type,
            @RequestParam final String amountRule, @RequestParam final String additionalRule,
            @RequestParam final String pendingAction, @RequestParam final Long approvalDepartment,
            @RequestParam final String currentDesignation) {

        List<Designation> designationList = assignmentService
                .getDesignationsByActiveAssignmentAndDesignationNames(customizedWorkFlowService.getNextDesignationsForActiveAssignments(type,
                        departmentRule, null, additionalRule, currentState,
                        pendingAction, new Date(), currentDesignation));
        if (designationList.isEmpty())
            designationList = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
        return designationList;

    }

    @RequestMapping(value = "/ajaxWorkFlow-getDesignationsByObjectTypeAndDesignation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Designation> getDesignationsByObjectTypeAndDesignation(
            @ModelAttribute("designations") @RequestParam final String departmentRule, @RequestParam final String currentState,
            @RequestParam final String type,
            @RequestParam final String amountRule, @RequestParam final String additionalRule,
            @RequestParam final String pendingAction, @RequestParam final Long approvalDepartment,
            @RequestParam final String currentDesignation) {

        List<Designation> designationList = designationService.getDesignationsByNames(customizedWorkFlowService.getNextDesignations(type,
                departmentRule, null, additionalRule, currentState,
                pendingAction, new Date(), currentDesignation));
        if (designationList.isEmpty())
            designationList = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
        return designationList;

    }

    @RequestMapping(value = "/ajaxWorkFlow-positionsByDepartmentAndDesignation", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getWorkFlowPositionByDepartmentAndDesignation(@RequestParam final String approvalDepartment,
                                                                @RequestParam final String approvalDesignation) {
    	 if (approvalDesignation != null && !approvalDesignation.equalsIgnoreCase("-1") && approvalDepartment != null
                 && !approvalDepartment.equalsIgnoreCase("-1")) {
        	
    		List<Assignment> assignmentList = microserviceUtils.getAssignments(approvalDepartment, approvalDesignation);
            //List<Assignment> assignmentList = assignmentService.findAllAssignmentsByDeptDesigAndDates(approvalDepartment,approvalDesignation, new Date());
            final Gson jsonCreator = new GsonBuilder().registerTypeAdapter(Assignment.class, new AssignmentAdaptor())
                    .create();
            return jsonCreator.toJson(assignmentList, new TypeToken<Collection<Assignment>>() {
            }.getType());
        }
        return "[]";
    }

}
