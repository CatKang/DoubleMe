package com.hackathon.view;

import com.hackathon.entity.FinalImageWindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FocusBoxView  extends ImageView {  
  
    private int co;  
    private int borderwidth = 5;
   
	public FocusBoxView(Context context) {  
        super(context);  
    }  
    public FocusBoxView(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public FocusBoxView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
    //设置颜色  
    public void setColor(int color){  
        co = color;  
    }  
    //设置边框宽度  
    public void setBorderWidth(int width){  
          
        borderwidth = width;  
    }  
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        
        Rect rec = canvas.getClipBounds(); 
      //设置边框颜色  
        Paint paint = new Paint(); 
        paint.setColor(co);  
        paint.setStyle(Paint.Style.STROKE); 
        
        //设置边框宽度  
        paint.setStrokeWidth(borderwidth);     
        canvas.drawRect(rec, paint);  
    }  
    
}  