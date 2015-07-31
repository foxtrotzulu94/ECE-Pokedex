package me.quadphase.qpdex;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

import me.quadphase.qpdex.pokedex.CentralAudioPlayer;
import me.quadphase.qpdex.pokedex.PokedexAssetFactory;


public class PokedexActivity extends AppCompatActivity {

//    AssetFileDescriptor afd;
    CentralAudioPlayer testy;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        Runtime.getRuntime().gc();
        System.gc();

        testy = CentralAudioPlayer.getInstance();
//        try{
//            afd = getAssets().openFd("1.ogg");
//            testy.updateInstace(1,afd);
//        }
//        catch(Exception e){
//            Log.e("QPDEX","Exception Occured!"+e.getMessage());
//        }



        //Set up Buttons
        Button cryButton = (Button)findViewById(R.id.button_pkmncry);
        if(!cryButton.hasOnClickListeners()) {
            cryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPokemonCry();
            }
        });
        }

        //Set up Search Bar (EditText)
        final EditText nameAndSearch = (EditText)findViewById(R.id.edittext_pkmnname);
        if(!nameAndSearch.hasOnClickListeners()){
            //Set the OnClick Listener
            nameAndSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Clear the Pokemon Name immediately.
                    nameAndSearch.setText("");
                }
            });

            //Piggyback and also set the OnFocusChange Method
            nameAndSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        //Take the keyboard away
                        //TODO: Has a lot of refinement to be done!
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }


        //Fill the list
        ListView pokedexList = (ListView) findViewById(R.id.listv_pkdexentries);
        ArrayAdapter<String> pokedexEntries = new ArrayAdapter<String>(
                this,
                R.layout.pokedexrow,
                R.id.textview_pkmn_list_entry,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                });
        pokedexList.setAdapter(pokedexEntries);
        pokedexList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("QPDex","List view touched");
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pokedex, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToPokemonData(View view){
        //TODO: Add info on the specific pokemon being viewed
        Intent intent = new Intent(this,DetailedPokemonActivity.class);
        startActivity(intent);
    }

    public void playPokemonCry(){
        Log.w("QPDEX","Playing Sound");

        testy.updateInstace(249, PokedexAssetFactory.getPokemonCry(this,249));
        testy.playSound();

    }

    public void saySomething(View view){
        //TODO: Move elsewhere similar to CentralMediaPlayer so that resources don't leak!
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{
                        String text = "Lugia's wings pack devastating power. A light fluttering of its wings can blow apart regular houses. As a result, this Pokémon chooses to live out of sight deep under the sea.";
                        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });

    }
}
