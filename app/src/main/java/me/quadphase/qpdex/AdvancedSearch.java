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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.pokemon.Ability;
import me.quadphase.qpdex.pokemon.EggGroup;
import me.quadphase.qpdex.pokemon.MinimalPokemon;
import me.quadphase.qpdex.pokemon.Pokemon;
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
 * TODO: All 6 Base stats
 * TODO: The ability to include/exclude Primal/Mega/Alternate Forms
 *
 */


public class AdvancedSearch extends ActionBarActivity {


    // pokemonFactory to access the various DB access functions
    PokemonFactory pokemonFactory;

    // Pokemon information to filter
    MinimalPokemon[] allMinimalPokemon;

    //Final Filtered List
    MinimalPokemon [] filteredMinimalPokemon;
    ArrayList<Integer> filteredNationalIds;
    ArrayList<Integer> filteredUniqueIds;
    //Contains either or
    ArrayList<Integer> currentFilterPokemonArrayList;

    // Arrays to fill Spinners
    Type[] allTypes;
    Ability[] allAbilities;
    EggGroup[] allEggGroups;
    String allGenerations [];

    // User Selections
    Type selectedType1;
    Type selectedType2;
    EggGroup selectedEggGroup1;
    EggGroup selectedEggGroup2;
    Ability selectedAbility;
    int selectedGeneration;


    // UI elements
    Spinner type1Spinner;
    Spinner type2Spinner;
    Spinner abilitySpinner;
    Spinner eggGroup1Spinner;
    Spinner eggGroup2Spinner;
    Spinner generationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        pokemonFactory = PokemonFactory.getPokemonFactory(this.getApplicationContext());

        allTypes = pokemonFactory.getAllTypes();
        allAbilities = pokemonFactory.getAllAbilities();
        allEggGroups = pokemonFactory.getAllEggGroups();
        allGenerations = pokemonFactory.getGeneratons();

