package me.quadphase.qpdex.exceptions;

import me.quadphase.qpdex.pokemon.Move;
import me.quadphase.qpdex.pokemon.Pokemon;

/**
 * Exception created when a move is attempted to be added to a pokemon who cannot learn that move.
 */
public class InvalidMoveException extends Exception {
    /**
     * Move that was tested
     */
    private Move move;

    /**
     * Pokemon that the move was being added to
     */
    private Pokemon pokemon;

    public InvalidMoveException() {
        super();
    }

    public InvalidMoveException(Pokemon pokemon, Move move) {
        super();
        this.move = move;
        this.pokemon = pokemon;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }
}
