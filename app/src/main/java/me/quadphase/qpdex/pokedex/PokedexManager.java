package me.quadphase.qpdex.pokedex;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.quadphase.qpdex.pokemon.MinimalPokemon;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * High level manager of all Pokemon related information
 * This class is a singleton that should be use when interaction is needed with activities.
 */
public class PokedexManager {

    /**
     * Internal class to generate a fail-safe Pokemon.
     * Can be used to check for other errors in logic or in application UI.
     * If this is ever displayed, then there's a problem that needs to be fixed
     */
    public class MissingNo extends Pokemon{
        public MissingNo(){
            super(
                    -1,                  // pokemonUniqueID,
                    0,                   // pokemonNationalID,
                    "MissingNo.",        // name,
                    "Ketsuban",// description,
                    3.3,                 // height in Meters,
                    1590.8,              // weight in Kilograms,
                    136,                 // attack,
                    0,                   // defence,
                    33,                  // hp,
                    3,                   // spAttack,
                    3,                   // spDefence,
                    29,                  // speed,
                    false,               // caught,
                    1,                   // genFirstAppeared,
                    0,                   // hatchTime,
                    29,                  // catchRate,
                    -1,                  // genderRatioMale,
                    null,                // locations,
                    null,                // abilities,
                    null,                // moves,
                    Arrays.asList(       //types
                            new Type("Bird","Invalid Type"),
                            new Type("Normal","Normal")),
                    null,                // eggGroups,
                    null                 // evolutions,
            );
        }

        public MinimalPokemon minimal(){
            return new MinimalPokemon(getNationalID(), super.getName(), super.getDescription(), super.getTypes(), false);
        }

    }

    private class M00 extends Pokemon{
        Random rng;
        public M00(){
            super(
                    -1,                  // pokemonUniqueID,
                    0,                   // pokemonNationalID,
                    "M'(00)",        // name,
                    "Often called the \"sister\" glitch counterpart to Missingno. due to having the same sprite and Pokédex number, and is found exclusively in Pokémon Red and Blue. If RBGlitchName00.png is traded to Pokémon Yellow, it will become a 3TrainerPoké $.\n" +
                            "\n" +
                            "Although similar to Missingno. at first glance, the two are separate glitch Pokémon with many differences as they have different index numbers; for example, RBGlitchName00.png can evolve into Kangaskhan while Missingno. cannot.",
                            // description,
                    0,                 // height in Meters,
                    0,              // weight in Kilograms,
                    0,                 // attack,
                    0,                   // defence,
                    0,                  // hp,
                    0,                   // spAttack,
                    0,                   // spDefence,
                    0,                  // speed,
                    false,               // caught,
                    1,                   // genFirstAppeared,
                    0,                   // hatchTime,
                    29,                  // catchRate,
                    -1,                  // genderRatioMale,
                    null,                // locations,
                    null,                // abilities,
                    null,                // moves,
                    Arrays.asList(       //types
                            new Type("Bird","Invalid Type"),
                            new Type("Normal","Normal")),
                    null,                // eggGroups,
                    null                 // evolutions,
            );
            rng = new Random();
            rng.setSeed(System.nanoTime());
        }

        public MinimalPokemon minimal(){
            return new MinimalPokemon(getNationalID(), super.getName(), super.getDescription(), super.getTypes(), false);
        }

        @Override
        public int retrieveStatFromString(String specific){
            return rng.nextInt(200);
        }

    }

    //Entity variables that describe inner state and function
    private static PokedexManager instance=null;
    private static CentralAudioPlayer jukebox=null;
    private static TTSController roboVoice;
    private boolean isReady=false;
    private boolean isDetailed=false;


    //Variables for context to handle global application state
    /**
     * The Generation when this Pokedex was updated/compiled
     */
    public static final int latestGeneration = 6;
    /**
     * Instance of the fail-safe class
     */
    public final MissingNo missingNo;

    private int maxPokemonNationalID = 721;

    private int restrictUpToGeneration = latestGeneration; //In practice, no restriction.

    private int currentPokemonNationalID = 0;

    private Pokemon currentDetailedPokemon;

    private MinimalPokemon currentMinimalPokemon;

    private BitmapDrawable currentOverviewSprite;

    private ArrayList<BitmapDrawable> allOverviewSprites;

    private BitmapDrawable currentType1;

    private BitmapDrawable currentType2;


    //Collections to assist the Pokedex display

    private Type[] allValidTypes;

    private MinimalPokemon[] allMinimalPokemon;

    private InputStream[] allMiniSprites;

    //private ArrayAdapter<MinimalPokemon> pokedexList; //Should probably stay with PokedexActivity class

    //Cache variables
    //private int cacheSize;
    //private HashMap<String,Pokemon> cachedDetailedPokemon; //Tentative, use an LRUHashMap
    private List<BitmapDrawable> cachedDisplaySprites;

    //Methods

