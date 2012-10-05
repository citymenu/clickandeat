<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/tld/escape.tld" prefix="util" %>
<%@ taglib uri="/WEB-INF/tld/selectbox.tld" prefix="select" %>
<%@ taglib uri="/WEB-INF/tld/message.tld" prefix="message" %>
<%@ taglib uri="/WEB-INF/tld/locale.tld" prefix="locale" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="locale" value="${pageContext.response.locale}"/>
<c:set var="user" value="${pageContext.request.remoteUser}"/>

<c:choose>
    <c:when test="${secure != null && secure}">
        <c:set var="resources" value="https://clickandeat.s3-external-3.amazonaws.com/resources"/>
    </c:when>
    <c:otherwise>
        <c:set var="resources" value="http://clickandeat.s3-external-3.amazonaws.com/resources"/>
    </c:otherwise>
</c:choose>

<c:set var="resources" value="${ctx}/resources"/>

<script type="text/javascript">
var ctx = "${ctx}";
var locale = "${locale}";
var language="<locale:language/>";
var country="<locale:country/>";
var ccy = "<message:message key="config.currency" escape="false"/>";
var resources = "${resources}";
</script>
