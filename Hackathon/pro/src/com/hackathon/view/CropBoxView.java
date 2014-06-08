package com.hackathon.view;

import com.hackathon.entity.FinalImageWindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CropBoxView  extends ImageView {  
  
    private int co;  
    private int borderwidth;
    private int leftWidth;
    
   
	
	public CropBoxView(Context context) {  
        super(context);  
    }  
    public CropBoxView(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public CropBoxView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
    //设置左边宽  
    public void setLeftWidth(int lw){  
    	leftWidth = lw;  
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
        // 画边框  
        Rect rec = canvas.getClipBounds();  
        rec.bottom--;  
        rec.right--;  
        Paint paint = new Paint();  
        //设置边框颜色  
        paint.setColor(co);  
        paint.setStyle(Paint.Style.STROKE);  
        //设置边框宽度  
        paint.setStrokeWidth(borderwidth);  
        canvas.drawRect(rec, paint);  
        this.drawSplitLine(canvas);
    }  
    
    private void drawSplitLine(Canvas canvas)
    {
    	Paint paint = new Paint();  
    	paint.setColor(Color.RED);
    	paint.setStrokeWidth(5);
    	paint.setStyle(Paint.Style.STROKE);
    	//paint.setPathEffect(DashPathEffect)
    	int pointTopX = leftWidth;
    	int pointTopY = 0;
    	int pointDownX = pointTopX;
    	int pointDownY = pointTopY + canvas.getHeight();
    	canvas.drawLine(pointTopX, pointTopY, pointDownX, pointDownY, paint);
    }
}  