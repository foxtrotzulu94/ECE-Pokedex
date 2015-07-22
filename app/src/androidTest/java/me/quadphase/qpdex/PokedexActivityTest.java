package me.quadphase.qpdex;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.TextView;


public class PokedexActivityTest
    extends ActivityInstrumentationTestCase2<PokedexActivity> {

    private PokedexActivity mPokedexActivity;
    private ImageView mTestSprite;
    private TextView mDescription;

    public PokedexActivityTest() {
        super(PokedexActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        mPokedexActivity = getActivity();
        mTestSprite = (ImageView) mPokedexActivity.findViewById(R.id.imageButton);
        mDescription = (TextView) mPokedexActivity.findViewById(R.id.textView4);
    }

    public void testPreconditions() {
        assertNotNull("mPokedexActivity is null", mPokedexActivity);
        assertNotNull("mTestSprite is null", mTestSprite);
        assertNotNull("mTitle is null", mDescription);
    }


    //TODO: Test for non placeholder text/var names
    public void testPokedexActivity_DescriptionText() {
        final String expected = "[ENTER POKEDEX ENTRY]";
        final String actual = mDescription.getText().toString();
        assertEquals(expected, actual);

    }
}
