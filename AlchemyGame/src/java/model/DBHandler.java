/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author sarab
 */
public class DBHandler {
    
       private static Connection connection;
       private PreparedStatement findUser;
       private PreparedStatement getUserCount;
       private PreparedStatement addUser;
       private PreparedStatement getAllPotions;
       private PreparedStatement getAllIngredients;
       private PreparedStatement getAllUserPotions;
       private PreparedStatement getAllUserIngredients;
       private PreparedStatement getPotionFromID;
       private PreparedStatement getIngredientFromID;
       private PreparedStatement getBattleItemFromID;
       private PreparedStatement updateUserIngredients;
       private PreparedStatement getUserID;
       private PreparedStatement checkIfIngredientNull;
       private PreparedStatement changeGold;
       private PreparedStatement updatePotionsSold;
       private PreparedStatement updatePotionsAmount;
       private PreparedStatement getUserRecipes;
       private PreparedStatement unlockRecipe;
       private PreparedStatement getAllBattleItems;
       
  

       int userCount;
       User[] users;
       ArrayList<String> allIngredientsByName;
       ArrayList<Integer> allIngredientsByID;
       ArrayList<Integer> userRecipes;
       
       
       
       public DBHandler(){
           connToDB();
       }
         
       public static void connToDB(){
          try{
           connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/alchemy","admin", "admin");
           System.out.println("connection successful");
          }
          catch(Exception e){
                System.out.println("cannot connect");
               e.printStackTrace();
          }
       }
       
