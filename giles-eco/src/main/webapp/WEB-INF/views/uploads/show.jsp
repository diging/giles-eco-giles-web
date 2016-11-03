<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h3>Upload #${upload.id}</h3>

<p>The following files were uploaded as part of this upload on <span class="date">${upload.createdDate}</span>:</p>

<div class="list-group">
<c:forEach items="${docs}" var="doc">
  <div class="list-group-item clearfix">
  <div class="pull-left">
    <h4 class="list-group-item-heading">
    	<a href="<c:url value="/documents/${doc.documentId}" />">Document #${doc.documentId}</a> 
   	</h4>
   	<p>
   	<c:if test="${ doc.request.status == 'SUBMITTED' }">
   		<c:set var="labelType" value="warning" />
   	</c:if>
   	<c:if test="${ doc.request.status == 'NEW' }">
   		<c:set var="labelType" value="info" />
   	</c:if>
   	<c:if test="${ doc.request.status == 'COMPLETE' }">
   		<c:set var="labelType" value="success" />
   	</c:if>
   	<c:if test="${ doc.request.status == 'FAILED' }">
   		<c:set var="labelType" value="danger" />
   	</c:if>
   	Document status: <span class="label label-${labelType}">${doc.statusLabel}</span>
   	</p>
    <a href="${doc.metadataUrl}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a>
    <p class="list-group-item-text">Uploaded on <span class="date">${doc.createdDate}</span>.</p>
    <ul style="list-style-type:none;">
    	<li>Uploaded file: <a href="<c:url value="/files/${doc.uploadedFile.id}" />" >${doc.uploadedFile.filename}</a></li>
  		<c:if test="${not empty doc.extractedTextFile}"><li>Embedded text: <a href="<c:url value="/files/${doc.extractedTextFile.id}" />" >${doc.extractedTextFile.filename}</a></li></c:if>
    </ul>
     
    </div>
    <c:if test="${not empty doc.firstImage}">
    <div class="pull-right">
    <a href="<c:url value="/files/${doc.firstImage.id}" />" >
    <img src="<c:url value="/files/${doc.firstImage.id}/img?dh=70" />" >
    </a> 
    </div>
    </c:if>
    
    <div class="text-right">
    <c:if test="${doc.access == 'PUBLIC'}">
   	 <span class="label label-info">Public</span>
   	 <button type="button" class="btn btn-link" title="Make document private" data-toggle="modal" data-docid="${doc.id}" data-target="#makePrivateModal">
	    <i class="fa fa-unlock-alt" aria-hidden="true"></i>
	 </button>
    </c:if>
    <c:if test="${doc.access != 'PUBLIC'}">
     <span class="label label-danger">Private</span>
     <button type="button" class="btn btn-link" title="Make document public" data-toggle="modal" data-docid="${doc.id}" data-target="#makePublicModal">
     	<i class="fa fa-lock" aria-hidden="true"></i>
     </button>
    </c:if>
    </div>
  </div>
 </c:forEach>
</div>

<div class="modal fade" id="makePublicModal" tabindex="-1" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Make document public</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to make this document public? Public documents can be accessed by anyone.</p>
      </div>
      <div class="modal-footer">
         <form class="form-inline" method="POST" id="submitMakePublicForm" action="">
         	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
         	<input type="hidden" value="PUBLIC" name="access" />
         	<input name="uploadId" type="hidden" value="${upload.id}" />
	       	<button type="button" class="btn btn-default" data-dismiss="modal">No, cancel.</button>
	        <button type="submit" class="btn btn-primary">Yes, make it public!</button>
      	</form>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="makePrivateModal" tabindex="-1" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Make document private</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to make this document private? Private documents can only be accessed by you.</p>
      </div>
      <div class="modal-footer">
         <form class="form-inline" method="POST" id="submitMakePrivateForm" action="">
         	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
         	<input type="hidden" value="PRIVATE" name="access" />
         	<input name="uploadId" type="hidden" value="${upload.id}" />
	       	<button type="button" class="btn btn-default" data-dismiss="modal">No, cancel.</button>
	        <button type="submit" class="btn btn-primary">Yes, make it private!</button>
      	</form>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script>
//# sourceURL=modaldoc.js

$('#makePublicModal').on('show.bs.modal', function (event) {
  var button = $(event.relatedTarget); // Button that triggered the modal
  var docId = button.data('docid'); // Extract info from data-* attributes
  // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
  // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
  var modal = $(this)
  var url = '<c:url value="/documents/" />';
  url += docId;
  url += "/access/change";
  modal.find("#submitMakePublicForm").attr('action', url);
})

$('#makePrivateModal').on('show.bs.modal', function (event) {
  var button = $(event.relatedTarget); // Button that triggered the modal
  var docId = button.data('docid'); // Extract info from data-* attributes
  // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
  // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
  var modal = $(this)
  var url = '<c:url value="/documents/" />';
  url += docId;
  url += "/access/change";
  modal.find("#submitMakePrivateForm").attr('action', url);
})
</script>