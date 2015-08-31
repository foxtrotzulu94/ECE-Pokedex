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

import me.quadphase.qpdex.R;
import me.quadphase.qpdex.pokemon.MinimalPokemon;

/**
 * Created by Javier Fajardo on 8/5/2015.
 */
//TODO: Document with JavaDocs
public class PokedexArrayAdapter extends ArrayAdapter<MinimalPokemon> implements Filterable {

    /**
     *  Class to take the MinimalPokemon object and toggle its corresponding caught icon
     */
    private class RowCaughtOnClickListener implements View.OnClickListener{

        MinimalPokemon RowPokemon;
        ImageButton Caught;

        public RowCaughtOnClickListener(MinimalPokemon apokemon,ImageButton sprite){

            RowPokemon = apokemon;
            Caught = sprite;
        }

        @Override
        public void onClick(View view) {
            RowPokemon.toggleCaught();
            Log.d("QPDEX", String.format("Pokemon %s has been caught", RowPokemon.getPokemonNationalID()));
            if (RowPokemon.isCaught()) {
                Caught.setAlpha(1.0f);
            } else {
                Caught.setAlpha(0.2f);
            }
        }

    }

    private float fontSize=0.0f;

    /// Commented out unused Custom filter code

//    private Filter customFilter = new Filter() {
//        //see http://www.survivingwithandroid.com/2012/10/android-listview-custom-filter-and.html
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            return null;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//
//        }
//    };

    public PokedexArrayAdapter(Context context, MinimalPokemon[] values){
        super(context, R.layout.pokedexrow,R.id.textview_pkmn_list_entry,values);
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

        if(super.getItem(position).isCaught()){
            caught.setAlpha(1.0f);
        }
        else{
            caught.setAlpha(0.2f);
        }

        if (!caught.hasOnClickListeners()) {
            caught.setOnClickListener(new RowCaughtOnClickListener(super.getItem(position), caught));
        }

        entryString.setText(super.getItem(position).toString());

        if(fontSize!=0.0f)
            entryString.setTextSize(fontSize);

        miniSprite.setImageDrawable(new BitmapDrawable(super.getContext().getResources(),
                PokedexAssetFactory.getPokemonMinimalSprite(super.getContext(),super.getItem(position).getPokemonNationalID())));

        return rowEntry;
    }

    public void setFontSize(float size){
        fontSize = size;
    }

    //    @Override
    public Filter getFiler(){
        return super.getFilter();
    }
}
