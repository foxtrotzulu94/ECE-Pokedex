package me.quadphase.qpdex;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.pokedex.PokedexAssetFactory;
import me.quadphase.qpdex.pokedex.PokedexManager;
import me.quadphase.qpdex.pokemon.Ability;
import me.quadphase.qpdex.pokemon.Move;
import me.quadphase.qpdex.pokemon.Pokemon;
import me.quadphase.qpdex.pokemon.Type;


public class DetailedPokemonActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Worker Thread in charge of filling up the progress bar
     * Stops updating the UI when Interrupted
     */
    private class statsUpdaterWorkerThread extends Thread{
        final private String textViewPrefix="text_";
        final private String progressBarPrefix="pbar_";
        private String statIdentifier="hp";
        private int finalValue=10;
        private Context current;
        private boolean updateUI=true;

        public statsUpdaterWorkerThread(String idName, int val, Context cur){
            statIdentifier = idName;
            finalValue = val;
            current = cur;
        }

        @Override
        public void run(){
            int displayID = getResources().getIdentifier(
                    String.format("%s%sval",textViewPrefix,statIdentifier),
                    "id", current.getPackageName());
            int barID = getResources().getIdentifier(
                    String.format("%s%sval",progressBarPrefix,statIdentifier),
                    "id", current.getPackageName());
            final TextView display = (TextView) findViewById(displayID);
            final ProgressBar anim = (ProgressBar) findViewById(barID);

            //Put the value immediately on the UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    display.setText(Integer.toString(finalValue));
                }
            });

            //Animate the progress bar loading
            try {
                for(int i=0; i<=finalValue; i++) {
                    final int j = i;
                    if(updateUI){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                anim.setProgress(j);
                            }
                        });
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                if(BuildConfig.DEBUG)
                    Log.d(this.getName(),"Thread Interrupted, stopping UI Update");
                updateUI = false;
            }
        }

    }

    /**
     * Master Thread to update the Stats progress bars.
     * Keeps a list of all workers until the created Thread object is deleted
     */
    private class statsUpdaterMasterThread extends Thread{
        List<Thread> workers;
        @Override
        public void run() {
            int statSum = 0;
            workers = new LinkedList<>();
            for (int i = 0;i<statIdentifiers.length;i++)
            {
                int statVal = detailedPokemon.retrieveStatFromString(statIdentifiers[i]);
                statSum+=statVal;
                Thread updaterThread = new statsUpdaterWorkerThread(
                        statIdentifiers[i],
                        statVal,
                        getBaseContext());
                updaterThread.start();
                workers.add(updaterThread);
            }
            final TextView total = (TextView) findViewById(R.id.textview_statstotalval);
            final int totalStatSum = statSum;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    total.setText(Integer.toString(totalStatSum));
                }
            });
        }

        public void preventFurtherExecution(){
            //This does not seem to work...
            for (int i = 0; i < workers.size(); i++) {
                statsUpdaterWorkerThread updater = (statsUpdaterWorkerThread)workers.get(i);
                updater.updateUI=false;
                updater.interrupt();
            }
        }
    }

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Instance of the Central Pokedex Manager
     */
    private PokedexManager contextMaster;
    /**
     * Instances of the Master stats updater Thread.
     */
    private statsUpdaterMasterThread statsBarController;


    /**
     * Currently detailed Pokemon.
     */
    private Pokemon detailedPokemon;


    /**
     * String array used to identify the suffix for UI elements that display staff
     * DO NOT CHANGE UNLESS corresponding "activity_detailed_pokemon.xml" IS CHANGED!
     */
    private String[] statIdentifiers = {"hp","attack","defense","spatk","spdef","speed"};
    /**
     * Boolean to check if activity was created completely at least once.
     */
    private boolean createdActivity=false;
    /**
     * Current index for Sprite Carousel
     */
    private int spriteIndex;

    private TextView pkmnName;
    private ImageView pkmnSprite;
    private ImageView pkmnType1;
    private ImageView pkmnType2;
    private TextView pkmnDescription;
    private TextView eggGroupSteps;

    private LinearLayout typeWeak;
    private LinearLayout typeStrong;
    private HorizontalScrollView evolutionChain;
    private LinearLayout eggGroupBox;
    private LinearLayout abilitiesBox;
    private LinearLayout movesBox;

    private TextView evolutionTab;
    private TextView alternatesTab;

    private CheckBox pokemonCaughtBox;
    private CheckBox pokemonInPartyBox;

    /**
     * This method retrieves all necessary elements from "activity_detailed_pokemon.xml"
     * It is used both to avoid retrieving elements within functions and keeping track of what
     * information is being displayed at any given iteration on a pokemon
     */
    private void retrieveInterfaceElements(){
        pkmnName = (TextView) findViewById(R.id.textview_pkmnname_detail);
        pkmnSprite = (ImageView) findViewById(R.id.imgbutton_pkmnsprite_detail);
        pkmnType1 = (ImageView) findViewById(R.id.imgview_pkmntype1_detail);
        pkmnType2 = (ImageView) findViewById(R.id.imgview_pkmntype2_detail);
        pkmnDescription = (TextView) findViewById(R.id.textview_detailedpkmndescript);
        eggGroupSteps = (TextView) findViewById(R.id.textview_eggsteps);

        typeStrong = (LinearLayout) findViewById(R.id.linlayout_typestrong);
        typeWeak = (LinearLayout) findViewById(R.id.linlayout_typeweak);
        evolutionChain = (HorizontalScrollView) findViewById(R.id.horizontalsv_evolutions_detail);
        eggGroupBox = (LinearLayout) findViewById(R.id.linlay_egggroupbox);
        abilitiesBox = (LinearLayout) findViewById(R.id.linlay_abilitiesbox);
        movesBox = (LinearLayout) findViewById(R.id.linlay_movebox);

        evolutionTab = (TextView) findViewById(R.id.title_evolutions);
        alternatesTab = (TextView) findViewById(R.id.title_altforms);

        pokemonCaughtBox = (CheckBox) findViewById(R.id.checkbox_caught);
        pokemonInPartyBox = (CheckBox) findViewById(R.id.checkbox_trainerparty);
    }

    /**
     * Creates a View element based on "custom_typematch_block" and does all the necessary screen
     * screen calculations for correct grid alignment. Primarily used with {@see fillTypeComparisonInfo()}
     * @param quantifier How detailedPokemon is affected by the Types being passed (i.e. Immune, etc)
     * @param types The list of types that correspond to the Identifier
     * @return A LinearLayout that can be directly set as a child of any LayoutView
     */
    private LinearLayout createTypeMatchBlock(String quantifier, List<Type> types){
        LinearLayout typeMatchBlock = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_typematch_block, null);
        GridLayout frame=(GridLayout) typeMatchBlock.findViewById(R.id.gridlay_types);
        TextView title = (TextView) typeMatchBlock.findViewById(R.id.characteristic);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenDPI = metrics.densityDpi;

        title.setText(quantifier);

        for (int i=0;i<types.size();i++) {
            ImageView typeBadge = new ImageView(this);

            typeBadge.setImageDrawable(
                    new BitmapDrawable(getResources(),
                            PokedexAssetFactory.getTypeBadge(this, types.get(i).getName())));

            typeBadge.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            typeBadge.setScaleType(ImageView.ScaleType.FIT_XY);
//            typeBadge.setMaxHeight(30);
            typeBadge.setAdjustViewBounds(true);
            typeBadge.getLayoutParams().width = (screenDPI/5)*2;
            frame.addView(typeBadge);
        }

        //TODO: We need to calculate for screen sizes
        // http://developer.android.com/reference/android/util/DisplayMetrics.html
        // (Ultra high DP devices may take 3 columns)
        // Mid DP devices can take 2
        // low DP can take only 1




        frame.setColumnCount(2);

        Configuration configuration = getResources().getConfiguration();
        int smallestScreenWidthDp = configuration.screenWidthDp;
        Log.d("QPDEX", String.format("widthDP %s, DPI %s", smallestScreenWidthDp,metrics.densityDpi));

        return typeMatchBlock;
    }

    /**
     * Creates a "row" to display a move within a LayoutView, based on "custom_movebox.xml"
     * @param condition The condition string of the Move (should be "TM##" or "Lv##", for "##" a number)
     * @param move The move object in question, from which the type is obtained
     * @return A LinearLayout that can be directly set as a child of any LayoutView
     */
    private LinearLayout createMovesSubBox(String condition, Move move){
        LinearLayout moveBox = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_movebox, null);
        ((TextView)moveBox.findViewById(R.id.textview_movecondition)).setText(condition);
        ((TextView)moveBox.findViewById(R.id.textview_movename)).setText(move.getName());

        ImageView moveType = (ImageView) moveBox.findViewById(R.id.imgview_movetype);
        moveType.setImageDrawable(new BitmapDrawable(getResources(), PokedexAssetFactory.getTypeBadge(this, move.getType().getName())));

        return moveBox;
    }

    /**
     * Creates a small view object, based on "custom_pkmnbox.xml", which holds a mini pokemon sprite
     * and the name of such Pokemon
     * @param evoName The name, to be displayed as a subtitle
     * @param evoSprite The BitmapDrawable resource of the Pokemon
     * @return A LinearLayout that can be directly set as a child of any LayoutView
     */
    private LinearLayout createCustomPokemonBox(String evoName, BitmapDrawable evoSprite){
        LinearLayout evoBox = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_pkmnbox, null);

        TextView evolutionText = (TextView) evoBox.findViewById(R.id.textview_evoname);
        evolutionText.setText(evoName);

        ImageView evolutionSprite = (ImageView) evoBox.findViewById(R.id.imgview_evosprite);
        evolutionSprite.setImageDrawable(evoSprite);

        return evoBox;
    }

    /**
     * Method which fetches and resets the entire view. This should be called after a new Pokemon
     * object has been placed a detailedPokemon in the {@see PokedexManager} instance
     */
    private void refreshAllDetails(){ //Call when change is needed on all things (New Pokemon in Focus)

        if(createdActivity){
            if(contextMaster.getSelectionOverviewSprite()!=null) {
                pkmnSprite.setImageDrawable(contextMaster.getSelectionOverviewSprite());
            }
            else {
                pkmnSprite.setImageResource(R.drawable.sprite_unknown);
            }

            statsBarController.preventFurtherExecution();
            statsBarController = null;

            //Remove all of the dynamically created elements.
            typeStrong.removeAllViews();
            typeWeak.removeAllViews();
            eggGroupBox.removeAllViews();
            abilitiesBox.removeAllViews();
            movesBox.removeAllViews();

            //TODO: Conditional Remove! (Don't remove if it's another similar Evolution
            evolutionChain.removeAllViews();
        }

        detailedPokemon = contextMaster.getCurrentDetailedPokemon();

        if(detailedPokemon==null){
            //Default to the fail-safe Pokemon
            contextMaster.updatePokedexSelection(contextMaster.missingNo,this);
        }
        Log.d("QPDEX",String.format("Detailing to %s",detailedPokemon.getPokemonNationalID()));
        spriteIndex = 0; //Might remove in the future

        //Set the name
        pkmnName.setText(String.format("  %s. %s",detailedPokemon.getPokemonNationalID(), detailedPokemon.getName()));

        //Set the Pokemon Type Badges
        setPokemonTypeInfo();

        //Reload stats
        statsBarController = new statsUpdaterMasterThread();
        statsBarController.start();

        //Set up CheckBox status
        setPokemonOwnershipInfo();

        //Place Description
        pkmnDescription.setText(detailedPokemon.getDescription());

        //Show the Evolution Chain by default
        // This is done to avoid calling "buildEvolutionChain and thus establishing a default option
        // for the tabs.
        showEvolutionChain();

        //Populate Types
        fillTypeComparisonInfo();

        //Load Pokemon Abilities
        populateAbilitiesInfo();

        //Fill in Egg Groups
        fillEggGroupInfo();

        //Place all moves
        populateMoveInfo();
    }

    private void setPokemonOwnershipInfo(){
        //TODO: fix considerably (separate task)
        pokemonCaughtBox.setEnabled(false);
        pokemonInPartyBox.setEnabled(false);

        //Setup the caught CheckBox
//        pokemonCaughtBox.setChecked(contextMaster.getCurrentDetailedPokemon().getCaught());
//        pokemonCaughtBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                contextMaster.getCurrentDetailedPokemon().toggleCaught(getBaseContext());
//            }
//        });
    }

    /**
     * Sets the Sprites for the detailedPokemon sprite badges by consulting {@see PokedexManager}
     */
    private void setPokemonTypeInfo(){
        if (contextMaster.getCurrentDetailedType1()!=null) {
            pkmnType1.setImageDrawable(contextMaster.getCurrentDetailedType1());
            pkmnType2.setImageDrawable(contextMaster.getCurrentDetailedType2());
        }
        else {
            Log.w("QPDEX", "Types were not available or null!!");
        }
    }

    /**
     * Fills the LinearLayout related to the Evolution Chain
     */
    private void buildEvolutionChain(){
        if(detailedPokemon.getEvolutions()!=null && !detailedPokemon.getEvolutions().isEmpty()){
            //TODO: Fix Evolution class to detect MegaEvolutions and check for those sprites.

            LinearLayout boxContainer = new LinearLayout(this);

            for (int i = 0; i < detailedPokemon.getEvolutions().size(); i++) {

                final Pokemon evoPokemon = detailedPokemon.getEvolutions().get(i).getEvolvesInto();

                LinearLayout evolutionBox = createCustomPokemonBox(
                        evoPokemon.getName(),
                        new BitmapDrawable(getResources(),
                                PokedexAssetFactory.getPokemonMinimalSprite(this, evoPokemon.getPokemonNationalID())));

                evolutionBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contextMaster.updatePokedexSelection(evoPokemon,getBaseContext());
                        refreshAllDetails();
                    }
                });
                evolutionBox.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        .3f));

                boxContainer.addView(evolutionBox);
            }
            evolutionChain.addView(boxContainer);

        }
        else{
            TextView notApplicable = new TextView(this);
            notApplicable.setText("No Evolutions to show");
            evolutionChain.addView(notApplicable);
        }
    }
    /**
     * Fills the LinearLayout related to the Evolution Chain to display all Alternate Forms
     */
    private void buildAlternateForms(){
        //TODO: Fill in Alternates
        //This requires a refactoring of the Pokemon Class to include an Alternate Form attribute
        // Which would probably be a separate List<Pokemon> with a particular name and format

        //For now, we'll place 3PokeTrainer$ to show it's not working

        LinearLayout altForm = createCustomPokemonBox("3TrainerPoke$",
                new BitmapDrawable(getResources(), PokedexAssetFactory.getPokemonMinimalSprite(this, 0)));
        altForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contextMaster.updatePokedexSelection(contextMaster.missingNo,getBaseContext());
                refreshAllDetails();
            }
        });
