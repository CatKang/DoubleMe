package com.hackathon.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.LibSift.namespace.SiftFun;
import com.hackathon.common.util.FileUtil;
import com.hackathon.common.util.GeometryUtil;
import com.hackathon.entity.HkWindow;
import com.hackathon.entity.ImageInfo;
import com.hackathon.main.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

public class HachathonFinalImageActivity extends Activity {
	private ImageView myImageFinalDown;
	private ImageView myImageFinalRight;
	private Button buttonRightYes;
	private Button buttonRightNo;
	private Button buttonBottomSave;
	private Button buttonBottomCancel;
	private ProgressBar progressBar;

	private FrameLayout myImageLayoutLeft;

	private HkWindow finalImageWindow;
//	private int morePix = 200;
	private float moreScale = (float) 0.08;
	/* ImageViewRight onToach */
	// boolean isFit = false;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;
	Bitmap bitmap;
	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 4f;// 最大缩放比例
	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	int mode = NONE;
	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;

	/* ImageViewRight onToach */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.final_image);
		//setProgressBarVisibility(true);
		
		buttonRightYes = (Button) findViewById(R.id.buttonRightYes);
		buttonRightNo = (Button) findViewById(R.id.buttonRightNo);
		buttonBottomSave = (Button) findViewById(R.id.buttonBottomSave);
		buttonBottomCancel = (Button) findViewById(R.id.buttonBottomCancel);
		myImageFinalDown = (ImageView) findViewById(R.id.myImageFinalDown);
		myImageFinalRight = (ImageView) findViewById(R.id.myImageFinalRight);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		myImageLayoutLeft = (FrameLayout) findViewById(R.id.myImageLayoutLeft);
		Bundle bundle = getIntent().getExtras();
		finalImageWindow = (HkWindow) bundle.getSerializable("HkWindow");

		//set control size
		LinearLayout.LayoutParams paramLeft = (LinearLayout.LayoutParams) myImageLayoutLeft
				.getLayoutParams();
		paramLeft.width = finalImageWindow.curFrameX - finalImageWindow.viewX;
		myImageLayoutLeft.setLayoutParams(paramLeft);
		LinearLayout.LayoutParams paramRight = (LinearLayout.LayoutParams) myImageFinalRight
				.getLayoutParams();
		int right_width =  finalImageWindow.viewWidth + finalImageWindow.viewX
				- finalImageWindow.curFrameX;
		int right_height = finalImageWindow.viewHeight;
		paramRight.width = right_width;
		myImageFinalRight.setLayoutParams(paramRight);
		
		//calculate image size to fit the right imageview
		Bitmap whole_image = FileUtil.loadBitmapFromFile("whole");
		Bitmap tmp_image = FileUtil.loadBitmapFromFile("right");
		float width_scale = (float)right_width /(float)tmp_image.getWidth();
		float height_scale = (float)right_height / (float)tmp_image.getHeight() ;
		Matrix tmp_matrix = new Matrix();
		tmp_matrix.postScale(width_scale, height_scale);
		Bitmap right_image = Bitmap.createBitmap(tmp_image, 0, 0, tmp_image.getWidth(), tmp_image.getHeight(), tmp_matrix, true);
		
		myImageFinalDown.setImageBitmap(whole_image);
		myImageFinalRight.setImageBitmap(right_image);
		
		
		
		
		// myImageFinalRight.setImageResource(R.drawable.chunse);
		myImageFinalRight.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// if (!isFit)
				// return false;

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					//myImageFinalRight.setScaleType(ScaleType.MATRIX);
					savedMatrix.set(matrix);
					prev.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					dist = GeometryUtil.spacing(event);
					if (dist > 10f) {
						savedMatrix.set(matrix);
						GeometryUtil.midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - prev.x,
								event.getY() - prev.y);
					} else if (mode == ZOOM) {
						float newDist = GeometryUtil.spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float tScale = newDist / dist;
							matrix.postScale(tScale, tScale, mid.x, mid.y);
						}
					}
					break;
				}
				myImageFinalRight.setImageMatrix(matrix);
				// CheckView();
				return true;
			}
		});

		buttonRightYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
				//切换状态
				
				getProcessPicture();
				setStatus("process");

				ProcessThread thread = new ProcessThread();
				thread.start();
						
				Toast.makeText(getApplicationContext(), "UI finished", 100).show();
				//int cur_phase = 1;
				
