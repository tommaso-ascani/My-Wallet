package com.example.my_wallet_app.Budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_accounts.fragment_accounts;
import com.example.my_wallet_app.fragment_accounts.fragment_add_account;
import com.example.my_wallet_app.fragment_planning;

import java.util.Objects;

public class fragment_add_budget extends DialogFragment {

    AppCompatActivity activity;
    EditText title;
    EditText amount;

    public fragment_add_budget(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_add_budget() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_add_budget, null);

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        title = mView.findViewById(R.id.titleValue);
        amount = mView.findViewById(R.id.amountValue);

        builder.setView(mView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(!amount.getText().toString().equals("")) {
                            if(Double.parseDouble(amount.getText().toString()) != 0) {
                                if(!title.getText().toString().equals("")){
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dbDAO.registerBudget(new Budget(title.getText().toString(), amount.getText().toString(), "0"));
                                        }
                                    });
                                    thread.start();
                                    try {
                                        thread.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Utilities.insertFragment(activity, new fragment_planning(activity), fragment_planning.class.getSimpleName());
                                } else {
                                    Toast toast = Toast.makeText(getContext(), "Titolo non inserito, inserirne uno!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Importo uguale a zero o non inserito!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(getContext(), "Importo uguale a zero o non inserito!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}