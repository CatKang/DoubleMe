package com.hackathon.entity;


import com.hackathon.main.R.drawable;

import android.graphics.Point;

public class CropFrame {
	private int color = drawable.white;
	private Point left_top;
	private int length;
	private int orientation;
	
	public CropFrame(Point _left_top, int _length, int _orientation)
	{
		left_top  = _left_top;	
		length = _length;
		orientation = _orientation;
	}
	
	public void setColor(int _color)
	{
		this.color = color;	
	}
	
}
