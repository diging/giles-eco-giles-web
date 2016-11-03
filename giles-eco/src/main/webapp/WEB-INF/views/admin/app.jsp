<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<h1>Registered App</h1>

This app uses <span class="label label-warning">${providerName}</span> for authentication.

<dl>
	<dt>App Name</dt>
	<dd>${app.name}</dd>
</dl>

<dl>
	<dt>App Id</dt>
	<dd>${app.id}</dd>
</dl>

<dl>
	<dt>Provider Client ID</dt>
	<dd>${app.providerClientId}</dd>
</dl>

<c:if test="${not empty token}" >
<div class="panel panel-warning">
	<div class="panel-heading">
		<h3 class="panel-title">App Access Token</h3>
	</div>
	<div class="panel-body">
	<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> Make sure to copy this token and keep it save. As soon as you leave this page, there is no way to retrieve this token again. Don't share this token with anyone as anyone with this token can use it to generate API tokens.
	<div class="well well-sm"><span class="breakwords">${token.token}</span></div>
	</div>
</div>
</c:if>
<c:if test="${empty token}">
<div class="panel panel-default">
  <div class="panel-body">
    There is no way to retrieve a token if you have lost or forgotten it. Please create a new token.
  </div>
</div>
</c:if>