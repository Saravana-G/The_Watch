package com.example.user.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;

/**
 * Created by user on 05-Jun-15.
 */
public class sign_up extends Activity {

    EditText email, user_name, password, confir_password;
    Button signup_button;
    PopupWindow pwindo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_sign_up);

        String fontPath = "fonts/oswaldbold.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        email = (EditText) findViewById(R.id.email_text);
        user_name = (EditText) findViewById(R.id.name_user);
        password = (EditText) findViewById(R.id.name_pass);
        confir_password = (EditText) findViewById(R.id.con_pass);

        signup_button = (Button) findViewById(R.id.sign_up_press);

        email.setTypeface(tf);
        user_name.setTypeface(tf);
        password.setTypeface(tf);
        confir_password.setTypeface(tf);
        signup_button.setTypeface(tf);
    }

    public void sign_up(View view) {
        boolean isWhitespace = user_name.getText().toString().matches(".*\\s+.*");
        if ((password.getText().toString().equals(confir_password.getText().toString())) && isWhitespace == false) {
            ParseUser user = new ParseUser();
            user.setUsername(user_name.getText().toString());
            user.setPassword(password.getText().toString());
            user.setEmail(email.getText().toString());
            user.put("Name","");
            pop_Up_display();
            //singing up into parse
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    pwindo.dismiss();
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        Intent intent = new Intent(getApplicationContext(), profile_details.class);
                        startActivity(intent);

                        finish();
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }

            });
        } else {
            if (isWhitespace == true)
                Toast.makeText(this, "Avoid spaces in username", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show();
        }
    }

    //popup window
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

}
