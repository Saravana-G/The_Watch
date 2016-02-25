package com.example.user.myapp;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.NavigationView;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;


import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blueradios.Brsp;
import com.blueradios.BrspCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 23-May-15.
 */
public class main_activity_1 extends AppCompatActivity {

    public static final int REQUEST_SELECT_DEVICE = 0;
    public static final int REQUEST_ENABLE = 1;
    private DrawerLayout drawerLayout;

    byte[] bytearray;


    NavigationView view;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothDevice selectedDeviceItem;

    public DatabaseHandler dbhandler;

    private ArrayList<String> ar;
    private ArrayList<BluetoothDevice> pairedDevices;

    private Intent broadcastintent;


    private String patientprofile = "profile.txt";
    private String phoneno;
    private String filedata;
    private String data1[] = new String[10];

    private ImageView profile;
    private ImageView imgBlink;
    private TextView name_header, email_header;

    public Animation anima;

    private int inrc = 1;
    private int Settingstimer = 0;
    private int synTimer = 0;
    private boolean incomingdata = false;
    private boolean bodytemp = false;
    private boolean Ackchck = false;
    private boolean dbat = false;
    private boolean panicalert = false;
    private boolean popup_flag = false;
    private boolean flag = false;

    private AlertDialog panicDialog;

    MenuItem item;
    MenuItem connect;


    final Handler pushsettingsHandler = new Handler();
    final Handler synchHandler = new Handler();
    final Handler connectHandler = new Handler();
    private Runnable pushsettingsTimer, synchTimer, connectTimer;


    private final String TAG = "Saravana" + this.getClass().getSimpleName();

    private Brsp _brsp;

    Boolean pair_flag = false;


