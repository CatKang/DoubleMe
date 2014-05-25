package com.hackathon.entity;

public class ImageSize {
	public int x;
	public int y;
	public int width;
	public int height;
	
	public ImageSize(int _x, int _y, int _width, int _height)
	{
		x = _x;
		y = _y;
		width = _width;
		height = _height;
	}
	
	public void change(int _x, int _y, int _width, int _height)
	{
		x = _x;
		y = _y;
		width = _width;
		height = _height;
	}
}
