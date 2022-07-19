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
package org.egov.services.voucher;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.common.contstants.CommonConstants;
import org.egov.commons.CFunction;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.DocumentUploads;
import org.egov.commons.utils.DocumentUtils;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.model.voucher.VoucherDetails;
import org.egov.model.voucher.VoucherTypeBean;
import org.egov.model.voucher.WorkflowBean;
import org.egov.pims.commons.Position;
import org.egov.utils.FinancialConstants;
import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.GLEngine.ChartOfAccounts;
import com.exilant.GLEngine.Transaxtion;

@Transactional(readOnly = true)
@Service
public class JournalVoucherActionHelper {
    final private static Logger LOGGER = Logger.getLogger(JournalVoucherActionHelper.class);
    private static final String FAILED = "Transaction failed";
    private static final String EXCEPTION_WHILE_SAVING_DATA = "Exception while saving data";
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<CVoucherHeader> voucherHeaderWorkflowService;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    @Qualifier("voucherService")
    private VoucherService voucherService;

    @Autowired
    @Qualifier("createVoucher")
    private CreateVoucher createVoucher;
    @Autowired
    @Qualifier("chartOfAccounts")
    private ChartOfAccounts chartOfAccounts;

    @Autowired
    private MicroserviceUtils microserviceUtils;

