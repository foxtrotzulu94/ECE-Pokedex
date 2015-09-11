package me.quadphase.qpdex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import me.quadphase.qpdex.databaseAccess.PokemonFactory;
import me.quadphase.qpdex.pokedex.PokedexManager;

public class IntroActivity extends AppCompatActivity {

    private PokedexManager contextMaster;

    private void setupAndLoad(){
        //Initialize the PokedexManager class
         contextMaster = PokedexManager.getInstance();

        //Tell the PokedexManager to begin caching operations
        //This takes care of any steps related to pre-fetching objects and building them together.
        contextMaster.beginCachingRoutines(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Set the background by manually calling the bitmap decoder.
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inPreferredConfig = Bitmap.Config.RGB_565;
        op.inSampleSize = 1;
        op.inDither = false;
        op.inPremultiplied = false;
        op.inScaled = true;
        LinearLayout lin = (LinearLayout)findViewById(R.id.lin_back);
        lin.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.menubackground, op)));

        //Show build information for debugging.
        if (BuildConfig.DEBUG) {
            TextView buildField = (TextView) findViewById(R.id.app_build);
            Date buildDate = new Date(BuildConfig.TIMESTAMP);
            Formatter formatter = new Formatter(new StringBuilder(), Locale.US);
            formatter.format("[ %1$s build ] \nBuild: %2$s\nCommitt: %3$s \n[from %4$s]",
                    BuildConfig.BUILD_TYPE,
                    buildDate.toString(),
                    BuildConfig.GIT_COMMIT_INFO,
                    BuildConfig.GIT_BRANCH);
            buildField.setText(formatter.toString());
        }

        //Register with the ExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        //Signal a prefetch.
//        setupAndLoad();

        //Signal for collection if needed
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
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

    public void switchToPokedex(View view){
        final Intent intent = new Intent(this,PokedexActivity.class);
        setupAndLoad();

        if(!contextMaster.isMinimalReady()){
            final ProgressDialog dialog = ProgressDialog.show(IntroActivity.this, "", "Loading. Please wait...", true);
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
                    while(!contextMaster.isMinimalReady()){
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

    public void switchToPokemonResources(View view){
        Intent intent = new Intent(this,PokemonResources.class);
        startActivity(intent);
    }

    public void showConstructionActivity(View view){
        Intent intent = new Intent(this,WIPActivity.class);
        startActivity(intent);
    }

    public void showCreditsActivity(View view) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}
