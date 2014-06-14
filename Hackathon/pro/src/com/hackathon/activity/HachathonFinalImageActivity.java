package com.hackathon.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.LibSift.namespace.SiftFun;
import com.hackathon.common.util.FileUtil;
import com.hackathon.common.util.GeometryUtil;
import com.hackathon.entity.FinalImageWindow;
import com.hackathon.entity.ImageSize;
import com.hackathon.main.R;

import com.hackathon.view.CropBoxView;
import com.hackathon.view.MyProgressDialog;
import com.hackathon.worker.HkExceptionHandler;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
	private CropBoxView cropBoxView;
	
	private FinalImageWindow finalImageWindow;
	public ImageSize cropBox;
	private TextView finaltext;

	private final float MOVEREMAINSCALE = (float) 0.2; // 移动后留在屏幕内部的比例
	private final float ZOOMINMAXSCALE = (float) 3.0; // 右侧图片放大比例上限
	private final float ZOOMOUTMINSCALE = (float) 0.3; // 右侧图盘缩小比例下限
	private final float MORESCALE = (float) 0.18; // 左边图片多给的比例
	private int MOREPIX = 0; // 左图实际多个的值
	private final float REMAINSCALE = (float) 0.10; // 右侧边栏的比例
	private final int MARGIN = 60;
	
	private MyProgressDialog progressDialog;
	/* ImageViewRight onTouch */
	// boolean isFit = false;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;
	Bitmap bitmap;
	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	int mode = NONE;
	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;
	/* ImageViewRight onToch end */
	
	int status = FIT;
	static final int FIT = 0;
	static final int PROCESS = 1;
	static final int SAVE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressDialog = MyProgressDialog.createDialog(this);
		Thread.setDefaultUncaughtExceptionHandler(new HkExceptionHandler()); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.final_image);
		// setProgressBarVisibility(true);

		buttonRightYes = (Button) findViewById(R.id.buttonRightYes);
		buttonRightNo = (Button) findViewById(R.id.buttonRightNo);
		buttonBottomSave = (Button) findViewById(R.id.buttonBottomSave);
		buttonBottomCancel = (Button) findViewById(R.id.buttonBottomCancel);
		myImageFinalDown = (ImageView) findViewById(R.id.myImageFinalDown);
		myImageFinalRight = (ImageView) findViewById(R.id.myImageFinalRight);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		myLayoutLeft = (FrameLayout) findViewById(R.id.myImageLayoutLeft);
		mBottomPhotoFrameLayout = (FrameLayout) findViewById(R.id.mBottomPhotoFrameLayout);
		cropBoxView = (CropBoxView) findViewById(R.id.cropBoxView);
		cropBoxView.setColor(Color.WHITE);
		setStatus("fit");

		// paintCropBox();
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
					boundRightImageInFrame(matrix);
					break;
				}
				myImageFinalRight.setImageMatrix(matrix);
				return true;
			}
		});

		buttonRightYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 切换状态
				getProcessPicture();
				setStatus("process");

				ProcessThread thread = new ProcessThread();
				thread.start();

