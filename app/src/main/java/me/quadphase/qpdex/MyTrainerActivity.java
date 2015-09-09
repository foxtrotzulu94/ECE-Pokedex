package me.quadphase.qpdex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MyTrainerActivity extends AppCompatActivity {

    private String[] trainerClassOptions = {"Beauty",
            "Biker",
            "Bird Keeper",
            "Blackbelt",
            "Boss",
            "Bug Catcher",
            "Burglar",
            "Champion",
            "Channeler",
            "Cooltrainer",
            "Cue Ball",
            "Elite Four",
            "Engineer",
            "Fisherman",
            "Gambler",
            "Gentleman",
            "Hiker",
            "Jr. Trainer♂",
            "Jr. Trainer♀",
            "Juggler",
            "Lass",
            "Leader",
            "PokéManiac",
            "Psychic",
            "Rival*",
            "Rocker",
            "Rocket",
            "Sailor",
            "Scientist",
            "Super Geek",
            "Swimmer",
            "Tamer",
            "Youngster"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainer_activty);

        //Signal Cleanup
        Runtime.getRuntime().gc();
        System.gc();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_trainer_activty, menu);
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
