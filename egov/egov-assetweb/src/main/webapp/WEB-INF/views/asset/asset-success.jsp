
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script>
	function processRequest(){
		console.log('posted the message');
	}
</script>
<div id="main">
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title text-center">
					<c:out value="${message }" /><br />
					<c:forEach items="${basMessages }" var="basMessage">
						<c:out value="${basMessage }" /><br />
					</c:forEach>
				</div>
			</div>
		</div>
	</div>			
	<div class="text-center"><input type="button" name="button2" id="button2" value="Close" class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();"/></div>		
</div>					
</div>
