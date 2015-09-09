package me.quadphase.qpdex;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Spinner;
import android.content.Intent;
import android.view.KeyEvent;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.pokemon.Ability;
import me.quadphase.qpdex.pokemon.EggGroup;
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
    int selectedHpLower;
    int selectedHpGreater;
    int selectedAttLower;
    int selectedAttGreater;
    int selectedDefGreater;
    int selectedDefLower;
    int selectedSpattLower;
    int selectedSpattGreater;
    int selectedSpdefLower;
    int selectedSpdefGreater;
    int selectedSpeedLower;
    int selectedSpeedGreater;


    // UI elements
    Spinner type1Spinner;
    Spinner type2Spinner;
    Spinner abilitySpinner;
    Spinner eggGroup1Spinner;
    Spinner eggGroup2Spinner;
    Spinner generationSpinner;

    EditText hpLowerTextUI;
    EditText hpGreaterTextUI;
    EditText attLowerTextUI;
    EditText attGreaterTextUI;
    EditText defLowerTextUI;
    EditText defGreaterTextUI;
    EditText spattLowerTextUI;
    EditText spattGreaterTextUI;
    EditText spdefLowerTextUI;
    EditText spdefGreaterTextUI;
    EditText speedLowerTextUI;
    EditText speedGreaterTextUI;

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

        hpLowerTextUI = (EditText) findViewById(R.id.hp_lower);
        hpLowerTextUI.setOnFocusChangeListener(new StatsChoice());

        hpGreaterTextUI = (EditText) findViewById(R.id.hp_greater);
        hpGreaterTextUI.setOnFocusChangeListener(new StatsChoice());

        attLowerTextUI = (EditText) findViewById(R.id.att_lower);
        attLowerTextUI.setOnFocusChangeListener(new StatsChoice());

        attGreaterTextUI = (EditText) findViewById(R.id.att_greater);
        attGreaterTextUI.setOnFocusChangeListener(new StatsChoice());

        defLowerTextUI = (EditText) findViewById(R.id.def_lower);
        defLowerTextUI.setOnFocusChangeListener(new StatsChoice());

        defGreaterTextUI = (EditText) findViewById(R.id.def_greater);
        defGreaterTextUI.setOnFocusChangeListener(new StatsChoice());

        spattLowerTextUI = (EditText) findViewById(R.id.spatt_lower);
        spattLowerTextUI.setOnFocusChangeListener(new StatsChoice());

        spattGreaterTextUI = (EditText) findViewById(R.id.spatt_greater);
        spattGreaterTextUI.setOnFocusChangeListener(new StatsChoice());

        spdefLowerTextUI = (EditText) findViewById(R.id.spdef_lower);
        spdefLowerTextUI.setOnFocusChangeListener(new StatsChoice());

        spdefGreaterTextUI = (EditText) findViewById(R.id.spdef_greater);
        spdefGreaterTextUI.setOnFocusChangeListener(new StatsChoice());

        speedLowerTextUI = (EditText) findViewById(R.id.speed_lower);
        speedLowerTextUI.setOnFocusChangeListener(new StatsChoice());

        speedGreaterTextUI = (EditText) findViewById(R.id.speed_greater);
        spdefGreaterTextUI.setOnFocusChangeListener(new StatsChoice());


    }

    private void fillInSpinnerValues(){

        //TODO: Filling out these Spinners with the actual objects seems to be causing a bottleneck,
        // Might want to just store strings to optimize
        long startTime = System.nanoTime();
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

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        Log.d("QPDex", String.format("Total filling spinners took %s ns",duration));
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

        for (int i=1; i <pokemonFactory.getMaxNationalID()+1; i++){
            filteredNationalIds.add(i);
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

    private class StatsChoice extends Activity implements TextView.OnFocusChangeListener {


        @Override
        public void onFocusChange(TextView view, boolean focus) {

            if (!view.getText().toString().equals("") && !focus) {
                int selectedStat = Integer.parseInt(view.getText().toString());
                int selectedUiId = view.getId();

                Log.d("QPDex", "Stats touched");

                Log.d("QPDEX", String.format("%s entered", String.valueOf(selectedStat)));

                if (selectedUiId == hpLowerTextUI.getId()) {
                    selectedHpLower = selectedStat;
                }
                else if (selectedUiId == hpGreaterTextUI.getId()){
                    selectedHpGreater = selectedStat;
                }
                else if (selectedUiId == attLowerTextUI.getId()){
                    selectedAttLower = selectedStat;
                }
                else if (selectedUiId == attGreaterTextUI.getId()){
                    selectedAttGreater = selectedStat;
                }
                else if (selectedUiId == defLowerTextUI.getId()){
                    selectedDefLower = selectedStat;
                }
                else if (selectedUiId == defGreaterTextUI.getId()){
                    selectedDefGreater = selectedStat;
                }
                else if (selectedUiId == spattLowerTextUI.getId()){
                    selectedSpattLower = selectedStat;
                }
                else if (selectedUiId == spattGreaterTextUI.getId()){
                    selectedSpattGreater = selectedStat;
                }
                else if (selectedUiId == spdefLowerTextUI.getId()){
                    selectedSpdefLower = selectedStat;
                }
                else if (selectedUiId == spdefGreaterTextUI.getId()){
                    selectedSpdefGreater = selectedStat;
                }
                else if (selectedUiId == speedLowerTextUI.getId()){
                    selectedSpeedLower = selectedStat;
                }
                else if (selectedUiId == speedGreaterTextUI.getId()){
                    selectedSpeedGreater = selectedStat;
                }
            }


            }






    }

    public void switchToPokedex(View view){
        long startTime = System.nanoTime();
        Intent intent = new Intent(this,PokedexActivity.class);

        //Comment this out, takes way tooo long 4+sec
        currentFilterPokemonArrayList.clear();
        currentFilterPokemonArrayList.addAll(filteredUniqueIds);

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
