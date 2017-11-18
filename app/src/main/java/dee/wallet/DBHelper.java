package dee.wallet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dee on 2017/11/9.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "wallet.db";
    private static int DATABASE_VERSION = 1;
    private static String RECORD_TABLE_NAME = "record";
    private static String CATEGORY_TABLE_NAME = "category";
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



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_RECORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE "+CATEGORY_TABLE_NAME;
        db.execSQL(SQL);
        String SQL1 = "DROP TABLE "+RECORD_TABLE_NAME;
        db.execSQL(SQL1);
    }
}
