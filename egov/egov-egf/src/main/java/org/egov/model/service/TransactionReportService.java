package org.egov.model.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.egov.model.report.TransactionReport;
import org.egov.model.repository.UlbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionReportService {

	private static final Logger LOGGER = Logger.getLogger(TransactionReportService.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UlbRepository ulbRepository;

	public List<TransactionReport> getTransactionReports(TransactionReport transactionReport) {
		List<TransactionReport> transactionReportList = new ArrayList<>();
		Date fromDate = transactionReport.getFromDate();
		Date toDate = transactionReport.getToDate();
		List<String> ulbNames = transactionReport.getUlbNames();

		for (String ulb : ulbNames) {
			if (!ulb.equals("ALL")) {
				TransactionReport report = generateReport(ulb, fromDate, toDate);
				transactionReportList.add(report);
			} else {
				List<String> ulbList = getUlbList();
				for (String ulbName : ulbList) {
					TransactionReport report = generateReport(ulbName, fromDate, toDate);
					transactionReportList.add(report);
				}
			}
		}
		return transactionReportList;
	}

	private TransactionReport generateReport(String ulb, Date fromDate, Date toDate) {
		TransactionReport report = new TransactionReport();
		report.setUlbName(ulb);
		report.setbNoOfTrxn(getBillCount("SELECT COUNT(*) FROM " + ulb + ".eg_billregister WHERE 1=1", fromDate, toDate));
		report.setvNoOfTrxn(getCount("SELECT COUNT(*) FROM " + ulb
				+ ".voucherheader WHERE backdateentry IS NOT NULL AND backdateentry != ''", fromDate, toDate));
		report.setMiscNoOfTrxn(
				getCount("SELECT COUNT(*) FROM " + ulb + ".voucherheader WHERE moduleid = '10'", fromDate, toDate));
		return report;
	}

	private BigInteger getCount(String query, Date fromDate, Date toDate) {
		if (fromDate != null && toDate != null) {
			query += " AND voucherdate BETWEEN :fromDate AND :toDate";
		}
		javax.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
		if (fromDate != null && toDate != null) {
			nativeQuery.setParameter("fromDate", fromDate);
			nativeQuery.setParameter("toDate", toDate);
		}
		List<BigInteger> resultList = nativeQuery.getResultList();
		return resultList.isEmpty() ? BigInteger.ZERO : resultList.get(0);
	}
	
	private BigInteger getBillCount(String query, Date fromDate, Date toDate) {
		if (fromDate != null && toDate != null) {
			query += " AND billdate BETWEEN :fromDate AND :toDate";
		}
		javax.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
		if (fromDate != null && toDate != null) {
			nativeQuery.setParameter("fromDate", fromDate);
			nativeQuery.setParameter("toDate", toDate);
		}
		List<BigInteger> resultList = nativeQuery.getResultList();
		return resultList.isEmpty() ? BigInteger.ZERO : resultList.get(0);
	}

	public List<String> getUlbList() {
		return ulbRepository.getUlbNames();
	}
}
