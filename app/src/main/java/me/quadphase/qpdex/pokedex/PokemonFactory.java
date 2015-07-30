package me.quadphase.qpdex.pokedex;

import java.util.ArrayList;
import java.util.List;

import me.quadphase.qpdex.pokemon.Ability;
import me.quadphase.qpdex.pokemon.EggGroup;
import me.quadphase.qpdex.pokemon.Evolution;
import me.quadphase.qpdex.pokemon.Game;
import me.quadphase.qpdex.pokemon.Location;
import me.quadphase.qpdex.pokemon.Move;
import me.quadphase.qpdex.pokemon.Party;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * Create all the Pokemon necessary by placing doing ordered calls to the Database Accessor Class
 */
//TODO: Document with JavaDocs!
public class PokemonFactory {
    //TODO: Implement and solve problem between PokemonID and NationalID
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //NOTE:
    // PokemonUniqueID: one-to-one mapping to a specific pokemon, including Mega Evolution and gendered
    // PokemonNationalID: one-to-many mapping used to identify pokemon by a common name, but without
    //                  relation to specific stats. This the number shown in the Pokedex.
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Pokemon createMinimalPokemon(int PokemonNationalID){
        return null;
    }

    public static ArrayList<Pokemon> createCompletePokemonByNationalID(int PokemonNationalID){
        return null;
    }

    public static Pokemon createCompletePokemon(int PokemonUniqueID){
        return null;
    }

    public static List<Evolution> getEvolutionChain(int PokemonNationalID){
        return null;
    }

    public static ArrayList<Location> getLocationForPokemon(int PokemonNationalID){
        return null;
    }

    public static List<Move> getMovesForPokemon(int PokemonNationalID){
        return null;
    }

    public static Type getType(String typeName){
        return null;
    }

    public static ArrayList<Type> getAllTypes(){
        return null;
    }

    public static ArrayList<Ability> getAllAbilities(){
        return null;
    }

    public static ArrayList<EggGroup> getAllPokemonEggGroups(){
        return null;
    }

    public static ArrayList<Game> getAllPokemonGames(){
        return null;
    }

    public static ArrayList<Integer> listAllCaughtPokemon(){
        return null;
    }

    public static Party retrievePokemonInParty(){
        return null;
    }


}
