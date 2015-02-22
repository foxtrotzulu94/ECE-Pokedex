package com.hackers.intj.mchackspokedex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;


public class Pokemon  {

//    public enum attributes {
//        pkdx_id(0),
//        ability1,
//        ability2,
//        ability3,
//        attack,
//        catch_rate,
//        created,
//        defense,
//        description,
//        egg_cycles,
//        egg_group1,
//        egg_group2,
//        ev_yield,
//        evolution_level,
//        evolution_detail,
//        evolution_method,
//        evolution,
//        exp,
//        growth_rate,
//        happiness,
//        height,
//        hp,
//        male_female_ratio,
//        modified,
//        name,
//        national_id,
//        sp_atk,
//        sp_def,
//        species,
//        speed,
//        sprite,
//        type1,
//        type2,
//        weight,
//    }

    String name;
    String description;
    int pkdx_id;
    String weight;
    String height;

    Ability[] abilities = new Ability[3];
    Type Type1;
    Type Type2;

    int hp;
    int attack;
    int defense;
    int sp_att;
    int sp_def;
    int speed;



    public Pokemon(){

    }

    public Pokemon(int pkdx_id, SQLiteDatabase database){

        //Unique Key ID in the DB
        this.pkdx_id = pkdx_id;

        //Ignore function parameters on rawQuery, We're injecting our own string
        Cursor C = database.rawQuery(String.format("SELECT * FROM pokemon WHERE pkdx_id=%s",String.valueOf(pkdx_id)),null);

        //HACK: the position numbers are hard-coded. We really don't expect to change the DB columns at all
        C.getColumnCount();
        C.moveToFirst();
        name = C.getString(24);
        description = C.getString(8);

//        Type1 = new Type(C.getInt(31), database);
//        Type2 = new Type(C.getInt(32), database);

        //TODO: Populate Abilities in Memory (Requires class model)
//        for(int i = 0; i<3; i++){
//
//            if(C.getInt(i+1)==-1){
//                break;
//            }
//            abilities[i] = new Ability(C.getInt(i));
//
//        }

        //The Attack Group Variables
        attack = C.getInt(4);
        defense = C.getInt(7);
        hp = C.getInt(21);
        sp_att = C.getInt(26);
        sp_def = C.getInt(27);
        speed = C.getInt(29);

    }

    public String getName(){

        return name;
    }

    public String getDescription(){
        return description;
    }


//    public Ability[] getAbilities() {
//        return abilities;
//    }

    public int getHp(){
        return hp;
    }
    public int getAttack(){
        return attack;
    }
    public int getDefense(){
        return defense;
    }
    public int getSp_att(){
        return sp_att;
    }
    public int getSp_def(){
        return sp_def;
    }
    public int getSpeed(){
        return speed;
    }
    public int getUniqueId(){
        return pkdx_id;
    }
}
