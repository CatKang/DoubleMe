package com.hackathon.entity;




public class FinalImageWindow{
	
	public int viewWidth;
	public int viewHeight;
	public int leftWidth; 
	public float wl_radio;


	public FinalImageWindow(int vwidth, int vheight) {

		viewWidth = vwidth;
		viewHeight = vheight;
		wl_radio = (float)viewWidth / (float)viewHeight;
	}
	public FinalImageWindow(FinalImageWindow window) {

		viewWidth = window.viewWidth;
		viewHeight = window.viewHeight;
		wl_radio = (float)viewWidth / (float)viewHeight;
	}

	
	
	

}
