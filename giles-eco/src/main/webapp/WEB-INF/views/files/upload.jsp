<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet"
	href="<c:url value="/resources/jquery-file-upload/css/style.css" />">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet"
	href="<c:url value="/resources/jquery-file-upload/css/jquery.fileupload.css" />">

<h2>Select files to upload</h2>

<div class="alert alert-info" role="alert">
	<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> The
	upload limit for files is 50MB.
</div>

<form class="form-inline">
<input id="csrfInput" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  				
	<div class="form-group">
		<label>Make uploaded files: </label> <select class="form-control"
			id="accessInput">
			<option value="PRIVATE" selected>Private</option>
			<option value="PUBLIC">Public</option>
		</select>
	</div>
	<br>
	<div class="form-group" id="upload-field">
		<span class="btn btn-success fileinput-button"> <i
			class="fa fa-plus" aria-hidden="true"></i> <span>Add files...</span>
			<!-- The file input field used as target for the file upload widget -->
			<input id="fileupload" type="file" name="file" multiple>
		</span>
	</div>
	<br> <br>
	<!-- The global progress bar -->
	<div id="progress" class="hidden">
		<i class="fa fa-refresh spinner-animate" aria-hidden="true"></i>
		Uploading your files. Please wait...
	</div>
	<div id="uploadDoneSuccess" class="hidden">
		<i class="fa fa-check-circle" aria-hidden="true"></i> Your upload
		successfully finished!
	</div>
	<div id="uploadDoneFail" class="hidden">
		<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> One or
		more files could not be uploaded.
	</div>
	<!-- The container for the uploaded files -->
	<ul id="files" class="list-group" style="margin-top: 35px;">

	</ul>

	<div id="failure_box" class="alert alert-danger hidden" role="alert">
		<strong>Upload Failed</strong> <br> Reason: <span
			id="failure_reason"></span>
	</div>
</form>

<a href="" id="jarsLink" class="btn btn-primary disabled">Add
	metadata in Jars</a>

<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/vendor/jquery.ui.widget.js" />"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script
	src="//blueimp.github.io/JavaScript-Load-Image/js/load-image.all.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script
	src="//blueimp.github.io/JavaScript-Canvas-to-Blob/js/canvas-to-blob.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.iframe-transport.js" />"></script>
<!-- The basic File Upload plugin -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload.js" />"></script>
<!-- The File Upload processing plugin -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-process.js" />"></script>
<!-- The File Upload image preview & resize plugin -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-image.js" />"></script>
<!-- The File Upload audio preview plugin -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-audio.js" />"></script>
<!-- The File Upload video preview plugin -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-video.js" />"></script>
<!-- The File Upload validation plugin -->
<script
	src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-validate.js" />"></script>

<script>
    //# sourceURL=upload.js
    var uploadIds = [];
    var jarsUrl = '${jars.url}${metadata.upload.add}';

    $(function() {
        'use strict';

        // Initialize the jQuery File Upload widget:
        $('#fileupload').fileupload({
            // Uncomment the following to send cross-domain cookies:
            //xhrFields: {withCredentials: true},
            url : '<c:url value="/files/upload" />?${_csrf.parameterName}=${_csrf.token}',
            singleFileUploads : false,
            submit : function(e, data) {
                var input = $('#accessInput');
                var csrf = $("#csrfInput");
                data.formData = {
                    access : input.val(),
                };
                return true;
            },
            progressall : function(e, data) {
                $('#progress').removeClass("hidden");
            },
            done : function(e, data) {
                $('#upload-field').prop("disabled", true);
                var response = JSON.parse(data.result);
                var success = true;
                response.files.forEach(function(element, index) {
                    var newItem = '<li class="list-group-item">';
                    if (element.status == 0) {
                        newItem += '<span class="badge badge-success">Success</span>';
                    } else {
                        newItem += '<span class="badge badge-failure">Failure</span>';
                        success = false;
                    }
                    newItem += element.name;
                    newItem += '</li>';
                    $('#files').append(newItem);
                    if (success) {
                        if (!uploadIds.includes(element.uploadId)) {
                            uploadIds.push(element.uploadId);
                        }
                    }
                });

                var uploadIdsString = "";
                uploadIds.forEach(function(element, index) {
                    uploadIdsString += "uploadIds=";
                    uploadIdsString += element;
                    uploadIdsString += "&";
                });

                resetBar();
                if (success) {
                    $('#jarsLink').attr('href', jarsUrl + "?" + uploadIdsString);
                    $('#jarsLink').removeClass('disabled');
                    $('#uploadDoneSuccess').removeClass("hidden");
                } else {
                    $('#uploadDoneFail').removeClass("hidden");
                }
            },
            fail : function(e, data) {
                var response = JSON.parse(data.jqXHR.responseText);
                $('#failure_reason').html(response.error);
                $('#failure_box').removeClass("hidden");
                resetBar();
            },
            start : function(e, data) {
                $('#failure_box').addClass("hidden");
                resetBar();
            }
        });

        function resetBar() {
            $('#progress').addClass("hidden");
            $('#uploadDoneSuccess').addClass("hidden");
            $('#uploadDoneFail').addClass("hidden");
        }
    });
</script>
