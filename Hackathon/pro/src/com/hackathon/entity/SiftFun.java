package com.hackathon.entity;


public class SiftFun {

    static {
        System.loadLibrary("siftConjunction");
    }
	/** 
	 
	    * @param width the current view width 
	 
	    * @param height the current view height 
	 
	    */ 
	 
	public static native int siftConjunction(String a, String b);  
	 
	}  


