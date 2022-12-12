package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_ACCNO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.TABLE_ACCOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_BANKNAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_ACCHOLDERNAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_BALANCE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final DataBaseHandler dataBaseHandler;
    private SQLiteDatabase db;
    public PersistentAccountDAO(Context context){
        dataBaseHandler = new DataBaseHandler(context);
    }

    public Account getAccount(String accountNum) throws InvalidAccountException {

        db = dataBaseHandler.getReadableDatabase();
        String select = COL_ACCNO + " = ?";
        String[] colSet ={COL_ACCNO, COL_BANKNAME, COL_ACCHOLDERNAME, COL_BALANCE};
        Cursor cursor = db.query(
                TABLE_ACCOUNT,
                colSet,
                select,
                new String[]{accountNum},
                null,
                null,
                null
        );

        if (cursor != null){
            cursor.moveToFirst();

            Account account = new Account(accountNum, cursor.getString(cursor.getColumnIndex(COL_BANKNAME)),
                    cursor.getString(cursor.getColumnIndex(COL_ACCHOLDERNAME)), cursor.getDouble(cursor.getColumnIndex(COL_BALANCE)));
            return account;

        }
        else {
            String exceptionMessage = "Account " + accountNum + " is invalid.";
            throw new InvalidAccountException(exceptionMessage);
        }
    }


    public List<String> getAccountNumbersList(){
        db = dataBaseHandler.getReadableDatabase();
        List<String> accountNumbersList = new ArrayList<String>();
        Cursor cursor = db.query(
                TABLE_ACCOUNT,
                new String[]{COL_ACCNO}, null, null, null, null, null
        );

        while(cursor.moveToNext()) {
            String accountNum = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACCNO));
            accountNumbersList.add(accountNum);
        }
        cursor.close();
        return accountNumbersList;
    }

    public List<Account> getAccountsList() {

        db = dataBaseHandler.getReadableDatabase();
        List<Account> accountsList = new ArrayList<Account>();
        Cursor cursor = db.query(
                TABLE_ACCOUNT,
                new String[]{COL_ACCNO, COL_BANKNAME, COL_ACCHOLDERNAME, COL_BALANCE}, null, null, null, null, null
        );

        while(cursor.moveToNext()) {
            String accountNum = cursor.getString(cursor.getColumnIndex(COL_ACCNO));
            String bankName = cursor.getString(cursor.getColumnIndex(COL_BANKNAME));
            String holdersName = cursor.getString(cursor.getColumnIndex(COL_ACCHOLDERNAME));
            double initialBalance = cursor.getDouble(cursor.getColumnIndex(COL_BALANCE));
            Account account = new Account(accountNum,bankName,holdersName,initialBalance);
            accountsList.add(account);
        }
        cursor.close();
        return accountsList;
    }


    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db = dataBaseHandler.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, COL_ACCNO + " = ?",new String[]{accountNo});
        db.close();
    }
    @Override
    public void addAccount(Account account) {
        db = dataBaseHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ACCNO,account.getAccountNo());
        contentValues.put(COL_BANKNAME,account.getBankName());
        contentValues.put(COL_ACCHOLDERNAME,account.getAccountHolderName());
        contentValues.put(COL_BALANCE,account.getBalance());
        //inserting
        db.insert(TABLE_ACCOUNT,null,contentValues);
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        db = dataBaseHandler.getWritableDatabase();
        String selection = COL_ACCNO + " = ?";
        Cursor cursor = db.query(
                TABLE_ACCOUNT,
                new String[]{COL_BALANCE},
                selection,
                new String[]{accountNo}, null, null, null
        );
        double balance;
        if(cursor.moveToFirst()){
            balance = cursor.getDouble(0);
        }
        else{
            String invalid = "Account " + accountNo + "is invalid";
            throw new InvalidAccountException(invalid);
        }

        ContentValues contentValues = new ContentValues();
        if(expenseType == ExpenseType.INCOME){
            contentValues.put(COL_BALANCE,balance+amount);
        }
        else{
            contentValues.put(COL_BALANCE, balance - amount);
        }
        db.update(TABLE_ACCOUNT,contentValues, COL_ACCNO + " =?", new String[] {accountNo});
        cursor.close();
        db.close();

    }


}


