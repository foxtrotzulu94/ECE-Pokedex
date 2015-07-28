package me.quadphase.qpdex;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/*
 * This code is based on the "ForceClose Example made by Hardik Trivedi
 * Original Respository is here: https://github.com/hardik-trivedi/ForceClose
 */

public class ExceptionCaughtActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_caught);
        //Get the textView
        TextView stackView = (TextView) findViewById(R.id.textview_errorstack);
        stackView.setMovementMethod(new ScrollingMovementMethod());
        //Show the error trace
        if(getIntent().hasExtra("error_trace"))
        {
            stackView.setText(getIntent().getStringExtra("error_trace"));
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
}
