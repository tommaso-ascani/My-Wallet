package com.example.my_wallet_app.fragment_transaction;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_wallet_app.R;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    private List<Transaction> transactionList;
    AppCompatActivity activity;

    public Adapter(List<Transaction> transactionList, AppCompatActivity activity) {
        this.transactionList = transactionList;
        this.activity = activity;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView transaction_account;
        private TextView transaction_toAccount;
        private TextView transaction_date;
        private TextView transaction_amount;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_account = itemView.findViewById(R.id.transaction_account);
            transaction_toAccount = itemView.findViewById(R.id.transaction_toAccount);
            transaction_date = itemView.findViewById(R.id.transaction_date);
            transaction_amount = itemView.findViewById(R.id.transaction_amount);
        }

        public TextView getTransaction_account() {
            return transaction_account;
        }

        public TextView getTransaction_toAccount() {
            return transaction_toAccount;
        }

        public TextView getTransaction_date() {
            return transaction_date;
        }

        public TextView getTransaction_amount() {
            return transaction_amount;
        }
    }




    @Override
    public int getItemViewType(final int position) {
        return R.layout.transactioncard;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new com.example.my_wallet_app.fragment_transaction.Adapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Transaction currentTransaction = transactionList.get(position);
        if(Objects.equals(currentTransaction.getAccount(), null)){
            holder.getTransaction_account().setText("Out of Wallet");
        }else{
            holder.getTransaction_account().setText(currentTransaction.getAccount());
        }
        holder.getTransaction_date().setText(currentTransaction.getDate());

        double temp = Double.parseDouble(currentTransaction.getAmount());
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.ITALIAN);
        formatter.format("%(,.2f", temp);

        if(Objects.equals(currentTransaction.getType(), "Expense")){
            holder.getTransaction_amount().setText("- " + sb + " €");
            holder.getTransaction_amount().setTextColor(Color.RED);
        }else if(Objects.equals(currentTransaction.getType(), "Income")){
            holder.getTransaction_amount().setText("+ " + sb + " €");
            holder.getTransaction_amount().setTextColor(Color.GREEN);
        }else if(Objects.equals(currentTransaction.getType(), "Transfer")){
            holder.getTransaction_amount().setText(sb + " €");
            holder.itemView.findViewById(R.id.transaction_toAccount).setVisibility(View.VISIBLE);
            if(Objects.equals(currentTransaction.getToAccount(), null)){
                holder.getTransaction_toAccount().setText("Out of Wallet");
            }else{
                holder.getTransaction_toAccount().setText(currentTransaction.getToAccount());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment_edit_transaction newFragment = new fragment_edit_transaction(activity, currentTransaction);
                newFragment.show(activity.getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
