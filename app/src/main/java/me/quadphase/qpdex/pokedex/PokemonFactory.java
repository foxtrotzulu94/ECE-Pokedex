package me.quadphase.qpdex.pokedex;

import java.util.ArrayList;
import java.util.List;

import me.quadphase.qpdex.pokemon.Evolution;
import me.quadphase.qpdex.pokemon.Location;
import me.quadphase.qpdex.pokemon.Move;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * Create all the Pokemon necessary by placing doing ordered calls to the Database Accessor Class
 */
//TODO: Document with JavaDocs!
public class PokemonFactory {
    //TODO: Implement and solve problem between PokemonID and NationalID

    public static Pokemon createMinimalPokemon(int PokemonID){
        return null;
    }

    public static Pokemon createCompletePokemon(int PokemonID){
        return null;
    }

    public static List<Evolution> getEvolutionChain(int PokemonID){
        return null;
    }

    public static ArrayList<Location> getLocationForPokemon(int PokemonID){
        return null;
    }

    public static List<Move> getMovesForPokemon(int PokemonID){
        return null;
    }

    public static Type getType(String typeName){
        return null;
    }

    public static ArrayList<Integer> getAllPokemonCaught(){
        return null;
    }



}
