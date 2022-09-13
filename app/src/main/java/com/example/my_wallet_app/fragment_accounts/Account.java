package com.example.my_wallet_app.fragment_accounts;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Accounts")
public class Account {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "Account_Name")
    public String Account_Name;

    @ColumnInfo(name = "Account_Balance")
    public String Account_Balance;

    public Account(String Account_Name, String Account_Balance) {
        this.Account_Name = Account_Name;
        this.Account_Balance = Account_Balance;
    }

    public Account(int id, String Account_Name, String Account_Balance) {
        this.id = id;
        this.Account_Name = Account_Name;
        this.Account_Balance = Account_Balance;
    }

    public Account() {

    }

    public String getAccount_Name() {
        return Account_Name;
    }

    public String getAccount_Balance() {
        return Account_Balance;
    }

    public int getId() {
        return id;
    }
}
