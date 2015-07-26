package me.quadphase.qpdex;

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

        //Show build date (DEVELOPMENT ONLY! REMOVE WHEN RELEASE)
        TextView buildField = (TextView) findViewById(R.id.app_build);
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        buildField.setText("Built: "+buildDate.toString()+" ["+BuildConfig.BUILD_TYPE+"]");

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

    public void showConstructionActivity(View view){
        Intent intent = new Intent(this,WIPActivity.class);
        startActivity(intent);
    }
}