//				while(cur_phase++<10){
//					Message msg = new Message();
//					Toast.makeText(getApplicationContext(), "UI finished" + cur_phase, 100).show();
//					msg.what = (cur_phase == 9) ? cur_phase : cur_phase++;
//					handler.sendMessage(msg);
//					}
//				while(!finished)
//				{
//					Message msg = new Message();
//					msg.what = (cur_phase == 9) ? cur_phase : cur_phase++;
//					//handler.sendMessage(msg);
//					
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
				
			}
		});
		buttonRightNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 发回上一个界面
				// TODO Auto-generated method stub
				// resetCamera();
//				Intent intent = new Intent();
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.setClass(HachathonFinalImageActivity.this, HachathonMainActivity.class);
//				startActivity(intent);
				startActivity(new Intent(HachathonFinalImageActivity.this,HachathonMainActivity.class));
			}
		});

		buttonBottomSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(HachathonFinalImageActivity.this,
						HachathonLastActivity.class));
			}
		});

		buttonBottomCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setStatus("fit");
			}
		});

	}

	private void setStatus(String input) {
		if ("fit".equals(input)) {
			myImageFinalRight.setVisibility(View.VISIBLE);
			myImageFinalDown.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			buttonRightYes.setVisibility(View.VISIBLE);
			buttonRightNo.setVisibility(View.VISIBLE);
			buttonBottomSave.setVisibility(View.INVISIBLE);
			buttonBottomCancel.setVisibility(View.INVISIBLE);
			Bitmap whole = FileUtil.loadBitmapFromFile("whole");
			Bitmap right = FileUtil.loadBitmapFromFile("right");
			myImageFinalRight.setImageBitmap(right);
			myImageFinalDown.setImageBitmap(whole);

		} 
		else if ("process".equals(input)) {
			progressBar.setVisibility(View.VISIBLE);
			buttonRightYes.setVisibility(View.INVISIBLE);
			buttonRightNo.setVisibility(View.INVISIBLE);

		}else if ("save".equals(input)) {
			progressBar.setVisibility(View.INVISIBLE);
			myImageFinalRight.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
			buttonBottomSave.setVisibility(View.VISIBLE);
			buttonBottomCancel.setVisibility(View.VISIBLE);
			Bitmap result = FileUtil.loadBitmapFromFile("final_tmp");
			myImageFinalDown.setImageBitmap(result);
		}

	}

	class ProcessThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Toast.makeText(getApplicationContext(), "begin new thread", 100).show();
			super.run();
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);

			generateFinalImage();
			
			msg = new Message();
			msg.what = -1;
			handler.sendMessage(msg);
		}
		
	}

	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
		
			case 0:
				//begin
				Toast.makeText(getApplicationContext(), "begin process", 100).show();
