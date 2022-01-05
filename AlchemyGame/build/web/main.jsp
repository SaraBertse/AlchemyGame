<%-- 
    Document   : main
    Created on : 29 Dec 2021, 15:17:41
    Author     : sarab
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Welcome to the Alchemy Game!</h1>
        
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
    </body>
</html>
