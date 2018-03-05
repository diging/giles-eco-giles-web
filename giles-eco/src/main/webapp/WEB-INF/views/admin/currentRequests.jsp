<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<h1>Recent Requests</h1>

<div class="panel panel-default" style="max-height: 400px; overflow-y:scroll">
  <!-- Default panel contents -->
  <div class="panel-heading">Recently processed requests</div>
  <div class="panel-body">
    <p>The following list shows the last requests processed in the Giles Ecosystem.</p>
  </div>

  <!-- Table -->
  <table class="table" id="requestsTable">
    
  </table>
 </div>
 
 <div class="panel panel-default" style="height: 400px; overflow-y:scroll">
  <!-- Default panel contents -->
  <div class="panel-heading">Recently sent requests</div>
  <div class="panel-body">
    <p>The following list shows the last requests that Giles sent.</p>
  </div>

  <!-- Table -->
  <table class="table" id="requestsTableSent">
    
  </table>
 </div>

<script>

var TIMEOUT = 2000;

$(function() {
    getProcessedRequests();
    getSentRequests();
});

function getProcessedRequests() {
    $.get( "<c:url value='/admin/requests/current/processed' />")
    .done(function(data) {
        $("#requestsTable").empty();
        $.each(data, function(i, item) {
            var tr = $("<tr></tr>");
            var td = $("<td></td>");
            tr.append(td);
            td.append("{0}/{1}/{2} {3}:{4}:{5} {6} ".format(item.time.monthValue, item.time.dayOfMonth, item.time.year, item.time.hour, item.time.minute, item.time.second, item.time.offset.id));
            td.append('<br>' + createLabelComplete(item.request.requestType));
            td = $("<td></td>");
            tr.append(td);
            if (item.request.filename) {
                 td.append(' ' + item.request.filename + '');
                 if (item.request.fileId) {
                     td.append(' (' + item.request.fileId + ')');
                 }
                 td.append($('<br>'));
            }
                          
            if (item.request.pages) {
                td.append("Pages:<br>");
                $.each(item.request.pages, function(idx, page) {
                    td.append(" [" + idx + "] " + page.filename);
                    td.append($('<br>'));
                });
            }
            $("#requestsTable").append(tr); 
        }); 
        setTimeout(getProcessedRequests, TIMEOUT);
    })
    .fail(function() {
        $("#requestsTable").append("Sorry, an error occurred.")
    });
}

function getSentRequests() {
    $.get( "<c:url value='/admin/requests/current/sent' />")
    .done(function(data) {
        $("#requestsTableSent").empty();
        $.each(data, function(i, item) {
            var tr = $("<tr></tr>");
            var td = $("<td></td>");
            tr.append(td);
            td.append("{0}/{1}/{2} {3}:{4}:{5} {6} ".format(item.time.monthValue, item.time.dayOfMonth, item.time.year, item.time.hour, item.time.minute, item.time.second, item.time.offset.id));
            td.append('<br>' + createLabelSent(item.request.requestType));
            td = $("<td></td>");
            tr.append(td);
            if (item.request.filename) {
                 td.append(' ' + item.request.filename + '');
                 if (item.request.fileId) {
                     td.append(' (' + item.request.fileId + ')');
                 }
                 td.append($('<br>'));
            }
                          
            if (item.request.pages) {
                td.append("Pages:<br>");
                $.each(item.request.pages, function(idx, page) {
                    td.append(" [" + idx + "] " + page.filename);
                    td.append($('<br>'));
                });
            }
            $("#requestsTableSent").append(tr); 
        }); 
        setTimeout(getSentRequests, TIMEOUT);
    })
    .fail(function() {
        $("#requestsTable").append("Sorry, an error occurred.")
    });
}

function createLabelComplete(type) {
    if (type == 'giles.request_type.storage.complete') {
        return '<span class="label label-warning">' + 'Storage Complete' + '</span>';
    } else if ( type == 'giles.request_type.text_extraction.complete') {
        return '<span class="label label-info">' + 'Text Extraction Complete' + '</span>';
    } else if ( type == 'giles.request_type.image_extraction.complete') {
        return '<span class="label label-primary">' + 'Image Extraction Complete' + '</span>';
    } else if ( type == 'giles.request_type.ocr.complete') {
        return '<span class="label label-success">' + 'OCR Complete' + '</span>';
    }
    
    return '<span class="label label-default">' + type + '</span>';
}

function createLabelSent(type) {
    if (type == 'giles.request_type.storage') {
        return '<span class="label label-warning">' + 'Storage' + '</span>';
    } else if ( type == 'giles.request_type.text_extraction') {
        return '<span class="label label-info">' + 'Text Extraction' + '</span>';
    } else if ( type == 'giles.request_type.image_extraction') {
        return '<span class="label label-primary">' + 'Image Extraction' + '</span>';
    } else if ( type == 'giles.request_type.ocr') {
        return '<span class="label label-success">' + 'OCR' + '</span>';
    }
    
    return '<span class="label label-default">' + type + '</span>';
}

String.prototype.format = function() {
    a = this;
    for (k in arguments) {
      a = a.replace("{" + k + "}", arguments[k])
    }
    return a
  }
</script>