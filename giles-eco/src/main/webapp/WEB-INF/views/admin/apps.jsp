<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h1>Registered Apps</h1>

<p class="text-right">
<a href="<c:url value="/admin/apps/register" />" class="btn btn-sm btn-primary">Register App</a>
</p>
<table class="table table-striped">
  <tr>
  	<th width="20%">App name</th>
  	<th width="">App Id</th>
  	<th width="15%"></th>
  </tr>
  
  <c:forEach items="${apps}" var="app">
  <tr>
  	<td><a href="<c:url value="/admin/apps/${app.id}" />">${app.name}</a></td>
  	
  	<td>${app.id}</td>
  	
  	<td><a href="<c:url value="/admin/apps/${app.id}/delete" />"><i class="fa fa-trash-o" aria-hidden="true"></i> Delete App</a>
  </tr>
  
  </c:forEach>
  
</table>