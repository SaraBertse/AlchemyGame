package model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Sara Bertse and Jacob Dwyer
 */
public class Potion {
    int id = 0;
    String name = "";
    int ingredient1ID = 0;
    int ingredient2ID = 0;
    int ingredient3ID = 0;
    int amount = 0;
    int sellPrice = 0;
    
    // fixes error in dbhandler DONT TOUCH!!
    public Potion(){
        
    }
    
    public Potion(int id, String name, int ingredient1ID, int ingredient2ID, int sellPrice){
        this.id = id;
        this.name = name;
        this.ingredient1ID = ingredient1ID;
        this.ingredient2ID = ingredient2ID;
        this.sellPrice = sellPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIngredient1ID() {
        return ingredient1ID;
    }

    public void setIngredient1ID(int ingredient1ID) {
        this.ingredient1ID = ingredient1ID;
    }

    public int getIngredient2ID() {
        return ingredient2ID;
    }

    public void setIngredient2ID(int ingredient2ID) {
        this.ingredient2ID = ingredient2ID;
    }

    public int getIngredient3ID() {
        return ingredient3ID;
    }

    public void setIngredient3ID(int ingredient3ID) {
        this.ingredient3ID = ingredient3ID;
    }
    
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
}
