package me.quadphase.qpdex.pokedex;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.widget.ArrayAdapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import me.quadphase.qpdex.pokemon.Pokemon;

/**
 * High level manager of all Pokemon related information
 * This class is a singleton that should be use when interaction is needed with activities.
 */
public class PokedexManager {
    //TODO: Set through Database retrieval for MAX generation.
    public static final int latestGeneration = 6;
    private static int countMaxNationalID = 721;

    private static PokedexManager instance = null;

    //Factory and Object Access Variables
    /**
     * Object in charge of creating Pokemon through calls to Database Accessor.
     */
    private PokemonFactory generator;

//      TODO: scrap. PokedexAssetFactory is now static.
//    /**
//     * Object in charge of creating Pokemon through calls to Database Accessor.
//     */
//    private PokedexAssetFactory assetStore;

    /**
     * Wrapper for Android's Media Class
     */
    private CentralAudioPlayer jukebox;


    //Lists and Collection variables
    /**
     * List of the retrieved database objects in minimal form for quick reference
     * @see this.pokemonList for the list sent to the ListView
     */
    private List<String> minimalPokemonObjects;

    /**
     * List for display purposes with ListAdapter
     */
    private ArrayAdapter<String> viewablePokemonList;


    //Information and Cached Pokemon Variables for quick loading
    /**
     * Current generation being viewed. In case we want the Pokedex to go back in time
     * As a safety measure, it always defaults to the latest generation.
     */
    private int generationCurrentlyDisplayed = latestGeneration;

    /**
     * Reference to the current pokemon being viewed or focused
     */
    private Pokemon selectedPokemon;

    /**
     * Cached reference for the Pokemon Cry file
     */
    private AssetFileDescriptor pokemonCry; //TODO: probably moved to CentralAudioPlayer

    /**
     * HashMap to access the sprite of the pokemon for a given generation
     * Will be a single mapped value if there is only one.
     */
    private HashMap<String,InputStream> pokemonSpriteList;

    //Constructor and Instance Access for Singleton
    protected PokedexManager(){
        jukebox = CentralAudioPlayer.getInstance();
        //TODO: fill with the rest of the needed fields
//        this.generator = generator;
//        this.assetStore = assetStore;
//        this.minimalPokemonObjects = minimalPokemonObjects;
//        this.viewablePokemonList = viewablePokemonList;
//        this.generationDisplayed = generationDisplayed;
//        this.selectedPokemon = selectedPokemon;
//        this.pokemonCry = pokemonCry;
//        this.pokemonSpriteList = pokemonSpriteList;
    }

    public static PokedexManager getInstance(){
        if(instance==null){
            instance = new PokedexManager();
        }
        return instance;
    }


    //Private methods for interacting with other classes in the package
    /**
     * Get the Current Pokemon Cry from the Asset Directory
     */
    private void retrievePokemonCryFromAssets(){
        //TODO: load the cry from the assets
    }


    //Setters and Getters
    /**
     * Return the ArrayAdapter for use in showing it through a ListView
     */
    public ArrayAdapter<String> getViewablePokemonList() {
        return viewablePokemonList;
    }

    /**
     * Get the Minimal Pokemon Objects. Not recommended for direct use.
     * @see this.getPokemonList()
     */
    public List<String> getMinimalPokemonObjects() {
        return minimalPokemonObjects;
    }

    /**
     * Place a specific list of Minimal Pokemon Objects
     * Useful for searching by generation.
     */
    public void setMinimalPokemonObjects(List<String> minimalPokemonObjects) {
        this.minimalPokemonObjects = minimalPokemonObjects;
    }

    /**
     * Retrieve the Built Pokemon Object being focused
     * {@link me.quadphase.qpdex.DetailedPokemonActivity} should use this for getting all info.
     */
    public Pokemon getSelectedPokemon() {
        return selectedPokemon;
    }

    /**
     * Indicate what Pokemon is being viewed
     */
    public void setSelectedPokemon(Pokemon selectedPokemon) { //TODO: switch to MinimalPokemon
        this.selectedPokemon = selectedPokemon;
    }

    /**
     * Retrieve the selected Pokemon's Cry
     * @see this.retrievePokemonCryFromAssets() for how the actual asset is fetched.
     */
    public AssetFileDescriptor getCurrentPokemonCry() {
        return pokemonCry;
    }

    /**
     * Get the Current Pokemon Cry from the Asset Directory
     *
     */
    public InputStream getPokemonLatestSprite(){
        //TODO: load the cry from the assets
        return pokemonSpriteList.get("6");
    }


    /**
     * Retrieve the HashMap containing all Pokemon Sprites
     */
    public HashMap<String, InputStream> getPokemonSpriteList() {
        return pokemonSpriteList;
    }

    /**
     * Retrieve a general sprite for the Pokemon within a Generation.
     * Returns null if the generation is not valid.
     */
    public InputStream getPokemonSpriteForGeneration(int genID){
        //Note that the query on the Dictionary is restricted to Generation.
        return pokemonSpriteList.get(Integer.toString(genID));
    }
}
