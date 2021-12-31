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
       private PreparedStatement getAllIngredients;
       private PreparedStatement updateFoundIngredients;
       private PreparedStatement getUserID;
       private PreparedStatement checkIfIngredientNull;

       int userCount;
       User[] users;
       ArrayList<String> allIngredientsByName;
       ArrayList<Integer> allIngredientsByID;
       
       
       
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
                   updateFoundIngredients = connection.prepareStatement("INSERT INTO "
                           + "user_ingredients (user_id,ingredient_id,amount) VALUES (?,?,?)");
                    updateFoundIngredients.setInt(1, uid);
                    updateFoundIngredients.setInt(2, allIngredientsByID.get(randIndex));
                    updateFoundIngredients.setInt(3, randAmount);
                    updateFoundIngredients.executeUpdate();
               } else {
                   updateFoundIngredients = connection.prepareStatement("UPDATE user_ingredients SET amount = amount+? WHERE user_id = ? AND ingredient_id = ?");
                    updateFoundIngredients.setInt(1, randAmount);
                    updateFoundIngredients.setInt(2, uid);
                    updateFoundIngredients.setInt(3, allIngredientsByID.get(randIndex));
                    updateFoundIngredients.executeUpdate();
               }
             //  updateFoundIngredients = connection.prepareStatement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quest;
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
