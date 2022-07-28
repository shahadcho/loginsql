package com.example.loginsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context,"Login.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("create Table users( name Text,consumer_ID Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion) {
        myDB.execSQL("Drop Table if exists users");
    }


    public boolean insertData(String name,String consumer_ID){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("consumer_ID",consumer_ID);
        long result = myDB.insert("users",null,contentValues);
        if(result == -1){
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean checkname(String name)
    {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where name = ?", new String[]{name});
        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }}
