/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Sara Bertse and Jacob Dwyer
 */
public class BattleItem {
    int id = 0;
    String name = "";
    int effect = 0;
    int purchasePrice = 0;
    int sellPrice = 0;
    String type = "";
    int amount = 0;
    
    public BattleItem(){
        
    }
    
    public BattleItem(int id, String name, int effect, int purchasePrice, int sellPrice, String type){
        this.id = id;
        this.name = name;
        this.effect = effect;
        this.purchasePrice = purchasePrice;
        this.sellPrice = sellPrice;
        this.type = type;
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

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
