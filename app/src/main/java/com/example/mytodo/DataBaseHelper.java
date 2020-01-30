package com.example.mytodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public final class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context) {
        super(context, "TaskToBeDone.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table TaskToBeDone (UID INTEGER PRIMARY KEY AUTOINCREMENT,  Title text, Details text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists TaskToBeDone");
        onCreate(db);
    }


    public static void writeData(SQLiteDatabase db, String heading, String detail) {
        ContentValues values = new ContentValues();
        values.put("Title", heading);
        values.put("Details", detail);
        db.insert("TaskToBeDone", null, values);
    }

    public static ArrayList<String> getData(SQLiteDatabase db, int num) {
        ArrayList<String> array = new ArrayList<>();
        String projection[] = {"UID", "Title", "Details"};
        Cursor c = db.query("TaskToBeDone", projection, null, null, null, null, null);
        c.moveToPosition(num);
        array.add(c.getString(1));
        array.add(c.getString(2));
        return array;
    }

    public static void delete(SQLiteDatabase db, String name) {
        String projection[] = {"UID", "Title", "Details"};
        Cursor c = db.query("TaskToBeDone", projection, null, null, null, null, null);
        c.moveToPosition(-1);
        while (c.moveToNext()) {
            if (c.getString(1).equals(name))
                db.delete("TaskToBeDone", "UID" + " = " + c.getString(0), null);
        }
    }

    public static long getProfilesCount(SQLiteDatabase db, String name) {
        long count = 0;
        if (name.equals("TaskToBeDone"))
            count = DatabaseUtils.queryNumEntries(db, "TaskToBeDone");
        return count;
    }

}
