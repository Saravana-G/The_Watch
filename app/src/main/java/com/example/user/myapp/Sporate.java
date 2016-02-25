package com.example.user.myapp;

public class Sporate {
	  int _id;  
	  String val;
	  String date;
	  String time;
	    public Sporate(){   }  
	    public Sporate(int id, String v, String d, String t){
	        this._id = id;  
	        this.val = v;  
	        this.date = d;
	        this.time = t;
	    }  
	   
	    public Sporate( String v, String d, String t){
	    	this.val = v;  
	        this.date = d;
	        this.time = t; 
	    }  
	    public int getID(){  
	        return this._id;  
	    }  
	  
	    public void setID(int id){  
	        this._id = id;  
	    }  
	  
	    public String getVal(){
	        return this.val;  
	    }  
	  
	    public void setVal(String v){
	        this.val = v;  
	    }  
	  
	    public String getdate(){
	        return this.date;  
	    }  
	   
	    public void setdate(String d){
	        this.date = d;  
	    }  
	    public String getime(){
	        return this.time;  
	    }  
	   
	    public void setime(String t){
	        this.time = t;  
	    }  
	}  



