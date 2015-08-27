package me.quadphase.qpdex.databaseAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.quadphase.qpdex.exceptions.PartyFullException;
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

    private static final String DB_NAME = "pokedex.db";

    private static PokemonFactory instance = null;

    private HashMap<Integer,Type> types;

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

        types = new HashMap<>();

//        loadAllTypes(); //Method within Database seems to be broken.
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
    public MinimalPokemon[] getAllPokemon() {
        MinimalPokemon[] allPokemon = new MinimalPokemon[getMaxNationalID()];

        for (int i = 0; i < getMaxNationalID(); i++) {
            allPokemon[i] = getMinimalPokemonByNationalID(i + 1);
        }

        return allPokemon;
    }

    /**
     * Creates objects of type {@link MinimalPokemon} based on the national ID of the pokemon
     * @param nationalID National ID of the pokemon
     * @return {@link MinimalPokemon} object
     */
    public MinimalPokemon getMinimalPokemonByNationalID(int nationalID) {
        // find the pokemonID from the nationalID using the pokemon_nationalID mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor cursor = database.query("pokemon_nationalID", null, "nationalID=?", selectionArg, null, null, null);

        // HACK: We are assuming that the first pokemon with this nationalID is the original pokemon
        // that we want to fetch, since there are many pokemon with the same nationalID in the case
        // of mega evolutions and various types.
        cursor.moveToFirst();
        int pokemonID = cursor.getInt(cursor.getColumnIndex("pokemonID"));
        Log.v("Database Access:", "From nationalID " + String.valueOf(nationalID) + " the pokemonID is: " + String.valueOf(pokemonID));
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
        // move the cursor to the correct entry of the pokemon table
        String[] pokemonIDString = {String.valueOf(pokemonID)};
        Cursor cursor = database.query("pokemon", null, "pokemonID=?", pokemonIDString, null, null, null, null);
        cursor.moveToFirst();

        // get all of the information stored in pokemon table:
        String name = cursor.getString(cursor.getColumnIndex("name"));
        Log.v("Database Access:", "From pokemonID " + String.valueOf(pokemonID) + " the name is: " + name);

        cursor.close();

        // get the nationalID:
        String[] selectionArg = {String.valueOf(pokemonID)};
        cursor = database.query("pokemon_nationalID", null, "pokemonID=?", selectionArg, null, null, null);
        cursor.moveToFirst();
        int nationalID = cursor.getInt(cursor.getColumnIndex("nationalID"));
        Log.v("Database Access:", "From pokemonID " + String.valueOf(pokemonID) + " the nationalID is: " + String.valueOf(nationalID));

        cursor.close();

        // get the description from the description table:
        selectionArg[0] = String.valueOf(nationalID);
        cursor = database.query("pokemon_description", null, "nationalID=?", selectionArg, null, null, null, null);
        cursor.moveToFirst();
        String description = cursor.getString(cursor.getColumnIndex("description"));
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
        // move the cursor to the correct entry of the pokemon table
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM pokemon WHERE pokemonID=%s", String.valueOf(pokemonID)), null);
        cursor.moveToFirst();

        // get all of the information stored in pokemon table:
        String name = cursor.getString(cursor.getColumnIndex("name"));
        double height = cursor.getFloat(cursor.getColumnIndex("height"));
        double weight = cursor.getFloat(cursor.getColumnIndex("weight"));
        int attack = cursor.getInt(cursor.getColumnIndex("attack"));
        int defence = cursor.getInt(cursor.getColumnIndex("defence"));
        int hp = cursor.getInt(cursor.getColumnIndex("hp"));
        int spAttack = cursor.getInt(cursor.getColumnIndex("spAttack"));
        int spDefence = cursor.getInt(cursor.getColumnIndex("spDefence"));
        int speed = cursor.getInt(cursor.getColumnIndex("speed"));
        int genFirstAppeared = cursor.getInt(cursor.getColumnIndex("genFirstAppeared"));
        int hatchTime = cursor.getInt(cursor.getColumnIndex("hatchTime"));
        int catchRate = cursor.getInt(cursor.getColumnIndex("catchRate"));
        int genderRatioMale = cursor.getInt(cursor.getColumnIndex("genderRatioMale"));

        // get the nationalID:
        String[] selectionArg = {String.valueOf(pokemonID)};
        cursor = database.query("pokemon_nationalID", null, "pokemonID=?", selectionArg, null, null, null);
        cursor.moveToFirst();
        int nationalID = cursor.getInt(cursor.getColumnIndex("nationalID"));

        // get the description:
        selectionArg[0] = String.valueOf(nationalID);
        cursor = database.query("pokemon_description", null, "national_id=?", selectionArg, null, null, null, null);
        cursor.moveToFirst();
        String description = cursor.getString(cursor.getColumnIndex("description"));

        // close the cursor
        cursor.close();

        boolean caught = isCaught(nationalID);

        // lists to fetch:
        List<Location> locations = getLocations(pokemonID);
        List<Ability> abilities = getAbilities(pokemonID);
        List<Move> moves = getMoves(pokemonID);
        List<Type> types = getTypes(pokemonID);
        List<EggGroup> eggGroups = getEggGroups(pokemonID);
        List<Evolution> evolutions = getEvolutions(pokemonID);


        return new Pokemon(pokemonID, nationalID, name, description, height, weight, attack, defence, hp,
                spAttack, spDefence, speed, caught, genFirstAppeared, hatchTime, catchRate, genderRatioMale,
                locations, abilities, moves, types, eggGroups, evolutions);
    }

    /**
     * Constructs the main {@link me.quadphase.qpdex.pokemon.Pokemon} from the information in
     * the database.
     *
     * @param nationalID - national ID of the pokemon to be brought into memory
     * @return the complete pokemon object including all of its information such as type(s),
     *          move(s) and ability(ies)
     */
    public Pokemon getPokemonByNationalID(int nationalID) { // TODO: return list of all
        // find the pokemonID from the nationalID using the pokemon_nationalID mapping table
        String[] selectionArg = {String.valueOf(nationalID)};
        Cursor cursor = database.query("pokemon_nationalID", null, "nationalID=?", selectionArg, null, null, null);

        // HACK: We are assuming that the first pokemon with this nationalID is the original pokemon
        // that we want to fetch, since there are many pokemon with the same nationalID in the case
        // of mega evolutions and various types.
        cursor.moveToFirst();
        int pokemonID = cursor.getInt(cursor.getColumnIndex("pokemonID"));

        // close the cursor
        cursor.close();

        return getPokemonByPokemonID(pokemonID);
    }

    /**
     * Goes into the locations table to retrieve the pokemon's locations
     *
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return the list of locations that the pokemon is found at
     */
    private List<Location> getLocations(int pokemonID) {
        List<Location> locations = new LinkedList<>();
        // move the cursor to the pokemon location mapping table
        String[] selectionArg = {String.valueOf(pokemonID)};
        Cursor mappingCursor = database.query("pokemon_locations", null, "pokemonID=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        // Get the cursors for the locations and games tables:
        Cursor locationCursor = database.query("locations", null, null, null, null, null, null);
        Cursor gameCursor = database.query("games", null, null, null, null, null, null);

        while (!mappingCursor.isAfterLast()) {
            selectionArg[0] = String.valueOf(mappingCursor.getInt(mappingCursor.getColumnIndex("locationID")));
            locationCursor = database.query("locations", null, "locationID=?", selectionArg, null, null, null);
            locationCursor.moveToFirst();

            int gameID = locationCursor.getInt(locationCursor.getColumnIndex("gameID"));

            selectionArg[0] = String.valueOf(gameID);
            gameCursor = database.query("games", null, "gameID=?", selectionArg, null, null, null);
            gameCursor.moveToFirst();

            Game game = new Game(gameCursor.getString(gameCursor.getColumnIndex("name")), gameCursor.getInt(gameCursor.getColumnIndex("generationID")));
            Location loc = new Location(locationCursor.getString(locationCursor.getColumnIndex("name")), game);

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
        Cursor mappingCursor = database.query("pokemon_abilities", null, "pokemonID=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        // Get the cursor for the ability table:
        Cursor cursor = database.query("abilities", null, null, null, null, null, null);

        while (!mappingCursor.isAfterLast()) {
            selectionArg[0] = String.valueOf(mappingCursor.getInt(mappingCursor.getColumnIndex("abilityID")));
            cursor = database.query("abilities", null, "abilityID=?", selectionArg, null, null, null);
            cursor.moveToFirst();

            Ability ability = new Ability(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("description")));

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
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return the list of moves that the pokemon is found at
     */
    private List<Move> getMoves(int pokemonID) {
        List<Move> moves = new LinkedList<>();
        // move the cursor to the pokemon moves mapping table
        String[] selectionArg = {String.valueOf(pokemonID)};
        Cursor mappingCursor = database.query("pokemon_moves", null, "pokemonID=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        while (!mappingCursor.isAfterLast()) {
            moves.add(getMove(mappingCursor.getInt(mappingCursor.getColumnIndex("moveID"))));

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
        Cursor cursor = database.query("moves", null, "moveID=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        Move move = new Move(cursor.getString(cursor.getColumnIndex("name")),
                cursor.getString(cursor.getColumnIndex("description")),
                cursor.getInt(cursor.getColumnIndex("power")),
                cursor.getInt(cursor.getColumnIndex("accuracy")),
                cursor.getInt(cursor.getColumnIndex("pp")),
                cursor.getString(cursor.getColumnIndex("affects")),
                cursor.getInt(cursor.getColumnIndex("genFirstAppeared")),
                getCategory(cursor.getInt(cursor.getColumnIndex("categoryID"))),
                getType(cursor.getInt(cursor.getColumnIndex("typeID"))));

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
        Cursor mappingCursor = database.query("pokemon_types", null, "pokemonID=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        while (!mappingCursor.isAfterLast()) {
            types.add(getType(mappingCursor.getInt(mappingCursor.getColumnIndex("typeID"))));

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
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return the list of egg groups that the pokemon belongs to
     */
    private List<EggGroup> getEggGroups(int pokemonID) {
        List<EggGroup> eggGroups = new LinkedList<>();

        // move the cursor to the egg group mapping table
        String[] selectionArg = {String.valueOf(pokemonID)};
        Cursor mappingCursor = database.query("pokemon_eggGroups", null, "pokemonID=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        // Get the cursors for the egg Groups tables:
        Cursor cursor = database.query("eggGroups", null, null, null, null, null, null);

        while (!mappingCursor.isAfterLast()) {
            selectionArg[0] = String.valueOf(mappingCursor.getInt(mappingCursor.getColumnIndex("eggGroupID")));
            cursor = database.query("eggGroups", null, "eggGroupID=?", selectionArg, null, null, null);
            cursor.moveToFirst();

            eggGroups.add(new EggGroup(cursor.getString(cursor.getColumnIndex("name"))));

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
        Cursor mappingCursor = database.query("pokemon_evolutions", null, "fromPokemonID=?", selectionArg, null, null, null);
        mappingCursor.moveToFirst();

        while (!mappingCursor.isAfterLast()) {
            int evolvesToPokemonID = mappingCursor.getInt(mappingCursor.getColumnIndex("toPokemonID"));
            String condition = mappingCursor.getString(mappingCursor.getColumnIndex("condition"));
            evolutions.add(new Evolution(condition, getMinimalPokemonByPokemonID(evolvesToPokemonID)));

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
        Type type = types.get(typeID);
        Log.v("Database Access: ", "From typeID " + String.valueOf(typeID) + " type obtained was " + type.getName());

        /* This loads a new Type object each time.
        String[] selectionArg = {String.valueOf(typeID)};
        Cursor cursor = database.query("types", null, "typeID=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        Type type = new Type(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("description")));

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
        Cursor cursor = database.query("categories", null, "categoryID=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex("name"));

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
        Cursor cursor = database.query("pokemon_caught", null, "nationalID=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        // assume not caught
        boolean caught = false;
        // verify if it is caught
        //TODO: Fix the code below, had to comment it out to compile the application and make it work.
//        if (cursor.getInt(cursor.getColumnIndex("isCaught")) == 1) {
//            caught = true;
//        }

        // close the cursor
        cursor.close();
        Log.v("Database Access: ", "T/F: Pokemon with nationalID " + String.valueOf(nationalID) + " was caught: " + String.valueOf(caught));

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
        Cursor cursor = database.query("pokemon_caught", null, "nationalID=?", selectionArg, null, null, null);
        cursor.moveToFirst();

        // assume not caught
        int caught = 0;
        // verify if it is caught, change it to not caught.
        if (cursor.getInt(cursor.getColumnIndex("isCaught")) == 0) {
            caught = 1;
        }
        // close the cursor
        cursor.close();

        // create new row content
        ContentValues content = new ContentValues();
        content.put("nationalID", nationalID);
        content.put("isCaught", caught);

        // replace the row with the nationalID with the new row
        database.beginTransaction();
        database.replaceOrThrow("pokemon_caught", null, content);
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
        Cursor cursor = database.query("games", null, null, null, null, null, null);
        cursor.moveToLast();

        int generationID = cursor.getInt(cursor.getColumnIndex("generationID"));

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
        Cursor cursor = database.query("pokemon_nationalID", null, null, null, null, null, null);
        cursor.moveToLast();

        int maxNationalID = cursor.getInt(cursor.getColumnIndex("nationalID"));

        // close the cursor:
        cursor.close();

        return maxNationalID;
    }

    /**
     * Determines the current number of types
     *
     * @return max typeID
     */
    public int getMaxTypeID() {
        Cursor cursor = database.query("types", null, null, null, null, null, null);
        cursor.moveToLast();

        int maxTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
        Log.d("Database Access: ", "Max typeID is: " + String.valueOf(maxTypeID));
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
        Cursor cursor = database.query("party", null, null, null, null, null, null);
        cursor.moveToFirst();

        Party myParty = new Party();

        Cursor moveCursor = database.query("party_moveSet", null, null, null, null, null, null);

        // go through the party table making the pokemon and associated moveSets for each one
        while (!(cursor.isAfterLast())) {
            int pokemonID = cursor.getInt(cursor.getColumnIndex("pokemonID"));
            int partyID = cursor.getInt(cursor.getColumnIndex("partyID"));

            List<Move> moves = new LinkedList<>();

            String[] selectionArg = {String.valueOf(partyID)};
            moveCursor = database.query("party_moveSet", null, "partyID=?", selectionArg, null, null, null);

            while (!(moveCursor.isAfterLast())) {
                moves.add(getMove(moveCursor.getInt(moveCursor.getColumnIndex("moveID"))));

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
        database.delete("party", "partyID=?", selectionArg);
        // delete the pokemon's moves from the moveSet table
        database.delete("party_moveSet", "partyID=?", selectionArg);
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
        partyContent.put("partyID", partyPokemon.getPartyID());
        partyContent.put("pokemonID", partyPokemon.getPokemon().getUniqueID());

        int numberOfMoves = partyPokemon.getMoveSet().getNumberOfMoves();
        List<Move> moves = partyPokemon.getMoveSet().getMoves();

        ContentValues[] moveContent = new ContentValues[numberOfMoves];
        for (int i = 0; i < numberOfMoves; i++) {
            moveContent[i].put("moveID", getMoveID(moves.get(i)));
        }


        // insert everything into the table at once, and if there is an exception, do not finalize
        // the transaction and throw an error
        try {
            database.beginTransaction();
            database.insertOrThrow("party", null, partyContent);
            // insert the moves
            for (int i = 0; i < numberOfMoves; i++) {
                database.insertOrThrow("party_moveSet", null, moveContent[i]);
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
        moveContent.put("partyID", partyID);
        // Get the cursor for the moves tables to determine the moveID associated with the move

        moveContent.put("moveID", getMoveID(move));

        try {
            database.beginTransaction();
            database.insertOrThrow("party_moveSet", null, moveContent);
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
        database.delete("party_moveSet", "partyID=? AND moveID=?", selectionArgs);
    }

    /**
     * Loads all the Types into memory. Should be done at program start (during splash screen)
     */
    public void loadAllTypes() {
        for (int i = 1; i < getMaxTypeID() + 1; i++) {
            String[] selectionArg = {String.valueOf(i)};
            Cursor cursor = database.query("types", null, "typeID=?", selectionArg, null, null, null);
            cursor.moveToFirst();

//            types.put(i, (new Type(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("description")))));
            types.put(i, (new Type(cursor.getString(cursor.getColumnIndex("name")), "E.ojpclycrb ru yd .yfl.")));

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
        String[] columnArg = {"moveID"};
        Cursor cursor = database.query("moves", columnArg, "name=?", selectionArg, null, null, null);
        cursor.moveToFirst();
        int moveID = cursor.getInt(cursor.getColumnIndex("moveID"));
        cursor.close();

        return moveID;
    }

}
