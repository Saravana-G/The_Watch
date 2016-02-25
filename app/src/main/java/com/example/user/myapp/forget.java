package com.example.user.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.text.ParseException;

/**
 * Created by USER on 24-06-2015.
 */
public class forget extends Activity {

    private EditText email;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetxml);
        email = (EditText) findViewById(R.id.email_edittext);

    }
    public void reset_onclick(View v)
    {
        ParseUser.requestPasswordResetInBackground(email.getText().toString(), new RequestPasswordResetCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Toast.makeText(forget.this, "Please check the mail", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),log_in.class));
                    finish();
                    // An email was successfully sent with reset instructions.
                } else {
                    Toast.makeText(forget.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Something went wrong. Look at the ParseException to see what's up.
                }
            }
        });
    }

}
