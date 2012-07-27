<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="locale" value="${pageContext.response.locale}"/>
<c:set var="user" value="${pageContext.request.remoteUser}"/>
<c:set var="scheme" value="${pageContext.request.scheme}"/>
<c:set var="resources" value="http://clickandeat.s3-external-3.amazonaws.com/resources"/>

<script type="text/javascript">var ctx = "${ctx}"; var locale = "${locale}"; var ccy = "<spring:message code="label.currency"/>";</script>

