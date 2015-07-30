package me.quadphase.qpdex.databaseAccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import me.quadphase.qpdex.pokemon.Ability;
import me.quadphase.qpdex.pokemon.Game;
import me.quadphase.qpdex.pokemon.Location;
import me.quadphase.qpdex.pokemon.minimalPokemon;
import me.quadphase.qpdex.pokemon.Move;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * Created by Nicole on 28-Jul-15.
 *
 * Class used to retrieve pokemon and their information from the database.
 */
public class PokemonFactory {

    private static final String DB_NAME = "pokedex.db";

    /**
     * SQLite database handle
     */
    private SQLiteDatabase database;

    /**
     * Constructor of the Pokemon Factory
     */
    public PokemonFactory(Context context){
        //Initialize the Database Handler
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(context, DB_NAME);
        database = dbOpenHelper.openDataBase();
    }


    /**
     * Retrieves from database a list of all pokemon
     *
     * @return List of all pokemon from the database with the nationalID and the name
     */
    public List<minimalPokemon> getAllPokemon() {
        return null;
    }

    /**
     * Constructs the {@link me.quadphase.qpdex.pokemon.Pokemon} from the information in the database.
     *
     * @param nationalID - pokemon ID of the pokemon to be brought into memory
     * @return the complete pokemon object including all of its information such as type(s),
     *          move(s) and ability(ies)
     */
    public Pokemon getPokemonByID(int nationalID) {
        // find the pokemonID from the nationalID using the pokemon_nationalID mapping table
        String[] columns = new String[1];
        columns[0] = "pokemonID";
        String[] selectionArg = new String[1];
        selectionArg[0] = String.valueOf(nationalID);
        Cursor cursor = database.query("pokemon_nationalID", columns, "nationalID=?", selectionArg, "pokemonID", null, null);

        // HACK: We are assuming that the first pokemon with this nationalID is the original pokemon
        // that we want to fetch, since there are many pokemon with the same nationalID in the case
        // of mega evolutions and various types.
        cursor.moveToFirst();
        int pokemonID = cursor.getInt(cursor.getColumnIndex("pokemonID"));

        // move the cursor to the correct entry of the pokemon table
        cursor = database.rawQuery(String.format("SELECT * FROM pokemon WHERE pokemonID=%s", String.valueOf(pokemonID)), null);
        cursor.moveToFirst();

        // get all of the information stored in pokemon table:
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
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

        //TODO
//        boolean caught = getCaught(nationalID);

        // lists to fetch: TODO: finish implementing these methods
        List<Location> locations = getLocations(pokemonID);
        List<Ability> abilities = getAbilities(pokemonID);
        List<Move> moves = getMoves(pokemonID);
//        List<Type> types = getTypes(pokemonID);
//        List<EggGroup> eggGroups = getEggGroups(pokemonID);
//        List<Evolution> evolutions = getEvolutions(pokemonID);

//        return new Pokemon(nationalID, name, description, height, weight, attack, defence, hp,
//                spAttack, spDefence, speed, caught, genFirstAppeared, hatchTime, catchRate, genderRatioMale,
//                locations, abilities, moves, types, eggGroups, evolutions);

        return null;
    }

    /**
     * Goes into the locations table to retrieve the pokemon's locations
     *
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return the list of locations that the pokemon is found at
     */
    private List<Location> getLocations(int pokemonID) {
        List<Location> locations = new LinkedList<>();
        // move the cursor to the pokemon location table
        String[] selectionArg = new String[1];
        selectionArg[0] = String.valueOf(pokemonID);
        Cursor mappingCursor = database.query("pokemon_locations", null, "pokemonID=?", selectionArg, null, null, null);

        // Get the cursors for the locations and games tables:
        Cursor locationCursor = database.query("locations", null, null, null, null, null, null);
        Cursor gameCursor = database.query("games", null, null, null, null, null, null);

        while (true) {
            locationCursor.moveToPosition(mappingCursor.getInt(mappingCursor.getColumnIndex("locationID")));

            int gameID = locationCursor.getInt(locationCursor.getColumnIndex("gameID"));
            gameCursor.moveToPosition(gameID);
            Game game = new Game(gameCursor.getString(gameCursor.getColumnIndex("name")), gameCursor.getInt(gameCursor.getColumnIndex("generationID")));
            Location loc = new Location(locationCursor.getString(locationCursor.getColumnIndex("name")), game);

            locations.add(loc);

            // go to the next location for this pokemonID
            mappingCursor.moveToNext();

            // exit the loop at the last location
            if (mappingCursor.isLast()) {break;}
        }
        return locations;
    }

    /**
     * goes into the abilities table to retrieve a list of the pokemon's abilities
     * @param pokemonID pokemonID in the pokemon table (not the nationalID)
     * @return a list of the pokemon's abilities
     */
    private List<Ability> getAbilities(int pokemonID) {
        List<Ability> abilities = new LinkedList<>();
        // move the cursor to the pokemon abilities table
        String[] selectionArg = new String[1];
        selectionArg[0] = String.valueOf(pokemonID);
        Cursor mappingCursor = database.query("pokemon_abilities", null, "pokemonID=?", selectionArg, null, null, null);

        // Get the cursor for the ability table:
        Cursor cursor = database.query("abilities", null, null, null, null, null, null);

        while (true) {
            cursor.moveToPosition(mappingCursor.getInt(mappingCursor.getColumnIndex("abilityID")));

            Ability ability = new Ability(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("description")));

            abilities.add(ability);

            // go to the next location for this pokemonID
            mappingCursor.moveToNext();

            // exit the loop at the last location
            if (mappingCursor.isLast()) {break;}
        }
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
        // move the cursor to the pokemon moves table
        String[] selectionArg = new String[1];
        selectionArg[0] = String.valueOf(pokemonID);
        Cursor mappingCursor = database.query("pokemon_moves", null, "pokemonID=?", selectionArg, null, null, null);

        // Get the cursors for the locations and games tables:
        Cursor cursor = database.query("moves", null, null, null, null, null, null);

        while (true) {
            cursor.moveToPosition(mappingCursor.getInt(mappingCursor.getColumnIndex("moveID")));

            // TODO: FINISH THIS
//            Move move = new Move(cursor.getString(cursor.getColumnIndex("name")),
//                    cursor.getString(cursor.getColumnIndex("description")),
//                    cursor.getInt(cursor.getColumnIndex("power")),
//                    cursor.getInt(cursor.getColumnIndex("accuracy")),
//                    cursor.getInt(cursor.getColumnIndex("pp")),
//                    cursor.getString(cursor.getColumnIndex("affects")),);
//
//            moves.add(move);

            // go to the next location for this pokemonID
            mappingCursor.moveToNext();

            // exit the loop at the last location
            if (mappingCursor.isLast()) {break;}
        }
        return moves;
    }

    /**
     * From the typeID, goes into the types table to construct the {@link Type} object.
     * @param typeID Unique type ID
     * @return  {@link Type} from the database corresponding to that typeID
     */
    private Type getType(int typeID) {
        String[] selectionArg = new String[1];
        selectionArg[0] = String.valueOf(typeID);
        Cursor cursor = database.query("types", null, "typeID=?", selectionArg, null, null, null);

        return new Type(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("description")));
    }

    /**
     * Determines the current generation of the games.
     *
     * TODO: For now, it returns a hardcoded value, but this should scrape the database to find the
     * current generation
     *
     * @return current generation
     */
    public int getCurrentGeneration() {
        return 6;
    }

    /**
     * Determines the current number of pokemon by national ID
     *
     * @return max national ID
     */
    public int getMaxNationalID() {
        return 719;
    }
}
