<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<h1>Change username for ${ user.firstname } ${ user.lastname }</h1>

<div class="well text-danger">
<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> 
Please keep in mind that changing your username will make it impossible for you to access any uploads
you have made with your current username. Please, only change your username if you know what you are doing!
</div>

<c:url var="actionUrl" value="/profile/username/change" />

<form:form 
	modelAttribute="usernameForm" 
	action="${actionUrl}" 
	method="POST">
	
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
	<spring:bind path="username">
	<div class="form-group ${status.error ? 'has-error' : ''}">
		<label for="username">Username</label> 
		<form:input
			type="text" class="form-control" id="username"
			path="username"></form:input>
		<small><form:errors path="username" cssClass="error"></form:errors></small>
	</div>
	</spring:bind>
	
	<button type="submit" class="btn btn-primary">Change username</button>
	
</form:form>

