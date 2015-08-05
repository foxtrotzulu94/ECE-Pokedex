package me.quadphase.qpdex.pokedex;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import me.quadphase.qpdex.R;
import me.quadphase.qpdex.pokemon.MinimalPokemon;

/**
 * Created by Javier Fajardo on 8/5/2015.
 */
//TODO: Document with JavaDocs
public class PokedexArrayAdapter extends ArrayAdapter<MinimalPokemon> {

    private final MinimalPokemon[] entryObjects;

    public PokedexArrayAdapter(Context context, MinimalPokemon[] values){
        super(context, R.layout.pokedexrow,R.id.textview_pkmn_list_entry,values);
        entryObjects = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        super.getView(position,convertView,parent);
        LayoutInflater li = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowEntry = li.inflate(R.layout.pokedexrow,parent,false);

        TextView entryString = (TextView) rowEntry.findViewById(R.id.textview_pkmn_list_entry);
        ImageView miniSprite = (ImageView) rowEntry.findViewById(R.id.img_pkmnmini);

        entryString.setText(entryObjects[position].toString());
        miniSprite.setImageDrawable(new BitmapDrawable(super.getContext().getResources(),
                PokedexAssetFactory.getPokemonMinimalSprite(super.getContext(),entryObjects[position].getNationalID())));

        return rowEntry;
    }
}
