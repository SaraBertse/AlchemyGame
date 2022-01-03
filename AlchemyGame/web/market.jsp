<%-- 
    Document   : market
    Created on : 29 dec. 2021, 16:50:02
    Author     : HP
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="model.Potion" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Market</title>
        <%
            ArrayList<Potion> potions = (ArrayList<Potion>)session.getAttribute("userPotions");
        %>
    </head>
    <body>
        <h1>Welcome to the Market!</h1>
        <table><tr><th>Potion</th><th>Amount</th><th>Gold</th><th>Sell</th></tr>
        <%
            int i = 0;
            for(Potion p : potions){
        %>
        <tr>
            <form method="post" action="/AlchemyGame/MarketServlet">
            <td><%= p.getName() %></td>
            <td><%= p.getAmount() %></td>
            <td><%= p.getSellPrice()%></td>
            <td><input type="hidden" name="action" value="sell<%=i%>">
            <input type="submit" value="Sell"></td>
            </form>
        </tr>


        <%
            i++;
            }
        %>
        </table>
    </body>
</html>