package me.quadphase.qpdex.pokedex;

import android.app.Application;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Handles all interaction with the Text-To-Speech resources given by Android. This ensures that the
 * resource is not leaked and can be correctly used within different contexts.
 * This class is a Singleton.
 */
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

    /**
     * Retrieve the Singleton instance of this class for immediate use. It will initialize to a context
     * and be "anchored" to it to prevent resource leak. If called within a different context, it will
     * request the release of the previous ttsEngine and initialize to the new context
     * @param givenContext The context from within which the method is called.
     */
    public static TTSController getOrSetInstance(Context givenContext){
        if(instance==null){
            instance = new TTSController(givenContext);
        }
        else if (givenContext!=useContext){
            instance.releaseEngine();
            instance.requestResource(givenContext);
        }

        return instance;
    }

    /**
     * Updates the instance to read the text provided
     * @param textToSpeak String to be read
     */
    @Override
    public void setText(String textToSpeak) {
        isDirty = ttsEngine.isSpeaking();
        cachedText = textToSpeak;
    }

    /**
     * Checks if the ttsEngine is initialized and updated
     */
    @Override
    public boolean isAvalaible() {
//        boolean canBeUsed = !isDirty && isInitialized;
        return !isDirty && isInitialized ;
    }

    /**
     * One shot call to read text. This uses no queues and will only interrupt itself if the cachedText
     * has chaged and thus the entry being read is no longer valid.
     */
    @Override
    public void speak() {
        if(isInitialized){
            if(isDirty || !ttsEngine.isSpeaking()) {
                ttsEngine.speak(cachedText, TextToSpeech.QUEUE_FLUSH, null);
                isDirty=false;
            }
        }
    }

    /**
     * Forces the controller to release the ttsEngine resources and give them back to the OS
     */
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
