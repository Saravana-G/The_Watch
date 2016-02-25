package com.example.user.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.FileInputStream;

/**
 * Created by user on 25-May-15.
 */
public class measurement_fragment extends android.support.v4.app.Fragment {


    private EditText editheart;
    private EditText editspo;
    private EditText edittemp;
    private EditText editinter;
    private Button setbutton, changebutton;
    private String separator = ",";
    public String settingsfile = "setfile.txt";
    private PopupWindow pwindo;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings
                , container, false);
        String fontPath = "fonts/oswaldbold.ttf";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), fontPath);

        editheart = (EditText) view.findViewById(R.id.heartrate);
        editspo = (EditText) view.findViewById(R.id.spo2);
        edittemp = (EditText) view.findViewById(R.id.temp);
        editinter = (EditText) view.findViewById(R.id.interval);

        editheart.setTypeface(tf);
        editspo.setTypeface(tf);
        edittemp.setTypeface(tf);
        editinter.setTypeface(tf);

        setbutton = (Button) view.findViewById(R.id.set);
        changebutton = (Button) view.findViewById(R.id.change);
        setbutton.setOnClickListener(listener);
        changebutton.setOnClickListener(changelistener);


        //setting the receiver to true action
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(data_receive, new IntentFilter("main"));
        return view;
    }


    //activated when a data is broadcasted
    BroadcastReceiver data_receive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String broadreceive = intent.getStringExtra("main");
            if ("notconnected".equals(broadreceive)) {
                pwindo.dismiss();
                showToast("Please connect the device");

            } else if ("success".equals(broadreceive)) {
                pwindo.dismiss();
                intent = new Intent(getActivity(), main_activity_1.class);
                startActivity(intent);
            }

        }

    };

    @Override
    public void onResume() {
        super.onResume();
        //	editheart.setFocusable(false);
        editheart.setEnabled(false);
        editspo.setEnabled(false);
        edittemp.setEnabled(false);
        editinter.setEnabled(false);
        setbutton.setEnabled(false);


        FileInputStream fin = null;
        try {
            fin = getActivity().openFileInput("setfile.txt");

            int c;
            String temp = "";
            if ((c = fin.read()) != -1) {
                Log.v("setfile", "notnull");
                do {
                    temp = temp + Character.toString((char) c);
                } while ((c = fin.read()) != -1);
                temp = temp.substring(0, temp.length());
                String ar[] = temp.split(",");
                String heartdata = datadisplay(ar[0]);
                String spodata = datadisplay(ar[1]);
                String tempdata = datadisplay(ar[2]);
                String interdata = datadisplay(ar[3]);
                editheart.setText(heartdata.toString());
                editspo.setText(spodata.toString());
                edittemp.setText(tempdata.toString());
                editinter.setText(interdata.toString());


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    View.OnClickListener changelistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editheart.setEnabled(true);
            editspo.setEnabled(true);
            edittemp.setEnabled(true);
            editinter.setEnabled(true);
            setbutton.setEnabled(true);
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            String h = editheart.getText().toString();
            String s = editspo.getText().toString();
            String t = edittemp.getText().toString();
            String i = editinter.getText().toString();
            if (h.length() == 2) {
                h = "0" + h;
            } else if (h.length() == 1) {
                h = "00" + h;
            }
            if (s.length() == 2) {
                s = "0" + s;
            } else if (s.length() == 1) {
                s = "00" + s;
            }
            if (t.length() == 2) {
                t = "0" + t;
            } else if (t.length() == 1) {
                t = "00" + t;
            }
            if (i.length() == 2) {
                i = "0" + i;
            } else if (i.length() == 1) {
                i = "00" + i;
            }
            String heartval = "DH" + h;
            String spoval = "DS" + s;
            String tempval = "DT" + t;
            String interval = "DD" + i;


            heartval = heartval + datastruct(heartval.length());
            spoval = spoval + datastruct(spoval.length());
            tempval = tempval + datastruct(tempval.length());
            interval = interval + datastruct(interval.length());

            try {
                FileOutputStream fOut = getActivity().openFileOutput(settingsfile, 0x0000);
                fOut.write(heartval.getBytes());
                fOut.write(separator.getBytes());
                fOut.write(spoval.getBytes());
                fOut.write(separator.getBytes());
                fOut.write(tempval.getBytes());
                fOut.write(separator.getBytes());
                fOut.write(interval.getBytes());
                fOut.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final Intent intent = new Intent("true");
            intent.putExtra("title", "push");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            pop_Up_display();
            //Toast.makeText(getApplicationContext(), "Please push the updated settings to the Device.By clicking 'Push Button' in ActionBar", Toast.LENGTH_LONG).show();
            editheart.setEnabled(false);
            editspo.setEnabled(false);
            edittemp.setEnabled(false);
            editinter.setEnabled(false);
            setbutton.setEnabled(false);


        }

    };

    private void pop_Up_display() {
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.screen_popup,
                    (ViewGroup) getView().findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 300, 100, true);
            pwindo.setFocusable(true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String datastruct(int val) {
        String plus = "";
        for (int i = 0; i < 10 - val; i++) {
            plus = plus + "+";
        }
        plus = plus + "qq";
        return plus;

    }

    public String datadisplay(String data) {
        String val = data.toString().substring(2, 10);
        String test = "";

        for (int i = 0; i < val.length(); i++) {
            test = test + val.charAt(i);
            if (test.endsWith("+")) {
                break;
            }

        }
        String result = test.substring(0, test.length() - 1);
        Log.v("test", result);
        return result;
    }


    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


}
