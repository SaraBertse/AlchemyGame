/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
public class User {
    
    private String username;
    private String password;
    private int gold;
    private int potions_crafted;
    private int potions_sold;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getPotions_crafted() {
        return potions_crafted;
    }

    public void setPotions_crafted(int potions_crafted) {
        this.potions_crafted = potions_crafted;
    }

    public int getPotions_sold() {
        return potions_sold;
    }

    public void setPotions_sold(int potions_sold) {
        this.potions_sold = potions_sold;
    }
}