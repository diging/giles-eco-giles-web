<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>



<div class="well">

	<h3>
		Kafka listeners are currently:
		<c:if test="${listenerStatus}">
			<span class="label label-success pull-right">Active</span>
		</c:if>
		<c:if test="${not listenerStatus}">
			<span class="label label-danger pull-right">Stopped</span>
		</c:if>
	</h3>

    <div style="margin-top: 70px;"><center>
	<c:if test="${listenerStatus}">
		<form:form
			action="${pageContext.request.contextPath}/admin/kafka/listeners/stop"
			method="POST">
			<input type="submit" value="Stop Kafka Listeners"
				class="btn btn-lg btn-danger" />
		</form:form>
		<p><br>
        Kafka listeners have been started !
        </p>
	</c:if>

	<c:if test="${not listenerStatus}">
		<form:form
			action="${pageContext.request.contextPath}/admin/kafka/listeners/start"
			method="POST">
			<input type="submit" value="Start Kafka Listeners"
				class="btn btn-lg btn-success" />
		</form:form>
		<p><br>
		Kafka listeners have been stopped !
	    </p>
	</c:if>
	</center></div>
</div>