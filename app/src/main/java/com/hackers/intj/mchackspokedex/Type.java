package com.hackers.intj.mchackspokedex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Type {

    public Type (int id, SQLiteDatabase database){

        if(id!=-1) {
            Cursor C = database.rawQuery("Select name from type where id=" + id, null);
            C.moveToFirst();

            name = C.getString(C.getColumnIndex("name"));
            this.id = id;
        }else
        {
            name = null;
            this.id = -1;
        }
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
