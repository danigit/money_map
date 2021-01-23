package com.example.moneymap;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Account {

    public String title;
    public String description;
    public double amount;

    public Account(String title, String description, double amount){
        this.title = title;
        this.description = description;
        this.amount = amount;
    }
}
