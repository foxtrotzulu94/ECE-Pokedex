package me.quadphase.qpdex.pokedex;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Handles all interaction related to Android's MediaPlayer Class to server Pokemon Cries correctly
 * This class is a Singleton that gets instantiated by the {@link PokedexManager}
 */
//TODO: Document with JavaDocs!
public class CentralAudioPlayer implements IMediaPlayerWrapper {
    //Internal Class
    protected class CompletedPlayback implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mediaPlayer){
            //Go back to the begginning of the track if nothing has changed.
            isPlaying = false;
            if(isDirty){
                mediaPlayer.reset();
                setNewPokemonCry();
            }
            else{
                mediaPlayer.seekTo(0);
            }
        }
    }

    //All Private Variables

    private static CentralAudioPlayer instance = null;

    private MediaPlayer androidMP;
    private AssetFileDescriptor cachedAudioFile=null;
    private int cachedNationalID =0;
    private boolean isDirty=true;
    private boolean isPlaying=false;


    //Constructor and Instance Access for Singleton
    protected CentralAudioPlayer(){
        androidMP = new MediaPlayer();
        androidMP.setOnCompletionListener(new CompletedPlayback());
    }

    public static CentralAudioPlayer getInstance(){
        if(instance==null){
            instance = new CentralAudioPlayer();
        }
        return instance;
    }

    private void setNewPokemonCry(){
        try {
            androidMP.setDataSource(cachedAudioFile.getFileDescriptor(),
                    cachedAudioFile.getStartOffset(),
                    cachedAudioFile.getLength());
        }
        catch (java.io.IOException exception){
            Log.e("QPDEX",exception.getMessage()+"\nCentral Media Player is Dirty and needs to Update Instance");
            isDirty = true;
            isPlaying = false;
        }
    }


    //Methods for behaviour
    @Override
    public void updateInstace(int pokemonNationalID, AssetFileDescriptor pokemonCry){
        cachedNationalID = pokemonNationalID;
        cachedAudioFile = pokemonCry;
        if (!isPlaying) {
            setNewPokemonCry();
        }
        else
        {
            isDirty = true;
        }
    }

    @Override
    public boolean isReady(){
        //TODO: Safeguard by opening a default cry and remove the cache params
        return (cachedAudioFile!=null && cachedNationalID !=0 && !isDirty && !isPlaying);
    }

    @Override
    public void playSound(){
        if (!isPlaying) {
            isPlaying = true;
            try {
                androidMP.prepare();
                androidMP.start();
                //isPlaying is set again as False by the onCompletion method.
            }
            catch (java.io.IOException exception){
                Log.e("QPDEX",exception.getMessage());
                isDirty = true;
                isPlaying = false;
            }
        }
        //This allow a really rapid fire rate of cries, similar to the Actual Pokedex.
        //But it might cause some nasty bugs.
        else{
            androidMP.reset();
            setNewPokemonCry();
            isPlaying = false;
            playSound();
        }
    }

}