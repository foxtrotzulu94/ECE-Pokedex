package me.quadphase.qpdex.exceptions;

import me.quadphase.qpdex.pokemon.PartyPokemon;

/**
 * Exception used to indicate that a pokemon is not currently in the party.
 */
public class PokemonNotInPartyException extends Exception {

    private PartyPokemon pokemon;

    public PokemonNotInPartyException() {
        super();
    }

    public PokemonNotInPartyException(PartyPokemon pokemon) {
        super();
        this.pokemon = pokemon;
    }

    public PartyPokemon getPokemon() {
        return pokemon;
    }

    public void setPokemon(PartyPokemon pokemon) {
        this.pokemon = pokemon;
    }
}
