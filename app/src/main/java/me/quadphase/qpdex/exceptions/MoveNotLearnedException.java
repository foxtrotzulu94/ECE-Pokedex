package me.quadphase.qpdex.exceptions;

/**
 * Exception to be used when trying to manipulate a move of a pokemon in a party when
 * the pokemon in question does not yet know that move.
 */
public class MoveNotLearnedException extends Exception {

    public MoveNotLearnedException() {
        super();
    }
}
