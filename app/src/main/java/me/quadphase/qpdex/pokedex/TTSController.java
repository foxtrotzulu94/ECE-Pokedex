package me.quadphase.qpdex.pokedex;

import android.speech.tts.TextToSpeech;

/**
 * Created by Javier Fajardo on 8/2/2015.
 */
public class TTSController implements ITTSEngineWrapper {

    private static TTSController instance=null;

    private boolean isDirty;
    private boolean isSpeaking;
    private String cachedText;
    private int cachedNationalID=0;
    private TextToSpeech ttsEngine;

    @Override
    public void setText(String textToSpeak) {

    }

    @Override
    public boolean isAvalaible() {
        return false;
    }

    @Override
    public void speak() {

    }

    @Override
    public void releaseEngine() {

    }
}
