<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h1>Your uploads</h1>

<p>
You have ${totalUploads} uploads.
</p>

<script type="text/javascript" src="<c:url value="/resources/jquery-pagination/jquery.simplePagination.js"/>"></script>
<script>
jQuery(document).ready(function($) {
    $(".link-row").click(function() {
        window.document.location = $(this).data("href");
    });
    
    $('.pagination').pagination({
        items: ${count},
        itemOnPage: 20,
        currentPage: ${page},
        cssStyle: '',
        prevText: '<span aria-hidden="true">&laquo;</span>',
        nextText: '<span aria-hidden="true">&raquo;</span>',
        onInit: function () {
            // fire first page loading
        },
        onPageClick: function (page, evt) {
            window.document.location = "<c:url value="/uploads" />" + "?page=" + page + "&sortDir=${sortDir}";
        }
    });
    $('.pagination').show();
});

$(".link-row").css("cursor", "pointer");
 </script>

<nav aria-label="Page navigation">
  <ul class="pagination" style="display: none">
    
<c:forEach begin="1" end="${count}" var="val">
    <li <c:if test="${val == page}">class="active"</c:if>><a href="<c:url value="/uploads?page=${val}&sortDir=${sortDir}" />"><c:out value="${val}"/></a></li>
</c:forEach>
    
  </ul>
</nav>

<table class="table table-striped">
	<thead>
	<tr>
		<th width="20%">Uploaded on &nbsp;&nbsp;<a href="<c:url value="/uploads?page=${page}&sortDir=${oppSortDir}" />"><i class="fa fa-sort" aria-hidden="true"></i></a></th>
		<th width="15%">Upload Id</th>
		<th># of Documents</th>
		<th>Status</th>
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
			<img src="https://img.shields.io/badge/${upload.status.subject}-${upload.status.status}-${upload.status.color}.svg">
		</td>
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
  <ul class="pagination" style="display: none">
    
<c:forEach begin="1" end="${count}" var="val">
    <li <c:if test="${val == page}">class="active"</c:if>><a href="<c:url value="/uploads?page=${val}&sortDir=${sortDir}" />"><c:out value="${val}"/></a></li>
</c:forEach>
    
  </ul>
</nav>


    