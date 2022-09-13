package com.example.my_wallet_app.fragment_accounts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;

import java.util.Objects;


public class fragment_add_account extends DialogFragment {

    AppCompatActivity activity;

    public fragment_add_account(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_add_account() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        View mView = inflater.inflate(R.layout.fragment_add_account_dialog, null);
        final EditText accountValue = mView.findViewById(R.id.accountValue);
        final EditText amountValue = mView.findViewById(R.id.amountValue);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(accountValue != null && amountValue != null){
                            if(amountValue.getText().toString().equals("")){
                                amountValue.setText("0");
                            }
                            Log.i("TAG", amountValue.getText().toString());

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int i;
                                    boolean register = true;
                                    for(i=0; i < dbDAO.getAccounts().size(); i++){
                                        if(Objects.equals(dbDAO.getAccounts().get(i).Account_Name, accountValue.getText().toString())){
                                            register = false;
                                        }
                                    }
                                    if(register){
                                        dbDAO.registerAccount(new Account(accountValue.getText().toString(), amountValue.getText().toString()));
                                    }
                                }
                            });
                            thread.start();
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Utilities.insertFragment(activity, new fragment_accounts(activity), fragment_accounts.class.getSimpleName());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fragment_add_account.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}