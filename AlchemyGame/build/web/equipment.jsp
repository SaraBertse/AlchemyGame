<%-- 
    Document   : equipment
    Created on : 29 dec. 2021, 16:49:56
    Author     : HP
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="model.BattleItem" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Equipment</title>
        <%
            ArrayList<BattleItem> inventory = (ArrayList<BattleItem>)session.getAttribute("userInventory");
            ArrayList<BattleItem> equipped = (ArrayList<BattleItem>)session.getAttribute("userEquipment");
        %>
    </head>
    <body>
        <h1>Equip your gear</h1>
        <table><tr><th>Type</th><th>Name</th><th>Effect</th></tr>
        <%
            int i = 0;
            for(BattleItem e : equipped){
        %>
        <tr>
            <form method="post" action="/AlchemyGame/EquipmentServlet">
            <td><%= e.getType() %></td>
            <td><%= e.getName() %></td>
            <td><%= e.getEffect()%></td>
            <td><input type="hidden" name="action" value="unequip<%=i%>">
            <input type="submit" value="Unequip"></td>
            </form>
        </tr>
        <%
            i++;
            }
        %>
        </table>
        <p/>
        <table><tr><th>Type</th><th>Name</th><th>Effect</th><th>Equip</th></tr>
        <%
            i = 0;
            for(BattleItem bi : inventory){
        %>
        <tr>
            <form method="post" action="/AlchemyGame/EquipmentServlet">
            <td><%= bi.getType() %></td>
            <td><%= bi.getName() %></td>
            <td><%= bi.getEffect()%></td>
            <td><input type="hidden" name="action" value="equip<%=i%>">
            <input type="submit" value="Equip"></td>
            </form>
        </tr>
        <%
            i++;
            }
        %>
        </table>
               <p>
            <form method="post" action="/AlchemyGame/EquipmentServlet">
            <input type="hidden" name="action" value="back">
            <input type="submit" value="Back">
            </form>
    </body>
</html>