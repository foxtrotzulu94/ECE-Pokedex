package me.quadphase.qpdex.pokemon;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.exceptions.MoveSetFullException;
import me.quadphase.qpdex.exceptions.PartyFullException;
import me.quadphase.qpdex.exceptions.PokemonNotInPartyException;

/**
 * These <code>Pokemon</code> are in your party. They each have a particular set of moves.
 */
public class Party {

    /**
     * Array of pokemon in the party
     * Max: 6
     */
    private PartyPokemon[] pokemon;

    /**
     * Constructor to make an empty party for up to 6 pokemon
     */
    public Party() {
        this.pokemon = new PartyPokemon[6];
    }

    /**
     * Adds a pokemon to the pokemon party.
     *
     * @param pokemon - pokemon to be added
     * @param moveSet - {@link MoveSet } that contains up to 4 moves that the pokemon knows
     */
    public void addPokemonToParty(Pokemon pokemon, MoveSet moveSet) throws PartyFullException {
        try {
            // try to find an available spot in the party
            int partyID = firstAvailableSpotInParty();

            // If there are less than 6 pokemon in the party, make a new party pokemon and add it
            // to the party in memory and in the database
            PartyPokemon partyPokemon = new PartyPokemon(pokemon, partyID, moveSet);
            // in memory:
            this.pokemon[partyID] = partyPokemon;
            // in database:
            PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
            pokemonFactory.addPokemonToParty(partyPokemon);
        } catch (PartyFullException e) {
            e.setPokemon(pokemon);
            throw e;
        } catch (Exception e) {
            // catches database writing exceptions
            // do something
        }
    }

    /**
     * Counts the number of pokemon that are currently in the party.
     *
     * @return the number of occupied spots in the party
     */
    public int countPokemonInParty() {
        int totalPokemon = 0;
        for (int i = 0; i < 6; i++) {
            if (pokemon[i] != null) {
                totalPokemon++;
            }
        }
        return totalPokemon;
    }

    /**
     * Determines the first available partyID.
     * @return the first available partyID
     */
    private int firstAvailableSpotInParty() throws PartyFullException {
        // go through the 6 possible spots and find the first that has no pokemon in it
        for (int i = 0; i < 6; i++) {
            if (pokemon[i] == null) {
                return i + 1;
            }
        }
        // if there are no available spots, throw an exception
        throw new PartyFullException();
    }

    /**
     * Removes a pokemon from the pokemon party.
     *
     * @param pokemon - party pokemon to be removed from the party
     */
    public void removePokemonFromParty(PartyPokemon pokemon) {
        try {
            // verify that pokemon is in party
            isPokemonInParty(pokemon);
            // remove the pokemon from the party in memory:
            this.pokemon[pokemon.getPartyID()] = null;
            // remove the pokemon from the party in database:
            PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
            pokemonFactory.removePokemonFromParty(pokemon.getPartyID());
        } catch (Exception e) {
            // do something
        }
    }

    /**
     * Add a move to a pokemon in the pokemon party.
     *
     * @param pokemon pokemon in the party to add the move to
     * @param move move to be added to the pokemon
     */
    public void addMoveToPokemon(PartyPokemon pokemon, Move move) {
        try {
            // verify that pokemon is in party
            // and that move is allowed for pokemon
            // and that moveSet is not full
            isPokemonInParty(pokemon);
            pokemon.verifyMoveAllowed(move);
            pokemon.verifyMoveSetFull();

            // add move to moveSet in memory
            pokemon.getMoveSet().addMove(move);
            // add move to the moveSet in database:
            PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
            pokemonFactory.addMoveToPartyPokemon(pokemon.getPartyID(), move);
        } catch (MoveSetFullException e) {
            e.setMove(move);
            // do something
        } catch (Exception e) {
            // do something
        }
    }

    /**
     * Determines if the exact {@link PartyPokemon} is currently in the party.
     *
     * @param pokemon PartyPokemon to verify
     * @throws PokemonNotInPartyException throws an exception when the pokemon in question is not in the party
     */
    private void isPokemonInParty(PartyPokemon pokemon) throws PokemonNotInPartyException {
        // go through the 6 possible spots until the pokemon is found
        for (int i = 0; i < 6; i++) {
            if (this.pokemon[i] == pokemon) {
                return;
            }
        }
        throw new PokemonNotInPartyException(pokemon);
    }

    /**
     * Removes a move from a pokemon in the party.
     *
     * @param pokemon pokemon to remove the move from
     * @param move move to remove
     */
    public void removeMoveFromPokemon(PartyPokemon pokemon, Move move) {
        try {
            // do appropriate verifications:
            isPokemonInParty(pokemon);
            pokemon.verifyMoveLearned(move);

            // remove the move from in memory:
            this.pokemon[pokemon.getPartyID()].getMoveSet().removeMove(move);

            // remove the move from the moveSet in the database:
            PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
            pokemonFactory.removeMoveFromPokemonInParty(pokemon.getPartyID(), move);
        } catch (Exception e) {
            // do something
        }
    }
}
