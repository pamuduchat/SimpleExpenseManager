package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_ACCNO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COl_DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.COL_EXPENSETYPE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler.TABLE_TRANSACTION;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DataBaseHandler databaseManager;
    private SQLiteDatabase db;
    public PersistentTransactionDAO(Context con) {
        databaseManager = new DataBaseHandler(con);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        db = databaseManager.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ACCNO,accountNo);
        contentValues.put(COl_DATE, dateFormat.format(date));
        contentValues.put(COL_AMOUNT,amount);
        contentValues.put(COL_EXPENSETYPE,String.valueOf(expenseType));
        db.insert(TABLE_TRANSACTION,null,contentValues);
        db.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactionsList = new ArrayList<>();
        db = databaseManager.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TRANSACTION,
                new String[]{COl_DATE, COL_ACCNO, COL_EXPENSETYPE, COL_AMOUNT}, null, null, null, null, null
        );
        if(cursor.moveToFirst()) {
             do {
                String dateString = cursor.getString(cursor.getColumnIndex(COl_DATE));
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                String account = cursor.getString(cursor.getColumnIndex(COL_ACCNO));
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(COL_EXPENSETYPE)));
                double amount = cursor.getDouble(cursor.getColumnIndex(COL_AMOUNT));
                Transaction newTransaction = new Transaction(date, account, expenseType, amount);
                transactionsList.add(newTransaction);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return transactionsList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactionsList = new ArrayList<>();
        db = databaseManager.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TRANSACTION,
                new String[]{COl_DATE, COL_ACCNO, COL_EXPENSETYPE, COL_AMOUNT}, null, null, null, null, null
        );
        int size = cursor.getCount();
        if(cursor.moveToFirst()){
            do {
                String dateString = cursor.getString(cursor.getColumnIndex(COl_DATE));
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                String account = cursor.getString(cursor.getColumnIndex(COL_ACCNO));
                ExpenseType expense = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(COL_EXPENSETYPE)));
                double amount = cursor.getDouble(cursor.getColumnIndex(COL_AMOUNT));
                Transaction newTransaction = new Transaction(date, account, expense, amount);
                transactionsList.add(newTransaction);
            }while(cursor.moveToNext());
        }
        if(size<=limit){
            return transactionsList;
        }
        return transactionsList.subList(size-limit,size);


    }
}
