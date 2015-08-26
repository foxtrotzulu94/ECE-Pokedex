package me.quadphase.qpdex.pokemon;

import java.util.List;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;

/**
 * This is a minimal Pokemon that only has the bare minimum necessary to be used on the main screen.
 */
public class MinimalPokemon {

    /**
     * National ID in the original Pokedex
     */
    private int nationalID;
    /**
     * Name of the Pokemon in English
     */
    private String name;
    /**
     * Description of the Pokemon from the latest generation
     */
    private String description;

    /**
     * List of the types that the Pokemon has
     * MAX: 2
     */
    private List<Type> types;

    private boolean caught;

    public MinimalPokemon(int nationalID, String name, String description, List<Type> types, boolean caught) {
        this.nationalID = nationalID;
        this.name = name;
        this.description = description;
        this.types = types;
        this.caught = caught;
    }

    public MinimalPokemon(int nationalID, String name, String description, List<Type> types) {
        this.nationalID = nationalID;
        this.name = name;
        this.description = description;
        this.types = types;
        this.caught = false;
    }

    public int getNationalID() {
        return nationalID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Type> getTypes() {
        return types;
    }

    public boolean isCaught() {
        return caught;
    }

    /**
     * Toggles the caught status of this minimalPokemon in memory and in the database.
     */
    public void toggleCaught() {
        // toggle in memory
        this.caught = !this.caught;
        // toggle in the database:
        PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
        pokemonFactory.toggleCaught(this.nationalID);
    }

    @Override
    public String toString(){
        return String.format("%s. %s",nationalID,name);
    }
}
