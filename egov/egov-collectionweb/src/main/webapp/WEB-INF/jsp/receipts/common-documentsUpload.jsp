<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--<%@ include file="/includes/taglibs.jsp"%>--%>
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
		<table width=89%">
			<tbody>
				<tr>
					<td valign="top">
						<table id="uploadertbl" width="80%">
							<tbody>
								<tr id="row1">
									<td><input type="file" name="file" id="file1"
										onchange="isValidFile(this.id)" class="padding-10"></td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td align="center">
						<button id="attachNewFileBtn" type="button"
							class="btn btn-primary" onclick="addFileInputField()">Add
							File</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>

</div>
<script>
var maxSize = 26214400;
var inMB = maxSize/1024/1024;
var fileformatsinclude = ['doc','docx','xls','xlsx','rtf','pdf','jpeg','jpg','png','txt','zip','dxf'];


jQuery(document).ready(function($){

	// jQuery code is in here

	});

function addFileInputField() {
    var uploaderTbl = document.getElementById("uploadertbl");
    var tbody = uploaderTbl.lastChild;
    var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
    var tempTrNo = trNo - 1;
    var curFieldValue = jQuery("#file" + tempTrNo).val();
    var documentsSize = parseFloat(jQuery("#documentsSize").val()) + parseFloat(trNo);
    if(curFieldValue == "") {
        bootbox.alert("Field is empty!");
        return;
    }

    var tr = document.createElement("tr");
    tr.setAttribute("id", "row"+trNo);
    var td = document.createElement("td");
    var inputFile = document.createElement("input");
    inputFile.setAttribute("type", "file");
    inputFile.setAttribute("name", "file");
    inputFile.setAttribute("id", "file" + trNo);
    inputFile.setAttribute("class", "padding-10");
    inputFile.setAttribute("onchange", "isValidFile(this.id)");
    td.appendChild(inputFile);
    tr.appendChild(td);
    tbody.appendChild(tr);
}

function getTotalFileSize() {
    var uploaderTbl = document.getElementById("uploadertbl");
    var tbody = uploaderTbl.lastChild;
    var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
    var totalSize = 0;
    for(var i = 1; i < trNo; i++) {
        totalSize += jQuery("#file"+i)[0].files[0].size; // in bytes
        if(totalSize > maxSize) {
            bootbox.alert('File size should not exceed '+ inMB +' MB!');
            jQuery("#file"+i).val('');
            return;
        }
    }
}

function isValidFile(id) {
	
	 var myfile= jQuery("#"+id).val();
    var ext = myfile.split('.').pop();
    if(jQuery.inArray(ext.toLowerCase(), fileformatsinclude) > -1){
        getTotalFileSize();
    } else {
        bootbox.alert("Please upload .doc, .docx, .xls, .xlsx, .rtf, .pdf, jpeg, .jpg, .png, .txt, .zip and .dxf format documents only");
        jQuery("#"+id).val('');
        return false;
    }
}

function deleteFileInputField(id){
    var uploaderTbl = document.getElementById("uploadertbl");
    uploaderTbl.deleteRow(document.getElementById(id));
}

function addSelectedFiles() {
    var uploaderTbl = $("#uploadertbl");
    window.opener.$("#documentDetails").append($(uploaderTbl));
}
</script>

