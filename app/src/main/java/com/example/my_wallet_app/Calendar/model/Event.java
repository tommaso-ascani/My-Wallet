package com.example.my_wallet_app.Calendar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "dateTime")
    public long time;

    @ColumnInfo(name = "eventName")
    public String eventName;

    @ColumnInfo(name = "eventText")
    public String eventText;

    @ColumnInfo(name = "eventType")
    public String Type;

    @ColumnInfo(name = "eventAmount")
    public String Amount;

    @ColumnInfo(name = "eventAccount")
    public String Account;

    public Event(long time, String eventText, String type, String amount, String account) {
        this.time = time;
        this.eventText = eventText;
        this.Type = type;
        this.Amount = amount;
        this.Account = account;
    }

    public Event(long time, String eventName, String eventText, String type, String amount, String account) {
        this.time = time;
        this.eventName = eventName;
        this.eventText = eventText;
        this.Type = type;
        this.Amount = amount;
        this.Account = account;
    }

    public Event() {
    }

    public Event(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getEventText() {
        return eventText;
    }

    public String getType() {
        return Type;
    }

    public String getAmount() {
        return Amount;
    }

    public String getAccount() {
        return Account;
    }
}
