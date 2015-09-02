package me.quadphase.qpdex.exceptions;

import me.quadphase.qpdex.pokemon.Pokemon;

/**
 * Exception to be thrown when the party is full (i.e. has 6 pokemon)
 * and the user is attempting to add another one.
 *
 * The pokemon that the user is attempting to add can be stored in this
 * exception so that it can be dealt with appropriately.
 */
public class PartyFullException extends Exception {
    /**
     * Pokemon that the user is attempting to add to the full party.
     */
    private Pokemon pokemon;

    /**
     * Default Constructor
     */
    public PartyFullException() {
        super();
    }

    /**
     * Constructor with Pokemon
     * @param pokemon pokemon that the user is attempting to add to the full party.
     */
    public PartyFullException(Pokemon pokemon) {
        super();
        this.pokemon = pokemon;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }
}
