package com.example.moneymap;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MoneyMap extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
