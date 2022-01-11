<%-- 
    Document   : main
    Created on : 29 Dec 2021, 15:17:41
    Author     : Sara Bertse and Jacob Dwyer
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Main Menu</title>
        <%
            int gold = (int)session.getAttribute("userGold");
            User u = (User)session.getAttribute("user");
        %>
    </head>
    <body>
        <h1>Welcome to the Alchemy Game!</h1>
        <div>Current gold: <%=gold %></div>
        <div>Potions crafted: <%=u.getPotionsCrafted() %></div>
        <div>Potions sold: <%=u.getPotionsSold() %></div>
        <form method="post" action="/AlchemyGame/MainServlet">
            <input type="hidden" name="action" value="brew">
            <input type="submit" value="Brew">
        </form>
        <form method="post" action="/AlchemyGame/MainServlet">
            <input type="hidden" name="action" value="equipment">
            <input type="submit" value="Equipment">
        </form>
        <form method="post" action="/AlchemyGame/MainServlet">
            <input type="hidden" name="action" value="market">
            <input type="submit" value="Market">
        </form>
        <form method="post" action="/AlchemyGame/MainServlet">
            <input type="hidden" name="action" value="quest">
            <input type="submit" value="Quest">
        </form>
        <p>
        <form method="post" action="/AlchemyGame/MainServlet">
            <input type="hidden" name="action" value="logout">
            <input type="submit" value="Log out">
        </form>
    </body>
</html>