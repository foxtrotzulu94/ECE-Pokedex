package me.quadphase.qpdex.exceptions;

import me.quadphase.qpdex.pokemon.Move;

/**
 * Exception to be thrown when the moveset of a pokemon in the party
 * is full (i.e. has 4 moves) and the user is attempting to add another one.
 */
public class MoveSetFullException extends Exception {

    /**
     * Move that was trying to be added to the full moveset
     */
    private Move move;

    public MoveSetFullException() {
        super();
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
