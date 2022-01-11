/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sarab
 */
public class User {
    
    private String username;
    private String password;
    private int gold;
    private int potionsCrafted;
    private int potionsSold;
    
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

    public int getPotionsCrafted() {
        return potionsCrafted;
    }

    public void setPotionsCrafted(int potionsCrafted) {
        this.potionsCrafted = potionsCrafted;
    }

    public int getPotionsSold() {
        return potionsSold;
    }

    public void setPotionsSold(int potionsSold) {
        this.potionsSold = potionsSold;
    }
}