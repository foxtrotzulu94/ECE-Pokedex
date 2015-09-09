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
        long startTime = System.nanoTime();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        pokemonFactory = PokemonFactory.getPokemonFactory(this.getApplicationContext());

        allTypes = pokemonFactory.getAllTypes();
        allAbilities = pokemonFactory.getAllAbilities();
        allEggGroups = pokemonFactory.getAllEggGroups();
        allGenerations = pokemonFactory.getGeneratons();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        Log.d("QPDex", String.format("Obtaining Spinner Data took %s ns",duration));

        // No need for MinimalPokemon
        //allMinimalPokemon = pokemonFactory.getAllMinimalPokemon();

        //setup and fill in all spinner contents
        retrieveInterfaceElements();
        fillInSpinnerValues();

        // Provide a set of solutions if no options are selected
        resetFilter();

        endTime = System.nanoTime();
        duration = (endTime - startTime);

        Log.d("QPDex", String.format("Total creation took %s ns",duration));

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
        hpGreaterTextUI = (EditText) findViewById(R.id.hp_greater);
        attLowerTextUI = (EditText) findViewById(R.id.att_lower);
        attGreaterTextUI = (EditText) findViewById(R.id.att_greater);
        defLowerTextUI = (EditText) findViewById(R.id.def_lower);
        defGreaterTextUI = (EditText) findViewById(R.id.def_greater);
        spattLowerTextUI = (EditText) findViewById(R.id.spatt_lower);
        spattGreaterTextUI = (EditText) findViewById(R.id.spatt_greater);
        spdefLowerTextUI = (EditText) findViewById(R.id.spdef_lower);
        spdefGreaterTextUI = (EditText) findViewById(R.id.spdef_greater);
        speedLowerTextUI = (EditText) findViewById(R.id.speed_lower);
        speedGreaterTextUI = (EditText) findViewById(R.id.speed_greater);


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

    /**
     * Function to collect the user input of stats
     */
    public void collectStats() {

        final int max_stat = 999;
        final int min_stat = 0;

        Log.d("QPDex", "Stats being collected");



        if (!hpLowerTextUI.getText().toString().equals("")) {
            selectedHpLower = Integer.parseInt(hpLowerTextUI.getText().toString());
        }else{
            selectedHpLower = min_stat;
        }

        if (!hpGreaterTextUI.getText().toString().equals("")){
            selectedHpGreater = Integer.parseInt(hpGreaterTextUI.getText().toString());
        }else {
            selectedHpGreater = max_stat;
        }

        if (!attLowerTextUI.getText().toString().equals("")){
            selectedAttLower = Integer.parseInt(attLowerTextUI.getText().toString());
        }else{
            selectedAttLower = min_stat;
        }

        if (!attGreaterTextUI.getText().toString().equals("")){
            selectedAttGreater = Integer.parseInt(attGreaterTextUI.getText().toString());
        }else {
            selectedAttGreater = max_stat;
        }

        if (!defLowerTextUI.getText().toString().equals("")){
            selectedDefLower = Integer.parseInt(defLowerTextUI.getText().toString());
        }else{
            selectedDefLower = min_stat;
        }

        if (!defGreaterTextUI.getText().toString().equals("")){
            selectedDefGreater = Integer.parseInt(defGreaterTextUI.getText().toString());
        }else {
            selectedDefGreater = max_stat;
        }

        if (!spattLowerTextUI.getText().toString().equals("")){
            selectedSpattLower = Integer.parseInt(spattLowerTextUI.getText().toString());
        }else{
            selectedSpattLower = min_stat;
        }

        if (!spattGreaterTextUI.getText().toString().equals("")){
            selectedSpattGreater = Integer.parseInt(spattGreaterTextUI.getText().toString());
        }
        else {
            selectedSpattGreater = max_stat;
        }

        if (!spdefLowerTextUI.getText().toString().equals("")){
            selectedSpdefLower = Integer.parseInt(spdefLowerTextUI.getText().toString());
        }else{
            selectedSpdefLower = min_stat;
        }

        if (!spdefGreaterTextUI.getText().toString().equals("")){
            selectedSpdefGreater = Integer.parseInt(spdefGreaterTextUI.getText().toString());
        }else {
            selectedSpdefGreater = max_stat;
        }

        if (!speedLowerTextUI.getText().toString().equals("")){
            selectedSpeedLower = Integer.parseInt(speedLowerTextUI.getText().toString());
        }else{
            selectedSpeedLower = min_stat;
        }

        if (!speedGreaterTextUI.getText().toString().equals("")){
            selectedSpeedGreater = Integer.parseInt(speedGreaterTextUI.getText().toString());
        }else {
            selectedSpeedGreater = max_stat;
        }

        Log.d("QPDEX", String.format("Stats chosen: %s/%s/%s/%s/%s/%s - %s/%s/%s/%s/%s/%s ",
                String.valueOf(selectedHpLower),String.valueOf(selectedAttLower),String.valueOf(selectedDefLower),
                String.valueOf(selectedSpattLower),String.valueOf(selectedSpdefLower),String.valueOf(selectedSpeedLower),
                String.valueOf(selectedHpGreater),String.valueOf(selectedAttGreater),String.valueOf(selectedDefGreater),
                String.valueOf(selectedSpattGreater),String.valueOf(selectedSpdefGreater),String.valueOf(selectedSpeedGreater)
                ));

    }

    /**
     * Function to filter for the stats of pokemon
     *
     * @param lowerLimit of the stat
     * @param upperLimit of the stat
     * @param statName either HP, DEFENCE, ATTACK, SPATTACK, SPDEFENCE or SPEED
     */
    private void filterStats(int lowerLimit, int upperLimit, String statName){

        // Filter only if one of the values is not a default value
        if(lowerLimit > 0 || upperLimit < 999){
            ArrayList<Integer> tempFilterPokemonArrayList = new ArrayList<Integer>();
            tempFilterPokemonArrayList = pokemonFactory.getAllUniqueIDsFromStat(lowerLimit, upperLimit, statName);
            currentFilterPokemonArrayList.retainAll(tempFilterPokemonArrayList);
            tempFilterPokemonArrayList.clear();
        }

    }






    public void switchToPokedex(View view){
        long startTime = System.nanoTime();
        Intent intent = new Intent(this,PokedexActivity.class);

        // Obtain all stats chosen
        collectStats();

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

        // If at least one is not default value, filter
        filterStats(selectedHpLower, selectedHpGreater, "HP");
        filterStats(selectedAttLower, selectedAttGreater, "ATTACK");
        filterStats(selectedDefLower, selectedDefGreater, "DEFENCE");
        filterStats(selectedSpattLower, selectedSpattGreater, "SPATTACK");
        filterStats(selectedSpdefLower, selectedSpdefGreater, "SPDEFENCE");
        filterStats(selectedSpeedLower, selectedSpeedGreater, "SPEED");
        //TODO: Filter by sum
        //filterStats(selectedSpeedLower, selectedSpeedGreater, "SUM");

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
