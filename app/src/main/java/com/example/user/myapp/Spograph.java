package com.example.user.myapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.achartengine.util.MathHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Spograph extends ActionBarActivity {

    ActionBar actionBar;

    TextView title_text;
    ImageButton left_arrow, right_arrow;

    final Calendar c = Calendar.getInstance();
    int yy, mm, dd;

    DecimalFormat formatter = new DecimalFormat("00");

    private XYMultipleSeriesRenderer multiRenderer;
    private View mChart;
    public DatabaseHandler dbhandler;
    private ArrayList<String> spodata;
    private ArrayList<String> spotime;
    private ArrayList<String> spodate;
    private Calendar calendar;
    private TextView dateView;
    private TextView avg_points,tot_points;
    private Button go;
    private int year, month, day;
    final Handler sporateHandler = new Handler();
    private Runnable sporateTimer;
    private boolean zoomenabler = false;
    //  String selecteday = initialdisplay();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spograph);
        dbhandler = new DatabaseHandler(this);

        avg_points = (TextView) findViewById(R.id.avg_points_text);
        tot_points = (TextView) findViewById(R.id.tot_points_text);

        init_action_bar();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        sporateHandler.removeCallbacks(sporateTimer);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        spographdisplay();
    }

    public void spographdisplay() {

        sporateTimer = new Runnable() {
            @Override
            public void run() {

                currentdatacheck();
                Log.v("test", "fiyaz");
                sporateHandler.postDelayed(this, 10000);
            }
        };
        sporateHandler.postDelayed(sporateTimer, 0);

    }

    private void currentdatacheck() {
        // TODO Auto-generated method stub

        String currentdate = date();

        Log.d("Reading: ", "Reading specific rows..");
        List<Sporate> d = dbhandler.getselectedspodata(currentdate);
        if (d.size() > 0) {
            spodata = new ArrayList<String>();
            spodate = new ArrayList<String>();
            spotime = new ArrayList<String>();
            for (Sporate cn : d) {
                spodata.add(cn.getVal());
                spodate.add(cn.getdate());
                spotime.add(cn.getime());
                String log = "Id: " + cn.getID() + " ,Val: " + cn.getVal() + " ,date: " + cn.getdate() + ",time: " + cn.getime();
                Log.d("Name: ", log);
            }
            opengraph();
        } else {
            LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
            chartContainer.removeAllViews();
            showToast("No data available to display graph");

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            DatePickerDialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker arg0, int yy, int mm, int dd) {
            // TODO Auto-generated method stub
            // yy = year
            // mm = month
            // dd = day

            // showDate(yy, mm + 1, dd);

            String date = formatter.format(dd) + "-" + formatter.format(++mm) + "-" + formatter.format(yy);
            title_text.setText(date);
            on_date_click();
        }

    };

    /*
        private void showDate(int year, int month, int day) {
            dateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));

        }
    */
    /*
        public void ondateclick(View v) {
            showDialog(999);
            go.setEnabled(true);

        }
    */
    public void spo2_calender_click(View view) {
        showDialog(999);
        //  go.setEnabled(true);
    }

    /*
        public void onviewdateclick(View v) {
            String result = null;
          //  go.setEnabled(false);
          //  String day = dateView.getText().toString();
            Log.v("day", day);

            String loctem[] = day.split("/");
            if (loctem[0].length() < 2) {
                loctem[0] = "0" + loctem[0];
            }
            if (loctem[1].length() < 2) {
                loctem[1] = "0" + loctem[1];
            }
            loctem[2] = loctem[2].substring(2, loctem[2].length());
            result = loctem[0] + loctem[1] + loctem[2];
            getdatafromtable(result);
        }
    */
    public void on_date_click() {
        String result = null;
        String date = String.valueOf(title_text.getText());

        String dd = date.substring(0, 2);
        String mm = date.substring(3, 5);
        String yy = date.substring(8, 10);
        avg_points.setText("");
        tot_points.setText("");

        result = dd + mm + yy;
        getdatafromtable(result);
    }

    private void getdatafromtable(String result) {
        // TODO Auto-generated method stub
        if (result.equals(date())) {
            Log.d("same", "same date");
            spographdisplay();
        } else {
            //getting the data to plot in the graph from cloud
            sporateHandler.removeCallbacks(sporateTimer);
            Log.d("Reading: ", "Reading specific rows..");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername() + "_Spo");
            query.whereContains("Date", result);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list.size() > 0) {
                        spodata = new ArrayList<String>();
                        spodate = new ArrayList<String>();
                        spotime = new ArrayList<String>();
                        for (int i = 0; i < list.size(); i++) {
                            spodata.add(list.get(i).getString("Value"));
                            spodate.add(list.get(i).getString("Date"));
                            spotime.add(list.get(i).getString("Time"));
                        }
                        opengraph();
                    } else {
                        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
                        chartContainer.removeAllViews();
                        showToast("No data available to display graph");
                    }
                }
            });

        }
    }

    public void opengraph() {
        Double highestval = 0.0;
        XYSeries spoRate = new XYSeries("Spoval");
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        Double[] sval = new Double[spodata.size()];
        for (int i = 0; i < spodata.size(); i++) {
            sval[i] = Double.parseDouble(spodata.get(i));
        }
/*
        Double d1=0.5;
        Double[] sval = new Double[10];
        for (int i = 0; i < 10; i++) {
            sval[i] = d1;
            d1=d1+5.0;
        }
*/
        // Adding data to Series
        for (int i = 0; i < sval.length; i++) {
            spoRate.add(i, sval[i]);
            if (sval[i] > highestval) {
                highestval = sval[i];
            }

        }

        // Creating a dataset to hold each series
        dataset.addSeries(spoRate);
        // Creating XYSeriesRenderer to customize spoRate
        XYSeriesRenderer spoRenderer = new XYSeriesRenderer();
        spoRenderer.setColor(Color.WHITE);
        spoRenderer.setFillPoints(true);
        spoRenderer.setLineWidth(2);
        spoRenderer.setDisplayChartValues(true);
        spoRenderer.setDisplayChartValuesDistance(10);
        //	spoRenderer.setPointStyle(PointStyle.TRIANGLE);
        //	spoRenderer.setStroke(BasicStroke.DOTTED);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        multiRenderer = new XYMultipleSeriesRenderer();

        //   multiRenderer.setChartTitle("Patient Reading Graph on " + selecteday);
        //   multiRenderer.setXTitle("Time");
        //   multiRenderer.setYTitle("Readings obtained from device");

        /***
         * Customizing graphs
         */

        //setting text size of the title
        multiRenderer.setChartTitleTextSize(25);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(23);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(20);
        //setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(true, true);

        //setting click false on graph
        multiRenderer.setClickEnabled(false);
        //setting zoom to false on both axis
        multiRenderer.setZoomEnabled(true, true);
        //setting lines to display on y axis
        multiRenderer.setShowGridY(false);
        //setting lines to display on x axis
        multiRenderer.setShowGridX(false);
        //setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
        //disable the legend
        multiRenderer.setShowLegend(false);
        //setting displaying line on grid
        multiRenderer.setShowGrid(false);
        //setting zoom to false
        multiRenderer.setZoomEnabled(true);
        //setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
        //setting to in scroll to false
        multiRenderer.setInScroll(false);
        //setting to set legend height of the graph
        multiRenderer.setLegendHeight(200);
        multiRenderer.setLegendTextSize(25);
        multiRenderer.setXLabelsPadding(8);


        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        //multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));

        //to remove the background black color
        multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));

        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setScale(2f);
        //setting x axis point size
        multiRenderer.setPointSize(4f);
        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        multiRenderer.setXLabelsAlign(Align.CENTER);
        //setting no of values to display in y axis
        multiRenderer.setYLabels(10);
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
        // if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(highestval+5);
        multiRenderer.setYAxisMin(0);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(0.0);
        //setting used to move the graph on xaxiz to .5 to the right

        //setting bar size or space between two bars
   //     multiRenderer.setBarSpacing(0.3);

        if (spotime.size() <= 10) {
            multiRenderer.setXAxisMax(spotime.size());
            multiRenderer.setXLabelsAngle(360);
            multiRenderer.setXLabels(RESULT_OK);
            for (int i = 0; i < spotime.size(); i++) {
                multiRenderer.addXTextLabel(i, spotime.get(i));
            }
            int size = spotime.size();
            double con = (int) size;

            //double[] i = {0.0, con, 0.0, highestval + 10.0};
            double[] i = {0.0, con, 0.0, 100};
            multiRenderer.setZoomLimits(i);
            multiRenderer.setPanLimits(i);
        } else if (spotime.size() > 10 && spotime.size() < 20) {
            multiRenderer.setXAxisMax(spotime.size());
            multiRenderer.setXLabelsAngle(315);
            multiRenderer.setXLabels(RESULT_OK);
            for (int i = 0; i < spotime.size(); i++) {
                multiRenderer.addXTextLabel(i, spotime.get(i));
            }
            int size = spotime.size();
            double con = (int) size;
            double[] i = {0.0, con, 0.0, highestval + 10.0};
            multiRenderer.setZoomLimits(i);
            multiRenderer.setPanLimits(i);
        } else if (spotime.size() >= 20) {
            multiRenderer.setXAxisMax(20);
            multiRenderer.setXLabelsAngle(315);
            zoomenabler = true;
            multiRenderer.setXLabels(RESULT_OK);
            multiRenderer.addXTextLabel(0, spotime.get(0));
            multiRenderer.addXTextLabel(spotime.size() / 2, spotime.get(spotime.size() / 2));
            multiRenderer.addXTextLabel(spotime.size() - 1, spotime.get(spotime.size() - 1));
            int size = spotime.size();
            double con = (int) size;
            double[] i = {0.0, con, 0.0, highestval + 10.0};
            multiRenderer.setZoomLimits(i);
            multiRenderer.setPanLimits(i);
        }
        int size = spotime.size();
        double con = (int) size;
        double[] i = {0.0, con, 0.0, highestval};
        //	multiRenderer.setZoomLimits(i);
        //multiRenderer.setPanLimits(i);
        //	multiRenderer.setZoomInLimitY(highestval+10.0);
        //	multiRenderer.setZoomInLimitX(con+10.0);

        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same


        multiRenderer.addSeriesRenderer(spoRenderer);
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
        chartContainer.removeAllViews();
        mChart= ChartFactory.getLineChartView(Spograph.this, dataset, multiRenderer);
    //    mChart = ChartFactory.getBarChartView(Spograph.this, dataset, multiRenderer, Type.DEFAULT);
        chartContainer.addView(mChart);

        ((GraphicalView) mChart).addZoomListener(new ZoomListener() {
            public void zoomApplied(ZoomEvent e) {
                if (zoomenabler == true) {
                    double start = multiRenderer.getXAxisMin();
                    double stop = multiRenderer.getXAxisMax();
                    multiRenderer.setXLabels(RESULT_OK);
                    List<Double> labels = MathHelper.getLabels(start, stop, 15);

		         	/*  for (Double label : labels) {
                           int i = label.intValue();
		         		  multiRenderer.addXTextLabel(label, spotime.get(i));
		         	  }*/
                    for (int i = 0; i < spotime.size(); i++) {
                        multiRenderer.addXTextLabel(i, spotime.get(i));
                    }
                    zoomenabler = false;
                }
            }

            public void zoomReset() {
                showToast("hi");
            }
        }, true, true);

        ((GraphicalView) mChart).addPanListener(new PanListener() {
            @Override
            public void panApplied() {
                // showToast("hi");
            }
        });

        Double avg_value=0.0;
        for (int k = 0; k < sval.length; k++) {
            avg_value=avg_value+sval[k] ;
        }
        avg_value=avg_value/sval.length;
        tot_points.setText(String.valueOf(sval.length));
        avg_points.setText(String.valueOf(avg_value));

    }

    public String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    /*
        public String initialdisplay() {
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            String currentDateandTime = sdf.format(new Date());
            currentDateandTime = currentDateandTime.substring(0, 2) + "/" + currentDateandTime.substring(2, 4) + "/" + currentDateandTime.substring(4, currentDateandTime.length());
            return currentDateandTime;
        }
    */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void init_action_bar() {
        //setting the custom actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appp_bar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View Action_bar_view = mInflater.inflate(R.layout.date_bar_for_graph, null);
        actionBar.setCustomView(Action_bar_view);

        title_text = (TextView) Action_bar_view.findViewById(R.id.title_text);
        left_arrow = (ImageButton) Action_bar_view.findViewById(R.id.left_arrow);
        right_arrow = (ImageButton) Action_bar_view.findViewById(R.id.right_arrow);

        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH);
        dd = c.get(Calendar.DAY_OF_MONTH);


        String date = formatter.format(dd) + "-" + formatter.format(++mm) + "-" + formatter.format(yy);
        title_text.setText(date);
    }

    public void left_arrow_click(View view) {

        String date = String.valueOf(title_text.getText());

        String dd = date.substring(0, 2);
        String mm = date.substring(3, 5);
        String yy = date.substring(6, 10);

        int d1 = Integer.parseInt(dd);
        int m1 = Integer.parseInt(mm);
        int y1 = Integer.parseInt(yy);


        Calendar date1 = new GregorianCalendar(y1, m1, d1);
        date1.add(Calendar.DAY_OF_MONTH, -1);
        int y2 = date1.get(Calendar.YEAR);
        int m2 = date1.get(Calendar.MONTH);
        int d2 = date1.get(Calendar.DAY_OF_MONTH);

        if (m2 == 0){
            m2 = 12;
            y2--;
        }
        String st_date = formatter.format(d2) + "-" + formatter.format(m2) + "-" + formatter.format(y2);
        title_text.setText(st_date);

        on_date_click();
    }

    public void right_arrow_click(View view) {
        String date = String.valueOf(title_text.getText());

        String dd = date.substring(0, 2);
        String mm = date.substring(3, 5);
        String yy = date.substring(6, 10);

        int d1 = Integer.parseInt(dd);
        int m1 = Integer.parseInt(mm);
        int y1 = Integer.parseInt(yy);

        Calendar date1 = new GregorianCalendar(y1, --m1, d1);
        date1.add(Calendar.DAY_OF_MONTH, 1);
        int y2 = date1.get(Calendar.YEAR);
        int m2 = date1.get(Calendar.MONTH);
        int d2 = date1.get(Calendar.DAY_OF_MONTH);

        if (m2 == 0) m2 = 12;
        m2=m2%12;
        if (c.after(date1)) {
            String st_date = formatter.format(d2) + "-" + formatter.format(++m2) + "-" + formatter.format(y2);
            title_text.setText(st_date);
            on_date_click();
        }
    }

}
