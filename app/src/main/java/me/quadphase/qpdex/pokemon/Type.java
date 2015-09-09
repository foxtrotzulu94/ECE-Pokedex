package me.quadphase.qpdex.pokemon;


import me.quadphase.qpdex.databaseAccess.PokemonFactory;

/** */
public class Type {
    /**
     * name of the type
     * can be either:
     * - normal
     * - fighting
     * - flying
     * - poison
     * - poison
     * - ground
     * - rock
     * - bug
     * - ghost
     * - steel
     * - fire
     * - water
     * - grass
     * - electric
     * - psychic
     * - ice
     * - dragon
     * - dark
     * - fairy
     * - ???
     */
    private String name;

    private int typeID;

    /**
     * Constructor
     */
    public Type(String name, int typeID) {
        this.name = name;
        this.typeID = typeID;
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public int getTypeID() { return typeID;}

    /**
     * Determine the attacking effectiveness of this type against a defending one.
     *
     * @param defendingType type that this type is defending against
     * @return the attacking effectiveness against the defendingType
     */
    public double getAttackingEffectivenessAgainst (Type defendingType) {
        PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
        return pokemonFactory.getTypeEffectivenessTable()[this.typeID][defendingType.getTypeID()];
    }

    /**
     * Determine the defending effectiveness of this type against an attacking one.
     *
     * @param attackingType type that this type is attacking
     * @return the defensive effectiveness against the attackingType
     */
    public double getDefendingEffectivenessAgainst (Type attackingType) {
        PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
        return pokemonFactory.getTypeEffectivenessTable()[attackingType.getTypeID()][this.typeID];
    }

    /**
     * Get the number of types in the game.
     *
     * @return int of the number of types possible, excluding the invalid bird/none type
     */
    public static int getNumberOfTypes() {
        return PokemonFactory.getPokemonFactory(null).getMaxTypeID();
    }

    /**
     * Gets an array of all possible types (including bird/none type)
     * @return array of all possible types (including bird/none type)
     */
    public static Type[] getListOfTypes() {
        return PokemonFactory.getPokemonFactory(null).getListOfTypes();
    }


    /**
     * Overide for advanced search to display the id and name of type
     *
     * @return id. TypeName in string format
     */
    @Override
    public String toString(){
        return String.format("%s",name);
    }
}

