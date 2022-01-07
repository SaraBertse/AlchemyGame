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
import model.Ingredient;
import model.Potion;
import model.Quest;

/**
 *
 * @author HP
 */
public class MainServlet extends HttpServlet {
    //TODO:
    //Implement so that you can't buy for more gold than you have
    //Implement different quests, depending on armor/weapon effect
    //Brewing equipment

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
            out.println("<title>Servlet MainServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MainServlet at " + request.getContextPath() + "</h1>");
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
        int uid = dbh.getUserID((String)application.getAttribute("username"));
        
        if ("brew".equals(request.getParameter("action"))) {
            ArrayList<Integer> userRecipesIDs = dbh.getUserRecipes(uid);
            ArrayList<Potion> userRecipes = new ArrayList();

            ArrayList<Potion> allUserPotions = dbh.fetchAllUserPotions(uid);
            session.setAttribute("userPotions", allUserPotions);
            
            //Converts user recipe ID's to potions
            for (int i = 0; i < userRecipesIDs.size(); i++) {
                userRecipes.add(dbh.getPotionById(userRecipesIDs.get(i)));
                for(int j = 0; j < allUserPotions.size(); j++){
                    if(userRecipes.get(i).getId() == allUserPotions.get(j).getId()){
                        userRecipes.get(i).setAmount(allUserPotions.get(j).getAmount());
                    }
                }
            }
            session.setAttribute("userRecipes", userRecipes);
            
            //Kör checkaren och skicka in boolean som attribut,
            //möjligtvis i en boolean array
       
            //Creates a multi-dimensional ArrayList to store ingredients of the potions
            ArrayList<ArrayList<Ingredient>> ingredients = new ArrayList<>(userRecipesIDs.size());
            for(int i=0; i < userRecipesIDs.size(); i++) {
                ingredients.add(new ArrayList());
            }
 
            //String array that will be filled with "" or "disabled" for each user recipe,
            //depending on if the user has the right ingredients or not.
            //used in the JSP to gray out buttons when appropriate
            String[] check = new String[userRecipesIDs.size()];
            
            //Fetches a list of ingredients for each potion
            for(int i = 0; i < userRecipesIDs.size(); i++){
                ingredients.get(i).add(dbh.getIngredientByID(userRecipes.get(i).getIngredient1ID()));
                ingredients.get(i).add(dbh.getIngredientByID(userRecipes.get(i).getIngredient2ID()));
                if (!(userRecipes.get(i).getIngredient3ID() == 0)){
                    ingredients.get(i).add(dbh.getIngredientByID(userRecipes.get(i).getIngredient3ID()));
                }
                //Fills the String array
                check[i]=dbh.checkIngrReq(uid,userRecipes.get(i).getIngredient1ID(), 
                        userRecipes.get(i).getIngredient2ID(), 
                        userRecipes.get(i).getIngredient3ID());
            }
            session.setAttribute("checkIngrArray",check);
            
            session.setAttribute("recipeIngredients", ingredients);
            
            ArrayList<Ingredient> userIngredients = new ArrayList<>();
            userIngredients = dbh.fetchAllUserIngredients(uid);
            session.setAttribute("userIngredients", userIngredients);
            
            RequestDispatcher rd = request.getRequestDispatcher("/brew.jsp");

            rd.forward(request, response);

        } else if ("equipment".equals(request.getParameter("action"))) {
            // list all items that can be equipped: user_battle_items
            // when press EQUIP, remove from list and in db user_battle_items,
            //     add to equipment list and db user_equipment
            // when equip, automatically unequip same type
            // (if time, make possible uneqip without equipping)
            
            ArrayList<BattleItem> userInventory = dbh.fetchAllUserBattleItems(uid);
            session.setAttribute("userInventory", userInventory);
            
            int[] equipment = dbh.fetchUserEquipment(uid);
            String[] types = {"","head", "chest", "hands", "legs", "feet", "weapon", "shield"};
            
            ArrayList<BattleItem> userEquipment = new ArrayList<>();
            int i = 1;
            BattleItem dummyBi = new BattleItem(0,"EMPTY",0,0,0,"EMPTY");
            if (equipment[0] != 0) {
                // excludes user_id (index 0) and cauldron (index 8)
                while (i < 8) {
                    if (equipment[i] != 0) {
                        userEquipment.add(dbh.getBattleItemByID(equipment[i]));
                    } else{
                        //BattleItem temp = dbh.getBattleItemByID(equipment[i]);
                        dummyBi = new BattleItem(0,"EMPTY",0,0,0,types[i]);
                        userEquipment.add(dummyBi);
                        
                    }
                    i++;
                }
                
            }
            else {
                while (i < 9) {
                    userEquipment.add(dummyBi);
                    i++;
                }
            }
            dummyBi = new BattleItem(0,"EMPTY",0,0,0,"EMPTY");
            session.setAttribute("userEquipment", userEquipment);
            
            RequestDispatcher rd = request.getRequestDispatcher("/equipment.jsp");
            rd.forward(request, response);
            
        } else if ("market".equals(request.getParameter("action"))) {
            ArrayList<Potion> allUserPotions = dbh.fetchAllUserPotions(uid);
            session.setAttribute("userPotions", allUserPotions);
            
            // ## add recipes available to buy ##
            // fetch all recipes
            // fetch user recipes
            // remove user recipes from list of all recipes
            // send
            ArrayList<Potion> allPotions = dbh.fetchAllPotions();
            ArrayList<Integer> userRecipes = dbh.getUserRecipes(uid);
            for(int i = 0; i < allPotions.size(); i++){
                for(int j = 0; j < userRecipes.size(); j++){
                    if(allPotions.get(i).getId() == userRecipes.get(j)){
                        allPotions.remove(i);
                    }
                }
            }
            session.setAttribute("availableRecipes", allPotions);
            
            //fetches all battle items
            ArrayList<BattleItem> allBattleItems = dbh.fetchAllBattleItems();
            session.setAttribute("allBattleItems",allBattleItems);
            
            RequestDispatcher rd = request.getRequestDispatcher("/market.jsp");
            rd.forward(request, response);
            
        } else if ("quest".equals(request.getParameter("action"))) {
            RequestDispatcher rd = request.getRequestDispatcher("/quest.jsp");
            Quest questRewards = dbh.goQuesting(3, uid);
            session.setAttribute("questRewards", questRewards);
            rd.forward(request, response);
      /*  } else if ("back".equals(request.getParameter("action"))){
            RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
            rd.forward(request, response);    */  
        } else if ("logout".equals(request.getParameter("action"))){
            session.invalidate();  
            RequestDispatcher rd = request.getRequestDispatcher("/index.html");
            rd.forward(request, response);
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");

            rd.forward(request, response);
        }
            
        //Inventory
        //Cauldron upgrade
        //Recipe book
        //Stats
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