<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h1>Generate Signing Secrets</h1>

<form:form
	action="${pageContext.request.contextPath}/admin/system/auth"
	method="POST">
	
	<div class="alert alert-danger" role="alert">
	<i class="fa fa-exclamation-triangle" aria-hidden="true"></i>
	Generating new signing secrets will cause all previously issued tokens to be invalidated. 
	This means that any app that is currently set up to talk to Giles won't be able to do so anymore.
	Only proceed if you really know what you are doing!
	</div>
	
	<button type="submit" class="btn btn-default btn-md">I understand. Generate new secrets!</button>
	<a href="${pageContext.request.contextPath}" class="btn btn-primary btn-md">Cancel and get me out of here!</a>
	
</form:form>