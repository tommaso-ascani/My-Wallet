package com.example.my_wallet_app.fragment_transaction;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;

import java.util.ArrayList;
import java.util.List;

public class fragment_transaction extends Fragment {

    private RecyclerView recyclerView;
    private List<Transaction> transactionList;
    private AppCompatActivity activity;

    public fragment_transaction(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_transaction() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionList = new ArrayList<>();

        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO ciao = db.dbDAO();

        transactionList.clear();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for(i=0; i < ciao.getTransactions().size(); i++){
                    transactionList.add(new Transaction(ciao.getTransactions().get(i).getId(), ciao.getTransactions().get(i).Type, ciao.getTransactions().get(i).Account, ciao.getTransactions().get(i).toAccount,  ciao.getTransactions().get(i).Amount, ciao.getTransactions().get(i).Date, ciao.getTransactions().get(i).Position, ciao.getTransactions().get(i).Image));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new Adapter(this.transactionList, activity));

        return view;
    }
}