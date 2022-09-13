package com.example.my_wallet_app.Notifications;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "Name")
    public String Name;

    @ColumnInfo(name = "Text")
    public String Text;

    @ColumnInfo(name = "Date")
    public String Date;

    @ColumnInfo(name = "Read")
    public boolean Read;

    public Notification(String name, String text, String date) {
        this.Name = name;
        this.Text = text;
        this.Date = date;
        this.Read = false;
    }

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public String getText() {
        return Text;
    }

    public String getDate() {
        return Date;
    }

    public boolean isRead() {
        return Read;
    }
}
