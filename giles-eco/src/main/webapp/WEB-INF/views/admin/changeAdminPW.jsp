<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2>Change Admin Password</h2>

<div class="alert alert-warning" role="alert">
<b>Warning!</b> Only proceed if you're absolutely sure you want to change the password of this administrator.
</div>

<c:url value="/admin/system/admins/password/change" var="actionUrl" />

<form:form modelAttribute="adminUser" action="${actionUrl}" method="POST">
  <form:input type="hidden" path="username" value="${username}" />
  <div class="form-group">
    <label for="password">Old Password</label>
    <form:input path="oldPassword" type="password" class="form-control" id="oldPassword" placeholder="Old password" />
    <small><form:errors path="oldPassword" cssClass="error"></form:errors></small>
  </div>
  <div class="form-group">
    <label for="password">New Password</label>
    <form:input path="newPassword" type="password" class="form-control" id="password" placeholder="New password" />
    <small><form:errors path="newPassword" cssClass="error"></form:errors></small>
  </div>
  <div class="form-group">
    <label for="password">Retype new Password</label>
    <form:input path="retypedPassword" type="password" class="form-control" id="retypedPassword" placeholder="Retype password" />
    <small><form:errors path="retypedPassword" cssClass="error"></form:errors></small>
  </div>
  
  <button type="submit" class="btn btn-primary">Change Password</button>
</form:form>