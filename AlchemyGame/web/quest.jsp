<%-- 
    Document   : quest
    Created on : 29 dec. 2021, 16:49:49
    Author     : HP
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="model.Quest" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quest Result</title>
        <%
            // currently works only for power < 5
            // fix so that more ingredients supported
            Quest q = (Quest)session.getAttribute("questRewards");
            String monster = q.getMonster();
            String ingr1 = q.getIngr1();
            String ingr2 = q.getIngr2();
            String ingr3 = q.getIngr3();
            int nrIngr1 = q.getNrIngr1();
            int nrIngr2 = q.getNrIngr2();
            int nrIngr3 = q.getNrIngr3();
        %>
    </head>
    <body>
        <h1>Congratulations!</h1>
        <p>You have slain a pack of <%=monster%>s!</P>
        <br>
        <p>You reward is <%=nrIngr1%> <%=ingr1%>!</P>
        
        <p>
        <form method="post" action="/AlchemyGame/MainServlet">
        <input type="hidden" name="action" value="back">
        <input type="submit" value="Back">
        </form>
    </body>
</html>