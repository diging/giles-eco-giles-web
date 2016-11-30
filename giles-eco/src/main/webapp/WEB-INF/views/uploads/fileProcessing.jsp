<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<tilesx:useAttribute name="fileBean" id="fileBean"/>
<c:if test="${fileBean.storedStatus == COMPLETE}">
<i class="fa fa-hdd-o" aria-hidden="true" title="File has been stored."></i>
</c:if>
<c:if test="${fileBean.textExtractionStatus == COMPLETE}">
<i class="fa fa-file-text-o" aria-hidden="true" title="Text has been extracted."></i>
</c:if>
<c:if test="${fileBean.imageExtractionStatus == COMPLETE}">
<i class="fa fa-file-image-o" aria-hidden="true" title="Images have been extracted."></i>
</c:if>
<c:if test="${fileBean.ocrStatus == COMPLETE}">
<i class="fa fa-file-text" aria-hidden="true" title="OCR has finished."></i>
</c:if>