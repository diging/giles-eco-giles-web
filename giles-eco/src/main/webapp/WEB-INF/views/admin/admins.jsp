<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>Admin User</h2>

<table class="table table-striped">
  <tr>
  	<th width="20%">Username</th>
  	
  	<th width="5%"></th>
  </tr>
  	
  <c:forEach items="${admins}" var="admin">
  <tr>
      <td>${admin.username}</td>
      <td align="right">
        <c:if test="${currentUser == admin.username}">
         <a href="<c:url value="/admin/system/admins/password/change" />">Change Password</a>
        </c:if> 
      </td>
  </tr>
  </c:forEach>
</table>