package me.quadphase.qpdex;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.quadphase.qpdex.pokedex.PokedexAssetFactory;
import me.quadphase.qpdex.pokemon.Type;


public class DetailedPokemonActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {restoreActionBar()}.
     */
    private CharSequence mTitle;

    private TextView pkmnName;
    private TableLayout statsTable;
    private LinearLayout typeWeak;
    private LinearLayout typeStrong;
    private LinearLayout evolutionChain;

    private void retrieveInterfaceElements(){
        pkmnName = (TextView) findViewById(R.id.textview_pkmnname_detail);
        statsTable = (TableLayout) findViewById(R.id.table_pkmnstats);
        typeStrong = (LinearLayout) findViewById(R.id.linlayout_typestrong);
        typeWeak = (LinearLayout) findViewById(R.id.linlayout_typeweak);
        evolutionChain = (LinearLayout) findViewById(R.id.linlayout_evolutions_detail);
    }

    private LinearLayout createTypeMatchBlock(String quantifier, List<Type> types){
        LinearLayout typeMatchBlock = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_typematch_block, null);
        GridLayout frame=(GridLayout) typeMatchBlock.findViewById(R.id.gridlay_types);
        TextView title = (TextView) typeMatchBlock.findViewById(R.id.characteristic);

        title.setText(quantifier);

        for (int i=0;i<types.size();i++) {
            ImageView typeBadge = new ImageView(this);

            typeBadge.setImageDrawable(
                    new BitmapDrawable(getResources(),
                            PokedexAssetFactory.getTypeBadge(this, types.get(i).getName())));
            typeBadge.setMaxHeight(30);
            typeBadge.setAdjustViewBounds(true);
            frame.addView(typeBadge);
        }
        return typeMatchBlock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_pokemon);

        //Pickup any garbage.
        Runtime.getRuntime().gc();
        System.gc();
        retrieveInterfaceElements();

        //TODO: Show tabs on this view if displaying multi-variant (suffixed) pokemon
        //      This could be Mega-Evolution, male and female forms, etc.

//        TextView testy = new TextView(this);
//        testy.setText("Immune To");
//        ImageView typy = new ImageView(this);
//        typy.setImageDrawable(new BitmapDrawable(getResources(), PokedexAssetFactory.getTypeBadge(this, "none")));
        List<Type> typeList = new ArrayList<Type>(3);
        typeList.add(new Type("Normal", ""));
        typeList.add(new Type("Electric", ""));
        typeList.add(new Type("Ice", ""));

        LinearLayout testy = createTypeMatchBlock("Immune",typeList);
        testy.setBackgroundResource(R.color.dex_blue_transparent);
        LinearLayout testy2 = createTypeMatchBlock("Resists",typeList);
        testy2.setBackgroundResource(R.color.dex_blue_transparent);
        LinearLayout testy3 = createTypeMatchBlock("Resists",typeList);
        testy3.setBackgroundResource(R.color.dex_yellow_transparent);
        typeStrong.addView(testy);
        typeStrong.addView(testy2);
        typeWeak.addView(testy3);

        for(int i = 1; i<=7; i++){
            BitmapDrawable miniEvo = new BitmapDrawable(getResources(),PokedexAssetFactory.getPokemonMinimalSprite(this,i));
            ImageView img = new ImageView(this);
            img.setImageDrawable(miniEvo);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("QPDEX", v.toString());
                }
            });
            img.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    .3f));
            img.setAdjustViewBounds(true);

            evolutionChain.addView(img);
        }

        //TODO: Abstract this to update for all general table rows.
        Thread spawnOff = new Thread(){
            @Override
            public void run(){
                //Use getResources().getIdentifier("titleText", "id", getContext().getPackageName());
                final TableLayout base = (TableLayout) findViewById(R.id.table_pkmnstats);
                final TextView display = (TextView) findViewById(R.id.text_hpval);
                final ProgressBar anim = (ProgressBar) findViewById(R.id.pbar_hpval);
                for(int i=0; i<129; i++){
                    final int j = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //HACK: NOT THREAD SAFE CODE!!!
                            display.setText(Integer.toString(j));
                            anim.setProgress(j);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

          }
        };
        spawnOff.start();



                //Load the Detailed View Image Button.
                //TODO: Extract and place in asset handling "Pokedex" class.
                ImageButton sprite = (ImageButton) findViewById(R.id.imgbutton_pkmnsprite_detail);
        InputStream rawBits;
        try{
            rawBits = getAssets().open("1/-1.png");
            sprite.setImageBitmap(BitmapFactory.decodeStream(rawBits));
        }
        catch (Exception e){
            Log.e("QPDEX","EXCEPTION OCCURRED");
        }

        //TODO: REMOVE LATER - TESTING ONLY!
        sprite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("Clicked on an Invalid Pokemon!");
            }
        });




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

//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//    }


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

//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((DetailedPokemonActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
    }
}
