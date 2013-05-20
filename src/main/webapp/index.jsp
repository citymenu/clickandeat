<%@ page import="javax.servlet.http.*" %>

<%
String ctx = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
response.setHeader("Location", ctx + "/home.html");
%>
