package org.egov.collection.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.egov.infra.microservice.models.RemitancePOJO;
import org.egov.infstr.services.PersistenceService;
import org.hibernate.SQLQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/remittanceBankdetail/")
public class AjaxControllerBankRemittance {
	
	@Autowired
    protected transient PersistenceService persistenceService;
	@RequestMapping(value = "gldetails")
	  @ResponseBody public List<RemitancePOJO> getTags(@RequestParam("receiptNo")
	  String receiptNo) { 
		  System.out.println(":::::::::Receipt No:::: "+receiptNo);
		  List<RemitancePOJO> details=new ArrayList<>(); 
		  SQLQuery query = null;
		  List<Object[]> rows = null; 
		  RemitancePOJO r = null;
	  
		  try { 
		  query = this.persistenceService.getSession().
				  createSQLQuery("select (select c2.name from chartofaccounts c2 where c2.glcode=gl.glcode),gl.glcode,gl.creditamount from generalledger gl where voucherheaderid =(select vmis.voucherheaderid from vouchermis vmis where vmis.reciept_number =:receipt_no) ) and gl.creditamount >0"); 
		  query.setString("receipt_no", receiptNo); 
		  query =this.persistenceService.getSession().
				  createSQLQuery("select c2.name,gl.glcode,sum(gl.creditamount) from chartofaccounts c2,generalledger gl,vouchermis vmis "
						  +" where c2.glcode = gl.glcode and gl.voucherheaderid =vmis.voucherheaderid and vmis.reciept_number in ('"
						  +receiptNo+"') and gl.creditamount >0 group by c2.name,gl.glcode ");
	  
	  System.out.println("::::::>>>>>"+query);
	  rows = query.list();
	  System.out.println("row size "+rows.size()); 
	  if(rows.size()!=0) 
	  {
		  for(Object[] e : rows) 
		  {	  
			  r = new RemitancePOJO(); 
			  r.setGlName((null!=e[0]?e[0].toString():null));
			  r.setGlcode((null!=e[1]?e[1].toString():null));
			  r.setAmount((null!=e[2]?e[2].toString():null)); 
			  details.add(r);
		  } 
	  }
	  return details; 
	  }
	  catch (Exception e) { 
		  e.printStackTrace(); 
	  } 
	  String n="Controller from ajax";
	  return details; 
	 }

}
