package com.example.my_wallet_app.fragment_transaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.example.my_wallet_app.fragment_transaction.Transaction;
import com.example.my_wallet_app.fragment_transaction.fragment_transaction;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class fragment_edit_transaction extends DialogFragment {

    AppCompatActivity activity;
    Transaction transaction;
    dbDAO dbDAO;
    MainDatabase db;

    public fragment_edit_transaction(AppCompatActivity activity, Transaction transaction) {
        this.activity = activity;
        this.transaction = transaction;
    }

    public fragment_edit_transaction() {
    }

    @SuppressLint("SetTextI18n")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_edit_transaction, null);

        db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO = db.dbDAO();

        TextView Title = mView.findViewById(R.id.title);
        TextView Account = mView.findViewById(R.id.accountValue);
        TextView toAccount = mView.findViewById(R.id.toAccountValue);
        TextView Amount = mView.findViewById(R.id.amountValue);
        TextView Date = mView.findViewById(R.id.dateValue);
        TextView Position = mView.findViewById(R.id.positionValue);

        ImageView Image = mView.findViewById(R.id.picture_displayed_imageview);

        Title.setText(transaction.getType());
        if(!Objects.equals(transaction.getAccount(), null)){
            Account.setText(transaction.getAccount());
        }else{
            Account.setText("Out of Wallet");
        }

        double temp = Double.parseDouble(transaction.getAmount());
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.ITALIAN);
        formatter.format("%(,.2f", temp);

        if(Objects.equals(transaction.getType(), "Expense")){
            Amount.setText("- " + sb + " €");
            Amount.setTextColor(Color.RED);
            mView.findViewById(R.id.toAccountLayout).setVisibility(View.GONE);
        }else if(Objects.equals(transaction.getType(), "Income")){
            Amount.setText("+ " + sb + " €");
            Amount.setTextColor(Color.GREEN);
            mView.findViewById(R.id.toAccountLayout).setVisibility(View.GONE);
        }else if(Objects.equals(transaction.getType(), "Transfer")){
            if(!Objects.equals(transaction.getToAccount(), null)){
                toAccount.setText(transaction.getToAccount());
            }else{
                toAccount.setText("Out of Wallet");
            }
            Amount.setText(sb + " €");
        }

        Date.setText(transaction.getDate());

        if(transaction.getPosition() != null){
            Position.setText(transaction.getPosition());
        }

        if(transaction.getImage() != null){
            Bitmap bitmap = Utilities.getImageBitmap(activity, Uri.parse(transaction.getImage()));
            Image.setImageBitmap(bitmap);
            Image.setVisibility(View.VISIBLE);
        }

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
                                if(Objects.equals(transaction.getType(), "Expense")){
                                    List<Budget> budgetList = dbDAO.getBudgets();

                                    int i;
                                    for(i=0; i<budgetList.size(); i++){
                                        double tempAmount = Double.parseDouble(budgetList.get(i).getBudget_Used())-Double.parseDouble(transaction.getAmount());
                                        dbDAO.updateBudget(String.valueOf(tempAmount), String.valueOf(budgetList.get(i).getId()));
                                    }

                                    dbDAO.removeTransaction(String.valueOf(transaction.getId()));
                                    Utilities.addToAccount(transaction.getAccount(), transaction.getAmount(), activity);
                                }else if(Objects.equals(transaction.getType(), "Income")){
                                    dbDAO.removeTransaction(String.valueOf(transaction.getId()));
                                    Utilities.removeToAccount(transaction.getAccount(), transaction.getAmount(), activity);
                                }else if(Objects.equals(transaction.getType(), "Transfer")){

                                    Utilities.removeToAccount(transaction.getToAccount(), transaction.getAmount(), activity);
                                    Utilities.addToAccount(transaction.getAccount(), transaction.getAmount(), activity);
                                    dbDAO.removeTransaction(String.valueOf(transaction.getId()));
                                }
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Utilities.insertFragment(activity, new fragment_transaction(activity), fragment_transaction.class.getSimpleName());
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