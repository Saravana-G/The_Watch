package com.example.user.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseUser;
public class simple_load extends Activity implements Animation.AnimationListener {
    ImageView back_ground;
    WebView gif;
    Animation animation2;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_loading_page);

        gif = (WebView) findViewById(R.id.gifImage);
        WebSettings settings = gif.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        gif.loadUrl("file:///android_asset/htic.gif");


        db= new DatabaseHandler(this);

        //SETTING A ANIMATION FOR LOAD PAGE
        back_ground = (ImageView)findViewById(R.id.bck);
        animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.quake);
        back_ground.startAnimation(animation2);
        animation2.setAnimationListener(this);


    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == animation2){
            //checking whether the user is logged in
            if(ParseUser.getCurrentUser()!=null) {
                if (!ParseUser.getCurrentUser().getString("Name").isEmpty()) {
                    db.fetchalldata();
                    startActivity(new Intent(this, main_activity_1.class));
                    finish();
                }
                else{
                    Toast.makeText(simple_load.this, "Please fill the profile details ", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), profile_details.class));
                    finish();
                }
            }
            else{
                startActivity(new Intent(this, log_in.class));
                finish();
            }
        }
    }
    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}

