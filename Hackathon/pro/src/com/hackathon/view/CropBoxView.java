package com.hackathon.view;

import com.hackathon.entity.FinalImageWindow;
import com.hackathon.entity.ImageSize;
import com.hackathon.main.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CropBoxView  extends ImageView {  
  
    private int co;  
    private int borderwidth = 5;
    

	private int leftWidth;
	
	public ImageSize cropArea = null;
    
	public void initialCropArea(ImageSize cropArea) {
		if (this.cropArea == null)
			this.cropArea = cropArea;
	}
	
	public void setCropArea(ImageSize cropArea) {
		this.cropArea = cropArea;
	}
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
    public int getBorderwidth() {
		return borderwidth;
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
//        if(cropArea == null)
//        	return;
//        Rect rec = canvas.getClipBounds(); 
        Rect rec = new Rect(this.cropArea.x, this.cropArea.y, cropArea.x + this.cropArea.width, cropArea.y + this.cropArea.height);
      //设置边框颜色  
        Paint paint = new Paint(); 
        paint.setColor(co);  
        paint.setStyle(Paint.Style.STROKE); 
       
        //画角
        this.drawCorner(canvas, paint, rec);
        
        //设置边框宽度  
        paint.setStrokeWidth(borderwidth);  
//        rec.top += borderwidth;
//        rec.left += borderwidth;
//        rec.bottom -= borderwidth;  
//        rec.right -= borderwidth;    
        canvas.drawRect(rec, paint);  
        
        //画分割线
        this.drawSplitLine(canvas);
        
        
        //画阴影
        //this.drawBg(canvas, paint);
    }  
    
    //四个角加粗
    private void drawCorner(Canvas canvas, Paint paint, Rect rec)
    {
    	int corner_length = (int)(rec.height() * 0.15);
    	int corner_thick = borderwidth * 2;
    	paint.setStrokeWidth(corner_thick);
    	Point left_top = new Point(rec.left, rec.top);
    	Point left_bottom = new Point(rec.left, rec.bottom);
    	Point right_top = new Point(rec.right, rec.top);
    	Point right_bottom = new Point(rec.right, rec.bottom);
    	
    	//画四条横线
    	canvas.drawLine(left_top.x, left_top.y, left_top.x + corner_length, left_top.y , paint);
    	canvas.drawLine(right_top.x - corner_length - corner_thick, right_top.y, right_top.x, right_top.y , paint);
    	canvas.drawLine(left_bottom.x, left_bottom.y, left_bottom.x + corner_length, left_bottom.y , paint);
    	canvas.drawLine(right_bottom.x - corner_length - corner_thick, right_bottom.y, right_bottom.x, right_bottom.y , paint);
    	
    	//画四条竖线
    	canvas.drawLine(left_top.x, left_top.y, left_top.x, left_top.y  + corner_length, paint);
    	canvas.drawLine(left_bottom.x, left_bottom.y - corner_length- corner_thick, left_bottom.x, left_bottom.y , paint);
    	canvas.drawLine(right_top.x, right_top.y, right_top.x, right_top.y  + corner_length, paint);
    	canvas.drawLine(right_bottom.x, right_bottom.y - corner_length- corner_thick, right_bottom.x, right_bottom.y , paint);
    	
    }
    
    //画分割线
    private void drawSplitLine(Canvas canvas)
    {
    	
    	Paint paint = new Paint();  
    	paint.setColor(Color.RED);
    	paint.setStrokeWidth(2);
    	paint.setStyle(Paint.Style.STROKE);
    	PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5}, 1);  
    	paint.setPathEffect(effects);  
    	//paint.setPathEffect(DashPathEffect)
    	int pointTopX = leftWidth;
    	int pointTopY = cropArea.y;
    	int pointDownX = pointTopX;
    	int pointDownY = pointTopY + cropArea.height;
    	canvas.drawLine(pointTopX, pointTopY, pointDownX, pointDownY, paint);
    }
    
    
    private void drawBg(Canvas canvas, Paint paint)
    {
    	
//    	  Resources res=getResources();
//    	  Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.chunse);
//    	  paint.setAlpha(30);
    	//  canvas.drawBitmap(bmp, 20, 20, paint);
    	
    }
}  