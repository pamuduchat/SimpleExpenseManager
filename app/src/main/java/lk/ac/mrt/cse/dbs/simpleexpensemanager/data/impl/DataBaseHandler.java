package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "200089G.sqllite";


    public static final String TABLE_ACCOUNT ="account" ;
    public static final String TABLE_TRANSACTION = "transactiontable";

    public static final String COL_ACCNO = "accountNo";
    public static final String COL_BANKNAME = "bankName";
    public static final String COL_ACCHOLDERNAME = "HoldersName";
    public static final String COL_BALANCE = "balance";

    public static final String COL_ID = "ID";
    public static final String COl_DATE = "date";
    public static final String COL_EXPENSETYPE = "expenseType";
    public static final String COL_AMOUNT = "amount";

    public DataBaseHandler(Context con){
        super(con,DB_NAME,null,1);
    }

    public void onCreate(SQLiteDatabase database){

        String query1 = "CREATE TABLE " + TABLE_ACCOUNT + "(" + COL_ACCNO + " TEXT PRIMARY KEY, " + COL_BANKNAME + " TEXT NOT NULL, " + COL_ACCHOLDERNAME + " TEXT NOT NULL, " + COL_BALANCE + " REAL NOT NULL)";
        String query2 = "CREATE TABLE " + TABLE_TRANSACTION + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COl_DATE + " TEXT NOT NULL, " + COL_EXPENSETYPE + " TEXT NOT NULL, " + COL_AMOUNT + " REAL NOT NULL, " + COL_ACCNO + " TEXT," + "FOREIGN KEY (" + COL_ACCNO + ") REFERENCES " + TABLE_ACCOUNT + "(" + COL_ACCNO + "))";

        database.execSQL(query1);
        database.execSQL(query2);
    }
    public void onUpgrade(SQLiteDatabase database,int old,int newV){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        onCreate(database);
    }
}