  public User[] findUsers() {
        try {
            getUserCount = connection.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet countResult = getUserCount.executeQuery();
            countResult.next();
            userCount = countResult.getInt("COUNT(*)");
            findUser = connection.prepareStatement("SELECT * FROM users");
            
            int i = 0;
            users = new User[userCount];
            ResultSet result = findUser.executeQuery();
            while (result.next()) { // skips null first line
                User u = new User();
                u.setUsername(result.getString("username"));
                u.setPassword(result.getString("password"));
                users[i] = u;
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public void registerUser(String username, String password) {
        try {
            int updatedRows = 0;
            addUser = connection.prepareStatement("INSERT INTO users"
                    + " (username,password,gold,potions_crafted,potions_sold) "
                    + "VALUES (?, ?,0,0,0)");
            addUser.setString(1, username);
            addUser.setString(2, password);
            updatedRows = addUser.executeUpdate();
            if (updatedRows != 1) {
                System.out.println("exception");
            }
        }
        catch(Exception e){
               e.printStackTrace();
        }
    }
    
    public Quest goQuesting(int power, int uid) {
        Quest quest = new Quest();
        allIngredientsByName = new ArrayList<>();
        allIngredientsByID = new ArrayList<>();
        
        try {
            getAllIngredients = connection.prepareStatement("select * from ingredients order by rarity asc;");
            quest.setMonster("troll");
          //  quest.setIngr1("troll fat");
            ResultSet result  = getAllIngredients.executeQuery();
            while(result.next()){
                    allIngredientsByName.add(result.getString("name"));
                    allIngredientsByID.add(result.getInt("id"));
            }
            if (power < 5){
               int randIndex = (int)(Math.random()*(6-0+1)+0);  //(max-min+1)+min
               int randAmount = (int)(Math.random()*(4-1+1)+1);
               
               quest.setIngr1(allIngredientsByName.get(randIndex));
               quest.setNrIngr1(randAmount);
               
               checkIfIngredientNull = connection.prepareStatement("select * from user_ingredients"
                       + " WHERE user_id = " +uid+ " AND ingredient_id = " + allIngredientsByID.get(randIndex));
               ResultSet result2 = checkIfIngredientNull.executeQuery();
               if (!result2.next()){
                   updateUserIngredients= connection.prepareStatement("INSERT INTO "
                           + "user_ingredients (user_id,ingredient_id,amount) VALUES (?,?,?)");
                    updateUserIngredients.setInt(1, uid);
                    updateUserIngredients.setInt(2, allIngredientsByID.get(randIndex));
                    updateUserIngredients.setInt(3, randAmount);
               } else {
                   updateUserIngredients= connection.prepareStatement("UPDATE user_ingredients SET amount = amount+? WHERE user_id = ? AND ingredient_id = ?");
                    updateUserIngredients.setInt(1, randAmount);
                    updateUserIngredients.setInt(2, uid);
                    updateUserIngredients.setInt(3, allIngredientsByID.get(randIndex));
                    
               } 
               updateUserIngredients.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quest;
    }
    
    public ArrayList<Potion> fetchAllPotions(){
        ArrayList<Potion> allPotions = new ArrayList<>();
        
        try{
            getAllPotions = connection.prepareStatement("select * from potions");
            ResultSet result = getAllPotions.executeQuery();
            
            while(result.next()){
                Potion p = getPotionById(result.getInt("id"));
                allPotions.add(p);
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return allPotions;
    }
   
    public ArrayList<Potion> fetchAllUserPotions(int uid){
        ArrayList<Potion> allUserPotions = new ArrayList<>();
        
        try{
            getAllUserPotions = connection.prepareStatement("select * from user_potions "
                    + "WHERE user_id = " + uid);
            ResultSet result = getAllUserPotions.executeQuery();
            
            while(result.next()){
                Potion p = getPotionById(result.getInt("potions_id"));
                p.setAmount(result.getInt("amount"));
                allUserPotions.add(p);
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        
        
        return allUserPotions;
    }
    
    
     public ArrayList<Ingredient> fetchAllUserIngredients(int uid){
        ArrayList<Ingredient> allUserIngredients = new ArrayList<>();
        
        try{
            getAllUserIngredients = connection.prepareStatement("select * from user_ingredients "
                    + "WHERE user_id = " + uid + " ORDER BY amount desc");
            ResultSet result = getAllUserIngredients.executeQuery();
            
            while(result.next()){
                Ingredient ingr = getIngredientByID(result.getInt("ingredient_id"));
                ingr.setAmount(result.getInt("amount"));
                allUserIngredients.add(ingr);
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
      
        return allUserIngredients;
    }
    
    
    
    // 
    public Potion getPotionById(int pid) {
        Potion p = new Potion();
        try {
            getPotionFromID = connection.prepareStatement("select * from potions "
                    + "WHERE id = " + pid);
            ResultSet result = getPotionFromID.executeQuery();
            
            if (result.next()) {
                p = new Potion(result.getInt("id"), result.getString("name"),
                        result.getInt("ingr1_id"), result.getInt("ingr2_id"), 
                        result.getInt("sell_price"));
                p.setRecipePrice(result.getInt("recipe_price"));
                if ((result.getString("ingr3_id")) != null) {
                    p.setIngredient3ID(result.getInt("ingr3_id"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }
    
     public Ingredient getIngredientByID(int iid){
         Ingredient ingr = new Ingredient();
         try{
             getIngredientFromID = connection.prepareStatement("select * FROM ingredients "
                     + "WHERE id = "+iid);
             ResultSet result = getIngredientFromID.executeQuery();
             if (result.next()){
                 ingr.setId(iid);
                 ingr.setName(result.getString("name"));
                 ingr.setRarity(result.getInt("rarity"));
             }
         } catch (Exception e){
             e.printStackTrace();
         }
         return ingr;
     }
    
    public void sellPotion(int uid, Potion p){
        try{
            increaseGold(uid, p.getSellPrice());
           
            updatePotionsSold = connection.prepareStatement("UPDATE users SET potions_sold = potions_sold+1 WHERE id = ?");
            updatePotionsSold.setInt(1, uid);
            updatePotionsSold.executeUpdate();
            
            updatePotionsAmount(uid, p);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void updatePotionsAmount(int uid, Potion p) {
        try {
            if (p.getAmount() == 1) {
                updatePotionsAmount = connection.prepareStatement("DELETE FROM user_potions "
                        + "WHERE user_id = ? AND potions_id = ?");
            } else {
                updatePotionsAmount = connection.prepareStatement("UPDATE user_potions SET amount = amount-1 "
                        + "WHERE user_id = ? AND potions_id = ?");
            }
            updatePotionsAmount.setInt(1, uid);
            updatePotionsAmount.setInt(2, p.getId()); 
            updatePotionsAmount.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void brew(int uid, Potion p){
        //Add a potion to user_potions;
        try {
            if (p.getAmount() == 0) {
                updatePotionsAmount = connection.prepareStatement("INSERT INTO "
                        + "user_potions (user_id,potions_id,amount) VALUES (?,?,?)");
                updatePotionsAmount.setInt(3, 1);
                //lägg till rad 280+281 vid knas
            } else {
                updatePotionsAmount = connection.prepareStatement("UPDATE user_potions SET amount = amount+1 "
                        + "WHERE user_id = ? AND potions_id = ?");
            }
            updatePotionsAmount.setInt(1, uid);
            updatePotionsAmount.setInt(2, p.getId()); 
            updatePotionsAmount.executeUpdate();
            
            //Remove ingredients used in brewing
            updateIngredientsList(uid,p);
   

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void updateIngredientsList(int uid, Potion p) {
        try {
            ArrayList<Ingredient> ingredients = fetchAllUserIngredients(uid);
            for (Ingredient ingr : ingredients) {
                if (ingr.getId() == p.getIngredient1ID()) {
                    if (ingr.getAmount() == 1) {
                        updateUserIngredients = connection.prepareStatement("DELETE "
                                + "FROM user_ingredients WHERE user_id = ? AND ingredient_id = ?");
                        updateUserIngredient(uid, p.getIngredient1ID());
                    } else {
                        updateUserIngredients = connection.prepareStatement("UPDATE user_ingredients SET amount = amount-1 "
                                + "WHERE user_id = ? AND ingredient_id = ?");
                        updateUserIngredient(uid, p.getIngredient1ID());
                    }
                } else if (ingr.getId() == p.getIngredient2ID()) {
                    if (ingr.getAmount() == 1) {
                        updateUserIngredients = connection.prepareStatement("DELETE "
                                + "FROM user_ingredients WHERE user_id = ? AND ingredient_id = ?");
                        updateUserIngredient(uid, p.getIngredient2ID());
                    } else {
                        updateUserIngredients = connection.prepareStatement("UPDATE user_ingredients SET amount = amount-1 "
                                + "WHERE user_id = ? AND ingredient_id = ?");
                        updateUserIngredient(uid, p.getIngredient2ID());
                    }
                } else if (ingr.getId() == p.getIngredient3ID()) {
                    if (ingr.getAmount() == 1) {
                        updateUserIngredients = connection.prepareStatement("DELETE "
                                + "FROM user_ingredients WHERE user_id = ? AND ingredient_id = ?");
                        updateUserIngredient(uid, p.getIngredient3ID());
                    } else {
                        updateUserIngredients = connection.prepareStatement("UPDATE user_ingredients SET amount = amount-1 "
                                + "WHERE user_id = ? AND ingredient_id = ?");
                        updateUserIngredient(uid, p.getIngredient3ID());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Implement back buttons

    public void updateUserIngredient(int uid, int ingr) {
        try {
            updateUserIngredients.setInt(1, uid);
            updateUserIngredients.setInt(2, ingr);
            updateUserIngredients.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    // used when buying a potion recipe
    public void unlockRecipe(int uid, Potion p){
        try{
            unlockRecipe = connection.prepareStatement("INSERT INTO unlocked_recipes (user_id, "
                    + "potions_id) VALUES (?,?)");
            unlockRecipe.setInt(1, uid);
            unlockRecipe.setInt(2, p.getId());
            unlockRecipe.executeUpdate();
        } catch(Exception e){
            e.printStackTrace();
        }
        
        decreaseGold(uid, p.getRecipePrice());
    }
    
    public ArrayList<Integer> getUserRecipes(int uid){
        ArrayList<Integer> userRecipes = new ArrayList<>();
        try {
            getUserRecipes = connection.prepareStatement("select * from unlocked_recipes"
                    + " where user_id = " + uid);
            ResultSet result = getUserRecipes.executeQuery();
            while(result.next()){
                userRecipes.add(result.getInt("potions_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRecipes;
    }
    
    public void increaseGold(int uid, int sellPrice){
        try{
            changeGold = connection.prepareStatement("UPDATE users SET gold = gold+? WHERE id = ?");
            changeGold.setInt(1, sellPrice);
            changeGold.setInt(2, uid);
            changeGold.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void decreaseGold(int uid, int buyPrice){
        try{
            changeGold = connection.prepareStatement("UPDATE users SET gold = gold-? WHERE id = ?");
            changeGold.setInt(1, buyPrice);
            changeGold.setInt(2, uid);
            changeGold.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ArrayList<BattleItem> fetchAllBattleItems() {

        ArrayList<BattleItem> allBattleItems = new ArrayList<>();
        try {
            BattleItem bi = new BattleItem();
            getAllBattleItems = connection.prepareStatement("SELECT * FROM battle_items");
            ResultSet result = getAllBattleItems.executeQuery();
            while (result.next()) {
                bi = getBattleItemByID(result.getInt("id"));
                allBattleItems.add(bi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allBattleItems;
    }
    
    public BattleItem getBattleItemByID(int id) {
        BattleItem bi = new BattleItem();
        try {
            getBattleItemFromID = connection.prepareStatement("SELECT * from battle_items WHERE id = ?");
            getBattleItemFromID.setInt(1,id);
            ResultSet result = getBattleItemFromID.executeQuery();
            while (result.next()){
                bi = new BattleItem(result.getInt("id"),result.getString("name"),result.getInt("effect"),
                result.getInt("purchase_price"),result.getInt("sell_price"),result.getString("item_type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bi;
    }
    
    
    public int getUserID(String username){
        int uID = -1;
        
        try{
            getUserID = connection.prepareStatement("SELECT id FROM users \n" +
                "WHERE username = ?");
            getUserID.setString(1, username);
            ResultSet userIDResult = getUserID.executeQuery();
            userIDResult.next();
            uID = userIDResult.getInt("id");
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return uID;
    }
            
}
