<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h1>Your uploads</h1>

<p>
You have ${totalUploads} uploads.
</p>

<table class="table table-striped">
	<thead>
	<tr>
		<th width="20%">Uploaded on &nbsp;&nbsp;<a href="<c:url value="/uploads?page=${page}&sortDir=${oppSortDir}" />"><i class="fa fa-sort" aria-hidden="true"></i></a></th>
		<th width="15%">Upload Id</th>
		<th># of Documents</th>
		<th>Uploaded Files</th>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${uploads}" var="upload">
	<tr class='link-row' data-href='<c:url value="/uploads/${upload.id}" />'>
		<td><span class="date">${upload.createdDate}</span></td>
		<td><a href="<c:url value="/uploads/${upload.id}" />">${upload.id}</a></td>
		<td>${upload.nrOfDocuments} uploaded document<c:if test="${upload.nrOfDocuments>1}">s</c:if></td>
		<td>
		<c:forEach items="${upload.uploadedFiles}" var="file" varStatus="loop">
			${file.filename}<c:if test="${!loop.last}">,</c:if>
		</c:forEach>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<nav aria-label="Page navigation">
  <ul class="pagination">
    <li <c:if test="${page == 1}">class="disabled"</c:if>>
      <a <c:if test="${page > 1}">href="<c:url value="/uploads?page=${page - 1}&sortDir=${sortDir}" />"</c:if> aria-label="Previous">
        <span aria-hidden="true">&laquo;</span>
      </a>
    </li>
<c:forEach begin="1" end="${count}" var="val">
    <li <c:if test="${val == page}">class="active"</c:if>><a href="<c:url value="/uploads?page=${val}&sortDir=${sortDir}" />"><c:out value="${val}"/></a></li>
</c:forEach>
    <li <c:if test="${page == count}">class="disabled"</c:if>>
      <a <c:if test="${page < count}">href="<c:url value="/uploads?page=${page + 1}&sortDir=${sortDir}" />"</c:if> aria-label="Next">
        <span aria-hidden="true">&raquo;</span>
      </a>
    </li>
  </ul>
</nav>

<script>
jQuery(document).ready(function($) {
    $(".link-row").click(function() {
        window.document.location = $(this).data("href");
    });
});

$(".link-row").css("cursor", "pointer");
</script>