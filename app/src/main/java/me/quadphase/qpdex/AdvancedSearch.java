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
import me.quadphase.qpdex.pokemon.Ability;
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
 *
 * TODO: Move all the final logic to the start intent function?

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
    Ability selectedAbility;


    // UI elements
    Spinner type1Spinner;
    Spinner type2Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        // pokemonFactpry to access the various DB access functions

        final PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(this.getApplicationContext());


        allTypes = pokemonFactory.getAllTypes();
        allMinimalPokemon = pokemonFactory.getAllMinimalPokemon();

        //setup and fill in all spinner contents
        retrieveInterfaceElements();
        fillInSpinnerValues();

        // Provide a set of solutions if no options are selected
        resetFilter();

    }

    private void retrieveInterfaceElements(){
        //Retrieve all UI Variables for setup
        type1Spinner = (Spinner) findViewById(R.id.type1_spinner);
        type2Spinner = (Spinner) findViewById(R.id.type2_spinner);



    }

    private void fillInSpinnerValues(){
        ArrayAdapter<Type> typeArrayAdapter = new ArrayAdapter<Type>(this, android.R.layout.simple_spinner_item, allTypes);
        typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        type1Spinner.setAdapter(typeArrayAdapter);
        type2Spinner.setAdapter(typeArrayAdapter);

        type1Spinner.setOnItemSelectedListener(new TypeSelectSpinner());
        type2Spinner.setOnItemSelectedListener(new TypeSelectSpinner());

    }

    private void resetFilter(){
        // Provide a set of solutions if no options are selected
        filteredNationalIds = new ArrayList<Integer>();
        for(int i=0; i< allMinimalPokemon.length; i++){
            filteredNationalIds.add(allMinimalPokemon[i].getPokemonNationalID());
        }
        filteredMinimalPokemon = allMinimalPokemon;
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

        /**
         * Takes care of finding pokemon with the type combination the user requests
         * To do this, when a type is selected, create a filtered list
         * Then Check if a second type is selected and filter from the previously filtered list
         *
         * Save the Types selected, this way we can compare and not have to refilter if both don't change
         * Type 0/None/Bird acts like a wildcard Type
         *
         * @param parent either the type1 or type spinner
         * @param view
         * @param pos corresponds to the typeID position
         * @param id corresponds to the id of the position
         */
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)

            Type selectedType = (Type) parent.getItemAtPosition(pos);
            Log.d("QPDex", "Spinner touched");
            Log.d("QPDEX", String.format("%s - %s chosen", parent.toString(), selectedType));

            // Determine other type, and set current type
            Type otherType;
            if (parent.getId() == type1Spinner.getId()){
                selectedType1 = selectedType;
                otherType = selectedType2;
            }
            else{
                selectedType2 = selectedType;
                otherType = selectedType1;
            }

            if(selectedType.getTypeID() == 0 && otherType !=null && otherType.getTypeID() == 0){
                resetFilter();
            }

            ArrayList<MinimalPokemon> tempFilterList = new ArrayList<MinimalPokemon>();

            // Create arrayList of pokemon and nationalIDs of the type that has just been modified
            // unless the Type is none, in that case all pokemon are valid
            if (selectedType.getTypeID() != 0) {
                // First clear filteredNatID list
                filteredNationalIds.clear();
                for (MinimalPokemon p : allMinimalPokemon) {
                    if (p.getTypes().contains(selectedType)) {
                    tempFilterList.add(p);
                    filteredNationalIds.add(p.getPokemonNationalID());
                    }
                }

               filteredMinimalPokemon = new MinimalPokemon[tempFilterList.size()];
               tempFilterList.toArray(filteredMinimalPokemon);
            }



            // After this step we have a list of pokemon that are of the type that was just modified



            // Now we need to make sure that the other spinner's type is accounted for
            // If both types selected are the same, just consider 1 type


            // Filter further
            if (otherType != null){
                // IF previously not None, but then the first Type is changed to none
                // Need to have entire selection
                if (selectedType.getTypeID() == 0){
                    filteredMinimalPokemon = allMinimalPokemon;
                }
                if(selectedType1 != selectedType2 && otherType.getTypeID() != 0) {

                   tempFilterList.clear();
                    //We can also clear the exisiting nationalID list
                    filteredNationalIds.clear();

                    for (MinimalPokemon p : filteredMinimalPokemon) {
                        if (p.getTypes().contains(otherType)) {
                            tempFilterList.add(p);
                            filteredNationalIds.add(p.getPokemonNationalID());
                        }
                    }
                    filteredMinimalPokemon = new MinimalPokemon[tempFilterList.size()];
                    tempFilterList.toArray(filteredMinimalPokemon);
                }


            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    public void switchToPokedex(View view){
        Intent intent = new Intent(this,PokedexActivity.class);
        //TODO: Investigate parceling the entire object?
        intent.putExtra("FILTERED_POKEMON_NATIONALID", filteredNationalIds);
        startActivity(intent);
    }


}
