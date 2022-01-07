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
public class EquipmentServlet extends HttpServlet {

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
            out.println("<title>Servlet EquipmentServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EquipmentServlet at " + request.getContextPath() + "</h1>");
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

        if("back".equals(request.getParameter("action"))){                
            RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
            rd.forward(request, response);        
        }
        // list all items that can be equipped: user_battle_items
        // when press EQUIP, remove from list and in db user_battle_items,
        //     add to equipment list and db user_equipment
        // when equip, automatically unequip same type
        // (if time, make possible uneqip without equipping)
        ArrayList<BattleItem> userInventory = (ArrayList<BattleItem>) session.getAttribute("userInventory");
        String eqStr = "equip";
        for (int i = 0; i < userInventory.size(); i++) {
            eqStr = "equip";
            eqStr += i;
            if (eqStr.equals(request.getParameter("action"))) {

                dbh.equip(uid, userInventory.get(i));

                int[] eqArr = dbh.fetchUserEquipment(uid);
                String[] types = {"", "head", "chest", "hands", "legs", "feet", "weapon", "shield"};

                ArrayList<BattleItem> userEquipment = new ArrayList<>();
                BattleItem dummyBi = new BattleItem(0, "EMPTY", 0, 0, 0, "EMPTY");
                
                int j = 1;
                while (j < 8) {
                    if (eqArr[j] != 0) {
                        userEquipment.add(dbh.getBattleItemByID(eqArr[j]));
                    } else {
                        dummyBi = new BattleItem(0, "EMPTY", 0, 0, 0, types[j]);
                        userEquipment.add(dummyBi);

                    }
                    j++;
                }
                
                dummyBi = new BattleItem(0, "EMPTY", 0, 0, 0, "EMPTY");
                session.setAttribute("userEquipment", userEquipment);
                
                userInventory = dbh.fetchAllUserBattleItems(uid);
                session.setAttribute("userInventory", userInventory);

                RequestDispatcher rd = request.getRequestDispatcher("/equipment.jsp");
                rd.forward(request, response);
            }

        }

        // session.getAttribute("userinventory");
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