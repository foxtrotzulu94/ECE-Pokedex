package me.quadphase.qpdex.databaseAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import me.quadphase.qpdex.exceptions.PartyFullException;
import me.quadphase.qpdex.pokedex.PokedexManager;
import me.quadphase.qpdex.pokemon.Ability;
import me.quadphase.qpdex.pokemon.EggGroup;
import me.quadphase.qpdex.pokemon.Evolution;
import me.quadphase.qpdex.pokemon.Game;
import me.quadphase.qpdex.pokemon.Location;
import me.quadphase.qpdex.pokemon.MinimalPokemon;
import me.quadphase.qpdex.pokemon.Move;
import me.quadphase.qpdex.pokemon.MoveSet;
import me.quadphase.qpdex.pokemon.Party;
import me.quadphase.qpdex.pokemon.PartyPokemon;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * Created by Nicole on 28-Jul-15.
 *
 * Class used to retrieve pokemon and their information from the database.
 */
public class PokemonFactory {

    //TODO: Fix and retest!
    private class DetailedListBuilder extends Thread{
        int startIndex;
        int endIndex;

        DetailedListBuilder(int startingPoint,int endPoint){
            startIndex = startingPoint;
            endIndex = endPoint;
        }

        @Override
        public void run(){
            buildPartOfDetailedPokemonList(startIndex,endIndex-startIndex);
        }

    }

    //TODO: Fix and retest!
    private class DetailedListMaster extends Thread{

        //Best arbitrary number I could choose...
        int coreMultiplier = 2;

        List<Thread> workerList;

