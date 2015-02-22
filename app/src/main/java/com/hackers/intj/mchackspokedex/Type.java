package com.hackers.intj.mchackspokedex;

import com.orm.SugarRecord;

/**
 * Created by Thinesh on 2015-02-21.
 */
public class Type extends SugarRecord<Type> {

    String name;
    int id;

    Type [] no_effect;
    Type [] resistance;
    Type [] super_effective;
    Type [] weakness;

}
