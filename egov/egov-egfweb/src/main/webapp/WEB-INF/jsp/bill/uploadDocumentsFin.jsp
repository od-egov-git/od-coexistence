<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ include file="/includes/taglibs.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<script src="<cdn:url value='/resources/javascript/documentsupload.js?rnd=${app_release_no}'/>"></script>
<style>
	.file-ellipsis {
		width : auto !Important;
	}
	
	.padding-10
	{
	  padding:10px;
	}
</style>
<div class="panel panel-primary" data-collapsed="0" style=" scrollable:true;">
<input type="hidden" id="hiddenCounter" value="2">
	<div class="panel-heading">
		<div class="panel-title">
			<h5>Please upload the supporting documents.Atleast one document is mandatory!</h5>
		</div>
	</div>
	<div class="container">
		
		<br>
		
			<input type="button" name="button2" id="button2" value="Add More"
				class="btn btn-default" onclick="addMore();" />
				
				<div class="row" id="uploadertbl">
					
						 		<div id="row1">			 				
									<div class="col-md-4 col-lg-4" id="f1">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 1</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f2" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 2</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f3" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 3</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f4" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 4</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f5" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 5</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f6" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 6</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f7" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 7</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f8" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 8</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									<div class="col-md-4 col-lg-4" id="f9" style="display:none">
									<label style="text-align: left; margin: 0; width: 100%;">File Option 9</label>
									<s:file style="width:100%" name="supportingDocuments" />
									</div>
									
									
										
								</div>
								
				</div>
				
			
			
			
		</div>
</div>
