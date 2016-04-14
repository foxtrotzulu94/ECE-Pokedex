package me.quadphase.qpdex;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by foxtrot on 12/09/15.
 */
public class TrainerEditInfoDialog extends DialogFragment{

    private View mainView;

    private String[] regionList;

    private Spinner classSelector;
    private Spinner regionSelector;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mainView = inflater.inflate(R.layout.dialog_trainerinfo, null);

        //TODO: Package in Bundle!
        regionSelector = (Spinner) mainView.findViewById(R.id.spinner_trainerregion);
        ArrayAdapter<String> dropdownValues = new ArrayAdapter<String>(
                getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                new String[] {"Kalos","Unova","Sinnoh","Hoenn","Johto","Kanto"});
        regionSelector.setAdapter(dropdownValues);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mainView)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("QPDEX_MyTrainer","Dialog completed successfully");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TrainerEditInfoDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
