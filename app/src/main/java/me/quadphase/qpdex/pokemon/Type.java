package me.quadphase.qpdex.pokemon;


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
//    public short getAttackingEffectivenessAgainst (Type defendingType) {
//        PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
//        return pokemonFactory.getTypeEffectivenessTable()[pokemonFactory.getTypeID(this)][pokemonFactory.getTypeID(defendingType)];
//    }

    /**
     * Determine the defending effectiveness of this type against an attacking one.
     *
     * @param attackingType type that this type is attacking
     * @return the defensive effectiveness against the attackingType
     */
//    public short getDefendingEffectivenessAgainst (Type attackingType) {
//        PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(null);
//        return pokemonFactory.getTypeEffectivenessTable()[pokemonFactory.getTypeID(attackingType)][pokemonFactory.getTypeID(this)];
//    }

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
