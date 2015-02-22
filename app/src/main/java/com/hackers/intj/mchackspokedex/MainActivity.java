package com.hackers.intj.mchackspokedex;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.database.Cursor;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private static final String DB_NAME = "pokedex.db";
    private SQLiteDatabase database;
    private ArrayList pokemons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        fillPokemonName();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        TextView t;
//
//
//        t = (TextView) findViewById(R.id.name);
//        t.setText("The COENDex");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void fillPokemonName() {

        pokemons = new ArrayList<String>();
        Cursor nameCursor = database.query("pokemon", new String[] {"pkdx_id",
                "name"}, null, null, null, null, "pkdx_id");

        nameCursor.moveToFirst();
        if(!nameCursor.isAfterLast()) {
            do {
                String name = nameCursor.getString(1);
                String num = nameCursor.getString(0);
                String formatted = String.format("%s. %s",num,name);
                pokemons.add(formatted);
            } while (nameCursor.moveToNext());
        }
        nameCursor.close();
    }



    public void switchToPokedex(View view){
        Intent intent = new Intent(this,PokedexActivity.class);
        intent.putExtra("BoxText",pokemons);
        startActivity(intent);
    }

}
