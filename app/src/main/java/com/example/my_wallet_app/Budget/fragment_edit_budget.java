package com.example.my_wallet_app.Budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.example.my_wallet_app.fragment_planning;

import java.util.Formatter;
import java.util.Locale;

public class fragment_edit_budget extends DialogFragment {

    AppCompatActivity activity;
    Budget budget;
    com.example.my_wallet_app.Database.dbDAO dbDAO;
    MainDatabase db;

    public fragment_edit_budget(AppCompatActivity activity, Budget budget) {
        this.activity = activity;
        this.budget = budget;
    }

    public fragment_edit_budget() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_edit_budget, null);

        db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO = db.dbDAO();

        double temp1 = Double.parseDouble(budget.getBudget_Used());
        double temp2 = Double.parseDouble(budget.getBudget_Value());

        StringBuilder budgetUsedFormatted = new StringBuilder();
        Formatter formatter1 = new Formatter(budgetUsedFormatted, Locale.ITALIAN);
        formatter1.format("%(,.2f", temp1);

        StringBuilder budgetValueFormatted = new StringBuilder();
        Formatter formatter2 = new Formatter(budgetValueFormatted, Locale.ITALIAN);
        formatter2.format("%(,.2f", temp2);

        TextView budgetName = mView.findViewById(R.id.budget);
        TextView budgetUsed = mView.findViewById(R.id.budgetUsedValue);
        TextView budgetValue = mView.findViewById(R.id.budgetValueValue);

        budgetName.setText(budget.getBudget_Name());
        budgetUsed.setText(budgetUsedFormatted+ " €");
        budgetValue.setText(budgetValueFormatted + " €");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dbDAO.removeBudget(String.valueOf(budget.getId()));
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Utilities.insertFragment(activity, new fragment_planning(activity), fragment_planning.class.getSimpleName());
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if(b != null){
            b.setTextColor(Color.RED);
        }

        return dialog;
    }
}