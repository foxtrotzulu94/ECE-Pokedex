package com.hackers.intj.mchackspokedex;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Thinesh on 2015-02-21.
 */
public class Ability {

    String Name;
    String Description;

   public Ability (int id, SQLiteDatabase database) {



   }

    public void setName(String name) {
        Name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