    @Autowired
    private DocumentUtils docUtils;
    @Transactional
	public CVoucherHeader createVcouher(List<VoucherDetails> billDetailslist, List<VoucherDetails> subLedgerlist,
			CVoucherHeader voucherHeader, VoucherTypeBean voucherTypeBean, WorkflowBean workflowBean) {
		// TODO Auto-generated method stub
        try {
        	LOGGER.info("Name ::: "+voucherTypeBean.getVoucherName());
        	LOGGER.info("Type ::: "+voucherTypeBean.getVoucherType());
        	LOGGER.info("Sub -Type ::: "+voucherTypeBean.getVoucherSubType());
            voucherHeader.setName(voucherTypeBean.getVoucherName());
            if("Receipt Journal".equalsIgnoreCase(voucherTypeBean.getVoucherName()))
            {
            	voucherHeader.setType("Receipt");
            	voucherHeader.setVoucherSubType(voucherTypeBean.getVoucherSubType());
            }
            else
            {
            	voucherHeader.setType(voucherTypeBean.getVoucherType());
            }
            voucherHeader.setVoucherSubType(voucherTypeBean.getVoucherSubType());
           
            //voucherHeader = createVoucherAndledger(billDetailslist, subLedgerlist, voucherHeader);
         // jayanta for save as draft
            voucherHeader = createVoucherAndledger(billDetailslist, subLedgerlist, voucherHeader, workflowBean);
            
            if (!"JVGeneral".equalsIgnoreCase(voucherTypeBean.getVoucherName()) && !"Receipt Journal".equalsIgnoreCase(voucherTypeBean.getVoucherName())) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(" Journal Voucher Action | Bill create | voucher name = " + voucherTypeBean.getVoucherName());
                voucherService.createBillForVoucherSubType(billDetailslist, subLedgerlist, voucherHeader, voucherTypeBean,
                        new BigDecimal(voucherTypeBean.getTotalAmount()));
            }
            else
            {
            	voucherService.updateSourcePathForGJV(voucherHeader);
            }
            if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())
                    && voucherHeader.getState() == null) {
                voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
            } 
			else {
                voucherHeader = transitionWorkFlow(voucherHeader, workflowBean);
                LOGGER.info("Voucher State ::: "+voucherHeader.getState());
                voucherService.applyAuditing(voucherHeader.getState());
            }
            voucherService.create(voucherHeader);
        
            
        } catch (final ValidationException e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }
   @Transactional
   public void saveDocuments(CVoucherHeader voucherHeader)
   {
	   List<DocumentUploads> files = voucherHeader.getDocumentDetail() == null ? null : voucherHeader.getDocumentDetail();
       final List<DocumentUploads> documentDetails;
       documentDetails = docUtils.getDocumentDetails(files, voucherHeader,
               CommonConstants.JOURNAL_VOUCHER_OBJECT);
       if (!documentDetails.isEmpty()) {
       	voucherHeader.setDocumentDetail(documentDetails);
       	voucherService.persistDocuments(documentDetails);
       }
   }
    /*public CVoucherHeader createVoucher(List<VoucherDetails> billDetailslist, List<VoucherDetails> subLedgerlist,
            CVoucherHeader voucherHeader, VoucherTypeBean voucherTypeBean, WorkflowBean workflowBean,List<DocumentUpload> documentList) throws Exception {
        try {
            voucherHeader.setName(voucherTypeBean.getVoucherName());
            voucherHeader.setType(voucherTypeBean.getVoucherType());
            voucherHeader.setVoucherSubType(voucherTypeBean.getVoucherSubType());
            voucherHeader = createVoucherAndledger(billDetailslist, subLedgerlist, voucherHeader);
            if (!"JVGeneral".equalsIgnoreCase(voucherTypeBean.getVoucherName())) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(" Journal Voucher Action | Bill create | voucher name = " + voucherTypeBean.getVoucherName());
                voucherService.createBillForVoucherSubType(billDetailslist, subLedgerlist, voucherHeader, voucherTypeBean,
                        new BigDecimal(voucherTypeBean.getTotalAmount()),documentList);
            }
            if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())
                    && voucherHeader.getState() == null) {
                voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
            } else {
                voucherHeader = transitionWorkFlow(voucherHeader, workflowBean);
                voucherService.applyAuditing(voucherHeader.getState());
            }
            voucherService.create(voucherHeader);
        } catch (final ValidationException e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }*/


    @Transactional
    public CVoucherHeader editVoucher(List<VoucherDetails> billDetailslist, List<VoucherDetails> subLedgerlist,
            CVoucherHeader voucherHeader, VoucherTypeBean voucherTypeBean, WorkflowBean workflowBean, String totaldbamount)
            throws Exception {
        try {
        	LOGGER.info("Inside edit");
        	System.out.println(":::::::::: "+voucherHeader.getBackdateentry());
        	voucherHeader.setBackdateentry(voucherHeader.getBackdateentry());
            voucherHeader = voucherService.updateVoucherHeader(voucherHeader, voucherTypeBean);
            LOGGER.info("after update");
            //added by deep for editing receipt type
			/*
			 * if(voucherHeader.getType().equalsIgnoreCase(FinancialConstants.
			 * JOURNALVOUCHER_NAME_RECEIPT)) { EgBillregister egBillregister =
			 * voucherService.updateBillForVSubType(billDetailslist, subLedgerlist,
			 * voucherHeader, voucherTypeBean, new BigDecimal(totaldbamount)); }
			 */
          //added by deep for editing receipt type
            
            voucherService.deleteGLDetailByVHId(voucherHeader.getId());
            voucherHeader.getGeneralLedger().removeAll(voucherHeader.getGeneralLedger());
            boolean CheckSaveAsDraft= true;
            if(workflowBean.getWorkFlowAction()!= null && !workflowBean.getWorkFlowAction().equalsIgnoreCase("SaveAsDraft"))
            {
                
                  for (final VoucherDetails vd : billDetailslist) {
                  if(vd.getGlcodeIdDetail()==null ||
                  vd.getGlcodeIdDetail().toString().equals("")) { 
                	  CheckSaveAsDraft=false;
                	  break; 
                	  } 
                  }
                 
                //CheckSaveAsDraft=false;
            }
            if(CheckSaveAsDraft)
            {
            
            final List<Transaxtion> transactions = voucherService.postInTransaction(billDetailslist, subLedgerlist,
                    voucherHeader);
            LOGGER.info("1");
            Transaxtion txnList[] = new Transaxtion[transactions.size()];
            txnList = transactions.toArray(txnList);
            final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            if (!chartOfAccounts.postTransaxtions(txnList, formatter.format(voucherHeader.getVoucherDate()))) {
                final List<ValidationError> errors = new ArrayList<ValidationError>();
                errors.add(new ValidationError("exp", "Engine Validation failed"));
                throw new ValidationException(errors);
            } else {
                if (!"JVGeneral".equalsIgnoreCase(voucherHeader.getName())) {
                    final String totalamount = totaldbamount;
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Journal Voucher Modify Action | Bill modify | voucher name = "
                                + voucherHeader.getName());
                    // cancelBill(voucherHeader.getId());
                    voucherService.updateBillForVSubType(billDetailslist, subLedgerlist, voucherHeader, voucherTypeBean,
                            new BigDecimal(totalamount));
                }
                voucherHeader.setStatus(FinancialConstants.PREAPPROVEDVOUCHERSTATUS);

            }
            }
            else
            {
                boolean ststud=voucherService.updateVoucherDraft(billDetailslist, voucherHeader);
            }
            LOGGER.info("2");
            voucherHeader = transitionWorkFlow(voucherHeader, workflowBean);
            voucherService.applyAuditing(voucherHeader.getState());
            voucherService.persist(voucherHeader);
        } catch (final ValidationException e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }

    @Transactional
    public CVoucherHeader transitionWorkFlow(final CVoucherHeader voucherHeader, WorkflowBean workflowBean) {
        final DateTime currentDate = new DateTime();
        final User user = securityUtils.getCurrentUser();
        EmployeeInfo info = null;
        LOGGER.info(":::: Transition start:::::::");
        LOGGER.info("WorkFlowAction ::: "+workflowBean.getWorkFlowAction());
        if (user != null && user.getId() != null)
            info = microserviceUtils.getEmployeeById(user.getId());

        if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            final String stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
            int size=voucherHeader.getStateHistory().size();//added abhishek on 12042021
			Position owenrPosName = new Position();
			Position owenrPos = new Position();
	        owenrPos.setId(workflowBean.getApproverPositionId());
			owenrPosName.setId(workflowBean.getApproverPositionId());
			HashMap<Long, Long> positionmap = new HashMap<>();
			for(int i=0;i<size;i++)
			{
				positionmap.put(voucherHeader.getStateHistory().get(i).getOwnerPosition(),
						voucherHeader.getStateHistory().get(i).getPreviousownerposition());
			}
			if(size>0)
			{
				Long owenrPos1=0l;
				if(positionmap.containsKey(user.getId()))
					owenrPos1=positionmap.get(user.getId());
				else
					owenrPos1=(long) voucherHeader.getStateHistory().get(size-1).getOwnerPosition();
				
				if(owenrPos1==null|| owenrPos1.equals(""))
					owenrPos1=(long) voucherHeader.getCreatedBy();
				owenrPosName.setId(owenrPos1);
				
				System.out.println("E owner position "+owenrPos1);
				owenrPos.setId(owenrPos1);
			}
			else
			{
				owenrPos.setId(voucherHeader.getState().getCreatedBy());
				owenrPosName.setId(voucherHeader.getState().getCreatedBy());
				
			}
            System.out.println("ownerPostion id- "+owenrPos);
            System.out.println("ownerPostion Nameid- "+owenrPosName);
			
            voucherHeader.transition().progressWithStateCopy().withSenderName(user.getName())
                    .withComments(workflowBean.getApproverComments())
                    .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                    //.withOwner(voucherHeader.getState().getInitiatorPosition()).withOwnerName((voucherHeader.getState().getInitiatorPosition() != null && voucherHeader.getState().getInitiatorPosition() > 0L) ? getEmployeeName(voucherHeader.getState().getInitiatorPosition()):"")
                    .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPosName).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                    //.withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
                    .withNextAction("");

        } else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            final WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
                    null, null, voucherHeader.getCurrentState().getValue(), null);
            Position pos=new Position();
            pos.setId(user.getId());
            voucherHeader.transition().end().withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withStateValue(wfmatrix.getCurrentDesignation() + " Approved").withDateInfo(currentDate.toDate())
                    .withOwner(pos)
                    .withNextAction(wfmatrix.getNextAction());

            voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
        } else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            voucherHeader.setStatus(FinancialConstants.CANCELLEDVOUCHERSTATUS);
            voucherHeader.transition().end().withStateValue(FinancialConstants.WORKFLOW_STATE_CANCELLED)
                    .withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
                    .withDateInfo(currentDate.toDate());
        } else {
        	
            if (null == voucherHeader.getState()) {
                final WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
                        null, null, workflowBean.getCurrentState(), null);
                String ststeValue=wfmatrix.getNextState();
                
                if ("Save As Draft".equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
                		ststeValue =FinancialConstants.WORKFLOW_STATE_SAVEASDRAFT;
                		voucherHeader.setStatus(6);
            		LOGGER.info("Saveasdraft ownerposition taken from user ::::::::");
            		LOGGER.info("ApproverPositionId ::::::::"+user.getId());
            		
                    voucherHeader.transition().start().withSenderName(user.getName())
                    .withComments(workflowBean.getApproverComments())
                   // .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate())
                    .withStateValue(ststeValue).withDateInfo(currentDate.toDate())
                    .withOwner(user.getId()).withOwnerName((user.getId() != null && user.getId() > 0L) ? getEmployeeName(user.getId()):"")
                    .withNextAction(wfmatrix.getNextAction())
                    .withInitiator(user.getId());
                }
                else 
                {
                LOGGER.info("ApproverPositionId ::::::::"+workflowBean.getApproverPositionId());
                voucherHeader.transition().start().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                       // .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate())
                        .withStateValue(ststeValue).withDateInfo(currentDate.toDate())
                        .withOwner(workflowBean.getApproverPositionId()).withOwnerName((workflowBean.getApproverPositionId() != null && workflowBean.getApproverPositionId() > 0L) ? getEmployeeName(workflowBean.getApproverPositionId()):"")
                        .withNextAction(wfmatrix.getNextAction())
                        .withInitiator(user.getId());
                        //.withInitiator((info != null && info.getAssignments() != null && !info.getAssignments().isEmpty())
                                //? info.getAssignments().get(0).getPosition() : null);
                }
            } else if (voucherHeader.getCurrentState().getNextAction().equalsIgnoreCase("END"))
                voucherHeader.transition().end().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        .withDateInfo(currentDate.toDate());
            else {
				/*
				 * if
				 * (!voucherHeader.getCurrentState().getValue().equalsIgnoreCase(workflowBean.
				 * getCurrentState())) { return voucherHeader; }
				 */
                final WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
                        null, null,voucherHeader.getCurrentState().getValue(), null);
                String ststeValue=wfmatrix.getNextState();
                Long owner = workflowBean.getApproverPositionId();
                if ("Save As Draft".equalsIgnoreCase(workflowBean.getWorkFlowAction()))
                {
                		ststeValue =FinancialConstants.WORKFLOW_STATE_SAVEASDRAFT;
                		owner = populatePosition();
                		voucherHeader.setStatus(6);
                		LOGGER.info("Voucher status::: "+voucherHeader.getStatus());
                		
                }
          ///////added abhishek//////////////////////////////
                if(voucherHeader.getState().getValue().equalsIgnoreCase("Rejected")) 
                {
	                HashMap<Long, String> positionmap = new HashMap<>();
	                HashMap<Long, String> positionmap1 = new HashMap<>();
	                int size=voucherHeader.getStateHistory().size();
	    			for(int i=0;i<size;i++)
	    			{
	    				positionmap.put(voucherHeader.getStateHistory().get(i).getLastModifiedBy(),
	    						voucherHeader.getStateHistory().get(i).getValue());
	    				positionmap1.put(voucherHeader.getStateHistory().get(i).getLastModifiedBy(),
	    						voucherHeader.getStateHistory().get(i).getNextAction());
	    			}
	    			if(positionmap.containsKey(user.getId()))
	    			{
	    				ststeValue=positionmap.get(user.getId());
	    				wfmatrix.setNextAction(positionmap1.get(user.getId()));
	    			}
	    			
                
                }
                
                voucherHeader.transition().progressWithStateCopy().withSenderName(user.getName())
                        .withComments(workflowBean.getApproverComments())
                        //.withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate())
                        .withStateValue(ststeValue).withDateInfo(currentDate.toDate())
                        .withOwner(owner).withOwnerName((owner != null && owner > 0L) ? getEmployeeName(owner):"")
                        .withNextAction(wfmatrix.getNextAction());
            }
        }
        LOGGER.info(":::::Transition Complete ::: ");
        return voucherHeader;
    }

    private HashMap<String, Object> createHeaderAndMisDetails(CVoucherHeader voucherHeader) throws ValidationException {
        final HashMap<String, Object> headerdetails = new HashMap<String, Object>();
        headerdetails.put(VoucherConstant.VOUCHERNAME, voucherHeader.getName());
        headerdetails.put(VoucherConstant.VOUCHERTYPE, voucherHeader.getType());
        headerdetails.put((String) VoucherConstant.VOUCHERSUBTYPE, voucherHeader.getVoucherSubType());
        headerdetails.put(VoucherConstant.VOUCHERNUMBER, voucherHeader.getVoucherNumber());
        headerdetails.put(VoucherConstant.VOUCHERDATE, voucherHeader.getVoucherDate());
        headerdetails.put(VoucherConstant.DESCRIPTION, voucherHeader.getDescription());
        headerdetails.put("backdateentry", voucherHeader.getBackdateentry());
        headerdetails.put("fileno", voucherHeader.getFileno());
        if (voucherHeader.getVouchermis().getDepartmentcode() != null)
            headerdetails.put(VoucherConstant.DEPARTMENTCODE, voucherHeader.getVouchermis().getDepartmentcode());
        if (voucherHeader.getFundId() != null)
            headerdetails.put(VoucherConstant.FUNDCODE, voucherHeader.getFundId().getCode());
        if (voucherHeader.getVouchermis().getSchemeid() != null)
            headerdetails.put(VoucherConstant.SCHEMECODE, voucherHeader.getVouchermis().getSchemeid().getCode());
        if (voucherHeader.getVouchermis().getSubschemeid() != null)
            headerdetails.put(VoucherConstant.SUBSCHEMECODE, voucherHeader.getVouchermis().getSubschemeid().getCode());
        if (voucherHeader.getVouchermis().getFundsource() != null)
            headerdetails.put(VoucherConstant.FUNDSOURCECODE, voucherHeader.getVouchermis().getFundsource().getCode());
        if (voucherHeader.getVouchermis().getDivisionid() != null)
            headerdetails.put(VoucherConstant.DIVISIONID, voucherHeader.getVouchermis().getDivisionid().getId());
        if (voucherHeader.getVouchermis().getFunctionary() != null)
            headerdetails.put(VoucherConstant.FUNCTIONARYCODE, voucherHeader.getVouchermis().getFunctionary().getCode());
        if (voucherHeader.getVouchermis().getFunction() != null)
            headerdetails.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
        return headerdetails;
    }

  /*  @Transactional
    public CVoucherHeader createVoucherAndledger(final List<VoucherDetails> billDetailslist,
            final List<VoucherDetails> subLedgerlist, CVoucherHeader voucherHeader) {
        try {
            final HashMap<String, Object> headerDetails = createHeaderAndMisDetails(voucherHeader);
            HashMap<String, Object> detailMap = null;
            HashMap<String, Object> subledgertDetailMap = null;
            final List<HashMap<String, Object>> accountdetails = new ArrayList<HashMap<String, Object>>();
            final List<HashMap<String, Object>> subledgerDetails = new ArrayList<HashMap<String, Object>>();

            detailMap = new HashMap<String, Object>();
            final Map<String, Object> glcodeMap = new HashMap<String, Object>();
            for (final VoucherDetails voucherDetail : billDetailslist) {
                detailMap = new HashMap<String, Object>();
                if (voucherDetail.getFunctionIdDetail() != null)
                    if (voucherHeader.getIsRestrictedtoOneFunctionCenter())
                        detailMap.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
                    else if (null != voucherDetail.getFunctionIdDetail()) {
                        final CFunction function = (CFunction) persistenceService.getSession().load(CFunction.class,
                                voucherDetail.getFunctionIdDetail());
                        detailMap.put(VoucherConstant.FUNCTIONCODE, function.getCode());
                    } else if (null != voucherHeader.getVouchermis().getFunction())
                        detailMap.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
                if (voucherDetail.getCreditAmountDetail().compareTo(BigDecimal.ZERO) == 0) {

                    detailMap.put(VoucherConstant.DEBITAMOUNT, voucherDetail.getDebitAmountDetail().toString());
                    detailMap.put(VoucherConstant.CREDITAMOUNT, "0");
                    detailMap.put(VoucherConstant.GLCODE, voucherDetail.getGlcodeDetail());
                    accountdetails.add(detailMap);
                    glcodeMap.put(voucherDetail.getGlcodeDetail(), VoucherConstant.DEBIT);
                } else {
                    detailMap.put(VoucherConstant.CREDITAMOUNT, voucherDetail.getCreditAmountDetail().toString());
                    detailMap.put(VoucherConstant.DEBITAMOUNT, "0");
                    detailMap.put(VoucherConstant.GLCODE, voucherDetail.getGlcodeDetail());
                    accountdetails.add(detailMap);
                    glcodeMap.put(voucherDetail.getGlcodeDetail(), VoucherConstant.CREDIT);
                }

            }

            for (final VoucherDetails voucherDetail : subLedgerlist) {
                subledgertDetailMap = new HashMap<String, Object>();
                final String amountType = glcodeMap.get(voucherDetail.getSubledgerCode()) != null ? glcodeMap.get(
                        voucherDetail.getSubledgerCode()).toString() : null; // Debit or Credit.
                if (voucherDetail.getFunctionDetail() != null && !voucherDetail.getFunctionDetail().equalsIgnoreCase("")
                        && !voucherDetail.getFunctionDetail().equalsIgnoreCase("0")) {
                    final CFunction function = (CFunction) persistenceService.find("from CFunction where id = ?",
                            Long.parseLong(voucherDetail.getFunctionDetail()));
                    subledgertDetailMap.put(VoucherConstant.FUNCTIONCODE, function != null ? function.getCode() : "");
                }
                if (null != amountType && amountType.equalsIgnoreCase(VoucherConstant.DEBIT))
                    subledgertDetailMap.put(VoucherConstant.DEBITAMOUNT, voucherDetail.getAmount());
                else if (null != amountType)
                    subledgertDetailMap.put(VoucherConstant.CREDITAMOUNT, voucherDetail.getAmount());
                subledgertDetailMap.put(VoucherConstant.DETAILTYPEID, voucherDetail.getDetailType().getId());
                subledgertDetailMap.put(VoucherConstant.DETAILKEYID, voucherDetail.getDetailKeyId());
                subledgertDetailMap.put(VoucherConstant.GLCODE, voucherDetail.getSubledgerCode());

                subledgerDetails.add(subledgertDetailMap);
            }

           // voucherHeader = createVoucher.createPreApprovedVoucher(headerDetails, accountdetails, subledgerDetails);
            // jayanta for save as draft
            voucherHeader = createVoucher.createPreApprovedVoucher(headerDetails, accountdetails, subledgerDetails, workflowBean);
            

        } catch (final HibernateException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(EXCEPTION_WHILE_SAVING_DATA, FAILED)));
        } catch (final ApplicationRuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        } catch (final ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (final Exception e) {
            // handle engine exception
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Posted to Ledger " + voucherHeader.getId());
        return voucherHeader;

    }*/
    
 

    private String populateEmpName() {
		Long empId = ApplicationThreadLocals.getUserId();
		String name = null;
		List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null, null, null);
		if (null != employs && employs.size() > 0) {
			name = employs.get(0).getAssignments().get(0).getEmployeeName();

		}
		return name;
	}
    
    private Long populatePosition() {
		Long empId = ApplicationThreadLocals.getUserId();
		Long pos = null;
		List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null, null, null);
		if (null != employs && employs.size() > 0) {
			pos = employs.get(0).getAssignments().get(0).getPosition();

		}
		return pos;
	}
    
    
    
 // jayanta for save as draft
    @Transactional
    public CVoucherHeader createVoucherAndledger(final List<VoucherDetails> billDetailslist,
            final List<VoucherDetails> subLedgerlist, CVoucherHeader voucherHeader, WorkflowBean workflowBean) {
        try {
            final HashMap<String, Object> headerDetails = createHeaderAndMisDetails(voucherHeader);
            HashMap<String, Object> detailMap = null;
            HashMap<String, Object> subledgertDetailMap = null;
            final List<HashMap<String, Object>> accountdetails = new ArrayList<HashMap<String, Object>>();
            final List<HashMap<String, Object>> subledgerDetails = new ArrayList<HashMap<String, Object>>();
            detailMap = new HashMap<String, Object>();
            final Map<String, Object> glcodeMap = new HashMap<String, Object>();
           
            
            
            for (final VoucherDetails voucherDetail : billDetailslist) {
                detailMap = new HashMap<String, Object>();
                
                if(workflowBean.getWorkFlowAction()!= null && !workflowBean.getWorkFlowAction().equalsIgnoreCase("SaveAsDraft"))
    			{
                	if(voucherDetail!=null)
                	{
                		System.out.println("voucherDetail.getFunctionIdDetail()   :::: "+voucherDetail.getFunctionIdDetail());
                	}
                	 if(voucherDetail.getFunctionIdDetail()==null)
                		 detailMap.put(VoucherConstant.FUNCTIONIDDETAILS, "");
                	 else
                		 detailMap.put(VoucherConstant.FUNCTIONIDDETAILS, voucherDetail.getFunctionIdDetail());
                	 
                	 
                	 if(voucherDetail.getFunctionDetail()==null)
                		 detailMap.put(VoucherConstant.FUNCTIONDETAILS, "");
                	 else
                		 detailMap.put(VoucherConstant.FUNCTIONDETAILS, voucherDetail.getFunctionDetail());
    			
    			}
                
                
                if (voucherDetail.getFunctionIdDetail() != null)
                    if (voucherHeader.getIsRestrictedtoOneFunctionCenter())
                        detailMap.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
                    else if (null != voucherDetail.getFunctionIdDetail()) {
                        final CFunction function = (CFunction) persistenceService.getSession().load(CFunction.class,
                                voucherDetail.getFunctionIdDetail());
                        detailMap.put(VoucherConstant.FUNCTIONCODE, function.getCode());
                    } else if (null != voucherHeader.getVouchermis().getFunction())
                        detailMap.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
                if (voucherDetail.getCreditAmountDetail().compareTo(BigDecimal.ZERO) == 0) {
                    detailMap.put(VoucherConstant.DEBITAMOUNT, voucherDetail.getDebitAmountDetail().toString());
                    detailMap.put(VoucherConstant.CREDITAMOUNT, "0");
                    detailMap.put(VoucherConstant.GLCODE, voucherDetail.getGlcodeDetail());
                    accountdetails.add(detailMap);
                    glcodeMap.put(voucherDetail.getGlcodeDetail(), VoucherConstant.DEBIT);
                } else {
                    detailMap.put(VoucherConstant.CREDITAMOUNT, voucherDetail.getCreditAmountDetail().toString());
                    detailMap.put(VoucherConstant.DEBITAMOUNT, "0");
                    detailMap.put(VoucherConstant.GLCODE, voucherDetail.getGlcodeDetail());
                    accountdetails.add(detailMap);
                    glcodeMap.put(voucherDetail.getGlcodeDetail(), VoucherConstant.CREDIT);
                }
            }
            for (final VoucherDetails voucherDetail : subLedgerlist) {
                subledgertDetailMap = new HashMap<String, Object>();
                final String amountType = glcodeMap.get(voucherDetail.getSubledgerCode()) != null ? glcodeMap.get(
                        voucherDetail.getSubledgerCode()).toString() : null; // Debit or Credit.
                if (voucherDetail.getFunctionDetail() != null && !voucherDetail.getFunctionDetail().equalsIgnoreCase("")
                        && !voucherDetail.getFunctionDetail().equalsIgnoreCase("0")) {
                    final CFunction function = (CFunction) persistenceService.find("from CFunction where id = ?",
                            Long.parseLong(voucherDetail.getFunctionDetail()));
                    subledgertDetailMap.put(VoucherConstant.FUNCTIONCODE, function != null ? function.getCode() : "");
                }
                if (null != amountType && amountType.equalsIgnoreCase(VoucherConstant.DEBIT))
                    subledgertDetailMap.put(VoucherConstant.DEBITAMOUNT, voucherDetail.getAmount());
                else if (null != amountType)
                    subledgertDetailMap.put(VoucherConstant.CREDITAMOUNT, voucherDetail.getAmount());
                subledgertDetailMap.put(VoucherConstant.DETAILTYPEID, voucherDetail.getDetailType().getId());
                subledgertDetailMap.put(VoucherConstant.DETAILKEYID, voucherDetail.getDetailKeyId());
                subledgertDetailMap.put(VoucherConstant.GLCODE, voucherDetail.getSubledgerCode());
                subledgerDetails.add(subledgertDetailMap);
            }
           // voucherHeader = createVoucher.createPreApprovedVoucher(headerDetails, accountdetails, subledgerDetails);
         // jayanta for save as draft
            voucherHeader = createVoucher.createPreApprovedVoucher(headerDetails, accountdetails, subledgerDetails, workflowBean);
            
        } catch (final HibernateException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(EXCEPTION_WHILE_SAVING_DATA, FAILED)));
        } catch (final ApplicationRuntimeException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        } catch (final ValidationException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (final Exception e) {
            e.printStackTrace();
            // handle engine exception
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Posted to Ledger " + voucherHeader.getId());
        return voucherHeader;
    }
    
    public String getEmployeeName(Long empId){
        
        return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }
}