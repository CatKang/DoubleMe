package com.hackathon.entity;




public class FinalImageWindow{
	
	public int viewX; // 照相机预览view左上角坐标
	public int viewY;
	public int viewWidth;
	public int viewHeight;
	public int leftWidth; 
	public float wl_radio;


	public FinalImageWindow(int vx, int vy, int vwidth, int vheight) {
		viewX = vx;
		viewY = vy;
		viewWidth = vwidth;
		viewHeight = vheight;
		wl_radio = (float)viewWidth / (float)viewHeight;
		
	}

	
	
	

}
