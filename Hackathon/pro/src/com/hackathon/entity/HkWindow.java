package com.hackathon.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.view.SurfaceView;

public class HkWindow implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 779451952874037587L;
	
	public int viewX; // 照相机预览view左上角坐标
	public int viewY;
	public int viewWidth;
	public int viewHeight;
	public int curFrameX; // 当前的分割框x值
	public int curFrameYMin; // 边框的上下边界
	public int curFrameYMax;
	public int curFrameXMin; // 边框所允许移动的左右界
	public int curFrameXMax;
	
	public double onFrameThresholdRadio = 0.03; // 认为在边框上的阈值
	public double insideThresholdRadio = 0.3; // 认为在边框上的阈值
	public double siderBarRadio = 0.09;
	
	public int siderBarWidth;
	public int onFrameThreshold; // 认为在边框上的阈值
	public int insideThreshold; // 认为在边框上的阈值
	public HkWindow(SurfaceView sview, int view_width, int view_height) {
//		viewHeight = sview.getwgetMeasuredHeight();
//		viewWidth = sview.getMeasuredWidth();
		viewHeight = view_height;
		viewWidth = view_width;
		int[] location = new int[2];
		sview.getLocationOnScreen(location);	
		viewX = location[0];
		viewY = location[1];
		initialWindow();
	}

	private void initialWindow() {
		onFrameThreshold = (int)(viewWidth * onFrameThresholdRadio);
		insideThreshold = (int)(viewWidth * insideThresholdRadio);
		curFrameX = viewX + viewWidth / 2;
		curFrameYMin = viewY;
		curFrameYMax = viewY + viewHeight;
		curFrameXMin = viewX + insideThreshold;
		curFrameXMax = viewX + (viewWidth - insideThreshold);
		siderBarWidth = (int)(viewWidth * siderBarRadio);
	}

	public boolean onFrame(int curX, int curY) {
		if (curX <= curFrameX + onFrameThreshold
				&& curX >= curFrameX - onFrameThreshold)
			return true;
		return false;
	}

	public ImageSize getImageSize(String type, int imageSizeX, int imageSizeY) {
		ImageSize image = null;
		int mid = (int) ((float) (curFrameX - viewX) / (float) viewWidth * imageSizeX);
		if ("left".equals(type)) {
			image = new ImageSize(0, 0, mid, imageSizeY);
		} else if ("right".equals(type)) {
			image = new ImageSize(mid, 0, imageSizeX - mid, imageSizeY);
		}
		return image;
	}
	
	public int boundInWindow(int dx)
	{
		int curFrameTmp = curFrameX + dx;
		dx = (curFrameTmp > curFrameXMax) ? curFrameXMax - curFrameX : dx;
		dx = (curFrameTmp < curFrameXMin) ? curFrameXMin - curFrameX: dx;
		return dx;
	}

}
