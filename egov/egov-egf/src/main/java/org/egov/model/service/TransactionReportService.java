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
				String ulbList="ANANDAPUR,ANGUL,ASKA,ATHAGARH,ATHMALLIK,ATTABIRA,BALANGIR,BALASORE,BALIMELA,BALLIGUDA,BALUGAON,BANKI,BANPUR,BARBIL,BARGARH,BARIPADA,BARPALI,BASUDEVPUR,BELLAGUNTHA,BELPAHAR,BERHAMPUR,BHADRAK,BHANJANAGAR,BHAWANIPATNA,BHUBAN,BIJEPUR,BINKA,BIRMITRAPUR,BOUDHGARH,BRAJRAJNAGAR,BUGUDA,CHAMPUA,CHANDBALI,CHATRAPUR,CHIKITI,CHOUDWAR,CUTTACK,DASPALLA,DEOGARH,DHAMNAGAR,DHARAMGARH,DHENKANAL,DIGAPAHANDI,GANJAM,GOPALPUR,GUDARI,GUDAYAGIRI,GUNUPUR,HINDOL,HINJILICUT,JAGATSINGHPUR,JAJPUR,JALESWAR,JATNI,JEYPORE,JHARSUGUDA,JODA,JUNAGARH,KABISURYANAGAR,KAMAKHYANAGAR,KANTABANJI,KARANJIA,KASHINAGAR,KENDRAPARA,KEONJHARGARH,KESINGA,KHALLIKOTE,KHANDAPADA,KHARIAR,KHARIARROAD,KHORDHA,KODALA,KONARK,KORAPUT,KOTPAD,KUCHINDA,MALKANGIRI,NABARANGPUR,NAYAGARH,NILGIRI,NIMAPARA,NUAPADA,ODAGAON,PADAMPUR,PARADEEP,PARALAKHEMUNDI,PATNAGARH,PATTAMUNDAI,PHULBANI,PIPLI,POLASARA,PURI,PURUSHOTTAMPUR,RAIRANGPUR,RAJGANGPUR,RAMBHA,RANPUR,RAYAGADA,REDHAKHOL,ROURKELA,SAMBALPUR,SONEPUR,SORO,SUNABEDA,SUNDARGARH,SURADA,TALCHER,TARBHA,TITILAGARH,TUSURA,UDALA,UMERKOTE,VYASANAGAR";
				String[] ulbs = ulbList.split(",");
				for(String ulb:ulbs) {
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
				String ulbList="ANANDAPUR,ANGUL,ASKA,ATHAGARH,ATHMALLIK,ATTABIRA,BALANGIR,BALASORE,BALIMELA,BALLIGUDA,BALUGAON,BANKI,BANPUR,BARBIL,BARGARH,BARIPADA,BARPALI,BASUDEVPUR,BELLAGUNTHA,BELPAHAR,BERHAMPUR,BHADRAK,BHANJANAGAR,BHAWANIPATNA,BHUBAN,BIJEPUR,BINKA,BIRMITRAPUR,BOUDHGARH,BRAJRAJNAGAR,BUGUDA,CHAMPUA,CHANDBALI,CHATRAPUR,CHIKITI,CHOUDWAR,CUTTACK,DASPALLA,DEOGARH,DHAMNAGAR,DHARAMGARH,DHENKANAL,DIGAPAHANDI,GANJAM,GOPALPUR,GUDARI,GUDAYAGIRI,GUNUPUR,HINDOL,HINJILICUT,JAGATSINGHPUR,JAJPUR,JALESWAR,JATNI,JEYPORE,JHARSUGUDA,JODA,JUNAGARH,KABISURYANAGAR,KAMAKHYANAGAR,KANTABANJI,KARANJIA,KASHINAGAR,KENDRAPARA,KEONJHARGARH,KESINGA,KHALLIKOTE,KHANDAPADA,KHARIAR,KHARIARROAD,KHORDHA,KODALA,KONARK,KORAPUT,KOTPAD,KUCHINDA,MALKANGIRI,NABARANGPUR,NAYAGARH,NILGIRI,NIMAPARA,NUAPADA,ODAGAON,PADAMPUR,PARADEEP,PARALAKHEMUNDI,PATNAGARH,PATTAMUNDAI,PHULBANI,PIPLI,POLASARA,PURI,PURUSHOTTAMPUR,RAIRANGPUR,RAJGANGPUR,RAMBHA,RANPUR,RAYAGADA,REDHAKHOL,ROURKELA,SAMBALPUR,SONEPUR,SORO,SUNABEDA,SUNDARGARH,SURADA,TALCHER,TARBHA,TITILAGARH,TUSURA,UDALA,UMERKOTE,VYASANAGAR";
				String[] ulbs = ulbList.split(",");
				for(String ulb:ulbs) {
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
			}
			
		return transactionReportList;
	}
	public List<Ulb> getUlbList(){
		return ulbRepo.findAll();
	}
}
