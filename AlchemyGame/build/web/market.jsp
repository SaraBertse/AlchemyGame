<%-- 
    Document   : market
    Created on : 29 dec. 2021, 16:50:02
    Author     : Sara Bertse and Jacob Dwyer
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="model.Potion" %>
<%@page import="model.BattleItem" %>
<%@page import="model.BrewingItem" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Market</title>
        <%
            int gold = (int)session.getAttribute("userGold");
            String[] checkGold = (String[])session.getAttribute("checkGold");
            String[] checkGoldRecipe = (String[])session.getAttribute("checkGoldRecipe");
            ArrayList<Potion> potions = (ArrayList<Potion>)session.getAttribute("userPotions");
            ArrayList<Potion> recipes = (ArrayList<Potion>)session.getAttribute("availableRecipes");
        %>
    </head>
    <body>
        <h1>Welcome to the Market!</h1>
        <div>Current gold: <%=gold %></div>
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
        <p/>
        <table><tr><th>Recipe</th><th>Cost</th><th>Buy</th></tr>
        <%
            i = 0;
            for(Potion r : recipes){
        %>
        <tr>
            <form method="post" action="/AlchemyGame/MarketServlet">
            <td><%= r.getName() %></td>
            <td><%= r.getRecipePrice() %></td>
            <td><input type="hidden" name="action" value="buy<%=i%>">
            <input type="submit" value="Buy" <%=checkGoldRecipe[i]%>></td>
            </form>
        </tr>
        <%
            i++;
            }
        %>
      </table>
        <table><tr><th>Name</th><th>Type</th><th>Effect</th><th>Purchase price</th></tr>
        <%
            ArrayList<BattleItem> battleItems = (ArrayList<BattleItem>)session.getAttribute("allBattleItems");
            i=0;
            for(BattleItem b : battleItems){
        %>
        <tr>
            <form method="post" action="/AlchemyGame/MarketServlet">
            <td><%= b.getName() %></td>
            <td><%= b.getType() %></td>
            <td><%= b.getEffect() %></td>
            <td><%= b.getPurchasePrice() %></td>
            <td><input type="hidden" name="action" value="buyeq<%=i%>">
            <input type="submit" value="Buy" <%=checkGold[i]%>></td>
            </form>
        </tr>
        <% 
            i++;
            }            
        %>
        </table>
           <table><tr><th>Name</th><th>Effect</th><th>Purchase price</th></tr>
               <%
                    ArrayList<BrewingItem> brewingItems = (ArrayList<BrewingItem>)session.getAttribute("brewingItems");
                    i = 0;
                    for(BrewingItem br : brewingItems){
                %>
                       <tr>
            <form method="post" action="/AlchemyGame/MarketServlet">
            <td><%= br.getName() %></td>
            <td><%= br.getEffect() %></td>
            <td><%= br.getPurchasePrice() %></td>
            <td><input type="hidden" name="action" value="breq<%=i%>">
            <input type="submit" value="Buy"></td>
            </form>
        </tr>
        <% 
            i++;
            }            
        %>
                <p>
            <form method="post" action="/AlchemyGame/MarketServlet">
            <input type="hidden" name="action" value="back">
            <input type="submit" value="Back">
            </form>
    </body>
</html>