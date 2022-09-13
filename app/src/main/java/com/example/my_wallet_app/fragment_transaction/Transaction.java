package com.example.my_wallet_app.fragment_transaction;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "Type")
    public String Type;

    @ColumnInfo(name = "Amount")
    public String Amount;

    @ColumnInfo(name = "Account")
    public String Account;

    @ColumnInfo(name = "toAccount")
    public String toAccount;

    @ColumnInfo(name = "Date")
    public String Date;

    @ColumnInfo(name = "Position")
    public String Position;

    @ColumnInfo(name = "Image")
    public String Image;

    public Transaction(String Type, String Account, String toAccount, String Amount, String Date, String Position, String Image) {
        this.Type = Type;
        this.Account = Account;
        this.toAccount = toAccount;
        this.Amount = Amount;
        this.Date = Date;
        this.Image = Image;
        this.Position = Position;
    }

    public Transaction(int id, String Type, String Account, String toAccount, String Amount, String Date, String Position, String Image) {
        this.id = id;
        this.Type = Type;
        this.Account = Account;
        this.toAccount = toAccount;
        this.Amount = Amount;
        this.Date = Date;
        this.Image = Image;
        this.Position = Position;
    }

    public Transaction() {

    }

    public int getId() {
        return id;
    }

    public String getType() { return Type; }

    public String getAmount() {
        return Amount;
    }

    public String getAccount() {
        return Account;
    }

    public String getToAccount() {
        return toAccount;
    }

    public String getDate() {
        return Date;
    }

    public String getImage() {
        return Image;
    }

    public String getPosition() {
        return Position;
    }
}