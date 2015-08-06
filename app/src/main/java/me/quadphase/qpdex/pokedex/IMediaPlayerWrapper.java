package me.quadphase.qpdex.pokedex;

import android.content.res.AssetFileDescriptor;

/**
 * Simplest possible form of MediaPlayer realization. Any class that implements the interface should
 * worry about the particular state of a Media Player object.
 */
public interface IMediaPlayerWrapper {
    /**
     * Checks whether the object is ready to play a sound
     * Call this to avoid interrupting playback when calling playSound().
     */
    public boolean isReady();

    /**
     * Signals the object to update itself with the provided data. It will queue the AssetFileDescriptor
     * if the instance is currently in the middle of playback.
     */
    public void updateInstance(int pokemonNationalID, AssetFileDescriptor pokemonCry);

    /**
     * One shot playback of a sound file. If the instance was created but not updated, it will catch
     * the exception but avoid playback.
     */
    public void playSound();

}
