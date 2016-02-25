package com.example.user.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.List;

/**
 * Created by user on 05-Jun-15.
 */
public class log_in extends Activity {

    EditText name, password;
    PopupWindow pwindo;
    DatabaseHandler db;
    public String settingsfile = "setfile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_log_in);

        String fontPath = "fonts/oswaldbold.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        name = (EditText) findViewById(R.id.name_user);
        name.setTypeface(tf);
        password = (EditText) findViewById(R.id.name_pass);
        name.setTypeface(tf);
        db = new DatabaseHandler(this);


        try {
            FileOutputStream fOut = openFileOutput(settingsfile, MODE_APPEND);
            fOut.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Onclick for login button
    public void login(View view) {
        pop_Up_display();
        //login method should pass the username and password

        ParseUser.logInInBackground(name.getText().toString(), password.getText().toString(), new LogInCallback() {

            @Override
            public void done(ParseUser parseUser, com.parse.ParseException e) {
                pwindo.dismiss();
                if (parseUser != null) {
                    // Hooray! The user is logged in.
                    if (parseUser.getBoolean("emailVerified") == true) {
                        if (!parseUser.getString("Name").isEmpty()) {
                            Log.w("df",parseUser.getString("Name"));
                            db.fetchalldata();
                            writesettingage(String.valueOf(parseUser.getInt("Age")));
                            startActivity(new Intent(getApplicationContext(), main_activity_1.class));
                            finish();
                        } else{
                            Toast.makeText(log_in.this,"Please fill the profile details ",Toast.LENGTH_LONG);
                            startActivity(new Intent(getApplicationContext(), profile_details.class));
                            finish();
                        }
                    } else
                        Toast.makeText(getApplication(), "Please verify your email", Toast.LENGTH_LONG).show();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });


    }


    private void writesettingage(String i) {
        // TODO Auto-generated method stub
        int age = Integer.parseInt(i);
        Log.d("age", "" + age);
        if (age >= 18 && age <= 35) {
            String separator = ",";
            String heartval = "DH010+++++qq";
            String spoval = "DS008+++++qq";
            String tempval = "DT010+++++qq";
            String interval = "DD010+++++qq";
            try {
                FileOutputStream fOut = openFileOutput(settingsfile, MODE_PRIVATE);
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

        } else if (age >= 36 && age <= 40) {
            String separator = ",";
            String heartval = "DH008+++++qq";
            String spoval = "DS010+++++qq";
            String tempval = "DT008+++++qq";
            String interval = "DD010+++++qq";
            try {
                FileOutputStream fOut = openFileOutput(settingsfile, MODE_PRIVATE);
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
        } else if (age >= 41 && age <= 55) {
            String separator = ",";
            String heartval = "DH006+++++qq";
            String spoval = "DS012+++++qq";
            String tempval = "DT006+++++qq";
            String interval = "DD010+++++qq";
            try {
                FileOutputStream fOut = openFileOutput(settingsfile, MODE_PRIVATE);
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
        } else if (age >= 56) {
            String separator = ",";
            String heartval = "DH006+++++qq";
            String spoval = "DS012+++++qq";
            String tempval = "DT006+++++qq";
            String interval = "DD010+++++qq";
            try {
                FileOutputStream fOut = openFileOutput(settingsfile, MODE_PRIVATE);
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
        }

    }


    //to create popup window
    private void pop_Up_display() {
        try {
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.screen_popup,
                    (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 300, 100, true);
            pwindo.setFocusable(true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Onclick for register button
    public void register(View view) {
        startActivity(new Intent(this, sign_up.class));

    }

    public void forgetpass_onclick(View v) {
        startActivity(new Intent(this, forget.class));
    }
}
