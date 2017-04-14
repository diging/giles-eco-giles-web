<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h1>Requests</h1>

<p>
Last resending: ${resendResult.completionTime}
<br>
Resent request count: ${resendResult.requestCount}
</p>

<form:form action="${pageContext.request.contextPath}/admin/requests/resend" method="POST">
<button type="submit" class="btn btn-primary">Resend completion requests</button>
</form:form>