package me.quadphase.qpdex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * This code is based on the "ForceClose Example made by Hardik Trivedi
 * Original Respository is here: https://github.com/hardik-trivedi/ForceClose
 */

public class ExceptionCaughtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_caught);

        //Cleanup any garbage
        Runtime.getRuntime().gc();
        System.gc();

        //Get the textView
        TextView stackView = (TextView) findViewById(R.id.textview_errorstack);
        stackView.setMovementMethod(new ScrollingMovementMethod());

        //Show the error trace
        if(getIntent().hasExtra("error_trace"))
        {
            stackView.setText(getIntent().getStringExtra("error_trace"));
            Log.wtf("QPDEX", getIntent().getStringExtra("error_trace"));
        }

        //If we didn't run out of memory, load a background image
        if(!getIntent().getStringExtra("error_trace").contains("OutOfMemory")) {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            op.inSampleSize = 3;
            op.inDither = false;
            op.inPremultiplied = false;
            op.inScaled = true;
            LinearLayout lin = (LinearLayout) findViewById(R.id.lin_exceptionframe);
            lin.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.glitchbackground, op)));
            ImageView exceptMan = (ImageView) findViewById(R.id.exception_icon);
            exceptMan.setImageResource(R.drawable.missingnomin);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exception_caught, menu);
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

    public void onExitException(View view){
        //We could also implement methods to relaunch the qpdex on exception or submit a bug report to us.
        Intent resetApp = new Intent(getBaseContext(), IntroActivity.class);
        resetApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(resetApp);
        this.finish();
    }

    public void onBackPressed(){
        super.onBackPressed();
        onExitException(null);
    }
}
