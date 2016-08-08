package com.ithub.mda.elearn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar Pahwa on 25-06-2016.
 */
public class ELearnLocalDB extends SQLiteOpenHelper {
    public ELearnLocalDB(Context context) {  super(context, "ELearnLocalDB.db", null, 1);  }
    @Override
    public void onCreate(SQLiteDatabase localDB) {
        localDB.execSQL("create table elearnuserdetails (" +
                "id integer primary key AUTOINCREMENT," +
                "name text,email text," +
                "password text," +
                "profilepic text," +
                "account text," +
                "status integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

