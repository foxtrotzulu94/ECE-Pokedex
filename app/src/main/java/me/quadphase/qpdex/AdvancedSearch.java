package me.quadphase.qpdex;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.pokemon.MinimalPokemon;
import me.quadphase.qpdex.pokemon.Type;


/**
 * Created by Thinesh on 31-Aug-15
 *
 * Class deals with the advanced search feature accessed from main menu
 *
 * Can filter pokemon by
 *
 * Type 1
 * Type 2
 * Ability
 * Generation Available
 * Egg Groups 1
 * Egg Groups 2
 * All 6 Base stats
 * The ability to include/exclude Primal/Mega/Alternate Forms

 */


public class AdvancedSearch extends ActionBarActivity {

    // Pokemon information to filter
    MinimalPokemon[] allMinimalPokemon;

    //Final Filtered List
    MinimalPokemon [] filteredMinimalPokemon;
    ArrayList<Integer> filteredNationalIds;

    // Arrays to fill Spinners
    Type[] allTypes;

    // User Selections
    Type selectedType1;
    Type selectedType2;

    // UI elements
    Spinner type1Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        // pokemonFactpry to access the various DB access functions

        final PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(this.getApplicationContext());

        allTypes = pokemonFactory.getAllTypes();
        allMinimalPokemon = pokemonFactory.getAllMinimalPokemon();

        //todo move to it's own function where all UI elements are filled
        type1Spinner = (Spinner) findViewById(R.id.type1_spinner);

        ArrayAdapter<Type> typeArrayAdapter = new ArrayAdapter<Type>(this, android.R.layout.simple_spinner_item, allTypes);
        typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        type1Spinner.setAdapter(typeArrayAdapter);

        type1Spinner.setOnItemSelectedListener(new TypeSelectSpinner());




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advanced_search, menu);
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

    private class TypeSelectSpinner implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)

            //TODO: Make sure filter is reset
            filteredNationalIds = new ArrayList<Integer>();
            if(filteredNationalIds != null){
                filteredNationalIds.clear();
            }
            selectedType1 = (Type) parent.getItemAtPosition(pos);
            Log.d("QPDex", "Spinner touched");
            Log.d("QPDEX", String.format("%s chosen", selectedType1));
            ArrayList<MinimalPokemon> tempFilterList = new ArrayList<MinimalPokemon>();


            //TODO: Need to make sure minimalpokemon all loaded up before filtering
            //TODO: Investigate parceling the entire object?

            if (selectedType1.getTypeID() != 0) {
                for (MinimalPokemon p : allMinimalPokemon) {
                    if (p.getTypes().contains(selectedType1)) {
                        tempFilterList.add(p);
                        filteredNationalIds.add(p.getPokemonNationalID());
                    }
                }

                filteredMinimalPokemon = new MinimalPokemon[tempFilterList.size()];
                tempFilterList.toArray(filteredMinimalPokemon);
            }




        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    public void switchToPokedex(View view){
        Intent intent = new Intent(this,PokedexActivity.class);
        intent.putExtra("FILTERED_POKEMON_NATIONALID", filteredNationalIds);
        startActivity(intent);
    }


}
