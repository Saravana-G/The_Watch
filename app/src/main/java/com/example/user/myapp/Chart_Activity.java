package com.example.user.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Chart_Activity extends Activity implements RangeSeekBar.OnRangeSeekBarChangeListener {
    RangeSeekBar<Integer> seekBar;
    ViewGroup seekbar_layout;

    private int min_value = 0;
    private int max_value = 1440;


    private int pixel_X = 0, pixel_Y = 0, pixel_X_end = 0;
    Canvas canvas;

    Bitmap bitmap;
    ImageView drawingImageView;

    HashMap<String, List<String>> category_left;
    List<String> category_list_left;
    ExpandableListView exp_list;
    ExpandableAdapter adapter_left;

    final Handler sporateHandler = new Handler();
    private Runnable sporateTimer;

    private ArrayList<String> spodata;
    private ArrayList<String> spotime;
    private ArrayList<String> spodate;

    private ArrayList<String> heartdata;
    private ArrayList<String> hearttime;
    private ArrayList<String> heartdate;

    private ArrayList<String> tempraturedata;
    private ArrayList<String> tempraturetime;
    private ArrayList<String> tempraturedate;

    private ArrayList<String> accdata;
    private ArrayList<String> acctime;
    private ArrayList<String> accdate;

    DatabaseHandler dbhandler;

    double highestval1 = 4000;
    double highestval2 = 4000;
    double highestval3 = 4000;
    double highestval4 = 4000;

    static double ps1=0,ps2=0,ps3=0,ps4=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        new MockData_chart_view();

        //calculating the current time in minutes
        double tot_min = 0;
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int am_pm = c.get(Calendar.AM_PM);
        tot_min = (hour * 60) + minute;
        if (am_pm == 1) tot_min = tot_min + (12 * 60);

        //setting the range beek limits 0 t0 1440 minutes(0 hr t0 24hr)
        //3rd armument for to tell im this activity we are using range seek bar
        //4th argument for restriction of the thumb limit should not go beyond the current minute
        seekBar = new RangeSeekBar<Integer>(0, 1440, this, (0.000695 * tot_min));
        seekbar_layout = (ViewGroup) findViewById(R.id.seekbar_placeholder1);
        seekbar_layout.addView(seekBar);
        seekBar.setOnRangeSeekBarChangeListener(this);

        //drawing the activity line
        drawingImageView = (ImageView) findViewById(R.id.DrawingImageView);
        //initialization of expandable list view
        exp_list = (ExpandableListView) findViewById(R.id.exp_list);
        init_exp_list();
        //to handle the value in the database
        dbhandler = new DatabaseHandler(this);
    }

    //initialising the expandable list view
    private void init_exp_list() {
        category_left = getInfo(0, 0, 0, 0);
        //getting key in hashmap
        category_list_left = new ArrayList<String>(category_left.keySet());
        adapter_left = new ExpandableAdapter(this, category_left, category_list_left);
        exp_list.setAdapter(adapter_left);

    }

    //Listner for the range seek bar to get the minimum and maximum selected value
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
        min_value = (int) minValue;
        max_value = (int) maxValue;


        graphdisplay();
        //initialising the draw line activity
        init_drawing_activity_line();

        double diff = max_value - min_value;
        double tot_lines = pixel_X_end / diff;
        //drawing the activity line with respect to the selected minimum and maximum value
        //3rd argument to tell the starting position
        //4th argument tells the contribution
        draw(min_value, max_value, 0, tot_lines);


    }


    private void open_graph_1() {

        XYSeries Series1 = new XYSeries("Series1", 0);
        XYSeries Series2 = new XYSeries("Series2", 1);
        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Creating XYSeriesRenderer to customize series1
        XYSeriesRenderer series1Renderer = new XYSeriesRenderer();
        //setting the color of the series1
        series1Renderer.setColor(Color.GREEN);
        //circle points at the each point
        series1Renderer.setPointStyle(PointStyle.CIRCLE);
        series1Renderer.setFillPoints(true);
        series1Renderer.setLineWidth(2);
        series1Renderer.setDisplayChartValues(true);

        // Creating XYSeriesRenderer to customize series2
        XYSeriesRenderer series2Renderer2 = new XYSeriesRenderer();
        //setting the color of the series1
        series2Renderer2.setColor(Color.RED);
        //circle points at the each point
        series2Renderer2.setPointStyle(PointStyle.CIRCLE);
        series2Renderer2.setFillPoints(true);
        series2Renderer2.setLineWidth(2);
        series2Renderer2.setDisplayChartValues(true);

        Double[] sval = new Double[accdata.size()];
        for (int i = 0; i < accdata.size(); i++) {
            sval[i] = Double.parseDouble(accdata.get(i));
        }
        highestval1 = 0;
        // Adding data to Series
        for (int i = 0; i < sval.length; i++) {
            //fetching the time in the database
            //converting the time in to minutes
            //adding that time(in minutes) in X co-ordinates in graph and also the corresponding Y value
            String time = acctime.get(i);
            String sub[] = time.split(":");
            int min = Integer.parseInt(sub[1]);
            int hrs = Integer.parseInt(sub[0]);
            int result = (hrs * 60) + min;

            Series1.add(result, sval[i]);
            if (sval[i] > highestval1) {
                highestval1 = sval[i];
            }
        }

        Double[] aval = new Double[heartdata.size()];
        for (int i = 0; i < heartdata.size(); i++) {
            aval[i] = Double.parseDouble(heartdata.get(i));
        }

        highestval2 = 0;
        // Adding data to Series
        for (int i = 0; i < aval.length; i++) {
            //fetching the time in the database
            //converting the time in to minutes
            //adding that time(in minutes) in X co-ordinates in graph and also the corresponding Y value
            String time = hearttime.get(i);
            String sub[] = time.split(":");
            int min = Integer.parseInt(sub[1]);
            int hrs = Integer.parseInt(sub[0]);
            int result = (hrs * 60) + min;

            if (aval[i] > highestval2) {
                highestval2 = aval[i];
            }

            Series2.add(result, aval[i]);
        }

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        //I'm passing the arg number as '2' because I'm holding the two series in the graph
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer(2);
        dataset.addSeries(0, Series1);
        dataset.addSeries(1, Series2);

        //hold the every series render
        multiRenderer.addSeriesRenderer(series1Renderer);
        multiRenderer.addSeriesRenderer(series2Renderer2);

        //customization of the graph
        multiRenderer = customize(multiRenderer, Series1, Series2, highestval1, highestval2);

        //removing the views already present in the linear layout
        //Because we are going to draw in that linear layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
        chartContainer.removeAllViews();

        //Finally we are drawing the graph
        //1st arg for telling that we are going to draw graph in this activity
        //2nd arg dataset(contains the all the series)
        ///3rd arg multiprender(contains the customization od the each series)
        String[] types = new String[]{BarChart.TYPE, LineChart.TYPE};
        // final GraphicalView grfv = ChartFactory.getCombinedXYChartView(this, dataset, mRenderer, types);
        GraphicalView mChart = (GraphicalView) ChartFactory.getCombinedXYChartView(this, dataset, multiRenderer, types);
        chartContainer.addView(mChart);

        double avg1 = 0.0;
        for (int k = 0; k < aval.length; k++) {
            avg1 = avg1 + aval[k];
        }
        avg1 = avg1 / aval.length;

        double avg2 = 0.0;
        for (int k = 0; k < sval.length; k++) {
            avg2 = avg2 + sval[k];
        }
        avg2 = avg2 / sval.length;
        if(ps1!=avg1 || ps2!=avg2) {
            category_left = getInfo(avg1, 0, avg2, 0);
            adapter_left = new ExpandableAdapter(this, category_left, category_list_left);
            exp_list.setAdapter(adapter_left);
            ps1=avg1;
            ps2=avg2;
        }


    }

    private void open_graph_2() {
        XYSeries Series3 = new XYSeries("Series3", 0);
        XYSeries Series4 = new XYSeries("Series4", 1);

        //creating dataset to hold the series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Creating XYSeriesRenderer to customize series1
        XYSeriesRenderer series3Renderer = new XYSeriesRenderer();
        //setting the color of the series3
        series3Renderer.setColor(Color.GREEN);
        //circle points at the each point
        series3Renderer.setPointStyle(PointStyle.CIRCLE);
        series3Renderer.setFillPoints(true);
        series3Renderer.setLineWidth(2);
        series3Renderer.setDisplayChartValues(true);

        // Creating XYSeriesRenderer to customize series1
        XYSeriesRenderer series4Renderer = new XYSeriesRenderer();
        //setting the color of the series4
        series4Renderer.setColor(Color.RED);
        //circle points at the each point
        series4Renderer.setPointStyle(PointStyle.CIRCLE);
        series4Renderer.setFillPoints(true);
        series4Renderer.setLineWidth(2);
        series4Renderer.setDisplayChartValues(true);


        Double[] sval = new Double[tempraturedata.size()];
        for (int i = 0; i < tempraturedata.size(); i++) {
            sval[i] = Double.parseDouble(tempraturedata.get(i));
        }

        highestval3 = 0;
        // Adding data to Series
        for (int i = 0; i < sval.length; i++) {
            //fetching the time in the database
            //converting the time in to minutes
            //adding that time(in minutes) in X co-ordinates in graph and also the corresponding Y value
            String time = tempraturetime.get(i);
            String sub[] = time.split(":");
            int min = Integer.parseInt(sub[1]);
            int hrs = Integer.parseInt(sub[0]);
            int result = (hrs * 60) + min;

            Series3.add(result, sval[i]);

            if (sval[i] > highestval3) {
                highestval3 = sval[i];
            }
        }

        Double[] aval = new Double[spodata.size()];
        for (int i = 0; i < spodata.size(); i++) {
            aval[i] = Double.parseDouble(spodata.get(i));
        }

        highestval4 = 0;
        // Adding data to Series
        for (int i = 0; i < aval.length; i++) {
            //fetching the time in the database
            //converting the time in to minutes
            //adding that time(in minutes) in X co-ordinates in graph and also the corresponding Y value
            String time = spotime.get(i);
            String sub[] = time.split(":");
            int min = Integer.parseInt(sub[1]);
            int hrs = Integer.parseInt(sub[0]);
            int result = (hrs * 60) + min;

            Series4.add(result, aval[i]);

            if (aval[i] > highestval4) {
                highestval4 = aval[i];
            }

        }

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        //I'm passing the arg number as '2' because I'm holding the two series in the graph
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer(2);
        dataset.addSeries(0, Series3);
        dataset.addSeries(1, Series4);

        //hold the every series render
        multiRenderer.addSeriesRenderer(series3Renderer);
        multiRenderer.addSeriesRenderer(series4Renderer);

        //customization of the graph
        multiRenderer = customize(multiRenderer, Series3, Series4, highestval3, highestval4);

        //removing the views already present in the linear layout
        //Because we are going to draw in that linear layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.maingraph2);
        chartContainer.removeAllViews();

        //Finally we are drawing the graph
        //1st arg for telling that we are going to draw graph in this activity
        //2nd arg dataset(contains the all the series)
        ///3rd arg multiprender(contains the customization od the each series)
        GraphicalView mChart = (GraphicalView) ChartFactory.getLineChartView(this, dataset, multiRenderer);
        chartContainer.addView(mChart);

        double avg1 = 0.0;
        for (int k = 0; k < aval.length; k++) {
            avg1 = avg1 + aval[k];
        }
        avg1 = avg1 / aval.length;

        double avg2 = 0.0;
        for (int k = 0; k < sval.length; k++) {
            avg2 = avg2 + sval[k];
        }
        avg2 = avg2 / sval.length;

        if(ps3!=avg1 || ps4!=avg2) {
            category_left = getInfo(avg1, 0, avg2, 0);
            adapter_left = new ExpandableAdapter(this, category_left, category_list_left);
            exp_list.setAdapter(adapter_left);
            ps3=avg1;
            ps4=avg2;
        }

    }

    XYMultipleSeriesRenderer customize(XYMultipleSeriesRenderer multiRenderer, XYSeries series1, XYSeries series2
            , double highvalue1, double highvalue2) {
        //I dont need the zoom buttom so setting the visibility of that button to "false"
        multiRenderer.setZoomButtonsVisible(false);
        ////setting the margin size for the graph in the order top, left, bottom, right
        //  multiRenderer.setMargins(new int[]{0, 10, 25, 50});
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        multiRenderer.setBarSpacing(4);
        multiRenderer.setApplyBackgroundColor(true);

        //to remove the background black color
        multiRenderer.setBackgroundColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        //to remove the black color outside the graph
        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        //setting the y label color(fro both scale)
        multiRenderer.setYLabelsColor(0, Color.BLACK);
        multiRenderer.setYLabelsColor(1, Color.BLACK);
        //setting the X label color
        multiRenderer.setXLabelsColor(Color.BLACK);
        //diasble the legend
        multiRenderer.setShowLegend(false);
        //diavle the line on the grid
        multiRenderer.setShowGrid(false);
        //  multiRenderer.setXTitle("series1");
        //  multiRenderer.setYTitle("series2");

        //setting size of the labels
        multiRenderer.setLabelsTextSize(15);

        //setting the minimum and maximum of X axis(for both series inidicatedd by 0 for series1 and 1 for series2)
        //minimum and max value is the selected range seek bar limits
        multiRenderer.setXAxisMin(min_value, 0);
        multiRenderer.setXAxisMax(max_value, 0);
        multiRenderer.setXAxisMin(min_value, 1);
        multiRenderer.setXAxisMax(max_value, 1);

        //setting the minumum value and maximum value for Y axis (both series)
        multiRenderer.setYAxisMin(0, 0);
        multiRenderer.setYAxisMax(highvalue1 + 5, 0);
        multiRenderer.setYAxisMin(0, 1);
        multiRenderer.setYAxisMax(highvalue2 + 5, 1);
        //setting the alignment
        multiRenderer.setYAxisAlign(Paint.Align.RIGHT, 0);
        multiRenderer.setYAxisAlign(Paint.Align.LEFT, 1);
        //disabling the panning and the zooming limit
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.setZoomEnabled(false, false);


        //coverting the minutes in to HH:MM format and then
        //naming the X label as the date in hrs and minutes
        int diff = max_value - min_value;
        int split = diff / 10;
        for (int i = min_value; i <= max_value; i = i + split) {
            int min = i % 60;
            int hrs = i / 60;
            multiRenderer.addXTextLabel(i, String.valueOf(hrs) + ":" + String.valueOf(min));
        }

        //serring of the  automated generated X labels
        multiRenderer.setXLabels(0);

        //multiRenderer.setXLabelsPadding(10);
        //multiRenderer.setYLabelsPadding(10);

        return multiRenderer;
    }

    private void init_drawing_activity_line() {

        bitmap = Bitmap.createBitmap((int) this.getWindowManager()
                .getDefaultDisplay().getWidth(), (int) this.getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        drawingImageView.setImageBitmap(bitmap);

        //converting the dp in to pixel according to the device
        final float scale = getResources().getDisplayMetrics().density;
        pixel_X = (int) (180 * scale + 0.5f);
        pixel_Y = (int) (500 * scale + 0.5f);
        pixel_X_end = (int) (780 * scale + 0.5f);
        canvas.translate(pixel_X, pixel_Y);

    }

    void draw(double min, double max, double start_draw, double tot_lines) {
        int y_value = 0;
        int sleep_count = MockData_chart_view.sleep_count;
        int sleep_start[] = MockData_chart_view.sleep_start;
        int sleep_end[] = MockData_chart_view.sleep_end;

        int awake_count = MockData_chart_view.awake_count;
        int[] awake_start = MockData_chart_view.awake_start;
        int[] awake_end = MockData_chart_view.awake_end;

        int active_count = MockData_chart_view.active_count;
        int active_start[] = MockData_chart_view.active_start;
        int active_end[] = MockData_chart_view.active_end;


        int unknown_count = MockData_chart_view.unknown_count;
        int unknown_start[] = MockData_chart_view.unknown_start;
        int unknown_end[] = MockData_chart_view.unknown_end;

        Paint paint = new Paint();
        paint.setStrokeWidth(30);

        //finding which one has max count
        //start
        int count[] = {sleep_count, awake_count, active_count, unknown_count};
        int max_count;
        int index = 0;
        for (int i = 1; i < count.length; i++) {
            if (count[index] < count[i])
                index = i;

        }
        max_count = count[index];

        //end
        boolean reach_end = false;
        for (int i = 0; i < max_count; i++) {
            if (i < sleep_count) {
                if (sleep_start[i] <= min && min <= sleep_end[i]) {
                    paint.setColor(Color.GREEN);
                    int diff_inner = (int) (sleep_end[i] - min);
                    if (sleep_end[i] != min) {
                        if (sleep_end[i] <= max) {

                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((diff_inner * tot_lines))), y_value, paint);
                            if (sleep_end[i] != max) {
                                start_draw = (float) (start_draw + ((diff_inner * tot_lines)));
                                min = sleep_end[i];
                            } else reach_end = true;
                        } else {
                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((max - min) * tot_lines)), y_value, paint);
                            reach_end = true;
                        }

                    }
                }
            }

            if (i < awake_count) {
                if (awake_start[i] <= min && min <= awake_end[i]) {
                    paint.setColor(Color.RED);

                    if (awake_end[i] != min) {
                        int diff_inner = (int) (awake_end[i] - min);
                        if (awake_end[i] <= max) {
                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((diff_inner * tot_lines))), y_value, paint);
                            if (awake_end[i] != max) {
                                start_draw = (float) (start_draw + ((diff_inner * tot_lines)));
                                min = awake_end[i];
                            } else reach_end = true;
                        } else {
                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((max - min) * tot_lines)), y_value, paint);
                            reach_end = true;
                        }

                    }

                }
            }


            if (i < active_count) {
                if (active_start[i] <= min && min <= active_end[i]) {
                    paint.setColor(Color.DKGRAY);
                    int diff_inner = (int) (active_end[i] - min);
                    if (active_end[i] != min) {
                        if (active_end[i] <= max) {

                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((diff_inner * tot_lines))), y_value, paint);
                            if (active_end[i] != max) {
                                start_draw = (float) (start_draw + ((diff_inner * tot_lines)));
                                min = active_end[i];
                            } else reach_end = true;
                        } else {
                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((max - min) * tot_lines)), y_value, paint);
                            reach_end = true;
                        }

                    }
                }
            }


            if (i < unknown_count) {
                if (unknown_start[i] <= min && min <= unknown_end[i]) {
                    paint.setColor(Color.MAGENTA);
                    int diff_inner = (int) (unknown_end[i] - min);
                    if (unknown_end[i] != min) {
                        if (unknown_end[i] <= max) {

                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((diff_inner * tot_lines))), y_value, paint);
                            if (unknown_end[i] != max) {
                                start_draw = (float) (start_draw + ((diff_inner * tot_lines)));
                                min = unknown_end[i];
                            } else reach_end = true;
                        } else {
                            canvas.drawLine((float) start_draw, y_value, (float) (start_draw + ((max - min) * tot_lines)), y_value, paint);
                            reach_end = true;
                        }

                    }
                }
            }
            if (!reach_end && i == max_count - 1) i = 0;
        }
    }

    //setting the string title and corresponding child in the "Expandable list view"
    protected static HashMap<String, List<String>> getInfo(double heart, double spo, double cal, double skin) {
        HashMap<String, List<String>> biometerics = new
                HashMap<String, List<String>>();
        List<String> heart_rate = new ArrayList<String>();
        List<String> calories = new ArrayList<String>();
        List<String> skin_temp = new ArrayList<String>();
        List<String> SpO2 = new ArrayList<String>();

        if (heart != 0)
            heart_rate.add(heart + "avg");
        else
            heart_rate.add(0 + "avg");
        if (heart != 0)
            calories.add(cal + "avg");
        else
            calories.add(0 + "avg");
        if (heart != 0)
            skin_temp.add(skin + "avg");
        else
            skin_temp.add(0 + "avg");
        if (heart != 0)
            SpO2.add(spo + "avg");
        else
            SpO2.add(0 + "avg");



        biometerics.put("HEART RATE", heart_rate);
        biometerics.put("CALORIES", calories);
        biometerics.put("TEMPERATURE", skin_temp);
        biometerics.put("SpO2", SpO2);


        return biometerics;


    }

    //Adapter for the expandable list view
    public class ExpandableAdapter extends BaseExpandableListAdapter {

        private Context ctx;
        private HashMap<String, List<String>> Category;
        private List<String> category_list;

        public ExpandableAdapter(Context ctx,
                                 HashMap<String, List<String>> Category,
                                 List<String> category_list)

        {
            this.ctx = ctx;
            this.Category = Category;
            this.category_list = category_list;


        }


        @Override
        public int getGroupCount() {
            //number of groups
            return category_list.size();
        }

        @Override
        public int getChildrenCount(int i) {
            //No of subtitles in each list
            return Category.get(category_list.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            //present groupp title
            return category_list.get(i);
        }

        @Override
        public Object getChild(int parent, int child) {

            //current child
            return Category.get(category_list.get(parent)).get(child);
        }

        @Override
        public long getGroupId(int i) {

            return i;
        }

        @Override
        public long getChildId(int parent, int child) {

            return child;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int parent, boolean isExpanded, View view, ViewGroup viewGroup) {

            String group_title = (String) getGroup(parent);
            if (view == null) {
                LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflator.inflate(R.layout.parent_layout_chart_view_expandabel_view, viewGroup, false);
            }
            TextView parent_text = (TextView) view.findViewById(R.id.parent_txt);
            parent_text.setTypeface(null, Typeface.BOLD);
            parent_text.setText(group_title);

            return view;
        }

        @Override
        public View getChildView(int parent, int child, boolean lastChild, View view, ViewGroup viewGroup) {
            String child_title = (String) getChild(parent, child);
            if (view == null) {
                LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflator.inflate(R.layout.child_layout_chart_view_expandabel_view, viewGroup, false);
            }
            TextView child_text = (TextView) view.findViewById(R.id.child_txt);
            child_text.setText(child_title);
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }
    }

    //dynamically checking whether data is available
    public void graphdisplay() {

        sporateTimer = new Runnable() {
            @Override
            public void run() {

                checking_for_data();
                Log.v("test", "fiyaz");
                sporateHandler.postDelayed(this, 10000);
            }
        };
        sporateHandler.postDelayed(sporateTimer, 0);

    }

    //checking and getting the data from database
    private void checking_for_data() {
        // TODO Auto-generated method stub

        String currentdate = date();
        sporateHandler.removeCallbacks(sporateTimer);
        Log.d("Reading: ", "Reading specific rows..");

        List<Accrate> A = dbhandler.getselectedaccdata(currentdate);
        if (A.size() > 0) {
            accdata = new ArrayList<String>();
            accdate = new ArrayList<String>();
            acctime = new ArrayList<String>();
            for (Accrate cn : A) {
                accdata.add(cn.getVal());
                accdate.add(cn.getdate());
                acctime.add(cn.getime());
                String log = "Id: " + cn.getID() + " ,Val: " + cn.getVal() + " ,date: " + cn.getdate() + ",time: " + cn.getime();
                Log.d("Name: ", log);
            }
        } else {
            // showToast("No steps data available");
        }
        List<Heartrate> H = dbhandler.getselectedheartdata(currentdate);
        if (H.size() > 0) {
            heartdata = new ArrayList<String>();
            heartdate = new ArrayList<String>();
            hearttime = new ArrayList<String>();
            for (Heartrate cn : H) {
                heartdata.add(cn.getVal());
                heartdate.add(cn.getdate());
                hearttime.add(cn.getime());
                String log = "Id: " + cn.getID() + " ,Val: " + cn.getVal() + " ,date: " + cn.getdate() + ",time: " + cn.getime();
                Log.d("Name: ", log);
            }

        } else {

            // showToast("no heart data available");
            //no heart rate data available


        }
        if (A.size() > 0 && H.size() > 0) {
            if (min_value != max_value)
                open_graph_1();
            else {
                LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
                chartContainer.removeAllViews();
            }

        }


        List<Temprate> T = dbhandler.getselectedtempdata(currentdate);
        if (T.size() > 0) {
            tempraturedata = new ArrayList<String>();
            tempraturedate = new ArrayList<String>();
            tempraturetime = new ArrayList<String>();
            for (Temprate cn : T) {
                tempraturedata.add(cn.getVal());
                tempraturedate.add(cn.getdate());
                tempraturetime.add(cn.getime());
                String log =  " ,Val: " + cn.getVal() + " ,date: " + cn.getdate() + ",time: " + cn.getime();
                Log.d("Name: ", log);
            }
        } else {
            //showToast("No Temperature data available");
        }
        List<Sporate> S = dbhandler.getselectedspodata(currentdate);
        if (S.size() > 0) {
            spodata = new ArrayList<String>();
            spodate = new ArrayList<String>();
            spotime = new ArrayList<String>();
            for (Sporate cn : S) {
                spodata.add(cn.getVal());
                spodate.add(cn.getdate());
                spotime.add(cn.getime());
                String log = "Id: " + cn.getID() + " ,Val: " + cn.getVal() + " ,date: " + cn.getdate() + ",time: " + cn.getime();
                Log.d("Name: ", log);
            }

        } else {
            // showToast("no spo data available");
            //no spo data available
        }


        if (T.size() > 0 && S.size() > 0) {
            if (min_value != max_value)
                open_graph_2();
            else {
                LinearLayout chartContainer = (LinearLayout) findViewById(R.id.maingraph2);
                chartContainer.removeAllViews();
            }
        }


    }

    public String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    @Override
    public void onPause() {
        super.onPause();
        sporateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        sporateHandler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sporateHandler.removeCallbacksAndMessages(null);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
