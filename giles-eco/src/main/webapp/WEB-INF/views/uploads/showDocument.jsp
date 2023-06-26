<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<ol class="breadcrumb">
  <li>Upload</li>
  <li><a href="<c:url value="/uploads/${document.uploadId}" />">${document.uploadId}</a></li>
</ol>

<h1>${document.id}</h1>

<p>
   <c:forEach items="${document.badges}" var="badge"><img src="https://img.shields.io/badge/${badge.subject}-${badge.status}-${badge.color}.svg"> </c:forEach>
</p>


<div class="row">
<div class="col-md-9">
<p>
	<c:if test="${document.access == 'PUBLIC'}">
		<span class="label label-info">Public</span>
	</c:if>
	<c:if test="${document.access == 'PRIVATE'}">
		<span class="label label-danger">Private</span>
	</c:if>

	&nbsp; &nbsp; <a href="${document.uploadedFile.metadataLink}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a>
	<button type="button" class="btn btn-link" title="Delete" data-toggle="modal" data-docid="${document.id}" data-target="#deleteDocument">
		<i class="fa fa-trash" aria-hidden="true"></i>
		Delete Document
	</button>
	
</p>

<div class="modal fade" id="deleteDocument" tabindex="-1" role="dialog">
  	<div class="modal-dialog">
    	<div class="modal-content">
      		<div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        		<h4 class="modal-title">Delete Document</h4>
      		</div>
      		<div class="modal-body">
       			<p>Are you sure you want to delete this document.</p>
      		</div>
      		<div class="modal-footer">
      			<button type="button" class="btn btn-default" data-dismiss="modal">No, cancel.</button>
	        	<button type="button" id="deleteDocumentConfirm" data-docid="${document.id}" class="btn btn-primary">Yes!</button>
      		</div>
    	</div>
  	</div>
</div>
<p>Uploaded on <span class="date">${document.createdDate}</span>.</p>


<dl class="dl-horizontal">
  <dt>Uploaded file: </dt>
  <dd>
  	<tiles:insertTemplate template="fileProcessing.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${document.uploadedFile}" type="object" /></tiles:insertTemplate>
  	<c:if test="${document.uploadedFile.storedStatus == 'FAILED'}"> <span class="label label-danger">Storage Failed</span></c:if> <a href="<c:url value="/files/${document.uploadedFile.id}" />" >
  	${document.uploadedFile.filename}</a>&nbsp; &nbsp; 
  	
  	<c:forEach items="${document.uploadedFile.badges}" var="badge"><img src="https://img.shields.io/badge/${badge.subject}-${badge.status}-${badge.color}.svg"></c:forEach>
  </dd>
  <dt>Embedded text: </dt>
  <dd>
  <c:if test="${not empty document.extractedTextFile}">
  	<tiles:insertTemplate template="fileProcessing.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${document.extractedTextFile}" type="object" /></tiles:insertTemplate>
  	
  	<a href="<c:url value="/files/${document.extractedTextFile.id}" />" >${document.extractedTextFile.filename}</a> &nbsp; &nbsp; 
  	<c:forEach items="${document.extractedTextFile.badges}" var="badge"><img src="https://img.shields.io/badge/${badge.subject}-${badge.status}-${badge.color}.svg"></c:forEach>
  </c:if>
  </dd>
  <dt>Additional Files: </dt>
  <dd>
    <c:if test="${not empty document.uploadedFile.additionalFiles}">
        <tiles:insertTemplate template="additionalFileTemplate.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${document.uploadedFile}" type="object" /></tiles:insertTemplate>
    </c:if>
    <c:if test="${not empty document.extractedTextFile.additionalFiles}">
        <tiles:insertTemplate template="additionalFileTemplate.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${document.extractedTextFile}" type="object" /></tiles:insertTemplate>
    </c:if>
  </dd>
</dl>

<h4>Pages</h4>

<c:if test="${not empty document.pages}">
<c:forEach items="${document.pages}" var="page">

