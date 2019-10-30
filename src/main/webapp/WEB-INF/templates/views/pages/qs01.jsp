<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../partials/head.jsp"/>

<h4>Embedded Signing Ceremony</h4>
<p>This example sends an envelope, and then uses an embedded signing ceremony for the signer.</p>

<form class="eg" action="" method="post" data-busy="form">
    <input type="hidden" name="_csrf" value="${csrfToken}">
    <button type="submit" class="btn btn-primary">Create Envelope</button>
</form>


<form class="eg" action="qs01/ceremony_url" method="get" data-busy="form">
    <button type="submit" class="btn btn-primary">Get View URL</button>
</form>


<form class="eg" action="qs01/envelope_by_template" method="get" data-busy="form">
    <button type="submit" class="btn btn-primary">Create Envelope By Template</button>
</form>


<form class="eg" action="qs01/ceremony_url_by_template1" method="get" data-busy="form">
    <button type="submit" class="btn btn-primary">Get View URL by Template Signer1</button>
</form>

<form class="eg" action="qs01/ceremony_url_by_template2" method="get" data-busy="form">
    <button type="submit" class="btn btn-primary">Get View URL by Template Signer2</button>
</form>

<form class="eg" action="qs01/download_documents" method="get" data-busy="form">
    <input type="text" name="envelope_id">
    <button type="submit" class="btn btn-primary">Download Documents</button>
</form>

<jsp:include page="../partials/foot.jsp"/>
