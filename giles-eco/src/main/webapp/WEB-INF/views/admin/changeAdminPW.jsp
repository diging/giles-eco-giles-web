<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2>Change Admin Password for ${adminUser.username}</h2>

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
        <small style="float: left; line-height: 20px">Password Strength &nbsp; &nbsp;</small>
        <div id="password-strength">
        </div>
        
    </div>
  <div class="form-group" style="clear:both;">
    <label for="password">Retype new Password</label>
    <form:input path="retypedPassword" type="password" class="form-control" id="retypedPassword" placeholder="Retype password" />
    <small><form:errors path="retypedPassword" cssClass="error"></form:errors></small>
  </div>
  
  <button type="submit" class="btn btn-primary">Change Password</button>
</form:form>

<script type="text/javascript" src="<c:url value="/resources/password-score/password-score.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/password-score/password-score-options.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/bootstrap-password-meter/bootstrap-strength-meter.js" />"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $('#password').strengthMeter('progressBar', {
            container: $('#password-strength')
        });
    });
</script>
