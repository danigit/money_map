package com.example.moneymap;

/**
 * Class that defines an account
 */
public class Account {

    public String title;
    public String description;
    public double amount;

    public Account(){}

    public Account(String title, String description, double amount){
        this.title = title;
        this.description = description;
        this.amount = amount;
    }
}
