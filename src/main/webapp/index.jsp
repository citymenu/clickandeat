<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<!-- Sets a reusable variable to hold the current context path -->
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:redirect url="${ctx}/home.html"/>