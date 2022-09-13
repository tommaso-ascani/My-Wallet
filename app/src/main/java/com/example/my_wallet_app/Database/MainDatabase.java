package com.example.my_wallet_app.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Notifications.Notification;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_transaction.Transaction;

@Database(entities = {Account.class, Transaction.class, Budget.class, Event.class, Notification.class}, version = 8)
public abstract class MainDatabase extends RoomDatabase {

    private static final String dbName = "My_Wallet";
    private static MainDatabase db;

    public static synchronized MainDatabase getDb(Context context){
        if (db == null){
            db = Room.databaseBuilder(context, MainDatabase.class, dbName).fallbackToDestructiveMigration().build();
        }
        return db;
    }

    public abstract dbDAO dbDAO();
}
