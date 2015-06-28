<%@ page import="org.springframework.security.saml.metadata.MetadataManager" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Spring Security SAML Sample Application - Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<font face="Arial,sans-serif" size=5><b>Sample Service Provider Federated by IdP: PowerSchool</b></font1>

<%
    WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext());
    MetadataManager mm = context.getBean("metadata", MetadataManager.class);
    Set<String> idps = mm.getIDPEntityNames();
    pageContext.setAttribute("idp", idps);
    String idpItem = mm.getDefaultIDP();
%>

<p>
<form action="<c:url value="${requestScope.idpDiscoReturnURL}"/>" method="GET">
    <table>
        <tr>
            <td>
				<input type="hidden" name="idp" id="idp_<%= idpItem %>" value="<%= idpItem %>"/>
            	<input type="submit" value="Access Content Requiring Authentication"/>
            </td>
        </tr>
    </table>
</form>
</p>

</body>
</html>