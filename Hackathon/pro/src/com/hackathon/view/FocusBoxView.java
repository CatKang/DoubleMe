package com.hackathon.view;

import com.hackathon.common.util.BitmapUtil;
import com.hackathon.entity.FinalImageWindow;
import com.hackathon.main.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FocusBoxView  extends ImageView {  
  
    private int co;  
    private int borderwidth = 5;
    private final Resources res=getResources();
    private int pic = R.drawable.focus_frame;
    
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
    
    public void focused(boolean isFocus)
    {
    	if (isFocus)
    		pic = R.drawable.focus_image_green;
    	else
    		pic = R.drawable.focus_frame;
    }
    @Override  
    protected void onDraw(Canvas canvas) {  
        
        Bitmap bmp=BitmapFactory.decodeResource(res, pic);
        int cwidth = this.getWidth();
        int cheight = this.getHeight();
        Bitmap resize_bmp = BitmapUtil.resizeBitmap(bmp, cwidth,cheight);
        canvas.drawBitmap(resize_bmp, new Matrix(), null);
        super.onDraw(canvas); 
//        Rect rec = canvas.getClipBounds(); 
//      //设置边框颜色  
//        Paint paint = new Paint(); 
//        paint.setColor(co);  
//        paint.setStyle(Paint.Style.STROKE); 
//        
//        //设置边框宽度  
//        paint.setStrokeWidth(borderwidth);     
//        canvas.drawRect(rec, paint);  
    }  
    
}  