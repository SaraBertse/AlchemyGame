/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BattleItem;
import model.DBHandler;
import model.Potion;

/**
 *
 * @author HP
 */
public class MarketServlet extends HttpServlet {

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
            out.println("<title>Servlet MarketServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MarketServlet at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        ServletContext application = request.getServletContext();
        HttpSession session = request.getSession(true);
        DBHandler dbh = (DBHandler) application.getAttribute("dbh");
        if (dbh == null) {
            dbh = new DBHandler();
        }

        int uid = dbh.getUserID((String) application.getAttribute("username"));

        ArrayList<Potion> potions = (ArrayList<Potion>) session.getAttribute("userPotions");
        String str = "sell";
        for (int i = 0; i < potions.size(); i++) {
            str = "sell";
            str += i;
            if (str.equals(request.getParameter("action"))) {
                dbh.sellPotion(uid, potions.get(i));

                potions = dbh.fetchAllUserPotions(uid);
                session.setAttribute("userPotions", potions);

                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);
            }
        }

        ArrayList<Potion> allPotions = dbh.fetchAllPotions();
        String buyStr = "buy";
        for (int i = 0; i < allPotions.size(); i++) {
            buyStr = "buy";
            buyStr += i;
            if (buyStr.equals(request.getParameter("action"))) {

                // check which potions user doesnt have
                ArrayList<Integer> userRecipes = dbh.getUserRecipes(uid);
                for (int j = 0; j < allPotions.size(); j++) {
                    for (int k = 0; k < userRecipes.size(); k++) {
                        if (allPotions.get(j).getId() == userRecipes.get(k)) {
                            allPotions.remove(j);
                        }
                    }
                }

                //buy
                dbh.unlockRecipe(uid, allPotions.get(i));
                allPotions.remove(i); // update available potions list

                session.setAttribute("availableRecipes", allPotions);

                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);
            }

        }

        String buyeqStr = "buyeq";
        ArrayList<BattleItem> allBattleItems = (ArrayList<BattleItem>) session.getAttribute("allBattleItems");
        for (int i = 0; i < allBattleItems.size(); i++) {
            buyeqStr = "buyeq";
            buyeqStr += i;
            if (buyeqStr.equals(request.getParameter("action"))) {
                //  dbh.sellPotion(uid, potions.get(i));

                //  potions = dbh.fetchAllUserPotions(uid);
                //  session.setAttribute("userPotions", potions);
                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);

            } 
            if ("back".equals(request.getParameter("action"))) {
                RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");

                rd.forward(request, response);
            }

            processRequest(request, response);
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