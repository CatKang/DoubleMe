package com.hackathon.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackathon.common.util.FileUtil;
import com.hackathon.common.util.GeometryUtil;
import com.hackathon.common.util.Log;
import com.hackathon.entity.HkWindow;
import com.hackathon.entity.ImageSize;
import com.hackathon.main.R;
import com.hackathon.worker.HkExceptionHandler;

public class HachathonMainActivity extends Activity implements
		SurfaceHolder.Callback, OnTouchListener {
	private static String TAG = "HMainActivity";
	private SurfaceView surfaceView;
	private Camera camera;

	private FrameLayout frameLayout;
	private LinearLayout mainLayout;
	private FrameLayout left;
	private FrameLayout right;
	private ImageView leftImage;
	private ImageView rightImage;
	private ImageView floatImage;
	private ImageView xiangjiImage;
	private ImageView moveImage;
	private Button noButton;
	private Button takephotoButton;

	private ImageView picFrameImageL;
	private ImageView picFrameImageR;

	HkWindow curWindow;
	int flag = 0;
	Size pictureSize;
	Size previewSize;
	int windowSize_width;
	int windowSize_height;

	// for frame drag
	boolean isSetFrame = false, can_drag = true;
	int lastX, lastY;
	TextView text;
	Log log_file = null;

	// for frame drag end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new HkExceptionHandler());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		this.initial();
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		takephotoButton = (Button) findViewById(R.id.takephotoButton);
		noButton = (Button) findViewById(R.id.noButton);
		floatImage = (ImageView) findViewById(R.id.imageFloat);
		rightImage = (ImageView) findViewById(R.id.imageRight);
		left = (FrameLayout) findViewById(R.id.left);
		right = (FrameLayout) findViewById(R.id.right);
		leftImage = (ImageView) findViewById(R.id.imageLeft);
		text = (TextView) findViewById(R.id.text);
		text.setVisibility(View.VISIBLE);
		xiangjiImage = (ImageView) findViewById(R.id.imageXiangji);
		moveImage = (ImageView) findViewById(R.id.imageMove);
		picFrameImageL = (ImageView) findViewById(R.id.picFrameImageL);
		picFrameImageR = (ImageView) findViewById(R.id.picFrameImageR);
		picFrameImageL.setVisibility(View.VISIBLE);
		picFrameImageR.setVisibility(View.GONE);
		// moveImage.setBackgroundResource(R.drawable.bai);
		surfaceView = (SurfaceView) this.findViewById(R.id.camera);
		setStatus("initial");
		takephotoButton.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				takephotoButton.setEnabled(false);
				// TODO Auto-generated method stub
				if (flag == 0) {
					text.setVisibility(View.VISIBLE);
					camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
					can_drag = false;
					// camera.stopPreview();
					// camera.startPreview();
				} else if (flag == 1) {
					// leftImage.setVisibility(View.VISIBLE);
					// rightImage.setVisibility(View.INVISIBLE);
					// floatImage.setVisibility(View.VISIBLE);
					// picFrameImageL.setVisibility(View.GONE);
					// picFrameImageR.setVisibility(View.VISIBLE);

					camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
					Camera.Parameters parameters = camera.getParameters();
					if (parameters.isAutoExposureLockSupported()) {
						parameters.setAutoExposureLock(false);
					}
				}
				// } else if (flag == 1) {
				// noButton.setVisibility(View.INVISIBLE);
				// floatImage.setVisibility(View.VISIBLE);
				// rightImage.setVisibility(View.INVISIBLE);
				// xiangjiImage.setVisibility(View.GONE);
				// moveImage.setVisibility(View.GONE);
				// leftImage.setVisibility(View.VISIBLE);
				// // floatImage.setBackgroundColor(Color.TRANSPARENT);
				// camera.stopPreview();
				// camera.startPreview();
				// picFrameImageL.setVisibility(View.GONE);
				// picFrameImageR.setVisibility(View.VISIBLE);
				// Camera.Parameters parameters = camera.getParameters();
				// if(parameters.isAutoExposureLockSupported())
				// {
				// parameters.setAutoExposureLock(false);
				// }
				// } else if (flag == 2) {
				// camera.takePicture(shutterCallback, rawCallback,
				// jpegCallback);
				// }
				// flag++;
				takephotoButton.setEnabled(true);
			}
		});
		surfaceView.setOnTouchListener(this);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//Toast.makeText(this, "后退键", Toast.LENGTH_SHORT).show();
			switch (flag) {
			case 0:
				HachathonMainActivity.this.finish();
				break;
			case 1:
				// setStatus("initial");
				finish();
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initial() {
		FileUtil.init_file_env();
		log_file = new Log();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		GeometryUtil.uniformScale(previewSize, windowSize_width,
				windowSize_height);
		surfaceView.setLayoutParams(new FrameLayout.LayoutParams(
				previewSize.width, previewSize.height));
		mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
				previewSize.width, previewSize.height));
		// Toast.makeText(getApplicationContext(), previewSize.width + ", " +
		// previewSize.height, 100).show();
		// surfaceView.setLayoutParams(new
		// FrameLayout.LayoutParams(previewSize.width, previewSize.height));
		// mainLayout.setLayoutParams(new
		// FrameLayout.LayoutParams(previewSize.width, previewSize.height));
		curWindow = new HkWindow(surfaceView, previewSize.width,
				previewSize.height);
		left.setLayoutParams(new LinearLayout.LayoutParams(
				curWindow.viewWidth / 2, curWindow.viewHeight));
		right.setLayoutParams(new LinearLayout.LayoutParams(
				curWindow.viewWidth / 2, curWindow.viewHeight));
		super.onWindowFocusChanged(hasFocus);
	}

	private void setStatus(String input) {
		if ("initial".equals(input)) {
			flag = 0;
			windowSize_width = getWindowManager().getDefaultDisplay()
					.getWidth();
			windowSize_height = getWindowManager().getDefaultDisplay()
					.getHeight();
			takephotoButton.setX(windowSize_width - 200);
			//takephotoButton.setY((windowSize_height - 50) / 2 - 50);

		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 自动生成方法存根

		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPictureFormat(PixelFormat.RGB_565);

			List<Camera.Size> s_previewSize = parameters
					.getSupportedPreviewSizes();
			List<Camera.Size> s_pictureSize = parameters
					.getSupportedPictureSizes();
			boolean vet = setPreveiewAndPictureSize(s_previewSize,
					s_pictureSize, windowSize_height);
			if (vet == false) {
				// TODO exit
				//Toast.makeText(getBaseContext(), "Fatal Error! Exit!", 100).show();

			}
			FileUtil.recordSupportSize(s_previewSize, s_pictureSize);

			// previewSize = GeometryUtil.getOptimalSize(
			// s_previewSize, windowSize_width, windowSize_height);
			// previewSize.width = 960;
			// previewSize.height = 720;
			//

			// pictureSize = GeometryUtil.getOptimalSize(
			// s_pictureSize, windowSize_width, windowSize_height);
			// pictureSize.width = 2048;
			// pictureSize.height = 1536;

			// record env.log
			FileUtil.recordEnv("PreviewSize: (" + previewSize.width + " , "
					+ previewSize.height + ")");
			FileUtil.recordEnv("PictureSize: (" + pictureSize.width + " , "
					+ pictureSize.height + ")");
			FileUtil.recordEnv("WindowSize: (" + windowSize_width + " , "
					+ windowSize_height + ")");

			parameters.setPreviewSize(previewSize.width, previewSize.height);
			try {
				parameters
						.setPictureSize(pictureSize.width, pictureSize.height);
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				// camera.startPreview();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		camera.startPreview();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		//Toast.makeText(getApplicationContext(), "surfaceStop", 100).show();
		// TODO 自动生成方法存根
		if (camera != null) {

			camera.release();
			camera = null;
		}

	}

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {

		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Handle RAW image data
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {

		public void onPictureTaken(byte[] _data, Camera _camera) {
			// Toast.makeText(getApplicationContext(), "jepgCallback",
			// 100).show();

			log_file.saveLog("image data_length : " + _data.length);
			Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
			// Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
			// _data.length);
			if (flag == 0) {
				flag = 1;

				ImageSize sizes = curWindow.getImageSize("left",
						pictureSize.width, pictureSize.height);

				Bitmap targetbm_left = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);
				sizes = curWindow.getImageSize("right", pictureSize.width,
						pictureSize.height);
				Bitmap targetbm_right = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);

				String path = FileUtil.memoryOneImage(targetbm_left, "left");
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);     
				Uri uri = Uri.fromFile(new File(path));   
				intent.setData(uri);     
				sendBroadcast(intent);
				path = FileUtil.memoryOneImage(targetbm_right, "float");
				uri = Uri.fromFile(new File(path));   
				intent.setData(uri);     
				sendBroadcast(intent);
				path = FileUtil.memoryOneImage(bm, "whole");
				uri = Uri.fromFile(new File(path));   
				intent.setData(uri);     
				sendBroadcast(intent);

				leftImage.setVisibility(View.VISIBLE);
				leftImage.setImageBitmap(targetbm_left);
				// xiangjiImage.setVisibility(View.VISIBLE);
				moveImage.setVisibility(View.GONE);
				picFrameImageL.setVisibility(View.GONE);
				picFrameImageR.setVisibility(View.VISIBLE);
				rightImage.setVisibility(View.INVISIBLE);
				floatImage.setVisibility(View.INVISIBLE);
				// rightImage.setBackgroundColor(Color.BLACK);
				// rightImage.getBackground().setAlpha(200);
				// rightImage.setVisibility(View.VISIBLE);
				noButton.setVisibility(View.INVISIBLE);
				floatImage.setVisibility(View.VISIBLE);
				floatImage.setImageBitmap(targetbm_right);
				floatImage.setAlpha(99);
				noButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						noButton.setEnabled(false);
						// TODO Auto-generated method stub
						leftImage.setVisibility(View.GONE);
						noButton.setVisibility(View.GONE);
						rightImage.setVisibility(View.VISIBLE);
						rightImage
								.setBackgroundResource(R.drawable.shadow_right);
						moveImage.setVisibility(View.VISIBLE);
						floatImage.setVisibility(View.INVISIBLE);
						xiangjiImage.setVisibility(View.GONE);
						picFrameImageL.setVisibility(View.VISIBLE);
						picFrameImageR.setVisibility(View.GONE);
						flag = 0;
						can_drag = true;
						camera.startPreview();
						noButton.setEnabled(true);
					}
				});
				Camera.Parameters parameters = camera.getParameters();
				if (parameters.isAutoExposureLockSupported()) {
					parameters.setAutoExposureLock(true);
				}
				camera.stopPreview();
				camera.startPreview();
				text.setVisibility(View.INVISIBLE);