<dl class="dl-horizontal clearfix">
	<dt><span class="label label-default">Page ${page.pageNr}</span>&nbsp; &nbsp; Image: </dt>
	<dd>
	    <c:if test="${page.imageFileStatus == 'FAILED' }">
	       There was an error creating the image file. <span class="fas fa-info-circle" title="${page.imageFileErrorMsg}" />
	    </c:if>
		<c:if test="${not empty page.imageFile.filename}">
		<tiles:insertTemplate template="fileProcessing.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page.imageFile}" type="object" /></tiles:insertTemplate>
		<a href="<c:url value="/files/${page.imageFile.id}" />" >${page.imageFile.filename}</a>
        &nbsp; &nbsp; <c:forEach items="${page.imageFile.badges}" var="badge"><img src="https://img.shields.io/badge/${badge.subject}-${badge.status}-${badge.color}.svg"></c:forEach>
		</c:if>
	</dd>
	
	<dt> Text: </dt>
	<dd>
	    <c:if test="${page.textFileStatus == 'FAILED' }">
           There was an error extracting the embedded text. <span class="fas fa-info-circle" title="${page.textFileErrorMsg}" />
        </c:if>
		<c:if test="${not empty page.textFile.filename}">
		<tiles:insertTemplate template="fileProcessing.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page.textFile}" type="object" /></tiles:insertTemplate>
		<a href="<c:url value="/files/${page.textFile.id}" />" >${page.textFile.filename}</a>&nbsp; &nbsp; 
        <c:forEach items="${page.textFile.badges}" var="badge"><img src="https://img.shields.io/badge/${badge.subject}-${badge.status}-${badge.color}.svg"></c:forEach>
		</c:if>
	</dd>
	<dt> OCR Result: </dt>
	<dd>
	    <c:if test="${page.ocrFileStatus == 'FAILED' }">
           There was an error running OCR on the image. <span class="fas fa-info-circle" title="${page.ocrFileErrorMsg}" />
        </c:if>
		<c:if test="${not empty page.ocrFile.filename}">
		<tiles:insertTemplate template="fileProcessing.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page.ocrFile}" type="object" /></tiles:insertTemplate>
		<a href="<c:url value="/files/${page.ocrFile.id}" />" >${page.ocrFile.filename}</a>&nbsp; &nbsp; 
        <c:forEach items="${page.ocrFile.badges}" var="badge"><img src="https://img.shields.io/badge/${badge.subject}-${badge.status}-${badge.color}.svg"></c:forEach>
		</c:if>
	</dd>
	<dt> Additional Files: </dt>
    <dd>
        <c:if test="${not empty page.imageFile.additionalFiles}">
            <tiles:insertTemplate template="additionalFileTemplate.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page.imageFile}" type="object" /></tiles:insertTemplate>
        </c:if>
        <c:if test="${not empty page.textFile.additionalFiles}">
            <tiles:insertTemplate template="additionalFileTemplate.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page.textFile}" type="object" /></tiles:insertTemplate>
        </c:if>
        <c:if test="${not empty page.ocrFile.additionalFiles}">
            <tiles:insertTemplate template="additionalFileTemplate.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page.ocrFile}" type="object" /></tiles:insertTemplate>
        </c:if>
        <c:if test="${not empty page.additionalFiles}">
            <tiles:insertTemplate template="additionalFileTemplate.jsp" flush="true" ><tiles:putAttribute name="fileBean" value="${page}" type="object" /></tiles:insertTemplate>
        </c:if>
    </dd>
</dl>
</c:forEach>
</c:if>
</div>

<div class="col-md-3">
<c:if test="${not empty document.firstImage}">
<div class="pull-right">
    <a href="<c:url value="/files/${document.firstImage.id}" />" >
    <img src="<c:url value="/files/${document.firstImage.id}/img?dw=200" />" >
    </a> 
</div>
</c:if>
</div>
</div>
<script>
const confirmBtn = document.getElementById('deleteDocumentConfirm');
confirmBtn.addEventListener('click', function() {
	  var docId = "${document.id}";
	  var modal = $(this);
	  var deleteUrl = "/giles/documents/${document.id}?${_csrf.parameterName}=${_csrf.token}";
	  $.ajax({
		  url: deleteUrl,
		  method: "POST",
		  success: function (result) {  
              window.location.href = '/giles/uploads'; // redirect          
          },
          error: function (e) {
              console.log(e);
          }
	  });
	})
</script>
