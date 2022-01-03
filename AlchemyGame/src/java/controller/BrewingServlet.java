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
import model.DBHandler;
import model.Ingredient;
import model.Potion;

/**
 *
 * @author sarab
 */
public class BrewingServlet extends HttpServlet {

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
            out.println("<title>Servlet BrewingServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BrewingServlet at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
        ServletContext application = request.getServletContext();
        HttpSession session = request.getSession(true);
        DBHandler dbh = (DBHandler) application.getAttribute("dbh");
        if (dbh == null) {
            dbh = new DBHandler();
        }

        int uid = dbh.getUserID((String) application.getAttribute("username"));

        ArrayList<Integer> userRecipesIDs = dbh.getUserRecipes(uid);
        ArrayList<Potion> userRecipes = new ArrayList();

        for (int i = 0; i < userRecipesIDs.size(); i++) {
            userRecipes.add(dbh.getPotionById(userRecipesIDs.get(i)));
        }
        session.setAttribute("userRecipes", userRecipes);

        ArrayList<ArrayList<Ingredient>> ingredients = new ArrayList<>(userRecipesIDs.size());
        for (int i = 0; i < userRecipesIDs.size(); i++) {
            ingredients.add(new ArrayList());
        }

        for (int i = 0; i < userRecipesIDs.size(); i++) {
            ingredients.get(i).add(dbh.getIngredientByID(userRecipes.get(i).getIngredient1ID()));
            ingredients.get(i).add(dbh.getIngredientByID(userRecipes.get(i).getIngredient2ID()));
            if (!(userRecipes.get(i).getIngredient3ID() == 0)) {
                ingredients.get(i).add(dbh.getIngredientByID(userRecipes.get(i).getIngredient3ID()));
            }
        }
        session.setAttribute("recipeIngredients", ingredients);

        ArrayList<Ingredient> userIngredients = new ArrayList<>();
        userIngredients = dbh.fetchAllUserIngredients(uid);
        session.setAttribute("userIngredients", userIngredients);

        ArrayList<Potion> potions = (ArrayList<Potion>) session.getAttribute("userPotions");
 
            String str = "brew";
            for (int i = 0; i < potions.size(); i++) {
                str = "brew";
                str += i;
                if (str.equals(request.getParameter("action"))) {
                    dbh.brew(uid, potions.get(i));

                    potions = dbh.fetchAllUserPotions(uid);
                    session.setAttribute("userPotions", potions);
                    
                    RequestDispatcher rd = request.getRequestDispatcher("/brew.jsp");

                    rd.forward(request, response);
                }
           
        }

   processRequest(request, response);
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
