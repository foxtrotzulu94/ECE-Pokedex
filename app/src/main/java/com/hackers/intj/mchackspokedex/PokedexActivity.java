package com.hackers.intj.mchackspokedex;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PokedexActivity extends ActionBarActivity {

    List<String> DisplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
        Intent intent = getIntent();
        DisplayText = intent.getStringArrayListExtra("BoxText");
//        List<String> value = new ArrayList<String>();
//
//        for(int i=0; i<720; i++)
//            value.add(DisplayText);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                DisplayText);
        ListView pokeList = (ListView) findViewById(R.id.listView);
        pokeList.setAdapter(arrayAdapter);

        pokeList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), "Some Stuff="+id+" Pos="+position, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(PokedexActivity.this, DetailedPokemonActivity.class);
//                startActivity(intent);
            }

//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
        });
        Toast.makeText(getBaseContext(), "Some Stuff", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pokedex, menu);
        return true;
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


}
