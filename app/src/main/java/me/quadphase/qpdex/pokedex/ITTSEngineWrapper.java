package me.quadphase.qpdex.pokedex;

/**
 * Straightforward Interface to define interaction with Android's built-in Text-To-Speech Engine
 */
public interface ITTSEngineWrapper {
    /**
     * Sets the string that the TTS Engine should speak next when calling {@link this.speak()}
     */
    public void setText(String textToSpeak);

    /**
     * Indicates if this engine instance has a valid string, was initialized correctly and the speak
     * method can be called reliably. Check this to avoid queueing the same text if calling from an
     * OnClick method.
     */
    public boolean isAvalaible();

    /**
     * Play the string which was previously set. If no string is set, the wrapper prevents playback.
     */
    public void speak();

    /**
     * Return the TTS Engine resources being used to Android and allow them to be garbage collected.
     * Do not release the engine while the application is running! Doing so incurs in delays when
     * calling the speak() method.
     */
    public void releaseEngine();

}
