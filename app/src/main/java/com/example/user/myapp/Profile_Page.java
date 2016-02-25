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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created by user on 05-Jun-15.
 */
public class Profile_Page extends Activity implements AdapterView.OnItemSelectedListener{
    private static int RESULT_LOAD_IMG = 1;
    private EditText name_edittext, age_edittext, weight_edittext, height_edittext, doctor_edittext, doctor_phone_edittext;
    private Spinner gender_spinner, disease_spinner;
    private Button set_button, edit_button, photo_button;
    private String disease_list[] = new String[]{"DISEASE", "DISEASE1", "DISEASE2", "DISEASE3"};
    private String gender_list[] = new String[]{"GENDER", "MALE", "FEMALE"};
    private ArrayAdapter<String> adapter_gender, adapter_disease;
    private String patientprofile = "profile.txt";
    private ImageView male_image, female_image, disease_image, profile_pic;
    private String gender = "Male", disease = "Disease1", separator = ",", imgDecodableString;
    private boolean flag = false, gender_flag = false, disease_flag = false;
    byte[] byteArray;
    PopupWindow pwindo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        String fontPath = "fonts/oswaldbold.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);


        name_edittext = (EditText) findViewById(R.id.name_edittext);
        age_edittext = (EditText) findViewById(R.id.age_edittext);
        weight_edittext = (EditText) findViewById(R.id.weight_editview);
        height_edittext = (EditText) findViewById(R.id.height_editview);
        doctor_edittext = (EditText) findViewById(R.id.doctor_editview);
        doctor_phone_edittext = (EditText) findViewById(R.id.doctor_phone_editview);

        name_edittext.setTypeface(tf);
        age_edittext.setTypeface(tf);
        weight_edittext.setTypeface(tf);
        height_edittext.setTypeface(tf);
        doctor_edittext.setTypeface(tf);
        doctor_phone_edittext.setTypeface(tf);

        gender_spinner = (Spinner) findViewById(R.id.gender_spinner1);
        disease_spinner = (Spinner) findViewById(R.id.disease_spinner1);

        set_button = (Button) findViewById(R.id.set_button);
        edit_button = (Button) findViewById(R.id.edit_button);
        photo_button = (Button) findViewById(R.id.photo_button);

        male_image = (ImageView) findViewById(R.id.male_imageview);
        female_image = (ImageView) findViewById(R.id.female_imageview);
        disease_image = (ImageView) findViewById(R.id.disease_imageview);
        profile_pic = (ImageView) findViewById(R.id.profileimage1);

