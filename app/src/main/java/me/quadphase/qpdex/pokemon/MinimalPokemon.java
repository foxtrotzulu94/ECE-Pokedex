package me.quadphase.qpdex.pokemon;

import android.util.Log;

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

    public MinimalPokemon(int nationalID, boolean wasCaught) {
        this.nationalID = nationalID;
        caught=wasCaught;
        //The rest can be set only once during deferred construction.
        types=null;
        name=null;
        description=null;
    }

    public int getPokemonNationalID() {
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
        pokemonFactory.setCaught(this.nationalID,this.caught);
    }

    public void setName(String name) {
        if(this.name==null)
            this.name = name;
        else
            Log.e("QPDEX_MinimalPkmn",String.format("Tried to overwrite validated data in %s.%s",this.nationalID, this.name));
    }

    public void setDescription(String description) {
        if (this.description==null)
            this.description = description;
        else
            Log.e("QPDEX_MinimalPkmn",String.format("Tried to overwrite validated data in %s.%s",this.nationalID, this.name));
    }

    public void setTypes(List<Type> types) {
        if(this.types==null)
            this.types = types;
        else
            Log.e("QPDEX_MinimalPkmn", String.format("Tried to overwrite validated data in %s.%s", this.nationalID, this.name));
    }

    @Override
    public String toString(){
        return String.format("%s. %s",nationalID,name);
    }



}
