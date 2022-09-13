package com.example.my_wallet_app.fragment_accounts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_wallet_app.R;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    private List<Account> accountList;
    AppCompatActivity activity;

    public Adapter(List<Account> accountList, AppCompatActivity activity) {
        this.activity = activity;
        this.accountList = accountList;
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView account_name;
        private TextView account_balance;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            account_name = itemView.findViewById(R.id.account_name);
            account_balance = itemView.findViewById(R.id.account_balance);
        }

        public TextView getAccount_name() {
            return account_name;
        }

        public TextView getAccount_balance() {
            return account_balance;
        }
    }




    @Override
    public int getItemViewType(final int position) {
        if (position == getItemCount()-1){
            return R.layout.account_add_card;
        }else{
            return R.layout.accountscard;
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if(holder.getItemViewType() == R.layout.accountscard){
            Account currentAccount = accountList.get(position);

            double temp = Double.parseDouble(currentAccount.getAccount_Balance());

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.ITALIAN);
            formatter.format("€ %(,.2f", temp);

            holder.getAccount_name().setText(currentAccount.getAccount_Name());
            holder.getAccount_balance().setText(String.valueOf(sb));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment_edit_account newFragment = new fragment_edit_account(activity, currentAccount);
                    newFragment.show(activity.getSupportFragmentManager(), "dialog");
                }
            });
        }else if(holder.getItemViewType() == R.layout.account_add_card){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragment_add_account newFragment = new fragment_add_account(activity);

                    if (true) {
                        // The device is using a large layout, so show the fragment as a dialog
                        newFragment.show(fragmentManager, "dialog");
                    } else {
                        // The device is smaller, so show the fragment fullscreen
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        // For a little polish, specify a transition animation
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        // To make it fullscreen, use the 'content' root view as the container
                        // for the fragment, which is always the root view for the activity
                        transaction.add(android.R.id.content, newFragment)
                                .addToBackStack(null).commit();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return accountList.size()+1;
    }
}
