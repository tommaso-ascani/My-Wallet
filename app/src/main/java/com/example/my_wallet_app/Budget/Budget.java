package com.example.my_wallet_app.Budget;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "Budget_Name")
    public String Budget_Name;

    @ColumnInfo(name = "Budget_Value")
    public String Budget_Value;

    @ColumnInfo(name = "Budget_Used")
    public String Budget_Used;

    public Budget(String budget_Name, String budget_Value, String budget_Used) {
        this.Budget_Name = budget_Name;
        this.Budget_Value = budget_Value;
        this.Budget_Used = budget_Used;
    }

    public Budget() {
    }

    public int getId() {
        return id;
    }

    public String getBudget_Name() {
        return Budget_Name;
    }

    public String getBudget_Value() {
        return Budget_Value;
    }

    public String getBudget_Used() {
        return Budget_Used;
    }

    public String getUsedPercentage() {
        double value;

        value = Double.parseDouble(getBudget_Used()) / Double.parseDouble(getBudget_Value());
        value = value * 100;

        return String.valueOf(value);
    }

    public String getBudgetRemain(){
        return String.valueOf(Double.parseDouble(getBudget_Value()) - Double.parseDouble(getBudget_Used()));
    }
}
