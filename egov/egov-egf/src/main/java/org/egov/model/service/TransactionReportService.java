package org.egov.model.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.model.report.TransactionReport;
import org.egov.model.report.Ulb;
import org.egov.model.repository.UlbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionReportService {

	private static final Logger LOGGER = Logger.getLogger(TransactionReportService.class);
	@PersistenceContext
    private EntityManager em;
	
	@Autowired
	private UlbRepository ulbRepo;
	
	public List<TransactionReport> getTransactionReports(TransactionReport transactionReport){
		
		List<TransactionReport> transactionReportList=new ArrayList<>();
		if(transactionReport.getUlbNames().size()>1) {
			for(String ulb:transactionReport.getUlbNames()) {
				if(!ulb.equals("ALL")) {
				String vhQuery="select count(*) from "+ulb+".voucherheader where backdateentry is not null and backdateentry != ''";
				String billQuery="select count(*) from "+ulb+".eg_billregister where billnumber like '202%%'";
				try {
				List<BigInteger> header=em.createNativeQuery(vhQuery).getResultList();
				List<BigInteger> bill=em.createNativeQuery(billQuery).getResultList();
				System.out.println(vhQuery);
				System.out.println(billQuery);
				BigInteger vhCount=header.get(0);
				BigInteger billCount=bill.get(0);
				TransactionReport report=new TransactionReport();
				report.setUlbName(ulb);
				report.setbNoOfTrxn(billCount);
				report.setvNoOfTrxn(vhCount);
				transactionReportList.add(report);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			}
		}else if(transactionReport.getUlbNames().size()==1) {
			if(transactionReport.getUlbNames().get(0).equals("ALL")) {
				
				List<String> ulbList=getUlbList();
				for(String ulb:ulbList) {

					String vhQuery="select count(*) from "+ulb+".voucherheader where backdateentry is not null and backdateentry != ''";
					String billQuery="select count(*) from "+ulb+".eg_billregister where billnumber like '202%%'";
					try {
					List<BigInteger> header=em.createNativeQuery(vhQuery).getResultList();
					List<BigInteger> bill=em.createNativeQuery(billQuery).getResultList();
					System.out.println(vhQuery);
					System.out.println(billQuery);
					BigInteger vhCount=header.get(0);
					BigInteger billCount=bill.get(0);
					TransactionReport report=new TransactionReport();
					report.setUlbName(ulb);
					report.setbNoOfTrxn(billCount);
					report.setvNoOfTrxn(vhCount);
					transactionReportList.add(report);
					}catch(Exception e) {
						e.printStackTrace();
					}
				
				
				}
			}else {
				for(String ulb:transactionReport.getUlbNames()) {
				String vhQuery="select count(*) from "+ulb+".voucherheader where backdateentry is not null and backdateentry != ''";
				String billQuery="select count(*) from "+ulb+".eg_billregister where billnumber like '202%%'";
				try {
				List<BigInteger> header=em.createNativeQuery(vhQuery).getResultList();
				List<BigInteger> bill=em.createNativeQuery(billQuery).getResultList();
				System.out.println(vhQuery);
				System.out.println(billQuery);
				BigInteger vhCount=header.get(0);
				BigInteger billCount=bill.get(0);
				TransactionReport report=new TransactionReport();
				report.setUlbName(ulb);
				report.setbNoOfTrxn(billCount);
				report.setvNoOfTrxn(vhCount);
				transactionReportList.add(report);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			}
			}else {
				
				List<String> ulbList=getUlbList();
				for(String ulb:ulbList) {
					String vhQuery="select count(*) from "+ulb+".voucherheader where backdateentry is not null and backdateentry != ''";
					String billQuery="select count(*) from "+ulb+".eg_billregister where billnumber like '202%%'";
					try {
					List<BigInteger> header=em.createNativeQuery(vhQuery).getResultList();
					List<BigInteger> bill=em.createNativeQuery(billQuery).getResultList();
					System.out.println(vhQuery);
					System.out.println(billQuery);
					BigInteger vhCount=header.get(0);
					BigInteger billCount=bill.get(0);
					TransactionReport report=new TransactionReport();
					report.setUlbName(ulb);
					report.setbNoOfTrxn(billCount);
					report.setvNoOfTrxn(vhCount);
					transactionReportList.add(report);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		return transactionReportList;
	}
	public List<String> getUlbList(){
		List<String> ulbNames = ulbRepo.getUlbNames();
		return ulbNames;
	}
}
