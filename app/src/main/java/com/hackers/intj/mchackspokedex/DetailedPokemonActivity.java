package com.hackers.intj.mchackspokedex;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.drawable.*;

/*
 * TODO: Fix the back button for this view. It crashes when using the ActionBar one :/
 */
public class DetailedPokemonActivity extends Activity {

    private static final String DB_NAME = "pokedex.db";
    private int describePokemon;
    private SQLiteDatabase database;
    private Pokemon fullPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_pokemon);

        //We expect to have an intent here with and ID number
        Intent intent = getIntent();
        describePokemon= intent.getIntExtra("pkdxid",-1);

        //Open the DB
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        //Query the DB
        fullPokemon = new Pokemon(describePokemon,database);

        //Test with TextView
        TextView placeholder = (TextView) findViewById(R.id.testybox);
        String temp = new String();
        temp+=("Selected Pokemon: "+fullPokemon.getName()+"\n");
        temp+=("Description: "+fullPokemon.getDescription()+"\n");
        placeholder.setText(temp);

        //Set name
        TextView placeholderName = (TextView) findViewById(R.id.name);
        placeholderName.setText(fullPokemon.getName());

        //Set types
        TextView placeholderTypes = (TextView) findViewById(R.id.types);
        placeholderTypes.setText("# "+String.valueOf(describePokemon));

        //Test with ImageButton
        ImageView placeholderImage = (ImageView) findViewById(R.id.imageView);
        int id = this.getResources().getIdentifier("p"+String.valueOf(describePokemon), "mipmap", "com.hackers.intj.mchackspokedex");
        placeholderImage.setImageResource(id);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_pokemon, menu);
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

        else if(id==R.id.home || id==R.id.homeAsUp){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
