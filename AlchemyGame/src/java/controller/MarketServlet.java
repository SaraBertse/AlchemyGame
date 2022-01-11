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
import model.BrewingItem;
import model.DBHandler;
import model.Potion;
import model.User;

/**
 *
 * @author Sara Bertse and Jacob Dwyer
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
        ArrayList<BrewingItem> allBrewingItems = (ArrayList<BrewingItem>) session.getAttribute("brewingItems");
        ArrayList<Potion> potions = (ArrayList<Potion>) session.getAttribute("userPotions");
        String str = "sell";
        for (int i = 0; i < potions.size(); i++) {
            str = "sell";
            str += i;
            if (str.equals(request.getParameter("action"))) {
                dbh.sellPotion(uid, potions.get(i));

                potions = dbh.fetchAllUserPotions(uid);
                session.setAttribute("userPotions", potions);
                session.setAttribute("userGold", dbh.fetchUserGold(uid));

                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);
            }
        }

        // when user buys a potion recipe
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
                
                // disable buy button for recipes where user gold not enough
                String[] checkGoldRecipe = new String[allPotions.size()];
                int index = 0;
                for(Potion recipe : allPotions){
                    checkGoldRecipe[index] = dbh.checkGoldReq(uid, recipe.getRecipePrice());
                    index++;
                }
                session.setAttribute("checkGoldRecipe", checkGoldRecipe);
                
                ArrayList<BattleItem> allBattleItems = (ArrayList<BattleItem>) session.getAttribute("allBattleItems");
                String[] checkGold = new String[allBattleItems.size()];
                index = 0;
                for(BattleItem bi : allBattleItems){
                    checkGold[index] = dbh.checkGoldReq(uid, bi.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGold", checkGold);
                
                String[] checkGoldBrew = new String[allBrewingItems.size()];
                index = 0;
                for (BrewingItem br : allBrewingItems) {
                    checkGoldBrew[index] = dbh.checkGoldReq(uid, br.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGoldBrew", checkGoldBrew);
                
                session.setAttribute("userGold", dbh.fetchUserGold(uid));

                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);
            }
        }

        // when user buys equipment
        String buyeqStr = "buyeq";
        ArrayList<BattleItem> allBattleItems = (ArrayList<BattleItem>) session.getAttribute("allBattleItems");
        ArrayList<BattleItem> userBattleItems = dbh.fetchAllUserBattleItems(uid);
        
        for (int i = 0; i < allBattleItems.size(); i++) {
            buyeqStr = "buyeq";
            buyeqStr += i;
            if (buyeqStr.equals(request.getParameter("action"))) {
                
                for(BattleItem all : allBattleItems){
                    
                    for(BattleItem userItems : userBattleItems){
                        if(all.getId() == userItems.getId()){
                            all.setAmount(userItems.getAmount());
                        }
                    }
                }
                
                dbh.buyBattleItem(uid, allBattleItems.get(i));
                
                // after purchase, check which equipm can be bought
                String[] checkGold = new String[allBattleItems.size()];
                int index = 0;
                for(BattleItem bi : allBattleItems){
                    checkGold[index] = dbh.checkGoldReq(uid, bi.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGold", checkGold);
                
                allPotions = (ArrayList<Potion>)session.getAttribute("availableRecipes");
              
                
                String[] checkGoldRecipe = new String[allPotions.size()];
                index = 0;
                for(Potion recipe : allPotions){
                    checkGoldRecipe[index] = dbh.checkGoldReq(uid, recipe.getRecipePrice());
                    index++;
                }
                session.setAttribute("checkGoldRecipe", checkGoldRecipe);
                
                String[] checkGoldBrew = new String[allBrewingItems.size()];
                index = 0;
                for (BrewingItem br : allBrewingItems) {
                    checkGoldBrew[index] = dbh.checkGoldReq(uid, br.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGoldBrew", checkGoldBrew);

                session.setAttribute("userGold", dbh.fetchUserGold(uid));
                
                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);
            }
            
            //Move out of loop?
            
            //processRequest(request, response);
        }
        
        
        String breqStr = "breq";
        
        //ArrayList<BattleItem> userBattleItems = dbh.fetchAllUserBattleItems(uid);
        
        for (int i = 0; i < allBrewingItems.size(); i++) {
            breqStr = "breq";
            breqStr += i;
            if (breqStr.equals(request.getParameter("action"))) {
            
                dbh.buyBrewingItem(uid, allBrewingItems.get(i));
                
                int brid = allBrewingItems.get(i).getId();
                for (int j = 0; j < 3; j++) {
                    if(!allBrewingItems.isEmpty())
                        if (allBrewingItems.get(0).getId() <= brid) {
                            allBrewingItems.remove(0);
                        }
                }
                session.setAttribute("brewingItems", allBrewingItems);
              
                // after purchase, check which equipm can be bought
                allBattleItems = (ArrayList<BattleItem>)session.getAttribute("allBattleItems");
                String[] checkGold = new String[allBattleItems.size()];
                int index = 0;
                for(BattleItem bi : allBattleItems){
                    checkGold[index] = dbh.checkGoldReq(uid, bi.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGold", checkGold);
                
                // after purchase, check which equipm can be bought
                String[] checkGoldBrewing = new String[allBrewingItems.size()];
                index = 0;
                for(BrewingItem br : allBrewingItems){
                    checkGoldBrewing[index] = dbh.checkGoldReq(uid, br.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGoldBrewing", checkGoldBrewing);
                
                String[] checkGoldBrew = new String[allBrewingItems.size()];
                index = 0;
                for (BrewingItem br : allBrewingItems) {
                    checkGoldBrew[index] = dbh.checkGoldReq(uid, br.getPurchasePrice());
                    index++;
                }
                session.setAttribute("checkGoldBrew", checkGoldBrew);
                
                String[] checkGoldRecipe = new String[allPotions.size()];
                index = 0;
                for(Potion recipe : allPotions){
                    checkGoldRecipe[index] = dbh.checkGoldReq(uid, recipe.getRecipePrice());
                    index++;
                }
                
                
                session.setAttribute("userGold", dbh.fetchUserGold(uid));
                session.setAttribute("checkGoldRecipe", checkGoldRecipe);
                
                
                RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
                rd.forward(request, response);
            } 
            
               // RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
               // rd.forward(request, response);
        }
        
        if ("back".equals(request.getParameter("action"))) {
            User u = dbh.fetchUserStats(uid);
            session.setAttribute("user", u);
            
            RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
            rd.forward(request, response);
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