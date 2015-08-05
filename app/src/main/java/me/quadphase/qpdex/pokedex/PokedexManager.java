package me.quadphase.qpdex.pokedex;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.widget.ArrayAdapter;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.quadphase.qpdex.IntroActivity;
import me.quadphase.qpdex.pokedex.CentralAudioPlayer;
import me.quadphase.qpdex.pokemon.MinimalPokemon;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * High level manager of all Pokemon related information
 * This class is a singleton that should be use when interaction is needed with activities.
 */
public class PokedexManager {

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
            return new MinimalPokemon(getNationalID(), super.getName(), super.getDescription(), super.getTypes());
        }

    }

    //Entity variables that describe inner state and function
    private static PokedexManager instance=null;
    private static CentralAudioPlayer jukebox=null;
    private static TTSController roboVoice;
    private boolean isReady=false;


    //Variables for context to handle global application state
    public static final int latestGeneration = 6;

    public final MissingNo missingNo;

    private int maxPokemonNationalID = 721;

    private int restrictUpToGeneration = latestGeneration; //In practice, no restriction.

    private int currentPokemonNationalID = 0;

    private Pokemon currentDetailedPokemon;

    private MinimalPokemon currentMinimalPokemon;

    private InputStream currentOverviewSprite;

    private InputStream currentType1;

    private InputStream currentType2;

    //Collections to assist the Pokedex display
    private Type[] allValidTypes;

    private MinimalPokemon[] allMinimalPokemon;

    private InputStream[] allMiniSprites;

    //private ArrayAdapter<MinimalPokemon> pokedexList; //Should probably stay with PokedexActivity class

    //Cache variables
    //private int cacheSize;
    //private HashMap<String,Pokemon> cachedDetailedPokemon; //Tentative, use an LRUHashMap
    //private List<InputStream> cachedDisplaySprites;

    //Methods

    //Singleton Constructor
    protected PokedexManager(){
        jukebox = CentralAudioPlayer.getInstance();
        //roboVoice = TTSController.getInstance();
        missingNo = new MissingNo();
    }

    public static PokedexManager getInstance(){
        if(instance==null){
            instance = new PokedexManager();
        }
        return instance;
    }

    public void updatePokedexSelection(MinimalPokemon pokedexSelection, Context currentContext){
        isReady = false;
        currentMinimalPokemon = pokedexSelection;
        currentPokemonNationalID = pokedexSelection.getNationalID();

        jukebox.updateInstace(currentPokemonNationalID, PokedexAssetFactory.getPokemonCry(currentContext, currentPokemonNationalID));
        currentOverviewSprite = PokedexAssetFactory.getPokemonSpriteInGeneration(currentContext,currentPokemonNationalID,restrictUpToGeneration);
        currentType1 = PokedexAssetFactory.getTypeBadge(currentContext, pokedexSelection.getTypes().get(0).getName());
        currentType2 = PokedexAssetFactory.getTypeBadge(currentContext, pokedexSelection.getTypes().get(1).getName());
        //roboVoice.setText(pokedexSelection.getDescription());

        //Can also prepare for Full Pokemon Object Construction here (i.e. spawn a worker thread)

        isReady = true;
    }

    //Getters and Setters
    public boolean isReady() {
        return isReady;
    }

    public MinimalPokemon getCurrentMinimalPokemon() {
        return currentMinimalPokemon;
    }

    public InputStream getSelectionOverviewSprite(){
        return currentOverviewSprite;
    }

    public InputStream getCurrentType1() {
        return currentType1;
    }

    public InputStream getCurrentType2() {
        return currentType2;
    }

    public InputStream getCurrentOverviewSprite() {
        return currentOverviewSprite;
    }
}
