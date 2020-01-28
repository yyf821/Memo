package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "starbuzz";
    private static final int DB_VERSION = 2;

    DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE MEMO (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "BODY TEXT, "
                    + "TIME TEXT);");
        }
        if (oldVersion < 2) {
            insertMemo(db,"aa","111","1999/12/21 11:22:33");
            insertMemo(db,"bb","222","2020/01/21 01:22:33");
            insertMemo(db,"cc","asdfghjkl;","2020/01/27 01:22:33");
        }
    }
    private static void insertMemo(SQLiteDatabase db, String name,
                                   String body, String time) {
        ContentValues memoValues = new ContentValues();
        memoValues.put("NAME", name);
        memoValues.put("BODY", body);
        memoValues.put("TIME", time);
        db.insert("MEMO", null, memoValues);
    }

}
