package me.quadphase.qpdex;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.content.res.Configuration;
import android.widget.TextView;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import me.quadphase.qpdex.BuildConfig;
import me.quadphase.qpdex.PokedexActivity;
import me.quadphase.qpdex.R;
import me.quadphase.qpdex.WIPActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
//        Window ourWin = getWindow();
//        ourWin.setFormat(PixelFormat.R);
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
        Intent intent = new Intent(this,PokedexActivity.class);
        startActivity(intent);
    }

    public void switchToPokemonResources(View view){
        Intent intent = new Intent(this,PokemonResources.class);
        startActivity(intent);
    }

    public void showConstructionActivity(View view){
        Intent intent = new Intent(this,WIPActivity.class);
        startActivity(intent);
    }
}