    //Singleton Constructor
    protected PokedexManager(){
        jukebox = CentralAudioPlayer.getInstance();
        //roboVoice = TTSController.getInstance();
        missingNo = new MissingNo();
        //TODO: REMOVE! testing ONLY!
        currentDetailedPokemon = new M00();
    }

    /**
     * Retrieve the Singleton Instance
     */
    public static PokedexManager getInstance(){
        if(instance==null){
            instance = new PokedexManager();
        }
        return instance;
    }

    //TODO: Implement!
    private void updateDetailedPokemon(){

        //Finally, say we're done.
        isDetailed = true;
    }

    /**
     * Change the currently selected Pokemon in the Pokedex and send a message to update all classes
     * This will also store a reference to the assets the Pokemon with the National ID is associated with.
     * @param pokedexSelection The minimal pokemon, preferrably from the {@link PokedexArrayAdapter}
     * @param currentContext The context in which the update occurs (usually, "this" within an Activity)
     */
    public void updatePokedexSelection(MinimalPokemon pokedexSelection, final Context currentContext){
        isReady = false;
        isDetailed = false;
        currentMinimalPokemon = pokedexSelection;
        currentPokemonNationalID = pokedexSelection.getNationalID();

        //Update Media Controller
        jukebox.updateInstance(currentPokemonNationalID, PokedexAssetFactory.getPokemonCry(currentContext, currentPokemonNationalID));

        //Update Graphics Assets
        //Load Sprite
        currentOverviewSprite = new BitmapDrawable(currentContext.getResources(),
                PokedexAssetFactory.getPokemonSpriteInGeneration(currentContext,currentPokemonNationalID,restrictUpToGeneration));

        //Load first type
        currentType1 = new BitmapDrawable(currentContext.getResources(),
                PokedexAssetFactory.getTypeBadge(currentContext, pokedexSelection.getTypes().get(0).getName()));
        if(pokedexSelection.getTypes().size()>1) {
            //Load second type (if any)
            currentType2 = new BitmapDrawable(currentContext.getResources(),
                    PokedexAssetFactory.getTypeBadge(currentContext, pokedexSelection.getTypes().get(1).getName()));
        }
        else{
            currentType2 = new BitmapDrawable(currentContext.getResources(),
                    PokedexAssetFactory.getTypeBadge(currentContext,"empty"));
        }
        //Set the description to be heard
        //roboVoice.setText(pokedexSelection.getDescription());

        //Spawn a thread to collect overview sprites
        Thread bitmapRetrieve = new Thread(){
            @Override
            public void run(){
                cachedDisplaySprites = new LinkedList<BitmapDrawable>();
                for (int i = 1; i <= latestGeneration; i++) {
                    InputStream file = PokedexAssetFactory.getPokemonSpriteInGeneration(
                            currentContext,
                            currentMinimalPokemon.getNationalID(),
                            i);
                    BitmapDrawable sprite = new BitmapDrawable(
                            currentContext.getResources(),
                            file
                            );
                    if(file!=null)
                        cachedDisplaySprites.add(sprite);
                }
            }
        };
        bitmapRetrieve.start();

        //TODO:
        //Can also prepare for Full Pokemon Object Construction here (i.e. spawn a worker thread)
//        currentDetailedPokemon = this.missingNo;

        isReady = true;
    }

    /**
     * Change the currently selected Pokemon in the Pokedex, mainly in the DetailedPokemonActivty
     * This will also store a reference to the assets the Pokemon with the National ID is associated with.
     * @param detailedPokemon The fully detailed pokemon. Should be given from a Pokemon's evolution list
     * @param currentContext The context in which the update occurs (usually, "this" within an Activity)
     */
    public void updatePokedexSelection(Pokemon detailedPokemon, Context currentContext){
        //For now, it just sets this variable. Later it should probably do more in terms of assets
        currentDetailedPokemon = detailedPokemon;
    }

    //Getters and Setters
    /**
     * Check if the object is ready and can be called
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Retrieve a reference to the MinimalPokemon currently loaded
     */
    public MinimalPokemon getCurrentMinimalPokemon() {
        return currentMinimalPokemon;
    }
    /**
     * Retrieve a reference to the MinimalPokemon currently loaded
     */
    public Pokemon getCurrentDetailedPokemon() {
        return currentDetailedPokemon;
    }
    /**
     * Get the currentMinimalPokemon's Sprite
     */
    public BitmapDrawable getSelectionOverviewSprite(){
        return currentOverviewSprite;
    }
    /**
     * Get the currentMinimalPokemon's 1st Type image badge
     */
    public BitmapDrawable getCurrentType1() {
        return currentType1;
    }
    /**
     * Get the currentMinimalPokemon's 2nd Type image badge. May return a transparent image if no 2nd type
     */
    public BitmapDrawable getCurrentType2() {
        return currentType2;
    }
    /**
     * Get the list of all Sprites valid for the current pokemon.
     */
    public List<BitmapDrawable> getAllDetailedPokemonSprites() {
        return cachedDisplaySprites;
    }


}
