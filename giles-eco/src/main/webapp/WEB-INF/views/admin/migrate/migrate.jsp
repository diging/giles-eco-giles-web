<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>Migrate User Data</h2>

<p>
<dl class="dl-horizontal">
  <dt>Last finished:</dt> <dd>${result.finished}</dd>
  <dt>Uploads:</dt> <dd>${result.migratedUploads}</dd>
  <dt>Documents:</dt> <dd>${result.migratedDocuments}</dd>
  <dt>Files:</dt> <dd>${result.migratedFiles}</dd>
  <dt>Requests:</dt> <dd>${result.migratedProcessingRequests}</dd>
</dl>
</p>

<c:url value="/admin/migrate" var="actionUrl" />

<form method="POST" action="${actionUrl}">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
<input type="text" name="username" class="form-control">
<button type="submit" class="btn btn-primary">Migrate</button>
</form>