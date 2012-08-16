<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:redirect url="${ctx}/home.html"/>