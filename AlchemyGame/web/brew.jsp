<%-- 
    Document   : brew
    Created on : 29 Dec 2021, 16:47:18
    Author     : sarab
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="model.Potion" %>
<%@page import="model.Ingredient" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Brew</title>
        <%
            ArrayList<Potion> potions = (ArrayList<Potion>)session.getAttribute("userRecipes");
            ArrayList<ArrayList<Ingredient>> recIngredients = (ArrayList<ArrayList<Ingredient>>)session.getAttribute("recipeIngredients");
        %>
    </head>
    <body>
        <h1>Welcome to brewing!</h1>
        <table><tr><th>Potion</th><th>Ingredient 1</th><th>Ingredient 2</th><th>Ingredient 3</th><th>Sell Price</th></tr>
        <%
            //Change String to string array with booleans, from getAttribute 
            String[] checkIngr = (String[])session.getAttribute("checkIngrArray");
            //WANT: Display amount as well. potionsReipes =/= userPotions (amount). Pröblem bröther.
            for(int i = 0; i<potions.size();i++){
            
        %>
            
        <tr>
            <form method="post" action="/AlchemyGame/BrewingServlet">
            <td><%= potions.get(i).getName() %></td>
            <td><%= recIngredients.get(i).get(0).getName() %></td>
            <td><%= recIngredients.get(i).get(1).getName() %></td>
            <td><% if(!(potions.get(i).getIngredient3ID() == 0)) 
                {out.print(recIngredients.get(i).get(2).getName());} %></td>
            <td><%= potions.get(i).getSellPrice()%></td>
            <td><input type="hidden" name="action" value="brew<%=i%>">
            <input type="submit" value="Brew" <%=checkIngr[i]%>></td>
            </form>
        </tr>


        <%
            } 
        %>
        </table>
        
        <p>
        <br>
        <table><tr><th>Ingredient name</th><th>Rarity</th><th>Amount</th>
                    <%  
                        ArrayList<Ingredient> userIngredients = (ArrayList<Ingredient>)session.getAttribute("userIngredients");
                        for (int i = 0; i < userIngredients.size(); i++) {
                    %>
            <tr>
                <td><%= userIngredients.get(i).getName()%></td>
                <td><%= userIngredients.get(i).getRarity()%></td>
                <td><%= userIngredients.get(i).getAmount()%></td>
            </tr>
            <% 
                }
                %>
        </table>
        <p>
            <form method="post" action="/AlchemyGame/BrewingServlet">
            <input type="hidden" name="action" value="back">
            <input type="submit" value="Back">
            </form>
         
    </body>
</html>