    private BrspCallback _brspCallback = new BrspCallback() {

        @Override
        public void onSendingStateChanged(Brsp obj) {
            Log.d(TAG, "onSendingStateChanged thread id:");
        }

        @Override
        public void onConnectionStateChanged(Brsp obj) {
            Log.d(TAG, "onConnectionStateChanged state:" + obj.getConnectionState() + " thread id:");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
                    //     BluetoothDevice currentDevice = brspObj.getDevice();
                    //     if (currentDevice != null && brspObj.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Creating bond for device:");
                    // currentDevice.getAddress());
                    // currentDevice.createBond();
                }
                //     }
            });
        }

        @Override
        public void onDataReceived(final Brsp obj) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte[] bytes = obj.readBytes();
                    obj.writeBytes(bytes);

                    if (bytes != null) {
                        String input = new String(bytes);
                        if (input.equals("ACK")) {
                            Ackchck = true;
                        }
                        if (input.equals("ACKL")) {
                            broadcastintent.putExtra("main", "success");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastintent);
                            //    setProgressBarIndeterminateVisibility(false);
                        }
                        if (input.equals("Dincome")) {
                            incomingdata = true;
                            //    synchHandler.removeCallbacks(synchTimer);
                        }
                        if (input.equals("Dend")) {
                            incomingdata = false;
                        }
                        if (input.equals("Cbat")) {
                            dbat = true;
                        }
                        if (input.equals("Ctemp")) {
                            bodytemp = true;
                        }
                        if (input.equals("PANALERT")) {
                            panicalert = true;
                        }
                        if (input.equals("SEND_DATE")) {
                            //   pop_Up_display();
                            pushData();
                        }
                        Toast.makeText(getBaseContext(), input, Toast.LENGTH_SHORT).show();
                        panicAlertcheck(input);
                        writdatatotable(input);
                        writebattery(input);
                        writeTemp(input);
                       /*	writeAlldata(input);*/

                    }
                }
            });
            super.onDataReceived(obj);

        }

        @Override
        public void onError(Brsp obj, Exception e) {
            Log.d(TAG, "onError:" + e.getMessage() + " thread id:");
            super.onError(obj, e);
        }

        @Override
        public void onBrspModeChanged(Brsp obj) {
            super.onBrspModeChanged(obj);
        }

        @Override
        public void onRssiUpdate(Brsp obj) {
            Log.d(TAG, "onRssiUpdate thread id:");
            super.onRssiUpdate(obj);
            Log.d(TAG, "Remote device RSSI:" + obj.getLastRssi()); // Log RSSI
        }

        @Override
        public void onBrspStateChanged(Brsp obj) {
            Log.d(TAG, "onBrspStateChanged thread id:");
            super.onBrspStateChanged(obj);
            obj.readRssi(); // read the RSSI once
            if (obj.getBrspState() == Brsp.BRSP_STATE_READY) {
                invalidateOptionsMenu();
                // Ready to write
                // _brsp.writeBytes("Test".getBytes());
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //attaching the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity1);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

 /*       try {
            FileOutputStream fOut = openFileOutput("mydevice.txt", MODE_APPEND);
            fOut.close();
        } catch (Exception e) {

        }
*/
        _brsp = new Brsp(_brspCallback, 10000, 10000);
        dbhandler = new DatabaseHandler(this);
        broadcastintent = new Intent("main");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // getting a reference to the view
        view = (NavigationView) findViewById(R.id.navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appp_bar);
        //setting the custom actionbar
        setSupportActionBar(toolbar);
        //setting the home button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.navicon);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        nav_header();

        data1 = this.getResources().getStringArray(R.array.dataItems);


        //checking the bluetooth
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (myBluetoothAdapter == null) {
            myBluetoothAdapter = bluetoothManager.getAdapter();
        }


        //listener for the list activity
        // view.setNavigationItemSelectedListener(this);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                //selecting the corresponding fragment & setting the actionbar title
                if (menuItem.getItemId() == R.id.dashboard_id) {
                    selectItem(0);
                }
                if (menuItem.getItemId() == R.id.chartview_id) {
                    selectItem(1);
                }
                if (menuItem.getItemId() == R.id.devicesettings_id) {
                    selectItem(2);
                }
                if (menuItem.getItemId() == R.id.measurement_id) {
                    selectItem(3);
                }
                if (menuItem.getItemId() == R.id.help_id) {
                    selectItem(4);
                }
                if (menuItem.getItemId() == R.id.logout_id) {
                    selectItem(5);
                }


                return true;
            }
        });


        dashboard_fragment one_fragmen = new dashboard_fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
        getSupportActionBar().setTitle(data1[0]);


        //setting the receiver to true action
        LocalBroadcastManager.getInstance(this).registerReceiver(data_receive, new IntentFilter("true"));
    }

    void nav_header() {
        //adding the profile pic ,email and name to the navigation header
        name_header = (TextView) findViewById(R.id.name_header);
        email_header = (TextView) findViewById(R.id.email_header);
        profile = (ImageView) findViewById(R.id.profile);

        //extracting the text from parse
        name_header.setText(ParseUser.getCurrentUser().getString("Name"));
        email_header.setText(ParseUser.getCurrentUser().getString("email"));
        phoneno = ParseUser.getCurrentUser().getString("Doctor_phone");
        //image is extracted and decoded
        try {
            ParseFile parsefile = ParseUser.getCurrentUser().getParseFile("profile_pic");
            parsefile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Log.w("dsf", bytes.length + "");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profile.setImageBitmap(bitmap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
   /*     FileInputStream fin = null;
        try {
            fin = openFileInput(patientprofile);
            int c;
            String temp = "";
            if ((c = fin.read()) != -1) {
                Log.v("patientfile", "notnull");
                do {
                    temp = temp + Character.toString((char) c);
                } while ((c = fin.read()) != -1);
                temp = temp.substring(0, temp.length());
                String ar[] = temp.split(",");



           //     name_header.setText(ParseUser.getCurrentUser().getString("Name"));
                phoneno=ar[7];
                    //setting the profile pic to the view
              //      profile.setImageBitmap(BitmapFactory
              //              .decodeFile(ar[8]));


            }
        }catch (Exception e) {

        }*/
    }

    //called when a item clicked or orientation changes to display the corresponding fragment
    private void selectItem(int i) {
        if (i == 0) {

            dashboard_fragment one_fragmen = new dashboard_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
            getSupportActionBar().setTitle("Dashboard");

        }
        if (i == 1) {

            startActivity(new Intent(this, Chart_Activity.class));
            Menu menu = view.getMenu();
            menu.getItem(0).setChecked(true);
            dashboard_fragment one_fragmen = new dashboard_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
            getSupportActionBar().setTitle("Dashboard");

        }
        if (i == 2) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            devicesettings_fragment one_fragmen = new devicesettings_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
            getSupportActionBar().setTitle("Device Settings");
        }
        if (i == 3) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            measurement_fragment one_fragmen = new measurement_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
            getSupportActionBar().setTitle("Measurement Settings");

        }
        if (i == 4) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            help_fragment one_fragmen = new help_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
            getSupportActionBar().setTitle("Help / Support");
        }
        if (i == 5) {
            //a alert window for logout
            new AlertDialog.Builder(this).setTitle("LOGOUT").setMessage("Are you sure you want to logout?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ParseUser.logOut();
                    finish();
                }
            }).setNegativeButton("No", null).show();
        }
        //closing the drawer after each click
        drawerLayout.closeDrawer(GravityCompat.START);

    }


    //activated when a data is broadcasted
    BroadcastReceiver data_receive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            if ("temp".equals(title)) {
                onDataSentoDevice("BODY TEMPRqq");
            }
            if ("push".equals(title)) {
                if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED)
                    pushData();
                else {
                    broadcastintent.putExtra("main", "notconnected");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastintent);
                }


            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            //connecting to the selected device and updating the my device file
            if (resultCode == RESULT_OK) {
                selectedDeviceItem = data.getParcelableExtra("device");

                String title = data.getStringExtra("title");
                setTitle(title);
                String mydevice = selectedDeviceItem.toString();
                //saving the mac address in the cloud
                ParseUser.getCurrentUser().put("Mac_address", mydevice);
                ParseUser.getCurrentUser().saveEventually();
                try {

                    FileOutputStream fOut = openFileOutput("mydevice.txt", MODE_PRIVATE);
                    fOut.write(mydevice.getBytes());
                    fOut.close();

                    doConnect();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checks whether the bletooth is enabled
        if (myBluetoothAdapter == null || !myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE);
        }
        //  resetdevice.setEnabled(false);

        //checks if a paired device available
        //if present connect withe the device directly
        //Or call the scanactivity for new device selection

    }

    @Override
    protected void onResume() {
        nav_header();
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //     connectHandler.removeCallbacks(connectTimer);
        doDisconnect();
    }

    private void doConnect() {
        Log.d(TAG, "Bond State:");
        if (selectedDeviceItem != null && _brsp.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED) {
            boolean result = false;
            Log.d(TAG, "Bond State:");
            String bondStateText = "";

            switch (selectedDeviceItem.getBondState()) {
                case BluetoothDevice.BOND_BONDED:
                    bondStateText = "BOND_BONDED";
                    break;
                case BluetoothDevice.BOND_BONDING:
                    bondStateText = "BOND_BONDING";
                    break;
                case BluetoothDevice.BOND_NONE:
                    bondStateText = "BOND_NONE";
                    break;
            }
            Log.d(TAG, "Bond State:" + bondStateText);

            result = _brsp.connect(this.getApplicationContext(), selectedDeviceItem);
            Log.d(TAG, "Connect result:" + result);
        }
    }

    private void doDisconnect() {
        Log.d(TAG, "Attempting to disconnect");
        if (_brsp.getConnectionState() != BluetoothGatt.STATE_DISCONNECTED)
            _brsp.disconnect();
    }

    private void panicAlertcheck(String input) {
        if (panicalert) {
            openalertdialog();
            panicalert = false;
        }

    }

    private void openalertdialog() {
        Toast.makeText(getApplicationContext(), phoneno, Toast.LENGTH_LONG).show();
        try {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(phoneno, null, "ODUNGA oduguna....", null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("PANIC ALERT");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(255, 0, 0));
        title.setTextSize(23);
        builder.setCustomTitle(title);
        // builder.setIcon(icon);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout_view = inflater.inflate(R.layout.panicalert, null);
        builder.setView(layout_view);
        builder.setCancelable(false);
        panicDialog = builder.create();
        panicDialog.show();
        onDataSentoDevice("PANIC MODEqq");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
        r.play();
        imgBlink = (ImageView) layout_view.findViewById(R.id.panicimage);
        blinkblinkImage(imgBlink);
        Button okbutton = (Button) layout_view.findViewById(R.id.panicokay);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                panicDialog.cancel();


            }
        });

    }

    protected void blinkblinkImage(ImageView img) {
        anima = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        anima.setDuration(400); // duration - half a second
        anima.setInterpolator(new LinearInterpolator()); // do not alter
        anima.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        anima.setRepeatMode(Animation.REVERSE); // Reverse animation at
        img.startAnimation(anima);
        //img.clearAnimation();

    }

    //updating the bulk value onto the database
    public void writdatatotable(String a) {
        if (incomingdata) {

            Log.v("incomedata", "true");
            Log.v("a", a);
            String data = null;
            if (a.length() > 0) {
                data = a.substring(0, 1);
                Log.v("data", data);
                if (data.equals("H")) {
                    Log.v("heart", a);
                    String loc[] = a.split(",");
                    loc[0] = loc[0].substring(1, loc[0].length());
                    loc[2] = loc[2].substring(0, loc[2].length() - 4);
                    loc[2] = loc[2].substring(0, 2) + ":" + loc[2].substring(2, loc[2].length());
                    Log.d("Insert: ", "Inserting into heartable");
                    dbhandler.addHeartdata(new Heartrate(loc[0], loc[1], loc[2]));

                } else if (data.equals("S")) {
                    Log.v("spo", a);
                    String loc[] = a.split(",");
                    loc[0] = loc[0].substring(1, loc[0].length());
                    loc[2] = loc[2].substring(0, loc[2].length() - 4);
                    loc[2] = loc[2].substring(0, 2) + ":" + loc[2].substring(2, loc[2].length());
                    Log.d("Insert: ", "Inserting into spotable");
                    dbhandler.addSpodata(new Sporate(loc[0], loc[1], loc[2]));
                } else if (data.equals("T")) {
                    Log.v("temp", a);
                    String loc[] = a.split(",");
                    loc[0] = loc[0].substring(1, loc[0].length());
                    float variable = Float.parseFloat(loc[0]);
                    variable = variable / 10;
                    String s = Float.toString(variable);
                    loc[2] = loc[2].substring(0, loc[2].length() - 4);
                    loc[2] = loc[2].substring(0, 2) + ":" + loc[2].substring(2, loc[2].length());
                    Log.d("Insert: ", "Inserting into temptable");
                    dbhandler.addTempdata(new Temprate(s, loc[1], loc[2]));
                } else if (data.equals("A")) {
                    Log.v("Acc", a);
                    String loc[] = a.split(",");
                    loc[0] = loc[0].substring(1, loc[0].length());
                    loc[2] = loc[2].substring(0, loc[2].length() - 4);
                    loc[2] = loc[2].substring(0, 2) + ":" + loc[2].substring(2, loc[2].length());
                    Log.d("Insert: ", "Inserting into acctable");
                    dbhandler.addAccdata(new Accrate(loc[0], loc[1], loc[2]));

                }
            }

        }
    }

    //sending the data to device
    public void onDataSentoDevice(String s) {
        Log.v("ondatsent", s);
        if (_brsp.getBrspState() == Brsp.BRSP_STATE_READY) {
            if (s.length() > 0) {
                String commandString = s + "\r";
                Log.v("commandString", commandString);
                _brsp.writeBytes(commandString.getBytes());
            }
        } else {
            Toast.makeText(this, "BRSP not ready", Toast.LENGTH_SHORT).show();
        }

    }

    //writing the temperature value in database
    public void writeTemp(String z) {
        if (bodytemp) {
            String dat = z.substring(0, 1);
            Log.v("bat", z);
            if (dat.equals("T")) {
                String loc[] = z.split(",");
                loc[0] = loc[0].substring(1, loc[0].length());
                float variable = Float.parseFloat(loc[0]);
                variable = variable / 10;
                String s = Float.toString(variable);
                loc[2] = loc[2].substring(0, loc[2].length() - 4);
                loc[2] = loc[2].substring(0, 2) + ":" + loc[2].substring(2, loc[2].length());
                Log.d("Insert: ", "Inserting into temptable");
                dbhandler.addTempdata(new Temprate(s, loc[1], loc[2]));
                bodytemp = false;
            }
        }
    }

    public void writebattery(String z) {
        if (dbat == true) {
            String dat = z.substring(0, 2);
            Log.v("bat", z);
            if (!dat.equals("Cb")) {
                float val = Float.parseFloat(z);
                val = (float) (((val - 3.3) / .9) * 100);
                int batteryval = (int) val;
                if (batteryval <= 15)
                    item.setIcon(R.drawable.batterylow);
                else if (batteryval <= 25)
                    item.setIcon(R.drawable.batteryquarter);
                else if (batteryval <= 50)
                    item.setIcon(R.drawable.batteryhalf);
                else if (batteryval <= 75)
                    item.setIcon(R.drawable.batterythreequarters);
                else if (batteryval <= 100)
                    item.setIcon(R.drawable.batteryfull);
                dbat = false;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        Log.w("iNSIDE", "oNCREATE");


        // FileInputStream fin = null;
        // try {
        //     fin = openFileInput("mydevice.txt");
        //getting the mac address associated with the current user

        //  int c;
        //   String temp = "";
          /*  if ((c = fin.read()) != -1) {
                Log.v("mydevice", "notnull");
                try {
                    do {
                        temp = temp + Character.toString((char) c);
                    } while ((c = fin.read()) != -1);*/
        String mac_addr;
        if (!ParseUser.getCurrentUser().getString("Mac_address").isEmpty() && !pair_flag) {
            mac_addr = ParseUser.getCurrentUser().getString("Mac_address");
            filedata = mac_addr;
            pairedevicelist();
            checkforpairedevice(filedata);
        } else if (!pair_flag) {
            Log.v("mydevice", "null");
            startActivityForResult(new Intent(this, ScanActivity.class), REQUEST_SELECT_DEVICE);
        }
        // } catch (Exception e) {
        //      e.printStackTrace();
        //  }
        return true;
    }

    //checks the connectivity periodically
    private void checkconnectivity(final MenuItem connect) {
        connectTimer = new Runnable() {
            @Override
            public void run() {
                //when connected image is displayed
                if (_brsp.getConnectionState() != BluetoothGatt.STATE_DISCONNECTED) {
                    connect.setIcon(R.drawable.pairingicon);
                    connect.setVisible(true);

                }


                //when disconnected image is invisible
                if (_brsp.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED) {
                    connect.setVisible(false);
                }
                connectHandler.postDelayed(this, 10000);
            }
        };
        connectHandler.postDelayed(connectTimer, 3000);

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item = menu.findItem(R.id.action_battery);
        connect = (MenuItem) menu.findItem(R.id.action_settings);
        checkconnectivity(connect);
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;


        //selecting the corresponding activity in action bar
        switch (item.getItemId()) {
            case R.id.action_battery:
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                synchdata();
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void synchdata() {
        if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED) {
            onDataSentoDevice("SYNC READGqq");
            synchfunction();
        } else {
            showToast("please connect to device");
        }
    }

    public void synchfunction() {
        synchTimer = new Runnable() {
            @Override
            public void run() {
                synTimer = synTimer + 1;
                if (synTimer > 5) {
                    onDataSentoDevice("SYNC READGqq");
                    synTimer = 0;
                }

                synchHandler.postDelayed(this, 3000);
            }
        };
        synchHandler.postDelayed(synchTimer, 3000);
    }

    //getting all the paired device from the phone bluetooth
    private void pairedevicelist() {
        // get paired devices
        pairedDevices = new ArrayList<BluetoothDevice>(myBluetoothAdapter.getBondedDevices());
        //  pairedDevices = myBluetoothAdapter.getBondedDevices();
        Log.v("pairedDevice", "" + pairedDevices.size());

    }

    //checking whether the device is present in paired list
    public void checkforpairedevice(String filedata) {

        Log.v("filedata", filedata);
        for (int j = 0; j < pairedDevices.size(); j++) {
            Log.v("filedata", j + pairedDevices.get(j).getAddress());
            if (pairedDevices.get(j).getAddress().equals(filedata)) {
                selectedDeviceItem = pairedDevices.get(j);
                Log.v("selectedDeviceItem", selectedDeviceItem.toString());
                doConnect();
                pair_flag = true;
                break;
            }
        }
        // Log.v("filedata", selectedDeviceItem.getAddress());
        if (pair_flag == false) {
            Log.w("sdf", "dcfdsf");
            startActivityForResult(new Intent(this, ScanActivity.class), REQUEST_SELECT_DEVICE);
            pair_flag = true;
        }

    }


    private void pushData() {
        if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED) {
            Senddevicesettings();
            onDataSentoDevice(ar.get(0));

            flag = true;
            pushsettingsTimer = new Runnable() {
                @Override
                public void run() {
                    if (flag == true) {
                        Log.v("flag", "true");
                        if (inrc < ar.size()) {
                            Acknowcheck(ar.get(inrc));

                        }
                        if (Settingstimer > 5) {
                            Settingstimer = 0;
                            onDataSentoDevice(ar.get(inrc - 1));
                        }
                    }

                    pushsettingsHandler.postDelayed(this, 5000);
                }
            };
            pushsettingsHandler.postDelayed(pushsettingsTimer, 0);

        } else {
            showToast("Please connect your Mobile with Device");
        }
    }

    private void Senddevicesettings() {
        FileInputStream fin = null;
        try {
            fin = openFileInput("setfile.txt");
            int c;
            String temp = "";
            if ((c = fin.read()) != -1) {
                Log.v("setfile", "notnull");
                do {
                    temp = temp + Character.toString((char) c);
                } while ((c = fin.read()) != -1);
                temp = temp.substring(0, temp.length());
                String loctemp[] = temp.split(",");
                String firstdata = "SYNC SETTGqq";
                ar = new ArrayList<String>();
                ar.add(firstdata);
                for (int k = 0; k < loctemp.length; k++) {
                    ar.add(loctemp[k]);
                    Log.v("arraydata", ar.get(k));
                }
                String currentDateandTime = dateTime();
                String currdat[] = currentDateandTime.split("_");
                String datevar = datetimeformate(currdat[0]);
                datevar = "Sd" + datevar + "qq";
                String timevar = datetimeformate(currdat[1]);
                timevar = "St" + timevar + "qq";
                ar.add(datevar);
                ar.add(timevar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Acknowcheck(String s) {
        if (Ackchck) {
            onDataSentoDevice(s);
            Ackchck = false;
            inrc++;
            Settingstimer = 0;
            Log.v("size outside", "" + inrc);
            if (ar.size() == inrc) {
                Log.v("size inside", "" + inrc);
                flag = false;
                inrc = 1;
                pushsettingsHandler.removeCallbacks(pushsettingsTimer);
            }
        } else {
            Settingstimer = Settingstimer + 1;
            showToast("waiting for acknownlegment");
        }
    }

    public String dateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public String datetimeformate(String a) {
        a = a.substring(0, 2) + ":" + a.substring(2, 4) + ":" + a.substring(4, 6);
        return a;
    }


    //onclick for profile header
    public void profile_click(View view) {
        startActivity(new Intent(this, Profile_Page.class));
    }

    //called when the back key is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //to call the alert dialog box
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            closeApp();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //method for creating the alert dialog box
    public void closeApp() {
        new AlertDialog.Builder(this).setTitle("QUIT").setMessage("Are you sure you want to close this app?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("No", null).show();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
