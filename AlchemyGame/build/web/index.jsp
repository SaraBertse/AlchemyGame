<%-- 
    Document   : index
    Created on : 11 jan. 2022, 17:21:08
    Author     : Sara Bertse and Jacob Dwyer
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Login</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%
            String logf = (String)session.getAttribute("loginFail");
            String regf = (String)session.getAttribute("regFail");
        %>
    </head>
    <body>
        <div <%=logf%>>LOGIN FAILED</div>
        <div <%=regf%>>REGISTRATION FAILED</div>
        <h1>Login</h1>
        <form method="post" action="/AlchemyGame/UserServlet">
            Username <input type="text" name="username"><br>
            Password <input type="text" name="password"><br>
            <input type="hidden" name="action" value="login">
            <input type="submit" value="Login">
        </form>
        <div> <br> 
             
             
        <p>
        <p>
        <p></div>
        <form method="post" action="/AlchemyGame/UserServlet">
            Username <input type="text" name="regusername"><br>
            Password <input type="text" name="regpassword"><br>
            <input type="hidden" name="regaction" value="register">
            <input type="submit" value="Register">
        </form>
    </body>
</html>