package me.quadphase.qpdex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.pokedex.CentralAudioPlayer;
import me.quadphase.qpdex.pokedex.PokedexArrayAdapter;
import me.quadphase.qpdex.pokedex.PokedexAssetFactory;
import me.quadphase.qpdex.pokedex.PokedexManager;
import me.quadphase.qpdex.pokedex.TTSController;
import me.quadphase.qpdex.pokemon.MinimalPokemon;


public class PokedexActivity extends AppCompatActivity {

    private class PokedexSingleClickListener implements AdapterView.OnItemClickListener{
        private int selectedItemIndex=0;
        private long doubleClickTime=0;
        private long doubleClickDelay=500;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("QPDex", "List view touched");
            Log.d("QPDEX", String.format("%s %s", pokedexListView.getSelectedItemPosition(), pokedexListView.getCheckedItemPosition()));
            PokedexActivity.this.inputSearch.setHint(parent.getItemAtPosition(position).toString());
            PokedexActivity.this.inputSearch.clearFocus();

            //We can now get the minimal Object and do things from here
            MinimalPokemon retrieved = (MinimalPokemon) parent.getItemAtPosition(position);
            contextMaster.updatePokedexSelection(retrieved, getApplicationContext(), false);
            dexVoice.setText(retrieved.getDescription());
            refreshPokedexOverviewPanel();

            if(selectedItemIndex == pokedexListView.getCheckedItemPosition()){
                if(System.currentTimeMillis() <= doubleClickDelay+doubleClickTime){
                    Log.d("QPDex", "List view double click fired.");
                    switchToPokemonData(view);
                }
            }

