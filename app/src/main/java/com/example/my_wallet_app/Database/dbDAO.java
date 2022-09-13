package com.example.my_wallet_app.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Calendar.model.Event;
import com.example.my_wallet_app.Notifications.Notification;
import com.example.my_wallet_app.fragment_accounts.Account;
import com.example.my_wallet_app.fragment_transaction.Transaction;

import java.util.List;

@Dao
public interface dbDAO {

    // Account -------------------------------------------------------------------------------------

    @Insert
    void registerAccount(Account account);

    @Query("SELECT * FROM Accounts")
    List<Account> getAccounts();

    @Query("SELECT * FROM Accounts WHERE Account_Name=(:Account)")
    Account getAccount(String Account);

    @Query("DELETE FROM Accounts WHERE id=(:id)")
    void removeAccount(String id);

    // Transaction ---------------------------------------------------------------------------------

    @Insert
    void registerTransaction(Transaction transaction);

    @Query("DELETE FROM Transactions WHERE id=(:id)")
    void removeTransaction(String id);

    @Query("DELETE FROM Transactions WHERE Account=(:Account) AND (Type='Income' OR Type='Expense')")
    void removeAccountTransactions(String Account);

    @Query("SELECT * FROM Transactions WHERE Account=(:Account) AND Type='Income' OR Type='Expense'")
    List<Transaction> getAccountTransactions(String Account);

    @Query("UPDATE Accounts SET Account_Balance = (:Amount) WHERE Account_Name = (:Account)")
    void addTransactionToAccount(String Amount, String Account);

    @Query("UPDATE Transactions SET Account = (:Account), toAccount = (:toAccount) WHERE id = (:id)")
    void updateTransferTransaction(String Account, String toAccount, String id);

    @Query("SELECT * FROM Transactions")
    List<Transaction> getTransactions();

    @Query("SELECT * FROM Transactions WHERE id=(:id)")
    Transaction getTransaction(String id);

    @Query("SELECT * FROM Transactions WHERE Type=(:Type)")
    List<Transaction> getTransactionsType(String Type);

    // Budget --------------------------------------------------------------------------------------

    @Insert
    void registerBudget(Budget budget);

    @Query("SELECT * FROM Budgets")
    List<Budget> getBudgets();

    @Query("SELECT * FROM Budgets")
    LiveData<List<Budget>> getBudgetsLive();

    @Query("SELECT * FROM Budgets WHERE id=(:id)")
    Budget getBudget(String id);

    @Query("UPDATE Budgets SET Budget_Used=(:budget) WHERE id=(:id)")
    void updateBudget(String budget, String id);

    @Query("DELETE FROM Budgets WHERE id=(:id)")
    void removeBudget(String id);

    // Event ---------------------------------------------------------------------------------------

    @Insert
    void registerEvent(Event event);

    @Query("SELECT * FROM Events")
    List<Event> getEvents();

    @Query("SELECT * FROM Events WHERE dateTime=(:id)")
    Event getEventID(long id);

    @Query("SELECT * FROM Events WHERE eventName=(:name)")
    Event getEvent(String name);

    @Query("DELETE FROM Events WHERE dateTime=(:time)")
    void removeEvent(long time);

    // Notification --------------------------------------------------------------------------------

    @Insert
    void registerNotification(Notification notification);

    @Query("SELECT * FROM Notifications")
    List<Notification> getNotifications();

    @Query("UPDATE Notifications SET Read='1' WHERE id=(:id)")
    void readNotification(String id);

    @Query("DELETE FROM Notifications WHERE id=(:id)")
    void removeNotification(String id);

    @Query("SELECT * FROM Notifications")
    LiveData<List<Notification>> getLiveNotifications();
}
