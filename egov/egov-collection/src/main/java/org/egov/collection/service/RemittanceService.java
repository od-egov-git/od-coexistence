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

package org.egov.collection.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Transient;

import org.egov.collection.bean.ReceiptBean;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.CollectionBankRemittanceReport;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.common.contstants.CommonConstants;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.DocumentUploads;
import org.egov.commons.repository.CommonDocumentUploadRepository;
import org.egov.commons.utils.DocumentUtils;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.utils.DateUtils;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class RemittanceService implements Serializable {
    private static final long serialVersionUID = 1849734164810403255L;
    @Autowired
    private DocumentUtils docUtils; // added abhishek 24032021
    @Autowired
    private FinancialUtils financialUtils;
	/*
	 * @Autowired private static CommonDocumentUploadRepository
	 * documentUploadRepository;// added abhishek 24032021
	 */    @Transient
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	 
    private List<DocumentUpload> documentDetail = new ArrayList<>();
    
    public abstract List<Receipt> createCashBankRemittance(List<ReceiptBean> receiptList, final String accountNumberId,
            final Date remittanceDate);

	/*
	 * public abstract List<ReceiptBean>
	 * findCashRemittanceDetailsForServiceAndFund(final String boundaryIdList, final
	 * String serviceCodes, final String fundCodes, Date startDate, Date endDate,
	 * String serviceTypeId);
	 */
    public abstract List<ReceiptBean> findCashRemittanceDetailsForServiceAndFund( String classification, Date fromDate, Date toDate, String businessCode,
    	    String receiptNo,String type);
    
   

    public List<CollectionBankRemittanceReport> prepareBankRemittanceReport(final List<ReceiptHeader> receiptHeaders) {
        final List<CollectionBankRemittanceReport> reportList = new ArrayList<CollectionBankRemittanceReport>(0);
        for (final ReceiptHeader receiptHead : receiptHeaders) {
            @SuppressWarnings("rawtypes")
            final Iterator itr = receiptHead.getReceiptInstrument().iterator();
            while (itr.hasNext()) {
                final CollectionBankRemittanceReport collBankRemitReport = new CollectionBankRemittanceReport();
                final InstrumentHeader instHead = (InstrumentHeader) itr.next();
                if (!instHead.getInstrumentType().getType().equals(CollectionConstants.INSTRUMENTTYPE_CASH)) {
                    collBankRemitReport.setChequeNo(instHead.getInstrumentNumber());
                    collBankRemitReport.setBranchName(instHead.getBankBranchName());
                    collBankRemitReport.setBankName(instHead.getBankId() != null ? instHead.getBankId().getName() : "");
                    collBankRemitReport.setChequeDate(instHead.getInstrumentDate());
                    collBankRemitReport.setPaymentMode(instHead.getInstrumentType().getType());
                    collBankRemitReport.setAmount(instHead.getInstrumentAmount().doubleValue());
                    collBankRemitReport.setReceiptNumber(receiptHead.getReceiptnumber());
                    collBankRemitReport.setReceiptDate(receiptHead.getReceiptDate());
                    collBankRemitReport.setVoucherNumber(receiptHead.getRemittanceVoucherNumber());
                    reportList.add(collBankRemitReport);
                } else {
                    collBankRemitReport.setVoucherNumber(receiptHead.getRemittanceReferenceNumber());
                    reportList.add(collBankRemitReport);
                }
            }
        }
        return reportList;
    }

    public List<CollectionBankRemittanceReport> prepareChequeRemittanceReport(final ReceiptBean receiptHeaders) {
    	final List<CollectionBankRemittanceReport> reportList = new ArrayList<CollectionBankRemittanceReport>(0);
       // for (final ReceiptBean receiptHead : receiptHeaders) {
            final CollectionBankRemittanceReport collBankRemitReport = new CollectionBankRemittanceReport();
            //collBankRemitReport.setVoucherNumber(receiptHead.getReceiptNumber());
            //collBankRemitReport.setReceiptNumber(receiptHead.getReceiptNumber());
            //collBankRemitReport.setReceiptDate(new Date(receiptHead.getReceiptDate()));
            //collBankRemitReport.setServiceType(receiptHead.getServiceName());
            collBankRemitReport.setVoucherNumber(receiptHeaders.getRemittanceVouherNumber());
            collBankRemitReport.setVoucherId(receiptHeaders.getVoucherid());
            reportList.add(collBankRemitReport);
        //}
        return reportList;
    }

    public List<CollectionBankRemittanceReport> prepareCashRemittanceReport(final List<Receipt> receiptHeaders) {
        final List<CollectionBankRemittanceReport> reportList = new ArrayList<CollectionBankRemittanceReport>(0);
        for (final Receipt receiptHead : receiptHeaders) {
            final CollectionBankRemittanceReport collBankRemitReport = new CollectionBankRemittanceReport();
            collBankRemitReport.setVoucherNumber(receiptHead.getReceiptNumber());
            collBankRemitReport.setReceiptNumber(receiptHead.getBill().get(0).getBillDetails().get(0).getReceiptNumber());
            collBankRemitReport.setReceiptDate(new Date(receiptHead.getBill().get(0).getBillDetails().get(0).getReceiptDate()));
            collBankRemitReport.setServiceType(receiptHead.getBill().get(0).getBillDetails().get(0).getBusinessService());
            reportList.add(collBankRemitReport);
        }
        return reportList;
    }
    
    public List<CollectionBankRemittanceReport> prepareCashRemittanceReportNew(final ReceiptBean receiptHeaders) {
        final List<CollectionBankRemittanceReport> reportList = new ArrayList<CollectionBankRemittanceReport>(0);
        //for (final ReceiptBean receiptHead : receiptHeaders) {
            final CollectionBankRemittanceReport collBankRemitReport = new CollectionBankRemittanceReport();
            //collBankRemitReport.setVoucherNumber(receiptHead.getReceiptNumber());
            //collBankRemitReport.setReceiptNumber(receiptHead.getReceiptNumber());
            //collBankRemitReport.setReceiptDate(new Date(receiptHead.getReceiptDate()));
            //collBankRemitReport.setServiceType(receiptHead.getServiceName());
            collBankRemitReport.setVoucherNumber(receiptHeaders.getRemittanceVouherNumber());
            collBankRemitReport.setVoucherId(receiptHeaders.getVoucherid());
            reportList.add(collBankRemitReport);
       // }
        return reportList;
    }
    

    //added by Abhishek on 24032021
    @Transactional
    public void saveDocuments(ReceiptHeader receiptHeader)
    {
 	   List<DocumentUpload> files = receiptHeader.getDocumentDetail() == null ? null : receiptHeader.getDocumentDetail();
        final List<DocumentUpload> documentDetails;
        documentDetails = financialUtils.getDocumentDetails(files, receiptHeader, CommonConstants.JOURNAL_VOUCHER_OBJECT);
        if (!documentDetails.isEmpty()) {
        	receiptHeader.setDocumentDetail(documentDetails);
            persistDocuments(documentDetails);
        }
    }
    
    public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
        if (documentDetailsList != null && !documentDetailsList.isEmpty())
            for (final DocumentUpload doc : documentDetailsList)
                documentUploadRepository.save(doc);
    }
    
	/*
	 * public static void persistDocuments(final List<DocumentUploads>
	 * documentDetailsList) { if (documentDetailsList != null &&
	 * !documentDetailsList.isEmpty()) for (final DocumentUploads doc :
	 * documentDetailsList) documentUploadRepository.save(doc); }
	 */
    //end
    
	public abstract List<ReceiptBean> findChequeRemittanceDetailsForServiceAndFund(String classification, Date fromDate, Date toDate, String businessCode,
    	    String receiptNo,String type);
	 
	/*
	 * public abstract List<ReceiptBean>
	 * findChequeRemittanceDetailsForServiceAndFund(final String boundaryIdList,
	 * final String serviceCodes, final String fundCodes, Date startDate, Date
	 * endDate,String serviceTypeId);
	 */

    public abstract List<Receipt> createChequeBankRemittance(List<ReceiptBean> receiptList, String accountNumberId,
            final Date remittanceDate, final String[] instrumentIdArray);
}
