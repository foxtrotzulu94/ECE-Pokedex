package com.hackers.intj.mchackspokedex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Type {

    public Type (int id, SQLiteDatabase database){

        Cursor C = database.rawQuery("Select name from type where id="+id,null);
        C.moveToFirst();

        name = C.getString(6);
        this.id = id;
    }

    String name;
    int id;

    Type [] no_effect;
    Type [] resistance;
    Type [] super_effective;
    Type [] weakness;


    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
