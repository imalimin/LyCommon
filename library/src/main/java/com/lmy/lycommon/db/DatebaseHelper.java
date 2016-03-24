package com.lmy.lycommon.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by lmy on 2015/10/13.
 */
public class DatebaseHelper extends SQLiteOpenHelper {
    private Context context;
    private final static String DATABASE_NAME = "zdb";
    private final static String SQLITE_SEQUENCE_NAME = "sqlite_sequence";
    private final static int DATABASE_VERSION = 1;

    public DatebaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public DatebaseHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DatebaseHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Context getContext() {
        return context;
    }

    public int getMaxId(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLITE_SEQUENCE_NAME + " WHERE name = ?", new String[]{tableName});
        cursor.moveToFirst();
        if (cursor.getCount() < 1){
            cursor.close();
            return 0;
        }
        int id=cursor.getInt(1);
        cursor.close();
        return id;
    }
}
