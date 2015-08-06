package me.quadphase.qpdex.pokedex;

import java.io.InputStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.util.Log;

/**
 * Describes all the paths and values to correctly access and create media elements
 */
//TODO: Document with JavaDocs!
public class PokedexAssetFactory {
    public static final String SPRITE_PATH="sprites";
    public static final String PKMN_SPRITE_PATH="sprites/pokemon";
    public static final String MINI_SPRITE_PATH="sprites/pokemon/mini";
    public static final String TYPE_SPRITE_PATH="sprites/types";
    public static final String SFX_PATH="sound";
    public static final String CRY_PATH="sound/cries";
    public static final String GEN_PREFIX="gen";

    public static final String IMG_FORMAT=".png";
    public static final String SOUND_FORMAT=".ogg";

    public static final String ERROR_IMG = "unknown.png";

    /**
     * Get the ByteStream of the Pokemon Sprite Asset in a particular generation for later loading
     * @param mainContext The context in which this is called
     * @param Generation The specific generation sprite (DOES NOT CHECK IF GENERATION IS VALID)
     * @param PokemonID The National ID of the Pokemon
     */
    public static InputStream getPokemonSpriteInGeneration(Context mainContext, int PokemonID, int Generation){
        //If the generation is within a valid range.
        //TODO: Verify Pokemon within Generation
        boolean validGeneration = Generation > 0 && Generation <= PokedexManager.latestGeneration;
        if (validGeneration) {
            Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);
            assetPath.format("%1$s/%2$s%3$s/%4$s%5$s",
                    PKMN_SPRITE_PATH,
                    GEN_PREFIX,
                    Integer.toString(Generation),
                    Integer.toString(PokemonID),
                    IMG_FORMAT);
            InputStream pkmnSprite=null;
            try{
                pkmnSprite = mainContext.getAssets().open(assetPath.toString());
            }
            catch(java.io.IOException exception){
                Log.e("QPDEX","Problem while loading sprite with path "+assetPath.toString()+"\n"+exception.getMessage());
            }
            return pkmnSprite;
        }
        else {
            Log.e("QPDEX","Invalid Generation Given at getPokemonSpriteInGeneration, returned Null");
            return null;
        }
    }

    /**
     * Get the ByteStream of the Pokemon Sprite Asset  for the latest generation
     * @param mainContext The context in which this is called
     * @param PokemonID The National ID of the Pokemon
     */
    public static InputStream getPokemonSprite(Context mainContext, int PokemonID){
        return getPokemonSpriteInGeneration(mainContext, PokemonID, PokedexManager.latestGeneration);
    }

    //TODO: Change or refactor
    /**
     * Get a HashMap for consulting all of the sprites of a particular Pokemon.
     * @param mainContext The context in which this is called
     * @param PokemonID The National ID of the Pokemon
     */
    public static HashMap<String,InputStream> getPokemonSprites(Context mainContext, int PokemonID){
        HashMap<String,InputStream> allSprites = new HashMap<String,InputStream>();
        int generationFirstAppeared = 1; //TODO: FIX so that the PokemonID can be used to detect and avoid having null values.

        for(int i=generationFirstAppeared; i<= PokedexManager.latestGeneration; i++){
            allSprites.put(Integer.toString(i), getPokemonSpriteInGeneration(mainContext,PokemonID, i));
        }

        //TODO: Use suffix table to load all assets correctly and get alternate sprites too!
//        if(Pokemon.hasSuffix){
//          Do something about loading the other, hyphenated assets.
//        }
        return allSprites;
    }

    /**
     * Retrieve the pointer to a Pokemon's Cry for immediate playback
     * @param mainContext The context in which this is called
     * @param PokemonID The National ID of the Pokemon
     */
    public static AssetFileDescriptor getPokemonCry(Context mainContext,int PokemonID){
        AssetFileDescriptor crySoundFile = null;
        Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);
        assetPath.format("%1$s/%2$s%3$s",
                CRY_PATH,
                Integer.toString(PokemonID),
                SOUND_FORMAT);
        try{
            crySoundFile = mainContext.getAssets().openFd(assetPath.toString());
        }
        catch(java.io.IOException exception){
            Log.e("QPDEX","Problem while loading sound (cry) with path "+assetPath.toString()+"\n"+exception.getMessage());
        }
        return crySoundFile;
    }

    /**
     * Obtain the ByteStream for the Mini Sprite of a given Pokemon
     * @param mainContext The context in which this is called
     * @param PokemonID The National ID of the Pokemon
     */
    public static InputStream getPokemonMinimalSprite(Context mainContext, int PokemonID){
        Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);
        assetPath.format("%1$s/%2$s%3$s",
                MINI_SPRITE_PATH,
                Integer.toString(PokemonID),
                IMG_FORMAT);
        InputStream pkmnSprite=null;
        try{
            pkmnSprite = mainContext.getAssets().open(assetPath.toString());
        }
        catch(java.io.IOException exception){
            Log.e("QPDEX","Problem while loading sprite with path "+assetPath.toString()+"\n"+exception.getMessage());
        }
        return pkmnSprite;
    }

    /**
     * Get the ByteStream for the Type image asset. This method is failsafe and returns a "None" type
     * if it finds the provided typeName is invalid.
     * @param mainContext The context in which this is called
     * @param typeName A Valid type name
     */
    public static InputStream getTypeBadge(Context mainContext, String typeName){
        Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);

        assetPath.format("%1$s/%2$s%3$s",
                TYPE_SPRITE_PATH,
                typeName.toLowerCase(),
                IMG_FORMAT);

        InputStream typeSprite=null;
        try{
            typeSprite = mainContext.getAssets().open(assetPath.toString());
        }
        catch(java.io.IOException exception){
            Log.e("QPDEX","Problem while loading type sprite with path "+assetPath.toString()+"\n"+exception.getMessage());
            Log.e("QPDEX","PokedexAssetFactory defaulting to none type");
            typeSprite = getTypeBadge(mainContext,"none");
        }
        return typeSprite;
    }

}
