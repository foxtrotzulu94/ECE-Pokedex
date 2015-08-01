package me.quadphase.qpdex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import me.quadphase.qpdex.pokedex.CentralAudioPlayer;
import me.quadphase.qpdex.pokedex.PokedexAssetFactory;


public class PokedexActivity extends AppCompatActivity {

    EditText inputSearch;
    ListView pokedexList;
    ArrayAdapter<String> pokedexEntries;
    CentralAudioPlayer audioPlayer;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        Runtime.getRuntime().gc();
        System.gc();

        //Retrieve all Variables for setup
        audioPlayer = CentralAudioPlayer.getInstance();
        inputSearch = (EditText) findViewById(R.id.edittext_pkmnname);
        pokedexList = (ListView) findViewById(R.id.listv_pkdexentries);

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

        //Fill the list
        pokedexEntries = new ArrayAdapter<String>(
                this,
                R.layout.pokedexrow,
                R.id.textview_pkmn_list_entry,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                        "Section 3",
                        "Section 4",
                        "Section 5",
                        "Section 6",
                        "Section 7",
                        "Section 8",
                        "Section 9",
                        "Section 10"

                });

        //Set up Search Bar (EditText)

        if(!inputSearch.hasOnClickListeners()){
            //Set the OnClick Listener
            inputSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Clear the Pokemon Name immediately.
                    //TODO: Make sure the selection in the list always follows the pokemon in the list view
//                    inputSearch.setText("");
                }
            });

            //Piggyback and also set the OnFocusChange Method
            inputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        pokedexList.setAdapter(pokedexEntries);
        pokedexList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("QPDex", "List view touched");
                PokedexActivity.this.inputSearch.setHint((String) parent.getItemAtPosition(position));
                PokedexActivity.this.inputSearch.clearFocus();
                //NOTE: This current behaviour is interesting because it resets the Search bar
                //      in a way that might be frustrating for the end user. Careful with this....
//                pokedexEntries.getFilter().filter("");

//                inputSearch.setText("");
//                Toast.makeText(getApplicationContext(),
//                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
//                        .show();
            }
        });


        //Setup the filter
        this.inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence a, int b, int c, int d) {
//                pokedexEntries.getFilter().filter(a);
//                pokedexEntries.notifyDataSetChanged();
                //TODO: fix the pokedex list an offset if the CharSequence is empty
                //This seems to be acting on the old list rather than the new, empty filter list.
                if(a.toString().isEmpty()){
                    //TODO: extract these methods to avoid ugly, anonymous methods
                    pokedexEntries.getFilter().filter(a, new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int count) {
                            Log.d("QPDEX", "Resetting" + Integer.toString(pokedexList.getSelectedItemPosition()));
                            pokedexList.setSelection(pokedexList.getCount()-1); //TODO: Set the offset here
                        }
                    });

                }
                else{
                    pokedexEntries.getFilter().filter(a);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence a, int b, int c, int d) {

            }

            @Override
            public void afterTextChanged(Editable e) {

            }
        });


        TextView description = (TextView) findViewById(R.id.textview_pkmndescript);
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
                        //TODO: Do more setup if success!
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
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
        tts.stop();
        tts.shutdown();
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
        //TODO: Also, notify that a full pokemon has to be constructed!
        Intent intent = new Intent(this,DetailedPokemonActivity.class);
        startActivity(intent);
    }

    public void playPokemonCry(){
        Log.w("QPDEX","Playing Sound");
        audioPlayer.updateInstace(0, PokedexAssetFactory.getPokemonCry(this, 32));
        audioPlayer.playSound();
    }

    public void saySomething(View view){
        inputSearch.clearFocus();
        //TODO: Move elsewhere similar to CentralMediaPlayer so that resources don't leak!
        TextView description = (TextView) findViewById(R.id.textview_pkmndescript);
        tts.speak(description.getText().toString(), TextToSpeech.QUEUE_ADD, null);
    }
}
