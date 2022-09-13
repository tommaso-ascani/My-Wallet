package com.example.my_wallet_app;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_accounts.fragment_accounts;
import com.example.my_wallet_app.fragment_transaction.Transaction;
import com.google.android.material.navigation.NavigationBarView;

import java.io.InputStream;
import java.util.List;

public class Utilities {

    public static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment, tag);
        transaction.addToBackStack(tag);

        transaction.commit();
    }

    public static void addToAccount(String Account, String Amount, Activity activity) {

        MainDatabase db = MainDatabase.getDb(activity.getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                double oldAmount = Double.parseDouble(dbDAO.getAccount(Account).Account_Balance);
                double newAmount = oldAmount + Double.parseDouble(Amount);
                dbDAO.addTransactionToAccount(String.valueOf(newAmount), Account);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void removeToAccount(String Account, String Amount, Activity activity) {

        MainDatabase db = MainDatabase.getDb(activity.getApplicationContext());
        dbDAO dbDAO = db.dbDAO();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                double oldAmount = Double.parseDouble(dbDAO.getAccount(Account).Account_Balance);
                double newAmount = oldAmount - Double.parseDouble(Amount);
                dbDAO.addTransactionToAccount(String.valueOf(newAmount), Account);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageBitmap(Activity activity, Uri currentPhotoUri){
        ContentResolver resolver = activity.getApplicationContext()
                .getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(currentPhotoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
