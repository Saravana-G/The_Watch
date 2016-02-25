package com.example.user.myapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.List;

/**
 * Created by user on 25-May-15.
 */
public class dashboard_fragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    ImageButton spo2_button, heart_button, steps_button, calory_button, temp_button;
    private TextView currentheart;
    private TextView currentspo;
    private TextView currentacc;
    private TextView currentemp;
    private TextView currentcalory;

    Runnable currentupdateTimer;

    DatabaseHandler dbhandler;


    final Handler currentupdateHandler = new Handler();




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboardxml, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        dbhandler =new DatabaseHandler(view.getContext());

        currentheart = (TextView) view.findViewById(R.id.textview_bpm);
        currentspo = (TextView) view.findViewById(R.id.textview_spo);
        currentacc = (TextView) view.findViewById(R.id.textview_steps);
        currentemp = (TextView) view.findViewById(R.id.textview_temp);
        currentcalory = (TextView) view.findViewById(R.id.textview_calory);
        spo2_button = (ImageButton) view.findViewById(R.id.soo2_button);
        heart_button = (ImageButton) view.findViewById(R.id.heart_button);
        steps_button = (ImageButton) view.findViewById(R.id.steps_button);
        calory_button = (ImageButton) view.findViewById(R.id.calory_button);
        temp_button = (ImageButton) view.findViewById(R.id.temp_button);
        spo2_button.setOnClickListener(this);
        heart_button.setOnClickListener(this);
        steps_button.setOnClickListener(this);
        calory_button.setOnClickListener(this);
        temp_button.setOnClickListener(this);
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        displaycurrentreadings();

    }
    //didplays the current value in the dashboard repeatedly
    private void displaycurrentreadings() {

        currentupdateTimer = new Runnable() {
            @Override
            public void run() {

              /*  try{
                    if(ParseUser.getCurrentUser()!=null) {
                        final ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername() + "_Heart");
                        query.fromLocalDatastore();
                        query.orderByAscending("Time");
                        query.setSkip(query.count() - 1);
                        Log.w("he",query.count()+"");
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, com.parse.ParseException e) {
                                if (list == null) {
                                    Log.d("score", e.getMessage());
                                } else {
                                    if (list.size() > 0) {
                                        String ss = list.get(0).getString("Value");
                                        Log.w("heart",ss);
                                        if (ss.startsWith("0"))
                                            ss = ss.substring(1);
                                        currentheart.setText(ss);
                                    }
                                }
                            }


                        });
                    }

                }*/
                try {
                    int a = dbhandler.getheartableCount();
                    Log.w("1234",a+"");
                    if (a != 0) {
                        Heartrate test = dbhandler.getlastheartdata(a);
                        String ss = test.getVal();
                        if (ss.startsWith("0"))
                            ss = ss.substring(1);
                        currentheart.setText(ss);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
              /*  try{
                    int a =dbhandler.getspotableCount();
                    if(a != 0){
                        Sporate test = dbhandler.getlastspodata(a);
                        String ss=test.getVal();
                        if(ss.startsWith("0"))
                            ss=ss.substring(1);
                        currentspo.setText(ss);
                        currentcalory.setText(ss);
                    }
                }*/
                try {
                    int a = dbhandler.getspotableCount();
                    if (a != 0) {
                        Sporate test = dbhandler.getlastspodata(a);
                        String ss = test.getVal();
                        if (ss.startsWith("0"))
                            ss = ss.substring(1);
                        currentspo.setText(ss);
                        currentcalory.setText(ss);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    int a =dbhandler.getacctableCount();
                    if(a != 0){
                        Accrate test = dbhandler.getlastaccdata(a);
                        String ss=test.getVal();
                        if(ss.startsWith("0"))
                            ss=ss.substring(1);
                        currentacc.setText(ss);

                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    int a =dbhandler.getemptableCount();
                    if(a != 0){
                        Temprate test = dbhandler.getlastempdata(a);
                        String ss=test.getVal();
                        if(ss.startsWith("0"))
                            ss=ss.substring(1);
                        currentemp.setText(ss);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                currentupdateHandler.postDelayed(this, 6000);
            }
        };
        currentupdateHandler.postDelayed(currentupdateTimer, 0);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.soo2_button) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Spograph.class);
            getActivity().startActivity(intent);
        }
        if (v.getId() == R.id.heart_button) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Heart_rate_graph.class);
            getActivity().startActivity(intent);

        }
        if (v.getId() == R.id.calory_button) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Calory_graph.class);
            getActivity().startActivity(intent);
        }
        if (v.getId() == R.id.steps_button) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), steps_activity.class);
            getActivity().startActivity(intent);
        }
        if (v.getId() == R.id.temp_button) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), temp_activity.class);
            getActivity().startActivity(intent);
        }
    }



}
