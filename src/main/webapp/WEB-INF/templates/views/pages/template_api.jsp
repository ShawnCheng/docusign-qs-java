<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../partials/head.jsp"/>

<h4>Template API Experiments</h4>
<p></p>

<form class="eg" action="template_api/list_templates" method="get" data-busy="form">
    <button type="submit" class="btn btn-primary">List Templates</button>
</form>

<form class="eg" action="template_api/template_roles" method="get" data-busy="form">
    <input type="text" name="template_id">
    <button type="submit" class="btn btn-primary">List Templates Role of Envelope</button>
</form>

<jsp:include page="../partials/foot.jsp"/>
