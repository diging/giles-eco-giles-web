<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h1>Profile</h1>

<p>
	Account has been <span class="label label-warning">${ user.accountStatus }</span>
</p>

<p>
<i class="fa fa-user" aria-hidden="true"></i> Roles:
	<c:if test="${user.roles.contains('ROLE_ADMIN')}"><span class="label label-danger">Admin</span></c:if>
	<c:if test="${user.roles.contains('ROLE_USER')}"><span class="label label-info">User</span></c:if>	
</p>

<h3>Details</h3>
<dl>
  <dt>Username:</dt>
  <dd>${ user.username }
  <br>
  <a href="<c:url value="/profile/username/change" />"><i class="fa fa-pencil" aria-hidden="true"></i> Change</a>
  </dd>
</dl>

<dl>
	<dt>First name:</dt>
	<dd>${ user.firstname }</dd>
</dl>

<dl>
	<dt>Last name:</dt>
	<dd>${ user.lastname }</dd>
</dl>

<dl>
	<dt>Email:</dt>
	<dd>${ user.email }</dd>
</dl>