//				targetbm_left.recycle();
//				targetbm_right.recycle();
//				bm.recycle();
			} else {
				flag = 0;
				ImageSize sizes = curWindow.getImageSize("right",
						pictureSize.width, pictureSize.height);
				Bitmap targetbm_right = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);
				String path = FileUtil.memoryOneImage(targetbm_right, "right");
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);     
				Uri uri = Uri.fromFile(new File(path));   
				intent.setData(uri);     
				sendBroadcast(intent);
				Intent finalImageIntent = new Intent(
						HachathonMainActivity.this,
						HachathonFinalImageActivity.class);
				finalImageIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finalImageIntent.putExtra("HkWindow", curWindow);
				startActivity(finalImageIntent);
				if(camera!=null)
				{
					camera.release();
				}
				// HachathonMainActivity.this.finish();
//				targetbm_right.recycle();
//				bm.recycle();
			}

		}
	};

	public boolean onTouch(View v, MotionEvent event) {
		if (!can_drag)
			return false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			if (curWindow.onFrame(lastX, lastY))
				isSetFrame = true;
			else
				isSetFrame = false;
			break;

		case MotionEvent.ACTION_MOVE:
			if (!isSetFrame)
				return true;
			int cx = (int) event.getRawX();
			int cy = (int) event.getRawY();
			int dx = curWindow.boundInWindow(cx - lastX);

			LinearLayout.LayoutParams paramLeft = (LinearLayout.LayoutParams) left
					.getLayoutParams();
			paramLeft.width += dx;
			paramLeft.height = curWindow.viewHeight;
			left.setLayoutParams(paramLeft);
			LinearLayout.LayoutParams paramRight = (LinearLayout.LayoutParams) right
					.getLayoutParams();
			paramRight.width -= dx;
			paramRight.height = curWindow.viewHeight;
			right.setLayoutParams(paramRight);
			curWindow.curFrameX += dx;
			lastX = cx;
			lastY = cy;
			// text.setText("curFrameX:" + curWindow.curFrameX + "   cx:" + cx
			// + "  cy:" + cy + "   dx:" + dx + "  paramLeft.width:"
			// + paramLeft.width + "   paramRight.width:"
			// + paramRight.width);
			break;

		case MotionEvent.ACTION_UP:
			isSetFrame = false;
			break;
		}
		return true;
	}

	/**
	 * set the Optimal previewSize and pictureSize 1，找跟屏幕比例最接近的preview size
	 * 2，找与选中的preview size 一致的 picture size 3，在符合条件的picture size
	 * 中找与target_pic_height 最接近的尺寸 3，设置previewSize pictureSize
	 * 
	 * @param s_previewSize
	 *            所有支持的preview size
	 * @param s_pictureSize
	 *            所有支持的picture size
	 * @param target_pic_height
	 *            目标图片比例
	 * @return 找到合适的返回true。 否则false
	 */
	private boolean setPreveiewAndPictureSize(List<Camera.Size> s_previewSize,
			List<Camera.Size> s_pictureSize, int target_pic_height) {
		double precision = 0.00001;
		double windowSize_ratio = (double) windowSize_width / windowSize_height;
		double tolerance_first = 0, tolerance_max = 0.5, tolerance_dt = 0.1;

		// 1.第一层， 循环可接受的preview 与屏幕的比例差距
		for (double tolerance_cur = tolerance_first; tolerance_cur <= tolerance_max; tolerance_cur += tolerance_dt) {
			// 2.第二层， 循环所有支持的previewsize
			for (Size preSize : s_previewSize) {
				double preRatio = (double) preSize.width
						/ (double) preSize.height;
				if (Math.abs(preRatio - windowSize_ratio) - tolerance_cur > precision)
					continue;
				// 找到最可接受的preview size， 用这个size 尝试去找同比例的picture size
				int min_diff = target_pic_height;
				Size cur_best_pic = null;
				// 3.第三层， 循环所有支持的picturesize
				for (Size picSize : s_pictureSize) {
					double picRatio = (double) picSize.width
							/ (double) picSize.height;

					if (Math.abs(picRatio - preRatio) <= precision) {
						// preview size 与 picture size 相等
						if (cur_best_pic == null
								|| Math.abs(picSize.height - target_pic_height) < min_diff) {
							min_diff = Math.abs(picSize.height
									- target_pic_height);
							cur_best_pic = picSize;
						}
					}
				}
				if (cur_best_pic != null) {
					previewSize = preSize;
					pictureSize = cur_best_pic;
					return true;
				}
			}
		}
		return false;
	}
}
