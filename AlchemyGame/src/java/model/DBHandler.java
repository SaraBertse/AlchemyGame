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
       private PreparedStatement getBrewingItems;
       private PreparedStatement getPotionFromID;
       private PreparedStatement getIngredientFromID;
       private PreparedStatement getIngredientIDFromName;
       private PreparedStatement getBattleItemFromID;
       private PreparedStatement updateUserIngredients;
       private PreparedStatement getUserID;
       private PreparedStatement getUserGold;
       private PreparedStatement checkIfIngredientNull;
       private PreparedStatement changeGold;
       private PreparedStatement updatePotionsSold;
       private PreparedStatement updatePotionsAmount;
       private PreparedStatement updateUserBattleItems;
       private PreparedStatement getUserRecipes;
       private PreparedStatement unlockRecipe;
       private PreparedStatement getAllBattleItems;
       private PreparedStatement getAllUserBattleItems;
       private PreparedStatement getUserEquipment;
       private PreparedStatement updateUserEquipment;
      
  

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
    
    // return sum effect of each equipped item as an int
    public int calculatePower(int uid){
        int power = 0;
        ArrayList<BattleItem>  equipment = new ArrayList<>();
        int[] equipmentID = fetchUserEquipment(uid);
        
        for(int i = 1; i < 8; i++){
            equipment.add(getBattleItemByID(equipmentID[i]));
        }
        
        for(BattleItem bi : equipment)
            power += bi.getEffect();
        
        return power;
    }
    
    // fetch list of ingredients whose rarity is at least min and at most max
    public ArrayList<Ingredient> fetchIngrListByRarity(int min, int max){
        ArrayList<Ingredient> ingrList = new ArrayList<>();
        
        try{
            getAllIngredients = connection.prepareStatement("select * from ingredients "
                    + "where rarity >= " + min + " and rarity <= " + max);
            ResultSet result  = getAllIngredients.executeQuery();
            while(result.next()){
                ingrList.add(getIngredientByID(result.getInt("id")));
            }
        } 
        catch(Exception e){
            e.printStackTrace();
        }
        
        return ingrList;
    }
    
    //Test with higher power -- DOES NOT WORK CORRECTLY, only retrieves first ingr
    public Quest quest2(int uid){
        Quest quest = new Quest();
        int power = calculatePower(uid);
        ArrayList<Ingredient> ingrList;
        
        // TODO adjust rarity scale
        int numOfIngredientsGotten = 1;
        if (power < 5) {
            ingrList = fetchIngrListByRarity(1, 2);
            quest = setRandomIngr(ingrList, numOfIngredientsGotten);
            quest.setMonster("rats");
        } else if (power >= 5 && power < 15) {
            numOfIngredientsGotten = 2;
            ingrList = fetchIngrListByRarity(1, 4);
            quest = setRandomIngr(ingrList, numOfIngredientsGotten);
            quest.setMonster("goblins");
        } else if (power >= 15 && power < 40) {
            numOfIngredientsGotten = 2;
            ingrList = fetchIngrListByRarity(1, 5);
            quest = setRandomIngr(ingrList, numOfIngredientsGotten);
            quest.setMonster("orcs");
        } else { // power >= 40
            numOfIngredientsGotten = 3;
            ingrList = fetchIngrListByRarity(1, 5);
            quest = setRandomIngr(ingrList, numOfIngredientsGotten);
            quest.setMonster("wyverns");
        }
        for (int i = 0; i < numOfIngredientsGotten; i++) {
            String ingrName = quest.getIngrNames()[i];
            int amount = quest.getIngrAmounts()[i];
            int ingrId = getIngredientIDFromName(ingrName);
            updateUserIngredient(uid, ingrId, amount);
        }
 
       return quest;
    }

    
    public int getIngredientIDFromName(String ingrName){
        int ingrId = 0;
        try {
            //Converts ingredient name into ingredient id
            getIngredientIDFromName = connection.prepareStatement("select id from "
                    + "ingredients where name = \"" + ingrName+"\"");
            ResultSet result = getIngredientIDFromName.executeQuery();
            if (result.next()) {
                ingrId = result.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingrId;
    }
    
    public void updateUserIngredient(int uid, int ingrId, int ingrAmount) {
        try {
            //Checks if the user has this ingredient
            checkIfIngredientNull = connection.prepareStatement("select * from user_ingredients"
                    + " WHERE user_id = " + uid + " AND ingredient_id = " + ingrId);
            ResultSet result2 = checkIfIngredientNull.executeQuery();
            if (!result2.next()) {
                updateUserIngredients = connection.prepareStatement("INSERT INTO "
                        + "user_ingredients (user_id,ingredient_id,amount) VALUES (?,?,?)");
                updateUserIngredients.setInt(1, uid);
                updateUserIngredients.setInt(2, ingrId);
                updateUserIngredients.setInt(3, ingrAmount);
            } else {
                updateUserIngredients = connection.prepareStatement("UPDATE user_ingredients SET amount = amount+? WHERE user_id = ? AND ingredient_id = ?");
                updateUserIngredients.setInt(1, ingrAmount);
                updateUserIngredients.setInt(2, uid);
                updateUserIngredients.setInt(3, ingrId);
            }
            updateUserIngredients.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // sets ingredients and amounts for a quest
    public Quest setRandomIngr(ArrayList<Ingredient> ingrList, int num){
        Quest q = new Quest();
        String[] ingr = new String[num];
        int[] ingrAmounts = new int[num];
        
        for(int i = 0; i < num; i++){
            ingr[i] = ingrList.get((int)(Math.random()*((ingrList.size()-1)-0+1)+0)).getName();
            ingrAmounts[i] = (int)(Math.random()*(4-i+1)+1);
        }
        q.setIngrAmounts(ingrAmounts);
        q.setIngrNames(ingr);
        return q;
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
            } else if (power > 10){
               quest.setIngr1(getIngredientByID(2).getName());
               quest.setNrIngr1(1);
               
               checkIfIngredientNull = connection.prepareStatement("select * from user_ingredients"
                       + " WHERE user_id = " +uid+ " AND ingredient_id = " + 2);
               ResultSet result2 = checkIfIngredientNull.executeQuery();
               if (!result2.next()){
                   updateUserIngredients= connection.prepareStatement("INSERT INTO "
                           + "user_ingredients (user_id,ingredient_id,amount) VALUES (?,?,?)");
                    updateUserIngredients.setInt(1, uid);
                    updateUserIngredients.setInt(2, allIngredientsByID.get(2));
                    updateUserIngredients.setInt(3, 1);
               } else {
                   updateUserIngredients= connection.prepareStatement("UPDATE user_ingredients SET amount = amount+? WHERE user_id = ? AND ingredient_id = ?");
                    updateUserIngredients.setInt(1, 1);
                    updateUserIngredients.setInt(2, uid);
                    updateUserIngredients.setInt(3, allIngredientsByID.get(2));
                    
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
    
    public ArrayList<BattleItem> fetchAllUserBattleItems(int uid) {
        ArrayList<BattleItem> allUserBattleItems = new ArrayList<>();
        
        try {
            BattleItem bi;
            getAllUserBattleItems = connection.prepareStatement("SELECT * FROM user_battle_items "
                    + "WHERE user_id = " + uid);
            ResultSet result = getAllUserBattleItems.executeQuery();
            while (result.next()) {
                bi = getBattleItemByID(result.getInt("battle_item_id"));
                bi.setAmount(result.getInt("amount"));
                allUserBattleItems.add(bi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allUserBattleItems;
    }
    
    public int[] fetchUserEquipment(int uid){
        int[] equipment = new int[9];
        
        try {
            getUserEquipment = connection.prepareStatement("SELECT * FROM user_equipment "
                    + "WHERE user_id = " + uid);
            ResultSet result = getUserEquipment.executeQuery();
            if(result.next()) {
                equipment[0] = result.getInt("user_id");
                equipment[1] = result.getInt("head");
                equipment[2] = result.getInt("chest");
                equipment[3] = result.getInt("hands");
                equipment[4] = result.getInt("legs");
                equipment[5] = result.getInt("feet");
                equipment[6] = result.getInt("weapon");
                equipment[7] = result.getInt("shield");
                equipment[8] = result.getInt("cauldron");
            }
            else{
                equipment[0] = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return equipment;
    }
    
    public int fetchUserGold(int uid){
        int gold = -1;
        
        try{
            getUserGold = connection.prepareStatement("SELECT gold FROM users "
                    + "WHERE id = " + uid);
            //getUserGold.setInt(1,uid);
            ResultSet result = getUserGold.executeQuery();
            if(result.next()){
                gold = result.getInt("gold");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return gold;    
    }
    
    public ArrayList<BrewingItem> fetchAllBrewingItems(){
        ArrayList<BrewingItem> brewingItems = new ArrayList<>();
        try{
            getBrewingItems = connection.prepareStatement("SELECT * from "
                    + "brewing_items");
            ResultSet result = getBrewingItems.executeQuery();
            while(result.next()){
                brewingItems.add(getBrewingItemById(result.getInt("id")));
            }      
        } catch (Exception e){
            e.printStackTrace();
        }
        return brewingItems;
    }
    
    public BrewingItem getBrewingItemById(int brid){
        BrewingItem item = new BrewingItem();
        try{
            getBrewingItems = connection.prepareStatement("SELECT * from "
                    + "brewing_items where id ="+brid);
            ResultSet result = getBrewingItems.executeQuery();
            if(result.next()){
                item = new BrewingItem(brid,result.getString("name"),result.getInt("effect"),
                result.getInt("purchase_price"));
            }
        } catch (Exception e) {
           e.printStackTrace(); 
        }
        return item;
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
    
    public void brew(int uid, Potion p, int cauldron){
        //Add a potion to user_potions;
        try {
            if (p.getAmount() == 0) {
                updatePotionsAmount = connection.prepareStatement("INSERT INTO "
                        + "user_potions (user_id,potions_id,amount) VALUES (?,?,?)");
                updatePotionsAmount.setInt(3, cauldron);
                //lägg till rad 280+281 vid knas
            } else {
                updatePotionsAmount = connection.prepareStatement("UPDATE user_potions SET amount = amount+"+cauldron+ " "
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
    
    public void equip(int uid, BattleItem bi){ // bi = item being equipped
        
        // if amount of bi in user_battle_items is 1, delete from there
        // else, amount-1
        // if equipped item id doesnt exit in user_battle_items,
        //      insert into user_battle_items the equipped item
        // else, amount+1
        
        // check type, update user_equipment at type with bi id
        // 
        try{               
            //Add unequipped item to user_battle_items;
            int[] equipment = fetchUserEquipment(uid);
            for(int i = 1; i < 8; i++){
                if(bi.getType().equals(getBattleItemByID(equipment[i]).getType())){
                    updateUserBattleItems = connection.prepareStatement("INSERT INTO user_battle_items " 
                        + "(user_id, battle_item_id,amount) VALUES (?,?,1) ON DUPLICATE KEY UPDATE "
                        + "amount=amount+1");
                    updateUserBattleItems.setInt(1, uid);
                    updateUserBattleItems.setInt(2,equipment[i]);
                    updateUserBattleItems.executeUpdate();
                    if(bi.getId() == equipment[i]){
                        bi.setAmount(bi.getAmount()+1);
                    }
                    
                    //Remove the equipped item from inventory
                    if(bi.getAmount() == 1){
                        updateUserBattleItems = connection.prepareStatement("DELETE from "
                        + "user_battle_items WHERE user_id = ? AND battle_item_id = ?");

                    } else {
                        updateUserBattleItems = connection.prepareStatement("UPDATE "
                        + "user_battle_items SET amount=amount-1 WHERE user_id = ? AND battle_item_id = ?");
                    }
                    updateUserBattleItems.setInt(1, uid);
                    updateUserBattleItems.setInt(2,bi.getId());
                    updateUserBattleItems.executeUpdate();
                }
            }

            // add to user_equipment
            updateUserEquipment = connection.prepareStatement("UPDATE user_equipment "
                    + "SET " + bi.getType() + " = ? WHERE user_id = ?");
            updateUserEquipment.setInt(1, bi.getId()); 
            updateUserEquipment.setInt(2, uid);
            updateUserEquipment.executeUpdate();
            
        } catch(Exception e){
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
    
    //update user_battle_items
    //
    //update gold
    public void buyBattleItem(int uid, BattleItem bi){
        
        try {
            if (bi.getAmount() == 0) {
                updateUserBattleItems = connection.prepareStatement("INSERT INTO "
                        + "user_battle_items (user_id,battle_item_id,amount) VALUES (?,?,?)");
                updateUserBattleItems.setInt(3, 1);
                //lägg till rad 280+281 vid knas
            } else {
                updateUserBattleItems = connection.prepareStatement("UPDATE user_battle_items SET amount = amount+1 "
                        + "WHERE user_id = ? AND battle_item_id = ?");
            }
            updateUserBattleItems.setInt(1, uid);
            updateUserBattleItems.setInt(2, bi.getId()); 
            updateUserBattleItems.executeUpdate();
            
            decreaseGold(uid, bi.getPurchasePrice());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void buyBrewingItems(int uid, BrewingItem brew){
        //Always do update on user equipment
        
        //Take money
        
        //
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
    
    // compare user gold to price of item
    public String checkGoldReq(int uid, int price){
        String check = "";
        
        if(fetchUserGold(uid) < price)
            check="disabled";
        
        return check;
    }
    
    //WIP to check ingredient requirements
    public String checkIngrReq(int uid, int ingr1, int ingr2, int ingr3) {
        String check = "";
        try {
            checkIfIngredientNull = connection.prepareStatement("select * from user_ingredients where "
                    + "user_id =" + uid + " and ingredient_id = " + ingr1);
            ResultSet result = checkIfIngredientNull.executeQuery();
            if (!(result.next())) {
                check = "disabled";
            }
            checkIfIngredientNull = connection.prepareStatement("select * from user_ingredients where "
                    + "user_id =" + uid + " and ingredient_id = " + ingr2);
            result = checkIfIngredientNull.executeQuery();
            if (!(result.next())) {
                check = "disabled";
            }
            if (ingr3 != 0) {
                checkIfIngredientNull = connection.prepareStatement("select * from user_ingredients where "
                        + "user_id =" + uid + " and ingredient_id = " + ingr3);
                result = checkIfIngredientNull.executeQuery();
                if (!(result.next())) {
                    check = "disabled";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
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
    
    public void initUser(int uid){
        try{
            updateUserEquipment = connection.prepareStatement("INSERT INTO user_equipment "
                + "(user_id, cauldron) VALUES (?,?)");
            updateUserEquipment.setInt(1,uid);
            updateUserEquipment.setInt(2,1);
            updateUserEquipment.executeUpdate();
            unlockRecipe(uid,getPotionById(1));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }       
}