package me.quadphase.qpdex;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyTrainerActivity extends FragmentActivity {

    //TODO: Stop Hard Coding
    private String[] trainerRegionOptions = {"Kalos","Unova","Sinnoh","Hoenn","Johto","Kanto"};

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

    private TextView editInfoTab;

    private void retrieveInterfaceElements(){
        editInfoTab = (TextView) findViewById(R.id.txtview_editinfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainer_activity);

        //Signal Cleanup
        Runtime.getRuntime().gc();
        System.gc();

        retrieveInterfaceElements();

        editInfoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEditInfo(v);
            }
        });


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

    public void onClickEditInfo(View view){
        TrainerEditInfoDialog editInfo = new TrainerEditInfoDialog();
        //TODO: Before showing, we need to pass arguments to the fragment "AlertDialog" using Bundles
        //      Bundles enable item serialization for communicating between UI Elements.
        //      The role of bundles here is to pass item that the alert dialog needs to display but
        //      Should not be consulting directly from the backend classes.
        editInfo.show(getFragmentManager(),"EditInfo");
    }

}
