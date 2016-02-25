package com.example.user.myapp;
import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "RvjiZpyV0Jgi26zxhN2L9kPtQCgVsaZyYdmrL6Ii", "cUZnyT8psFfvyLFDhiMQWygonVMXSo7QkEPrz0mS");
    }
}