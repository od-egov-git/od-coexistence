<%--<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>--%>
<%--<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>--%>
<%@ taglib uri="/WEB-INF/tags/struts-tags.tld" prefix="s" %>
<%--<link rel="stylesheet" type="text/css"
	href="/services/EGF/resources/css/jquery-ui/css/smoothness/jquery-ui-1.8.4.custom.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="../resources/css/jquery/ui.jqgrid.css" />--%>

<style>
    .file-ellipsis {
        width : auto !Important;
    }

    .padding-10
    {
        padding:10px;
    }
    .div-height{
        height:30%
    }
</style>
<div align="center" class="panel panel-primary div-height" data-collapsed="0" style=" scrollable:true;">
   
              		
   
    
			 <table align="center">
			 <th>View Document Details <br/> <br/></th>
				
					  <s:if test="%{voucherHeader.documentDetail.size()>0}">
				   		          <s:iterator var="p" value="%{voucherHeader.documentDetail}" status="s">
				   		         <tr>
				                    <td class="padding-10"> 
				                      <a target="_blank" href="/services/EGF/voucher/preApprovedVoucher-downloadVoucherDoc.action?voucherHeaderId=<s:property value='%{objectId}'/>&fileStoreId=<s:property value='%{fileStore.fileStoreId}'/>"><s:property value="fileStore.fileName"/></a>
				                    </td>
				                    <td class="padding-10">
				                      <span><a href="/services/EGF/voucher/journalVoucherModify-deleteVoucherDoc.action?voucherHeaderId=<s:property value='%{objectId}'/>&fileid=<s:property value='%{id}'/> ">Remove</a></span>
				                    </td>
				                    
				                 </tr><br />
				                  </s:iterator>
				      </s:if>

				
			</table>

       
</div>