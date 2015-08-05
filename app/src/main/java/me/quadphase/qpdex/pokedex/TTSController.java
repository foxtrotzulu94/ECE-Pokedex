package me.quadphase.qpdex.pokedex;

import android.app.Application;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Javier Fajardo on 8/2/2015.
 */
//TODO: Document with JavaDocs
public class TTSController implements ITTSEngineWrapper {

    private static TTSController instance=null;
    private static Context useContext;

    private boolean isDirty=true;
    private boolean isInitialized=false;
    private String cachedText;
    private int cachedNationalID=0;
    private TextToSpeech ttsEngine;

    protected TTSController(Context givenContext){
        requestResource(givenContext);
    }

    protected void requestResource(Context givenContext){
        useContext = givenContext;
        ttsEngine = new TextToSpeech(useContext, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result=ttsEngine.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("QPDex", "TTSController: This Language is not supported");
                    }
                    else{
                        //TODO: Do more setup if success!
                        isInitialized = true;
                    }
                }
                else {
                    Log.e("QPDex", "TTSController: Initilization Failed!");

                }
            }
        });
    }

    public static TTSController getOrSetInstance(Context givenContext){
        if(instance==null){
            instance = new TTSController(givenContext);
        }
        else{
            instance.releaseEngine();
            instance.requestResource(givenContext);
        }

        return instance;
    }

    @Override
    public void setText(String textToSpeak) {
        isDirty = ttsEngine.isSpeaking();
        cachedText = textToSpeak;
    }

    @Override
    public boolean isAvalaible() {
//        boolean canBeUsed = !isDirty && isInitialized;
        return !isDirty && isInitialized ;
    }

    @Override
    public void speak() {
        if(isInitialized){
            if(isDirty || !ttsEngine.isSpeaking()) {
                ttsEngine.speak(cachedText, TextToSpeech.QUEUE_FLUSH, null);
                isDirty=false;
            }
        }
    }

    @Override
    public void releaseEngine() {
        if (ttsEngine!=null) {
            ttsEngine.stop();
            ttsEngine.shutdown();
            ttsEngine = null;
        }
        useContext = null;

        isDirty = true;
        isInitialized = false;

        //Advise the system it would be a good time for garbage collection.
        System.gc();
    }
}