        adapter_disease = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, disease_list);
        adapter_gender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender_list);

        disease_spinner.setAdapter(adapter_disease);
        gender_spinner.setAdapter(adapter_gender);
        disease_spinner.setOnItemSelectedListener(this);
        gender_spinner.setOnItemSelectedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == false) {
            //disabling the edittext
            name_edittext.setEnabled(false);
            age_edittext.setEnabled(false);
            gender_spinner.setVisibility(View.INVISIBLE);
            weight_edittext.setEnabled(false);
            height_edittext.setEnabled(false);
            disease_spinner.setVisibility(View.INVISIBLE);
            doctor_edittext.setEnabled(false);
            doctor_phone_edittext.setEnabled(false);
            male_image.setVisibility(View.INVISIBLE);
            female_image.setVisibility(View.INVISIBLE);
            disease_image.setVisibility(View.INVISIBLE);
            set_button.setEnabled(false);
            photo_button.setEnabled(false);
            //extracting the data from parse and setting it to the textfields
            name_edittext.setText(ParseUser.getCurrentUser().getString("Name"));
            age_edittext.setText(String.valueOf(ParseUser.getCurrentUser().getInt("Age")));
            if (ParseUser.getCurrentUser().getString("Gender").equals("Male")) {
                male_image.setVisibility(View.VISIBLE);
            } else if (ParseUser.getCurrentUser().getString("Gender").equals("Female")) {
                female_image.setVisibility(View.VISIBLE);
            }
            weight_edittext.setText(String.valueOf(ParseUser.getCurrentUser().getInt("Weight")));
            height_edittext.setText(String.valueOf(ParseUser.getCurrentUser().getInt("Height")));
            disease_image.setVisibility(View.VISIBLE);
            doctor_edittext.setText(ParseUser.getCurrentUser().getString("Doctor_Name"));
            doctor_phone_edittext.setText(ParseUser.getCurrentUser().getString("Doctor_phone"));
            try
            {
            ParseFile parsefile = ParseUser.getCurrentUser().getParseFile("profile_pic");
            parsefile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profile_pic.setImageBitmap(bitmap);
                    byteArray = bytes;
                }

            });
            }catch(Exception e)
            {

            }
            flag = true;
        }

    }

    public void edit_onclick(View v) {
        name_edittext.setEnabled(true);
        age_edittext.setEnabled(true);
        gender_spinner.setVisibility(View.VISIBLE);
        disease_spinner.setPrompt("Select Gender");
        weight_edittext.setEnabled(true);
        height_edittext.setEnabled(true);
        disease_spinner.setVisibility(View.VISIBLE);
        disease_spinner.setPrompt("Select Disease");
        doctor_edittext.setEnabled(true);
        doctor_phone_edittext.setEnabled(true);
        male_image.setVisibility(View.INVISIBLE);
        female_image.setVisibility(View.INVISIBLE);
        disease_image.setVisibility(View.INVISIBLE);
        set_button.setEnabled(true);
        photo_button.setEnabled(true);
    }

    public void set_onclick(View v) {

        if (gender_flag && disease_flag) {
            pop_Up_display();
            //creating a file to save the image
            ParseFile files = new ParseFile("pnd.png",byteArray);
            //creating a reference for current user to save the modified details
            ParseUser parseUser=ParseUser.getCurrentUser();
                    parseUser.put("Name", name_edittext.getText().toString());
                    parseUser.put("Age", Integer.parseInt(age_edittext.getText().toString()));
                    parseUser.put("Gender", gender);
                    parseUser.put("Weight", Integer.parseInt(weight_edittext.getText().toString()));
                    parseUser.put("Height", Integer.parseInt(height_edittext.getText().toString()));
                    parseUser.put("Disease", disease);
                    parseUser.put("Doctor_Name", doctor_edittext.getText().toString());
                    parseUser.put("Doctor_phone", doctor_phone_edittext.getText().toString());
                    parseUser.put("profile_pic", files);
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                pwindo.dismiss();
                                finish();
                            } else
                                Log.w("parse", e.getMessage());
                        }
                    });
         /*   try {
                FileOutputStream fOut = openFileOutput(patientprofile, MODE_PRIVATE);

                fOut.write(name_edittext.getText().toString().getBytes());
                fOut.write(separator.getBytes());
                fOut.write(age_edittext.getText().toString().getBytes());
                fOut.write(separator.getBytes());
                fOut.write(gender.getBytes());
                fOut.write(separator.getBytes());
                fOut.write(weight_edittext.getText().toString().getBytes());
                fOut.write(separator.getBytes());
                fOut.write(height_edittext.getText().toString().getBytes());
                fOut.write(separator.getBytes());
                fOut.write(disease.getBytes());
                fOut.write(separator.getBytes());
                fOut.write(doctor_edittext.getText().toString().getBytes());
                fOut.write(separator.getBytes());
                fOut.write(doctor_phone_edittext.getText().toString().getBytes());
              //  fOut.write(separator.getBytes());
             //   fOut.write(imgDecodableString.getBytes());


                fOut.close();
               Intent intent = new Intent(this, main_activity_1.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        } else {
            Toast.makeText(this, "Please select all the fields", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
        Spinner spin = (Spinner) parent;
        if (spin.getId() == R.id.gender_spinner1) {
            if (i == 0)
                gender_flag = false;
            if (i == 1) {
                gender = "Male";
                gender_flag = true;
            }
            if (i == 2) {
                gender = "Female";
                gender_flag = true;
            }
        }
        if (spin.getId() == R.id.disease_spinner1) {
            if (i == 0)
                disease_flag = false;
            if (i == 1) {
                disease = "Disease1";
                disease_flag = true;
            }
            if (i == 2) {
                disease = "Disease2";
                disease_flag = true;
            }
            if (i == 3) {
                disease = "Disease3";
                disease_flag = true;
            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Onclick for upload button
    public void change_onclick(View view) {
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
                // Set the Image in ImageView after decoding the String
                BitmapFactory.Options bounds = new BitmapFactory.Options();

                bounds.inSampleSize = 8;
                profile_pic.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString,bounds));
                Bitmap bit = BitmapFactory
                        .decodeFile(imgDecodableString,bounds);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.WEBP, 20, stream);
                byteArray= null;
                byteArray = stream.toByteArray();
            } else {

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
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

}
