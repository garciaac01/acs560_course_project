package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by garci on 11/17/2015.
 */
public class AddItemFragment extends DialogFragment {
    EditText itemToAdd;
    public static final String EXTRA_NEW_ITEM = "com.ipfw.myezshopper.new_item";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_shopping_list_item, null);

        itemToAdd = (EditText) v.findViewById(R.id.item_to_add);
        AlertDialog addItemAlert =  new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.add_new_item).setNegativeButton(android.R.string.cancel, addDialogListener).setPositiveButton(android.R.string.ok, addDialogListener).create();

        return addItemAlert;

    }

    DialogInterface.OnClickListener addDialogListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    sendResult(Activity.RESULT_OK, itemToAdd.getText().toString());
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void sendResult(int resultCode, String itemToAdd)
    {
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NEW_ITEM, itemToAdd);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
