<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h1>Signing Secrets Generated</h1>

<p>
New signing secrets have been successfully generated. 
You should now delete old app tokens and generate new ones so that other apps can continue to talk to Giles.
</p>
<p>
<a href="${pageContext.request.contextPath}/admin/apps">Take me to app tokens!</a>
</p>