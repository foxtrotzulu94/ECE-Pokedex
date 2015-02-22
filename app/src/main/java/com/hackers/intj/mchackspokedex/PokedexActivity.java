package com.hackers.intj.mchackspokedex;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PokedexActivity extends ActionBarActivity {

    private static final String DB_NAME = "pokedex.db";

    //UI Elements in Activity
    private int pokemonInView;
    private ImageButton pokemonSprite; //android:id="@+id/pokemonSprite"
    private TextView pokemonDescriber; //android:id="@+id/pokemonDescriber"
    private ListView pokemonList; //android:id="@+id/listView"

    //Database Handle
    private SQLiteDatabase database;

    private List<String> DisplayText;
    private ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        //Initialize
        pokemonList = (ListView) findViewById(R.id.listView);
        pokemonDescriber = (TextView) findViewById(R.id.pokemonDescriber);
        pokemonSprite = (ImageButton) findViewById(R.id.pokemonSprite);

        //Receive an Intent in case
        Intent intent = getIntent();
        DisplayText = intent.getStringArrayListExtra("BoxText");

        //The intent data is received and is used to create an ArrayAdapter, passed on to the list.
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                DisplayText);

        //Initialize the Database Handler
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        //Manipulate the ListView
        pokemonList.setAdapter(arrayAdapter);
        //And register the listener to it
        pokemonList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Just update our column
                //Toast.makeText(getBaseContext(), "Some Stuff=" + id + " Pos=" + position, Toast.LENGTH_LONG).show();
            //Old Code that switched to the Detailed
//                Intent intent = new Intent(PokedexActivity.this, DetailedPokemonActivity.class);
//                String data=(String)parent.getItemAtPosition(position);
//                String ID = data.split("[. ]+")[0];
//                intent.putExtra("pkdxid", Integer.parseInt(ID));
//                startActivity(intent);
            //End Old Code
                String data=(String)parent.getItemAtPosition(position);
                String ID = data.split("[. ]+")[0];
                Toast.makeText(getBaseContext(),ID,Toast.LENGTH_LONG);
                pokemonInView = Integer.parseInt(ID);
                UpdateColumn(pokemonInView);
            }

        });
        //Debug Text
        //Toast.makeText(getBaseContext(), "Some Stuff", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pokedex, menu);
        EditText a = (EditText) findViewById(R.id.inputSearch);

        a.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence a, int b, int c, int d){
                                PokedexActivity.this.arrayAdapter.getFilter().filter(a);
                            }
                            @Override
                            public void beforeTextChanged(CharSequence a, int b, int c, int d){

                            }

                            @Override
                            public void afterTextChanged(Editable e){

                            }
        });

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

    private void UpdateColumn(int pkdx_id){
        //TODO: Place all necessary stuff to update our side column
        //Do Raw DB Get and look at Name, Description and (TBD) Type
        Cursor current = database.rawQuery(String.format("SELECT %s FROM pokemon WHERE pkdx_id=%s", "name,description",String.valueOf(pkdx_id)), null);
        current.moveToFirst();
        String temp = new String();
        temp+=String.format("%d. %s \n",
                pkdx_id,
                current.getString(current.getColumnIndexOrThrow("name")));
        temp+=String.format("\n Description: %s",
                current.getString(current.getColumnIndexOrThrow("description")));

        //pokemonDescriber.setText(current.getString(current.getColumnIndexOrThrow("description")));

        //Change the TextField
        pokemonDescriber.setText(temp);

        //Change the Picture

        //Call the Column Object and tell it to update all of this stuff, NOW
    }

    public void switchToDetail(View view){
        Intent intent = new Intent(this,DetailedPokemonActivity.class);
        //view.get
        intent.putExtra("pkdxid", pokemonInView);
        startActivity(intent);
    }

}
