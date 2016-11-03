<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h1>Delete App</h1>

<dl>
	<dt>App Name</dt>
	<dd>${app.name}</dd>
</dl>

<dl>
	<dt>App Id</dt>
	<dd>${app.id}</dd>
</dl>

<div class="alert alert-danger">
	<p>
	<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <strong>Are you sure you want to remove this app?</strong></p>
	<p>
	 The token generated for this app will be revoked and applications using it won't be able to authenticate with it anymore.
	</p>
</div>

<c:url value="/admin/apps/${app.id}/delete" var="actionUrl" />
<c:url value="/admin/apps/" var="cancelUrl" />

<form:form action="${actionUrl}" method="POST">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<button type="submit" class="btn btn-primary btn-md">Yes, delete!</button>
	<a href="${cancelUrl}" class="btn btn-default btn-md">No, cancel!</button>
</form:form>
