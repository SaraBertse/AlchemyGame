/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.DBHandler;
import model.User;

/**
 *
 * @author sarab
 */
public class UserServlet extends HttpServlet {
    String password="notworking";
    User[] users;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
 @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                RequestDispatcher rd = request.getRequestDispatcher("/index.html");
                rd.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        User u;
        password = request.getParameter("password"); 
        if (password != null){
            password = encodePassw(password); // encodes to MD5
        } 
        if (session.isNew()) {
            u = new User();
            u.setUsername(request.getParameter("username"));
            u.setPassword(password);
            //session.setAttribute("user", u);
        } else {
            // retrieve the already existring user
            u = (User) session.getAttribute("user");
        }
        ServletContext application = request.getServletContext();
        DBHandler dbh = (DBHandler) application.getAttribute("dbh");
        if (dbh == null) {
            dbh = new DBHandler();
        }
        //Get username and password from the POST
        users = dbh.findUsers();
        
        //password = encodePassw(password); // encodes to MD5
        
        session.setAttribute("password",password);
        //       application.setAttribute("users", users);
        if ("login".equals(request.getParameter("action"))) {
            // check if user is authorized with the "dbh" object
            for(User temp : users) {
                //Checks that the username exists in the database
                if (temp.getUsername().equals(request.getParameter("username"))){
                    //password = request.getParameter("password");
                    RequestDispatcher rd;
                    if (temp.getPassword().equals(password)){
                            rd = request.getRequestDispatcher("/main.jsp");
                            application.setAttribute("username", request.getParameter("username"));
                            //rd.forward(request, response);
                    } else {
                        rd = request.getRequestDispatcher("/indexcopy.html");
                        //rd.forward(request, response);
                    }
                    
                  //  List<Result> quiz1History; // if default num shows up something is wrong
                  //  List<Result> quiz2History;
                   // quiz1History = dbh.getResults(1, request.getParameter("username"));
                   // quiz2History = dbh.getResults(2, request.getParameter("username"));
                   // session.setAttribute("quiz1History", quiz1History);
                   // session.setAttribute("quiz2History", quiz2History);
                    rd.forward(request, response);
                } 
            }
            RequestDispatcher rd = request.getRequestDispatcher("/indexcopy.html"); //Ska gå in på quizzen ist
            rd.forward(request, response);
            //Registers a new user
        } else if ("register".equals(request.getParameter("regaction"))){
                String regpassword = request.getParameter("regpassword");
                if (regpassword != null){
                    regpassword = encodePassw(regpassword); // encodes to MD5
                }
                RequestDispatcher rd;
                for(User temp : users) {
                    //Checks if the username exists in the database
                    if (temp.getUsername().equals(request.getParameter("regusername"))){
                        rd = request.getRequestDispatcher("/failedregister.html");
                        rd.forward(request, response);
                    }
                }
            dbh.registerUser(request.getParameter("regusername"),regpassword);
            rd = request.getRequestDispatcher("main.jsp");
            rd.forward(request, response);
        }
    }
    
    public String encodePassw(String passw){
        try {
  
            MessageDigest md = MessageDigest.getInstance("MD5");
  
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(passw.getBytes());
  
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
  
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
  
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
