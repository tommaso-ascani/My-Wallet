package com.example.my_wallet_app.Budget;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.core.annotations.Line;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_accounts.fragment_add_account;
import com.example.my_wallet_app.fragment_accounts.fragment_edit_account;
import com.example.my_wallet_app.fragment_transaction.fragment_edit_transaction;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    private List<Budget> budgetList;
    AppCompatActivity activity;
    private LinearProgressIndicator linearProgressIndicator;

    public Adapter(List<Budget> budgetList, AppCompatActivity activity) {
        this.activity = activity;
        this.budgetList = budgetList;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView Budget_Name;
        private TextView Budget_Percentage;
        private TextView Budget_Remain;
        private LinearProgressIndicator progressBar;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            Budget_Name = itemView.findViewById(R.id.budgetTitle);
            Budget_Percentage = itemView.findViewById(R.id.budgetPercentage);
            Budget_Remain = itemView.findViewById(R.id.budgetValue);
            progressBar = itemView.findViewById(R.id.budgetProgressBar);
        }

        public TextView getBudget_Name() {
            return Budget_Name;
        }

        public TextView getBudget_Percentage() {
            return Budget_Percentage;
        }

        public TextView getBudget_Remain() {
            return Budget_Remain;
        }

        public LinearProgressIndicator getProgressBar() {
            return progressBar;
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == getItemCount()-1){
            return R.layout.account_add_card;
        }else{
            return R.layout.budget_item;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if(holder.getItemViewType() == R.layout.budget_item){
            Budget currentBudget = budgetList.get(position);

            linearProgressIndicator = holder.getProgressBar();
            linearProgressIndicator.setIndicatorColor(activity.getResources().getColor(R.color.progressBarDefaultColor));
            linearProgressIndicator.setBackgroundColor(activity.getResources().getColor(R.color.progressBarDefaultBackgroundColor));

            double temp1 = Double.parseDouble(currentBudget.getBudgetRemain());
            StringBuilder budgetRemain = new StringBuilder();
            Formatter formatter1 = new Formatter(budgetRemain, Locale.ITALIAN);
            formatter1.format("€ %(,.2f", temp1);

            double temp2 = Double.parseDouble(currentBudget.getUsedPercentage());
            StringBuilder usedPercentage = new StringBuilder();
            Formatter formatter2 = new Formatter(usedPercentage, Locale.ITALIAN);
            formatter2.format("%(,.0f", temp2);

            holder.getBudget_Name().setText(currentBudget.getBudget_Name());
            holder.getBudget_Percentage().setText(usedPercentage + " %");
            holder.getBudget_Remain().setText(budgetRemain);
            holder.getProgressBar().setProgress((int) Double.parseDouble(currentBudget.getUsedPercentage()));

            if(Double.parseDouble(currentBudget.getUsedPercentage()) >= 75 && Double.parseDouble(currentBudget.getUsedPercentage()) < 100){
                linearProgressIndicator.setIndicatorColor(activity.getResources().getColor(R.color.progressBar75Color));
                linearProgressIndicator.setBackgroundColor(activity.getResources().getColor(R.color.progressBar75BackgroundColor));
            }else if(Double.parseDouble(currentBudget.getUsedPercentage()) >= 100){
                linearProgressIndicator.setIndicatorColor(activity.getResources().getColor(R.color.progressBar100Color));
                linearProgressIndicator.setBackgroundColor(activity.getResources().getColor(R.color.progressBar100BackgroundColor));
            }

            holder.itemView.setOnClickListener(l -> {
                fragment_edit_budget newFragment = new fragment_edit_budget(activity, currentBudget);
                newFragment.show(activity.getSupportFragmentManager(), "dialog");
            });
        }else if(holder.getItemViewType() == R.layout.account_add_card){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragment_add_budget newFragment = new fragment_add_budget(activity);

                    if (true) {
                        newFragment.show(fragmentManager, "dialog");
                    } else {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.add(android.R.id.content, newFragment)
                                .addToBackStack(null).commit();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return budgetList.size()+1;
    }
}
