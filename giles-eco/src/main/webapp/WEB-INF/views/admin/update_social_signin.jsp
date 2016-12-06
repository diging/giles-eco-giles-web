<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h1>Update Social SignIn Configuration</h1>

<p>
Below you can update the configuration of the social signin providers you are using with Giles.
</p>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingOne">
      <h4 class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
          GitHub SignIn
        </a>
      </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
      <div class="panel-body">
        <form:form modelAttribute="githubConfig" id="githubUpdateForm"
        	action="${pageContext.request.contextPath}/admin/system/social/github"
			method="POST">
			<div class="form-group">
				<label for="githubSecret">GitHub Client ID</label>
				<form:input type="text" class="form-control" id="clientId"
					placeholder="GitHub Client ID" path="clientId" value=""></form:input>
				<small><form:errors class="error" path="clientId"></form:errors></small>
			</div>
			<div class="form-group">
				<label for="githubSecret">GitHub Secret</label>
				<form:input type="text" class="form-control" id="secret"
					placeholder="GitHub Secret" path="secret" value=""></form:input>
				<small><form:errors class="error" path="secret"></form:errors></small>
			</div>
			
			<button id="githubFormSubmit" class="btn btn-primary btn-md">Save</button>
		</form:form>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
          Google SignIn
        </a>
      </h4>
    </div>
    <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
      <div class="panel-body">
        <form:form modelAttribute="googleConfig" id="googleUpdateForm"
        	action="${pageContext.request.contextPath}/admin/system/social/google"
			method="POST">
			<div class="form-group">
				<label for="googleClientid">Google Client ID</label>
				<form:input type="text" class="form-control" id="clientId"
					placeholder="Google Client ID" path="clientId" value=""></form:input>
				<small><form:errors class="error" path="clientId"></form:errors></small>
			</div>
			<div class="form-group">
				<label for="googleSecret">Google Secret</label>
				<form:input type="text" class="form-control" id="secret"
					placeholder="Google Secret" path="secret" value=""></form:input>
				<small><form:errors class="error" path="secret"></form:errors></small>
			</div>
			
			<button id="googleFormSubmit" class="btn btn-primary btn-md">Save</button>
		</form:form>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingThree">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
          MITREid SignIn
        </a>
      </h4>
    </div>
    <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
      <div class="panel-body">
        <form:form modelAttribute="mitreidConfig" id="mitreidUpdateForm"
        	action="${pageContext.request.contextPath}/admin/system/social/mitreid"
			method="POST">
			<div class="form-group">
				<label for="clientId">MITREid Client ID</label>
				<form:input type="text" class="form-control" id="clientId"
					placeholder="MITREid Client ID" path="clientId" value=""></form:input>
				<small><form:errors class="error" path="clientId"></form:errors></small>
			</div>
			<div class="form-group">
				<label for="secret">MITREid Secret</label>
				<form:input type="text" class="form-control" id="secret"
					placeholder="MITREid Secret" path="secret" value=""></form:input>
				<small><form:errors class="error" path="secret"></form:errors></small>
			</div>
			
			<button id="mitreidFormSubmit" class="btn btn-primary btn-md">Save</button>
		</form:form>
      </div>
    </div>
  </div>
</div>

<div id="progressDlg" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
    	<div class="modal-header">
    		Giles is reloading. Please wait...
    	</div>
    	<div class="modal-body">
	      <div class="progress">
			  <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
			    <span class="sr-only"></span>
			  </div>
		  </div>
		</div>
    </div>
  </div>
</div>

<script>
//# sourceURL=ajax.js
$(function() {
	$("#githubUpdateForm").submit(function(e) {
	    submitForm(this);
	    e.preventDefault(); //STOP default action
	});
	$("#googleUpdateForm").submit(function(e) {
	    submitForm(this);
	    e.preventDefault(); //STOP default action
	});
	$("#mitreidUpdateForm").submit(function(e) {
	    submitForm(this);
	    e.preventDefault(); //STOP default action
	});
});

function submitForm(form) {
    var postData = $(form).serializeArray();
    var formURL = $(form).attr("action");
    $("#progressDlg").modal('show');
    $.ajax(
    {
        url : formURL,
        type: "POST",
        data : postData,
        success:function(data, textStatus, jqXHR) 
        {
            window.location.href = "${pageContext.request.contextPath}/admin/system/social";
        },
        error: function(jqXHR, textStatus, errorThrown) 
        {
            checkStatus();   
        }
    });
}

function checkStatus() {
    $.ajax("${pageContext.request.contextPath}")
      .done(function() {
        window.location.href = "${pageContext.request.contextPath}/admin/system/social";
      })
      .fail(function() {
     	 checkStatus();
      });
}
</script>