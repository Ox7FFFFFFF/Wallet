package dee.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dee on 2017/11/9.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "wallet.db";
    private static int DATABASE_VERSION = 1;
    public static String RECORD_TABLE_NAME = "record";
    public static String CATEGORY_TABLE_NAME = "category";
    public static String CLOCK_TABLE_NAME = "clock";
    private static String CREATE_RECORD_TABLE = "CREATE TABLE "+RECORD_TABLE_NAME+" (" +
            "_id INTEGER PRIMARY KEY NOT NULL,"+
            "_name VARCHAR,"+
            "_cost INTEGER,"+
            "_date DATETIME,"+
            "_category INTEGER,"+
            "FOREIGN KEY(_category) REFERENCES "+CATEGORY_TABLE_NAME+"(_id));";
    private static String CREATE_CATEGORY_TABLE = "CREATE TABLE "+CATEGORY_TABLE_NAME+" ("+
            "_id INTEGER PRIMARY KEY NOT NULL,"+
            "_type INTEGER,"+
            "_name VARCHAR);";
    private static String CREATE_CLOCK_TABLE = "CREATE TABLE "+CLOCK_TABLE_NAME+" ("+
            "_id INTEGER PRIMARY KEY NOT NULL,"+
            "_hour INTEGER,"+
            "_minute INTEGER,"+
            "_duration VARCHAR,"+
            "_turn INTEGER);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_RECORD_TABLE);
        db.execSQL(CREATE_CLOCK_TABLE);

        String[] categoryExpenseList = new String[] {"Activity","School","Lunch","Breakfast","Dinner"};
        String[] categoryIncomeList = new String[] {"Salary","Home"};

        for(int i=0;i<categoryExpenseList.length;i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put("_type",0);
            contentValues.put("_name",categoryExpenseList[i]);
            db.insert(CATEGORY_TABLE_NAME,null,contentValues);
        }
        for(int i=0;i<categoryIncomeList.length;i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put("_type",1);
            contentValues.put("_name",categoryIncomeList[i]);
            db.insert(CATEGORY_TABLE_NAME,null,contentValues);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+CATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE "+RECORD_TABLE_NAME);
        db.execSQL("DROP TABLE "+CLOCK_TABLE_NAME);
    }
}
