package com.example.user.myapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseObject;

/**
 * Created by user on 25-May-15.
 */
public class devicesettings_fragment extends android.support.v4.app.Fragment  {

    Button clear_button;
    DatabaseHandler dbhandle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        View view=inflater.inflate(R.layout.devicesettingsxml,container,false);

        clear_button = (Button) view.findViewById(R.id.clear_button);
        dbhandle = new DatabaseHandler(getActivity());


        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setIcon(R.drawable.warning).setTitle(R.string.msg_quit_title)
                        .setMessage(R.string.msg_quit_detail).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ParseObject.unpinAllInBackground();
                        getActivity().deleteFile("mydevice.txt");
                        getActivity().deleteFile("profile.txt");
                        getActivity().deleteFile("setfile.txt");
                        Log.d("Deleting: ", "deleting all row");
                        int a = dbhandle.deleteAllheart();
                        Log.v("rows",""+a);
                        int b = dbhandle.deleteAllspo();
                        Log.v("rows",""+b);
                        int c = dbhandle.deleteAllacc();
                        Log.v("rows",""+c);
                        int d = dbhandle.deleteAlltemp();
                        Log.v("rows",""+d);
                        getActivity().finish();
                    }
                }).setNegativeButton(R.string.no, null).show();
            }
        });
        return view;
    }

}
