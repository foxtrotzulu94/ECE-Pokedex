package me.quadphase.qpdex;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import me.quadphase.qpdex.pokemon.Pokemon;


public class PokedexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        Runtime.getRuntime().gc();
        System.gc();

        //Set up Buttons
        Button cryButton = (Button)findViewById(R.id.button_pkmncry);
        if(!cryButton.hasOnClickListeners()) {
            cryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPokemonCry();
            }
        });
        }

        //Set up Search Bar (EditText)
        final EditText nameAndSearch = (EditText)findViewById(R.id.edittext_pkmnname);
        if(!nameAndSearch.hasOnClickListeners()){
            //Set the OnClick Listener
            nameAndSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Clear the Pokemon Name immediately.
                    nameAndSearch.setText("");
                }
            });

            //Piggyback and also set the OnFocusChange Method
            nameAndSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        //Take the keyboard away
                        //TODO: Has a lot of refinement to be done!
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }


        //Fill the list
        ListView pokedexList = (ListView) findViewById(R.id.listv_pkdexentries);
        ArrayAdapter<String> pokedexEntries = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                });
        pokedexList.setAdapter(pokedexEntries);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pokedex, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
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

    public void switchToPokemonData(View view){
        //TODO: Add info on the specific pokemon being viewed
        Intent intent = new Intent(this,DetailedPokemonActivity.class);
        startActivity(intent);
    }

    public void playPokemonCry(){
        Log.w("QPDEX","Playing Sound");
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.c249);
        mediaPlayer.start();
//        mediaPlayer.release();
    }
}
