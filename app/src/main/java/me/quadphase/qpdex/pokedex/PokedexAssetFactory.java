package me.quadphase.qpdex.pokedex;

import java.io.InputStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

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


    public static InputStream getPokemonSpriteInGeneration(int PokemonID, int Generation){
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
                pkmnSprite = Resources.getSystem().getAssets().open(assetPath.toString());
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

    public static InputStream getPokemonSprite(int PokemonID){
        return getPokemonSpriteInGeneration(PokemonID, PokedexManager.latestGeneration);
    }

    public static HashMap<String,InputStream> getPokemonSprites(int PokemonID){
        HashMap<String,InputStream> allSprites = new HashMap<String,InputStream>();
        int generationFirstAppeared = 1; //TODO: FIX so that the PokemonID can be used to detect and avoid having null values.

        for(int i=generationFirstAppeared; i<= PokedexManager.latestGeneration; i++){
            allSprites.put(Integer.toString(i), getPokemonSpriteInGeneration(PokemonID, i));
        }

        //TODO: Use suffix table to load all assets correctly and get alternate sprites too!
//        if(Pokemon.hasSuffix){
//          Do something about loading the other, hyphenated assets.
//        }
        return allSprites;
    }

    public static AssetFileDescriptor getPokemonCry(int PokemonID){
        AssetFileDescriptor crySoundFile = null;
        Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);
        assetPath.format("%1$s/%2$s%3$s",
                CRY_PATH,
                Integer.toString(PokemonID),
                SOUND_FORMAT);
        try{
            crySoundFile = Resources.getSystem().getAssets().openFd(assetPath.toString());
        }
        catch(java.io.IOException exception){
            Log.e("QPDEX","Problem while loading sound (cry) with path "+assetPath.toString()+"\n"+exception.getMessage());
        }
        return crySoundFile;
    }

    //TODO: Implement
    public static InputStream getPokemonMinimalSprite(int PokemonID){
        Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);
        assetPath.format("%1$s/%2$s%3$s",
                MINI_SPRITE_PATH,
                Integer.toString(PokemonID),
                IMG_FORMAT);
        InputStream pkmnSprite=null;
        try{
            pkmnSprite = Resources.getSystem().getAssets().open(assetPath.toString());
        }
        catch(java.io.IOException exception){
            Log.e("QPDEX","Problem while loading sprite with path "+assetPath.toString()+"\n"+exception.getMessage());
        }
        return pkmnSprite;
    }


    public static InputStream getTypeBadge(String typeName){
        //TODO: Check if valid type string
        Formatter assetPath = new Formatter(new StringBuilder(), Locale.US);

        assetPath.format("%1$s/%2$s%3$s",
                TYPE_SPRITE_PATH,
                typeName.toLowerCase(),
                IMG_FORMAT);

        InputStream typeSprite=null;
        try{
            typeSprite = Resources.getSystem().getAssets().open(assetPath.toString());
        }
        catch(java.io.IOException exception){
            Log.e("QPDEX","Problem while loading type sprite with path "+assetPath.toString()+"\n"+exception.getMessage());
        }
        return typeSprite;
    }

}
