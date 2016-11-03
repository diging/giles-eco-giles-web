<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>Users</h2>

<table class="table table-striped">
  <tr>
  	<th width="20%">Username</th>
  	<th>Full Name</th>
  	<th width="30%">Email</th>
  	<th>Provider User Id</th>
  	<th>Provider</th>
  	<th></th>
  	<th width="5%">Status</th>
  	<th width="5%"></th>
  </tr>
  
  <c:forEach items="${users}" var="user">
  <tr <c:if test="${user.accountStatus == 'ADDED' }">class="warning"</c:if>>
  	<td>${user.username}</td>
  	<td>${user.fullname}</td>
  	<td>${user.email}</td>
  	<td>${user.userIdOfProvider}</td>
  	<td>${user.provider}</td>
  	<td>
  		<c:if test="${user.roles.contains('ROLE_ADMIN')}"><span class="label label-danger">Admin</span></c:if>
  		<c:if test="${user.roles.contains('ROLE_USER')}"><span class="label label-info">User</span></c:if>	
  	</td>
  	<td>${user.accountStatus}</td>
  	<td>
  		<form class="pull-right" action="<c:url value="/users/user/${user.username}/remove" />" method="POST">
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  		<button style="padding: 0px;" class="btn-link" type="submit" title="Remove user account"><i class="fa fa-user-times" aria-hidden="true"></i></button>
  		</form>
  		
  		<c:if test="${user.accountStatus != 'APPROVED'}">
  		<form action="<c:url value="/users/user/${user.username}/approve" />" method="POST">
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  		<button style="padding: 0px;" class="btn-link" type="submit" title="Approve user account"><i class="fa fa-thumbs-o-up" aria-hidden="true"></i></button>
  		</form>
  		</c:if>
  		
  		<c:if test="${user.accountStatus == 'APPROVED'}">
  		<form class="pull-right" class="form-inline" action="<c:url value="/users/user/${user.username}/revoke" />" method="POST">
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  		<button style="padding: 0px;" class="btn-link" type="submit" title="Revoke user account"><i class="fa fa-thumbs-o-down" aria-hidden="true"></i></button>
  		</form>
  		</c:if>
  		
  		<c:if test="${user.accountStatus == 'APPROVED' and not user.roles.contains('ROLE_ADMIN')}">
  		<form class="pull-right" action="<c:url value="/users/user/${user.username}/role/add" />" method="POST">
  		<input type="hidden" name="role" value="ROLE_ADMIN" />
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  		<button style="padding: 0px;" class="btn-link" type="submit" title="Make user admin"><i class="fa fa-key" aria-hidden="true"></i></button>
  		</form>
  		</c:if>
  		
  		<c:if test="${user.accountStatus == 'APPROVED' and user.roles.contains('ROLE_ADMIN')}">
  		<form class="pull-right" action="<c:url value="/users/user/${user.username}/role/remove" />" method="POST">
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  		<input type="hidden" name="role" value="ROLE_ADMIN" />
  		<button style="padding: 0px;" class="btn-link" type="submit" title="Revoke admin rights"><i class="fa fa-user" aria-hidden="true"></i></button>
  		</form>
  		</c:if>
  	</td>
  </tr>
  </c:forEach>
  	
</table>