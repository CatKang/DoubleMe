package com.hackathon.common.listener;

import java.util.ArrayList;

import com.hackathon.activity.HachathonMainActivity;
import com.hackathon.common.util.Log;
import com.hackathon.entity.HkWindow;
import com.hackathon.view.FocusBoxView;

import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.FrameLayout;
import android.widget.Toast;

public class HKOnGestureListener extends SimpleOnGestureListener implements Camera.AutoFocusCallback{
	private HkWindow mainWindow;
	private Camera camera;
	private FocusBoxView focusBoxView;
	private Log log_file;
	private int flag = 0;
	private final float focusBoxLengthRadio = (float)0.1;
	private int halfBoxSize;


	public HKOnGestureListener(HkWindow curWindow, Camera came, FocusBoxView fbView, Log file)
	{
		mainWindow = curWindow;
		focusBoxView = fbView;
		camera = came;
		log_file = file;
		halfBoxSize = (int)(mainWindow.viewHeight * focusBoxLengthRadio);
	}
	
	public void refreshFocusBox(int flag)
	{
		this.flag = flag;
		if (flag == 0)
			paintFocusBox((float)(mainWindow.curFrameX / 2.0) , (float)(mainWindow.viewHeight / 2.0));
		else	
			paintFocusBox((float)(mainWindow.curFrameX + (mainWindow.viewWidth - mainWindow.curFrameX) / 2.0) , (float)(mainWindow.viewHeight / 2.0));
	}
	
	private boolean pressValidation(float fx, float fy)
	{
		if (flag == 0 && (int)fx > mainWindow.curFrameX - halfBoxSize)
				return false;
		if (flag == 1 && (int)fx < mainWindow.curFrameX + halfBoxSize)
				return false;
		return true;
	}
	
	private void paintFocusBox(float fx, float fy)
	{
		
    	focusBoxView.setX(fx - halfBoxSize);
    	focusBoxView.setY(fy - halfBoxSize);
    	focusBoxView.setLayoutParams(new FrameLayout.LayoutParams(halfBoxSize * 2,
    			halfBoxSize * 2));
    	focusBoxView.setColor(Color.WHITE);
    	focusBoxView.invalidate();
	}
	
	@Override
	public void onShowPress(MotionEvent e)  
    {  
            float fx=e.getX();  
            float fy=e.getY();  
            if (!pressValidation(fx, fy))
            	return;
            paintFocusBox(fx, fy);
            camera.autoFocus(this);//自动对焦
            Camera.Parameters parameters = camera.getParameters();
            int mm = parameters.getMaxNumFocusAreas();  
            log_file.saveLog("getMaxNumFocusAreas : " + mm);
            if(mm > 0)  
            {        
                     
                    ArrayList<Area> al = new ArrayList<Area>();  
                    //映射到Area的取值范围中
                    float x1 = (fx/mainWindow.viewWidth)*2000-1000;  
                    float y1 = (fy/mainWindow.viewHeight)*2000-1000;  
                    Area area = new Area(new Rect((int)x1-100,(int)y1-100,(int)x1+100,(int)y1+100), 1000);  
                    //Camera.Area area = new Camera.Area(new Rect(-500, -500, 500,500), 0);
                    al.add(area);  
                    try  
                    {  
                    	   if (flag == 0)
                    	   {
                    		   //只有当flag为0的时候才设置测光
                    		   parameters.setMeteringAreas(al);
                    	   }
                    	   parameters.setFocusAreas(al);  
                           log_file.saveLog("area position: "+ x1 + " , " + y1);
                           camera.setParameters(parameters);  
                            
                    }catch (Throwable t)  
                    {  
                          log_file.saveLog(t, "onShowPress");
                    }  
            }
    }

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		focusBoxView.setColor(Color.GREEN);
    	focusBoxView.invalidate();
	}  
	
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


    
}