/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sarab
 */
public class Quest {
    String monster = "rats";
    String ingr1 = "";
    int nrIngr1=0;
    String ingr2 = "";
    int nrIngr2=0;
    String ingr3 = "";
    int nrIngr3=0;


    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        this.monster = monster;
    }

    public String getIngr1() {
        return ingr1;
    }

    public void setIngr1(String ingr1) {
        this.ingr1 = ingr1;
    }

    public String getIngr2() {
        return ingr2;
    }

    public void setIngr2(String ingr2) {
        this.ingr2 = ingr2;
    }

    public String getIngr3() {
        return ingr3;
    }

    public void setIngr3(String ingr3) {
        this.ingr3 = ingr3;
    }
    
        public int getNrIngr1() {
        return nrIngr1;
    }

    public void setNrIngr1(int nrIngr1) {
        this.nrIngr1 = nrIngr1;
    }

    public int getNrIngr2() {
        return nrIngr2;
    }

    public void setNrIngr2(int nrIngr2) {
        this.nrIngr2 = nrIngr2;
    }

    public int getNrIngr3() {
        return nrIngr3;
    }

    public void setNrIngr3(int nrIngr3) {
        this.nrIngr3 = nrIngr3;
    }

}
