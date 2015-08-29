package me.quadphase.qpdex.pokedex;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import me.quadphase.qpdex.R;
import me.quadphase.qpdex.pokemon.MinimalPokemon;
import me.quadphase.qpdex.pokemon.Type;

/**
 * Created by Javier Fajardo on 8/5/2015.
 */
//TODO: Document with JavaDocs
public class PokedexArrayAdapter extends ArrayAdapter<MinimalPokemon> implements Filterable {

    private  MinimalPokemon[] entryObjects;
    private  MinimalPokemon[] originalObjects;
    private ArrayList<MinimalPokemon> filteredArray;
    private float fontSize=0.0f;
    protected Filter minimalFilter;


    @Override
    public Filter getFilter(){
        if(minimalFilter == null)
            minimalFilter = new customFilter();
        return minimalFilter;

    }

    private class customFilter extends Filter {
        //see http://www.survivingwithandroid.com/2012/10/android-listview-custom-filter-and.html

        /**
         * Implements the simple filters on minimal pokemon list
         *
         * @param constraint is the character sequence corresponding to pokemon name or national ID
         * @return the minimal pokemon objects that fit the constrait critera
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            // If no constraint specified, no filtering needed
            if (constraint == null || constraint.length() == 0){
                results.values = originalObjects;
                results.count = originalObjects.length;
            }
            else {
                ArrayList<MinimalPokemon> nMinimalPokemonList = new ArrayList<MinimalPokemon>();

                for (MinimalPokemon p : entryObjects) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase())){
                        nMinimalPokemonList.add(p);
                    }
                }
                results.values = nMinimalPokemonList;
                results.count = nMinimalPokemonList.size();

            }

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredArray.addAll((List<MinimalPokemon>)results.values);
            if (results.count == 0){
                notifyDataSetInvalidated();
            }
            else{
                // set the ArrayAdaptor to 0
                notifyDataSetChanged();

                clear();
                //Rebuild what's shown in the ArrayAdaptor

                for(int i=0; i< results.count; i++){
                    add((MinimalPokemon)filteredArray.get(i));
                }
                notifyDataSetChanged();
            }
        }
    };

    public PokedexArrayAdapter(Context context, MinimalPokemon[] values){
        super(context, R.layout.pokedexrow,R.id.textview_pkmn_list_entry,values);
        entryObjects = values;
        originalObjects =  entryObjects;
        filteredArray = new ArrayList();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        super.getView(position,convertView,parent);
        LayoutInflater li = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowEntry = li.inflate(R.layout.pokedexrow,parent,false);

        LinearLayout row = (LinearLayout) rowEntry.findViewById(R.id.linlayout_pokedexrow);
        TextView entryString = (TextView) rowEntry.findViewById(R.id.textview_pkmn_list_entry);
        ImageView miniSprite = (ImageView) rowEntry.findViewById(R.id.img_pkmnmini);
        final ImageButton caught = (ImageButton) rowEntry.findViewById(R.id.imgbutton_caught);

        if(entryObjects[position].isCaught()){
            caught.setAlpha(1.0f);
        }
        else{
            caught.setAlpha(0.2f);
        }

        if (!caught.hasOnClickListeners()) {
            caught.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entryObjects[position].toggleCaught();

                    if (entryObjects[position].isCaught()) {
                        Log.d("QPDEX", String.format("Pokemon %s has been caught", entryObjects[position].getPokemonNationalID()));
                        caught.setAlpha(1.0f);
                    } else {
                        Log.d("QPDEX", String.format("Pokemon %s has been removed from caught list", entryObjects[position].getPokemonNationalID()));
                        caught.setAlpha(0.2f);
                    }
                }
            });
        }

        entryString.setText(entryObjects[position].toString());

        if(fontSize!=0.0f)
            entryString.setTextSize(fontSize);

        miniSprite.setImageDrawable(new BitmapDrawable(super.getContext().getResources(),
                PokedexAssetFactory.getPokemonMinimalSprite(super.getContext(),entryObjects[position].getPokemonNationalID())));

        return rowEntry;
    }

    public void setFontSize(float size){
        fontSize = size;
    }

}
