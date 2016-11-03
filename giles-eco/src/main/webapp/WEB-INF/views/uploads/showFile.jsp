<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<ol class="breadcrumb">
  <li>Upload</li>
  <li><a href="<c:url value="/uploads/${file.uploadId}" />">${file.uploadId}</a></li>
  <li>Document</li>
  <li><a href="<c:url value="/documents/${file.documentId}" />">${file.documentId}</a></li>
</ol>

<h1>${file.filename}</h1>

<div class="row">
<div class="col-md-4">

<p>
	<c:if test="${file.access == 'PUBLIC'}">
		<span class="label label-info">Public</span>
	</c:if>
	<c:if test="${file.access == 'PRIVATE'}">
		<span class="label label-danger">Private</span>
	</c:if>
	&nbsp; &nbsp; <a href="${file.metadataLink}"><i class="fa fa-globe"
		aria-hidden="true"></i> View/edit metadata</a>

</p>

<p>Uploaded on <span class="date">${file.uploadDate}</span>.</p>

<c:if test="${not empty derivedFrom}">

	<span class="label label-warning">Derived</span> This file is derived from <a
				href="<c:url value="/files/${derivedFrom.id}" />">${derivedFrom.id}</a>.
		
</c:if>

</div>
<div class="col-md-8" >
<div class="text-center">
<div class="panel panel-default">
  <div class="panel-body" style="min-height: 350px">
	<c:choose>
		<c:when
			test="${file.contentType == 'application/pdf' or file.contentType == 'text/plain'}">
			
			<a style="margin-top: 40px;" class="btn btn-primary btn-md"
				href="<c:url value="/files/${file.id}/content" />"><i class="fa fa-download" aria-hidden="true"></i> Download
				${file.filename}</a>
		</c:when>
		<c:otherwise>
			
				<img src="<c:url value="/files/${file.id}/img?dw=600" /> ">
			
		</c:otherwise>
	</c:choose>
</div>
</div>
</div>

</div>
</div>
