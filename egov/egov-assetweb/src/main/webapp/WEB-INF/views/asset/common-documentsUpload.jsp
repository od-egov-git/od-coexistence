<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<script src="<cdn:url value='/resources/app/js/common/commondocumentupload.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>

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
    <div class="panel-heading">
      	<div>
			<table width="89%" border=0 id="labelid">
				<th>Add Document Details </th>
			</table>
		</div>
    </div>
	<div>
         <table width=80%">
             <tbody>
                 <tr>
                     <td valign="top">
                         <table id="uploadertbl" width="30%"><tbody>
                         <tr id="row1">
                             <td>
                                 <input type="file" name="file" id="file1" onchange="isValidFile(this.id)" class="padding-10">
                             </td>
                             <td>
                             	<input type="button" name="remove" id="remove" value ="Remove" onclick="deleteFileInputField(this.row)">
                             </td>
                         </tr>
                         </tbody></table>
                     </td>
                 </tr>
                    <!--  <tr>
                         <td align="center">
                             <button id="attachNewFileBtn" type="button" class="btn btn-primary" onclick="addFileInputField()">Add File</button>
                         </td>
                     </tr> -->
              </tbody>
         </table>
    </div>
</div>