package com.example.my_wallet_app.fragment_accounts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_accounts.fragment_accounts;
import com.example.my_wallet_app.fragment_transaction.Transaction;
import com.example.my_wallet_app.fragment_transaction.fragment_transaction;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class fragment_edit_account extends DialogFragment {

    AppCompatActivity activity;
    Account account;
    com.example.my_wallet_app.Database.dbDAO dbDAO;
    MainDatabase db;

    public fragment_edit_account(AppCompatActivity activity, Account account) {
        this.activity = activity;
        this.account = account;
    }

    public fragment_edit_account() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_edit_account, null);

        db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO = db.dbDAO();

        TextView accountText = mView.findViewById(R.id.account);
        TextView balanceText = mView.findViewById(R.id.balanceValue);

        accountText.setText(account.getAccount_Name());

        double temp = Double.parseDouble(account.getAccount_Balance());

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.ITALIAN);
        formatter.format("%(,.2f", temp);
        balanceText.setText(sb + " €");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("DELETE ACCOUNT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<Budget> budgetList = dbDAO.getBudgets();

                                int y;
                                for(y=0; y < dbDAO.getAccountTransactions(account.getAccount_Name()).size(); y++){
                                    int x;
                                    for(x=0; x<budgetList.size(); x++){
                                        double tempAmount = Double.parseDouble(budgetList.get(x).getBudget_Used())-Double.parseDouble(dbDAO.getAccountTransactions(account.getAccount_Name()).get(x).getAmount());
                                        dbDAO.updateBudget(String.valueOf(tempAmount), String.valueOf(budgetList.get(x).getId()));
                                    }
                                }

                                int i;
                                for (i=0; i < dbDAO.getTransactionsType("Transfer").size(); i++){
                                    if(Objects.equals(dbDAO.getTransactionsType("Transfer").get(i).getAccount(), account.Account_Name)){
                                        dbDAO.updateTransferTransaction(null, dbDAO.getTransactionsType("Transfer").get(i).getToAccount(), String.valueOf( dbDAO.getTransactionsType("Transfer").get(i).getId()));
                                    } else if(Objects.equals(dbDAO.getTransactionsType("Transfer").get(i).getToAccount(), account.Account_Name)){
                                        dbDAO.updateTransferTransaction( dbDAO.getTransactionsType("Transfer").get(i).getAccount(), null, String.valueOf( dbDAO.getTransactionsType("Transfer").get(i).getId()));
                                    }
                                    if(Objects.equals(dbDAO.getTransactionsType("Transfer").get(i).getAccount(), null) && Objects.equals(dbDAO.getTransactionsType("Transfer").get(i).getToAccount(), null)){
                                        dbDAO.removeTransaction(String.valueOf(dbDAO.getTransactionsType("Transfer").get(i).getId()));
                                    }
                                }

                                dbDAO.removeAccount(String.valueOf(account.getId()));
                                dbDAO.removeAccountTransactions(account.Account_Name);
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Utilities.insertFragment(activity, new fragment_accounts(activity), fragment_accounts.class.getSimpleName());
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