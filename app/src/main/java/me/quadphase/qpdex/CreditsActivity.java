package me.quadphase.qpdex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        //An array of all the websites used. Make sure the strings match the textview ID used,
        //Make sure to update array if new resources hyperlinks are added
        String[] websitearray = {"bulbapedia", "serebii", "smogon", "veekun"};
        for (int i = 0; i < websitearray.length; i++){
            int websiteID = getResources().getIdentifier(String.format("%s",websitearray[i]),"id",getPackageName());
            TextView websiteHyperlink = (TextView) findViewById(websiteID);
            websiteHyperlink.setText(Html.fromHtml(websiteHyperlink.getText().toString()));
            websiteHyperlink.setGravity(View.TEXT_ALIGNMENT_CENTER);
            websiteHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credits, menu);
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
