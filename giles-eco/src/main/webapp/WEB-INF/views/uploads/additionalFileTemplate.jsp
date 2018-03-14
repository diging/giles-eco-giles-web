<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<tilesx:useAttribute name="fileBean" id="fileBean"/>

  <c:forEach items="${fileBean.additionalFiles}" var="entry" >
  ${entry.value.processor}: <c:if test="${entry.value.status == 'COMPLETE'}"><i class="fas fa-hdd" aria-hidden="true" title="File has been stored."></i></c:if>
   <a href="<c:url value="/files/${entry.value.fileId}" />" >${entry.value.filename}</a><br>
  </c:forEach>