//				Toast.makeText(getApplicationContext(), "UI finished", 100)
//						.show();
				// int cur_phase = 1;
				// while(cur_phase++<10){
				// Message msg = new Message();
				// Toast.makeText(getApplicationContext(), "UI finished" +
				// cur_phase, 100).show();
				// msg.what = (cur_phase == 9) ? cur_phase : cur_phase++;
				// handler.sendMessage(msg);
				// }
				// while(!finished)
				// {
				// Message msg = new Message();
				// msg.what = (cur_phase == 9) ? cur_phase : cur_phase++;
				// //handler.sendMessage(msg);
				//
				// try {
				// Thread.sleep(500);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// }

			}
		});
		buttonRightNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(HachathonFinalImageActivity.this,
						HachathonMainActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
				HachathonFinalImageActivity.this.finish();
			}
		});

		buttonBottomSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap final_bitmap = FileUtil.loadBitmapFromFile("final_tmp");
				String path = FileUtil.memoryOneImage(final_bitmap, "final");
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);     
				Uri uri = Uri.fromFile(new File(path));   
				intent.setData(uri);     
				sendBroadcast(intent);
				
				Bitmap final_left = FileUtil.loadBitmapFromFile("final_left");
				path = FileUtil.memoryOneImage(final_left, "final_record_left");
				
				Bitmap final_right = FileUtil.loadBitmapFromFile("final_right");
				FileUtil.memoryOneImage(final_right, "final_record_right");
				
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
		finaltext = (TextView)findViewById(R.id.finaltextview);
		finaltext.setText(R.string.finaltext);
		
	}

	/**
	 * 右图移动缩放时，将其限制在画框内
	 * 
	 * @param 当前右图的matrix
	 *            ，方法内会修改以限制
	 * @return 是否做过限制，没有返回true
	 */
	private boolean boundRightImageInFrame(Matrix m) {
		int dw = myImageFinalRight.getDrawable().getBounds().width();
		int dh = myImageFinalRight.getDrawable().getBounds().height();
		float[] values = new float[9];
		m.getValues(values);
		float sx = values[0];
		float sy = values[4];
		Log.d("lxy", "scale_X = " + sx + ", scale_Y = " + sy);
		int image_dx = (int) values[2];
		int image_dy = (int) values[5];
		int image_width = (int) (dw * sx);
		int image_height = (int) (dh * sy);

		// check and set the zoom scale
		if (sx > ZOOMINMAXSCALE) {
			float newScale = ZOOMINMAXSCALE / sx;
			matrix.postScale(newScale, newScale, mid.x, mid.y);
			return false;
		} else if (sx < ZOOMOUTMINSCALE) {
			float newScale = ZOOMOUTMINSCALE / sx;
			matrix.postScale(newScale, newScale, mid.x, mid.y);
			return false;
		}

		// check and set the location on x direction
		boolean noError = true;
		int remain_width = (int) (image_width * MOVEREMAINSCALE);
		int remain_height = (int) (image_height * MOVEREMAINSCALE);
		if (image_dx < 0 && image_width + image_dx <= remain_width) {
			int new_dx = image_width - remain_width;
			matrix.postTranslate((-1) * image_dx - new_dx, 0);
			noError = false;
		} else if (image_dx > 0 && dw - image_dx <= remain_width) {
			int new_dx = dw - remain_width;
			matrix.postTranslate(new_dx - image_dx, 0);
			noError = false;
		}

		// check and set the location on y direction
		if (image_dy < 0 && image_height + image_dy <= remain_height) {
			int new_dy = image_height - remain_height;
			matrix.postTranslate(0, (-1) * image_dy - new_dy);
			noError = false;
		} else if (image_dy > 0 && dh - image_dy <= remain_height) {
			int new_dy = dh - remain_height;
			matrix.postTranslate(0, new_dy - image_dy);
			noError = false;
		}
		return noError;
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//Toast.makeText(this, "后退键", Toast.LENGTH_SHORT).show();
			switch (status)
			{
			case FIT:
				buttonRightNo.callOnClick();
				break;
			case PROCESS:
				break;
			case SAVE:
				buttonBottomCancel.callOnClick();
				break;
			}
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		paintCropBox();
		super.onWindowFocusChanged(hasFocus);
	}

	private void setStatus(String input) {
		if ("fit".equals(input)) {
			status = FIT;
			myImageFinalRight.setVisibility(View.VISIBLE);
			myImageFinalDown.setVisibility(View.VISIBLE);
			if(progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			progressBar.setVisibility(View.INVISIBLE);
			buttonRightYes.setVisibility(View.VISIBLE);
			buttonRightNo.setVisibility(View.VISIBLE);
			buttonBottomSave.setVisibility(View.GONE);
			buttonBottomCancel.setVisibility(View.GONE);
			cropBoxView.setVisibility(View.VISIBLE);
			paintFinalImageView();

		} else if ("process".equals(input)) {
			status = PROCESS;
			progressBar.setVisibility(View.INVISIBLE);
			if(!progressDialog.isShowing())
			{
				progressDialog.show();
			}
			buttonRightYes.setVisibility(View.GONE);
			buttonRightNo.setVisibility(View.GONE);
			finaltext.setText("");
			buttonBottomSave.setVisibility(View.INVISIBLE);
			buttonBottomCancel.setVisibility(View.INVISIBLE);
			cropBoxView.setVisibility(View.GONE);

		} else if ("save".equals(input)) {
			status = SAVE;
			progressBar.setVisibility(View.INVISIBLE);
			if(progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			myImageFinalRight.setVisibility(View.INVISIBLE);
			cropBoxView.setVisibility(View.GONE);
			buttonBottomSave.setVisibility(View.VISIBLE);
			buttonBottomCancel.setVisibility(View.VISIBLE);
			finaltext.setText(R.string.finaltext2);
			Bitmap result = FileUtil.loadBitmapFromFile("final_tmp");
			myImageFinalDown.setImageBitmap(result);
		}

	}

	private void paintFinalImageView() {
		Bitmap whole_image = FileUtil.loadBitmapFromFile("whole");
		Bitmap right_image = FileUtil.loadBitmapFromFile("right");

		// get width-length radio
		// float wl_radio = (float)whole_image.getWidth() /
		// (float)whole_image.getHeight();

		// get down image actual size
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int t_window_width = (int) (screenWidth * (1 - REMAINSCALE)) - 2
				* MARGIN;
		int t_window_height = screenHeight - 2 * MARGIN;
		// int view_height = (int)((float)view_width / wl_radio);
		int[] preSize = { whole_image.getWidth(), whole_image.getHeight() };
		GeometryUtil.uniformScale(preSize, t_window_width, t_window_height);
		int view_width = preSize[0];
		int view_height = preSize[1];

		float shrink_radio = (float) view_width
				/ (float) whole_image.getWidth();

		// get right image actual size
		int right_view_width = (int) (right_image.getWidth() * shrink_radio);

		// get left layout actual size
		int left_view_width = view_width - right_view_width;

		// initial FinalImageWindow
		// finalImageWindow = new FinalImageWindow(MARGIN, MARGIN, view_width,
		// view_height);
		finalImageWindow = new FinalImageWindow(view_width, view_height);
		finalImageWindow.leftWidth = left_view_width;
		cropBoxView.setLeftWidth(left_view_width);
		cropBox = new ImageSize(0, 0, view_width, view_height);

		// get image
		Matrix right_matrix = new Matrix();
		right_matrix.postScale(shrink_radio, shrink_radio);
		Bitmap tmp_right_image = Bitmap.createBitmap(right_image, 0, 0,
				right_image.getWidth(), right_image.getHeight(), right_matrix,
				true);
		Matrix whole_matrix = new Matrix();
		whole_matrix.postScale(shrink_radio, shrink_radio);
		Bitmap tmp_whole_image = Bitmap.createBitmap(whole_image, 0, 0,
				whole_image.getWidth(), whole_image.getHeight(), whole_matrix,
				true);

		// paint this three view
		// LinearLayout.LayoutParams parameterDown = new
		// LinearLayout.LayoutParams(view_width, view_height);
		// parameterDown.leftMargin = parameterDown.topMargin = MARGIN;
		// mBottomPhotoFrameLayout.setLayoutParams(parameterDown);
		myImageFinalRight.setLayoutParams(new LinearLayout.LayoutParams(
				right_view_width, view_height));
		myLayoutLeft.setLayoutParams(new LinearLayout.LayoutParams(
				left_view_width, view_height));

		myImageFinalDown.setImageBitmap(tmp_whole_image);
		myImageFinalRight.setImageBitmap(tmp_right_image);

		// paint crop box
		paintCropBox();
	}

	private void refreshCropBox() {
		myImageFinalRight.setDrawingCacheEnabled(true);
		Bitmap tmp_right = myImageFinalRight.getDrawingCache();
		List<ImageSize> sizes = ImageProjection(tmp_right.getWidth(),
				tmp_right.getHeight());
		ImageSize size_left = sizes.get(0);
		ImageSize size_right = sizes.get(1);
		myImageFinalRight.setDrawingCacheEnabled(false);
		cropBox.change(size_left.x, size_left.y, size_left.width - MOREPIX
				+ size_right.width, size_right.height);
		cropBoxView.setLeftWidth(size_left.width - MOREPIX);
		// cropBox.change(finalImageWindow.viewX + size_left.x,
		// finalImageWindow.viewY + size_left.y, size_left.width - MOREPIX,
		// size_right.height);
		paintCropBox();
	}


	private void paintCropBox() {
		//int out_width = cropBoxView.getBorderwidth();
		int out_width = 0;
		// Toast.makeText(getApplicationContext(), cropBox.x + "," + cropBox.y +
		// ", "+ cropBox.width + ", "+ cropBox.height , 500).show();
		cropBoxView.setX(cropBox.x - out_width);
		cropBoxView.setY(cropBox.y - out_width);
		// cropBoxView.setLayoutParams(new
		// LinearLayout.LayoutParams(cropBox.width, cropBox.height) );
		cropBoxView.setLayoutParams(new FrameLayout.LayoutParams(cropBox.width + 2 * out_width,
				cropBox.height + 2 * out_width));
		cropBoxView.invalidate();
		
	}

	class ProcessThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Toast.makeText(getApplicationContext(), "begin new thread",
			// 100).show();
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
				// begin
//				Toast.makeText(getApplicationContext(), "begin process", 100)
//						.show();
				// progressBar.setProgress(0);
				break;
			case -1:
				// end
//				Toast.makeText(getApplicationContext(), "end process", 100)
//						.show();
				// 关闭ProgressDialog
				// progressBar.setProgress(100);
				setStatus("save");

				break;

			default:
//				Toast.makeText(getApplicationContext(), "what : " + msg.what,
//						100).show();
				// progressBar.setProgress(msg.what * 10);
				break;
			}
		}
	};

	/**
	 * 得到将要处理的左右图
	 */
	private void getProcessPicture() {

		myImageFinalDown.setDrawingCacheEnabled(true);
		myImageFinalRight.setDrawingCacheEnabled(true);
		Bitmap tmp_left = myImageFinalDown.getDrawingCache();
		Bitmap tmp_right = myImageFinalRight.getDrawingCache();

		// get image size
		List<ImageSize> sizes = ImageProjection(tmp_right.getWidth(),
				tmp_right.getHeight());
		ImageSize size_left = sizes.get(0);
		ImageSize size_right = sizes.get(1);

		// generate image
		Bitmap target_left = Bitmap.createBitmap(tmp_left, size_left.x,
				size_left.y, size_left.width, size_left.height);
		String path = FileUtil.memoryOneImage(target_left, "final_left");
		
		Bitmap target_right = Bitmap.createBitmap(tmp_right, size_right.x,
				size_right.y, size_right.width, size_right.height);
		path = FileUtil.memoryOneImage(target_right, "final_right");
		myImageFinalRight.setDrawingCacheEnabled(false);
		myImageFinalDown.setDrawingCacheEnabled(false);

	}

	/**
	 * 得到当前用户看到的左边图像及右边图像的尺寸位置
	 * 
	 * @param rwidth
	 *            右图长
	 * @param rheight
	 *            右图宽
	 * @return
	 */
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
		MOREPIX = (int) (finalImageWindow.viewWidth * MORESCALE);
		int width_left_nomore = finalImageWindow.leftWidth
				+ ((image_dx > 0) ? image_dx : 0);
		int width_left = width_left_nomore + MOREPIX;
		if (width_left > finalImageWindow.viewWidth) {
			width_left = finalImageWindow.viewWidth;
			MOREPIX = width_left - width_left_nomore;
		}

		// calculate right image size
		int y_right, height_right;
		int x_right = (image_dx > 0) ? image_dx : 0;
		int width_right = (image_dx > 0) ? image_width
				: (image_width + image_dx);
		if (width_right + width_left_nomore > finalImageWindow.viewWidth)
			width_right = finalImageWindow.viewWidth - width_left_nomore;
		if (image_dy > 0) {
			y_right = image_dy;
			if (y_right + image_height > finalImageWindow.viewHeight)
				height_right = finalImageWindow.viewHeight - y_right;
			else
				height_right = image_height;
		} else {
			y_right = 0;
			height_right = image_height + image_dy;
			if (height_right > finalImageWindow.viewHeight)
				height_right = finalImageWindow.viewHeight;

		}
		int height_left = height_right;

		ImageSize size_left = new ImageSize(x_left, y_left, width_left,
				height_left);
		ImageSize size_right = new ImageSize(x_right, y_right, width_right,
				height_right);

		result.add(size_left);
		result.add(size_right);
		return result;

	}

	/**
	 * 直接将两张图片强拼接在一起
	 */
	private void generateFinalImage_directly() {
		Bitmap final_left = FileUtil.loadBitmapFromFile("final_left");
		Bitmap final_right = FileUtil.loadBitmapFromFile("final_right");

		int tmp_width = final_left.getWidth() - MOREPIX;
		Bitmap result = Bitmap.createBitmap(tmp_width + final_right.getWidth(),
				final_left.getHeight(), Config.RGB_565);
		// Bitmap result = Bitmap.createBitmap(final_left.getWidth() +
		// final_right.getWidth() , final_right.getHeight(), Config.RGB_565);

		Bitmap tmp_left = Bitmap.createBitmap(final_left, 0, 0, tmp_width,
				final_left.getHeight());
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(tmp_left, new Matrix(), null);
		canvas.drawBitmap(final_right, tmp_left.getWidth(), 0, null);
		FileUtil.memoryOneImage(result, "final_tmp");

		// myImageFinalDown.setImageBitmap(result);
	}

	/**
	 * 拼接图像
	 */
	private void generateFinalImage() {
		String fileName1 = FileUtil.getFilePathByType("final_left");
		String fileName2 = FileUtil.getFilePathByType("final_right");
		int ret = SiftFun.siftConjunction(fileName1, fileName2);
		// Toast.makeText(getApplicationContext(), "conjunction finished : " +
		// ret, 100).show();
		if (ret != 0)
			generateFinalImage_directly();
	}

}
