package me.quadphase.qpdex.pokemon;

import java.util.List;

/**
 * This is a wrapper class for the moves that a pokemon in your party has
 */
public class MoveSet {

    /**
     * Constant indicating the maximum number of moves in a moveset
     */
    public final static int MAX_MOVES_IN_MOVESET = 4;

    /**
     * the moves that the pokemon in the party has.
     */
    private List<Move> moves;

    /**
     * Constructor
     */
    public MoveSet(List<Move> moves) {
        this.moves = moves;
    }

    /**
     * adds a move to the moveset if it is not full
     */
    public boolean addMove(Move move) {
        return true;
    }

    /**
     * removes a move from the moveset
     */
    public boolean removeMove(Move move) {
        return true;
    }

    /**
     * Getter
     */
    public List<Move> getMoves() {
        return moves;
    }

    /**
     * Determines the number of moves currently in the moveSet
     * @return number of moves in moveSet
     */
    public int getNumberOfMoves() {
        return moves.size();
    }
}