//				progressBar.setProgress(0);
				break;
			case -1:
				//end
				Toast.makeText(getApplicationContext(), "end process", 100).show();
				// 关闭ProgressDialog
				//progressBar.setProgress(100);
				setStatus("save");
				
				break;

			default:
				Toast.makeText(getApplicationContext(), "what : " + msg.what, 100).show();
				//progressBar.setProgress(msg.what * 10);
				break;
			}
			
			
		}
	};

	private void getProcessPicture() {
		int dw = myImageFinalRight.getDrawable().getBounds().width();
		int dh = myImageFinalRight.getDrawable().getBounds().height();
		Matrix m = myImageFinalRight.getImageMatrix();
		float[] values = new float[9];
		m.getValues(values);
		float sx = values[0];
		float sy = values[4];
		Log.d("lxy", "scale_X = " + sx + ", scale_Y = " + sy);
		int image_dx = (int) values[2];
		int image_dy = (int) values[5];
		int image_width = (int) (dw * sx);
		int image_height = (int) (dh * sy);

		Bitmap tmp_left = FileUtil.loadBitmapFromFile("whole");
		myImageFinalRight.setDrawingCacheEnabled(true);
		Bitmap right = FileUtil.loadBitmapFromFile("right");
		Bitmap tmp_right = Bitmap.createBitmap(right, 0, 0, right.getWidth(), right.getHeight(), m, true); 
		//Bitmap tmp_right = myImageFinalRight.getDrawingCache();

		// calculate left image size
		int x_left = 0;
		int y_left = (image_dy < 0) ? 0 : image_dy;
		int morePix = (int)(tmp_left.getWidth() * moreScale);
		int width_left = finalImageWindow.curFrameX - finalImageWindow.viewX
				+ ((image_dx>0)?image_dx:0) + morePix;
		if (width_left > finalImageWindow.viewWidth)
			width_left = finalImageWindow.viewWidth;
		// calculate right image size
		int x_right = (image_dx > 0) ? image_dx : 0;
		int y_right = (image_dy > 0) ? image_dy : 0;
		int width_right = (image_dx > 0) ? image_width : image_width + image_dx;
		if (image_width + image_dx > tmp_right.getWidth())
			width_right = tmp_right.getWidth() - image_dx;
		int height_right = (image_dy > 0) ? image_height : image_height + image_dy;
		if (height_right + image_dy > tmp_right.getHeight())
			height_right = tmp_right.getHeight() - image_dy;
		int height_left = height_right;

		// generate image
		Bitmap target_left = Bitmap.createBitmap(tmp_left, x_left, y_left,
				width_left, height_left);
		FileUtil.memoryOneImage(target_left, "final_left");

		Bitmap target_right = Bitmap.createBitmap(tmp_right, x_right, y_right,
				width_right, height_right);
		FileUtil.memoryOneImage(target_right, "final_right");
		
		myImageFinalRight.setDrawingCacheEnabled(false);
		// if (image_height + image_dy > target_right.getHeight())
		// image_height = target_right.getHeight() - image_dy;

		// int height_left = finalImageWindow.viewHeight - image_dy;

		// if(height_left > image_height)
		// height_left = image_height;
		// int width_left = curFrameX - viewX + image_dx + morePix;

		// if (y_left + width_left > target_left.getWidth())
		// width_left = target_left.getWidth() - y_left;

		// Bitmap target_right =
		// BitmapFactory.decodeFile(strCaptureFilePathRight);
		
		//int curFrame[]
//		cutFrame[0] = viewX;
//		cutFrame[1] = viewY + y_left;
//		cutFrame[2] = viewX + width_left + image_width + image_dx;
//		cutFrame[3] = viewY + y_left + height_left;
	}
	
	private void generateFinalImage_directly()
	{
		Bitmap final_left = FileUtil.loadBitmapFromFile("final_left");
		Bitmap final_right = FileUtil.loadBitmapFromFile("final_right");
		
		Bitmap result = Bitmap.createBitmap(final_left.getWidth() + final_right.getWidth(), final_left.getHeight(),
				Config.RGB_565);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(final_left, new Matrix(), null);
		canvas.drawBitmap(final_right, final_left.getWidth(), 0, null);
		FileUtil.memoryOneImage(result, "final_tmp");
		
		//myImageFinalDown.setImageBitmap(result);
	}
	
	private void generateFinalImage()
	{
//		Bitmap tmp_left  = FileUtil.loadBitmapFromFile("final_left");
//		Bitmap tmp_right = FileUtil.loadBitmapFromFile("final_right");
//		int left_width_half = tmp_left.getWidth() / 2;
//		int right_width_half = tmp_right.getWidth() / 2;
//		Bitmap part1 = Bitmap.createBitmap(tmp_left , 0, 0 , left_width_half, tmp_left.getHeight());
//		Bitmap part2 = Bitmap.createBitmap(tmp_left,left_width_half, 0, left_width_half, tmp_left.getHeight() );
//		Bitmap part3 = Bitmap.createBitmap(tmp_right, 0, 0, right_width_half, tmp_right.getHeight());
//		Bitmap part4 = Bitmap.createBitmap(tmp_right,right_width_half, 0,right_width_half, tmp_right.getHeight());
//		FileUtil.memoryOneImage(part2, "final_left_tmp");
//		FileUtil.memoryOneImage(part3, "final_right_tmp");
		
//		String fileName1 = FileUtil.getFilePathByType("final_left_tmp");
//		 String fileName2 = FileUtil.getFilePathByType("final_right_tmp");
		
		String fileName1 = FileUtil.getFilePathByType("final_left");
		 String fileName2 = FileUtil.getFilePathByType("final_right");
		 int ret = SiftFun.siftConjunction(fileName1, fileName2);
		 //Toast.makeText(getApplicationContext(), "conjunction finished : " + ret, 100).show();
		 if (ret != 0)
			 generateFinalImage_directly();
//		 else
//		 {
//			 //Bitmap result = FileUtil.loadBitmapFromFile("final_tmp");
//			// myImageFinalDown.setImageBitmap(result);
//			 
//			 //conjuction part1 and part4
//		 }
	}
	
	
}