        @Override
        public void run(){
            if (allDetailedPokemon==null && detailedPokemonShortList==null) {
                workerList = new LinkedList<>();

                //Create new short list (matches minimalPokemon with a corresponding Pokemon)
                detailedPokemonShortList = new Pokemon[getMaxNationalID()+1];

                //Create a new Large Pokemon List
                allDetailedPokemon = new Pokemon[getMaxUniqueID()+1];

                //Set the fail-safe
                allDetailedPokemon[0] = PokedexManager.getInstance().missingNo;
                detailedPokemonShortList[0] = allDetailedPokemon[0];

                int optimalWorkerNumber = Runtime.getRuntime().availableProcessors()*coreMultiplier;
                int subdivisionSize = getMaxNationalID() / optimalWorkerNumber;
                int remainder = getMaxNationalID() % optimalWorkerNumber;
                int currentIndex=1;

                Log.d("QPDEX_PkmnBuilder",String.format("Optimal Workers: %s",optimalWorkerNumber));

                for (int i = 1; i <= optimalWorkerNumber; i++) {
                    Thread worker;
//                    Thread worker1;
//                    Thread worker2;

                    //Build the list from the opposite ends
                    // We do this because the user is VERY likely to touch either the first couple
                    // of entries OR the last couple, rather than anything smack in the middle

//                    if(i==1){
//                        worker1 = new DetailedListBuilder(currentIndex, subdivisionSize * i);
//                        worker2 = new DetailedListBuilder(subdivisionSize*(optimalWorkerNumber-1), (subdivisionSize*optimalWorkerNumber)+remainder);
//                    }
//                    else{
//                        worker1 = new DetailedListBuilder(currentIndex, subdivisionSize * i);
//                        worker2 = new DetailedListBuilder(subdivisionSize*(optimalWorkerNumber-i), subdivisionSize * (optimalWorkerNumber-i));
//                    }
//

                    if(i<optimalWorkerNumber) {
                        worker = new DetailedListBuilder(currentIndex, subdivisionSize * i);
                    }
                    else {
                        worker = new DetailedListBuilder(currentIndex, (subdivisionSize*i)+remainder);
                    }

                    Log.d("QPDEX",String.format("Current index built %s",currentIndex));

                    currentIndex = currentIndex+subdivisionSize-1;
                    worker.start();
                    workerList.add(worker);

//                    worker1.start();
//                    worker2.start();
//                    workerList.add(worker1);
//                    workerList.add(worker2);

                }

                try {
                    for (int i = 0; i < workerList.size(); i++) {
                        workerList.get(i).join();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private final boolean PRINT_DEBUG = false;

    private static final String DB_NAME = "pokedex.db";

    // database constant strings:
    private final String ABILITIES_TABLE = "abilities";
    private final String CATEGORY_TABLE = "category";
    private final String EGG_GROUPS_TABLE = "eggGroups";
    private final String GAMES_TABLE = "games";
    private final String LOCATIONS_TABLE = "locations";
    private final String MOVES_TABLE = "moves";
    private final String TYPES_TABLE = "types";
    private final String PARTY_TABLE = "party";
    private final String PARTY_MOVESET_TABLE = "party_moveSet";
    private final String POKEMON_COMMON_INFO_TABLE = "pokemon_common_info";
    private final String POKEMON_UNIQUE_INFO_TABLE = "pokemon_unique_info";
    private final String POKEMON_ABILITIES_TABLE = "pokemon_abilities";
    private final String POKEMON_CAUGHT_TABLE = "pokemon_caught";
    private final String POKEMON_EGG_GROUPS_TABLE = "pokemon_eggGroups";
    private final String POKEMON_EVOLUTIONS_TABLE = "pokemon_evolutions";
    private final String POKEMON_LOCATIONS_TABLE = "pokemon_locations";
    private final String POKEMON_NATIONAL_ID_TO_UNIQUE_ID_TABLE = "pokemon_nationalID";
    private final String POKEMON_SUFFIX_TABLE = "pokemon_suffix";
    private final String POKEMON_TYPES_TABLE = "pokemon_types";
    private final String TYPE_EFFECTIVENESS_TABLE = "types_effectiveness";
    private final String POKEMON_MOVES_TABLE = "pokemon_moves";

    private final String NAME = "name";
    private final String DESCRIPTION = "description";
    private final String POWER = "power";
    private final String ACCURACY = "accuracy";
    private final String PP = "pp";
    private final String AFFECTS = "affects";
    private final String GENERATION_FIRST_APPEARED = "genFirstAppeared";
    private final String HEIGHT = "height";
    private final String WEIGHT = "weight";
    private final String ATTACK = "attack";
    private final String DEFENCE = "defence";
    private final String HP = "hp";
    private final String SPECIAL_ATTACK = "spattack";
    private final String SPECIAL_DEFENCE = "spdefence";
    private final String SPEED = "speed";
    private final String CATCH_RATE = "catchRate";
    private final String CAUGHT = "caught";
    private final String CONDITION = "condition";
    private final String GENDER_RATIO_MALE = "genderRatioMale";
    private final String HATCH_TIME = "hatchTime";
    private final String LEARN_METHOD = "learnMethod";
    private final String SUFFIX = "suffix";
    private final String EFFECTIVE_LEVEL = "effectiveLevel";

    private final String ABILITIY_ID = "abilityID";
    private final String CATEGORY_ID = "categoryID";
    private final String EGG_GROUP_ID = "eggGroupID";
    private final String GAME_ID = "gameID";
    private final String GENERATION_ID = "generationID";
    private final String LOCATION_ID = "locationID";
    private final String MOVE_ID = "moveID";
    private final String TYPE_ID = "typeID";
    private final String PARTY_ID = "partyID";
    private final String POKEMON_UNIQUE_ID = "pokemonUniqueID";
    private final String POKEMON_NATIONAL_ID = "pokemonNationalID";
    private final String FROM_TYPE_ID = "fromTypeID";
    private final String TO_TYPE_ID = "toTypeID";
    private final String FROM_POKEMON_ID = "fromPokemonID";
    private final String TO_POKEMON_ID = "toPokemonID";

    private MinimalPokemon[] allMinimalPokemon;
    private Pokemon[] detailedPokemonShortList;
    private Pokemon[] allDetailedPokemon;

    private static PokemonFactory instance = null;

    private Type[] types;

    /**
     * SQLite database handle
     */
    private SQLiteDatabase database;

    /**
     * Singleton constructor of the Pokemon Factory
     */
    private PokemonFactory(Context context){
        //Initialize the Database Handler
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(context, DB_NAME);
        database = dbOpenHelper.openDataBase();

        setupLargeCacheLists();

        types = new Type[getMaxTypeID() + 1];

        loadAllTypes();
    }

    private void setupLargeCacheLists(){
        //Create new short list (matches minimalPokemon with a corresponding Pokemon)
        detailedPokemonShortList = new Pokemon[getMaxNationalID()+1];

        //Create a new Large Pokemon List
        allDetailedPokemon = new Pokemon[getMaxUniqueID()+1];

        //Set the fail-safe
        allDetailedPokemon[0] = PokedexManager.getInstance().missingNo;
        detailedPokemonShortList[0] = allDetailedPokemon[0];
    }

    /**
     * Method used to get the singleton {@link PokemonFactory}
     *
     * @param context context of the app
     * @return a PokemonFactory
     */
    public static PokemonFactory getPokemonFactory(Context context) {
        if (instance == null) {
            instance = new PokemonFactory(context);
        }
        return instance;
    }


    /**
     * Retrieves from database a list of all pokemon
     *
     * @return List of all main (no suffix) minimalPokemon in the database
     */
    public MinimalPokemon[] getAllMinimalPokemon() {
        //TODO: Perform aggressive optimizations when possible

        if (allMinimalPokemon==null) {
            allMinimalPokemon = new MinimalPokemon[getMaxNationalID()+1];

            //Secretly added the fail-safe Pokemon...
            allMinimalPokemon[0] = PokedexManager.getInstance().missingNo.minimal();

            for (int i = 1; i <= getMaxNationalID(); i++) {
                allMinimalPokemon[i] = getMinimalPokemonByNationalID(i);
            }
        }

        return allMinimalPokemon;
    }

    public void getAllDetailedPokemon(){
        //TODO: Perform aggressive optimizations when possible

        if (allDetailedPokemon==null || detailedPokemonShortList==null) {
            setupLargeCacheLists();
        }

        for (int i = 1; i <= getMaxNationalID(); i++) {

            //If the entry is null
            if(detailedPokemonShortList[i]==null){
                //Check the mapping to the long list if it's there by any chance
                if(allDetailedPokemon[checkUniqueIDFromNationalID(i)]!=null){
                    detailedPokemonShortList[i] = allDetailedPokemon[checkUniqueIDFromNationalID(i)];
                }
                //Or build it from scratch
                else {
                    detailedPokemonShortList[i] = getPokemonByNationalID(i);
                }
            }

            if(PRINT_DEBUG)
                Log.d("QPDEX",String.format("Building %s",i));
//                //We have to slow down the loop intentionally...
//                try {
//                    Thread.sleep(10);
//                }
//                catch(InterruptedException e){
//
//                }
        }

    }

    //TODO: FIX thread classes before calling again.
    public void getAllDetailedPokemonInParallel(){
        Thread masterBuilder = new DetailedListMaster();
        masterBuilder.start();
        try {
            masterBuilder.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void buildPartOfDetailedPokemonList(int start, int end){
        for (int i = start; i < end; i++) {
            //If the entry is null
            if(detailedPokemonShortList[i]==null){
                //Check the mapping to the long list if it's there by any chance
                if(allDetailedPokemon[checkUniqueIDFromNationalID(i)]!=null){
                    detailedPokemonShortList[i] = allDetailedPokemon[checkUniqueIDFromNationalID(i)];
                }
                //Or build it from scratch
                else {
                    detailedPokemonShortList[i] = getPokemonByNationalID(i);
                }
            }

            Log.d("QPDEX",String.format("Building %s",i));
        }
    }

    /**
     * Creates objects of type {@link MinimalPokemon} based on the national ID of the pokemon
     * @param nationalID National ID of the pokemon
     * @return {@link MinimalPokemon} object
     */
    public MinimalPokemon getMinimalPokemonByNationalID(int nationalID) {
        // find the pokemonID from the nationalID using the pokemon_nationalID mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor cursor = database.query(POKEMON_NATIONAL_ID_TO_UNIQUE_ID_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);

        // HACK: We are assuming that the first pokemon with this nationalID is the original pokemon
        // that we want to fetch, since there are many pokemon with the same nationalID in the case
        // of mega evolutions and various types.
        cursor.moveToFirst();
        int pokemonID = cursor.getInt(cursor.getColumnIndex(POKEMON_UNIQUE_ID));
        if(PRINT_DEBUG)
            Log.v("Database Access", "From nationalID " + String.valueOf(nationalID) + " the pokemonID is: " + String.valueOf(pokemonID));
        // close the cursor
        cursor.close();

        return getMinimalPokemonByPokemonID(pokemonID);
    }

    /**
     * creates a {@link MinimalPokemon} object of the pokemon with the Pokemon ID given
     * @param pokemonID Unique identifier used in the database to store pokemon
     * @return {@link MinimalPokemon} object of the pokemon in question
     */
    public MinimalPokemon getMinimalPokemonByPokemonID(int pokemonID) {
        String[] pokemonIDString = {String.valueOf(pokemonID)};
        Cursor cursor = database.query(POKEMON_UNIQUE_INFO_TABLE, null, POKEMON_UNIQUE_ID + "=?", pokemonIDString, null, null, null, null);
        cursor.moveToFirst();
        // get all of the information stored in pokemon common table:
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        if(PRINT_DEBUG)
            Log.v("Database Access", "From pokemonID " + String.valueOf(pokemonID) + " the name is: " + name);
        cursor.close();

        // get the nationalID:
        String[] selectionArg = {String.valueOf(pokemonID)};
        cursor = database.query(POKEMON_NATIONAL_ID_TO_UNIQUE_ID_TABLE, null, POKEMON_UNIQUE_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();
        int nationalID = cursor.getInt(cursor.getColumnIndex(POKEMON_NATIONAL_ID));
        if(PRINT_DEBUG)
            Log.v("Database Access:", "From pokemonID " + String.valueOf(pokemonID) + " the nationalID is: " + String.valueOf(nationalID));
        cursor.close();

        // get the description from the description table:
        selectionArg[0] = String.valueOf(nationalID);
        cursor = database.query(POKEMON_COMMON_INFO_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();
        String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        if(PRINT_DEBUG)
            Log.v("Database Access:", "From nationalID " + String.valueOf(nationalID) + " the description is: " + description);
        // close the cursor
        cursor.close();

        List<Type> types = getTypes(pokemonID);

        boolean caught = isCaught(nationalID);

        return new MinimalPokemon(nationalID, name, description, types, caught);

    }

    /**
     * Constructs the {@link me.quadphase.qpdex.pokemon.Pokemon} from the information in the database.
     *
     * @param pokemonID - pokemon ID in the database of the pokemon to be brought into memory
     * @return the complete pokemon object including all of its information such as type(s),
     *          move(s) and ability(ies)
     */
    public Pokemon getPokemonByPokemonID(int pokemonID) {
        if (allDetailedPokemon[pokemonID]!=null) {
            return allDetailedPokemon[pokemonID];
        } else {
            // move the cursor to the correct entry of the pokemon table
            Cursor cursor = database.query(POKEMON_UNIQUE_INFO_TABLE, null, POKEMON_UNIQUE_ID +"=?", new String[]{String.valueOf(pokemonID)}, null, null, null, null);
            cursor.moveToFirst();

            // get all of the information stored in pokemon table:
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            double height = cursor.getFloat(cursor.getColumnIndex(HEIGHT));
            double weight = cursor.getFloat(cursor.getColumnIndex(WEIGHT));
            int attack = cursor.getInt(cursor.getColumnIndex(ATTACK));
            int defence = cursor.getInt(cursor.getColumnIndex(DEFENCE));
            int hp = cursor.getInt(cursor.getColumnIndex(HP));
            int spAttack = cursor.getInt(cursor.getColumnIndex(SPECIAL_ATTACK));
            int spDefence = cursor.getInt(cursor.getColumnIndex(SPECIAL_DEFENCE));
            int speed = cursor.getInt(cursor.getColumnIndex(SPEED));

            // get the nationalID:
            String[] selectionArg = {String.valueOf(pokemonID)};
            cursor = database.query(POKEMON_NATIONAL_ID_TO_UNIQUE_ID_TABLE, null, POKEMON_UNIQUE_ID+"=?", selectionArg, null, null, null);
            cursor.moveToFirst();
            int nationalID = cursor.getInt(cursor.getColumnIndex(POKEMON_NATIONAL_ID));

            // get the description:
            selectionArg[0] = String.valueOf(nationalID);
            cursor = database.query(POKEMON_COMMON_INFO_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null, null);
            cursor.moveToFirst();
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            int genFirstAppeared = cursor.getInt(cursor.getColumnIndex(GENERATION_FIRST_APPEARED));
            int hatchTime = cursor.getInt(cursor.getColumnIndex(HATCH_TIME));
            int catchRate = cursor.getInt(cursor.getColumnIndex(CATCH_RATE));
            int genderRatioMale = cursor.getInt(cursor.getColumnIndex(GENDER_RATIO_MALE));

            // close the cursor
            cursor.close();

            boolean caught = isCaught(nationalID);

            // lists to fetch:
            List<Location> locations = null;//getLocations(nationalID);
            List<Ability> abilities = getAbilities(pokemonID);
            List<Move> moves = null;//getMoves(nationalID);
            List<Type> types = getTypes(pokemonID);
            List<EggGroup> eggGroups = getEggGroups(nationalID);
            List<Evolution> evolutions = getEvolutions(pokemonID);


             Pokemon newEntry = new Pokemon(pokemonID, nationalID, name, description, height, weight, attack, defence, hp,
                    spAttack, spDefence, speed, caught, genFirstAppeared, hatchTime, catchRate, genderRatioMale,
                    locations, abilities, moves, types, eggGroups, evolutions);

            allDetailedPokemon[pokemonID] = newEntry;

            return newEntry;
        }
    }

    /**
     * Constructs the main {@link me.quadphase.qpdex.pokemon.Pokemon} from the information in
     * the database.
     *
     * @param nationalID - national ID of the pokemon to be brought into memory
     * @return the complete pokemon object including all of its information such as type(s),
     *          move(s) and ability(ies)
     */
    public Pokemon getPokemonByNationalID(int nationalID) {
        if (!isDetailedNationalIDBuiltAndReady(nationalID)) {
            Pokemon returnObject = getPokemonByPokemonID(checkUniqueIDFromNationalID(nationalID));
            detailedPokemonShortList[nationalID] = returnObject;
            return returnObject;
        } else {
            return detailedPokemonShortList[nationalID];
        }
    }

    public int checkUniqueIDFromNationalID(int nationalID){
        // find the pokemonID from the nationalID using the pokemon_nationalID mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor cursor = database.query(POKEMON_NATIONAL_ID_TO_UNIQUE_ID_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);

        // HACK: We are assuming that the first pokemon with this nationalID is the original pokemon
        // that we want to fetch, since there are many pokemon with the same nationalID in the case
        // of mega evolutions and various types.
        cursor.moveToFirst();
        int retVal = cursor.getInt(cursor.getColumnIndex(POKEMON_UNIQUE_ID));
        cursor.close();
        return retVal;

    }

    /**
     * Goes into the locations table to retrieve the pokemon's locations
     *
     * @param nationalID nationalID in the pokemon table
     * @return the list of locations that the pokemon is found at
     */
    private List<Location> getLocations(int nationalID) {
        List<Location> locations = new LinkedList<>();
        // move the cursor to the pokemon location mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor mappingCursor = database.query(POKEMON_LOCATIONS_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        // Get the cursors for the locations and games tables:
        Cursor locationCursor = database.query(LOCATIONS_TABLE, null, null, null, null, null, null);
        Cursor gameCursor = database.query(GAMES_TABLE, null, null, null, null, null, null);

        while (!mappingCursor.isAfterLast()) {
            selectionArg[0] = String.valueOf(mappingCursor.getInt(mappingCursor.getColumnIndex(LOCATION_ID)));
            locationCursor = database.query(LOCATIONS_TABLE, null, LOCATION_ID + "=?", selectionArg, null, null, null);
            locationCursor.moveToFirst();

            int gameID = locationCursor.getInt(locationCursor.getColumnIndex(GAME_ID));

            selectionArg[0] = String.valueOf(gameID);
            gameCursor = database.query(GAMES_TABLE, null, GAME_ID + "=?", selectionArg, null, null, null);
            gameCursor.moveToFirst();

            Game game = new Game(gameCursor.getString(gameCursor.getColumnIndex(NAME)), gameCursor.getInt(gameCursor.getColumnIndex(GENERATION_ID)));
            Location loc = new Location(locationCursor.getString(locationCursor.getColumnIndex(NAME)), game);

            locations.add(loc);

            // go to the next location for this pokemonID
            mappingCursor.moveToNext();
        }

        // close the cursors
        mappingCursor.close();
        locationCursor.close();
        gameCursor.close();

        return locations;
    }

    /**
     * goes into the abilities table to retrieve a list of the pokemon's abilities
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return a list of the pokemon's abilities
     */
    private List<Ability> getAbilities(int pokemonID) {
        List<Ability> abilities = new LinkedList<>();
        // move the cursor to the pokemon abilities mapping table
        String[] selectionArg = {String.valueOf(pokemonID)};
        Cursor mappingCursor = database.query(POKEMON_ABILITIES_TABLE, null, POKEMON_UNIQUE_ID + "=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        // Get the cursor for the ability table:
        Cursor cursor = database.query(ABILITIES_TABLE, null, null, null, null, null, null);

        while (!mappingCursor.isAfterLast()) {
            selectionArg[0] = String.valueOf(mappingCursor.getInt(mappingCursor.getColumnIndex(ABILITIY_ID)));
            cursor = database.query(ABILITIES_TABLE, null, ABILITIY_ID + "=?", selectionArg, null, null, null);
            cursor.moveToFirst();

            Ability ability = new Ability(cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(DESCRIPTION)));

            abilities.add(ability);

            // go to the next ability for this pokemonID
            mappingCursor.moveToNext();
        }

        // close the cursors
        cursor.close();
        mappingCursor.close();

        return abilities;
    }

    /**
     * Goes into the moves table to retrieve the pokemon's moves
     *
     * @param nationalID pokemonID in the pokemon table (not the nationalID)
     * @return the list of moves that the pokemon is found at
     */
    private List<Move> getMoves(int nationalID) {
        List<Move> moves = new LinkedList<>();
        // move the cursor to the pokemon moves mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor mappingCursor = database.query(POKEMON_MOVES_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        while (!mappingCursor.isAfterLast()) {
            moves.add(getMove(mappingCursor.getInt(mappingCursor.getColumnIndex(MOVE_ID))));

            // go to the next move for this pokemonID
            mappingCursor.moveToNext();
        }

        // close the cursor
        mappingCursor.close();

        return moves;
    }

    /**
     * Gets an object of type Move of the given moveID.
     *
     * @param moveID unique id of the move in the database
     * @return {@link Move} object
     */
    private Move getMove(int moveID) {
        // Get the cursor for the moves tables:
        String[] selectionArg = {String.valueOf(moveID)};
        Cursor cursor = database.query(MOVES_TABLE, null, MOVE_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        Move move = new Move(cursor.getString(cursor.getColumnIndex(NAME)),
                cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(POWER)),
                cursor.getInt(cursor.getColumnIndex(ACCURACY)),
                cursor.getInt(cursor.getColumnIndex(PP)),
                cursor.getString(cursor.getColumnIndex(AFFECTS)),
                cursor.getInt(cursor.getColumnIndex(GENERATION_FIRST_APPEARED)),
                getCategory(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))),
                getType(cursor.getInt(cursor.getColumnIndex(TYPE_ID))));

        cursor.close();

        return move;
    }

    /**
     * Goes into the types table to retrieve the pokemon's types
     *
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return the list of types that the pokemon is
     */
    private List<Type> getTypes(int pokemonID) {

        List<Type> types = new LinkedList<>();
        // move the cursor to the pokemon types mapping table
        String[] selectionArg = {String.valueOf(pokemonID)};
        Cursor mappingCursor = database.query(POKEMON_TYPES_TABLE, null, POKEMON_UNIQUE_ID + "=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        while (!mappingCursor.isAfterLast()) {
            types.add(getType(mappingCursor.getInt(mappingCursor.getColumnIndex(TYPE_ID))));

            // go to the next location for this pokemonID
            mappingCursor.moveToNext();
        }

        // close the cursor
        mappingCursor.close();

        return types;
    }

    /**
     * Goes into the egg groups table to retrieve the pokemon's egg groups
     *
     * @param nationalID nationalID in the pokemon table
     * @return the list of egg groups that the pokemon belongs to
     */
    private List<EggGroup> getEggGroups(int nationalID) {
        List<EggGroup> eggGroups = new LinkedList<>();

        // move the cursor to the egg group mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor mappingCursor = database.query(POKEMON_EGG_GROUPS_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        // Get the cursors for the egg Groups tables:
        Cursor cursor = database.query(EGG_GROUPS_TABLE, null, null, null, null, null, null);

        while (!mappingCursor.isAfterLast()) {
            selectionArg[0] = String.valueOf(mappingCursor.getInt(mappingCursor.getColumnIndex(EGG_GROUP_ID)));
            cursor = database.query(EGG_GROUPS_TABLE, null, EGG_GROUP_ID + "=?", selectionArg, null, null, null);
            cursor.moveToFirst();

            eggGroups.add(new EggGroup(cursor.getString(cursor.getColumnIndex(NAME))));

            // go to the next egg group for this pokemonID
            mappingCursor.moveToNext();
        }

        // close the cursor
        cursor.close();
        mappingCursor.close();

        return eggGroups;
    }

    /**
     * Goes into the evolution table to retrieve the pokemon's evolutions
     *
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return the list of evolutions of the pokemon
     */
    private List<Evolution> getEvolutions(int pokemonID) {
        List<Evolution> evolutions = new LinkedList<>();

        // move the cursor to the evolutions mapping table
        String[] selectionArg = {String.valueOf(pokemonID)};
        Cursor mappingCursor = database.query(POKEMON_EVOLUTIONS_TABLE, null, FROM_POKEMON_ID + "=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        //By having Evolutions point to a full Pokemon object, Building a single instance of a Pokemon
        // will result in a DFS to build all of its evolutions. Therefore, with good reason, this
        // should only be done once!
        while (!mappingCursor.isAfterLast()) {
            int evolvesToPokemonID = mappingCursor.getInt(mappingCursor.getColumnIndex(TO_POKEMON_ID));
            String condition = mappingCursor.getString(mappingCursor.getColumnIndex(CONDITION));
            evolutions.add(new Evolution(condition, getPokemonByPokemonID(evolvesToPokemonID)));

            // go to the next evolution for this pokemonID
            mappingCursor.moveToNext();
        }

        // close the cursor
        mappingCursor.close();

        return evolutions;
    }

    /**
     * From the typeID, goes into the types table to construct the {@link Type} object.
     * @param typeID Unique type ID
     * @return  {@link Type} from the database corresponding to that typeID
     */
    private Type getType(int typeID) {
        Type type = types[typeID];

        if(PRINT_DEBUG)
            Log.v("Database Access", "From typeID " + String.valueOf(typeID) + " type obtained was " + type.getName());

        /* This loads a new Type object each time.
        String[] selectionArg = {String.valueOf(typeID)};
        Cursor cursor = database.query(TYPES_TABLE, null, TYPE_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        Type type = new Type(cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(DESCRIPTION)));

        // close the cursor
        cursor.close();
        */
        return type;
    }

    /**
     * Maps the categoryID to the name of the category.
     *
     * @param categoryID from the moves table
     * @return String of the name of the category
     */
    private String getCategory(int categoryID) {
        String[] selectionArg = {String.valueOf(categoryID)};
        Cursor cursor = database.query(CATEGORY_TABLE, null, CATEGORY_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(NAME));

        // close the cursor
        cursor.close();

        return name;
    }

    /**
     * Checks whether the pokemon has been caught or not.
     *
     * @param nationalID national ID of the pokemon
     * @return true if the pokemon has been caught, false otherwise
     */
    private boolean isCaught(int nationalID) {
        // TODO: Implement this method with correct names when database is changed
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor cursor = database.query(POKEMON_CAUGHT_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        // assume not caught
        boolean caught = false;
        // verify if it is caught
        if (cursor.getInt(cursor.getColumnIndex(CAUGHT)) == 1) {
            caught = true;
        }

        // close the cursor
        cursor.close();
        if(PRINT_DEBUG)
            Log.v("Database Access", "T/F: Pokemon with nationalID " + String.valueOf(nationalID) + " was caught: " + String.valueOf(caught));

        return caught;
    }

    /**
     * Used to toggle whether the pokemon has been caught or not.
     * If the pokemon was caught, it will change the database to say that it is not caught.
     * The opposite is also true.
     *
     * @param nationalID national ID of the pokemon
     */
    public void toggleCaught(int nationalID) {
        // TODO: Implement this method with correct names when database is changed
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor cursor = database.query(POKEMON_CAUGHT_TABLE, null, POKEMON_NATIONAL_ID + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        // assume not caught
        int caught = 0;
        // verify if it is caught, change it to not caught.
        if (cursor.getInt(cursor.getColumnIndex(CAUGHT)) == 0) {
            caught = 1;
        }
        // close the cursor
        cursor.close();

        // create new row content
        ContentValues content = new ContentValues();
        content.put(POKEMON_NATIONAL_ID, nationalID);
        content.put(CAUGHT, caught);

        // replace the row with the nationalID with the new row
        database.beginTransaction();
        database.replaceOrThrow(POKEMON_CAUGHT_TABLE, null, content);
        database.endTransaction();
    }

    /**
     * Sets the value of caught to caught or not in the database.
     *
     * @param nationalID nationalID of the pokemon that is caught
     * @param value true if changing to caught, false if changing to not caught
     */
    public void setCaught(int nationalID, boolean value){
        int caught = value? 1 : 0;
        // create new row content
        ContentValues content = new ContentValues();
        content.put(POKEMON_NATIONAL_ID, nationalID);
        content.put(CAUGHT, caught);

        // replace the row with the nationalID with the new row
        database.beginTransaction();
        database.replaceOrThrow(POKEMON_CAUGHT_TABLE, null, content);
        database.endTransaction();
    }

    /**
     * Determines the current generation of the games.
     *
     * @return current generation
     */
    public int getCurrentGeneration() {
        /*
        TODO: test this method, and the getMaxNationalID, to make sure that this selection command
                 works to get the max. If not, let @Nicole know :)
        */
        Cursor cursor = database.query(GAMES_TABLE, null, null, null, null, null, null);
        cursor.moveToLast();

        int generationID = cursor.getInt(cursor.getColumnIndex(GENERATION_ID));

        // close the cursor:
        cursor.close();

        return generationID;
    }

    /**
     * Determines the current number of pokemon by national ID
     *
     * @return max national ID
     */
    public int getMaxNationalID() {
        Cursor cursor = database.query(POKEMON_NATIONAL_ID_TO_UNIQUE_ID_TABLE, null, null, null, null, null, null);
        cursor.moveToLast();

        int maxNationalID = cursor.getInt(cursor.getColumnIndex(POKEMON_NATIONAL_ID));

        // close the cursor:
        cursor.close();

        return maxNationalID;
    }

    /**
     * Determines the current number of pokemon by their unique ID
     *
     * @return max national ID
     */
    public int getMaxUniqueID() {
        Cursor cursor = database.query(POKEMON_UNIQUE_INFO_TABLE, null, null, null, null, null, null);
        cursor.moveToLast();

        int maxUniqueID = cursor.getInt(cursor.getColumnIndex(POKEMON_UNIQUE_ID));

        // close the cursor:
        cursor.close();

        return maxUniqueID;
    }

    /**
     * Determines the current number of types
     *
     * @return max typeID
     */
    public int getMaxTypeID() {
        Cursor cursor = database.query(TYPES_TABLE, null, null, null, null, null, null);
        cursor.moveToLast();

        int maxTypeID = cursor.getInt(cursor.getColumnIndex(TYPE_ID));
        if(PRINT_DEBUG)
            Log.v("Database Access", "Max typeID is: " + String.valueOf(maxTypeID));
        // close the cursor:
        cursor.close();

        return maxTypeID;
    }


    /**
     * Get the party including all of the {@link Pokemon} and their respective {@link MoveSet}
     *
     * @return the {@link Party} of the user
     */
    public Party getParty() {
        // grab the whole party table
        Cursor cursor = database.query(PARTY_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();

        Party myParty = new Party();

        Cursor moveCursor = database.query(PARTY_MOVESET_TABLE, null, null, null, null, null, null);

        // go through the party table making the pokemon and associated moveSets for each one
        while (!(cursor.isAfterLast())) {
            int pokemonID = cursor.getInt(cursor.getColumnIndex(POKEMON_UNIQUE_ID));
            int partyID = cursor.getInt(cursor.getColumnIndex(PARTY_ID));

            List<Move> moves = new LinkedList<>();

            String[] selectionArg = {String.valueOf(partyID)};
            moveCursor = database.query(PARTY_MOVESET_TABLE, null, PARTY_ID + "=?", selectionArg, null, null, null);

            while (!(moveCursor.isAfterLast())) {
                moves.add(getMove(moveCursor.getInt(moveCursor.getColumnIndex(MOVE_ID))));

                moveCursor.moveToNext();
            }

            try {
                myParty.addPokemonToParty(getPokemonByPokemonID(pokemonID), new MoveSet(moves));
            } catch (PartyFullException e) {
                // this should not happen in this context since there should not be more than
                // 6 pokemon in the party stored in the database.
            }

            cursor.moveToNext();
        }

        cursor.close();
        moveCursor.close();

        return myParty;
    }

    /**
     * Removes a pokemon and their associated moveSet from the database.
     *
     * @param partyID the array index in the {@link Party} associated with the pokemon to be removed.
     */
    public void removePokemonFromParty(int partyID) {
        String[] selectionArg = {String.valueOf(partyID)};

        database.beginTransaction();
        // delete the pokemon from the party table
        database.delete(PARTY_TABLE, PARTY_ID + "=?", selectionArg);
        // delete the pokemon's moves from the moveSet table
        database.delete(PARTY_MOVESET_TABLE, PARTY_ID + "=?", selectionArg);
        database.endTransaction();
    }

    /**
     * Adds the information associated to the pokemon to the party table.
     *
     * Note: Assumes that the partyID that is being passed is valid (i.e. not already occupied by
     *       another pokemon, and less than 6, which is the max number of pokemon in a party,
     *       since verification should have happened at previous step)
     *
     * @param partyPokemon the pokemon and moveset to be added (includes the partyID)
     */
    public void addPokemonToParty(PartyPokemon partyPokemon) throws Exception {
        // TODO: Should we verify here as well that there are not already 6 pokemon in the database?
        // It would be easy enough to just go in the database and make sure that the partyID
        // is not there

        // firstly, add the party
        // create new row content for party table
        ContentValues partyContent = new ContentValues();
        partyContent.put(PARTY_ID, partyPokemon.getPartyID());
        partyContent.put(POKEMON_UNIQUE_ID, partyPokemon.getPokemon().getPokemonUniqueID());

        int numberOfMoves = partyPokemon.getMoveSet().getNumberOfMoves();
        List<Move> moves = partyPokemon.getMoveSet().getMoves();

        ContentValues[] moveContent = new ContentValues[numberOfMoves];
        for (int i = 0; i < numberOfMoves; i++) {
            moveContent[i].put(MOVE_ID, getMoveID(moves.get(i)));
        }


        // insert everything into the table at once, and if there is an exception, do not finalize
        // the transaction and throw an error
        try {
            database.beginTransaction();
            database.insertOrThrow(PARTY_TABLE, null, partyContent);
            // insert the moves
            for (int i = 0; i < numberOfMoves; i++) {
                database.insertOrThrow(PARTY_MOVESET_TABLE, null, moveContent[i]);
            }
            database.endTransaction();
        } catch (Exception e) {
            throw new Exception("Database Error: Pokemon not successfully added to party.");
        }

    }

    /**
     * Adds a move to a pokemon in the party.
     *
     * @param partyID index in the array in party that the pokemon is associated to
     * @param move move to be added.
     */
    public void addMoveToPartyPokemon(int partyID, Move move) throws Exception {
        ContentValues moveContent = new ContentValues();
        moveContent.put(PARTY_ID, partyID);
        // Get the cursor for the moves tables to determine the moveID associated with the move

        moveContent.put(MOVE_ID, getMoveID(move));

        try {
            database.beginTransaction();
            database.insertOrThrow(PARTY_MOVESET_TABLE, null, moveContent);
            database.endTransaction();
        } catch (Exception e) {
            throw new Exception("Database Error: Move not added to party pokemon.");
        }
    }

    /**
     * Removes a move from the party_moveSet table associated with a specific pokemon
     * @param partyID partyID of the pokemon to remove the move from
     * @param move move to be removed
     */
    public void removeMoveFromPokemonInParty(int partyID, Move move) {
        String[] selectionArgs = {String.valueOf(partyID), String.valueOf(getMoveID(move))};
        database.delete(PARTY_MOVESET_TABLE, PARTY_ID + "=? AND " + MOVE_ID + "=?", selectionArgs);
    }

    /**
     * Loads all the Types into memory. Should be done at program start (during splash screen)
     */
    public void loadAllTypes() {
        // fail safe 0th index of types:
        types[0] = new Type("None/Bird", 0);
        // get the real types from the database:
        for (int i = 1; i < getMaxTypeID() + 1; i++) {
            String[] selectionArg = {String.valueOf(i)};
            Cursor cursor = database.query(TYPES_TABLE, null, TYPE_ID + "=?", selectionArg, null, null, null);
            cursor.moveToFirst();

            types[i] = new Type(cursor.getString(cursor.getColumnIndex(NAME)), i);

            // close the cursor
            cursor.close();
        }
    }

    /**
     * Retrieves the moveID of the move.
     *
     * @param move move to get ID of
     * @return int moveID of the move
     */
    private int getMoveID(Move move) {
        String[] selectionArg = {move.getName()};
        String[] columnArg = {MOVE_ID};
        Cursor cursor = database.query(MOVES_TABLE, columnArg, NAME + "=?", selectionArg, null, null, null);
        cursor.moveToFirst();
        int moveID = cursor.getInt(cursor.getColumnIndex(MOVE_ID));
        cursor.close();

        return moveID;
    }

    public boolean isDetailedNationalIDBuiltAndReady(int nationalID){
        return detailedPokemonShortList!=null && detailedPokemonShortList[nationalID]!=null;
    }

}
