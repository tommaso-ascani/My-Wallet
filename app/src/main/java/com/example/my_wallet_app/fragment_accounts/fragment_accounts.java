package com.example.my_wallet_app.fragment_accounts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;

import java.util.ArrayList;
import java.util.List;

public class fragment_accounts extends Fragment {

    private RecyclerView recyclerView;
    private List<Account> accountList;
    private AppCompatActivity activity;

    public fragment_accounts(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_accounts() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountList = new ArrayList<Account>();


        MainDatabase db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO ciao = db.dbDAO();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for(i=0; i < ciao.getAccounts().size(); i++){
                    accountList.add(new Account(ciao.getAccounts().get(i).getId(), ciao.getAccounts().get(i).Account_Name, ciao.getAccounts().get(i).Account_Balance));
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
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.accountRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        recyclerView.setAdapter(new Adapter(this.accountList, activity));

        return view;
    }
}