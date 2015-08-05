package me.quadphase.qpdex.pokemon;

import java.util.List;

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

    public MinimalPokemon(int nationalID, String name, String description, List<Type> types) {
        this.nationalID = nationalID;
        this.name = name;
        this.description = description;
        this.types = types;
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

    @Override
    public String toString(){
        return String.format("%s. %s",nationalID,name);
    }
}
