package com.hackathon.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.LibSift.namespace.SiftFun;
import com.hackathon.common.util.FileUtil;
import com.hackathon.common.util.GeometryUtil;
import com.hackathon.entity.CropFrame;
import com.hackathon.entity.FinalImageWindow;
import com.hackathon.entity.ImageSize;
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
import android.view.ViewGroup.LayoutParams;
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
	private FrameLayout myLayoutLeft;
	private FrameLayout mBottomPhotoFrameLayout;
	
	private View frameTop;
	private View frameBottom;
	private View frameLeft;
	private View frameRight;
	
	
	private FinalImageWindow finalImageWindow;
	public ImageSize cropBox;
	
	private float moreScale = (float) 0.08;    //左边图片多给的比例
	private float remainScale = (float) 0.20;  //右侧边栏的比例
	private int margin = 60;
	
	/* ImageViewRight onTouch */
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
	/* ImageViewRight onToch end */
	
	
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
		myLayoutLeft = (FrameLayout) findViewById(R.id.myImageLayoutLeft);
		mBottomPhotoFrameLayout = (FrameLayout) findViewById(R.id.mBottomPhotoFrameLayout);
		frameTop = (View)findViewById(R.id.frameTop);
		frameBottom = (View)findViewById(R.id.frameBottom);
		frameLeft = (View)findViewById(R.id.frameLeft);
		frameRight = (View)findViewById(R.id.frameRight);

		setStatus("fit");
		
		
		myImageFinalRight.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				refreshCropBox();
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
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
				startActivity(new Intent(HachathonFinalImageActivity.this,HachathonMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
			}
		});

		buttonBottomSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap final_bitmap = FileUtil.loadBitmapFromFile("final_tmp");
				FileUtil.memoryOneImage(final_bitmap, "final");
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
			paintFinalImageView();

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

	private void paintFinalImageView()
	{
		Bitmap whole_image = FileUtil.loadBitmapFromFile("whole");
		Bitmap right_image = FileUtil.loadBitmapFromFile("right");
		
		//get width-length radio
		float wl_radio = (float)whole_image.getWidth() / (float)whole_image.getHeight();
		
		//get down image actual size
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int view_width = (int)(screenWidth * (1 - remainScale)) - 2 * margin;
		int view_height = (int)((float)view_width / wl_radio);

		
		float shrink_radio =  (float)view_width / (float)whole_image.getWidth() ;
		
		//get right image actual size
		int right_view_width = (int)(right_image.getWidth() * shrink_radio);
		
		//get left layout actual size
		int left_view_width = view_width - right_view_width;
		
		//initial FinalImageWindow
		finalImageWindow = new FinalImageWindow(margin, margin, view_width, view_height);
		finalImageWindow.leftWidth = left_view_width;
		cropBox = new ImageSize(margin, margin, view_width, view_height);
		
		//get image
		Matrix right_matrix = new Matrix();
		right_matrix.postScale(shrink_radio, shrink_radio);
		Bitmap tmp_right_image = Bitmap.createBitmap(right_image, 0, 0, right_image.getWidth(), right_image.getHeight(), right_matrix, true);
		Matrix whole_matrix = new Matrix();
		whole_matrix.postScale(shrink_radio, shrink_radio);
		Bitmap tmp_whole_image = Bitmap.createBitmap(whole_image, 0, 0, whole_image.getWidth(), whole_image.getHeight(), whole_matrix, true);
		
		//paint this three view
		LinearLayout.LayoutParams parameterDown = new LinearLayout.LayoutParams(view_width, view_height);
		parameterDown.leftMargin = parameterDown.topMargin = margin;
		mBottomPhotoFrameLayout.setLayoutParams(parameterDown);
		myImageFinalRight.setLayoutParams(new LinearLayout.LayoutParams(right_view_width, view_height));
		myLayoutLeft.setLayoutParams(new LinearLayout.LayoutParams(left_view_width,view_height));
		
		myImageFinalDown.setImageBitmap(tmp_whole_image);
		myImageFinalRight.setImageBitmap(tmp_right_image);
		
		//paint crop box
		paintCropBox();
	}
	

	
	private void refreshCropBox()
	{	
		myImageFinalRight.setDrawingCacheEnabled(true);
		Bitmap tmp_right = myImageFinalRight.getDrawingCache();
		List<ImageSize> sizes = ImageProjection(tmp_right.getWidth(), tmp_right.getHeight());
		ImageSize size_left = sizes.get(0);
		ImageSize size_right = sizes.get(1);
		myImageFinalRight.setDrawingCacheEnabled(false);
		int morePix = (int)(finalImageWindow.viewWidth * moreScale);
		cropBox.change(size_left.x, size_left.y, size_left.width - morePix + size_right.width, size_right.height);
		paintCropBox();
	}
	
	private void paintCropBox()
	{
		Toast.makeText(getApplicationContext(), cropBox.x + "," + cropBox.y + ", "+ cropBox.width + ", "+ cropBox.height , 500).show();
		frameTop.setX(cropBox.x);
		frameTop.setY(cropBox.y);	
		frameBottom.setX(cropBox.x);
		frameBottom.setY(cropBox.y+cropBox.height);
		frameLeft.setX(cropBox.x);
		frameLeft.setY(cropBox.y);
		frameRight.setX(cropBox.x+cropBox.width);
		frameRight.setY(cropBox.y);
		frameTop.setLayoutParams(new LinearLayout.LayoutParams(cropBox.width, 5));
		frameBottom.setLayoutParams(new LinearLayout.LayoutParams(cropBox.width, 5));
		frameLeft.setLayoutParams(new LinearLayout.LayoutParams(5,cropBox.height));
		frameRight.setLayoutParams(new LinearLayout.LayoutParams(5,cropBox.height));
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
		
		myImageFinalDown.setDrawingCacheEnabled(true);
		myImageFinalRight.setDrawingCacheEnabled(true);
		Bitmap tmp_left = myImageFinalDown.getDrawingCache();
		Bitmap tmp_right = myImageFinalRight.getDrawingCache();

		//get image size
		List<ImageSize> sizes = ImageProjection(tmp_right.getWidth(), tmp_right.getHeight());
		ImageSize size_left = sizes.get(0);
		ImageSize size_right = sizes.get(1);

		// generate image
		Bitmap target_left = Bitmap.createBitmap(tmp_left, size_left.x, size_left.y,
				size_left.width, size_left.height);
		FileUtil.memoryOneImage(target_left, "final_left");

		Bitmap target_right = Bitmap.createBitmap(tmp_right, size_right.x, size_right.y,
				size_right.width, size_right.height);
		FileUtil.memoryOneImage(target_right, "final_right");
		
		myImageFinalRight.setDrawingCacheEnabled(false);
		myImageFinalDown.setDrawingCacheEnabled(false);
		
	}
	
	
	private List<ImageSize> ImageProjection(int rwidth, int rheight) {
		
		List<ImageSize> result = new ArrayList<ImageSize>();
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
		
		// calculate left image size
		int x_left = 0;
		int y_left = (image_dy < 0) ? 0 : image_dy;
		int morePix = (int) (finalImageWindow.viewWidth * moreScale);
		int width_left = finalImageWindow.leftWidth
				+ ((image_dx > 0) ? image_dx : 0) + morePix;
		if (width_left > finalImageWindow.viewWidth)
			width_left = finalImageWindow.viewWidth;
		
		// calculate right image size
		int x_right = (image_dx > 0) ? image_dx : 0;
		int y_right = (image_dy > 0) ? image_dy : 0;
		int width_right = (image_dx > 0) ? image_width
				: (image_width + image_dx);
		if (image_width + image_dx > rwidth)
			width_right = rwidth - image_dx;
		int height_right = (image_dy > 0) ? image_height
				: (image_height + image_dy);
		if (height_right + image_dy > rheight)
			height_right = rheight - image_dy;
		int height_left = height_right;

		ImageSize size_left = new ImageSize(x_left, y_left, width_left, height_left);
		ImageSize size_right = new ImageSize(x_right, y_right, width_right, height_right);

		result.add(size_left);
		result.add(size_right);
		return result;

	}
	
	
	
	private void generateFinalImage_directly()
	{
		Bitmap final_left = FileUtil.loadBitmapFromFile("final_left");
		Bitmap final_right = FileUtil.loadBitmapFromFile("final_right");
		int morePix = (int)(finalImageWindow.viewWidth * moreScale);
		int tmp_width = final_left.getWidth() - morePix;
		Bitmap result = Bitmap.createBitmap(tmp_width + final_right.getWidth(), final_left.getHeight(),
				Config.RGB_565);
		//Bitmap result = Bitmap.createBitmap(final_left.getWidth() + final_right.getWidth() , final_right.getHeight(), Config.RGB_565);
		
		Bitmap tmp_left = Bitmap.createBitmap(final_left, 0, 0, tmp_width, final_left.getHeight());
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(tmp_left, new Matrix(), null);
		canvas.drawBitmap(final_right, tmp_left.getWidth(), 0, null);
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
