package com.hackers.intj.mchackspokedex;



/**
 * Created by Thinesh on 2015-02-21.
 */
public class Pokemon  {

    public Pokemon(){

    }

    public Pokemon(int pkdx_id, String name){
        //String a =this.description;
        this.pkdx_id = pkdx_id;
        this.name = name;
    }

    String name;
    String description;
    int pkdx_id;
    String weight;
    String height;

    Ability[] abilities;
    Type Type1;
    Type Type2;

    int hp;
    int attack;
    int defense;
    int sp_att;
    int sp_def;






}