            selectedItemIndex = pokedexListView.getCheckedItemPosition();
            doubleClickTime = System.currentTimeMillis();

        }
    }

    private class PokedexLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,int pos, long id) {
            pokedexListView.performItemClick(v,pos,id);
            switchToPokemonData(v);
            return true;
        }
    }

    private class PokedexSearchBarWatch implements TextWatcher{
        @Override
        public void onTextChanged(CharSequence a, int b, int c, int d) {
            if(a.toString().isEmpty()){
                pokedexEntries.getFilter().filter(a, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        Log.d("QPDEX", "Resetting" + Integer.toString(pokedexListView.getSelectedItemPosition()));
                        //Set a new selection here if necessary
                    }
                });
            }
            else{
                pokedexEntries.getFilter().filter(a);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence a, int b, int c, int d) {
            //No Override
        }

        @Override
        public void afterTextChanged(Editable e) {
            //No Override
        }
    }


    //Application global references
    private CentralAudioPlayer audioPlayer;
    private PokedexManager contextMaster;
    private TTSController dexVoice;

    //UI Elements
    EditText inputSearch;
    ListView pokedexListView;
    ImageButton overviewImage;
    TextView overviewDescription;
    ImageView overviewType1;
    ImageView overviewType2;


    //Assistant Containers
    ArrayAdapter<MinimalPokemon> pokedexEntries;

    //Extra container for advanced Search
    ArrayAdapter<MinimalPokemon> filteredPokedexEntries;

    private void refreshPokedexOverviewPanel(){
        overviewDescription.setText(contextMaster.getCurrentMinimalPokemon().getDescription());
        overviewType1.setImageDrawable(contextMaster.getCurrentMinimalType1());
        overviewType1.setScaleType(ImageView.ScaleType.FIT_XY);
        overviewType2.setImageDrawable(contextMaster.getCurrentMinimalType2());
        overviewType2.setScaleType(ImageView.ScaleType.FIT_XY);
        overviewImage.setImageDrawable(contextMaster.getSelectionOverviewSprite());
    }

    private void retrieveInterfaceElements(){
        //Retrieve all UI Variables for setup
        overviewImage = (ImageButton) findViewById(R.id.imgbutton_pkmnsprite);
        overviewDescription = (TextView) findViewById(R.id.textview_pkmndescript);
        overviewType1 = (ImageView) findViewById(R.id.imgview_pkmntype1);
        overviewType2 = (ImageView) findViewById(R.id.imgview_pkmntype2);
        inputSearch = (EditText) findViewById(R.id.edittext_pkmnname);
        pokedexListView = (ListView) findViewById(R.id.listv_pkdexentries);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        //Signal Cleanup
        Runtime.getRuntime().gc();
        System.gc();

        retrieveInterfaceElements();

        //Set up app context.
        contextMaster = PokedexManager.getInstance();
        dexVoice = TTSController.getOrSetInstance(this);
        audioPlayer = CentralAudioPlayer.getInstance();
        audioPlayer.updateInstance(0, PokedexAssetFactory.getPokemonCry(this, 0));
        dexVoice.setText(overviewDescription.getText().toString());

        //Set up Buttons
        Button cryButton = (Button)findViewById(R.id.button_pkmncry);
        if(!cryButton.hasOnClickListeners()) {
            cryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPokemonCry(view);
            }
        });
        }

        //Call the Factory and get the MinimalPokemon list
        final PokemonFactory pokemonFactory = PokemonFactory.getPokemonFactory(this.getApplicationContext());

        long startTime = System.nanoTime();

        // Check if coming from intro activity or from Advanced search.
        // Advanced search will have extra info
        Bundle extraInfo = getIntent().getExtras();
        MinimalPokemon[] listy = pokemonFactory.getAllMinimalPokemon();
        if(extraInfo != null) {

            ArrayList<Integer> filteredPokemonNationalid = extraInfo.getIntegerArrayList("FILTERED_POKEMON_NATIONALID");

            MinimalPokemon [] filteredlist = new MinimalPokemon[filteredPokemonNationalid.size()];

            for (int pos=0; pos<filteredPokemonNationalid.size(); pos++){
                filteredlist[pos] = listy[filteredPokemonNationalid.get(pos)];
            }
            //Initialize the ArrayAdapter object.
            pokedexEntries = new PokedexArrayAdapter(this,filteredlist);
        }
        else {
            //Initialize the ArrayAdapter object with full list.
            pokedexEntries = new PokedexArrayAdapter(this,listy);

        }
        final long minBuild = System.nanoTime();
        Log.d("QPDEX", String.format("All MinimalPokemon done in: %s ns", minBuild - startTime));



        //Setup the pokedexListView object
        pokedexListView.setAdapter(pokedexEntries);
        pokedexListView.setOnItemClickListener(new PokedexSingleClickListener());
        pokedexListView.setLongClickable(true);
        pokedexListView.setOnItemLongClickListener(new PokedexLongClickListener());

        //Set up Search Bar (EditText)
        if(!inputSearch.hasOnClickListeners()){
            //Set the OnClick Listener
            inputSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Clear the Pokemon Name immediately (?)
                    //No overrided behaviour for now. Just used to make "hasOnClickListeners" as true.
                }
            });

            //Piggyback and also set the OnFocusChange Method
            inputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        //Take the keyboard away
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }
        //Setup the filter
        this.inputSearch.addTextChangedListener(new PokedexSearchBarWatch());
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (contextMaster.isReady())
            refreshPokedexOverviewPanel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pokedex, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Log.d("QPDex","Back to intro menu");
        super.onBackPressed();
        dexVoice.releaseEngine();
        this.finish(); //There's unfortunately no guarantee that this is done immediately.
        System.gc();
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

    public void onClickNextPokemon(View view){
        int shownItemCount = pokedexListView.getCount();
        int currentPosition =pokedexListView.getCheckedItemPosition();
        int newPos = (currentPosition+1) % shownItemCount;
        //Update the listview
        pokedexListView.setSelection(newPos);
        pokedexListView.performItemClick(view, newPos,newPos);
        Log.d("QPDEX", String.format("count: %s, pos: %s",shownItemCount,newPos));
    }

    public void onClickPreviousPokemon(View view){
        int shownItemCount = pokedexListView.getCount();
        int currentPosition = pokedexListView.getCheckedItemPosition();
        currentPosition = (currentPosition-1) % shownItemCount;
        if(currentPosition<0){
            currentPosition = shownItemCount-1;
        }
        //Update the listview
        pokedexListView.setSelection(currentPosition);
        pokedexListView.performItemClick(view, currentPosition, currentPosition);
        Log.d("QPDEX", String.format("count: %s, pos: %s", shownItemCount, currentPosition));
    }

    public void switchToPokemonData(View view){

        final Intent intent = new Intent(this,DetailedPokemonActivity.class);

        //We might need to signal the PokedexManager to see if the activity can load.
        final PokemonFactory pkmnBuild = PokemonFactory.getPokemonFactory(this);
        final int selectedNationalID = contextMaster.getCurrentMinimalPokemon().getPokemonNationalID();
        contextMaster.updatePokedexSelection(contextMaster.getCurrentMinimalPokemon(), getApplicationContext(), true);
        Log.d("QPDEX",String.format("Switching to %s",selectedNationalID));

        if(!pkmnBuild.isDetailedNationalIDBuiltAndReady(selectedNationalID)){
            final ProgressDialog dialog = ProgressDialog.show(PokedexActivity.this, "", "Loading. Please wait...", true);
            Thread modalHandler = new Thread(){
                @Override
                public void run(){

                    //Show loading
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setCancelable(true);
                        }
                    });

                    //Wait for a while
                    while(!pkmnBuild.isDetailedNationalIDBuiltAndReady(selectedNationalID)){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //Dismiss the loading and proceed.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            startActivity(intent);
                        }
                    });
                }
            };
            modalHandler.setPriority(Thread.MAX_PRIORITY);
            modalHandler.start();

        }
        else {
            startActivity(intent);
        }
    }

    public void playPokemonCry(View view){
        Log.w("QPDEX","Playing Sound");
        audioPlayer.playSound();
    }

    public void saySomething(View view){
        inputSearch.clearFocus();
        dexVoice.speak();
    }

    public void showConstructionActivity(View view){
        Intent intent = new Intent(this,WIPActivity.class);
        startActivity(intent);
    }
}
