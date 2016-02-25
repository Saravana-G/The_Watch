package com.example.user.myapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 28-May-15.
 */
public class MockData_chart_view {
/*
    private static DatabaseHandler db;
    static List<Heartrate> hearlist = new ArrayList<Heartrate>();
    static List<Sporate> spo = new ArrayList<Sporate>();
    static List<Temprate> temperature = new ArrayList<Temprate>();
    static List<Accrate> accurate = new ArrayList<Accrate>();
*/
    static int index_heart=0;
    static int index_spo=0;
    static int index_temp=0;
    static int index_acc=0;


    public MockData_chart_view(){
        generate_values();
    }
    /*
    public MockData_chart_view(String date){
        generate_values(date);
    }
    */



    protected static int x1[]=new int[1440];
    protected static int y1[]=new int[1440];

    protected static int x2[]=new int[1440];
    protected static int y2[]=new int[1440];

    protected static int x3[]=new int[1440];
    protected static int y3[]=new int[1440];

    protected static int x4[]=new int[1440];
    protected static int y4[]=new int[1440];

    protected static int getX1(int index){
        return x1[index];
    }
    protected static int getY1(int index){
        return y1[index];
    }

    protected static int getX2(int index){
        return x2[index];
    }
    protected static int getY2(int index){
        return y2[index];
    }

    protected static int getX3(int index){
        return x3[index];
    }
    protected static int getY3(int index){
        return y3[index];
    }

    protected static int getX4(int index){
        return x4[index];
    }
    protected static int getY4(int index){
        return y4[index];
    }

    static int sleep_count=4;
    static int  sleep_start[]=new int[sleep_count];
    static int  sleep_end[]=new int[sleep_count];


    static int awake_count=4;
    static int  awake_start[]=new int[awake_count];
    static int  awake_end[]=new int[awake_count];

    static int active_count=4;
    static int  active_start[]=new int[active_count];
    static int  active_end[]=new int[active_count];

    static int unknown_count=4;
    static int  unknown_start[]=new int[unknown_count];
    static int  unknown_end[]=new int[unknown_count];

    protected static void generate_values(){
        Random random=new Random();
        for(int i=0;i<1440;i++){
            x1[i]=i;
            x2[i]=i;
            x3[i]=i;
            x4[i]=i;

            y1[i]=random.nextInt(20);
            y2[i]=random.nextInt(20);
            y3[i]=random.nextInt(20);
            y4[i]=random.nextInt(5);
        }


        sleep_start[0]=0;
        sleep_end[0]=120;
        awake_start[0]=120;
        awake_end[0]=240;
        active_start[0]=240;
        active_end[0]=300;
        unknown_start[0]=300;
        unknown_end[0]=360;

        sleep_start[1]=360;
        sleep_end[1]=420;
        awake_start[1]=420;
        awake_end[1]=480;
        active_start[1]=480;
        active_end[1]=600;
        unknown_start[1]=600;
        unknown_end[1]=720;

        sleep_start[2]=720;
        sleep_end[2]=780;
        awake_start[2]=780;
        awake_end[2]=900;
        active_start[2]=900;
        active_end[2]=960;
        unknown_start[2]=960;
        unknown_end[2]=1080;

        sleep_start[3]=1080;
        sleep_end[3]=1200;
        awake_start[3]=1200;
        awake_end[3]=1260;
        active_start[3]=1260;
        active_end[3]=1380;
        unknown_start[3]=1380;
        unknown_end[3]=1440;

    }
/*
    private void generate_values(String date) {

        index_heart=0;

        hearlist=db.getselectedheartdata(date);

        for (Heartrate hr : hearlist){
            String time=hr.getime();
            String val=hr.getVal();
            x1[index_heart]= Integer.parseInt(time);
            y1[index_heart]= Integer.parseInt(val);
            index_heart++;

        }

        index_temp=0;

        temperature=db.getselectedtempdata(date);

        for (Temprate tmp : temperature){
            String time=tmp.getime();
            String val=tmp.getVal();
            x2[index_temp]= Integer.parseInt(time);
            y2[index_temp]= Integer.parseInt(val);
            index_temp++;

        }

        index_spo=0;

        spo=db.getselectedspodata(date);

        for (Sporate sp : spo){
            String time=sp.getime();
            String val=sp.getVal();
            x3[index_spo]= Integer.parseInt(time);
            y3[index_spo]= Integer.parseInt(val);
            index_spo++;

        }

        index_acc=0;

        accurate=db.getselectedaccdata(date);

        for (Accrate acc : accurate){
            String time=acc.getime();
            String val=acc.getVal();
            x4[index_acc]= Integer.parseInt(time);
            y4[index_acc]= Integer.parseInt(val);
            index_acc++;

        }

    }
*/
}

