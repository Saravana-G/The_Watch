package com.example.user.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;


import com.parse.ParseFile;
import com.parse.ParseUser;

import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;


public class profile_details extends Activity implements AdapterView.OnItemSelectedListener {
    private static int RESULT_LOAD_IMG = 1;
    public String imgDecodableString;
  //  public String settingsfile = "setfile.txt";
  //  public String patientprofile = "profile.txt";
    String separator = ",", patientname, patientage, doctorname, height, weight, doctorphone, selectedisease = "Disease", sex = "Gender";
    PopupWindow pwindo;

    Spinner diseaseE, genderE;
    ArrayAdapter<String> dataAdapter, dataAdapter_GENDER;
    EditText name, age, wtE, htE, doctor, doctor_phone;

    byte[] byteArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_main);


        String fontPath = "fonts/oswaldbold.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        diseaseE = (Spinner) findViewById(R.id.spinner_disease);
        genderE = (Spinner) findViewById(R.id.spinner_gender);
        name = (EditText) findViewById(R.id.nameE);
        age = (EditText) findViewById(R.id.ageE);
        wtE = (EditText) findViewById(R.id.WtE);
        htE = (EditText) findViewById(R.id.HtE);
        doctor = (EditText) findViewById(R.id.doctorE);
        doctor_phone = (EditText) findViewById(R.id.doctor_phoneE);

        name.setTypeface(tf);
        age.setTypeface(tf);
        wtE.setTypeface(tf);
        htE.setTypeface(tf);
        doctor.setTypeface(tf);
        doctor_phone.setTypeface(tf);

        String list[] = new String[]{"Select Disease", "DISEASE1", "DISEASE2", "DISEASE3"};
        //setting the adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                list);
        diseaseE.setAdapter(dataAdapter);
        diseaseE.setOnItemSelectedListener(this);

        String gender_list[] = new String[]{"Gender", "MALE", "FEMALE"};
        dataAdapter_GENDER = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                gender_list);
        genderE.setAdapter(dataAdapter_GENDER);
        genderE.setOnItemSelectedListener(this);

        Bitmap bit = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.male);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.WEBP, 20, stream);
        byteArray = stream.toByteArray();


//profile page text file
/*
        try {
            FileOutputStream fOut = openFileOutput(patientprofile, MODE_APPEND);
            fOut.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
*/

    }


    //Onclick for upload button
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                BitmapFactory.Options bounds = new BitmapFactory.Options();

                bounds.inSampleSize = 16;
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString, bounds));
                Log.w("",imgDecodableString);
                Bitmap bit = BitmapFactory
                        .decodeFile(imgDecodableString, bounds);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.WEBP, 10, stream);
                byteArray = stream.toByteArray();


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    //Onclick for proceed button
    public void Proceed(View view) {
        patientname = name.getText().toString();
        patientage = age.getText().toString();
        doctorname = doctor.getText().toString();
        doctorphone = doctor_phone.getText().toString();
        weight = wtE.getText().toString();
        height = htE.getText().toString();
        if (!patientname.trim().equals("") && !patientage.trim().equals("") && !doctorname.trim().equals("") && !doctorphone.trim().equals("") && !weight.trim().equals("") && !height.trim().equals("") && sex != "Gender" && selectedisease != "Disease") {
            pop_Up_display();
            Log.w(patientage + patientname + sex + selectedisease, "");
            writeprofile();


        } else Toast.makeText(this, "Please select all the fields", Toast.LENGTH_LONG).show();

    }

    public void writeprofile() {
     /*   Log.d("helo", "test");
        try {
            FileOutputStream fOut = openFileOutput(patientprofile, MODE_PRIVATE);
            fOut.write(patientname.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(patientage.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(sex.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(weight.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(height.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(selectedisease.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(doctorname.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(doctorphone.getBytes());
            fOut.write(separator.getBytes());
            fOut.write(imgDecodableString.getBytes());

            fOut.close();

        } catch (Exception e) {

        }*/
        //creating a file to save the image
        ParseFile files = new ParseFile(byteArray);
        //creating a reference for current user to save the user details
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("Name", patientname);
        parseUser.put("Age", Integer.parseInt(patientage));
        parseUser.put("Gender", sex);
        parseUser.put("Weight", Integer.parseInt(weight));
        parseUser.put("Height", Integer.parseInt(height));
        parseUser.put("Disease", selectedisease);
        parseUser.put("Doctor_Name", doctorname);
        parseUser.put("Doctor_phone", doctorphone);
        parseUser.put("profile_pic", files);
        parseUser.put("Mac_address","");
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                pwindo.dismiss();
                if (e == null) {
                    ParseUser.logOut();
                    Intent i = new Intent(getApplicationContext(), log_in.class);
                    startActivity(i);
                    finish();
                } else
                    Log.w("parse", e.getMessage());
            }

        });

    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spin = (Spinner) adapterView;
        if (spin.getId() == R.id.spinner_gender) {
            if (i == 0)
                sex = "Gender";
            if (i == 1)
                sex = "Male";
            if (i == 2)
                sex = "Female";
        }
        if (spin.getId() == R.id.spinner_disease) {
            if (i == 0)
                selectedisease = "Disease";
            if (i == 1)
                selectedisease = "Disease1";
            if (i == 2)
                selectedisease = "Disease2";
            if (i == 3)
                selectedisease = "Disease3";
        }

    }

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


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}