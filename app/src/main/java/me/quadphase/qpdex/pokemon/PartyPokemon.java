package me.quadphase.qpdex.pokemon;

import me.quadphase.qpdex.exceptions.InvalidMoveException;
import me.quadphase.qpdex.exceptions.MoveNotLearnedException;
import me.quadphase.qpdex.exceptions.MoveSetFullException;

/**
 * Class that is used to wrap {@link Pokemon} and {@link MoveSet} in a single place to store them in the party.
 */
public class PartyPokemon {

    /**
     * A pokemon in the party
     */
    private Pokemon pokemon;
    /**
     * The unique identifier of each pokemon in the party
     *
     * Must be between 1 and 6 inclusively
     */
    private int partyID;
    /**
     * MoveSet of the pokemon
     */
    private MoveSet moveSet;

    /**
     * Constructor
     *
     * @param pokemon pokemon in the party
     * @param partyID way of differentiating between the same types of pokemon in the party
     * @param moveSet moveSet that this particular pokemon has
     */
    public PartyPokemon(Pokemon pokemon, int partyID, MoveSet moveSet) {
        this.pokemon = pokemon;
        this.partyID = partyID;
        this.moveSet = moveSet;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public int getPartyID() {
        return partyID;
    }

    public MoveSet getMoveSet() {
        return moveSet;
    }

    /**
     * Determines whether the move is legal for the given pokemon.
     *
     * @param move move to verify
     * @throws InvalidMoveException if the move is not valid with this pokemon
     */
    public void verifyMoveAllowed(Move move) throws InvalidMoveException {
        if (this.pokemon.getMoves().contains(move)) return;
        throw new InvalidMoveException(this.pokemon, move);
    }

    /**
     * Determines whether the moveSet is full
     * @throws MoveSetFullException when the moveSet is full
     */
    public void verifyMoveSetFull() throws MoveSetFullException {
        if (moveSet.getNumberOfMoves() == MoveSet.MAX_MOVES_IN_MOVESET) {
            return;
        }
        throw new MoveSetFullException();
    }

    /**
     * Determines whether the move being passed is already in the moveSet of the pokemon in the party
     * @param move move to verify
     * @throws MoveNotLearnedException when the move is not in the moveSet of this pokemon
     */
    public void verifyMoveLearned(Move move) throws MoveNotLearnedException {
        if (moveSet.getMoves().contains(move)) return;
        throw new MoveNotLearnedException();
    }
}