        // No need for MinimalPokemon
        //allMinimalPokemon = pokemonFactory.getAllMinimalPokemon();

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
        abilitySpinner = (Spinner) findViewById(R.id.ability_spinner);
        eggGroup1Spinner = (Spinner) findViewById(R.id.egg1_spinner);
        eggGroup2Spinner = (Spinner) findViewById(R.id.egg2_spinner);
        generationSpinner = (Spinner) findViewById(R.id.generation_spinner);

    }

    private void fillInSpinnerValues(){

        //TODO: Filling out these Spinners with the actual objects seems to be causing a bottleneck,
        // Might want to just store strings to optimize
        ArrayAdapter<Type> typeArrayAdapter = new ArrayAdapter<Type>(this, android.R.layout.simple_spinner_item, allTypes);
        typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<Ability> abilityArrayAdapter = new ArrayAdapter<Ability>(this, android.R.layout.simple_spinner_item, allAbilities);
        abilityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<EggGroup> eggGroupArrayAdapter = new ArrayAdapter<EggGroup>(this, android.R.layout.simple_spinner_item, allEggGroups);
        eggGroupArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> generationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allGenerations);
        generationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        type1Spinner.setAdapter(typeArrayAdapter);
        type2Spinner.setAdapter(typeArrayAdapter);
        abilitySpinner.setAdapter(abilityArrayAdapter);
        eggGroup1Spinner.setAdapter(eggGroupArrayAdapter);
        eggGroup2Spinner.setAdapter(eggGroupArrayAdapter);
        generationSpinner.setAdapter(generationArrayAdapter);

        type1Spinner.setOnItemSelectedListener(new DropDownSelectSpinner());
        type2Spinner.setOnItemSelectedListener(new DropDownSelectSpinner());
        abilitySpinner.setOnItemSelectedListener(new DropDownSelectSpinner());
        eggGroup1Spinner.setOnItemSelectedListener(new DropDownSelectSpinner());
        eggGroup2Spinner.setOnItemSelectedListener(new DropDownSelectSpinner());
        generationSpinner.setOnItemSelectedListener(new DropDownSelectSpinner());
    }

    private void resetFilter(){
        // Provide a set of solutions if no options are selected
        filteredNationalIds = new ArrayList<Integer>();
        filteredUniqueIds = new ArrayList<Integer>();
        currentFilterPokemonArrayList = new ArrayList<Integer>();

        for(int i =1; i< pokemonFactory.getMaxUniqueID()+1; i++ ){
            filteredUniqueIds.add(i);
            currentFilterPokemonArrayList.add(i);
        }
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

    private class DropDownSelectSpinner implements AdapterView.OnItemSelectedListener {

        /**
         * Saves the chosen selections from the dropdown spinners
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

            // Determine other type, and set current type
            Type otherType = new Type("Temp", -1);
            if (parent.getId() == type1Spinner.getId()){
                Type selectedType = (Type) parent.getItemAtPosition(pos);
                Log.d("QPDex", "Spinner touched");
                Log.d("QPDEX", String.format("%s - %s chosen", parent.toString(), selectedType));
                selectedType1 = selectedType;
                otherType = selectedType2;
            }
            else if (parent.getId() == type2Spinner.getId()){
                Type selectedType = (Type) parent.getItemAtPosition(pos);
                Log.d("QPDex", "Spinner touched");
                Log.d("QPDEX", String.format("%s - %s chosen", parent.toString(), selectedType));
                selectedType2 = selectedType;
                otherType = selectedType1;
            }
            else if(parent.getId() == abilitySpinner.getId()){

                selectedAbility = allAbilities[pos];
            }
            else if(parent.getId() == eggGroup1Spinner.getId()){

                selectedEggGroup1 = allEggGroups[pos];
            }
            else if(parent.getId() == eggGroup2Spinner.getId()){

                selectedEggGroup2 = allEggGroups[pos];
            }
            else if(parent.getId() == generationSpinner.getId()){
                // Don't actual Need the string value, pos is enough
                selectedGeneration = pos;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    public void switchToPokedex(View view){
        long startTime = System.nanoTime();
        Intent intent = new Intent(this,PokedexActivity.class);

        //Comment this out, takes way tooo long 4+sec
        //resetFilter();

        ArrayList<Integer> tempFilterPokemonArrayList = new ArrayList<>();

        // Filtering algorithm, will go through a list of all uniqueIDs and apply filters
        // consecutively. Once the list is filtered, convert the unique to nationalIds

        //Start with the ability chosen, if any. Use the ability ID to get all NationalID's associated
        if(selectedAbility.getName() != "None"){

            currentFilterPokemonArrayList = pokemonFactory.getAllUniqueIDsFromAbility(Arrays.asList(allAbilities).indexOf(selectedAbility));
        }


        // Filter the remaining IDs by type twice

        if(selectedType1.getTypeID() != 0){

            tempFilterPokemonArrayList = pokemonFactory.getAllUniqueIDsFromType(Arrays.asList(allTypes).indexOf(selectedType1));
            currentFilterPokemonArrayList.retainAll(tempFilterPokemonArrayList);
            tempFilterPokemonArrayList.clear();
        }



        if(selectedType2.getTypeID() != 0){

            tempFilterPokemonArrayList = pokemonFactory.getAllUniqueIDsFromType(Arrays.asList(allTypes).indexOf(selectedType2));
            currentFilterPokemonArrayList.retainAll(tempFilterPokemonArrayList);
            tempFilterPokemonArrayList.clear();
        }



        // When Done filtering by UniqueID's convert uniqueIDs to NationalId's
        filteredNationalIds = pokemonFactory.convertUniqueToNational(currentFilterPokemonArrayList);

        // Filter national IDs based on Egg Groups
        if(selectedEggGroup1.getName() != "None"){

            tempFilterPokemonArrayList = pokemonFactory.getAllNationalIdsFromEggGroup(Arrays.asList(allEggGroups).indexOf(selectedEggGroup1));
            filteredNationalIds.retainAll(tempFilterPokemonArrayList);
            tempFilterPokemonArrayList.clear();
        }

        if(selectedEggGroup2.getName() != "None"){

            tempFilterPokemonArrayList = pokemonFactory.getAllNationalIdsFromEggGroup(Arrays.asList(allEggGroups).indexOf(selectedEggGroup2));
            filteredNationalIds.retainAll(tempFilterPokemonArrayList);
            tempFilterPokemonArrayList.clear();
        }


        if(selectedGeneration != 0){

            tempFilterPokemonArrayList = pokemonFactory.getAllNationalIdsFromGenerationFirstAppeared(selectedGeneration);
            filteredNationalIds.retainAll(tempFilterPokemonArrayList);
            tempFilterPokemonArrayList.clear();
        }


        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        Log.d("QPDex", String.format("Total Filtering took %s ns",duration));


        //TODO: Investigate parceling the entire object?
        intent.putExtra("FILTERED_POKEMON_NATIONALID", filteredNationalIds);
        startActivity(intent);
    }


}