//        altForm.setLayoutParams(new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                .3f));
        evolutionChain.addView(altForm);


    }

    /**
     * Fills in how the different types affect the detailedPokemon.
     */
    private void fillTypeComparisonInfo(){
        // determine the effectiveness of each type against the pokemon in question. To do this
        // on a pokemon with just one type, use the information directly. If the pokemon has 2
        // types, multiply the attacking effectiveness of both types to determine the final values.

        // This pokemon is immune to these types when defending itself (i.e. 0 effectiveness)
        List<Type> typeImmune = new LinkedList<>();
        // This pokemon is resistant to these types when defending itself (i.e. 1/2 effectiveness)
        List<Type> typeResist = new LinkedList<>();
        // This pokemon is very resistant to these types when defending itself (i.e. 1/4 effectiveness)
        List<Type> typeResistStrong = new LinkedList<>();
        // This pokemon is weak to these types when defending itself (i.e. 2 effectiveness)
        List<Type> typeWeakTo = new LinkedList<>();
        // This pokemon is very weak to these types when defending itself (i.e. 4 effectiveness)
        List<Type> typeWeakStrong = new LinkedList<>();

        // get the types of the pokemon
        List<Type> types = detailedPokemon.getTypes();

        // get the list of all the types
        Type[] allTypes = Type.getListOfTypes();

        // when the pokemon has one type:
        if (types.size() == 1) {
            Type type = types.get(0);
            for (int i = 1; i < Type.getNumberOfTypes() + 1; i++) {
                double effectiveDefence = type.getDefendingEffectivenessAgainst(allTypes[i]);

                // add the types to the correct lists
                if (effectiveDefence == 0)
                    typeImmune.add(allTypes[i]);
                else if (effectiveDefence == 0.25)
                    typeResistStrong.add(allTypes[i]);
                else if (effectiveDefence == 0.5)
                    typeResist.add(allTypes[i]);
                else if (effectiveDefence == 2)
                    typeWeakTo.add(allTypes[i]);
                else if (effectiveDefence == 4)
                    typeWeakStrong.add(allTypes[i]);
            }
        } // when the pokemon has two types:
        else if (types.size() == 2) {
            Type type1 = types.get(0);
            Type type2 = types.get(1);
            for (int i = 1; i < Type.getNumberOfTypes() + 1; i++) {
                double effectiveDefence = type1.getDefendingEffectivenessAgainst(allTypes[i]) *
                        type2.getDefendingEffectivenessAgainst(allTypes[i]);

                // add the types to the correct lists
                if (effectiveDefence == 0)
                    typeImmune.add(allTypes[i]);
                else if (effectiveDefence == 0.25)
                    typeResistStrong.add(allTypes[i]);
                else if (effectiveDefence == 0.5)
                    typeResist.add(allTypes[i]);
                else if (effectiveDefence == 2)
                    typeWeakTo.add(allTypes[i]);
                else if (effectiveDefence == 4)
                    typeWeakStrong.add(allTypes[i]);
            }
        }

        // show the lists in the activity:
        if (typeImmune.size() != 0) {
            LinearLayout immune = createTypeMatchBlock("Immune", typeImmune);
            immune.setBackgroundResource(R.color.dex_blue_transparent);
            typeStrong.addView(immune);
        }
        if (typeResist.size() != 0) {
            LinearLayout resists = createTypeMatchBlock("Resists", typeResist);
            resists.setBackgroundResource(R.color.dex_blue_transparent);
            typeStrong.addView(resists);
        }
        if (typeResistStrong.size() != 0) {
            LinearLayout resistsAttack = createTypeMatchBlock("Strongly Resists", typeResistStrong);
            resistsAttack.setBackgroundResource(R.color.dex_blue_transparent);
            typeStrong.addView(resistsAttack);
        }
        if (typeWeakTo.size() != 0) {
            LinearLayout weak = createTypeMatchBlock("Weak",typeWeakTo);
            weak.setBackgroundResource(R.color.dex_pink_transparent);
            typeWeak.addView(weak);
        }
        if (typeWeakStrong.size() != 0) {
            LinearLayout weakStrong = createTypeMatchBlock("Strongly Weak", typeWeakStrong);
            weakStrong.setBackgroundResource(R.color.dex_pink_transparent);
            typeWeak.addView(weakStrong);
        }
    }

    /**
     * Fills all information related to the detailedPokemon's abilities and sets the onClickListener
     * to open up a modal for more info on a particular ability.
     */
    private void populateAbilitiesInfo(){
        if(detailedPokemon.getAbilities()!=null && !detailedPokemon.getAbilities().isEmpty()) {
            final List<Ability> allAbilities = detailedPokemon.getAbilities();
            for (int i = 0; i < detailedPokemon.getAbilities().size(); i++) {
                String ability = allAbilities.get(i).getName();
                final String description = allAbilities.get(i).getDescription();
                Button abilityButton = new Button(this, null, android.R.attr.buttonStyleSmall);
                abilityButton.setText(ability);
                abilityButton.setMaxHeight(32);
                abilityButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: replace with code to call Modal
                        Toast.makeText(getBaseContext(), description, Toast.LENGTH_SHORT).show();
                    }
                });
                abilitiesBox.addView(abilityButton);
            }
        }
        else{
            Log.d("QPDEX", "Pokemon possibly not correctly built. Has no abilities!" + detailedPokemon.toString());
        }
    }

    /**
     * Writes the EggGroups the pokemon is in.
     */
    private void fillEggGroupInfo(){
        if(detailedPokemon.getEggGroups()!=null && !detailedPokemon.getEggGroups().isEmpty()) {
            for (int i = 0; i < detailedPokemon.getEggGroups().size(); i++) {
                TextView eggGroupName = new TextView(this);
                eggGroupName.setText(detailedPokemon.getEggGroups().get(i).getName());
                eggGroupName.setTextColor(Color.DKGRAY);

                eggGroupBox.addView(eggGroupName);
            }
        }
        eggGroupSteps.setText(String.format("%s\n Steps",detailedPokemon.getHatchTime()));
    }

    /**
     * Fills the list of Moves the Pokemon learns, whether through Level-Up or TM/HM
     */
    private void populateMoveInfo(){
        TextView firstNote = new TextView(this);
        firstNote.setText("Moves Learnt By Level Up");
        movesBox.addView(firstNote);

        if(detailedPokemon.getMoves()!=null){
            List<Move> allMoves = detailedPokemon.getMoves();
            for (int i = 0; i < allMoves.size(); i++) {
                LinearLayout someMove =createMovesSubBox("Start", allMoves.get(i));
                LinearLayout.LayoutParams marginTest = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                marginTest.setMargins(5,5,5,5);
                someMove.setLayoutParams(marginTest);
                movesBox.addView(someMove);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_pokemon);

        //Pickup any garbage
        Runtime.getRuntime().gc();
        System.gc();

        //Setup everything to start working
        retrieveInterfaceElements();
        contextMaster = PokedexManager.getInstance();
        detailedPokemon = contextMaster.getCurrentDetailedPokemon();

        //Refresh all UI Elements and set the createdActivity boolean for others to know.
        refreshAllDetails();
        createdActivity=true;

        //Load the Detailed View Image Button.
        if(contextMaster.getSelectionOverviewSprite()!=null)
            pkmnSprite.setImageDrawable(contextMaster.getSelectionOverviewSprite());
        else
            pkmnSprite.setImageResource(R.drawable.sprite_unknown);

        //Set the on click listener for a carousel.
        pkmnSprite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                spriteIndex+=1;
                List<BitmapDrawable> sprites = contextMaster.getAllDetailedPokemonSprites();
                if(sprites!=null) {
                    spriteIndex = spriteIndex % sprites.size();
                    BitmapDrawable newSprite = sprites.get(spriteIndex);
                    pkmnSprite.setImageDrawable(newSprite);
//                throw new RuntimeException("Clicked on an Invalid Pokemon!");
                }
            }
        });

        //Set the onclick listeners for our "tabs" if they DON'T have them
        if(! (evolutionTab.hasOnClickListeners() && alternatesTab.hasOnClickListeners())){
            evolutionTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEvolutionChain();
                }
            });

            alternatesTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlternateForms();
                }
            });
        }


        //Setup the Navigation fragment
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.detailed_pokemon, menu);
//            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * Informs the instance that an update is needed and refreshes the UI accordingly
     */
    public void notifyUpdate(){
        refreshAllDetails();
    }

    /**
     * Shows the Evolution Chain of detailedPokemon and handles the false tabbing system in
     * "activity_detailed_pokemon.xml"
     */
    public void showEvolutionChain(){
        evolutionChain.removeAllViews();
        buildEvolutionChain();
        evolutionTab.setBackgroundColor(getResources().getColor(R.color.dex_detail_greylight));
        alternatesTab.setBackgroundColor(getResources().getColor(R.color.dex_detail_greydarkened));
    }

    /**
     * Shows the alternate forms of detailedPokemon and handles the false tabbing system in
     * "activity_detailed_pokemon.xml"
     */
    public void showAlternateForms(){
        evolutionChain.removeAllViews();
        buildAlternateForms();
        evolutionTab.setBackgroundColor(getResources().getColor(R.color.dex_detail_greydarkened));
        alternatesTab.setBackgroundColor(getResources().getColor(R.color.dex_detail_greylight));
    }

    public void onClickNextPokemon(View view){
        int currentNationalID=contextMaster.getCurrentDetailedPokemon().getPokemonNationalID();
        int maxNationalID = PokemonFactory.getPokemonFactory(this).getMAX_NATIONAL_ID();
        int nextNationalID = (currentNationalID+1) % (maxNationalID+1);
        contextMaster.updatePokedexSelection(nextNationalID,this,true);
        refreshAllDetails();
        mNavigationDrawerFragment.performItemSelection(nextNationalID);
    }

    public void onClickPreviousPokemon(View view){
        int currentNationalID=contextMaster.getCurrentDetailedPokemon().getPokemonNationalID();
        int maxNationalID = contextMaster.getMaxPokemonNationalID();
        int nextNationalID = (currentNationalID-1);
        if(nextNationalID<0){
            nextNationalID = maxNationalID-1;
        }

        contextMaster.updatePokedexSelection(nextNationalID,this,true);
        refreshAllDetails();
        mNavigationDrawerFragment.performItemSelection(nextNationalID);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detailed_pokemon, container, false);
            return rootView;
        }
    }
}
